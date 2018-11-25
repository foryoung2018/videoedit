package com.wmlive.hhvideo.heihei.record.presenter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.dongci.sun.gpuimglibrary.common.CutEntity;
import com.dongci.sun.gpuimglibrary.gles.filter.GPUImageFilter;
import com.dongci.sun.gpuimglibrary.player.DCAsset;
import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.record.FilterInfoEntity;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.activity.EditLocalMvActivity;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.engine.VideoEngine;
import com.wmlive.hhvideo.heihei.record.engine.config.MVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.config.aVideoConfig;
import com.wmlive.hhvideo.heihei.record.engine.constant.SdkConstant;
import com.wmlive.hhvideo.heihei.record.engine.content.PlayerContentFactory;
import com.wmlive.hhvideo.heihei.record.engine.listener.ExportListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerCreateListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.PlayerListener;
import com.wmlive.hhvideo.heihei.record.engine.listener.VideoListener;
import com.wmlive.hhvideo.heihei.record.engine.model.MAsset;
import com.wmlive.hhvideo.heihei.record.engine.model.MScene;
import com.wmlive.hhvideo.heihei.record.engine.model.MediaObject;
import com.wmlive.hhvideo.heihei.record.engine.model.Scene;
import com.wmlive.hhvideo.heihei.record.engine.utils.VideoUtils;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtilSdk;
import com.wmlive.hhvideo.heihei.record.widget.CircleProgressDialog;
import com.wmlive.hhvideo.heihei.record.widget.ScaleTextureView;
import com.wmlive.hhvideo.heihei.record.widget.SysAlertDialog;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Author：create by admin on 2018/11/13 12:10
 * Email：haitian.jiang@welines.cn
 */
public class EditLocalPresenter extends BasePresenter<EditLocalPresenter.IEditLocalView> {

    public static final String TAG = EditLocalPresenter.class.getSimpleName();
    private static final int STEP_CUT = 1;
    private static final int STEP_TRIM_VIDEO = 2;
    private static final int STEP_SPLIT_VIDEO = 3;
    private static final int STEP_OVER = 4;
    private PlayerEngine playerEngine;
    private VideoEngine videoEngine;
    private Context context;
    List<MediaObject> mediaObjects;
    private String path;
    private IEditLocalView iEditLocalView;
    ScaleTextureView textureView;
    ViewGroup videoContainer;
    //模板时间
    private static final float CLIP_DRUATION = 15f;
    private int rotate = 0;
    private static final float RATIO_CONTAINER_VERTICAL = 300 / 400f;
    private static final float RATIO_CONTAINER_HORIZONTAL = 400 / 300f;

    private int[] videoWH;
    private int[] screenWH = new int[2];

    private List<MediaObject> list = new ArrayList<MediaObject>();//素材视频 asset
    private long minValue;
    private long maxValue;
    private long duration = 6000l;
    private CircleProgressDialog dialog;
    private float srcW, srcH;
    private float volume = 1.0f;
    private RectF rect;
    private String basepath;
    private String videoPathForResult;
    private String audioPathForResult;
    private GPUImageFilter filter;
    private ScaleTextureView.OnTextureViewChangeListener listener;


    public EditLocalPresenter(IEditLocalView view) {
        super(view);
//        listener = (ScaleTextureView.OnTextureViewChangeListener) view;
        context = (Context) view;
        this.iEditLocalView = view;
    }

    private boolean needResetPlayer = true;

    public void setPlayListener() {

        if (playerEngine != null) {
            playerEngine.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            playerEngine.setOnPlaybackListener(new PlayerListener() {
                @Override
                public void onPlayerPrepared() {
                    play();
                }

                @Override
                public boolean onPlayerError(int what, int extra) {
                    return false;
                }

                @Override
                public void onPlayerCompletion() {
                }

                @Override
                public void onGetCurrentPosition(float position) {
                    KLog.i("onGetCurrentPosition-edit" + position);
                }
            });

        }
    }

