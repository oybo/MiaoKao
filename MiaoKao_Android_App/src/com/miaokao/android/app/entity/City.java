package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-26 下午5:28:43 
 */
public class City implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
