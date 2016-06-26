package com.lptiyu.tanke.messagesystem.helper;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.global.Conf;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageHelper implements
    SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.message_recycler_view)
  RecyclerView mRecyclerView;
  @BindView(R.id.message_refresh_layout)
  SwipeRefreshLayout mSwipeRefreshLayout;
  @BindView(R.id.default_tool_bar_imageview)
  ImageView mBackButton;
  @BindView(R.id.default_tool_bar_textview)
  TextView mTitleText;

  protected AppCompatActivity context;
  protected static final long LIMIT_TIME = 300000L;//5 minutes
  protected static final int MESSAGE_NUM_EVERY_PAGE = 5;


  public MessageHelper(AppCompatActivity activity, View view, int type) {
    context = activity;
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    layoutManager.setOrientation(VERTICAL);
    mRecyclerView.setLayoutManager(layoutManager);
    mSwipeRefreshLayout.setOnRefreshListener(this);
  }

  protected List<Message> decorateMessageList(List<Message> list) {
    List<Message> messages = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      if (i == 0) {
        Message message = new Message();
        message.setTime(list.get(i).getTime());
        message.setType(Conf.TIME_TYPE);
        messages.add(message);
      }
      messages.add(list.get(i));

      if (i == list.size() - 1) {
        break;
      }

      long time = list.get(i).getTime();
      long nextTime = list.get(i + 1).getTime();

      if ((nextTime - time) >= LIMIT_TIME) {
        Message message = new Message();
        message.setTime(list.get(i + 1).getTime());
        message.setType(Conf.TIME_TYPE);
        messages.add(message);
      }

    }
    return messages;
  }

  @Override
  public void onRefresh() {

  }

  public void finish() {
  }
}
