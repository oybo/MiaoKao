package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.DSchoolCourse;
import com.miaokao.android.app.util.PubUtils;

public class DSchoolCourseAdapter extends CommonAdapter<DSchoolCourse> {

	public DSchoolCourseAdapter(Context context, List<DSchoolCourse> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, DSchoolCourse item) {
		
		// 类型
		helper.setText(R.id.item_d_s_course_type, item.getP_type());
		// 班别
		helper.setText(R.id.item_d_s_course_name, item.getP_name());
		// 时间
		helper.setText(R.id.item_d_s_course_time, item.getTime_node());
		// 原价   打折假
		String p_price = item.getP_price();	// 原价
		String discount_price = item.getDiscount_price();	// 打折假
		
		final TextView pPriceTxt = helper.getView(R.id.item_d_s_course_p_price);
		pPriceTxt.setText(p_price + "元");
		
		TextView discountPriceTxt = helper.getView(R.id.item_d_s_course_stu_price);

		if(TextUtils.isEmpty(discount_price)) {
			discountPriceTxt.setVisibility(View.GONE);
		} else {
			discountPriceTxt.setText(discount_price + "元");
			discountPriceTxt.setVisibility(View.VISIBLE);
			// 加上删除线
			final View lineView = helper.getView(R.id.item_d_s_course_p_price_line_view);
			pPriceTxt.post(new Runnable() {
				@Override
				public void run() {
					int width = pPriceTxt.getWidth();
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, PubUtils.dip2px(mContext, 1));
					params.addRule(RelativeLayout.CENTER_IN_PARENT);
					lineView.setLayoutParams(params);
					lineView.setVisibility(View.VISIBLE);
				}
			});
		}
	}

}
