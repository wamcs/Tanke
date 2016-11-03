package com.lptiyu.tanke.fragments.messagesystem;

import com.lptiyu.tanke.entity.response.MessageEntity;
import com.lptiyu.tanke.mybase.IBasePresenter;
import com.lptiyu.tanke.mybase.IBaseView;

import java.util.List;

/**
 * Created by Jason on 2016/10/31.
 */

public class MessageContact {
    interface IMessageView extends IBaseView {
        void successLoadMessage(List<MessageEntity> list);
    }

    interface IMessagePresenter extends IBasePresenter {
        void loadMessage(int page);
    }
}
