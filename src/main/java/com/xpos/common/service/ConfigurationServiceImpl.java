package com.xpos.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.example.ConfigurationExample;
import com.xpos.common.persistence.mybatis.ConfigurationMapper;

@Service
public class ConfigurationServiceImpl implements ConfigurationService{
	private boolean isInitialized = false;
	private final Map<String, Configuration> GLOBAL_CONFIGURATION = new HashMap<>();

	@Resource
	private ConfigurationMapper confMapper;

	@Override
	public boolean save(Configuration configuration) {
		initialize();
		
		//insert to DB
		if(confMapper.insert(configuration) == 1){
			//save to local Map
			GLOBAL_CONFIGURATION.put(configuration.getName(), configuration);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean update(Configuration configuration) {
		initialize();
		
		//update to DB
		if(confMapper.updateByPrimaryKey(configuration) == 1){
			//save to local Map
			GLOBAL_CONFIGURATION.put(configuration.getName(), configuration);
			return true;
		}
		
		return false;
	}

	@Override
	public Configuration findById(Long id) {
		initialize();
		//fetch from DB
		ConfigurationExample example = new ConfigurationExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		Configuration conf = confMapper.selectOneByExample(example);
		if(conf != null){
			// save/replace local Map
			GLOBAL_CONFIGURATION.put(conf.getName(), conf);
		}
		return conf;
	}

	@Override
	public Configuration findByName(String name) {
		initialize();
		//try local Map first
		Configuration conf = GLOBAL_CONFIGURATION.get(name);
		if(conf == null){
			//fetch from DB
			ConfigurationExample example = new ConfigurationExample();
			example.createCriteria().addCriterion("name = ", name).addCriterion("deleted = ", false);
			conf = confMapper.selectOneByExample(example);
			if(conf != null){
				GLOBAL_CONFIGURATION.put(conf.getName(), conf);
			}
		}
		return conf;
	}

	@Override
	public boolean deleteById(Long id) {
		initialize();
		
		Configuration conf = findById(id);
		if(conf == null){
			return true;
		}
		conf.setDeleted(true);
		if(confMapper.updateByPrimaryKey(conf) == 1){
			GLOBAL_CONFIGURATION.remove(conf.getName());
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteByName(String name) {
		initialize();
		
		Configuration conf = findByName(name);
		if(conf == null){
			return true;
		}
		conf.setDeleted(true);
		if(confMapper.updateByPrimaryKey(conf) == 1){
			GLOBAL_CONFIGURATION.remove(name);
			return true;
		}
		return false;
	}

	private void load() {
		Map<String, Configuration> configurations = new HashMap<>();
		ConfigurationExample example = new ConfigurationExample();
		example.createCriteria();
		List<Configuration> list = confMapper.selectByExample(example, null);
		for(Configuration conf : list){
			configurations.put(conf.getName(), conf);
		}
		GLOBAL_CONFIGURATION.clear();
		GLOBAL_CONFIGURATION.putAll(configurations);
	}
	
	@Override
	public boolean initialize() {
		if(isInitialized){
			return true;
		}
		return initialize(false);
	}

	@Override
	public synchronized boolean initialize(boolean isForceRefresh) {
		if(isInitialized && !isForceRefresh){
			return true;
		}
		load();
		isInitialized = true;
		return isInitialized;
	}
}
