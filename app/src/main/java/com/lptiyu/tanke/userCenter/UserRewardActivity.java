package com.lptiyu.tanke.userCenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.Reward;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.thread;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class UserRewardActivity extends BaseActivity {

  private UserRewardActivityController controller;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_reward);
    controller = new UserRewardActivityController(this,
        getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return controller;
  }

  public static class UserRewardActivityController extends BaseListActivityController<Reward> {

    @BindView(R.id.default_tool_bar_textview)
    TextView mToolbarTitle;
    @BindView(R.id.no_data_imageview)
    ImageView mNoDataImage;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private UserRewardAdapter adapter = new UserRewardAdapter();

    public UserRewardActivityController(UserRewardActivity activity, View view) {
      super(activity, view);
      ButterKnife.bind(this, view);
      mToolbarTitle.setText(getString(R.string.user_reward));
      swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          refreshTop();
        }
      });

      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setAdapter(adapter);
      refreshTop();
    }

    @Override
    public Observable<List<Reward>> requestData(int page) {
      return HttpService.getUserService().getRewards(Accounts.getId(), Accounts.getToken(), page)
          .map(new Func1<Response<List<Reward>>, List<Reward>>() {
            @Override
            public List<Reward> call(Response<List<Reward>> listResponse) {
              if (listResponse.getStatus() != Response.RESPONSE_OK) {
                throw new RuntimeException(listResponse.getInfo());
              }
              List<Reward> result = listResponse.getData();
              if (result.size() == 0) {
                if (mNoDataImage != null) {
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
    public BaseAdapter<Reward> getAdapter() {
      return adapter;
    }

    @Override
    public void onRefreshStateChanged(boolean isRefreshing) {
      swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void back() {
      finish();
    }

    @Override
    public boolean onBackPressed() {
      back();
      return true;
    }

    @Override
    public void onError(Throwable t) {
      ToastUtil.TextToast(t.getMessage());
    }
  }

  public static class UserRewardAdapter extends BaseAdapter<Reward> {

    @Override
    public BaseViewHolder<Reward> onCreateViewHolder(ViewGroup parent, int viewType) {
      return new UserRewardHolder(parent, R.layout.item_reward);
    }
  }

  public static class UserRewardHolder extends BaseViewHolder<Reward> {

    @BindView(R.id.reward_details)
    TextView rewardDetails;

    @BindView(R.id.reward_time)
    TextView rewardTime;

    @BindView(R.id.reward_title)
    TextView rewardTitle;

    public UserRewardHolder(ViewGroup parent, int layoutId) {
      super(parent, layoutId);
      ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(Reward entity) {
      rewardTitle.setText(entity.getName());
      rewardDetails.setText(entity.getContent());
    }
  }
}
