package com.lptiyu.tanke.messagesystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.messagesystem.adpater.MessageListAdapter;
import com.lptiyu.tanke.pojo.MessageEntity;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class MessageListFragment extends BaseFragment implements
    SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.message_list_swipe_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayout;
  @BindView(R.id.message_list_recycler_view)
  RecyclerView mRecyclerView;

  private boolean isRefreshing = false;

  private MessageListAdapter adapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = fromResLayout(inflater, container, R.layout.fragment_message_list);
    ButterKnife.bind(this, view);
    init();
    return view;
  }

  private void init() {
    swipeRefreshLayout.setOnRefreshListener(this);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.setAdapter(adapter = new MessageListAdapter(getContext()));
  }

  @Override
  public void onRefresh() {
    if (isRefreshing) {
      return;
    }
    isRefreshing = true;
    HttpService.getGameService().getSystemMessage(Accounts.getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .map(new Func1<Response<List<MessageEntity>>, List<MessageList>>() {
          @Override
          public List<MessageList> call(Response<List<MessageEntity>> listResponse) {
            List<MessageList> result = new ArrayList<>();
            if (listResponse == null || listResponse.getStatus() == 0) {
              ToastUtil.TextToast("获取消息失败");
              return result;
            }
            List<MessageEntity> serverMessageDatas = listResponse.getData();
            if (serverMessageDatas == null || serverMessageDatas.size() == 0) {
              return result;
            }
            MessageList tempMessage = new MessageList();
            for (MessageEntity me : serverMessageDatas) {
              tempMessage.setName("步道官方");
              tempMessage.setIsRead(false);
              tempMessage.setContent(me.getContent());
              tempMessage.setTime(TimeUtils.parseDate(me.getCreateTime(), TimeUtils.totalFormat).getTime());
              result.add(tempMessage);
            }
            return result;
          }
        })
        .subscribe(new Action1<List<MessageList>>() {
          @Override
          public void call(List<MessageList> messageLists) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
            adapter.updateMessageData(messageLists);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            isRefreshing = false;
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil.TextToast("获取消息失败");
          }
        });
  }

  @Override
  public FragmentController getController() {
    return null;
  }
}