    public void play() {
        iEditLocalView.onPlayStart();
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }

        if (needResetPlayer) {
            playerEngine.seekTo(0);
        }
        playerEngine.start();
        playerEngine.setAutoRepeat(true);
    }

    public void pause(boolean needResetPlayer) {
        iEditLocalView.onPlayPause();
        this.needResetPlayer = needResetPlayer;
        if (playerEngine == null || playerEngine.isNull()) {
            return;
        }
        if (needResetPlayer) {
            playerEngine.seekTo(0);
        }
        playerEngine.pause();
        playerEngine.setAutoRepeat(false);
    }

    private void initPlayer(PlayerEngine player, String videoPath, ViewGroup videoContainer) {
        if (player == null)
            return;
        playerEngine = player;
        textureView = new ScaleTextureView(context);
        textureView.setOnTextureViewChangeListener(((ScaleTextureView.OnTextureViewChangeListener)context));
        this.videoContainer = videoContainer;
        textureView.setWrapper(videoContainer);
        path = videoPath;
        KLog.i(TAG, "initPlayer() called with: player = [" + player + "], videoPath = [" + videoPath + "], videoContainer = [" + videoContainer + "]");
        loadScence();
        videoWH = RecordUtil.getVideoWH(path);
        screenWH[0] = context.getResources().getDisplayMetrics().widthPixels;
        screenWH[1] = context.getResources().getDisplayMetrics().heightPixels;
        if (videoWH[1] == 0 || videoWH[0] == 0) {
            return;
        }
        srcW = videoWH[0];
        srcH = videoWH[1];
        float ratio = (float) videoWH[0] / videoWH[1];
        playerEngine.reset();
        playerEngine.setPreviewAspectRatio(ratio);
        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);

        vto2 = videoContainer.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(layoutListener);
    }




    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            containerWidth = iEditLocalView.getRect()[0];
            containerHeight = iEditLocalView.getRect()[1];
            adjustWH();
            ready();
        }
    };

    private int containerWidth, containerHeight;

    boolean isRecyclerViewInit;
    ViewTreeObserver vto2;
    private int listSize;

    private float minWidth,minheight,rectWidth,rectHeight,tvWidth,tvHeight;

    private void adjustWH() {
        if (videoContainer.getViewTreeObserver().isAlive()) {
            videoContainer.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        }
        _adjust();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (tvWidth), (int) (tvHeight));
        params.gravity = Gravity.CENTER;
        textureView.setLayoutParams(params);
        if (videoContainer.getChildCount() < 1) {
            videoContainer.addView(textureView);
        }

        textureView.setRotation(-rotate * 90 % 360);
        textureView.setTranslateLimit(rectWidth, rectHeight);
        textureView.reset();
    }




    public void init(PlayerEngine playerEngine, String videoPath, ViewGroup videoContainer, long minDuration) {
        this.duration = minDuration;
        initPlayer(playerEngine, videoPath, videoContainer);
        initThumbnail();
    }

    private void initThumbnail() {
        if (TextUtils.isEmpty(path))
            return;
        long total = VideoUtils.getVideoLength(path) / 1000000;

        PlayerEngine engine = new PlayerEngine();
        engine.setSnapShotResource(path);
        iEditLocalView.onThumbnailUpdate(engine, (int) duration, (int) total * 1000, (int) duration, 0);
    }

    public void restore() {
//        initThumbnail();
        rotate(true);
        textureView.log();
//        minValue = 0;
//        replayOnChanged();
//        replay();
    }

    public void replayOnChanged() {
        mediaObjects.clear();
        playerEngine.reset();
        playerEngine = null;
        textureView = null;
        playerEngine = new PlayerEngine();
        textureView = new ScaleTextureView(context);
        videoContainer.removeAllViews();
        textureView.setWrapper(videoContainer);
        textureView.setOnTextureViewChangeListener(((ScaleTextureView.OnTextureViewChangeListener)context));
        _adjust();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (tvWidth), (int) (tvHeight));
        params.gravity = Gravity.CENTER;
        textureView.setLayoutParams(params);
        videoContainer.addView(textureView);

        textureView.setRotation(-rotate * 90 % 360);
        textureView.setTranslateLimit(rectWidth, rectHeight);
        textureView.reset();

        MediaObject mediaObject = new MAsset(path);
