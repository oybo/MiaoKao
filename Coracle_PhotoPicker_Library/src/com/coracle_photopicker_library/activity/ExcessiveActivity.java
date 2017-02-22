package com.coracle_photopicker_library.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.coracle_photopicker_library.CoracleManager;
import com.coracle_photopicker_library.utils.PhotoConstancts;

import java.util.List;

/**
 * 实现功能中间页面
 * Created by ouyangbo on 2015/11/3.
 */
public class ExcessiveActivity extends Activity {

    private CoracleManager mCoracleManager;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCoracleManager = CoracleManager.getInstance();

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 1);
        if(type == 1) {
            // 1 = 选择图片
            int num = intent.getIntExtra("num", 1);
            boolean isCamera = intent.getBooleanExtra("isCamera", true);

            intent = new Intent(this, PhotoPicKerActivity.class);
            // 选择图片限制数量，1为单选，
            intent.putExtra(PhotoConstancts.PHOTO_NUM, num);
            // 是否添加拍照Item，默认添加
            intent.putExtra(PhotoConstancts.PHOTO_IS_SHOW_CAMERA, isCamera);
            startActivityForResult(intent, PhotoConstancts.PHOTO_SET_RESULT_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PhotoConstancts.PHOTO_SET_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            final List<String> selectImages = (List<String>) data.getSerializableExtra(PhotoConstancts.PHOTO_SET_RESULT_DATA);

            CoracleManager.onPictureListenner listenner = mCoracleManager.getPictureListenner();
            if(listenner != null) {
                listenner.selectImages(selectImages);
            }
        }

        finish();
    }

}
