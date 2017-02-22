package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.MakeDate;

/**
 * @TODO 科目二中预约时间日期
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-29 下午11:16:44 
 */
public class MakeStudyCarWeekAdapter extends CommonAdapter<MakeDate> {

	public MakeStudyCarWeekAdapter(Context context, List<MakeDate> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, MakeDate item) {
		
		// 周几
		helper.setText(R.id.item_week_name, item.getWeekValue());
		
		// 日期
		helper.setText(R.id.item_week_date, item.getDate());
	}

}