//        MediaObject mediaObject = PlayerContentFactory.createMediaFromTrim(path);
        mediaObjects.add(mediaObject);
        mediaObject.setTimeRange((long) minValue * 1000, duration * 1000);
        mediaObject.setVolume(volume);
        KLog.d(TAG, "replayOnChanged: filter==" + filter);
        if (filter != null) {
            mediaObject.setGpuImageFilter(filter);
        }
//        playerEngine.reset();
//        playerEngine.setAspectRatioFitMode(MediaObject.FillTypeScaleToFit);
        ready();
        iEditLocalView.onInflate();
//        playerEngine.start();
    }



    private void _adjust() {

        if(videoWH[1]==0||videoWH[0]==0){
            return;
        }
        float ratio = (float) videoWH[0]/videoWH[1];
        float useRatio = RATIO_CONTAINER_VERTICAL;
//        rectWidth  = context.getResources().getDimensionPixelSize(R.dimen.t300dp);
//        rectHeight  = context.getResources().getDimensionPixelSize(R.dimen.t400dp);
        rectWidth = containerWidth;
        rectHeight = containerHeight;

        if(rotate%2!=0){
            useRatio = RATIO_CONTAINER_HORIZONTAL;
//            rectWidth  = context.getResources().getDimensionPixelSize(R.dimen.t400dp);
//            rectHeight  = context.getResources().getDimensionPixelSize(R.dimen.t300dp);
            rectWidth = containerHeight;
            rectHeight = containerWidth;
        }

        //如果视频太宽
        if(ratio>useRatio){
            tvHeight = rectHeight;
            tvWidth = rectHeight*ratio;
        }
        else if(ratio<useRatio){
            tvWidth = rectWidth;
            tvHeight = tvWidth/ratio;
        }else {
            tvWidth = rectWidth;
            tvHeight = rectHeight;
        }

    }
    private List<MediaObject> loadScence() {
        mediaObjects = new ArrayList<>();
//        MediaObject mediaObject = new MAsset(path);
        MediaObject mediaObject = PlayerContentFactory.createMediaFromTrim(path);
        mediaObject.setTimeRange((long) 0, duration * 1000);
        playerEngine.setVolume(0, 100);
        mediaObjects.add(mediaObject);
        int width = mediaObjects.get(0).getWidth();
        int height = mediaObjects.get(0).getHeight();
        KLog.i(TAG, "replayOnChanged() called" + "width " + width + " height " + height);
        return mediaObjects;
    }

    public void rotate(boolean restore) {
        if (restore && rotate % 2 == 0) {

        } else {
//            int temp = videoWH[0];
//            videoWH[0] = videoWH[1];
//            videoWH[1] = temp;
        }
        rotate++;
        if (restore) {
            rotate = 0;
            adjustWH();
//            replayOnChanged();
        } else {
            adjustWH();
        }
        ready();
    }

    private void ready() {
        playerEngine.build(textureView, (int) tvWidth, (int) tvHeight, 24, true, new PlayerCreateListener() {
            @Override
            public void playCreated() {
                setPlayListener();
                if (playerEngine != null) {
                    playerEngine.setMediaAndPrepare(mediaObjects);
                    playerEngine.setAutoRepeat(true);
                }
            }
        });
    }

    public void close() {
        playerEngine.release();
    }

    /**
     * 1.裁剪视频
     * 2.分离视频音频
     * 3.导出视频，
     * 4.设置音频音量
     */
    public void export() {
        pause(true);
        playerEngine.release();
        videoEngine = new VideoEngine();
        basepath = Environment.getExternalStorageDirectory().toString();
//        basepath = RecordManager.get().getProductEntity().shortVideoList.get(((EditLocalMvActivity) context).videoIndex).baseDir;
        cutRawFileRange();
    }

    public void next() {
        aVideoConfig config = VideoUtils.getMediaInfor(path);
        if (config.rotation != 0) {
            swapWH();
        }
        KLog.i(TAG, "scaleX : " + textureView.getScaleX() + " " + textureView.getTranslationX());
        KLog.i(TAG, "scaleY : " + textureView.getScaleY() + " " + textureView.getTranslationY());
        float left = (tvWidth * textureView.getScaleX() - rectWidth) / 2 - textureView.getTranslationX();
        float top = (tvHeight * textureView.getScaleY() - rectHeight) / 2 - textureView.getTranslationY();


        if (rotate % 4 == 1) {
            left = (tvWidth * textureView.getScaleY() - rectWidth) / 2 + textureView.getTranslationY();
            top = (tvHeight * textureView.getScaleY() - rectHeight) / 2 - textureView.getTranslationX();
        } else if (rotate % 4 == 2) {
            left = (tvWidth * textureView.getScaleX() - rectWidth) / 2 + textureView.getTranslationX();
            top = (tvHeight * textureView.getScaleY() - rectHeight) / 2 + textureView.getTranslationY();
        } else if (rotate % 4 == 3) {
            left = (tvWidth * textureView.getScaleX() - rectWidth) / 2 - textureView.getTranslationY();
            top = (tvHeight * textureView.getScaleY() - rectHeight) / 2 + textureView.getTranslationX();
        } else {

        }
        float right = left + rectWidth;
        float bottom = top + rectHeight;

        left = left / tvWidth * videoWH[0] / textureView.getScaleX();
        top = top / tvHeight * videoWH[1] / textureView.getScaleY();
        right = right / tvWidth * videoWH[0] / textureView.getScaleX();
        bottom = bottom / tvHeight * videoWH[1] / textureView.getScaleY();


//        if(config.rotation!=0){
//            left = left + top;
//            top = left - top;
//            left = left -top;
//
//            right = right + bottom;
//            bottom = right - bottom;
//            right = right -bottom;
//        }
        RectF rectF = new RectF(left, top, right, bottom);
        Log.d(TAG, "next() called" + rectF);
//        rectF = new RectF(0, 0, 1920, 1080);
//        Log.d(TAG, "next() called" + rectF);
        this.rect = rectF;
        if (config.rotation != 0) {
            swapWH();
        }
        export();

    }

    private void swapWH() {
        videoWH[0] = videoWH[0] + videoWH[1];
        videoWH[1] = videoWH[0] - videoWH[1];
        videoWH[0] = videoWH[0] - videoWH[1];
    }

    private void exportFinal(String videoPath) {
//        MAsset mediaObject = (MAsset)PlayerContentFactory.createMediaFromTrim(videoPath);
        MAsset mediaObject = new MAsset();
        mediaObject.setSourceType(DCAsset.DCAssetTypeVideo);
        mediaObject.setFillType(DCAsset.DCAssetFillTypeScaleToFit);
        mediaObject.setCropRect(rect);//
        mediaObject.setRectInVideo(new RectF(0, 0, 1f, 1f));
        mediaObject.setTimeRange(new DCAsset.TimeRange(minValue * 1000, (long) (duration * 1000.0)));
        mediaObject.setStartTimeInScene(0L);
        mediaObject.setFilePath(videoPath);
//        mediaObject.setVolume(volume);
        mediaObject.setRotate(-rotate * 90 % 360);
        list.add(mediaObject);
        MVideoConfig mVideoConfig = new MVideoConfig();
        String exportPath = createVideoFile(basepath);
        mVideoConfig.setVideoPath(exportPath);
        mVideoConfig.setVideoSize(RecordSetting.PRODUCT_WIDTH, RecordSetting.PRODUCT_HEIGHT);
        mVideoConfig.setDefaultAudioInfo();
//        mVideoConfig.setRotate(-rotate*90);
        Scene scene = new MScene();
        scene.assets = list;
        List<Scene> scenes = new ArrayList<Scene>(1);
        scenes.add(scene);
        RecordUtilSdk._exportMvVideo(scenes, mVideoConfig, null, new ExportListener() {
            @Override
            public void onExportStart() {

            }

            @Override
            public void onExporting(int progress, int max) {
            }

            @Override
            public void onExportEnd(int var1, String path) {
                KLog.i(TAG + "onExportEnd() called with: var1 = [" + var1 + "], path = [" + path + "]");
                if (var1 >= SdkConstant.RESULT_SUCCESS) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    videoPathForResult = path;
                    Message msg = Message.obtain();
                    msg.obj = path;
                    msg.what = STEP_SPLIT_VIDEO;//去设置音量
                    mHandler.sendMessage(msg);
                } else {
                    new File(path).delete();
                    if (dialog != null) {
                        dialog.setMessage(context.getString(R.string.generate_video_fail));
                        dialog.setCancelable(true);
                    }
                }
            }
        });
    }

    /**
     * 给当前修改音量的audio设置音量
     */
    private void setVolume(String path) {
        audioPathForResult = createAudioFile(basepath);
        if (RecordManager.get().getProductEntity() == null)
            return;
        List<CutEntity> list = new ArrayList<CutEntity>();
        CutEntity cutEntity = new CutEntity();
        cutEntity.audioPath = path;
        cutEntity.volume = volume;
        cutEntity.cutAudioPath = audioPathForResult;
        list.add(cutEntity);
        new VideoEngine().setVolume(list, new VideoListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(TAG, "onFinish() called with: code = [" + code + "], outpath = [" + outpath + "]");
                if (code >= SdkConstant.RESULT_SUCCESS) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    mHandler.sendEmptyMessage(STEP_OVER);
                } else {
                    new File(path).delete();
                    if (dialog != null) {
                        dialog.setMessage(context.getString(R.string.generate_video_fail));
                        dialog.setCancelable(true);
                    }
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    public static String createTimestampFile(String dirPath, String prefix, String extension) {
        if (!TextUtils.isEmpty(dirPath)) {
            String filePath = dirPath
                    + File.separator
                    + prefix
                    + RecordFileUtil.getTimestampString()
                    + extension;
            return filePath;
        }
        return null;
    }

    private String createAudioFile(String basepath) {
        return createTimestampFile(basepath, RecordManager.PREFIX_SPLIT_FILE, RecordManager.SUFFIX_AUDIO_FILE);
    }

    private String createVideoFile(String basepath) {
        return createTimestampFile(basepath, RecordManager.PREFIX_SPLIT_FILE, RecordManager.SUFFIX_VIDEO_FILE);
    }

    private void splitVideoAudio(String path) {

        String videoOut = createVideoFile(basepath);
        String audioOut = createAudioFile(basepath);

        List<CutEntity> list = new ArrayList<CutEntity>(1);
        CutEntity cutEntity = new CutEntity();
        cutEntity.path = path;
        cutEntity.cutAudioPath = audioOut;
        cutEntity.cutPath = videoOut;
        list.add(cutEntity);

        new VideoEngine().splitVideoAudioSingle(list, new VideoListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(TAG + "onFinish() called with: code = [" + code + basepath + "], videoOut = [" + "" + videoOut + "], audioOut = [" + audioOut + "]");
                if (code == SdkConstant.RESULT_SUCCESS) {
                    videoPathForResult = videoOut;
                    audioPathForResult = audioOut;
                    //导出视频，
                    Message msg = new Message();
                    msg.what = STEP_CUT;
                    msg.obj = videoOut;
                    mHandler.sendMessage(msg);
                    //执行音频设置音量
                } else {
                    new File(path).delete();
                    if (dialog != null) {
                        dialog.setMessage(context.getString(R.string.generate_video_fail));
                        dialog.setCancelable(true);
                    }
                }
            }

            @Override
            public void onError() {
                if (dialog != null) {
                    dialog.setMessage(context.getString(R.string.generate_video_fail));
                    dialog.setCancelable(true);
                }
            }
        });
    }

    private void cutRawFileRange() {
        KLog.i(TAG + "cut-raw-file: code = [" + path + "]");
        videoEngine.cutVideo(path, minValue * 1000, duration * 1000, new VideoListener() {
            @Override
            public void onStart() {
                if (dialog == null) {
                    dialog = SysAlertDialog.showCircleProgressDialog(context, context.getString(R.string.join_media), true, false);
                    dialog.show();
                }
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish(int code, String outpath) {
                KLog.i(TAG + "onFinish() called with: code = [" + code + "], outpath = [" + outpath + "]");
                if (code >= SdkConstant.RESULT_SUCCESS) {
                    Message msg = new Message();
                    msg.what = STEP_TRIM_VIDEO;//去分离
                    msg.obj = outpath;
                    mHandler.sendMessage(msg);
                } else {//剪裁失败
                    new File(outpath).delete();
                    if (dialog != null) {
                        dialog.setMessage(context.getString(R.string.generate_video_fail));
                        dialog.setCancelable(true);
                    }
                }
            }

            @Override
            public void onError() {
                KLog.d("clipVideo---onError>");
                if (dialog != null) {
                    dialog.setMessage(context.getString(R.string.generate_video_fail));
                    dialog.setCancelable(true);
                }
            }
        });
    }

    /**
     * 1.裁剪视频
     * 2.分离视频音频
     * 3.导出视频，
     * 4.设置音频音量
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            KLog.i(TAG + "handleMessage() called with: msg = [" + msg.what + "  " + msg.obj + "]");
            switch (msg.what) {
                case STEP_CUT:
                    String path = (String) msg.obj;
                    if (!TextUtils.isEmpty(path)) {
                        exportFinal(path);
                    }
                    break;
                case STEP_TRIM_VIDEO:
                    String pathToSplit = (String) msg.obj;
                    if (!TextUtils.isEmpty(pathToSplit)) {
                        splitVideoAudio(pathToSplit);
                    }
                    break;
                case STEP_SPLIT_VIDEO://给音频设置音量
                    setVolume(audioPathForResult);
                    break;
                case STEP_OVER:
                    if (!TextUtils.isEmpty(audioPathForResult) && !TextUtils.isEmpty(videoPathForResult)) {
//                        iEditLocalView.onExportSuccess(audioPathForResult, videoPathForResult);
                        Log.d(TAG, "handleMessage() called with: audioPathForResult = [" + audioPathForResult + "]" + " videoPathForResult = [" + videoPathForResult + "]");
                    }
                    break;
            }
        }
    };


    public void setStartTime(long minValue, long maxValue, long duration) {
        KLog.i(TAG + "setStartTime() called with: minValue = [" + minValue + "], maxValue = [" + maxValue + "], duration = [" + duration + "]");
        this.minValue = minValue;
    }

    public void setVolume(int volume) {
        this.volume = (float) (volume + .0f) / 100;
        playerEngine.setVolume(0, (float) (volume + .0f) / 100);
    }

    public void setFilter(GPUImageFilter filter) {
        this.filter = filter;

    }

    public interface IEditLocalView extends BaseView {

        void onPlayStart();

        void onPlayPause();

        void onPlayCompletion();

        void onExportSuccess(String audioPathForResult, String videoPathForResult);

        /**
         * @param engine
         * @param duration      目前截取的时长
         * @param totalDuration 视频总时长
         * @param maxDuration   多个视频最大时长
         * @param startTime     开始时间
         */
        void onThumbnailUpdate(PlayerEngine engine, int duration, int totalDuration, int maxDuration, int startTime);

        void onInflate();

        int[] getRect();
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
