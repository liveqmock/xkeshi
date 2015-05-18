package com.xpos.common.service;

import com.xpos.common.entity.Configuration;


public interface ConfigurationService {
	
	public boolean save(Configuration configuration);
	
	public boolean update(Configuration configuration);
	
	//updateValueBykey
	
	public Configuration findById(Long id);
	
	public Configuration findByName(String name);
	
	public boolean deleteById(Long id);
	
	public boolean deleteByName(String name);
	
	/** 从数据库加载所有配置项。
	 * 如果本地内存中还未初始化，尝试首次加载，否则跳过该步骤
	 */
	public boolean initialize();
	
	/** 从数据库加载所有配置项
	 * @param isForceRefresh 是否强制重新加载（不考虑本地是否已初始化）
	 */
	public boolean initialize(boolean isForceRefresh);
}
