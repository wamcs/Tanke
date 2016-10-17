package com.lptiyu.tanke.widget.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.DisplayUtils;
import com.lptiyu.tanke.utils.ShareHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * author:wamcs
 * date:2016/3/12
 * email:kaili@hustunique.com
 */
public class ShareDialog extends AppCompatActivity {

    //    private int[] imagePath = {R.mipmap.img_share_qq,
    //        R.mipmap.img_share_wechat,
    ////      R.mipmap.img_share_sina,
    //        R.mipmap.img_share_wechat_moment};
    private int[] imagePath = {R.drawable.share_qq, R.drawable.share_wechat, R.drawable.share_wechat_moment};
    private String[] titleArr = {"QQ", "微信", "朋友圈"};

    @BindView(R.id.share_gridView)
    GridView gridView;

    private String mTitle;
    private String mText;
    private String mImageUrl;
    private String mShareUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_share);
        ButterKnife.bind(this);
        init();
        initGridView();
        setShareContent();
    }

    private void init() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(Conf.SHARE_TITLE);
        mText = intent.getStringExtra(Conf.SHARE_TEXT);
        mImageUrl = intent.getStringExtra(Conf.SHARE_IMG_URL);
        mShareUrl = intent.getStringExtra(Conf.SHARE_URL);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        //        params.height = (int) (DisplayUtils.height() * 0.15f);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = DisplayUtils.width();
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }

    private void initGridView() {

        List<HashMap<String, Object>> shareList = new ArrayList<>();
        for (int i = 0; i < imagePath.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemImage", imagePath[i]);
            map.put("ItemTitle", titleArr[i]);
            shareList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, shareList, R.layout.layout_dialog_share_item,
                new String[]{"ItemImage", "ItemTitle"}, new int[]{R.id.dialog_share_item, R.id.tv_title});

        gridView.setAdapter(adapter);

    }

    private void setShareContent() {

        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ShareHelper.share(ShareHelper.SHARE_QQ, mTitle, mText, mImageUrl, mShareUrl);
                        finish();
                        break;
                    case 1:
                        ShareHelper.share(ShareHelper.SHARE_WECHAT_FRIENDS, mTitle, mText, mImageUrl, mShareUrl);
                        finish();
                        break;
                    case 2:
                        ShareHelper.share(ShareHelper.SHARE_WECHAT_CIRCLE, mTitle, mText, mImageUrl, mShareUrl);
                        finish();
                        break;
          /*case 3:
            ShareHelper.share(ShareHelper.SHARE_WEIBO, mTitle, mText, mImageUrl, mShareUrl);
            finish();
            break;*/
                }
            }
        };
        gridView.setOnItemClickListener(listener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.translate_in_bottom, R.anim.translate_out_bottom);
    }

    @OnClick(R.id.img_close)
    public void onClick() {
        this.finish();
    }
}
