package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.DSchoolDiscount;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-19 下午12:06:20
 */
public class ItemDiscountAdapter extends CommonAdapter<DSchoolDiscount> {

	public ItemDiscountAdapter(Context context, List<DSchoolDiscount> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, DSchoolDiscount item) {

		ImageView imageView = helper.getView(R.id.item_discount_icon);
		ImageLoader.getInstance().displayImage(item.getIcon(), imageView, AppContext.getInstance().getOptions());

		String text = item.getP_name() + item.getType() + item.getValue() + "元, 截止到" + item.getEnd_date();
		helper.setText(R.id.item_discount_txt, text);

	}

}
