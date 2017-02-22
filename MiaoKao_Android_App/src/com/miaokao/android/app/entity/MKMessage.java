package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-26 下午12:00:07
 */
public class MKMessage implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private String mer_account;
	private String mer_name;
	private String id;
	private String from;
	private String to;
	private String content;
	private String time;
	private String status;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
