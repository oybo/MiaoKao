package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.MerComment;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-21 上午11:23:39 
 */
public class CommentAdapter extends CommonAdapter<MerComment> {

	public CommentAdapter(Context context, List<MerComment> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, MerComment item) {
		
		// 头像
		RoundAngleImageView imageView = helper.getView(R.id.item_comment_image);
		ImageLoader.getInstance().displayImage("", imageView, AppContext.getInstance().getHeadImageOptions());
		
		// 帐号
		helper.setText(R.id.item_comment_account, item.getUser_id());
		
		//时间
		helper.setText(R.id.item_comment_time, item.getTime());

		// 评论类型
		String rate = item.getRate();
		if("-1".equals(rate)) {
			helper.setText(R.id.item_comment_type, "差评");
		} else if("0".equals(rate)) {
			helper.setText(R.id.item_comment_type, "中评");
		} else if("1".equals(rate)) {
			helper.setText(R.id.item_comment_type, "好评");
		}
		
		//评论
		helper.setText(R.id.item_comment_info_txt, item.getContent());
		
	}

}
