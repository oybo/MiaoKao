package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import android.widget.ImageView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.Consume;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-3-10 下午5:47:01 
 */
public class ConsumeDetailsAdapter extends CommonAdapter<Consume> {

	public ConsumeDetailsAdapter(Context context, List<Consume> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, Consume item) {
		
		// 时间
		helper.setText(R.id.item_consume_detail_date_txt, item.getTime());
		// 图标
		ImageView imageView = helper.getView(R.id.item_consume_detail_type_image);
		String type = item.getTrans_type();
		if("prize".equals(type)) {
			// 打赏
			imageView.setImageResource(R.drawable.play_four_icon);
		} else if("study".equals(type)) {
			// 学费
			imageView.setImageResource(R.drawable.play_four_icon);
		} else if("add".equals(type)) {
			// 
			imageView.setImageResource(R.drawable.play_four_icon);
		} else {
			imageView.setImageResource(R.drawable.play_four_icon);
		}
		
		// 金额
		helper.setText(R.id.item_consume_detail_price_txt, "-" + item.getTotal_fee());
		// 打赏给谁
		helper.setText(R.id.item_consume_detail_name_txt, item.getCoach_name());
		// 物品名称和数量
		helper.setText(R.id.item_consume_detail_article_txt, item.getName() + " x " + item.getPrize_num());
		
	}

}
