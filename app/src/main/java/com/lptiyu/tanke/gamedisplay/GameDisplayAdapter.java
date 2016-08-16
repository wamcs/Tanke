package com.lptiyu.tanke.gamedisplay;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayAdapter extends BaseAdapter<GameDisplayEntity> {

    private FooterViewHolder footerHolder;
    private ElasticHeaderViewHolder elasticHeaderViewHolder;

    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private static final int VIEW_TYPE_ELASTIC = 3;

    private GameDisplayFragment fragment;

    public GameDisplayAdapter(GameDisplayFragment fragment) {
        this.fragment = fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder result;
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                result = new NormalViewHolder(parent, fragment);
                break;

            case VIEW_TYPE_FOOTER:
                footerHolder = new FooterViewHolder(fragment.recyclerView);
                result = footerHolder;
                break;

            case VIEW_TYPE_ELASTIC:
                elasticHeaderViewHolder = new ElasticHeaderViewHolder(fragment.recyclerView, fragment);
                result = elasticHeaderViewHolder;
                break;
            default:
                result = new NormalViewHolder(parent, fragment);
        }
        return result;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<GameDisplayEntity> holder, int position) {
        if (dataList == null) {
            return;
        }
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                if (dataList.size() > 3) {
                    holder.bind(dataList.get(position + 2));
                } else {
                    holder.bind(dataList.get(position - 1));
                }
                break;

            case VIEW_TYPE_FOOTER:
                break;

            case VIEW_TYPE_ELASTIC:
                if (dataList.size() > 3) {
                    elasticHeaderViewHolder.bind(dataList.subList(0, 3));
                } else {
                    elasticHeaderViewHolder.bind(dataList);
                }
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ELASTIC;
        }
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return Math.max(0, dataList == null || dataList.size() <= 3 ? 0 : dataList.size() - 3) + 2;
    }

    public void showFooter() {
        footerHolder.mRoot.setVisibility(View.VISIBLE);
    }

    public boolean isFooterVisible() {
        return footerHolder != null && footerHolder.mRoot.getVisibility() == View.VISIBLE;
    }

    public void hideFooter() {
        footerHolder.mRoot.setVisibility(View.GONE);
    }

    class FooterViewHolder extends BaseViewHolder<GameDisplayEntity> {
        @BindView(R.id.item_loading_root)
        RelativeLayout mRoot;

        public FooterViewHolder(ViewGroup parent) {
            super(fromResLayout(parent, R.layout.item_loading_bottom));
            ButterKnife.bind(this, itemView);
            mRoot.setVisibility(View.GONE);
        }

        @Override
        public void bind(GameDisplayEntity entity) {

        }

    }

}

