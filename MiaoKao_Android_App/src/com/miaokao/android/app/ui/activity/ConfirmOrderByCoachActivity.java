package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.inteface.LoginStatusListenner;
import com.miaokao.android.app.recerver.MyRecerver;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

public class ConfirmOrderByCoachActivity extends BaseActivity implements OnClickListener, LoginStatusListenner {

	private Context mContext;
	private Coach mCoach;
	private JSONObject mJsonObject;
	private TextView mNameTxt;
	private TextView mP_NameTxt;
	private TextView mP_TypeTxt;
	private TextView mP_PriceTxt, mAllPriceTxt;
	private int mPrice, mStuPrice;
	private TextView mUserNameTxt, mUserPhoneTxt, mUserSexTxt, mUserAddressTxt;
	private int mLoginType;
	private CheckBox mIsStudentCBox;
	private TextView mPrivilegeTxt;
	private TextView mOnlineTxt, mOfflineTxt;
	private TextView mInfoTxt;
	private TextView mFirstPayBlTxt, mFristPayTxt;
	private RelativeLayout mIsStudentLayout, mFristPayLayout;
	private TextView mSelectCoachNameTxt;
	private JSONObject mDetailObject, mProductObject, mPayObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_confirm_order_coach);
		
		mContext = this;
		Bundle bundle = getIntent().getExtras();
		mCoach = (Coach) bundle.getSerializable("coach");
		try {
			mJsonObject = new JSONObject(bundle.getString("date"));
		} catch (Exception e) {
			e.printStackTrace();
			mJsonObject = new JSONObject();
		}
		
		initView();
		initDate();
	}

	private void initDate() {
		mSelectCoachNameTxt.setText(mCoach.getName());
		mNameTxt.setText(mCoach.getMer_name());	// 广人驾校
		
		mDetailObject = new JSONObject();
		JSONArray detailArray = mJsonObject.optJSONArray("detail");
		if (detailArray != null && detailArray.length() > 0) {
			try {
				mDetailObject = detailArray.getJSONObject(0);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		mProductObject = new JSONObject();
		JSONArray productArray = mJsonObject.optJSONArray("product");
		int len = productArray.length();
		if (productArray != null && len > 0) {
			try {
				mProductObject = productArray.getJSONObject(len - 1);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		mPayObject = new JSONObject();
		JSONArray payArray = mJsonObject.optJSONArray("pay_rate");
		if (payArray != null && payArray.length() > 0) {
			try {
				mPayObject = payArray.getJSONObject(0);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
				
		mP_NameTxt.setText(mDetailObject.optString("type"));	// C1
		mP_TypeTxt.setText(mProductObject.optString("p_name"));	// 暑假班
		String p_price = mProductObject.optString("p_price");	// 费用
		String discount_price = mProductObject.optString("discount_price");
		if(!TextUtils.isEmpty(discount_price)) {
			p_price = discount_price;
		}
		// 培训费用
		mP_PriceTxt.setText(p_price + "元");
		// 备注
		mInfoTxt.setText(mProductObject.optString("p_intro"));
		// 总计金额
		try {
			double temp = Double.parseDouble(p_price);
			mPrice = (int) temp;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		mAllPriceTxt.setText(mPrice + "元");
		// 学生优化金额
		try {
			double temp = Double.parseDouble(mProductObject.optString("stu_price"));
			mStuPrice = (int) temp;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		mPrivilegeTxt.setText("-" + (mPrice - mStuPrice) + "元");
		// 首付款
		try {
			double pay_rate = Double.parseDouble(mPayObject.optString("first_pay_rate"));
			// 比例
			mFirstPayBlTxt.setText("首批付款(" + PubUtils.numberToPercent(pay_rate) + ")");
			// 金额
			mFristPayTxt.setText(PubUtils.moneyFormat(String.valueOf(mPrice * pay_rate)) + "元");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.confirm_common_actionbar, "确认订单");
		
		mNameTxt = (TextView) findViewById(R.id.confirm_name_txt);
		mP_NameTxt = (TextView) findViewById(R.id.confirm_p_name_txt);
		mP_TypeTxt = (TextView) findViewById(R.id.confirm_p_type_txt);
		mP_PriceTxt = (TextView) findViewById(R.id.confirm_p_price_txt);
		mAllPriceTxt = (TextView) findViewById(R.id.confirm_order_all_price);
		
		mUserNameTxt = (TextView) findViewById(R.id.confirm_order_name_txt);
		mUserSexTxt = (TextView) findViewById(R.id.confirm_order_sex_txt);
		mUserPhoneTxt = (TextView) findViewById(R.id.confirm_order_phone_txt);
		mUserAddressTxt = (TextView) findViewById(R.id.confirm_order_address_txt);
		
		mIsStudentCBox = (CheckBox) findViewById(R.id.confirm_is_student_cb);
		mPrivilegeTxt = (TextView) findViewById(R.id.confirm_privilege_txt);
		mIsStudentLayout = (RelativeLayout) findViewById(R.id.confirm_is_student_layout);
		mIsStudentLayout.setVisibility(View.GONE);
		mFirstPayBlTxt = (TextView) findViewById(R.id.confirm_first_pay_bl_txt);
		mFristPayTxt = (TextView) findViewById(R.id.confirm_first_pay_txt);
		mFristPayLayout = (RelativeLayout) findViewById(R.id.confirm_first_pay_layout);
		mInfoTxt = (TextView) findViewById(R.id.confirm_p_info_txt);
		mSelectCoachNameTxt = (TextView) findViewById(R.id.confirm_select_coach_name_txt);
		
		findViewById(R.id.confirm_order_contact_txt).setOnClickListener(this);
		mOnlineTxt = (TextView) findViewById(R.id.confirm_is_online_pay_txt);
		mOnlineTxt.setOnClickListener(this);
		mOfflineTxt = (TextView) findViewById(R.id.confirm_is_offline_pay_txt);
		mOfflineTxt.setOnClickListener(this);
		
		mOnlineTxt.setSelected(true);
		
		findViewById(R.id.confirm_submit_bt).setOnClickListener(this);
		
		mIsStudentCBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				float pay_rate = Float.parseFloat(mPayObject.optString("first_pay_rate"));
				if(isChecked) {
					// 是学生
					mIsStudentLayout.setVisibility(View.VISIBLE);
					mAllPriceTxt.setText(mStuPrice + "元");
					
					// 金额
					mFristPayTxt.setText(PubUtils.moneyFormat(String.valueOf(mStuPrice * pay_rate)) + "元");
				} else {
					// 不是学生
					mIsStudentLayout.setVisibility(View.GONE);
					mAllPriceTxt.setText(mPrice + "元");
				
					// 金额
					mFristPayTxt.setText(PubUtils.moneyFormat(String.valueOf(mPrice * pay_rate)) + "元");
				}
			}
		});
		
		if("1".equals(mCoach.getIs_for_fenqi())) {
			// 允许分批
			mOnlineTxt.setVisibility(View.VISIBLE);
			mOnlineTxt.setSelected(true);
			mOfflineTxt.setSelected(false);
			mFristPayLayout.setVisibility(View.VISIBLE);
		} else {
			mOnlineTxt.setVisibility(View.GONE);
			mOnlineTxt.setSelected(false);
			mOfflineTxt.setSelected(true);
			mFristPayLayout.setVisibility(View.GONE);
		}
		
		checkLoginStatu();
		
		// 登录回调
		MyRecerver.mListenner.add(this);
	}
	
	private void checkLoginStatu() {
		// 判断是否登录，以及是否需要完善个人资料
		if (mAppContext.mUser == null) {
			// 未登录
			checkLogin(1);
		} else {
			String name = mAppContext.mUser.getUser_name();
			String phone = mAppContext.mUser.getMobile();
			String address = mAppContext.mUser.getAddress();
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
				// 未完善资料
				checkLogin(2);
			} else {
				// 已登录，已完善资料
				checkLogin(3);
			}
		}
	}

	private void checkLogin(int type) {
		mLoginType = type;
		switch (type) {
		case 1:
			findViewById(R.id.confirm_order_user_info_layout).setVisibility(View.GONE);
			findViewById(R.id.confirm_order_contact_txt).setVisibility(View.VISIBLE);
			break;
		case 2:
			findViewById(R.id.confirm_order_user_info_layout).setVisibility(View.GONE);
			findViewById(R.id.confirm_order_contact_txt).setVisibility(View.VISIBLE);
			break;
		case 3:
			findViewById(R.id.confirm_order_user_info_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.confirm_order_contact_txt).setVisibility(View.GONE);
			
			mUserNameTxt.setText(mAppContext.mUser.getUser_name());
			mUserSexTxt.setText(mAppContext.mUser.getSex());
			mUserPhoneTxt.setText(mAppContext.mUser.getMobile());
			mUserAddressTxt.setText(mAppContext.mUser.getAddress());
			break;

		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirm_order_contact_txt:
			// 完善个人资料
			Intent intent = new Intent(mContext, ContactWayActivity.class);
			intent.putExtra("type", mLoginType);	// mLoginType=1 未登录， mLoginType=2 未完善全个人信息
			startActivity(intent);
			break;
		case R.id.confirm_is_online_pay_txt:
			mOnlineTxt.setSelected(true);
			mOfflineTxt.setSelected(false);
			mFristPayLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.confirm_is_offline_pay_txt:
			mOnlineTxt.setSelected(false);
			mOfflineTxt.setSelected(true);
			mFristPayLayout.setVisibility(View.GONE);
			break;
		case R.id.confirm_submit_bt:
			// 提交订单
			if(TextUtils.isEmpty(mSelectCoachNameTxt.getText().toString())) {
				showDialogTips(mContext, "请选择教练");
				return;
			}
			// 1,先检测订单状态
			checkOrder();
			break;
		}
	}

	private void checkOrder() {
		if(mAppContext.mUser == null) {
			showDialogTipsNotCancel(mContext, "请完善个人资料!", new onDialogOkListenner() {
				@Override
				public void onClick() {
					Intent intent = new Intent(mContext, ContactWayActivity.class);
					intent.putExtra("type", mLoginType);
					startActivity(intent);
				}
			});
			return;
		}
		String url = PubConstant.REQUEST_BASE_URL + "/app_check_order.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("order_type", "XUECHE");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("mer_id", mCoach.getMer_account());
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					String result = object.optString("result");
					if("ok".equals(result)) {
						// 直接提交订单
						submitOrder();
					} else {
						if("repeated_order".equals(result)) {
							// 说明已经有订单了，
							showDialogTipsAndCancel(mContext, "您有未完成的订单", new onDialogOkListenner() {
								@Override
								public void onClick() {
									startActivity(new Intent(mContext, MyOrderActivity.class));
								}
							});
						} else {
							showDialogTips(mContext, "提交订单失败");
						}
					}
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void goPay(Map<String, String> postData, String id) {
		Order order = new Order();
		
		String price = mOfflineTxt.isSelected() ? String.valueOf(mPrice) : mFristPayTxt.getText().toString().replace("元", "");
		order.setFirst_pay_num(price);	// 金额
		order.setProduct_name(mP_TypeTxt.getText().toString());	// 班别
		order.setMer_name(mNameTxt.getText().toString());	// 驾校名称
		order.setOrder_no("");	// 订单号
		order.setMer_id(mProductObject.optString("p_mer_id"));	// 驾校帐号
		
		Intent intent = new Intent(mContext, PayActivity.class);
		intent.putExtra("type", "submit_order");
		intent.putExtra("id", id);	// 第几批
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", (Serializable) order);
		bundle.putSerializable("postData", (Serializable) postData); 
		intent.putExtras(bundle);
		startActivityForResult(intent, PubConstant.PAY_SUCCESS_CODE);
	}
	
	protected void submitOrder() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_add_order.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("order_type", "XUECHE");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("user_name", mAppContext.mUser.getUser_name());
		postData.put("user_mobile", mAppContext.mUser.getMobile());
		postData.put("user_career", mIsStudentCBox.isChecked() ? "学生" : "其他");	// 用户职业，学生有优惠
		postData.put("coach_id", mCoach.getAccount());	// 教练ID
		postData.put("mer_id", mCoach.getMer_account());
		postData.put("mer_type", "1"); // 商家类型，1表示商家，2表示个人
		postData.put("mer_name", mCoach.getMer_name());
		postData.put("product_id", mProductObject.optString("p_id"));
		postData.put("product_name", mProductObject.optString("p_name"));
		postData.put("product_price", mProductObject.optString("p_price"));
		postData.put("product_num", "");
		postData.put("class_level", mProductObject.optString("p_type"));
		postData.put("discount", "0");
		postData.put("total_price", mIsStudentCBox.isChecked() ? String.valueOf(mStuPrice) : String.valueOf(mPrice));
		postData.put("paid_money", mOfflineTxt.isSelected() ? String.valueOf(mPrice) : mFristPayTxt.getText().toString().replace("元", ""));
		postData.put("pay_channel", "alipay");	// wx / alipay  /  cash
		
		// 现在都需要先支付再提交订单了
		goPay(postData, "1");
		
//		if(isAllPay) {
//			// 如果是线上付款，需要调用支付第一批款项
//			goPay(postData, "1");
//			return;
//		}
//		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
//			@Override
//			public void responseResult(final JSONObject jsonObject) {
//				JSONObject object = jsonObject.optJSONObject("message");
//				if(object != null && !"null".equals(object)) {
//					String result = object.optString("result");
//					if("false".equals(result) || TextUtils.isEmpty(result)) {
//						// 失败
//						showDialogTips(mContext, "提交订单失败");
//					} else {
//						// 成功
//						showDialogTipsNotCancel(mContext, "提交订单成功", new onDialogOkListenner() {
//							@Override
//							public void onClick() {
//								// 订单成功后 需要关闭页面跳转瞄考首页
//								sendBroadcast(new Intent(PubConstant.SUBMIT_ORDER_SUCCESS_FINISH_KEY));
//								// 关闭页面
//								finish();
//							}
//						});
//					}
//				}
//			}
//			@Override
//			public void responseError() {
//				
//			}
//		}, true, getClass().getName());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.PAY_SUCCESS_CODE && resultCode == Activity.RESULT_OK) {
			// 订单成功后 需要关闭页面跳转瞄考首页
			sendBroadcast(new Intent(PubConstant.SUBMIT_ORDER_SUCCESS_FINISH_KEY));
			finish();
		} else if(requestCode == PubConstant.SELECT_COACH_NAME_CODE && resultCode == Activity.RESULT_OK) {
			// 显示选择的教练名称
			Coach coach = (Coach) data.getExtras().getSerializable("coach");
			mSelectCoachNameTxt.setText(coach.getName());
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
		MyRecerver.mListenner.remove(this);
	}

	@Override
	public void login(boolean isLogin) {
		checkLoginStatu();		
	}
	
}
