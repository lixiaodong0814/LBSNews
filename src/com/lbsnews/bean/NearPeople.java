package com.lbsnews.bean;

import java.io.Serializable;

public class NearPeople implements Serializable {
	private int id;
	private String userPicStr;
	private String nickName;
	private String account;
	private double latitude;
	private double longitude;
	private String addrress;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserPicStr() {
		return userPicStr;
	}
	public void setUserPicStr(String userPicStr) {
		this.userPicStr = userPicStr;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getAddrress() {
		return addrress;
	}
	public void setAddrress(String addrress) {
		this.addrress = addrress;
	}
	
}
