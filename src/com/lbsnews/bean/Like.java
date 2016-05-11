package com.lbsnews.bean;

import java.io.Serializable;

public class Like implements Serializable {
	int id;
	int nid;
	String newsAccount;
	String likeAccount;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNid() {
		return nid;
	}
	public void setNid(int nid) {
		this.nid = nid;
	}
	public String getNewsAccount() {
		return newsAccount;
	}
	public void setNewsAccount(String newsAccount) {
		this.newsAccount = newsAccount;
	}
	public String getLikeAccount() {
		return likeAccount;
	}
	public void setLikeAccount(String likeAccount) {
		this.likeAccount = likeAccount;
	}
}
