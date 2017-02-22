package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 学车教练列表适配器
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-3-6 上午11:56:47
 */
public class DrivingCoachAdapter extends CommonAdapter<Coach> {

	private Handler mHandler;
	
	public DrivingCoachAdapter(Context context, List<Coach> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		mHandler = new Handler();
	}

	@Override
	public void convert(ViewHolder helper, Coach item) {
//		// 设置item显示动画
//		final View contenntView = helper.getConvertView();
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
//				translateAnimation.setDuration(300);
//				translateAnimation.setInterpolator(new AccelerateInterpolator());
//				contenntView.startAnimation(translateAnimation);
//			}
//		}, helper.getPosition() * 100);

		// 名称
		helper.setText(R.id.item_driving_coach_name_tv, item.getName());
		// 驾校
		helper.setText(R.id.item_driving_coach_school_name_tv, "(" + item.getMer_name() + ")");
		// 图片
		RoundAngleImageView imageView = helper.getView(R.id.item_driving_coach_icon_iv);
		ImageLoader.getInstance().displayImage(item.getHead_img(), imageView,
				AppContext.getInstance().getHeadImageOptions());
		// 金额
		helper.setText(R.id.item_driving_coach_price_iv, "¥ " + item.getMer_price());
		// 训练场
		helper.setText(R.id.item_driving_coach_changdi, item.getMer_name() + "训练场");
		// 说明
		helper.setText(R.id.item_driving_coach_info, item.getAge() + "年教练," + item.getRate() + "%学生满意度");

	}

}
