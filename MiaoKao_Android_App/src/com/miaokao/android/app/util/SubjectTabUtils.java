package com.miaokao.android.app.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.SubjectTab;
import com.miaokao.android.app.ui.activity.WebviewActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 学车页面的科目导航 管理类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-2-24 上午10:33:43
 */
public class SubjectTabUtils {
	
	private static SubjectTabUtils mInstance;
	
	public List<SubjectTab> mSubjectTabs;
	
	public synchronized static SubjectTabUtils getInstance() {
		if(mInstance == null) {
			synchronized (SubjectTabUtils.class) {
				if(mInstance == null) {
					mInstance = new SubjectTabUtils();
				}
			}
		}
		return mInstance;
	}
	
	public SubjectTabUtils() {
		mSubjectTabs = new ArrayList<>();
	}

	public void initSubTabs(final Context context, GridView gridView) {
		if(mSubjectTabs.size() > 0) {
			gridView.setVisibility(View.VISIBLE);
			// 根据rate排序
			Collections.sort(mSubjectTabs, new SubjectComparator());
			gridView.setAdapter(new MySubjectTabAdapter(context, mSubjectTabs, R.layout.item_subject_tab_activity));
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					SubjectTab subjectTab = mSubjectTabs.get(arg2);
					Intent intent = new Intent(context, WebviewActivity.class);
					intent.putExtra("title", subjectTab.getTitle());
					intent.putExtra("url", subjectTab.getUrl());
					((Activity) context).startActivity(intent);
					((Activity) context).overridePendingTransition(R.anim.in_left, R.anim.in_right);
				}
			});
		}
	}

	private class MySubjectTabAdapter extends CommonAdapter<SubjectTab> {

		public MySubjectTabAdapter(Context context, List<SubjectTab> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder helper, SubjectTab item) {
			
			ImageView imageView = helper.getView(R.id.item_subject_tab_image);
			ImageLoader.getInstance().displayImage(item.getIcon(), imageView, AppContext.getInstance().getOptions(0));
			
			helper.setText(R.id.item_subject_tab_txt, item.getTitle());
		}
		
	}
	
}
