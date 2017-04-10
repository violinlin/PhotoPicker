package com.violin.photopicker.picker.bean;

import java.util.List;

/**
 * Created by whl on 2016/12/14.
 * 相册实体类
 */

public class PhotoFolderBean {
    /* 文件夹名 */
    private String name;
    /* 文件夹路径 */
    private String dirPath;
    /* 该文件夹下图片列表 */
    private List<PhotoBean> photoList;
    /* 标识是否选中该文件夹 */
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public List<PhotoBean> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<PhotoBean> photoList) {
        this.photoList = photoList;
    }
}
