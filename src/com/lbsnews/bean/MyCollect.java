package com.lbsnews.bean;

import java.io.Serializable;
import java.util.Date;

public class MyCollect implements Serializable {
	
	private int id;
	private int nid;
	private String userPicStr;
	private String nickName;
	private String textContent;
	private String picContent;
	private Date publishTime;
	private String newsAccount;
	private String collectAccount;
	
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
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public String getPicContent() {
		return picContent;
	}
	public void setPicContent(String picContent) {
		this.picContent = picContent;
	}
	public Date getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public String getNewsAccount() {
		return newsAccount;
	}
	public void setNewsAccount(String newsAccount) {
		this.newsAccount = newsAccount;
	}
	public String getCollectAccount() {
		return collectAccount;
	}
	public void setCollectAccount(String collectAccount) {
		this.collectAccount = collectAccount;
	}
	public int getNid() {
		return nid;
	}
	public void setNid(int nid) {
		this.nid = nid;
	}
	
}
