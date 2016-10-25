package com.lptiyu.tanke.activities.feedback;

/**
 * Created by Jason on 2016/8/9.
 */
public class FeedbackContact {
    interface IFeedbackView {
        void successSubmit();

        void failSubmit();

        void netException();
    }

    interface IFeedbackPresenter {
        void submitFeedback(String contact, String content);
    }
}
