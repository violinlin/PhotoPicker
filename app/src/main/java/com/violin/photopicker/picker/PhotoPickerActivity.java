package com.violin.photopicker.picker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.violin.photopicker.R;
import com.violin.photopicker.picker.adapter.FolderAdapter;
import com.violin.photopicker.picker.adapter.PhotoAdapter;
import com.violin.photopicker.picker.bean.PhotoBean;
import com.violin.photopicker.picker.bean.PhotoFolderBean;
import com.violin.photopicker.picker.utils.PhotoUtil;
import com.violin.photopicker.picker.utils.PickMode;
import com.violin.photopicker.picker.utils.Util;
import com.violin.photopicker.picker.view.FolderItemView;
import com.violin.photopicker.picker.view.PhotoItemView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PhotoPickerActivity extends Activity {
    private Map<String, PhotoFolderBean> mFolderMap;
    private PhotoAdapter photoAdapter;
    public static final String ALL_PHOTOS = "所有图片";
    private ArrayList<String> seletedPhotos = new ArrayList<>();
    private TextView folderTV;
    private RelativeLayout bottomLayout;
    private FolderAdapter folderAdapter;
    private PopupWindow popupWindow;
    private RecyclerView pRecyclerView;
    private TextView countTV;
    private RelativeLayout headerLayout;
    private TextView nextTV;
    public final int PICK_PHOTO_FROM_CAMERA = 1;//调取相机界面请求code
    private final int CROP_PHOTO = 2;//图片裁剪
    private File tempFile;//图片存储路径
    private PickMode pickMode;
    private String TEMP = "temp";

    /**
     * 启动器分为fragment或activity
     *
     * @param activity
     * @param pickMode
     */
    //界面启动器
    public static void launch(Activity activity, PickMode pickMode) {
        Intent intent = new Intent(activity, PhotoPickerActivity.class);
        intent.putExtra(PickMode.PICK_MODE, pickMode);
        activity.startActivityForResult(intent, PickMode.REQUEST_CODE);
    }

    //界面启动器
    public static void launch(Fragment fragment, PickMode pickMode) {
        Intent intent = new Intent(fragment.getContext(), PhotoPickerActivity.class);
        intent.putExtra(PickMode.PICK_MODE, pickMode);
        fragment.startActivityForResult(intent, PickMode.REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_photo_picker_activity);

        if (savedInstanceState != null) {
            tempFile = new File(savedInstanceState.getString(TEMP));
        }
        pickMode = getIntent().getParcelableExtra(PickMode.PICK_MODE);
        if (pickMode == null) {
            pickMode = new PickMode(PickMode.MULTY_MODE, 9, PickMode.MODE_HAS_CAMERA);
        }
        initView();
        initData();
    }

    //初始化组件
    private void initView() {
        TextView backTV = (TextView) findViewById(R.id.tv_back);
        backTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nextTV = (TextView) findViewById(R.id.tv_next);
        nextTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seletedPhotos.size() > 0) {
                    setResultData();
                } else {
                    Toast.makeText(getBaseContext(), "请先选择相片", Toast.LENGTH_SHORT).show();
                }
            }
        });


        pRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        pRecyclerView.setLayoutManager(manager);
        pRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = Util.dip2px(getBaseContext(), 1);
                outRect.left = outRect.right = Util.dip2px(getBaseContext(), 1);
            }
        });
        photoAdapter = new PhotoAdapter(this);
        photoAdapter.setHasCamera(pickMode.getHasCamera() == PickMode.MODE_HAS_CAMERA ? true : false);
        photoAdapter.setMode(pickMode.getMode());
        photoAdapter.setListener(new PhotoItemView.Listener() {
            @Override
            public void onPhotoItemClick(View view) {
                int position = (int) view.getTag();
                if (photoAdapter.isHasCamera()) {
                    if (position == 0) {
                        if (photosEnoughToast()) {
                            return;
                        }
                        takePhoto();

                    } else {
                        pickPhotoOperate(position);
                    }

                } else {
                    pickPhotoOperate(position);
                }


            }
        });

        pRecyclerView.setAdapter(photoAdapter);

        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        headerLayout = (RelativeLayout) findViewById(R.id.header_layout);
        folderTV = (TextView) findViewById(R.id.tv_folder);
        countTV = (TextView) findViewById(R.id.tv_count);
        folderTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoAdapter.getData().size() > 1) {//相册是否有相片
                    showFloderPanel();
                }

            }
        });

        loadPhotoData();

    }

    //    初始化相册文件列表
    private void initFolderPanel() {
        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(width);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopAnimStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.pp_photofloder_layout, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.getHeight();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(folderAdapter);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(view);

    }

    //显示相册列表
    private void showFloderPanel() {

        if (popupWindow != null && folderAdapter != null) {
            folderAdapter.notifyDataSetChanged();
            //测量recuclerview的高度
            int intw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int inth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            popupWindow.getContentView().measure(intw, inth);
            int ph = popupWindow.getContentView().getMeasuredHeight();
            int height = (int) Math.min(ph, getResources().getDisplayMetrics().heightPixels * 0.6);
            popupWindow.setHeight(height);
            popupWindow.showAsDropDown(bottomLayout, 0, -bottomLayout.getHeight() - height);
        }

    }




    //拍照处理
    private void takePhoto() {
        try {
            tempFile = PhotoUtil.getTempFile(this, PhotoUtil.TAG_ORIGIN);
            Intent cIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            if (cIntent.resolveActivity(getPackageManager()) != null) {
                cIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(cIntent, PICK_PHOTO_FROM_CAMERA);
            } else {
                Toast.makeText(this, "没找到摄像头", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //图片缩放处理
    private void startPhotoZoom(Uri uri) {
        tempFile = PhotoUtil.getTempFile(this, PhotoUtil.TAG_SCALE);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        intent.putExtra("output", Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", false);//是否返回Bitmap
        startActivityForResult(intent, CROP_PHOTO);
    }

    //选取照片
    private void pickPhotoOperate(int position) {
        PhotoBean bean = photoAdapter.getData().get(position);
        if (pickMode.getMode() == PickMode.MULTY_MODE) {

            if (seletedPhotos.contains(bean.getPath())) {
                bean.setSelected(false);
                seletedPhotos.remove(bean.getPath());
            } else {
                if (photosEnoughToast()) {
                    return;
                }
                bean.setSelected(true);
                seletedPhotos.add(bean.getPath());
            }
            updateSelectCount();

            photoAdapter.notifyItemChanged(position);
        } else if (pickMode.getMode() == PickMode.SINGLE_MODE) {
            startPhotoZoom(Uri.fromFile(new File(bean.getPath())));
        }


    }

    //更改当前选中的相片数目
    private void updateSelectCount() {
        countTV.setText(photoAdapter.getData().size() - 1 + "张");
        nextTV.setText(seletedPhotos.size() > 0 ? "(" + seletedPhotos.size() + "/" + pickMode.getCount() + ")下一步" : "下一步");
    }

    //图片是否超过上限
    private boolean photosEnoughToast() {
        if (seletedPhotos.size() >= pickMode.getCount()) {
            Toast.makeText(getBaseContext(), "超过最大选择张数", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    //    加载图片
    private void loadPhotoData() {
        Observable.create(new Observable.OnSubscribe<Map<String, PhotoFolderBean>>() {
            @Override
            public void call(Subscriber<? super Map<String, PhotoFolderBean>> subscriber) {
                PhotoUtil photoUtil = new PhotoUtil();
                subscriber.onNext(photoUtil.getPhotos(PhotoPickerActivity.this));

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, PhotoFolderBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {


                    }

                    @Override
                    public void onNext(Map<String, PhotoFolderBean> photoMap) {

                        setData(photoMap);
                    }
                });

    }

    //    初始化数据
    private void initData() {
        mFolderMap = new HashMap<>();
        folderAdapter = new FolderAdapter(this);


    }

    //设置数据
    private void setData(final Map<String, PhotoFolderBean> photoMap) {
        mFolderMap.clear();
        mFolderMap.putAll(photoMap);
        photoAdapter.updateData(mFolderMap.get(ALL_PHOTOS).getPhotoList());
        final List<PhotoFolderBean> folderBeanList = new ArrayList<>();
        Set<String> folderNameSet = photoMap.keySet();
        for (String s : folderNameSet) {
            folderBeanList.add(photoMap.get(s));
        }
        folderAdapter.updateData(folderBeanList);
        folderAdapter.setListener(new FolderItemView.Listener() {
            @Override
            public void onFolderClick(View view) {
                for (PhotoFolderBean photoFolderBean : folderBeanList) {
                    photoFolderBean.setIsSelected(false);
                }
                int position = (int) view.getTag();
                PhotoFolderBean bean = folderBeanList.get(position);
                photoAdapter.updateData(bean.getPhotoList());
                bean.setIsSelected(true);
                pRecyclerView.scrollToPosition(0);
                popupWindow.dismiss();
                countTV.setText(photoAdapter.getData().size() - 1 + "张");
                folderTV.setText(bean.getName());
            }
        });
        countTV.setText(photoAdapter.getData().size() - 1 + "张");
        initFolderPanel();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tempFile != null) {
            outState.putString(TEMP, tempFile.getAbsolutePath());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        当没拍照或没裁剪,返回值!=RESULT_OK
        if (resultCode != RESULT_OK) {
            return;
        }

        if (tempFile == null) {
            return;
        }

        if (tempFile.exists()) {
//            发送广播添加图片
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + tempFile.getAbsolutePath())));
        }
        switch (requestCode) {
            case PICK_PHOTO_FROM_CAMERA:

                if (pickMode.getMode() == PickMode.SINGLE_MODE) {
                    startPhotoZoom(Uri.fromFile(tempFile));
                } else if (pickMode.getMode() == PickMode.MULTY_MODE) {
                    PhotoBean bean = new PhotoBean(tempFile.getAbsolutePath());
                    bean.setSelected(true);
                    photoAdapter.addItemData(bean);
                    seletedPhotos.add(tempFile.getAbsolutePath());
                    updateSelectCount();

                }

                break;
            case CROP_PHOTO:
                if (pickMode.getMode() == PickMode.SINGLE_MODE) {
                    seletedPhotos.add(tempFile.getAbsolutePath());
                    setResultData();
                }
                break;


        }

    }


    private void setResultData() {

        Intent intent = new Intent();
        intent.putStringArrayListExtra(PickMode.PICK_DATA, seletedPhotos);
        setResult(PickMode.RESULT_CODE, intent);
        PhotoPickerActivity.this.finish();
    }


}
