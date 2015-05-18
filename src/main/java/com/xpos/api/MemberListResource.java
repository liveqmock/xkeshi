package com.xpos.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xpos.api.param.LatestRegisterMember;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.member.Member;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.service.TerminalService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.Pager;


public class MemberListResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(MemberListResource.class);
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private MemberService memberService;
	
	/**
	 * /merchant/{mid}/member/list?deviceNumber=xxx&registerDate=2013-01-01&page=1&pagesize=20
	 * @return
	 */
	@Get("json")
	public Representation getMemberList(){
		String mid = (String) getRequestAttributes().get("mid");
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String registerDate = getQuery().getFirstValue("registerDate");
		String pageTo = getQuery().getFirstValue("page");
		String pagesize = getQuery().getFirstValue("pagesize");
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("0401", "deviceNumber不能为空"));
		}
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("0402","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("0403","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
			return new JsonRepresentation(new ValidateError("0404","商户与设备不匹配"));
		}
		
		//Default display the first page
		Integer currentPage = (StringUtils.isBlank(pageTo) || !StringUtils.isNumeric(pageTo))? 1:Integer.parseInt(pageTo);
		if(currentPage <= 0){
			currentPage = 1;
		}
		//Default page size is 20
		Integer size = (StringUtils.isBlank(pagesize) || !StringUtils.isNumeric(pagesize))? 20:Integer.parseInt(pagesize);
		if(size <= 0){
			size = 20;
		}
		
		//parse start date
		DateTime registerDateTime = null;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		try{
			registerDateTime = fmt.parseDateTime(registerDate);
		}catch(Exception e){
			registerDateTime = new DateTime().withTimeAtStartOfDay().minusDays(7).toDateTime(); //默认查询最近一周
		}
		
		JsonRepresentation re = null;
		Pager<Member> pager = new Pager<>();
		pager.setPageSize(size);
		pager.setPageNumber(currentPage);
		List<LatestRegisterMember> memberList = new ArrayList<>();
		try{
			MemberSearcher searcher = new MemberSearcher();
			searcher.setCreateStartDate(registerDateTime.toDate());
			memberService.findMembersByBusiness(terminal.getShop(), pager, searcher);
			if(CollectionUtils.isNotEmpty(pager.getList())){
				for(Member member : pager.getList()){
					LatestRegisterMember latestMember = new LatestRegisterMember();
					latestMember.setId(member.getId());
					latestMember.setName(member.getName());
					latestMember.setPhone(member.getMobile());
					latestMember.setRegisterDate(new DateTime(member.getCreateDate()).toString("yyyy-MM-dd"));
					memberList.add(latestMember);
				}
			}
			re = new JsonRepresentation(ResCode.General.OK);
			JSONObject json = re.getJsonObject();
			json.put("total", pager.getTotalCount());
			json.put("page", currentPage);
			json.put("pagesize", size);
			json.put("hasPrefix", pager.isForward());
			json.put("hasNext", pager.isNext());
			json.put("list", memberList);
			
		}catch(Exception e){
			logger.error("Cannot find latest register member due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	
}
