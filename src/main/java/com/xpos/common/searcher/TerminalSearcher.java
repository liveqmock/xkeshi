package com.xpos.common.searcher;


import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.TerminalExample;

public class TerminalSearcher  extends AbstractSearcher<Terminal>  {
	
	private  String key ;
    
	 private Date lastStartLogin   ;
	 
	 private Date lastEndLogin ;
	 
	 public boolean getHasParameter(){
			return StringUtils.isNotBlank(key) || lastStartLogin != null || lastEndLogin !=null;
	}
	 
	@Override
	public Example<?> getExample() {
	     example  =  new TerminalExample();
	     example.setOrderByClause(" createDate DESC");
	     Criteria criteria = example.createCriteria();
	 	
	     //TODO 优化 by luxj
	     if(StringUtils.isNotEmpty(key) ){
	    	String sql  = "shop_id in (select id from Shop where name  like CONCAT('%', \""+key+"\", '%')";
	    		   sql += " or shop_id  like CONCAT('%', \""+key+"\", '%'))  or deviceNumber like CONCAT('%', \""+key+"\", '%')";
	    		   criteria.addCriterion(sql);
		}	
		if (lastStartLogin != null ) 
			criteria.addCriterion(" lastLogin >= ", new DateTime(lastStartLogin).toString("yyyy-MM-dd HH:mm:ss"));
		if ( lastEndLogin != null) 
			criteria.addCriterion(" lastLogin <=  ",new DateTime(lastEndLogin).toString("yyyy-MM-dd HH:mm:ss"));
	   return (TerminalExample)example;
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getLastStartLogin() {
		return lastStartLogin;
	}

	public void setLastStartLogin(Date lastStartLogin) {
		this.lastStartLogin = lastStartLogin;
	}

	public Date getLastEndLogin() {
		return lastEndLogin;
	}

	public void setLastEndLogin(Date lastEndLogin) {
		this.lastEndLogin = lastEndLogin;
	}
	 
  
}
