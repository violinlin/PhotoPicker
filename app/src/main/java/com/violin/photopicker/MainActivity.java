package com.violin.photopicker;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.violin.photopicker.picker.utils.CompressUtil;
import com.violin.photopicker.picker.utils.PhotoUtil;
import com.violin.photopicker.picker.utils.PickMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
                    compressPhotos(v, dialog);
                } else {
                    Toast.makeText(v.getContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera(v);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        photoAdapter = new PhotoAdapter(this);
        recyclerView.setAdapter(photoAdapter);
    }

    /**
     * 压缩图片
     *
     * @param v
     * @param dialog
     */
    private void compressPhotos(final View v, final ProgressDialog dialog) {
        rx.Observable
                .just(mPhotos.get(0))
                .map(new Func1<String, File>() {
                    @Override
                    public File call(String s) {
                        return new File(s);
                    }
                })
                .map(new Func1<File, CompressBean>() {
                    @Override
                    public CompressBean call(File file) {
                        return new CompressUtil(v.getContext(), file).compress();
                    }
                }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CompressBean>() {
                    @Override
                    public void onCompleted() {
                        dialog.cancel();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.cancel();
                    }

                    @Override
                    public void onNext(CompressBean compressBean) {
                        Log.d("whl", compressBean.toString());
                        Log.d("whl", "time" + (System.currentTimeMillis() - startTime));
                    }
                });

    }

    private void showCamera(View view) {

        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("whl", "请求获取权限");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {

                Snackbar.make(view, "请求获取打开相册权限", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CAMERA}, 100);
                            }
                        }).show();


            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, 100);

            }


        } else {
            Log.i("whl", "获取了相机权限");

            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1001);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("whl", permissions[0] + "----" + grantResults[0]);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1001);
            } else {
                Snackbar.make(getWindow().getDecorView(), "获取权限被拒绝", Snackbar.LENGTH_SHORT)
                        .show();
            }

        }

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
