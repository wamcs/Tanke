package com.lptiyu.tanke.utils.retrofit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class WriteToDiskMapper implements Func1<ResponseBody, File> {

  private File file = null;

  public WriteToDiskMapper(File file) {
    this.file = file;
  }

  @Override
  public File call(ResponseBody responseBody) {
    return writeResponseBodyToDisk(responseBody, file);
  }

  private File writeResponseBodyToDisk(ResponseBody body, File file) {
    try {
      InputStream inputStream = null;
      OutputStream outputStream = null;

      try {
        byte[] fileReader = new byte[4096];

        inputStream = body.byteStream();
        outputStream = new FileOutputStream(file);

        while (true) {
          int read = inputStream.read(fileReader);
          if (read == -1) {
            break;
          }
          outputStream.write(fileReader, 0, read);
        }
        outputStream.flush();
        return file;
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }

        if (outputStream != null) {
          outputStream.close();
        }
      }
    } catch (IOException e) {
      throw Exceptions.propagate(e);
    }
  }
}
