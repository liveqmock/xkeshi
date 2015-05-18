package com.xpos.api;

import org.apache.commons.lang3.StringUtils;

public abstract class CacheableResource extends BaseResource{
	
	//@Autowired
	//private Cache idempotenceCache;
	
	/**
	 * Cache the entity with key = 'ticket'
	 * @param ticket
	 * @param entity
	 */
	protected void cache(String ticket, String value){
		if(StringUtils.isNotBlank(ticket)){
			//idempotenceCache.put(new Element(ticket, value));
		}
	}
	
	protected boolean isCached(String ticket){
		if(StringUtils.isNotBlank(ticket)){
			//return idempotenceCache.get(ticket) != null;
			return false;
		}else{
			return false;
		}
	}
	
	protected String getCachedEntity(String ticket){
		if(isCached(ticket)){
			//Element element = idempotenceCache.get(ticket);
			//return (String)element.getObjectValue();
			return null;
		}else{
			return null;
		}
	}

}
