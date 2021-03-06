package com.lptiyu.tanke.gamedisplay;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lptiyu.tanke.MainActivityController;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseListFragmentController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.userCenter.ui.LocateActivity;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ShaPrefer;
import com.lptiyu.tanke.utils.thread;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/19
 *
 * @author ldx
 */
public class GameDisplayController extends BaseListFragmentController<GameDisplayEntity> {

    @BindView(R.id.location)
    CustomTextView mLocate;
    @BindView(R.id.no_data_imageview)
    ImageView mNoDataImageView;
    @BindView(R.id.ctv_title)
    CustomTextView ctv_title;

    //As a view controller
    GameDisplayFragment fragment;

    GameDisplayAdapter adapter;

    private City requestLocation;

    public static final int SCANNER_REQUEST_CODE = 0x2;

    private boolean isCheckData = false;
    private LocationClient locationClient;

    private Handler mHandler = new Handler();

    public GameDisplayController(GameDisplayFragment fragment, MainActivityController controller, View view) {
        super(fragment, controller, view);
        this.fragment = fragment;
        this.adapter = fragment.getAdapter();
        ButterKnife.bind(this, view);
        init();
    }

    /**
     * 判断网络，请求数据，并开始定位及定位接下来的逻辑
     */
    private void init() {
        requestLocation = ShaPrefer.getCity();
        //        refreshTop();
        loadNetWorkData();
        initLocation();
        ctv_title.setText(Accounts.getNickName() + "");
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            refreshTop();
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNetUnConnectDialog();
                        }
                    });
                }
            }, 100);
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                .OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
                loadNetWorkData();
            }
        });
    }

    public void changeCurrentCity(City c) {
        if (c == null) {
            Timber.e("You want to change the city, but the city is null");
            return;
        }
        requestLocation = c;
        refreshTop();
        ShaPrefer.put("", requestLocation);
    }


    @Override
    public void refreshBottom() {

        adapter.showFooter();
        super.refreshBottom();

    }

    @Override
    public void refreshTop() {
        adapter.refreshBanner();
        super.refreshTop();
    }

    @Override
    public void onRefreshStateChanged(boolean refreshing) {
        if (fragment != null) {
            fragment.loading(refreshing);
        }
    }

    @Override
    public void onError(Throwable t) {
        fragment.loadingError(t);
    }

    @Override
    public Observable<List<GameDisplayEntity>> requestData(int page) {
        return HttpService.getGameService()
                .getGamePage(Accounts.getId(), Accounts.getToken(), requestLocation.getId(), page)
                .map(new Func1<Response<List<GameDisplayEntity>>, List<GameDisplayEntity>>() {
                    @Override
                    public List<GameDisplayEntity> call(Response<List<GameDisplayEntity>> listResponse) {
                        if (listResponse.getStatus() != Response.RESPONSE_OK) {
                            thread.mainThread(new Runnable() {
                                @Override
                                public void run() {
                                    mNoDataImageView.setVisibility(View.VISIBLE);
                                }
                            });
                            return new ArrayList<>();
                        }
                        if (!isCheckData) {
                            if (listResponse.getData().isEmpty()) {
                                thread.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImageView.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                thread.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImageView.setVisibility(View.GONE);
                                    }
                                });
                            }
                            isCheckData = true;
                        }
                        return listResponse.getData();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public GameDisplayAdapter getAdapter() {
        return adapter;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClient = new LocationClient(getContext());
        locationClient.setLocOption(option);

        Observable.create(
                new Observable.OnSubscribe<BDLocation>() {
                    @Override
                    public void call(final Subscriber<? super BDLocation> subscriber) {
                        // 首先调用百度得到地址
                        subscriber.onStart();
                        locationClient.registerLocationListener(new BDLocationListener() {
                            @Override
                            public void onReceiveLocation(BDLocation bdLocation) {
                                if (bdLocation == null || bdLocation.getCity() == null) {
                                    subscriber.onError(new Exception("BDLocation is null."));
                                } else {
                                    subscriber.onNext(bdLocation);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .filter(new Func1<BDLocation, Boolean>() {
                    @Override
                    public Boolean call(BDLocation bdLocation) {
                        // 然后看是否和当前的地址保持一致
                        // 如果一致，什么都不用做，结束，如果不一致，就继续处理
                        String location = ShaPrefer.getString(getString(R.string.main_page_location_key), "");
                        // If location in ShaPreference is not equal to BDLocation, emit it.
                        String cityCode = bdLocation.getCityCode();
                        ShaPrefer.put(Conf.CITY_CODE, cityCode);
                        return !location.equals(cityCode);
                    }
                })
                .flatMap(new Func1<BDLocation, Observable<City>>() {
                    @Override
                    public Observable<City> call(final BDLocation bdLocation) {
                        // 现在我们知道BDLocation，也就是百度请求得到的地址，和当前地址不一致，
                        // 需要判断，当前地址是否是支持的地址
                        return HttpService.getGameService()
                                .getSupportedCities()
                                .map(new Func1<Response<List<City>>, City>() {
                                    @Override
                                    public City call(Response<List<City>> response) {
                                        if (response.getStatus() != 1 || response.getData() == null) {
                                            Timber.e("SupportedCities request failed. %d, %s",
                                                    response.getStatus(), response.getInfo());
                                            return null;
                                        }

                                        List<City> cities = response.getData();
                                        // 是否有bdLocation.getCity()
                                        for (City city : cities) {
                                            if (city.getName().equals(bdLocation.getCity())) {
                                                return city;
                                            }
                                        }
                                        return null;
                                    }
                                });
                    }
                })
                .filter(new Func1<City, Boolean>() {
                    @Override
                    public Boolean call(City s) {
                        // 如果合法
                        return s != null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<City>() {
                    @Override
                    public void call(City c) {
                        // 更新本地记录
                        fragment.changeToCurrentCityDialog(c);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        fragment.loadingError(throwable);
                    }
                });
    }

    @OnClick(R.id.location)
    void clickLocation() {
        startActivityForResult(new Intent(getContext(), LocateActivity.class), Conf.REQUEST_CODE_LOCATION);
    }

    @OnClick(R.id.scanner)
    void scanner() {
        //    startActivityForResult(playing Intent(getContext(), CaptureActivity.class), SCANNER_REQUEST_CODE);
    }

    void onItemClick(GameDisplayEntity gameDisplayEntity) {
        // TODO jump to different page
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Conf.REQUEST_CODE_LOCATION:
                City loc = data.getParcelableExtra(getString(R.string.main_page_location_key));
                if (loc == null) {
                    break;
                }
                requestLocation = loc;
                mLocate.setText(requestLocation.getName());
                isCheckData = false;
                refreshTop();
                break;
            case SCANNER_REQUEST_CODE:

                break;
            default:
        }
    }

}
