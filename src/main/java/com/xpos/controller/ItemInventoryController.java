package com.xpos.controller;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeDetail;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeRecord;
import com.xpos.common.exception.GenericException;
import com.xpos.common.searcher.ItemInventoryChangeRecordSearcher;
import com.xpos.common.searcher.ItemSearcher;
import com.xpos.common.service.ItemInventoryService;
import com.xpos.common.service.ItemService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.DateUtil;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("inventory")
public class ItemInventoryController extends BaseController{
	
	@Resource
	private ItemService itemService;
	
	@Resource
	private ItemInventoryService itemInventoryService;
	
	@Resource
	private ShopService shopService;

	/**
	 * 普通商户勾选商品，跳转至商品批量出入库页面
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/batch/editlist", method=RequestMethod.GET)
	public String inventoryEditList(ItemSearcher searcher, Pager<Item> pager,  Model model){
		if(CollectionUtils.isEmpty(searcher.getIds())) {
			model.addAttribute("status",STATUS_FAILD);
			model.addAttribute("msg", "请勾选商品");
			return  "redirect:/item/list";
		}
		searcher.setBusiness(getBusiness());
		pager = itemService.searchItems(searcher, pager);
		model.addAttribute("searcher", searcher);
		model.addAttribute("items", pager);
		return "item/batch_edit_list";
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/item/list")
	@RequestMapping(value="/batch/add",method=RequestMethod.POST)
    public HttpEntity<String> inventoryEdit(ModelAndView mav,@RequestBody List<ItemInventoryChangeDetail> iicdList) {
		//校验批量出入库商品数量
		JSONObject jsonObj = new JSONObject();
		GrantResult grantResult = new GrantResult(FAILD, "操作失败");
		if(CollectionUtils.isEmpty(iicdList)) {
			jsonObj.put("status",STATUS_FAILD);
			jsonObj.put("msg", "请填写商品出入库数量");
			jsonObj.put("url","/inventory/batch/editlist");
		}else {
			Map<ItemInventoryChangeDetail,String> failMap = itemInventoryService.batchAdd(iicdList,getBusiness(),getAccount());
			if(failMap == null || failMap.size()==0){
				grantResult = new GrantResult(SUCCESS, "操作成功");
				jsonObj.put("status", STATUS_SUCCESS);
				jsonObj.put("msg", "批量出入库成功");
				jsonObj.put("url","/item/list");
			}else {
				jsonObj.put("status", STATUS_FAILD);
				JSONArray array = new JSONArray();
				for(Entry<ItemInventoryChangeDetail, String> entry : failMap.entrySet()){
					JSONObject obj = new JSONObject();
					obj.put("itemId", entry.getKey().getItem().getId());
					obj.put("itemName", entry.getKey().getItem().getName());
					obj.put("error", entry.getValue());
					array.add(obj);
				}
				jsonObj.put("list", array);
			}
		}
		grantResult.setResult(jsonObj);
		JSONResponse  jsonResponse = new JSONResponse(grantResult);;
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
		 
	}
	
	/**
	 * 集团或普通商户跳转至后台出入库统计页面
	 */
	@RequestMapping(value="/recordlist", method=RequestMethod.GET)
	public String inventoryRecordList(ItemInventoryChangeRecordSearcher searcher, Pager<ItemInventoryChangeRecord> pager,  Model model){
		searcher.setBusiness(getBusiness());
		pager = itemInventoryService.searchItemInventorys(searcher, pager);
		model.addAttribute("searcher", searcher);
		model.addAttribute("recordcount", pager.getTotalCount());
		model.addAttribute("records", pager);
		return "item/change_record_list";
	}
	
