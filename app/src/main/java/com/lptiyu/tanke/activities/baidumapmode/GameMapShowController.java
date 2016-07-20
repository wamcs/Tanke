package com.lptiyu.tanke.activities.baidumapmode;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.widget.TickView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/7/20
 * email:kaili@hustunique.com
 */
public class GameMapShowController extends ActivityController {


  @BindView(R.id.map_view)
  TextureMapView mapView;
  @BindView(R.id.tick_view)
  TickView mTickView;
  @BindView(R.id.zoom_in)
  ImageView mZoomIn;
  @BindView(R.id.zoom_out)
  ImageView mZoomOut;


  MapHelper mapHelper;


  public GameMapShowController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this,view);
  }

  private void init(){
    mapHelper = new MapHelper(getActivity(), mapView);
  }




  @OnClick(R.id.zoom_in)
  void zoomIn() {
    if (mapHelper.mapZoomIn()) {
      mZoomOut.setClickable(true);
    } else {
      mZoomIn.setClickable(false);
    }
  }

  @OnClick(R.id.zoom_out)
  void zoomOut() {
    if (mapHelper.mapZoomOut()) {
      mZoomIn.setClickable(true);
    } else {
      mZoomOut.setClickable(false);
    }
  }

}
