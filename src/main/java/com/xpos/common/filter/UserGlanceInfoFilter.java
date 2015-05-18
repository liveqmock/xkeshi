package com.xpos.common.filter;
 /**
  * 暂存浏览用户的地理位置信息
  * @author xk
  *
  */
public class UserGlanceInfoFilter {
	
	   //通过浏览器获取到的用户的地理信息(经纬度)
		private String     provinceCode;
		private String     cityCode; 
		private String     districtCode ;
		private double  userLat ;
		private double  userLng ;
		
		public UserGlanceInfoFilter(){
			
		}
		
		public UserGlanceInfoFilter(double userLat, double userLng,
				String districtCode, String cityCode, String provinceCode) {
			super();
			this.userLat = userLat;
			this.userLng = userLng;
			this.districtCode = districtCode;
			this.cityCode = cityCode;
			this.provinceCode = provinceCode;
		}
		public double getUserLat() {
			return userLat;
		}
		public void setUserLat(double userLat) {
			this.userLat = userLat;
		}
		public double getUserLng() {
			return userLng;
		}
		public void setUserLng(double userLng) {
			this.userLng = userLng;
		}

		public String getProvinceCode() {
			return provinceCode;
		}

		public void setProvinceCode(String provinceCode) {
			this.provinceCode = provinceCode;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}

		public String getDistrictCode() {
			return districtCode;
		}

		public void setDistrictCode(String districtCode) {
			this.districtCode = districtCode;
		}
	
		 
		 
		
}
