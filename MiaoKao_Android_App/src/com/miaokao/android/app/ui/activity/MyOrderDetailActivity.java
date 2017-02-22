package com.miaokao.android.app.ui.activity;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 订单详情页
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-29 上午10:52:11 
 */
public class MyOrderDetailActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private Order mOrder;
	private int mPrice;
	private ImageView mIconImage;
	private TextView mNameTxt, mDateTxt, mAllPrice;
	private TextView mFirstBLTxt, mFirstPayNumTxt, mFirstPayStatuTxt;
	private TextView mSecondBLTxt, mSecondPayNumTxt, mSecondPayStatuTxt;
	private TextView mThirdBLTxt, mThirdPayNumTxt, mThirdPayStatuTxt;
	private TextView mFourBLTxt, mFourPayNumTxt, mFourPayStatuTxt;
	private TextView mFivthBLTxt, mFivthPayNumTxt, mFivthPayStatuTxt;
	private String mBullId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_my_order_detail);
		
		mContext = this;
		mOrder = (Order) getIntent().getExtras().getSerializable("order");
		initView();
		initData();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.my_order_detail_common_actionbar, mOrder.getMer_name());
		
		mIconImage = (ImageView) findViewById(R.id.item_my_order_detail_icon);
		mNameTxt = (TextView) findViewById(R.id.item_my_order_detail_product_name);
		mDateTxt = (TextView) findViewById(R.id.item_my_order_detail_time);
		mAllPrice = (TextView) findViewById(R.id.item_my_order_detail_all_price);
		
		mFirstBLTxt = (TextView) findViewById(R.id.order_detail_first_pay_bl_txt);
		mFirstPayNumTxt = (TextView) findViewById(R.id.order_detail_first_pay_num_txt);
		mFirstPayStatuTxt = (TextView) findViewById(R.id.order_detail_first_pay_status_txt);
		
		mSecondBLTxt = (TextView) findViewById(R.id.order_detail_second_pay_bl_txt);
		mSecondPayNumTxt = (TextView) findViewById(R.id.order_detail_second_pay_num_txt);
		mSecondPayStatuTxt = (TextView) findViewById(R.id.order_detail_second_pay_status_txt);
		
		mThirdBLTxt = (TextView) findViewById(R.id.order_detail_third_pay_bl_txt);
		mThirdPayNumTxt = (TextView) findViewById(R.id.order_detail_third_pay_num_txt);
		mThirdPayStatuTxt = (TextView) findViewById(R.id.order_detail_third_pay_status_txt);
		
		mFourBLTxt = (TextView) findViewById(R.id.order_detail_fourth_pay_bl_txt);
		mFourPayNumTxt = (TextView) findViewById(R.id.order_detail_fourth_pay_num_txt);
		mFourPayStatuTxt = (TextView) findViewById(R.id.order_detail_fourth_pay_status_txt);
		
		mFivthBLTxt = (TextView) findViewById(R.id.order_detail_fivth_pay_bl_txt);
		mFivthPayNumTxt = (TextView) findViewById(R.id.order_detail_fivth_pay_num_txt);
		mFivthPayStatuTxt = (TextView) findViewById(R.id.order_detail_fivth_pay_status_txt);
		
		findViewById(R.id.item_my_order_detail_refund).setOnClickListener(this);
		findViewById(R.id.order_detail_first_pay_layout).setOnClickListener(this);
		findViewById(R.id.order_detail_second_pay_layout).setOnClickListener(this);
		findViewById(R.id.order_detail_third_pay_layout).setOnClickListener(this);
		findViewById(R.id.order_detail_fourth_pay_layout).setOnClickListener(this);
		findViewById(R.id.order_detail_fivth_pay_layout).setOnClickListener(this);
	}
	
	private void initData() {
		// 头像，名称，时间
		ImageLoader.getInstance().displayImage(mOrder.getMer_head_img(), mIconImage, mAppContext.getOptions());
		mNameTxt.setText(mOrder.getProduct_name());
		mDateTxt.setText(mOrder.getAdd_time());
		// 总计
		try {
			mPrice = Integer.parseInt(mOrder.getTotal_price());
		} catch (NumberFormatException e) {}
		
		mAllPrice.setText("总价：" + mOrder.getTotal_price() + "元");
		// 首付
		if(TextUtils.isEmpty(mOrder.getFirst_pay_num())) {
			mOrder.setFirst_pay_num("0");
		}
		if(!"0".equals(mOrder.getFirst_pay_num())) {
			String ratio = PubUtils.getPercent(Float.parseFloat(mOrder.getFirst_pay_num()), mPrice);
			mFirstBLTxt.setText("首批付款  " + ratio);
			mFirstPayNumTxt.setText("金额: " + mOrder.getFirst_pay_num() + "元");
		}
		// 二批
		if(TextUtils.isEmpty(mOrder.getSecond_pay_num())) {
			mOrder.setSecond_pay_num("0");
		}
		if(!"0".equals(mOrder.getSecond_pay_num())) {
			String ratio = PubUtils.getPercent(Float.parseFloat(mOrder.getSecond_pay_num()), mPrice);
			mSecondBLTxt.setText("二批付款  " + ratio);
			mSecondPayNumTxt.setText("金额: " + mOrder.getSecond_pay_num() + "元");
		}
		// 三批
		if(TextUtils.isEmpty(mOrder.getThird_pay_num())) {
			mOrder.setThird_pay_num("0");
		}
		if(!"0".equals(mOrder.getThird_pay_num())) {
			String ratio = PubUtils.getPercent(Float.parseFloat(mOrder.getThird_pay_num()), mPrice);
			mThirdBLTxt.setText("三批付款  " + ratio);
			mThirdPayNumTxt.setText("金额: " + mOrder.getThird_pay_num() + "元");
		}
		// 四批
		if(TextUtils.isEmpty(mOrder.getFourth_pay_num())) {
			mOrder.setFourth_pay_num("0");
		}
		if(!"0".equals(mOrder.getFourth_pay_num())) {
			String ratio = PubUtils.getPercent(Float.parseFloat(mOrder.getFourth_pay_num()), mPrice);
			mFourBLTxt.setText("四批付款  " + ratio);
			mFourPayNumTxt.setText("金额: " + mOrder.getFourth_pay_num() + "元");
		}
		// 五批
		if(TextUtils.isEmpty(mOrder.getFivth_pay_num())) {
			mOrder.setFivth_pay_num("0");
		}
		if(!"0".equals(mOrder.getFivth_pay_num())) {
			String ratio = PubUtils.getPercent(Float.parseFloat(mOrder.getFivth_pay_num()), mPrice);
			mFivthBLTxt.setText("五批付款  " + ratio);
			mFivthPayNumTxt.setText("金额: " + mOrder.getFivth_pay_num() + "元");
		}
		// 显示几批
		if("0".equals(mOrder.getFirst_pay_num())) {
			visible(1);
		} else {
			if("0".equals(mOrder.getSecond_pay_num())) {
				visible(2);
			} else {
				if("0".equals(mOrder.getThird_pay_num())) {
					visible(3);
				} else {
					if("0".equals(mOrder.getFourth_pay_num())) {
						visible(4);
					} else {
						if("0".equals(mOrder.getFivth_pay_num())) {
							visible(5);
						}
					}
				}
			}
		}
		// 支付状态
		int pay_status = Integer.parseInt(mOrder.getPay_status());
		switch (pay_status) {
		case 0:
			mFirstPayStatuTxt.setText("去付款");
			mFirstPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_gopay_selector);
			mSecondPayStatuTxt.setText("待确认");
			mSecondPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			mThirdPayStatuTxt.setText("待确认");
			mThirdPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			mFourPayStatuTxt.setText("待确认");
			mFourPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			mFivthPayStatuTxt.setText("待确认");
			mFivthPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			break;
		case 1:
			mFirstPayStatuTxt.setText("已支付");
			mFirstPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mSecondPayStatuTxt.setText("去付款");
			mSecondPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_gopay_selector);
			mThirdPayStatuTxt.setText("待确认");
			mThirdPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			mFourPayStatuTxt.setText("待确认");
			mFourPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			mFivthPayStatuTxt.setText("待确认");
			mFivthPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			break;
		case 2:
			mFirstPayStatuTxt.setText("已支付");
			mFirstPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mSecondPayStatuTxt.setText("已支付");
			mSecondPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mThirdPayStatuTxt.setText("去付款");
			mThirdPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_gopay_selector);
			mFourPayStatuTxt.setText("待确认");
			mFourPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			mFivthPayStatuTxt.setText("待确认");
			mFivthPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			break;
		case 3:
			mFirstPayStatuTxt.setText("已支付");
			mFirstPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mSecondPayStatuTxt.setText("已支付");
			mSecondPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mThirdPayStatuTxt.setText("已支付");
			mThirdPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mFourPayStatuTxt.setText("去付款");
			mFourPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_gopay_selector);
			mFivthPayStatuTxt.setText("待确认");
			mFivthPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_dqr_selector);
			break;
		case 4:
			mFirstPayStatuTxt.setText("已支付");
			mFirstPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mSecondPayStatuTxt.setText("已支付");
			mSecondPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mThirdPayStatuTxt.setText("待确认");
			mThirdPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mFourPayStatuTxt.setText("已支付");
			mFourPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mFivthPayStatuTxt.setText("去支付");
			mFivthPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_gopay_selector);
			break;
		case 5:
			mFirstPayStatuTxt.setText("已支付");
			mFirstPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mSecondPayStatuTxt.setText("已支付");
			mSecondPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mThirdPayStatuTxt.setText("已支付");
			mThirdPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mFourPayStatuTxt.setText("已支付");
			mFourPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			mFivthPayStatuTxt.setText("待确认");
			mFivthPayStatuTxt.setBackgroundResource(R.drawable.order_pay_statu_ok_selector);
			break;
			
		}
	}
	
	private void visible(int position) {
		switch (position) {
		case 1:
			findViewById(R.id.order_detail_first_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_second_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_third_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fourth_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fivth_pay_layout).setVisibility(View.GONE);
			break;
		case 2:
			findViewById(R.id.order_detail_second_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_third_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fourth_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fivth_pay_layout).setVisibility(View.GONE);
			break;
		case 3:
			findViewById(R.id.order_detail_third_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fourth_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fivth_pay_layout).setVisibility(View.GONE);
			break;
		case 4:
			findViewById(R.id.order_detail_fourth_pay_layout).setVisibility(View.GONE);
			findViewById(R.id.order_detail_fivth_pay_layout).setVisibility(View.GONE);
			break;
		case 5:
			findViewById(R.id.order_detail_fivth_pay_layout).setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_my_order_detail_refund:
			// 申请退款
			if(!"0".equals(mOrder.getPay_status())) {
				Intent intent = new Intent(mContext, ApplyRefundActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrder);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		case R.id.order_detail_first_pay_layout:
			// 首付
			String payTxt = mFirstPayStatuTxt.getText().toString();
			orderOnclick(payTxt, "1");
			break;
		case R.id.order_detail_second_pay_layout:
			// 二批
			payTxt = mSecondPayStatuTxt.getText().toString();
			orderOnclick(payTxt, "2");
			break;
		case R.id.order_detail_third_pay_layout:
			// 三批
			payTxt = mThirdPayStatuTxt.getText().toString();
			orderOnclick(payTxt, "3");
			break;
		case R.id.order_detail_fourth_pay_layout:
			// 四批
			payTxt = mFourPayStatuTxt.getText().toString();
			orderOnclick(payTxt, "4");
			break;
		case R.id.order_detail_fivth_pay_layout:
			// 五批
			payTxt = mFivthPayStatuTxt.getText().toString();
			orderOnclick(payTxt, "5");
			break;
		}
	}
	
	private void orderOnclick(String payTxt, String id) {
		if("已支付".equals(payTxt)) {
			Intent intent = new Intent(mContext, OrderDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("order", (Serializable) mOrder);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if("去付款".equals(payTxt)) {
			goPay(id);
		}
	}

	private void goPay(String id) {
		mBullId = id;
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
			//刷新状态  ?  到底要怎么弄
			mOrder.setPay_status(mBullId);
			initData();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
