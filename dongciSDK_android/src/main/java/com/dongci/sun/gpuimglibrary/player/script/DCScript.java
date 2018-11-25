package com.dongci.sun.gpuimglibrary.player.script;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.DCOptions;
import com.dongci.sun.gpuimglibrary.player.math.DCVector2;
import com.dongci.sun.gpuimglibrary.player.math.DCVector3;
import com.dongci.sun.gpuimglibrary.player.script.action.DCAction;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorAction;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionPullCurtain;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionRotate;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionScale;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionTranslate;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionTranslucent;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionValue;
import com.dongci.sun.gpuimglibrary.player.script.action.DCActorActionVector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCActionTypePullCurtain;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCActionTypeRotate;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCActionTypeScale;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCActionTypeTranslate;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCActionTypeTranslucent;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentBottom;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentCenter;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentHorizontalCenter;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentLeft;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentRight;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentTop;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentVerticalCenter;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCInterPolatorTypeAccelerate;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCInterPolatorTypeAccelerateDecelerate;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCInterPolatorTypeDecelerate;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCInterPolatorTypeLinear;

public class DCScript {
    public String version;
    public DCAsset.TimeRange timeRange;
    public List<DCActor> actors = new ArrayList<DCActor>();
    public List<DCAction> actions = new ArrayList<DCAction>();
    public boolean isAnimated;
    public boolean isFullscreen;

    public DCScript() {
        this.timeRange = new DCAsset.TimeRange(0, 0);
    }

