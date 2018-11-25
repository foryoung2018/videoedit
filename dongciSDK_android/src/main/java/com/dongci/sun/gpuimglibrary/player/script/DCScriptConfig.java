package com.dongci.sun.gpuimglibrary.player.script;

import com.dongci.sun.gpuimglibrary.player.DCAsset;

import java.util.ArrayList;
import java.util.List;

public class DCScriptConfig {
    public static class DCPair {
        public int left;
        public int right;
    }

    public String scriptFilePath;
    public DCAsset.TimeRange timeRange;
    public List<DCPair> mappingTable = new ArrayList<>();

    public DCScriptConfig() {
    }

    public DCScriptConfig(String scriptFilePath, DCAsset.TimeRange timeRange, List<DCPair> mappingTable) {
        this.scriptFilePath = scriptFilePath;
        this.timeRange = timeRange;
        this.mappingTable = mappingTable;
    }
}
