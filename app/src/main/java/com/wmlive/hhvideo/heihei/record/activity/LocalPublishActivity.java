package com.wmlive.hhvideo.heihei.record.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.rd.vecore.VirtualVideo;
//import com.rd.vecore.VirtualVideoView;
//import com.rd.vecore.exception.InvalidArgumentException;
//import com.rd.vecore.exception.InvalidStateException;
//import com.rd.vecore.models.Scene;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.subject.TopicInfo;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.mainhome.activity.DiscoveryActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MessageActivity;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerViewFactory;
import com.wmlive.hhvideo.heihei.record.engine.exception.InvalidArgumentException;
import com.wmlive.hhvideo.heihei.record.engine.exception.InvalidStateException;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.PlayerResize;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.model.PublishModel;
import com.wmlive.hhvideo.heihei.record.service.PublishBGService;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.heihei.record.widget.TopicInfoHolder;
import com.wmlive.hhvideo.heihei.subject.SubjectSearchActivity;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.UploadMaskView;
import com.wmlive.hhvideo.widget.dialog.RemindDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LocalPublishActivity extends DcBaseActivity implements
        UploadMaskView.UploadMaskListener
//        VirtualVideoView.VideoViewListener
{

    private static final String KEY_FROM_TYPE = "key_from_type";
    public static final byte FORM_SEARCH = 0;
    public static final byte FORM_DRAFT = 1;

    //    @BindView(R.id.videoView)
//    VirtualVideoView videoView;
//    @BindView(R.id.videoviewsdk)
//    TextureView videoViewSdk;
    @BindView(R.id.etDesc)
    EditText etDesc;
    @BindView(R.id.tvTopicLabel)
    TextView tvTopicLabel;
    @BindView(R.id.ivDeleteTopic)
    ImageView ivDeleteTopic;
    @BindView(R.id.llAddTopic)
    LinearLayout llAddTopic;
    @BindView(R.id.cbSaveLocal)
    CheckBox cbSaveLocal;
    @BindView(R.id.cbAllow)
    CheckBox cbAllow;
    @BindView(R.id.llSaveDraft)
    LinearLayout llSaveDraft;
    @BindView(R.id.tv_publish)
    TextView llPublish;
    @BindView(R.id.llInfoPanel)
    LinearLayout llInfoPanel;
    @BindView(R.id.ivPlayerStatus)
    ImageView ivPlayerStatus;
    @BindView(R.id.player_content)
    RelativeLayout frameLayout;

    private PlayerEngine playerEngine;
    //    private VirtualVideo virtualVideo;
    private UploadMaskView viewUploadMask;
    private TopicInfoHolder topicHolder;
    private PopupWindow ppwShare;
    private HandlerThread handlerThread;
    private Handler handler;
    private CircleProgressDialog dialog;
    private float currentPosition = 0;
    private boolean isClickPause = false;
    private boolean isClickPlay = false;

    private boolean isPlayError = false;
    private Scene<MediaObject> videoScene;
    private byte fromType = FORM_SEARCH;

    public static void startLocalPublishActivity(BaseCompatActivity ctx, byte fromType) {
        if (PublishUtils.showToast()) {
            return;
        }
        Intent intent = new Intent(ctx, LocalPublishActivity.class);
        intent.putExtra(KEY_FROM_TYPE, fromType);
        ctx.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_local_publish;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("", true);
        setBlackToolbar();
        if (RecordManager.get().getProductEntity() == null || RecordManager.get().getProductEntity().combineVideo == null) {
            toastFinish();
            return;
        }
        EventHelper.register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fromType = getIntent().getByteExtra(KEY_FROM_TYPE, FORM_SEARCH);
//        virtualVideo = new VirtualVideo();
//        virtualVideo.addScene(videoScene);
        viewUploadMask = new UploadMaskView(this);
        getWindow().addContentView(viewUploadMask,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewUploadMask.setVisibility(View.INVISIBLE);
        viewUploadMask.setUploadMaskListener(this);
        llPublish.setOnClickListener(this);
        llAddTopic.setOnClickListener(this);
        frameLayout.setClickable(true);
        frameLayout.setOnClickListener(this);
        topicHolder = new TopicInfoHolder(llAddTopic, tvTopicLabel, ivDeleteTopic, cbSaveLocal, cbAllow);
        handlerThread = new HandlerThread("SaveHandler");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PublishActivity.MSG_PUBLISH:
                    case PublishActivity.MSG_SAVE:
                        if (dialog == null) {
                            dialog = SysAlertDialog.showCircleProgressDialog(LocalPublishActivity.this,
                                    getString(msg.what == PublishActivity.MSG_PUBLISH ?
                                            R.string.stringStartPublish : R.string.stringStartSave), true, false);
                        }
                        break;
                    case PublishActivity.MSG_DISMISS:
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
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 300);

        TopicInfoEntity infoEntity = RecordManager.get().getProductEntity().getTopicInfo();
        if (infoEntity != null) {
//            etDesc.setText(infoEntity.topicDesc);
            topicHolder.showTopic(infoEntity.topicTitle);
        }
        etDesc.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int destLength = dest.length();
                if (destLength + source.length() > PublishActivity.EDITTEXT_DESC_MAX_LENGTH) {
                    ToastUtil.showToast(R.string.subject_add_error);
                    return source.subSequence(0, PublishActivity.EDITTEXT_DESC_MAX_LENGTH - destLength);
                }
                return source;
            }
        }});

        TaskManager.get().getAllIp();
        /**
         * 播放器点击事件
         */
        initPlayer();
    }

    private void initPlayer() {
        aVideoConfig mediaInfor = VideoUtils.getMediaInfor(RecordManager.get().getProductEntity().combineVideo);
        float v = mediaInfor.getVideoWidth() * 1.0f / mediaInfor.getVideoHeight();
        int screenWidth = ScreenUtil.getWidth(this);
        int height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, 154);
        int videoViewWidth;
        int videoViewHeight;
        if (v >= 1) {
            videoViewWidth = screenWidth;
            videoViewHeight = (int) (screenWidth / v);
        } else {
            videoViewHeight = height;
            videoViewWidth = (int) (height * v);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoViewWidth, videoViewHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        frameLayout.setLayoutParams(layoutParams);
        playerEngine = new PlayerEngine();
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        playerEngine.setPreviewAspectRatio(RecordManager.get().getProductEntity().getExceptRatio());
        combinePreview();
    }

    private void setPlayerListener() {
        KLog.i("setonclick--onclick-->" + playerEngine.isPlaying());
        playerEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KLog.i("onclick-->" + playerEngine.isPlaying());
                if (playerEngine.isPlaying()) {
                    isClickPause = true;
                    pauseVideo();
                } else {
                    isClickPlay = true;
                    playVideo((int) currentPosition);
                }
            }
        });
        //播放器进度
        playerEngine.setOnPlaybackListener(new PlayerListener() {
            @Override
            public void onPlayerPrepared() {

            }

            @Override
            public boolean onPlayerError(int var1, int var2) {
                isPlayError = true;
//                ivPlayerStatus.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public void onPlayerCompletion() {
                KLog.i("onPlayerCompletion--->" + isClickPause + playerEngine.getCurrentPosition());
//                if/* (!isClickPause) {
//                    playVideo(0);
//                }
//                isClic*/kPause = false;

            }

            @Override
            public void onGetCurrentPosition(float var1) {
                KLog.i("position-->" + var1 + "duration-->" + playerEngine.getDuration());
            }
        });
    }

    private boolean clickPublish;

    @Override
    protected void onSingleClick(View v) {

        DeviceUtils.hiddenKeyBoard(frameLayout);
        switch (v.getId()) {
            case R.id.act_view_parent:
                KLog.i("click---video" + playerEngine.isPlaying());
                if (playerEngine.isPlaying()) {
                    isClickPause = true;
                    pauseVideo();
                } else {
                    playVideo(currentPosition);
                }
                break;
            case R.id.tv_publish:
                if (!RecordManager.get().hasPublishingProduct() && !clickPublish) {
                    clickPublish = true;
                    if (GlobalParams.StaticVariable.sCurrentNetwork == 1 && !GlobalParams.StaticVariable.sHasShowedRemind) {
                        GlobalParams.StaticVariable.sHasShowedRemind = true;
                        new RemindDialog(this)
                                .setListener(new RemindDialog.RemindClickListener() {
                                    @Override
                                    public void onOkClick() {
                                        doPublish();
                                    }
                                })
                                .show();

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
                    etDesc.setText("");
                } else {
                    startActivityForResult(new Intent(this, SubjectSearchActivity.class), PublishActivity.REQUEST_CODE_ADD_TOPIC);
                }
                break;
            default:
        }
    }

    private void doPublish() {
        if (GlobalParams.StaticVariable.ispublishing) {//当前正在发布
            return;
        }
        saveInfo()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                        startActivity(new Intent(LocalPublishActivity.this, MessageActivity.class));
                        RecordUtil.moveToPublishing(RecordManager.get().getProductEntity());
                        GlobalParams.StaticVariable.ispublishing = true;
                        Intent intent = new Intent(LocalPublishActivity.this, PublishBGService.class);
                        intent.putExtra("ifsave", cbSaveLocal.isChecked());
                        intent.putExtra("publishType", PublishModel.TYPE_UPLOAD);
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
                        showToast(getString(R.string.publish_falied));
                    }
                });
    }

    private void saveDraft() {
        saveInfo()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        RecordUtil.moveToDraft(RecordManager.get().getProductEntity());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("======发布失败:" + throwable.getMessage());
                        showToast(getString(R.string.publish_falied));
                    }
                });
    }

    private void combinePreview() {

        playerEngine.reset();
        playerEngine.clearEffects();
        Scene scene = playerEngine.createScene();
        //路径问空  或者不存在，
        String path = (RecordManager.get().getProductEntity().combineVideoAudio == null || !new File(RecordManager.get().getProductEntity().combineVideoAudio).exists()) ? RecordManager.get().getProductEntity().combineVideo : RecordManager.get().getProductEntity().combineVideoAudio;

        MAsset asset = new MAsset(path);
        KLog.i("localpublis--->combineAudio" + path);
        asset.setVolume(1.0f);
        if (path == null || !new File(path).exists() || VideoUtils.getVideoLength(path) == 0) {//如果播放路径不存在
            ToastUtil.showToast("视频错误，请重试");
            finish();
            return;
        }
        scene.addMedia(asset);
        int[] targetSize = PlayerViewFactory.measureViewWHLocalPublish(this, RecordManager.get().getProductEntity().combineVideoAudio, 200);
        KLog.d("Trim-videoAct--11>>" + targetSize[0] + "width:>" + targetSize[1]);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(targetSize[0], targetSize[1]);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        TextureView textureView = new TextureView(this);
        frameLayout.removeAllViews();
        frameLayout.addView(textureView, params);

        playerEngine.build(textureView, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                setPlayerListener();
//                if (!isClickPause) {
                playerEngine.addScene(scene);
                playerEngine.setAutoRepeat(true);
                playVideo(0);
//                }
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
                            String coverPath = RecordManager.get().getProductEntity().baseDir
                                    + File.separator
                                    + RecordManager.PREFIX_COVER_FILE
                                    + RecordFileUtil.getTimestampString() + ".jpg";
                            KLog.i("=====缩略图路径：" + coverPath);
                            if (playerEngine.getSnapShot(500, coverPath)) {
                                RecordManager.get().getProductEntity().coverPath = coverPath;
                                KLog.i("=====生成缩略图成功");
                            } else {
                                KLog.e("=====生成缩略图失败");
                            }
                            RecordManager.get().getProductEntity().getTopicInfo().topicDesc = etDesc.getText().toString();
                            RecordManager.get().getProductExtend().allowTeam = cbAllow.isChecked();
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void onUploadOk(PublishResponseEntity entity) {
        KLog.i("======返回的分享数据为：" + entity);
        if (entity != null && entity.share_info != null) {
            ppwShare = PopupWindowUtils.showUploadResultPanel(this, frameLayout, new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    entity.share_info.objId = entity.opus_id;
                    entity.share_info.shareType = ShareEventEntity.TYPE_OPUS;
                    switch (v.getId()) {
                        case R.id.tvOk:
                            exitPublish(true);
                            break;
                        case R.id.llWeChat:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_WECHAT;
                            if (entity.share_info.wxprogram_share_info != null) {
                                wxMinAppShare(0, entity.share_info, null);
                            } else {
                                wechatShare(0, entity.share_info);
                            }
                            break;
                        case R.id.llCircle:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_FRIEND;
                            wechatShare(1, entity.share_info);
                            break;
                        case R.id.llWeibo:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_WEIBO;
                            weiboShare(entity.share_info);
                            break;
                        case R.id.llQQ:
                            entity.share_info.shareTarget = ShareEventEntity.TARGET_QQ;
                            qqShare(entity.share_info);
                            break;
                        default:
                            break;
                    }
                    if (null != ppwShare && ppwShare.isShowing()) {
                        ppwShare.dismiss();
                    }
                }
            });
        } else {
            KLog.i("======返回的分享数据为空：" + entity);
        }
    }

    @Override
    public void onPublishExit(boolean isSuccess) {
        exitPublish(isSuccess);
    }

    private void exitPublish(boolean isSuccess) {
        if (viewUploadMask != null) {
            viewUploadMask.dismiss();
        }
        RecordManager.get().clearAll();
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    EventHelper.post(GlobalParams.EventType.TYPE_SHOW_MAIN_FIRST, 5);
                }
                MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                startActivity(new Intent(LocalPublishActivity.this, MessageActivity.class));
            }
        }, 250);
    }

    private void playVideo(float position) {
        if (!isPlayError) {
//            if (position > 0) {
//                playerEngine.seekTo(currentPosition / 1000000f);
//            }
//            KLog.i(currentPosition+ "currentPosition==-->" + currentPosition / 1000000f);
            playerEngine.setAutoRepeat(true);
            playerEngine.start();
//            isClickPause = false;
            ivPlayerStatus.setVisibility(View.GONE);
        }
    }

    private void pauseVideo() {
        if (!playerEngine.isNull() && !isPlayError) {
            currentPosition = playerEngine.getCurrentPosition();
            if (playerEngine.isPlaying()) {
                playerEngine.setAutoRepeat(false);
                playerEngine.pause();
                KLog.i("onclick--pauseVideo" + currentPosition);
            }
        }
        ivPlayerStatus.setVisibility(View.VISIBLE);
    }

    private void releaseAnim() {
        if (playerEngine != null && !playerEngine.isNull()) {
            playerEngine.release();
        }
        playerEngine = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerEngine != null && !playerEngine.isNull()) {
            if (viewUploadMask.getVisibility() != View.VISIBLE) {
//                playVideo();
                combinePreview();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isClickPause = true;
        pauseVideo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShareEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_SHARE_EVENT) {
            if (eventEntity.data != null && eventEntity.data instanceof ShareEventEntity) {
                exitPublish(true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShareCancelEvent(EventEntity eventEntity) {
        if (eventEntity.code == GlobalParams.EventType.TYPE_SHARE_CANCEL_EVENT) {
            exitPublish(true);
        }
    }

    @Override
    protected void onDestroy() {
        EventHelper.unregister(this);
        topicHolder = null;
        isClickPause = false;
        if (dialog != null) {
            dialog.dismiss();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }
        releaseAnim();
        clickPublish = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (viewUploadMask != null) {
            if (viewUploadMask.getPublishStatus() == UploadMaskView.STATUS_NORMAL) {
                if (RecordManager.get().getProductEntity() != null && RecordManager.get().getProductEntity().hasVideo() && fromType == FORM_DRAFT) {
                    SysAlertDialog.createAlertDialog(this, "", getString(R.string.giveup_draft),
                            getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    RecordUtil.deleteProduct(RecordManager.get().getProductEntity(), true);
//                                    RecordManager.get().clearAll();
                                    dialog.dismiss();
//                                    finish();
                                }
                            }, getString(R.string.sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RecordUtil.moveToDraft(RecordManager.get().getProductEntity());
                                    RecordManager.get().clearAll();
                                    dialog.dismiss();
                                    finish();
                                }
                            }, false, null)
                            .show();
                } else {
                    normalBack();
                }
            } else if (viewUploadMask.getPublishStatus() == UploadMaskView.STATUS_PUBLISH_OK
                    || viewUploadMask.getPublishStatus() == UploadMaskView.STATUS_PUBLISH_FAIL) {
                exitPublish(viewUploadMask.getPublishStatus() == UploadMaskView.STATUS_PUBLISH_OK);
            } else {
                KLog.i("======正在发布，不能退出");
                showToast(R.string.stringUploadingDoNotExit);
            }
        } else {
            normalBack();
        }
    }

    private void normalBack() {
        if (fromType == FORM_SEARCH) {
            //如果不发布，需要删除文件夹
            if (RecordManager.get().getProductEntity() != null) {
                FileUtil.deleteAll(RecordManager.get().getProductEntity().baseDir, true);
            }
        }
        RecordManager.get().clearAll();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PublishActivity.REQUEST_CODE_ADD_TOPIC:
                if (resultCode == RESULT_OK) {
                    TopicInfo topicInfo = data.getParcelableExtra(TopicInfo.INTENT_EXTRA_KEY_NAME);
                    KLog.i("=====topicInfo:" + (topicInfo == null ? "null" : topicInfo.toString()));
                    if (topicInfo != null) {
                        topicHolder.showTopic(topicInfo.getTitle());
                        RecordManager.get().setTopicInfo(new TopicInfoEntity(topicInfo.getTopicId(), topicInfo.getTitle(), ""));
                    } else {
                        topicHolder.showTopic(null);
                        etDesc.setText("");
                        RecordManager.get().setTopicInfo(new TopicInfoEntity(0, topicInfo.getTitle(), ""));
                    }
                    RecordManager.get().updateProduct();
                }
                break;
            default:
                break;
        }
    }

}
