package com.xpos.common.service;

import java.io.IOException;
import java.util.List;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.example.MerchantExample;
import com.xpos.common.entity.security.Account;
import com.xpos.common.utils.Pager;


public interface MerchantService {
	
	public boolean update(Merchant merchant);
	
	public List<Merchant>  findAllMerchant( MerchantExample example);
	
	public Merchant  findMerchant (Long id);
	
	public Pager<Merchant>  findMerchants(MerchantExample example  , Pager<Merchant> pager );

	public boolean saveMerchant(Merchant merchant, Account account) throws IOException;
	
	//public boolean updateMerchant(Merchant merchant, Account account) throws IOException;
	
	public boolean deleteMerchant (Long merchantId);
	
	public boolean deleteMerchants ( Long[] merchantIds);

	boolean batchSetVisible(Long[] ids, boolean visible);

	boolean updateMerchant(Merchant merchant, com.xkeshi.pojo.po.Account account)
			throws IOException;
	
}
