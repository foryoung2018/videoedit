package com.wmlive.hhvideo.heihei.beans.personal;

import java.io.Serializable;

/**
 * 图片的bean
 */
public class Photo implements Serializable {

    private int id;//id
    private String path;  //路径
    private boolean isCamera;//是否为相机

    public Photo(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setIsCamera(boolean isCamera) {
        this.isCamera = isCamera;
    }
}