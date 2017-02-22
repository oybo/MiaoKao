package com.miaokao.android.app.util;

import java.util.Comparator;

import com.miaokao.android.app.entity.PlayTour;

/**
 * @TODO 打赏物品排序
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-2-26 下午3:08:25 
 */
public class PlayTourComparator implements Comparator<PlayTour> {

	@Override
	public int compare(PlayTour lhs, PlayTour rhs) {
		
		
		return lhs.getRate().compareTo(rhs.getRate());
	}

}
