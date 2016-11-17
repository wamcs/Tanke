package com.lptiyu.tanke.activities.userdirectionrunlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.drrecorddetail.DRRecordDetailActivity;
import com.lptiyu.tanke.adapter.DRRecordListAdapter;
import com.lptiyu.tanke.entity.response.DRRecordEntity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.RecyclerViewItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDirectionRunListActivity extends MyBaseActivity implements UserDirectionRunContact
        .IUserDirectionRunView {
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;
    @BindView(R.id.recyclerView_message_list)
    RecyclerView recyclerViewMessageList;
    @BindView(R.id.swipe_message_list)
    SwipeRefreshLayout swipeMessageList;
    @BindView(R.id.no_data_imageview)
    ImageView noDataImageview;
    private UserDirectionRunPresenter presenter;
    private DRRecordListAdapter adapter;
    private List<DRRecordEntity> totallist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_direction_run_list);
        ButterKnife.bind(this);

        defaultToolBarTextview.setText("乐跑记录");

        presenter = new UserDirectionRunPresenter(this);
        initSwipe();
        swipeMessageList.setRefreshing(true);
        loadDRListData();
    }

    private void loadDRListData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.loadDRList();
        } else {
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showNetExceptionPopupwindow(UserDirectionRunListActivity.this, new
                            PopupWindowUtils.OnRetryCallback() {
                                @Override
                                public void onRetry() {
                                    loadDRListData();
                                }
                            });
                }
            });
        }
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }

    @Override
    public void successLoadDRList(List<DRRecordEntity> list) {
        swipeMessageList.setRefreshing(false);
        if (list != null && list.size() > 0) {
            totallist = list;
            setAdapter();
        } else {
            noDataImageview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void failLoad(String errMsg) {
        swipeMessageList.setRefreshing(false);
        noDataImageview.setVisibility(View.VISIBLE);
    }

    private void setAdapter() {
        recyclerViewMessageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DRRecordListAdapter(this, totallist);
        recyclerViewMessageList.setAdapter(adapter);
        recyclerViewMessageList.addItemDecoration(new RecyclerViewItemDecoration(this));
        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int position) {
                DRRecordEntity dRRecord = totallist.get(position);
                Intent intent = new Intent(UserDirectionRunListActivity.this, DRRecordDetailActivity.class);
                intent.putExtra(Conf.DRRecordEntity, dRRecord);
                UserDirectionRunListActivity.this.startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    private void initSwipe() {
        swipeMessageList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDRListData();
            }
        });
    }
}
