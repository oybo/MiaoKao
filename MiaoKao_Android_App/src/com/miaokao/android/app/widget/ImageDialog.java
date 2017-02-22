package com.miaokao.android.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 自定义图片查看Dialog
 * 
 * @author ouyangbo
 * 
 */
public class ImageDialog extends Dialog {

	private Context mContext;
	private String mPath;

	public ImageDialog(Context context, String path) {
		this(context, R.style.signin_dialog_style, path);
	}

	public ImageDialog(Context context, int theme) {
		this(context, theme, "");
	}

	public ImageDialog(Context context, int theme, String path) {
		super(context, theme);
		this.mContext = context;
		this.mPath = path;

		init();
	}

	private void init() {
		setContentView(R.layout.dialog_image_view);
		this.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT; // 必须加这句

		setImage();

		findViewById(R.id.dialog_image_ok_bt).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void setImage() {
		if (!TextUtils.isEmpty(mPath)) {
			ImageView imageView = (ImageView) findViewById(R.id.dialog_image_iv);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.CENTER);
			ImageLoader.getInstance().displayImage(mPath, imageView,
					AppContext.getInstance().getAllOptions(R.drawable.image_location_loading),
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

						}

						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							((ImageView) arg1).setScaleType(ScaleType.FIT_XY);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {

						}
					});

		}
	}

}
