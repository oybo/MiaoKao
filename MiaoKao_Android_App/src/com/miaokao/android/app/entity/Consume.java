package com.miaokao.android.app.entity;

/**
 * @TODO 我的钱包 账单明细 实体类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-3-10 下午5:40:09
 */
public class Consume {

	private String coach_id;
	private String coach_name;
	private String name;
	private String prize_num;
	private String time;
	private String total_fee;
	private String trans_type;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrize_num() {
		return prize_num;
	}

	public void setPrize_num(String prize_num) {
		this.prize_num = prize_num;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getTrans_type() {
		return trans_type;
	}

	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
	}

}
