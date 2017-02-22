package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.MerComment;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-27 下午1:25:48 
 */
public class TalkDrivingSchoolActivity extends BaseActivity {

	private Context mContext;
	private EditText mContentET;
	private String mMerId;
	private String mCoachId;
	private String mRate;
	private List<MerComment> mComments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_talk_driving_school);
		
		mContext = this;
		initView();
		
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		Intent intent = getIntent();
		mMerId = intent.getStringExtra("mer_id");
		mCoachId = intent.getStringExtra("coach_id");
		mRate = intent.getStringExtra("rate");
		mComments = (List<MerComment>) intent.getExtras().getSerializable("comment_list");
		
		String title = "说说驾校";
		if(!TextUtils.isEmpty(mCoachId)) {
			title = "教练评价";
		}
		initTopBarAll(R.id.talk_d_s_common_actionbar, title, "全部评论", new OnRightClickListenner() {
			
			@Override
			public void onClick() {
				Intent intent = new Intent(mContext, CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("comment_list", (Serializable) mComments);
				intent.putExtras(bundle);
				intent.putExtra("mer_id", mMerId);
				intent.putExtra("coach_id", mCoachId);
				startActivity(intent);
			}
		});
		
		mContentET = (EditText) findViewById(R.id.talk_d_s_content_et);
		
		findViewById(R.id.talk_d_s_ok_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = mContentET.getText().toString().trim();
				if(!TextUtils.isEmpty(content)) {
					if(!TextUtils.isEmpty(mMerId)) {
						// 评论驾校
						String url = PubConstant.REQUEST_BASE_URL + "/app_merchants_service.php";
						submitComment(content, url, 1);
					} else {
						// 评论教练
						String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
						submitComment(content, url, 2);
					}
				}
			}
		});
	}

	protected void submitComment(String content, String url, int type) {
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "add_comment");
		if(type == 1) {
			// 驾校
			postData.put("mer_id", mMerId);
		} else if(type == 2) {
			postData.put("coach_id", mCoachId);
		}
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("content", content);
		postData.put("rate", mRate);
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					String result = object.optString("result");
					if("ok".equals(result) || "提交成功".equals(result)) {
						setResult(Activity.RESULT_OK);
						finish();
					} else {
						showDialogTips(mContext, result);
					}
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
}
