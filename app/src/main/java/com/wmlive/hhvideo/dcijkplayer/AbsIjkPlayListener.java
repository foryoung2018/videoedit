package com.wmlive.hhvideo.dcijkplayer;

/**
 * Created by yangjiangang on 2018/7/27.
 */

public abstract class AbsIjkPlayListener implements IjkPlayListener {

        @Override
        public void onPlayStart() {

        }

        @Override
        public void onPlayStop() {

        }

        @Override
        public void onPlayPause() {

        }

        @Override
        public void onPlayResume() {

        }

        @Override
        public void onPlayError(int errorCode) {

        }

        @Override
        public void onPlayCompleted() {

        }

        @Override
        public void onPlayBufferStart() {

        }

        @Override
        public void onPlayBufferEnd() {

        }

        @Override
        public void onPlayPreparing() {

        }

        @Override
        public void onPlayPrepared() {

        }

        @Override
        public void onAudioRenderingStart() {

        }

        @Override
        public void onVideoRenderingStart() {

        }

        @Override
        public void onVideoRotationChanged(int rotate) {

        }

        @Override
        public void onPlayingPosition(long position) {

        }

        @Override
        public void onFileError(int code, String errorMessage) {

        }

        @Override
        public void onLoopStart() {

        }

        @Override
        public void onClickPause() {

        }

        @Override
        public void onPlayTimeCompleted(long videoId, String url, int videoDuring) {

        }

        @Override
        public void onDoubleClick(float x, float y) {

        }
}