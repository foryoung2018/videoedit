package com.dongci.sun.gpuimglibrary.player.script.action;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.script.JsonTool;

import org.json.JSONException;
import org.json.JSONObject;

public class DCAction {
    public DCAsset.TimeRange timeRange;
    public Interpolator interpolator;

    public DCAction(JSONObject jsonObject) throws JSONException {
        interpolator = new LinearInterpolator();
        if (jsonObject != null) {
            JSONObject timeRangeObj = JsonTool.getJSONObject(jsonObject, "timeRange");
            if (timeRangeObj != null) {
                double beginTime = JsonTool.getDouble(timeRangeObj, "beginTime");
                double duration = JsonTool.getDouble(timeRangeObj, "duration");
                this.timeRange = new DCAsset.TimeRange((long)(beginTime * 1000000), (long)(duration * 1000000));
            }
        }
    }
}
