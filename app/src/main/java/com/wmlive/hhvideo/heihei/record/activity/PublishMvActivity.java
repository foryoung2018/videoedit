package com.wmlive.hhvideo.heihei.record.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.MyAppActivityManager;
import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.heihei.beans.record.MusicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.beans.record.TopicInfoEntity;
import com.wmlive.hhvideo.heihei.beans.subject.TopicInfo;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.mainhome.activity.MessageActivity;
import com.wmlive.hhvideo.heihei.mainhome.util.PublishUtils;
import com.wmlive.hhvideo.heihei.quickcreative.ChooseStyle4QuickActivity;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.PlayerConfig;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.service.PublishMvBGService;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.heihei.record.widget.TextureVideoViewOutlineProvider;
import com.wmlive.hhvideo.heihei.record.widget.TopicInfoHolder;
import com.wmlive.hhvideo.heihei.subject.SubjectSearchActivity;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.RemindDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.VIDEO_EXPORT_HEIGHT;
import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.VIDEO_EXPORT_WIDTH;

/**
 * 发布作品的界面
 */
public class PublishMvActivity extends DcBaseActivity {
    public static final int REQUEST_NEED_RELOAD = 100;
    public static final String KEY_FROM_RECORD = "key_from_record";
    public static final int REQUEST_CODE_ADD_TOPIC = 120;
    public static final int EDITTEXT_DESC_MAX_LENGTH = 20;
    private static final String TAG = PublishMvActivity.class.getSimpleName();

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
    @BindView(R.id.framevideo)
    RelativeLayout frameVideos;
    PlayerEngine playerEngine;

    private TopicInfoHolder topicHolder;
    private float lastPosition = -1f;
    private boolean isPlay = true;
    private boolean fromRecord = true;
    private HandlerThread handlerThread;
    private Handler handler;
    public static final int MSG_PUBLISH = 100;
    public static final int MSG_SAVE = 200;
    public static final int MSG_DISMISS = 300;
    private CircleProgressDialog dialog;
    private String worksName;
    private List<MediaObject> assets;

    public static void startPublishActivity(Context ctx, boolean fromRecord) {
        if (PublishUtils.showToast()) {
            return;
        }
        Intent intent = new Intent(ctx, PublishMvActivity.class);
        intent.putExtra(KEY_FROM_RECORD, fromRecord);
        ctx.startActivity(intent);
    }

    public static void startPublishActivity(Context ctx, List<MediaObject> list, boolean b) {
        if (PublishUtils.showToast()) {
            return;
        }

        Intent intent = new Intent(ctx, PublishMvActivity.class);
        intent.putParcelableArrayListExtra("asset", (ArrayList<? extends Parcelable>) list);
        intent.putExtra(KEY_FROM_RECORD, b);
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
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fromRecord = getIntent().getBooleanExtra(KEY_FROM_RECORD, true);
        assets = getIntent().getParcelableArrayListExtra("asset");
        Log.d("assets", "initData: assets==" + assets);
        initView();
        initHandler();
        initPlayer();
        setListener();
        initOriginalMix();
        TaskManager.get().getAllIp();
        initLayoutParams();

    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRelativeMode(true);
        setTitle("", true);
        setBlackToolbar();
        TextView tvSave = new TextView(this);
        tvSave.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        tvSave.setText(getApplicationContext().getResources().getString(R.string.stringSaveToDraft));
        tvSave.setTextSize(14);
        TypedValue tv = new TypedValue();
        tvSave.setBackgroundResource(tv.resourceId);
        tvSave.setGravity(Gravity.CENTER);
        tvSave.setPadding(10, 6, DeviceUtils.dip2px(this, 15), 6);
        topicHolder = new TopicInfoHolder(llAddTopic, tvTopicLabel, ivDeleteTopic, cbSaveLocal, cbAllow);
        topicHolder.showLoacalInfo(true);
        cbAllow.setChecked(RecordManager.get().getProductExtend().allowTeam);
        setToolbarRightView(tvSave, new MyClickListener() {
            @Override
            protected void onMyClick(View v) {
                saveDraft();
            }
        });
        TopicInfoEntity topicInfo = RecordManager.get().getProductEntity().topicInfo;
        if (topicInfo != null) {
            topicHolder.showTopic(topicInfo.topicTitle);
            KLog.d("topic", topicInfo);
        }

    }

