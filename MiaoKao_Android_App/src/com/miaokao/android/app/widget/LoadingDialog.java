package com.miaokao.android.app.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.activity.MainActivity;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-30 下午2:17:10 
 */
public class LoadingDialog extends Dialog {

	private static LoadingDialog mLoadingDialog;
	private static boolean mIsCancel;
	private static String[] mTags;
	private static Context mContext;
	
	private LoadingDialog(Context context) {
		super(context, R.style.selector_dialog);
		
		init();
	}

	private void init() {
		setContentView(R.layout.dialog_loading_view);
		
		this.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT; // 必须加这句
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		setCanceledOnTouchOutside(false);
		
	}

	/**
	 * Dialog
	 * @param context  -- 上下文
	 * @param tags     -- 请求tags
	 * @return
	 */
	public static LoadingDialog createLoadingDialog(final Context context, String... tags) {
		return createLoadingDialog(context, false, tags);
	}
	
	/**
	 * Dialog
	 * @param context  -- 上下文
	 * @param isCancel -- 是否可以返回键
	 * @param tags     -- 请求tags
	 * @return
	 */
	public static LoadingDialog createLoadingDialog(final Context context, boolean isCancel, String... tags) {
		mIsCancel = isCancel;
		mTags = tags;
		mContext = context;
		mLoadingDialog = new LoadingDialog(context);
		
		mLoadingDialog.findViewById(R.id.dialog_loading_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!(context instanceof MainActivity)) {
					// dismiss
					mLoadingDialog.dismiss();
					// finish
					((Activity)context).finish();
					// call request
					if(mTags != null) {
						for(String tag : mTags) {
							AppContext.getInstance().callRequest(tag);
						}
					}
				}
			}
		});
		
		return mLoadingDialog;
	}
	
	@Override
	public void onBackPressed() {
		if (!mIsCancel) {
			if(mTags != null) {
				for(String tag : mTags) {
					AppContext.getInstance().callRequest(tag);
				}
			}
			if(!(mContext instanceof MainActivity)) {
				// dismiss
				mLoadingDialog.dismiss();
				// finish
				((Activity) mContext).finish();
				// call request
				if(mTags != null) {
					for(String tag : mTags) {
						AppContext.getInstance().callRequest(tag);
					}
				}
			}
			super.onBackPressed();
		}
	}
	
	
}
