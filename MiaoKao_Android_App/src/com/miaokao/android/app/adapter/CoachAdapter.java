package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CoachAdapter extends CommonAdapter<Coach> {

	public CoachAdapter(Context context, List<Coach> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, Coach item) {

		// 头像
		RoundAngleImageView imageView = helper.getView(R.id.item_coach_icon_image);
		ImageLoader.getInstance().displayImage(item.getHead_img(), imageView, AppContext.getInstance().getHeadImageOptions());
		// 姓名
		helper.setText(R.id.item_coach_name_txt, item.getName());
		// 姓名
		helper.setText(R.id.item_coach_time_txt, item.getTime());
		// 评分
		helper.setText(R.id.item_coach_rate_txt, item.getRate());

	}

}
