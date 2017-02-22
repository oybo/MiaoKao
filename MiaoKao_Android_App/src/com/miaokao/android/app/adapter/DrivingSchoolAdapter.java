package com.miaokao.android.app.adapter;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.DSchoolDiscount;
import com.miaokao.android.app.entity.DrivingSchool;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.MListView;
import com.miaokao.android.app.widget.MessagePopupWindow;
import com.miaokao.android.app.widget.RatingBarView;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 学车驾校列表适配器
 * @author Administrator
 *
 */
public class DrivingSchoolAdapter extends CommonAdapter<DrivingSchool> {

	private Handler mHandler;
	private MessagePopupWindow mPopupWindow;
	
	public DrivingSchoolAdapter(Context context, List<DrivingSchool> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		mHandler = new Handler();
	}

	@Override
	public void convert(ViewHolder helper, DrivingSchool item) {
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
		helper.setText(R.id.item_driving_name_tv, item.getMer_name());
		// 图片
		RoundAngleImageView imageView = helper.getView(R.id.item_driving_icon_iv);
		ImageLoader.getInstance()
				.displayImage(item.getMer_head_img(), imageView, AppContext.getInstance().getHeadImageOptions());
		// 评分
		int rate = 0;
		try {
			rate = Integer.parseInt(item.getMer_rate().substring(0, item.getMer_rate().indexOf(".")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		RatingBarView ratingBar = helper.getView(R.id.item_driving_pf_rating);
		ratingBar.setRating(rate);
		helper.setText(R.id.item_driving_grade_txt, item.getMer_rate() + "分");
		// 报名数
		helper.setText(R.id.item_driving_apply_txt, item.getMer_member_num() + "人已报名");
		// 起
		helper.setText(R.id.item_driving_price_iv, item.getLowerst_price() + " 起");
		
		// 保 分
		final ImageView baoView = helper.getView(R.id.item_driving_bao_txt);
		if ("1".equals(item.getIs_for_return())) {
			baoView.setVisibility(View.VISIBLE);
		} else {
			baoView.setVisibility(View.GONE);
		}
		
		final ImageView fenView = helper.getView(R.id.item_driving_fen_txt);
		if ("1".equals(item.getIs_for_fenqi())) {
			fenView.setVisibility(View.VISIBLE);
		} else {
			fenView.setVisibility(View.GONE);
		}
		// 距离
		if(AppContext.getInstance().mAMapLocation != null) {
			double longitude = AppContext.getInstance().mAMapLocation.getLongitude();
			double latitude = AppContext.getInstance().mAMapLocation.getLatitude();
			
			helper.setText(
					R.id.item_driving_distance,
					PubUtils.Distance(longitude, latitude, Double.parseDouble(item.getMer_longitude()),
							Double.parseDouble(item.getMer_latitude())));
		}
		// 减
		MListView listView = helper.getView(R.id.item_d_s_discount_listview);
		List<DSchoolDiscount> schoolDiscounts = item.getdSchoolDiscounts();
		if (schoolDiscounts != null && schoolDiscounts.size() > 0) {
			listView.setAdapter(new ItemDiscountAdapter(mContext, schoolDiscounts, R.layout.item_discount_activity));
		}

		baoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow = new MessagePopupWindow((Activity) mContext, "商户加入\"学习保障计划\",学习有保障");
				mPopupWindow.showTop(baoView);
			}
		});

		fenView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow = new MessagePopupWindow((Activity) mContext, "支持学费分批支付");
				mPopupWindow.showTop(fenView);
			}
		});
	}

}
