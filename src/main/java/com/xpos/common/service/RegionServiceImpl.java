package com.xpos.common.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xpos.common.entity.Region;
import com.xpos.common.entity.example.RegionExample;
import com.xpos.common.persistence.mybatis.RegionMapper;

@Service
public class RegionServiceImpl implements RegionService {
	
	@Resource
	private RegionMapper regionMapper;

	@Override
	public List<Region> findRegionList(RegionExample regionExample) {
		return regionMapper.selectByExample(regionExample, null);
	}

	@Override
	public Region findRegion(RegionExample regionExample) {
		return regionMapper.selectOneByExample(regionExample);
	}

}
