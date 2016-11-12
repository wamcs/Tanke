package com.lptiyu.tanke.activities.userdirectionrunlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.drrecorddetail.DRRecordDetailActivity;
import com.lptiyu.tanke.adapter.DRRecordListAdapter;
import com.lptiyu.tanke.entity.response.DRRecordEntity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.widget.CustomTextView;

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
        presenter.loadDRList();
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }

    @Override
    public void successLoadDRList(List<DRRecordEntity> list) {
        if (list != null && list.size() > 0) {
            totallist = list;
            setAdapter();
        } else {
            Toast.makeText(this, "暂无乐跑记录", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAdapter() {
        recyclerViewMessageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DRRecordListAdapter(this, totallist);
        recyclerViewMessageList.setAdapter(adapter);
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
        swipeMessageList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeMessageList.setRefreshing(false);
            }
        });
    }
}
