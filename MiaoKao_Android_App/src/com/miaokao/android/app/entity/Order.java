package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-28 下午10:38:00
 */
public class Order implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;

	private String order_no;
	private String order_type;
	private String mer_id;
	private String mer_name;
	private String mer_head_img;
	private String user_id;
	private String user_name;
	private String user_mobile;
	private String user_career;
	private String product_id;
	private String product_name;
	private String first_pay_no;
	private String first_pay_num;
	private String first_pay_paid;
	private String second_pay_no;
	private String second_pay_num;
	private String second_pay_paid;
	private String third_pay_no;
	private String third_pay_num;
	private String third_pay_paid;
	private String fourth_pay_no;
	private String fourth_pay_num;
	private String fourth_pay_paid;
	private String fivth_pay_no;
	private String fivth_pay_num;
	private String fivth_pay_paid;
	private String should_pay_money;
	private String paid_money;
	private String total_price;
	private String pay_status;
	private String pay_channel;
	private String status;
	private String add_time;

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getMer_id() {
		return mer_id;
	}

	public void setMer_id(String mer_id) {
		this.mer_id = mer_id;
	}

	public String getMer_name() {
		return mer_name;
	}

	public void setMer_name(String mer_name) {
		this.mer_name = mer_name;
	}

	public String getMer_head_img() {
		return mer_head_img;
	}

	public void setMer_head_img(String mer_head_img) {
		this.mer_head_img = mer_head_img;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_mobile() {
		return user_mobile;
	}

	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}

	public String getUser_career() {
		return user_career;
	}

	public void setUser_career(String user_career) {
		this.user_career = user_career;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getFirst_pay_no() {
		return first_pay_no;
	}

	public void setFirst_pay_no(String first_pay_no) {
		this.first_pay_no = first_pay_no;
	}

	public String getFirst_pay_num() {
		return first_pay_num;
	}

	public void setFirst_pay_num(String first_pay_num) {
		this.first_pay_num = first_pay_num;
	}

	public String getFirst_pay_paid() {
		return first_pay_paid;
	}

	public void setFirst_pay_paid(String first_pay_paid) {
		this.first_pay_paid = first_pay_paid;
	}

	public String getSecond_pay_no() {
		return second_pay_no;
	}

	public void setSecond_pay_no(String second_pay_no) {
		this.second_pay_no = second_pay_no;
	}

	public String getSecond_pay_num() {
		return second_pay_num;
	}

	public void setSecond_pay_num(String second_pay_num) {
		this.second_pay_num = second_pay_num;
	}

	public String getSecond_pay_paid() {
		return second_pay_paid;
	}

	public void setSecond_pay_paid(String second_pay_paid) {
		this.second_pay_paid = second_pay_paid;
	}

	public String getThird_pay_no() {
		return third_pay_no;
	}

	public void setThird_pay_no(String third_pay_no) {
		this.third_pay_no = third_pay_no;
	}

	public String getThird_pay_num() {
		return third_pay_num;
	}

	public void setThird_pay_num(String third_pay_num) {
		this.third_pay_num = third_pay_num;
	}

	public String getThird_pay_paid() {
		return third_pay_paid;
	}

	public void setThird_pay_paid(String third_pay_paid) {
		this.third_pay_paid = third_pay_paid;
	}

	public String getFourth_pay_no() {
		return fourth_pay_no;
	}

	public void setFourth_pay_no(String fourth_pay_no) {
		this.fourth_pay_no = fourth_pay_no;
	}

	public String getFourth_pay_num() {
		return fourth_pay_num;
	}

	public void setFourth_pay_num(String fourth_pay_num) {
		this.fourth_pay_num = fourth_pay_num;
	}

	public String getFourth_pay_paid() {
		return fourth_pay_paid;
	}

	public void setFourth_pay_paid(String fourth_pay_paid) {
		this.fourth_pay_paid = fourth_pay_paid;
	}

	public String getFivth_pay_no() {
		return fivth_pay_no;
	}

	public void setFivth_pay_no(String fivth_pay_no) {
		this.fivth_pay_no = fivth_pay_no;
	}

	public String getFivth_pay_num() {
		return fivth_pay_num;
	}

	public void setFivth_pay_num(String fivth_pay_num) {
		this.fivth_pay_num = fivth_pay_num;
	}

	public String getFivth_pay_paid() {
		return fivth_pay_paid;
	}

	public void setFivth_pay_paid(String fivth_pay_paid) {
		this.fivth_pay_paid = fivth_pay_paid;
	}

	public String getShould_pay_money() {
		return should_pay_money;
	}

	public void setShould_pay_money(String should_pay_money) {
		this.should_pay_money = should_pay_money;
	}

	public String getPaid_money() {
		return paid_money;
	}

	public void setPaid_money(String paid_money) {
		this.paid_money = paid_money;
	}

	public String getTotal_price() {
		return total_price;
	}

	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}

	public String getPay_status() {
		return pay_status;
	}

	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}

	public String getPay_channel() {
		return pay_channel;
	}

	public void setPay_channel(String pay_channel) {
		this.pay_channel = pay_channel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

}
