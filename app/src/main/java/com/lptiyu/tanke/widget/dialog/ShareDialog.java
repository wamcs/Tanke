package com.lptiyu.tanke.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.ShareHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/3/12
 * email:kaili@hustunique.com
 */
public class ShareDialog extends BaseDialog {

    private int[] imagePath = {R.mipmap.img_qq,
            R.mipmap.img_weixin,
            R.mipmap.img_weibo,
            R.mipmap.img_share_wechat_moment};

    @BindView(R.id.share_gridView)
    GridView gridView;

    public ShareDialog(Context context) {
        super(context);
        this.withIcon(R.mipmap.game_details_share_btn)
                .withTitle("分享到")
                .setCustomView(R.layout.layout_dialog_share, context);
        initGridView(context);
    }

    private void initGridView(Context context) {
        List<HashMap<String, Object>> shareList = new ArrayList<>();
        for (int anImagePath : imagePath) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemImage", anImagePath);
            shareList.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(context, shareList, R.layout.layout_dialog_share_item,
                new String[]{"ItemImage"}, new int[]{R.id.dialog_share_item});
        gridView.setAdapter(adapter);

    }

    public void setShareContent(String title, String text, String imageUrl, String shareUrl) {
        final String mTitle = title;
        final String mText = text;
        final String mImageUrl = imageUrl;
        final String mShareUrl = shareUrl;
        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ShareHelper.share(ShareHelper.SHARE_QQ, mTitle, mText, mImageUrl, mShareUrl);
                        break;
                    case 1:
                        ShareHelper.share(ShareHelper.SHARE_WECHAT_FRIENDS, mTitle, mText, mImageUrl, mShareUrl);
                        break;
                    case 2:
                        ShareHelper.share(ShareHelper.SHARE_WEIBO, mTitle, mText, mImageUrl, mShareUrl);
                        break;
                    case 3:
                        ShareHelper.share(ShareHelper.SHARE_WECHAT_CIRCLE, mTitle, mText, mImageUrl, mShareUrl);
                        break;
                }
            }
        };
        gridView.setOnItemClickListener(listener);
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
}
