package com.xpos.common.utils;

import javax.annotation.Resource;

import com.xpos.common.persistence.mybatis.LandmarkMapper;
import com.xpos.common.persistence.mybatis.PositionMapper;

public class MapDistance {
	private final static double PI = 3.14159265358979323;// 圆周率
	private final static double R = 6370996.81;  // 地球的半径
	
	@Resource
	private PositionMapper positionMapper;
	
	@Resource
	private LandmarkMapper landmarkMapper;
	
	
	private MapDistance() {
	}

	/**
	 * 纬度lat 经度lon
	 * @param longt1
	 * @param lat1
	 * @param longt2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double longt1, double lat1, double longt2, double lat2) {
		double x, y, distance;
		x = (longt2 - longt1) * PI * R * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		y = (lat2 - lat1) * PI * R / 180;
		distance = Math.hypot(x, y);
		return distance;
	}
    
	/**
	 * 计算两个经纬度之间的距离是否小于或等于R
	 * @param longt1
	 * @param lat1
	 * @param longt2
	 * @param lat2
	 * @param radius
	 * @return true/false
	 */
	public static boolean isRadiusInner(double longt1, double lat1, double longt2, double lat2, double radius) {
		
		return getDistance(longt1, lat1, longt2, lat2)<=radius ? true : false;
	}
	public static void main(String[] args) {
		System.out.println(  PI * R  );
		System.out.println(getDistance(120.18225d ,30.26412d ,120.208947d,30.265771d));
	}
}
