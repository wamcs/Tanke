package com.lptiyu.tanke.gamedetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.pojo.GAME_TYPE;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

  private long gameId = 1000000001;

  private GameDetailsEntity mGameDetailsEntity;

  public GameDetailsController(GameDetailsActivity activity, View view) {
    super(activity, view);
    gameId = getIntent().getIntExtra("data", Integer.MIN_VALUE);
    gameId = 1000000001;
    ButterKnife.bind(this, view);
    init();
  }

  private void bind(GameDetailsEntity entity) {
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

  private static GameDetailsEntity entity = new GameDetailsEntity();

  static {
    entity.setArea("华中科技大学");
    entity.setTitle("这是一个用来测试的活动");
    entity.setStartDate("2016-06-12");
    entity.setEndDate("2016-12-12");
    entity.setStartTime("08:30:00");
    entity.setEndTime("18:00:00");
    entity.setGameId(1);
    entity.setGameIntro("华中科技大学，是一所历史悠久的百年古校。" +
        "是一所位于湖北省武汉市的中国顶尖综合研究型大学 ，" +
        "学校前身为1952年设立的华中工学院、1907年德国医师埃里希·宝隆博士创立的上海德文医学堂以及1898年张之洞建立的湖北工艺学堂。2" +
        "000年由华中理工大学、同济医科大学和武汉城市建设学院合并成立。截止2014年4月，学校拥有214个硕士学位授权点，" +
        "175个博士学位授权点，35个博士后科研流动站；有一级学科国家重点学科7个，二级学科国家重点学科15个（内科学、外科学按三级），" +
        "国家重点（培育）学科7个，湖" +
        "北省重点学科34个。在校本科生32449人，研究生22837人，博士研究生6445人、硕士研究生16392人，各类留学生1745人。");
    entity.setRule("羽毛球运动的前身是板羽球（但现在羽毛球和板羽球已发展成不同的两种球类运动），也就是使用木板拍打扎有羽毛的球体（类似毽子），并让它避免落地的游戏，已有近二千年的历史，在古代欧洲、中国、日本都可以看见它的身影[1][2]。其中源自古希腊的一种板羽球，更发展出将实木板拍改为木制外框，并在中间绑着紧绷的羊皮，因具弹性而更容易拍打。这种游戏曾向东方传播至古代印度，并远达暹罗、中国与日本等地[3]。然而这类游戏的目的都只是让球尽量保持在空中而不落地，与现代羽毛球运动的精神大异其趣。\n" +
        "\n" +
        "19世纪中叶，印度西部的浦那出现了现代羽毛球运动，当时是以地名“浦那（Poona）”来称呼这种运动。驻在当地英国人颇为喜爱这种新运动，因而将它传回英国本土。1873年，在英国格洛斯特郡的伯明顿庄园举行了一场公开表演，引起许多人的注意，并逐渐传播开来。后来人们便以该场表演的庄园名称“伯明顿（Badminton）”来称呼这项运动，然而在华语地区该名称并未普及，而是依球具而称之为“羽毛球”运动。\n" +
        "\n" +
        "早期的英国羽毛球运动仍然使用来自印度的不成文规则，但因不够严谨而经常引发争议。1887年，“巴斯羽毛球俱乐部”加以研究改良，完成了第一部书面羽毛球运动规则[4]。\n" +
        "\n" +
        "1893年，英国羽毛球协会成立，重新修订并统一了羽毛球比赛的规则。1934年，第一个世界性的羽毛球组织—国际羽毛球联合会在英国成立。1981年与成立于1978年的世界羽毛球联合会合并，名称仍为国际羽毛球联合会。2006年9月24日，国际羽毛球联合会改名为羽毛球世界联合会，目前共有159个会员国或地区。");

    entity.setImg("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1464928750&di=5b64da2b21dfba927dba72deec1dface&src=http://photo.5imxbbs.com/forum/201405/26/120534fn0wlz8x7888jfwv.jpg");
    entity.setPeoplePlaying(123);
    entity.setType(GAME_TYPE.TEAMS);
  }

  private void init() {
    if (subscription != null) {
      subscription.unsubscribe();
      subscription = null;
    }

    progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

//    subscription = HttpService.getGameService().getGameDetails(gameId)
    Observable.just(entity)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<GameDetailsEntity, GameDetailsEntity>() {
          @Override
          public GameDetailsEntity call(GameDetailsEntity entity) {
//            if (gameDetailsEntityResponse.getStatus() != Response.RESPONSE_OK || gameDetailsEntityResponse.getData() == null) {
//              Timber.e("Network Error (%d, %s)", gameDetailsEntityResponse.getStatus(), gameDetailsEntityResponse.getInfo());
//              throw new IllegalStateException(gameDetailsEntityResponse.getInfo());
//            }
            return entity;
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
    progressDialog.show();
    HttpService.getGameService().downloadGameZip(
        "http://7xrl39.com1.z0.glb.clouddn.com/1000000001_2000000001_1464075128.zip")
        .map(new Func1<Response<ResponseBody>, File>() {
          @Override
          public File call(Response<ResponseBody> response) {
            String contentDisposition = response.headers().get("Content-Disposition");
            System.out.println("contentDisposition = " + contentDisposition);
            String[] files = contentDisposition.split("\"");

            File file = new File(DirUtils.getTempDirectory(), files[1]);

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
            progressDialog.dismiss();
            Intent intent = new Intent(getContext(), GamePlayingActivity.class);
            intent.putExtra(Conf.GAME_ID, gameId);
            startActivity(intent);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast(throwable.getMessage());
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
