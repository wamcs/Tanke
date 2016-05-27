package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.ContextController;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.Inflater;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/5/26
 * email:kaili@hustunique.com
 */
public class ImageChooseDialog extends BaseDialog {



    private ContextController mController;
    private File mTempFile;
    private OnImageChoosedListener mListener;
    private OnPermissionGetListener permissionGetListener;

    public ImageChooseDialog(Context context, ContextController controller) {
        super(context);
        mController = controller;
        this.withTitle(context.getString(R.string.select_image))                                  //.withTitle(null)  no title
                .setCustomView(R.layout.layout_dialog_image_choose, context);
    }

    @Override
    public BaseDialog setCustomView(int resId, Context context) {
        View v = Inflater.inflate(resId, null, false);
        return setCustomView(v, context);
    }

    @Override
    public BaseDialog setCustomView(View view, Context context) {
        ButterKnife.bind(this, view);
        return super.setCustomView(view, context);
    }

    @Override
    public void show() {
        if (null == mTempFile) {
            mTempFile = new File(DirUtils.getResDirectory(), Conf.TEMP_AVATAR_NAME);
        }
        if (mTempFile.exists()) {
            Timber.d("=====tempFile exist");
        } else {
            Timber.d("=====tempFile not exist");
        }
        super.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case Conf.REQUEST_CODE_TAKE_PHOTO:
                startCutPhoto(Uri.fromFile(mTempFile), Conf.USER_AVATAR_SIZE, Conf.USER_AVATAR_SIZE);
                break;

            case Conf.REQUEST_CODE_GALLERY:
                if (null != data) {
                    startCutPhoto(data.getData(), Conf.USER_AVATAR_SIZE, Conf.USER_AVATAR_SIZE);
                }
                break;

            case Conf.REQUEST_CODE_CUT_PHOTO:
                if (null != data) {
                    mListener.onImageChoosed(mTempFile);
                }
                break;
        }
    }

    private void startCutPhoto(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
        if (mController instanceof FragmentController) {
            ((FragmentController) mController).startActivityForResult(intent, Conf.REQUEST_CODE_CUT_PHOTO);
        } else if (mController instanceof ActivityController) {
            ((ActivityController) mController).startActivityForResult(intent, Conf.REQUEST_CODE_CUT_PHOTO);
        }
    }

    @OnClick(R.id.dialog_user_avatar_take_photo)
    void startTakePhoto() {
        permissionGetListener.onPermissionGet();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
        if (mController instanceof FragmentController) {
            ((FragmentController) mController).startActivityForResult(intent, Conf.REQUEST_CODE_TAKE_PHOTO);
        } else if (mController instanceof ActivityController) {
            ((ActivityController) mController).startActivityForResult(intent, Conf.REQUEST_CODE_TAKE_PHOTO);
        }
        dismiss();
    }

    @OnClick(R.id.dialog_user_avatar_gallery)
    void startSystemGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (mController instanceof FragmentController) {
            ((FragmentController) mController).startActivityForResult(galleryIntent, Conf.REQUEST_CODE_GALLERY);
        } else if (mController instanceof ActivityController) {
            ((ActivityController) mController).startActivityForResult(galleryIntent, Conf.REQUEST_CODE_GALLERY);
        }
        dismiss();
    }




    public interface OnImageChoosedListener {
        void onImageChoosed(File file);
    }

    public interface OnPermissionGetListener{
        void onPermissionGet();
    }

    public void setOnImageChoosedListener(OnImageChoosedListener listener) {
        mListener = listener;
    }

    public void setOnPermissionGetListener(OnPermissionGetListener listener){
        permissionGetListener = listener;
    }

}
