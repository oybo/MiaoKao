1:需添加权限	
	<!-- 分享相关的权限 -->
    <uses-permission android:name="android.permission.GET_TASKS" />  
    <uses-permission android:name="android.permission.INTERNET" />  
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />  
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />  
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />  
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />  
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />  
    <uses-permission android:name="android.permission.MANAGE_ACCOUNTS"/>  
    <uses-permission android:name="android.permission.GET_ACCOUNTS"/>  
    
2：需添加Activity，以及回调类
		<!-- 分享相关的页面（ShareSDK需要调用到的） -->
        <activity
            android:name="com.mob.tools.MobUIShell"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
        <!-- QQ分享回调  必须加 -->
        <activity
            android:name="cn.sharesdk.tencent.qq.ResultReceiver"
            android:launchMode="singleTask"
            android:noHistory="true" >
            <!--
            	如果集成QQ分享，或者使用QQ客户端来进行QQ空间的分享，须要在此处添加一个对ACTION_VIEW
            	事件的过滤器，其中的scheme是“tencent”前缀再开发者应用的加上appId。如果此过滤器不设置，
            	则分享结束以后不能得到正确的回调
            -->
            <intent-filter android:priority="1000" >
                <data android:scheme="tencent100371282" />

                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
        </activity>
        <activity
            android:name="cn.sharesdk.tencent.qzone.ResultReceiver"
            android:launchMode="singleTask"
            android:noHistory="true" >
            <!--
            	如果集成QQ分享，或者使用QQ客户端来进行QQ空间的分享，须要在此处添加一个对ACTION_VIEW
            	事件的过滤器，其中的scheme是“tencent”前缀再开发者应用的加上appId。如果此过滤器不设置，
            	则分享结束以后不能得到正确的回调
            -->
            <intent-filter android:priority="1000" >
                <data android:scheme="tencent100371282" />

                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
        </activity>

        <!-- 微信分享回调 必须加 -->
        <activity
            android:name="com.coracle.dyrs.app.wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
            
3:在ShareConfig.java类中配置分享平台AppId等
            
4：微信回调必须把WXEntryActivity.java类 拷贝到主工程中， 路径必须是包名+wxapi+.WXEntryActivity
       比如：com.coracle.dyrs.app.wxapi.WXEntryActivity
       
5：具体分享调用方法      详见ShareUtil.java类，有详细注释
            