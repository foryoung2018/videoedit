package com.dongci.sun.gpuimglibrary.common;

public class SLSample {
    private long offset = 0;
    private long size = 0;

    public SLSample(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }
}
