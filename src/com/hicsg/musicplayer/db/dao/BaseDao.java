package com.hicsg.musicplayer.db.dao;

import java.io.IOException;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hicsg.musicplayer.db.helper.DBToolHelper;

public abstract class BaseDao {
	protected DBToolHelper helper;

	protected SQLiteDatabase sqliteDB;

	public BaseDao(Context context) {
		helper = new DBToolHelper(context);
		try {  
			helper.createDataBase();  
		} catch (IOException ioe) {  
		    throw new Error("Unable to create database");  
		}  
		try {  
			helper.openDataBase();  
		} catch (SQLException sqle) {  
		    try {
				throw sqle;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}  
		sqliteDB = helper.getWritableDatabase();
	}

	public BaseDao(Context context, boolean isRead) {
		helper = new DBToolHelper(context);
		try {  
			helper.createDataBase();  
		} catch (IOException ioe) {  
		    throw new Error("Unable to create database");  
		}  
		try {  
			helper.openDataBase();  
		} catch (SQLException sqle) {  
		    try {
				throw sqle;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}  
		if (isRead) {
			sqliteDB = helper.getReadableDatabase();
		} else {
			sqliteDB = helper.getWritableDatabase();
		}
	}

	public void close() {
		if (helper != null) {
			helper.close();
		}
	}

	/**
	 * 手动设置开始事务
	 */
	public void beginTransaction() {
		sqliteDB.beginTransaction();
	}

	/**
	 * 设置开始事务结束
	 */
	public void endTransaction() {
		sqliteDB.setTransactionSuccessful();// 设置事务处理成功，不设置会自动回滚不提交
		sqliteDB.endTransaction();
	}
}
