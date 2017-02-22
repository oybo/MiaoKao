package com.miaokao.android.app.entity;

/**
 * @TODO 学车页面 Tab 分类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-2-29 下午5:43:05
 */
public class TypeSelecter {

	private String name;
	private String type;
	private String key;
	private String value;
	private boolean select;
	private int tabType;	// tab 类型， 分类=1，排序=2，筛选=3

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public int getTabType() {
		return tabType;
	}

	public void setTabType(int tabType) {
		this.tabType = tabType;
	}

}
