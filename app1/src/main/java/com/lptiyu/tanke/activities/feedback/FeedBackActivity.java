package com.lptiyu.tanke.activities.feedback;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.base.MyBaseActivity;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedBackActivity extends MyBaseActivity implements FeedbackContact.IFeedbackView {

    @BindView(R.id.et_input_contact)
    EditText etInputContact;
    @BindView(R.id.et_input_feedback)
    EditText etInputFeedback;
    @BindView(R.id.tv_commit_feedback)
    TextView tvCommitFeedback;
    private FeedbackPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);

        presenter = new FeedbackPresenter(this);
    }

    @OnClick({R.id.back_btn, R.id.tv_commit_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.tv_commit_feedback:
                String contact = etInputContact.getText() + "";
                if (contact.equals("")) {
                    ToastUtil.TextToast("请填写您的联系方式");
                    return;
                }
                String content = etInputFeedback.getText() + "";
                if (content.equals("")) {
                    ToastUtil.TextToast("先写点什么吧");
                    return;
                }
                presenter.submitFeedback(contact, content);
                tvCommitFeedback.setEnabled(false);
                break;
        }
    }

    @Override
    public void successSubmit() {
        ToastUtil.TextToast("您的反馈我们已经收到，感谢您的宝贵意见！");
        etInputFeedback.setText("");
        etInputContact.setText("");
        tvCommitFeedback.setEnabled(true);
    }

    @Override
    public void failSubmit() {
        ToastUtil.TextToast("意见反馈失败，请重试");
        tvCommitFeedback.setEnabled(true);
    }

    @Override
    public void netException() {
        ToastUtil.TextToast("网络异常，请重试！");
        tvCommitFeedback.setEnabled(true);
    }
}
