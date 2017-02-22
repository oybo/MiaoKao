package com.miaokao.android.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 欢迎页,
 * @author 欧阳博
 *
 */
public class WelcomeActivity extends Activity {

	private boolean mLoadImageSuccess;
	private ImageView mBgImageView;
	private CountDownTimer mWelcomeTimer;
	private CountDownTimer mImageTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		mBgImageView = (ImageView) findViewById(R.id.welcome_bg_iv);
		
		AppContext.getInstance().mUser = null;
		
		// 开启定位服务
		AppContext.getInstance().startLocalService();
		
		mWelcomeTimer = new CountDownTimer(5000, 5000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				
			}
			
			@Override
			public void onFinish() {
				if(!mLoadImageSuccess) {
					goMain();
					ImageLoader.getInstance().cancelDisplayTask(mBgImageView);
				}
			}
		};
		mWelcomeTimer.start();
		
		String image_path = PreferenceUtils.getInstance().getString(PubConstant.LOADING_IMAGE_PATH, "");
		if (!TextUtils.isEmpty(image_path)) {
			ImageLoader.getInstance().displayImage(image_path, mBgImageView,
					AppContext.getInstance().getOptions(0), new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							mLoadImageSuccess = true;
							// 2秒再跳转
							mImageTimer = new CountDownTimer(3000, 3000) {
								
								@Override
								public void onTick(long millisUntilFinished) {
									
								}
								
								@Override
								public void onFinish() {
									goMain();
								}
							};
							mImageTimer.start();
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});
		}
		
		PreferenceUtils.getInstance().remove(PubConstant.IS_SAVE_CRASH_LOG);
	}

	protected void goMain() {
		startActivity(new Intent(this, MainActivity.class));
		overridePendingTransition(R.anim.in_left, R.anim.in_right);
		finish();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWelcomeTimer.cancel();
		if(mImageTimer != null) {
			mImageTimer.cancel();
		}
	}
}
