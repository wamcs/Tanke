package com.lptiyu.tanke.messagesystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.MainActivityController;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.FragmentController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.messagesystem.adpater.MessageListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class MessageListFragment extends BaseFragment {

    @BindView(R.id.message_list_recycler_view)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = fromResLayout(inflater, container, R.layout.fragment_message_list);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MessageListAdapter adapter = new MessageListAdapter(getContext());
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public FragmentController getController() {
        return null;
    }
}
