package com.wmlive.hhvideo.heihei.record.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.common.CutEntity;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.subject.TopicInfo;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MessageActivity;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.PlayerConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideosListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.service.PublishBGService;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.PublishEditMenuView;
import com.wmlive.hhvideo.heihei.record.widget.SmallEditVideoView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.heihei.record.widget.TopicInfoHolder;
import com.wmlive.hhvideo.heihei.subject.SubjectSearchActivity;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.ParamUtis;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.dialog.RemindDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 发布作品的界面
 */
public class PublishActivity extends DcBaseActivity {
    public static final int REQUEST_NEED_RELOAD = 100;
    public static final String KEY_FROM_RECORD = "key_from_record";
    public static final String KEY_FROM_RECORD_FROM = "key_from_record_from";//录制页面从哪里过来
    public static final int REQUEST_CODE_ADD_TOPIC = 120;
    public static final int EDITTEXT_DESC_MAX_LENGTH = 20;

    @BindView(R.id.videoViewsdk)
    TextureView videoViewsdk;
    @BindView(R.id.rlPreview)
    RelativeLayout rlPreview;
    @BindView(R.id.etDesc)
    EditText etDesc;
    @BindView(R.id.cbSaveLocal)
    CheckBox cbSaveLocal;
    @BindView(R.id.cbAllow)
    CheckBox cbAllow;
    @BindView(R.id.llInfoPanel)
    LinearLayout llInfoPanel;
    @BindView(R.id.llSaveDraft)
    LinearLayout llSaveDraft;
    @BindView(R.id.tv_publish)
    TextView llPublish;
    @BindView(R.id.tvTopicLabel)
    TextView tvTopicLabel;
    @BindView(R.id.ivDeleteTopic)
    ImageView ivDeleteTopic;
    @BindView(R.id.ivPlayerStatus)
    ImageView ivPlayerStatus;
    @BindView(R.id.llAddTopic)
    LinearLayout llAddTopic;
    @BindView(R.id.customFrameView)
    CustomFrameView customFrameView;
    @BindView(R.id.player_content)
    public FrameLayout frameLayoutPlayers;
    @BindView(R.id.framevideo)
    RelativeLayout frameVideos;
    PlayerEngine playerEngine;

    private TopicInfoHolder topicHolder;
    private float lastPosition = -1f;
    private boolean isPlay = true;
    private boolean fromRecord = true;
    private int recordFrom = 0;
    private HandlerThread handlerThread;
    private Handler handler;
    public static final int MSG_PUBLISH = 100;
    public static final int MSG_SAVE = 200;
    public static final int MSG_DISMISS = 300;

    private CircleProgressDialog dialog;
    public static PublishActivity mContext;
    private PublishEditMenuView publishEditMenu;
    private List<SmallEditVideoView> editVideoViewList;
    private float currentPosition = 0;
    private boolean needSeekVideo;
    private boolean canEditingClick = true;
    //    private UploadMaskView viewUploadMask;
    private String worksName;

    static PublishActivity activity;
    /**
     * 从录制页面进来，将要进行编辑的 音频文件
     */
    public static Map<Integer, String> audiosMap = new HashMap<Integer, String>();

    public static void startPublishActivity(Context ctx, boolean fromRecord, int recordFrom) {
        if (PublishUtils.showToast()) {
            return;
        }
        Intent intent = new Intent(ctx, PublishActivity.class);
        intent.putExtra(KEY_FROM_RECORD_FROM, recordFrom);
        intent.putExtra(KEY_FROM_RECORD, fromRecord);
        ctx.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        KLog.d("restart---publish>");
        if (playerEngine != null)
            playerEngine.release();
    }

