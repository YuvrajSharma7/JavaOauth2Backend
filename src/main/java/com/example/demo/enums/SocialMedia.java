package com.example.demo.enums;


public enum SocialMedia {

	GOOGLE(1,"Google"),FACEBOOK(2,"Facebook");
	
	private Integer id;
	private String displayName;
	
	SocialMedia(Integer id,String displayName){
		this.id = id;
		this.displayName=displayName;
	}
	
	public Integer getId() {
		return this.id;
	}
	

	
	public SocialMedia getById(Integer id) {
		for(SocialMedia socialMedia :SocialMedia.values()) {
			if(socialMedia.getId().equals(id)) {
				return socialMedia;
			}
		}
		return null;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
