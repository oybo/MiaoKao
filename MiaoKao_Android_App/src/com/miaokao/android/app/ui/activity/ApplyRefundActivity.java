package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.miaokao.android.app.R;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;

/**
 * @TODO 申请退款
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-29 下午2:42:31 
 */
public class ApplyRefundActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private Order mOrder;
	private TextView mRefundAllTxt, mRefundPortionTxt;
	private EditText mContentET;
	private boolean isAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_apply_refund);
		
		mContext = this;
		mOrder = (Order) getIntent().getExtras().getSerializable("order");
		initView();
		
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.apply_refund_common_actionbar, "我要退款");
		
		mRefundAllTxt = (TextView) findViewById(R.id.apply_refund_all_txt);
		mRefundPortionTxt = (TextView) findViewById(R.id.apply_refund_portion_txt);
		mRefundPortionTxt.setSelected(true);
		mContentET = (EditText) findViewById(R.id.apply_refund_cause_et);
		
		mRefundAllTxt.setOnClickListener(this);
		mRefundPortionTxt.setOnClickListener(this);
		findViewById(R.id.apply_refund_ok_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.apply_refund_all_txt:
			// 全部退款
			isAll = true;
			mRefundAllTxt.setSelected(true);
			mRefundPortionTxt.setSelected(false);
			break;
		case R.id.apply_refund_portion_txt:
			// 部分退款
			isAll = false;
			mRefundAllTxt.setSelected(false);
			mRefundPortionTxt.setSelected(true);
			break;
		case R.id.apply_refund_ok_bt:
			// 提交
			String content = mContentET.getText().toString().trim();
			if(TextUtils.isEmpty(content)) {
				content = "";
			}
			applyRefund(content);
			break;
		}
	}

	private void applyRefund(String content) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "to_return");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("mer_id", mOrder.getMer_id());
		postData.put("product_id", mOrder.getProduct_id());
		postData.put("reason", content);
		postData.put("return_type", isAll ? "1" : "2");
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					Intent intent = new Intent(mContext, ApplyRefundAffirmActivity.class);
					intent.putExtra("title", "退款申请");
					intent.putExtra("result", jsonArray.toString());
					startActivity(intent);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());		
	}
	
	
}
