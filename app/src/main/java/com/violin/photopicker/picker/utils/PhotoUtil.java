package com.violin.photopicker.picker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.violin.photopicker.picker.PhotoPickerActivity;
import com.violin.photopicker.picker.bean.PhotoBean;
import com.violin.photopicker.picker.bean.PhotoFolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by whl on 2016/12/14.
 * 查找相片.jpeg,png
 */

public class PhotoUtil {
    public static String TAG_SCALE = "scale";//图片缩放前缀
    public static String TAG_COMPRESS = "compress";//图片压缩前缀
    public static String TAG_ORIGIN = "gl";//拍照原图前缀


    //    获取图片的缓存目录
    public static File getTempFile(Context context, String pres) {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String timeStamp = String.valueOf(new Date().getTime());
            File pFile = new File(Environment.getExternalStorageDirectory() + File.separator + "gamelink");
            if (!pFile.exists()) {
                pFile.mkdirs();
            }
            file = new File(pFile, pres + timeStamp + ".jpg");
        } else {
            File cacheDir = context.getCacheDir();
            String timeStamp = String.valueOf(new Date().getTime());
            file = new File(cacheDir, pres + timeStamp + ".jpg");
        }

        return file;

    }


    //查询手机中的图片
    public Map<String, PhotoFolderBean> getPhotos(Context context) {
        Map<String, PhotoFolderBean> folderMap = new HashMap<>();

        String allPhotosKey = PhotoPickerActivity.ALL_PHOTOS;
        PhotoFolderBean allFolder = new PhotoFolderBean();
        allFolder.setName(allPhotosKey);
        allFolder.setDirPath(allPhotosKey);
        allFolder.setPhotoList(new ArrayList<PhotoBean>());
        folderMap.put(allPhotosKey, allFolder);

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//获取图片的URI:SD卡上的图片内容
        ContentResolver mContentResolver = context.getContentResolver();
        Log.d("whl",imageUri.toString());
        // 只查询jpeg和png的图片
        /**
         * @param1 查询内容的URI, 这里是获取外部存储的所有图片内容
         * @param2 查询返回的列, null表示返回所有的列
         * @param3 查询返回行的筛选条件
         * @param4 替换参数三中的?占位符
         * @param5 返回行的排序方式 desc倒叙排列
         */
        Cursor mCursor = mContentResolver.query(imageUri, null,
                MediaStore.Images.Media.MIME_TYPE + " in(?, ?)",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
//      获取图片路径字段所在的列
        int pathIndex = mCursor
                .getColumnIndex(MediaStore.Images.Media.DATA);

        if (mCursor.moveToFirst()) {
            do {
                // 获取图片的路径
                String path = mCursor.getString(pathIndex);

                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();

                if (folderMap.containsKey(dirPath)) {
                    PhotoBean photo = new PhotoBean(path);
                    PhotoFolderBean photoFolder = folderMap.get(dirPath);
                    photoFolder.getPhotoList().add(photo);
                    folderMap.get(allPhotosKey).getPhotoList().add(photo);
                } else {
                    PhotoFolderBean photoFolder = new PhotoFolderBean();
                    List<PhotoBean> photoList = new ArrayList<>();
                    PhotoBean photo = new PhotoBean(path);
                    photoList.add(photo);
                    photoFolder.setPhotoList(photoList);
                    photoFolder.setDirPath(dirPath);
                    photoFolder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    folderMap.put(dirPath, photoFolder);
                    folderMap.get(allPhotosKey).getPhotoList().add(photo);
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return folderMap;
    }

}
