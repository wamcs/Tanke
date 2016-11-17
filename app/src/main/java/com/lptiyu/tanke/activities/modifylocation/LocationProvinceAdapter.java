package com.lptiyu.tanke.activities.modifylocation;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.pojo.CityStruct;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.LabelButton;

import java.util.List;

/**
 * @author : xiaoxiaoda
 *         date: 15-12-2
 *         email: daque@hustunique.com
 */
public class LocationProvinceAdapter extends BaseAdapter {

    private boolean hasLocateItem = false;
    private boolean hasHotItem = false;
    private boolean hasNormalItem = false;

    private LocateUserActivityController mController;

    private String locateItem;
    private CityStruct cityStruct;
    private List<CityStruct> hotItems;
    private List<String> normalItems;

    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_NORMAL = 2;
    public static final int VIEW_TYPE_HOT = 3;
    public static final int VIEW_TYPE_LOCATE = 4;

    private HotCityItemListener hotCityItemListener;
    private LocationListener locationListener;

    public LocationProvinceAdapter(LocateUserActivityController controller, @Nullable String locateItem, @Nullable
            List<CityStruct> hotItems, @Nullable List<String> normalItems) {

        mController = controller;

        this.locateItem = locateItem;
        this.hotItems = hotItems;
        this.normalItems = normalItems;

        if (null != locateItem) {
            hasLocateItem = true;
        }
        if (null != hotItems) {
            hasHotItem = true;
        }
        if (null != normalItems) {
            hasNormalItem = true;
        }
    }

    public void setHotCityItemLisitener(HotCityItemListener listener) {
        this.hotCityItemListener = listener;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (hasLocateItem) {
            count += 2;
        }
        if (hasHotItem) {
            count += 2;
        }
        if (hasNormalItem) {
            count += (1 + normalItems.size());
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        Object s = null;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER:
                if (0 == position) {
                    s = getString(R.string.current_location);
                }
                if (2 == position) {
                    s = getString(R.string.hot_location);
                }
                if (4 == position) {
                    s = getString(R.string.all_location);
                }
                break;

            case VIEW_TYPE_NORMAL:
                s = normalItems.get(position - 5);
                break;

            case VIEW_TYPE_LOCATE:
                s = locateItem;
                break;
        }
        return s;
    }

    @Override
    public int getItemViewType(int position) {
        if (1 == position) {
            return VIEW_TYPE_LOCATE;
        }
        if (0 == position || 2 == position || 4 == position) {
            return VIEW_TYPE_HEADER;
        }
        if (5 <= position) {
            return VIEW_TYPE_NORMAL;
        }
        return VIEW_TYPE_HOT;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        switch (getItemViewType(position)) {

            case VIEW_TYPE_HEADER:
                view = Inflater.inflate(R.layout.item_locate_activity_list_header, parent, false);
                ((CustomTextView) view.findViewById(R.id.locate_activity_list_header)).setText(String.valueOf(getItem
                        (position)));
                break;

            case VIEW_TYPE_NORMAL:
                view = Inflater.inflate(R.layout.item_locate_activity_list_normal, parent, false);
                ((CustomTextView) view.findViewById(R.id.locate_activity_list_normal_item)).setText(String.valueOf
                        (getItem(position)));
                break;

            case VIEW_TYPE_HOT:
                view = Inflater.inflate(R.layout.item_locate_activity_list_hot_city, parent, false);
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.locate_activity_list_hot_city_layout);
                for (int i = 0; i < hotItems.size(); i++) {
                    LabelButton button = new LabelButton(mController.getContext());
                    button.setText(hotItems.get(i).getmName());
                    final CityStruct struct = hotItems.get(i);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hotCityItemListener.onClick(v, struct);
                        }
                    });
                    layout.addView(button, i);
                    layout.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.white10));
                }
                break;

            case VIEW_TYPE_LOCATE:
                view = Inflater.inflate(R.layout.item_locate_activity_list_locate, parent, false);
                LabelButton button = (LabelButton) view.findViewById(R.id.locate_activity_list_locate_item);
                button.setText(String.valueOf(getItem(position)));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationListener.onClick(v, cityStruct);
                    }
                });
                break;
        }
        return view;
    }

    public void setLocateItem(CityStruct cs, String loc) {
        cityStruct = cs;
        locateItem = loc;
        notifyDataSetChanged();
    }

    /**
     * Prepare Data in list before smooth in
     *
     * @param msg
     */
    public void prepareData(String msg) {

        //TODO: load province list info

    }

    private String getString(int strId) {
        return mController.getString(strId);
    }

    public interface HotCityItemListener {
        void onClick(View view, CityStruct cityStruct);
    }

    public interface LocationListener {
        void onClick(View view, CityStruct cityStruct);
    }
}
