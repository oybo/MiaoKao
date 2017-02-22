package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

/**
 * @TODO 意见反馈
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-3-9 下午2:12:48
 */
public class MyFeedbackActivity extends BaseActivity implements OnClickListener {

	private EditText mContentET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_feedback);

		initView();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.my_feedback_common_actionbar, "意见反馈");

		mContentET = (EditText) findViewById(R.id.feedback_content_et);

		findViewById(R.id.feedback_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback_bt:
			String content = mContentET.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				return;
			}

			sunmit(content);
			break;
		}
	}

	private void sunmit(String content) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "add_suggestion");
		postData.put("content", content);
		mAppContext.netRequest(MyFeedbackActivity.this, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					String result = object.optString("result");
					if("ok".equals(result)) {
						showDialogTipsNotCancel(MyFeedbackActivity.this, "您的意见已提交，非常感谢!", new onDialogOkListenner() {
							@Override
							public void onClick() {
								finish();
							}
						});
					}
				}
			}

			@Override
			public void responseError() {

			}
		}, true, getClass().getName());
	}

}
