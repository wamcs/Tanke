package com.lptiyu.tanke.messagesystem.helper;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.MessageNotification;
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
public class MessageHelper {

    @BindView(R.id.message_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.message_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.default_tool_bar_imageview)
    ImageView mBackButton;
    @BindView(R.id.default_tool_bar_textview)
    TextView mTitleText;
    @BindView(R.id.img_empty_view)
    ImageView mNoDataImageView;

    protected AppCompatActivity context;
    protected LinearLayoutManager manager;
    protected static final long LIMIT_TIME = 300000L;//5 minutes
    protected static final int MESSAGE_NUM_EVERY_PAGE = 3;

    public MessageHelper(AppCompatActivity activity, View view, int type) {
        context = activity;
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        manager = new LinearLayoutManager(context);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);
    }

    protected List<MessageNotification> decorateMessageList(List<MessageNotification> list) {
        List<MessageNotification> messages = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                MessageNotification message = new MessageNotification();
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

            if ((time - nextTime) >= LIMIT_TIME) {
                MessageNotification message = new MessageNotification();
                message.setTime(list.get(i + 1).getTime());
                message.setType(Conf.TIME_TYPE);
                messages.add(message);
            }

        }
        return messages;
    }


    public void finish() {
    }
}