    private void initHandler() {
        handlerThread = new HandlerThread("SaveHandler");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SAVE:
                        if (dialog == null) {
                            dialog = SysAlertDialog.showCircleProgressDialog(PublishMvActivity.this, getApplicationContext().getResources().getString(msg.what == MSG_PUBLISH ?
                                    R.string.stringStartPublish : R.string.stringStartSave), true, false);
                        }
                        break;
                    case MSG_DISMISS:
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        if (msg.arg1 > 0) {
                            MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }


    /**
     * 设置播放器宽高以及位置
     */
    private void initLayoutParams() {
        int height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, 180);
        int width = (int) (height * 9.0f / 16);
        KLog.d(TAG, "initLayoutParams: width===" + width + "  height==" + height);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) frameVideos.getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        frameVideos.setLayoutParams(layoutParams);
    }


    private void initPlayer() {
        if (playerEngine == null)
            playerEngine = new PlayerEngine();
        else
            playerEngine.reset();

        List<MediaObject> list = new ArrayList<>();
        String path = RecordManager.get().getProductEntity().combineAudio;
        list.addAll(assets);
        if (!TextUtils.isEmpty(path)) {
            MediaObject mediaObject = new MAsset(path);
            KLog.i("播放item音频 的地址：" + path);
            mediaObject.setSourceType(MediaObject.MediaObjectTypeAudio);
            mediaObject.assetId = 9;
            mediaObject.setTimeRange(0, (long) VideoUtils.getAudioLength(RecordManager.get().getProductEntity().combineAudio));
            //获取视频的时长
            mediaObject.setVolume(1.0f);
            list.add(mediaObject);
        }
        TextureView textureView = new TextureView(this);
        textureView.setOutlineProvider(new TextureVideoViewOutlineProvider(30));
        textureView.setClipToOutline(true);
        frameVideos.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        frameVideos.addView(textureView, params);
        KLog.i("playVideo-->pre");//VIDEO_EXPORT_WIDTH, VIDEO_EXPORT_HEIGHT

        playerEngine.build(textureView, VIDEO_EXPORT_WIDTH, VIDEO_EXPORT_HEIGHT, PlayerConfig.fps, false, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                playerEngine.setBackgroundColor(Color.parseColor(RecordManager.get().getProductEntity().extendInfo.bgColor));
                KLog.i("playVideo-->end");
                playerEngine.setMediaAndPrepare(list);
                playerEngine.setAutoRepeat(true);
                playerEngine.start();
                ivPlayerStatus.setVisibility(View.GONE);
                playerEngine.setOnPlaybackListener(new PlayerListener() {
                    @Override
                    public void onPlayerPrepared() {

                    }

                    @Override
                    public boolean onPlayerError(int var1, int var2) {
                        return false;
                    }

                    @Override
                    public void onPlayerCompletion() {
                    }

                    @Override
                    public void onGetCurrentPosition(float var1) {

                    }
                });
            }
        });
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

    @Override
    protected void onSingleClick(View v) {
        DeviceUtils.hiddenKeyBoard(videoViewsdk);
        switch (v.getId()) {
            case R.id.framevideo:
                handlePlayVideoEvent();
                break;
            case R.id.tv_publish:
                handlePublishEvent();
                break;
            case R.id.llSaveDraft:
                saveDraft();
                break;
            case R.id.llAddTopic:
                handleTopicSelectEvent();
                break;
            default:
        }
    }

    /**
     * 点击播放处理
     */
    private void handlePlayVideoEvent() {
        if (playerEngine.isPlaying()) {
            isPlay = false;
            pauseVideo();
        } else {
            playVideo();
        }
    }

    long startTemp;
    /**
     * 点击发布处理
     */
    private void handlePublishEvent() {
        startTemp = System.currentTimeMillis();
        KLog.i("Time--=>" + startTemp);
        if (!RecordManager.get().hasPublishingProduct()) {
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
    }

    /**
     * 处理话题选择
     */
    private void handleTopicSelectEvent() {
        if (topicHolder.hasTopic()) {
            topicHolder.showAdd();
        } else {
            startActivityForResult(new Intent(PublishMvActivity.this, SubjectSearchActivity.class), REQUEST_CODE_ADD_TOPIC);
        }
    }

    private void doPublish() {
        KLog.i("Time--=1>" + (System.currentTimeMillis() - startTemp));
        if (GlobalParams.StaticVariable.ispublishing) {
            return;
        }

        RecordManager.get().getProductEntity().topicInfo.topicDesc = etDesc.getText().toString();
        RecordManager.get().getProductExtend().allowTeam = cbAllow.isChecked();
        GlobalParams.StaticVariable.ispublishing = true;
        updateDb();
        KLog.i("Time--=10>" + (System.currentTimeMillis() - startTemp));
//        MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);
        KLog.i("Time--=11>" + (System.currentTimeMillis() - startTemp));
        startActivity(new Intent(PublishMvActivity.this, MessageActivity.class));
        KLog.i("Time--=12" + (System.currentTimeMillis() - startTemp));
        Intent intent = new Intent(PublishMvActivity.this, PublishMvBGService.class);
        intent.putExtra("ifsave", cbSaveLocal.isChecked());
        intent.putParcelableArrayListExtra("asset", (ArrayList<? extends Parcelable>) assets);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        KLog.i("Time--=2>" + (System.currentTimeMillis() - startTemp));
    }
    //线程更新数据库
    private void updateDb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyAppActivityManager.getInstance().finishAllActivityExceptTwo(MainActivity.class,MessageActivity.class);
                RecordUtil.moveToPublishing(RecordManager.get().getProductEntity());
            }
        }).start();
    }

    private void saveDraft() {
        if (RecordUtil.moveToDraft(RecordManager.get().getProductEntity())) {
            showToast("已保存到草稿");
        }
        if (!TextUtils.isEmpty(etDesc.getText())) {
            RecordManager.get().getProductEntity().topicInfo.topicDesc = etDesc.getText().toString();
        }
        RecordManager.get().getProductExtend().allowTeam = cbAllow.isChecked();
        RecordManager.get().clearAll();
        MyAppActivityManager.getInstance().finishAllActivityExceptOne(MainActivity.class);

//        handler.sendEmptyMessage(MSG_SAVE);
//        saveInfo()
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        if (RecordUtil.moveToDraft(RecordManager.get().getProductEntity())) {
//                            showToast("已保存到草稿");
//                        }
//                        RecordManager.get().clearAll();
//                        Message message = Message.obtain();
//                        message.what = MSG_DISMISS;
//                        message.arg1 = 1;
//                        handler.sendMessage(message);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        KLog.i("======发布失败:" + throwable.getMessage());
//                        showToast("保存出错");
//                        Message message = Message.obtain();
//                        message.what = MSG_DISMISS;
//                        message.arg1 = 1;
//                        handler.sendMessage(message);
//                    }
//                });

    }

//    private Observable<Boolean> saveInfo() {
//        return Observable.just(1)
//                .subscribeOn(Schedulers.computation())
//                .map(new Function<Integer, Boolean>() {
//                    @Override
//                    public Boolean apply(Integer integer) throws Exception {
//                        if (RecordManager.get().getProductEntity() != null) {
//                            String coverPath = RecordManager.get().getProductEntity().baseDir + File.separator + RecordManager.PREFIX_COVER_FILE + RecordFileUtil.getTimestampString() + ".jpg";
//                            KLog.i("=====缩略图路径：" + coverPath);
//                            //生成缩略图， 整个播放器的缩略图
//                            if (playerEngine.getSnapShot(500, coverPath)) {
//                                RecordManager.get().getProductEntity().coverPath = coverPath;
//                                KLog.i("=====生成缩略图成功");
//                            } else {
//                                KLog.i("=====生成缩略图失败");
//                            }
//                            if (!TextUtils.isEmpty(etDesc.getText())) {
//                                RecordManager.get().getProductEntity().topicInfo.topicDesc = etDesc.getText().toString();
//                            }
//                            RecordManager.get().getProductExtend().allowTeam = cbAllow.isChecked();
//                        }
//                        return true;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread());
//    }


    private void playVideo() {
        isPlay = true;
        if (playerEngine.isNull()) {
            initPlayer();
        } else {
            playerEngine.setAutoRepeat(true);
            playerEngine.start();
        }
        ivPlayerStatus.setVisibility(View.GONE);
    }

    private void pauseVideo() {
        playerEngine.setAutoRepeat(false);
        playerEngine.pause();
        lastPosition = playerEngine.getCurrentPosition();
        ivPlayerStatus.setVisibility(View.VISIBLE);
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
                        RecordManager.get().setTopicInfo(new TopicInfoEntity(0, topicInfo.getTitle(), ""));
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
            default:
                break;
        }
    }


    private void releaseAnim() {
        if (playerEngine != null && playerEngine.isNull()) {
            playerEngine.release();
            playerEngine.destroy();
        }
        playerEngine = null;
    }

    private int getPreviewHeight() {
        return frameVideos.getHeight();
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
        }
        releaseAnim();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (GlobalParams.StaticVariable.ispublishing)
            return;
        playerEngine.reset();
        startActivity(new Intent(this, ChooseStyle4QuickActivity.class));
        finish();
    }


}
