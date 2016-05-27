package com.lptiyu.tanke.initialization.controller;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.widget.dialog.DatePickerDialog;
import com.lptiyu.tanke.widget.dialog.ImageChooseDialog;
import com.lptiyu.tanke.widget.dialog.NumberPickerDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class CompleteInformationController extends ActivityController {


    @BindView(R.id.complete_next_button)
    LinearLayout mCompleteNextButton;

    @BindView(R.id.complete_avatar_image_view)
    SimpleDraweeView mCompleteAvatarImageView;

    @BindView(R.id.complete_nickname_text)
    EditText mCompleteNicknameText;
    @BindView(R.id.complete_gender_male_button)
    TextView mCompleteGenderMaleButton;
    @BindView(R.id.complete_gender_female_button)
    TextView mCompleteGenderFemaleButton;

    @BindView(R.id.complete_height_text)
    TextView mCompleteHeightText;
    @BindView(R.id.complete_birthday_text)
    TextView mCompleteBirthdayText;
    @BindView(R.id.complete_weight_text)
    TextView mCompleteWeightText;

    @BindView(R.id.complete_location_button)
    ImageView mCompleteLocationButton;
    @BindView(R.id.complete_location_text)
    TextView mCompleteLocationText;

    private ImageChooseDialog mImageChooseDialog;
    private NumberPickerDialog mNumberPickerDialog;
    private DatePickerDialog mDatePickerDialog;

    private SimpleDraweeView mImage;
    private Context context;

    private int mUserGender;

    public CompleteInformationController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init();
    }

    private void init(){
        context = getContext();
    }

    @OnClick(R.id.complete_weight_button)
    void changeWeight(){
        if (null == mNumberPickerDialog) {
            mNumberPickerDialog = new NumberPickerDialog(context);
        }
        mNumberPickerDialog.setOnNumberPickedListener(new NumberPickerDialog.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                mCompleteWeightText.setText(String.format("%d%s",number,"KG"));
            }
        });
        mNumberPickerDialog.withMinMaxValue(Conf.MIN_WEIGHT,Conf.MAX_WEIGHT).show();
    }

    @OnClick(R.id.complete_avatar_image_view)
    void changeImage() {
        if (null == mImageChooseDialog) {
            mImageChooseDialog = new ImageChooseDialog(context);
            mImageChooseDialog.setContextController(CompleteInformationController.this);
            mImageChooseDialog.setOnImageChoosedListener(new ImageChooseDialog.OnImageChoosedListener() {
                @Override
                public void onImageChoosed(File file) {
                    ImagePipeline pipeline = Fresco.getImagePipeline();
                    pipeline.evictFromCache(Uri.fromFile(file));
                    mImage.setImageURI(Uri.fromFile(file));
                }
            });
        }
        mImageChooseDialog.show();
    }

    @OnClick(R.id.complete_height_button)
    void changeHeight(){
        if (null == mNumberPickerDialog) {
            mNumberPickerDialog = new NumberPickerDialog(context);
        }
        mNumberPickerDialog.setOnNumberPickedListener(new NumberPickerDialog.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                mCompleteHeightText.setText(String.format("%d%s",number,"CM"));
            }
        });
        mNumberPickerDialog.withMinMaxValue(Conf.MIN_HEIGHT,Conf.MAX_HEIGHT).show();
    }

    @OnClick(R.id.complete_birthday_button)
        void changeBirthday(){
        if (null == mDatePickerDialog) {
            mDatePickerDialog = new DatePickerDialog(context);
            mDatePickerDialog.setOnDateChoosedListener(new DatePickerDialog.OnDateChoosedListener() {
                @Override
                public void onDateChoosed(String date) {
                     mCompleteBirthdayText.setText(date);
                }
            });
        }
        mDatePickerDialog.show();
        }

    @OnClick(R.id.complete_last_button)
    void back(){
        finish();
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
}
