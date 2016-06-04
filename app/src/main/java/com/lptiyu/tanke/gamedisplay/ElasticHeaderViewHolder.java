package com.lptiyu.tanke.gamedisplay;

import android.animation.ObjectAnimator;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 *
 * 控制RecyclerView的首部
 *
 * @author ldx
 */
public class ElasticHeaderViewHolder extends BaseViewHolder<GameDisplayEntity> {

  @BindView(R.id.left_image_view)
  CircularImageView leftImageView;

  @BindView(R.id.middle_image_view)
  CircularImageView middleImageView;

  @BindView(R.id.right_image_view)
  CircularImageView rightImageView;

  @BindView(R.id.left_title)
  TextView leftTitle;

  @BindView(R.id.middle_title)
  TextView middleTitle;

  @BindView(R.id.right_title)
  TextView rightTitle;

  GameDisplayFragment fragment;

  @BindView(R.id.jelly_view)
  ElasticHeaderLayout elasticHeaderLayout;

  ObjectAnimator animator = new ObjectAnimator();

  public ElasticHeaderViewHolder(ViewGroup parent, GameDisplayFragment fragment) {
    super(fromResLayout(parent, R.layout.item_game_display_header));
    ButterKnife.bind(this, itemView);
    this.fragment = fragment;
    init();
  }

  private void init() {
    animator.setInterpolator(new ElasticOutInterpolator());
    animator.setTarget(elasticHeaderLayout);
    animator.setPropertyName("percent");
    animator.setDuration(400);
  }

  public void setPercent(float percent) {
    elasticHeaderLayout.setPercent(percent);
  }

  public float getPercent() {
    return elasticHeaderLayout.getPercent();
  }

  public void smoothBack() {
    if (elasticHeaderLayout.getPercent() == 0 && !animator.isRunning()) {
      return;
    }
    animator.cancel();
    animator.setFloatValues(elasticHeaderLayout.getPercent(), 0);
    animator.start();
  }

  @Override
  public void bind(List<GameDisplayEntity> entities) {
    bindEntity(middleImageView, middleTitle, entities.get(0));
    bindEntity(leftImageView, leftTitle, entities.get(1));
    bindEntity(rightImageView, rightTitle, entities.get(2));
  }

  public void bindEntity(ImageView imageView, TextView textView, GameDisplayEntity entity) {
    Glide.with(fragment).load(entity.getImg())
        .centerCrop()
        .into(imageView);
    textView.setText(entity.getTitle());
  }
}
