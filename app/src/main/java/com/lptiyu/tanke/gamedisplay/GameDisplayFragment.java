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

import com.lptiyu.tanke.MainActivityController;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/21
 *
 * @author ldx
 */
public class GameDisplayFragment extends BaseFragment {

  private GameDisplayController controller;

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
  }

  public GameDisplayAdapter getAdapter() {
    return adapter;
  }

  public void changeToCurrentCity(String city) {
    new AlertDialog.Builder(getContext())
        .setMessage(String.format(getString(R.string.change_city_dialog_message), city))
        .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        })
        .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        })
    ;
  }

  public void loading(boolean enable) {
    if (enable) {
      ToastUtil.TextToast(getString(R.string.loading));
    }
  }

  public void loadingError() {
    ToastUtil.TextToast(getString(R.string.loading_error));
  }

  @Override
  public GameDisplayController getController() {
    return controller;
  }
}
