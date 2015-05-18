package com.xpos.common.service;

import com.xkeshi.pojo.vo.param.POSGatewayAccountParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.example.POSGatewayAccountExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.persistence.mybatis.POSGatewayAccountMapper;

@Service
public class POSGatewayAccountServiceImpl implements POSGatewayAccountService{
	private final static Logger logger = LoggerFactory.getLogger(POSGatewayAccountServiceImpl.class);
	
	@Autowired
	private POSGatewayAccountMapper posGatewayAccountMapper;
	
	@Autowired
	private ShopService shopService;

	@Override
	public POSGatewayAccount findByAccountAndType(String gatewayAccount, POSGatewayAccountType gatewayType) {
		POSGatewayAccountExample example = new POSGatewayAccountExample();
		example.createCriteria()
			.addCriterion("account = ", gatewayAccount)
			.addCriterion("type = '" + gatewayType.name() + "'")
			.addCriterion("deleted = ", false);
		return posGatewayAccountMapper.selectOneByExample(example);
	}

	@Override
	public POSGatewayAccount findByBusinessAndType(Business business, POSGatewayAccountType gatewayType) {
		POSGatewayAccountExample example = new POSGatewayAccountExample();
		example.createCriteria()
			.addCriterion("businessId = ", business.getSelfBusinessId())
			.addCriterion("businessType = '" + business.getSelfBusinessType() + "'")
			.addCriterion("type = '" + gatewayType.name() + "'")
			.addCriterion("deleted = ", false);
		return posGatewayAccountMapper.selectOneByExample(example);
	}
	
	@Override
	public boolean save(POSGatewayAccount account) {
		//校验参数非空
		if(account == null || StringUtils.isBlank(account.getTerminal()) 
				|| StringUtils.isBlank(account.getAccount()) || account.getType() == null){
			return false;
		}
		
		ShopInfo sif = shopService.findShopInfoByShopId(account.getBusinessId());
		if(sif == null) {
			ShopInfo shopInfo = new ShopInfo ();
			shopInfo.setShopId(account.getBusinessId());
			shopService.saveOrUpdateShopInfo(shopInfo);
		}
		POSGatewayAccountExample example = new POSGatewayAccountExample();
		example.createCriteria().addCriterion("businessId = ", account.getBusinessId())
			.addCriterion("businessType = 'SHOP'")
			.addCriterion("type='" + account.getType() + "'")
			.addCriterion("terminal='"+account.getTerminal()+"'")
			.addCriterion("deleted=",false);
		POSGatewayAccount act = posGatewayAccountMapper.selectOneByExample(example);
		if(act != null){
			//一个终端只能绑定一个账号
			return false;
		}
		
		return posGatewayAccountMapper.insert(account) == 1;
		
	}

	@Override
	public boolean update(POSGatewayAccount account) {
		//valid
		if(account == null || StringUtils.isBlank(account.getTerminal()) 
				|| StringUtils.isBlank(account.getAccount()) || account.getType() == null){
			return false;
		}
		POSGatewayAccountExample example = new POSGatewayAccountExample();
		example.createCriteria().addCriterion("businessId = ", account.getBusinessId())
			.addCriterion("businessType = 'SHOP'")
			.addCriterion("account = '"+ account.getAccount()+"'")
			.addCriterion("type='" + account.getType() + "'")
			.addCriterion("terminal='"+account.getTerminal()+"'")
			.addCriterion("id !=", account.getId())
			.addCriterion("deleted=",false);
		POSGatewayAccount accounts = posGatewayAccountMapper.selectOneByExample(example);
		if(accounts != null){
			return false;
		}
		
		return posGatewayAccountMapper.updateByPrimaryKey(account) == 1;
	}


	@Override
	public boolean deleteByShopId(Long shopId, Long accountId) {
		POSGatewayAccountExample example = new POSGatewayAccountExample();
		example.createCriteria().addCriterion("businessId = ", shopId)
		.addCriterion("businessType = 'SHOP'")
		.addCriterion("id = ", accountId)
		.addCriterion("deleted = ",false);
		POSGatewayAccount account = posGatewayAccountMapper.selectOneByExample(example);
		if(account != null){
			account.setDeleted(true);
			return posGatewayAccountMapper.updateByPrimaryKey(account) == 1;
		}
		return false;
	}

