package com.xpos.controller;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.searcher.PictureSearcher;
import com.xpos.common.service.PictureService;
import com.xpos.common.utils.Pager;

/**
 * 相册管理
 * @author hongk
 */

@Controller
@RequestMapping("album")
public class AlbumController extends BaseController {

	@Resource
	private PictureService pictureService;

	
	@RequestMapping(value="/list" )
	public String findShops(Pager<Picture> pager, PictureSearcher searcher,  Model model){
		
		pager = pictureService.findAlbumList(pager, (PictureExample)searcher.getExample() , getBusiness());
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return "album/album_list";
	}
	
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String showAddAlbum (Model model){
		return "album/album_input";
	}
	
	@RequestMapping(value="/editPic/{picId}", method=RequestMethod.GET)
	public String showEditAlbum (Model model,@PathVariable Long picId){
		model.addAttribute("pic", pictureService.findAlbumBypicId(picId));
		return "album/album_input";
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/album/list")
	@RequestMapping(value="", method=RequestMethod.POST)
	public String addAlbum(MultipartFile albumFile, Model model,Picture picture , RedirectAttributes  attributes){
		if (!albumFile.isEmpty()) {
			Picture album = pictureService.getPictureFromMultipartFile(albumFile);
			//判断是否存在相同的MD5
			if (pictureService.findDetailPicture(album,getBusiness())==null) {
				picture.setData(album.getData());
				picture.setName(album.getName());
				picture.setOriginalName(album.getOriginalName());
				pictureService.saveAlbum(picture,getBusiness());
			}else {
				attributes.addFlashAttribute("status", STATUS_FAILD);
				attributes.addFlashAttribute("msg", "上传的图片已存在，请勿重复上传");
			   return  "redirect:/album/add";
			}
		}
		model.addAttribute("status",STATUS_SUCCESS);
		model.addAttribute("msg", "添加图片成功");
		return findShops(new Pager<Picture>(), new PictureSearcher(), model);
	}
	
	@RequestMapping(value="/edit/{picId}", method=RequestMethod.POST)
	public String editAlbum(MultipartFile albumFile, Model model,Picture picture,@PathVariable Long picId){
		if (!albumFile.isEmpty()) {
			Picture album = pictureService.getPictureFromMultipartFile(albumFile);
			//判断是否存在相同的MD5(除自身以外)
			if (pictureService.findDetailPicture(album,getBusiness())==null) {
				picture.setId(picId);
				picture.setData(album.getData());
				picture.setName(album.getName());
				picture.setOriginalName(album.getOriginalName());
				pictureService.editAlbum(picture,getBusiness());
			}else {
				model.addAttribute("status", STATUS_FAILD);
				model.addAttribute("msg", "上传的图片已存在，请勿重复上传");
				return showEditAlbum(model, picId);
			}
		}else {
			picture.setId(picId);
			pictureService.editAlbumWithOutFile(picture);
		}
		model.addAttribute("status",STATUS_SUCCESS);
		model.addAttribute("msg", "修改图片成功");
		return findShops(new Pager<Picture>(), new PictureSearcher(), model);
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.DELETE)
	public String deleteAlbum(@PathVariable Long id,Model model){
		if(pictureService.deleteAlbum(id)) {
			model.addAttribute("status",STATUS_SUCCESS);
			model.addAttribute("msg", "删除成功");
		}else {
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", "删除失败");
		}
		return findShops(new Pager<Picture>(), new PictureSearcher(), model);
	}
	
}
