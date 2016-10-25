package com.lptiyu.tanke.gameplaying.records;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.GameService;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.thread;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Used to log the records when users in running game.
 * <p/>
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RecordsHandler extends Handler {

  private static final int RUNNING_RECORDS_MESSAGE = 0x1 << 1;

  private static final int RESUME_FROM_DISC = 0x1 << 2;

  private long gameId;

  long teamId;

  private RunningRecord mLastRecord;

  private MemRecords memRecords;

  private DiskRecords diskRecords;

  private static AtomicInteger mPointIndex = new AtomicInteger(1);

  //=========================  PUBLIC INTERFACE  ===============================


  private RecordsHandler(Looper looper, Builder builder) {
    super(looper);
    teamId = builder.teamId;
    gameId = builder.gameId;
    memRecords = builder.memRecords;
    diskRecords = builder.diskRecords;
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
          mPointIndex = new AtomicInteger(memRecords.getAll().size());
          callback.dataResumed(memRecords.getAll());
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleRecordMessage(Message msg) {
    if (msg.obj == null || !(msg.obj instanceof RunningRecord)) {
      Timber.e("Handle message error, object is null or not RunningRecord");
      return;
    }
    RunningRecord record = (RunningRecord) msg.obj;
    record.setIndex(mPointIndex.getAndAdd(1));
    record.setTeamId(teamId);
    if (mLastRecord != null) {
      record.setDistance((int) DistanceUtil.getDistance(mLastRecord.getLatLng(), record.getLatLng()));
    }
    mLastRecord = record;
    memRecords.add(record);
    uploadToServer(record);

    //TODO : upload record to server, only the special record such as arrive the point or finish the task in the point

    diskRecords.appendRecord(record);
  }

  private void uploadToServer(final RunningRecord record) {
    HttpService.getGameService()
        .uploadTeamGameRecords(Accounts.getId(), Accounts.getToken(), record.getIndex(),
            gameId, record.getTeamId(), record.getPointId(), record.getTaskId(),
            String.valueOf(record.getDistance()), String.valueOf(record.getX()),
            String.valueOf(record.getY()), teamId == Conf.TEMP_TEAM_ID ? GameService.USER_RECORD : GameService.TEAM_RECORD, record.getState().getType())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Response<Void>>() {
          @Override
          public void call(Response<Void> voidResponse) {
            if (voidResponse.getStatus() == Response.RESPONSE_OK) {
              Timber.d(AppData.globalGson().toJson(record));
            }
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Timber.e("upload records error");
          }
        });
  }

  public MemRecords getMemRecords() {
    return memRecords;
  }

  public static final class Builder {
    private long gameId = Long.MIN_VALUE;
    private long teamId = Long.MIN_VALUE;
    private MemRecords memRecords;
    private DiskRecords diskRecords;
    //    private FutureCallback<Response<List<RunningRecord>>> callback;
    private Handler mainHandler;

    public Builder(long gameId, long teamId) {
      withGameId(gameId);
      withTeamId(teamId);
    }

    public Builder withGameId(long val) {
      gameId = val;
      return this;
    }

    public Builder withTeamId(long teamId) {
      this.teamId = teamId;
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

    public Builder withMainHandler(Handler val) {
      mainHandler = val;
      return this;
    }

    public RecordsHandler build() {
      if (gameId == Long.MIN_VALUE || teamId == Integer.MIN_VALUE) {
        throw new IllegalArgumentException("You need to pass in activityId team_id");
      }

      if (memRecords == null) {
        memRecords = new MemRecords();
      }

      if (diskRecords == null) {
        diskRecords = new DiskRecords(DiskRecords.generateFile(gameId));
      }

      if (mainHandler == null) {
        mainHandler = new Handler(Looper.getMainLooper());
      }

      HandlerThread thread = new HandlerThread("record-handler");
      thread.start();
      return new RecordsHandler(thread.getLooper(), this);
    }
  }


}
