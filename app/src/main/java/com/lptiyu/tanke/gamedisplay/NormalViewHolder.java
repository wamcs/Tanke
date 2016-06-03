package com.lptiyu.tanke.gamedisplay;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GAME_TYPE;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 *
 * @author ldx
 */
public class NormalViewHolder extends BaseViewHolder<GameDisplayEntity> {

  @BindView(R.id.image_view)
  ImageView imageView;

  @BindView(R.id.title)
  TextView title;

  @BindView(R.id.team_type)
  TextView teamType;

  @BindView(R.id.location)
  TextView location;

  @BindView(R.id.time)
  TextView timeView;

  @BindView(R.id.tag)
  TextView tag;

  GameDisplayEntity gameDisplayEntity;

  private GameDisplayFragment fragment;

  NormalViewHolder(ViewGroup parent, GameDisplayFragment fragment) {
    super(fromResLayout(parent, R.layout.item_game_display));
    ButterKnife.bind(this, itemView);
    this.fragment = fragment;
  }

  public void bind(GameDisplayEntity entity) {
    this.gameDisplayEntity = entity;
    parseImage(entity);
    parseTitle(entity);
    parseLocation(entity);
    parseTime(entity);
    parseTag(entity);
    parseTeamType(entity);
  }

  private void parseImage(GameDisplayEntity entity) {
    Glide.with(fragment).load(entity.getImg()).into(imageView);
  }

  private void parseTitle(GameDisplayEntity entity) {
    title.setText(entity.getTitle());
  }

  private void parseLocation(GameDisplayEntity entity) {
    location.setText(entity.getArea());
  }

  private void parseTime(GameDisplayEntity entity) {
    Observable.just(entity).map(
        new Func1<GameDisplayEntity, String>() {
          @Override
          public String call(GameDisplayEntity entity) {
            return TimeUtils.parseTime(fragment.getContext(),
                entity.getStartDate(), entity.getEndDate(),
                entity.getStartTime(), entity.getEndTime());
          }
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
          @Override
          public void call(String s) {
            timeView.setText(s);
          }
        });

  }

  private void parseTag(GameDisplayEntity entity) {
    switch (entity.getRecommend()) {
      case RECOMMENDED:
        tag.setText("推荐");
        return;
      default:
    }

  }

  private void parseTeamType(GameDisplayEntity entity) {
    if (entity.getType() == GAME_TYPE.TEAMS) {
      teamType.setText(fragment.getString(R.string.team_type_team));
    } else {
      teamType.setText(fragment.getString(R.string.team_type_individule));
    }
  }

  @OnClick(R.id.item_root)
  void onItemClick() {
    GameDisplayController controller = fragment.getController();
    if (controller == null) {
      Timber.e("GameDisplayFragment get Controller is null");
      return;
    }

    controller.onItemClick(gameDisplayEntity);
  }
}
