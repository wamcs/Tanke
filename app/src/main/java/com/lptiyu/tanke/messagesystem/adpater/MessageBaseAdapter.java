package com.lptiyu.tanke.messagesystem.adpater;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.widget.CustomTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class MessageBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private MessageViewHolderClickListener messageViewHolderClickListener;

  protected Pattern pattern = Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\\\._-]+\\\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\\\&%_\\\\./-~-]*)?");

  class MessageViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.message_item_image_view)
    ImageView mImage;
    @BindView(R.id.message_item_title)
    CustomTextView mTitle;
    @BindView(R.id.message_item_time)
    CustomTextView mTime;
    @BindView(R.id.message_item_content)
    CustomTextView mContent;

    public MessageViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.layout_message_item_right_root)
    public void onItemClicked() {
      if (messageViewHolderClickListener != null) {
        messageViewHolderClickListener.onMessageItemClicked(getAdapterPosition());
      }
    }

  }

  class TimeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.message_item_time_text)
    TextView mTime;

    public TimeViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public MessageBaseAdapter(MessageViewHolderClickListener listener) {
    messageViewHolderClickListener = listener;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }

  //为string中url添加链接
  protected SpannableString addURLLink(String message) {
    Matcher matcher = pattern.matcher(message);
    if (!matcher.find()) {
      return new SpannableString(message);
    }
    SpannableString spannableString = new SpannableString(message);
    int num = matcher.groupCount();
    for (int i = 0; i < num; i++) {
      String url = matcher.group(i);
      int start = matcher.start(i);
      int end = matcher.end(i);
      spannableString.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return spannableString;
  }

  public interface MessageViewHolderClickListener {
    public void onMessageItemClicked(int position);
  }
}
