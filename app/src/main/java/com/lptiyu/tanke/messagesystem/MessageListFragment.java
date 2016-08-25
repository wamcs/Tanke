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
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.database.MessageNotification;
import com.lptiyu.tanke.database.MessageNotificationDao;
import com.lptiyu.tanke.database.MessageNotificationList;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.messagesystem.adpater.MessageListAdapter;
import com.lptiyu.tanke.pojo.MessageEntity;
import com.lptiyu.tanke.utils.ToastUtil;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = fromResLayout(inflater, container, R.layout.fragment_message_list);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter = new MessageListAdapter(getContext()));
        onRefresh();
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
                .map(new Func1<Response<List<MessageEntity>>, MessageNotificationList>() {
                    @Override
                    public MessageNotificationList call(Response<List<MessageEntity>> listResponse) {
                        MessageNotificationList result;
                        if (listResponse == null || listResponse.getStatus() == 0) {
                            ToastUtil.TextToast("暂无最新消息");
                            return null;
                        }
                        List<MessageEntity> serverMessageDatas = listResponse.getData();

                        // add official msg to message table
                        MessageNotificationDao messageDao = DBHelper.getInstance().getPushMessageDao();
                        for (MessageEntity me : serverMessageDatas) {
                            MessageNotification messages = new MessageNotification();
                            messages.setId(me.getId());
                            messages.setTitle(me.getTitle());
                            messages.setImage(me.getImgUrl());
                            messages.setAlert(me.getContent());
                            messages.setUrl(me.getUrl());
                            messages.setTime(me.getCreateTime());
                            messages.setType(Conf.MESSAGE_LIST_TYPE_OFFICIAL);
                            messageDao.insertOrReplace(messages);
                        }

                        //update the official msg item
                        MessageEntity lastMsg = serverMessageDatas.get(0);
                        result = new MessageNotificationList();
                        result.setName(getString(R.string.message_type_official));
                        result.setIsRead(false);
                        result.setContent(lastMsg.getContent());
                        result.setUserId(Conf.MESSAGE_LIST_USERID_OFFICIAL);
                        result.setType(Conf.MESSAGE_LIST_TYPE_OFFICIAL);
                        result.setTime(lastMsg.getCreateTime());
                        return result;
                    }
                })
                .subscribe(new Action1<MessageNotificationList>() {
                    @Override
                    public void call(MessageNotificationList messageList) {
                        isRefreshing = false;
                        swipeRefreshLayout.setRefreshing(false);
                        if (messageList != null) {
                            adapter.updateMessageData(messageList);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        isRefreshing = false;
                        swipeRefreshLayout.setRefreshing(false);
                        ToastUtil.TextToast("获取消息失败");
                        Timber.e(throwable, "here");
                    }
                });
    }

    @Override
    public FragmentController getController() {
        return null;
    }
}
