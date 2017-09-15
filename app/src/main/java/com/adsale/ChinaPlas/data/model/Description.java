package com.adsale.ChinaPlas.data.model;

public class Description {
	public String descriptionEng;
	public String descriptionTC;
	public String descriptionSC;
	
	
	public String getDescription(int language) {
		String description="";
		if(language==1){
			description=descriptionEng;
		}else if(language==2){
			description=descriptionSC;
		}else{
			description=descriptionTC;
		}
		return description;
	}
	
	
	@Override
	public String toString() {
		return "Description [descriptionEng=" + descriptionEng + ", descriptionTC=" + descriptionTC + ", descriptionSC=" + descriptionSC + "]";
	}
	
	

}
