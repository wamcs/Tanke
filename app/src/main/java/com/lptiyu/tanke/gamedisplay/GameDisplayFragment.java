package com.lptiyu.tanke.gamedisplay;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.MainActivityController;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/21
 *
 * @author ldx
 */
public class GameDisplayFragment extends BaseFragment {

  private GameDisplayController controller;

  @BindView(R.id.relative_layout)
  RelativeLayout toolBar;
  @BindView(R.id.load_data_error_imageview)
  ImageView mNoDataImage;

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;

  private GameDisplayAdapter adapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_game_display, container, false);
    ButterKnife.bind(this, view);
    init();
    controller = new GameDisplayController(this, (MainActivityController) getActivityController(), view);
    return view;
  }

  private void init() {
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter = new GameDisplayAdapter(this));
    recyclerView.addItemDecoration(new ElasticItemDecoration(getContext()));
    ElasticTouchListener listener = new ElasticTouchListener();
    recyclerView.addOnItemTouchListener(listener);
    listener.setOnRefreshListener(new ElasticTouchListener.OnRefreshListener() {
      @Override
      public void onRefresh() {
        if (!controller.isRefreshing()) {
          controller.refreshTop();
        }
      }
    });
  }

  public GameDisplayAdapter getAdapter() {
    return adapter;
  }

  public void changeToCurrentCityDialog(final City city) {
    new AlertDialog.Builder(getContext())
        .setMessage(String.format(getString(R.string.change_city_dialog_message), city.getName()))
        .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            controller.changeCurrentCity(city);
          }
        })
        .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Timber.v("用户取消了City(%s)的修改", city.getName());
          }
        })
    ;
  }

  public void loading(boolean enable) {
    if (enable) {
      if (mNoDataImage != null) {
        mNoDataImage.setVisibility(View.GONE);
      }
      ToastUtil.TextToast(getString(R.string.loading));
    }
  }

  public void loadingError(Throwable t) {
    if (mNoDataImage != null) {
      if (adapter.getItemCount() == 0) {
        mNoDataImage.setVisibility(View.VISIBLE);
      }
    }
//    ToastUtil.TextToast(t.getMessage());
  }

  @Override
  public GameDisplayController getController() {
    return controller;
  }

  @OnClick(R.id.load_data_error_imageview)
  public void reLoadDataWhenError() {
    if (!controller.isRefreshing()) {
      controller.refreshTop();
    }
  }
}
