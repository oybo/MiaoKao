package com.coracle_share_library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.qzone.QZone.ShareParams;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 分享工具类
 * 
 * @author ouyangbo@coracle.com
 * 
 */
public class ShareUtils {

	private static final String TENCEN_WB_PACKAGENAME = "com.tencent.WBlog";
	private static final String TENCEN_QQ_PACKAGENAME = "com.tencent.mobileqq";
	private static final String TENCEN_WX_PACKAGENAME = "com.tencent.mm";

	private static ShareUtils instance = null;
	private Context mContext;

	public static ShareUtils getInstance(Context context) {
		if (instance == null) {
			synchronized (ShareUtils.class) {
				if (instance == null) {
					instance = new ShareUtils(context);
				}
			}
		}
		return instance;
	}

	private ShareUtils(Context context) {
		mContext = context;
		ShareSDK.initSDK(mContext, ShareConfig.APPKEY);
	}

	/**
	 * QQ空间分享
	 * @param title	--标题
	 * @param titleUrl	--标题的网络链接[无则可以填空]
	 * @param txt	--文本内容 
	 * @param imageUrl	--图片网络地址
	 * @param imagePath	--图片本地地址
	 * @param site	--分享此内容的网站名称 [无则可以填空]
	 * @param siteUrl	--分享此内容的网站地址 [无则可以填空]
	 * @param listenner	--分享回调 [无则可以填空]
	 */
	public void qZoneShare(String title, String titleUrl, String txt, String imageUrl, String imagePath, String site,
			String siteUrl, final ShareCallBackListenner listenner) {
		boolean isClient = false;
		if (!isInstanll(TENCEN_QQ_PACKAGENAME)) {
			Toast.makeText(mContext, "QQ未安装，请先安装。", Toast.LENGTH_SHORT).show();
			return;
		} else {
			isClient = true;
			Toast.makeText(mContext, "加载中,请稍后...", Toast.LENGTH_SHORT).show();
		}
		
		cn.sharesdk.tencent.qzone.QZone.ShareParams sp = new cn.sharesdk.tencent.qzone.QZone.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);// 设置分享类型，有文本，图片 ,等等
		sp.setTitle(title);
		sp.setTitleUrl(TextUtils.isEmpty(titleUrl) ? "http://www.baidu.com" : titleUrl); // 该字段不能为空
		sp.setText(txt);
		sp.setImageUrl(imageUrl);
		sp.setImagePath(imagePath);
		sp.setSite(site);
		sp.setSiteUrl(siteUrl);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AppId", ShareConfig.APPID_QZONE);
		map.put("AppKey", ShareConfig.APPKEY_QZONE);
		// 如果已经安装客户端则客户端分享，否则网页
		if (isClient) {
			map.put("ShareByAppClient", "true");
		} else {
			map.put("ShareByAppClient", "false");
		}
		map.put("Enable", ShareConfig.ENABLE_QZONE);
		ShareSDK.setPlatformDevInfo(QZone.NAME, map);

