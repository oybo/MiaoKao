package com.miaokao.android.app.entity;

/**
 * @TODO 打赏的物品实体类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-2-26 下午3:20:18
 */
public class PlayTour {

	private String id;
	private String name;
	private String icon;
	private String price;
	private String rate;
	private boolean select;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}
