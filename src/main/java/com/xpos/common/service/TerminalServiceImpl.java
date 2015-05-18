package com.xpos.common.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.xkeshi.utils.Tools;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.Terminal.TerminalType;
import com.xpos.common.entity.example.OperatorExample;
import com.xpos.common.entity.example.TerminalExample;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.persistence.mybatis.OperatorMapper;
import com.xpos.common.persistence.mybatis.POSOperationLogMapper;
import com.xpos.common.persistence.mybatis.ShopMapper;
import com.xpos.common.persistence.mybatis.TerminalMapper;
import com.xpos.common.utils.Pager;

@Service
public class TerminalServiceImpl implements TerminalService{
	
	@Resource
	private OperatorMapper operatorMapper;
	
	@Resource
	private TerminalMapper terminalMapper;
	
	@Resource
	private ShopMapper shopMapper;
	
	@Resource
	private OperatorShiftService  operatorShiftService  ;
	
	@Resource
	private POSOperationLogMapper posOperationLogMapper;

	@Override
	public List<Operator> findOperatorsByShopId(Long id) {

		OperatorExample example = new OperatorExample();
		example.createCriteria().addCriterion("deleted=", false)
								.addCriterion("shop_id=",id);
		return operatorMapper.selectByExample(example, null);
	}

	@Override
	public List<Terminal> findTerminalsByShopId(Long id) {
		TerminalExample example = new TerminalExample();
		example.createCriteria().addCriterion("deleted=", false)
		.addCriterion("shop_id=",id);
		return terminalMapper.selectByExample(example, null);
	}

	@Override
	public Pager<Terminal> findTerminalList(Pager<Terminal> pager,
			TerminalExample example) {
		if(example == null)
			example = new TerminalExample();
		example.appendCriterion("deleted=", false);
		List<Terminal> list = terminalMapper.selectByExample(example, pager);
		int totalCount = terminalMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		return pager;
	}
	
	public String addTerminalByShopId(Long shopId,Terminal terminal)  {
		TerminalExample example = new TerminalExample();
		example.createCriteria().addCriterion("shop_id =", shopId)
								.addCriterion("deleted=",false);
		List<Terminal> terminalList = terminalMapper.selectByExample(example, null);
		if(CollectionUtils.isEmpty(terminalList)){ //商户下没有已绑定的设备
			terminal.setShop(shopMapper.selectByPrimaryKey(shopId));
			if(TerminalType.CASHIER.equals(terminal.getTerminalType())){
				terminal.setCode("A");
			}
		}else{
			for(Terminal ter : terminalList){
				if(StringUtils.equals(ter.getDeviceNumber(), terminal.getDeviceNumber())){
					return "该设备码已被绑定";
				}
			}
			terminal.setShop(shopMapper.selectByPrimaryKey(shopId));
			if(TerminalType.CASHIER.equals(terminal.getTerminalType())){
				terminal.setCode(generateTerminalCode(terminalList));
			}
		}
		//2015-3-11 14:22:22 后台绑定设备时增加deviceSecret   writed by snoopy
		if(StringUtils.isBlank(terminal.getDeviceSecret())) {
			terminal.setDeviceSecret(Tools.getUUID());
		}
		return terminalMapper.insert(terminal)>0?null:"设备码添加失败";
	}
	
	public boolean removeTerminalById(Terminal terminal) {
		terminal.setDeleted(true);
		return terminalMapper.updateByPrimaryKey(terminal)==1;
	}

	@Override
	public Terminal findTerminalByDevice(String deviceNumber) {
		if(StringUtils.isBlank(deviceNumber)){
			return null;
		}
		TerminalExample example = new TerminalExample();
		example.createCriteria().addCriterion("deviceNumber=", deviceNumber)
								.addCriterion("deleted=", false);
		return terminalMapper.selectOneByExample(example);
	}

	@Override
	public boolean updateLastLoginDate(Long id, Date date) {
		Terminal terminal = new Terminal();
		terminal.setId(id);
		terminal.setLastLogin(date);
		return terminalMapper.updateByPrimaryKey(terminal) == 1;
	}

	@Override
	public boolean appendOperationRecord(POSOperationLog operationLog) {
		return posOperationLogMapper.insert(operationLog) == 1;
		
	}

	@Override
	public Terminal findTerminalsByTerminalId(Long terminalId) {
		return terminalMapper.selectByPrimaryKey(terminalId);
	}
	
	private String generateTerminalCode(List<Terminal> terminalList) {
		Set<String> codeSet = new TreeSet<>();
		for(Terminal ter : terminalList){ //遍历已有设备编号，放入set
			if(TerminalType.CASHIER.equals(ter.getTerminalType())){
				codeSet.add(ter.getCode());
			}
		}
		
		for(char i = 0; i < 676; i++){ //遍历A-ZZ
			String code = null;
			if(i < 26){
				code = "" + (char)(i + 65);
			}else{
				code = "" + (char)(i/26 + 64) + (char)(i%26 + 65);
			}
			
			if(!codeSet.contains(code)){
				return code;
			}
		}
		return null;
	}

}
