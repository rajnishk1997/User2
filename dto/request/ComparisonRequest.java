package com.optum.dto.request;

import java.util.List;

import com.optum.entity.GppSheet;

public class ComparisonRequest {
	 private int sotJsonId;
	    private int gppJsonId;
	    private GppSheet gppSheet;
		public int getSotJsonId() {
			return sotJsonId;
		}
		public void setSotJsonId(int sotJsonId) {
			this.sotJsonId = sotJsonId;
		}
		public int getGppJsonId() {
			return gppJsonId;
		}
		public void setGppJsonId(int gppJsonId) {
			this.gppJsonId = gppJsonId;
		}
		public GppSheet getGppSheet() {
			return gppSheet;
		}
		public void setGppSheet(GppSheet gppSheet) {
			this.gppSheet = gppSheet;
		}
	    
	    

  
}

