package com.miaokao.android.app.service;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.activity.PushMsgActivity;
import com.miaokao.android.app.util.PubConstant;
import com.umeng.common.message.Log;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

/**
 * Developer defined push intent service. 
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}. 
 * @author lucas
 *
 */
//完全自定义处理类
//参考文档的1.6.5
//http://dev.umeng.com/push/android/integration#1_6_5
public class MyPushIntentService extends UmengBaseIntentService{
	
	private static final String TAG = MyPushIntentService.class.getName();

	private NotificationManager mNotificationManager;
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		// 需要调用父类的函数，否则无法统计到消息送达
		super.onMessage(context, intent);
		try {
			//可以通过MESSAGE_BODY取得消息体
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			
			Intent msgIntent = new Intent(PubConstant.PUSH_MESSAGE_B_KEY);
			msgIntent.putExtra("push_result", msg.custom);
			sendBroadcast(msgIntent);
			
			Log.e(TAG, "message="+message);    //消息体
			Log.e(TAG, "custom="+msg.custom);    //自定义消息的内容
			Log.e(TAG, "title="+msg.title);    //通知标题
			Log.e(TAG, "text="+msg.text);    //通知内容
			// code  to handle message here
			// ...
			
			try {
				JSONObject jsonObject = new JSONObject(msg.custom);
				String type = jsonObject.optString("type");
				String content = jsonObject.optString("content");
				if("message".equals(type)) {
					// 提示消息类型
					showNotific(content);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showNotific(msg.custom);
			}
			
			
//			// 对完全自定义消息的处理方式，点击或者忽略
//			boolean isClickOrDismissed = true;
//			if(isClickOrDismissed) {
//				//完全自定义消息的点击统计
//				UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//			} else {
//				//完全自定义消息的忽略统计
//				UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
//			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void showNotific(String message) {
		if(mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		// 因为这类通知只需要一个就够了，所以固定ID
		int id = 0;
		Intent intent = new Intent(this, PushMsgActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, 0);
		
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//设置提示音
	        
		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("消息")
				.setContentText(message)
				.setWhen(System.currentTimeMillis())
				//.setOngoing(true)
				.setTicker("喵考")
				.setSound(uri) //设置提示音
				.setAutoCancel(true)
				.setContentIntent(pendingIntent);
		
		mNotificationManager.notify(id, mBuilder.build());
	}
	
}
