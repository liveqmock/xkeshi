package com.xpos.controller.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.MemberTypeListVO;
import com.xkeshi.pojo.vo.MemberTypeVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.member.MemberTypeService;
import com.xpos.controller.BaseController;


/**
 * 会员类型相关API controller
 * The type API member controller.
 */
@Controller
@RequestMapping("api/member_type")
public class APIMemberTypeController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(APIMemberTypeController.class);
	private static final String IMG_PREFIX = "http://xpos-img.b0.upaiyun.com/";

	@Autowired
	private MemberTypeService memberTypeService;
	
	@Autowired
	private ShopService shopService;

	/** 查询某个商户的会员类型 */
	@ResponseBody
	@RequestMapping(value = "/list",method = RequestMethod.GET)
	public Result getMemberTypeListByShop(@ModelAttribute SystemParam param) {
		Result result = new Result("会员类型列表","0");
		
		List<MemberTypeVO> memberTypeList = new ArrayList<>();
		
		Shop shop = shopService.findShopByIdIgnoreVisible(param.getMid());
		try {
			if(shop.getMerchant() == null || Boolean.FALSE.equals(shop.getMerchant().getMemberCentralManagement())){
				//普通商户 或者 会员非统一管理的子商户
				List<ShopMemberType> shopMemberTypeList = memberTypeService.findShopMemberTypeListByShop(shop);
				if(CollectionUtils.isNotEmpty(shopMemberTypeList)){
					for(ShopMemberType shopMemberType : shopMemberTypeList){
						MemberTypeVO memberTypeVO = convertMemberType(shopMemberType);
						memberTypeList.add(memberTypeVO);
					}
				}
				result.setResult(new MemberTypeListVO(memberTypeList));
			}else if(Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
				// 会员统一管理的子商户
				List<MerchantMemberType> merchantMemberTypeList = memberTypeService.findMerchantMemberTypeListByMerchant(shop.getMerchant());
				if(CollectionUtils.isNotEmpty(merchantMemberTypeList)){
					for(MerchantMemberType merchantMemberType : merchantMemberTypeList){
						MemberTypeVO memberTypeVO = convertMemberType(merchantMemberType);
						memberTypeList.add(memberTypeVO);
					}
				}
				result.setResult(new MemberTypeListVO(memberTypeList));
			}
		} catch (Exception e) {
			logger.info("获取会员列表信息失败", e);
			result.setRes("1001");
			result.setDescription("请确保已初始化会员模块");
		}
		
		return result;
	}

	private MemberTypeVO convertMemberType(MemberType memberType) {
		MemberTypeVO memberTypeVO = new MemberTypeVO();
		memberTypeVO.setId(memberType.getId());
		memberTypeVO.setName(memberType.getName());
		memberTypeVO.setDiscount(memberType.getDiscount().multiply(new BigDecimal(10)).setScale(3, RoundingMode.HALF_UP));
		memberTypeVO.setDefault(memberType.isDefault());
		if(memberType.getCoverPicture() != null){
			memberTypeVO.setCoverPicture(IMG_PREFIX + memberType.getCoverPicture().toString());
		}
		return memberTypeVO;
	}
	
}
