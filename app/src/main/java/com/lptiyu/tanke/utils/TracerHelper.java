package com.lptiyu.tanke.utils;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;

import java.util.List;

/**
 * Created by Jason on 2016/9/28.
 */

public class TracerHelper implements TraceListener {
    public LBSTraceClient traceClient;
    public TraceCallback callback;

    public TracerHelper(Context context, TraceCallback callback) {
        traceClient = new LBSTraceClient(context.getApplicationContext());
        this.callback = callback;

    }

    public void queryProcessedTrace(int lineID, List<TraceLocation> traceLocations, int coordinateType) {
        if (traceClient != null) {
            traceClient.queryProcessedTrace(lineID, traceLocations, coordinateType, this);
        }
    }

    @Override
    public void onRequestFailed(int lineID, String errorInfo) {
        if (callback != null) {
            callback.onRequestFailed(lineID, errorInfo);
        }
    }

    @Override
    public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
        if (callback != null) {
            callback.onTraceProcessing(lineID, index, segments);
        }
    }

    @Override
    public void onFinished(int lineID, List<LatLng> list, int distance, int waitingTime) {
        if (callback != null) {
            callback.onFinished(lineID, list, distance, waitingTime);
        }
    }

    public interface TraceCallback {
        void onRequestFailed(int lineID, String errorInfo);

        void onFinished(int lineID, List<LatLng> list, int distance, int waitingTime);

        void onTraceProcessing(int lineID, int index, List<LatLng> segments);
    }
}
