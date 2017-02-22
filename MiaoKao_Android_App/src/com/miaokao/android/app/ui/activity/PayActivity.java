package com.miaokao.android.app.ui.activity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.pingplusplus.android.PaymentActivity;

/**
 * @TODO 支付页面
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-3 下午8:54:02
 */
public class PayActivity extends BaseActivity implements OnClickListener {

	/**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    
    private String mPayType;
	private Context mContext;
	private TextView mSubjectNameTxt, mPayPriceTxt;
	private TextView mAliPayTxt, mWxPayTxt;
	
	private String mId;
	private String mPayPrice;
	private Order mOrder;

	private String mType;
	private Map<String, String> postData;
	
	private boolean isPayTour;
	private JSONObject mPayTourJsonObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pay);

		mContext = this;
		Intent intent = getIntent();
		// 打赏支付
		isPayTour = intent.getBooleanExtra("isPayTour", false);
		if(isPayTour) {
			String payTourData = intent.getStringExtra("payTourData");
			try {
				mPayTourJsonObject = new JSONObject(payTourData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 报名支付
		mType = intent.getStringExtra("type");
		mId = intent.getStringExtra("id");
		mOrder = (Order) intent.getExtras().getSerializable("order");
		postData = (Map<String, String>) intent.getExtras().getSerializable("postData");
		
		initView();
		if(isPayTour) {
			mSubjectNameTxt.setText("打赏");
			try {
				mPayPriceTxt.setText(mPayTourJsonObject.getDouble("price") + "元");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			initData();
		}
	}

	private void initData() {
		mSubjectNameTxt.setText("第" +mId+ "批付款");
		if("1".equals(mId)) {
			mPayPrice = mOrder.getFirst_pay_num();
		} else if("2".equals(mId)) {
			mPayPrice = mOrder.getSecond_pay_num();
		} else if("3".equals(mId)) {
			mPayPrice = mOrder.getThird_pay_num();
		} else if("4".equals(mId)) {
			mPayPrice = mOrder.getFourth_pay_num();
		} else if("5".equals(mId)) {
			mPayPrice = mOrder.getFivth_pay_num();
		}
		mPayPriceTxt.setText(mPayPrice + "元");
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.pay_common_actionbar, "支付");

		mSubjectNameTxt = (TextView) findViewById(R.id.pay_subject_name_txt);
		mPayPriceTxt = (TextView) findViewById(R.id.pay_amount_txt);
		mAliPayTxt = (TextView) findViewById(R.id.pay_alipay_txt);
		mWxPayTxt = (TextView) findViewById(R.id.pay_wx_txt);

		findViewById(R.id.pay_alipay_layout).setOnClickListener(this);
		findViewById(R.id.pay_wx_layout).setOnClickListener(this);
		findViewById(R.id.pay_ok_bt).setOnClickListener(this);
		
		mAliPayTxt.setSelected(true);
		mPayType = CHANNEL_ALIPAY;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pay_alipay_layout:
			// 支付宝
			mAliPayTxt.setSelected(true);
			mWxPayTxt.setSelected(false);
			
			mPayType = CHANNEL_ALIPAY;
			break;
		case R.id.pay_wx_layout:
			// 微信
			mAliPayTxt.setSelected(false);
			mWxPayTxt.setSelected(true);
			
			mPayType = CHANNEL_WECHAT;
			break;
		case R.id.pay_ok_bt:
			// 提交订单
			if(isPayTour) {
				playTour();
			} else {
				if("submit_order".equals(mType)) {
					// 先提交订单，再支付
					submitOrder();
				} else {
					getPayInfo();
				}
			}
			break;
		}
	}

	protected void submitOrder() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_add_order.php";
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					String result = object.optString("result");
					if("false".equals(result) || TextUtils.isEmpty(result)) {
						// 失败
						showDialogTips(mContext, "提交订单失败");
					} else {
						// 成功
						mOrder.setOrder_no(result);
						getPayInfo();
					}
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	/**
	 * 去服务端请求获取支付信息签名等
	 */
	private void getPayInfo() {
		double parsed = Double.parseDouble(mPayPrice);
        String formatted = NumberFormat.getCurrencyInstance(Locale.CHINA).format((parsed));
        String replaceable = String.format("[%s, \\s.]", NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency().getSymbol(Locale.CHINA));
        String cleanString = formatted.toString().replaceAll(replaceable, "");
        double amount = Double.parseDouble(new BigDecimal(cleanString).toString());
        
//        String url = "http://115.29.190.76/ping_pay/example/pay.php";	// 测试地址
		String url = "http://115.29.190.76/pingxx/pay/pay.php";		// 正式地址
        
        JSONObject jsonObject = new JSONObject();
        try {
			jsonObject.put("channel", mPayType);
			jsonObject.put("amount", amount); // 因为传给支付平台的金额单位是分，所以这里得 * 100
			jsonObject.put("subject", mOrder.getProduct_name());
			jsonObject.put("body", mOrder.getMer_name());
			jsonObject.put("order_no", mOrder.getOrder_no());
			jsonObject.put("bill_num", mId);
			jsonObject.put("mer_id", mOrder.getMer_id());
			jsonObject.put("user_id", mAppContext.mUser.getLoginName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        mAppContext.netRequestForJson(mContext, url, jsonObject, new RequestListenner() {
			
			@Override
			public void responseResult(JSONObject jsonObject) {
				pay(jsonObject.toString());
			}
			
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	/**
	 * 打赏支付
	 */
	private void playTour() {
		try {
			double parsed = mPayTourJsonObject.getDouble("price");
			String formatted = NumberFormat.getCurrencyInstance(Locale.CHINA).format((parsed / 100));
			String replaceable = String.format("[%s, \\s.]", NumberFormat.getCurrencyInstance(Locale.CHINA).getCurrency()
					.getSymbol(Locale.CHINA));
			String cleanString = formatted.toString().replaceAll(replaceable, "");
			int amount = Integer.valueOf(new BigDecimal(cleanString).toString());

			String url = "http://115.29.190.76/prize_pay/pay/pay.php";
			
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("channel", mPayType);
				jsonObject.put("amount", amount * 100); // 因为传给支付平台的金额单位是分，所以这里得 * 100
				jsonObject.put("subject", mPayTourJsonObject.getString("subject"));
				jsonObject.put("body", mPayTourJsonObject.getInt("body"));
				jsonObject.put("prize_id", mPayTourJsonObject.getString("prize_id"));
				jsonObject.put("trans_type", "prize");
				jsonObject.put("coach_id", mPayTourJsonObject.getString("coach_id"));
				jsonObject.put("user_id", AppContext.getInstance().mUser.getLoginName());
				jsonObject.put("mode", "live");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mAppContext.netRequestForJson(mContext, url, jsonObject, new RequestListenner() {

				@Override
				public void responseResult(JSONObject jsonObject) {
					pay(jsonObject.toString());
				}

				@Override
				public void responseError() {

				}
			}, true, getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void pay(String charge) {
		Intent intent = new Intent();
		String packageName = getPackageName();
		ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
		intent.setComponent(componentName);
		intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
		startActivityForResult(intent, PubConstant.REQUEST_CODE_PAYMENT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 支付页面返回处理
		if (requestCode == PubConstant.REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				String result = data.getExtras().getString("pay_result");
				/*
				 * 处理返回值 "success" - payment succeed "fail" - payment failed
				 * "cancel" - user canceld "invalid" - payment plugin not
				 * installed
				 */
				if("success".equals(result)) {
					showDialogTipsNotCancel(mContext, "支付成功", new onDialogOkListenner() {
						@Override
						public void onClick() {
							// 
							setResult(Activity.RESULT_OK);
							finish();
						}
					});
				} else if("fail".equals(result)) {
					showDialogTips(mContext, "支付失败");
				} else if("cancel".equals(result)) {
					showDialogTips(mContext, "取消支付");
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
	
}
