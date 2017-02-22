package com.miaokao.android.app.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Make;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.ScanCodeUtils;

/**
 * @TODO 生成二维码页面 供扫描签到
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-5 下午6:53:34 
 */
public class SignInForScanActivity extends BaseActivity {

	private ImageView mCodeImage;
	private Make mMake;
	private Bitmap mBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sign_in_for_scan);
		
		mMake = (Make) getIntent().getExtras().getSerializable("make");
		initView();
		getScanCode();
	}

	/**
	 * 生成二维码
	 */
	private void getScanCode() {
		if(mMake != null) {
			String codeStr = "today_check," + mAppContext.mUser.getUser_id() + "," + mMake.getTime_node();
			mBitmap = ScanCodeUtils.createQRImage(this, codeStr);
			mCodeImage.setImageBitmap(mBitmap);
		}
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.sign_in_for_scan_common_actionbar, "签到");
		
		mCodeImage = (ImageView) findViewById(R.id.sign_in_for_scan_code_image);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}
	
	
}
