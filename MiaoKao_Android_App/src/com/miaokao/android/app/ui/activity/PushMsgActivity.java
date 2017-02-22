package com.miaokao.android.app.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;

/**
 * @TODO 收到消息 弹出通知点击事件处理页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-4-13 下午6:22:43 
 */
public class PushMsgActivity extends BaseActivity {

	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_message);
		
		mContext = this;
		
//		ImageView imageView = (ImageView) findViewById(R.id.push_bg_iv);
//		String image_path = PreferenceUtils.getInstance().getString(PubConstant.LOADING_IMAGE_PATH, "");
//		if (!TextUtils.isEmpty(image_path)) {
//			ImageLoader.getInstance().displayImage(image_path, imageView,
//					AppContext.getInstance().getOptions(0), new ImageLoadingListener() {
//						
//						@Override
//						public void onLoadingStarted(String arg0, View arg1) {
//						}
//						
//						@Override
//						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//						}
//						
//						@Override
//						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//							
//						}
//						
//						@Override
//						public void onLoadingCancelled(String arg0, View arg1) {
//						}
//					});
//		}
		
		// 判断是否登录
		if(AppContext.getInstance().mUser == null) {
			// 需要先登录
			RequestConstants.autoLogin(mContext);
		} else {
			// 直接到我的消息页面
			startActivity(new Intent(mContext, MyMessageActivity.class));
			finish();
		}
		
		initRecriver();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(PubConstant.LOGIN_STATE_KEY.equals(action)) {
				// 跳转到我的消息页面
				startActivity(new Intent(mContext, MyMessageActivity.class));
				finish();
			}
		}
	};
	
	private void initRecriver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.LOGIN_STATE_KEY);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
}
