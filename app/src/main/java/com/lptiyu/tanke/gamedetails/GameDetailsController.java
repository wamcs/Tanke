package com.lptiyu.tanke.gamedetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipScanner;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
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
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.dialog.ShareDialog;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
  CustomTextView mTextTitle;

  @BindView(R.id.image_cover)
  ImageView mImageCover;

  @BindView(R.id.team_type_text)
  CustomTextView mTextTeamType;

  @BindView(R.id.game_detail_location)
  CustomTextView mTextLocation;

  @BindView(R.id.game_detail_time)
  CustomTextView mTextTime;

  @BindView(R.id.game_detail_intro)
  CustomTextView mTextGameIntro;

  @BindView(R.id.game_detail_rule)
  CustomTextView mTextRule;

  @BindView(R.id.game_detail_ensure)
  CustomTextView mTextEnsure;

  @BindView(R.id.num_playing)
  CustomTextView mTextPeoplePlaying;

  AlertDialog mLoadingDialog;
  ProgressDialog progressDialog;

  private Subscription gameDetailsSubscription;

  private Observable<Boolean> isGameFinishObservable;
  private Subscription isGameFinishSubscription;

  private long gameId;
  private String tempGameZipUrl;
  private ShareDialog shareDialog;

  private GameZipScanner mGameZipScanner;

  private GameDetailsEntity mGameDetailsEntity;

  public GameDetailsController(GameDetailsActivity activity, View view) {
    super(activity, view);
    gameId = getIntent().getLongExtra(Conf.GAME_ID, Integer.MIN_VALUE);
    ButterKnife.bind(this, view);
    init();
  }

  private void bind(GameDetailsEntity entity) {
    this.mGameDetailsEntity = entity;
    mTextTitle.setText(entity.getTitle());
    mTextLocation.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
    mTextLocation.getPaint().setAntiAlias(true);//抗锯齿
    mTextLocation.setText(entity.getArea());
    mTextPeoplePlaying.setText(String.valueOf(entity.getPeoplePlaying()));
    parseTime(mTextTime, entity);
    if (entity.getType() == GAME_TYPE.INDIVIDUALS) {
      mTextTeamType.setVisibility(View.GONE);
    } else {
      mTextTeamType.setText(String.format(getString(R.string.team_game_formatter), entity.getMinNum()));
    }
    mTextGameIntro.setText(Html.fromHtml(Html.fromHtml(entity.getGameIntro()).toString()));
    mTextRule.setText(Html.fromHtml(Html.fromHtml(entity.getRule()).toString()));

    isGameFinishSubscription = isGameFinishObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
          @Override
          public void call(Boolean aBoolean) {
            if (aBoolean) {
              mTextEnsure.setText(getString(R.string.enter_game_share));
            } else {
              mTextEnsure.setText(getString(R.string.enter_game_play));
              isGameFinishedFromServer(gameId);
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            mTextEnsure.setText(getString(R.string.enter_game_play));
            isGameFinishedFromServer(gameId);
          }
        });

    Glide.with(getActivity()).load(entity.getImg()).error(R.mipmap.need_to_remove).centerCrop().into(mImageCover);
  }

  public void parseTime(final CustomTextView textView, GameDetailsEntity entity) {
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
    if (gameDetailsSubscription != null) {
      gameDetailsSubscription.unsubscribe();
      gameDetailsSubscription = null;
    }

    showLoadingDialog();
    mGameZipScanner = new GameZipScanner();
    progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    isGameFinishObservable = Observable.just(gameId)
        .map(new Func1<Long, Boolean>() {
          @Override
          public Boolean call(Long id) {
            return RecordsUtils.isGameFinishedFromDisk(id);
          }
        });

    //prepare for the network request
    gameDetailsSubscription = HttpService.getGameService().getGameDetails(gameId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Response<GameDetailsEntity>, GameDetailsEntity>() {
          @Override
          public GameDetailsEntity call(Response<GameDetailsEntity> response) {
            if (response.getStatus() != Response.RESPONSE_OK || response.getData() == null) {
              mLoadingDialog.dismiss();
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
            mLoadingDialog.dismiss();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            mLoadingDialog.dismiss();
            ToastUtil.TextToast(throwable.getMessage());
          }
        });
  }

  /**
   * This method is to load running records from server
   * if the local records is damage or the records return the game is not finished
   * reload the records from server and check
   *
   * @param gameId target game
   */
  private void isGameFinishedFromServer(long gameId) {
    final long gameIdInner = gameId;
    HttpService.getGameService()
        .getRunningRecords(Accounts.getId(), gameId, mGameDetailsEntity.getType().value)
        .map(new Func1<Response<List<RunningRecord>>, Boolean>() {
          @Override
          public Boolean call(Response<List<RunningRecord>> listResponse) {
            if (listResponse == null || listResponse.getStatus() == 0) {
              return false;
            }
            List<RunningRecord> records = listResponse.getData();
            if (records == null || records.size() == 0) {
              return false;
            }
            RecordsUtils.cacheServerRecordsInLocal(gameIdInner, records);
            for (RunningRecord record : records) {
              if (record.getState() == RunningRecord.RECORD_TYPE.GAME_FINISH) {
                return true;
              }
            }
            return false;
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
          @Override
          public void call(Boolean aBoolean) {
            if (aBoolean) {
              mTextEnsure.setText(getString(R.string.enter_game_share));
            } else {
              mTextEnsure.setText(getString(R.string.enter_game_play));
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Timber.e(throwable, "here");
          }
        });
  }

  /**
   * This method is to check whether the game zip has been download
   * and the game zip is out of date or not
   * <p>
   * If the unix timestamp is not match with server's, then must
   * clean the game records、zip package、game dir etc
   */
  private void checkDiskAndNextStep() {
    // first scan the dir and check the zip is exist or not
    if (mGameZipScanner == null) {
      mGameZipScanner = new GameZipScanner();
    }
    long timeStamp = mGameZipScanner.getGameZipFileTimeStamp(gameId);
    if (timeStamp == GameZipScanner.ZIP_FILE_NOT_FOUND_TIMESTAMP) {
      // it means that the game zip is not exist, need download
      startGetGameZipUrlAndDownload();
    } else {
      // the zip is exist, then check is it out of date
      //TODO : invoke api to check whether the zio is out of date
      startPlayingGame();
    }
  }

  private void startGetGameZipUrlAndDownload() {
    mGameDetailsEntity.setGameId(gameId);
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
            String data = response.getData();
            if (data == null || data.length() == 0) {
              Timber.e("当前游戏并未提供下载连接");
              throw new RuntimeException(response.getInfo());
            }
            tempGameZipUrl = response.getData();
            return HttpService.getGameService().downloadGameZip(response.getData());
          }
        })
        .map(new Func1<retrofit2.Response<ResponseBody>, File>() {
          @Override
          public File call(retrofit2.Response<ResponseBody> response) {
            String url = tempGameZipUrl;
            System.out.println("url = " + url);
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
            mGameZipScanner.reload();
            startPlayingGame();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast("游戏包下载出错");
            progressDialog.dismiss();
          }
        }, new Action0() {
          @Override
          public void call() {
            progressDialog.dismiss();
          }
        });
  }

  private void startPlayingGame() {
    Intent intent = new Intent(getContext(), GamePlayingActivity.class);
    intent.putExtra(Conf.GAME_ID, gameId);
    intent.putExtra(Conf.GAME_TYPE, mGameDetailsEntity.getType());
    if (mGameDetailsEntity.getType() == GAME_TYPE.TEAMS) {
      //TODO : need pass team id to GamePlayingActivity when the team game open
    }
    startActivity(intent);
  }

  private void showLoadingDialog() {
    if (mLoadingDialog == null) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
      CustomTextView textView = ((CustomTextView) view.findViewById(R.id.loading_dialog_textview));
      textView.setText(getString(R.string.loading));
      mLoadingDialog = new AlertDialog.Builder(getActivity())
          .setCancelable(false)
          .setView(view)
          .create();
    }
    mLoadingDialog.show();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @OnClick(R.id.back_btn)
  public void goBackClicked() {
    finish();
  }

  @OnClick(R.id.share_btn)
  public void shareClicked() {
    if (null == shareDialog) {
      shareDialog = new ShareDialog(getContext());
      shareDialog.setShareContent(
          String.format("我正在玩定向AR游戏 %s，一起来探秘吧", mGameDetailsEntity.getTitle()),
          Html.fromHtml(Html.fromHtml(mGameDetailsEntity.getGameIntro()).toString()).toString(),
          mGameDetailsEntity.getImg(),
          mGameDetailsEntity.getShareUrl());
    }
    shareDialog.show();
  }

  @OnClick(R.id.game_detail_location)
  public void startLocationDetailMap() {
    Intent intent = new Intent(getActivity(), GameDetailsLocationActivity.class);
    intent.putExtra(Conf.LATITUDE, Double.valueOf(mGameDetailsEntity.getLatitude()));
    intent.putExtra(Conf.LONGITUDE, Double.valueOf(mGameDetailsEntity.getLongitude()));
    startActivity(intent);
  }

  @OnClick(R.id.game_detail_ensure)
  public void ensureClicked() {
    if (mGameDetailsEntity == null) {
      ToastUtil.TextToast("获取游戏信息失败");
      return;
    }
    if (mGameDetailsEntity.getType() == GAME_TYPE.TEAMS) {
      ToastUtil.TextToast("团队赛正在开发中");
      return;
    }
    checkDiskAndNextStep();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (gameDetailsSubscription != null && !gameDetailsSubscription.isUnsubscribed()) {
      gameDetailsSubscription.unsubscribe();
    }
    if (isGameFinishSubscription != null && !isGameFinishSubscription.isUnsubscribed()) {
      isGameFinishSubscription.unsubscribe();
    }
  }
}
