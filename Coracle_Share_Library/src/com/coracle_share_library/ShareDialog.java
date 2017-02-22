package com.coracle_share_library;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class ShareDialog {

	private static ShareDialog instance = null;

	private Dialog mDialog;
	private ShareUtils mShareUtils;
	private Context mContext;
	
	public static ShareDialog getInstance(Context context) {
		if (instance == null) {
			synchronized (ShareUtils.class) {
				if (instance == null) {
					instance = new ShareDialog(context);
				}
			}
		}
		return instance;
	}
	
	private ShareDialog(Context context) {
		mContext = context;
		mShareUtils = ShareUtils.getInstance(context);
	}
	
	
	public void createShareDialog(String title, String content, String url, Bitmap bitmap) {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		mDialog = new Dialog(mContext, R.style.MyDialogStyle2);
		mDialog.setContentView(R.layout.dialog_share_view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setCancelable(true);
		mDialog.findViewById(R.id.wxpyqbtn).setOnClickListener(onClickListener);
		mDialog.findViewById(R.id.xlwbbtn).setOnClickListener(onClickListener);
		mDialog.findViewById(R.id.txwbbtn).setOnClickListener(onClickListener);
		mDialog.findViewById(R.id.qqkjbtn).setOnClickListener(onClickListener);
		mDialog.findViewById(R.id.dialogbtn).setOnClickListener(onClickListener);
		Window window = mDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.bottotop);
		mDialog.show();

//		this.title = title;
//		this.content = content;
//		this.url = url;
//		this.bitmap = bitmap;
	}
	
	OnClickListener onClickListener = new OnClickListener() {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.wxpyqbtn:
//				// 微信朋友圈
//				mShareUtils.wechatMomentsShare(title, content, url, musicUrl, bitmap, listenner);
//				mDialog.dismiss();
//				break;
//			case R.id.xlwbbtn:
//				// 新浪微博
//				mShareUtils.sineWeiBoShare(title, content, imageUrl, listenner);
//				mDialog.dismiss();
//				break;
//			case R.id.txwbbtn:
//				// 腾讯微博
//				mShareUtils.tencentWeiBoShare(title, imageUrl, content, listenner);				
//				mDialog.dismiss();
//				break;
//			case R.id.qqkjbtn:
//				// QQ空间
//				mShareUtils.qZoneShare(title, titleUrl, content, imageUrl, site, siteUrl, listenner);
//				mDialog.dismiss();
//				break;
//			case R.id.dialogbtn:
//				mDialog.dismiss();
//				break;
//			}
		}
	};
}
