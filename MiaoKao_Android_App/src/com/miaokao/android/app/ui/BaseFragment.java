package com.miaokao.android.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.activity.MainActivity.OnMainCallbackListenner;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.DialogTips.onDialogCancelListenner;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.miaokao.android.app.widget.HeaderView;

/**
 * Fragment Fragment基类 Created by ouyangbo on 2015/10/14.
 */
public abstract class BaseFragment extends Fragment {

	protected AppContext mAppContext;
	protected HeaderView mHeaderView;
	private DialogTips mDialogTips;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mAppContext == null) {
			mAppContext = AppContext.getInstance();
		}
	}

	public abstract void refreshFragment();

	public abstract void setCallBackListenner(OnMainCallbackListenner callbackListenner);

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

	protected void showDialogTipsAndCancel(Context context, String message, onDialogOkListenner okListenner) {
		if (mDialogTips != null && mDialogTips.isShowing()) {
			mDialogTips.cancel();
		}
		mDialogTips = new DialogTips(context, message);
		mDialogTips.setCanceledOnTouchOutside(false);
		mDialogTips.setCancelable(false);
		mDialogTips.setOkListenner(okListenner);
		mDialogTips.setCancelListenner(null);
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
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_left, R.anim.in_right);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		getActivity().overridePendingTransition(R.anim.in_left, R.anim.in_right);
	}
	
	public void startActivity(Context context, Intent intent) {
		super.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.in_left, R.anim.in_right);
	}

	public void startActivityForResult(Context context, Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		((Activity)context).overridePendingTransition(R.anim.in_left, R.anim.in_right);
	}

	protected void initTopBarOnlyTitle(int layoutId, String title) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteLeftView();
		mHeaderView.deleteRightView();
	}

	protected void initTopBarLeftAndTitle(int layoutId, String title) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				getActivity().finish();
			}
		});
	}

	protected void initTopBarLeftAndTitle(int layoutId, String title, HeaderView.OnLeftClickListenner listenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setOnLeftClickListenner(listenner);
	}

	protected void initTopBarLeftAndTitle(int layoutId, int resouceId, String title) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setLeftImg(resouceId);
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				getActivity().finish();
			}
		});
	}

	protected void initTopBarLeftAndTitle(int layoutId, int resouceId, String title,
			HeaderView.OnLeftClickListenner listenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteRightView();
		mHeaderView.setLeftImg(resouceId);
		mHeaderView.setOnLeftClickListenner(listenner);
	}

	protected void initTopBarRightAndTitle(int layoutId, String title, String rightTxt,
			HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteLeftView();
		mHeaderView.setRightTxt(rightTxt);
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void initTopBarRightAndTitle(int layoutId, String title, int resouceId,
			HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.deleteLeftView();
		mHeaderView.setRightImg(resouceId);
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void initTopBarAll(int layoutId, String title, String rightTxt, HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.setRightTxt(rightTxt);
		mHeaderView.addRightView();
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				getActivity().finish();
			}
		});
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void initTopBarAllLeftRight(int layoutId, String title, int leftResouceId,
			HeaderView.OnLeftClickListenner leftListenner, int rightResouceId,
			HeaderView.OnRightClickListenner rightListenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.setLeftImg(leftResouceId);
		mHeaderView.setOnLeftClickListenner(leftListenner);
		mHeaderView.addRightView();
		mHeaderView.setRightImg(rightResouceId);
		mHeaderView.setOnRightClickListenner(rightListenner);
	}

	protected void refreshLeftTxt(String txt) {
		mHeaderView.setLeftTxt(txt, R.drawable.b_s_title_down);
	}

	protected void initTopBarAll(int layoutId, String title, int resouceId, HeaderView.OnRightClickListenner listenner) {
		mHeaderView = (HeaderView) getActivity().findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.setRightImg(resouceId);
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				getActivity().finish();
			}
		});
		mHeaderView.setOnRightClickListenner(listenner);
	}

	protected void refreshTitle(String title) {
		mHeaderView.setTitile(title);
	}

	/**
	 * Layout动画
	 * 
	 * @return
	 */
	protected LayoutAnimationController getAnimationController() {
		int duration = 500;
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(duration);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(duration);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
		controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
		return controller;
	}
}
