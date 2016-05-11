package com.lbsnews.bean;

import java.io.Serializable;

/**
 * ×¢²áÓÃ»§
 * @author lxd
 * 2016-3-16
 */
public class User implements Serializable {
	
	private int id;
	private String account;
	private String password;
	private String nickName;
	private String sex;
	private String telephone;
	private byte[] headPic;
	private String picName;
	private String picPath;
	private String headPicStr;
	private String address;
	private double latitude;
	private double longitude;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getSex() {
		return sex;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPicName() {
		return picName;
	}
	
	public void setPicName(String picName) {
		this.picName = picName;
	}
	
	public byte[] getHeadPic() {
		return headPic;
	}
	
	public void setHeadPic(byte[] headPic) {
		this.headPic = headPic;
	}
	
	public String getPicPath() {
		return picPath;
	}
	
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getHeadPicStr() {
		return headPicStr;
	}

	public void setHeadPicStr(String headPicStr) {
		this.headPicStr = headPicStr;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
	

}
