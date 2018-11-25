package com.dongci.sun.gpuimglibrary.player.math;

public class DCMatrix4 {
    public float[] m = new float[16];

    public DCMatrix4() {
        this.m[0] = 1;
        this.m[1] = 0;
        this.m[2] = 0;
        this.m[3] = 0;
        this.m[4] = 0;
        this.m[5] = 1;
        this.m[6] = 0;
        this.m[7] = 0;
        this.m[8] = 0;
        this.m[9] = 0;
        this.m[10] = 1;
        this.m[11] = 0;
        this.m[12] = 0;
        this.m[13] = 0;
        this.m[14] = 0;
        this.m[15] = 1;
    }

    public DCMatrix4(DCMatrix4 mat4) {
        for (int i = 0; i < this.m.length; ++i) {
            this.m[i] = mat4.m[i];
        }
    }

    public DCMatrix4(float m00, float m01, float m02, float m03,
                     float m10, float m11, float m12, float m13,
                     float m20, float m21, float m22, float m23,
                     float m30, float m31, float m32, float m33) {
        this.m[0] = m00;
        this.m[1] =m01;
        this.m[2] =m02;
        this.m[3] =m03;
        this.m[4] = m10;
        this.m[5] = m11;
        this.m[6] = m12;
        this.m[7] = m13;
        this.m[8] = m20;
        this.m[9] = m21;
        this.m[10] = m22;
        this.m[11] = m23;
        this.m[12] = m30;
        this.m[13] = m31;
        this.m[14] = m32;
        this.m[15] = m33;
    }

    public static DCMatrix4 createTranslation(float x, float y, float z) {
        DCMatrix4 mat4 = new DCMatrix4();
        mat4.m[12] = x;
        mat4.m[13] = y;
        mat4.m[14] = z;
        return mat4;
    }

    public static DCMatrix4 createScale(float x, float y, float z) {
        DCMatrix4 mat4 = new DCMatrix4();
        mat4.m[0] = x;
        mat4.m[5] = y;
        mat4.m[10] = z;
        return mat4;
    }

    public static DCMatrix4 createRotation(float radians, float x, float y, float z) {
        DCVector3 v = DCVector3.normalize(new DCVector3(x, y, z));
        double cos = Math.cos(radians);
        double cosp = 1.0 - cos;
        double sin = Math.sin(radians);
        return new DCMatrix4(
                (float)(cos + cosp * v.v[0] * v.v[0]),
                (float)(cosp * v.v[0] * v.v[1] + v.v[2] * sin),
                (float)(cosp * v.v[0] * v.v[2] - v.v[1] * sin),
                0.0f,
                (float)(cosp * v.v[0] * v.v[1] - v.v[2] * sin),
                (float)(cos + cosp * v.v[1] * v.v[1]),
                (float)(cosp * v.v[1] * v.v[2] + v.v[0] * sin),
                0.0f,
                (float)(cosp * v.v[0] * v.v[2] + v.v[1] * sin),
                (float)(cosp * v.v[1] * v.v[2] - v.v[0] * sin),
                (float)(cos + cosp * v.v[2] * v.v[2]),
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f
        );
    }

