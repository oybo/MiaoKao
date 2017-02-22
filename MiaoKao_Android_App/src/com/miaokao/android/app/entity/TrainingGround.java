package com.miaokao.android.app.entity;

import java.io.Serializable;

/**
 * @TODO 训练场
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-5 上午10:55:48
 */
public class TrainingGround implements Serializable {

	/**   */
	private static final long serialVersionUID = 1L;

	private String sup_no;
	private String sup_name;
	private String sup_addr;
	private boolean select;

	public String getSup_no() {
		return sup_no;
	}

	public void setSup_no(String sup_no) {
		this.sup_no = sup_no;
	}

	public String getSup_name() {
		return sup_name;
	}

	public void setSup_name(String sup_name) {
		this.sup_name = sup_name;
	}

	public String getSup_addr() {
		return sup_addr;
	}

	public void setSup_addr(String sup_addr) {
		this.sup_addr = sup_addr;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

}
