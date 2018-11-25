package com.dongci.sun.gpuimglibrary.player.renderObject;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.dongci.sun.gpuimglibrary.player.DCAssetInfo;
import com.dongci.sun.gpuimglibrary.player.script.DCTimeEvent;

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
//                float seconds = (float)presentationTime / 1000000.0f;
                double frameInterval = this.assetInfo.assetWrapper.mAsset.frameInterval;
                if (timeEvent != null) {
                    if (timeEvent.targetDuration <= 0) {
                        index = (int)(presentationTime / frameInterval) % this.assetInfo.assetWrapper.getBitmapCount();
                    } else {
                        int beginIndex = (int)(timeEvent.beginTime / frameInterval);
                        int endIndex = (int)(timeEvent.endTime / frameInterval);

                        if (timeEvent.eventTime + timeEvent.targetDuration < presentationTime) {
                            index = endIndex;
                        } else {
                            index = (int)(beginIndex + (presentationTime - timeEvent.eventTime) * (endIndex - beginIndex + 1) / timeEvent.targetDuration);
                        }
                    }
                } else {
                    index = (int)(presentationTime / frameInterval) % this.assetInfo.assetWrapper.getBitmapCount();
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
