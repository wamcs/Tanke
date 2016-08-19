package com.lptiyu.tanke.initialization.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.location.CityStruct;
import com.lptiyu.tanke.location.LocateUserActivity;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.utils.ThirdLoginHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.dialog.DatePickerDialog;
import com.lptiyu.tanke.widget.dialog.ImageChooseDialog;
import com.lptiyu.tanke.widget.dialog.NumberPickerDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.baidu.location.LocationClientOption.LocationMode;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class CompleteInformationController extends ActivityController implements BDLocationListener {

    @BindView(R.id.complete_next_button)
    LinearLayout mCompleteNextButton;

    @BindView(R.id.complete_avatar_image_view)
    CircularImageView mAvatarImageView;

    @BindView(R.id.complete_nickname_text)
    EditText mNicknameText;
    @BindView(R.id.complete_gender_male_button)
    TextView mGenderMaleButton;
    @BindView(R.id.complete_gender_female_button)
    TextView mGenderFemaleButton;

    @BindView(R.id.complete_height_text)
    TextView mHeightText;
    @BindView(R.id.complete_birthday_text)
    TextView mBirthdayText;
    @BindView(R.id.complete_weight_text)
    TextView mWeightText;
    @BindView(R.id.complete_location_text)
    TextView mLocationText;

    @BindView(R.id.complete_location_button)
    ImageView mLocationButton;

    private ImageChooseDialog mImageChooseDialog;
    private NumberPickerDialog mNumberPickerDialog;
    private DatePickerDialog mDatePickerDialog;

    private final static int MALE_TYPE = 1;
    private final static int FEMALE_TYPE = 2;

    private Context context;
    private LocationClient client;
    private City city;

    private int mUserGender;
    private File mImageFile;
    private UserDetails mUserDetails;
    private boolean isAvatarSet;

    private static final String DEFAULT_NICKNAME = "这就是昵称";
    private static final String DEFAULT_ADDRESS = "武汉市";
    private static final String DEFAULT_WEIGHT = "55";
    private static final String DEFAULT_HEIGHT = "175";
    private static final String DEFAULT_BIRTHDAY = "1990-01-01";

    public CompleteInformationController(AppCompatActivity activity, View view) {
        super(activity, view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        context = getContext();
        city = new City();
        initDefaultInfo();
        mLocationText.setText("定位中");
        locate();
        mLocationButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Intent intent = new Intent(getActivity(), LocateUserActivity.class);
                        startActivityForResult(intent, Conf.REQUEST_CODE_START_USER_LOCATE);
                        mLocationButton.animate().scaleX(0.9f).scaleY(0.9f).setInterpolator(new BounceInterpolator())
                                .setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        mLocationButton.animate().scaleY(1.0f)
                                .scaleX(1.0f).setInterpolator(new BounceInterpolator())
                                .setDuration(100).start();
                        break;
                }
                return true;
            }
        });

    }

    private void initDefaultInfo() {
        mUserDetails = new UserDetails();
        mUserDetails.setAddress(DEFAULT_ADDRESS);
        mUserDetails.setBirthday(DEFAULT_BIRTHDAY);
        mUserDetails.setHeight(DEFAULT_HEIGHT);
        mUserDetails.setWeight(DEFAULT_WEIGHT);
        mUserDetails.setNickname(DEFAULT_NICKNAME);
        mUserGender = MALE_TYPE;

        changeGender(MALE_TYPE);
        mNicknameText.setText(DEFAULT_NICKNAME);
        mWeightText.setText(DEFAULT_WEIGHT);
        mHeightText.setText(DEFAULT_HEIGHT);
        mLocationText.setText(DEFAULT_ADDRESS);
        mBirthdayText.setText(DEFAULT_BIRTHDAY);

        initDataFromThirdLogin();
    }

    private void initDataFromThirdLogin() {
        UserDetails userDetails = ThirdLoginHelper.getUserDetail();
        String nickName = userDetails.getNickname();
        if (nickName != null) {
            mUserDetails.setNickname(nickName);
            mNicknameText.setText(nickName);
        }
        String avatar = userDetails.getAvatar();
        if (avatar != null) {
            mUserDetails.setAvatar(avatar);
            mAvatarImageView.setImageURI(Uri.parse(avatar));
            isAvatarSet = true;
        }
        if (userDetails.getSex() != null) {
            switch (userDetails.getSex()) {
                case "男":
                    changeGender(MALE_TYPE);
                    mUserGender = MALE_TYPE;
                    break;
                case "女":
                    changeGender(FEMALE_TYPE);
                    mUserGender = FEMALE_TYPE;
                    break;
                default:
                    changeGender(MALE_TYPE);
                    mUserGender = MALE_TYPE;
                    break;
            }
        }
    }

    private void changeGender(int type) {
        switch (type) {
            case MALE_TYPE:
                mGenderMaleButton.setBackgroundResource(R.drawable.male_select_style);
                mGenderMaleButton.setTextColor(getResources().getColor(R.color.white10));
                mGenderFemaleButton.setBackgroundColor(getResources().getColor(R.color.white00));
                mGenderFemaleButton.setTextColor(getResources().getColor(R.color.grey06));
                if (!isAvatarSet) {
                    mAvatarImageView.setBackground(getDrawable(R.mipmap.img_male));
                }
                mUserGender = MALE_TYPE;
                break;
            case FEMALE_TYPE:
                mGenderFemaleButton.setBackgroundResource(R.drawable.female_select_style);
                mGenderFemaleButton.setTextColor(getResources().getColor(R.color.white10));
                mGenderMaleButton.setBackgroundColor(getResources().getColor(R.color.white00));
                mGenderMaleButton.setTextColor(getResources().getColor(R.color.grey06));
                if (!isAvatarSet) {
                    mAvatarImageView.setBackground(getDrawable(R.mipmap.img_female));
                }
                mUserGender = FEMALE_TYPE;
                break;
        }
    }

    @OnClick(R.id.complete_weight_button)
    void changeWeight() {
        if (null == mNumberPickerDialog) {
            mNumberPickerDialog = new NumberPickerDialog(context);
        }
        mNumberPickerDialog.setOnNumberPickedListener(new NumberPickerDialog.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                mUserDetails.setWeight(String.valueOf(number));
                mWeightText.setText(mUserDetails.getWeight() + "KG");
            }
        });
        mNumberPickerDialog.withTitle(getString(R.string.change_weight));
        mNumberPickerDialog.withMinMaxValue(Conf.MIN_WEIGHT, Conf.MAX_WEIGHT).show();
    }

    @OnClick(R.id.complete_avatar_image_view)
    void changeImage() {
        PermissionDispatcher.showCameraWithCheck(((BaseActivity) getActivity()));
    }

    @OnClick(R.id.complete_height_button)
    void changeHeight() {
        if (null == mNumberPickerDialog) {
            mNumberPickerDialog = new NumberPickerDialog(context);
        }
        mNumberPickerDialog.setOnNumberPickedListener(new NumberPickerDialog.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                mUserDetails.setHeight(String.valueOf(number));
                mHeightText.setText(mUserDetails.getHeight() + "CM");
            }
        });
        mNumberPickerDialog.withTitle(getString(R.string.change_height));
        mNumberPickerDialog.withMinMaxValue(Conf.MIN_HEIGHT, Conf.MAX_HEIGHT).show();
    }

    @OnClick(R.id.complete_birthday_button)
    void changeBirthday() {
        if (null == mDatePickerDialog) {
            mDatePickerDialog = new DatePickerDialog(context);
            mDatePickerDialog.setOnDateChoosedListener(new DatePickerDialog.OnDateChoosedListener() {
                @Override
                public void onDateChoosed(String date) {
                    mUserDetails.setBirthday(date);
                    mBirthdayText.setText(date);
                }
            });
        }
        mDatePickerDialog.show();
    }

    @OnClick(R.id.complete_last_button)
    void back() {
        finish();
    }

    @OnClick(R.id.complete_gender_male_button)
    void changeGenderToMale() {
        changeGender(MALE_TYPE);
    }

    @OnClick(R.id.complete_gender_female_button)
    void changeGenderToFemale() {
        changeGender(FEMALE_TYPE);
    }


    @OnClick(R.id.complete_next_button)
    void next() {
        if (!isAvatarSet) {
            ToastUtil.TextToast(getString(R.string.please_select_avatar));
            return;
        }

        if (mNicknameText.getText() != null && mNicknameText.getText().length() != 0) {
            mUserDetails.setNickname(mNicknameText.getText().toString());
        }

        if (mImageFile != null) {
            HttpService.getUserService().
                    uploadUserAvatar(Accounts.getId(), Accounts.getToken(), RequestBody.create(MediaType.parse
                            ("multipart/form-data"), mImageFile))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Response<String>>() {
                        @Override
                        public void call(Response<String> stringResponse) {
                            if (stringResponse == null) {
                                return;
                            }
                            mUserDetails.setAvatar(stringResponse.getData());
                            uploadAllUserInfo();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    });
        } else {
            uploadAllUserInfo();
        }
    }

    private void locate() {
        client = new LocationClient(getContext());
        initLocation();
        client.registerLocationListener(this);
        client.start();
    }

    private void uploadAllUserInfo() {
        HttpService.getUserService().resetUserDetailsAll(Accounts.getId(), Accounts.getToken(),
                mUserDetails.getNickname(), mUserDetails.getBirthday(), mUserGender == 1 ? "男" : "女",
                mUserDetails.getHeight(), mUserDetails.getWeight(), mUserDetails.getAddress(), mUserDetails.getAvatar())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> voidResponse) {
                        if (voidResponse == null) {
                            Timber.e("upload user info error");
                            return;
                        }
                        if (voidResponse.getStatus() == 1) {
                            ToastUtil.TextToast("上传信息成功");
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtil.TextToast(voidResponse.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable.toString());
                    }
                });
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        client.setLocOption(option);
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            mUserDetails.setAddress(bdLocation.getCity());
            mLocationText.setText(bdLocation.getCity());
            city.setName(bdLocation.getCity());
            city.setProvince(bdLocation.getProvince());
            city.setLatitude(bdLocation.getLatitude());
            city.setLongtitude(bdLocation.getLongitude());
            ToastUtil.TextToast("定位成功");

        } else {
            ToastUtil.TextToast("定位失败");
            mLocationText.setText("武汉");
            mUserDetails.setAddress("武汉");
            city.setName("武汉");
            city.setProvince("湖北");
            city.setLatitude(30.515372);
            city.setLongtitude(114.419876);
        }
        client.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mImageChooseDialog) {
            mImageChooseDialog.onActivityResult(requestCode, resultCode, data);
        }
        switch (resultCode) {
            case Conf.RESULT_CODE_START_USER_LOCATE:
                CityStruct cityStruct = data.getParcelableExtra(Conf.CITY_STRUCT);
                if (cityStruct == null) {
                    return;
                }
                mUserDetails.setAddress(cityStruct.getmName());
                mLocationText.setText(cityStruct.getmName());
                city.setName(cityStruct.getmName());
                break;
        }
    }


    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_CAMERA)
    public void startCamera() {
        if (null == mImageChooseDialog) {
            mImageChooseDialog = new ImageChooseDialog(context, this);
            mImageChooseDialog.setOnImageChoosedListener(new ImageChooseDialog.OnImageChoosedListener() {
                @Override
                public void onImageChoosed(File file) {
                    Glide.with(getActivity()).load(file).error(R.mipmap.default_avatar).into(mAvatarImageView);
                    mImageFile = file;
                    isAvatarSet = true;
                }
            });
        }
        mImageChooseDialog.show();
    }

    @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
    public void startLocate() {
        client.start();
        mLocationButton.animate().scaleX(0.9f).scaleY(0.9f).setInterpolator(new BounceInterpolator()).setDuration
                (100).start();
    }

}
