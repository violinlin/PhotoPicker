package com.violin.photopicker.picker.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;


/**
 * Created by whl on 2016/11/1.
 */

public class ImageUtil {

    //通过图片路径加载图片
    public static void loadImageWithFile(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(new File(path))
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做缓存处理
                .into(imageView);

    }


}
