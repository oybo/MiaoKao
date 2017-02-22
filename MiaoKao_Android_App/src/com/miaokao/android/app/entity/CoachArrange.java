package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-4 下午6:02:14
 */
public class CoachArrange implements Serializable {

	/**   */
	private static final long serialVersionUID = 3978400745070611884L;

	private String r_date;
	private String time_node;
	private String exercise_name;
	private String coach_id;
	private String user_id;
	private String status;
	private String user_name;
	private String head_img;
	private String order_no;

	public String getR_date() {
		return r_date;
	}

	public void setR_date(String r_date) {
		this.r_date = r_date;
	}

	public String getTime_node() {
		return time_node;
	}

	public void setTime_node(String time_node) {
		this.time_node = time_node;
	}

	public String getExercise_name() {
		return exercise_name;
	}

	public void setExercise_name(String exercise_name) {
		this.exercise_name = exercise_name;
	}

	public String getCoach_id() {
		return coach_id;
	}

	public void setCoach_id(String coach_id) {
		this.coach_id = coach_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

}
