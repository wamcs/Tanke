package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.enums.GameRecordAndPointStatus;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;

import java.io.File;
import java.util.List;
import java.util.Map;

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
        Map<String, Task> taskMap = point.getTaskMap();
        String taskId = point.getTaskId().get(0);
        Task task = taskMap.get(taskId);//因为每个攻击点只有一个任务，所以，此处可以直接取0

        switch (point.getState()) {
            case GameRecordAndPointStatus.UNSTARTED://未开启
                vh.ctvPointName.setText("未开启");
                vh.img.setImageResource(R.drawable.default_pic);
                vh.imgLabel.setVisibility(View.GONE);
                break;
            case GameRecordAndPointStatus.PLAYING://正在玩
                vh.ctvPointName.setText(task.getTaskName() + "");
                setImgBitmap(position, vh, taskId);
                vh.imgLabel.setVisibility(View.VISIBLE);
                vh.imgLabel.setImageResource(R.drawable.playing);
                break;
            case GameRecordAndPointStatus.FINISHED://已完成
                vh.ctvPointName.setText(task.getTaskName() + "");
                setImgBitmap(position, vh, taskId);
                vh.imgLabel.setVisibility(View.VISIBLE);
                vh.imgLabel.setImageResource(R.drawable.done);
                break;
        }

        return convertView;
    }

    private void setImgBitmap(int position, ViewHolder vh, String taskId) {
        //通过File操作获取游戏包中的图片
        StringBuilder builder = new StringBuilder();
        builder.append(unZippedDir).append("/").append(position).append("/").append(taskId);
        File file = new File(builder.toString());
        String[] list = file.list();
        if (list == null || list.length == 0) {
            vh.img.setImageResource(R.drawable.default_pic);
        } else {
            for (String fileDir : list) {
                if (fileDir.endsWith(position+".jpg") || fileDir.endsWith(position+".png")) {
                    String imgDir = builder.append("/").append(fileDir).toString();
                    Log.i("jason", "章节点图片路径：" + imgDir);
                    vh.img.setImageBitmap(BitmapFactory.decodeFile(imgDir));
                }
            }
        }
    }

    static class ViewHolder {
        @BindView(R.id.img)
        CircularImageView img;
        @BindView(R.id.ctv_pointName)
        CustomTextView ctvPointName;
        @BindView(R.id.img_label)
        ImageView imgLabel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
