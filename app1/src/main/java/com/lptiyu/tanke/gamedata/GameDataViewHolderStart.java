package com.lptiyu.tanke.gamedata;

import android.net.Uri;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameDataEntity;
import com.lptiyu.tanke.pojo.GameDataStartEntity;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.widget.CustomTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-23
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataViewHolderStart extends BaseViewHolder<GameDataEntity> {

  @BindView(R.id.item_game_data_start_image)
  RoundedImageView roundedImageView;
  @BindView(R.id.item_game_data_start_game_title)
  CustomTextView gameTitle;
  @BindView(R.id.item_game_data_start_game_location)
  CustomTextView gameLocation;
  @BindView(R.id.item_game_data_start_game_complete_num)
  CustomTextView gameCompleteNum;
  @BindView(R.id.item_game_data_task_complete_time)
  CustomTextView gameCompleteTime;

  public GameDataViewHolderStart(ViewGroup parent) {
    super(fromResLayout(parent, R.layout.item_game_data_start));
    ButterKnife.bind(this, itemView);
  }

  @Override
  public void bind(final GameDataEntity entity) {
    GameDataStartEntity startEntity = ((GameDataStartEntity) entity);
    gameCompleteTime.setText(TimeUtils.getDateTime(startEntity.getStartTime()));
    HttpService.getGameService().getGameFinishedNum(startEntity.getGameId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Response<Integer>>() {
          @Override
          public void call(Response<Integer> integerResponse) {
            int num;
            if (integerResponse == null || integerResponse.getStatus() == 0) {
              num = 256;
            } else {
              num = integerResponse.getData();
            }
            gameCompleteNum.setText(String.format(getContext().getString(R.string.complete_person_num_formatter), num));
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            gameCompleteNum.setText(String.format(getContext().getString(R.string.complete_person_num_formatter), 256));
          }
        });

    Glide.with(getContext()).load(Uri.parse(startEntity.getGameImage())).error(R.drawable.default_pic).into(roundedImageView);
    gameTitle.setText(startEntity.getGameTitle());
    gameLocation.setText(startEntity.getGameLoc());
  }

}
