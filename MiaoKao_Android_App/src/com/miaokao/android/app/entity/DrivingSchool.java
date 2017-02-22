package com.miaokao.android.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 驾校
 * 
 * @author ouyangbo
 * 
 */
public class DrivingSchool implements Serializable {

	/** 	 */
	private static final long serialVersionUID = 1L;

	public DrivingSchool() {
	}

	private String id;
	private String mer_account;
	private String mer_head_img;
	private String mer_phone;
	private String mer_name;
	private String mer_addr;
	private String mer_provice;
	private String mer_city;
	private String mer_zone;
	private String mer_router;
	private String mer_licence_pic;
	private String mer_rate;
	private String is_for_bankcard;
	private String is_for_drinking;
	private String is_for_fenqi;
	private String is_for_food;
	private String is_for_invoice;
	private String is_for_parking;
	private String is_for_return;
	private String is_for_shuttle;
	private String is_for_student;
	private String is_for_wifi;
	private String mer_member_num;
	private String mer_comment_num;
	private String mer_lastest_comment;
	private String mer_cheapest_price;
	private String mer_lastest_rate;
	private String mer_last_comment_time;
	private String mer_finish_member;
	private String mer_add_time;
	private String mer_latitude;
	private String mer_longitude;
	private String lowerst_price;
	private String mer_intro;
	private List<DSchoolDiscount> dSchoolDiscounts;

	// ------------------详细页需要添加的数据-----------------------------------
	private String hd_service; // 驾校设施
	private String ss_service; // 服务
	private String router; // 公交路线
	private List<DSchoolCourse> dSchoolCourses; // 课程报名
	private List<MerComment> merComments; // 评论
	private String d_school_info; // 驾校简介

	// ------------------确认订单里需要的数据-----------------------------------
	private String first_pay_rate;
	private String second_pay_rate;
	private String third_pay_rate;
	private String fourth_pay_rate;
	private String fivth_pay_rate;
	
	public String getHd_service() {
		return hd_service;
	}

	public void setHd_service(String hd_service) {
		this.hd_service = hd_service;
	}

	public String getSs_service() {
		return ss_service;
	}

	public void setSs_service(String ss_service) {
		this.ss_service = ss_service;
	}

	public String getRouter() {
		return router;
	}

	public void setRouter(String router) {
		this.router = router;
	}

	public List<DSchoolCourse> getdSchoolCourses() {
		return dSchoolCourses;
	}

	public void setdSchoolCourses(List<DSchoolCourse> dSchoolCourses) {
		this.dSchoolCourses = dSchoolCourses;
	}

	public List<MerComment> getMerComments() {
		return merComments;
	}

	public void setMerComments(List<MerComment> merComments) {
		this.merComments = merComments;
	}

	public String getD_school_info() {
		return d_school_info;
	}

