package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import android.widget.TextView;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.Coach;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-4 下午2:08:26 
 */
public class SelectCoachAdapter extends CommonAdapter<Coach> {

	public SelectCoachAdapter(Context context, List<Coach> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, Coach item) {
		
		TextView nameView = helper.getView(R.id.item_select_coach_name_txt);
		nameView.setText(item.getName());

		boolean useConch = item.isUseCoach();
		if(useConch) {
			nameView.setTextColor(mContext.getResources().getColor(R.color.color_E86F57));
			helper.setText(R.id.item_select_coach_tag_txt, "常用教练");
		} else {
			nameView.setTextColor(mContext.getResources().getColor(R.color.color_000000));
			helper.setText(R.id.item_select_coach_tag_txt, "");
		}
		
	}

}
