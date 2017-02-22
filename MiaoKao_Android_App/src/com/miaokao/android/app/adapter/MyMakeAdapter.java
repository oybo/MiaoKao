package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.Make;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-5 下午2:24:06 
 */
public class MyMakeAdapter extends CommonAdapter<Make> {

	public MyMakeAdapter(Context context, List<Make> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, Make item) {
		
		// 教练名称
		helper.setText(R.id.item_my_make_coach_name_txt, item.getCoach_name());
		// 训练名称
		helper.setText(R.id.item_my_make_exercise_name_txt, item.getExercise_name());
		// 日期
		helper.setText(R.id.item_my_make_date_txt, item.getR_date());
		// 时间
		helper.setText(R.id.item_my_make_time_txt, item.getTime_node());
	}

}
