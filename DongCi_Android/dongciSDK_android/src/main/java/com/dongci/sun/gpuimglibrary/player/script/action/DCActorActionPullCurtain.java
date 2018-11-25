package com.dongci.sun.gpuimglibrary.player.script.action;

import com.dongci.sun.gpuimglibrary.player.DCOptions;
import com.dongci.sun.gpuimglibrary.player.script.JsonTool;

import org.json.JSONException;
import org.json.JSONObject;

import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentBottom;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentCenter;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentHorizontalCenter;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentLeft;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentRight;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentTop;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCAlignmentVerticalCenter;

public class DCActorActionPullCurtain extends DCActorActionValue {
    public @DCOptions.DCAlignment String alignment;

    public DCActorActionPullCurtain(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        if (jsonObject != null) {
            String alignment = JsonTool.getString(jsonObject, "alignment");
            if (alignment != null) {
                switch (alignment) {
                    case DCAlignmentLeft:
                        this.alignment = DCAlignmentLeft;
                        break;
                    case DCAlignmentRight:
                        this.alignment = DCAlignmentRight;
                        break;
                    case DCAlignmentTop:
                        this.alignment = DCAlignmentTop;
                        break;
                    case DCAlignmentBottom:
                        this.alignment = DCAlignmentBottom;
                        break;
                    case DCAlignmentHorizontalCenter:
                        this.alignment = DCAlignmentHorizontalCenter;
                        break;
                    case DCAlignmentVerticalCenter:
                        this.alignment = DCAlignmentVerticalCenter;
                        break;
                    case DCAlignmentCenter:
                        this.alignment = DCAlignmentCenter;
                        break;
                }
            } else {
                this.alignment = DCAlignmentCenter;
            }
        }
    }
}
