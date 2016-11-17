package com.lptiyu.tanke.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.taskimagescale.TaskImageScaleActivity;
import com.lptiyu.tanke.entity.Task;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.WebViewUtils;

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
    private int count;

    public LVForPointTaskAdapter(Context context, List<Task> list_tasks) {
        this.list_tasks = list_tasks;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkTaskStatus();
    }

    private void checkTaskStatus() {
        int max_index = -1;//最后一个已完成的task的角标
        for (int i = 0; i < list_tasks.size(); i++) {
            Task task = list_tasks.get(i);
            if (!TextUtils.isEmpty(task.exp)) {
                task.status = PointTaskStatus.FINISHED;
                max_index = i;
            } else {
                task.status = PointTaskStatus.UNSTARTED;
            }
        }
        if (max_index != list_tasks.size() - 1) {
            //没有全部完成
            list_tasks.get(max_index + 1).status = PointTaskStatus.PLAYING;
            count = max_index + 2;
        } else {
            //全部完成
            count = max_index + 1;
        }
    }

    @Override
    public int getCount() {
        return count;
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
            @Override
            public void onPageFinished(WebView view, String url) {
                // 注入js函数监听  更改行间距和字体颜色以及图片宽高
                finalVh.webView.loadUrl("javascript:(function(){"
                        + "var p = document.getElementsByTagName(\"p\"); "
                        + "for(var j=0;j<p.length;j++) {"
                        + "p[j].style.lineHeight=\"1.8\";"
                        + "p[j].style.color=\"#666666\";}"
                        + "var objs = document.getElementsByTagName(\"img\"); "
                        + "for(var i=0;i<objs.length;i++) {"
                        + "var url =  objs[i].src.replace(\"_thumbs/Images\",\"images\"); "
                        + "var img = new Image();"
                        + "img.src = url;"
                        + "objs[i].style.width=document.documentElement.clientWidth-16;"
                        + "var rate =parseFloat(img.height)/parseFloat(img.width);"
                        + "objs[i].style.height=document.documentElement.clientWidth*rate;"
                        + "    objs[i].onclick=function() {  "
                        + "        window.imagelistner.openImage(url);  "
                        + "    }  }})()"
                );
            }
        });

        //        vh.webView.setVisibility(View.GONE);
        //        vh.tvContent.setVisibility(View.GONE);
        //        URLImageGetter imageGetter = new URLImageGetter(context, vh.tvContent);
        //        vh.tvContent.setText(Html.fromHtml(task.content, imageGetter, null));
        //        vh.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动
        //        vh.tvContent.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页

        switch (task.status) {
            case PointTaskStatus.FINISHED:
                vh.rlMissionSuccess.setVisibility(View.VISIBLE);
                vh.webView.setVisibility(View.VISIBLE);
                break;
            case PointTaskStatus.PLAYING:
                vh.rlMissionSuccess.setVisibility(View.GONE);
                vh.webView.setVisibility(View.VISIBLE);
                break;
            case PointTaskStatus.UNSTARTED:
                vh.rlMissionSuccess.setVisibility(View.GONE);
                vh.webView.setVisibility(View.GONE);
                break;
        }

        return convertView;
    }

    @JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent(context, TaskImageScaleActivity.class);
        intent.putExtra(Conf.TASK_IMG, img);
        context.startActivity(intent);
    }

    static class ViewHolder {
        @BindView(R.id.webView)
        WebView webView;
        //        @BindView(R.id.tv_content)
        //        TextView tvContent;
        @BindView(R.id.rl_mission_success)
        RelativeLayout rlMissionSuccess;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

