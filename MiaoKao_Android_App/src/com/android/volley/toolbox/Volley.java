/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.google.zxing.common.StringUtils;
import com.miaokao.android.app.R;

public class Volley {

	/**
	 * Default on-disk cache directory.
	 */
	private static final String DEFAULT_CACHE_DIR = "volley";

	private Context mContext;

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 * 
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for
	 *            default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, HttpStack stack, boolean selfSignedCertificate,
			int rawId) {
		File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				if (selfSignedCertificate) {
					stack = new HurlStack(null, buildSSLSocketFactory(context, rawId));
				} else {
					stack = new HurlStack();
				}
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				if (selfSignedCertificate)
					stack = new HttpClientStack(getHttpClient(context, rawId));
				else {
					stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
				}
			}
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
		queue.start();

		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 * 
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		// 如果你目前还没有证书,那么先用下面的这行代码,http可以照常使用.
		// return newRequestQueue(context, null, false, 0);
		// 此处R.raw.certificateName 表示你的证书文件,替换为自己证书文件名字就好
		return newRequestQueue(context, new HurlStack(null, createSslSocketFactory()), true, R.raw.miaokao_key);
	}

	private static SSLSocketFactory createSslSocketFactory() {
		TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}
		} };

		SSLContext sslContext = null;
		SSLSocketFactory sslSocketFactory = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, byPassTrustManagers, new SecureRandom());
			sslSocketFactory = sslContext.getSocketFactory();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
		}

		return sslSocketFactory;
	}

	private static SSLSocketFactory buildSSLSocketFactory(Context context, int certRawResId) {
		KeyStore keyStore = null;
		try {
			keyStore = buildKeyStore(context, certRawResId);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			sslContext.init(null, tmf.getTrustManagers(), null);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		return sslContext.getSocketFactory();

	}

	private static HttpClient getHttpClient(Context context, int certRawResId) {
		KeyStore keyStore = null;
		try {
			keyStore = buildKeyStore(context, certRawResId);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (keyStore != null) {
		}
		org.apache.http.conn.ssl.SSLSocketFactory sslSocketFactory = null;
		try {
			sslSocketFactory = new org.apache.http.conn.ssl.SSLSocketFactory(keyStore);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}

		HttpParams params = new BasicHttpParams();

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

		return new DefaultHttpClient(cm, params);
	}

	private static KeyStore buildKeyStore(Context context, int certRawResId) throws KeyStoreException,
			CertificateException, NoSuchAlgorithmException, IOException {
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);

		Certificate cert = readCert(context, certRawResId);
		keyStore.setCertificateEntry("ca", cert);

		return keyStore;
	}

	private static Certificate readCert(Context context, int certResourceID) {
		InputStream inputStream = context.getResources().openRawResource(certResourceID);
		Certificate ca = null;

		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			ca = cf.generateCertificate(inputStream);

		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return ca;
	}
}