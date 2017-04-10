package com.violin.photopicker.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.violin.photopicker.R;
import com.violin.photopicker.picker.bean.PhotoFolderBean;
import com.violin.photopicker.picker.view.FolderItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by whl on 2016/12/14.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private Context mContext;
    private List<PhotoFolderBean> folderBeens;
    private FolderItemView.Listener listener;

    public FolderAdapter(Context context) {
        this.mContext = context;
        folderBeens = new ArrayList<>();
    }

    public void updateData(List<PhotoFolderBean> beens) {
        folderBeens.clear();
        folderBeens.addAll(beens);
        notifyDataSetChanged();
    }

    public void setListener(FolderItemView.Listener listener) {
        this.listener = listener;

    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FolderItemView itemView = new FolderItemView(parent.getContext());
        itemView.setListener(listener);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        FolderItemView itemView = (FolderItemView) holder.itemView;
        itemView.setData(folderBeens.get(position));
        itemView.setTag(position);
        holder.radioButton.setTag(position);

    }

    @Override
    public int getItemCount() {
        return folderBeens == null ? 0 : folderBeens.size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        public RadioButton radioButton;

        public FolderViewHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.radiobutton);
        }
    }
}
