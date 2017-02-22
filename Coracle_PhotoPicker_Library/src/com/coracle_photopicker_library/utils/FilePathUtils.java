package com.coracle_photopicker_library.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件路径工具类
 * 
 * @author ouyangbo
 * 
 */
public class FilePathUtils {

	public static final String UNZIP_PATH_NAME = "unZip";
	public static final String DB_PATH_NAME = "db";
	public static final String FILE_PATH_NAME = "file";
	public static final String IMAGE_PATH_NAME = "image";
	public static final String TEMP_ZIP_PATH_NAME = "zipTemp";
	public static final String RECORD_PATH_NAME = "record";
	public static final String VIDEO_PATH_NAME = "video";
	public static final String LOG_PATH_NAME = "log";

	private static String cachePath;

	/**
	 * 获得当前应用默认的解压路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultUnzipFile(Context context) {
		return getPath(context, UNZIP_PATH_NAME);
	}

	/**
	 * 获取SD卡目录下相对应包名程序下的数据库的路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultDataBasePath(Context context) {
		return getPath(context, DB_PATH_NAME);
	}

	/**
	 * 获取SD卡目录下相对应包名程序下的文件保存的图片的路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultFilePath(Context context) {
		return getPath(context, FILE_PATH_NAME);
	}

	/**
	 * 获取SD卡目录下相对应包名程序下的拍照保存的图片的路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultImagePath(Context context) {
		return getPath(context, IMAGE_PATH_NAME);
	}

	/**
	 * 获取SD卡目录下相对应包名程序下的临时缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultTempZipFilePath(Context context) {
		File cacheDir = getDiskCacheDir(context, TEMP_ZIP_PATH_NAME);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir.getAbsolutePath();
	}

	/**
	 * 获取SD卡目录下相对应包名程序下的录音保存的图片的路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultRecordPath(Context context) {
		return getPath(context, RECORD_PATH_NAME);
	}

	/**
	 * 获取SD卡目录下相对应包名程序下的视频保存的图片的路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultVideoPath(Context context) {
		return getPath(context, VIDEO_PATH_NAME);
	}

	/**
	 * 日志保存目录
	 * 
	 * @return
	 */
	public static String getDefaultLogPath(Context context) {
		return getPath(context, LOG_PATH_NAME);
	}

	/**
	 * 获得SD卡上缓存目录下文件夹路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getPath(Context context, String dirName) {
		File cacheDir = getDiskCacheDir(context, dirName);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir.getAbsolutePath();
	}

	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址。
	 */
	@SuppressLint("NewApi")
	private static File getDiskCacheDir(Context context, String uniqueName) {
		if (TextUtils.isEmpty(cachePath) || !new File(cachePath).exists()) {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !Environment.isExternalStorageRemovable()) {
				if (context.getExternalCacheDir() != null) {
					cachePath = context.getExternalCacheDir().getPath();
				} else {
					cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
				}
			} else {
				cachePath = context.getCacheDir().getPath();
			}
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	public static boolean isPicture(File file) {
		if (null == file) {
			return false;
		}
		return getMIMEType(file).startsWith("image/");
	}

	private static String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if ("".equals(end)) {
			return type;
		}
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	public static List<String> getExtSDCardPaths() {
		List<String> paths = new ArrayList<String>();
		String extFileStatus = Environment.getExternalStorageState();
		File extFile = Environment.getExternalStorageDirectory();
		if (extFileStatus.equals(Environment.MEDIA_MOUNTED) && extFile.exists() && extFile.isDirectory() && extFile.canWrite()) {
			paths.add(extFile.getAbsolutePath());
		}
		try {
			// obtain executed result of command line code of 'mount', to judge
			// whether tfCard exists by the result
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("mount");
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int mountPathIndex = 1;
			while ((line = br.readLine()) != null) {
				// format of sdcard file system: vfat/fuse
				if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage")) || line.contains("secure")
						|| line.contains("asec") || line.contains("firmware") || line.contains("shell") || line.contains("obb")
						|| line.contains("legacy") || line.contains("data")) {
					continue;
				}
				String[] parts = line.split(" ");
				int length = parts.length;
				if (mountPathIndex >= length) {
					continue;
				}
				String mountPath = parts[mountPathIndex];
				if (!mountPath.contains("/") || mountPath.contains("data") || mountPath.contains("Data")) {
					continue;
				}
				File mountRoot = new File(mountPath);
				if (!mountRoot.exists() || !mountRoot.isDirectory() || !mountRoot.canWrite()) {
					continue;
				}
				boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
				if (equalsToPrimarySD) {
					continue;
				}
				paths.add(mountPath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paths;
	}

	private final static String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" }, { ".apk", "application/vnd.android.package-archive" }, { ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" }, { ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" }, { ".c", "text/plain" },
			{ ".class", "application/octet-stream" }, { ".conf", "text/plain" }, { ".cpp", "text/plain" }, { ".doc", "application/msword" },
			{ ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, { ".xls", "application/vnd.ms-excel" },
			{ ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }, { ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" }, { ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" }, { ".h", "text/plain" },
			{ ".htm", "text/html" }, { ".html", "text/html" }, { ".jar", "application/java-archive" }, { ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" }, { ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" }, { ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" }, { ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" }, { ".mov", "video/quicktime" }, { ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" }, { ".mp4", "video/mp4" }, { ".mpc", "application/vnd.mpohun.certificate" }, { ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" }, { ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" }, { ".pdf", "application/pdf" }, { ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" }, { ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" }, { ".prop", "text/plain" },
			{ ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" }, { ".sh", "text/plain" },
			{ ".tar", "application/x-tar" }, { ".tgz", "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav", "audio/x-wav" },
			{ ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" }, { ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" }, { ".zip", "application/x-zip-compressed" }, { "", "*/*" } };
}
