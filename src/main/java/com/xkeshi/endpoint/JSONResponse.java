package com.xkeshi.endpoint;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSON;

/**
 * This class has two properties: A status code and JSON string as the result
 * @author xk
 */
public  class JSONResponse {
		
		private HttpStatus  httpStatus  ;
		
		/**  json内容  */
		private String body;

		public JSONResponse(HttpStatus httpStatus, String body) {
			super();
			this.httpStatus = httpStatus;
			this.body = body;
		}
			
		public JSONResponse(Object objResult ) {
			super();
			this.httpStatus = HttpStatus.OK;
			this.body = toJson(objResult);
		}
		
		public JSONResponse(HttpStatus httpStatus, GrantResult objResult ) {
			super();
			this.httpStatus = httpStatus;
			this.body = toJson(objResult);
		}

		/**
		 * @param body The JSON 
		 */
		public JSONResponse(int responseStatus, String body) {
			super();
			this.httpStatus  =  HttpStatus.valueOf(responseStatus);
			this.body = body;
		}
		
		
		/**
		 * @return The JSON string value.
		 */
		public String getBody() {
			return body;
		}

		public HttpStatus getHttpStatus() {
			return httpStatus;
		}
	 

		/**
		 * Encode the object to JSON format string.
		 * @param source The object that you want to change to JSON string.
		 * @return The JSON encoded string.
		 * @throws IllegalStateException If the translation failed.
		 */
		public   String toJson(Object source) {
			   return JSON.toJSONString(source);
		}

		/**
		 * json to obj
		 * @return
		 */
		public   Object  toObject(){
			return  JSON.parse(body);
		}
		
		 
	}