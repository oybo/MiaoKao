package com.miaokao.android.app.recerver;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.miaokao.android.app.inteface.LoginStatusListenner;
import com.miaokao.android.app.util.PubConstant;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-12 上午10:36:37 
 */
public class MyRecerver extends BroadcastReceiver {

	public static List<LoginStatusListenner> mListenner = new ArrayList<LoginStatusListenner>();
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if(PubConstant.LOGIN_STATE_KEY.equals(action)) {
			// 登录与退出广播
			boolean isLogin = intent.getBooleanExtra("isLogin", false);
			
			for(LoginStatusListenner listenner : mListenner) {
				listenner.login(isLogin);
			}
		}
		
	}

}
