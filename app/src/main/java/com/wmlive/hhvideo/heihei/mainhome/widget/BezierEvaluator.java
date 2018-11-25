package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Created by lsq on 1/9/2018.5:52 PM
 *
 * @author lsq
 * @describe 添加描述
 */
public class BezierEvaluator implements TypeEvaluator<Point> {

    private Point controlPoint;

    public BezierEvaluator(Point controlPoint) {
        this.controlPoint = controlPoint;
    }

    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        int x = (int) ((1 - fraction) * (1 - fraction) * startValue.x + 2 * fraction * (1 - fraction) * controlPoint.x + fraction * fraction * endValue.x);
        int y = (int) ((1 - fraction) * (1 - fraction) * startValue.y + 2 * fraction * (1 - fraction) * controlPoint.y + fraction * fraction * endValue.y);
        return new Point(x, y);
    }
}
