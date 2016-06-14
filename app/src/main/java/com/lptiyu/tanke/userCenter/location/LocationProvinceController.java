package com.lptiyu.tanke.userCenter.location;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ContextController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.utils.Display;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationProvinceController extends ContextController implements LocationScroll, BDLocationListener {

  @BindView(R.id.locate_activity_listview_province)
  ListView mListView;

  private LocationClient client;

  private List<City> list = new ArrayList<>();
  private LocationProvinceAdapter adapter;

  private LocateActivityController mController;

  private static final int LIST_WIDTH = Display.width();

  public LocationProvinceController(LocateActivityController controller, View view) {
    super(controller.getContext());
    mController = controller;
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {

    List<String> normalItems = LocationFileParser.loadProvinceList();

    adapter = new LocationProvinceAdapter(mController, "正在定位...", normalItems,true);

    adapter.setLocationListener(new LocationProvinceAdapter.LocationListener() {
      @Override
      public void onClick(View view, String city) {
        if (city.equals("正在定位...")){
          return;
        }
        setResultAndFinish(city);
      }

    });
    mListView.setAdapter(adapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (adapter.getItemViewType(position)) {

          case LocationProvinceAdapter.VIEW_TYPE_NORMAL:
            mController.moveToList(LocateActivityController.LOCATION_PAGE_CITY, String.valueOf(adapter.getItem(position)));
            break;

        }
      }
    });

    HttpService.getGameService().getSupportedCities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Response<List<City>>>() {
              @Override
              public void call(Response<List<City>> listResponse) {
                list.clear();
                list.addAll(listResponse.getData());
              }
            });
    locate();
  }

  private void locate() {
    client = new LocationClient(getContext());
    initLocation();
    client.registerLocationListener(this);
    client.start();
  }


  private void initLocation() {
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
    );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
    option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
    int span = 1000;
    option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
    option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
    option.setOpenGps(true);//可选，默认false,设置是否使用gps
    option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
    option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
    option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
    option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
    client.setLocOption(option);
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
    if (null != client) {
      client.stop();
      client.unRegisterLocationListener(this);
    }
  }

  private void setResultAndFinish(String city) {
    Intent intent = new Intent();
    intent.putExtra(Conf.USER_LOCATION, city);
    mController.setResult(Conf.REQUEST_CODE_LOCATION, intent);
    mController.finish();
  }

  @Override
  public void onReceiveLocation(BDLocation bdLocation) {
    String cityName;
    Log.d("lk",bdLocation.getLocType()+"");
    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
            || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
      cityName = bdLocation.getCity();
      ToastUtil.TextToast("定位成功");

    } else {
      ToastUtil.TextToast("定位失败");
      cityName = "武汉";
    }
    client.stop();

    for (City itemCity : list){
      if (itemCity.getName().equals(cityName)){
        adapter.setLocateItem(cityName,true);
        break;
      }
      if (list.indexOf(itemCity) == list.size()-1){
        adapter.setLocateItem(cityName,false);
      }
    }

  }
}