	public void setD_school_info(String d_school_info) {
		this.d_school_info = d_school_info;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMer_account() {
		return mer_account;
	}

	public void setMer_account(String mer_account) {
		this.mer_account = mer_account;
	}

	public String getMer_head_img() {
		return mer_head_img;
	}

	public void setMer_head_img(String mer_head_img) {
		this.mer_head_img = mer_head_img;
	}

	public String getMer_intro() {
		return mer_intro;
	}

	public void setMer_intro(String mer_intro) {
		this.mer_intro = mer_intro;
	}
	
	public String getMer_phone() {
		return mer_phone;
	}

	public void setMer_phone(String mer_phone) {
		this.mer_phone = mer_phone;
	}

	public String getMer_name() {
		return mer_name;
	}

	public void setMer_name(String mer_name) {
		this.mer_name = mer_name;
	}

	public String getMer_addr() {
		return mer_addr;
	}

	public void setMer_addr(String mer_addr) {
		this.mer_addr = mer_addr;
	}

	public String getMer_provice() {
		return mer_provice;
	}

	public void setMer_provice(String mer_provice) {
		this.mer_provice = mer_provice;
	}

	public String getMer_city() {
		return mer_city;
	}

	public void setMer_city(String mer_city) {
		this.mer_city = mer_city;
	}

	public String getMer_zone() {
		return mer_zone;
	}

	public void setMer_zone(String mer_zone) {
		this.mer_zone = mer_zone;
	}

	public String getMer_router() {
		return mer_router;
	}

	public void setMer_router(String mer_router) {
		this.mer_router = mer_router;
	}

	public String getMer_licence_pic() {
		return mer_licence_pic;
	}

	public void setMer_licence_pic(String mer_licence_pic) {
		this.mer_licence_pic = mer_licence_pic;
	}

	public String getMer_rate() {
		return mer_rate;
	}

	public void setMer_rate(String mer_rate) {
		this.mer_rate = mer_rate;
	}

	public String getIs_for_bankcard() {
		return is_for_bankcard;
	}

	public void setIs_for_bankcard(String is_for_bankcard) {
		this.is_for_bankcard = is_for_bankcard;
	}

	public String getIs_for_drinking() {
		return is_for_drinking;
	}

	public void setIs_for_drinking(String is_for_drinking) {
		this.is_for_drinking = is_for_drinking;
	}

	public String getIs_for_fenqi() {
		return is_for_fenqi;
	}

	public void setIs_for_fenqi(String is_for_fenqi) {
		this.is_for_fenqi = is_for_fenqi;
	}

	public String getIs_for_food() {
		return is_for_food;
	}

	public void setIs_for_food(String is_for_food) {
		this.is_for_food = is_for_food;
	}

	public String getIs_for_invoice() {
		return is_for_invoice;
	}

	public void setIs_for_invoice(String is_for_invoice) {
		this.is_for_invoice = is_for_invoice;
	}

	public String getIs_for_parking() {
		return is_for_parking;
	}

	public void setIs_for_parking(String is_for_parking) {
		this.is_for_parking = is_for_parking;
	}

	public String getIs_for_return() {
		return is_for_return;
	}

	public void setIs_for_return(String is_for_return) {
		this.is_for_return = is_for_return;
	}

	public String getIs_for_shuttle() {
		return is_for_shuttle;
	}

	public void setIs_for_shuttle(String is_for_shuttle) {
		this.is_for_shuttle = is_for_shuttle;
	}

	public String getIs_for_student() {
		return is_for_student;
	}

	public void setIs_for_student(String is_for_student) {
		this.is_for_student = is_for_student;
	}

	public String getIs_for_wifi() {
		return is_for_wifi;
	}

	public void setIs_for_wifi(String is_for_wifi) {
		this.is_for_wifi = is_for_wifi;
	}

	public String getMer_member_num() {
		return mer_member_num;
	}

	public void setMer_member_num(String mer_member_num) {
		this.mer_member_num = mer_member_num;
	}

	public String getMer_comment_num() {
		return mer_comment_num;
	}

	public void setMer_comment_num(String mer_comment_num) {
		this.mer_comment_num = mer_comment_num;
	}

	public String getMer_lastest_comment() {
		return mer_lastest_comment;
	}

	public void setMer_lastest_comment(String mer_lastest_comment) {
		this.mer_lastest_comment = mer_lastest_comment;
	}

	public String getMer_cheapest_price() {
		return mer_cheapest_price;
	}

	public void setMer_cheapest_price(String mer_cheapest_price) {
		this.mer_cheapest_price = mer_cheapest_price;
	}

	public String getMer_lastest_rate() {
		return mer_lastest_rate;
	}

	public void setMer_lastest_rate(String mer_lastest_rate) {
		this.mer_lastest_rate = mer_lastest_rate;
	}

	public String getMer_last_comment_time() {
		return mer_last_comment_time;
	}

	public void setMer_last_comment_time(String mer_last_comment_time) {
		this.mer_last_comment_time = mer_last_comment_time;
	}

	public String getMer_finish_member() {
		return mer_finish_member;
	}

	public void setMer_finish_member(String mer_finish_member) {
		this.mer_finish_member = mer_finish_member;
	}

	public String getMer_add_time() {
		return mer_add_time;
	}

	public void setMer_add_time(String mer_add_time) {
		this.mer_add_time = mer_add_time;
	}

	public String getMer_latitude() {
		return mer_latitude;
	}

	public void setMer_latitude(String mer_latitude) {
		this.mer_latitude = mer_latitude;
	}

	public String getMer_longitude() {
		return mer_longitude;
	}

	public void setMer_longitude(String mer_longitude) {
		this.mer_longitude = mer_longitude;
	}

	public String getLowerst_price() {
		return lowerst_price;
	}

	public void setLowerst_price(String lowerst_price) {
		this.lowerst_price = lowerst_price;
	}

	public List<DSchoolDiscount> getdSchoolDiscounts() {
		return dSchoolDiscounts;
	}

	public void setdSchoolDiscounts(List<DSchoolDiscount> dSchoolDiscounts) {
		this.dSchoolDiscounts = dSchoolDiscounts;
	}

	public String getFirst_pay_rate() {
		return first_pay_rate;
	}

	public void setFirst_pay_rate(String first_pay_rate) {
		this.first_pay_rate = first_pay_rate;
	}

	public String getSecond_pay_rate() {
		return second_pay_rate;
	}

	public void setSecond_pay_rate(String second_pay_rate) {
		this.second_pay_rate = second_pay_rate;
	}

	public String getThird_pay_rate() {
		return third_pay_rate;
	}

	public void setThird_pay_rate(String third_pay_rate) {
		this.third_pay_rate = third_pay_rate;
	}

	public String getFourth_pay_rate() {
		return fourth_pay_rate;
	}

	public void setFourth_pay_rate(String fourth_pay_rate) {
		this.fourth_pay_rate = fourth_pay_rate;
	}

	public String getFivth_pay_rate() {
		return fivth_pay_rate;
	}

	public void setFivth_pay_rate(String fivth_pay_rate) {
		this.fivth_pay_rate = fivth_pay_rate;
	}

}
