package com.lptiyu.tanke.fragments.pointtask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.mybase.MyBaseFragment;

/**
 * Created by Jason on 2016/10/12.
 */

public class EmptyFragment extends MyBaseFragment {
    public static EmptyFragment create() {
        return new EmptyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_pointtask, container, false);
        return view;
    }
}
