package com.coracle_photopicker_library.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coracle_photopicker_library.CoracleManager;
import com.coracle_photopicker_library.R;
import com.coracle_photopicker_library.adapter.base.CommonAdapter;
import com.coracle_photopicker_library.adapter.base.ViewHolder;
import com.coracle_photopicker_library.utils.PhotoConstancts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhotoAdapter extends CommonAdapter<String> {

	private boolean mIsCamera;
	private List<String> mDatas;
	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public List<String> mSelectedImage = new LinkedList<String>();
	/**
	 * 最大限制数
	 */
	private int mCount;
	/**
	 * 选择回调
	 */
	private PhotoPickerListenner mPickerListenner;

	public PhotoAdapter(Context context, List<String> datas, int itemLayoutId, boolean isCamera, int count,
			PhotoPickerListenner pickerListenner) {
		super(context, datas, itemLayoutId);
		this.mDatas = datas;
		this.mIsCamera = isCamera;
		this.mCount = count;
		this.mPickerListenner = pickerListenner;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {

		final ImageView mImageView = helper.getView(R.id.photo_item_image);
		final ImageButton mSelect = helper.getView(R.id.photo_item_select_bt);
		final RelativeLayout mCamera = helper.getView(R.id.photo_item_camera);

		if(mCount == 1) {
			helper.getView(R.id.photo_item_select_layout).setVisibility(View.GONE);
		}
		
//		// 设置no_pic
//		mImageView.setImageResource(R.drawable.pictures_no);
		// 设置no_selected
		mSelect.setImageResource(R.drawable.picture_unselected);

		// 设置图片
		if (PhotoConstancts.CAMERA_TAG.equals(item)) {
			mImageView.setVisibility(View.GONE);
			mSelect.setVisibility(View.GONE);
			mCamera.setVisibility(View.VISIBLE);
			mCamera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 调用拍照
					mPickerListenner.camera();
				}
			});
		} else {
			mImageView.setVisibility(View.VISIBLE);
			mSelect.setVisibility(View.VISIBLE);
			mCamera.setVisibility(View.GONE);

			helper.setImageByUrl(R.id.photo_item_image, item);
		}

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		helper.getView(R.id.photo_item_select_layout).setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {
				// 已经选择过该图片
				if (mSelectedImage.contains(item)) {
					mSelectedImage.remove(item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else
				// 未选择该图片
				{
					if (mSelectedImage.size() >= mCount) {
						Toast.makeText(mContext, "最多选择" + mCount + "张图片!", Toast.LENGTH_SHORT).show();
						return;
					}
					mSelectedImage.add(item);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}
				mPickerListenner.select(mSelectedImage.size());
			}
		});
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCount == 1) {
					if (mSelectedImage.contains(item)) {
						mSelectedImage.remove(item);
						mSelect.setImageResource(R.drawable.picture_unselected);
						mImageView.setColorFilter(null);
					} else
					// 未选择该图片
					{
						if (mSelectedImage.size() >= mCount) {
							Toast.makeText(mContext, "最多选择" + mCount + "张图片!", Toast.LENGTH_SHORT).show();
							return;
						}
						mSelectedImage.add(item);
						mSelect.setImageResource(R.drawable.pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
					}
					mPickerListenner.select(mSelectedImage.size());
					return;
				}
				// 点击浏览图片
				int curIndex = helper.getPosition();
				List<String> tempPaths = new ArrayList<String>();
				tempPaths.addAll(mDatas);
				if(mIsCamera) {
					tempPaths.remove(0);
					curIndex = curIndex - 1;
				}

				CoracleManager.getInstance().browserImageDel(mContext, tempPaths, mSelectedImage, curIndex, mCount, new CoracleManager.onSelectPictureListenner() {
					@Override
					public void add(String path) {
						mSelectedImage.add(path);

						notifyDataSetChanged();
						mPickerListenner.select(mSelectedImage.size());
					}

					@Override
					public void del(String path) {
						mSelectedImage.remove(path);

						notifyDataSetChanged();
						mPickerListenner.select(mSelectedImage.size());
					}
				});
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(item)) {
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}

	public interface PhotoPickerListenner {
		public void camera();

		public void select(int size);
	}
}
