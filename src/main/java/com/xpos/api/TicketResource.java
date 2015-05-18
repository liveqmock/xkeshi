package com.xpos.api;


import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class TicketResource extends BaseResource{
	/**
	 * API 0: 为了解决HTTP的post和put的幂等性问题，每次提交数据之前都需要通过这个接口来获取全局唯一的ticket
	 * @return
	 */
	@Get("json")
	public Representation newTicket(){
		return new JsonRepresentation(StringUtils.join("{\"ticket\":\"",UUID.randomUUID().toString(),"\"}"));
	}
}
