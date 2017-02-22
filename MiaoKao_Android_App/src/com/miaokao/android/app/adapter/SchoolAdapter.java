package com.miaokao.android.app.adapter;

import java.util.List;
import android.content.Context;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.School;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-27 上午11:34:25 
 */
public class SchoolAdapter extends CommonAdapter<School> {

	public SchoolAdapter(Context context, List<School> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder helper, School item) {
		
		helper.setText(R.id.item_school_name, item.getName());
	}

}
