package com.xpos.controller;

import com.xkeshi.pojo.po.AlipayTransactionDetail;
import com.xkeshi.pojo.po.AlipayTransactionList;
import com.xkeshi.service.AlipayTransactionService;
import com.xpos.common.entity.face.Business;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by szw on 2015/3/9.
 */
@Controller
@RequestMapping("/alipay_transaction")
public class AlipayTransactionController extends BaseController{

    @Autowired
    private AlipayTransactionService alipayTransactionService;

    /**
     * 支付宝扫码付款流水详情列表
     */
    @RequestMapping(value="/alipay_qrcode/list", method = RequestMethod.GET)
    public String  AlipayQRCodeList(Pager<AlipayTransactionList> pager,  String key, AlipayTransactionList alipayTransactionList, Model model) {
        model.addAttribute("key", key);
        model.addAttribute("alipayTransaction", alipayTransactionList);
        Business business = getBusiness();
        String businessType =business.getSelfBusinessType().toString();
        alipayTransactionList.setBusinessId(business.getSelfBusinessId());
        pager = alipayTransactionService.AlipayQRCodeList(key,businessType, alipayTransactionList,pager);
        model.addAttribute("pager", pager);
        return "pos_transaction/alipay_qrcode_list";
    }

    /**
     * 支付宝扫码付单笔详情
     */
    @RequestMapping(value="/detail/{eid}/alipay_qrcode" ,method  = RequestMethod.GET)
    public String POSTransactionDetailForAlipayQRCode(@PathVariable("eid") String eid, Model model ){
        Long id = IDUtil.decode(eid);
        AlipayTransactionDetail alipayTransactionDetail = alipayTransactionService.findAlipayTransactionById(id);
        if (alipayTransactionDetail == null) {
            model.addAttribute("status", STATUS_FAILD);
            model.addAttribute("msg", "对不起该笔交易不存在");
        }else{
            model.addAttribute("alipayTransactionDetail", alipayTransactionDetail);
        }
        return "pos_transaction/alipay_qrcode_detail";
    }

}
