package com.coracle_photopicker_library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.coracle_photopicker_library.activity.ExcessiveActivity;
import com.coracle_photopicker_library.activity.PhotoViewActivity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ouyangbo on 2015/11/3.
 */
public class CoracleManager {

    private static CoracleManager mInstance;

    private onPictureListenner mPictureListenner;
    public onSelectPictureListenner mSelctListenner;

    private CoracleManager() {

    }

    public static CoracleManager getInstance() {
        if (mInstance == null) {
            synchronized (CoracleManager.class) {
                if (mInstance == null) {
                    mInstance = new CoracleManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * 打开选择图片
     *
     * @param context   -- 上下文
     * @param num       -- 限制选择数量
     * @param isCamera  -- 是否显示照相机
     * @param listenner -- 回调
     */
    public void selectImage(Context context, int num, boolean isCamera, onPictureListenner listenner) {
        this.mPictureListenner = listenner;

        Intent intent = new Intent(context, ExcessiveActivity.class);
        intent.putExtra("type", 1);     // 1=选择图片
        intent.putExtra("num", num);     // 选择图片限制数量
        intent.putExtra("isCamera", isCamera);     // 是否显示照相机
        context.startActivity(intent);
    }

    /**
     * 浏览图片
     *
     * @param context  -- 上下文
     * @param images   -- 查看图片集合
     * @param curIndex -- 页标
     */
    public void browserImage(Context context, List<String> images, int curIndex) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable("paths", (Serializable) images);
        bundle.putInt("curIndex", curIndex);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 浏览图片 -- 带选择和剔除的
     *
     * @param context      -- 上下文
     * @param images       -- 查看图片集合
     * @param selectImages -- 默认已选择图片集合
     * @param curIndex     -- 页标
     * @param count     -- 限制选择数量
     * @param listenner     -- 回调
     */
    public void browserImageDel(Context context, List<String> images, List<String> selectImages, int curIndex, int count,
                             onSelectPictureListenner listenner) {
        this.mSelctListenner = listenner;

        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable("paths", (Serializable) images);
        bundle.putSerializable("selectPaths", (Serializable) selectImages);
        bundle.putInt("curIndex", curIndex);
        bundle.putInt("count", count);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public onPictureListenner getPictureListenner() {
        return mPictureListenner;
    }

    public onSelectPictureListenner getSelectListenner() {
        return mSelctListenner;
    }

    public interface onSelectPictureListenner {

        public void add(String path);

        public void del(String path);
    }

    public interface onPictureListenner {

        public void selectImages(List<String> images);
    }
}
