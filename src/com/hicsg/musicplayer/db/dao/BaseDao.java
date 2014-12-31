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
	 * �ֶ����ÿ�ʼ����
	 */
	public void beginTransaction() {
		sqliteDB.beginTransaction();
	}

	/**
	 * ���ÿ�ʼ�������
	 */
	public void endTransaction() {
		sqliteDB.setTransactionSuccessful();// ����������ɹ��������û��Զ��ع����ύ
		sqliteDB.endTransaction();
	}
}
