package com.lptiyu.tanke.userCenter.viewholder;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.widget.CustomTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/14
 *
 * @author ldx
 */
public class GameFinishedHolder extends BaseViewHolder<GameFinishedEntity> {

    @BindView(R.id.image_view)
    RoundedImageView mItemPicture;

    @BindView(R.id.item_finished_title)
    CustomTextView title;

    @BindView(R.id.item_finished_type)
    CustomTextView type;

    @BindView(R.id.item_finished_complete_time)
    CustomTextView completeTime;

    @BindView(R.id.item_finished_consuming_time)
    CustomTextView consumingTime;

    @BindView(R.id.item_finished_exp)
    CustomTextView exp;

    @BindView(R.id.game_finished_list_item)
    RelativeLayout mItem;

    public GameFinishedHolder(ViewGroup parent) {
        super(fromResLayout(parent, R.layout.item_game_finished));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(final GameFinishedEntity entity) {
        if (entity == null || mItemPicture == null) {
            return;
        }
        Glide.with(getContext()).load(entity.img).error(R.drawable.default_pic).into(mItemPicture);
        title.setText(entity.name);
        type.setText("");
        String ftime = entity.endTime;
        completeTime.setText(ftime.substring(0, ftime.lastIndexOf(":")) + "完成");
        consumingTime.setText("用时" + TimeUtils.parseSecondToHourAndMinutes(Long.parseLong(entity.getTotalTime())));
        exp.setText(String.format(getContext().getString(R.string.user_game_finished_get_exp_formatter), entity
                .expPoints));

        mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunApplication.gameId = entity.gameId;
                RunApplication.type = entity.type;
                RunApplication.entity = entity;
                RunApplication.entity.title = entity.name;
                getContext().startActivity(new Intent(getContext(), GamePlayingActivity.class));
            }
        });
    }
}
