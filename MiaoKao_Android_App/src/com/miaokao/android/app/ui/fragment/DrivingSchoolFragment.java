package com.miaokao.android.app.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.DrivingCoachAdapter;
import com.miaokao.android.app.adapter.DrivingSchoolAdapter;
import com.miaokao.android.app.adapter.TypeListAdapter;
import com.miaokao.android.app.entity.City;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.DrivingSchool;
import com.miaokao.android.app.entity.TopADImage;
import com.miaokao.android.app.entity.TypeSelecter;
import com.miaokao.android.app.ui.BaseFragment;
import com.miaokao.android.app.ui.activity.CutLocationActivity;
import com.miaokao.android.app.ui.activity.DrivingCoachDetailActivity;
import com.miaokao.android.app.ui.activity.DrivingSchoolDetailActivity;
import com.miaokao.android.app.ui.activity.MainActivity.OnMainCallbackListenner;
import com.miaokao.android.app.ui.activity.SearchDrivingSchoolActivity;
import com.miaokao.android.app.ui.activity.WebviewDialog;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.util.SubjectTabUtils;
import com.miaokao.android.app.util.TimeUtil;
import com.miaokao.android.app.util.ViewPagerSlideUtils;
import com.miaokao.android.app.util.ViewPagerSlideUtils.OnItemSlideOnclickListenner;
import com.miaokao.android.app.widget.HeaderView.OnLeftClickListenner;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.MListView;
import com.miaokao.android.app.widget.SwitchView;
import com.miaokao.android.app.widget.SwitchView.OnCheckedChangeListener;

public class DrivingSchoolFragment extends BaseFragment implements OnClickListener {

