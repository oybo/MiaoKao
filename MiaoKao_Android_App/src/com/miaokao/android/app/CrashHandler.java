package com.miaokao.android.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.coracle_photopicker_library.utils.FilePathUtils;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 * @author way
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例
	private Context mContext;// 程序的Context对象

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, final Throwable ex) {
		ex.printStackTrace();
		// 1秒钟后重启应用
		Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(i);
		//
		// Intent intent = new Intent(mContext, WelcomeActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1500, restartIntent);

		String ss = PreferenceUtils.getInstance().getString(PubConstant.IS_SAVE_CRASH_LOG, "");
		if (TextUtils.isEmpty(PreferenceUtils.getInstance().getString(PubConstant.IS_SAVE_CRASH_LOG, ""))) {
			new Thread() {
				public void run() {
					// 保存日志文件
					saveCrashInfo2File(ex);
					Looper.prepare();
					Toast.makeText(mContext, "很抱歉,程序出现异常,需要重启应用", 0).show();
					Looper.loop();
				}
			}.start();
			PreferenceUtils.getInstance().putString(PubConstant.IS_SAVE_CRASH_LOG, "save");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		// 退出程序
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private String saveCrashInfo2File(Throwable ex) {
		Log.e("error", ex.toString());
		StringBuffer sb = new StringBuffer();

		String errPath = getDefaultLogFileName(mContext);
		File f = new File(errPath);
		try {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();

				sb.append(PubUtils.getDevId(mContext));
			}
			sb.append("\r\n\r\n\r\n\r\ncrash==================================================\r\n");

			sb.append("\r\n user: " + PreferenceUtils.getInstance().getString(PubConstant.LOGIN_NAME_KEY, "") + " \r\n");

			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
			ex.printStackTrace(printWriter);
			sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":\r\n\r\n");
			sb.append(result.toString().replace("\n", "\r\n"));
			sb.append("\r\n\r\n");

			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(errPath, true);
			writer.write(sb.toString());
			printWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日志保存目录
	 * 
	 * @return
	 */
	private String getDefaultLogFileName(Context mContext) {
		return FilePathUtils.getDefaultLogPath(mContext) + "/" + mContext.getPackageName() + "-"
				+ new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".txt";
	}
}
