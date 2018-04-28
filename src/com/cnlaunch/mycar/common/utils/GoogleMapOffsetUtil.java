package com.cnlaunch.mycar.common.utils;

import android.graphics.Point;

public class GoogleMapOffsetUtil {
	//地图偏移值，缩放系数。以19级为基准
	private static final double[] RATIOS = new double[] { 0, 280000,
			140000, 71000, 36000, 17000, 9000, 4000, 2000, 568, 549, 278, 139,
			69, 35, 17, 8, 4, 2.15, 1.07, 0.54, 0.27 };

	public static double getRatioX(int i) {
		if(i<1 || i>RATIOS.length){
			return 0;
		}
		return 1.07/RATIOS[i];
	}

	public static double getRatioY(int i) {
		if(i<1 || i>RATIOS.length){
			return 0;
		}

		return 1.07/RATIOS[i];
	}

	public static void pointOffset(Point p, long preOffsetX, long preOffsetY,
			double ratioX, double ratioY) {
		p.x += preOffsetX * ratioX;
		p.y += preOffsetY * ratioY;
	}

}
