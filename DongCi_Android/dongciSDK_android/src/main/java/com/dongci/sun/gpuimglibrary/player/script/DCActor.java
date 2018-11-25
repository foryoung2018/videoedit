package com.dongci.sun.gpuimglibrary.player.script;

import com.dongci.sun.gpuimglibrary.player.DCOptions;
import com.dongci.sun.gpuimglibrary.player.math.DCVector2;
import com.dongci.sun.gpuimglibrary.player.math.DCVector3;

import org.json.JSONException;
import org.json.JSONObject;

public class DCActor {
    public static final String ACTOR_TYPE_VIDEO = "video";
    public static final String ACTOR_TYPE_DECORATION = "decoration";
    public static final String ACTOR_TYPE_BILLBOARD = "billboard";

    public int actorId;
    public int assetId;
    public int trackId;
    public int renderIndex;
    public DCVector3 translation;
    public DCVector3 rotation;
    public DCVector3 scale;
    public float transparency;
    public DCOptions.DCCoordinateInfo cropRect;
    public DCOptions.DCCoordinateInfo currentCropRect;
    public String decorationName;
    public int decorationActorId;
    public String type;
    public boolean hasRendered;

    public DCActor() {
        this.translation = new DCVector3(0, 0, 0);
        this.rotation = new DCVector3(0, 0, 0);
        this.scale = new DCVector3(1, 1, 1);
        this.transparency = 1;
        this.cropRect = new DCOptions.DCCoordinateInfo(
                new DCVector2(0, 0),
                new DCVector2(1, 0),
                new DCVector2(0, 1),
                new DCVector2(1, 1)
        );
        this.currentCropRect = new DCOptions.DCCoordinateInfo();
        this.currentCropRect.setRawData(
                this.cropRect.lt.x(),
                this.cropRect.lt.y(),
                this.cropRect.rt.x(),
                this.cropRect.rt.y(),
                this.cropRect.lb.x(),
                this.cropRect.lb.y(),
                this.cropRect.rb.x(),
                this.cropRect.rb.y()
        );

        this.type = ACTOR_TYPE_VIDEO;
        this.decorationActorId = -1;
    }

    public DCActor(JSONObject jsonObject) throws JSONException {
        this();
        if (jsonObject != null) {
            this.actorId = JsonTool.getInt(jsonObject, "id");
            this.assetId = JsonTool.getInt(jsonObject, "assetId");
            this.renderIndex = JsonTool.getInt(jsonObject, "renderIndex");

            JSONObject cropRectObj = JsonTool.getJSONObject(jsonObject, "cropRect");
            if (cropRectObj != null) {
                float left = (float) JsonTool.getDouble(cropRectObj, "left");
                float top = (float) JsonTool.getDouble(cropRectObj, "top");
                float right = (float) JsonTool.getDouble(cropRectObj, "right");
                float bottom = (float) JsonTool.getDouble(cropRectObj, "bottom");

                this.cropRect = new DCOptions.DCCoordinateInfo(
                        new DCVector2(left, top),
                        new DCVector2(right, top),
                        new DCVector2(left, bottom),
                        new DCVector2(right, bottom)
                );
            } else {
                this.cropRect = new DCOptions.DCCoordinateInfo(
                        new DCVector2(0, 0),
                        new DCVector2(1, 0),
                        new DCVector2(0, 1),
                        new DCVector2(1, 1)
                );
            }
        }
        this.currentCropRect.setRawData(
                this.cropRect.lt.x(),
                this.cropRect.lt.y(),
                this.cropRect.rt.x(),
                this.cropRect.rt.y(),
                this.cropRect.lb.x(),
                this.cropRect.lb.y(),
                this.cropRect.rb.x(),
                this.cropRect.rb.y()
        );

        String type = JsonTool.getString(jsonObject, "type");
        if (type != null) {
            this.type = type;
        }

        this.decorationName = JsonTool.getString(jsonObject, "decoration");

        this.decorationActorId = JsonTool.getInt(jsonObject, "decorationActorId");
    }
}
