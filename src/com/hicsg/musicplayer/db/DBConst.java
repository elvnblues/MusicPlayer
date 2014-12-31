package com.hicsg.musicplayer.db;

public class DBConst {
	// 数据库名
		public static final String DATABASE_NAME = "musicplayer_db";
		//版本号
		public static final int DATABASE_VERSION = 2014111701;
		
		public static final String DROP_SQL_PRE = "DROP TABLE IF EXISTS ";
		
//		public static String DB_PATH = "/data/data/com.hicsg.musicplayer/databases/";  
		public static String DB_PATH = android.os.Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/musicplayer"; 
		public static final String DB_NAME = "musicplayer_db";
		
		public static final int REQUEST_COUNT = 3;
		
		//topic
		public static final String MUSICINFO_TABLE = "musicInfo";
		public static final String MUSICINFO_VISION_TABLE = "musicInfo_vision";
		//考题表创建SQL语句
		public static final String CREATE_MUSICINFO_TABLE = "create table IF NOT EXISTS musicInfo(_id integer primary key autoincrement,musicName varchar(100),musicUrl varchar(1000))";
		public static final String CREATE_MUSICINFO_VISION_TABLE = "create table IF NOT EXISTS musicInfo_vision(_id integer primary key autoincrement,vision_value varchar(100),update_time datatime)";
}
