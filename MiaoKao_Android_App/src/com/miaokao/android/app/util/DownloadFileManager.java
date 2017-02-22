package com.miaokao.android.app.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.coracle_photopicker_library.utils.FilePathUtils;

/**
 * 管理下载的工具类
 * 
 * @author: ouyangbo
 * @date: 2014-11-13
 * @email: ouyangbo@kingnode.com
 * @remark:
 * @modify by:
 */
public class DownloadFileManager {

	private static ExecutorService mFileThreadPool = Executors.newFixedThreadPool(1);

	private static final int BUFFER_SIZE = 1024 * 10;
	private static final int CONNECTION_TIME_OUT = 60 * 1000;
	private static final int SO_TIME_OUT = 60 * 1000;
	private static final int DEFAULT_HOST_CONNECTIONS = 300;
	private static final int DEFAULT_MAX_CONNECTIONS = 600;
	private static HttpClient mHttpClient;
	public static Map<String, String> mDownloadFiles = new HashMap<>();

	/**
	 * 添加一个单下载任务
	 * 
	 * @param downPath
	 *            -- 下载url
	 * @param downloadListenner
	 *            --下载回调接口
	 */
	@SuppressLint("HandlerLeak")
	public static void downloadFile(Context context, String downPath, DownloadListenner downloadListenner) {
		if (TextUtils.isEmpty(downPath)) {
			return;
		}
		FileDownloadTask task = new FileDownloadTask(context, downPath, downloadListenner);
		task.executeOnExecutor(mFileThreadPool);
	}

	static class FileDownloadTask extends AsyncTask<Void, Integer, File> {
		private Context mContext;
		private String mDownPath;
		private DownloadListenner mListenner;

		public FileDownloadTask(Context context, final String downPath, DownloadListenner listenner) {
			this.mContext = context;
			this.mDownPath = downPath;
			this.mListenner = listenner;
			mDownloadFiles.put(downPath, "0");
		}

		@Override
		protected File doInBackground(Void... params) {
			HttpResponse response = null;
			try {
				long mFileSize = 0;
				long localFileLenght = 0;
				String fileName = getFileName(mDownPath);
				File file = new File(FilePathUtils.getDefaultFilePath(mContext), fileName);

				if (file.exists()) {
					// 如果文件存在，就得考虑是否断点下载了，
					mFileSize = getNetFileSize(mDownPath); // 网络文件大小
					localFileLenght = file.length(); // 本地文件大小
					if (localFileLenght >= mFileSize) {
						file.delete();
						file.createNewFile();
						localFileLenght = 0;
					}
				}

				HttpClient httpClient = getHttpClient();
				HttpGet httpget = new HttpGet(mDownPath);
				if (localFileLenght > 0) {
					// 断点下载，设置下载的数据位置XX字节到XX字节
					httpget.addHeader("RANGE", "bytes=" + localFileLenght + "-");
				}

				response = httpClient.execute(httpget);
				if (mFileSize <= 0) {
					mFileSize = response.getEntity().getContentLength();
				}

				mListenner.downloadBegin(mDownPath, Integer.parseInt(mFileSize + ""));

				int sCode = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				if (entity != null && sCode < HttpStatus.SC_BAD_REQUEST) {
					int progress = 0;
					if (entity != null) {

						RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
						randomAccessFile.seek(localFileLenght);

						InputStream inputStream = entity.getContent();
						int b = 0;
						final byte buffer[] = new byte[1024];
						while ((b = inputStream.read(buffer)) != -1) {
							randomAccessFile.write(buffer, 0, b);
							progress += b;
							publishProgress(progress);
						}
						randomAccessFile.close();
						inputStream.close();
					}
					return file;
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			mListenner.downloadProgress(mDownPath, values[0]);
		}

		@Override
		protected void onPostExecute(File file) {
			if (null == file) {
				mListenner.downloadError(mDownPath);
				mDownloadFiles.put(mDownPath, "-1");
			} else {
				if (file.length() == 0) {
					mDownloadFiles.put(mDownPath, "-1");
					mListenner.downloadError(mDownPath);
				} else {
					mDownloadFiles.put(mDownPath, "1");
					mListenner.downloadFinish(mDownPath, file);
				}
			}
		};
	}

	public static String getFileName(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			int dot = filePath.lastIndexOf("/");
			if ((dot > -1) && (dot < (filePath.length() - 1))) {
				return filePath.substring(dot + 1, filePath.length());
			}
		}
		return filePath;
	}

	/** 获取网络地址文件大小 */
	public static Long getNetFileSize(String url) {
		Long count = -1L;
		final HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter("http.socket.timeout", 5000);

		final HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			final int code = response.getStatusLine().getStatusCode();
			final HttpEntity entity = response.getEntity();
			if (entity != null && code == 200) {
				count = entity.getContentLength();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return count;
	}

	public static HttpClient getHttpClient() {
		if (mHttpClient == null) {
			synchronized (DownloadFileManager.class) {
				if (mHttpClient == null) {
					final HttpParams httpParams = new BasicHttpParams();

					// timeout: get connections from connection pool
					ConnManagerParams.setTimeout(httpParams, 1000);
					// timeout: connect to the server
					HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIME_OUT);
					// timeout: transfer data from server
					HttpConnectionParams.setSoTimeout(httpParams, SO_TIME_OUT);

					// set max connections per host
					ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(
							DEFAULT_HOST_CONNECTIONS));
					// set max total connections
					ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

					// use expect-continue handshake
					HttpProtocolParams.setUseExpectContinue(httpParams, true);
					// disable stale check
					HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

					HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
					HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

					HttpClientParams.setRedirecting(httpParams, false);

					// set user agent
					String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
					HttpProtocolParams.setUserAgent(httpParams, userAgent);

					// disable Nagle algorithm
					HttpConnectionParams.setTcpNoDelay(httpParams, true);
					HttpConnectionParams.setSocketBufferSize(httpParams, BUFFER_SIZE);

					// scheme: http and https
					SchemeRegistry schemeRegistry = new SchemeRegistry();
					schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
					schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

					ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
					mHttpClient = new DefaultHttpClient(manager, httpParams);
				}
			}
		}
		return mHttpClient;
	}

	/**
	 * 文件下载的回调接口
	 * 
	 * @author: ouyangbo
	 * @date: 2014-11-13
	 * @email: ouyangbo@kingnode.com
	 * @remark:
	 * @modify by:
	 */
	public interface DownloadListenner {

		public void downloadBegin(String path, int allSize);

		public void downloadFinish(String path, File saveFile);

		public void downloadProgress(String path, int progress);

		public void downloadError(String path);
	}
}
