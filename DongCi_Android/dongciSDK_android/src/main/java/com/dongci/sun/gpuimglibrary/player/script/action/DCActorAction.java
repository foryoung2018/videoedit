package com.dongci.sun.gpuimglibrary.player.script.action;

import com.dongci.sun.gpuimglibrary.player.script.JsonTool;

import org.json.JSONException;
import org.json.JSONObject;

public class DCActorAction extends DCAction {
    public int actorId;

    public DCActorAction(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        if (jsonObject != null) {
            this.actorId = JsonTool.getInt(jsonObject, "actorId");
        }
    }
}
