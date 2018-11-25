package com.wmlive.hhvideo.heihei.record.activitypresenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.BaseModel;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.LayoutInfo;
import com.wmlive.hhvideo.heihei.beans.opus.UploadMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.quickcreative.CreativeTemplateListBean;
import com.wmlive.hhvideo.heihei.beans.record.MvConfigItem;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvBgEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvConfig;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvMaterialEntity;
import com.wmlive.hhvideo.heihei.beans.recordmv.MvTemplateEntity;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.quickcreative.ConfigJsonBean;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.download.DownloadBean;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;


/**
 * Mv 录制页面基础功能逻辑
 */
public class RecordMvActivityHelper {

    public static final String EXTRA_RECORD_TYPE = "recordType";//录制，踹轨，草稿箱，
    public static final int EXTRA_RECORD_TYPE_RECORD = 1;//录制，踹轨，草稿箱，
    public static final int EXTRA_RECORD_TYPE_DRAFT = 2;//录制，踹轨，草稿箱，
    public static final int EXTRA_RECORD_TYPE_REPLACE = 3;//录制，踹轨，草稿箱，
    public static final int EXTRA_RECORD_TYPE_USE_CURENT_TEMPLATE = 4;//使用改模版进行创作
    public static final int TYPE_RECORD_MV = 40;//product类型为创意mv
    public static final String EXTRA_OPUS_ID = "extra_opus_id";//当前作品id**

    public static List<MvConfigItem> configlist;
    /**
     * 当前使用的配置信息
     */
    public MvConfig mvConfig;

    private Map<String, UploadMaterialEntity> mainDownloadMap;

    private ConfigJsonBean configJsonBean;

    private String currentTemplateName;

    public static void startRecordActivity(final BaseCompatActivity ctx, final int recordType, final long opusId) {
        if (PublishUtils.showToast()) {
            return;
        }
        final BaseModel count = new BaseModel();
        new RxPermissions(ctx).requestEach(RecordSetting.RECORD_PERMISSIONS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        KLog.i(count.type + "====请求权限：" + permission.toString() + permission.granted);
                        if (!permission.granted) {
                            if (Manifest.permission.CAMERA.equals(permission.name)) {
                                new PermissionDialog(ctx, 20).show();
                            } else if (Manifest.permission.RECORD_AUDIO.equals(permission.name)) {
                                new PermissionDialog(ctx, 10).show();
                            }
                        } else {
                            count.type++;
                        }
                        if (count.type == 3) {
                            KLog.i("=====获取权限：成功");
                            int result = -1;//-1表示权限获取失败，-2表示相机初始化失败，0表示权限和相机都成功
                            result = RecordManager.get().initRecordCore(ctx) ? 0 : -2;
                            KLog.i("=====获取权限：成功" + result);
                            if (result == 0) {
                                Intent intent = new Intent(ctx, RecordMvActivity.class);
                                intent.putExtra(EXTRA_RECORD_TYPE, recordType);
                                intent.putExtra(EXTRA_OPUS_ID, opusId);
                                ctx.startActivity(intent);
                            } else if (result == -1) {
                                ToastUtil.showToast("请在系统设置中允许App运行必要的权限");
                            } else {
                                KLog.i("=====初始化相机失败");
                                ToastUtil.showToast("初始化相机失败");
                                new PermissionDialog(ctx, 20).show();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.getMessage();
                        KLog.i("=====初始化相机失败:" + throwable.getMessage());
                        ToastUtil.showToast("初始化相机失败");
                    }
                });
    }

    RecordMvActivity activity;

    public RecordMvActivityHelper(RecordMvActivity activity) {
        this.activity = activity;
    }

    public String getCurrentTemplateName(){
        return currentTemplateName;
    }

    public void setCurrentTemplateName(String templateName){
        currentTemplateName = templateName;
        RecordManager.get().getProductEntity().getExtendInfo().template_name = templateName;
    }

    /**
     * 从配置文件读取 需要配置的信息
     */
    public void readConfigOnly(String templateName) {
        String fileString = FileUtil.getJsonConfigString(activity, AppCacheFileUtils.getAppCreativePath() + templateName + File.separator + "config.json");
        configJsonBean = JSON.parseObject(fileString, ConfigJsonBean.class);
        mvConfig = new MvConfig();
        mvConfig.duration = Float.parseFloat(configJsonBean.getRecordDuration());
        setCurrentTemplateName(templateName);
    }

