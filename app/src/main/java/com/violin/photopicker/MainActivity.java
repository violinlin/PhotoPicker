package com.violin.photopicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.violin.photopicker.picker.PhotoPickerActivity;
import com.violin.photopicker.picker.utils.PickMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mPhotos;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            mPhotos = data.getStringArrayListExtra(PickMode.PICK_DATA);
            if (mPhotos!=null&&mPhotos.size()>1){
                photoAdapter.setData(mPhotos);
            }
        }
    }
}
