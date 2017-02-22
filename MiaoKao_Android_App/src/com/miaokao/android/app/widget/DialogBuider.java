package com.miaokao.android.app.widget;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.TrainingGround;

/**
 * @TODO 训练场
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-4 下午12:49:13 
 */
public class DialogBuider extends Dialog {

	private ListView mListView;
	
	public DialogBuider(Context context, List<TrainingGround> listStrs, OnItemClickListener listener) {
		super(context, R.style.signin_dialog_style);
		
		init(context, listStrs, listener);
	}

	private void init(Context context, List<TrainingGround> listStrs, OnItemClickListener listener) {
		setContentView(R.layout.dialog_buider_activity);
		this.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT; // 必须加这句
		
		mListView = (ListView) findViewById(R.id.dialog_buider_lv);
		
		mListView.setAdapter(new DialogBuiderAdapter(context, listStrs, R.layout.item_dialog_buider_activity));
		
		mListView.setOnItemClickListener(listener);
	}

	private class DialogBuiderAdapter extends CommonAdapter<TrainingGround> {

		public DialogBuiderAdapter(Context context, List<TrainingGround> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder helper, TrainingGround item) {
			
			helper.setText(R.id.item_dialog_buider_txt, item.getSup_name());
		}
		
	}
	
	
}
