package com.miaokao.android.app.widget;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import com.coracle_share_library.ShareUtils;
import com.coracle_share_library.ShareUtils.ShareCallBackListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;

/**
 * @TODO 弹出分享选择
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-22 下午10:07:00
 */
public class SharePopupWindow extends PopupWindow implements OnClickListener {

	private static final List<ShareEntity> SHARE_ENTITIES = new ArrayList<>();
	static {
		// 微信好友
		SHARE_ENTITIES.add(new ShareEntity(R.drawable.share_wy_friend, "微信好友"));
		// 微信朋友圈
		SHARE_ENTITIES.add(new ShareEntity(R.drawable.share_wy_pyq, "朋友圈"));
		// QQ好友
		SHARE_ENTITIES.add(new ShareEntity(R.drawable.share_qq_friend, "QQ好友"));
		// 新浪微博
		SHARE_ENTITIES.add(new ShareEntity(R.drawable.share_xl_wb, "新浪微博"));
	}

	private int mCount;
	private Context mContext;
	private Window mWindow;
	private String mTitle;
	private String mContent;
	private String mImageUrl;
	private String mUrl;
	
	private DissListenner mDissListenner;

	public SharePopupWindow(Context context, Window window, String title, String content, String imageUrl, String url) {
		super(context);
		
		this.mContext = context;
		this.mWindow = window;
		this.mTitle = title;
		this.mContent = content;
		this.mImageUrl = imageUrl;
		this.mUrl = url;

		init();
	}

	private void init() {
		final View popupView = View.inflate(mContext, R.layout.view_share_pop_layout, null);

		MGirdView gridview = (MGirdView) popupView.findViewById(R.id.pop_choose_share_gridview);
		initGridview(gridview);

		// 设置SelectPicPopupWindow的View
		this.setContentView(popupView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.anim_popup_dir);
		setBackgroundDrawable(new BitmapDrawable());

		new CountDownTimer(175, 25) {
			@Override
			public void onTick(long millisUntilFinished) {
				mCount++;
				backgroundAlpha(mWindow, (float) (1 - (0.1 * mCount)));
			}

			@Override
			public void onFinish() {

			}
		}.start();

		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				backgroundAlpha(mWindow, 1f);
				if(mDissListenner != null) {
					mDissListenner.dissmiss();
				}
			}
		});
	}

	private void initGridview(MGirdView gridview) {
		gridview.setAdapter(new MySharePopAdapter(mContext, SHARE_ENTITIES, R.layout.item_share_pop_view));
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				dismiss();
				switch (arg2) {
				case 0:
					// 微信好友
					wxShare();
					break;
				case 1:
					// 微信朋友圈
					wxPyjShare();
					break;
				case 2:
					// QQ好友

					break;
				case 3:
					// 新浪微博

					break;
				}
			}
		});
	}

	private void wxShare() {
		Bitmap bitmap = null;
		if (TextUtils.isEmpty(mImageUrl)) {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
		}
		ShareUtils.getInstance(mContext).wechatShare(mTitle, mContent, bitmap, mImageUrl, "", mUrl,
				new ShareCallBackListenner() {

					@Override
					public void success() {

					}

					@Override
					public void error() {

					}

					@Override
					public void cancel() {

					}
				});
	}

	private void wxPyjShare() {
		Bitmap bitmap = null;
		if (TextUtils.isEmpty(mImageUrl)) {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
		}
		ShareUtils.getInstance(mContext).wechatMomentsShare(mTitle, mContent, bitmap, mImageUrl, "", mUrl,
				new ShareCallBackListenner() {

					@Override
					public void success() {

					}

					@Override
					public void error() {

					}

					@Override
					public void cancel() {

					}
				});
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(Window window, float bgAlpha) {
		if(window != null) {
			mWindow = window;
		}
		WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		mWindow.setAttributes(lp);
	}

	public void setDissListenner(DissListenner dissListenner) {
		mDissListenner = dissListenner;
	}
	
	@Override
	public void onClick(View v) {
	}

	static class ShareEntity {
		private int icon;
		private String name;

		public ShareEntity(int icon, String name) {
			this.icon = icon;
			this.name = name;
		}

		public int getIcon() {
			return icon;
		}

		public void setIcon(int icon) {
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	private class MySharePopAdapter extends CommonAdapter<ShareEntity> {

		public MySharePopAdapter(Context context, List<ShareEntity> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder helper, ShareEntity item) {

			helper.setImageResource(R.id.item_share_pop_icon, item.getIcon());

			helper.setText(R.id.item_share_pop_name, item.getName());
		}
	}

	public interface DissListenner {
		public void dissmiss();
	}
	
}