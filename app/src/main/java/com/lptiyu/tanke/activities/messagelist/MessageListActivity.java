package com.lptiyu.tanke.activities.messagelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.messagedetail.SystemWebActivity;
import com.lptiyu.tanke.adapter.MessageListAdapter;
import com.lptiyu.tanke.entity.response.MessageEntity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lptiyu.tanke.R.id.default_tool_bar_textview;

public class MessageListActivity extends MyBaseActivity {

    @BindView(default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;
    @BindView(R.id.recyclerView_message_list)
    RecyclerView recyclerViewMessageList;
    @BindView(R.id.swipe_message_list)
    SwipeRefreshLayout swipeMessageList;
    @BindView(R.id.no_data_imageview)
    ImageView emptyView;
    private MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        defaultToolBarTextview.setText("消息中心");
        final ArrayList<MessageEntity> listMessage = getIntent().getParcelableArrayListExtra(Conf.MESSAGE_LIST);
        if (listMessage != null) {
            recyclerViewMessageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false));
            adapter = new MessageListAdapter(this, listMessage);
            recyclerViewMessageList.setAdapter(adapter);
            adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onClick(int position) {
                    MessageEntity messageEntity = listMessage.get(position);
                    Intent intent = new Intent(MessageListActivity.this, SystemWebActivity.class);
                    intent.putExtra(Conf.MESSAGE_URL, messageEntity.url);
                    intent.putExtra(Conf.MESSAGE_TITLE, messageEntity.title);
                    MessageListActivity.this.startActivity(intent);
                }

                @Override
                public void onLongClick(int position) {

                }
            });
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        swipeMessageList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO 数据假刷新
                swipeMessageList.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }
}