    @Override
    public POSGatewayAccount findBankCardPosGatewayAccount(Long id) {
        POSGatewayAccountExample example = new POSGatewayAccountExample();
        example.createCriteria()
                .addCriterion("businessId = ", id)
                .addCriterion("businessType = 'SHOP'")
                .addCriterion("type in ('UMPAY','SHENGPAY','BOC')")
                .addCriterion("deleted = ", false);
        POSGatewayAccount posGatewayAccount = posGatewayAccountMapper.selectOneByExample(example);
        if(posGatewayAccount == null){
            POSGatewayAccount po = new POSGatewayAccount();
            po.setBusinessId(id);
            po.setBusinessType(Business.BusinessType.SHOP);
            po.setType(POSGatewayAccountType.SHENGPAY);
            po.setEnable(0);
            posGatewayAccountMapper.insert(po);
            return po;
        }
        return posGatewayAccount;
    }

    @Override
    public POSGatewayAccount findByShopIdAndType(Long id, POSGatewayAccountType type) {
        POSGatewayAccountExample example = new POSGatewayAccountExample();
        example.createCriteria()
                .addCriterion("businessId = ", id)
                .addCriterion("businessType = 'SHOP'")
                .addCriterion("type ='" + type + "'")
                .addCriterion("deleted = ", false);
        POSGatewayAccount posGatewayAccount = posGatewayAccountMapper.selectOneByExample(example);
        if(posGatewayAccount == null){
            POSGatewayAccount po = new POSGatewayAccount();
            po.setBusinessId(id);
            po.setBusinessType(Business.BusinessType.SHOP);
            po.setType(type);
            po.setEnable(0);
            posGatewayAccountMapper.insert(po);
            return po;
        }
        return posGatewayAccount;
    }

    @Override
    public boolean saveOrUpdate(POSGatewayAccountParam param) {
        POSGatewayAccountExample example = new POSGatewayAccountExample();
        example.createCriteria().addCriterion("businessId = ", param.getShopId())
                .addCriterion("businessType = 'SHOP'")
                .addCriterion("type = ", param.getType())
                .addCriterion("deleted = ",false);
        POSGatewayAccount account = posGatewayAccountMapper.selectOneByExample(example);
        boolean flag;
        POSGatewayAccount po = new POSGatewayAccount();
        po.setAccount(param.getAccount());
        po.setType(POSGatewayAccountType.valueOf(param.getType()));
        po.setSignKey(param.getSignKey().trim());
        if(param.getEnableWX()!=null){
            po.setEnable(param.getEnableWX());
        }
        if(param.getEnableZFB()!=null){
            po.setEnable(param.getEnableZFB());
        }
        if(param.getEnableYHK()!=null){
            po.setEnable(param.getEnableYHK());
        }
        if(StringUtils.isNotEmpty(param.getTerminal())){
            po.setTerminal(param.getTerminal().trim());
        }
        if (account != null){
            po.setId(account.getId());
            flag = posGatewayAccountMapper.updateByPrimaryKey(po) > 0;
        }
        else{
            po.setBusinessId(param.getShopId());
            po.setBusinessType(Business.BusinessType.SHOP);
            flag = posGatewayAccountMapper.insert(po) > 0;
        }
        return flag;
    }

    @Override
    public boolean updateBandCard(POSGatewayAccountParam param) {
        POSGatewayAccount account = findBankCardPosGatewayAccount(param.getShopId());
        boolean flag;
        POSGatewayAccount po = new POSGatewayAccount();
        po.setAccount(param.getAccount());
        po.setType(POSGatewayAccountType.valueOf(param.getType()));
        po.setSignKey(param.getSignKey().trim());
        if(param.getEnableWX()!=null){
            po.setEnable(param.getEnableWX());
        }
        if(param.getEnableZFB()!=null){
            po.setEnable(param.getEnableZFB());
        }
        if(param.getEnableYHK()!=null){
            po.setEnable(param.getEnableYHK());
        }
        po.setTerminal(param.getTerminal().trim());
        if (account != null){
            po.setId(account.getId());
            flag = posGatewayAccountMapper.updateByPrimaryKey(po) > 0;
        }
        else{
            po.setBusinessId(param.getShopId());
            po.setBusinessType(Business.BusinessType.SHOP);
            flag = posGatewayAccountMapper.insert(po) > 0;
        }
        return flag;
    }

}
