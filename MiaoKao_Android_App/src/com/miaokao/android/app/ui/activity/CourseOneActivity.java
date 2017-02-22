package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.AlertDialogPopupWindow;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;

/**
 * @TODO 科目一
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-3 下午4:35:07 
 */
public class CourseOneActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private String mBill_txt;
	private String mBill_num;
	private Order mOrder;
	private TextView mInfoTxt;
	private AlertDialogPopupWindow mDialogPopupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_one);
		
		Intent intent = getIntent();
		mBill_txt = intent.getStringExtra("bill_txt");
		mBill_num = intent.getStringExtra("bill_num");
		mOrder = (Order) intent.getExtras().getSerializable("order");
		mContext = this;
		initView();
		
		if("1".equals(mBill_num)) {
			mInfoTxt.setText(getString(R.string.course_one_info_txt));
		} else if("4".equals(mBill_num)) {
			mInfoTxt.setText(getString(R.string.course_four_info_txt));
		}
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.course_one_common_actionbar, mBill_txt);
		initTopBarAll(R.id.course_one_common_actionbar, mBill_txt, R.drawable.gengduo, new OnRightClickListenner() {
			@Override
			public void onClick() {
				String title = "";
				if("1".equals(mBill_num)) {
					// 科目一
					if("alipay".equals(mOrder.getPay_channel())) {
						title = "确认完成后，请支付驾考培训费用第二批款项";
					} else {
						// 现金付款，直接调用完成
						title = "确定本阶段完成学习?";
					}
				} else if("4".equals(mBill_num)) {
					// 科目四
					title = "确定本阶段完成学习?";
				}
				
				mDialogPopupWindow = new AlertDialogPopupWindow(CourseOneActivity.this, title, "确认支付", "", itemsOnClick);
				mDialogPopupWindow.showAtLocation(mInfoTxt, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			}
		});
		
		mInfoTxt = (TextView) findViewById(R.id.course_info_txt);
		findViewById(R.id.course_one_ok_bt).setOnClickListener(this);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			mDialogPopupWindow.dismiss();
			switch (v.getId()) {
			case R.id.alert_dialog_ok_bt:
				// 确定
				if("1".equals(mBill_num)) {
					// 科目一
					if("alipay".equals(mOrder.getPay_channel())) {
						goPay("2");
					} else {
						// 现金付款，直接调用完成
						affirm();
					}
				} else if("4".equals(mBill_num)) {
					// 科目四
					affirm();
				}
				break;
			case R.id.alert_dialog_cancel_bt:
				// 取消

				break;
			}
		}

	};

	private void affirm() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "study_status");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("bill_num", (Integer.parseInt(mBill_num) + 1) + "");
		postData.put("order_no", mOrder.getOrder_no());
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if("ok".equals(result)) {
					// 确认完成 退出 返回到首页。
					sendBroadcast(new Intent(PubConstant.MAKE_SUCCESS_FINISH_KEY));
					finish();
				} else {
					showDialogTips(mContext, result);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, "affirm");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.course_one_ok_bt:
			// 去做题库
			String title = "";
			String url = "";
			if("1".equals(mBill_num)) {
				// 科目一
				title = "科目一";
				url = "http://www.qinghuayu.com/running/km1.html";
			} else if("4".equals(mBill_num)) {
				// 科目四
				title = "科目四";
				url = "http://www.qinghuayu.com/running/km4.html";
			}
			Intent intent = new Intent(mContext, WebviewActivity.class);
			intent.putExtra("title", title);
			intent.putExtra("url", url);
			startActivity(intent);
			overridePendingTransition(R.anim.in_left, R.anim.in_right);
			break;
		}
	}
	
	private void goPay(String id) {
		Intent intent = new Intent(mContext, PayActivity.class);
		intent.putExtra("id", id);	// 第几批
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", (Serializable) mOrder);
		intent.putExtras(bundle);
		startActivityForResult(intent, PubConstant.PAY_SUCCESS_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.PAY_SUCCESS_CODE && resultCode == Activity.RESULT_OK) {
			finish();
		}
	}
	
}
