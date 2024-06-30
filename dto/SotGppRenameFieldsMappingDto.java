package com.optum.dto;

public class SotGppRenameFieldsMappingDto {

	 private int sotRid;
	    private int gppRid;
	    private String sotGppRemark;
	    private Long gppsheetRid;
	    private int currentUserId;
		public int getSotRid() {
			return sotRid;
		}
		public void setSotRid(int sotRid) {
			this.sotRid = sotRid;
		}
		public int getGppRid() {
			return gppRid;
		}
		public void setGppRid(int gppRid) {
			this.gppRid = gppRid;
		}
		public String getSotGppRemark() {
			return sotGppRemark;
		}
		public void setSotGppRemark(String sotGppRemark) {
			this.sotGppRemark = sotGppRemark;
		}
		public int getCurrentUserId() {
			return currentUserId;
		}
		public void setCurrentUserId(int currentUserId) {
			this.currentUserId = currentUserId;
		}
		public Long getGppsheetRid() {
			return gppsheetRid;
		}
		public void setGppsheetRid(Long gppsheetRid) {
			this.gppsheetRid = gppsheetRid;
		}

}
