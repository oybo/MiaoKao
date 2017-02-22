package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO 预约实体类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-5 下午2:25:32
 */
public class Make implements Serializable{

	/**  */
	private static final long serialVersionUID = 1L;
	
	
	private String coach_id;
	private String coach_name;
	private String exercise_name;
	private String r_date;
	private String time_node;

	public String getCoach_id() {
		return coach_id;
	}

	public void setCoach_id(String coach_id) {
		this.coach_id = coach_id;
	}

	public String getCoach_name() {
		return coach_name;
	}

	public void setCoach_name(String coach_name) {
		this.coach_name = coach_name;
	}

	public String getExercise_name() {
		return exercise_name;
	}

	public void setExercise_name(String exercise_name) {
		this.exercise_name = exercise_name;
	}

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

}