    public static DCMatrix4 createXRotation(float radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new DCMatrix4(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, (float)cos, (float)sin, 0.0f,
                0.0f, (float)-sin, (float)cos, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static DCMatrix4 createYRotation(double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new DCMatrix4(
                (float)cos, 0.0f, (float)-sin, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                (float)sin, 0.0f, (float)cos, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static DCMatrix4 createZRotation(float radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new DCMatrix4(
                (float)cos, (float)sin, 0.0f, 0.0f,
                (float)-sin, (float)cos, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        );
    }

    public static DCMatrix4 ortho(float left, float right,
                                  float bottom, float top,
                                  float nearZ, float farZ) {
        float ral = right + left;
        float rsl = right - left;
        float tab = top + bottom;
        float tsb = top - bottom;
        float fan = farZ + nearZ;
        float fsn = farZ - nearZ;

        return new DCMatrix4(
                2.0f / rsl, 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / tsb, 0.0f, 0.0f,
                0.0f, 0.0f, -2.0f / fsn, 0.0f,
                -ral / rsl, -tab / tsb, -fan / fsn, 1.0f
        );
    }

    public static DCMatrix4 lookAt(float eyeX, float eyeY, float eyeZ,
                                   float centerX, float centerY, float centerZ,
                                   float upX, float upY, float upZ) {
        DCVector3 ev = new DCVector3(eyeX, eyeY, eyeZ);
        DCVector3 cv = new DCVector3(centerX, centerY, centerZ);
        DCVector3 uv = new DCVector3(upX, upY, upZ);
        DCVector3 n = DCVector3.normalize(DCVector3.add(ev, DCVector3.negate(cv)));
        DCVector3 u = DCVector3.normalize(DCVector3.crossProduct(uv, n));
        DCVector3 v = DCVector3.crossProduct(n, u);

        return new DCMatrix4(
                u.v[0], v.v[0], n.v[0], 0.0f,
                u.v[1], v.v[1], n.v[1], 0.0f,
                u.v[2], v.v[2], n.v[2], 0.0f,
                (float)DCVector3.dotProduct(DCVector3.negate(u), ev),
                (float)DCVector3.dotProduct(DCVector3.negate(v), ev),
                (float)DCVector3.dotProduct(DCVector3.negate(n), ev),
                1.0f
        );
    }

    public static DCMatrix4 multiply(DCMatrix4 matLeft, DCMatrix4 matRight) {
        DCMatrix4 mat = new DCMatrix4();
        mat.m[0]    = matLeft.m[0]  * matRight.m[0] + matLeft.m[1]  * matRight.m[4] + matLeft.m[2]  * matRight.m[8]     + matLeft.m[3]  * matRight.m[12];
        mat.m[1]    = matLeft.m[0]  * matRight.m[1] + matLeft.m[1]  * matRight.m[5] + matLeft.m[2]  * matRight.m[9]     + matLeft.m[3]  * matRight.m[13];
        mat.m[2]    = matLeft.m[0]  * matRight.m[2] + matLeft.m[1]  * matRight.m[6] + matLeft.m[2]  * matRight.m[10]    + matLeft.m[3]  * matRight.m[14];
        mat.m[3]    = matLeft.m[0]  * matRight.m[3] + matLeft.m[1]  * matRight.m[7] + matLeft.m[2]  * matRight.m[11]    + matLeft.m[3]  * matRight.m[15];
        mat.m[4]    = matLeft.m[4]  * matRight.m[0] + matLeft.m[5]  * matRight.m[4] + matLeft.m[6]  * matRight.m[8]     + matLeft.m[7]  * matRight.m[12];
        mat.m[5]    = matLeft.m[4]  * matRight.m[1] + matLeft.m[5]  * matRight.m[5] + matLeft.m[6]  * matRight.m[9]     + matLeft.m[7]  * matRight.m[13];
        mat.m[6]    = matLeft.m[4]  * matRight.m[2] + matLeft.m[5]  * matRight.m[6] + matLeft.m[6]  * matRight.m[10]    + matLeft.m[7]  * matRight.m[14];
        mat.m[7]    = matLeft.m[4]  * matRight.m[3] + matLeft.m[5]  * matRight.m[7] + matLeft.m[6]  * matRight.m[11]    + matLeft.m[7]  * matRight.m[15];
        mat.m[8]    = matLeft.m[8]  * matRight.m[0] + matLeft.m[9]  * matRight.m[4] + matLeft.m[10] * matRight.m[8]     + matLeft.m[11] * matRight.m[12];
        mat.m[9]    = matLeft.m[8]  * matRight.m[1] + matLeft.m[9]  * matRight.m[5] + matLeft.m[10] * matRight.m[9]     + matLeft.m[11] * matRight.m[13];
        mat.m[10]   = matLeft.m[8]  * matRight.m[2] + matLeft.m[9]  * matRight.m[6] + matLeft.m[10] * matRight.m[10]    + matLeft.m[11] * matRight.m[14];
        mat.m[11]   = matLeft.m[8]  * matRight.m[3] + matLeft.m[9]  * matRight.m[7] + matLeft.m[10] * matRight.m[11]    + matLeft.m[11] * matRight.m[15];
        mat.m[12]   = matLeft.m[12] * matRight.m[0] + matLeft.m[13] * matRight.m[4] + matLeft.m[14] * matRight.m[8]     + matLeft.m[15] * matRight.m[12];
        mat.m[13]   = matLeft.m[12] * matRight.m[1] + matLeft.m[13] * matRight.m[5] + matLeft.m[14] * matRight.m[9]     + matLeft.m[15] * matRight.m[13];
        mat.m[14]   = matLeft.m[12] * matRight.m[2] + matLeft.m[13] * matRight.m[6] + matLeft.m[14] * matRight.m[10]    + matLeft.m[15] * matRight.m[14];
        mat.m[15]   = matLeft.m[12] * matRight.m[3] + matLeft.m[13] * matRight.m[7] + matLeft.m[14] * matRight.m[11]    + matLeft.m[15] * matRight.m[15];
        return mat;
    }

    public DCVector3 matrixMultiplyVector3(DCVector3 vec3) {
        return matrixMultiplyVector4(new DCVector4(vec3.x(), vec3.y(), vec3.z(), 1.0f)).vector3();
    }

    public DCVector4 matrixMultiplyVector4(DCVector4 vec4) {
        float x = vec4.x() * this.m[0] + vec4.y() * this.m[4] + vec4.z() * this.m[8] + vec4.w() * this.m[12];
        float y = vec4.x() * this.m[1] + vec4.y() * this.m[5] + vec4.z() * this.m[9] + vec4.w() * this.m[13];
        float z = vec4.x() * this.m[2] + vec4.y() * this.m[6] + vec4.z() * this.m[10] + vec4.w() * this.m[14];
        float w = vec4.x() * this.m[3] + vec4.y() * this.m[7] + vec4.z() * this.m[11] + vec4.w() * this.m[15];
        return new DCVector4(x, y, z, w);
    }
}
