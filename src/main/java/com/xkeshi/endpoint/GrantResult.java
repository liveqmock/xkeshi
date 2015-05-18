package com.xkeshi.endpoint;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
/**
 * 
 * @author xk
 * 响应
 */
public class GrantResult {
	
	@JsonProperty("status")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String status;
	
	@JsonProperty("description")
	@JsonSerialize(include = Inclusion.NON_NULL)
	private String description ;
	
	@JsonProperty("result")
	@JsonSerialize(include  = Inclusion.NON_NULL)
	private Object  result ;
	
	public GrantResult() {
		super();
	}

	public GrantResult(String status, String description) {
		super();
		this.status = status;
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
 
}
