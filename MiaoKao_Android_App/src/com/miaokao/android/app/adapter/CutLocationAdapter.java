package com.miaokao.android.app.adapter;

import java.util.List;

import android.content.Context;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.City;

/**
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-17 上午10:16:20 
 */
public class CutLocationAdapter extends CommonAdapter<City> {

	public CutLocationAdapter(Context context, List<City> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, City item) {
		
		helper.setText(R.id.item_cut_localtion_txt, item.getName());
		
	}

}
