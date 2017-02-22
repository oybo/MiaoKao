package com.miaokao.android.app.ui.activity;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.HeaderView.OnLeftClickListenner;

/**
 * @TODO webview页面
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-20 下午3:29:47
 */
public class WebviewActivity extends BaseActivity implements OnClickListener {

	private static final boolean WEB_CACHE = true;

	private WebView mWebView;
	private ImageView mBackImage;
	private int mBackCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_webview);

		initView();
	}

	private void initView() {
		Intent intent = getIntent();

		initTopBarLeftAndTitle(R.id.webview_common_actionbar, intent.getStringExtra("title"), new OnLeftClickListenner() {
			@Override
			public void onClick() {
				goBack();
			}
		});

		mWebView = (WebView) findViewById(R.id.webview);
		mBackImage = (ImageView) findViewById(R.id.webview_exit);
		mBackImage.setOnClickListener(this);

		mWebView.loadUrl(intent.getStringExtra("url"));

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
			String appCacheDir = getDir("cache", Context.MODE_PRIVATE).getPath();
			settings.setAppCachePath(appCacheDir);
		} else {
			settings.setAppCacheEnabled(false);
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		}

		settings.setAllowFileAccess(true);
		settings.setPluginState(PluginState.ON);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		String databasePath = getDir("localstorage", Context.MODE_PRIVATE).getPath();
		settings.setDatabasePath(databasePath);
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
				PubUtils.sendSms(WebviewActivity.this, number, "");
				return true;
			} else if (url.startsWith("tel:")) {
				// 拨打电话
				String number = url.substring(4);
				PubUtils.callPhone(WebviewActivity.this, number);
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
			} else {
				mBackCount++;
				mBackImage.setVisibility(View.VISIBLE);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.webview_exit:
			finish();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	private void goBack() {
		if(mWebView.canGoBack()) {
			mWebView.goBack();   //后退 
			mBackCount--;
			if(mBackCount == 0) {
				mBackImage.setVisibility(View.GONE);
			}
		} else {
			finish();
		}
	}
	
}
