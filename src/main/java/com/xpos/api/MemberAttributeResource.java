package com.xpos.api;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.service.TerminalService;
import com.xpos.common.service.member.MemberAttributeService;
import com.xpos.common.service.member.MemberTypeService;


public class MemberAttributeResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(MemberAttributeResource.class);
	
	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private MemberTypeService memberTypeService;
	
	@Autowired
	private MemberAttributeService memberAttributeService;
	
	/**
	 * 获取指定商户的会员模板扩展信息
	 * /merchant/{mid}/member/attribute/template?deviceNumber=xxx
	 */
	@Get
	public Representation getMemberAttributes(){
		try {
			Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
			String deviceNumber = getQuery().getFirstValue("deviceNumber");
			if(StringUtils.isBlank(deviceNumber)){
				return new JsonRepresentation(new ValidateError("3901","deviceNumber不能为空"));
			}else if(mid <= 0){
				return new JsonRepresentation(new ValidateError("3902","商户Id不能为空"));
			}
			
			//take care of device_number here...
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("3903","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("3904","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
				return new JsonRepresentation(new ValidateError("3905","商户与设备不匹配"));
			}
			
			JSONObject json = new JSONObject();
			json.put("res", "0");
			Shop shop = terminal.getShop();
			List<? extends MemberType> memberTypes = null;
			if(shop.getMerchant() != null && Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){ //会员统一管理的子商户，加载集团公用会员类型
				memberTypes = memberTypeService.findMerchantMemberTypeListWithAttributeTemplateByMerchantId(shop.getMerchant().getId());
			}else{
				memberTypes = memberTypeService.findShopMemberTypeListWithAttributeTemplateByShopId(shop.getId());
			}
			if(!CollectionUtils.isEmpty(memberTypes)){
				JSONArray array = new JSONArray();
				for(MemberType memberType : memberTypes){
					JSONObject obj = new JSONObject();
					obj.put("id", memberType.getId());
					obj.put("name", memberType.getName());
					obj.put("discount", memberType.getDiscount());
					List<MemberAttribute> attributeList = memberAttributeService.findAttributeListByTemplate(memberType.getMemberAttributeTemplate().getId());
					if(!CollectionUtils.isEmpty(attributeList)){
						JSONArray attrArray = new JSONArray();
						for(MemberAttribute attr : attributeList){
							JSONObject attrObj = new JSONObject();
							attrObj.put("id", attr.getId());
							attrObj.put("name", attr.getName());
							attrObj.put("type", attr.getAttributeType().name());
							attrObj.put("required", attr.isRequired());
							switch(attr.getAttributeType()){
								case select: case checkbox:
									String values = attr.getOptionalValues();
									values = values.replace("[", "");
									values = values.replace("]", "");
									values = values.replace("\"", "");
									attrObj.put("value", values);
									break;
								default:
									attrObj.put("value", "");
							}
							attrArray.put(attrObj);
						}
						obj.put("extendAttributes", attrArray);
					}
					array.put(obj);
				}
				json.put("memberType", array);
			}
			return new JsonRepresentation(json);
		} catch (Exception e) {
			logger.error("Cannot get member attributes due to "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
}
