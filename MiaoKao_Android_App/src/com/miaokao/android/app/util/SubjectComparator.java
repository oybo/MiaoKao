package com.miaokao.android.app.util;

import java.util.Comparator;

import com.miaokao.android.app.entity.SubjectTab;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-2-26 下午3:08:25 
 */
public class SubjectComparator implements Comparator<SubjectTab> {

	@Override
	public int compare(SubjectTab lhs, SubjectTab rhs) {
		
		
		return lhs.getRate().compareTo(rhs.getRate());
	}

}
