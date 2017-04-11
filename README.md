# PhotoPicker
从手机相册中选取图片,支持多选和单选,单选时支持裁剪

## 获取手机中的图片
> 通过ContentResolver获取SD卡上的图片数据，需要在清单文件中添加`<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`权限（后面会有拍照处理，所以直接申请写权限了）

```java
  Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//获取图片的URI:SD卡上的图片内容
        ContentResolver mContentResolver = context.getContentResolver();

        // 只查询jpeg和png的图片
        /**
         * @param1 查询内容的URI, 这里是获取外部存储的所有图片内容
         * @param2 查询返回的列, null表示返回所有的列
         * @param3 查询返回行的筛选条件
         * @param4 替换参数三中的?占位符
         * @param5 返回行的排序方式 desc倒叙排列
         */
        Cursor mCursor = mContentResolver.query(imageUri, null,
                MediaStore.Images.Media.MIME_TYPE + " in(?, ?)",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
```
## 通过相机拍摄照片
> 通过隐式意图跳转拍照界面，设置`MediaStore.EXTRA_OUTPU`字段，拍照后图片的存储路径。在`onActivityResult()`中接受回调信息

```java
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
```

## 图片的裁剪
> 通过隐式意图跳转裁剪界面，参数为要裁剪图片的路径Uri。同拍照处理，在`onActivityResult`中获取返回信息
```java
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
```
