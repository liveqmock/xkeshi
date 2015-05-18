package com.xkeshi.pojo.vo.shopPrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author snoopy
 *
 */
public class ShopPrinterListVO{
	
	private List<ShopPrinterVO> printers = new ArrayList<ShopPrinterVO>();
	
	public ShopPrinterListVO(){
		
	}
	
	public ShopPrinterListVO(List<ShopPrinterVO> printers){
		this.printers = printers;
	}

	public List<ShopPrinterVO> getPrinters() {
		return printers;
	}

	public void setPrinters(List<ShopPrinterVO> printers) {
		this.printers = printers;
	}

	

}