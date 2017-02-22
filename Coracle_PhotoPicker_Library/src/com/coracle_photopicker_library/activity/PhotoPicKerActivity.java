package com.coracle_photopicker_library.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import com.coracle_photopicker_library.R;
import com.coracle_photopicker_library.activity.ListImageDirPopupWindow.OnImageDirSelected;
import com.coracle_photopicker_library.adapter.PhotoAdapter;
import com.coracle_photopicker_library.adapter.PhotoAdapter.PhotoPickerListenner;
import com.coracle_photopicker_library.entity.ImageFloder;
import com.coracle_photopicker_library.utils.FilePathUtils;
import com.coracle_photopicker_library.utils.PhotoConstancts;
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PhotoPicKerActivity extends Activity implements OnClickListener {

    private static final int REQUEST_CAMERA = 101;

    private ProgressDialog mProgressDialog;

    private Context mContext;
    private GridView mPhotoGridView;
    private TextView mFolderName;
    private TextView mFolderCount;
    private Button mSubmitBT;
    private int mScreenHeight;
    private int mAllCount;
    private boolean mIsCamera;
    private File mTempFile;
    private PhotoAdapter mPhotoAdapter;
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
    /**
     * 所有的图片
     */
    private List<String> mImgs = new ArrayList<String>();
    /**
     * 除所有图片之后的选项选择的图片
     */
    private List<String> mFolderImgs = new ArrayList<String>();

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            setAdapter(mImageFloders.get(0));
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        mContext = this;
        Intent intent = getIntent();
        // 是否带拍照，默认带
        mIsCamera = intent.getBooleanExtra(PhotoConstancts.PHOTO_IS_SHOW_CAMERA, true);
        // 限制个数, 如果为1，则是单选
        mAllCount = intent.getIntExtra(PhotoConstancts.PHOTO_NUM, 1);
        // 初始化View
        initView();
        initImages();
    }

    private void initImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(mContext, null, "正在加载...");
        // 开始线程获取图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mContext.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                String firstImage = null;
                int totalCount = 0;
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);
                }
                mCursor.close();

                // 在这里把“所以文件夹”选项加上，并计算所有图片
                int len = mImageFloders.size();
                for (int i = 0; i < len; i++) {
                    ImageFloder floder = mImageFloders.get(i);
                    File imgDir = new File(floder.getDir());
                    List<String> imgs = Arrays.asList(imgDir.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }));
                    for (String img : imgs) {
                        mImgs.add(imgDir.getAbsolutePath() + "/" + img);
                    }
                }
                if (mIsCamera) {
                    mImgs.add(0, PhotoConstancts.CAMERA_TAG);
                }
                ImageFloder floder = new ImageFloder();
                floder.setName("所有图片");
                floder.setDir("all"); // 表示所有图片
                floder.setFirstImagePath(firstImage);
                floder.setCount(totalCount);
                mImageFloders.add(0, floder);

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext()).inflate(R.layout.photo_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(new OnImageDirSelected() {

            @Override
            public void selected(ImageFloder floder) {
                setAdapter(floder);
                mListImageDirPopupWindow.dismiss();
            }
        });
    }

    private void setAdapter(ImageFloder floder) {
        if ("all".equals(floder.getDir())) {
            mPhotoAdapter = new PhotoAdapter(mContext, mImgs, R.layout.photo_grid_item, mIsCamera, mAllCount,
                    pickerListenner);
        } else {
            File imgDir = new File(floder.getDir());
            List<String> imgs = Arrays.asList(imgDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
                        return true;
                    return false;
                }
            }));
            mFolderImgs.clear();
            for (String img : imgs) {
                mFolderImgs.add(imgDir.getAbsolutePath() + "/" + img);
            }
            mPhotoAdapter = new PhotoAdapter(mContext, mFolderImgs, R.layout.photo_grid_item, mIsCamera, mAllCount,
                    pickerListenner);
        }
        mPhotoGridView.setAdapter(mPhotoAdapter);

        mFolderName.setText(floder.getName());
        mFolderCount.setText(floder.getCount() + "张");
    }

    private void initView() {
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        mSubmitBT = (Button) findViewById(R.id.main_photo_submit);
        mSubmitBT.setVisibility(View.GONE);

        mPhotoGridView = (GridView) findViewById(R.id.main_photo_picker_gridview);
        mFolderName = (TextView) findViewById(R.id.main_photo_picker_folder_name);
        mFolderCount = (TextView) findViewById(R.id.main_photo_picker_folder_count);

        mSubmitBT.setOnClickListener(this);
        mFolderName.setOnClickListener(this);
        findViewById(R.id.main_photo_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_photo_picker_folder_name) {

            // 选择文件夹
            chooseFolder();
        } else if (v.getId() == R.id.main_photo_back) {

            finish();
        } else if (v.getId() == R.id.main_photo_submit) {

            // 提交
            setResult();
        }
    }

    private void chooseFolder() {
        mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
        mListImageDirPopupWindow.showAsDropDown(mFolderName, 0, 0);

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }

    private PhotoPickerListenner pickerListenner = new PhotoPickerListenner() {

        @Override
        public void camera() {
            showCameraAction();
        }

        @Override
        public void select(int size) {
            if (mAllCount == 1) {
                // 单选直接返回
                setResult();
                return;
            }
            if (size > 0) {
                mSubmitBT.setText("完成(" + size + ")");
                mSubmitBT.setVisibility(View.VISIBLE);
            } else {
                mSubmitBT.setVisibility(View.GONE);
            }
        }
    };

    private void setResult() {
        Intent data = new Intent();
        data.putExtra(PhotoConstancts.PHOTO_SET_RESULT_DATA, (Serializable) mPhotoAdapter.mSelectedImage);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 创建临时文件
        mTempFile = new File(FilePathUtils.getDefaultImagePath(mContext), System.currentTimeMillis() + ".png");
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 设置系统相机拍照后的输出路径
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
        cameraIntent.putExtra("autofocus", true); // 自动对焦
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            String path = mTempFile.getAbsolutePath();
            ImageFloder floder = mImageFloders.get(0);
            floder.setFirstImagePath(path);
            mImgs.add(1, path);
            mPhotoAdapter.notifyDataSetChanged();

            floder.setCount(floder.getCount() + 1);
            mFolderCount.setText(floder.getCount() + "张");
        }
    }

}
