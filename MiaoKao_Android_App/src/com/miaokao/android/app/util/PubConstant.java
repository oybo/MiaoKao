package com.miaokao.android.app.util;

public class PubConstant {

	public static final boolean DEBUG = true;
	
	public static final int REQUEST_CODE_PAYMENT = 100;
	public static final int CUT_LOCATION_REQUEST_CODE = 101;
	public static final int REGISTER_PHONE_REQUEST_CODE = 102;
	public static final int REGISTER_AUTH_CODE_REQUEST_CODE = 103;
	public static final int EDIT_INFO_CODE_REQUEST_CODE = 104;
	public static final int COMMENT_REQUEST_CODE = 105;
	public static final int SELECT_COACH_CODE = 106;
	public static final int USER_INFO_CODE = 107;
	public static final int PAY_SUCCESS_CODE = 108;
	public static final int REFRESH_ORDER_SUCCESS_CODE = 109;
	public static final int SELECT_COACH_NAME_CODE = 110;
	
	public static final String REQUEST_BASE_URL = "https://www.qinghuayu.com/running/service";
	
	/**    下拉刷新时间key     */
	public static final String XLIST_TIME_KEY = "xlist_time_key";
	/**    定位城市key     */
	public static final String LOCAL_CITY = "local_city_key";
	/**    启动页图片下载时间     */
	public static final String LOADING_IMAGE_TIME = "loading_image_time";
	/**    启动页图片下载地址     */
	public static final String LOADING_IMAGE_PATH = "loading_image_path";
	/**       */
	public static final String IS_SAVE_CRASH_LOG = "is_save_crash_log";
	public static final String LOGIN_NAME_KEY = "login_name_key";
	public static final String PASSWORD_KEY = "password_key";
	
	/**    广播     */
	public static final String BROADCAST_BASE = "miaokao_";
	public static final String PUSH_MESSAGE_B_KEY = BROADCAST_BASE + "push_message_b_key";
	public static final String LOGIN_STATE_KEY = BROADCAST_BASE + "login_state_key";
	public static final String LOCAL_SUCCESS_KEY = BROADCAST_BASE + "local_success_key";
	public static final String MAKE_SUCCESS_FINISH_KEY = BROADCAST_BASE + "make_success_finish_key";
	public static final String SUBMIT_ORDER_SUCCESS_FINISH_KEY = BROADCAST_BASE + "order_success_finish_key";
	public static final String LOADING_GUANGGAO_IAMGE_KEY = BROADCAST_BASE + "loading_guanggao_image_key";
	public static final String SHOW_UPDATE_KEY = BROADCAST_BASE + "show_update_key";
	public static final String REFRESH_SCHOO_LLIST = BROADCAST_BASE + "refresh_scholl_list";
	
}
