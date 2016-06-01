package com.lptiyu.tanke.gamedisplay;

import android.content.Context;
import android.net.Uri;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/1
 *
 * @author ldx
 */
public class DummyData {

  public static final List<GameDisplayEntity> entities = new ArrayList<>();
  private static final String ANDROID_RESOURCE = "android.resource://";
  private static final String FOREWARD_SLASH = "/";

  private static Uri resourceIdToUri(Context context, int resourceId) {
    return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
  }

  static {
    addData(R.mipmap.need_to_remove_1, "华科风云");
    addData(R.mipmap.need_remove_2, "中华上下五千年");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_to_remove_1, "五四三二一");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_to_remove_1, "华科风云");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_remove_3, "五四三二一");
    addData(R.mipmap.need_to_remove_1, "五四三二一");
    addData(R.mipmap.need_to_remove_1, "五四三二一");
    addData(R.mipmap.need_to_remove_1, "五四三二一");
    addData(R.mipmap.need_to_remove_1, "五四三二一");
  }

  private static void addData(int resId, String title) {
    GameDisplayEntity entity = new GameDisplayEntity();
    entity.setImg(resourceIdToUri(AppData.getContext(), resId).toString());
    entity.setTitle(title);
    entities.add(entity);
  }
}
