package com.xpos.api;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.xpos.common.entity.Configuration;
import com.xpos.common.service.ConfigurationService;

public class BaseResource extends ServerResource {
	@Autowired
	private ConfigurationService confService;
	
	@Override
	public Representation handle() {
		String clientType = ServletUtils.getRequest(getRequest()).getHeader("AppType");
		String posVersion = ServletUtils.getRequest(getRequest()).getHeader("AppVersion");
		
		if(StringUtils.isBlank(clientType) && StringUtils.isBlank(posVersion)){
			return super.handle();
		}
		
		boolean skipUpdate = false;
		String latestVersion = null; //最新的版本号
		String updateURL = null; //安装包下载地址
		String packageDesc = null; //新版本更新描述
		String packageSize = null; //安装包大小
		
		Configuration conf = confService.findByName("terminalVersion." + clientType);
		if(conf == null){
			skipUpdate = true;
		}else{
			latestVersion = conf.getValue();
			skipUpdate = compareVersion(latestVersion, posVersion);
			
			if(!skipUpdate){
				updateURL = confService.findByName("terminalDownloadUrl." + clientType).getValue();
				
				Configuration descConf = confService.findByName("terminalPackageDescription." + clientType);
				if(descConf != null){ //可能部分包没有该配置项，做空判断
					packageDesc = descConf.getValue();
				}
				
				Configuration sizeConf = confService.findByName("terminalPackageSize." + clientType);
				if(sizeConf != null){ //可能部分包没有该配置项，做空判断
					int byteSize = NumberUtils.toInt(sizeConf.getValue(), 0);
					if(byteSize < 1024 * 1024){ //小于1M，以K为单位
						packageSize = byteSize/1024 + "K";
					}else if(byteSize < 1024 * 1024 * 1024){ //小于1G，以M为单位
						packageSize = byteSize/(1024 * 1024) + "M";
					}
				}
			}
		}
		
		JsonRepresentation re = null;
		if(skipUpdate){
			return super.handle();
		}else{
			JSONObject json = new JSONObject();
			json.put("res", "-100");
			json.put("description", "sdfsadfsadf");
			json.put("version", latestVersion);
			json.put("url", updateURL+"?t="+new Random().nextInt(100000));
			if(StringUtils.isNotBlank(packageDesc)){
				json.put("description", packageDesc);
			}
			if(StringUtils.isNotBlank(packageSize)){
				json.put("size", packageSize);
			}
			re = new JsonRepresentation(json);
			getResponse().setEntity(re);
			return re;
		}
	}
	
	/*
	 * 1.根据约定，测试版本以".test"结尾
	 * 2.测试版本只能升级到测试版本。正式版本只能升级到测试版本。测试版本与正式版本之间无法交叉升级
	 * 3.只能从低版本号升级到高版本号
	 */
	protected boolean compareVersion(String latestVersion, String posVersion){
		if((StringUtils.containsIgnoreCase(latestVersion, "test") && !StringUtils.containsIgnoreCase(posVersion, "test"))
				|| (!StringUtils.containsIgnoreCase(latestVersion, "test") && StringUtils.containsIgnoreCase(posVersion, "test"))){
			//后缀不同，说明不同环境交叉升级
			return true;
		}else{
			latestVersion = StringUtils.removeEndIgnoreCase(latestVersion, ".test");
			posVersion = StringUtils.removeEndIgnoreCase(posVersion, ".test");
			//移除后缀开始比较版本号
			String[] latestVers = StringUtils.split(latestVersion, ".");
			String[] posVers = StringUtils.split(posVersion, ".");
			if(latestVers.length != posVers.length){
				//版本长度不同，无法升级
				return true;
			}else{
				for(int i = 0; i < latestVers.length; i++){
					int latestVer = NumberUtils.toInt(latestVers[i], 0);
					int posVer = NumberUtils.toInt(posVers[i], 0);
					if(latestVer > posVer){
						return false;
					}else if(latestVer < posVer){
						return true;
					}
				}
				return true;
			}
		}
	}
}
