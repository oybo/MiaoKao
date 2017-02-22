package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.util.TimeUtil;

/**
 * @TODO 订单明细
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-3 下午10:04:44 
 */
public class OrderDetailActivity extends BaseActivity {

	private Context mContext;
	private Order mOrder;
	private TextView mNameTxt, mOrderIdTxt, mDateTxt, mPayTypeTxt, mOrderStatuTxt, mPriceTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_order_detail);
		
		mContext = this;
		mOrder = (Order) getIntent().getExtras().getSerializable("order");
		initView();
		initData();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.order_detail_common_actionbar, "订单明细");
		
		mNameTxt = (TextView) findViewById(R.id.order_detail_name_txt);
		mOrderIdTxt = (TextView) findViewById(R.id.order_detail_id_txt);
		mDateTxt = (TextView) findViewById(R.id.order_detail_date_txt);
		mPayTypeTxt = (TextView) findViewById(R.id.order_detail_pay_type_txt);
		mOrderStatuTxt = (TextView) findViewById(R.id.order_detail_status_txt);
		mPriceTxt = (TextView) findViewById(R.id.order_detail_price_txt);
		
		findViewById(R.id.order_detail_name_txt);
	}
	
	private void initData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "order_detail");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("pay_no", mOrder.getFirst_pay_no());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					String mer_name = object.optString("mer_name");
					String mer_id = object.optString("mer_id");
					String user_id = object.optString("user_id");
					String order_no = object.optString("order_no");
					String bill_no = object.optString("bill_no");
					String bill_num = object.optString("bill_num");
					String channel_2 = object.optString("channel_2");
					String fee_2 = object.optString("fee_2");
					String trans_time = object.optString("trans_time");
					
					mNameTxt.setText(mer_name);
					mOrderIdTxt.setText(order_no);
					mDateTxt.setText(TimeUtil.longToString(Long.parseLong(trans_time), TimeUtil.FORMAT_DATE_TIME_SECOND));
					mPayTypeTxt.setText(channel_2);
					mOrderStatuTxt.setText(bill_num);
					mPriceTxt.setText(fee_2 + "元");
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
