package com.lptiyu.tanke.io.net;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/23
 *
 * @author ldx
 */
public final class Response<T> {

  public static final int RESPONSE_OK = 1;

  private int status;

  private String info;

  private T data;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "Response{" +
        "status=" + status +
        ", info='" + info + '\'' +
        ", data=" + data +
        '}';
  }
}
