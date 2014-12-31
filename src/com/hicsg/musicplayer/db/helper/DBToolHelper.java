package com.hicsg.musicplayer.db.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.hicsg.musicplayer.R;
import com.hicsg.musicplayer.db.DBConst;

public class DBToolHelper extends SQLiteOpenHelper {

	private SQLiteDatabase myDataBase;

	private Context myContext;

	public DBToolHelper(Context context) {
		super(context, DBConst.DATABASE_NAME, null, DBConst.DATABASE_VERSION);
		myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database. ��ϵͳ�ﴴ��һ���µ����ݿⲢ�Ұ��Լ�����������д��
	 * */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist ʲô���鶼���������ݿ��Ѿ�����
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			// ͨ���������������ϵͳĬ�ϵ�·���´���һ���յ����ݿ�,��������Ҫȥ�ܹ����Ǹ����ݿ������ǵ����ݿ⡣
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private synchronized boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DBConst.DB_PATH + DBConst.DB_NAME;
			checkDB = SQLiteDatabase.openOrCreateDatabase(myPath, null);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream �򿪱������ݿ��������
		InputStream myInput = myContext.getAssets().open(DBConst.DB_NAME);
		// Path to the just created empty db ·���ոմ����Ŀ����ݿ�
		String outFileName = DBConst.DB_PATH + DBConst.DB_NAME;
		// Open the empty db as the output stream �򿪿����ݿ�������
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile �Ѵ�����ֽڴ������ļ���������ļ�
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		// Open the database
//		String myPath = DBConst.DB_PATH + DBConst.DB_NAME;
//		myDataBase = SQLiteDatabase.openOrCreateDatabase(myPath, null);
		
		try {
			boolean b = false;
			// �õ����ݿ������·����
			String databaseFilename = DBConst.DB_PATH + "/" + DBConst.DB_NAME;
			// �����ݿ��ļ�����Դ�ļ��ŵ����ʵط�����Դ�ļ�Ҳ�������ݿ��ļ�������Ŀ��res�µ�rawĿ¼�У�
			// �����ݿ��ļ����Ƶ�SD���� 
			File dir = new File(DBConst.DB_PATH);
			if (!dir.exists())
				b = dir.mkdir();
			// �ж��Ƿ���ڸ��ļ�
			if (!(new File(databaseFilename)).exists()) {
				// �����ڵõ����ݿ�����������
				InputStream is = myContext.getResources().openRawResource(
						R.raw.musicplayer_db);
				// ���������
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				// ���������
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				// �ر���Դ
				fos.close();
				is.close();
			}
			// �õ�SQLDatabase����
			myDataBase = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#close()
	 * �ر����д򿪵�SQLiteDatabase
	 */
	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase) ����һ�δ������ݿ�ʱ�ص��÷���
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// ���� ��
		db.execSQL(DBConst.CREATE_MUSICINFO_TABLE);
		// db.execSQL(DBConst.CREATE_MUSICINFO_VISION_TABLE);
		//
		// MusicInfoDao musicInfoDao = new MusicInfoDao(myContext);
		// musicInfoDao.addMusicInfo("Sara - ���",
		// "http://qd.baidupcs.com/file/51bd2353ff7d9ea624be7ed4469ec4ed?bkt=p2-nb-82&fid=2636535922-250528-324524799610150&time=1415957185&sign=FDTAXERB-DCb740ccc5511e5e8fedcff06b081203-6HTx8NP3ZU8FRgM7ogRk2QuJoXI%3D&to=qb&fm=Qin,B,U,nc&newver=1&newfm=1&flow_ver=3&expires=8h&rt=pr&r=250056824&mlogid=1227403421&vuk=2636535922&vbdid=3587078685&fin=Sara%20-%20%E9%85%8D%E8%A7%92.mp3&fn=Sara%20-%20%E9%85%8D%E8%A7%92.mp3");
		// musicInfoDao.addMusicInfo("ݭ�������",
		// "http://nb.baidupcs.com/file/f298e1d7c3eaf6484e400113fc072ba6?bkt=p2-nb-82&fid=2636535922-250528-639174528573646&time=1416383899&sign=FDTAXERLB-DCb740ccc5511e5e8fedcff06b081203-wrNp2Wj3Wrsf5O3VQI4vCVbbVvQ%3D&to=nbb&fm=Nin,B,T,nc&newver=1&newfm=1&flow_ver=3&sl=81723486&expires=8h&rt=pr&r=568149094&mlogid=2577162243&vuk=2636535922&vbdid=3587078685&fin=%E8%8E%AA%E8%82%9A%E5%9B%B0%E6%AD%BB%E9%94%95.mp3&fn=%E8%8E%AA%E8%82%9A%E5%9B%B0%E6%AD%BB%E9%94%95.mp3");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int) �����ݿ�汾����ʱ�ص��÷���
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(DBConst.DROP_SQL_PRE + DBConst.MUSICINFO_TABLE);

		onCreate(db);
	}
}
