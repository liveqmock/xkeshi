package com.xpos.common.service;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.entity.example.ShopExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.exception.GenericException;
import com.xpos.common.persistence.mybatis.PictureMapper;
import com.xpos.common.persistence.mybatis.ShopMapper;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.Pager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService{
	
		private Log logger = LogFactory.getLog(getClass());
	
		@Resource
		private UpYun upYunImgUploader;
		
		@Resource
		private PictureMapper pictureMapper;
	    
		@Resource
		private ShopMapper  shopMapper ;
		
		@Override
		public Picture getPictureFromMultipartFile(MultipartFile file){
			try {
				byte[] data = file.getBytes();
				String fileName = FileMD5.getFileMD5String(data);
				String originalName = file.getOriginalFilename();

                String suffix = originalName.substring(originalName.lastIndexOf(".")+1,originalName.length());
                String[] types = new String[]{"jpg","png","bmp","gif"};
                Boolean booleanSuffix = false;
                for (int i = 0;i<types.length; i++) {
                    if( suffix.equalsIgnoreCase(types[i])) {
                        booleanSuffix = true;
                        break;
                    }
                }
                if (booleanSuffix == false) {
                    throw new GenericException("文件格式错误! 可用图片格式：jpg,png,bmp,gif!");
                }

				StringBuilder replacedFileName = new StringBuilder(originalName);
				Picture picture = new Picture();
				picture.setName(replacedFileName.replace(0, originalName.lastIndexOf("."), fileName).toString());
				picture.setOriginalName(originalName);
				picture.setData(data);
				return picture;
				
			} catch (IOException e) {
				logger.error("上传图片读取文件出错!");
				throw new GenericException("error reading picture data!");
			}			
		}

		@Override
		public boolean uploadPicture(Picture picture) {
			if(StringUtils.isNotBlank(picture.getPath())){
				throw new GenericException("error picture path!");
			}
			if(picture.getData() == null){
				throw new GenericException("error picture data!");
			}
			
			picture.setPath("/" + picture.getPictureType().toString().toLowerCase()+"/"+ picture.getForeignId());
			upYunImgUploader.debug = true;
			boolean result = upYunImgUploader.writeFile(picture.getPath() + "/" + picture.getName(), picture.getData(), true);
			if(picture.getId()==null) {
				result = result && pictureMapper.insert(picture) > 0;
			}else {
				result = result && pictureMapper.updateByPrimaryKey(picture)>0;
			}
			return result;
		}
		
		public Pager<Picture> findAlbumList(Pager<Picture> pager, PictureExample example, Business business){
			if(example == null)
				example = new PictureExample();
			example.appendCriterion("foreignId=",business.getAccessBusinessId(BusinessModel.ALBUM))
					.addCriterion("deleted=", false)
					.addCriterion("pictureType='ALBUM'");
			List<Picture> list = pictureMapper.selectByExample(example, pager);
			int totalCount = pictureMapper.countByExample(example);
			pager.setTotalCount(totalCount);
			pager.setList(list);
			return pager;
		}
		
		@Override
		@Transactional
		public boolean saveAlbum(Picture picture,Business business) {
			boolean result = false;
			boolean resultSave = false;
			picture.setForeignId(business.getAccessBusinessId(BusinessModel.ALBUM));
			picture.setPictureType(PictureType.ALBUM);
			result = uploadPicture(picture) ;
			picture.setPath("/" + picture.getPictureType().toString().toLowerCase()+"/"+ picture.getForeignId());
			resultSave = pictureMapper.updateByPrimaryKey(picture)>0;
			return result && resultSave;
		}
		
		@Override
		@Transactional
		public boolean editAlbum(Picture picture,Business business) {
			boolean result = false;
			boolean resultSave = false;
			picture.setForeignId(business.getAccessBusinessId(BusinessModel.ALBUM));
			picture.setPictureType(PictureType.ALBUM);
			result = uploadPicture(picture);
			picture.setPath("/" + picture.getPictureType().toString().toLowerCase()+"/"+ picture.getForeignId());
			resultSave = pictureMapper.updateByPrimaryKey(picture)>0;
			return result && resultSave;
		}
		
		@Override
		public boolean editAlbumWithOutFile(Picture picture) {
			return pictureMapper.updateByPrimaryKey(picture)>0;
		}
		
		public boolean deleteAlbum(Long id) {
			Picture picture = new Picture();
			picture.setId(id);
			picture.setDeleted(true);
			return pictureMapper.updateByPrimaryKey(picture)>0;
		}
		
		public Picture findDetailPicture(Picture picture,Business business) {
			PictureExample example = new PictureExample();
			example.appendCriterion("name like '%"+picture.getName().substring(0, picture.getName().indexOf("."))+"%'")
					.addCriterion("pictureType='ALBUM'")
					.addCriterion("foreignId=", business.getSelfBusinessId())
					.addCriterion("deleted=",false);
			return pictureMapper.selectOneByExample(example);
		}
		
		public Picture findDetailPictureWithoutOwn(Picture picture,Long picId,Business business) {
			PictureExample example = new PictureExample();
			example.appendCriterion("name like '%"+picture.getName().substring(0, picture.getName().indexOf("."))+"%'")
			.addCriterion("pictureType='ALBUM'")
			.addCriterion("foreignId=", business.getSelfBusinessId())
			.addCriterion("id!=",picId)
			.addCriterion("deleted=",false);
			return pictureMapper.selectOneByExample(example);
		}
		
		public Picture findAlbumBypicId(Long picId) {
			return pictureMapper.selectByPrimaryKey(picId);
		}

		@Override
		public Pager<Picture> findShopByAlbumList(Pager<Picture> pager,
				PictureExample example,Business business , Long shopId) {
				ShopExample shopExample  = new ShopExample();
				Criteria criteria = shopExample.createCriteria();
						 criteria.addCriterion("id=", shopId)
					             .addCriterion("deleted = ", false);
				if(business  instanceof Merchant)
					criteria.addCriterion("merchant_id = ", business.getSelfBusinessId());
				Shop shop  = shopMapper.selectOneByExample(shopExample );
				if(shop != null ){
					if(example == null)
						example = new PictureExample();
					example.appendCriterion("foreignId=",shopId)
							.addCriterion("deleted=", false)
							.addCriterion("pictureType=",Picture.PictureType.ALBUM.toString());
					List<Picture> list = pictureMapper.selectByExample(example, pager);
					int totalCount = pictureMapper.countByExample(example);
					pager.setTotalCount(totalCount);
					pager.setList(list);
				}
			return pager;
		}

		@Override
		public boolean saveAlbum(Picture picture, Long shopId) {
			boolean result = false;
			boolean resultSave = false;
			picture.setForeignId(shopId);
			picture.setPictureType(PictureType.ALBUM);
			result = uploadPicture(picture) ;
			picture.setPath("/" + picture.getPictureType().toString().toLowerCase()+"/"+ picture.getForeignId());
			if(picture.getId() == null) {
				resultSave = pictureMapper.insert(picture)>0;
			}else {
				resultSave = pictureMapper.updateByPrimaryKey(picture)>0;
			}
			return result && resultSave;
		}

		@Override
		public boolean editAlbum(Picture picture, Long shopId) {
			boolean result = false;
			boolean resultSave = false;
			picture.setForeignId(shopId);
			picture.setPictureType(PictureType.ALBUM);
			result = uploadPicture(picture);
			picture.setPath("/" + picture.getPictureType().toString().toLowerCase()+"/"+ picture.getForeignId());
			resultSave = pictureMapper.updateByPrimaryKey(picture)>0;
			return result && resultSave;
		}
}
