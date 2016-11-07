package com.lptiyu.tanke.fragments.messagesystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.messagelist.MessageListActivity;
import com.lptiyu.tanke.entity.response.MessageEntity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.DateFormatterUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageFragment extends MyBaseFragment implements MessageContact.IMessageView {
    @BindView(R.id.img_message_picture)
    ImageView imgMessagePicture;
    @BindView(R.id.tv_message_name)
    TextView tvMessageName;
    @BindView(R.id.img_message_red_spot)
    ImageView imgMessageRedSpot;
    @BindView(R.id.tv_message_time)
    TextView tvMessageTime;
    @BindView(R.id.tv_message_content)
    TextView tvMessageContent;
    @BindView(R.id.rl_message_layout)
    RelativeLayout rlMessageLayout;
    @BindView(R.id.default_tool_bar_imageview)
    ImageView defaultToolBarImageview;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;
    private MessagePresenter presenter;
    private int page = 1;
    private ArrayList<MessageEntity> messageList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MessagePresenter(this);
        loadMessage();
    }

    private void loadMessage() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.loadMessage(page);
        } else {
            getActivity().getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getActivity(), new PopupWindowUtils
                            .OnRetryCallback() {
                        @Override
                        public void onRetry() {
                            loadMessage();
                        }
                    });
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        defaultToolBarImageview.setVisibility(View.GONE);
        defaultToolBarTextview.setText("消息中心");
        return view;
    }

    @Override
    public void successLoadMessage(List<MessageEntity> list) {
        if (list != null && list.size() > 0) {
            messageList = new ArrayList<>();
            for (MessageEntity entity : list) {
                messageList.add(entity);
            }
            MessageEntity messageEntity = list.get(0);
            bindData(messageEntity);
        } else {
            Toast.makeText(getActivity(), "暂无消息", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindData(MessageEntity messageEntity) {
        if (messageEntity != null) {
            tvMessageContent.setText(messageEntity.title + "");
            tvMessageTime.setText(DateFormatterUtils.parseTimeStamp(messageEntity.create_time));
        }
    }

    @OnClick(R.id.rl_message_layout)
    public void onClick() {
        imgMessageRedSpot.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), MessageListActivity.class);
        intent.putParcelableArrayListExtra(Conf.MESSAGE_LIST, messageList);
        startActivity(intent);
    }
}
