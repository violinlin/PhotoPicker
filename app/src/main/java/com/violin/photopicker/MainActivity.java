package com.violin.photopicker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.violin.photopicker.picker.PhotoPickerActivity;
import com.violin.photopicker.picker.bean.CompressBean;
import com.violin.photopicker.picker.utils.CompressUtils;
import com.violin.photopicker.picker.utils.ImageUtil;
import com.violin.photopicker.picker.utils.PickMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mPhotos;
    private PhotoAdapter photoAdapter;
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotos = new ArrayList<>();
        findViewById(R.id.single_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickMode pickMode = new PickMode(PickMode.SINGLE_MODE, 1, PickMode.MODE_HAS_CAMERA);
                PhotoPickerActivity.launch(MainActivity.this, pickMode);
            }
        });
        findViewById(R.id.multiple_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickMode pickMode = new PickMode(PickMode.MULTY_MODE, 9, PickMode.MODE_HAS_CAMERA);
                PhotoPickerActivity.launch(MainActivity.this, pickMode);
            }
        });

        findViewById(R.id.multiple_no_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickMode pickMode = new PickMode(PickMode.MULTY_MODE, 9, PickMode.MODE_NO_CAMERA);
                PhotoPickerActivity.launch(MainActivity.this, pickMode);
            }
        });

        Button compressButton = (Button) findViewById(R.id.compress);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("图片压缩中");


        compressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotos != null && mPhotos.size() > 0) {
                    dialog.show();
                    startTime = System.currentTimeMillis();
                    new CompressUtils(MainActivity.this).getCompressBeans(mPhotos).setListener(new CompressUtils.Listener() {
                        @Override
                        public void onCompressComplete(List<CompressBean> been, float parent) {
                            dialog.setProgress((int) parent * 100);
                            Log.d("whl", "parent" + parent);
                            if (parent == 1) {
                                Log.d("whl", "time" + (System.currentTimeMillis() - startTime));
                                dialog.cancel();
                            }

                        }
                    });
                } else {
                    Toast.makeText(v.getContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        photoAdapter = new PhotoAdapter(this);
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickMode.REQUEST_CODE && resultCode == PickMode.RESULT_CODE) {
            mPhotos.clear();
            mPhotos = data.getStringArrayListExtra(PickMode.PICK_DATA);
            if (mPhotos != null && mPhotos.size() > 0) {
                photoAdapter.setData(mPhotos);
            }
        }
    }
}
