package com.lbsnews.bean;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
	
	private int id;
	private int newsId;
	private String commentAccount;
	private String commentAccountPicStr;
	private String commentNickName;
	private String commentContent;
	private Date commentTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public String getCommentAccount() {
		return commentAccount;
	}
	public void setCommentAccount(String commentAccount) {
		this.commentAccount = commentAccount;
	}
	public String getCommentAccountPicStr() {
		return commentAccountPicStr;
	}
	public void setCommentAccountPicStr(String commentAccountPicStr) {
		this.commentAccountPicStr = commentAccountPicStr;
	}
	public String getCommentNickName() {
		return commentNickName;
	}
	public void setCommentNickName(String commentNickName) {
		this.commentNickName = commentNickName;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public Date getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(Date commentTime) {
		this.commentTime = commentTime;
	}
}
