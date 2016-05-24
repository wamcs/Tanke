package com.lptiyu.tanke.gameplaying.records;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.lptiyu.tanke.utils.thread;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

/**
 * Used to log the records when users in running game.
 * <p>
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RecordsHandler extends Handler {

  private static final int RUNNING_RECORDS_MESSAGE = 0x1 << 1;

  private static final int RESUME_FROM_DISC = 0x1 << 2;

  private String activityId;

  int teamId;

  int temp = 1;

  boolean isTeamMaster;

  private RunningRecord mLastRecord = new RunningRecord.Builder().withType(RunningRecord.Type.OnFinish).build();

  private MemRecords memRecords;

  private DiskRecords diskRecords;

  private NetworkRecords networkRecords;

  private static AtomicInteger mPointIndex = new AtomicInteger(0);

  private static Handler mainHandler = new Handler(Looper.getMainLooper());

  private static final double LOCATION_DISTANCE_THRESHOLD_BOTTOM = 0.5;
  private static final double LOCATION_DISTANCE_THRESHOLD_TOP = 10;

  //=========================  PUBLIC INTERFACE  ===============================


  private RecordsHandler(Looper looper, Builder builder) {
    super(looper);
    isTeamMaster = builder.isTeamMaster;
    teamId = builder.teamId;
    activityId = builder.activity_id;
    memRecords = builder.memRecords;
    diskRecords = builder.diskRecords;
    networkRecords = builder.networkRecords;
  }

  public void dispatchRunningRecord(RunningRecord record) {
    Message msg = obtainMessage(RUNNING_RECORDS_MESSAGE, record);
    msg.sendToTarget();
  }

  public void dispatchResumeFromDisc(ResumeCallback callback) {
    obtainMessage(RESUME_FROM_DISC, callback).sendToTarget();
  }

  public interface ResumeCallback {
    void dataResumed(List<RunningRecord> recordList);
  }

  //========================== INNER METHOD ==================================

  @Override
  public void handleMessage(Message msg) {
    switch (msg.what) {
      case RUNNING_RECORDS_MESSAGE:
        handleRecordMessage(msg);
        break;
      case RESUME_FROM_DISC:
        handleResumeFromDisc(msg);
        break;
      default:
    }
  }

  private void handleResumeFromDisc(Message msg) {
    memRecords.clear();
    try {
      diskRecords.fromDisk(memRecords);
      final ResumeCallback callback = (ResumeCallback) msg.obj;
      thread.mainThread(new Runnable() {
        @Override
        public void run() {
          callback.dataResumed(memRecords.getAll());
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  Gson gson = new Gson();

  private void handleRecordMessage(Message msg) {
    if (msg.obj == null || !(msg.obj instanceof RunningRecord)) {
      Timber.e("Handle message error, object is null or not RunningRecord");
      return;
    }
    RunningRecord record = (RunningRecord) msg.obj;
    // get a new message, but we sometimes need not to handle it.
    if (mLastRecord != null && mLastRecord.getType() == RunningRecord.Type.Normal.type && record.getX() != null && record.getY() != null &&
        DistanceUtil.getDistance(
            new LatLng(Double.valueOf(record.getX()), Double.valueOf(record.getY())),
            new LatLng(Double.valueOf(mLastRecord.getX()), Double.valueOf(mLastRecord.getY()))) < LOCATION_DISTANCE_THRESHOLD_BOTTOM) {
      record.setIs_master(false);
//      networkRecords.sendToNetwork(record, activityId, new FutureCallback<Response<List<RunningRecord>>>() {
//        @Override
//        public void onCompleted(final Exception e, final Response<List<RunningRecord>> result) {
//          mainHandler.post(new Runnable() {
//            @Override
//            public void run() {
//              callback.onCompleted(e, result);
//            }
//          });
//        }
//      });

      Timber.v("Records abandoned");
      return;
    }

    record.setIndex(mPointIndex.getAndAdd(1));

    String s = gson.toJson(record);
    Timber.v(s);

    mLastRecord = record;

    memRecords.add(record);
    //TODO : upload record to server, only the special record such as arrive the point or finish the task in the point
//    networkRecords.sendToNetwork(record, activityId, new FutureCallback<Response<List<RunningRecord>>>() {
//      @Override
//      public void onCompleted(final Exception e, final Response<List<RunningRecord>> result) {
//        mainHandler.post(new Runnable() {
//          @Override
//          public void run() {
//            callback.onCompleted(e, result);
//          }
//        });
//      }
//    });
    diskRecords.appendRecord(record);
    RecordsUtils.writeMetaMessage(activityId, memRecords.getMetaMessage());
  }

  public MemRecords getMemRecords() {
    return memRecords;
  }

  public static final class Builder {
    private String activity_id = null;
    private boolean isTeamMaster = false;
    private int teamId = Integer.MIN_VALUE;
    private MemRecords memRecords;
    private DiskRecords diskRecords;
    private NetworkRecords networkRecords;
//    private FutureCallback<Response<List<RunningRecord>>> callback;
    private Handler mainHandler;

    public Builder(String activity_id, int teamId, boolean isTeamMaster) {
      withActivity_id(activity_id);
      withIsTeamMaster(isTeamMaster);
      withTeamId(teamId);
    }

    public Builder withActivity_id(String val) {
      activity_id = val;
      return this;
    }

    public Builder withTeamId(int teamId) {
      this.teamId = teamId;
      return this;
    }

    public Builder withIsTeamMaster(boolean val) {
      isTeamMaster = val;
      return this;
    }

    public Builder withMemRecords(MemRecords val) {
      memRecords = val;
      return this;
    }

    public Builder withDiskRecords(DiskRecords val) {
      diskRecords = val;
      return this;
    }

    public Builder withNetworkRecords(NetworkRecords val) {
      networkRecords = val;
      return this;
    }

    public Builder withMainHandler(Handler val) {
      mainHandler = val;
      return this;
    }

    public RecordsHandler build() {
      if (activity_id == null || teamId == Integer.MIN_VALUE) {
        throw new IllegalArgumentException("You need to pass in activityId team_id");
      }

      if (memRecords == null) {
        memRecords = new MemRecords();
      }

      if (diskRecords == null) {
        diskRecords = new DiskRecords(DiskRecords.generateFile(activity_id));
      }

      if (mainHandler == null) {
        mainHandler = new Handler(Looper.getMainLooper());
      }

      if (networkRecords == null) {
        networkRecords = new NetworkRecords();
      }

      HandlerThread thread = new HandlerThread("record-handler");
      thread.start();
      return new RecordsHandler(thread.getLooper(), this);
    }
  }


}
