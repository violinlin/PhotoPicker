package com.violin.photopicker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by whl on 2016/12/22.
 * <p>
 * 发布图片的适配器
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private ArrayList<String> mPhotos;
    private Context mContext;

    public void setUpPhotos(List<String> upPhotos) {
    }

    public PhotoAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<String> photos) {
        if (photos != null) {
            this.mPhotos = photos;
            notifyDataSetChanged();
        }
    }

    public void deleteData(int position) {
        if (mPhotos != null) {
            mPhotos.remove(position);
            notifyDataSetChanged();
        }


    }

    public void clearData() {
        this.mPhotos.clear();
        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return this.mPhotos;

    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PhotoItemView itemView = new PhotoItemView(mContext);
        itemView.setLayoutParams(new FrameLayout.LayoutParams(parent.getResources().getDisplayMetrics().widthPixels / 3, parent.getResources().getDisplayMetrics().widthPixels / 3));
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoItemView itemView = (PhotoItemView) holder.itemView;
        holder.deleteIV.setTag(position);
        holder.itemView.setTag(position);
        itemView.setData(mPhotos.get(position));

    }

    @Override
    public int getItemCount() {
        return mPhotos==null?0:mPhotos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoIV;
        public ImageView deleteIV;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            deleteIV = (ImageView) itemView.findViewById(R.id.delete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = (int) v.getTag();

                }
            });
            deleteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    deleteData(position);

                }
            });
        }
    }
}

