package com.dongci.sun.gpuimglibrary.player.script;

import android.util.Log;

import com.dongci.sun.gpuimglibrary.player.DCAsset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DCTimeEventManager {
    private List<DCTimeEvent> timeEvents;

    private static DCTimeEventManager instance;

    public static DCTimeEventManager timeEventManager() {
        if (instance == null) {
            instance = new DCTimeEventManager();
        }
        return instance;
    }

    private DCTimeEventManager() {
        this.timeEvents = new ArrayList<>();
    }

    public boolean createTimeEventsWithJsonFile(String filepath) throws JSONException {
        Log.d("filepath", "createTimeEventsWithJsonFile: filepath==" + filepath);
        JSONObject audioConfigObj = JsonTool.loadJSONFromFile(filepath);
        if (audioConfigObj == null) {
            return false;
        }
        JSONArray voiceInfoAry = JsonTool.getJSONArray(audioConfigObj, "voiceInfo");
        if (voiceInfoAry != null) {
            for (int i = 0; i < voiceInfoAry.length(); ++i) {
                JSONObject assetObj = voiceInfoAry.getJSONObject(i);
                if (assetObj != null) {
                    int assetId = JsonTool.getInt(assetObj, "assetId");
                    JSONArray trackObjAry = JsonTool.getJSONArray(assetObj, "tracks");
                    if (trackObjAry != null) {
                        for (int j = 0; j < trackObjAry.length(); ++j) {
                            JSONObject trackObj = trackObjAry.getJSONObject(j);
                            if (trackObj != null) {
                                int trackId = JsonTool.getInt(trackObj, "trackId");
                                JSONArray infoObjAry = JsonTool.getJSONArray(trackObj, "voiceInfo");
                                if (infoObjAry != null) {
                                    for (int m = 0; m < infoObjAry.length(); ++m) {
                                        JSONObject infoObj = infoObjAry.getJSONObject(m);
                                        if (infoObj != null) {
                                            long inputStartTime = JsonTool.getLong(infoObj, "inputStartTime");
                                            long inputEndTime = JsonTool.getLong(infoObj, "inputEndTime");
                                            long outputStartTime = JsonTool.getLong(infoObj, "outputStartTime");
                                            double timeScale = JsonTool.getDouble(infoObj, "scale");
                                            int playMode = JsonTool.getInt(infoObj, "playMode");

                                            DCTimeEvent timeEvent = new DCTimeEvent();
                                            timeEvent.assetId = assetId;
                                            timeEvent.trackId = trackId;
                                            timeEvent.eventTime = outputStartTime;
                                            timeEvent.beginTime = inputStartTime;
                                            timeEvent.endTime = inputEndTime;
                                            timeEvent.scale = (float)(1.0 / timeScale);
                                            timeEvent.targetDuration = (long) ((timeEvent.endTime - timeEvent.beginTime) * timeEvent.scale);
                                            timeEvent.playMode = playMode;
                                            this.timeEvents.add(timeEvent);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(this.timeEvents, new Comparator<DCTimeEvent>() {
            @Override
            public int compare(DCTimeEvent o1, DCTimeEvent o2) {
                if (o1.eventTime > o2.eventTime) {
                    return 1;
                } else if (o1.eventTime < o2.eventTime) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return true;
    }

    public  void clearEvents() {
        this.timeEvents.clear();
    }
    public DCTimeEvent getTimeEvent(long time, int trackId, DCAsset.TimeRange scriptTimeRange) {
        DCTimeEvent dest = null;
        for (DCTimeEvent timeEvent : this.timeEvents) {
            if (timeEvent.trackId == trackId) {
                if (time >= timeEvent.eventTime) {
                    dest = timeEvent;
                }
            }
        }
        if (dest == null) {
            for (DCTimeEvent event : this.timeEvents) {
                if (event.trackId == trackId) {
                    if (event.eventTime > scriptTimeRange.startTime && event.eventTime < scriptTimeRange.endTime()) {
                        dest = new DCTimeEvent();
                        dest.assetId = event.assetId;
                        dest.trackId = event.trackId;
                        dest.eventTime = scriptTimeRange.startTime;
                        dest.beginTime = 0;
                        dest.endTime = event.endTime;
                        dest.scale = event.scale;
                        return dest;
                    }
                }
            }
        }
        return dest;
    }
}
