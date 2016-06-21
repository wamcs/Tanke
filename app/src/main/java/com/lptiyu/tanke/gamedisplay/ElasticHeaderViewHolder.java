package com.lptiyu.tanke.gamedisplay;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.gamedetails.GameDetailsActivity;
import com.lptiyu.tanke.gamedetails.GameDetailsController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
  CustomTextView leftTitle;

  @BindView(R.id.middle_title)
  CustomTextView middleTitle;

  @BindView(R.id.right_title)
  CustomTextView rightTitle;

  GameDisplayFragment fragment;

  @BindView(R.id.jelly_view)
  ElasticHeaderLayout elasticHeaderLayout;

  ObjectAnimator animator = new ObjectAnimator();

  List<GameDisplayEntity> gameDisplayEntities;

  public ElasticHeaderViewHolder(ViewGroup parent, GameDisplayFragment fragment) {
    super(fromResLayout(parent, R.layout.item_game_display_header));
    ButterKnife.bind(this, itemView);
    this.fragment = fragment;
    init();
  }

  @OnClick(R.id.left_image_view)
  public void left_image_view() {
    jump(1);
  }

  @OnClick(R.id.middle_image_view)
  public void middle_image_view() {
    jump(0);
  }

  @OnClick(R.id.right_image_view)
  public void right_image_view() {
    jump(2);
  }

  public void jump(int index) {
    if (gameDisplayEntities == null || gameDisplayEntities.size() < index + 1 || gameDisplayEntities.get(index) == null) {
      return;
    }
    Intent intent = new Intent(getContext(), GameDetailsActivity.class);
    intent.putExtra(Conf.GAME_ID, gameDisplayEntities.get(index).getId());
    getContext().startActivity(intent);
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

  public void bind(List<GameDisplayEntity> entities) {
    if (entities == null || entities.size() == 0) {
      hideHeader();
      return;
    }
    showHeader();
    bindEntity(middleImageView, middleTitle, entities.size() < 1 ? null : entities.get(0));
    bindEntity(leftImageView, leftTitle, entities.size() < 2 ? null : entities.get(1));
    bindEntity(rightImageView, rightTitle, entities.size() < 3 ? null : entities.get(2));
    this.gameDisplayEntities = entities;
  }

  public void bindEntity(ImageView imageView, TextView textView, GameDisplayEntity entity) {
    if (entity == null) {
      return;
    }
    Glide.with(fragment).load(entity.getImg())
        .into(imageView);
    textView.setText(entity.getTitle());
  }

  @Override
  public void bind(GameDisplayEntity entity) {

  }

  private void hideHeader() {
    if (middleImageView != null) {
      middleImageView.setVisibility(View.GONE);
    }
    if (middleTitle != null) {
      middleTitle.setVisibility(View.GONE);
    }
    if (leftImageView != null) {
      leftImageView.setVisibility(View.GONE);
    }
    if (leftTitle != null) {
      leftTitle.setVisibility(View.GONE);
    }
    if (rightImageView != null) {
      rightImageView.setVisibility(View.GONE);
    }
    if (rightTitle != null) {
      rightTitle.setVisibility(View.GONE);
    }
  }

  private void showHeader() {
    if (middleImageView != null) {
      middleImageView.setVisibility(View.VISIBLE);
    }
    if (middleTitle != null) {
      middleTitle.setVisibility(View.VISIBLE);
    }
    if (leftImageView != null) {
      leftImageView.setVisibility(View.VISIBLE);
    }
    if (leftTitle != null) {
      leftTitle.setVisibility(View.VISIBLE);
    }
    if (rightImageView != null) {
      rightImageView.setVisibility(View.VISIBLE);
    }
    if (rightTitle != null) {
      rightTitle.setVisibility(View.VISIBLE);
    }
  }
}
