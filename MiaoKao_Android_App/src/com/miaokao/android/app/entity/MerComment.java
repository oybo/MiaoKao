package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO 驾校评论
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-19 下午4:24:16
 */
public class MerComment implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	
	
	private String num;
	private String mer_id;
	private String user_id;
	private String rate;
	private String content;
	private String time;

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMer_id() {
		return mer_id;
	}

	public void setMer_id(String mer_id) {
		this.mer_id = mer_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
