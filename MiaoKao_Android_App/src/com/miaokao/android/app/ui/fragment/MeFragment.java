package com.miaokao.android.app.ui.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.inteface.LoginStatusListenner;
import com.miaokao.android.app.recerver.MyRecerver;
import com.miaokao.android.app.ui.BaseFragment;
import com.miaokao.android.app.ui.activity.LoginActivity;
import com.miaokao.android.app.ui.activity.MainActivity.OnMainCallbackListenner;
import com.miaokao.android.app.ui.activity.MyFeedbackActivity;
import com.miaokao.android.app.ui.activity.MyMessageActivity;
import com.miaokao.android.app.ui.activity.MyOrderActivity;
import com.miaokao.android.app.ui.activity.MySetActivity;
import com.miaokao.android.app.ui.activity.MyWalletActivity;
import com.miaokao.android.app.ui.activity.PayActivity;
import com.miaokao.android.app.ui.activity.RegisterPhoneActivity;
import com.miaokao.android.app.ui.activity.UserInfoActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.HeaderView;
import com.miaokao.android.app.widget.LastCommentDialog;
import com.miaokao.android.app.widget.LastCommentDialog.CommentCallBack;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

public class MeFragment extends BaseFragment implements OnClickListener, LoginStatusListenner {

	private LastCommentDialog mCommentDialog;
	private RoundAngleImageView mHeadImage;
	private TextView mNameTxt;
	private TextView mPhoneTxt;
	private TextView mPriceTxt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_me, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		initData();
		checkLogin();
	}

	private void initData() {
		// 头像
		if (mAppContext.mUser != null) {
			final String head_image = mAppContext.mUser.getHead_img();
			// 删除缓存
			MemoryCacheUtils.removeFromCache(head_image, ImageLoader.getInstance().getMemoryCache());
			DiskCacheUtils.removeFromCache(head_image, ImageLoader.getInstance().getDiskCache());
			
			ImageLoader.getInstance().displayImage(head_image, mHeadImage, mAppContext.getHeadImageOptions());
		}
	}

	private void initView() {
		initTopBarOnlyTitle(R.id.me_common_actionbar, "我的");
		HeaderView headerView = (HeaderView) getActivity().findViewById(R.id.me_common_actionbar);
		headerView.hindBottomLine();
		

		mHeadImage = (RoundAngleImageView) getActivity().findViewById(R.id.me_icon_image);
		mNameTxt = (TextView) getActivity().findViewById(R.id.me_fragment_username_txt);
		mPhoneTxt = (TextView) getActivity().findViewById(R.id.me_fragment_phone_txt);
		mPriceTxt = (TextView) getActivity().findViewById(R.id.me_my_wallet_txt);

		getActivity().findViewById(R.id.me_login_txt).setOnClickListener(this);
		getActivity().findViewById(R.id.me_register_txt).setOnClickListener(this);
		getActivity().findViewById(R.id.me_my_message_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.me_my_order_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.me_my_setting_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.me_fragment_login_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.me_my_wallet_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.me_feedback_layout).setOnClickListener(this);
		
		MyRecerver.mListenner.add(this);
	}

	@Override
	public void refreshFragment() {

	}

	@Override
	public void setCallBackListenner(OnMainCallbackListenner callbackListenner) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.me_login_txt:
			// 登录
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.me_register_txt:
			// 注册
			startActivity(new Intent(getActivity(), RegisterPhoneActivity.class));
			break;
		case R.id.me_fragment_login_layout:
			// 加载个人信息
			startActivityForResult(new Intent(getActivity(), UserInfoActivity.class), PubConstant.USER_INFO_CODE);
			break;
		case R.id.me_my_wallet_layout:
			// 我的钱包
			if (!mAppContext.isLogin(getActivity())) {
				return;
			}
			startActivity(new Intent(getActivity(), MyWalletActivity.class));
			break;
		case R.id.me_my_message_layout:
			// 我的消息
			if (!mAppContext.isLogin(getActivity())) {
				return;
			}
			startActivity(new Intent(getActivity(), MyMessageActivity.class));
			break;
		case R.id.me_my_order_layout:
			// 我的订单
			if (!mAppContext.isLogin(getActivity())) {
				return;
			}
			startActivity(new Intent(getActivity(), MyOrderActivity.class));
			break;
		case R.id.me_feedback_layout:
			// 意见反馈
			if (!mAppContext.isLogin(getActivity())) {
				return;
			}
			startActivity(new Intent(getActivity(), MyFeedbackActivity.class));
			break;
		case R.id.me_my_setting_layout:
			// 我的设置
			if (!mAppContext.isLogin(getActivity())) {
				return;
			}
			startActivity(new Intent(getActivity(), MySetActivity.class));
			break;
		}
	}

	private void checkLogin() {
		if (mAppContext.mUser != null) {
			// 已登录
			getActivity().findViewById(R.id.me_fragment_no_login_layout).setVisibility(View.GONE);
			getActivity().findViewById(R.id.me_fragment_login_layout).setVisibility(View.VISIBLE);

			mNameTxt.setText(mAppContext.mUser.getUser_name());
			String mobile = mAppContext.mUser.getMobile();
			if(!TextUtils.isEmpty(mobile)) {
				mobile = mobile.substring(0, 3) + "***" + mobile.subSequence(mobile.length() - 4, mobile.length());
			}
			mPhoneTxt.setText(mobile);
			String price = mAppContext.mUser.getBalance();
			mPriceTxt.setText((TextUtils.isEmpty(price) ? "0" : price) + "元");
			mPriceTxt.setVisibility(View.VISIBLE);
			
			getUncomment();
			initData();
		} else {
			getActivity().findViewById(R.id.me_fragment_no_login_layout).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.me_fragment_login_layout).setVisibility(View.GONE);
			mHeadImage.setImageResource(R.drawable.head_image_default);
			mPriceTxt.setVisibility(View.GONE);
		}
	}
	
	private void getUncomment() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_uncomment");
		mAppContext.netRequest(getActivity(), url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null && jsonArray.length() > 0) {
					try {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							// 需要评论
							mCommentDialog = new LastCommentDialog(getActivity(), jsonArray.getJSONObject(0).toString());
							mCommentDialog.setonPayListenner(new CommentCallBack() {
								@Override
								public void pay(String data) {
									Intent intent = new Intent(getActivity(), PayActivity.class);
									intent.putExtra("isPayTour", true);	// 打赏
									intent.putExtra("payTourData", data);
									startActivityForResult(intent, PubConstant.PAY_SUCCESS_CODE);
								}
							});
							mCommentDialog.show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public void responseError() {
				
			}
		}, false, "getUncomment");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.USER_INFO_CODE && UserInfoActivity.updateIcon) {
			initData();
		} else if(requestCode == PubConstant.PAY_SUCCESS_CODE && resultCode == Activity.RESULT_OK) {
			if(mCommentDialog != null && mCommentDialog.isShowing()) {
				mCommentDialog.dismiss();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyRecerver.mListenner.remove(this);
	}
	
	@Override
	public void login(boolean isLogin) {
		checkLogin();		
	}

}
