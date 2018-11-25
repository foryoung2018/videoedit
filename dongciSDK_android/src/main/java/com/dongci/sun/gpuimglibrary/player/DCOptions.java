package com.dongci.sun.gpuimglibrary.player;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.dongci.sun.gpuimglibrary.player.math.DCVector2;
import com.dongci.sun.gpuimglibrary.player.math.DCVector3;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DCOptions {

    /////////////////////////////////////////////////////////
    // enum
    /////////////////////////////////////////////////////////
    // Alignment
    public static final String DCAlignmentLeft = "alignment_left";
    public static final String DCAlignmentRight = "alignment_right";
    public static final String DCAlignmentTop = "alignment_top";
    public static final String DCAlignmentBottom = "alignment_bottom";
    public static final String DCAlignmentHorizontalCenter = "alignment_horizontal_center";
    public static final String DCAlignmentVerticalCenter = "alignment_vertical_center";
    public static final String DCAlignmentCenter = "alignment_center";
    @StringDef({DCAlignmentLeft, DCAlignmentRight, DCAlignmentTop, DCAlignmentBottom, DCAlignmentHorizontalCenter, DCAlignmentVerticalCenter, DCAlignmentCenter})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCAlignment {
    }

    // action type
    public static final String DCActionTypeTranslate = "translate";
    public static final String DCActionTypeRotate = "rotate";
    public static final String DCActionTypeScale = "scale";
    public static final String DCActionTypeTranslucent = "translucent";
    public static final String DCActionTypePullCurtain = "pull_curtain";
    @StringDef({DCActionTypeTranslate, DCActionTypeRotate, DCActionTypeScale, DCActionTypeTranslucent, DCActionTypePullCurtain})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCActionType {
    }

    // interpolator type
    public static final String DCInterPolatorTypeLinear = "linear";
    public static final String DCInterPolatorTypeAccelerate = "accelerate";
    public static final String DCInterPolatorTypeAccelerateDecelerate = "accelerate_decelerate";
    public static final String DCInterPolatorTypeDecelerate = "decelerate";
    @StringDef({DCInterPolatorTypeLinear, DCInterPolatorTypeAccelerate, DCInterPolatorTypeAccelerateDecelerate, DCInterPolatorTypeDecelerate})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCInterPolatorType {
    }

    // play mode
    public static final int DCPlayModeForward = 0;
    public static final int DCPlayModeBackward = 1;
    @IntDef({DCPlayModeForward, DCPlayModeBackward})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DCPlayMode {
    }

    /////////////////////////////////////////////////////////
    // struct
    /////////////////////////////////////////////////////////
    // coordinate info
    public final static class DCCoordinateInfo {
        public DCVector2 lt;
        public DCVector2 rt;
        public DCVector2 lb;
        public DCVector2 rb;
        private float[] data = new float[8];

        public DCCoordinateInfo() {
            this.lt = new DCVector2();
            this.rt = new DCVector2();
            this.lb = new DCVector2();
            this.rb = new DCVector2();
        }
        public DCCoordinateInfo(DCVector2 lt, DCVector2 rt, DCVector2 lb, DCVector2 rb) {
            this.lt = lt;
            this.rt = rt;
            this.lb = lb;
            this.rb = rb;
        }

        public void setRawData(float ltx, float lty, float rtx, float rty, float lbx, float lby, float rbx, float rby) {
            this.lt.setRawData(ltx, lty);
            this.rt.setRawData(rtx, rty);
            this.lb.setRawData(lbx, lby);
            this.rb.setRawData(rbx, rby);
        }

        public static void copy(DCCoordinateInfo dest, DCCoordinateInfo src) {
            if (dest == null || src == null) {
                return;
            }
            DCVector2.copy(dest.lt, src.lt);
            DCVector2.copy(dest.rt, src.rt);
            DCVector2.copy(dest.lb, src.lb);
            DCVector2.copy(dest.rb, src.rb);
        }

        public float[] getData() {
            data[0] = lt.x();
            data[1] = lt.y();
            data[2] = rt.x();
            data[3] = rt.y();
            data[4] = lb.x();
            data[5] = lb.y();
            data[6] = rb.x();
            data[7] = rb.y();
            return data;
        }
    }

    // vertex info
    public final static class DCVertexInfo {
        public DCVector3 lt;
        public DCVector3 rt;
        public DCVector3 lb;
        public DCVector3 rb;
        private float[] data = new float[12];

        public DCVertexInfo() {
            this.lt = new DCVector3();
            this.rt = new DCVector3();
            this.lb = new DCVector3();
            this.rb = new DCVector3();
        }
        public DCVertexInfo(DCVector3 lt, DCVector3 rt, DCVector3 lb, DCVector3 rb) {
            this.lt = lt;
            this.rt = rt;
            this.lb = lb;
            this.rb = rb;
        }

        public void setRawData(float ltx, float lty, float ltz, float rtx, float rty, float rtz, float lbx, float lby, float lbz, float rbx, float rby, float rbz) {
            this.lt.setRawData(ltx, lty, ltz);
            this.rt.setRawData(rtx, rty, rtz);
            this.lb.setRawData(lbx, lby, lbz);
            this.rb.setRawData(rbx, rby, rbz);
        }

        public static void copy(DCVertexInfo dest, DCVertexInfo src) {
            if (dest == null || src == null) {
                return;
            }
            DCVector3.copy(dest.lt, src.lt);
            DCVector3.copy(dest.rt, src.rt);
            DCVector3.copy(dest.lb, src.lb);
            DCVector3.copy(dest.rb, src.rb);
        }

        public float[] getData() {
            data[0] = lt.x();
            data[1] = lt.y();
            data[2] = lt.z();
            data[3] = rt.x();
            data[4] = rt.y();
            data[5] = rt.z();
            data[6] = lb.x();
            data[7] = lb.y();
            data[8] = lb.z();
            data[9] = rb.x();
            data[10] = rb.y();
            data[11] = rb.z();
            return data;
        }
    }

//    // time event
//    public final static class DCTimeEvent {
//        public float eventTime;
//        public float beginTime;
//        public float endTime;
//        public float targetDuration;
//
//        public DCTimeEvent() {
//            this.eventTime = 0;
//            this.beginTime = 0;
//            this.endTime = 0;
//            this.targetDuration = 0;
//        }
//
//        public DCTimeEvent(float eventTime, float beginTime, float endTime, float targetDuration) {
//            this.eventTime = eventTime;
//            this.beginTime = beginTime;
//            this.endTime = endTime;
//            this.targetDuration = targetDuration;
//        }
//    }
}
