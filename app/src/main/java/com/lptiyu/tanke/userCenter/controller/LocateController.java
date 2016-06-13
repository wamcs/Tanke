package com.lptiyu.tanke.userCenter.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.userCenter.adapter.LocateListAdapter;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class LocateController extends ActivityController implements BDLocationListener {

    @BindView(R.id.default_tool_bar_textview)
    TextView mTitle;

    @BindView(R.id.locate_activity_locate_city)
    TextView mLocateCity;
    @BindView(R.id.locate_activity_locate_error_text)
    TextView mLocateErrorText;

    @BindView(R.id.location_recycler_View)
    RecyclerView mRecyclerView;

    private List<City> list;
    private LocationClient client;
    private City city;

    public LocateController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init();
    }

    private void init(){
        city = new City();
        mTitle.setText("选择城市");
        mLocateErrorText.setVisibility(View.GONE);
        HttpService.getGameService().getSupportedCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<List<City>>>() {
                    @Override
                    public void call(Response<List<City>> listResponse) {
                        list = listResponse.getData();
                    }
                });
        locate();
        client.start();

        LinearLayoutManager manager =new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        LocateListAdapter adapter =new LocateListAdapter(list);
        adapter.setOnCityItemClickListener(new LocateListAdapter.OnCityItemClickListener() {
            @Override
            public void OnCityItemClick(int position) {
                city = list.get(position);
                clickLocation();
            }
        });

    }

    private void locate() {

        client = new LocationClient(getContext());
        initLocation();
        client.registerLocationListener(this);
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


    @OnClick(R.id.default_tool_bar_imageview)
    void back(){
        finish();
    }

    @OnClick(R.id.locate_activity_locate_layout)
    void clickLocation(){
        Intent intent = new Intent();
        intent.putExtra(Conf.USER_LOCATION,city.getName());
        getActivity().setResult(Conf.REQUEST_CODE_LOCATION,intent);
        finish();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            mLocateCity.setText(bdLocation.getCity());
            city.setName(bdLocation.getCity());
            city.setProvince(bdLocation.getProvince());
            city.setLatitude(bdLocation.getLatitude());
            city.setLongtitude(bdLocation.getLongitude());
            ToastUtil.TextToast("定位成功");

        } else {
            ToastUtil.TextToast("定位失败");
            mLocateCity.setText("武汉");
            city.setName("武汉");
            city.setProvince("湖北");
            city.setLatitude(30.515372);
            city.setLongtitude(114.419876);
        }
        client.stop();

        for (City itemCity : list){
            if (itemCity.getName().equals(city.getName())){
                mLocateErrorText.setVisibility(View.VISIBLE);
                break;
            }
        }
    }
}
