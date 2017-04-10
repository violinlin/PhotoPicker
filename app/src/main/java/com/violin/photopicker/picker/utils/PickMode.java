package com.violin.photopicker.picker.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whl on 2016/12/19.
 */

public class PickMode implements Parcelable {
    public static final int RESULT_CODE = 3;//返回code
    public static final int REQUEST_CODE = 4;//请求code
    public static final String PICK_MODE = "pick_mode";//用户接受pickmode对象
    public static final String PICK_DATA = "pick_data";//用于接受返回字段
    public static final int SINGLE_MODE = 1;//单选模式
    public static final int MULTY_MODE = 2;//多选模式

    public static final int MODE_HAS_CAMERA = 1;//是否包含拍照键
    public static final int MODE_NO_CAMERA = 2;//是否包含拍照键
    private int mode;//默认多选模式
    private int count;//默认选取9张
    private int hasCamera = MODE_HAS_CAMERA;

    public PickMode(int mode, int count,int cameraMode) {
        this.mode = mode;
        this.count = count;
        this.hasCamera=cameraMode;
    }

    protected PickMode(Parcel in) {
        mode = in.readInt();
        count = in.readInt();
        hasCamera=in.readInt();
    }

    public static final Creator<PickMode> CREATOR = new Creator<PickMode>() {
        @Override
        public PickMode createFromParcel(Parcel in) {
            return new PickMode(in);
        }

        @Override
        public PickMode[] newArray(int size) {
            return new PickMode[size];
        }
    };

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHasCamera() {
        return hasCamera;
    }

    public void setHasCamera(int hasCamera) {
        this.hasCamera = hasCamera;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mode);
        dest.writeInt(count);
        dest.writeInt(hasCamera);
    }
}
