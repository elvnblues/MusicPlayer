package com.hicsg.musicplayer.constrant;

public interface Const {
	static final int SHOWCOUNT = 10;
	static final String MUSICPATH = "/musicplayer/musicfile/";//音乐文件存储路径
	
	public static final int ALL = 0;//全部循环
	public static final int SINGLE = 1;//单曲循环
	public static final int RANDOM = 2;//随机
	
	static final String PLAYMODE_NAME = "playmode_name";//SharedPreference 播放模式(name)
	static final String PLAYMODE_KEY = "playmode_key";//SharedPreference 播放模式(key)
}
