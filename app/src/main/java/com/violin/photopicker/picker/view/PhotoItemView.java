package com.violin.photopicker.picker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.violin.photopicker.R;
import com.violin.photopicker.picker.bean.PhotoBean;
import com.violin.photopicker.picker.utils.ImageUtil;


/**
 * Created by whl on 2016/12/14.
 * 图片选择子视图
 */

public class PhotoItemView extends FrameLayout implements View.OnClickListener {

    private ImageView imageView;
    private CheckBox checkBox;
    private View maskView;
    private PhotoBean photoBean;
    private LinearLayout cameraLayout;
    private FrameLayout photoLayout;

    public PhotoItemView(Context context) {
        this(context, null);
    }

    public PhotoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.pp_photopicker_item_view, this);
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageview);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        maskView = findViewById(R.id.mask);
        checkBox.setOnClickListener(this);
        cameraLayout = (LinearLayout) findViewById(R.id.camera_layout);
        photoLayout = (FrameLayout) findViewById(R.id.photo_layout);
        this.setOnClickListener(this);
    }

    public void setData(PhotoBean photoBean, boolean isSingle) {
        this.photoBean = photoBean;
        checkBox.setVisibility(isSingle ? GONE : VISIBLE);
        if (photoBean.isCamera()) {
            cameraLayout.setVisibility(VISIBLE);
            photoLayout.setVisibility(GONE);
        } else {
            cameraLayout.setVisibility(GONE);
            photoLayout.setVisibility(VISIBLE);
            ImageUtil.loadImageWithFile(getContext(), photoBean.getPath(), imageView);
            if (photoBean.isSelected()) {
                maskView.setVisibility(VISIBLE);
                checkBox.setChecked(true);
            } else {
                maskView.setVisibility(GONE);
                checkBox.setChecked(false);
            }
        }


    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;

    }


    @Override
    public void onClick(View v) {
        listener.onPhotoItemClick(v);
    }

    public interface Listener {
        void onPhotoItemClick(View v);
    }
}
