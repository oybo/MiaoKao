package com.miaokao.android.app.entity;

import java.io.Serializable;
import java.util.List;

public class Coach implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private String mer_id;
	private String account;
	private String type;
	private String head_img;
	private String name;
	private String mobile;
	private String sex;
	private String rate;
	private String time;
	private String intro;
	private String cer_img;

	// --------详情页面要用到的-----------
	private String cRate;
	private String zRate;
	private String hRate;
	private List<MerComment> comments;

	// ---------常用联系人要用-----------------
	private boolean useCoach;
	
	//-----------教练列表要用到的------------/
	private String comment_num;
	private String age;
	private String member_num;
	private String one_hour_price;
	private String cource_price;
	private String car_no;
	private String coach_video;
	private String l_comment;
	private String m_comment;
	private String h_comment;
	private String is_for_quick;
	private String is_for_hour;
	private String is_for_fenqi;
	private String is_for_return;
	private String is_for_shuttle;
	private String mer_account;
	private String mer_name;
	private String province;
	private String city;
	private String zone;
	private String latitude;
	private String longitude;
	private String mer_price;
	private List<DSchoolDiscount> dSchoolDiscounts;
	
	public String getMer_id() {
		return mer_id;
	}

	public void setMer_id(String mer_id) {
		this.mer_id = mer_id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getCer_img() {
		return cer_img;
	}

	public void setCer_img(String cer_img) {
		this.cer_img = cer_img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getcRate() {
		return cRate;
	}

	public void setcRate(String cRate) {
		this.cRate = cRate;
	}

	public String getzRate() {
		return zRate;
	}

	public void setzRate(String zRate) {
		this.zRate = zRate;
	}

	public String gethRate() {
		return hRate;
	}

	public void sethRate(String hRate) {
		this.hRate = hRate;
	}

	public List<MerComment> getComments() {
		return comments;
	}

	public void setComments(List<MerComment> comments) {
		this.comments = comments;
	}

	public boolean isUseCoach() {
		return useCoach;
	}

	public void setUseCoach(boolean useCoach) {
		this.useCoach = useCoach;
	}

	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getMember_num() {
		return member_num;
	}

	public void setMember_num(String member_num) {
		this.member_num = member_num;
	}

	public String getOne_hour_price() {
		return one_hour_price;
	}

	public void setOne_hour_price(String one_hour_price) {
		this.one_hour_price = one_hour_price;
	}

	public String getCource_price() {
		return cource_price;
	}

	public void setCource_price(String cource_price) {
		this.cource_price = cource_price;
	}

	public String getCar_no() {
		return car_no;
	}

	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}

	public String getCoach_video() {
		return coach_video;
	}

	public void setCoach_video(String coach_video) {
		this.coach_video = coach_video;
	}

	public String getL_comment() {
		return l_comment;
	}

	public void setL_comment(String l_comment) {
		this.l_comment = l_comment;
	}

	public String getM_comment() {
		return m_comment;
	}

	public void setM_comment(String m_comment) {
		this.m_comment = m_comment;
	}

	public String getH_comment() {
		return h_comment;
	}

	public void setH_comment(String h_comment) {
		this.h_comment = h_comment;
	}

	public String getIs_for_quick() {
		return is_for_quick;
	}

	public void setIs_for_quick(String is_for_quick) {
		this.is_for_quick = is_for_quick;
	}

	public String getIs_for_hour() {
		return is_for_hour;
	}

	public void setIs_for_hour(String is_for_hour) {
		this.is_for_hour = is_for_hour;
	}

	public String getIs_for_fenqi() {
		return is_for_fenqi;
	}

	public void setIs_for_fenqi(String is_for_fenqi) {
		this.is_for_fenqi = is_for_fenqi;
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

	public String getMer_account() {
		return mer_account;
	}

	public void setMer_account(String mer_account) {
		this.mer_account = mer_account;
	}

	public String getMer_name() {
		return mer_name;
	}

	public void setMer_name(String mer_name) {
		this.mer_name = mer_name;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMer_price() {
		return mer_price;
	}

	public void setMer_price(String mer_price) {
		this.mer_price = mer_price;
	}

	public List<DSchoolDiscount> getdSchoolDiscounts() {
		return dSchoolDiscounts;
	}

	public void setdSchoolDiscounts(List<DSchoolDiscount> dSchoolDiscounts) {
		this.dSchoolDiscounts = dSchoolDiscounts;
	}

}
