package com.violin.photopicker.picker.bean;

/**
 * Created by whl on 2016/12/14.
 * 存储照片的实体类
 */

public class PhotoBean {
    private String path;  //路径
    private boolean isSelected;
    private boolean isCamera;//是否是拍照键

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public PhotoBean(String path) {
        this.path = path;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
