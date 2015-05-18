package com.xpos.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.searcher.POSTransactionSearcher;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("pos_transaction")
public class POSTransactionController  extends  BaseController{

   
	@Resource
	private POSTransactionService   posTransactionService;
	
	@Resource
	private ShopService shopService;
	
	
	/**
	 * pos交易流水详情列表
	 */
	@RequestMapping(value="bank_card/list")
	public String  POSTransactionList( Pager<POSTransaction>  pager , POSTransactionSearcher   searcher,   Model  model){
		searcher.setType(POSTransactionType.BANK_CARD);
		pager = posTransactionService.findTransactions(getBusiness(), searcher, pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			Map<String, Business> applicableShops = new HashMap<>();
			List<Shop> shopList = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			for(Shop shop : shopList){
				applicableShops.put(shop.getId().toString(), shop);
			}
			model.addAttribute("applicableShops", applicableShops);
		}
		return "pos_transaction/pos_transaction_list";
	}
	
	/**
	 * 电子券交易流水
	 * TODO 电子券功能一直未启用，macro.ftl屏蔽入口，重构时考虑删除
	@RequestMapping(value="cmcc_ticket/list")
	public String  CMCCTicketList(Pager<POSTransaction> pager, POSTransactionSearcher searcher, Model model){
		searcher.setType(POSTransactionType.CMCC_TICKET);
		pager = posTransactionService.findTransactions(getBusiness(), searcher, pager);
		model.addAttribute("pager", pager);
		
		//消费统计
		Set<POSTransactionStatus> originalStatusSet = searcher.getStatusSet() == null ? null : new HashSet<>(searcher.getStatusSet());
		Set<POSTransactionStatus> statusSet = null;
		if(originalStatusSet == null || originalStatusSet.contains(POSTransactionStatus.PAID_SUCCESS)){
			statusSet = new HashSet<>();
			statusSet.add(POSTransactionStatus.PAID_SUCCESS);
			searcher.setStatusSet(statusSet);
			String[] statistic = posTransactionService.getCMCCTicketStatistic(getBusiness(), searcher);
			model.addAttribute("success_count", statistic[0]);
			model.addAttribute("success_amount", statistic[1]);
		}
		if(originalStatusSet == null || originalStatusSet.contains(POSTransactionStatus.PAID_FAIL)){
			statusSet = new HashSet<>();
			statusSet.add(POSTransactionStatus.PAID_FAIL);
			searcher.setStatusSet(statusSet);
			String[] statistic = posTransactionService.getCMCCTicketStatistic(getBusiness(), searcher);
			model.addAttribute("fail_count", statistic[0]);
			model.addAttribute("fail_amount", statistic[1]);
		}
		if(originalStatusSet == null || originalStatusSet.contains(POSTransactionStatus.UNPAID)){
			statusSet = new HashSet<>();
			statusSet.add(POSTransactionStatus.UNPAID);
			searcher.setStatusSet(statusSet);
			String[] statistic = posTransactionService.getCMCCTicketStatistic(getBusiness(), searcher);
			model.addAttribute("unpaid_count", statistic[0]);
			model.addAttribute("unpaid_amount", statistic[1]);
		}
		if(originalStatusSet == null || originalStatusSet.contains(POSTransactionStatus.PAID_REVOCATION)){
			statusSet = new HashSet<>();
			statusSet.add(POSTransactionStatus.PAID_REVOCATION);
			searcher.setStatusSet(statusSet);
			String[] statistic = posTransactionService.getCMCCTicketStatistic(getBusiness(), searcher);
			model.addAttribute("revocation_count", statistic[0]);
			model.addAttribute("revocation_amount", statistic[1]);
		}
		
		searcher.setStatusSet(originalStatusSet);
		model.addAttribute("searcher", searcher);
		return "pos_transaction/cmcc_ticket_list";
	}
	*/
	
	/**
	 * pos交易单笔详情
	 */
	@RequestMapping(value="/detail/{eid}" ,method  = RequestMethod.GET)
	public String POSTransactionDetail(@PathVariable("eid") String eid  , Model model ){
		Long id = IDUtil.decode(eid);
		POSTransaction posTransaction = posTransactionService.findTransactionById(id);
		if (posTransaction == null) {
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", "对不起该笔交易不存在");
		}else{
			model.addAttribute("posTransaction", posTransaction);
		}
		return "pos_transaction/pos_transaction_detail";
	}
	
	
	/**
	 * 支付宝扫码付款流水详情列表
	 */
	@RequestMapping(value="alipay_qrcode/list")
	public String  AlipayQRCodeList( Pager<POSTransaction> pager , POSTransactionSearcher searcher, Model model){
		searcher.setType(POSTransactionType.ALIPAY);
		pager = posTransactionService.findTransactions(getBusiness(), searcher, pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			Map<String, Business> applicableShops = new HashMap<>();
			List<Shop> shopList = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			for(Shop shop : shopList){
				applicableShops.put(shop.getId().toString(), shop);
			}
			model.addAttribute("applicableShops", applicableShops);
		}
		return "pos_transaction/alipay_qrcode_list";
	}
	
	/**
	 * 支付宝扫码付单笔详情
	 */
	@RequestMapping(value="/detail/{eid}/alipay_qrcode" ,method  = RequestMethod.GET)
	public String POSTransactionDetailForAlipayQRCode(@PathVariable("eid") String eid  , Model model ){
		Long id = IDUtil.decode(eid);
		POSTransaction posTransaction = posTransactionService.findTransactionById(id);
		if (posTransaction == null) {
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", "对不起该笔交易不存在");
		}else{
			model.addAttribute("posTransaction", posTransaction);
		}
		return "pos_transaction/alipay_qrcode_detail";
	}
}
