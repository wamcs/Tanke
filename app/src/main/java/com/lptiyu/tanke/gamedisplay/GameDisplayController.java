package com.lptiyu.tanke.gamedisplay;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lptiyu.tanke.MainActivityController;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.pojo.GameDisplayEntity;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.ShaPrefer;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.CaptureActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/19
 *
 * @author ldx
 */
public class GameDisplayController extends FragmentController {

  //As a view controller
  GameDisplayFragment fragment;

  GameDisplayAdapter adapter;

  public static final int LOCATION_REQUEST_CODE = 0x1;

  public static final int SCANNER_REQUEST_CODE = 0x2;

  private LocationClient locationClient;

  public GameDisplayController(GameDisplayFragment fragment, MainActivityController controller, View view) {
    super(fragment, controller, view);
    this.fragment = fragment;
    this.adapter = fragment.getAdapter();
    ButterKnife.bind(this, view);
    init();
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  private void init() {
    if (!NetworkUtil.checkIsNetworkConnected()) {
      ToastUtil.TextToast(R.string.no_network);
      return;
    }

    inflateRecyclerView(null);

    initLocation();
  }


  private void inflateRecyclerView(@Nullable String loc) {

    fragment.loading(true);

    if (loc == null) {
      loc = ShaPrefer.getString(getString(R.string.main_page_location_key), null);
    }

    HttpService.getGameService().getGamePage(loc, 0)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Response<List<GameDisplayEntity>>>() {
          @Override
          public void call(Response<List<GameDisplayEntity>> response) {
            fragment.loading(false);
            if (response.getStatus() == Response.RESPONSE_OK) {
              updateList(response.getData());
            } else {
              ToastUtil.Exception(new Exception(
                  String.format("%s(status:%d)", response.getInfo(), response.getStatus())));
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            fragment.loading(false);
            fragment.loadingError();
          }
        });

  }

  private void initLocation() {
    LocationClientOption option = new LocationClientOption();
    option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
    option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
    option.setOpenGps(true);//可选，默认false,设置是否使用gps
    option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
    option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
    option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
    option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
    locationClient = new LocationClient(getContext());
    locationClient.setLocOption(option);


    Observable.create(
        new Observable.OnSubscribe<BDLocation>() {
          @Override
          public void call(final Subscriber<? super BDLocation> subscriber) {
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
        .observeOn(Schedulers.io())
        .filter(new Func1<BDLocation, Boolean>() {
          @Override
          public Boolean call(BDLocation bdLocation) {
            String location = ShaPrefer.getString(getString(R.string.main_page_location_key), "");
            // If location in ShaPreference is not equal to BDLocation, emit it.
            return !location.equals(bdLocation.getCity());
          }
        })
        .flatMap(new Func1<BDLocation, Observable<String>>() {
          @Override
          public Observable<String> call(final BDLocation bdLocation) {
            return HttpService.getGameService()
                .getSupportedCities()
                .contains(bdLocation.getCity())
                .map(new Func1<Boolean, String>() {
                  @Override
                  public String call(Boolean aBoolean) {
                    if (aBoolean) {
                      return bdLocation.getCity();
                    }
                    return null;
                  }
                });
          }
        })
        .filter(new Func1<String, Boolean>() {
          @Override
          public Boolean call(String s) {
            return s != null;
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
          @Override
          public void call(String s) {
            ShaPrefer.put(getString(R.string.main_page_location_key), s);
          }
        });
  }

  private void updateList(List<GameDisplayEntity> gameEntries) {
    if (adapter == null) {
      adapter = new GameDisplayAdapter((GameDisplayFragment) getFragment());
    }

    adapter.setData(gameEntries);
  }

  @OnClick(R.id.location)
  public void clickLocation() {
    startActivityForResult(new Intent(getContext(), LocationActivity.class), LOCATION_REQUEST_CODE);
  }

  @OnClick(R.id.scanner)
  public void scanner() {
    startActivityForResult(new Intent(getContext(), CaptureActivity.class), SCANNER_REQUEST_CODE);
  }

  public void onItemClick(GameDisplayEntity gameDisplayEntity, int position) {
    int id = ShaPrefer.getInt(String.format(getString(R.string.has_downloaded_mask), gameDisplayEntity.getId()), -1);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    switch (requestCode) {
      case LOCATION_REQUEST_CODE:
        String loc = data.getStringExtra(getString(R.string.main_page_location_key));
        if (loc == null) {
          break;
        }
        inflateRecyclerView(loc);
        break;
      case SCANNER_REQUEST_CODE:

        break;
      default:
    }
  }
}
