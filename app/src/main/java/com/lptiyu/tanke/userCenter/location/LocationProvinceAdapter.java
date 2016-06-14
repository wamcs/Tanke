package com.lptiyu.tanke.userCenter.location;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.pojo.CityStruct;
import com.lptiyu.tanke.utils.Inflater;

import java.util.List;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationProvinceAdapter extends BaseAdapter {

  private boolean hasLocateItem = false;
  private boolean hasNormalItem = false;
  private boolean isLocateCityOpen;

  private LocateActivityController mController;

  private String locateItem;
  private List<String> normalItems;

  public static final int VIEW_TYPE_HEADER = 1;
  public static final int VIEW_TYPE_NORMAL = 2;
  public static final int VIEW_TYPE_LOCATE = 3;

  private LocationListener locationListener;

  public LocationProvinceAdapter(LocateActivityController controller, @Nullable String locateItem, @Nullable List<String> normalItems, Boolean isLocateCityOpen) {

    mController = controller;

    this.locateItem = locateItem;
    this.normalItems = normalItems;
    this.isLocateCityOpen = isLocateCityOpen;

    if (null != locateItem) {
      hasLocateItem = true;
    }
    if (null != normalItems) {
      hasNormalItem = true;
    }
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
          s = getString(R.string.all_location);
        }
        break;

      case VIEW_TYPE_NORMAL:
        s = normalItems.get(position - 3);
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
    if (0 == position || 2 == position ) {
      return VIEW_TYPE_HEADER;
    }
      return VIEW_TYPE_NORMAL;

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
        ((TextView) view.findViewById(R.id.locate_activity_list_header)).setText(String.valueOf(getItem(position)));
        break;

      case VIEW_TYPE_NORMAL:
        view = Inflater.inflate(R.layout.item_locate_activity_list_normal, parent, false);
        ((TextView) view.findViewById(R.id.locate_activity_list_normal_item)).setText(String.valueOf(getItem(position)));
        break;


      case VIEW_TYPE_LOCATE:
        view = Inflater.inflate(R.layout.item_locate_locate_city, parent, false);
        TextView name = (TextView) view.findViewById(R.id.locate_activity_locate_city);
        TextView tip = (TextView) view.findViewById(R.id.locate_activity_locate_error_text);
        LinearLayout button = (LinearLayout) view.findViewById(R.id.locate_activity_locate_layout);
        name.setText(String.valueOf(getItem(position)));
        if (isLocateCityOpen){
          tip.setVisibility(View.GONE);
        }
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            locationListener.onClick(v, locateItem);
          }
        });
        break;
    }
    return view;
  }

  public void setLocateItem(String loc, Boolean isOpen) {
    locateItem = loc;
    isLocateCityOpen = isOpen;
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

  public interface LocationListener {
    void onClick(View view, String string);
  }
}
