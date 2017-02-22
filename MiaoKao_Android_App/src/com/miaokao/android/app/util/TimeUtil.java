package com.miaokao.android.app.util;

import android.annotation.SuppressLint;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.miaokao.android.app.entity.MakeDate;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM月dd日";

	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";

	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;// 年
	private static final int MONTH = 30 * 24 * 60 * 60;// 月
	private static final int DAY = 24 * 60 * 60;// 天
	private static final int HOUR = 60 * 60;// 小时
	private static final int MINUTE = 60;// 分钟

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * 
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		System.out.println("timeGap: " + timeGap);
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "年前";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "个月前";
		} else if (timeGap > DAY) {// 1天以上
			timeStr = timeGap / DAY + "天前";
		} else if (timeGap > HOUR) {// 1小时-24小时
			timeStr = timeGap / HOUR + "小时前";
		} else if (timeGap > MINUTE) {// 1分钟-59分钟
			timeStr = timeGap / MINUTE + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	/**
	 * 获取当前日期的指定格式的字符串
	 * 
	 * @param format
	 *            指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
	 * @return
	 */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	// date类型转换为String类型
	// formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	// data Date类型的时间
	public static String dateToString(Date data, String formatType) {
		return new SimpleDateFormat(formatType).format(data);
	}

	// long类型转换为String类型
	// currentTime要转换的long类型的时间
	// formatType要转换的string类型的时间格式
	public static String longToString(long currentTime, String formatType) {
		String strTime = "";
		Date date = longToDate(currentTime, formatType);// long类型转成Date类型
		strTime = dateToString(date, formatType); // date类型转成String
		return strTime;
	}

	// string类型转换为date类型
	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
	// HH时mm分ss秒，
	// strTime的时间格式必须要与formatType的时间格式相同
	public static Date stringToDate(String strTime, String formatType) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		try {
			date = formatter.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	// long转换为Date类型
	// currentTime要转换的long类型的时间
	// formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	public static Date longToDate(long currentTime, String formatType) {
		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
		String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
		Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
		return date;
	}

	// string类型转换为long类型
	// strTime要转换的String类型的时间
	// formatType时间格式
	// strTime的时间格式和formatType的时间格式必须相同
	public static long stringToLong(String strTime, String formatType) {
		Date date = stringToDate(strTime, formatType); // String类型转成date类型
		if (date == null) {
			return 0;
		} else {
			long currentTime = dateToLong(date); // date类型转成long类型
			return currentTime;
		}
	}

	// date类型转换为long类型
	// date要转换的date类型的时间
	public static long dateToLong(Date date) {
		return date.getTime();
	}

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	/**
	 * 获取聊天时间：
	 * 
	 * @Title: getChatTime
	 * @Description: TODO
	 * @param @param timesamp
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			result = getTime(timesamp);
			break;
		}

		return result;
	}
	
	/**
	 * 计算今天开始.计算包括今天的一周日期
	 */
	@SuppressLint("NewApi")
	public static List<MakeDate> getWeekDate() {
		List<MakeDate> makeDates = new ArrayList<>();
		
		String[] weekDays = DateFormatSymbols.getInstance(Locale.CHINA).getWeekdays();
		String pattern = "MM月dd日";
		SimpleDateFormat sdf_year = new SimpleDateFormat(FORMAT_DATE);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		// 今天日期
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String today = sdf.format(date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today, sdf_year.format(date)));
//		System.out.println("--" + today + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		// 计算往后6天
		c.add(Calendar.DAY_OF_YEAR, 1);
		// 后1
		Date today_plus1_date = c.getTime();
		String today_plus1 = sdf.format(today_plus1_date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today_plus1, sdf_year.format(today_plus1_date)));
//		System.out.println("--" + today_plus1 + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		// 后2
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date today_plus2_date = c.getTime();
		String today_plus2 = sdf.format(today_plus2_date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today_plus2, sdf_year.format(today_plus2_date)));
//		System.out.println("--" + today_plus2 + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		// 后3
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date today_plus3_date = c.getTime();
		String today_plus3 = sdf.format(today_plus3_date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today_plus3, sdf_year.format(today_plus3_date)));
//		System.out.println("--" + today_plus3 + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		// 后4
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date today_plus4_date = c.getTime();
		String today_plus4 = sdf.format(today_plus4_date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today_plus4, sdf_year.format(today_plus4_date)));
//		System.out.println("--" + today_plus4 + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		// 后5
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date today_plus5_date = c.getTime();
		String today_plus5 = sdf.format(today_plus5_date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today_plus5, sdf_year.format(today_plus5_date)));
//		System.out.println("--" + today_plus5 + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		// 后6
		c.add(Calendar.DAY_OF_YEAR, 1);
		Date today_plus6_date = c.getTime();
		String today_plus6 = sdf.format(today_plus6_date);
		makeDates.add(new MakeDate(weekDays[c.get(Calendar.DAY_OF_WEEK)], today_plus6, sdf_year.format(today_plus6_date)));
//		System.out.println("--" + today_plus6 + "-" + weekDays[c.get(Calendar.DAY_OF_WEEK)]);
		
		return makeDates;
	}
	
}