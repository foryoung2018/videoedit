package com.dongci.sun.gpuimglibrary.player.renderObject;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.dongci.sun.gpuimglibrary.player.DCAssetInfo;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEvent;

import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCPlayModeBackward;
import static com.dongci.sun.gpuimglibrary.player.DCOptions.DCPlayModeForward;

public class DCRenderImages extends DCRenderImage {

    public DCRenderImages(DCAssetInfo assetInfo) {
        super(assetInfo);
    }

    @Override
    public int updateTexture(long presentationTime, DCTimeEvent timeEvent) {
        if (this.textureID == 0) {
            super.updateTexture(presentationTime, timeEvent);
        } else {
            if (this.assetInfo.assetWrapper.getBitmapCount() > 1) {
                int index;
                double frameInterval = this.assetInfo.assetWrapper.mAsset.frameInterval;
                if (timeEvent != null) {
                    switch (timeEvent.playMode) {
                        case DCPlayModeBackward:
                        {
                            int beginIndex = (int)((double)timeEvent.endTime / frameInterval);
                            if ((int)((presentationTime - timeEvent.eventTime) / frameInterval) / (beginIndex + 1) >= 1) {
                                beginIndex = 0;
                                index = beginIndex + (int)((presentationTime - timeEvent.eventTime) / frameInterval) % (this.assetInfo.assetWrapper.getBitmapCount() - beginIndex);
                            } else {
                                index = beginIndex - (int)((presentationTime - timeEvent.eventTime) / frameInterval) % (beginIndex + 1);
                            }
                        }
                        break;
                        case DCPlayModeForward:
                        default:
                        {
                            int beginIndex = (int)((double)timeEvent.beginTime / frameInterval);
                            if ((int)((presentationTime - timeEvent.eventTime) / frameInterval) / (this.assetInfo.assetWrapper.getBitmapCount() - beginIndex) > 1) {
                                beginIndex = 0;
                            }
                            index = beginIndex + (int)((presentationTime - timeEvent.eventTime) / frameInterval) % (this.assetInfo.assetWrapper.getBitmapCount() - beginIndex);
                        }
                        break;
                    }
                } else {
                    index = (int)((double)presentationTime / frameInterval) % this.assetInfo.assetWrapper.getBitmapCount();
                }
                if (index >= this.assetInfo.assetWrapper.getBitmapCount()) {
                    index = this.assetInfo.assetWrapper.getBitmapCount() - 1;
                } else if (index < 0) {
                    return -1;
                }
                Bitmap db = this.assetInfo.assetWrapper.getBitmap(index);
                if (db != null) {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textureID);
                    GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, db);
                }
            }
        }
        return this.textureID;
    }
}
