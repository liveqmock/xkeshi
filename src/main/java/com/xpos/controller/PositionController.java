package com.xpos.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xpos.common.entity.Landmark;
import com.xpos.common.entity.Position;
import com.xpos.common.entity.example.LandmarkExample;
import com.xpos.common.entity.example.RegionExample;
import com.xpos.common.searcher.LandmarkSearcher;
import com.xpos.common.searcher.RegionSearcher;
import com.xpos.common.service.LandmarkService;
import com.xpos.common.service.RegionService;



@Controller
@RequestMapping("position")
public class PositionController extends BaseController{
	
	private Log logger = LogFactory.getLog(getClass());
	
	@Resource
	private RegionService  regionService   ; 
	@Resource
	private LandmarkService landmarkService;

	/** 后台地理位置编辑弹框 */
	@RequestMapping(value = "input", method = RequestMethod.GET)
	public String selectPostion(String address,String lat, String lng, String adrsEmpty , String isEdit , Model model){
		model.addAttribute("address", address);
		model.addAttribute("lat", lat);
		model.addAttribute("lng", lng);
		model.addAttribute("adrsEmpty", adrsEmpty);
		model.addAttribute("isEdit", isEdit);
		return "position_input";
	}


	@RequestMapping(value = "/landmark", method = RequestMethod.GET)
	public String initLandmarkMaintain(@RequestParam(value = "city_code", required = false, defaultValue = "330100") String city_code, 
			Map<String, Object> model,LandmarkSearcher searcher) throws Exception{
		//默认加载杭州市地标(city_code = 330100)
		String _city_code = "330100";
		if(!StringUtils.isBlank(city_code) && city_code.length() == 6 && NumberUtils.isDigits(city_code)){
			_city_code = city_code.toString();
		}
		model.put("city_code", _city_code);
		//加载地标
		List<Landmark> landmarks = landmarkService.loadLandmarkMap((LandmarkExample)searcher.getExample()).get(_city_code);
		model.put("landmarks", landmarks);
		
		return "landmark/landmark_list";
	}
	
	/** 后台地理位置编辑弹框 */
	@RequestMapping(value = "maintainlandmarkposition", method = RequestMethod.GET)
	public String maintainLandmark(@ModelAttribute Landmark landmark, 
			                       @RequestParam("type")String type, 
			                       HttpServletResponse response,
			                       String location ,ModelMap map){
		logger.debug("地标管理"+landmark.getId());
		map.put("city_code", landmark.getCityCode());
		map.put("type", type);
		map.put("location", location);
		if(StringUtils.equalsIgnoreCase("edit", type)){
			map.put("landmark", landmark);
			Position p = landmark.getPosition();
			if(p != null){
				map.put("position", p);
			}
		}else if(StringUtils.equalsIgnoreCase("add", type)){
			map.put("landmark", null);
		}
		response.addHeader("Access-Control-Allow-Origin", "http://www.xpos.com");
		return "landmark/landmark_input";
	}
	
	@RequestMapping(value = "/add/landmark", method = RequestMethod.POST)
	public HttpEntity<String> addLandmark(@RequestBody Landmark landmark, Model model) {
	    Long id = landmarkService.saveLandmark(landmark);
		JSONResponse  jsonResponse = new JSONResponse(id > 0 ? new GrantResult(SUCCESS, "地标创建成功") : new GrantResult(FAILD, "地标创建失败"));
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	@RequestMapping(value = "/edit/landmark", method = RequestMethod.POST)
	public HttpEntity<String> editLandmark(@RequestBody Landmark landmark, Model model) {
		JSONResponse  jsonResponse = new JSONResponse(landmarkService.modifyLandmark(landmark) ? new GrantResult(SUCCESS, "编辑地标成功") : new GrantResult(FAILD, "编辑地标失败"));
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	@RequestMapping(value="/district" ,method =RequestMethod.GET)
	public HttpEntity<String> getdistrict( RegionSearcher searcher ,Model model){
		GrantResult grantResult = new GrantResult(SUCCESS, "查询成功");
		grantResult.setResult(regionService.findRegionList((RegionExample) searcher.getExample()));
		JSONResponse  jsonResponse = new JSONResponse(grantResult);
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
}
