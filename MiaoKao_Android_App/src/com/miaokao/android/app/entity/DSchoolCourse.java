package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * 报名课程
 * 
 * @author ouyangbo
 * 
 */
public class DSchoolCourse implements Serializable {

	/**   */
	private static final long serialVersionUID = 1L;

	private String id;
	private String p_name;
	private String p_intro;
	private String reserved_times;
	private String status;
	private String p_price;
	private String discount_price;
	private String p_mer_id;
	private String p_add_time;
	private String time_node;
	private String stu_price;
	private String stu_discount_price;
	private String p_type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getP_name() {
		return p_name;
	}

	public void setP_name(String p_name) {
		this.p_name = p_name;
	}

	public String getP_intro() {
		return p_intro;
	}

	public void setP_intro(String p_intro) {
		this.p_intro = p_intro;
	}

	public String getReserved_times() {
		return reserved_times;
	}

	public void setReserved_times(String reserved_times) {
		this.reserved_times = reserved_times;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getP_price() {
		return p_price;
	}

	public void setP_price(String p_price) {
		this.p_price = p_price;
	}

	public String getDiscount_price() {
		return discount_price;
	}

	public void setDiscount_price(String discount_price) {
		this.discount_price = discount_price;
	}

	public String getP_mer_id() {
		return p_mer_id;
	}

	public void setP_mer_id(String p_mer_id) {
		this.p_mer_id = p_mer_id;
	}

	public String getP_add_time() {
		return p_add_time;
	}

	public void setP_add_time(String p_add_time) {
		this.p_add_time = p_add_time;
	}

	public String getTime_node() {
		return time_node;
	}

	public void setTime_node(String time_node) {
		this.time_node = time_node;
	}

	public String getStu_price() {
		return stu_price;
	}

	public void setStu_price(String stu_price) {
		this.stu_price = stu_price;
	}

	public String getStu_discount_price() {
		return stu_discount_price;
	}

	public void setStu_discount_price(String stu_discount_price) {
		this.stu_discount_price = stu_discount_price;
	}

	public String getP_type() {
		return p_type;
	}

	public void setP_type(String p_type) {
		this.p_type = p_type;
	}

}
