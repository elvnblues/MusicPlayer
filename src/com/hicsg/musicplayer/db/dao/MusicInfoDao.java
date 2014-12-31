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
	 * ����һ���µ�������Ϣ
	 * 
	 * @param name
	 * @param url
	 * @return ���������¼���кţ����к���һ���ڲ�ֵ��������id�޹أ��������󷵻�-1
	 */
	public long addMusicInfo(String name, String url) {
		ContentValues values = new ContentValues();
		values.put(MUSICNAME, name);
		values.put(MUSICURL, url);
		return sqliteDB.insert(DBConst.MUSICINFO_TABLE, null, values);
	}

	/**
	 * ����ID�޸Ķ�Ӧ��������Ϣ
	 * 
	 * @param Id
	 * @param name
	 * @param url
	 * @return ���ر��޸ĵļ�¼����
	 */
	public int updateMusicInfoByID(int Id, String name, String url) {
		ContentValues values = new ContentValues();
		values.put(MUSICNAME, name);
		values.put(MUSICURL, url);
		return sqliteDB.update(DBConst.MUSICINFO_TABLE, values, "_id=?",
				new String[] { String.valueOf(Id) });
	}

	/**
	 * ����IDɾ����Ӧ��������Ϣ
	 * 
	 * @param Id
	 * @return ���ر�ɾ���ļ�¼����
	 */
	public int deleteMusicInfoByID(int Id) {
		return sqliteDB.delete(DBConst.MUSICINFO_TABLE, "_id=?",
				new String[] { String.valueOf(Id) });
	}

	/**
	 * ��ѯ����������Ϣ��������������
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
