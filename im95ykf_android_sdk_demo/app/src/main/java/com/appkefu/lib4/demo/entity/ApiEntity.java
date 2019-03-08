package com.appkefu.lib4.demo.entity;

public class ApiEntity {

	private int id;
	private String apiName;
	private String unreadMessageCounts;

	public ApiEntity(int id, String apiName) {
		super();
		this.id = id;
		this.apiName = apiName;
		this.unreadMessageCounts="0";
	}

	public int getId() {
		return id;
	} 
	  
	public void setId(int id) {
		this.id = id;
	}
	
	public String getApiName() {
		return apiName;
	}
	
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getUnreadMessageCounts() {
		return unreadMessageCounts;
	}

	public void setUnreadMessageCounts(String unreadMessageCounts) {
		this.unreadMessageCounts = unreadMessageCounts;
	}
	
}
