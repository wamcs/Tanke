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
    public TraceCallback callback;
    private Context context;

    public TracerHelper(Context context, TraceCallback callback) {
        this.callback = callback;
        this.context = context;

    }

    public void queryProcessedTrace(int lineID, List<TraceLocation> traceLocations, int coordinateType) {
        new LBSTraceClient(context).queryProcessedTrace(lineID, traceLocations, coordinateType, this);
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
