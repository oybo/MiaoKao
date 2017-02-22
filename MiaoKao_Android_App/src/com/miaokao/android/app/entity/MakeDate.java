package com.miaokao.android.app.entity;

/**
 * @TODO 预约时间日期
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-4 下午4:40:18
 */
public class MakeDate {

	private String weekValue;
	private String date;
	private String dateYear;

	public MakeDate(String weekValue, String date, String dateYear) {
		this.weekValue = weekValue;
		this.date = date;
		this.dateYear = dateYear;
	}

	public String getWeekValue() {
		return weekValue;
	}

	public void setWeekValue(String weekValue) {
		this.weekValue = weekValue;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDateYear() {
		return dateYear;
	}

	public void setDateYear(String dateYear) {
		this.dateYear = dateYear;
	}

}
