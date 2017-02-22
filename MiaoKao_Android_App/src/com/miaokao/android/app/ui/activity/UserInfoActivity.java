package com.miaokao.android.app.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.coracle_photopicker_library.CoracleManager;
import com.coracle_photopicker_library.utils.FilePathUtils;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.BitmapUtil;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.util.okhttp.OkHttpUtils;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.miaokao.android.app.widget.SelectPicPopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 个人信息页面
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-22 下午2:19:59
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private static final int REQUEST_CAMERA = 101;
	private static final int RESULT_REQUEST_CODE = 102;

	private Context mContext;
	private RoundAngleImageView mHeadImage;
	private File mHeadFile;
	private String mPicPath;
	private String mHeadImagePath;
	private Bitmap imageBitmap;
	private SelectPicPopupWindow mSelectPicPopupWindow;
	private TextView mNameTxt;
	private TextView mSexTxt;
	private TextView mAddressTxt;
	private TextView mSchoolTxt;
	private TextView mMajorTxt;
	private TextView mAgeTxt;
	public static boolean updateIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_info);

		mContext = this;
		updateIcon = false;
		initView();
		initData();
	}
	
	private void initView() {
		initTopBarLeftAndTitle(R.id.user_info_common_actionbar, "个人信息");
		
		mHeadImage = (RoundAngleImageView) findViewById(R.id.my_info_icon_image);
		mNameTxt = (TextView) findViewById(R.id.my_info_name_txt);
		mSexTxt = (TextView) findViewById(R.id.my_info_sex_txt);
		mAddressTxt = (TextView) findViewById(R.id.my_info_address_txt);
		mSchoolTxt = (TextView) findViewById(R.id.my_info_school_txt);
		mMajorTxt = (TextView) findViewById(R.id.my_info_major_txt);
		mAgeTxt = (TextView) findViewById(R.id.my_info_age_txt);
		
		mHeadImage.setOnClickListener(this);
		findViewById(R.id.my_info_name_layout).setOnClickListener(this);
		findViewById(R.id.my_info_sex_layout).setOnClickListener(this);
		findViewById(R.id.my_info_address_layout).setOnClickListener(this);
		findViewById(R.id.my_info_school_layout).setOnClickListener(this);
		findViewById(R.id.my_info_major_layout).setOnClickListener(this);
		findViewById(R.id.my_info_age_layout).setOnClickListener(this);
		findViewById(R.id.user_info_head_ayout).setOnClickListener(this);
		
		if(TextUtils.isEmpty(mHeadImagePath)) {
			mHeadImagePath = mAppContext.mUser.getHead_img();
		}
	}

	private void initData() {
		// 头像
		ImageLoader.getInstance().displayImage(mAppContext.mUser.getHead_img(), mHeadImage,
				mAppContext.getHeadImageOptions());
		// 姓名
		mNameTxt.setText(mAppContext.mUser.getUser_name());
		// 性别
		mSexTxt.setText(mAppContext.mUser.getSex());
		// 地址
		mAddressTxt.setText(mAppContext.mUser.getAddress());
		// 学校
		mSchoolTxt.setText(mAppContext.mUser.getSchool());
		// 专业
		mMajorTxt.setText(mAppContext.mUser.getMajor());
		// 年级
		mAgeTxt.setText(mAppContext.mUser.getStatus());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_info_head_ayout:
			// 编辑头像
			chooseHeadImage();
			break;
		case R.id.my_info_icon_image:
			// 查看头像
			if(!TextUtils.isEmpty(mHeadImagePath)) {
				List<String> images = new ArrayList<>();
				images.add(mHeadImagePath);
				CoracleManager.getInstance().browserImage(mContext, images, 0);
			}
			break;
		case R.id.my_info_name_layout:
			// 姓名
			goEdiUserInfoActivity(1);
			break;
		case R.id.my_info_sex_layout:
			// 性别
			goEdiUserInfoActivity(2);
			break;
		case R.id.my_info_address_layout:
			// 地址
			goEdiUserInfoActivity(3);
			break;
		case R.id.my_info_school_layout:
			// 学校
			goEdiUserInfoActivity(4);
			break;
		case R.id.my_info_major_layout:
			// 专业
			goEdiUserInfoActivity(5);
			break;
		case R.id.my_info_age_layout:
			// 年级
			goEdiUserInfoActivity(6);
			break;
		}
	}
	
	private void goEdiUserInfoActivity(int type) {
		Intent intent = new Intent(this, EditInfoActivity.class);
		intent.putExtra("type", type);
		startActivityForResult(intent, PubConstant.EDIT_INFO_CODE_REQUEST_CODE);
	}

	private void chooseHeadImage() {
		mSelectPicPopupWindow = new SelectPicPopupWindow(this, itemsOnClick);

		mSelectPicPopupWindow.showAtLocation(mHeadImage, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			mSelectPicPopupWindow.dismiss();
			switch (v.getId()) {
			case R.id.pop_choose_carme_txt:
				// 拍照   -- 需要判断SD卡
				if(!PubUtils.ExistSDCard()) {
					showDialogTips(mContext, "未找到SD储存卡");
					return;
				}
				// 创建临时文件
				File picFile = new File(FilePathUtils.getDefaultImagePath(mContext), System.currentTimeMillis() + ".png");
				mPicPath = picFile.getAbsolutePath();
		        // 跳转到系统照相机
		        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        // 设置系统相机拍照后的输出路径
		        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
		        cameraIntent.putExtra("autofocus", true); // 自动对焦
		        startActivityForResult(cameraIntent, REQUEST_CAMERA);
				break;
			case R.id.pop_choose_library_txt:
				// 选图
				CoracleManager.getInstance().selectImage(mContext, 1, false, new CoracleManager.onPictureListenner() {
					@Override
					public void selectImages(List<String> images) {
						String path = images.get(0);
						// 裁剪图片
						checkPhotoZoom(path);
					}
				});
				break;
			}
		}

	};

	// 裁剪图片
	private void checkPhotoZoom(final String path) {
		mHeadImagePath = path;
		mHeadFile = new File(FilePathUtils.getDefaultImagePath(mContext), mAppContext.mUser.getUser_no() + ".png");
		try {
			if (mHeadFile.exists()) {
				mHeadFile.delete();
			}
			mHeadFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File sFile = new File(path);
		// copy文件
		fileChannelCopy(sFile, mHeadFile);
		startPhotoZoom(Uri.fromFile(sFile), Uri.fromFile(mHeadFile), 300);// 截图
	}

	/**
	 * 复制文件
	 * 
	 * @param s 源文件
	 * @param t 目标文件
	 */
	private void fileChannelCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			FileChannel in = fi.getChannel();// 得到对应的文件通道
			FileChannel out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fo != null)
					fo.close();
				if (fi != null)
					fi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 跳转至系统截图界面进行截图
	 * 
	 * @param data
	 * @param size
	 */
	private void startPhotoZoom(Uri sUri, Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");//动作-裁剪
		intent.setDataAndType(sUri, "image/*");//类型
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//
		// 输出文件
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		// 输出格式
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);// 裁剪比例
		intent.putExtra("aspectY", 1);// 裁剪比例
		intent.putExtra("outputX", size);// 输出大小
		intent.putExtra("outputY", size);// 裁剪比例后输出比例
		intent.putExtra("scale", true);// 缩放
		intent.putExtra("scaleUpIfNeeded", true);// 如果小于要求输出大小，就放大
		intent.putExtra("return-data", false);// 不返回缩略图
		intent.putExtra("noFaceDetection", true);// 关闭人脸识别
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if(requestCode == REQUEST_CAMERA) {
				if (resultCode == Activity.RESULT_OK) {
					// 拍照返回
					// 裁剪图片
					checkPhotoZoom(mPicPath);
				}
				
			} else if (requestCode == RESULT_REQUEST_CODE) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					imageBitmap = bundle.getParcelable("data");
				}
				if (imageBitmap == null) {
					imageBitmap = BitmapUtil.decodeThumbBitmapForFile(mHeadFile.getAbsolutePath(), 0, 0);
				}
				if (imageBitmap != null) {
					mHeadImage.setImageBitmap(imageBitmap);
				}
				// 保存头像
				saveHeadImage();
			} else if(requestCode == PubConstant.EDIT_INFO_CODE_REQUEST_CODE) {
				if (resultCode == Activity.RESULT_OK) {
					// 刷新数据
					initData();
				}
			}
	}

	private void saveHeadImage() {
		final String url = PubConstant.REQUEST_BASE_URL + "/app_upload_head_img_for_android.php";
		new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				String str = null;
				try {
					str = OkHttpUtils.postFile(url, mHeadFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return str;
			}
			
			protected void onPostExecute(String value) {
				System.out.println("---" + value);
				if(!TextUtils.isEmpty(value)) {
					try {
						JSONObject jsonObject = new JSONObject(value);
						JSONObject object = jsonObject.optJSONObject("message");
						String result = object.optString("result");
						if("ok".equals(result)) {
//							showDialogTips(mContext, "头像编辑成功");
							updateIcon = true;
							// 
							if (TextUtils.isEmpty(mAppContext.mUser.getHead_img())) {
								mAppContext.mUser
										.setHead_img("http://www.qinghuayu.com/running/service/files/head_img/"
												+ mAppContext.mUser.getUser_no() + ".png");
							}
						} else {
							if(mContext != null && !UserInfoActivity.this.isFinishing()) {
								showDialogTips(mContext, "头像编辑失败");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
		}.execute();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
