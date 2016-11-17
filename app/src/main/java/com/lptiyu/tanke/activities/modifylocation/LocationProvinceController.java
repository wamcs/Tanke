package com.lptiyu.tanke.activities.modifylocation;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ContextController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.CityStruct;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.LocationFileParser;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationProvinceController extends ContextController implements LocationScroll {

    @BindView(R.id.locate_activity_listview_province)
    ListView mListView;

    private LocationHelper locationHelper;

    private List<CityStruct> hotItems;
    private List<String> normalItems;
    private LocationProvinceAdapter adapter;

    private LocateUserActivityController mController;

    private static final int LIST_WIDTH = DisplayUtils.width();

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
                        mController.moveToList(LocateUserActivityController.LOCATION_PAGE_CITY, String.valueOf
                                (adapter.getItem(position)));
                        break;

                }
            }
        });

        locationHelper = new LocationHelper(getContext(), new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    CityStruct struct = new CityStruct();
                    struct.setmName(aMapLocation.getCity());
                    struct.setmCode(aMapLocation.getCityCode());
                    if (null != aMapLocation.getProvince()) {
                        StringBuilder sb = new StringBuilder(aMapLocation.getProvince());
                        sb.append(" ").append(aMapLocation.getCity());
                        adapter.setLocateItem(struct, sb.toString());
                    }
                    locationHelper.stopLocation();
                }
            }
        });
        locationHelper.startLocation();
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
        if (null != locationHelper) {
            locationHelper.stopLocation();
            locationHelper.onDestroy();
        }
    }

    private void setResultAndFinish(CityStruct cityStruct) {
        Intent intent = new Intent();
        intent.putExtra(Conf.CITY_STRUCT, cityStruct);
        mController.setResult(Conf.RESULT_CODE_START_USER_LOCATE, intent);
        mController.finish();
    }

}
