package com.hicsg.musicplayer.entity;

public class MusicInfo {
	private int ID;//��ˮ��
	private String musicName;//������
	private String musicUrl;//���ֵ�ַ
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
