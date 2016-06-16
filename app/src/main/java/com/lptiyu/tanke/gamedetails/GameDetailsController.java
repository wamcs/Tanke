package com.lptiyu.tanke.gamedetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GAME_TYPE;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/2
 *
 * @author ldx
 */
public class GameDetailsController extends ActivityController {

  @BindView(R.id.game_detail_title)
  TextView mTextTitle;

  @BindView(R.id.image_cover)
  ImageView mImageCover;

  @BindView(R.id.team_type_text)
  TextView mTextTeamType;

  @BindView(R.id.game_detail_location)
  TextView mTextLocation;

  @BindView(R.id.game_detail_time)
  TextView mTextTime;

  @BindView(R.id.game_detail_intro)
  TextView mTextGameIntro;

  @BindView(R.id.game_detail_rule)
  TextView mTextRule;

  @BindView(R.id.num_playing)
  TextView mTextPeoplePlaying;

  ProgressDialog progressDialog;

  private Subscription subscription;

  private long gameId = 1000000001L;

  private GameDetailsEntity mGameDetailsEntity;

  public GameDetailsController(GameDetailsActivity activity, View view) {
    super(activity, view);
    gameId = getIntent().getLongExtra(Conf.GAME_ID, Integer.MIN_VALUE);
    ButterKnife.bind(this, view);
    init();
  }

  private void bind(GameDetailsEntity entity) {
    Timber.e(AppData.globalGson().toJson(entity));
    this.mGameDetailsEntity = entity;
    mTextTitle.setText(entity.getTitle());
    Glide.with(getActivity()).load(entity.getImg()).centerCrop().into(mImageCover);
    mTextGameIntro.setText(entity.getGameIntro());
    mTextRule.setText(entity.getRule());
    mTextLocation.setText(entity.getArea());
    mTextTeamType.setText(entity.getType() == GAME_TYPE.INDIVIDUALS ?
        getString(R.string.team_type_individule) : getString(R.string.team_type_team));
    mTextPeoplePlaying.setText(String.valueOf(entity.getPeoplePlaying()));
    parseTime(mTextTime, entity);
  }

  public void parseTime(final TextView textView, GameDetailsEntity entity) {
    Observable.just(entity).map(
        new Func1<GameDetailsEntity, String>() {
          @Override
          public String call(GameDetailsEntity entity) {
            return TimeUtils.parseTime(getContext(),
                entity.getStartDate(), entity.getEndDate(),
                entity.getStartTime(), entity.getEndTime());
          }
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
          @Override
          public void call(String s) {
            textView.setText(s);
          }
        });
  }

  private void init() {
    if (subscription != null) {
      subscription.unsubscribe();
      subscription = null;
    }

    progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    subscription = HttpService.getGameService().getGameDetails(gameId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Response<GameDetailsEntity>, GameDetailsEntity>() {
          @Override
          public GameDetailsEntity call(Response<GameDetailsEntity> response) {
            if (response.getStatus() != Response.RESPONSE_OK || response.getData() == null) {
              Timber.e("Network Error (%d, %s)", response.getStatus(), response.getInfo());
              throw new IllegalStateException(response.getInfo());
            }
            return response.getData();
          }
        })
        .subscribe(new Action1<GameDetailsEntity>() {
          @Override
          public void call(GameDetailsEntity gameDetailsEntity) {
            bind(gameDetailsEntity);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast(throwable.getMessage());
          }
        });

  }

  @OnClick(R.id.back_btn)
  public void goBackClicked() {
    finish();
  }

  @OnClick(R.id.share_btn)
  public void shareClicked() {

  }

  @OnClick(R.id.game_detail_ensure)
  public void ensureClicked() {
    if (mGameDetailsEntity == null) {
      Toast.makeText(getContext(), "获取游戏信息失败", Toast.LENGTH_SHORT).show();
      return;
    }

    progressDialog.show();
    HttpService.getGameService()
        .getIndividualGameZipUrl(Accounts.getId(),
            Accounts.getToken(),
            mGameDetailsEntity.getGameId())
        .flatMap(new Func1<Response<String>, Observable<retrofit2.Response<ResponseBody>>>() {
          @Override
          public Observable<retrofit2.Response<ResponseBody>> call(Response<String> response) {
            if (response.getStatus() != Response.RESPONSE_OK) {
              throw new RuntimeException(response.getInfo());
            }
            return HttpService.getGameService().downloadGameZip(response.getData());
          }
        })
        .map(new Func1<retrofit2.Response<ResponseBody>, File>() {
          @Override
          public File call(retrofit2.Response<ResponseBody> response) {
            String url = mGameDetailsEntity.getZipUrl();
            String[] segs = url.split("/");
            if (segs.length == 0) {
              throw new IllegalStateException("Wrong url can not split file name");
            }

            File file = new File(DirUtils.getTempDirectory(), segs[segs.length - 1]);

            try {
              if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Create file failed.");
              }
              BufferedSink sink = Okio.buffer(Okio.sink(file));
              sink.writeAll(response.body().source());
              sink.close();
              return file;
            } catch (IOException e) {
              if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
              }
              throw Exceptions.propagate(e);
            }
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<File>() {
          @Override
          public void call(File file) {
            Intent intent = new Intent(getContext(), GamePlayingActivity.class);
            intent.putExtra(Conf.GAME_ID, gameId);
            startActivity(intent);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast(throwable.getMessage());
          }
        }, new Action0() {
          @Override
          public void call() {
            progressDialog.dismiss();
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}
