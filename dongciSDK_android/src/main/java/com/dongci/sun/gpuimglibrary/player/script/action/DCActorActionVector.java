package com.dongci.sun.gpuimglibrary.player.script.action;

import com.dongci.sun.gpuimglibrary.player.math.DCVector3;
import com.dongci.sun.gpuimglibrary.player.script.JsonTool;

import org.json.JSONException;
import org.json.JSONObject;

public class DCActorActionVector extends DCActorAction {
    public DCVector3 valueFrom;
    public DCVector3 valueTo;

    public DCActorActionVector(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        if (jsonObject != null) {
            JSONObject valueFromObj = JsonTool.getJSONObject(jsonObject, "valueFrom");
            JSONObject valueToObj = JsonTool.getJSONObject(jsonObject, "valueTo");
            if (valueFromObj != null && valueToObj != null) {
                float x = (float)JsonTool.getDouble(valueFromObj, "x");
                float y = (float)JsonTool.getDouble(valueFromObj, "y");
                float z = (float)JsonTool.getDouble(valueFromObj, "z");
                this.valueFrom = new DCVector3(x, y, z);

                x = (float)JsonTool.getDouble(valueToObj, "x");
                y = (float)JsonTool.getDouble(valueToObj, "y");
                z = (float)JsonTool.getDouble(valueToObj, "z");
                this.valueTo = new DCVector3(x, y, z);
            }
        }
    }
}
