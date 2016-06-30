package com.lptiyu.tanke.update;


/**
 * @author : xiaoxiaoda
 *         date: 16-6-27
 *         email: wonderfulifeel@gmail.com
 */
public class VersionEntity {

  private String url;

  private String versionName;

  private int versionCode;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }
}
