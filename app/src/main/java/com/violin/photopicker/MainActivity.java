package com.violin.photopicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.violin.photopicker.picker.PhotoPickerActivity;
import com.violin.photopicker.picker.utils.PickMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickMode pickMode = new PickMode(PickMode.SINGLE_MODE, 1, PickMode.MODE_HAS_CAMERA);
                PhotoPickerActivity.launch(MainActivity.this, pickMode);
            }
        });
    }
}
