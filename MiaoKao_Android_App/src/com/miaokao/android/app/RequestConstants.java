package com.miaokao.android.app;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.entity.User;
import com.miaokao.android.app.service.MyPushIntentService;
import com.miaokao.android.app.util.DecriptUtils;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.tag.TagManager;

/**
 * @TODO 公共的接口请求
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-18 上午10:14:37
 */
public class RequestConstants {

	private static Thread mThread;
	
	public static boolean LOGIN_END = true;
	public static String VERSION;
	public static String UPGRADE;
	public static String URL;
	
	/**
	 * 获取短信验证码接口
	 * @param phone
	 * @param listenner
	 * @param tag
	 */
	public static void getAuthCode(Context context, String phone, RequestListenner listenner, String tag) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_send_verify_code.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("mobile", phone);
		AppContext.getInstance().netRequest(context, url, postData, listenner, true, tag);
	}
	
	/**
	 * 验证短信验证码接口
	 * @param phone
	 * @param authCode
	 * @param listenner
	 * @param tag
	 */
	public static void checkAuthCode(Context context, String phone, String authCode, RequestListenner listenner, String tag) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "verify_code");
		postData.put("mobile", phone);
		postData.put("code", authCode);
		AppContext.getInstance().netRequest(context, url, postData, listenner, true, tag);
	}

	/**     自动登录      */
	public static void autoLogin(final Context context) {
		String loginName = PreferenceUtils.getInstance().getString(PubConstant.LOGIN_NAME_KEY, "");
		String pwd = PreferenceUtils.getInstance().getString(PubConstant.PASSWORD_KEY, "");
		
		autoLogin(context, loginName, DecriptUtils.decryptBASE64(pwd));
	}
	
	/**     登录      */
	public static void autoLogin(final Context context, final String loginName, final String password) {
		if(!TextUtils.isEmpty(loginName) && !TextUtils.isEmpty(password)) {
			// 自动登录
			String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
			Map<String, String> postData = new HashMap<String, String>();
			postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
			postData.put("type", "member_login");
			postData.put("user_id", loginName);
			postData.put("user_pwd", DecriptUtils.SHA1(password));
			AppContext.getInstance().netRequest(context, url, postData, new RequestListenner() {
				
				@Override
				public void responseResult(JSONObject jsonObject) {
					
					JSONObject object = jsonObject.optJSONObject("message");
					if(object != null && !"null".equals(object)) {
						String result = object.optString("result");
						if("ok".equals(result)) {
							// 获取用户信息
							getUserInfo(context, loginName, password);
						} else {
							sendLoginStatus(context, false);
						}
					}
				}
				
				@Override
				public void responseError() {
					sendLoginStatus(context, false);
				}
			}, false, "autoLogin");
		}
	}
	
	private static void getUserInfo(final Context context, final String userName, final String pwd) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "member_info");
		postData.put("user_id", userName);
		AppContext.getInstance().netRequest(context, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					AppContext.getInstance().mUser = new User();
					AppContext.getInstance().mUser.setLoginName(userName);
					AppContext.getInstance().mUser.setUser_no(object.optString("user_no"));
					AppContext.getInstance().mUser.setUser_id(object.optString("user_id"));
					AppContext.getInstance().mUser.setUser_name(object.optString("user_name"));
					AppContext.getInstance().mUser.setSex(object.optString("sex"));
					AppContext.getInstance().mUser.setCareer(object.optString("career"));
					AppContext.getInstance().mUser.setEmail(object.optString("email"));
					AppContext.getInstance().mUser.setMobile(object.optString("mobile"));
					AppContext.getInstance().mUser.setHead_img(object.optString("head_img"));
					AppContext.getInstance().mUser.setStatus(object.optString("status"));
					AppContext.getInstance().mUser.setAddress(object.optString("address"));
					AppContext.getInstance().mUser.setSchool(object.optString("school"));
					AppContext.getInstance().mUser.setMajor(object.optString("major"));
					AppContext.getInstance().mUser.setGrade(object.optString("grade"));
					AppContext.getInstance().mUser.setPlace(object.optString("place"));
					AppContext.getInstance().mUser.setCoach_id(object.optString("coach_id"));
					AppContext.getInstance().mUser.setCoach_name(object.optString("coach_name"));
					AppContext.getInstance().mUser.setBalance(object.optString("balance"));
					
					// 登录成功保存帐号和密码  -- 密码加密
					PreferenceUtils.getInstance().putString(PubConstant.LOGIN_NAME_KEY, userName);
					PreferenceUtils.getInstance().putString(PubConstant.PASSWORD_KEY, DecriptUtils.encryptBASE64(pwd));
					
					// 发送登录成功广播
					sendLoginStatus(context, true);
					
					// 表示登录 and 获取用户信息结束。
					LOGIN_END = true;
				} else {
					sendLoginStatus(context, false);
				}
			}
			
			@Override
			public void responseError() {
				sendLoginStatus(context, false);
			}
		}, false, "auto_get_user_info");
	}
	
	public static void sendLoginStatus(final Context context, boolean success) {
		Intent intent = new Intent(PubConstant.LOGIN_STATE_KEY);
		intent.putExtra("isLogin", success);
		context.sendBroadcast(intent);
		
		if(success) {
			// 登录成功，开启推送服务链接,异步执行
			mThread = new Thread(){
				@Override
				public void run() {
					super.run();
					// 友盟推送
					PushAgent mPushAgent = PushAgent.getInstance(context.getApplicationContext());
					// 开启推送服务
					mPushAgent.enable();
					// 统计应用启动
					mPushAgent.onAppStart();
					// 设置自定义消息处理类
					mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
					mPushAgent.getMessageHandler();
					try {
						// 添加tag
						TagManager tagManager = mPushAgent.getTagManager();
						tagManager.add("student");
						tagManager.add("cid" + AppContext.getInstance().mUser.getCoach_id());
						// 添加别名, 需要先删除
						mPushAgent.removeAlias(AppContext.getInstance().mUser.getUser_id(), "userid");
						mPushAgent.addAlias(AppContext.getInstance().mUser.getUser_id(), "userid");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			mThread.start();
		}
	}
	
	public static void checkVersion(final Context context) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "version");
		postData.put("app", "mewkao");
		postData.put("client_type", "andriod");
		AppContext.getInstance().netRequest(context, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("message");
					if(jsonArray != null) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String version = object.optString("version");
							String upgrade = object.optString("upgrade");
							final String url = object.optString("url");
							
							if(!TextUtils.isEmpty(url) && !TextUtils.isEmpty(version)) {
								int v = Integer.parseInt(version.replace(".", ""));
								int tv = Integer.parseInt(PubUtils.getSoftVersion(context).replace(".", ""));
								if(v > tv) {
									// 版本号不一致 前去下载
									VERSION = version;
									UPGRADE = upgrade;
									URL = url;
									Intent intent = new Intent(PubConstant.SHOW_UPDATE_KEY);
									context.sendBroadcast(intent);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void responseError() {
				
			}
		}, false, "check_version");
	}
	
}
