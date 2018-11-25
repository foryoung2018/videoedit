package com.wmlive.hhvideo.utils;

import android.graphics.Path;
import android.graphics.RectF;

import com.wmlive.hhvideo.heihei.beans.frame.LayoutInfo;

/**
 * Created by wenlu on 2017/8/29.
 */

public class DrawPathUtils {

    public static Path getDrawPath(LayoutInfo info) {
        return getDrawPath(info, 0);
    }

    public static Path getDrawPath(LayoutInfo info, int borderWidth) {

        Path path = new Path();
        if (LayoutInfo.TYPE_SHAPE_RECT.equals(info.layout_type)) {
            path.addRect(new RectF(info.xr + borderWidth, info.yr + borderWidth, info.xr + info.wr + borderWidth, info.yr + info.hr + borderWidth), Path.Direction.CW);
//                info.path.op(anomalyLayout.path, Path.Op.DIFFERENCE);
        } else if (LayoutInfo.TYPE_SHAPE_CIRCLE.equals(info.layout_type)) {
            path.addCircle(info.xr + info.wr / 2, info.yr + info.hr / 2, info.wr / 2, Path.Direction.CW);
        } else if (LayoutInfo.TYPE_SHAPE_OVAL.equals(info.layout_type)) {
//                path.addOval(x, y, x + w, y + h, Path.Direction.CW);
        } else {
//                initPoints();
//                for (int i = 0; i < points.size(); i++) {
//                    LayoutInfo.FramePoint point = points.get(i);
//                    if (i == 0) {
//                        path.moveTo((float) point.x, (float) point.y);
//                        continue;
//                    }
//                    path.lineTo((float) point.x, (float) point.y);
//                }
        }
        path.close();
        return path;
    }

    public static Path getDrawBorderPath(LayoutInfo info, int borderWidth) {

        Path path = new Path();
        if (LayoutInfo.TYPE_SHAPE_RECT.equals(info.layout_type)) {
            path.addRect(new RectF(info.xr + borderWidth / 2, info.yr + borderWidth / 2, info.xr + info.wr + 1.5f * borderWidth, info.yr + info.hr + 1.5f * borderWidth), Path.Direction.CW);
//                info.path.op(anomalyLayout.path, Path.Op.DIFFERENCE);
        } else if (LayoutInfo.TYPE_SHAPE_CIRCLE.equals(info.layout_type)) {
            path.addCircle(info.xr + info.wr / 2, info.yr + info.hr / 2, info.wr / 2, Path.Direction.CW);
        } else if (LayoutInfo.TYPE_SHAPE_OVAL.equals(info.layout_type)) {
//                path.addOval(x, y, x + w, y + h, Path.Direction.CW);
        } else {
//                initPoints();
//                for (int i = 0; i < points.size(); i++) {
//                    LayoutInfo.FramePoint point = points.get(i);
//                    if (i == 0) {
//                        path.moveTo((float) point.x, (float) point.y);
//                        continue;
//                    }
//                    path.lineTo((float) point.x, (float) point.y);
//                }
        }
        path.close();
        return path;
    }

    public static Path getDrawBorderPathSmall(LayoutInfo info, int borderWidth) {

        Path path = new Path();
        if (LayoutInfo.TYPE_SHAPE_RECT.equals(info.layout_type)) {
            path.addRect(new RectF(info.xr + borderWidth / 2, info.yr, info.xr + info.wr + borderWidth / 2, info.yr + info.hr), Path.Direction.CW);
//                info.path.op(anomalyLayout.path, Path.Op.DIFFERENCE);
        } else if (LayoutInfo.TYPE_SHAPE_CIRCLE.equals(info.layout_type)) {
            path.addCircle(info.xr + info.wr / 2, info.yr + info.hr / 2, info.wr / 2, Path.Direction.CW);
        } else if (LayoutInfo.TYPE_SHAPE_OVAL.equals(info.layout_type)) {
//                path.addOval(x, y, x + w, y + h, Path.Direction.CW);
        } else {
//                initPoints();
//                for (int i = 0; i < points.size(); i++) {
//                    LayoutInfo.FramePoint point = points.get(i);
//                    if (i == 0) {
//                        path.moveTo((float) point.x, (float) point.y);
//                        continue;
//                    }
//                    path.lineTo((float) point.x, (float) point.y);
//                }
        }
        path.close();
        return path;
    }
}
