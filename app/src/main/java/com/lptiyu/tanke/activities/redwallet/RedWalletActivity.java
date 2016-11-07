package com.lptiyu.tanke.activities.redwallet;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RedWalletActivity extends MyBaseActivity {

    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_wallet);
        ButterKnife.bind(this);

        defaultToolBarTextview.setText("红包提现");
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }
}
