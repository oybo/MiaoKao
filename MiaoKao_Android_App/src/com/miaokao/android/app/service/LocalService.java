package com.miaokao.android.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.entity.SubjectTab;
import com.miaokao.android.app.entity.TopADImage;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.util.SubjectTabUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-26 下午3:51:57 
 */
public class LocalService extends Service implements AMapLocationListener {

	private int mLocalCount;
	private boolean isGetAd;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(this);
		
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(false);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 自动登录
		RequestConstants.autoLogin(this);
		//启动定位
		mLocationClient.startLocation();
		// 获取学车页面 科目导航
		getSubjectTabs();
		// 版本检测
		RequestConstants.checkVersion(this);
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void getSubjectTabs() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "index_top_icon");
		postData.put("system", "ios");
		postData.put("resolution", "high");
		AppContext.getInstance().netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					SubjectTabUtils.getInstance().mSubjectTabs.clear();
					int len = jsonArray.length();
					for(int i=0; i<len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						if(object != null) {
							String name = object.optString("name");
							String rate = object.optString("rate");
							String icon = object.optString("icon");
							String link = object.optString("link");
							
							SubjectTabUtils.getInstance().mSubjectTabs.add(new SubjectTab(icon, name, link, rate));
						}
					}
					// 发送广播更新
					// 并且发送广播，防止接口请求延迟
					sendBroadcast(new Intent(PubConstant.LOADING_GUANGGAO_IAMGE_KEY));
				}
				
			}
			@Override
			public void responseError() {
				
			}
		}, false, "getSubjectTabs");
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
	        if (amapLocation.getErrorCode() == 0) {
	        //定位成功回调信息，设置相关消息
	        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
	        amapLocation.getLatitude();//获取纬度
	        amapLocation.getLongitude();//获取经度
	        amapLocation.getAccuracy();//获取精度信息
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = new Date(amapLocation.getTime());
	        df.format(date);//定位时间
	        amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
	        amapLocation.getCountry();//国家信息
	        amapLocation.getProvince();//省信息
	        amapLocation.getCity();//城市信息
	        amapLocation.getDistrict();//城区信息
	        amapLocation.getRoad();//街道信息
	        amapLocation.getCityCode();//城市编码
	        amapLocation.getAdCode();//地区编码
	        
	        AppContext.getInstance().mAMapLocation = amapLocation;
	        // 这里发送个广播去刷新学车驾校列表
	        sendBroadcast(new Intent(PubConstant.REFRESH_SCHOO_LLIST));
	        // 定位成功3次后停止定位
	        System.out.println("++++++++++" + mLocalCount);
	        mLocalCount++;
	        if(mLocalCount >= 3) {
	        	mLocationClient.stopLocation();
	        }
	        // 这里去请求广告页接口
	        if(!isGetAd) {
	        	getAD("qidong");
	        	getAD("banner_top");
	        	isGetAd = true;
	        }
	    } else {
	              //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//	        Log.e("AmapError","location Error, ErrCode:"
//	            + amapLocation.getErrorCode() + ", errInfo:"
//	            + amapLocation.getErrorInfo());
	        }
	    }
	}
	
	private void getAD(final String type) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_ad");
		postData.put("province", AppContext.getInstance().mAMapLocation.getProvince());
		postData.put("city", AppContext.getInstance().mAMapLocation.getCity());
		postData.put("area", AppContext.getInstance().mAMapLocation.getDistrict());
		postData.put("position", type);
		postData.put("mobile_type", android.os.Build.MODEL);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		postData.put("width", displayMetrics.widthPixels + "");
		postData.put("height", displayMetrics.heightPixels + "");
		postData.put("UUID", PubUtils.getDevId(this));
		postData.put("version", PubUtils.getSoftVersion(this));
		postData.put("app", "mewkao");
		AppContext.getInstance().netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				if("qidong".equals(type)) {
					// 启动页
					initQiDong(jsonObject);
				} else {
					// 广告页
					initGuangGao(jsonObject);
				}
				
			}
			@Override
			public void responseError() {
				
			}
		}, false, type);
	}

	protected void initGuangGao(JSONObject jsonObject) {
		try {
			JSONArray jsonArray = jsonObject.optJSONArray("message");
			if (jsonArray != null && !"null".equals(jsonArray)) {
				List<TopADImage> topADImages = new ArrayList<>();
				int len = jsonArray.length();
				for(int i=0; i<len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					if (object != null && !"null".equals(object)) {
						TopADImage topADImage = new TopADImage();
						topADImage.setName(object.optString("name"));
						topADImage.setPosition(object.optString("position"));
						topADImage.setTime(object.optString("time"));
						topADImage.setAct_name(object.optString("act_name"));
						topADImage.setAd_img_url(object.optString("ad_img_url"));
						topADImage.setUrl(object.optString("url"));
						topADImages.add(topADImage);
					}
				}
				AppContext.getInstance().mTopADImages = topADImages;
				// 并且发送广播，防止接口请求延迟
				sendBroadcast(new Intent(PubConstant.LOADING_GUANGGAO_IAMGE_KEY));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void initQiDong(JSONObject jsonObject) {
		try {
			JSONArray jsonArray = jsonObject.optJSONArray("message");
			if (jsonArray != null && !"null".equals(jsonArray)) {
				JSONObject object = jsonArray.getJSONObject(0);
				if (object != null && !"null".equals(object)) {
					String time = object.getString("time");
					String ad_img_url = object.getString("ad_img_url");
					// 启动页图片
					String sp_time = PreferenceUtils.getInstance().getString(PubConstant.LOADING_IMAGE_TIME, "");
					// 暂时不判断时间版本
//					if (!sp_time.equals(time)) {
						// 时间版本不一样，就得重新下载了
						if(!TextUtils.isEmpty(ad_img_url)) {
							// 删除缓存
//							MemoryCacheUtils.removeFromCache(ad_img_url, ImageLoader.getInstance().getMemoryCache());
//							DiskCacheUtils.removeFromCache(ad_img_url, ImageLoader.getInstance().getDiskCache());
							// 重新下载
							ImageLoader.getInstance().loadImage(ad_img_url, null);
						}

						PreferenceUtils.getInstance().putString(PubConstant.LOADING_IMAGE_TIME, time);
						PreferenceUtils.getInstance().putString(PubConstant.LOADING_IMAGE_PATH, ad_img_url);
//					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mLocationClient != null) {
			//销毁定位客户端。
			mLocationClient.onDestroy();
		}
	}
	
}
