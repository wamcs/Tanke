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
import com.lptiyu.tanke.database.MessageNotification;
import com.lptiyu.tanke.database.MessageNotificationDao;
import com.lptiyu.tanke.database.MessageNotificationList;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.messagesystem.SystemWebActivity;
import com.lptiyu.tanke.messagesystem.adpater.MessageBaseAdapter;
import com.lptiyu.tanke.messagesystem.adpater.PushAdapter;
import com.lptiyu.tanke.pojo.MessageEntity;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.thread;

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
    private MessageNotificationDao messageDao;
    private List<MessageNotification> messagesList;
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
        messagesList = new ArrayList<>();
        adapter = new PushAdapter(context, messagesList, this);
        mRecyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisiableItem + 1 == adapter.getItemCount() && !isRefreshing) {
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
                                .where(MessageNotificationDao.Properties.Type.eq(integer))
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
                .map(new Func1<Integer, List<MessageNotification>>() {
                    @Override
                    public List<MessageNotification> call(Integer integer) {
                        if (mCurrentMsgIndex >= mTotalMsgCount) {
                            return null;
                        }
                        List<MessageNotification> result;
                        result = decorateMessageList(messageDao.queryBuilder()
                                .where(MessageNotificationDao.Properties.Type.eq(integer))
                                .orderDesc(MessageNotificationDao.Properties.Id)
                                .offset(mCurrentPage * MESSAGE_NUM_EVERY_PAGE)
                                .limit(MESSAGE_NUM_EVERY_PAGE)
                                .list());
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MessageNotification>>() {
                    @Override
                    public void call(List<MessageNotification> messages) {
                        isRefreshing = false;
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (messages == null || messages.size() == 0) {
                            ToastUtil.TextToast("暂无历史消息");
                            return;
                        }

                        messagesList.addAll(messages);
                        adapter.notifyDataSetChanged();
                        if (messagesList == null || messagesList.size() == 0) {
                            if (mNoDataImageView != null) {
                                thread.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImageView.setVisibility(View.VISIBLE);
                                    }
                                });
                                return;
                            }
                        } else {
                            if (mNoDataImageView != null) {
                                thread.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImageView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }

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

    private void loadMessageFromNet() {
        isRefreshing = true;
        HttpService.getGameService().getSystemMessage(Accounts.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Response<List<MessageEntity>>>() {
                    @Override
                    public void call(Response<List<MessageEntity>> listResponse) {
                        messagesList.clear();
                        List<MessageNotification> list = new ArrayList<>();
                        List<MessageEntity> messageEntityList = listResponse.getData();

                        //                        if (messageEntityList == null || messageEntityList.size() == 0) {
                        //                            if (mNoDataImageView != null) {
                        //                                thread.mainThread(new Runnable() {
                        //                                    @Override
                        //                                    public void run() {
                        //                                        mNoDataImageView.setVisibility(View.VISIBLE);
                        //                                    }
                        //                                });
                        //                                return;
                        //                            }
                        //                        } else {
                        //                            if (mNoDataImageView != null) {
                        //                                thread.mainThread(new Runnable() {
                        //                                    @Override
                        //                                    public void run() {
                        //                                        mNoDataImageView.setVisibility(View.GONE);
                        //                                    }
                        //                                });
                        //                            }
                        //                        }

                        for (MessageEntity me : messageEntityList) {
                            MessageNotification messages = new MessageNotification();
                            messages.setId(me.getId());
                            messages.setTitle(me.getTitle());
                            messages.setImage(me.getImgUrl());
                            messages.setAlert(me.getContent());
                            messages.setUrl(me.getUrl());
                            messages.setTime(me.getCreateTime());
                            messages.setType(Conf.MESSAGE_LIST_TYPE_OFFICIAL);
                            list.add(messages);
                        }
                        messagesList.addAll(decorateMessageList(list));
                        adapter.notifyDataSetChanged();

                        if (messagesList == null || messagesList.size() == 0) {
                            if (mNoDataImageView != null) {
                                thread.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImageView.setVisibility(View.VISIBLE);
                                    }
                                });
                                return;
                            }
                        } else {
                            if (mNoDataImageView != null) {
                                thread.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImageView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }

                        MessageNotificationList result;
                        MessageEntity lastMsg = messageEntityList.get(messageEntityList.size() - 1);
                        result = new MessageNotificationList();
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
        if (messagesList == null || messagesList.size() == 0 || messagesList.size() < position) {
            return;
        }
        MessageNotification messages = messagesList.get(position);
        if (messages == null) {
            return;
        }
        Intent intent = new Intent(context, SystemWebActivity.class);
        intent.putExtra(Conf.MESSAGE_URL, messages.getUrl());
        intent.putExtra(Conf.MESSAGE_TITLE, messages.getTitle());
        context.startActivity(intent);
    }

    @Override
    public void finish() {
        context.unregisterReceiver(receiver);
    }

    @Override
    public void onRefresh() {
        if (isRefreshing) {
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
                MessageNotification messages = new Gson().fromJson(jsonString, MessageNotification.class);

                DBHelper.getInstance().getPushMessageDao().insert(messages);

                MessageNotificationList list = new MessageNotificationList();
                list.setTime(messages.getTime());
                list.setContent(messages.getAlert());
                list.setIsRead(false);
                list.setType(messages.getType());
                switch (messages.getType()) {
                    case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
                        list.setName(context.getString(R.string.message_type_official));
                        break;
                    case Conf.MESSAGE_LIST_TYPE_SYSTEM:
                        list.setName("系统消息");
                        break;
                }

                DBHelper.getInstance().getMessageListDao().insert(list);

                if ((messages.getTime() - messagesList.get(messagesList.size() - 1).getTime()) > LIMIT_TIME) {
                    MessageNotification timeMessageNotification = new MessageNotification();
                    messages.setTime(messages.getTime());
                    messages.setType(Conf.TIME_TYPE);
                    messagesList.add(timeMessageNotification);
                }
                messagesList.add(messages);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
