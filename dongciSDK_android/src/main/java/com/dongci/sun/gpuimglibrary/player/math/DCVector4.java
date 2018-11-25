package com.dongci.sun.gpuimglibrary.player.math;

public class DCVector4 {
    public float[] v = new float[4];

    public DCVector4() {
        this.v[0] = 0;
        this.v[1] = 0;
        this.v[2] = 0;
        this.v[3] = 0;
    }

    public DCVector4(DCVector4 vec4) {
        this.v[0] = vec4.x();
        this.v[1] = vec4.y();
        this.v[2] = vec4.z();
        this.v[3] = vec4.w();
    }

    public DCVector4(float x, float y, float z, float w) {
        this.v[0] = x;
        this.v[1] = y;
        this.v[2] = z;
        this.v[3] = w;
    }

    public float x() {
        return this.v[0];
    }

    public float y() {
        return this.v[1];
    }

    public float z() {
        return this.v[2];
    }

    public float w() {
        return this.v[3];
    }

    public void setRawData(float x, float y, float z, float w) {
        this.v[0] = x;
        this.v[1] = y;
        this.v[2] = z;
        this.v[3] = w;
    }

    public static void copy(DCVector4 dest, DCVector4 src) {
        if (dest == null || src == null) {
            return;
        }
        dest.v[0] = src.v[0];
        dest.v[1] = src.v[1];
        dest.v[2] = src.v[2];
        dest.v[3] = src.v[3];
    }

    public DCVector3 vector3() {
        return new DCVector3(
                this.v[0] / this.v[3],
                this.v[1] / this.v[3],
                this.v[2] / this.v[3]
        );
    }
}
