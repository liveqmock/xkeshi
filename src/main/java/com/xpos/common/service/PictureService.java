package com.xpos.common.service;

import org.springframework.web.multipart.MultipartFile;

import com.xpos.common.entity.Picture;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.utils.Pager;


public interface PictureService {
	
	/**
	 * 返回图片信息
	 */
	public Picture getPictureFromMultipartFile(MultipartFile file);
	
	/**
	 * 上传图片
	 */
	public boolean uploadPicture(Picture picture);
	
	
	/**
	 * 相册List
	 */
	public Pager<Picture> findAlbumList(Pager<Picture> pager, PictureExample example, Business business);
	
	
	/**
	 * 保存相册
	 */
	boolean saveAlbum(Picture picture,Business business);
	
	/**
	 * 保存相册
	 */
	boolean editAlbum(Picture picture,Business business);
	
	/**
	 * 保存相册
	 */
	boolean editAlbumWithOutFile(Picture picture);
	
	/**
	 * 删除相册照片
	 */
	boolean deleteAlbum(Long id);
	
	/**
	 * 保存时查询是否存在
	 * @param business 
	 */
	public Picture findDetailPicture(Picture picture, Business business);
	
	/**
	 * 修改时查询是否存在
	 * @param business 
	 */
	public Picture findDetailPictureWithoutOwn(Picture picture, Long picId, Business business);
	
	/**
	 *根据picId查询图片	
	 */
	public Picture findAlbumBypicId(Long picId);
	
	/**
	 * Business相册List (集团管理下,管理商户的相册)
	 */
	public Pager<Picture> findShopByAlbumList(Pager<Picture> pager, PictureExample example,Business business, Long  shopId);

	boolean saveAlbum(Picture picture, Long shopId);

	boolean editAlbum(Picture picture, Long shopId);
}
