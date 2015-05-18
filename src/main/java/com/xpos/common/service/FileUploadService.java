package com.xpos.common.service;

import java.io.File;


/**
 * File upload services
 * @author pwan
 *
 */
public interface FileUploadService {
	
	
	/**
	 * Upload common files except pictures. e.g .docx, .xlsx and .txt
	 * @param filePath The target storage path in UpYun bucket. 
	 * This value can be append to the UpYun binding address directly to display the picture. http://${binding-url}/filePath
	 * @param file The {@link File} object to be uploaded
	 * @return
	 */
	public boolean uploadFile(String filePath, File file);
	public boolean uploadFile(String filePath, byte[] fileData);
	
	/**
	 * Upload image file and compress it into given sizes
	 * @param filePath The target storage path in UpYun bucket. 
	 * This value can be append to the UpYun binding address directly to display the picture. http://${binding-url}/filePath
	 * @param file The {@link File} object to be uploaded
	 * @param thumbSize The thumbnail size. e.g. "600x600"
	 * @return
	 */
	public boolean uploadImg(String filePath, File file, String thumbSize);
	public boolean uploadImg(String filePath, byte[] fileData, String thumbSize);
	public boolean uploadImg(String filePath, File file);
	public boolean uploadImg(String filePath, byte[] fileData);
	
	/**
	 * Upload image file and crop it into given sizes
	 * @param filePath The target storage path in UpYun bucket. 
	 * This value can be append to the UpYun binding address directly to display the picture. http://${binding-url}/filePath
	 * @param file The {@link File} object to be uploaded
	 * @param cropSize The thumb size, e.g. "50,50,300,300"
	 * @return
	 */
//	public boolean uploadAndCropImg(String filePath, File file, String[] cropSizeArray);
//	public boolean uploadAndCropImg(String filePath, byte[] fileData, String[] cropSizeArray);
}
