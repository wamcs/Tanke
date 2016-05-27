package com.lptiyu.tanke.gameplaying.records;

import com.google.gson.Gson;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
class DiskRecords {

  private File mCacheFile = null;

  private Gson gson = new Gson();

  static File generateFile(long gameId) {
    return new File(DirUtils.getRecordDirectory(), String.format("aid=%d.run", gameId));
  }

  static File generateMetaFile(long gameId) {
    return new File(DirUtils.getRecordDirectory(), String.format("aid=%d.meta", gameId));
  }

  DiskRecords(File cacheFile) {
    this.mCacheFile = cacheFile;
  }

  public boolean isEmpty() {
    return mCacheFile == null || !mCacheFile.isFile() || mCacheFile.length() == 0;
  }

  public RunningRecord firstItem() throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(mCacheFile), 2048);
    return gson.fromJson(reader.readLine(), RunningRecord.class);
  }

  public RunningRecord lastItem() throws IOException {
    return gson.fromJson(FileUtils.tail(mCacheFile), RunningRecord.class);
  }

  public File getCacheFile() {
    return mCacheFile;
  }

  public void appendRecord(RunningRecord record) {
    if (record == null) {
      Timber.e("Params is null");
      return;
    }

    synchronized (this) {
      try {
        FileWriter writer = new FileWriter(mCacheFile, true);
        writer.append(gson.toJson(record));
        writer.append("\n");
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void appendRecords(List<RunningRecord> recordList) {
    if (recordList == null || 0 == recordList.size()) {
      return;
    }

    synchronized (this) {
      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(mCacheFile, true));
        for (RunningRecord record : recordList) {
          writer.append(gson.toJson(record));
          writer.newLine();
        }
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * load all data from disk, the file send in when construct.
   *
   * @param memRecords
   */
  public void fromDisk(MemRecords memRecords) throws IOException {
    if (memRecords == null) {
      return;
    }

    synchronized (this) {
      BufferedReader reader = new BufferedReader(new FileReader(mCacheFile));
      String line = reader.readLine();
      while (line != null) {
        memRecords.add(gson.fromJson(line, RunningRecord.class));
        line = reader.readLine();
      }
      reader.close();
    }

  }

}
