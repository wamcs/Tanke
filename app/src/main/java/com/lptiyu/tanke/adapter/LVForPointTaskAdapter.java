package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.taskimagescale.TaskImageScaleActivity;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.enums.TaskType;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.WebViewUtils;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/7/12.
 */
public class LVForPointTaskAdapter extends BaseAdapter {
    private List<Task> list_tasks;
    private Context context;
    private LayoutInflater inflater;
  //  private ArrayList<TaskRecord> list_task_record;
    private int count = 0;

    public LVForPointTaskAdapter(Context context, List<Task> list_tasks) {
        this.list_tasks = list_tasks;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (Task task : list_tasks) {
            if (task.state == PointTaskStatus.FINISHED || task.state == PointTaskStatus.PLAYING) {
                count++;
            }
            else {

            }
        }
    }

    @Override
    public int getCount() {
        //        Log.i("jason", "LVForPointTaskAdapter-->getCount():isPointOver=" + isPointOver + " list_tasks.size
        // ()=" +
        //                list_tasks.size() + "  count=" + count);
        //return isPointOver ? list_tasks.size() : count + 1;
        return  count;
    }

    @Override
    public Object getItem(int position) {
        return list_tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (vh == null) {
            convertView = inflater.inflate(R.layout.item_lv_pointtask, viewGroup, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Task task = list_tasks.get(position);
        WebViewUtils.setWebView(context, vh.webView);
        vh.webView.loadData(task.content, "text/html;charset=UTF-8", null);
        // 特别要注意这行代码,意思是在js中条用android中的第一个参数的实际名字。这里第一个参数是this。
        //也就是本类的实例。imgelistener是本类的实例在js中的名字。
        // 也就是说你要在js中调用MainActivity这个类中的方法的话就必须给MainActivity这个类在js中的名字，
        //这样你才能在js中调用android中的类中的方法。
        vh.webView.addJavascriptInterface(this, "imagelistner");
        final ViewHolder finalVh = vh;
        vh.webView.setWebViewClient(new WebViewClient() {

            public  void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // 防止双击内容缩小
                finalVh.webView.loadUrl("javascript:(function(){"
                        + "var oMeta = document.createElement('meta'); "
                        + "oMeta.name = \"viewport\"; "
                        + "oMeta.content = \"initial-scale=1.0, user-scalable=no\"; "
                        + "document.getElementsByTagName('head')[0].appendChild(oMeta); "
                        + "})()");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 注入js函数监听
                finalVh.webView.loadUrl("javascript:(function(){"
                        + "var objs = document.getElementsByTagName(\"img\"); "
                        + "for(var i=0;i<objs.length;i++)  " + "{"
                        + "    objs[i].onclick=function()  " + "    {  "
                        + "        var url =  this.src.replace(\"_thumbs/Images\",\"images\"); "
                        + "        window.imagelistner.openImage(url);  "
                        + "    }  " + "}" + "})()");
            }
        });


        WebSettings wSet = vh.webView.getSettings();
        wSet.setLoadWithOverviewMode(true);
        wSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wSet.setSupportZoom(false);

        //修改适合的字体，避免因为系统的设置，导致字体过大或过小
        int j = wSet.getDefaultFixedFontSize();
        wSet.setDefaultFontSize(j);


        switch (task.state) {
            case PointTaskStatus.UNSTARTED://未开启
                vh.rlFinishInfo.setVisibility(View.GONE);
                break;
            case PointTaskStatus.PLAYING://正在玩
                vh.rlFinishInfo.setVisibility(View.GONE);
                break;
            case PointTaskStatus.FINISHED://已完成
                {
                    vh.rlFinishInfo.setVisibility(View.VISIBLE);

                    vh.ctvExp.setText("+" + task.exp);
                    if (task.finishTime != null && task.finishTime !="")
                    vh.ctvFfinishTime.setText(task.finishTime.substring(0, task.finishTime.lastIndexOf(":")));
                }
        }

        if (Integer.parseInt(task.type) == TaskType.FINISH) {
            vh.rlFinishInfo.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.webView)
        WebView webView;
        @BindView(R.id.ctv_exp)
        CustomTextView ctvExp;
        @BindView(R.id.ctv_finish_time)
        CustomTextView ctvFfinishTime;
        @BindView(R.id.rl_finishInfo)
        RelativeLayout rlFinishInfo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void refresh(List<Task> list_tasks) {
        this.list_tasks = list_tasks;
        notifyDataSetChanged();
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Toast.makeText(context, img, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, TaskImageScaleActivity.class);
        intent.putExtra(Conf.TASK_IMG,img);
        context.startActivity(intent);
        //        new AlertDialog.Builder(context).setMessage("图片被点击了").setNegativeButton("确定", null).setCancelable
        // (true).show();
    }
}

