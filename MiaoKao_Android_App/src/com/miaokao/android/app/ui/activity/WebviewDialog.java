package com.miaokao.android.app.ui.activity;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.HeaderView;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.SharePopupWindow;
import com.miaokao.android.app.widget.SharePopupWindow.DissListenner;

/**
 * @TODO Webview浏览器 Dialog
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-30 上午10:44:54
 */
public class WebviewDialog extends Dialog {

	private static final boolean WEB_CACHE = true;
	private Context mContext;
	private String mTitle, mUrl;
	private WebView mWebView;
	private SharePopupWindow mSharePopupWindow;
	private View mView;
	private Handler mHandler = new Handler();

	public WebviewDialog(Context context, String title, String url) {
		super(context, R.style.selector_dialog);

		mContext = context;
		mTitle = title;
		mUrl = url;
		init();
	}

	private void init() {
		setContentView(R.layout.activity_webview);

		Window window = getWindow();
		window.getAttributes().width = LayoutParams.MATCH_PARENT;
		window.getAttributes().height = LayoutParams.MATCH_PARENT;
		window.setWindowAnimations(R.style.anim_activity_dialog_dir);
		initView();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.webview_common_actionbar, mTitle, R.drawable.gengduo, new OnRightClickListenner() {
			@Override
			public void onClick() {
				mView.setVisibility(View.VISIBLE);
				String title = "我在喵考学车，现在报名有优惠哦";
				String content = "我在喵考学车，现在报名有优惠哦";
				String imageUrl = "";
				if (AppContext.getInstance().mUser != null) {
					imageUrl = AppContext.getInstance().mUser.getHead_img();
				}
				String url = mUrl;
				mSharePopupWindow = new SharePopupWindow(mContext, ((Activity) mContext).getWindow(), title, content, imageUrl, url);
				mSharePopupWindow.showAtLocation(mWebView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				mSharePopupWindow.setDissListenner(new DissListenner() {
					@Override
					public void dissmiss() {
						mView.setVisibility(View.GONE);
					}
				});
			}
		});

		mView = findViewById(R.id.webview_dialog_bg_view);
		mWebView = (WebView) findViewById(R.id.webview);

		mWebView.loadUrl(mUrl);

		// 设置webview支持点击连接下载，
		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(final String url, final String userAgent, final String contentDisposition,
					final String mimetype, final long contentLength) {
				if (url.toLowerCase().startsWith("file://")) {
					// 本地文件，直接不管了。。
					return;
				}
				// 这里以后可以支持点击弹出提示框，然后确定再去Native端实现下载，
			}
		});

		// WebviewClient
		mWebView.setWebViewClient(new WasWebviewClient(mWebView));
		// WebchromeClient
		mWebView.setWebChromeClient(new WasWebchromeClient(mWebView));
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// WebView.setWebContentsDebuggingEnabled(true);
		// }
		resume();
		WebSettings settings = mWebView.getSettings();
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);

		if (WEB_CACHE) {
			settings.setAppCacheEnabled(true);
			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
			settings.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小，我设的是8M
			String appCacheDir = mContext.getDir("cache", Context.MODE_PRIVATE).getPath();
			settings.setAppCachePath(appCacheDir);
		} else {
			settings.setAppCacheEnabled(false);
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		}

		settings.setAllowFileAccess(true);
		settings.setPluginState(PluginState.ON);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		String databasePath = mContext.getDir("localstorage", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(databasePath);
	}

	private void initTopBarLeftAndTitle(int layoutId, String title, int resouceId, OnRightClickListenner listenner) {
		HeaderView mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView = (HeaderView) findViewById(layoutId);
		mHeaderView.setTitile(title);
		mHeaderView.setRightImg(resouceId);
		mHeaderView.setOnLeftClickListenner(new HeaderView.OnLeftClickListenner() {

			@Override
			public void onClick() {
				dismiss();
			}
		});
		mHeaderView.setOnRightClickListenner(listenner);
	}

	@SuppressLint("NewApi")
	public void resume() {
		// 处理硬件加速
		int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		if (version >= 11) {
			mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		// 设置滚动条在3.0版本之前不隐藏
		if (version < 11) {
			mWebView.setVerticalScrollBarEnabled(false);
		}
		// 支持跨页面访问
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		}
	}

	private class WasWebviewClient extends WebViewClient {

		private static final String TAG = "WebRuntime";
		private WebView webview;

		public WasWebviewClient(WebView webview) {
			this.webview = webview;
		}

		// @Override
		@Override
		public void onPageFinished(final WebView view, final String url) {
			// 用异步的方式，通知WasWebview加载完成了
		}

		@Override
		public void onPageStarted(WebView view, final String url, Bitmap favicon) {
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("sms:")) {
				// 发送短信
				String number = url.substring(4);
				PubUtils.sendSms(mContext, number, "");
				return true;
			} else if (url.startsWith("tel:")) {
				// 拨打电话
				String number = url.substring(4);
				PubUtils.callPhone(mContext, number);
				return true;
			} else if (url.startsWith("mailto:")) {
				// 发送邮件
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");

				String p = url.substring(7);
				int idx = p.indexOf('?');
				String addresses = p;
				String query = null;
				if (idx >= 0) {
					addresses = p.substring(0, idx);
					query = p.substring(idx + 1);
				}
				// 收件人
				String[] emailAddList = addresses.split(";");
				if (emailAddList != null && emailAddList.length > 0) {
					intent.putExtra(Intent.EXTRA_EMAIL, emailAddList);
				}
				if (query != null) {
					String[] params = query.split("&");
					for (String param : params) {
						int ii = param.indexOf('=');
						if (ii > 0) {
							String name = param.substring(0, ii);
							String value = param.substring(ii + 1);
							try {
								value = java.net.URLDecoder.decode(value, "utf-8");
							} catch (UnsupportedEncodingException e) {
								continue;
							}
							if (name.equalsIgnoreCase("SUBJECT")) {
								// 标题
								intent.putExtra(Intent.EXTRA_SUBJECT, value);
							} else if (name.equalsIgnoreCase("BODY")) {
								// 内容
								intent.putExtra(Intent.EXTRA_TEXT, value);
							} else if (name.equalsIgnoreCase("CC")) {
								// 抄送
								String[] cc = value.split(";");
								if (cc != null && cc.length > 0) {
									intent.putExtra(Intent.EXTRA_CC, cc);
								}
							} else if (name.equalsIgnoreCase("BCC")) {
								// 密送
							}
						}
					}
				}
				webview.getContext().startActivity(Intent.createChooser(intent, "发送邮件"));
				return true;
			}
			return false;
		}
	}

	private class WasWebchromeClient extends WebChromeClient {

		private WebView webview;

		public WasWebchromeClient(WebView webview) {
			this.webview = webview;
		}

		// @Override
		// 这个是2.3以前的
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		}

		// @Override
		// 这个是2.3以及2.3以后的
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId());
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			AlertDialog dlg = new AlertDialog.Builder(webview.getContext()).setTitle(R.string.app_name)
					.setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							result.confirm();
						}
					}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							result.cancel();
						}
					}).create();
			dlg.setCanceledOnTouchOutside(false);
			dlg.show();
			return true;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			AlertDialog dlg = new AlertDialog.Builder(webview.getContext()).setTitle(R.string.app_name)
					.setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							result.confirm();
						}
					}).create();
			dlg.setCanceledOnTouchOutside(false);
			dlg.show();
			return true;
		}

		@Override
		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(estimatedSize * 2);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack(); // 后退
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
