package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO 折扣
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-18 下午3:10:16
 */
public class DSchoolDiscount implements Serializable{

	/**  */
	private static final long serialVersionUID = 1L;
	
	
	private String mer_id;
	private String p_name;
	private String type;
	private String icon;
	private String value;
	private String num;
	private String career;
	private String start_date;
	private String end_date;

	public String getP_name() {
		return p_name;
	}

	public void setP_name(String p_name) {
		this.p_name = p_name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMer_id() {
		return mer_id;
	}

	public void setMer_id(String mer_id) {
		this.mer_id = mer_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getCareer() {
		return career;
	}

	public void setCareer(String career) {
		this.career = career;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

}
