package com.lptiyu.tanke.messagesystem;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListFragmentController;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.database.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/16
 *
 * @author ldx
 */
public class MessageControllerNew extends BaseListFragmentController<MessageEntity>{

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  @BindView(R.id.swipe_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayout;

  public MessageControllerNew(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshTop();
      }
    });
    refreshTop();
  }

  BaseAdapter<MessageEntity> adapter = new BaseAdapter<MessageEntity>() {
    @Override
    public BaseViewHolder<MessageEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
      return new MessageViewHolder(parent);
    }
  };

  @Override
  public Observable<List<MessageEntity>> requestData(int page) {
    return null;
  }

  public static final List<MessageEntity> dummyData = new ArrayList<>();
  static {
    MessageEntity entity = new MessageEntity();
  }

  @NonNull
  @Override
  public BaseAdapter<MessageEntity> getAdapter() {
    return adapter;
  }

  @Override
  public void onRefreshStateChanged(boolean isRefreshing) {

  }

  @Override
  public void onError(Throwable t) {

  }


}
