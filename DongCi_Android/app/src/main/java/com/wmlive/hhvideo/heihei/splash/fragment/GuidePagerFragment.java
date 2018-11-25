package com.wmlive.hhvideo.heihei.splash.fragment;


import android.media.MediaPlayer;
import android.view.View;
import com.wmlive.hhvideo.common.base.BaseFragment;
import com.wmlive.hhvideo.heihei.mainhome.activity.MainActivity;
import com.wmlive.hhvideo.heihei.splash.SplashActivity;
import com.wmlive.hhvideo.heihei.splash.view.FullScreenVideoView;
import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class GuidePagerFragment extends BaseFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{
    @BindView(R.id.videoview_guide)
    FullScreenVideoView videoviewGuide;
    private int curPage;
    private boolean mHasPaused;

    @Override
    protected void initData() {
        super.initData();
        lazyLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void lazyLoad() {
        if (getArguments() == null) {
            return;
        }
        videoviewGuide = (FullScreenVideoView) findViewById(R.id.videoview_guide);
        int videoRes = getArguments().getInt("res");
        curPage = getArguments().getInt("page");
        videoviewGuide.setOnPreparedListener(this);
        videoviewGuide.setVideoPath("android.resource://" + getActivity().getPackageName() + "/" + videoRes);

    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.fragment_guide_pager;
    }

    @Override
    protected void onSingleClick(View v) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (videoviewGuide != null) {
            videoviewGuide.requestFocus();
            videoviewGuide.seekTo(0);
            videoviewGuide.start();
            videoviewGuide.setOnCompletionListener(this);
        }
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasPaused) {
            if (videoviewGuide != null) {
                videoviewGuide.seekTo(curPage);
                videoviewGuide.resume();
            }
        }
        return;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoviewGuide != null) {
            curPage = videoviewGuide.getCurrentPosition();
        }
        mHasPaused = true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (videoviewGuide != null) {
            videoviewGuide.stopPlayback();
        }
        return;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (videoviewGuide != null) {
            videoviewGuide.stopPlayback();
        }
//        MainActivity.startMainActivity(getActivity());
//        getActivity().finish();
    }
}
