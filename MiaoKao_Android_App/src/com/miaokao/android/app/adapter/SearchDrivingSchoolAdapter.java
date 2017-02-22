package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import android.widget.TextView;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.DrivingSchool;

/**
 * @TODO 驾校搜索适配器
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-18 下午4:11:35 
 */
public class SearchDrivingSchoolAdapter extends CommonAdapter<DrivingSchool> {

	public SearchDrivingSchoolAdapter(Context context, List<DrivingSchool> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, DrivingSchool item) {
		
		// 名称
		TextView textView = helper.getView(R.id.item_search_dschool_name);
		textView.setText(((DrivingSchool)item).getMer_name());
		
		// 评分
		TextView txt = helper.getView(R.id.item_search_dschool_grade);
		txt.setText(((DrivingSchool)item).getMer_rate() + "分");
		
	}

}
