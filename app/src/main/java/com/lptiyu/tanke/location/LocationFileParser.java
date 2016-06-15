package com.lptiyu.tanke.location;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.DirUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: 2319821734@qq.com
 */
public class LocationFileParser {

  public static final int FILE_TYPE_FROM_ASSETS = 1;
  public static final int FILE_TYPE_FROM_DIR = 2;
  /**
   * cache history province
   * cache province name list
   */
  private static ArrayList<String> cachedProvince = new ArrayList<>();

  /**
   * cache history read in city
   * key : province name
   * value : list of the city in the key province
   */
  private static ArrayList<CityStruct> hotCitys;

  private static HashMap<String, ArrayList<CityStruct>> cachedCitys = new HashMap<>();

  private static ArrayList<LinkedTreeMap> temp = new ArrayList<>();

  private static ArrayList<LocationStruct> cachedLocs = new ArrayList<>();

  /**
   * Init when start Application
   * Cache the city info
   *
   * @param context
   * @param fileType
   * @param fileName
   */
  public static boolean init(Context context, int fileType, String fileName) {
    InputStreamReader isr = null;
    try {
      switch (fileType) {
        case FILE_TYPE_FROM_ASSETS:
          isr = new InputStreamReader(context.getResources().getAssets().open(fileName));
          break;
        case FILE_TYPE_FROM_DIR:
          File file = new File(DirUtils.getResDirectory(), fileName);
          InputStream is = new FileInputStream(file);
          isr = new InputStreamReader(is);
          //TODO : ready to read city file from directory
          break;
      }

      if (null == isr) {
        isr = new InputStreamReader(context.getResources().getAssets().open(Conf.DEFAULT_CITY_ASSETS));
      }
      Gson gson = new Gson();
      temp = gson.fromJson(new JsonReader(isr), temp.getClass());
      for (int i = 0; i < temp.size(); i++) {
        LinkedTreeMap ltm = temp.get(i);
        LocationStruct ls = gson.fromJson(ltm.toString(), LocationStruct.class);
        if (!Conf.HOT_CITY.equals(ls.getmProvinceName())) {
          cachedLocs.add(ls);
          cachedProvince.add(ls.getmProvinceName());
          cachedCitys.put(ls.getmProvinceName(), ls.getmCitys());
        } else {
          hotCitys = ls.getmCitys();
        }
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static ArrayList<String> loadProvinceList() {
    return cachedProvince;
  }

  public static ArrayList<CityStruct> loadCityList(String provinceName) {
    ArrayList<CityStruct> citys = cachedCitys.get(provinceName);
    if (null != citys) {
      return citys;
    }
    return citys;
  }

  public static ArrayList<CityStruct> loadHotCityList() {
    return hotCitys;
  }

}
