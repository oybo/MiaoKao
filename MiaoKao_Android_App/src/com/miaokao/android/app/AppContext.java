package com.miaokao.android.app;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.amap.api.location.AMapLocation;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miaokao.android.app.entity.TopADImage;
import com.miaokao.android.app.entity.User;
import com.miaokao.android.app.service.LocalService;
import com.miaokao.android.app.ui.activity.LoginActivity;
import com.miaokao.android.app.ui.activity.MainActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.ToastFactory;
import com.miaokao.android.app.widget.LoadingDialog;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class AppContext extends Application {

	private static AppContext mInstance;
	private static RequestQueue mRequestQueue;
	public User mUser;
	public AMapLocation mAMapLocation;
	public String mCookie;
	public boolean mProcessFlag = false;
	public boolean mHomeResume = false;
	private Intent mLocalServiceIntent;
	public List<TopADImage> mTopADImages;
	private LoadingDialog mLoadingDialog;
	private Stack<Activity> activityStack;

	public static AppContext getInstance() {
		return mInstance;
	}

	public static RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
		mProcessFlag = true;
		// 初始化Volley,使用网络请求，单例
		mRequestQueue = Volley.newRequestQueue(this);
		// 初始化图片加载器
		initImageLoader();
		// 封装catch
		CrashHandler.getInstance().init(this);
	}

	/**
	 * 添加Activity到堆
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	/**
	 * 结束Activity   false:保留登录页， true:全部退出
	 */
	public void finishAllActivity(boolean isContainLogin) {
		Activity loginA = null;
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			Activity activity = activityStack.get(i);
			if(null != activity) {
				if(activity instanceof MainActivity && !isContainLogin) {
					loginA = activity;
					continue;
				}
				activity.finish();
			}
		}
		activityStack.clear();
		if(!isContainLogin && loginA != null) {
			addActivity(loginA);
		}
	}
	
	public void checkProcessStatus() {
		if(!AppContext.getInstance().mProcessFlag) {
			// 1秒钟后重启应用
			Intent intent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1500, restartIntent);
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
	
	public void startLocalService() {
		mLocalServiceIntent = new Intent(this, LocalService.class);
		startService(mLocalServiceIntent);
	}

	public void stopLocalService() {
		if (mLocalServiceIntent != null) {
			stopService(mLocalServiceIntent);
			sendBroadcast(new Intent(PubConstant.LOCAL_SUCCESS_KEY));
		}
	}

	public boolean isLogin(Activity activity) {
		if (mUser != null) {
			return true;
		}

		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_left, R.anim.in_right);

		return false;
	}

	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
				.diskCacheSize(30 * 1024 * 1024).diskCacheFileCount(100).build();
		ImageLoader.getInstance().init(config);
	}

	// 默认的
	public DisplayImageOptions getOptions() {
		return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				// .displayer(new FadeInBitmapDisplayer(300)) // 设置这个，刷新图片时不会闪
				.displayer(new SimpleBitmapDisplayer()).build();
	}
	
	// 自定义默认的
	public DisplayImageOptions getOptions(int resouceId) {
		return new DisplayImageOptions.Builder().showImageOnLoading(0)
				.showImageForEmptyUri(resouceId).showImageOnFail(resouceId)
				.cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				// .displayer(new FadeInBitmapDisplayer(300)) // 设置这个，刷新图片时不会闪
				.displayer(new SimpleBitmapDisplayer())
				.displayer(new FadeInBitmapDisplayer(1000))
				.build();
	}
	
	// 自定义默认的
	public DisplayImageOptions getAllOptions(int resouceId) {
		return new DisplayImageOptions.Builder().showImageOnLoading(resouceId)
				.showImageForEmptyUri(resouceId).showImageOnFail(resouceId)
				.cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				// .displayer(new FadeInBitmapDisplayer(300)) // 设置这个，刷新图片时不会闪
				.displayer(new SimpleBitmapDisplayer())
				.displayer(new FadeInBitmapDisplayer(1000))
				.build();
	}

	// 自定义默认园的
	public DisplayImageOptions getRoundedOptions(int defaultImgResourceId) {
		return new DisplayImageOptions.Builder().showImageOnLoading(defaultImgResourceId)
				.showImageForEmptyUri(defaultImgResourceId).showImageOnFail(defaultImgResourceId).cacheInMemory(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				// .displayer(new FadeInBitmapDisplayer(300)) // 设置这个，刷新图片时不会闪
				.displayer(new SimpleBitmapDisplayer()).displayer(new RoundedBitmapDisplayer(10)).build();
	}

	// 加载头像
	public DisplayImageOptions getHeadImageOptions() {
		return new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.head_image_default)
				.showImageForEmptyUri(R.drawable.head_image_default).showImageOnFail(R.drawable.head_image_default)
				.cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				// .displayer(new FadeInBitmapDisplayer(300)) // 设置这个，刷新图片时不会闪
				.displayer(new SimpleBitmapDisplayer()).build();
	}

	private void showLoading(Context context, String tag) {
		try {
			if(mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			mLoadingDialog = LoadingDialog.createLoadingDialog(context, tag);
			mLoadingDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void dismissLoading(Context context) {
		try {
			if(mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 网络请求，提交JsonObject 
	 * @param context
	 * @param url
	 * @param jsonObject
	 * @param listenner
	 * @param tag
	 */
	public void netRequestForJson(final Context context, String url, JSONObject jsonObject,
			final RequestListenner listenner, final boolean isShow, final String tag) {
		if(isShow) {
			showLoading(context, tag);
		}
		
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						dismissLoading(context);
						
						if(PubConstant.DEBUG) {
							System.out.println(tag + "--" + response);
						}
						listenner.responseResult(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissLoading(context);
						
						listenner.responseError();
						ToastFactory.getToast(context, getErrorMessage(error)).show();
					}
				});
		// 设置请求标识，可以根据标识终止请求
		jsonRequest.setTag(tag);
		// 设置超时时间，失败请求次数，退避乘数
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(jsonRequest);

	}
	
	/**
	 * 网络请求，GET方式，返回Json格式
	 * 
	 * @param context -- 上下文
	 * @param url  -- 请求地址
	 * @param listenner -- 请求回调
	 */
	public void netRequest(final Context context, String url, final RequestListenner listenner, final boolean isShow, String tag) {
		if(isShow) {
			showLoading(context, tag);
		}
		
		StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissLoading(context);
				
				if(PubConstant.DEBUG) {
					System.out.println("-get-" + response);
				}
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(jsonObject != null) {
					listenner.responseResult(jsonObject);
				} else {
					listenner.responseError();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissLoading(context);
				
				listenner.responseError();
				ToastFactory.getToast(context, getErrorMessage(error)).show();
			}
		}) {
			// 设置编码格式
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				String str = null;
				try {
					str = new String(response.data, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
			}
		};
		// 设置请求标识，可以根据标识终止请求
		request.setTag(tag);
		// 设置超时时间，失败请求次数，退避乘数
		request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(request);
	}

	/**
	 * 网络请求，POST方式，返回Json格式
	 * 
	 * @param context -- 上下文
	 * @param url -- 请求地址
	 * @param postData -- 提交参数，Map集合
	 * @param listenner  -- 请求回调
	 */
	public void netRequest(final Context context, String url, final Map<String, String> postData,
			final RequestListenner listenner, final boolean isShow, final String tag) {
		if(isShow) {
			showLoading(context, tag);
		}
		
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissLoading(context);
				
				if(PubConstant.DEBUG) {
					System.out.println(tag + "--" + response);
				}
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(jsonObject != null) {
					listenner.responseResult(jsonObject);
				} else {
					listenner.responseError();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissLoading(context);
				
				listenner.responseError();
				ToastFactory.getToast(context, getErrorMessage(error)).show();
			}
		}) {
			// 初始化头，-- 主要是cookies
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headerMaps = new HashMap<String, String>();
				if(!"autoLogin".equals(tag)) {
					if (!TextUtils.isEmpty(mCookie)) {
						headerMaps.put("Cookie", mCookie);
					}
				}
				return headerMaps;
			}

			// 这里初始化post参数
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (postData == null) {
					return new HashMap<String, String>();
				}
				return postData;
			}

			// 设置编码格式,和获取响应头信息
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				try {
					if(response != null && response.headers != null) {
						Map<String, String> responseHeaders = response.headers;
						try {
							String set_cookies = responseHeaders.get("Set-Cookie");
							if (!TextUtils.isEmpty(set_cookies)) {
								mCookie = set_cookies.split(" ")[0];
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String dataString = new String(response.data, "UTF-8");
					return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
				} catch (UnsupportedEncodingException e) {
					return Response.error(new ParseError(e));
				}
			}
		};

		// 设置请求标识，可以根据标识终止请求
		request.setTag(tag);
		// 设置超时时间，失败请求次数，退避乘数
		request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(request);
	}

	/**
	 * 终止某个tag的网络请求
	 * 
	 * @param tag
	 */
	public void callRequest(String tag) {
		mRequestQueue.cancelAll(tag);
	}

	private String getErrorMessage(Object error) {
		if (error instanceof TimeoutError) {
			return getString(R.string.net_error_timeout);
		} else if (error instanceof ServerError) {
			return getString(R.string.net_error_service);
		} else if (error instanceof JSONException) {
			return getString(R.string.net_error_return);
		} else if ((error instanceof NetworkError) || (error instanceof NoConnectionError)) {
			return getString(R.string.net_error_set_net);
		}
		return getString(R.string.net_error);
	}

	public interface RequestListenner {
		/**
		 * 网络请求返回字符串
		 */
		public void responseResult(JSONObject jsonObject);

		public void responseError();
	}

}