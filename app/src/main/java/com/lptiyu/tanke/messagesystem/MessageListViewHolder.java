package com.lptiyu.tanke.messagesystem;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.database.MessageList;
import com.lptiyu.tanke.database.MessageListDao;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-25
 *         email: wonderfulifeel@gmail.com
 */
public class MessageListViewHolder extends BaseViewHolder<MessageList> {

  @BindView(R.id.message_list_item_picture)
  CircularImageView mPicture;
  @BindView(R.id.message_list_item_name)
  CustomTextView mName;
  @BindView(R.id.message_list_item_content)
  CustomTextView mContent;
  @BindView(R.id.message_list_item_time)
  CustomTextView mTime;
  @BindView(R.id.message_list_item_layout)
  RelativeLayout mLayout;

  private WeakReference<MessageListDao> daoWeakReference;

  public MessageListViewHolder(ViewGroup parent, MessageListDao dao) {
    super(fromResLayout(parent, R.layout.item_message_list));
    ButterKnife.bind(this, itemView);
    daoWeakReference = new WeakReference<>(dao);
  }

  @Override
  public void bind(MessageList entity) {
    final MessageList item = entity;
    mName.setText(item.getName());
    mContent.setText(Html.fromHtml(Html.fromHtml(item.getContent()).toString()).toString());
    mTime.setText(TimeUtils.parsePartTime(item.getTime()));

    switch (item.getType()) {
      case Conf.OFFICIAL_MESSAGE:
        mLayout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            jumpToMessage(item, Conf.OFFICIAL_MESSAGE);
          }
        });
        mPicture.setBackgroundResource(R.mipmap.ic_launcher);
        break;
      case Conf.SYSTEM_MESSAGE:
        mLayout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            jumpToMessage(item, Conf.SYSTEM_MESSAGE);
          }
        });
        mPicture.setBackgroundResource(R.mipmap.default_avatar);
        break;
    }
  }

  private void jumpToMessage(MessageList item, int type) {
    if (daoWeakReference != null && daoWeakReference.get() != null) {
      daoWeakReference.get().update(item);
    }
    Context context = getContext();
    Intent intent = new Intent(context, MessageActivity.class);
    intent.putExtra(Conf.MESSAGE_TYPE, type);
    context.startActivity(intent);
  }

}
