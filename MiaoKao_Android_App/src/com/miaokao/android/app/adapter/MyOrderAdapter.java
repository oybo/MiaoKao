package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.Order;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 我的订单适配器
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-28 下午10:37:23 
 */
public class MyOrderAdapter extends CommonAdapter<Order> {

	public MyOrderAdapter(Context context, List<Order> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, Order item) {
		// 名称
		helper.setText(R.id.item_my_order_name, item.getMer_name());
		// 订单状态
		String statusStr = "";
		if("0".equals(item.getStatus())) {
			statusStr = "订单取消";
		} else if("1".equals(item.getStatus())) {
			statusStr = "正常订单";
		} else if("2".equals(item.getStatus())) {
			statusStr = "已退款";
		} else if("3".equals(item.getStatus())) {
			statusStr = "订单完成";
		}
		helper.setText(R.id.item_my_order_state, statusStr);
		// 头像
		ImageView imageView = helper.getView(R.id.item_my_order_icon);
		ImageLoader.getInstance().displayImage(item.getMer_head_img(), imageView, AppContext.getInstance().getOptions());
		// 班别
		helper.setText(R.id.item_my_order_product_name, item.getProduct_name());
		// 时间
		helper.setText(R.id.item_my_order_time, item.getAdd_time());
		// 总价
		helper.setText(R.id.item_my_order_all_price, "总价：" + item.getTotal_price() + "元");
	}

}
