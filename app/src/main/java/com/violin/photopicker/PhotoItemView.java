package com.violin.photopicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.violin.photopicker.picker.utils.ImageUtil;
import com.violin.photopicker.picker.utils.Util;


/**
 * Created by whl on 2016/12/22.
 */

public class PhotoItemView extends FrameLayout {

    private ImageView photoIV;
    private ImageView delete;

    public PhotoItemView(Context context) {
        this(context, null);
    }

    public PhotoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.main_post_photo_view, this);
        int padding = Util.dip2px(getContext(), 3);
        setPadding(padding, padding, padding, padding);
        initView();
    }

    private void initView() {
        photoIV = (ImageView) findViewById(R.id.imageview);
        delete = (ImageView) findViewById(R.id.delete);

    }
    public ImageView getDeleteView(){
        return delete;
    }
    public ImageView getPhotoIV(){
        return photoIV;
    }


    public void setData(String photo) {
        ImageUtil.loadImageWithFile(getContext(), photo, photoIV);

    }

    public void setData(int photoRes) {
        delete.setVisibility(GONE);
        photoIV.setImageResource(photoRes);
    }


}
