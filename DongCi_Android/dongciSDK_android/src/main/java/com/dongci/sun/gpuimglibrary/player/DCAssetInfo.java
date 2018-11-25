package com.dongci.sun.gpuimglibrary.player;

import com.dongci.sun.gpuimglibrary.player.math.DCMatrix4;
import com.dongci.sun.gpuimglibrary.player.math.DCVector3;

public class DCAssetInfo {
    public DCAssetWrapper assetWrapper;
    public DCOptions.DCCoordinateInfo coordinates;
    public DCOptions.DCVertexInfo vertices;
    public DCVector3 centerVertex;

    public static DCAssetInfo createAssetInfo(DCAssetWrapper assetWrapper, float renderWidth, float renderHeight) {
        if (assetWrapper == null || renderWidth <= 0 || renderHeight <= 0) {
            return null;
        }
        DCAssetInfo assetInfo = new DCAssetInfo();
        assetInfo.assetWrapper = assetWrapper;

        DCAsset asset = assetWrapper.mAsset;

        // rect in video
        float x1 = asset.rectInVideo.left;
        float y1 = asset.rectInVideo.top;
        float x2 = asset.rectInVideo.right;
        float y2 = asset.rectInVideo.bottom;

        float clipWidth = (x2 - x1) * renderWidth;
        float clipHeight = (y2 - y1) * renderHeight;

        float assetWidth = asset.cropRect.width();
        float assetHeight = asset.cropRect.height();

        float destWidth = Math.min(clipWidth, assetWidth);
        float destHeight = Math.min(clipHeight, assetHeight);

        float currentVideoWidth = assetWrapper.getWidth();
        float currentVideoHeight = assetWrapper.getHeight();

        destWidth = Math.min(destWidth, currentVideoWidth);
        destHeight = Math.min(destHeight, currentVideoHeight);

        switch (asset.fillType) {
            case DCAsset.DCAssetFillTypeAspectFill:
                x2 = x1 + destWidth / renderWidth;
                y2 = y1 + destHeight / renderHeight;
                break;
            case DCAsset.DCAssetFillTypeScaleToFit:
            default:
                break;
        }

//        float leftTopX = 2.0f * x1 - 1.0f;
//        float leftTopY = -2.0f * y1 + 1.0f;
//
//        float rightTopX = 2.0f * x2 - 1.0f;
//        float rightTopY = -2.0f * y1 + 1.0f;
//
//        float leftBottomX = 2.0f * x1 - 1.0f;
//        float leftBottomY = -2.0f * y2 + 1.0f;
//
//        float rightBottomX = 2.0f * x2 - 1.0f;
//        float rightBottomY = -2.0f * y2 + 1.0f;

//        float leftTopX = 2.0f * x1 - 1.0f;
//        float leftTopY = -2.0f * y1 + 1.0f;
        float leftBottomX = 2.0f * x1 - 1.0f;
        float leftBottomY = -2.0f * y1 + 1.0f;

//        float rightTopX = 2.0f * x2 - 1.0f;
//        float rightTopY = -2.0f * y1 + 1.0f;
        float rightBottomX = 2.0f * x2 - 1.0f;
        float rightBottomY = -2.0f * y1 + 1.0f;

        float leftTopX = 2.0f * x1 - 1.0f;
        float leftTopY = -2.0f * y2 + 1.0f;

        float rightTopX = 2.0f * x2 - 1.0f;
        float rightTopY = -2.0f * y2 + 1.0f;

        // uv
        x1 = asset.cropRect.left;
        y1 = asset.cropRect.top;

        switch (asset.fillType) {
            case DCAsset.DCAssetFillTypeScaleToFit:
                x2 = Math.min(currentVideoWidth, asset.cropRect.right);
                y2 = Math.min(currentVideoHeight, asset.cropRect.bottom);
                break;
            case DCAsset.DCAssetFillTypeAspectFill:
            default:
                x2 = x1 + destWidth;
                y2 = y1 + destHeight;
                break;
        }

        float uvLeftTopX = x1 / currentVideoWidth;
        float uvLeftTopY = 1.0f - y2 / currentVideoHeight;

        float uvRightTopX = x2 / currentVideoWidth;
        float uvRightTopY = 1.0f - y2 / currentVideoHeight;

        float uvLeftBottomX = x1 / currentVideoWidth;
        float uvLeftBottomY = 1.0f - y1 / currentVideoHeight;

        float uvRightBottomX = x2 / currentVideoWidth;
        float uvRightBottomY = 1.0f - y1 / currentVideoHeight;

        assetInfo.coordinates.setRawData(
                uvLeftTopX, uvLeftTopY,
                uvRightTopX, uvRightTopY,
                uvLeftBottomX, uvLeftBottomY,
                uvRightBottomX, uvRightBottomY
        );

        assetInfo.vertices.setRawData(
                leftTopX * renderWidth / 2.0f, leftTopY * renderHeight / 2.0f, 0,
                rightTopX * renderWidth / 2.0f, rightTopY * renderHeight / 2.0f, 0,
                leftBottomX * renderWidth / 2.0f, leftBottomY * renderHeight / 2.0f, 0,
                rightBottomX * renderWidth / 2.0f, rightBottomY * renderHeight / 2.0f, 0
        );
        assetInfo.centerVertex.setRawData(
                (assetInfo.vertices.rb.x() + assetInfo.vertices.lt.x()) / 2.0f,
                (assetInfo.vertices.rb.y() + assetInfo.vertices.lt.y()) / 2.0f,
                0
        );

        // build tranlate matrix to translate the vertices center pos to (0, 0, 0)
        DCMatrix4 translateMat = DCMatrix4.createTranslation(-assetInfo.centerVertex.x(), -assetInfo.centerVertex.y(), -assetInfo.centerVertex.z());
        assetInfo.vertices.lt = translateMat.matrixMultiplyVector3(assetInfo.vertices.lt);
        assetInfo.vertices.rt = translateMat.matrixMultiplyVector3(assetInfo.vertices.rt);
        assetInfo.vertices.lb = translateMat.matrixMultiplyVector3(assetInfo.vertices.lb);
        assetInfo.vertices.rb = translateMat.matrixMultiplyVector3(assetInfo.vertices.rb);

        return assetInfo;
    }

    private DCAssetInfo() {
        this.coordinates = new DCOptions.DCCoordinateInfo();
        this.vertices = new DCOptions.DCVertexInfo();
        this.centerVertex = new DCVector3();
    }
}
