package com.miaokao.android.app.util;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.DSchoolCourse;
import com.miaokao.android.app.entity.DSchoolDiscount;
import com.miaokao.android.app.entity.DrivingSchool;
import com.miaokao.android.app.entity.MKMessage;
import com.miaokao.android.app.entity.MerComment;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

public class PubUtils {

	/** 拨打号码 */
	public static void callPhone(Context context, String mobile) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/** 发送短信 */
	public static void sendSms(Context context, String to, String message) {
		Uri uri = Uri.parse("smsto:" + to);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", message);
		context.startActivity(it);
	}

	/** 验证是否手机号 */
	public static boolean isMobileNO(String mobiles) {
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		}
//		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Pattern p = Pattern.compile("^(1)\\d{10}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 小数点转化百分百
	 * @param number
	 * @return
	 */
	public static String numberToPercent(String number) {
		try {
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMaximumFractionDigits(2);
			return nf.format(Double.parseDouble(number));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}

	/**
	 * 小数点转化百分百
	 * @param number
	 * @return
	 */
	public static String numberToPercent(double number) {
		try {
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMaximumFractionDigits(2);
			return nf.format(number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return number + "";
	}

	/**
	 * 金额保留两位小数位
	 * @param price
	 * @return
	 */
	public static String moneyFormat(String price) {
		try {
			if(!price.contains(".")) {
				return price;
			}
			DecimalFormat myformat = new DecimalFormat();
			myformat.applyPattern("##.00");
			return myformat.format(Double.parseDouble(price));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
	}
	
	public static boolean ExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/** dp转换px */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/** 等比例缩放Bitmap */
	public static Bitmap big(Bitmap b, float x, float y) {
		int w = b.getWidth();
		int h = b.getHeight();
		float sx = (float) x / w;// 要强制转换，不转换我的在这总是死掉。
		float sy = (float) y / h;
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
		return resizeBmp;
	}

	/** 
     * Layout动画 
     *  
     * @return 
     */  
	public static LayoutAnimationController getAnimationController() {
		int duration = 300;
		AnimationSet set = new AnimationSet(true);

		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 250, 0);
		translateAnimation.setDuration(duration);
		translateAnimation.setInterpolator(new AccelerateInterpolator());
		set.addAnimation(translateAnimation);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1f);
		alphaAnimation.setDuration(duration);
		set.addAnimation(alphaAnimation);

		// 得到一个LayoutAnimationController对象；
		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
		// 设置控件显示的顺序；
		controller.setOrder(LayoutAnimationController.ORDER_REVERSE);
		// 设置控件显示间隔时间；
		controller.setDelay(0.3f);
		return controller;
	}

	/**
	 * 计算百分比
	 * 
	 * @param all
	 *            , pro
	 * @return
	 */
	public static String getSHCollagen(int all, int pro) {
		String str = "";
		if (all < 0 || pro < 0 || all < pro) {
			return str;
		}
		try {
			double proTemp = (double) pro * 100;
			double allTemp = all;
			BigDecimal bigPro = new BigDecimal(proTemp + "");
			BigDecimal bigAll = new BigDecimal(allTemp + "");
			BigDecimal proDou = bigPro.divide(bigAll, 2, BigDecimal.ROUND_HALF_UP);
			str = proDou.toString();
			if (str.indexOf(".") > 0) {
				str = str.substring(0, str.indexOf("."));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str + "%";
	}

	/**
	 * 文件大小转换
	 * 
	 * @param fileLen
	 * @return
	 */
	public static String formatFileSizeToString(long fileLen) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileLen < 1024) {
			fileSizeString = df.format((double) fileLen) + "B";
		} else if (fileLen < 1048576) {
			fileSizeString = df.format((double) fileLen / 1024) + "K";
		} else if (fileLen < 1073741824) {
			fileSizeString = df.format((double) fileLen / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileLen / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 调用系统安装apk
	 * 
	 * @param path
	 *            本地文件绝对路径
	 */
	public static void installApk(Context context, File file) {
		if (!file.exists()) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：千米
	 */
	public static String Distance(double long1, double lat1, double long2, double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));

		return String.format("%.2f", d / 1000) + "千米";
	}

	/**
	 * 获取 移动终端设备id号
	 * 
	 * @param context
	 *            :上下文文本对象
	 * @return id 移动终端设备id号
	 */
	public static String getDevId(Context context) {
		String id = PreferenceUtils.getInstance().getString("devicesID", "");
		if (id.length() == 0) {
			try {
				id = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			} catch (Exception e) {
			}
			if (id == null)
				id = "";
		}
		if (id.length() == 0) {
			try {
				id = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
			} catch (Exception e) {
			}
			if (id == null)
				id = "";
		}
		if (id.length() == 0) {
			try {
				id = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
			} catch (Exception e) {
			}
			if (id == null)
				id = "";
		}
		if (id.length() == 0) {
			try {
				Class<?> c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class, String.class);
				id = (String) (get.invoke(c, "ro.serialno", "unknown"));
			} catch (Exception e) {
			}
			Log.i("infor", "id is " + id);
		}
		if (id.length() == 0 || "0".equals(id)) {
			// 随机生成
			id = UUID.randomUUID().toString().replaceAll("-", "");
			PreferenceUtils.getInstance().putString("devicesID", id);
		}
		return id;
	}

	/**
	 * 获取应用当前版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getSoftVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(AppContext.getInstance().getPackageName(), 0);
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

	/** 计算两数百分比 */
	public static String getPercent(float x, float total) {
		String baifenbi = "";// 接受百分比的值
		double baiy = x * 1.0;
		double baiz = total * 1.0;
		double fen = baiy / baiz;

		// ##.00% 百分比格式，后面不足2位的用0补齐 baifenbi=nf.format(fen);
		// DecimalFormat df1 = new DecimalFormat("##.0%");
		DecimalFormat df1 = new DecimalFormat("##%");

		baifenbi = df1.format(fen);
		return baifenbi;
	}

	public static void showUpgrade(final Context context, String version, String upgrade, final String url) {
		if(!version.equals(PubUtils.getSoftVersion(context))) {
			DialogTips dialogTips = new DialogTips(context, "发现新版本,点击确定下载");
			// 版本号不一致 前去下载
			if("1".equals(upgrade)) {
				// 强制更新
				dialogTips.setCanceledOnTouchOutside(false);
				dialogTips.setCancelable(false);
			} else {
				dialogTips.setCancelListenner(null);
			}
			dialogTips.setOkListenner(new onDialogOkListenner() {
				@Override
				public void onClick() {
					NotifiDownLoadManager.getInstance(context).startNotiUpdateTask(url, "开始下载喵考", "喵考");
				}
			});
			dialogTips.show();
		}
	}
	
	public static void logout(final Context context, final boolean isAll) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "member_login_out");
		AppContext.getInstance().netRequest(context, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if (object != null && !"null".equals(object)) {
					String result = object.optString("result").trim();
					if ("ok".equals(result)) {
						AppContext.getInstance().mUser = null;
						// 发送登录成功广播
						Intent intent = new Intent(PubConstant.LOGIN_STATE_KEY);
						intent.putExtra("isLogin", false);
						context.sendBroadcast(intent);

						if(!isAll) {
							// 如果为false， 就是在设置里面的退出 则清除帐号缓存
							PreferenceUtils.getInstance().remove(PubConstant.LOGIN_NAME_KEY);
							PreferenceUtils.getInstance().remove(PubConstant.PASSWORD_KEY);
						}
						
						AppContext.getInstance().finishAllActivity(isAll);
					} else {
						new DialogTips(context, "退出失败,请重试").show();
					}
				}
			}

			@Override
			public void responseError() {

			}
		}, true, "logout");
	}

	// 获取教练列表
	public static List<Coach> analysisCoachs(List<Coach> drivingSchools, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.optJSONArray("coach");
		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.optJSONObject(i);
				Coach coach = new Coach();
				drivingSchools.add(analysisCoach(coach, object));
			}
		}

		return drivingSchools;
	}

	// 获取教练详情
	public static void analysisCoachDetail(Coach coach, JSONObject jsonObject) {
		// 基本信息
		JSONArray detailArray = jsonObject.optJSONArray("detail");
		if (detailArray != null && detailArray.length() > 0) {
			JSONObject object = detailArray.optJSONObject(0);

			analysisCoach(coach, object);
		}
		// 好差评
		JSONArray rateArray = jsonObject.optJSONArray("rate_num");
		if (rateArray != null && rateArray.length() > 0) {
			for (int i = 0; i < rateArray.length(); i++) {
				JSONObject object = rateArray.optJSONObject(i);
				if (object != null && !"null".equals(object)) {
					String rate = object.optString("rate");
					if ("-1".equals(rate)) {
						// 差评
						coach.setcRate(object.optString("num"));
					} else if ("0".equals(rate)) {
						// 中评
						coach.setzRate(object.optString("num"));
					} else if ("1".equals(rate)) {
						// 好评
						coach.sethRate(object.optString("num"));
					}
				}

			}
		}
		// 评论
		List<MerComment> comments = new ArrayList<MerComment>();
		JSONArray commentsArray = jsonObject.optJSONArray("comment");
		if (commentsArray != null) {
			analysisComment(comments, commentsArray);
		}
		coach.setComments(comments);
	}

	/**
	 * // 教练基本信息
	 * 
	 * @param coach
	 * @param object
	 * @return
	 */
	private static Coach analysisCoach(Coach coach, JSONObject object) {
		if (object != null && !"null".equals(object)) {
			coach.setMer_id(object.optString("mer_id"));
			coach.setAccount(object.optString("account"));
			coach.setType(object.optString("type"));
			coach.setHead_img(object.optString("head_img"));
			coach.setCer_img(object.optString("cer_img"));
			coach.setName(object.optString("name"));
			coach.setIntro(object.optString("intro"));
			coach.setMobile(object.optString("mobile"));
			coach.setSex(object.optString("sex"));
			coach.setRate(object.optString("rate"));
			coach.setTime(object.optString("time"));
		}

		return coach;
	}
	
	/**
	 * 解析教练列表获取
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static List<Coach> analysisDCoach(JSONObject jsonObject) {
		List<Coach> drivingCoachs = new ArrayList<Coach>();
		
		JSONArray jsonArray = jsonObject.optJSONArray("list");
		if (jsonArray != null && !"null".equals(jsonArray)) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.optJSONObject(i);
				if (object != null && !"null".equals(object)) {
					Coach coach = new Coach();
					// merchant
					coach.setRate(object.optString("rate"));
					coach.setComment_num(object.optString("comment_num"));
					coach.setAge(object.optString("age"));
					coach.setName(object.optString("name"));
					coach.setMember_num(object.optString("member_num"));
					coach.setOne_hour_price(object.optString("one_hour_price"));
					coach.setCource_price(object.optString("cource_price"));
					coach.setAccount(object.optString("account"));
					coach.setCar_no(object.optString("car_no"));
					coach.setCer_img(object.optString("cer_img"));
					coach.setCoach_video(object.optString("coach_video"));
					coach.setHead_img(object.optString("head_img"));
					coach.setIntro(object.optString("intro"));
					coach.setL_comment(object.optString("l_comment"));
					coach.setM_comment(object.optString("m_comment"));
					coach.setH_comment(object.optString("h_comment"));
					coach.setIs_for_quick(object.optString("is_for_quick"));
					coach.setIs_for_hour(object.optString("is_for_hour"));
					coach.setIs_for_fenqi(object.optString("is_for_fenqi"));
					coach.setIs_for_return(object.optString("is_for_return"));
					coach.setIs_for_shuttle(object.optString("is_for_shuttle"));
					coach.setMer_account(object.optString("mer_account"));
					coach.setMer_name(object.optString("mer_name"));
					coach.setProvince(object.optString("province"));
					coach.setCity(object.optString("city"));
					coach.setZone(object.optString("zone"));
					coach.setLatitude(object.optString("latitude"));
					coach.setLongitude(object.optString("longitude"));
					coach.setMer_price(object.optString("mer_price"));

					// discount
					JSONArray discountArray = object.optJSONArray("discount");
					List<DSchoolDiscount> schoolDiscounts = new ArrayList<DSchoolDiscount>();
					analysisDiscounts(schoolDiscounts, discountArray);
					coach.setdSchoolDiscounts(schoolDiscounts);

					drivingCoachs.add(coach);
				}
			}
		}
		
		return drivingCoachs;
	}

	/**
	 * 解析驾校获取
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static List<DrivingSchool> analysisDSchool(JSONObject jsonObject) {
		List<DrivingSchool> drivingSchools = new ArrayList<DrivingSchool>();

		JSONArray jsonArray = jsonObject.optJSONArray("list");
		if (jsonArray != null && !"null".equals(jsonArray)) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.optJSONObject(i);
				if (object != null && !"null".equals(object)) {
					DrivingSchool drivingSchool = new DrivingSchool();

					// merchant
					analysisMerchant(drivingSchool, object);
					// discount
					JSONArray discountArray = object.optJSONArray("discount");
					List<DSchoolDiscount> schoolDiscounts = new ArrayList<DSchoolDiscount>();
					analysisDiscounts(schoolDiscounts, discountArray);
					drivingSchool.setdSchoolDiscounts(schoolDiscounts);

					drivingSchools.add(drivingSchool);
				}
			}
		}

		return drivingSchools;
	}

	public static void getDrivingSchool(DrivingSchool drivingSchool, JSONObject jsonObject) {
		// merchant
		JSONArray merchantsArray = jsonObject.optJSONArray("merchants");
		if (merchantsArray != null && merchantsArray.length() > 0) {
			JSONObject merchantObject = merchantsArray.optJSONObject(0);
			if (merchantObject != null && !"null".equals(merchantObject)) {
				analysisMerchant(drivingSchool, merchantObject);
			}
		}
		// discount
		JSONArray discountArray = jsonObject.optJSONArray("discount");
		List<DSchoolDiscount> schoolDiscounts = new ArrayList<DSchoolDiscount>();
		if (discountArray != null) {
			discountArray = discountArray.optJSONArray(0);
			if (discountArray != null) {
				analysisDiscounts(schoolDiscounts, discountArray);
			}
		}
		drivingSchool.setdSchoolDiscounts(schoolDiscounts);
		// service
		JSONArray serviceArray = jsonObject.optJSONArray("service");
		if (serviceArray != null) {
			JSONObject serviceObject = serviceArray.optJSONObject(0);
			if (serviceObject != null && !"null".equals(serviceObject)) {
				drivingSchool.setRouter(serviceObject.optString("router"));
				drivingSchool.setSs_service(serviceObject.optString("ss_service"));
				drivingSchool.setHd_service(serviceObject.optString("hd_service"));
			}
		}
		// mer_comment
		JSONArray mer_commentArray = jsonObject.optJSONArray("mer_comment");
		List<MerComment> merComments = new ArrayList<MerComment>();
		if (mer_commentArray != null) {
			analysisComment(merComments, mer_commentArray);
		}
		drivingSchool.setMerComments(merComments);
		// price
		JSONArray priceArray = jsonObject.optJSONArray("price");
		List<DSchoolCourse> dSchoolCourses = new ArrayList<DSchoolCourse>();
		if (priceArray != null) {
			analysisPrice(dSchoolCourses, priceArray);
		}
		drivingSchool.setdSchoolCourses(dSchoolCourses);
		// pay_rate
		JSONArray pay_rateArray = jsonObject.optJSONArray("pay_rate");
		if (pay_rateArray != null && !"null".equals(pay_rateArray)) {
			JSONObject object = pay_rateArray.optJSONObject(0);
			if (object != null && !"null".equals(object)) {
				drivingSchool.setFirst_pay_rate(object.optString("first_pay_rate"));
				drivingSchool.setSecond_pay_rate(object.optString("second_pay_rate"));
				drivingSchool.setThird_pay_rate(object.optString("third_pay_rate"));
				drivingSchool.setFourth_pay_rate(object.optString("fourth_pay_rate"));
				drivingSchool.setFivth_pay_rate(object.optString("fivth_pay_rate"));
			}
		}
	}

	/** 解析活动班 */
	private static void analysisDiscounts(List<DSchoolDiscount> schoolDiscounts, JSONArray discountArray) {
		if (discountArray != null && !"null".equals(discountArray)) {
			int dLen = discountArray.length();
			for (int j = 0; j < dLen; j++) {
				JSONObject discountObject = discountArray.optJSONObject(j);
				if (discountObject != null && !"null".equals(discountObject)) {
					DSchoolDiscount dSchoolDiscount = new DSchoolDiscount();

					dSchoolDiscount.setMer_id(discountObject.optString("mer_id"));
					dSchoolDiscount.setP_name(discountObject.optString("p_name"));
					dSchoolDiscount.setType(discountObject.optString("type"));
					dSchoolDiscount.setIcon(discountObject.optString("icon"));
					dSchoolDiscount.setValue(discountObject.optString("value"));
					dSchoolDiscount.setCareer(discountObject.optString("career"));
					dSchoolDiscount.setStart_date(discountObject.optString("start_date"));
					dSchoolDiscount.setEnd_date(discountObject.optString("end_date"));

					schoolDiscounts.add(dSchoolDiscount);
				}
			}
		}
	}

	private static void analysisPrice(List<DSchoolCourse> dSchoolCourses, JSONArray priceArray) {
		int len = priceArray.length();
		for (int i = 0; i < len; i++) {
			JSONObject jsonObject = priceArray.optJSONObject(i);
			if (jsonObject != null) {
				DSchoolCourse dSchoolCourse = new DSchoolCourse();
				dSchoolCourse.setId(jsonObject.optString("id"));
				dSchoolCourse.setP_name(jsonObject.optString("p_name"));
				dSchoolCourse.setP_intro(jsonObject.optString("p_intro"));
				dSchoolCourse.setReserved_times(jsonObject.optString("reserved_times"));
				dSchoolCourse.setStatus(jsonObject.optString("status"));
				dSchoolCourse.setP_price(jsonObject.optString("p_price"));
				dSchoolCourse.setDiscount_price(jsonObject.optString("discount_price"));
				dSchoolCourse.setP_mer_id(jsonObject.optString("p_mer_id"));
				dSchoolCourse.setP_add_time(jsonObject.optString("p_add_time"));
				dSchoolCourse.setTime_node(jsonObject.optString("time_node"));
				dSchoolCourse.setStu_price(jsonObject.optString("stu_price"));
				dSchoolCourse.setStu_discount_price(jsonObject.optString("stu_discount_price"));
				dSchoolCourse.setP_type(jsonObject.optString("p_type"));

				dSchoolCourses.add(dSchoolCourse);
			}
		}

	}

	public static void analysisComment(List<MerComment> merComments, JSONArray mer_commentArray) {
		if (mer_commentArray != null && !"null".equals(mer_commentArray)) {
			int len = mer_commentArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject jsonObject = mer_commentArray.optJSONObject(i);
				if (jsonObject != null && !"null".equals(jsonObject)) {
					MerComment comment = new MerComment();
					comment.setContent(jsonObject.optString("content"));
					comment.setMer_id(jsonObject.optString("mer_id"));
					comment.setNum(jsonObject.optString("num"));
					comment.setRate(jsonObject.optString("rate"));
					comment.setTime(jsonObject.optString("time"));
					comment.setUser_id(jsonObject.optString("user_id"));

					merComments.add(comment);
				}
			}
		}
	}

	/** 解析驾校基本信息 */
	private static void analysisMerchant(DrivingSchool drivingSchool, JSONObject object) {
		try {
			drivingSchool.setId(object.optString("id"));
			drivingSchool.setMer_account(object.optString("mer_account"));
			drivingSchool.setMer_head_img(object.optString("mer_head_img"));
			drivingSchool.setMer_phone(object.optString("mer_phone"));
			drivingSchool.setMer_name(object.optString("mer_name"));
			drivingSchool.setMer_addr(object.optString("mer_addr"));
			drivingSchool.setMer_provice(object.optString("mer_provice"));
			drivingSchool.setMer_city(object.optString("mer_city"));
			drivingSchool.setMer_zone(object.optString("mer_zone"));
			drivingSchool.setMer_router(object.optString("mer_router"));
			drivingSchool.setMer_licence_pic(object.optString("mer_licence_pic"));
			drivingSchool.setMer_rate(object.optString("mer_rate"));
			drivingSchool.setIs_for_bankcard(object.optString("is_for_bankcard"));
			drivingSchool.setIs_for_drinking(object.optString("is_for_drinking"));
			drivingSchool.setIs_for_fenqi(object.optString("is_for_fenqi"));
			drivingSchool.setIs_for_food(object.optString("is_for_food"));
			drivingSchool.setIs_for_invoice(object.optString("is_for_invoice"));
			drivingSchool.setIs_for_parking(object.optString("is_for_parking"));
			drivingSchool.setIs_for_return(object.optString("is_for_return"));
			drivingSchool.setIs_for_shuttle(object.optString("is_for_shuttle"));
			drivingSchool.setIs_for_student(object.optString("is_for_student"));
			drivingSchool.setIs_for_wifi(object.optString("is_for_wifi"));
			drivingSchool.setMer_member_num(object.optString("mer_member_num"));
			drivingSchool.setMer_comment_num(object.optString("mer_comment_num"));
			drivingSchool.setMer_lastest_comment(object.optString("mer_lastest_comment"));
			drivingSchool.setMer_cheapest_price(object.optString("mer_cheapest_price"));
			drivingSchool.setMer_lastest_rate(object.optString("mer_lastest_rate"));
			drivingSchool.setMer_last_comment_time(object.optString("mer_last_comment_time"));
			drivingSchool.setMer_finish_member(object.optString("mer_finish_member"));
			drivingSchool.setMer_add_time(object.optString("mer_add_time"));
			drivingSchool.setMer_latitude(object.optString("mer_latitude"));
			drivingSchool.setMer_longitude(object.optString("mer_longitude"));
			drivingSchool.setLowerst_price(object.optString("lowerst_price"));
			drivingSchool.setMer_intro(object.optString("mer_intro"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 解析我的消息 */
	public static void analysisMyMessage(List<MKMessage> mkMessagess, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.optJSONArray("message");
		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.optJSONObject(i);
				if (object != null && !"null".equals(object)) {
					MKMessage mkMessage = new MKMessage();
					mkMessage.setMer_account(object.optString("mer_account"));
					mkMessage.setMer_name(object.optString("mer_name"));
					mkMessage.setId(object.optString("id"));
					mkMessage.setFrom(object.optString("from"));
					mkMessage.setTo(object.optString("to"));
					mkMessage.setContent(object.optString("content"));
					mkMessage.setTime(object.optString("time"));
					mkMessage.setStatus(object.optString("status"));

					mkMessagess.add(mkMessage);
				}
			}
		}
	}

	/** 解析我的订单 */
	public static void analysisMyOrder(List<Order> mkMessagess, JSONArray jsonArray) {
		if (jsonArray != null && !"null".equals(jsonArray)) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.optJSONObject(i);
				if (object != null && !"null".equals(object)) {
					Order order = new Order();
					order.setOrder_no(object.optString("order_no"));
					order.setOrder_type(object.optString("order_type"));
					order.setMer_id(object.optString("mer_id"));
					order.setMer_name(object.optString("mer_name"));
					order.setMer_head_img(object.optString("mer_head_img"));
					order.setUser_id(object.optString("user_id"));
					order.setUser_name(object.optString("user_name"));
					order.setUser_mobile(object.optString("user_mobile"));
					order.setUser_career(object.optString("user_career"));
					order.setProduct_id(object.optString("product_id"));
					order.setProduct_name(object.optString("product_name"));
					order.setFirst_pay_no(object.optString("first_pay_no"));
					order.setFirst_pay_num(object.optString("first_pay_num"));
					order.setFirst_pay_paid(object.optString("first_pay_paid"));
					order.setSecond_pay_no(object.optString("second_pay_no"));
					order.setSecond_pay_num(object.optString("second_pay_num"));
					order.setSecond_pay_paid(object.optString("second_pay_paid"));
					order.setThird_pay_no(object.optString("third_pay_no"));
					order.setThird_pay_num(object.optString("third_pay_num"));
					order.setThird_pay_paid(object.optString("third_pay_paid"));
					order.setFourth_pay_no(object.optString("fourth_pay_no"));
					order.setFourth_pay_num(object.optString("fourth_pay_num"));
					order.setFourth_pay_paid(object.optString("fourth_pay_paid"));
					order.setFivth_pay_no(object.optString("fivth_pay_no"));
					order.setFivth_pay_num(object.optString("fivth_pay_num"));
					order.setFivth_pay_paid(object.optString("fivth_pay_paid"));
					order.setShould_pay_money(object.optString("should_pay_money"));
					order.setPaid_money(object.optString("paid_money"));
					order.setTotal_price(object.optString("total_price"));
					order.setPay_status(object.optString("pay_status"));
					order.setPay_channel(object.optString("pay_channel"));
					order.setStatus(object.optString("status"));
					order.setAdd_time(object.optString("add_time"));

					mkMessagess.add(order);
				}
			}
		}
	}

}
