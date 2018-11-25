package com.dongci.sun.gpuimglibrary.player.script;

import com.dongci.sun.gpuimglibrary.player.DCAssetInfo;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DCScriptManager {
    public DCScript defaultScript;
    public DCScript currentScript;
    public List<DCScript> scripts;

    private static DCScriptManager instance;

    public static DCScriptManager scriptManager() {
        if (instance == null) {
            instance = new DCScriptManager();
        }
        return instance;
    }

    private DCScriptManager() {
        scripts = new ArrayList<>();
    }

    public void initDefaultScript(List<DCAssetInfo> assetInfos) {
        List<DCActor> actors = new ArrayList<>();
        for (int i = 0; i < assetInfos.size(); ++i) {
            DCAssetInfo info = assetInfos.get(i);
            DCActor actor = new DCActor();
            actor.actorId = i;
            actor.assetId = info.assetWrapper.mAsset.assetId;
            actor.renderIndex = i;
            actor.translation = info.centerVertex;
            actors.add(actor);
        }
        this.defaultScript = new DCScript();
        this.defaultScript.actors = actors;
        this.currentScript = this.defaultScript;
    }

    public List<DCActor> updateActors(long presentationTime) {
        List<DCActor> actors = null;
        DCScript targetScript = null;
        for (DCScript script : this.scripts) {
            if (script.timeRange.containsTime(presentationTime) || presentationTime > script.timeRange.endTime()) {
                targetScript = script;
            }
        }
        if (targetScript != null) {
            actors = targetScript.updateActors(presentationTime);
            this.currentScript = targetScript;
        }
        if (actors == null) {
            if (this.defaultScript != null) {
                actors = this.defaultScript.actors;
                this.currentScript = this.defaultScript;
            }
        }
        return actors;
    }

    public void createScriptsWithScriptConfigs(List<DCScriptConfig> configs) throws JSONException {
        if (configs == null) {
            return;
        }
        for (DCScriptConfig config : configs) {
            DCScript script = new DCScript(config.scriptFilePath);
            if (script != null) {
                script.timeRange = config.timeRange;
                for (DCActor actor : script.actors) {
                    for (DCScriptConfig.DCPair pair : config.mappingTable) {
                        if (actor.actorId == pair.left) {
                            actor.trackId = pair.right;
                        }
                    }
                }
            }
            DCScriptManager.scriptManager().scripts.add(script);
        }
    }

    public void clearScripts() {
        DCScriptManager.scriptManager().scripts.clear();
    }

    public boolean checkDecoration(String name) {
        if (scripts == null || name == null) {
            return false;
        }
        for (DCScript script : scripts) {
            for (DCActor actor : script.actors) {
                if (actor.decorationName != null && actor.decorationName.length() > 0 && actor.decorationName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
