package com.lptiyu.tanke.global;

import com.lptiyu.tanke.utils.ShaPrefer;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class CurrentLocation {

  public String getCachedLocation() {
    return ShaPrefer.getString("currentLocation", "");
  }

}
