package com.dongci.sun.gpuimglibrary.gles.filter.diyfilter;

import com.dongci.sun.gpuimglibrary.gles.filter.filternew.GPUImageColorMatrixFilter;

public class GPUImageHSBFilter extends GPUImageColorMatrixFilter {
    private static final float RLUM = 0.3f;
    private static final float GLUM = 0.59f;
    private static final float BLUM = 0.11f;

    private static float[] sMatrix = {
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0};

    public GPUImageHSBFilter() {
        super();
    }

    @Override
    public void onInit() {
        super.onInit();
        reset();
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        reset();
    }

    public void reset() {
        identmat(sMatrix);
        setColorMatrix(sMatrix);
    }

    public void rotateHue(float h) {
        huerotatemat(sMatrix, h);
        setColorMatrix(sMatrix);
    }

    public void adjustSaturation(float s) {
        saturatemat(sMatrix, s);
        setColorMatrix(sMatrix);
    }

    public void adjustBrightness(float b) {
        cscalemat(sMatrix, b, b, b);
        setColorMatrix(sMatrix);
    }

    @Override
    public void setColorMatrix(final float[] colorMatrix) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};
        mmat[0] = colorMatrix[0];
        mmat[1] = colorMatrix[4];
        mmat[2] = colorMatrix[8];
        mmat[3] = colorMatrix[12];

        mmat[4] = colorMatrix[1];
        mmat[5] = colorMatrix[5];
        mmat[6] = colorMatrix[9];
        mmat[7] = colorMatrix[13];

        mmat[8] = colorMatrix[2];
        mmat[9] = colorMatrix[6];
        mmat[10] = colorMatrix[10];
        mmat[11] = colorMatrix[14];

        mmat[12] = colorMatrix[3];
        mmat[13] = colorMatrix[7];
        mmat[14] = colorMatrix[11];
        mmat[15] = colorMatrix[15];
        super.setColorMatrix(mmat);
    }

    private static void identmat(float[] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            matrix[i] = 0;
        }
        matrix[0] = 1.0f;
        matrix[5] = 1.0f;
        matrix[10] = 1.0f;
        matrix[15] = 1.0f;
    }

    private static void huerotatemat(float[] mat, double rot) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};
        double mag;
        float[] lx = {0}, ly = {0}, lz = {0};
        double xrs, xrc;
        double yrs, yrc;
        double zrs, zrc;
        double zsx, zsy;

        identmat(mmat);

        /* rotate the grey vector into positive Z */
        mag = Math.sqrt(2.0);
        xrs = 1.0 / mag;
        xrc = 1.0 / mag;
        xrotatemat(mmat, (float)xrs, (float)xrc);
        mag = Math.sqrt(3.0);
        yrs = -1.0 / mag;
        yrc = Math.sqrt(2.0) / mag;
        yrotatemat(mmat, (float)yrs, (float)yrc);

        /* shear the space to make the luminance plane horizontal */
        xformpnt(mmat, RLUM, GLUM, BLUM, lx, ly, lz);
        zsx = lx[0] / lz[0];
        zsy = ly[0] / lz[0];
        zshearmat(mmat, (float)zsx, (float)zsy);

        /* rotate the hue */
        zrs = Math.sin(rot * Math.PI / 180.0f);
        zrc = Math.cos(rot * Math.PI/180.0);
        zrotatemat(mmat, (float)zrs, (float)zrc);

        /* unshear the space to put the luminance plane back */
        zshearmat(mmat, (float)-zsx, (float)-zsy);

        /* rotate the grey vector back into place */
        yrotatemat(mmat, (float)-yrs, (float)yrc);
        xrotatemat(mmat, (float)-xrs, (float)xrc);

        matrixmult(mmat, mat, mat);
    }

    private static void saturatemat(float[] mat, float sat) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};
        float a, b, c, d, e, f, g, h, i;
        float rwgt, gwgt, bwgt;

        rwgt = RLUM;
        gwgt = GLUM;
        bwgt = BLUM;

        a = (1.0f - sat) * rwgt + sat;
        b = (1.0f - sat) * rwgt;
        c = (1.0f - sat) * rwgt;
        d = (1.0f - sat) * gwgt;
        e = (1.0f - sat) * gwgt + sat;
        f = (1.0f - sat) * gwgt;
        g = (1.0f - sat) * bwgt;
        h = (1.0f - sat) * bwgt;
        i = (1.0f - sat) * bwgt + sat;
        mmat[0] = a;
        mmat[1] = b;
        mmat[2] = c;
        mmat[3] = 0.0f;

        mmat[4] = d;
        mmat[5] = e;
        mmat[6] = f;
        mmat[7] = 0.0f;

        mmat[8] = g;
        mmat[9] = h;
        mmat[10] = i;
        mmat[11] = 0.0f;

        mmat[12] = 0.0f;
        mmat[13] = 0.0f;
        mmat[14] = 0.0f;
        mmat[15] = 1.0f;
        matrixmult(mmat, mat, mat);
    }

    private static void cscalemat(float[] mat, float rscale, float gscale, float bscale) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};

        mmat[0] = rscale;
        mmat[1] = 0.0f;
        mmat[2] = 0.0f;
        mmat[3] = 0.0f;

        mmat[4] = 0.0f;
        mmat[5] = gscale;
        mmat[6] = 0.0f;
        mmat[7] = 0.0f;


        mmat[8] = 0.0f;
        mmat[9] = 0.0f;
        mmat[10] = bscale;
        mmat[11] = 0.0f;

        mmat[12] = 0.0f;
        mmat[13] = 0.0f;
        mmat[14] = 0.0f;
        mmat[15] = 1.0f;
        matrixmult(mmat, mat, mat);
    }

    private static void xrotatemat(float[] mat, float rs, float rc) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};

        mmat[0] = 1.0f;
        mmat[1] = 0.0f;
        mmat[2] = 0.0f;
        mmat[3] = 0.0f;

        mmat[4] = 0.0f;
        mmat[5] = rc;
        mmat[6] = rs;
        mmat[7] = 0.0f;

        mmat[8] = 0.0f;
        mmat[9] = -rs;
        mmat[10] = rc;
        mmat[11] = 0.0f;

        mmat[12] = 0.0f;
        mmat[13] = 0.0f;
        mmat[14] = 0.0f;
        mmat[15] = 1.0f;
        matrixmult(mmat, mat, mat);
    }

    private static void yrotatemat(float[] mat, float rs, float rc) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};

        mmat[0] = rc;
        mmat[1] = 0.0f;
        mmat[2] = -rs;
        mmat[3] = 0.0f;

        mmat[4] = 0.0f;
        mmat[5] = 1.0f;
        mmat[6] = 0.0f;
        mmat[7] = 0.0f;

        mmat[8] = rs;
        mmat[9] = 0.0f;
        mmat[10] = rc;
        mmat[11] = 0.0f;

        mmat[12] = 0.0f;
        mmat[13] = 0.0f;
        mmat[14] = 0.0f;
        mmat[15] = 1.0f;
        matrixmult(mmat, mat, mat);
    }

    private static void zrotatemat(float[] mat, float rs, float rc) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};

        mmat[0] = rc;
        mmat[1] = rs;
        mmat[2] = 0.0f;
        mmat[3] = 0.0f;

        mmat[4] = -rs;
        mmat[5] = rc;
        mmat[6] = 0.0f;
        mmat[7] = 0.0f;

        mmat[8] = 0.0f;
        mmat[9] = 0.0f;
        mmat[10] = 1.0f;
        mmat[11] = 0.0f;

        mmat[12] = 0.0f;
        mmat[13] = 0.0f;
        mmat[14] = 0.0f;
        mmat[15] = 1.0f;
        matrixmult(mmat, mat, mat);
    }

    private static void zshearmat(float[] mat, float dx, float dy) {
        float[] mmat = {
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0};

        mmat[0] = 1.0f;
        mmat[1] = 0.0f;
        mmat[2] = dx;
        mmat[3] = 0.0f;

        mmat[4] = 0.0f;
        mmat[5] = 1.0f;
        mmat[6] = dy;
        mmat[7] = 0.0f;

        mmat[8] = 0.0f;
        mmat[9] = 0.0f;
        mmat[10] = 1.0f;
        mmat[11] = 0.0f;

        mmat[12] = 0.0f;
        mmat[13] = 0.0f;
        mmat[14] = 0.0f;
        mmat[15] = 1.0f;
        matrixmult(mmat, mat, mat);
    }

    private static void xformpnt(float[] matrix, float x, float y, float z, float[] tx, float[] ty, float[] tz) {
        tx[0] = x * matrix[0] + y * matrix[4] + z * matrix[8] + matrix[12];
        ty[0] = x * matrix[1] + y * matrix[5] + z * matrix[9] + matrix[13];
        tz[0] = x * matrix[2] + y * matrix[6] + z * matrix[10] + matrix[14];
    }

    private static void matrixmult(float[] a, float[] b, float[] c) {
        int x, y;
        float[] temp = new float[16];

        for(y=0; y<4 ; y++) {
            for(x=0 ; x<4 ; x++) {
                int index = y * 4 + x;
                temp[index] = b[y * 4] * a[x]
                        + b[y * 4 + 1] * a[4 + x]
                        + b[y * 4 + 2] * a[8 + x]
                        + b[y * 4 + 3] * a[12 + x];
            }
        }

        for(y=0; y<4; y++) {
            for(x=0; x<4; x++) {
                c[y * 4 + x] = temp[y * 4 + x];
            }
        }
    }
}
