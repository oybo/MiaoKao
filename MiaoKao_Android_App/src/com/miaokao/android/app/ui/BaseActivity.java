package com.miaokao.android.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.util.ToastFactory;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.DialogTips.onDialogCancelListenner;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.miaokao.android.app.widget.HeaderView;

/**
 * Created by ouyangbo on 2015/10/12.
 * 
 * @author ouyangbo
 */
public class BaseActivity extends Activity {

	protected AppContext mAppContext;
	protected HeaderView mHeaderView;
	private DialogTips mDialogTips;
	protected Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (mAppContext == null) {
			mAppContext = AppContext.getInstance();
		}

		mAppContext.addActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 判断是否异常重启过
		AppContext.getInstance().checkProcessStatus();
		
		if (AppContext.getInstance().mHomeResume && RequestConstants.LOGIN_END) {
			if (AppContext.getInstance().mUser != null) {
				// 登录成功的状态才进行
				// 从home键到重新可见的 - 重新登录
				RequestConstants.LOGIN_END = false;
				RequestConstants.autoLogin(this);
				AppContext.getInstance().mHomeResume = false;
			}
		}
	}

	protected void showDialogTips(Context context, String message) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.show();
	}
	
	protected void showDialogTipsNotCancel(Context context, String message, onDialogOkListenner okListenner) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.setCanceledOnTouchOutside(false);
		mDialogTips.setCancelable(false);
		mDialogTips.setOkListenner(okListenner);
		mDialogTips.show();
	}
	
	protected void showDialogTipsNotCancel(Context context, String message, String rightTxt,
			onDialogOkListenner okListenner) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.setCanceledOnTouchOutside(false);
		mDialogTips.setCancelable(false);
		mDialogTips.setOkListenner(rightTxt, okListenner);
		mDialogTips.show();
	}

	protected void showDialogTipsAll(Context context, String message, String leftTxt,
			onDialogCancelListenner cancelListenner, String rightTxt, onDialogOkListenner okListenner) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.setCanceledOnTouchOutside(false);
		mDialogTips.setCancelable(false);
		mDialogTips.setCancelListenner(leftTxt, cancelListenner);
		mDialogTips.setOkListenner(rightTxt, okListenner);
		mDialogTips.show();
	}
	
	protected void showDialogTipsAndCancel(Context context, String message, onDialogOkListenner okListenner) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.setOkListenner(okListenner);
		mDialogTips.setCancelListenner(null);
		mDialogTips.show();
	}
	
	protected void showDialogTipsAndCancel(Context context, String message, String rightTxt, onDialogOkListenner okListenner) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.setOkListenner(rightTxt, okListenner);
		mDialogTips.setCancelListenner(null);
		mDialogTips.show();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_left, R.anim.in_right);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.in_left, R.anim.in_right);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.edit_left, R.anim.edit_right);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.edit_left, R.anim.edit_right);
	}

	protected void showToast(String msg) {
		ToastFactory.getToast(mAppContext, msg).show();
	}

	protected void showToastCenter(String msg) {
		ToastFactory.showToastCenter(mAppContext, msg).show();
	}

	protected void cancelToast() {
		ToastFactory.cancelToast();
	}

	protected void initTopBarOnlyTitle(int layoutId, String title) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteLeftView();
		mHeaderView.deleteRightView();
	}

	protected void initTopBarLeftAndTitle(int layoutId, String title) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				finish();
			}
		});
	}

	protected void initTopBarLeftAndTitle(int layoutId, String title, HeaderView.OnLeftClickListenner listenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setOnLeftClickListenner(listenner);
	}

	protected void initTopBarLeftAndTitle(int layoutId, int resouceId, String title) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setLeftImg(resouceId);
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				finish();
			}
		});
	}

	protected void initTopBarLeftAndTitle(int layoutId, int resouceId, String title,
			HeaderView.OnLeftClickListenner listenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setLeftImg(resouceId);
		mHeaderView.setOnLeftClickListenner(listenner);
	}

	protected void initTopBarRightAndTitle(int layoutId, String title, String rightTxt,
			HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteLeftView();
		mHeaderView.setRightTxt(rightTxt);
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void initTopBarRightAndTitle(int layoutId, String title, int resouceId,
			HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteLeftView();
		mHeaderView.setRightImg(resouceId);
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void initTopBarAll(int layoutId, String title, String rightTxt, HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.addRightView();
		mHeaderView.setRightTxt(rightTxt);
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				finish();
			}
		});
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void initTopBarAllLeftRight(int layoutId, String title, int leftResouceId,
			HeaderView.OnLeftClickListenner leftListenner, int rightResouceId,
			HeaderView.OnRightClickListenner rightListenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.setLeftImg(leftResouceId);
		mHeaderView.setOnLeftClickListenner(leftListenner);
		mHeaderView.addRightView();
		mHeaderView.setRightImg(rightResouceId);
		mHeaderView.setOnRightClickListenner(rightListenner);
	}

	protected void initTopBarAll(int layoutId, String title, int resouceId, HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.setRightImg(resouceId);
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				finish();
			}
		});
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void refreshTitle(String title) {
		mHeaderView.setTitile(title);
	}
	
}
