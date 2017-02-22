package com.miaokao.android.app.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.ConsumeDetailsAdapter;
import com.miaokao.android.app.entity.Consume;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.MListView;

/**
 * @TODO 我的钱包 账单明细
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-3-10 下午5:03:48
 */
public class ConsumeDetailsActivity extends BaseActivity {

	private Context mContext;
	private MListView mListView;
	private List<Consume> mConsumes;
	private ConsumeDetailsAdapter mAdapter;
	private PullToRefreshScrollView mRefreshScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consume_details);

		mContext = this;
		initView();
		
		mConsumes = new ArrayList<>();
		mAdapter = new ConsumeDetailsAdapter(mContext, mConsumes, R.layout.item_consume_detail_activity);
		mListView.setAdapter(mAdapter);
		
		getData();
	}

	private void getData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_trans");
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				mRefreshScrollView.onRefreshComplete();
				
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null && jsonArray.length() > 0) {
					mConsumes.clear();
					int len = jsonArray.length();
					for(int i=0; i<len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						if(object != null && !"null".equals(object)) {
							Consume consume = new Consume();
							consume.setCoach_id(object.optString("coach_id"));
							consume.setCoach_name(object.optString("coach_name"));
							consume.setName(object.optString("name"));
							consume.setPrize_num(object.optString("prize_num"));
							consume.setTime(object.optString("time"));
							consume.setTotal_fee(object.optString("total_fee"));
							consume.setTrans_type(object.optString("trans_type"));
							mConsumes.add(consume);
						}
					}
					
					mAdapter.notifyDataSetChanged();
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.consume_detail_common_actionbar, "账单明细");

		mListView = (MListView) findViewById(R.id.consume_detail_listview);

		mRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.consume_detail_refresh_scrollview);
		mRefreshScrollView.setMode(Mode.PULL_FROM_START);
		mRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				getData();
			}
		});
		
	}

}
