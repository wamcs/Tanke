package com.lptiyu.tanke.userCenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

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

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    UserRewardAdapter adapter = new UserRewardAdapter();

    public UserRewardActivityController(UserRewardActivity activity, View view) {
      super(activity, view);
      ButterKnife.bind(this, view);

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
    public Observable<List<Reward>> requestData(int page) {
      return HttpService.getUserService().getRewards(Accounts.getId(), Accounts.getToken(), page)
          .map(new Func1<Response<List<Reward>>, List<Reward>>() {
            @Override
            public List<Reward> call(Response<List<Reward>> listResponse) {
              if (listResponse.getStatus() == Response.RESPONSE_OK) {
                throw new RuntimeException(listResponse.getInfo());
              }
              return listResponse.getData();
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
