package com.lptiyu.tanke.location;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ContextController;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.Display;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationProvinceController extends ContextController implements LocationScroll, BDLocationListener {

  @BindView(R.id.locate_activity_listview_province)
  ListView mListView;

  private LocationClient mLocationClient;

  private List<CityStruct> hotItems;
  private List<String> normalItems;
  private LocationProvinceAdapter adapter;

  private LocateUserActivityController mController;

  private static final int LIST_WIDTH = Display.width();

  public LocationProvinceController(LocateUserActivityController controller, View view) {
    super(controller.getContext());
    mController = controller;
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {

    hotItems = LocationFileParser.loadHotCityList();
    normalItems = LocationFileParser.loadProvinceList();

    adapter = new LocationProvinceAdapter(mController, "正在定位...", hotItems, normalItems);
    adapter.setHotCityItemLisitener(new LocationProvinceAdapter.HotCityItemListener() {
      @Override
      public void onClick(View view, CityStruct cityStruct) {
        setResultAndFinish(cityStruct);
      }
    });
    adapter.setLocationListener(new LocationProvinceAdapter.LocationListener() {
      @Override
      public void onClick(View view, CityStruct cityStruct) {
        if (null == cityStruct) {
          ToastUtil.TextToast(getString(R.string.is_locating));
          return;
        }
        setResultAndFinish(cityStruct);
      }
    });
    mListView.setAdapter(adapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (adapter.getItemViewType(position)) {

          case LocationProvinceAdapter.VIEW_TYPE_NORMAL:
            mController.moveToList(LocateUserActivityController.LOCATION_PAGE_CITY, String.valueOf(adapter.getItem(position)));
            break;

        }
      }
    });

    mLocationClient = new LocationClient(getContext());
    mLocationClient.registerLocationListener(this);

    LocationClientOption mLocationClientOption = new LocationClientOption();
    mLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
    mLocationClientOption.setIsNeedAddress(true);
    mLocationClient.setLocOption(mLocationClientOption);
  }

  @Override
  public void onReceiveLocation(BDLocation location) {
    if (location != null) {
      CityStruct struct = new CityStruct();
      struct.setmName(location.getCity());
      struct.setmCode(location.getCityCode());
      if (null != location.getProvince()) {
        StringBuilder sb = new StringBuilder(location.getProvince());
        sb.append(" ").append(location.getCity());
        adapter.setLocateItem(struct, sb.toString());
      }
      mLocationClient.stop();
    }
  }

  public void startLocate() {
    if (mLocationClient != null) {
      mLocationClient.start();
    }
  }

  @Override
  public void prepare(String msg) {
    if (null != adapter) {
      adapter.prepareData(msg);
    }
  }

  @Override
  public void smoothIn() {
    if (null != mListView) {
      mListView.animate().x(0).alpha(1f);
    }
  }

  @Override
  public void smoothOut() {
    if (null != mListView) {
      mListView.animate().x(-LIST_WIDTH).alpha(0f);
    }
  }

  public void onDestory() {
    if (null != mLocationClient) {
      if (mLocationClient.isStarted()) {
        mLocationClient.stop();
      }
      mLocationClient.unRegisterLocationListener(this);
    }
  }

  private void setResultAndFinish(CityStruct cityStruct) {
    Intent intent = new Intent();
    intent.putExtra(Conf.CITY_STRUCT, cityStruct);
    mController.setResult(Conf.RESULT_CODE_START_USER_LOCATE, intent);
    mController.finish();
  }

}
