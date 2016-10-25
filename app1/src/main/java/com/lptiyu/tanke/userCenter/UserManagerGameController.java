package com.lptiyu.tanke.userCenter;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.gamedetails.GameDetailsLocationActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameManageEntity;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.thread;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.dialog.TextDialog;
import com.lptiyu.zxinglib.android.Contents;
import com.lptiyu.zxinglib.android.Intents;
import com.lptiyu.zxinglib.android.encode.EncodeActivity;
import com.lptiyu.zxinglib.core.BarcodeFormat;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/7
 *
 * @author ldx
 */
public class UserManagerGameController extends BaseListActivityController<GameManageEntity> {

  @BindView(R.id.default_tool_bar_textview)
  TextView mToolbarTitle;
  @BindView(R.id.no_data_imageview)
  ImageView mNoDataImage;
  @BindView(R.id.swipe_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayout;

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private LinearLayoutManager mLayoutManager;

  private UserManagerAdapter adapter = new UserManagerAdapter();

  public UserManagerGameController(UserManagerGameActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    refreshTop();
    init();
  }

  private void init() {
    mToolbarTitle.setText(getString(R.string.user_manager));
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshTop();
      }
    });
    recyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        int totalItemCount = mLayoutManager.getItemCount();
        if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
          refreshBottom();
        }
      }
    });

  }

  @Override
  public Observable<List<GameManageEntity>> requestData(int page) {
    return
        HttpService.getUserService()
            .getManagerTask(Accounts.getId(), Accounts.getToken(), page)
            .map(new Func1<Response<List<GameManageEntity>>, List<GameManageEntity>>() {
              @Override
              public List<GameManageEntity> call(Response<List<GameManageEntity>> gameManageEntityResponse) {
                if (gameManageEntityResponse.getStatus() != Response.RESPONSE_OK) {
                  throw new RuntimeException(gameManageEntityResponse.getInfo());
                }
                List<GameManageEntity> result = gameManageEntityResponse.getData();
                if (result.size() == 0) {
                  if (mNoDataImage != null && mLayoutManager.getItemCount() <= 0) {
                    thread.mainThread(new Runnable() {
                      @Override
                      public void run() {
                        mNoDataImage.setVisibility(View.VISIBLE);
                      }
                    });
                  }
                } else {
                  if (mNoDataImage != null) {
                    thread.mainThread(new Runnable() {
                      @Override
                      public void run() {
                        mNoDataImage.setVisibility(View.GONE);
                      }
                    });
                  }
                }
                return result;
              }
            });
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

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
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
    RoundedImageView gameImage;

    @BindView(R.id.task_title)
    CustomTextView taskTitle;

    @BindView(R.id.game_title)
    CustomTextView gameTitle;

    @BindView(R.id.task_location)
    CustomTextView taskLocation;

    GameManageEntity entity;

    TextDialog mTextDialog;

    public GameManagerGameHolder(ViewGroup parent) {
      super(parent, R.layout.item_user_manager_game);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.task_location)
    public void game_rule() {
      Intent intent = new Intent(getContext(), GameDetailsLocationActivity.class);
      intent.putExtra(Conf.LATITUDE, Double.valueOf(entity.getLatitude()));
      intent.putExtra(Conf.LONGITUDE, Double.valueOf(entity.getLongtitude()));
      getContext().startActivity(intent);
    }

    @OnClick(R.id.game_pass_rule)
    void showGamePassRule() {

      if (mTextDialog == null) {
           mTextDialog = new TextDialog(getContext());
           mTextDialog.ensureButton.setVisibility(View.GONE);
           mTextDialog.cancelButton.setText("关闭");
           mTextDialog.setmListener(new TextDialog.OnTextDialogButtonClickListener() {
          @Override
          public void onPositiveClicked() {
          }

          @Override
          public void onNegtiveClicked() {
            mTextDialog.dismiss();
          }
        });

      }
      mTextDialog.isCancelable(false);//点击窗口外不能关闭
      mTextDialog.show(entity.getContent());
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
      gameTitle.setText(entity.getGameTitle());
      taskTitle.setText(entity.getTitle());
      taskLocation.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
      taskLocation.setText(entity.getAddress());
    }

  }
}
