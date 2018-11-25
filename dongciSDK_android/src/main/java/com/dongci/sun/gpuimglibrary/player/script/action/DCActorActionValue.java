package com.dongci.sun.gpuimglibrary.player.script.action;

import org.json.JSONException;
import org.json.JSONObject;

public class DCActorActionValue extends DCActorAction {
    public double valueFrom;
    public double valueTo;

    public DCActorActionValue(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        if (jsonObject != null) {
            this.valueFrom = jsonObject.getDouble("valueFrom");
            this.valueTo = jsonObject.getDouble("valueTo");
        }
    }
}
