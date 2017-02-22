package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.TrainingGround;

/**
 * @TODO 选择场地地址 & 接送地址
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-22 下午3:05:31 
 */
public class TrainingGroundAdapter extends CommonAdapter<TrainingGround> {
	
	public TrainingGroundAdapter(Context context, List<TrainingGround> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, TrainingGround item) {
		
		helper.setText(R.id.item_training_ground_name_txt, item.getSup_name());

		helper.setText(R.id.item_training_ground_code_txt, "(场地代码：" + item.getSup_no() + ")");
		
		TextView selectTxt = helper.getView(R.id.item_training_ground_isselect_txt);
		if(item.isSelect()) {
			selectTxt.setSelected(true);
		} else {
			selectTxt.setSelected(false);
		}
	}

}
