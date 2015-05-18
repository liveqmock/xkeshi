package com.xpos.api;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.drongam.hermes.entity.SMS;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xkeshi.utils.EncryptionUtil;
import com.xpos.api.param.MemberVo;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.service.OperatorService;
import com.xpos.common.service.OperatorShiftService;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.BeanUtil;


public class MemberResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(MemberResource.class);
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private OperatorShiftService  operatorShiftService;
	
	@Autowired
	private OperatorService operatorService;

	@Autowired
	private  XShopService  xShopService   ;
	
	@Autowired
	private XMerchantService  xMerchantService   ;
	
	@Autowired
	private SMSService  smsService   ;
		
	
	/**
	 * 更新会员信息
	 * /merchant/{mid}/member/{mbid}/update
	 */
	@Put("json")
	public Representation updateMember(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		
		try {
			MemberVo mv = JSON.parseObject(entity.getText(), MemberVo.class);
			String validation = BeanUtil.validate(mv);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("1101",validation));
			}
			
			//take care of device_number here...
			String deviceNumber = mv.getDeviceNumber();
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("1105","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("1103","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(mv.getMidLong())){
				return new JsonRepresentation(new ValidateError("1102","商户与设备不匹配"));
			}
			
			Shop shop = terminal.getShop();
			//take care of member id
			String mbid = mv.getMbid();
			Member member = null;
			if(StringUtils.isBlank(mbid) || !StringUtils.isNumeric(mbid) || mv.getMbidLong() <= 0){
				return new JsonRepresentation(new ValidateError("1101", "会员ID未指定"));
			}else if((member = memberService.findMemberByIdWithAttributes(mv.getMbidLong())) == null){ //根据会员Id查找不到会员，
				return new JsonRepresentation(new ValidateError("1104", "指定会员不存在"));
			}else if(shop.getMerchant() == null && (!BusinessType.SHOP.equals(member.getBusinessType()) || member.getBusinessId() != shop.getId())){ //普通商户，但是商户Id不匹配)
				
			}
			
			Member updateMember = new Member();
			if(BusinessType.MERCHANT.equals(member.getBusinessType())){
				updateMember.setMemberType(new MerchantMemberType());
			}else if(BusinessType.SHOP.equals(member.getBusinessType())){
				updateMember.setMemberType(new ShopMemberType());
			}
			updateMember.setId(mv.getMbidLong());
			updateMember.setShop(shop);
			updateMember.setName(mv.getName());
			updateMember.setBirthday(mv.getBirthdayDate());
			updateMember.setEmail(mv.getEmail());
			updateMember.setMobile(mv.getMobile());
			updateMember.setGender(mv.getSex());
			updateMember.getMemberType().setId(mv.getMemberTypeId()); //FIXME, 传入的memberTypeId要与数据库现有memberTypeId匹配，不允许在pad端修改
			updateMember.getMemberType().setMemberAttributeTemplate(new MemberAttributeTemplate());
			if(!CollectionUtils.isEmpty(mv.getMemberAttributes())){
				updateMember.getMemberType().getMemberAttributeTemplate().setMemberAttributeList(mv.getMemberAttributes());
			}
			
			//check if the mobile exists
			if(StringUtils.isBlank(mv.getMobile()) || !StringUtils.isNumeric(mv.getMobile())){
				return new JsonRepresentation(new ValidateError("1101","手机号格式错误"));
			}else if(!StringUtils.equals(member.getMobile(), mv.getMobile())){
				Member _member = memberService.findMemberByMobileForShop(shop, updateMember.getMobile());
				if(_member != null){
					return new JsonRepresentation(new ValidateError("1106","手机号已存在"));
				}
			}
			
			try{
				if(memberService.validateAndUpdateMember(updateMember, shop)){
					return new JsonRepresentation(ResCode.General.OK);
				}else{
					return new JsonRepresentation(new ValidateError("1108", "会员更新失败"));
				}
			}catch(Exception e){
				return new JsonRepresentation(new ValidateError("1108", "会员更新失败：" + e.getMessage()));
			}
		} catch (Exception e) {
			logger.error("Cannot update member info due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	
	/**
	 * 新会员注册
	 * /member/register
	 */
	@Post("json")
	public Representation register(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			MemberVo mv = JSON.parseObject(entity.getText(), MemberVo.class);
			String validation = BeanUtil.validate(mv);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("0201",validation));
			}
			
			
			//take care of device_number here...
			String deviceNumber = mv.getDeviceNumber();
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			Operator operator = operatorService.findById(mv.getOperatorId());
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("0202","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("0203","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(mv.getMidLong())){
				return new JsonRepresentation(new ValidateError("0204","商户与设备不匹配"));
			}else if(mv.getMemberTypeId() == null || mv.getMemberTypeId() <= 0){
				return new JsonRepresentation(new ValidateError("0201", "会员类型参数错误"));
			}else if(operator == null || !operator.getShop().getId().equals(terminal.getShop().getSelfBusinessId())){
				return new JsonRepresentation(new ValidateError("0214","操作员信息错误"));
			}
			
			//check if the mobile exists
			Shop shop = terminal.getShop();
			Member member = memberService.findMemberByMobileForShop(shop, mv.getMobile());
			String sessionCode = null;
			com.xkeshi.pojo.po.Shop shopPO = xShopService.findShopByShopId(shop.getId());
			if (shopPO.getEnableShift()) {
				POSOperationLog operatorLog = operatorShiftService.getLastOperatorSession(deviceNumber, mv.getOperatorId());
				if (operatorLog != null){ 
					sessionCode = operatorLog.getOperatorSessionCode();
				}
			}
			
			if(member != null){
				return new JsonRepresentation(new ValidateError("0206","手机号已存在"));
			}
			
			Member mem = new Member();
			if(shop.getMerchant() == null || Boolean.FALSE.equals(shop.getMerchant().getMemberCentralManagement())){
				mem.setMemberType(new ShopMemberType());
				mem.setBusiness(shop);
			}else if(Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
				mem.setMemberType(new MerchantMemberType());
				mem.setBusiness(shop.getMerchant());
			}else{
				return new JsonRepresentation(new ValidateError("0208","集团会员模块还未初始化"));
			}
			//copy values from mv to mem here
			mem.setShop(shop);
			mem.setName(mv.getName());
			mem.setMobile(mv.getMobile());
			mem.setGender(mv.getSex());
			mem.setBirthday(mv.getBirthdayDate());
			mem.setEmail(mv.getEmail());
			mem.getMemberType().setId(mv.getMemberTypeId());
			mem.getMemberType().setMemberAttributeTemplate(new MemberAttributeTemplate());
			if(!CollectionUtils.isEmpty(mv.getMemberAttributes())){
				mem.getMemberType().getMemberAttributeTemplate().setMemberAttributeList(mv.getMemberAttributes());
			}
			mem.setOperator(operator);
			mem.setOperatorSessionCode(sessionCode);
			//save mem
			try{
				//设置初始会员密码：随机6位数字和小写字母
				String rawPassword = StringUtils.lowerCase(EncryptionUtil.generateRandomCharAndNumber(6));
				String salt = EncryptionUtil.getSalt();
				mem.setSalt(salt);
				mem.setPassword(EncryptionUtil.encodePassword(rawPassword, salt));
				if(memberService.validateAndSave(mem, shop)){
					re = new JsonRepresentation(ResCode.General.OK);
					JSONObject json = re.getJsonObject();
					json.put("mbid", mem.getId());
					String message  =  StringUtils.join("您好！恭喜您成功注册为",shop.getName(),"会员，账号：",mem.getMobile(),
							"，初始密码：",rawPassword, "。 修改密码或个人信息请访问 http://member.xka.me/", 
							BusinessType.MERCHANT.equals(mem.getBusinessType()) ? "m/" : "s/", mem.getBusinessId());
					SMS sms = new SMS();
					sms.setMobile(mem.getMobile());
					sms.setMessage(message);
					String  hiddenContent = StringUtils.left(message, message.length()-6)+"******";
					smsService.sendSMSAndDeductions(shop.getId() ,BusinessType.SHOP,sms,hiddenContent,"会员注册成功,发送成功短信" );
				}else{
					return new JsonRepresentation(new ValidateError("0207","新会员保存失败"));
				}
			} catch(Exception e){
				re = new JsonRepresentation(new ValidateError("0207","新会员保存失败：" + e.getMessage()));
			}
			return re;
			
		} catch (Exception e) {
			logger.error("Cannot register member due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
		
	}
	
	/**
	 * 用户登陆(获取用户信息)
	 * /merchant/{mid}/member/{phone}?deviceNumber=xxx
	 */
	@Get("json")
	public Representation getInfo(){
		JsonRepresentation re = null;
		try {
			String mid = (String) getRequestAttributes().get("mid");
			String phone = (String) getRequestAttributes().get("phone");
			String deviceNumber = getQuery().getFirstValue("deviceNumber");
			if(StringUtils.isBlank(deviceNumber)){
				return new JsonRepresentation(new ValidateError("1404","deviceNumber不能为空"));
			}else if(StringUtils.isBlank(mid) || StringUtils.isBlank(phone)){
				return new JsonRepresentation(new ValidateError("1401","商户Id和手机号不能为空"));
			}
			
			//take care of device_number here...
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("1405","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("1403","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
				return new JsonRepresentation(new ValidateError("1402","商户与设备不匹配"));
			}
				
			//get member by phone
			Shop shop = terminal.getShop();
			Member member = null;
			if(shop.getMerchant() != null && shop.getMerchant().getMemberCentralManagement()){
				List<Member> members = memberService.findMembersByMobileForMerchant(shop.getMerchant(), phone);
				if(CollectionUtils.isNotEmpty(members)){
					member = members.get(0);
				}
			}else{
				member = memberService.findMemberByMobileForShop(shop, phone);
			}
			if(member == null){
				return new JsonRepresentation(new ValidateError("1406","未找到相应会员信息"));
			}else{
				member = memberService.findMemberByIdWithAttributes(member.getId());
			}
			
			//拼装json
			JSONObject json = new JSONObject();
			json.put("mid", mid);
			json.put("res", "0");
			json.put("id", member.getId());
			json.put("name", member.getName());
			json.put("sex", member.getGender());
			json.put("mobile", member.getMobile());
			json.put("birthday", new DateTime(member.getBirthday()).toString("yyyy-MM-dd"));
			json.put("email", member.getEmail());
			
			
			MemberType type = member.getMemberType();
			JSONObject typeJson = new JSONObject();
			typeJson.put("id", type.getId());
			typeJson.put("name", type.getName());
			typeJson.put("discount", type.getDiscount().toString());
			json.put("memberType", typeJson);
			
			List<MemberAttribute> attributeList = member.getMemberType().getMemberAttributeTemplate().getMemberAttributeList();
			if(!CollectionUtils.isEmpty(attributeList)){
				JSONArray attrArray = new JSONArray();
				for(MemberAttribute attr : attributeList){
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("id", attr.getId());
					jsonObj.put("name", attr.getName());
					jsonObj.put("type", attr.getAttributeType().name());
					jsonObj.put("required", attr.isRequired());
					jsonObj.put("value", attr.getStoredValue());
					attrArray.put(jsonObj);
				}
				json.put("extendAttributes", attrArray);
			}
			
			re = new JsonRepresentation(json);
			return re;
		} catch (Exception e) {
			logger.error("Cannot get member info due to "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		
	}
	
}
