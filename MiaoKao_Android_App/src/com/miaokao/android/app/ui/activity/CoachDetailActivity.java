package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.CommentAdapter;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.MerComment;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.ImageDialog;
import com.miaokao.android.app.widget.MListView;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.miaokao.android.app.widget.SelectGradePopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CoachDetailActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private Coach mCoach;
	private RoundAngleImageView mHeadImage;
	private TextView mNameTxt;
	private TextView mInfoTxt;
	private TextView mGradeTxtTxt;
	private TextView mHGradeTxt, mZGradeTxt, mCGradeTxt;
	private MListView mListView;
	private List<MerComment> mMerComments;
	private CommentAdapter mAdapter;
	private SelectGradePopupWindow mPopupWindow;
	private Thread mThread;
	private TextView mHpTxt, mZpTxt, mCpTxt;
	private LinearLayout mHpLayout, mZpLayout, mCpLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_coach_detail);
		
		mContext = this;
		mCoach = (Coach) getIntent().getExtras().getSerializable("coach");
		
		initView();
		initData();
		getData();
	}
	
	private void getData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "detail");
		postData.put("coach_id", mCoach.getAccount());
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				// 单独再获取教练评论
				getComments();
				// 解析
				mThread = new Thread(){
					@Override
					public void run() {
						super.run();
						PubUtils.analysisCoachDetail(mCoach, jsonObject);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								initData();
							}
						});
					}
				};
				mThread.start();
			}
			
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initData() {
		// 头像
		ImageLoader.getInstance().displayImage(mCoach.getHead_img(), mHeadImage, AppContext.getInstance().getHeadImageOptions());
		// 姓名
		mNameTxt.setText(mCoach.getName());
		// 介绍
		mInfoTxt.setText(mCoach.getIntro());
		// 综合评分
		mGradeTxtTxt.setText(mCoach.getRate());
		// 好差评分数
		mHGradeTxt.setText("好评(" + (TextUtils.isEmpty(mCoach.gethRate()) ? "0" : mCoach.gethRate()) +")");
		mZGradeTxt.setText("中评(" + (TextUtils.isEmpty(mCoach.getzRate()) ? "0" : mCoach.getzRate()) +")");
		mCGradeTxt.setText("差评(" + (TextUtils.isEmpty(mCoach.getcRate()) ? "0" : mCoach.getcRate()) +")");
		// 学员评价
		if(mCoach.getComments() != null) {
			mMerComments.addAll(mCoach.getComments());
			mAdapter.notifyDataSetChanged();
		}
		mHpTxt.post(new Runnable() {
			@Override
			public void run() {
				try {
					int txtWidth = mHpTxt.getWidth();
					if(txtWidth == 0) {
						txtWidth = mHpLayout.getWidth();
					}
					int txtHeight = mHpTxt.getHeight();
					if(txtHeight == 0) {
						txtHeight = mHpLayout.getHeight();
					}
					// 这里设置各评论比例
					int hp = Integer.parseInt(mCoach.gethRate());
					int zp = Integer.parseInt(mCoach.getzRate());
					int cp = Integer.parseInt(mCoach.getcRate());
					txtWidth = txtWidth / (hp + zp + cp);
					
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(hp * txtWidth, LinearLayout.LayoutParams.MATCH_PARENT);
					mHpTxt.setLayoutParams(params);
					
					params = new LinearLayout.LayoutParams(zp * txtWidth, LinearLayout.LayoutParams.MATCH_PARENT);
					mZpTxt.setLayoutParams(params);
					
					params = new LinearLayout.LayoutParams(cp * txtWidth, LinearLayout.LayoutParams.MATCH_PARENT);
					mCpTxt.setLayoutParams(params);
					
					// layout长度
					params = new LinearLayout.LayoutParams((hp + zp + cp) * txtWidth, txtHeight);
					mHpLayout.setLayoutParams(params);
					mZpLayout.setLayoutParams(params);
					mCpLayout.setLayoutParams(params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void getComments() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_comment");
		postData.put("coach_id", mCoach.getAccount());
		postData.put("page", "0");
		postData.put("size", "30");
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				mThread = new Thread(){
					@Override
					public void run() {
						super.run();
						JSONArray jsonArray = jsonObject.optJSONArray("comment");
						if(jsonArray != null && !"null".equals(jsonArray)) {
							mMerComments.clear();
							PubUtils.analysisComment(mMerComments, jsonArray);
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									mAdapter.notifyDataSetChanged();	
								}
							});
						}
					}
				};
				mThread.start();
			}
			
			@Override
			public void responseError() {
				
			}
		}, false, "getComment");
	}

	private void initView() {
		initTopBarAll(R.id.coach_detail_common_actionbar, "教练", R.drawable.heart, new OnRightClickListenner() {
			
			@Override
			public void onClick() {
				// 判断是否登录
				// 需要登录
				if(!mAppContext.isLogin(CoachDetailActivity.this)) {
					return;
				}
				// 到评论页面
				mPopupWindow = new SelectGradePopupWindow(CoachDetailActivity.this, 2, OnClickListener);
				// 设置layout在PopupWindow中显示的位置
				mPopupWindow.showAtLocation(mHeadImage, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
			}
		});
		
		mListView = (MListView) findViewById(R.id.coach_comment_listview);
		mHeadImage = (RoundAngleImageView) findViewById(R.id.coach_detail_icon_image);
		mNameTxt = (TextView) findViewById(R.id.coach_detail_name_txt);
		mInfoTxt = (TextView) findViewById(R.id.coach_detail_user_info_txt);
		mGradeTxtTxt = (TextView) findViewById(R.id.coach_detail_grade_txt);
		mHGradeTxt = (TextView) findViewById(R.id.coach_detail_h_grade_txt);
		mZGradeTxt = (TextView) findViewById(R.id.coach_detail_z_grade_txt);
		mCGradeTxt = (TextView) findViewById(R.id.coach_detail_c_grade_txt);
		mHpLayout = (LinearLayout) findViewById(R.id.coach_detail_hp_layout);
		mZpLayout = (LinearLayout) findViewById(R.id.coach_detail_zp_layout);
		mCpLayout = (LinearLayout) findViewById(R.id.coach_detail_cp_layout);
		mHpTxt = (TextView) findViewById(R.id.coach_detail_hp_txt);
		mZpTxt = (TextView) findViewById(R.id.coach_detail_zp_txt);
		mCpTxt = (TextView) findViewById(R.id.coach_detail_cp_txt);
		
		mMerComments = new ArrayList<>();
		mAdapter = new CommentAdapter(this, mMerComments, R.layout.item_comment_activity);
		mListView.setAdapter(mAdapter);
		
		findViewById(R.id.coach_detail_look_lce_txt).setOnClickListener(this);
	}

	private View.OnClickListener OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, TalkDrivingSchoolActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("comment_list", (Serializable) mMerComments);
			intent.putExtras(bundle);
			switch (v.getId()) {
			case R.id.pop_choose_comment_xh_txt:
				// 好评
				intent.putExtra("rate", "1");
				break;
			case R.id.pop_choose_comment_zp_txt:
				// 中评
				intent.putExtra("rate", "0");
				break;
			case R.id.pop_choose_comment_bxh_txt:
				// 差评
				intent.putExtra("rate", "-1");
				break;
			}
			intent.putExtra("coach_id", mCoach.getAccount());
			startActivityForResult(intent, PubConstant.COMMENT_REQUEST_CODE);
			mPopupWindow.dismiss();
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.coach_detail_look_lce_txt:
			// 查看教练证书
			ImageDialog imageDialog = new ImageDialog(mContext, mCoach.getCer_img());
			imageDialog.show();
			break;

		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.COMMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			// 刷新评论
			getComments();
		}
	}
	
}
