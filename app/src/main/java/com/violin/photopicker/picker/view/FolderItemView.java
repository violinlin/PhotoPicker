package com.violin.photopicker.picker.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.violin.photopicker.R;
import com.violin.photopicker.picker.bean.PhotoFolderBean;
import com.violin.photopicker.picker.utils.ImageUtil;
import com.violin.photopicker.picker.utils.Util;


/**
 * Created by whl on 2016/12/15.
 */

public class FolderItemView extends LinearLayout implements View.OnClickListener {

    private ImageView imageview;
    private TextView folderNameTV;
    private TextView folderCountTV;
    private RadioButton radioButton;

    public FolderItemView(Context context) {
        this(context, null);
    }

    public FolderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.pp_photofloder_item_view, this);
        LayoutParams params = new LayoutParams(context.getResources().getDisplayMetrics().widthPixels, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setOrientation(HORIZONTAL);
        setPadding(Util.dip2px(context, 10), Util.dip2px(context, 5), Util.dip2px(context, 10), Util.dip2px(context, 5));
        setBackgroundColor(Color.parseColor("#ffffff"));
        setGravity(Gravity.CENTER_VERTICAL);
        initView();
    }

    private void initView() {
        imageview = (ImageView) findViewById(R.id.imageview);
        folderNameTV = (TextView) findViewById(R.id.tv_folder_name);
        folderCountTV = (TextView) findViewById(R.id.tv_folder_count);
        radioButton = (RadioButton) findViewById(R.id.radiobutton);
        this.setOnClickListener(this);
        radioButton.setOnClickListener(this);
    }

    public void setData(PhotoFolderBean bean) {
        if (bean.getPhotoList().size() > 0) {
            folderCountTV.setText(bean.getPhotoList().size() + "张");
            folderNameTV.setText(bean.getName());
            radioButton.setChecked(bean.isSelected());
            ImageUtil.loadImageWithFile(getContext(), bean.getPhotoList().get(0).getPath(), imageview);
        }
    }

    private FolderItemView.Listener listener;

    public void setListener(FolderItemView.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onFolderClick(v);
        }

    }

    public interface Listener {
        void onFolderClick(View view);
    }
}
