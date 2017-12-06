package com.violin.photopicker.picker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.violin.photopicker.picker.bean.CompressBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by whl on 2016/12/20.
 * <p>
 * 参考鲁班算法  https://github.com/Curzibn/Luban/blob/master/DESCRIPTION.md
 */

public class CompressUtil {

    private File mDesFile;
    private File mSrcFile;

    private long MAX_SIZE = 1 * 1024 * 1024;


    /**
     * 源文件
     *
     * @param context 上下文
     * @param srcFile 源文件
     */
    public CompressUtil(Context context, File srcFile) {

        this.mSrcFile = srcFile;
        String name = srcFile.getName();
        this.mDesFile = getTempFile(context, name);


    }

    public CompressBean compress() {
        CompressBean compressBean = null;

        String srcPath = mSrcFile.getAbsolutePath();

//        加载图片，获取图片的宽高
        BitmapFactory.Options whOptions = new BitmapFactory.Options();
        whOptions.inJustDecodeBounds = true;
        whOptions.inSampleSize = 1;
        BitmapFactory.decodeFile(srcPath, whOptions);
        Log.d("whl", "compress--src--w:" + whOptions.outWidth + "h:" + whOptions.outHeight);
//          根据图片尺寸计算图片的压缩率
        BitmapFactory.Options reSizeOptions = new BitmapFactory.Options();

        reSizeOptions.inSampleSize = computeSize(whOptions.outWidth, whOptions.outHeight);

        Log.d("whl", "resize" + reSizeOptions.inSampleSize);

//        如果原图片不用缩放尺寸 且不超过最大限制，直接返回原图
        if (reSizeOptions.inSampleSize == 1 && mSrcFile.length() <= MAX_SIZE) {
            compressBean = new CompressBean();
            compressBean.setPath(mSrcFile.getAbsolutePath());
            compressBean.setSize(mSrcFile.length());
            compressBean.setWidhth(whOptions.outWidth);
            compressBean.setHeight(whOptions.outHeight);

        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, reSizeOptions);
            try {
                bitmap = rotatingImage(bitmap);

                compressBean = compressByQuality(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        return compressBean;


    }

    /**
     * 压缩图片质量
     */
    private CompressBean compressByQuality(Bitmap bitmap) {

        Log.d("whl", "compressByQuality-----");
        CompressBean bean = new CompressBean();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        while (stream.toByteArray().length > MAX_SIZE && options > 50) {
            stream.reset();
            options -= 10;
            Log.d("whl", options + "options");
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }

        bean.setWidhth(bitmap.getWidth());
        bean.setHeight(bitmap.getHeight());

        bean.setPath(mDesFile.getAbsolutePath());
        bitmap.recycle();
        try {
            FileOutputStream fos = new FileOutputStream(mDesFile);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();
            bean.setSize(mDesFile.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bean;

    }

    /**
     * @param srcWidth  源图片宽度
     * @param srcHeight 原图片高度
     * @return
     */
    private int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide >= 1664 && longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    /***
     *  旋转屏幕角度
     * @param bitmap
     * @return
     */
    private Bitmap rotatingImage(Bitmap bitmap) throws IOException {


        ExifInterface srcExif = new ExifInterface(mSrcFile.getAbsolutePath());

        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d("whl", "rotatingImage--" + orientation);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public static final String prefix = "compress_";// 压缩文件前缀

    //    获取图片的缓存目录
    public static File getTempFile(Context context, String name) {
        File file;
        String desName = prefix + name;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File pFile = new File(Environment.getExternalStorageDirectory() + File.separator + "photopicker");
            if (!pFile.exists()) {
                pFile.mkdirs();
            }
            file = new File(pFile, desName);
        } else {
            File cacheDir = context.getCacheDir();
            file = new File(cacheDir, desName);
        }

        return file;

    }

}
