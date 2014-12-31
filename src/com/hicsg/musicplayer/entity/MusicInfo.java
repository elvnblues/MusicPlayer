package com.hicsg.musicplayer.entity;

public class MusicInfo {
	private int ID;//流水号
	private String musicName;//音乐名
	private String musicUrl;//音乐地址
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getMusicName() {
		return musicName;
	}
	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}
	public String getMusicUrl() {
		return musicUrl;
	}
	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}
}