		Platform qzone = ShareSDK.getPlatform(mContext, QZone.NAME);
		if (listenner != null) {
			qzone.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					listenner.error();
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					listenner.success();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					listenner.cancel();
				}
			});
		}
		qzone.share(sp);
	}

	/**
	 * QQ好友分享
	 * @param title  --标题 [无则可以填空]
	 * @param titleUrl  --标题的网络链接 [无则可以填空]
	 * @param txt  --内容 [无则可以填空]
	 * @param imageUrl  --图片网络地址 [无则可以填空]
	 * @param imagePath  --图片本地地址 [无则可以填空]
	 * @param listenner --分享回调 [无则可以填空]
	 */
	public void qqShare(String title, String titleUrl, String txt, String imageUrl, String imagePath,
			final ShareCallBackListenner listenner) {
		boolean isClient = false;
		if (!isInstanll(TENCEN_QQ_PACKAGENAME)) {
			Toast.makeText(mContext, "QQ未安装，请先安装。", Toast.LENGTH_SHORT).show();
			return;
		} else {
			isClient = true;
			Toast.makeText(mContext, "加载中,请稍后...", Toast.LENGTH_SHORT).show();
		}
		
		cn.sharesdk.tencent.qq.QQ.ShareParams sp = new cn.sharesdk.tencent.qq.QQ.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle(title);
		sp.setTitleUrl(TextUtils.isEmpty(titleUrl) ? "http://www.baidu.com" : titleUrl);
		sp.setText(txt);
		sp.setImageUrl(imageUrl);
		sp.setImagePath(imagePath);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AppId", ShareConfig.APPID_QQFRIEND);
		map.put("AppKey", ShareConfig.APPKEY_QQFRIEND);
		// 如果已经安装客户端则客户端分享，否则网页
		if (isClient) {
			map.put("ShareByAppClient", "true");
		} else {
			map.put("ShareByAppClient", "false");
		}
		map.put("Enable", ShareConfig.ENABLE_QQFRIEND);
		ShareSDK.setPlatformDevInfo(QQ.NAME, map);

		Platform qq = ShareSDK.getPlatform(mContext, QQ.NAME);
		if (listenner != null) {
			qq.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					listenner.error();
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					listenner.success();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					listenner.cancel();
				}
			});
		}
		qq.share(sp);
	}

	/**
	 * 微信朋友圈的分享
	 * @param title --标题
	 * @param txt  --文本内容
	 * @param bitmap --标识图片，Bitmap
	 * @param imageUrl  --图片网络地址
	 * @param imagePath --图片本地地址
	 * @param url  --连接地址
	 * @param listenner  --回调
	 */
	public void wechatMomentsShare(String title, String txt, Bitmap bitmap, String imageUrl, String imagePath,
			String url, final ShareCallBackListenner listenner) {
		boolean isClient = false;
		if (!isInstanll(TENCEN_WX_PACKAGENAME)) {
			Toast.makeText(mContext, "微信未安装，请先安装。", Toast.LENGTH_SHORT).show();
			return;
		} else {
			isClient = true;
			Toast.makeText(mContext, "加载中,请稍后...", Toast.LENGTH_SHORT).show();
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AppId", ShareConfig.APPID_CIRCLE_FRIEND);
		map.put("AppSecret", ShareConfig.APPSECRET_CIRCLE_FRIEND);
		map.put("BypassApproval", ShareConfig.BYPASSAPPROVAL_CIRCLE_FRIEND);
		// 如果已经安装客户端则客户端分享，否则网页
		if (isClient) {
			map.put("ShareByAppClient", "true");
		} else {
			map.put("ShareByAppClient", "false");
		}
		map.put("Enable", ShareConfig.ENABLE_CIRCLE_FRIEND);

		ShareSDK.setPlatformDevInfo(WechatMoments.NAME, map);

		cn.sharesdk.wechat.moments.WechatMoments.ShareParams sp = new cn.sharesdk.wechat.moments.WechatMoments.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle(title);
		sp.setText(txt);
		sp.setImageData(bitmap);
		sp.setImageUrl(imageUrl);
		sp.setImagePath(imagePath);
		sp.setUrl(url);
		
//		cn.sharesdk.wechat.moments.WechatMoments.ShareParams sp = new cn.sharesdk.wechat.moments.WechatMoments.ShareParams();
//		sp.setShareType(Platform.SHARE_IMAGE);
//		sp.setImageUrl(imageUrl);

		Platform wechatMoments = ShareSDK.getPlatform(mContext, WechatMoments.NAME);
		if (listenner != null) {
			wechatMoments.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					listenner.error();
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					listenner.success();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					listenner.cancel();
				}
			});
		}
		wechatMoments.share(sp);
	}

	/**
	 * 微信好友的分享
	 * @param title  --标题
	 * @param txt  --内容
	 * @param bitmap  --图片辨识
	 * @param imageUrl --图片网络地址
	 * @param imagePath --图片本地地址
	 * @param url  --链接地址
	 * @param listenner --分享回调
	 */
	public void wechatShare(String title, String txt, Bitmap bitmap, String imageUrl, String imagePath, String url,
			final ShareCallBackListenner listenner) {
		boolean isClient = false;
		if (!isInstanll(TENCEN_WX_PACKAGENAME)) {
			Toast.makeText(mContext, "微信未安装，请先安装。", Toast.LENGTH_SHORT).show();
			return;
		} else {
			isClient = true;
			Toast.makeText(mContext, "加载中,请稍后...", Toast.LENGTH_SHORT).show();
		}

		ShareParams sp = new ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle(title);
		sp.setText(txt);
		sp.setImageUrl(imageUrl);
		sp.setImageData(bitmap);
		sp.setImagePath(imagePath);
		sp.setUrl(url);
		
//		cn.sharesdk.wechat.moments.WechatMoments.ShareParams sp = new cn.sharesdk.wechat.moments.WechatMoments.ShareParams();
//		sp.setShareType(Platform.SHARE_IMAGE);
//		sp.setImageUrl(imageUrl);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AppId", ShareConfig.APPID_WXFRIEND);
		map.put("AppSecret", ShareConfig.APPSECRET_WXFRIEND);
		map.put("BypassApproval", ShareConfig.BYPASSAPPROVAL_WXFRIEND);
		// 如果已经安装客户端则客户端分享，否则网页
		if (isClient) {
			map.put("ShareByAppClient", "true");
		} else {
			map.put("ShareByAppClient", "false");
		}
		map.put("Enable", ShareConfig.ENABLE_WXFRIEND);

		ShareSDK.setPlatformDevInfo(WechatMoments.NAME, map);

		Platform wechat = ShareSDK.getPlatform(mContext, Wechat.NAME);
		if (listenner != null) {
			wechat.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					listenner.error();
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					listenner.success();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					listenner.cancel();
				}
			});
		}
		wechat.share(sp);
	}

	/**
	 * 新浪微博分享
	 * @param title  --标题
	 * @param txt  --内容
	 * @param url  --连接
	 * @param imageUrl --图片的网络路径
	 * @param imagePath --图片的本地路径
	 * @param listenner  --分享回调
	 */
	public void sineWeiBoShare(String title, String txt, String url, String imageUrl, String imagePath,
			final ShareCallBackListenner listenner) {
		cn.sharesdk.sina.weibo.SinaWeibo.ShareParams sp = new cn.sharesdk.sina.weibo.SinaWeibo.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle(title);
		sp.setText(txt + url);
		sp.setImageUrl(imageUrl);
		sp.setImagePath(imagePath);

		Platform weibo = ShareSDK.getPlatform(mContext, SinaWeibo.NAME);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AppKey", ShareConfig.APPKEY_SINA_WEIBO);
		map.put("AppSecret", ShareConfig.APPSECRET_SINA_WEIBO);
		map.put("RedirectUrl", ShareConfig.REDIRECTURL_SINA_WEIBO);
		map.put("ShareByAppClient", ShareConfig.SHAREBYAPPCLIENT_SINA_WEIBO);
		map.put("Enable", ShareConfig.ENABLE_SINA_WEIBO);
		ShareSDK.setPlatformDevInfo(SinaWeibo.NAME, map);

		if (listenner != null) {
			weibo.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					listenner.error();
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					listenner.success();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					listenner.cancel();
				}
			});
		}
		weibo.share(sp);
	}

	/**
	 * 腾讯微博分享
	 * @param title  --标题
	 * @param txt  --内容
	 * @param url  --连接
	 * @param imageUrl --图片的网络路径
	 * @param imagePath --图片的本地路径
	 * @param listenner  --分享回调
	 */
	public void tencentWeiBoShare(String title, String txt, String url, String imageUrl, String imagePath,
			final ShareCallBackListenner listenner) {
		// 腾讯微博分享 需要安装客户端
		if (!isInstanll(TENCEN_WB_PACKAGENAME)) {
			Toast.makeText(mContext, "腾讯微博未安装，请先安装。", Toast.LENGTH_SHORT).show();
			return;
		} else {
			Toast.makeText(mContext, "加载中,请稍后...", Toast.LENGTH_SHORT).show();
		}
		cn.sharesdk.tencent.weibo.TencentWeibo.ShareParams sp = new cn.sharesdk.tencent.weibo.TencentWeibo.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle(title);
		sp.setText(txt + url);
		sp.setImageUrl(imageUrl);
		sp.setImagePath(imagePath);

		Platform weibo = ShareSDK.getPlatform(mContext, TencentWeibo.NAME);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("AppKey", ShareConfig.APPKEY_TX_WEIBO);
		map.put("AppSecret", ShareConfig.APPSECRET_TX_WEIBO);
		map.put("RedirectUrl", ShareConfig.REDIRECTURL_TX_WEIBO);
		map.put("Enable", ShareConfig.ENABLE_TX_WEIBO);
		ShareSDK.setPlatformDevInfo(TencentWeibo.NAME, map);

		if (listenner != null) {
			weibo.setPlatformActionListener(new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					listenner.error();
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					listenner.success();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					listenner.cancel();
				}

			});
		}
		weibo.share(sp);
	}

	/**
	 * 分享回调
	 * 
	 * @author ouyangbo@coracle.com
	 */
	public interface ShareCallBackListenner {

		public void error();

		public void success();

		public void cancel();

	}

	private boolean isInstanll(String packageName) {
		final PackageManager pack = mContext.getPackageManager();
		List<PackageInfo> pinfo = pack.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
				if (pn.contains(packageName)) {
					return true;
				}
			}
		}
		return false;
	}
}
