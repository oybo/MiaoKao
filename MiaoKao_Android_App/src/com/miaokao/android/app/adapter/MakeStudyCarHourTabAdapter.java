package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-29 下午11:16:44 
 */
public class MakeStudyCarHourTabAdapter extends CommonAdapter<String> {

	public MakeStudyCarHourTabAdapter(Context context, List<String> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, String item) {
		
		helper.setText(R.id.item_tab_hour_name, item);
		
	}

}
