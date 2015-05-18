package com.xpos.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.Landmark;
import com.xpos.common.entity.Position;
import com.xpos.common.entity.Position.PositionType;
import com.xpos.common.entity.example.LandmarkExample;
import com.xpos.common.persistence.mybatis.LandmarkMapper;
import com.xpos.common.persistence.mybatis.PositionMapper;


@Service
public class LandmarkServiceImpl implements LandmarkService{

	private final static Logger logger = LoggerFactory.getLogger(LandmarkServiceImpl.class);
	
	@Resource
	private LandmarkMapper landmarkMapper;
	
	@Resource
	private PositionMapper positionMapper;
	
	
	/*@Override
	@Transactional
	@CacheEvict(value="xpos-maintain", allEntries = true)
	public boolean deleteLandmark(Integer id) {
		Landmark landmark = landmarkMapper.getById(id);
		if(landmark == null){
			return false;
		}
		
		logger.info("删除地标，id=["+id+"]");
		try{
			Position p = landmark.getPosition();
			if(p != null && positionMapper.delete(p.getId()) != 1){
				throw new RuntimeException();
			}
			
			if(landmarkMapper.remove(id) != 1){
				throw new RuntimeException();
			}
			return true;
		}catch(RuntimeException dae){
			logger.error("删除地标失败。", dae);
			throw new RuntimeException();
		}
	}
*/
	@Override
	@Transactional
	public boolean modifyLandmark(Landmark landmark) {
		Position p = landmark.getPosition();
		if(p == null || landmark.getRadius() <= 0 
			|| landmark == null || landmark.getId() == null || landmark.getId() <= 0){
			return false;
		}
		logger.info("修改地标。name=["+landmark.getName()+"], cityCode=["+landmark.getCityCode()+"]");
		try{
			if(landmarkMapper.updateByPrimaryKey(landmark) != 1){
				throw new RuntimeException();
			}
			
			Position _p = landmark.getPosition();
			if(_p != null){
				p.setId(_p.getId());
			}
			p.setForeignId(landmark.getId());
			p.setType(PositionType.LANDMARK);
			if(positionMapper.updateByPrimaryKey(p) != 1){
				throw new RuntimeException();
			}
			return true;
		}catch(RuntimeException dae){
			logger.error("修改地标失败。", dae);
			throw dae;
		}
	}

	@Override
	@Transactional
	public Long saveLandmark(Landmark landmark) {
		Position p = landmark.getPosition();
		if(p == null){
			return -1L;
		}
		try{
			landmarkMapper.insert(landmark);
			p.setType(PositionType.LANDMARK);
			p.setForeignId(landmark.getId());
			positionMapper.insert(p);
			landmark.setPosition(p);
			landmarkMapper.updateByPrimaryKey(landmark);
			return landmark.getId();
		}catch(DataAccessException dae){
			logger.error("创建新地标失败。", dae);
			return -1L;
		}
	}

	@Override
	public Map<String, List<Landmark>> loadLandmarkMap(LandmarkExample example) {
		Map<String, List<Landmark>> landmarkMap = new TreeMap<String, List<Landmark>>();
		List<Landmark> landmarkList = null;
		//从DB查出所有地标
		if (example!=null) {
			example.appendCriterion("deleted=", false);
			landmarkList  = landmarkMapper.selectByExample(example, null);
		}else {
			landmarkList = selectAllLandmark();
		}
		for(Landmark landmark : landmarkList){
			List<Landmark> list = landmarkMap.get(landmark.getCityCode());
			if(list == null){
				list = new ArrayList<>();
				landmarkMap.put(landmark.getCityCode(), list);
			}
			
			list.add(landmark);
		}
		return landmarkMap;
	}

	@Override
	@Cacheable(value="xpos-maintain", key="'allLandmarkMap'")
	public Map<Long, Landmark> loadAllLandmarkMap() {
		
		Map<Long, Landmark> allLandmarkMap = new TreeMap<Long, Landmark>();
		//从DB查出所有地标
		List<Landmark> landmarkList = selectAllLandmark();
		for(Landmark landmark : landmarkList){
			allLandmarkMap.put(landmark.getId(), landmark);
		}
		return allLandmarkMap;
	}
	
   private List<Landmark> selectAllLandmark(){
	   LandmarkExample example = new LandmarkExample();
		example.createCriteria().addCriterion("deleted=", false);
		return landmarkMapper.selectByExample(example, null);
   }

	
	
}
