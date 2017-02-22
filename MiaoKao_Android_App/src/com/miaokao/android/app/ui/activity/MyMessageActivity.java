package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.MyMessageAdapter;
import com.miaokao.android.app.entity.MKMessage;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.util.ToastFactory;
import com.miaokao.android.app.widget.swipemenulistview.SwipeMenu;
import com.miaokao.android.app.widget.swipemenulistview.SwipeMenuCreator;
import com.miaokao.android.app.widget.swipemenulistview.SwipeMenuItem;
import com.miaokao.android.app.widget.swipemenulistview.SwipeMenuListView;
import com.miaokao.android.app.widget.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

/**
 * @TODO 我的消息页面
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-24 上午9:27:18
 */
public class MyMessageActivity extends BaseActivity {

	private Context mContext;
	private SwipeMenuListView mListView;
	private List<MKMessage> mMkMessages;
	private MyMessageAdapter mAdapter;
	private Thread mThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_message);

		mContext = this;
		initView();
		initData();
		getData();
	}

	private void getData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_message");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				
				mThread = new Thread(){
					public void run() {
						PubUtils.analysisMyMessage(mMkMessages, jsonObject);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mAdapter.notifyDataSetChanged();	
							}
						});
					};
				};
				mThread.start();
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initData() {
		mMkMessages = new ArrayList<>();
		mAdapter = new MyMessageAdapter(mContext, mMkMessages, R.layout.item_my_message_activity);
		mListView.setAdapter(mAdapter);
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.my_message_common_actionbar, "我的消息");

		mListView = (SwipeMenuListView) findViewById(R.id.my_message_listView);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(PubUtils.dip2px(mContext, 70));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);
		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// delete
					deleteItem(position);
					break;
				}
			}
		});
		// other setting
//		mListView.setCloseInterpolator(new BounceInterpolator());
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MKMessage message =  mMkMessages.get(position);
				Intent intent = new Intent(mContext, MyMessageDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("message", (Serializable) message);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	protected void deleteItem(final int position) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "del_message");
		postData.put("msg_id", mMkMessages.get(position).getId());
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if("ok".equals(result)) {
					ToastFactory.getToast(mContext, "删除成功").show();
					
					mMkMessages.remove(position);
					mAdapter.notifyDataSetChanged();
				} else {
					ToastFactory.getToast(mContext, "删除失败").show();
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