    @Override
    protected void initData() {
        KLog.d("initData---publish>");
        super.initData();
        mContext = this;
        if (RecordManager.get().isFrameInfoValid()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setRelativeMode(true);
            KLog.d("initData---publish2>");
            setTitle("", true);
            setBlackToolbar();
            TextView tvSave = new TextView(this);
            tvSave.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
            tvSave.setText(getApplicationContext().getResources().getString(R.string.stringSaveToDraft));
            tvSave.setTextSize(14);
            TypedValue tv = new TypedValue();
            tvSave.setBackgroundResource(tv.resourceId);
            tvSave.setGravity(Gravity.CENTER);
            KLog.d("initData---publish4>");
            tvSave.setPadding(10, 6, DeviceUtils.dip2px(this, 15), 6);
            setToolbarRightView(tvSave, new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    saveDraft();
                }
            });
            publishEditMenu = new PublishEditMenuView(this);
            publishEditMenu.setMenuClickListener(onMenuClickListener);
            setToolbarCenterView(publishEditMenu, null);
            fromRecord = getIntent().getBooleanExtra(KEY_FROM_RECORD, true);
            recordFrom = getIntent().getIntExtra(KEY_FROM_RECORD_FROM, RecordActivitySdk.TYPE_NORMAL);
            handlerThread = new HandlerThread("SaveHandler");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case MSG_PUBLISH:
                        case MSG_SAVE:
                            if (dialog == null) {
                                dialog = SysAlertDialog.showCircleProgressDialog(PublishActivity.this, getApplicationContext().getResources().getString(msg.what == MSG_PUBLISH ?
                                        R.string.stringStartPublish : R.string.stringStartSave), true, false);
                            }
                            break;
                        case MSG_DISMISS:
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                            }
                            if (msg.arg1 > 0) {
                                MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                            }
                            break;
                        default:
                    }
                }
            };
