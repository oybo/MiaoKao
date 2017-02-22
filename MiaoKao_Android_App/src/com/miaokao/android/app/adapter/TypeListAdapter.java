package com.miaokao.android.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.TypeSelecter;

/**
 * @TODO 学车页面 分类适配器
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-2-29 下午10:51:23
 */
public class TypeListAdapter extends CommonAdapter<TypeSelecter> {

	private Drawable typeDrawable, moreDrawable;
	
	public TypeListAdapter(Context context, List<TypeSelecter> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		
		typeDrawable = mContext.getResources().getDrawable(R.drawable.checkbox_btn_selector);
		typeDrawable.setBounds(0, 0, typeDrawable.getMinimumWidth(), typeDrawable.getMinimumHeight());

		moreDrawable = mContext.getResources().getDrawable(R.drawable.switch_btn_selector);
		moreDrawable.setBounds(0, 0, moreDrawable.getMinimumWidth(), moreDrawable.getMinimumHeight());
	}

	@Override
	public void convert(ViewHolder helper, TypeSelecter item) {

		TextView textView = helper.getView(R.id.item_type_title_txt);
		textView.setText(item.getName());

		if (item.isSelect()) {
			textView.setSelected(true);
		} else {
			textView.setSelected(false);
		}

		
//		int tabType = item.getTabType();
//		if(tabType == 1 || tabType == 2) {
//			textView.setCompoundDrawables(null, null, typeDrawable, null);
//		} else {
//			textView.setCompoundDrawables(null, null, moreDrawable, null);
//		}

	}

}
