本Library为选择手机图片和拍照库

一：需要添加权限如下
    <!-- SDK 读写 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <!-- 拍照 -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />

二：需要注册库中Activity
    <!-- ==========选择图片Activity==========  -->
        <activity
            android:name="com.coracle_photopicker_library.activity.ExcessiveActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen" />
        <activity
            android:name="com.coracle_photopicker_library.activity.PhotoPicKerActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:screenOrientation="portrait" />
        <!-- 浏览图片Activity  -->
        <activity
            android:name="com.coracle_photopicker_library.activity.PhotoViewActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:screenOrientation="portrait" />

三：调用代码
1：    /**
       * 打开选择图片
       *
       * @param context   -- 上下文
       * @param num       -- 限制选择数量
       * @param isCamera  -- 是否显示照相机
       * @param listenner -- 回调
       */
CoracleManager.getInstance().selectImage(context, num, true, new CoracleManager.onPictureListenner() {
                        @Override
                        public void selectImages(List<String> images) {
                            mImages.remove("add");
                            mImages.addAll(images);
                            if (mImages.size() != 9) {
                                mImages.add("add");
                            }

                            mImageAdapter.notifyDataSetChanged();
                        }
                    });

2:     /**
       * 浏览图片
       *
       * @param context  -- 上下文
       * @param images   -- 查看图片集合
       * @param curIndex -- 页标
       */

CoracleManager.getInstance().browserImage(mContext, tempList, i);

3:      /**
            * 浏览图片 -- 带选择和剔除的
            *
            * @param context      -- 上下文
            * @param images       -- 查看图片集合
            * @param selectImages -- 默认已选择图片集合
            * @param curIndex     -- 页标
            * @param count     -- 限制选择数量
            * @param listenner     -- 回调
            */
CoracleManager.getInstance().browserImageDel(context, images, selectImages, i, num, new CoracleManager.onSelectPictureListenner() {
                        @Override
                        public void add(String path) {
                            mImages.add(path);
                            mImageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void del(String path) {
                            mImages.remove(path);
                            if(mImages.size() != 9) {
                                mImages.add("add");
                            }
                            mImageAdapter.notifyDataSetChanged();
                        }
                    });