    public void readConfig(String templateName) {
        String fileString = FileUtil.getJsonConfigString(activity, AppCacheFileUtils.getAppCreativePath() + templateName + File.separator + "config.json");
        configJsonBean = JSON.parseObject(fileString, ConfigJsonBean.class);
        mvConfig = new MvConfig();
        mvConfig.duration = Float.parseFloat(configJsonBean.getRecordDuration());
        initConfig(templateName);
        setCurrentTemplateName(templateName);
    }

    private void initConfig(String templateName){
        int count = configJsonBean.getItems().size();
        if(currentTemplateName!=null && currentTemplateName.equals(templateName)){//如果和之前的一致，不需要改变
            return;
        }
        if(currentTemplateName==null){//第一次进来
            RecordManager.get().getProductEntityMv(RecordMvActivityHelper.initDefaultFrame(count));
            createConfigList(count);
            createMaterialList(count);
        }else if(!currentTemplateName.equals(templateName)){//如果切换模板进来，
            createConfigList(count);
            addMaterial(count);
        }

        //更新ui
        activity.recordMvActivityView.initRecordOptionPanelMV();
    }

    public ConfigJsonBean getConfigJsonBean(){
        if(configJsonBean==null)
            readConfig(currentTemplateName);
        return configJsonBean;
    }

    /**
     * 构造素材配置集合，下载状态
     * 初始化数据时，调用
     * @param count
     */
    public static List<MvConfigItem> createConfigList(int count) {
        List<MvConfigItem> list = new ArrayList<MvConfigItem>();
        for (int i = 0; i < count; i++) {
            MvConfigItem configItem = new MvConfigItem();
            list.add(configItem);
        }
        configlist = list;
        return list;
    }

    /**
     * 构造素材集合
     *
     * @param materials
     */
    public static List<ShortVideoEntity> createMaterialListWidthId(List<UploadMaterialEntity> materials) {
        int count = materials.size();
        List<ShortVideoEntity> list = new ArrayList<ShortVideoEntity>();
        for (int i = 0; i < count; i++) {
            ShortVideoEntity shortVideoEntity = new ShortVideoEntity();
            shortVideoEntity.originalId = materials.get(i).ori_id;//设置id，踹轨进来的素材
            list.add(shortVideoEntity);
        }
        RecordManager.get().getProductEntity().setShortVideos(list);
        RecordFileUtil.prepareDirIndex(0, true);
        return list;
    }

    /**
     * 构造素材集合
     *
     * @param count
     */
    public static List<ShortVideoEntity> createMaterialList(int count) {
        List<ShortVideoEntity> list = new ArrayList<ShortVideoEntity>();
        for (int i = 0; i < count; i++) {
            ShortVideoEntity shortVideoEntity = new ShortVideoEntity();
            list.add(shortVideoEntity);
        }
        RecordManager.get().getProductEntity().setShortVideos(list);
        RecordFileUtil.prepareDirIndex(0, true);
        return list;
    }

    public List<ShortVideoEntity> addMaterial(int count){
        int oldCount = RecordManager.get().getProductEntity().shortVideoList.size();
        if(count>oldCount){//需要添加，
            for (int i = 0; i < count - oldCount; i++) {
                RecordManager.get().getProductEntity().shortVideoList.add(new ShortVideoEntity());
            }
        }else {//不需要处理

        }

        return null;
    }


    public static FrameInfo initDefaultFrame(int count) {
        FrameInfo frameInfo = new FrameInfo();
        List<LayoutInfo> layouts = new ArrayList<LayoutInfo>();
        for (int i = 0; i < count; i++) {
            LayoutInfo layoutInfo = new LayoutInfo();
            layouts.add(layoutInfo);
        }
        frameInfo.setLayout(1, layouts);
        return frameInfo;
    }

    /**
     * 录制完成后，设置缩率图
     *
     * @param videoPath
     * @return
     */
    public String getSnapShot(String videoPath) {
        String imagePath = FileUtil.createVideoThumb(videoPath);
        KLog.i("snapshot-->" + imagePath);
        return imagePath;
    }