//            initPlayer();
            topicHolder = new TopicInfoHolder(llAddTopic, tvTopicLabel, ivDeleteTopic, cbSaveLocal, cbAllow);
            topicHolder.showLoacalInfo(true);
            KLog.d("initData---publish5>");
            setListener();
            initOriginalMix();
            KLog.d("initData---publish6>");
            initEditVideoViewList();
            KLog.d("initData---publish7>");
            cbAllow.setChecked(RecordManager.get().getProductExtend().allowTeam);
            SPUtils.putInt(this, SPUtils.KEY_EDITING_STEP, RecordSetting.STEP_PUBLISH);
            KLog.d("initData---publish8>");
            TaskManager.get().getAllIp();
            initAudios();

            KLog.d("initData---publish9>");
        } else {
            toastFinish();
        }
    }

    public void initAudios() {
        if (RecordManager.get().getProductEntity() == null)
            return;
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audiosMap == null)
                    audiosMap = new HashMap<Integer, String>();
                for (int i = 0; i < RecordManager.get().getProductEntity().shortVideoList.size(); i++) {
                    ShortVideoEntity entity = RecordManager.get().getProductEntity().shortVideoList.get(i);
                    if (entity.editingAudioPath != null) {
                        String tempAudio = RecordFileUtil.createAudioFile(entity.baseDir);
                        FileUtil.copyFile(new File(entity.editingAudioPath), new File(tempAudio));
                        audiosMap.put(i, tempAudio);
                    }
                }
                handlerUi.post(runnableUi);
                handlerUi.post(runnableSetVolume);
            }
        }).start();

    }

    private void initPlayer() {
        KLog.i("combinePreview---initPlayer>");
        if (playerEngine != null)
            playerEngine.reset();
        else
            playerEngine = new PlayerEngine();
        TextureView textureView = new TextureView(this);
        frameVideos.removeAllViews();
        ivPlayerStatus.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        frameVideos.addView(textureView, params);

        playerEngine.build(textureView, PlayerConfig.width, PlayerConfig.height, PlayerConfig.fps, true, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                canEditingClick = false;
                refreshVideoAndMusic();
                canEditingClick = true;
                playerEngine.setAutoRepeat(true);
                KLog.i("**** * onPlayerPrepared currentPosition--pre playCreated" + lastPosition);

//                isPlay = true;
//                if (needSeekVideo) {
//                    needSeekVideo = false;
//                    playerEngine.seekTo(currentPosition);
//                    KLog.i("**** * onPlayerPrepared currentPosition " + lastPosition);
//                }

                playerEngine.setOnPlaybackListener(new PlayerListener() {
                    @Override
                    public void onPlayerPrepared() {
                        //将scence 添加，背景图添加
//                        isPlay = true;
//                        if (needSeekVideo) {
//                            needSeekVideo = false;
//                            playerEngine.seekTo(currentPosition);
//                            KLog.i("**** * onPlayerPrepared currentPosition " + lastPosition);
//                        }
                    }

                    @Override
                    public boolean onPlayerError(int var1, int var2) {
                        isPlay = false;
                        pauseMusic();
                        ivPlayerStatus.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public void onPlayerCompletion() {

                    }

                    @Override
                    public void onGetCurrentPosition(float var1) {

                    }
                });
                getRootView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playVideo();
                    }
                }, 200);
            }
        });
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        playerEngine.setPreviewAspectRatio(RecordManager.get().getSetting().getVideoRatio());
    }

    private void setListener() {
        llSaveDraft.setOnClickListener(this);
        llPublish.setOnClickListener(this);
        llAddTopic.setOnClickListener(this);
        cbSaveLocal.setOnClickListener(this);
        cbAllow.setOnClickListener(this);
        frameVideos.setOnClickListener(this);
        etDesc.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int destLength = dest.length();
                if (destLength + source.length() > EDITTEXT_DESC_MAX_LENGTH) {
                    ToastUtil.showToast(R.string.subject_add_error);
                    return source.subSequence(0, EDITTEXT_DESC_MAX_LENGTH - destLength);
                }
                return source;
            }
        }});
    }

    private void refreshVideoAndMusic() {
        combinePreview();
        if (RecordManager.get().getProductEntity() != null) {
            TopicInfoEntity topicInfo = RecordManager.get().getProductEntity().topicInfo;
            if (topicInfo != null) {
                if (!TextUtils.isEmpty(topicInfo.topicDesc)) {
                    etDesc.setText(topicInfo.topicDesc);
                }
                topicHolder.showTopic(topicInfo.topicTitle);
            } else {
                topicHolder.showAdd();
                etDesc.setText("");
            }
        }
    }

    private void setPlayerData() {
        List<MediaObject> mediaObjects = PlayerContentFactory.getPlayerMediaFromProductNew(RecordManager.get().getProductEntity());
        if (mediaObjects == null) {
            return;
        } else {
            if (mediaObjects.size() == 0) {
                ToastUtil.showToast("视频资源加载失败");
                return;
            }
        }
        //将scence 添加，背景图添加
        playerEngine.setMediaAndPrepare(mediaObjects);
    }

    /**
     * 设置数据，视频，+ 组合音频
     */
    private void setPlayerDataNew() {
        List<MediaObject> mediaObjects = PlayerContentFactory.getPlayerMediaFromProductWidthAudio(RecordManager.get().getProductEntity());
        if (mediaObjects == null) {
            return;
        } else {
            if (mediaObjects.size() == 0) {
                ToastUtil.showToast("视频资源加载失败");
                return;
            }
        }
        //将scence 添加，背景图添加
        playerEngine.setMediaAndPrepare(mediaObjects);
    }

    private void combinePreview() {
        if (playerEngine.isNull()) {
            initPlayer();
            return;
        }
        //组合视频，因为重新编辑裁剪操作，需要重新组合视频
//        setPlayerData();
        setPlayerDataNew();
    }

    private void initOriginalMix() {
        List<ShortVideoEntity> shortVideoList = RecordManager.get().getProductEntity().shortVideoList;
        MusicInfoEntity musicInfo = RecordManager.get().getProductEntity().musicInfo;
        if (shortVideoList != null && musicInfo != null && !TextUtils.isEmpty(musicInfo.getMusicPath())) {
            int maxValue = 0;
            for (int i = 0, size = shortVideoList.size(); i < size; i++) {
                ShortVideoEntity entity = shortVideoList.get(i);
                if (entity != null && maxValue < entity.getOriginalMixFactor()) {
                    maxValue = entity.getOriginalMixFactor();
                }
            }
            RecordManager.get().getProductEntity().originalMixFactor = maxValue;
        }
    }

    private boolean clickPublish;


    @Override
    protected void onSingleClick(View v) {
        DeviceUtils.hiddenKeyBoard(videoViewsdk);
        switch (v.getId()) {
            case R.id.framevideo:
                if (playerEngine.isPlaying()) {
                    playerEngine.pause();
                    isPlay = false;
                    pauseVideo();
                } else {
                    playVideo();
                }
                break;
            case R.id.tv_publish:
                if (!RecordManager.get().hasPublishingProduct() && !clickPublish) {
                    clickPublish = true;
                    if (GlobalParams.StaticVariable.sCurrentNetwork == 1 && !GlobalParams.StaticVariable.sHasShowedRemind) {
                        new RemindDialog(this)
                                .setListener(new RemindDialog.RemindClickListener() {
                                    @Override
                                    public void onOkClick() {
                                        doPublish();
                                    }
                                })
                                .show();
                        GlobalParams.StaticVariable.sHasShowedRemind = true;
                    } else {
                        doPublish();
                    }
                } else {
                    showToast("当前有作品正在发布，请发布完成之后再发布");
                }
                break;
            case R.id.llSaveDraft:
                saveDraft();
                break;
            case R.id.llAddTopic:
                if (topicHolder.hasTopic()) {
                    topicHolder.showAdd();
                } else {
                    startActivityForResult(new Intent(PublishActivity.this, SubjectSearchActivity.class), REQUEST_CODE_ADD_TOPIC);
                }
                break;
            default:
        }
    }

    private void doPublish() {
        if (GlobalParams.StaticVariable.ispublishing) {
            return;
        }
        saveInfo()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                        startActivity(new Intent(PublishActivity.this, MessageActivity.class));
                        RecordUtil.moveToPublishing(RecordManager.get().getProductEntity());
                        GlobalParams.StaticVariable.ispublishing = true;
                        Intent intent = new Intent(PublishActivity.this, PublishBGService.class);
                        intent.putExtra("ifsave", cbSaveLocal.isChecked());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("======发布失败:" + throwable.getMessage());
                        showToast("发布失败");
                    }
                });
    }

    private void saveDraft() {
        handler.sendEmptyMessage(MSG_SAVE);
        saveInfo()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (RecordUtil.moveToDraft(RecordManager.get().getProductEntity())) {
                            showToast("已保存到草稿");
                        }
                        RecordManager.get().clearAll();
                        Message message = Message.obtain();
                        message.what = MSG_DISMISS;
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("======发布失败:" + throwable.getMessage());
                        showToast("保存出错");
                        Message message = Message.obtain();
                        message.what = MSG_DISMISS;
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                });

    }

    private Observable<Boolean> saveInfo() {
        return Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        if (RecordManager.get().getProductEntity() != null) {
                            String coverPath = RecordManager.get().getProductEntity().baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
                            KLog.i("=====缩略图路径：" + coverPath);
                            //生成缩略图， 整个播放器的缩略图
                            if (playerEngine.getSnapShot(500, coverPath)) {
                                RecordManager.get().getProductEntity().coverPath = coverPath;
                                KLog.i("=====生成缩略图成功");
                            } else {
                                KLog.i("=====生成缩略图失败");
                            }
                            if (!TextUtils.isEmpty(etDesc.getText())) {
                                RecordManager.get().getProductEntity().topicInfo.topicDesc = etDesc.getText().toString();
                            }
                            RecordManager.get().getProductExtend().allowTeam = cbAllow.isChecked();
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 顶部菜单
     */
    private PublishEditMenuView.OnMenuClickListener onMenuClickListener = new PublishEditMenuView.OnMenuClickListener() {
        @Override
        public void onVolumeClick() {
            // 调节音量
            if (editVideoViewList != null && editVideoViewList.size() != 0) {
                int lastShowType = editVideoViewList.get(0).getViewType();
                if (customFrameView.getVisibility() == View.VISIBLE && lastShowType == SmallEditVideoView.VIEW_TYPE_VOLUME) {
                    customFrameView.setVisibility(View.GONE);
                } else {
                    customFrameView.setVisibility(View.VISIBLE);
                    SmallEditVideoView itemView;
                    int itemSize = editVideoViewList.size();
                    for (int i = 0; i < itemSize; i++) {
                        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(i);
                        itemView = editVideoViewList.get(i);
                        if (videoEntity != null && !TextUtils.isEmpty(videoEntity.editingVideoPath)) {
                            itemView.setViewType(SmallEditVideoView.VIEW_TYPE_VOLUME);
                            KLog.i("hsing", "videoEntity.getOriginalMixFactor() " + videoEntity.getOriginalMixFactor());
                            itemView.showVolumeBtn(true);
                        }
                    }
                }
            } else {
                initEditVideoViewList();
            }
        }

        @Override
        public void onEffectClick() {
            // 调节特效
            if (!playerEngine.isNull()) {
                playerEngine.pause();
                isPlay = false;
            }
            if (editVideoViewList != null && editVideoViewList.size() != 0) {
                int lastShowType = editVideoViewList.get(0).getViewType();
                if (customFrameView.getVisibility() == View.VISIBLE && lastShowType == SmallEditVideoView.VIEW_TYPE_EFFECT) {
                    customFrameView.setVisibility(View.GONE);
                } else {
                    customFrameView.setVisibility(View.VISIBLE);
                    SmallEditVideoView itemView;
                    int itemSize = editVideoViewList.size();
                    for (int i = 0; i < itemSize; i++) {
                        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(i);
                        itemView = editVideoViewList.get(i);
                        if (videoEntity != null && !TextUtils.isEmpty(videoEntity.editingVideoPath)) {
                            itemView.setViewType(SmallEditVideoView.VIEW_TYPE_EFFECT);
                            itemView.showEffectBtn(true);
                        }
                    }
                }
            } else {
                initEditVideoViewList();
            }
        }

        @Override
        public void onEditingClick() {
            // 裁剪视频
            if (canEditingClick) {
                EditProductionActivity.startEditProductionActivity(PublishActivity.this, EditProductionActivity.REQUEST_PAGE_TYPE_EDITING);
            }
        }
    };

    /**
     * 初始化视频编辑viewlist
     */
    private void initEditVideoViewList() {
        customFrameView.setVisibility(View.GONE);
        if (editVideoViewList == null) {
            editVideoViewList = new ArrayList<>();
            FrameInfo mFrameInfo = RecordManager.get().getFrameInfo();
            if (mFrameInfo != null) {
                ParamUtis.setLayoutParam(this, rlPreview, mFrameInfo.canvas_height, 170);
                ParamUtis.setLayoutParam(this, customFrameView, mFrameInfo.canvas_height, 170);
                SmallEditVideoView itemView;
                int layoutSize = mFrameInfo.getLayout().size();
                for (int i = 0; i < layoutSize; i++) {
                    ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(i);
                    itemView = new SmallEditVideoView(this);
                    itemView.setLayoutInfo(mFrameInfo.getLayout().get(i), false);
                    itemView.setTag(i);
                    if (videoEntity != null && !TextUtils.isEmpty(videoEntity.editingVideoPath)) {
                        itemView.setViewType(SmallEditVideoView.VIEW_TYPE_VOLUME);
                        KLog.i("hsing", "videoEntity.getOriginalMixFactor() " + videoEntity.getOriginalMixFactor());
                        itemView.setVideoVolume(videoEntity.getOriginalMixFactor());
                        itemView.setOnEventListener(eventListener);
                        if (!videoEntity.isImport()) {
                            videoEntity.setNeedExport(true);
                            videoEntity.hasEdited = true;
                        }
                    }
                    editVideoViewList.add(itemView);
                }
                if (editVideoViewList.size() > 0) {
                    customFrameView.setFrameView(mFrameInfo, editVideoViewList, false);
                }
            }
        }
    }

    private void playVideo() {
        isPlay = true;
        if (playerEngine.isNull()) {
            initPlayer();
        } else {

//            KLog.i("publish--playVideo-start" + (frameVideos.getVisibility() == View.VISIBLE));
//            for (int i = 0; i < frameVideos.getChildCount(); i++) {
//                KLog.i(i + "publish--playVideo-start-child>" + (frameVideos.getChildAt(i).getVisibility() == View.VISIBLE));
//            }
//            playerEngine.start();
            playerEngine.seekToPlay(lastPosition > 0 ? (long) lastPosition : 0, true);
//            playerEngine.se
        }
        ivPlayerStatus.setVisibility(View.GONE);
    }

    private void pauseVideo() {
        if (playerEngine != null) {
            if (playerEngine.isPlaying()) {
                playerEngine.pause();
            }
            pauseMusic();
        }
    }

    /**
     * 视频编辑View点击事件
     */
    private SmallEditVideoView.OnEventListener eventListener = new SmallEditVideoView.OnEventListener() {
        @Override
        public void onEffectClick(int index, SmallEditVideoView view) {
            // 特效
            if (!playerEngine.isNull()) {
                playerEngine.pause();
                isPlay = false;
            }

            final ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
            if (videoEntity != null && videoEntity.hasVideo()) {
                if (videoEntity.hasEffectAndFilter()) {
                    SysAlertDialog.showAlertDialog(PublishActivity.this, R.string.release_clear_effect_alert, R.string.release_back_press_cancel,
                            null, R.string.release_back_press_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    videoEntity.deleteEffect();
                                    if (videoEntity.isImport() && !TextUtils.isEmpty(videoEntity.importVideoPath)
                                            && new File(videoEntity.importVideoPath).exists()) {
                                        videoEntity.editingVideoPath = RecordFileUtil.createVideoFile(videoEntity.baseDir);
                                        try {
                                            FileUtil.copy(videoEntity.importVideoPath, videoEntity.editingVideoPath);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    RecordManager.get().updateProduct();
                                    EditVideoActivity.startEditVideoActivity(PublishActivity.this, index, true);
                                }
                            });
                } else {
                    if (videoEntity.isImport() && !TextUtils.isEmpty(videoEntity.importVideoPath)
                            && new File(videoEntity.importVideoPath).exists()) {
                        videoEntity.editingVideoPath = RecordFileUtil.createVideoFile(videoEntity.baseDir);
                        try {
                            FileUtil.copy(videoEntity.importVideoPath, videoEntity.editingVideoPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    RecordManager.get().updateProduct();
                    EditVideoActivity.startEditVideoActivity(PublishActivity.this, index, true);
                }
            }
        }

        @Override
        public void onVolumeChange(int index, SmallEditVideoView view, int volume) {
            // 音量改变
            ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
            if (videoEntity != null) {
                videoEntity.setOriginalMixFactor(volume);
                videoEntity.setNeedExport(true);
                videoEntity.hasEdited = true;
                videoEntity.volume = (float) volume / (float) RecordSetting.MAX_VOLUME;
                //数据更新完成
//                KLog.i(index + "onVaolumeChange-->scence" + playerEngine.getScene().get(ListToScence(index)).filePath);
                RecordManager.get().updateProduct();
                setVolume();
//                if (playerEngine.getScene() != null){
////                    playerEngine.setVolume(ListToScence(index),videoEntity.volume);
//                    playerEngine.getScene().get(ListToScence(index)).setVolume(videoEntity.volume);
//                }else
//                    KLog.e("onVaolumeChange-error->scence-is null");

//                for (int i = 0; i < RecordManager.get().getProductEntity().shortVideoList.size(); i++) {
//                    playerEngine.setVolume(ListToScence(index),videoEntity.volume);
//                    playerEngine.getScene().get(ListToScence(i)).setVolume(videoEntity.volume);
//                    KLog.i(index + "onVaolumeChange-->volume" + RecordManager.get().getShortVideoEntity(i).volume);
//                }
//                for (int i = 0; i < playerEngine.getScene().size(); i++) {
//                    KLog.i(index + "player-onVaolumeChange-->volume" + playerEngine.getScene().get(i).getVolume());
//                }
            }
        }
    };

    /**
     * 选中的id，与 播放器里的id
     */
    private int ListToScence(int index) {
        ShortVideoEntity videoEntity = RecordManager.get().getShortVideoEntity(index);
        if (playerEngine.getScene() != null) {
            for (int i = 0; i < playerEngine.getScene().size(); i++) {
                KLog.i(i + "ListToScence-filePath-->" + playerEngine.getScene().get(i).filePath);
                KLog.i("videoEntity-filePath-->" + videoEntity.combineVideoAudio);
                if (playerEngine.getScene().get(i).filePath.equals(videoEntity.combineVideoAudio)) {//播放器中的资源与 资源列表相同
                    KLog.i("videoEntity-filePath-->" + i);
                    return i;
                }
            }
        }
        return 0;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_TOPIC:
                if (resultCode == RESULT_OK) {
                    TopicInfo topicInfo = data.getParcelableExtra(TopicInfo.INTENT_EXTRA_KEY_NAME);
                    KLog.i("=====topicInfo:" + (topicInfo == null ? "null" : topicInfo.toString()));
                    if (topicInfo != null) {
                        topicHolder.showTopic(topicInfo.getTitle());
                        RecordManager.get().setTopicInfo(new TopicInfoEntity(topicInfo.getTopicId(), topicInfo.getTitle(), ""));
                    } else {
                        topicHolder.showTopic(null);
                        RecordManager.get().setTopicInfo(null);
                    }
                    RecordManager.get().updateProduct();
                }
                break;
            case REQUEST_NEED_RELOAD:
                if (resultCode == RESULT_OK) {
                    isPlay = false;
                    lastPosition = -1;
                    initPlayer();
                }
                break;
            case RecordActivitySdk.REQUEST_EDIT_VIDEO:
                // 编辑特效
                isPlay = false;
                lastPosition = -1;
                initPlayer();
                break;
            default:
                break;
        }
    }

    private void pauseMusic() {
        lastPosition = playerEngine.getCurrentPosition();
        ivPlayerStatus.setVisibility(View.VISIBLE);
        KLog.i("**** * onPlayerPrepared currentPosition--pre pauseMusic" + lastPosition);
    }

    private void releaseAnim() {
        if (playerEngine != null && playerEngine.isNull()) {
            playerEngine.release();
            playerEngine.destroy();
        }
        playerEngine = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RecordManager.get().getProductEntity() == null)//发布结束，
            return;
        if (playerEngine != null && lastPosition != -1f) {
            if (isPlay) {
                playerEngine.seekTo(lastPosition);
                initPlayer();
            }
        }
        etDesc.setText(worksName);

        for (int i = 0; i < RecordManager.get().getProductEntity().shortVideoList.size(); i++) {
            KLog.i(i + "publisActivity-video" + RecordManager.get().getShortVideoEntity(i).editingVideoPath);
            KLog.i(i + "publisActivity-audio" + RecordManager.get().getShortVideoEntity(i).editingAudioPath);
            KLog.i(i + "publisActivity-all" + RecordManager.get().getShortVideoEntity(i).combineVideoAudio);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerEngine != null) {
            if (playerEngine.isPlaying()) {
                pauseVideo();
            }
            lastPosition = playerEngine.getCurrentPosition();
            playerEngine.release();
        } else {
            lastPosition = -1f;
        }
        worksName = etDesc.getText().toString();

    }

    @Override
    protected void onDestroy() {
        topicHolder = null;
        isPlay = false;
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        releaseAnim();
        audiosMap.clear();
        audiosMap = null;
        mContext = null;
        clickPublish = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //TODO release MediaPlayer
        playerEngine.reset();
        normalBack();

    }

    private void normalBack() {
        RecordActivitySdk.startRecordActivity(this, RecordActivitySdk.TYPE_PBLISH, recordFrom);
        super.onBackPressed();
    }

    /**
     * 将 音频合并成一个单独的音频文件
     * 然后添加到播放器资源中，准备播放
     */
    private void muxAudio(String[] audiores) {
        mixAudio(audiores, new VideoListener() {
            @Override
            public void onStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog();
                    }
                });

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {//进行处理
                KLog.i("mux--finish->" + code);
                if (audiores != null) {
                    for (String audio : audiores) {//删除临时音频文件
                        FileUtil.deleteFile(audio);
                    }
                }
                handlerUi.post(runnableUi);

                initPlayer();
            }

            @Override
            public void onError() {
                KLog.e("mux--onError->");
                handlerUi.post(runnableUi);
            }


        });
    }

    Handler handlerUi = new Handler();

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            dismissDialog();
        }
    };

    Runnable runnableSetVolume = new Runnable() {
        @Override
        public void run() {
            setVolume();
        }
    };


    private void showDialog() {
        if (dialog == null)
            dialog = SysAlertDialog.showCircleProgressDialog(PublishActivity.this, getApplicationContext().getResources().getString(R.string.loading), true, false);
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void mixAudio(String[] audiores, VideoListener videoListener) {
        long time = System.currentTimeMillis();
        if (audiores == null) {//默认数据
            RecordUtilSdk.mixAudios(RecordManager.get().getProductEntity(), new VideoListener() {
                @Override
                public void onStart() {
                    if (videoListener != null)
                        videoListener.onStart();
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onFinish(int code, String outpath) {
                    KLog.i(System.currentTimeMillis() + "mixAudio---onFinish-time>" + (System.currentTimeMillis() - time));
                    KLog.i("mixAudio---onFinish>" + code + outpath);
                    //混合成功后
                    if (code == SdkConstant.RESULT_SUCCESS) {
                        KLog.i("mixAudio--onFinish-deleteFile-combineAudio>" + RecordManager.get().getProductEntity().combineAudio);
                        FileUtil.deleteFile(RecordManager.get().getProductEntity().combineAudio);
                        RecordManager.get().getProductEntity().combineAudio = outpath;
                        RecordManager.get().updateProduct();
                    }
                    if (videoListener != null)
                        videoListener.onFinish(code, outpath);
                }

                @Override
                public void onError() {
                    videoListener.onError();
                }
            });
            return;
        }
        ArrayList<String> audios = new ArrayList<String>();
        for (String audio : audiores) {
            if (!TextUtils.isEmpty(audio))
                audios.add(audio);
        }
        RecordUtilSdk.mixAudios(audios, new VideoListener() {
            @Override
            public void onStart() {
                if (videoListener != null)
                    videoListener.onStart();
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(System.currentTimeMillis() + "mixAudio---onFinish-time>" + (System.currentTimeMillis() - time));
                KLog.i("mixAudio---onFinish>" + code + outpath);
                //混合成功后
                if (code == SdkConstant.RESULT_SUCCESS) {
                    KLog.i("mixAudio--onFinish-deleteFile-combineAudio>" + RecordManager.get().getProductEntity().combineAudio);
                    FileUtil.deleteFile(RecordManager.get().getProductEntity().combineAudio);
                    RecordManager.get().getProductEntity().combineAudio = outpath;
                    RecordManager.get().updateProduct();
                }
                if (videoListener != null)
                    videoListener.onFinish(code, outpath);
            }

            @Override
            public void onError() {
                videoListener.onError();
            }
        });
    }

    /**
     * 给当前修改音量的audio设置音量
     * 之后将音频合并
     */
    private void setVolume() {
        pauseVideo();
        needSeekVideo = true;
        if (RecordManager.get().getProductEntity() == null)
            return;
        List<CutEntity> list = initClipVolumeData(RecordManager.get().getProductEntity());
        new VideoEngine().setVolumeWithNoSet(list, new VideosListener() {
            @Override
            public void onStart() {
                showDialog();
            }

            @Override
            public void onProgress(int progress) {
//                allProgress = RecordUtil.calculateProgressNew(progress, startPercentag, finalPerEntity);
//                if (viewCallback != null) {
//                    viewCallback.onPublishing(TYPE_EXPORT_VIDEO, allProgress);
//                }
            }

            @Override
            public void onFinish(int code, String... outpath) {
                muxAudio(outpath);//合并音频
            }

            @Override
            public void onError(int code, String msg) {

            }


        });
    }

    /**
     * 用作音频音量改变
     *
     * @param productEntity
     * @return
     */
    private List<CutEntity> initClipVolumeData(ProductEntity productEntity) {
        List<CutEntity> list = new ArrayList<CutEntity>();
        for (int i = 0; i < productEntity.shortVideoList.size(); i++) {
            ShortVideoEntity entity = productEntity.shortVideoList.get(i);

            CutEntity cutEntity = new CutEntity();
            cutEntity.path = entity.editingVideoPath;

            /**
             * 创建临时 音频文件
             */

            if (entity.editingAudioPath != null) {
                cutEntity.audioPath = audiosMap.get(i);
                cutEntity.volume = entity.volume;
                String autido = RecordFileUtil.createAudioFile(entity.baseDir);
                cutEntity.cutAudioPath = autido;
            } else {
                cutEntity.audioPath = entity.editingAudioPath;
            }
            KLog.i("setVolume--ori>" + entity.editingAudioPath);
            KLog.i("setVolume--vol>" + entity.volume);
            KLog.i("setVolume--res>" + cutEntity.cutAudioPath);
            list.add(cutEntity);
        }
        return list;
    }


    /**
     * 获取正在编辑的临时数据
     *
     * @return
     */
    public String[] getAudios() {
        if (audiosMap == null) {
            initAudios();
        }
        String[] result = new String[audiosMap.size()];

        Iterator iter = audiosMap.entrySet().iterator();
        int index = 0;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();

            String val = entry.getValue().toString();
            result[index] = val;
            index++;
        }
        return result;
    }

    public static void initAudios1(Handler handler) {
        if (RecordManager.get().getProductEntity() == null) {
            handler.sendEmptyMessage(2);
            return;
        }

        handler.sendEmptyMessage(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audiosMap == null)
                    audiosMap = new HashMap<Integer, String>();
                for (int i = 0; i < RecordManager.get().getProductEntity().shortVideoList.size(); i++) {
                    ShortVideoEntity entity = RecordManager.get().getProductEntity().shortVideoList.get(i);
                    if (entity.editingAudioPath != null) {
                        String tempAudio = RecordFileUtil.createAudioFile(entity.baseDir);
                        FileUtil.copyFile(new File(entity.editingAudioPath), new File(tempAudio));
                        audiosMap.put(i, tempAudio);
                    }
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

    }
}
