package com.miaokao.android.app.util;

import java.io.File;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.miaokao.android.app.util.DownloadFileManager.DownloadListenner;

public class NotifiDownLoadManager {

	private static NotifiDownLoadManager mInstance;
	private int mId = 0;
	private NotificationManager mNotificationManager;
	private Context mContext;

	private NotifiDownLoadManager(Context context) {
		// 初始化Notification对象
		mContext = context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static NotifiDownLoadManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (NotifiDownLoadManager.class) {
				if (mInstance == null) {
					mInstance = new NotifiDownLoadManager(context);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 开启下载，以notification通知的形式显示，百分百形式
	 * 
	 * @param context
	 * @param url
	 */
	public void startNotiUpdateTask(final String url, final String title, String message) {
		int id = mId++;
		// 调用下载
		startDownload(url, title, message, id);
	}

	private void startDownload(String url, String title, final String message, final int id) {
		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle(message)
				.setWhen(System.currentTimeMillis()).setContentText("当前下载:").setOngoing(true).setTicker(title)
				.setProgress(100, 0, false);
		mNotificationManager.notify(id, mBuilder.build());
		DownloadFileManager.downloadFile(mContext, url, new DownloadListenner() {
			private int mAllSize = 0;
			private long lastClickTime;

			@Override
			public void downloadProgress(String path, int progress) {
				if (mAllSize > 0) {
					long time = System.currentTimeMillis();
					long timeD = time - lastClickTime;
					if (0 < timeD && timeD < 500) {
						return; // 防止更新速度过快，而产生卡顿
					}
					lastClickTime = time;

					String value = PubUtils.getSHCollagen(mAllSize, progress);
					mBuilder.setProgress(mAllSize, progress, false);
					mBuilder.setContentText("当前下载:" + value + " 已接收" + PubUtils.formatFileSizeToString(progress) + "/"
							+ PubUtils.formatFileSizeToString(mAllSize));
					mNotificationManager.notify(id, mBuilder.build());
				}
			}

			@Override
			public void downloadFinish(String path, File saveFile) {
				mNotificationManager.cancel(id);
				// 调用安装
				PubUtils.installApk(mContext, saveFile);
			}

			@Override
			public void downloadError(String path) {
				mNotificationManager.cancel(id);
				Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void downloadBegin(String path, int allSize) {
				mAllSize = allSize;
			}
		});
	}

}
