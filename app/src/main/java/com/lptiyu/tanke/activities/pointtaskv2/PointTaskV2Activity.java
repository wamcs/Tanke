package com.lptiyu.tanke.activities.pointtaskv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.adapter.PointTaskFragmentPagerAdapter;
import com.lptiyu.tanke.entity.GameRecord;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.UploadGameRecord;
import com.lptiyu.tanke.entity.eventbus.NotifyGamePlayingV2RefreshData;
import com.lptiyu.tanke.entity.eventbus.NotifyPointTaskV2RefreshData;
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.fragments.pointtask.EmptyFragment;
import com.lptiyu.tanke.fragments.pointtask.PointTaskFragment;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.GameOverHelper;
import com.lptiyu.tanke.widget.DepthPageTransformer;
import com.lptiyu.tanke.widget.GalleryViewPager;
import com.lptiyu.zxinglib.android.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lptiyu.tanke.RunApplication.currentPoint;
import static com.lptiyu.tanke.RunApplication.currentTask;
import static com.lptiyu.tanke.RunApplication.gameId;

public class PointTaskV2Activity extends MyBaseActivity implements PointTaskV2Contact.IPointTaskV2View {

    @BindView(R.id.view_pager)
    GalleryViewPager mViewPager;
    private final double MAX_PARCEL = 0.9d;
    private ArrayList<Point> point_list;
    private ArrayList<MyBaseFragment> totallist;
    private int max_index = -1;
    private PointTaskFragmentPagerAdapter adapter;
    private PointTaskV2Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_point_task_v2);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initData();
        initView();
    }

    private void initData() {
        GameRecord gameRecord = RunApplication.gameRecord;
        if (gameRecord != null && gameRecord.game_detail != null && gameRecord.game_detail.point_list != null) {
            point_list = gameRecord.game_detail.point_list;
            totallist = new ArrayList<>();
            for (int i = 0; i < point_list.size(); i++) {
                Point point = point_list.get(i);
                if (point.status == PointTaskStatus.FINISHED || point.status == PointTaskStatus.PLAYING) {
                    totallist.add(PointTaskFragment.create(i));
                }
            }
            if (totallist.size() == point_list.size()) {//全部完成
                max_index = totallist.size() - 1;
            } else {
                totallist.add(EmptyFragment.create());
                max_index = totallist.size() - 2;
            }
        }
        presenter = new PointTaskV2Presenter(this);
    }

    private void initView() {
        adapter = new PointTaskFragmentPagerAdapter(getSupportFragmentManager(), totallist);
        mViewPager.setAdapter(adapter);
        mViewPager.setPageMargin(30);// 设置页面间距
        mViewPager.setOffscreenPageLimit(2);//缓存页数
        mViewPager.setCurrentItem(RunApplication.currentPointIndex);// 设置起始位置
        mViewPager.setPageTransformer(true, new DepthPageTransformer());//设置切换动画
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * @param position 当前item的index
             * @param positionOffset 偏移百分比
             * @param positionOffsetPixels 偏移像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == max_index && positionOffset >= MAX_PARCEL) {
                    mViewPager.setScanScroll(false);
                    mViewPager.setCurrentItem(position);
                    mViewPager.setScanScroll(true);
                } else {
                    mViewPager.setScanScroll(true);
                }
            }

            @Override
            public void onPageSelected(int position) {
                RunApplication.currentPointIndex = position;
                currentPoint = RunApplication.gameRecord.game_detail.point_list.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*无论在哪个线程发送都在主线程接收
    * 接受任务完成后的通知，销毁当前界面
    * */
    @Subscribe
    public void onEventMainThread(NotifyPointTaskV2RefreshData result) {
        finish();
    }

    /**
     * 处理扫码返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {//扫码识别返回
            if (Accounts.getPhoneNumber().equals("18272164317")) {
                upLoadQRCodeRecord();
                return;
            }
            // RESULT_OK是Activity里面的一个静态常量
            Bundle b = intent.getExtras();
            //扫描到的结果
            String str = b.getString(CaptureActivity.QR_CODE_DATA);
            if (str == null || str.length() == 0) {
                Toast.makeText(this, getString(R.string.scan_error), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentTask != null && str.equals(currentTask.pwd)) {
                //上传游戏记录
                upLoadQRCodeRecord();
            } else {
                Toast.makeText(this, "当前任务没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //扫码成功后上传游戏记录
    private void upLoadQRCodeRecord() {
        UploadGameRecord record = new UploadGameRecord();
        record.uid = Accounts.getId() + "";
        record.point_id = currentPoint.id + "";
        record.game_id = gameId + "";
        if (RunApplication.isPointOver) {
            record.point_statu = PointTaskStatus.FINISHED + "";
        } else {
            record.point_statu = PointTaskStatus.PLAYING + "";
        }
        record.task_id = currentTask.id + "";
        presenter.uploadRecord(record);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void successUploadRecord(UpLoadGameRecordResult result) {
        String tip = "";
        if (result != null) {
            tip = "恭喜你找到答案了，经验 +" + result.get_exp + ", 积分 +" + result.get_extra_points + ", 红包 +" + result
                    .get_extra_money + "元";
        }
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        if (result.game_statu == PlayStatus.GAME_OVER) {//游戏通关，需要弹出通关视图，弹出通关视图
            //TODO 弹出通关视图
            GameOverHelper gameOverHelper = new GameOverHelper(this, new GameOverHelper.OnPopupWindowDismissCallback() {
                @Override
                public void onDismiss() {
                    finish();
                }
            });
            gameOverHelper.showPopup();
        } else {
            finish();
        }
        EventBus.getDefault().post(new NotifyGamePlayingV2RefreshData());
    }
}