	private PullToRefreshScrollView mRefreshScrollView;
	private MListView mListView;
	/**      驾校列表适配器         */
	private DrivingSchoolAdapter mSchoolAdapter;
	private List<DrivingSchool> mDrivingSchools, mTempSchoolList;
	/**      教练列表适配器         */
	private DrivingCoachAdapter mCoachAdapter;
	private List<Coach> mDrivingCoachs, mTempCoachList;
	private TextView[] mTabTxts, mCopyTabTxts;
	private int mTabIndex;
	private Thread mThread;
	private Handler mHandler = new Handler();
	private OnMainCallbackListenner mMainCallBackListenner;
	private int mPage;
	private boolean isSchool;
	private String mCityTxt;
	private ViewPagerSlideUtils mPagerSlideUtils;
	private MListView mTypeListView;
	private List<TypeSelecter> mTypeLists, mTypeSelectors, mSortSelectors, mMoreSelectors, mTempSelectLists;
	/**    轮播图的Layout      */
	private RelativeLayout mSlideImageHeaderView;
	/**    科目一二等导航的列表      */
	private GridView mSubjectTabGridView;
	/**     筛选的Layout        */
	private LinearLayout mTypeHeaderView;
	/**   分类，排序，筛选 列表适配器    */
	private TypeListAdapter mTypeListAdapter;
	/**    整个筛选view      */
	private LinearLayout mScreenView;
	/**    分类 排序 筛选 取消块      */
	private LinearLayout mTabTypeCacelLayout;
	/**    筛选确认和取消块      */
	private LinearLayout mTabMoreLayout;
 	private String mRank;
 	private String longitude, latitude;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_driving_school, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		initData();
		initReceiver();
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.LOCAL_SUCCESS_KEY);
		filter.addAction(PubConstant.SUBMIT_ORDER_SUCCESS_FINISH_KEY);
		filter.addAction(PubConstant.LOADING_GUANGGAO_IAMGE_KEY);
		filter.addAction(PubConstant.REFRESH_SCHOO_LLIST);
		getActivity().registerReceiver(receiver, filter);
	}

	private void initData() {
		mTempSchoolList = new ArrayList<>();
		mTempCoachList = new ArrayList<>();
		mDrivingSchools = new ArrayList<>();
		mDrivingCoachs = new ArrayList<>();
		mSchoolAdapter = new DrivingSchoolAdapter(getActivity(), mDrivingSchools, R.layout.item_driving_school);
		mCoachAdapter = new DrivingCoachAdapter(getActivity(), mDrivingCoachs, R.layout.item_driving_coach);
		
		mTypeLists = new ArrayList<>();
		mTypeSelectors = new ArrayList<>();
		mSortSelectors = new ArrayList<>();
		mMoreSelectors = new ArrayList<>();
		mTempSelectLists = new ArrayList<>();
		
		// 1获取驾校列表
		// 2获取分类列表
		mRank = "rate";	// 初始是rate
		mPage = 0;
		switchList(true);
	}
	
	private void refreshAdapter() {
		if(isSchool) {
			mSchoolAdapter.notifyDataSetChanged();
			mListView.setAdapter(mSchoolAdapter);
		} else {
			mCoachAdapter.notifyDataSetChanged();
			mListView.setAdapter(mCoachAdapter);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initView() {
		initTopBarAllLeftRight(R.id.d_school_common_actionbar, "", R.drawable.b_s_title_down,
				new OnLeftClickListenner() {
					@Override
					public void onClick() {
						startActivityForResult(new Intent(getActivity(), CutLocationActivity.class),
								PubConstant.CUT_LOCATION_REQUEST_CODE);
					}
				}, R.drawable.sousuo, new OnRightClickListenner() {
					@Override
					public void onClick() {
						Intent intent = new Intent(getActivity(), SearchDrivingSchoolActivity.class);
						intent.putExtra("isSchool", isSchool);
						startActivity(intent);
					}
				});
		
		mCityTxt = PreferenceUtils.getInstance().getString(PubConstant.LOCAL_CITY, "深圳市");
		if (mAppContext.mAMapLocation != null) {
			mCityTxt = mAppContext.mAMapLocation.getCity();
		}
		refreshLeftTxt(mCityTxt);
		PreferenceUtils.getInstance().putString(PubConstant.LOCAL_CITY, mCityTxt);

		SwitchView switchView = (SwitchView) getActivity().findViewById(R.id.d_school_switch);
		switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(boolean isChecked) {
				switchList(isChecked);
			}
		});
		mListView = (MListView) getActivity().findViewById(R.id.driving_school_listview);
		mRefreshScrollView = (PullToRefreshScrollView) getActivity().findViewById(R.id.driving_school_refresh_scrollview);
		mRefreshScrollView.setMode(Mode.BOTH);
		mRefreshScrollView.setRefreshTime(PreferenceUtils.getInstance().getString(PubConstant.XLIST_TIME_KEY, "无"));
		mRefreshScrollView.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// 这里写下拉刷新的任务
				mPage = 0;
				getDrivingSchool(true, mPage);				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// 这里写上拉加载更多的任务
				mPage++;
				getDrivingSchool(false, mPage);				
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(isSchool) {
					Intent intent = new Intent(getActivity(), DrivingSchoolDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("drivingSchool", (Serializable) mDrivingSchools.get(arg2));
					intent.putExtras(bundle);
					startActivity(getActivity(), intent);
				} else {
					Intent intent = new Intent(getActivity(), DrivingCoachDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("coach", (Serializable) mDrivingCoachs.get(arg2));
					intent.putExtras(bundle);
					startActivity(getActivity(), intent);
				}
			}
		});
		mSlideImageHeaderView = (RelativeLayout) getActivity().findViewById(
				R.id.view_actonbar_viewpager_head_layout);
		// 设置广告高度为屏幕的1/6
		mSlideImageHeaderView.post(new Runnable() {
			@Override
			public void run() {
				int height = getResources().getDisplayMetrics().heightPixels / 7;
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						height);
				mSlideImageHeaderView.setLayoutParams(params);
			}
		});
		initViewPager();
		// addHeader - 科目导航
		LinearLayout headerTabView = (LinearLayout) getActivity()
				.findViewById(R.id.view_actionbar_gridview_head_layout);
		mSubjectTabGridView = (GridView) headerTabView.findViewById(R.id.view_d_school_gridview);
		SubjectTabUtils.getInstance().initSubTabs(getActivity(), mSubjectTabGridView);
		// -- 筛选
		mTypeHeaderView = (LinearLayout) getActivity().findViewById(R.id.view_actionbar_listview_head_layout);
		mTypeHeaderView.findViewById(R.id.b_s_lv_tab_type_layout).setOnClickListener(tabOnClickListener);
		mTypeHeaderView.findViewById(R.id.b_s_lv_tab_sort_layout).setOnClickListener(tabOnClickListener);
		mTypeHeaderView.findViewById(R.id.b_s_lv_tab_more_layout).setOnClickListener(tabOnClickListener);
		
		// ----这部分属于fragment布局里------
		mScreenView = (LinearLayout) getActivity().findViewById(R.id.driving_school_listview_screen_view);
		mTabMoreLayout = (LinearLayout) getActivity().findViewById(R.id.d_school_more_layout);
		mTabTypeCacelLayout = (LinearLayout) getActivity().findViewById(R.id.d_school_more_cancel_layout);
		mTabTypeCacelLayout.setOnClickListener(this);
		mTypeListView = (MListView) getActivity().findViewById(R.id.b_s_lv_tab_listview);
		mTypeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (mTabIndex) {
				case 0:
					// 分类
					for(TypeSelecter typeSelecter : mTypeSelectors) {
						typeSelecter.setSelect(false);
					}
					TypeSelecter type = mTypeSelectors.get(arg2);
					type.setSelect(true);
					mTypeLists.clear();
					mTypeLists.addAll(mTypeSelectors);
					
					selectMore(false);
					// 刷新数据
					mRank = type.getValue();
					mPage = 0;
					getDrivingSchool(true, mPage);
					break;
				case 1:
					// 排序
					for(TypeSelecter typeSelecter : mSortSelectors) {
						typeSelecter.setSelect(false);
					}
					TypeSelecter sort = mSortSelectors.get(arg2);
					sort.setSelect(true);
					mTypeLists.clear();
					mTypeLists.addAll(mSortSelectors);

					selectMore(false);
					// 刷新数据
					mRank = sort.getValue();
					mPage = 0;
					getDrivingSchool(true, mPage);
					break;
				case 2:
					// 筛选
					TypeSelecter more = mMoreSelectors.get(arg2);
					more.setSelect(!more.isSelect());
					mTypeLists.clear();
					mTypeLists.addAll(mMoreSelectors);
					break;
				}
				mTypeListAdapter.notifyDataSetChanged();
			}
		});
		TextView tab1 = (TextView) getActivity().findViewById(R.id.b_s_lv_tab_type_txt);
		TextView tab2 = (TextView) getActivity().findViewById(R.id.b_s_lv_tab_sort_txt);
		TextView tab3 = (TextView) getActivity().findViewById(R.id.b_s_lv_tab_more_txt);
		mTabTxts = new TextView[] { tab1, tab2, tab3 };
		mTabTxts[0].setSelected(true);
		
		LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.driving_school_listview_type_view);
		TextView t1 = (TextView) linearLayout.findViewById(R.id.b_s_lv_tab_type_txt);
		TextView t2 = (TextView) linearLayout.findViewById(R.id.b_s_lv_tab_sort_txt);
		TextView t3 = (TextView) linearLayout.findViewById(R.id.b_s_lv_tab_more_txt);
		mCopyTabTxts = new TextView[] { t1, t2, t3 };
		mCopyTabTxts[0].setSelected(true);
		linearLayout.findViewById(R.id.b_s_lv_tab_type_layout).setOnClickListener(tabOnClickListener);
		linearLayout.findViewById(R.id.b_s_lv_tab_sort_layout).setOnClickListener(tabOnClickListener);
		linearLayout.findViewById(R.id.b_s_lv_tab_more_layout).setOnClickListener(tabOnClickListener);
		
		getActivity().findViewById(R.id.d_school_more_cancel_bt).setOnClickListener(this);
		getActivity().findViewById(R.id.d_school_more_ok_bt).setOnClickListener(this);

		
		
	}

	/**
	 * 驾校与教练切换
	 * @param isChecked	true=驾校， false=教练
	 */
	protected void switchList(boolean isChecked) {
		isSchool = isChecked;
		getDrivingSchool(true, mPage);
		getTabTypes();
	}

	private void initViewPager() {
		if(mAppContext.mTopADImages != null && mAppContext.mTopADImages.size() > 0) {
			mPagerSlideUtils = new ViewPagerSlideUtils();
			mPagerSlideUtils.init(mSlideImageHeaderView, getActivity(), mAppContext.mTopADImages, new OnItemSlideOnclickListenner() {
				@Override
				public void onClick(int position) {
					TopADImage topADImage = mAppContext.mTopADImages.get(position);
					
					new WebviewDialog(getActivity(), topADImage.getAct_name(), topADImage.getUrl()).show();
				}
			});
			// 开启定时任务轮播图片
			mPagerSlideUtils.startPlay();
		}
	}

	private void getDrivingSchool(final boolean isRefresh, int page) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_merchants_service.php";
		String coach_url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";

		HashMap<String, String> postData = new HashMap<>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", isSchool ? "list" : "all_coach_list");
		postData.put("rank", mRank); // 分数排名rate 评论数量 comment_num 报名数量member_num
