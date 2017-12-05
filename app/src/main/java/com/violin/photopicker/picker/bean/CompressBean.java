package com.violin.photopicker.picker.bean;

/**
 * Created by whl on 2016/12/28.
 * 上传的图片的实体类
 */

public class CompressBean {
    private int widhth;//图片宽度
    private int height;//图片高度
    private String path;//图片路径
    private String key;
    private double size;

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CompressBean(int widhth, int height, String path){
        this.widhth=widhth;
        this.height=height;
        this.path=path;
    }

    public CompressBean(){

    }

    public int getWidhth() {
        return widhth;
    }

    public void setWidhth(int widhth) {
        this.widhth = widhth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CompressBean{" +
                "widhth=" + widhth +
                ", height=" + height +
                ", path='" + path + '\'' +
                '}';
    }
}
