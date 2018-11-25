/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wmlive.hhvideo.dcijkplayer.widget.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.dongci.sun.gpuimglibrary.R;
import com.wmlive.hhvideo.dcijkplayer.L;

import java.lang.ref.WeakReference;

public final class MeasureHelper {
    private WeakReference<View> mWeakView;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mVideoRotationDegree;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT;

    public MeasureHelper(View view) {
        mWeakView = new WeakReference<View>(view);
    }

    public View getView() {
        if (mWeakView == null)
            return null;
        return mWeakView.get();
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * Must be called by View.onMeasure(int, int)
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");
        float v = mVideoWidth * 1.0f / mVideoHeight;
        if (v == (540 * 1.0f / 960) || v == (544 * 1.0f / 960)) {
            mCurrentAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT;
        } else {
            mCurrentAspectRatio = IRenderView.AR_ADJUST_MATCH_WIDTH;
        }
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }

        L.i("=====83=mCurrentAspectRatio:" + mCurrentAspectRatio);
        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mCurrentAspectRatio == IRenderView.AR_MATCH_PARENT) {
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            float displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                L.i("=====96=mCurrentAspectRatio:" + mCurrentAspectRatio + ",widthSpecSize:" + widthSpecSize + ",heightSpecSize:" + heightSpecSize + " ,mVideoWidth：" + mVideoWidth + " ,mVideoHeight:" + mVideoHeight);
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;

                switch (mCurrentAspectRatio) {
                    case IRenderView.AR_16_9_FIT_PARENT:
                        displayAspectRatio = 16.0f / 9.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case IRenderView.AR_4_3_FIT_PARENT:
                        displayAspectRatio = 4.0f / 3.0f;
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
                            displayAspectRatio = 1.0f / displayAspectRatio;
                        break;
                    case IRenderView.AR_ASPECT_FIT_PARENT:
                    case IRenderView.AR_ASPECT_FILL_PARENT:
                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    case IRenderView.AR_ADJUST_MATCH_WIDTH:
                    default:
                        if (mVideoSarNum > 0 && mVideoSarDen > 0)
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                        break;
                }
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;

                switch (mCurrentAspectRatio) {
                    case IRenderView.AR_ADJUST_BASE_WIDTH://以宽度为基准
                        width = widthSpecSize;
                        height = (int) (width / displayAspectRatio);
                        break;
                    case IRenderView.AR_ADJUST_MATCH_WIDTH:
                        width = widthSpecSize;
                        height = (int) (width / displayAspectRatio);
                        break;
                    case IRenderView.AR_ASPECT_FIT_PARENT:
                    case IRenderView.AR_16_9_FIT_PARENT:
                    case IRenderView.AR_4_3_FIT_PARENT:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                    case IRenderView.AR_ASPECT_FILL_PARENT:
                        if (shouldBeWider) {
                            // not high enough, fix height
                            height = heightSpecSize;
                            width = (int) (height * displayAspectRatio);
                        } else {
                            // not wide enough, fix width
                            width = widthSpecSize;
                            height = (int) (width / displayAspectRatio);
                        }
                        break;

                    case IRenderView.AR_ASPECT_WRAP_CONTENT:
                    default:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                // the size is fixed
                L.i("=====165=mCurrentAspectRatio:" + mCurrentAspectRatio);
                width = widthSpecSize;
                height = heightSpecSize;
                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                L.i("=====177=mCurrentAspectRatio:" + mCurrentAspectRatio);
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                L.i("=====187=mCurrentAspectRatio:" + mCurrentAspectRatio);
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                L.i("=====196=mCurrentAspectRatio:" + mCurrentAspectRatio);
                if (mCurrentAspectRatio == IRenderView.AR_ADJUST_MATCH_WIDTH) {
                    width = widthSpecSize;
                    height = heightSpecSize;
                } else {
                    // neither the width nor the height are fixed, try to use actual video size
                    width = mVideoWidth;
                    height = mVideoHeight;
                    if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                        // too tall, decrease both width and height
                        height = heightSpecSize;
                        width = height * mVideoWidth / mVideoHeight;
                    }
                    if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                        // too wide, decrease both width and height
                        width = widthSpecSize;
                        height = width * mVideoHeight / mVideoWidth;
                    }
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        mMeasuredWidth = width;
        mMeasuredHeight = height;
        L.i("=====213=mCurrentAspectRatio:" + mCurrentAspectRatio + ",mMeasuredWidth:" + mMeasuredWidth + ",mMeasuredHeight:" + mMeasuredHeight);
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void setAspectRatio(int aspectRatio) {
//        mCurrentAspectRatio = aspectRatio;
        mCurrentAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT;
    }

    @NonNull
    public static String getAspectRatioText(Context context, int aspectRatio) {
        String text;
        switch (aspectRatio) {
            case IRenderView.AR_ASPECT_FIT_PARENT:
                text = context.getString(R.string.VideoView_ar_aspect_fit_parent);
                break;
            case IRenderView.AR_ASPECT_FILL_PARENT:
                text = context.getString(R.string.VideoView_ar_aspect_fill_parent);
                break;
            case IRenderView.AR_ASPECT_WRAP_CONTENT:
                text = context.getString(R.string.VideoView_ar_aspect_wrap_content);
                break;
            case IRenderView.AR_MATCH_PARENT:
                text = context.getString(R.string.VideoView_ar_match_parent);
                break;
            case IRenderView.AR_16_9_FIT_PARENT:
                text = context.getString(R.string.VideoView_ar_16_9_fit_parent);
                break;
            case IRenderView.AR_4_3_FIT_PARENT:
                text = context.getString(R.string.VideoView_ar_4_3_fit_parent);
                break;
            default:
                text = context.getString(R.string.N_A);
                break;
        }
        return text;
    }
}