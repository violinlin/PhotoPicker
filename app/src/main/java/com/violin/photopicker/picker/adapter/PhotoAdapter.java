package com.violin.photopicker.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.violin.photopicker.R;
import com.violin.photopicker.picker.bean.PhotoBean;
import com.violin.photopicker.picker.utils.PickMode;
import com.violin.photopicker.picker.view.PhotoItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by whl on 2016/12/14.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private Context mContext;
    private List<PhotoBean> photoBeanList;
    private PhotoItemView.Listener listener;
    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setHasCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public boolean isHasCamera() {
        return hasCamera;
    }

    private boolean hasCamera = true;//是否包含拍照键

    public PhotoAdapter(Context context) {
        this.mContext = context;
        photoBeanList = new ArrayList<>();
    }

    public List<PhotoBean> getData() {
        return photoBeanList;
    }


    public void updateData(List<PhotoBean> cBeens) {
        photoBeanList.clear();
        photoBeanList.addAll(cBeens);
        if (hasCamera) {
            PhotoBean cameraBeen = new PhotoBean("");
            cameraBeen.setCamera(true);
            photoBeanList.add(0, cameraBeen);
        }
        notifyDataSetChanged();
    }

    public void addItemData(PhotoBean photoBean) {
        int position = 0;
        if (hasCamera) {
            position = 1;
        }
        photoBeanList.add(position, photoBean);
        notifyDataSetChanged();
    }

    public void setListener(PhotoItemView.Listener listener) {
        this.listener = listener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PhotoItemView itemView = new PhotoItemView(mContext);
        itemView.setListener(listener);

        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoItemView itemView = (PhotoItemView) holder.itemView;
        itemView.setTag(position);
        holder.checkBox.setTag(position);
        itemView.setData(photoBeanList.get(position), getMode() == PickMode.SINGLE_MODE ? true : false);

    }

    @Override
    public int getItemCount() {
        return photoBeanList == null ? 0 : photoBeanList.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }


}
