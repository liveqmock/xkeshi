package com.xpos.api.result;

public interface ResCode {
	enum General implements ResCode{
		OK("0","OK"),
		SERVER_INTERNAL_ERROR("-1","服务器内部错误"),
		PARAMS_NOT_MATCHED("-2","参数不匹配");
		

		private General(String res, String description){
			setRes(res);
			setDescription(description);
		}
		
		private String res;
		private String description;
		
		@Override
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		public String toString(){
			StringBuffer bf = new StringBuffer("{\"res\":\"");
			bf.append(getRes()).append("\",\"description\":\"").append(getDescription()).append("\"}");
			return bf.toString();
		}
		public String getRes() {
			return res;
		}

		public void setRes(String res) {
			this.res = res;
		}
		
	}
	
	public String getDescription();
	public String getRes();
}
