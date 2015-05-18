package com.xpos.common.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xpos.common.service.UpYun;
import com.xpos.common.service.UpYun.PARAMS;



/**
 * The facade of UpYun service to make upload file to UpYun easily 
 * @author pwan
 *
 */
@Service("upYunService")
public class UpYunServiceImpl implements FileUploadService {
	private static Logger logger = LoggerFactory.getLogger(UpYunServiceImpl.class);
	
	@Resource
	private UpYun upYunFileUploader;
	@Resource
	private UpYun upYunImgUploader;

	

	@Override
	public boolean uploadFile(String filePath, File file) {
		if(StringUtils.isBlank(filePath)){
			logger.error("UpYun file uploading...,filePaht is null");
			return false;
		}
		if(file == null){
			logger.error("UpYun file uploading...,file object is null");
			return false;
		}
		try {
			return upYunFileUploader.writeFile(filePath, file, true);  //auto create directory
		} catch (IOException e) {
			logger.error("Cannot upload file to UpYun due to "+ e.getMessage());
			return false;
		}
	}

	@Override
	public boolean uploadFile(String filePath, byte[] fileData) {
		if(StringUtils.isBlank(filePath)){
			logger.error("UpYun file uploading...,filePaht is null");
			return false;
		}
		if(fileData == null || fileData.length <= 0){
			logger.error("UpYun file uploading...,fileData is null");
			return false;
		}
		return upYunFileUploader.writeFile(filePath, fileData, true);  //auto create directory
	}

	@Override
	public boolean uploadImg(String filePath, File file, String thumbSize) {
		if(StringUtils.isBlank(filePath)){
			logger.error("UpYun image uploading...,filePaht is null");
			return false;
		}
		if(file == null){
			logger.error("UpYun image uploading...,file object is null");
			return false;
		}
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put(PARAMS.KEY_X_GMKERL_TYPE.getValue(),PARAMS.VALUE_FIX_BOTH.getValue());
			params.put(PARAMS.KEY_X_GMKERL_VALUE.getValue(), thumbSize);
			params.put(PARAMS.KEY_X_GMKERL_QUALITY.getValue(), "95");
			params.put(PARAMS.KEY_X_GMKERL_UNSHARP.getValue(), "true");
			return upYunImgUploader.writeFile(filePath, file, true,params);  //auto create directory
		} catch (IOException e) {
			logger.error("Cannot upload image to UpYun due to "+ e.getMessage());
			return false;
		}
	}

	@Override
	public boolean uploadImg(String filePath, byte[] fileData, String thumbSize) {
		if(StringUtils.isBlank(filePath)){
			logger.error("UpYun image uploading...,filePaht is null");
			return false;
		}
		if(fileData == null || fileData.length <= 0){
			logger.error("UpYun image uploading...,file object is null");
			return false;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put(PARAMS.KEY_X_GMKERL_TYPE.getValue(),PARAMS.VALUE_FIX_BOTH.getValue());
		params.put(PARAMS.KEY_X_GMKERL_VALUE.getValue(), thumbSize);
		params.put(PARAMS.KEY_X_GMKERL_QUALITY.getValue(), "95");
		params.put(PARAMS.KEY_X_GMKERL_UNSHARP.getValue(), "true");
		return upYunImgUploader.writeFile(filePath, fileData, true, params);  //auto create directory
	}

	@Override
	public boolean uploadImg(String filePath, File file) {
		return uploadImg(filePath, file, null);
	}

	@Override
	public boolean uploadImg(String filePath, byte[] fileData) {
		return uploadImg(filePath, fileData, null);
	}

}


