package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.Landmark;
import com.xpos.common.entity.example.LandmarkExample;


public interface LandmarkService {

	/*boolean deleteLandmark(Integer id);*/

	boolean modifyLandmark(Landmark landmark);

	Long saveLandmark(Landmark landmark);

	/** 加载、过滤无商户关联的地标 */
	/*Map<Integer, List<Landmark>> loadAvailableLandmark();

	/** 清空缓存 */
	/*void refreshAvailableLandmark();*/
	
	/** 以市级代码加载所有地标。
	 * key--市级代码city_code, value--Landmark对象列表
	 */
	Map<String, List<Landmark>> loadLandmarkMap(LandmarkExample example);
	
	/** 简单加载所有地标。
	 * key-Landmark对象的id, value--对应的Landmark对象
	 */
	Map<Long, Landmark> loadAllLandmarkMap();
	
	/** 把所有地标拼装成JSON格式 */
	/*Map<Integer, String> generateLandmarksJson();
	/**
	 * 清空cacheAllLandmarkInnerShop缓存
	 */
	/*void refreshLandmarkInnerShop();*/
}