// 		postData.put("province", "广东省"); // 省
		postData.put("city", mCityTxt); // 市
// 		postData.put("zone", "福田区"); // 区
// 		postData.put("mer_name", ""); // 搜索关键字
		postData.put("page", page + "");
		postData.put("size", "10");
		// 经纬度
		longitude = AppContext.getInstance().mAMapLocation == null ? "" : AppContext.getInstance().mAMapLocation
				.getLongitude() + "";
		latitude = AppContext.getInstance().mAMapLocation == null ? "" : AppContext.getInstance().mAMapLocation
				.getLatitude() + "";
		postData.put("longitude", longitude);
		postData.put("latitude", latitude);

		// 分类
		for(TypeSelecter typeSelecter : mTypeSelectors) {
			if(typeSelecter.isSelect()) {
				postData.put(typeSelecter.getKey(), typeSelecter.getValue());
				break;
			}
		}
		// 排序,	默认按距离
		String sortKey = "rank";
		String sortValue = "distance";
		for(TypeSelecter typeSelecter : mSortSelectors) {
			if(typeSelecter.isSelect()) {
				sortKey = typeSelecter.getKey();
				sortValue = typeSelecter.getValue();
				break;
			}
		}
		postData.put(sortKey, sortValue);
		// 筛选
		for(TypeSelecter typeSelecter : mMoreSelectors) {
			if(typeSelecter.isSelect()) {
				postData.put(typeSelecter.getKey(), "1");
			}
		}
		mAppContext.netRequest(getActivity(), isSchool ? url : coach_url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {

				mThread = new Thread() {
					@Override
					public void run() {
						super.run();
						if(isSchool) {
							// 驾校解析
							List<DrivingSchool> tempList = PubUtils.analysisDSchool(jsonObject);
							if (isRefresh) {	// 上拉刷新
								mDrivingSchools.clear();
							} else {	// 下拉加载更多
								if (tempList.size() == 0) {	// 如果返回的数据长度为0，该页不能算，请求页码需要减1
									mPage--;
								}
							}
							mDrivingSchools.addAll(tempList);
						} else {
							// 教练解析
							mTempCoachList = PubUtils.analysisDCoach(jsonObject);
							if (isRefresh) {	// 上拉刷新
								mDrivingCoachs.clear();
							} else {	// 下拉加载更多
								if (mTempCoachList.size() == 0) {	// 如果返回的数据长度为0，该页不能算，请求页码需要减1
									mPage--;
								}
							}
							mDrivingCoachs.addAll(mTempCoachList);
						}
						
						// 主线程刷新
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								refreshAdapter();

								mRefreshScrollView.onRefreshComplete();
								if (isRefresh) {
									// 刷新时间
									String time = TimeUtil.getChatTime(System.currentTimeMillis());
									PreferenceUtils.getInstance().putString(PubConstant.XLIST_TIME_KEY, time);
									mRefreshScrollView.setRefreshTime(time);
								}
							}
						});
					}
				};
				mThread.start();
			}

			@Override
			public void responseError() {
				if(isRefresh) {
					// 请求失败，清空数据
					if(isSchool) {
						mDrivingSchools.clear();
					} else {
						mDrivingCoachs.clear();
					}
					refreshAdapter();
				}
				mRefreshScrollView.onRefreshComplete();
				if(mPage > 0) {
					mPage--;
				}
			}
		}, true, getClass().getName());
	}

	@Override
	public void refreshFragment() {
		
	}

	@Override
	public void setCallBackListenner(OnMainCallbackListenner callbackListenner) {
		this.mMainCallBackListenner = callbackListenner;
	}

	private OnClickListener tabOnClickListener = new OnClickListener() {

		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		public void onClick(View v) {
			selectMore(true);
			
			int index = 0;
			mTabTxts[mTabIndex].setSelected(false);
			mCopyTabTxts[mTabIndex].setSelected(false);
			switch (v.getId()) {
			case R.id.b_s_lv_tab_type_layout:
				// 分类
				index = 0;
				mTypeLists.clear();
				mTypeLists.addAll(mTypeSelectors);
				mTypeListAdapter.notifyDataSetChanged();
				// 隐藏筛选里的清空和确认
				mTabMoreLayout.setVisibility(View.GONE);
				break;
			case R.id.b_s_lv_tab_sort_layout:
				// 排序
				index = 1;
				mTypeLists.clear();
				mTypeLists.addAll(mSortSelectors);
				mTypeListAdapter.notifyDataSetChanged();
				// 隐藏筛选里的清空和确认
				mTabMoreLayout.setVisibility(View.GONE);
				break;
			case R.id.b_s_lv_tab_more_layout:
				// 更多筛选
				index = 2;
				mTypeLists.clear();
				mTypeLists.addAll(mMoreSelectors);
				mTypeListAdapter.notifyDataSetChanged();
				// 显示筛选里的清空和确认
				mTabMoreLayout.setVisibility(View.VISIBLE);
				break;
			}
			mTabTxts[index].setSelected(true);
			mCopyTabTxts[index].setSelected(true);
			mTabIndex = index;
		}
	};

	private void selectMore(boolean isMore) {
		if (isMore) {
//			mListView.setVisibility(View.GONE);
			
//			if(isSchool) {
//				mTempSchoolList.clear();
//				mTempSchoolList.addAll(mDrivingSchools);
//				mDrivingSchools.clear();
//			} else {
//				mTempCoachList.clear();
//				mTempCoachList.addAll(mDrivingCoachs);
//				mDrivingCoachs.clear();
//			}
//			refreshAdapter();
//			
//			if(mTempSelectLists.size() > 0) {
//				mTypeLists.clear();
//				mTypeLists.addAll(mTempSelectLists);
//				mTypeListAdapter.notifyDataSetChanged();
//			}
			
			mTypeHeaderView.setVisibility(View.GONE);
			
			mScreenView.setVisibility(View.VISIBLE);
			mTypeListView.setVisibility(View.VISIBLE);
			mTabTypeCacelLayout.setVisibility(View.VISIBLE);
		} else {
//			mListView.setVisibility(View.VISIBLE);

//			if(isSchool) {
//				mDrivingSchools.clear();
//				mDrivingSchools.addAll(mTempSchoolList);
//			} else {
//				mDrivingCoachs.clear();
//				mDrivingCoachs.addAll(mTempCoachList);
//			}
//			refreshAdapter();

			mTypeHeaderView.setVisibility(View.VISIBLE);
			
			mScreenView.setVisibility(View.GONE);
			mTypeListView.setVisibility(View.GONE);
			mTabTypeCacelLayout.setVisibility(View.GONE);
			mTabMoreLayout.setVisibility(View.GONE);
		}
	}

	private void getTabTypes() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "data_selector");
		postData.put("selector_type", isSchool ? "school" : "coach");
		mAppContext.netRequest(getActivity(), url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					mTypeLists.clear();
					mTypeSelectors.clear();
					mSortSelectors.clear();
					mMoreSelectors.clear();
					int len = jsonArray.length();
					for(int i=0; i<len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						TypeSelecter typeSelecter = new TypeSelecter();
						typeSelecter.setName(object.optString("name"));						
						typeSelecter.setType(object.optString("type"));
						typeSelecter.setKey(object.optString("key"));
						typeSelecter.setValue(object.optString("value"));
						mTypeLists.add(typeSelecter);
					}
					
					for(TypeSelecter typeSelecter : mTypeLists) {
						String type = typeSelecter.getType();
						if("classify".equals(type)) {
							typeSelecter.setTabType(1);
							mTypeSelectors.add(typeSelecter);
						} else if("order".equals(type)) {
							typeSelecter.setTabType(2);
							// 排序默认距离
							if(typeSelecter.getValue().equals("distance")) {
								typeSelecter.setSelect(true);
							}
							mSortSelectors.add(typeSelecter);
						} else {
							typeSelecter.setTabType(3);
							mMoreSelectors.add(typeSelecter);
						}
					}
					mTypeLists.clear();
					switch (mTabIndex) {
					case 0:	// 分类
						mTypeLists.addAll(mTypeSelectors);
						break;
					case 1:	// 排序
						mTypeLists.addAll(mSortSelectors);
						break;
					case 2: // 筛选
						mTypeLists.addAll(mMoreSelectors);
						break;
					}
					mTypeListAdapter = new TypeListAdapter(getActivity(), mTypeLists, R.layout.item_type_list_activity);
					mTypeListView.setAdapter(mTypeListAdapter);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, false, "getTabTypes");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.d_school_more_cancel_layout:
			// 取消分类选择
			selectMore(false);
			break;
		case R.id.d_school_more_cancel_bt:
			// 清空筛选
			for(TypeSelecter typeSelecter : mMoreSelectors) {
				typeSelecter.setSelect(false);
			}
			mTypeLists.clear();
			mTypeLists.addAll(mMoreSelectors);
			mTypeListAdapter.notifyDataSetChanged();
			
			selectMore(false);
			// 刷新数据
			mPage = 0;
			getDrivingSchool(true, mPage);
			break;
		case R.id.d_school_more_ok_bt:
			// 完成筛选
			mTempSelectLists.clear();
			mTempSelectLists.addAll(mMoreSelectors);
			selectMore(false);
			// 刷新数据
			mPage = 0;
			getDrivingSchool(true, mPage);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PubConstant.CUT_LOCATION_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				City city = (City) data.getExtras().getSerializable("city");
				mCityTxt = city.getName();
				refreshLeftTxt(mCityTxt);
				// 刷新城市数据
				mPage = 0;
				getDrivingSchool(true, mPage);
			}
			break;

		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (PubConstant.LOCAL_SUCCESS_KEY.equals(action)) {
				if (TextUtils.isEmpty(mCityTxt)) {
					if (mAppContext.mAMapLocation != null) {
						mCityTxt = mAppContext.mAMapLocation.getCity();
						if (!TextUtils.isEmpty(mCityTxt)) {
							refreshLeftTxt(mCityTxt);
							PreferenceUtils.getInstance().putString(PubConstant.LOCAL_CITY, mCityTxt);
						}
					}
				}
			} else if (PubConstant.SUBMIT_ORDER_SUCCESS_FINISH_KEY.equals(action)) {
				mMainCallBackListenner.onCurrentTab(0);
			} else if(PubConstant.LOADING_GUANGGAO_IAMGE_KEY.equals(action)) {
				// 
				if(mPagerSlideUtils == null) {
					initViewPager();
				}
				//
				SubjectTabUtils.getInstance().initSubTabs(getActivity(), mSubjectTabGridView);
			} else if(PubConstant.REFRESH_SCHOO_LLIST.equals(action)) {
				if(TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude)) {
					// 定位到了地址，如果是第一次则去刷新列表
					getDrivingSchool(true, mPage);
				}
			}
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		if(mPagerSlideUtils != null) {
			mPagerSlideUtils.setStopSlide(false);
		}
	};
	
	@Override
	public void onPause() {
		super.onPause();
		if(mPagerSlideUtils != null) {
			mPagerSlideUtils.setStopSlide(true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
		if(mPagerSlideUtils != null) {
			mPagerSlideUtils.stopPlay();
		}
	}

}
