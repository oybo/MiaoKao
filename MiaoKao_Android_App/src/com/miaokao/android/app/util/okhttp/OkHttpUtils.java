package com.miaokao.android.app.util.okhttp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.util.okhttp.helper.ProgressHelper;
import com.miaokao.android.app.util.okhttp.listener.impl.UIProgressListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * @TODO ok http 上传 与 下载
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-15 下午2:33:44
 */
public class OkHttpUtils {
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

	private static final OkHttpClient client = new OkHttpClient();

	private static Map<String, String> downloadings = new HashMap<>();

	/**
	 * 文件上传
	 * 
	 * @param url
	 * @param file
	 * @return
	 */
	public static String postFile(String url, File file) {
		String result = null;
		try {
			MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

			builder.addFormDataPart("fileName", file.getName());
			builder.addFormDataPart("uploadedfile[]", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));

			// 构建请求体
			RequestBody requestBody = builder.build();
			// 构建请求
			Request request = new Request.Builder().url(url)// 地址
					.addHeader("Cookie", AppContext.getInstance().mCookie).post(requestBody)// 添加请求体
					.build();

			Response response = client.newCall(request).execute();

			if (response.isSuccessful()) {
				result = response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 文件下载
	 * 
	 * @param url
	 */
	public static void dowloadFile(String url, UIProgressListener uiProgressListener, final DownloadFileListenner listenner) {
		if(downloadings.containsKey(url)) {
			return;
		}
		downloadings.put(url, "");
		// 构造请求
		final Request request1 = new Request.Builder().url(url).build();
		// 包装Response使其支持进度回调
		ProgressHelper.addProgressResponseListener(client, uiProgressListener).newCall(request1)
				.enqueue(new Callback() {
					
					@Override
					public void onResponse(Response response) throws IOException {
						listenner.onResponse(response);
					}
					
					@Override
					public void onFailure(Request request, IOException e) {
						listenner.onFailure(e);
						downloadings.remove(request.urlString());
					}
				});
	}
	
	public interface DownloadFileListenner {
		void onResponse(Response response);
		void onFailure(IOException e);
	}

}
