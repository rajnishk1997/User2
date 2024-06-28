package com.optum.dto;

public class SotGppRenameFieldsMappingDto {

	 private int sotRid;
	    private int gppRid;
	    private String sotGppRemark;
	    private String gppSheet;
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
		public String getGppSheet() {
			return gppSheet;
		}
		public void setGppSheet(String gppSheet) {
			this.gppSheet = gppSheet;
		}
		public int getCurrentUserId() {
			return currentUserId;
		}
		public void setCurrentUserId(int currentUserId) {
			this.currentUserId = currentUserId;
		}

}