    public void measurePreviewHeight(RelativeLayout preview) {
        KLog.i("measurePreviewHeight-->" + preview.getHeight());
    }

    /**
     * 创建素材下载任务
     *
     * @param response
     * @return
     */
    public ArrayList<DownloadBean> createMetrialDownload(MvMaterialEntity response) {
        mainDownloadMap = new HashMap<>();
        List<UploadMaterialEntity> materials = response.materials;
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        if (materials.size() > 0) {
            for (int i = 0; i < materials.size(); i++) {
                UploadMaterialEntity uploadMaterialEntity = materials.get(i);
                uploadMaterialEntity.index = i;
                //映射
                String videopath = uploadMaterialEntity.material_video;
                if (TextUtils.isEmpty(videopath)) {
                    videopath = uploadMaterialEntity.material_video_high;
                }
                mainDownloadMap.put(videopath, uploadMaterialEntity);
                //下载任务集合
                if (!TextUtils.isEmpty(videopath)) {
                    int downloadId = FileDownloadUtils.generateId(videopath, RecordFileUtil.getMaterialDir());
                    uploadMaterialEntity.downloadId = downloadId;
                    DownloadBean downloadBean = new DownloadBean(downloadId, videopath,
                            RecordFileUtil.getMaterialDir(), "", "", uploadMaterialEntity.material_index);
                    downloadList.add(downloadBean);
                }
                //给素材集合中的元素赋值

                RecordManager.get().getProductEntity().shortVideoList.get(i).coverUrl = uploadMaterialEntity.material_cover;
                RecordMvActivityHelper.configlist.get(i).state = 1;
                KLog.d("onGetMaterial: uploadMaterialEntity==" + uploadMaterialEntity);

            }
            activity.setMVListData();
        }
        return downloadList;
    }

    /**
     * 构造模板下载请求
     *
     * @param mvTemplateEntity
     * @param mvBgEntity
     * @return
     */
    public ArrayList<DownloadBean> createTemplateDownload(MvTemplateEntity mvTemplateEntity, MvBgEntity mvBgEntity) {
        ArrayList<DownloadBean> downloadList = new ArrayList<>();
        String tempPath = mvTemplateEntity.getZip_path();
        String bgPath = mvBgEntity.getBg_resource();
        int tempDownloadId = FileDownloadUtils.generateId(tempPath, AppCacheFileUtils.getAppCreativePath());
        int bgDownloadId = FileDownloadUtils.generateId(bgPath, AppCacheFileUtils.getAppCreativePath());
        DownloadBean tempDownload = new DownloadBean(tempDownloadId, tempPath, AppCacheFileUtils.getAppCreativePath(), "", "", 7);
        DownloadBean bgDownload = new DownloadBean(bgDownloadId, bgPath, AppCacheFileUtils.getAppCreativePath(), "", "", 8);
        downloadList.add(tempDownload);
        downloadList.add(bgDownload);
        return downloadList;
    }

    /**
     * 判断模板是不是可使用模板
     *
     * @param context
     * @param tempName
     * @return
     */
    public static boolean isNoInvalidTemplate(Context context, String tempName) {
        String ziplistStr = SPUtils.getString(context, SPUtils.CREATIVE_ZIP_LIST, "");
        if (!TextUtils.isEmpty(ziplistStr)) {
            CreativeTemplateListBean bean = JsonUtils.parseObject(ziplistStr, CreativeTemplateListBean.class);
            List<String> remove_template_list = bean.getRemove_template_list();
            if (!CollectionUtil.isEmpty(remove_template_list)) {
                for (int i = 0; i < remove_template_list.size(); i++) {
                    if (remove_template_list.get(i).equals(tempName)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * 判断下一步是否可以进行
     * @return
     */
    public static boolean checkNext(){
        //正常录制情况，使用该模板创作
        // 没有录制，不可以下一步，
        if(!RecordManager.get().getProductEntity().hasRecordMvVideo()){//没有录制视频，
            return false;
        }
        //踹轨，必须有一个自己录制， 草稿箱

        return true;
    }


}
