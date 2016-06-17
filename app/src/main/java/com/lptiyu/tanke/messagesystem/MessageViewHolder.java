package com.lptiyu.tanke.messagesystem;

import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;

import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/16
 *
 * @author ldx
 */
public class MessageViewHolder extends BaseViewHolder<MessageEntity> {

  public MessageViewHolder(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_message_layout));
    ButterKnife.bind(this, itemView);
    init();
  }

  private void init() {

  }

  @Override
  public void bind(MessageEntity entity) {

  }
}