	/**
	 * 集团或普通商户跳转至后台出入库统计详情页面
	 */
	@RequestMapping(value="/detaillist/{id}", method=RequestMethod.GET)
	public String inventoryDetailList(@PathVariable(value="id") Long id,  Model model,@RequestParam(value="key", required=false) String key){
		ItemInventoryChangeRecord iicr = itemInventoryService.selectItemInventoryChangeRecordById(id);
		Shop shop = shopService.findShopByIdIgnoreVisible(iicr.getBusinessId());
		if(BusinessType.MERCHANT.equals(getBusiness().getSelfBusinessType())) {
			if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
				return null;
			}
		}else {
			if(!getBusiness().getSelfBusinessType().equals(iicr.getBusinessType()) || !getBusiness().getSelfBusinessId().equals(iicr.getBusinessId())) {
				return null;
			}
		}
		List<ItemInventoryChangeDetail> detailList = itemInventoryService.findDetaiListByRecordId(id,key);
		model.addAttribute("detailList", detailList);
		model.addAttribute("record", iicr);
		model.addAttribute("key", key);
		return "item/change_detail_list";
	}
	
	/** 导出EXCEL 【出入库记录】 */
	@RequestMapping(value="/record/export", method = RequestMethod.GET)
	public void exportItemInventroyRecord(ItemInventoryChangeRecordSearcher searcher, HttpServletResponse response) throws Exception{
		//generate Data
		Pager<ItemInventoryChangeRecord> pager = new Pager<>();
		searcher.setBusiness(getBusiness());
		pager = itemInventoryService.searchItemInventorys(searcher, pager);
		if(pager == null)
			throw new Exception("导出数据出错");
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("出入库记录");
		// 第三步，在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 生成一个字体  
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式  
		style.setFont(font); 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		sheet.setColumnWidth(0, 5000);  
	    sheet.setColumnWidth(1,	5000);  
	    sheet.setColumnWidth(2, 3000);  
	    sheet.setColumnWidth(3, 3000);  
	    sheet.setColumnWidth(4, 5000);  
		
		//生成首行（表头）
		HSSFCell cell = row.createCell(1);
		cell.setCellStyle(style);
		HSSFRichTextString text = new HSSFRichTextString("出入库记录,共计"+pager.getList().size()+"笔出入库记录");
		cell.setCellValue(text);
		
		//生成第二行（标题行）
		row = sheet.createRow(1);
		String[] headers = new String[5];
		if(getBusiness().getSelfBusinessType().equals(BusinessType.MERCHANT)) {
			headers = new String[]{"操作时间", "出/入库商品种类", "出库数量","入库数量","操作商户/操作员"};
		}else {
			headers = new String[]{"操作时间", "出/入库商品种类", "出库数量","入库数量","操作员"};
		}
		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);  
			text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		// 第五步，写入实体数据
		if(!CollectionUtils.isEmpty(pager.getList())){
			int index = 2; //从第三行开始输出
			for(ItemInventoryChangeRecord iicr : pager.getList()){
				row = sheet.createRow(index++);
				for(int j = 0; j < 5; j++){
					cell = row.createCell(j);
					switch(j){
						case 0:
							cell.setCellValue(DateUtil.getDate(iicr.getCreatedDate(),"yyyy-MM-dd HH:mm:ss"));break;
						case 1:
							cell.setCellValue(iicr.getExportItemQuantity()+"/"+iicr.getImportItemQuantity());break;
						case 2:
							cell.setCellValue(iicr.getExportTotalQuantity());break;
						case 3:
							cell.setCellValue(iicr.getImportTotalQuantity());break;
						case 4:
							if(getBusiness().getSelfBusinessType().equals(BusinessType.MERCHANT)) {
								cell.setCellValue(iicr.getShop().getName()+"/"+iicr.getAccount().getUsername());break;
							}else {
								cell.setCellValue(iicr.getAccount().getUsername());break;
							}
					}
				}
			}
		}
		
		// 第六步，将文件存到指定位置
		try {
			String fileName = "ItemInventoryChangeRecord(" + DateUtil.getDate(new Date(),"yyyy-MM-dd HH:mm:ss"+")");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("商户出入库导出EXCEL失败！", e);
			throw new GenericException("商户库存出入库EXCEL失败");
		}
	}
	
	/** 导出EXCEL 【出入库详细记录】 */
	@RequestMapping(value="/detail/export/{id}", method = RequestMethod.GET)
	public void exportItemInventroyDetail(@PathVariable(value="id") Long id,@RequestParam(value="key", required=false) String key,
			HttpServletResponse response) throws Exception{
		//generate Data
		ItemInventoryChangeRecord iicr = itemInventoryService.selectItemInventoryChangeRecordById(id);
		Shop shop = shopService.findShopByIdIgnoreVisible(iicr.getBusinessId());
		if(BusinessType.MERCHANT.equals(getBusiness().getSelfBusinessType())) {
			if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
				throw new Exception("没有权限导出数据");
			}
		}else {
			if(!getBusiness().getSelfBusinessType().equals(iicr.getBusinessType()) || !getBusiness().getSelfBusinessId().equals(iicr.getBusinessId())) {
				throw new Exception("没有权限导出数据");
			}
		}
		List<ItemInventoryChangeDetail> detailList = itemInventoryService.findDetaiListByRecordId(id,key);
		if(iicr == null || detailList == null) {
			throw new Exception("导出数据出错");
		}
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("出入库记录明细");
		// 第三步，在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 生成一个字体  
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式  
		style.setFont(font); 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		sheet.setColumnWidth(0, 3000);  
	    sheet.setColumnWidth(1,	5000);  
	    sheet.setColumnWidth(2, 3000);  
	    sheet.setColumnWidth(3, 5000);  
	    sheet.setColumnWidth(4, 3000);  
	    sheet.setColumnWidth(5, 3000);  
	    sheet.setColumnWidth(6, 3000);  
		
		//生成首行(首行包含3行)（表头）
		HSSFCell cell = row.createCell(1);
		cell.setCellStyle(style);
		HSSFRichTextString text = new HSSFRichTextString("出入库记录明细");
		cell.setCellValue("出入库记录明细");
		
		row = sheet.createRow(1);
		cell = row.createCell(1);
		cell.setCellValue("操作时间："+DateUtil.getDate(iicr.getCreatedDate(),"yyyy-MM-dd HH:mm:ss"));
		
		row = sheet.createRow(2);
		cell = row.createCell(1);
		cell.setCellValue("操作员："+iicr.getAccount().getUsername());
		
		row = sheet.createRow(3);
		cell = row.createCell(1);
		cell.setCellValue("操作商户："+iicr.getShop().getName());
		
		
		
		
		//生成第二行（标题行）
		row = sheet.createRow(4);
		String[] headers = new String[5];
			headers = new String[]{"排列序号", "商品", "单价","分类","操作前库存","入库/出库","操作后库存"};
		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);  
			text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		// 第五步，写入实体数据
		if(!CollectionUtils.isEmpty(detailList)){
			int index = 5; //从第三行开始输出
			for(ItemInventoryChangeDetail iicd : detailList){
				row = sheet.createRow(index++);
				for(int j = 0; j < 7; j++){
					cell = row.createCell(j);
					switch(j){
						case 0:
							cell.setCellValue(iicd.getItem().getSequence()==null?50:iicd.getItem().getSequence());
							cell.setCellStyle(style);  break;
						case 1:
							cell.setCellValue(iicd.getItem().getName());
							cell.setCellStyle(style);  break;
						case 2:
							cell.setCellValue(iicd.getItem().getPrice().toString()+"元");
							cell.setCellStyle(style);  break;
						case 3:
							cell.setCellValue(iicd.getItem().getCategory().getName());
							cell.setCellStyle(style);  break;
						case 4:
							cell.setCellValue(iicd.getBeforeChangeQuantity());
							cell.setCellStyle(style);  break;
						case 5:
							if(iicd.isInventoryType()) {
								cell.setCellValue("+"+iicd.getQuantity());
								cell.setCellStyle(style);  break;
							}else {
								cell.setCellValue("-"+iicd.getQuantity());
								cell.setCellStyle(style);  break;
							}
						case 6:
							cell.setCellValue(iicd.getAfterChangeQuantity());
							cell.setCellStyle(style);  break;
					}
				}
			}
		}
		
		// 第六步，将文件存到指定位置
		try {
			String fileName = "ItemInventoryChangeDetail(" + DateUtil.getDate(new Date(),"yyyy-MM-dd HH:mm:ss"+")");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error(iicr.getShop().getName()+"商户出入库明细导出EXCEL失败！", e);
			throw new GenericException(iicr.getShop().getName()+"商户库存出入库EXCEL失败");
		}
	}
	
	
	
}