    public DCScript(String filename) throws JSONException {
        JSONObject scriptObj = JsonTool.loadJSONFromFile(filename);
        if (scriptObj == null) {
            return;
        }
        this.version = JsonTool.getString(scriptObj, "version");

        // is fullscreen
        this.isFullscreen = JsonTool.getBoolean(scriptObj, "isFullscreen");

        // timeRange
        JSONObject timeRangeObj = JsonTool.getJSONObject(scriptObj, "timeRange");
        if (timeRangeObj != null) {
            double beginTime = JsonTool.getDouble(timeRangeObj, "beginTime");
            double duration = JsonTool.getDouble(timeRangeObj, "duration");
            this.timeRange = new DCAsset.TimeRange((long)(beginTime * 1000000), (long)(duration * 1000000));
        }

        // read actors
        JSONArray actorObjs = JsonTool.getJSONArray(scriptObj, "actors");
        if (actorObjs != null) {
            for (int i = 0; i < actorObjs.length(); ++i) {
                JSONObject actorObj = actorObjs.getJSONObject(i);
                if (actorObj != null) {
                    DCActor actor = new DCActor(actorObj);
                    this.actors.add(actor);
                }
            }
            Collections.sort(this.actors, new Comparator<DCActor>() {
                @Override
                public int compare(DCActor o1, DCActor o2) {
                    if (o1.renderIndex > o2.renderIndex) {
                        return 1;
                    } else if (o1.renderIndex < o2.renderIndex) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }

        // read actions
        JSONArray actionObjs = JsonTool.getJSONArray(scriptObj, "actions");
        if (actionObjs != null) {
            for (int i = 0; i < actionObjs.length(); ++i) {
                JSONObject actionObj = actionObjs.getJSONObject(i);
                if (actionObj != null) {
                    String actionType = JsonTool.getString(actionObj, "actionType");
                    DCAction action = null;
                    switch (actionType) {
                        case DCActionTypeTranslate:
                            action = new DCActorActionTranslate(actionObj);
                            break;
                        case DCActionTypeRotate:
                            action = new DCActorActionRotate(actionObj);
                            break;
                        case DCActionTypeScale:
                            action = new DCActorActionScale(actionObj);
                            break;
                        case DCActionTypeTranslucent:
                            action = new DCActorActionTranslucent(actionObj);
                            break;
                        case DCActionTypePullCurtain:
                            action = new DCActorActionPullCurtain(actionObj);
                            break;
                    }
                    if (action != null) {
                        JSONObject interpolatorObj = JsonTool.getJSONObject(actionObj, "interpolator");
                        if (interpolatorObj != null) {
                            String interpolatorType = JsonTool.getString(interpolatorObj, "interpolatorType");
                            double factor = JsonTool.getDouble(interpolatorObj, "factor");
                            if (interpolatorType != null) {
                                switch (interpolatorType) {
                                    case DCInterPolatorTypeLinear:
                                        action.interpolator = new LinearInterpolator();
                                        break;
                                    case DCInterPolatorTypeAccelerate:
                                        action.interpolator = new AccelerateInterpolator((float)factor);
                                        break;
                                    case DCInterPolatorTypeAccelerateDecelerate:
                                        action.interpolator = new AccelerateDecelerateInterpolator();
                                        break;
                                    case DCInterPolatorTypeDecelerate:
                                        action.interpolator = new DecelerateInterpolator((float)factor);
                                        break;
                                }
                            }
                        }
                    }
                    this.actions.add(action);
                    if (action.timeRange.duration > 0) {
                        this.isAnimated = true;
                    }
                }
            }
        }
    }

    public List<DCActor> updateActors(long presentationTime) {
        if (presentationTime < this.timeRange.startTime) {
            return null;
        }
        if (this.timeRange.containsTime(presentationTime)) {
            long currentTime = presentationTime - this.timeRange.startTime;
            for (DCActor actor : this.actors) {
                // translate
                actor.translation = getVector3(DCActorActionTranslate.class, currentTime, actor.actorId, new DCVector3(0, 0, 0), null);
                actor.translation.setRawData(actor.translation.x(), -actor.translation.y(), actor.translation.z());

                // rotate
                actor.rotation = getVector3(DCActorActionRotate.class, currentTime, actor.actorId, new DCVector3(0, 0, 0), null);
                actor.rotation.setRawData((float)(actor.rotation.x() * Math.PI / 180.0),
                        (float)(actor.rotation.y() * Math.PI / 180.0), (float)(actor.rotation.z() * Math.PI / 180.0));

                // scale
                actor.scale = getVector3(DCActorActionScale.class, currentTime, actor.actorId, new DCVector3(1, 1, 1), null);

                // transparency
                actor.transparency = getVector3(DCActorActionTranslucent.class, currentTime, actor.actorId, new DCVector3(1, 0, 0), null).x();

                // crop rect
                actorCropRect(currentTime, actor);
            }
        }
        return this.actors;
    }

    private void actorCropRect(long currentTime, DCActor actor) {
        DCActorActionPullCurtain action;
        DCAction[] actions = new DCAction[1];
        float scale = getVector3(DCActorActionPullCurtain.class, currentTime, actor.actorId, new DCVector3(1, 0, 0), actions).x();
        if (actions[0] != null) {
            action = (DCActorActionPullCurtain)actions[0];
            float w = actor.cropRect.rt.x() - actor.cropRect.lt.x();
            float h = actor.cropRect.lb.y() - actor.cropRect.lt.y();

            switch (action.alignment) {
                case DCAlignmentRight:
                {
                    w *= scale;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.rt = new DCVector2(actor.cropRect.rt.x(), actor.cropRect.rt.y());
                    info.rb = new DCVector2(actor.cropRect.rb.x(), actor.cropRect.rb.y());
                    info.lt = new DCVector2(info.rt.x() - w, info.rt.y());
                    info.lb = new DCVector2(info.rb.x() - w, info.rb.y());
                    actor.currentCropRect = info;
                }
                break;
                case DCAlignmentTop:
                {
                    h *= scale;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.lt = new DCVector2(actor.cropRect.lt.x(), actor.cropRect.lt.y());
                    info.rt = new DCVector2(actor.cropRect.rt.x(), actor.cropRect.rt.y());
                    info.lb = new DCVector2(info.lt.x(), info.lt.y() + h);
                    info.rb = new DCVector2(info.rt.x(), info.rt.y() + h);
                    actor.currentCropRect = info;
                }
                break;
                case DCAlignmentBottom:
                {
                    h *= scale;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.lb = new DCVector2(actor.cropRect.lb.x(), actor.cropRect.lb.y());
                    info.rb = new DCVector2(actor.cropRect.rb.x(), actor.cropRect.rb.y());
                    info.lt = new DCVector2(info.lb.x(), info.lb.y() - h);
                    info.rt = new DCVector2(info.rb.x(), info.rb.y() -h);
                    actor.currentCropRect = info;
                }
                break;
                case DCAlignmentHorizontalCenter:
                {
                    float dalte = w * (1.0f - scale) / 2.0f;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.lt = new DCVector2(actor.cropRect.lt.x() + dalte, actor.cropRect.lt.y());
                    info.lb = new DCVector2(actor.cropRect.lb.x() + dalte, actor.cropRect.lb.y());
                    info.rt = new DCVector2(actor.cropRect.rt.x() - dalte, actor.cropRect.rt.y());
                    info.rb = new DCVector2(actor.cropRect.rb.x() - dalte, actor.cropRect.rb.y());
                    actor.currentCropRect = info;
                }
                break;
                case DCAlignmentVerticalCenter:
                {
                    float dalte = h * (1.0f - scale) / 2.0f;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.lt = new DCVector2(actor.cropRect.lt.x(), actor.cropRect.lt.y() + dalte);
                    info.lb = new DCVector2(actor.cropRect.lb.x(), actor.cropRect.lb.y() - dalte);
                    info.rt = new DCVector2(actor.cropRect.rt.x(), actor.cropRect.rt.y() + dalte);
                    info.rb = new DCVector2(actor.cropRect.rb.x(), actor.cropRect.rb.y() - dalte);
                    actor.currentCropRect = info;
                }
                break;
                case DCAlignmentCenter:
                {
                    float dalteW = w * (1.0f - scale) / 2.0f;
                    float dalteH = h * (1.0f - scale) / 2.0f;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.lt = new DCVector2(actor.cropRect.lt.x() + dalteW, actor.cropRect.lt.y() + dalteH);
                    info.lb = new DCVector2(actor.cropRect.lb.x() + dalteW, actor.cropRect.lb.y() - dalteH);
                    info.rt = new DCVector2(actor.cropRect.rt.x() - dalteW, actor.cropRect.rt.y() + dalteH);
                    info.rb = new DCVector2(actor.cropRect.rb.x() - dalteW, actor.cropRect.rb.y() - dalteH);
                    actor.currentCropRect = info;
                }
                break;
                case DCAlignmentLeft:
                default:
                {
                    w *= scale;
                    DCOptions.DCCoordinateInfo info = new DCOptions.DCCoordinateInfo();
                    info.lt = new DCVector2(actor.cropRect.lt.x(), actor.cropRect.lt.y());
                    info.lb = new DCVector2(actor.cropRect.lb.x(), actor.cropRect.lb.y());
                    info.rt = new DCVector2(info.lt.x() + w, actor.cropRect.rt.y());
                    info.rb = new DCVector2(info.lb.x() + w, actor.cropRect.rb.y());
                    actor.currentCropRect = info;
                }
                break;
            }
        } else {
            DCOptions.DCCoordinateInfo.copy(actor.currentCropRect, actor.cropRect);
        }
    }

    private DCVector3 getVector3(Class c, long currentTime, int actorId, DCVector3 defaultValue, DCAction[] out) {
        DCAction action1 = null;
        DCAction action2 = null;
        long minDuration = Long.MAX_VALUE;
        for (DCAction action : this.actions) {
            if (action.getClass().equals(c) && ((DCActorAction)action).actorId == actorId) {
                if (action.timeRange.containsTime(currentTime)) {
                    action1 = action;
                    break;
                } else {
                    long endTime = action.timeRange.endTime();
                    if (currentTime >= endTime) {
                        long d = currentTime - endTime;
                        if (d <= minDuration) {
                            minDuration = d;
                            action2 = action;
                        }
                    }
                }
            }
        }
        DCVector3 vec3 = new DCVector3(defaultValue.x(), defaultValue.y(), defaultValue.z());
        if (action1 != null) {
            long d = currentTime - action1.timeRange.startTime;
            float pos = action1.timeRange.duration == 0 ? 0 : (float) d / (float) action1.timeRange.duration;
            pos = action1.interpolator.getInterpolation(pos);
            if (DCActorActionVector.class.isAssignableFrom(action1.getClass())) {
                DCActorActionVector a = (DCActorActionVector)action1;
                vec3.setRawData(
                        a.valueFrom.x() + pos * (a.valueTo.x() - a.valueFrom.x()),
                        a.valueFrom.y() + pos * (a.valueTo.y() - a.valueFrom.y()),
                        a.valueFrom.z() + pos * (a.valueTo.z() - a.valueFrom.z())
                );
            } else if (DCActorActionValue.class.isAssignableFrom(action1.getClass())) {
                double x = ((DCActorActionValue)action1).valueFrom + pos * (((DCActorActionValue)action1).valueTo - ((DCActorActionValue)action1).valueFrom);
                x = Math.min(1.0, Math.max(0.0, x));
                vec3.setRawData((float) x, 0, 0);
            }
        } else if (action2 != null) {
            if (DCActorActionVector.class.isAssignableFrom(action2.getClass())) {
                vec3.setRawData(
                        ((DCActorActionVector)action2).valueTo.x(),
                        ((DCActorActionVector)action2).valueTo.y(),
                        ((DCActorActionVector)action2).valueTo.z()
                );
            } else if (DCActorActionValue.class.isAssignableFrom(action2.getClass())) {
                vec3.setRawData((float) Math.min(1.0, Math.max(0.0, ((DCActorActionValue)action2).valueTo)), 0, 0);
            }
        }
        if (out != null && out.length > 0) {
            if (action1 != null) {
                out[0] = action1;
            } else if (action2 != null) {
                out[0] = action2;
            }
        }
        return vec3;
    }
}
