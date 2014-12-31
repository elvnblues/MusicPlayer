package com.hicsg.musicplayer.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.hicsg.musicplayer.db.DBConst;

public class MusicInfoDao extends BaseDao {

	private static final String ID = "_id";
	private static final String MUSICNAME = "musicName";
	private static final String MUSICURL = "musicUrl";

	public MusicInfoDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 插入一条新的音乐信息
	 * 
	 * @param name
	 * @param url
	 * @return 返回新添记录的行号，该行号是一个内部值，与主键id无关，发生错误返回-1
	 */
	public long addMusicInfo(String name, String url) {
		ContentValues values = new ContentValues();
		values.put(MUSICNAME, name);
		values.put(MUSICURL, url);
		return sqliteDB.insert(DBConst.MUSICINFO_TABLE, null, values);
	}

	/**
	 * 根据ID修改对应的音乐信息
	 * 
	 * @param Id
	 * @param name
	 * @param url
	 * @return 返回被修改的记录条数
	 */
	public int updateMusicInfoByID(int Id, String name, String url) {
		ContentValues values = new ContentValues();
		values.put(MUSICNAME, name);
		values.put(MUSICURL, url);
		return sqliteDB.update(DBConst.MUSICINFO_TABLE, values, "_id=?",
				new String[] { String.valueOf(Id) });
	}

	/**
	 * 根据ID删除对应的音乐信息
	 * 
	 * @param Id
	 * @return 返回被删除的记录条数
	 */
	public int deleteMusicInfoByID(int Id) {
		return sqliteDB.delete(DBConst.MUSICINFO_TABLE, "_id=?",
				new String[] { String.valueOf(Id) });
	}

	/**
	 * 查询所有音乐信息按照音乐名排序
	 * 
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, String>> getAllMusicInfo() {
		Cursor cursor = null;
		List<Map<String, String>> musicInfoList = new ArrayList<Map<String, String>>();
		cursor = sqliteDB.query(DBConst.MUSICINFO_TABLE, new String[] { ID
				+ "," + MUSICNAME + "," + MUSICURL }, null, null, null, null,
				MUSICNAME + " desc", null);
		try {
			while (cursor.moveToNext()) {
				Map<String, String> musicMap = new HashMap<String, String>();
				musicMap.put(ID, cursor.getString(0));
				musicMap.put(MUSICNAME, cursor.getString(1));
				musicMap.put(MUSICURL, cursor.getString(2));
				musicInfoList.add(musicMap);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {

				}
			}
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception e) {

				}
			}
		}
		return musicInfoList;
	}
}
