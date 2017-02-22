package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import android.widget.ImageView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.MKMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-26 上午11:59:39 
 */
public class MyMessageAdapter extends CommonAdapter<MKMessage> {

	public MyMessageAdapter(Context context, List<MKMessage> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, MKMessage item) {
		
		// 头像
//		ImageView imageView = helper.getView(R.id.my_message_head_image);
//		ImageLoader.getInstance().displayImage(item.get, imageView, AppContext.getInstance().getHeadImageOptions());
		
		// 名称
		helper.setText(R.id.my_message_name, item.getMer_name());
		// 时间
		helper.setText(R.id.my_message_time, item.getTime());
		// 内容
		helper.setText(R.id.my_message_content, item.getContent());
	}

}
