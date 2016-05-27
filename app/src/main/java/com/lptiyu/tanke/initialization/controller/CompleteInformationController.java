package com.lptiyu.tanke.initialization.controller;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.UserDetails;
import com.lptiyu.tanke.pojo.UserEntity;
import com.lptiyu.tanke.utils.ThirdLoginHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.dialog.DatePickerDialog;
import com.lptiyu.tanke.widget.dialog.ImageChooseDialog;
import com.lptiyu.tanke.widget.dialog.NumberPickerDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.baidu.location.LocationClientOption.*;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class CompleteInformationController extends ActivityController implements BDLocationListener{


    @BindView(R.id.complete_next_button)
    LinearLayout mCompleteNextButton;

    @BindView(R.id.complete_avatar_image_view)
    SimpleDraweeView mAvatarImageView;

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

    private ImageChooseDialog mImageChooseDialog;
    private NumberPickerDialog mNumberPickerDialog;
    private DatePickerDialog mDatePickerDialog;

    private final static int MALE_TYPE = 1;
    private final static int FEMALE_TYPE = 2;

    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;

    private Context context;
    private LocationClient client;

    private int mUserGender;
    private boolean isAvatarSet;



    public CompleteInformationController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        context = getContext();
        changeGender(MALE_TYPE);
        mUserGender = MALE_TYPE;
        UserDetails Muser = ThirdLoginHelper.getUserDetail();
        initDataFromThirdLogin(Muser);

        mLocationText.setText("定位中");
        locate();
        client.start();

    }

    private void initDataFromThirdLogin(UserDetails userDetails){
        if (userDetails.getNickname()!=null) {
            mNicknameText.setText(userDetails.getNickname());
        }
        if (userDetails.getAvatar()!=null) {
            mAvatarImageView.setImageURI(Uri.parse(userDetails.getAvatar()));
            isAvatarSet = true;
        }
        if(userDetails.getSex()!=null) {
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

    private void changeGender(int type){
        switch (type){
            case MALE_TYPE:
                mGenderMaleButton.setBackgroundResource(R.drawable.male_select_style);
                mGenderMaleButton.setTextColor(getResources().getColor(R.color.white10));
                mGenderFemaleButton.setBackgroundColor(getResources().getColor(R.color.white00));
                mGenderFemaleButton.setTextColor(getResources().getColor(R.color.grey06));
                if (!isAvatarSet){
                    mAvatarImageView.setBackground(getDrawable(R.mipmap.img_male));
                }
                mUserGender = MALE_TYPE;
                break;
            case FEMALE_TYPE:
                mGenderFemaleButton.setBackgroundResource(R.drawable.female_select_style);
                mGenderFemaleButton.setTextColor(getResources().getColor(R.color.white10));
                mGenderMaleButton.setBackgroundColor(getResources().getColor(R.color.white00));
                mGenderMaleButton.setTextColor(getResources().getColor(R.color.grey06));
                if (!isAvatarSet){
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
                mWeightText.setText(String.format("%d%s", number, "KG"));
            }
        });
        mNumberPickerDialog.withMinMaxValue(Conf.MIN_WEIGHT, Conf.MAX_WEIGHT).show();
    }

    @OnClick(R.id.complete_avatar_image_view)
    void changeImage() {
        if (null == mImageChooseDialog) {
            mImageChooseDialog = new ImageChooseDialog(context,this);
            mImageChooseDialog.setOnPermissionGetListener(new ImageChooseDialog.OnPermissionGetListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onPermissionGet() {
                    if (!(getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                        requestCameraPermission();
                    }
                }
            });
            mImageChooseDialog.setOnImageChoosedListener(new ImageChooseDialog.OnImageChoosedListener() {
                @Override
                public void onImageChoosed(File file) {
                    ImagePipeline pipeline = Fresco.getImagePipeline();
                    pipeline.evictFromCache(Uri.fromFile(file));
                    mAvatarImageView.setImageURI(Uri.fromFile(file));
                    isAvatarSet = true;
                }
            });
        }
        mImageChooseDialog.show();
    }

    @OnClick(R.id.complete_height_button)
    void changeHeight() {
        if (null == mNumberPickerDialog) {
            mNumberPickerDialog = new NumberPickerDialog(context);
        }
        mNumberPickerDialog.setOnNumberPickedListener(new NumberPickerDialog.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                mHeightText.setText(String.format("%d%s", number, "CM"));
            }
        });
        mNumberPickerDialog.withMinMaxValue(Conf.MIN_HEIGHT, Conf.MAX_HEIGHT).show();
    }

    @OnClick(R.id.complete_birthday_button)
    void changeBirthday() {
        if (null == mDatePickerDialog) {
            mDatePickerDialog = new DatePickerDialog(context);
            mDatePickerDialog.setOnDateChoosedListener(new DatePickerDialog.OnDateChoosedListener() {
                @Override
                public void onDateChoosed(String date) {
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
    void changeGenderToMale(){
        changeGender(MALE_TYPE);
    }

    @OnClick(R.id.complete_gender_female_button)
    void changeGenderToFemale(){
        changeGender(FEMALE_TYPE);
    }

    @OnClick(R.id.complete_location_button)
    void chooseLocation(){
        locate();
    }

    @OnClick(R.id.complete_next_button)
    void next(){
        if (!isAvatarSet){
            ToastUtil.TextToast(getString(R.string.please_select_avatar));
            return;
        }

        Editable nickname = mNicknameText.getText();
        if (nickname.length() == 0){
            ToastUtil.TextToast(getString(R.string.please_input_nick_name));
            return;
        }

       /* switch (mUserGender){
            case MALE_TYPE:
                Muser.setSex("男");
                break;
            case FEMALE_TYPE:
                Muser.setSex("女");
                break;
        }
        Muser.setAddress(mLocationText.getText().toString());
        Muser.setNickname(mNicknameText.getText().toString());
        Muser.setBirthday(mBirthdayText.getText().toString());
        Muser.setHeight(mHeightText.getText().toString());
        Muser.setWeight(mWeightText.getText().toString());
*/
        //TODO:user avatar
    }

    private void locate(){
        if (null == client){
            client = new LocationClient(getContext());
            initLocation();
            client.registerLocationListener(this);
        }
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
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
    protected boolean isToolbarEnable() {
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.d("lk", "address string:" + bdLocation.getAddrStr());
        Log.d("lk", "city string:" + bdLocation.getCity());
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
            mLocationText.setText(bdLocation.getAddrStr());

        }else {
            ToastUtil.TextToast("定位失败");
            mLocationText.setText("武汉");
        }

        client.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mImageChooseDialog) {
            mImageChooseDialog.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



  //android M 调用相机权限需要在使用时调用
    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
        }
    }
}
