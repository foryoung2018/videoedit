package com.dc.platform.voicebeating;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.dongci.sun.gpuimglibrary.player.script.DCScriptConfig;
import com.dongci.sun.gpuimglibrary.player.script.DCScriptManager;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEventManager;
import com.dongci.sun.gpuimglibrary.player.script.JsonTool;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.quickcreative.ConfigJsonBean;
import com.wmlive.hhvideo.heihei.quickcreative.VideoConfigJsonBean;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dongci.sun.gpuimglibrary.player.script.DCActor.ACTOR_TYPE_DECORATION;
import static com.dongci.sun.gpuimglibrary.player.script.DCActor.ACTOR_TYPE_VIDEO;

public class DCVoiceBeatingTool {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("dcvoicebeating-native");
    }

    public static final String TAG = "DCVoiceBeatingTool";


    VoiceBeat voiceBeat = null;


    public DCVoiceBeatingTool() {
        voiceBeat = VoiceBeat.createVoiceBeat();
    }


    public void generateBeatingVoiceFile() {
        voiceBeat.generateBeatingVoice();
    }

    public int getAudioDuration() {
        return voiceBeat.getAudioDuration();
    }

    public VoiceAnalysisInfo getVoiceAnalysisInfo(int assetId, String audioName) {
        VoiceAssetInfo assetInfo = new VoiceAssetInfo(assetId, audioName);
        VoiceAnalysisInfo analysisInfo = voiceBeat.generateVoiceAnalysisInfo(assetInfo);
        return analysisInfo;
    }

    public void destory() {
        voiceBeat.destory();
    }

    public void setVoiceBeatCallback(NotifyCallback callback) {
        voiceBeat.setCallback(callback);
    }

    public byte[] getAudioData(int len) {

        return voiceBeat.getAudioData(len);
    }

    public boolean stopThread() {
        return voiceBeat.stopThread();
    }

    public int audioSeek(int seekPos) {
        return voiceBeat.audioSeek(seekPos);
    }

    /**
     * 音频处理生成 .wav和.json文件
     *
     * @param path           模板解压后的文件路径
     * @param shortVideoList 视频集合
     */


    public String beatingVoice(String path, List<ShortVideoEntity> shortVideoList, ConfigJsonBean configJsonBean) {
        ArrayList<VoiceAssetInfo> assetList = new ArrayList<>();
        ArrayList<VoiceAnalysisInfo> analysisInfoList = new ArrayList<>();
        for (int i = 0; i < shortVideoList.size(); i++) {
            ShortVideoEntity videoEntity = shortVideoList.get(i);
            if (!TextUtils.isEmpty(videoEntity.editingAudioPath) && new File(videoEntity.editingAudioPath) {
            }.exists()) {
                VoiceAssetInfo assetInfo = new VoiceAssetInfo(i + 1, videoEntity.editingAudioPath);
                assetList.add(assetInfo);
                //VoiceAnalysisInfo analysisInfo = voiceBeat.generateVoiceAnalysisInfo(assetInfo);
                //analysisInfoList.add(analysisInfo);
                Log.d(TAG, "beatingVoice: videoEntity.extendInfo.analysisInfo===" + videoEntity.extendInfo.analysisInfo);
                analysisInfoList.add(videoEntity.extendInfo.analysisInfo);
            }
        }
        String exportPath = RecordFileUtil.createTimestampFile(RecordManager.get().getProductEntity().baseDir,
                RecordManager.PREFIX_CREATIVE_AUDIO_FILE,
                RecordManager.SUFFIX_AUDIO_FILE, true);
        VoiceInputInfo inputInfo = new VoiceInputInfo(
                path + File.separator + configJsonBean.getMidiurl(),
                path + File.separator + configJsonBean.getAudio_script(), assetList, analysisInfoList,
                exportPath);
        KLog.d(TAG, "beatingVoice: exportPath==" + exportPath);

        HashMap<Integer, ArrayList<VoiceTrackInfo>> map = voiceBeat.getVoiceTrackInfo(inputInfo);


        RecordManager.get().getProductEntity().combineAudio = exportPath;//导出最终的音频

        //准备播放脚本
        String jsonPath = RecordFileUtil.createTimestampFile(RecordManager.get().getProductEntity().baseDir,
                RecordManager.PREFIX_CREATIVE_AUDIO_FILE,
                RecordManager.SUFFIX_JSON_FILE, true);
        try {
            voiceInfoToJson(map, jsonPath);
            boolean timeEventsWithJsonFile = DCTimeEventManager.timeEventManager().createTimeEventsWithJsonFile(jsonPath);
            if (timeEventsWithJsonFile) {
                return jsonPath;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }


    public static void prepareScript(String jsonPath, String bgvJsonPath) {
        List<DCScriptConfig> dcScriptConfigs = null;
        try {
            dcScriptConfigs = loadScriptConfig(jsonPath, bgvJsonPath);
            DCScriptManager.scriptManager().createScriptsWithScriptConfigs(dcScriptConfigs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static String getDecorationConfigPath(String jsonPath, String decorationResourceJsonPath) throws JSONException {
        JSONObject scriptDic = JsonTool.loadJSONFromFile(jsonPath);
        if (scriptDic == null) {
            return null;
        }

        JSONArray actors = scriptDic.getJSONArray("actors");
        List<String> neededDecorationNames = new ArrayList<>();
        int videoActorCount = 0;

        for (int i = 0; i < actors.length(); ++i) {
            JSONObject actor = actors.getJSONObject(i);
            if (actor != null) {
                String type = actor.getString("type");
                if (type != null) {
                    if (type.equals(ACTOR_TYPE_DECORATION)) {
                        String decorationName = actor.getString("decoration");
                        if (decorationName != null) {
                            neededDecorationNames.add(decorationName);
                        }
                    } else if (type.equals(ACTOR_TYPE_VIDEO)) {
                        videoActorCount++;
                    }
                }
            }
        }
        // actual count of actors == videoActorCount - 1
        videoActorCount--;
        if (neededDecorationNames.size() == 0 || videoActorCount <= 0) {
            return jsonPath;
        }

        JSONObject decorationDic = JsonTool.loadJSONFromFile(decorationResourceJsonPath);
        if (decorationDic == null) {
            return jsonPath;
        }
        JSONArray decorations = decorationDic.getJSONArray("decorations");
        if (decorations == null) {
            return jsonPath;
        }

        List<String> decorationNames = new ArrayList<>();
        for (int i = 0; i < decorations.length(); ++i) {
            JSONObject decoration = decorations.getJSONObject(i);
            if (decoration != null) {
                String decorationName = decoration.getString("name");
                if (decorationName != null) {
                    decorationNames.add(decorationName);
                }
            }
        }

        boolean exists = false;
        for (int i = 0; i < neededDecorationNames.size(); ++i) {
            String neededName = neededDecorationNames.get(i);
            for (int j = 0; j < decorationNames.size(); ++j) {
                if (neededName.equals(decorationNames.get(j))) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                // default video script
                // check videoActorCount

                return AppCacheFileUtils.getAppCreativeAssetsPath() + videoActorCount + ".json";
            }
        }
        return jsonPath;
    }


    public static List<DCScriptConfig> loadScriptConfig(String voicejsonPath, String bgvJsonPath) throws JSONException {
        List<DCScriptConfig> configs = new ArrayList<>();
        JSONObject jsonObject = JsonTool.loadJSONFromFile(voicejsonPath + File.separator + "videoconfig.json");
        if (jsonObject != null) {
            JSONArray jsonConfigs = JsonTool.getJSONArray(jsonObject, "video_script");
            if (jsonConfigs != null) {
                for (int i = 0; i < jsonConfigs.length(); ++i) {
                    JSONObject jsonConfig = jsonConfigs.getJSONObject(i);
                    if (jsonConfig != null) {
                        DCScriptConfig config = new DCScriptConfig();
//                        config.scriptFilePath = path + File.separator + JsonTool.getString(jsonConfig, "id");

                        config.scriptFilePath = getDecorationConfigPath(voicejsonPath + File.separator + JsonTool.getString(jsonConfig, "id"), bgvJsonPath);
                        KLog.d(TAG, "loadScriptConfig: config.scriptFilePath====" + config.scriptFilePath);


                        JSONObject jsonTimeRange = JsonTool.getJSONObject(jsonConfig, "timeRange");
                        if (jsonTimeRange != null) {
                            long beginTime = (long) (JsonTool.getDouble(jsonTimeRange, "beginTime") * 1000000);
                            long endTime = (long) (JsonTool.getDouble(jsonTimeRange, "endTime") * 1000000);
                            long duration = endTime - beginTime;
                            config.timeRange = new DCAsset.TimeRange(beginTime, duration);
                        }

                        JSONArray jsonPairs = JsonTool.getJSONArray(jsonConfig, "actormap");
                        if (jsonPairs != null) {
                            for (int j = 0; j < jsonPairs.length(); ++j) {
                                JSONObject jsonPair = jsonPairs.getJSONObject(j);
                                if (jsonPair != null) {
                                    int actorId = JsonTool.getInt(jsonPair, "actorid");
                                    int trackId = JsonTool.getInt(jsonPair, "trackid");

                                    DCScriptConfig.DCPair pair = new DCScriptConfig.DCPair();
                                    pair.left = actorId;
                                    pair.right = trackId;

                                    config.mappingTable.add(pair);
                                }
                            }
                        }
                        configs.add(config);
                    }
                }
            }
        }
        return configs;
    }

    /**
     * @param map    voice info struct
     * @param output output path
     * @ZXGoto save voice info to json file
     */
    public static void voiceInfoToJson(HashMap<Integer, ArrayList<VoiceTrackInfo>> map, String output) throws JSONException, IOException {
        if (map == null || output == null) {
            return;
        }

        // convert voice info to JSONObject
        JSONObject destDict = new JSONObject();
        JSONArray assetAry = new JSONArray();
        for (Integer assetId : map.keySet()) {
            ArrayList<VoiceTrackInfo> ary = map.get(assetId);
            JSONArray infos = new JSONArray();
            if (ary != null) {
                for (VoiceTrackInfo trackInfo : ary) {
                    JSONArray voiceInfo = new JSONArray();
                    for (VoiceNodeInfo nodeInfo : trackInfo.voiceInfo) {
                        JSONObject nodeInfoDict = new JSONObject();
                        nodeInfoDict.put("inputStartTime", nodeInfo.inputStartTime);
                        nodeInfoDict.put("inputEndTime", nodeInfo.inputEndTime);
                        nodeInfoDict.put("outputStartTime", nodeInfo.outputStartTime);
                        nodeInfoDict.put("outputEndTime", nodeInfo.outputEndTime);
                        nodeInfoDict.put("scale", nodeInfo.scale);
                        voiceInfo.put(nodeInfoDict);
                    }
                    JSONObject infoDict = new JSONObject();
                    infoDict.put("trackId", trackInfo.trackId);
                    infoDict.put("voiceInfo", voiceInfo);
                    infos.put(infoDict);
                }
            }
            JSONObject assetDict = new JSONObject();
            assetDict.put("assetId", assetId);
            assetDict.put("tracks", infos);
            assetAry.put(assetDict);
        }
        destDict.put("voiceInfo", assetAry);

        // to write JSONObject to file
        OutputStream outputStream = new FileOutputStream(output);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write(destDict.toString());
        writer.close();
        outputStream.close();
    }

}
