package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.utils.BitMapUtils;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/7/12.
 */
public class GVForGamePlayingAdapter extends BaseAdapter {
    private List<Point> list_points;
    private Context context;
    private LayoutInflater inflater;
    private String unZippedDir;

    public GVForGamePlayingAdapter(Context context, List<Point> list_points, String unZippedDir) {
        this.list_points = list_points;
        this.context = context;
        this.unZippedDir = unZippedDir;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list_points == null ? 0 : list_points.size();
    }

    @Override
    public Object getItem(int position) {
        return list_points.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (vh == null) {
            convertView = inflater.inflate(R.layout.item_gv_gameplaying, viewGroup, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Point point = list_points.get(position);
        setImgBitmap(vh, point.point_img);

        switch (point.state) {
            case PointTaskStatus.UNSTARTED://未开启
                vh.ctvPointName.setText("未开启");
                vh.imgLabel.setVisibility(View.GONE);
                vh.imgLock.setVisibility(View.VISIBLE);
                vh.imgTransparent.setVisibility(View.VISIBLE);
                break;
            case PointTaskStatus.PLAYING://正在玩
                vh.ctvPointName.setText(point.point_title);
                //                vh.ctvPointName.setText("第" + (position + 1) + "站：" + point.point_title);
                if (point.isNew) {
                    vh.imgLabel.setVisibility(View.VISIBLE);
                    vh.imgLabel.setImageResource(R.drawable.playing);
                } else {
                    vh.imgLabel.setVisibility(View.GONE);
                }
                vh.imgLock.setVisibility(View.GONE);
                vh.imgTransparent.setVisibility(View.GONE);
                break;
            case PointTaskStatus.FINISHED://已完成
                vh.ctvPointName.setText(point.point_title);
                //                vh.ctvPointName.setText("第" + (position + 1) + "站：" + point.point_title);
                vh.imgLabel.setVisibility(View.VISIBLE);
                vh.imgLabel.setImageResource(R.drawable.done);
                vh.imgLock.setVisibility(View.GONE);
                vh.imgTransparent.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

    private void setImgBitmap(ViewHolder vh, String point_img) {
        //通过File操作获取游戏包中的图片
        StringBuilder builder = new StringBuilder();
        builder.append(unZippedDir).append("/").append(point_img);
        String path = new File(builder.toString()).getAbsolutePath();
        if (path == null || path.length() == 0) {
            vh.img.setImageResource(R.drawable.default_pic);
        } else {
            //压缩处理
            Bitmap bitmap = BitMapUtils.getBitmap(path, 100, 100);
            vh.img.setImageBitmap(bitmap);
            //            vh.img.setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }

    static class ViewHolder {
        @BindView(R.id.img)
        CircularImageView img;
        @BindView(R.id.img_transparent)
        ImageView imgTransparent;
        @BindView(R.id.img_lock)
        ImageView imgLock;
        @BindView(R.id.ctv_pointName)
        CustomTextView ctvPointName;
        @BindView(R.id.img_label)
        ImageView imgLabel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

