package com.lptiyu.tanke.messagesystem.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.database.MessageDao;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.messagesystem.SystemWebActivity;
import com.lptiyu.tanke.messagesystem.adpater.MessageBaseAdapter;
import com.lptiyu.tanke.messagesystem.adpater.PushAdapter;
import com.lptiyu.tanke.pojo.MessageEntity;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class PushHelper extends MessageHelper implements
    SwipeRefreshLayout.OnRefreshListener,
    MessageBaseAdapter.MessageViewHolderClickListener {

  private int lastVisiableItem;
  private int mTotalMsgPageCount;
  private int mTotalMsgCount;
  private int mCurrentPage;
  private int mCurrentMsgIndex;
  private int type;
  private boolean isRefreshing = false;

  private PushAdapter adapter;
  private MessageDao messageDao;
  private List<Message> messageList;
  private MessageReceiver receiver;

  public PushHelper(AppCompatActivity activity, View view, int type) {
    super(activity, view, type);
    this.type = type;
    registerReceiver();
    init();
  }

  private void init() {
    switch (type) {
      case Conf.MESSAGE_LIST_TYPE_SYSTEM:
        mTitleText.setText(context.getString(R.string.message_type_system));

        break;
      case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
        mTitleText.setText(context.getString(R.string.message_type_official));
        break;
    }
    messageDao = DBHelper.getInstance().getPushMessageDao();
    messageList = new ArrayList<>();
    adapter = new PushAdapter(context, messageList, this);
    mRecyclerView.setAdapter(adapter);
    mSwipeRefreshLayout.setOnRefreshListener(this);
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE
            && lastVisiableItem + 1 == adapter.getItemCount() && !isRefreshing){
          loadMessageFromDb();
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        lastVisiableItem = manager.findLastVisibleItemPosition();
      }
    });

    // calculate page number
    Observable.just(type)
        .map(new Func1<Integer, Integer>() {
          @Override
          public Integer call(Integer integer) {
             return (int) messageDao.queryBuilder()
                .where(MessageDao.Properties.Type.eq(integer))
                .count();
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Integer>() {
          @Override
          public void call(Integer result) {
            mTotalMsgCount = result;
            if (mTotalMsgCount % MESSAGE_NUM_EVERY_PAGE == 0) {
              mTotalMsgPageCount = mTotalMsgCount / MESSAGE_NUM_EVERY_PAGE;
            } else {
              mTotalMsgPageCount = mTotalMsgCount / MESSAGE_NUM_EVERY_PAGE + 1;
            }
            mCurrentPage = 0;
            mCurrentMsgIndex = 0;
            loadMessageFromDb();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Timber.e(throwable, "init error");
          }
        });
  }

  private void registerReceiver() {
    receiver = new MessageReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Conf.PUSH_ACTION);
    filter.setPriority(10);
    context.registerReceiver(receiver, filter);
  }

  private void loadMessageFromDb() {
    if (isRefreshing) {
      return;
    }
    isRefreshing = true;
    mSwipeRefreshLayout.setRefreshing(true);
    Observable.just(type)
        .map(new Func1<Integer, List<Message>>() {
          @Override
          public List<Message> call(Integer integer) {
            if (mCurrentMsgIndex >= mTotalMsgCount) {
              return null;
            }
            List<Message> result;
            result = decorateMessageList(messageDao.queryBuilder()
                .where(MessageDao.Properties.Type.eq(integer))
                .orderDesc(MessageDao.Properties.Id)
                .offset(mCurrentPage * MESSAGE_NUM_EVERY_PAGE)
                .limit(MESSAGE_NUM_EVERY_PAGE)
                .list());
            return result;
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Message>>() {
          @Override
          public void call(List<Message> messages) {
            isRefreshing = false;
            mSwipeRefreshLayout.setRefreshing(false);
            if (messages == null) {
              ToastUtil.TextToast("暂无历史消息");
              return;
            }
            messageList.addAll(messages);
            adapter.notifyDataSetChanged();
            if (mCurrentPage < mTotalMsgPageCount) {
              mCurrentPage += 1;
            }
            if (mCurrentMsgIndex < mTotalMsgCount) {
              mCurrentMsgIndex -= messages.size();
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            isRefreshing = false;
            mSwipeRefreshLayout.setRefreshing(false);
            Timber.e(throwable, "message error");
          }
        });
  }

  private void loadMessageFromNet(){
    isRefreshing = true;
    HttpService.getGameService().getSystemMessage(Accounts.getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<Response<List<MessageEntity>>>() {
          @Override
          public void call(Response<List<MessageEntity>> listResponse) {
            messageList.clear();
            List<Message> list = new ArrayList<>();
            List<MessageEntity> messageEntityList = listResponse.getData();
            for (MessageEntity me : messageEntityList){
              Message message = new Message();
              message.setId(me.getId());
              message.setTitle(me.getTitle());
              message.setImage(me.getImgUrl());
              message.setAlert(me.getContent());
              message.setUrl(me.getUrl());
              message.setTime(me.getCreateTime());
              message.setType(Conf.MESSAGE_LIST_TYPE_OFFICIAL);
              list.add(message);
            }
            messageList.addAll(decorateMessageList(list));
            adapter.notifyDataSetChanged();

            MessageList result;
            MessageEntity lastMsg = messageEntityList.get(messageEntityList.size() - 1);
            result = new MessageList();
            result.setName(context.getString(R.string.message_type_official));
            result.setIsRead(false);
            result.setContent(lastMsg.getContent());
            result.setUserId(Conf.MESSAGE_LIST_USERID_OFFICIAL);
            result.setType(Conf.MESSAGE_LIST_TYPE_OFFICIAL);
            result.setTime(lastMsg.getCreateTime());
            DBHelper.getInstance().getMessageListDao().insertOrReplace(result);
            isRefreshing = false;
            mSwipeRefreshLayout.setRefreshing(false);
          }
        });
  }


  @Override
  public void onMessageItemClicked(int position) {
    if (messageList == null || messageList.size() == 0 || messageList.size() < position) {
      return;
    }
    Message message = messageList.get(position);
    if (message == null) {
      return;
    }
    Intent intent = new Intent(context, SystemWebActivity.class);
    intent.putExtra(Conf.MESSAGE_URL, message.getUrl());
    intent.putExtra(Conf.MESSAGE_TITLE, message.getTitle());
    context.startActivity(intent);
  }

  @Override
  public void finish() {
    context.unregisterReceiver(receiver);
  }

  @Override
  public void onRefresh() {
    if (isRefreshing){
      return;
    }
    loadMessageFromNet();
  }

  public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(Conf.PUSH_ACTION)) {
        String jsonString = intent.getExtras().getString("com.avos.avoscloud.Data");
        Timber.d("push data json is %s", jsonString);
        Message message = new Gson().fromJson(jsonString, Message.class);

        DBHelper.getInstance().getPushMessageDao().insert(message);

        MessageList list = new MessageList();
        list.setTime(message.getTime());
        list.setContent(message.getAlert());
        list.setIsRead(false);
        list.setType(message.getType());
        switch (message.getType()) {
          case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
            list.setName(context.getString(R.string.message_type_official));
            break;
          case Conf.MESSAGE_LIST_TYPE_SYSTEM:
            list.setName("系统消息");
            break;
        }

        DBHelper.getInstance().getMessageListDao().insert(list);

        if ((message.getTime() - messageList.get(messageList.size() - 1).getTime()) > LIMIT_TIME) {
          Message timeMessage = new Message();
          message.setTime(message.getTime());
          message.setType(Conf.TIME_TYPE);
          messageList.add(timeMessage);
        }
        messageList.add(message);
        adapter.notifyDataSetChanged();
      }
    }
  }

}
