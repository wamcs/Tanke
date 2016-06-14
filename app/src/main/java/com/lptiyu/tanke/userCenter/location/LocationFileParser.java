package com.lptiyu.tanke.userCenter.location;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.lptiyu.tanke.pojo.CityStruct;
import com.lptiyu.tanke.pojo.LocationStruct;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: 2319821734@qq.com
 */
public class LocationFileParser {

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

  private static HashMap<String, ArrayList<CityStruct>> cachedCitys = new HashMap<>();

  private static ArrayList<LinkedTreeMap> temp = new ArrayList<>();

  /**
   * Init when start Application
   * Cache the city info
   *
   * @param context
   * @param fileName
   */
  public static void init(Context context, String fileName) {
    InputStreamReader isr = null;
    try {
      isr = new InputStreamReader(context.getResources().getAssets().open(fileName));
      Gson gson = new Gson();
      temp = gson.fromJson(new JsonReader(isr), temp.getClass());
      for (int i = 0; i < temp.size(); i++) {
        LinkedTreeMap ltm = temp.get(i);
        LocationStruct ls = gson.fromJson(ltm.toString(), LocationStruct.class);
        cachedProvince.add(ls.getmProvinceName());
        cachedCitys.put(ls.getmProvinceName(), ls.getmCitys());

      }

    } catch (IOException e) {
      e.printStackTrace();
    }

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

}
