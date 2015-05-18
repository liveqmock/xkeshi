package com.xpos.controller.api;

import com.xkeshi.pojo.vo.MemberTofuVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.service.PrepaidService;
import com.xkeshi.service.XMemberService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xpos.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 会员API controller
 * The type API member controller.
 */
@Controller
@RequestMapping("api/")
public class APIMemberController extends BaseController {


	@Autowired
	private PrepaidService prepaidService;


    @Autowired
    private XMerchantService xMerchantService;

    @Autowired
    private XShopService xShopService;

    @Autowired
    private XMemberService xMemberService;


    /**
     *
     * 获取会员登录后的豆腐块信息
     * Gets member tofu info.
     *
     * @param param the param
     * @param mobileNumber the mobile number
     * @return the member tofu info
     */
    @ResponseBody
    @RequestMapping(value = "member/{mobileNumber}",method = RequestMethod.GET)
    public Result getMemberTofuInfo(@ModelAttribute SystemParam param,
                                    @PathVariable("mobileNumber") String mobileNumber) {

        Result result = new Result( "会员登录后会员信息","0");

        MemberTofuVO memberTofu = xMemberService.getMemberTofuInfo(mobileNumber,param.getMid());

        if(memberTofu == null){
        	result = new Result("会员不存在","1001");
        }
        result.setResult(memberTofu);
        

        return result;
    }





}
