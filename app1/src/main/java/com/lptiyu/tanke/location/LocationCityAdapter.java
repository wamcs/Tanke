package com.lptiyu.tanke.location;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;

import java.util.List;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationCityAdapter extends BaseAdapter {

  private List<CityStruct> mCitys;

  private LocateUserActivityController mController;

  public LocationCityAdapter(LocateUserActivityController controller, List<CityStruct> city) {
    mController = controller;
    mCitys = city;
  }

  @Override
  public int getCount() {
    return mCitys.size() + 1;
  }

  @Override
  public CityStruct getItem(int position) {
    if (0 == position) {
      return null;
    }
    return mCitys.get(position - 1);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    if (0 == position) {
      return LocationProvinceAdapter.VIEW_TYPE_HEADER;
    }
    return LocationProvinceAdapter.VIEW_TYPE_NORMAL;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = null;
    switch (getItemViewType(position)) {

      case LocationProvinceAdapter.VIEW_TYPE_HEADER:
        view = Inflater.inflate(R.layout.item_locate_activity_list_header, parent, false);
        ((TextView) view.findViewById(R.id.locate_activity_list_header)).setText(getString(R.string.all_location));
        break;

      case LocationProvinceAdapter.VIEW_TYPE_NORMAL:
        view = Inflater.inflate(R.layout.item_locate_activity_list_normal, parent, false);
        ((TextView) view.findViewById(R.id.locate_activity_list_normal_item)).setText(getItem(position).getmName());
        break;
    }
    return view;
  }

  /**
   * Prepare city data in list before smooth in
   * @param msg
   */
  public void prepareData(String msg) {
    mCitys.clear();
    mCitys.addAll(LocationFileParser.loadCityList(msg));
    notifyDataSetChanged();
    //TODO : prepare data from directory

  }

  public String getString(int strId) {
    return mController.getString(strId);
  }

}
