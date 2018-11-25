package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongci.sun.gpuimglibrary.gles.filter.FilterUtils;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.discovery.activity.SearchVideoActivity;
import com.wmlive.hhvideo.heihei.record.adapter.FilterAdapter;
import com.wmlive.hhvideo.heihei.record.engine.PlayerEngine;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.presenter.EditLocalPresenter;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.heihei.record.widget.CircleRecyclerView;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimVideoViewNew;
import com.wmlive.hhvideo.heihei.record.widget.MaskLayout;
import com.wmlive.hhvideo.heihei.record.widget.ScaleTextureView;
import com.wmlive.hhvideo.heihei.record.widget.ScaleXViewMode;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Author：create by admin on 2018/11/13 11:53
 * Email：haitian.jiang@welines.cn
 */
public class EditLocalMvActivity extends DcBaseActivity<EditLocalPresenter> implements EditLocalPresenter.IEditLocalView, CustomTrimVideoViewNew.OnRangeChangeListener,
        CustomTrimVideoViewNew.onVolumeChangeListener, ScaleTextureView.OnTextureViewChangeListener, CustomTrimVideoViewNew.TrimViewPresenter {

    private PlayerEngine playerEngine;
    @BindView(R.id.llController)
    LinearLayout llController;
    @BindView(R.id.imageRotate)
    ImageView imageRotate;
    @BindView(R.id.imagePlay)
    ImageView ivPlay;
    @BindView(R.id.tvRestore)
    TextView tvRestore;
    @BindView(R.id.rlPlayerContainer)
    FrameLayout rlPlayerContainer;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.mCustomTrimVideoView)
    CustomTrimVideoViewNew mCustomTrimVideoView;
    @BindView(R.id.filter_layout)
    RelativeLayout filter_layout;
    @BindView(R.id.rvFilter)
    CircleRecyclerView rvFilter;
    @BindView(R.id.maskLayout)
    MaskLayout maskLayout;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.ivBack)
    ImageView ivBack;



    private static final String VIDEO_INDEX = "videoIndex";

    private String videoPath;
    public int videoIndex;

    @Override
    public void onClick(View v) {
        if (v != tvRestore) {
            tvRestore.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        if (v == tvNext) {
            presenter.next();
        } else if (v == ivPlay) {
            if (playerEngine.isPlaying()) {
                presenter.pause(false);
            } else {
                presenter.play();
            }
        } else if (v == tvRestore) {
            presenter.restore();
            tvRestore.setTextColor(ContextCompat.getColor(this, R.color.hh_color_a_50));
        } else if (v == imageRotate) {
//            presenter.restore();
            presenter.rotate(false);
        }
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustomTrimVideoView.setRatio(ratio);
        if (!playerEngine.isPlaying()) {
            presenter.play();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_local_mv;
    }


    public static void startEditLocalMvActivity(Activity context, int requestCode, int shortIndex, String localPath, long minDuration) {
        KLog.i("beanPath-->" + localPath);
        Intent intent = new Intent(context, EditLocalMvActivity.class);
        intent.putExtra(VIDEO_INDEX, shortIndex);
        intent.putExtra(RecordMvActivity.PATH_FROM_LOCALALBUM, localPath);
        intent.putExtra(RecordMvActivity.LENGTH_FROM_LOCALALBUM, minDuration);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected EditLocalPresenter getPresenter() {
        return new EditLocalPresenter(this);
    }

    @Override
    protected void initData() {
        super.initData();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        initTitleBar();

        initView();
        Intent intent = getIntent();
//        if (intent != null) {
//            videoPath = intent.getStringExtra(RecordMvActivity.PATH_FROM_LOCALALBUM);
            videoIndex = intent.getIntExtra(VIDEO_INDEX, 0);
            long minDuration = (long) intent.getLongExtra(RecordMvActivity.LENGTH_FROM_LOCALALBUM, 2000);
//            videoPath = "/storage/emulated/0/DCIM/Video/V81124-193838.mp4";
            videoPath = "/sdcard/69.mp4";
            playerEngine = new PlayerEngine();
            int[] videoWH = RecordUtil.getVideoWH(videoPath);
            if (videoWH[1] == 0 || videoWH[0] == 0) {
                return;
            }
            ratio = (float) videoWH[0] / videoWH[1];
            presenter.init(playerEngine, videoPath, container, minDuration);
//        }

        initFilterView();

    }

    private void initFilterView() {
        FilterAdapter filterAdapter = new FilterAdapter(this);
        rvFilter.setNeedCenterForce(true);
        rvFilter.setAdapter(filterAdapter);
        rvFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFilter.setViewMode(new ScaleXViewMode());
        filterAdapter.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object obj) {
                rvFilter.smoothScrollDelView((View) obj);
                presenter.setFilter(FilterUtils.filterWithType(EditLocalMvActivity.this, FilterUtils.getAllFilterList().get(RecordSetting.FILTER_LIST.get(position).filterId)));
                presenter.replayOnChanged();
            }
        });

    }

    float ratio;

    private void initView() {
        ivPlay.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvRestore.setOnClickListener(this);
        imageRotate.setOnClickListener(this);
        mCustomTrimVideoView.setOnRangeChangeListener(this);
        mCustomTrimVideoView.setOnVolumeChangeListener(this);
        mCustomTrimVideoView.setTrimViewPresenter(this);
    }

