package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.CoachArrange;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-29 下午11:16:44 
 */
public class MakeStudyCarHourAdapter extends CommonAdapter<CoachArrange> {

	private String mUserId;
	
	public MakeStudyCarHourAdapter(Context context, List<CoachArrange> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		mUserId = AppContext.getInstance().mUser.getLoginName();
	}

	@Override
	public void convert(ViewHolder helper, CoachArrange item) {
		
		TextView textView = helper.getView(R.id.item_hour_date_txt);
		
		if(null != item) {
			if(mUserId.equals(item.getUser_id())) {
				// 已预约
				textView.setBackgroundResource(R.color.make_F4D216);
			} else {
				// 不可用
				textView.setBackgroundResource(R.color.make_99111111);
			}
		} else {
			textView.setBackgroundResource(R.drawable.bt_bg_selector);
		}
	}

}
