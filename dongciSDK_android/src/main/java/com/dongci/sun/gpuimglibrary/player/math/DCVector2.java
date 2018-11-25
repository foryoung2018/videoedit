package com.dongci.sun.gpuimglibrary.player.math;

public class DCVector2 {
    public float[] v = new float[2];

    public DCVector2() {
        this.v[0] = 0;
        this.v[1] = 0;
    }

    public DCVector2(DCVector2 vec2) {
        this.v[0] = vec2.x();
        this.v[1] = vec2.y();
    }

    public DCVector2(float x, float y) {
        this.v[0] = x;
        this.v[1] = y;
    }

    public float x() {
        return this.v[0];
    }

    public float y() {
        return this.v[1];
    }

    public void setRawData(float x, float y) {
        this.v[0] = x;
        this.v[1] = y;
    }

    public static void copy(DCVector2 dest, DCVector2 src) {
        if (dest == null || src == null) {
            return;
        }
        dest.v[0] = src.v[0];
        dest.v[1] = src.v[1];
    }
}