//    private void initTitleBar() {
//        setTitle("", true);
//        setBlackToolbar();
//        tvNext = new TextView(this);
//        tvNext.setText("下一步");
//        tvNext.setTextSize(16);
//        TypedValue tv = new TypedValue();
//        tvNext.setBackgroundResource(tv.resourceId);
//        tvNext.setTextColor(getResources().getColor(R.color.hh_color_g));
//        tvNext.setGravity(Gravity.CENTER);
//        tvNext.setPadding(10, 6, DeviceUtils.dip2px(EditLocalMvActivity.this, 15), 6);
//        setToolbarRightView(tvNext, this);
//    }


    @Override
    public void onPlayStart() {
        ivPlay.setSelected(true);
    }

    @Override
    public void onPlayPause() {
        ivPlay.setSelected(false);
    }

    @Override
    public void onPlayCompletion() {
        ivPlay.setSelected(false);
    }

    @Override
    public void onExportSuccess(String audioPathForResult, String videoPathForResult) {
        Intent intent = new Intent();
        intent.putExtra(SearchVideoActivity.SHORT_VIDEO_PATH, videoPathForResult);
        intent.putExtra(SearchVideoActivity.SHORT_AUDIO_PATH, audioPathForResult);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onThumbnailUpdate(PlayerEngine engine, int duration, int totalDuration, int maxDuration, int startTime) {
        mCustomTrimVideoView.setPlayer(engine, duration, totalDuration, maxDuration, startTime, ratio);
    }

    @Override
    public void onInflate() {
        rlPlayerContainer.invalidate();
    }

    @Override
    public int[] getRect() {
        int[] size = maskLayout.getRectSize();
        return size;
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.pause(false);
    }


    @Override
    public void onValuesChanged(long minValue, long maxValue, long duration, int changeType) {
        Log.d(EditLocalPresenter.TAG, "onValuesChanged() called with: minValue = [" + minValue + "], maxValue = [" + maxValue + "], duration = [" + duration + "], changeType = [" + changeType + "]");
        presenter.setStartTime(minValue, maxValue, duration);
    }

    @Override
    public void onValuesChangeEnd() {
//        presenter.play();
        Log.d(EditLocalPresenter.TAG, "onValuesChangeEnd() called");
        presenter.replayOnChanged();
//        tvRestore.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void onValuesChangeStart() {
        Log.d(EditLocalPresenter.TAG, "onValuesChangeStart() called");
        presenter.pause(true);
    }

    @Override
    public void onValuesChanged(int volume) {
        presenter.setVolume(volume);
    }

    @Override
    public void OnTextureViewChange() {
        tvRestore.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void onFilterClick(ImageView imageFilter) {
        //点击显示滤镜
        if(filter_layout.getVisibility() == View.VISIBLE){
            imageFilter.setImageResource(R.drawable.icon_video_topbar_filter_dis);
            filter_layout.setVisibility(View.GONE);
            llController.setVisibility(View.VISIBLE);
        }else {
            imageFilter.setImageResource(R.drawable.icon_video_topbar_filter_nor);
            filter_layout.setVisibility(View.VISIBLE);
            llController.setVisibility(View.GONE);
        }
    }


}
