package com.dongci.sun.gpuimglibrary.player.math;

public class DCVector3 {
    public float[] v = new float[3];

    public DCVector3() {
        this.v[0] = 0;
        this.v[1] = 0;
        this.v[2] = 0;
    }

    public DCVector3(DCVector3 vec3) {
        this.v[0] = vec3.x();
        this.v[1] = vec3.y();
        this.v[2] = vec3.z();
    }

    public DCVector3(float x, float y, float z) {
        this.v[0] = x;
        this.v[1] = y;
        this.v[2] = z;
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

    public void setRawData(float x, float y, float z) {
        this.v[0] = x;
        this.v[1] = y;
        this.v[2] = z;
    }

    public static void copy(DCVector3 dest, DCVector3 src) {
        if (dest == null || src == null) {
            return;
        }
        dest.v[0] = src.v[0];
        dest.v[1] = src.v[1];
        dest.v[2] = src.v[2];
    }

    public static DCVector3 normalize(DCVector3 vec3) {
        double scale = 1.0 / length(vec3);
        return new DCVector3((float)(vec3.v[0] * scale), (float)(vec3.v[1] * scale), (float)(vec3.v[2] * scale));
    }

    public static double length(DCVector3 vec3) {
        return Math.sqrt(vec3.v[0] * vec3.v[0] + vec3.v[1] * vec3.v[1] + vec3.v[2] * vec3.v[2]);
    }

    public static DCVector3 add(DCVector3 vec3Left, DCVector3 vec3Right) {
        return new DCVector3(
                vec3Left.v[0] + vec3Right.v[0],
                vec3Left.v[1] + vec3Right.v[1],
                vec3Left.v[2] + vec3Right.v[2]
        );
    }

    public static DCVector3 subtract(DCVector3 vec3Left, DCVector3 vec3Right) {
        return new DCVector3(
                vec3Left.v[0] - vec3Right.v[0],
                vec3Left.v[1] - vec3Right.v[1],
                vec3Left.v[2] - vec3Right.v[2]
        );
    }

    public static DCVector3 multiply(DCVector3 vec3Left, DCVector3 vec3Right) {
        return new DCVector3(
                vec3Left.v[0] * vec3Right.v[0],
                vec3Left.v[1] * vec3Right.v[1],
                vec3Left.v[2] * vec3Right.v[2]
        );
    }

    public static DCVector3 divide(DCVector3 vec3Left, DCVector3 vec3Right) {
        return new DCVector3(
                vec3Left.v[0] / vec3Right.v[0],
                vec3Left.v[1] / vec3Right.v[1],
                vec3Left.v[2] / vec3Right.v[2]
        );
    }

    public static double dotProduct(DCVector3 vec3Left, DCVector3 vec3Right) {
        return vec3Left.v[0] * vec3Right.v[0] + vec3Left.v[1] * vec3Right.v[1] + vec3Left.v[2] * vec3Right.v[2];
    }

    public static DCVector3 crossProduct(DCVector3 vec3Left, DCVector3 vec3Right) {
        return new DCVector3(
                vec3Left.v[1] * vec3Right.v[2] - vec3Left.v[2] * vec3Right.v[1],
                vec3Left.v[2] * vec3Right.v[0] - vec3Left.v[0] * vec3Right.v[2],
                vec3Left.v[0] * vec3Right.v[1] - vec3Left.v[1] * vec3Right.v[0]
        );
    }

    public static DCVector3 negate(DCVector3 vec3) {
        return new DCVector3(-vec3.v[0], -vec3.v[1], -vec3.v[2]);
    }

    public static double distance(DCVector3 vec3Start, DCVector3 vec3End) {
        return length(subtract(vec3End, vec3Start));
    }
}
