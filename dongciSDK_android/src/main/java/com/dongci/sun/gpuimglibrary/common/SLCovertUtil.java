package com.dongci.sun.gpuimglibrary.common;

import java.nio.ByteBuffer;

public class SLCovertUtil {

    public native static int convertVideoFrame(ByteBuffer src, ByteBuffer dest, int destFormat, int width, int height, int padding, int swap);

}
