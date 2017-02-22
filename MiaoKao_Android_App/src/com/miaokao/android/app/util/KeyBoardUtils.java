package com.miaokao.android.app.util;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 *  @author ouyangbo
 *  @date   2015-3-20
 *  @email  ouyangbo@kingnode.com
 *  @remark 
 *  @modify by
 */
/**
 * 打开或关闭软键盘
 * 
 * @author zhy
 * 
 */
public class KeyBoardUtils {
	/**
	 * 打卡软键盘
	 * 
	 * @param editText 输入框
	 * @param context 上下文
	 */
	public static void openKeybord(EditText editText, Context context) {
		editText.requestFocus();
		InputMethodManager imm1 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm1.showSoftInput(editText, 0);
	}

	/**
	 * 关闭软键盘
	 * @param context 上下文
	 */
	public static void closeKeybord(Context context) {
		InputMethodManager manager = ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE));
		Activity activity = (Activity)context;
		if ((activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)) {
			if (activity.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
