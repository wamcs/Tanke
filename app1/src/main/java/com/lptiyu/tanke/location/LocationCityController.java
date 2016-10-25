package com.lptiyu.tanke.location;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ContextController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.Display;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationCityController extends ContextController implements LocationScroll {

  @BindView(R.id.locate_activity_listview_city)
  ListView mListView;

  private List<CityStruct> mCitys;
  private LocationCityAdapter adapter;

  private LocateUserActivityController mController;

  private static final int LIST_WIDTH = Display.width();

  public LocationCityController(LocateUserActivityController controller, View view) {
    super(controller.getContext());
    mController = controller;
    ButterKnife.bind(this, view);

    init();
  }

  private void init() {
    mListView.setX(LIST_WIDTH);
    mCitys = new ArrayList<>();
    adapter = new LocationCityAdapter(mController, mCitys);
    mListView.setAdapter(adapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (0 == position) {
          return;
        }
        Intent intent = new Intent();
        intent.putExtra(Conf.CITY_STRUCT, adapter.getItem(position));
        mController.setResult(Conf.RESULT_CODE_START_USER_LOCATE, intent);
        mController.finish();
      }
    });
  }

  @Override
  public void prepare(String msg) {
    if (null != adapter) {
      adapter.prepareData(msg);
    }
    mListView.smoothScrollToPosition(0);
  }

  @Override
  public void smoothIn() {
    mListView.animate().alpha(1f).x(0);
  }

  @Override
  public void smoothOut() {
    mListView.animate().alpha(0f).x(LIST_WIDTH);
  }

}
