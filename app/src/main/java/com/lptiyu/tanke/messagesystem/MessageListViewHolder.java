package com.lptiyu.tanke.messagesystem;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
  @BindView(R.id.message_list_item_red_spot)
  ImageView mRedSpot;

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
//    mContent.setText(Html.fromHtml(Html.fromHtml(item.getContent()).toString()).toString());
    mContent.setText(item.getName());
    mTime.setText(TimeUtils.parseCompleteTime(item.getTime()));
    if (entity.getIsRead()) {
      mRedSpot.setVisibility(View.GONE);
    } else {
      mRedSpot.setVisibility(View.VISIBLE);
    }
    switch (item.getType()) {
      case Conf.MESSAGE_LIST_TYPE_OFFICIAL:
        mLayout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            jumpToMessage(item, Conf.MESSAGE_LIST_TYPE_OFFICIAL);
          }
        });
        mPicture.setBackgroundResource(R.mipmap.ic_launcher);
        break;
      case Conf.MESSAGE_LIST_TYPE_SYSTEM:
        mLayout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            jumpToMessage(item, Conf.MESSAGE_LIST_TYPE_SYSTEM);
          }
        });
        mPicture.setBackgroundResource(R.mipmap.default_avatar);
        break;
    }
  }

  private void jumpToMessage(MessageList item, int type) {
    if (!item.getIsRead()) {
      mRedSpot.setVisibility(View.GONE);
      if (daoWeakReference != null && daoWeakReference.get() != null) {
        item.setIsRead(true);
        daoWeakReference.get().update(item);
      }
    }
    Context context = getContext();
    Intent intent = new Intent(context, MessageActivity.class);
    intent.putExtra(Conf.MESSAGE_TYPE, type);
    context.startActivity(intent);
  }

}
