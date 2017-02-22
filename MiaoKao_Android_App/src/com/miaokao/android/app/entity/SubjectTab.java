package com.miaokao.android.app.entity;

/**
 * @TODO 科目导航实体类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-2-24 下午4:28:59
 */
public class SubjectTab {

	private String icon;
	private String title;
	private String url;
	private String rate;

	public SubjectTab(String icon, String title, String url, String rate) {
		this.icon = icon;
		this.title = title;
		this.url = url;
		this.rate = rate;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}
