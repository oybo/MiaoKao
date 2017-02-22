package com.miaokao.android.app.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;

/**
 * 上传文件公共类
 * 
 * @author ouyangbo@kingnode.com
 */
public class UploadUtil {

	private static UploadUtil instance;
	private Context mContext;

	private UploadUtil(Context context) {
		this.mContext = context;
	}

	public static UploadUtil getInstance(Context context) {
		if (instance == null) {
			instance = new UploadUtil(context);
		}
		return instance;
	}

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 
	 * @param url 	Service net address
	 * @param params 	text content
	 * @param files 	pictures
	 * @return String	 result of Service response
	 * @throws IOException
	 */
	public String post(String url, Map<String, String> params, Map<String, File> files) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(10 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}
		}

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// 发送文件数据
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\""
						+ file.getValue().getName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		int res = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		StringBuilder sb2 = new StringBuilder();
		if (res == 200) {
			int ch;
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		return sb2.toString();
	}

	/**
	 * 使用 表单 形式 上传文件
	 * 
	 * @param filePath
	 *            -- 上传文件路径
	 * @param url
	 *            -- 上传服务器地址
	 * @param params
	 *            -- 上传所要提交的参数
	 * @return --
	 */
	public String formUpload(String filePath, String url, Map<String, String> params) throws Exception {
		File file = new File(filePath);
		InputStream is = new FileInputStream(file);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		if (null != params && params.size() > 0) {
			// 表单参数
			HttpEntity entity = null;
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			try {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				entity = new UrlEncodedFormEntity(postParams, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			post.setEntity(entity);
		}
		
		ByteArrayBody isb = new ByteArrayBody(input2byte(is), file.getName());
//		InputStreamBody isb = new InputStreamBody(is, file.getName());
		
		MultipartEntity multipartEntity = new MultipartEntity();
		multipartEntity.addPart("uploadedfile[]", isb);
		multipartEntity.addPart("filename", new StringBody(file.getName()));
		post.setEntity(multipartEntity);
		HttpResponse response = client.execute(post);

		String result = null;
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			is = response.getEntity().getContent();
			result = inStream2String(is);
		}
		return result;
	}
	
	// 将输入流转换成字符串
	public static String inStream2String(InputStream is) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return new String(baos.toByteArray());
	}

	/**     InputStream to byte[]           */
	public static final byte[] input2byte(InputStream inStream) throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        return in2b;  
    }  
	
}
