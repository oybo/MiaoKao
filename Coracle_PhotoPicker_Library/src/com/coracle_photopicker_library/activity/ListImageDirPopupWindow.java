package com.coracle_photopicker_library.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.coracle_photopicker_library.R;
import com.coracle_photopicker_library.adapter.base.CommonAdapter;
import com.coracle_photopicker_library.adapter.base.ViewHolder;
import com.coracle_photopicker_library.entity.ImageFloder;
import com.coracle_photopicker_library.utils.BasePopupWindowForListView;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder> {
	
	private ListView mListDir;
	private int mSelectId;
	private CommonAdapter<ImageFloder> mAdapter;

	public ListImageDirPopupWindow(int width, int height, List<ImageFloder> datas, View convertView) {
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews() {
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mAdapter = new CommonAdapter<ImageFloder>(context, mDatas, R.layout.photo_list_dir_item) {
			@Override
			public void convert(ViewHolder helper, ImageFloder item) {
				helper.setText(R.id.photo_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.photo_dir_item_image, item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
				
				ImageView imageView = helper.getView(R.id.photo_dir_item_select_iv);
				imageView.setVisibility(View.GONE);
				if(mSelectId == helper.getPosition()) {
					imageView.setVisibility(View.VISIBLE);
					imageView.setImageResource(R.drawable.dir_choose);
				}
			}
		};
		mListDir.setAdapter(mAdapter);
	}

	public interface OnImageDirSelected {
		void selected(ImageFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents() {
		mListDir.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (mImageDirSelected != null) {
					mImageDirSelected.selected(mDatas.get(position));
					mSelectId = position;
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
		// TODO Auto-generated method stub
	}

}
