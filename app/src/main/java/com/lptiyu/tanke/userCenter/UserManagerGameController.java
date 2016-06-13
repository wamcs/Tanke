package com.lptiyu.tanke.userCenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameManageEntity;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.Contents;
import com.lptiyu.zxinglib.android.Intents;
import com.lptiyu.zxinglib.android.encode.EncodeActivity;
import com.lptiyu.zxinglib.core.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/7
 *
 * @author ldx
 */
public class UserManagerGameController extends BaseListActivityController<GameManageEntity> {

  @BindView(R.id.swipe_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayout;

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private UserManagerAdapter adapter = new UserManagerAdapter();

  public UserManagerGameController(UserManagerGameActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    refreshTop();
    init();
  }

  private void init() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshTop();
      }
    });
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
  }

  @Override
  public Observable<List<GameManageEntity>> requestData(int page) {
    return
//        HttpService.getUserService()
//        .getManagerTask(Accounts.getId(), Accounts.getToken(), page)
//        .map(new Func1<Response<List<GameManageEntity>>, List<GameManageEntity>>() {
//          @Override
//          public List<GameManageEntity> call(Response<List<GameManageEntity>> gameManageEntityResponse) {
//            if (gameManageEntityResponse.getStatus() != Response.RESPONSE_OK) {
//              throw new RuntimeException(gameManageEntityResponse.getInfo());
//            }
//            return gameManageEntityResponse.getData();
//          }
//        });
        Observable.just(dummyDatas);
  }

  private static List<GameManageEntity> dummyDatas = new ArrayList<>();

  static {
    GameManageEntity entity = new GameManageEntity();
    entity.setImg("http://s10.sinaimg.cn/middle/4cafd049ta44ee96828f9&690");
    entity.setTitle("绝望坡惊魂");
    entity.setQrcode("jienbuvueiwi://asdfji/fajsdifjimmbbbcjiddji");
    dummyDatas.add(entity);
    GameManageEntity entity1 = new GameManageEntity();
    entity1.setImg("http://h.hiphotos.baidu.com/zhidao/pic/item/cc11728b4710b91217962a89c3fdfc0392452276.jpg");
    entity1.setTitle("毛主席的召唤");
    entity1.setQrcode("jienbuvueiwi://asdfji/fajsdifjimmbbbcjiddji");
    dummyDatas.add(entity1);
  }

  @NonNull
  @Override
  public BaseAdapter<GameManageEntity> getAdapter() {
    return adapter;
  }

  @Override
  public void onRefreshStateChanged(boolean isRefreshing) {
    swipeRefreshLayout.setRefreshing(isRefreshing);
  }

  @Override
  public void onError(Throwable t) {
    ToastUtil.TextToast(t.getMessage());
  }

  private static final class UserManagerAdapter extends BaseAdapter<GameManageEntity> {

    @Override
    public BaseViewHolder<GameManageEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
      return new GameManagerGameHolder(parent);
    }

  }

  static final class GameManagerGameHolder extends BaseViewHolder<GameManageEntity> {

    @BindView(R.id.image_view)
    ImageView gameImage;

    @BindView(R.id.detail_message)
    TextView gameDetailMessage;

    @BindView(R.id.task_title)
    TextView taskTitle;

    @BindView(R.id.game_title)
    TextView gameTitle;

    GameManageEntity entity;

    public GameManagerGameHolder(ViewGroup parent) {
      super(parent, R.layout.item_user_manager_game);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.game_rule)
    public void game_rule() {
      Toast.makeText(getContext(), "尚未实现", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.game_qrcode)
    public void game_qrcode() {
      Intent intent = new Intent(getContext(), EncodeActivity.class);
      intent.setAction(Intents.Encode.ACTION);
      intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.name());
      intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
      intent.putExtra(Intents.Encode.SHOW_CONTENTS, false);
      intent.putExtra(Intents.Encode.DATA, entity.getQrcode());
      getContext().startActivity(intent);
    }

    @Override
    public void bind(GameManageEntity entity) {
      this.entity = entity;
      Glide.with(itemView.getContext()).load(entity.getImg()).into(gameImage);
      gameTitle.setText(entity.getTitle());
    }
  }
}
