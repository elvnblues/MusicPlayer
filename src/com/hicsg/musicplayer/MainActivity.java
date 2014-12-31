package com.hicsg.musicplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hicsg.musicplayer.adapter.musicInfoListAdapter;
import com.hicsg.musicplayer.constrant.Const;
import com.hicsg.musicplayer.constrant.ConstClass;
import com.hicsg.musicplayer.db.dao.MusicInfoDao;
import com.hicsg.musicplayer.entity.MusicInfo;
import com.hicsg.musicplayer.tools.LogUtils;

@SuppressWarnings("static-access")
public class MainActivity extends Activity {

	// UI
	private TextView title_top_text;
	private ImageView title_top_right;

	private TextView tv_musicName;
	private ImageButton ib_music_play;
	private ListView lv_main_musicList;

	private SeekBar sbar_music;
	private TextView tv_music_time;

	// menu_playmode
	private PopupWindow menu_playmode_pop;
	private View menu_playmode_contentView;
	private RadioGroup rg_playmode;
	private RadioButton rb_playmode_all;
	private RadioButton rb_playmode_random;
	private RadioButton rb_playmode_single;

	// DATA
	private int playMode;// ��ǰ�Ĳ���ģʽ(Ĭ��ȫ��ѭ������)

	private static final String ID = "_id";
	private static final String MUSICNAME = "musicName";
	private static final String MUSICURL = "musicUrl";

	private MusicInfoDao musicInfoDao;
	private List<Map<String, String>> musicInfoList;
	private musicInfoListAdapter miListAdapter;
	private String[] musicNames;

	private String filePath;
	private MediaPlayer mediaPlayer;
	private boolean pause;
	private int playPosition;// ��ǰ���ŵ����ֽ���
	private String playPath;// ��ǰ���ŵ����ֵ�ַ
	private int positionNow;// ��ǰ���ŵ�������ListV

	private int count;
	private int current;
	private boolean finished;
	private boolean paused;
	private int progress = -1;

	// SeekBar

	public static final String TAG = "MainActivity";

	@Override
	protected void onStart() {
		super.onStart();
		setData();
		playMode = ConstClass.getSharedPreferencesVal(Const.PLAYMODE_NAME,
				MainActivity.this, Const.PLAYMODE_KEY);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		setContentView(R.layout.activity_main);

		init();
		setListener();
		startProgressUpdate();//
	}

	@SuppressLint("NewApi")
	private void init() {
		tv_musicName = (TextView) this.findViewById(R.id.tv_musicName);
		ib_music_play = (ImageButton) this.findViewById(R.id.ib_music_play);
		lv_main_musicList = (ListView) findViewById(R.id.lv_main_musiclist);

		sbar_music = (SeekBar) findViewById(R.id.sbar_music);
		sbar_music.setOnSeekBarChangeListener(new MySeekbar());
		tv_music_time = (TextView) findViewById(R.id.tv_music_position_time);

		title_top_text = (TextView) findViewById(R.id.title_top_text);
		title_top_right = (ImageView) findViewById(R.id.title_top_right);
		title_top_text.setText(R.string.app_name);
		title_top_right.setVisibility(View.VISIBLE);
		title_top_right.setBackgroundResource(R.drawable.image_btn_title_menu);

		mediaPlayer = new MediaPlayer();

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new MyPhoneListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		menu_playmode_contentView = getLayoutInflater().inflate(
				R.layout.menu_playmode_pop, null, true);
		menu_playmode_pop = new PopupWindow(menu_playmode_contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		menu_playmode_pop.setBackgroundDrawable(new BitmapDrawable());// �������ſ��Ե�����أ���������ťdismiss()popwindow
		menu_playmode_pop.setOutsideTouchable(true);
		// menu_playmode_pop.setAnimationStyle(R.style.PopupAnimation);
	}

	private void setListener() {
		ib_music_play.setOnClickListener(btn_onClickListener);
		title_top_right.setOnClickListener(btn_onClickListener);
		lv_main_musicList
				.setOnItemClickListener(musicList_OnChildClickListener);
		mediaPlayer.setOnCompletionListener(mediaPlayer_OnCompletionListener);
		menu_playmode_contentView
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub

						menu_playmode_pop.dismiss();
					}
				});
	}

	private void setData() {
		musicInfoDao = new MusicInfoDao(MainActivity.this);
		musicInfoList = musicInfoDao.getAllMusicInfo();//��ԃ���ݿ������
		// Toast.makeText(MainActivity.this, "����="+musicInfoList.size(),
		// Toast.LENGTH_SHORT).show();
		if (musicInfoList.size() > 0) {
			List<MusicInfo> listValues = new ArrayList<MusicInfo>();
			musicNames = new String[musicInfoList.size()];
			for (int i = 0; i < musicInfoList.size(); i++) {
				Map<String, String> listMap = musicInfoList.get(i);
				MusicInfo musicInfo = new MusicInfo();
				musicInfo.setID(Integer.parseInt(listMap.get(ID)));
				musicInfo.setMusicName(listMap.get(MUSICNAME));
				musicInfo.setMusicUrl(listMap.get(MUSICURL));
				// if(i==2){
				// Toast.makeText(MainActivity.this,
				// listMap.get(MUSICNAME)+"|"+listMap.get(ID),
				// Toast.LENGTH_SHORT).show();
				// }
				musicNames[i] = listMap.get(MUSICNAME);// ���������ƴ洢��musicNames��
				listValues.add(musicInfo);
			}
			miListAdapter = new musicInfoListAdapter(MainActivity.this,
					listValues);
		}
		/*
		 * ��ѯSD�����ļ��������ļ����ݣ����뵽�����б�
		 * �����ṩһ����ť��ѯ�ļ��������Ƿ���� 
		 * ������ھ�ֻ����һ������
		 */
		lv_main_musicList.setAdapter(miListAdapter);
//		MusicInfoDao musicInfoDao = new MusicInfoDao(MainActivity.this);
//		musicInfoDao
//				.addMusicInfo(
//						"ԭ���� - ��̽�����Ƭͷ��������",
//						"http://nj.baidupcs.com/file/01f6189d40b862895e48a168d2383252?bkt=p2-nj-301&fid=2636535922-250528-880465108811389&time=1420008141&sign=FDTAXERLBH-DCb740ccc5511e5e8fedcff06b081203-f6N5j7ThaDA9CAYM7dYYdpr7Xbs%3D&to=nb&fm=Nan,B,U,ny&newver=1&newfm=1&flow_ver=3&sl=81723466&expires=8h&rt=sh&r=441965610&mlogid=3227330291&vuk=2636535922&vbdid=3587078685&fin=%E5%8E%9F%E5%A3%B0%E5%B8%A6%20-%20%E7%A5%9E%E6%8E%A2%E5%A4%8F%E6%B4%9B%E5%85%8B%E7%89%87%E5%A4%B4%E6%9B%B2%E5%8A%A0%E9%85%8D%E4%B9%90.mp3&fn=%E5%8E%9F%E5%A3%B0%E5%B8%A6%20-%20%E7%A5%9E%E6%8E%A2%E5%A4%8F%E6%B4%9B%E5%85%8B%E7%89%87%E5%A4%B4%E6%9B%B2%E5%8A%A0%E9%85%8D%E4%B9%90.mp3");
//		musicInfoDao
//				.addMusicInfo(
//						"�ܽ���-һ����ȫ���",
//						"http://nj.baidupcs.com/file/7ef9bb35e699b8409a3c44dc95186d45?bkt=p2-nj-301&fid=2636535922-250528-755575043660057&time=1420008223&sign=FDTAXERLBH-DCb740ccc5511e5e8fedcff06b081203-rNN6oEfi38%2FdLqW6lvv%2BZNcJ77M%3D&to=nb&fm=Nan,B,U,ny&newver=1&newfm=1&flow_ver=3&sl=81723466&expires=8h&rt=pr&r=243797683&mlogid=841463741&vuk=2636535922&vbdid=3587078685&fin=%E5%91%A8%E6%9D%B0%E4%BC%A6-%E4%B8%80%E5%8F%A3%E6%B0%94%E5%85%A8%E5%BF%B5%E5%AF%B9.mp3&fn=%E5%91%A8%E6%9D%B0%E4%BC%A6-%E4%B8%80%E5%8F%A3%E6%B0%94%E5%85%A8%E5%BF%B5%E5%AF%B9.mp3");
//		musicInfoDao
//				.addMusicInfo(
//						"ݭ�������",
//						"http://lx.cdn.baidupcs.com/file/f298e1d7c3eaf6484e400113fc072ba6?bkt=p2-nb-82&xcode=e56de8f2d65c6622dc3b1b65d3b2060ae177548541a3a5b3d9f439426665a097&fid=2636535922-250528-639174528573646&time=1420008195&sign=FDTAXERLBH-DCb740ccc5511e5e8fedcff06b081203-2yecxWrhJsl6w7HY2znoMTP5q08%3D&to=sc&fm=Nin,B,U,ny&sta_dx=0&sta_cs=3&sta_ft=mp3&sta_ct=5&newver=1&newfm=1&flow_ver=3&sl=81723466&expires=8h&rt=sh&r=522423162&mlogid=3223752192&vuk=2636535922&vbdid=3587078685&fin=%E8%8E%AA%E8%82%9A%E5%9B%B0%E6%AD%BB%E9%94%95.mp3&fn=%E8%8E%AA%E8%82%9A%E5%9B%B0%E6%AD%BB%E9%94%95.mp3");
//
//		musicInfoDao.close();

	}

	/**
	 * ��������
	 */
	private void playMusic() {
		if (mediaPlayer.isPlaying()) {// ������ڲ���
			mediaPlayer.pause();// ��ͣ
			pause = true;
			ib_music_play.setBackgroundResource(R.drawable.ic_player_play);
		} else {
			if (pause) {// ���������ͣ״̬
				mediaPlayer.start();// ��������
				pause = false;
				ib_music_play.setBackgroundResource(R.drawable.ic_player_pause);
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param playPosition
	 */
	private void play(int playPosition, String musicUrl) {
		try {
			mediaPlayer.reset();// �Ѹ�������ָ�����ʼ״̬

			/**
			 * ͨ��MediaPlayer.setDataSource()
			 * �ķ���,��URL���ļ�·�����ַ����ķ�ʽ����.ʹ��setDataSource ()����ʱ,Ҫע����������:
			 * 1.������ɵ�MediaPlayer ����ʵ��Null ����ļ��.
			 * 2.����ʵ�ֽ���IllegalArgumentException ��IOException
			 * ���쳣,�ںܶ������,�����õ��ļ����²�������. 3.��ʹ��URL ����������ý���ļ�,���ļ�Ӧ��Ҫ��֧��pragressive
			 * ����.
			 */
			mediaPlayer.setDataSource(musicUrl);
			mediaPlayer.prepare();// ���л���
			mediaPlayer.setOnPreparedListener(new MyPreparedListener(
					playPosition));
			ib_music_play.setBackgroundResource(R.drawable.ic_player_pause);
			sbar_music.setMax(mediaPlayer.getDuration());// ���ý�����
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ͨ�����ŵ�ǰѡ�е��±겥������
	 * 
	 * @param position
	 */
	private void playMusicByPosition(int position) {
		if (mediaPlayer.isPlaying()) {// ������ڲ���
			mediaPlayer.pause();// ��ͣ
			pause = true;
			ib_music_play.setBackgroundResource(R.drawable.ic_player_play);
		}
		if (musicInfoList.size() > 0) {// ���������
			String musicUrl;
			String musicName;
			Map musicInfoMap = musicInfoList.get(position);
			musicName = (String) musicInfoMap.get(MUSICNAME);
			musicUrl = (String) musicInfoMap.get(MUSICURL);
			tv_musicName.setText(musicName);
			tv_musicName.setTag(R.id.tv_musicName, musicUrl);
			musicName = musicName + ".mp3";
			String musicPath = Const.MUSICPATH + musicName;
			File audio = new File(Environment.getExternalStorageDirectory(),
					musicPath);
			playPath = audio.getAbsolutePath();
			if (audio.exists()) {// �������
				play(0, playPath);// ��������
				miListAdapter.notifyDataSetChanged();
			} else {// ������
				new MyAsyncTask().execute(musicUrl, Const.MUSICPATH, musicName);// �첽���أ����سɹ��󲥷�
			}
			
		}
	}

	/**
	 * ֻ�е绰����֮�����ͣ���ֵĲ���
	 */
	private final class MyPhoneListener extends
			android.telephony.PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// �绰����
				if (mediaPlayer.isPlaying()) {
					playPosition = mediaPlayer.getCurrentPosition();// ��õ�ǰ����λ��
					mediaPlayer.stop();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE: // ͨ������
				if (playPosition > 0 && filePath != null) {
					play(playPosition, playPath);
					playPosition = 0;
				}
				break;
			}

		}
	}

	/*
	 * // ���ô��ڴ��ڲ��ɼ���ʱ�򴥷�
	 * 
	 * @Override protected void onPause() { if (mediaPlayer.isPlaying()) {
	 * playPosition = mediaPlayer.getCurrentPosition();// ��õ�ǰ����λ��
	 * mediaPlayer.stop(); } super.onPause(); }
	 * 
	 * // ���ô��ڴ������»ص�ǰ̨ʱ�򴥷�
	 * 
	 * @Override protected void onResume() { if (playPosition > 0 && filePath !=
	 * null) { play(); mediaPlayer.seekTo(playPosition); playPosition = 0; }
	 * super.onResume(); }
	 */

	private OnItemClickListener musicList_OnChildClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			positionNow = position;
			playMusicByPosition(positionNow);
		}
	};
	private OnClickListener btn_onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ib_music_play:// ���Ű�ť
				playMusic();
				break;
			case R.id.title_top_right:// �˵���ť
				if (menu_playmode_pop.isShowing()) {

					menu_playmode_pop.dismiss();
				}
				menu_playmode_pop.showAsDropDown(v);
				break;
			}

		}
	};
	/*
	 * һ�׸貥����ɺ�(�������ò���ģʽ)
	 */
	private OnCompletionListener mediaPlayer_OnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (playMode == Const.ALL) {// ˳�򲥷�
				if (positionNow == musicInfoList.size() - 1) {
					positionNow = 0;// ��һ�׸�
				} else {
					positionNow++;
				}
			} else if (playMode == Const.RANDOM) {// �������
				positionNow = (int) (Math.random() * musicInfoList.size());
			} else if (playMode == Const.SINGLE) {
				// positionNow = positionNow;
			}
			playMusicByPosition(positionNow);
		}
	};

	private final class MyPreparedListener implements
			android.media.MediaPlayer.OnPreparedListener {
		private int playPosition;

		public MyPreparedListener(int playPosition) {
			this.playPosition = playPosition;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start();// ��ʼ����
			if (playPosition > 0) {
				mediaPlayer.seekTo(playPosition);
			}
		}

	}

	/*
	 * ����ʱ��TextView����ʾ
	 */
	private Handler timeHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				tv_music_time.setText("-"
						+ setMusicTimeFormat(mediaPlayer.getDuration(),
								mediaPlayer.getCurrentPosition()));
			}
		};
	};

	/**
	 * �첽���أ����سɹ��󲥷�
	 * 
	 * @author Administrator ��������������String musicUrl , String musicPath , String
	 *         musicName
	 */
	class MyAsyncTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				String musicUrl = params[0];
				String musicPath = params[1];
				String musicName = params[2];
				int status = ConstClass.isConnect(musicUrl);
				if (status == 200) {
					URL url = new URL(musicUrl);
					URLConnection conn = url.openConnection();
					count = conn.getContentLength();
					InputStream is = conn.getInputStream();
					OutputStream os = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ musicPath + musicName);
					byte[] buffer = new byte[1024];
					int len = -1;
					while (!finished) {
						while (!paused && (len = is.read(buffer)) > 0) {
							current += len;
							os.write(buffer, 0, len);
							progress = current * 100 / count;
							publishProgress(progress);
						}
					}
				} else if (status == 404) {
					Toast.makeText(MainActivity.this, "���ֵ�ַ�����ڣ�",
							Toast.LENGTH_SHORT).show();
				} else if (status == 403) {
					Toast.makeText(MainActivity.this, "���ֵ�ַ�쳣����鿴��־��",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "���ֵ�ַ�쳣����鿴��־��",
							Toast.LENGTH_SHORT).show();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				;
				e.printStackTrace();
			}

			return progress + "";
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, result + "");
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values[0] > 0) {
				if (values[0] >= 99) {
					finished = true;
					miListAdapter.updateProgress(positionNow, values[0]);
					miListAdapter.notifyDataSetChanged();
					play(0, playPath);// ��������
				}
				finished = false;
				miListAdapter.updateProgress(positionNow, values[0]);
				miListAdapter.notifyDataSetChanged();
				super.onProgressUpdate(values);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	// ����������
	class MySeekbar implements OnSeekBarChangeListener {
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			mediaPlayer.seekTo(seekBar.getProgress());
		}

	}

	public void startProgressUpdate() {
		// ����Thread ���ڶ���ˢ��SeekBar
		DelayThread dThread = new DelayThread(100);
		dThread.start();
	}
	public class DelayThread extends Thread {
		int milliseconds;

		public DelayThread(int i) {
			milliseconds = i;
		}

		public void run() {
			while (true) {
				try {
					sleep(milliseconds);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mHandle.sendEmptyMessage(0);
				timeHandler.sendEmptyMessage(0);// ����ʣ��ʱ��
			}
		}
	}
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				sbar_music.setProgress(mediaPlayer.getCurrentPosition());
			}
		}
	};

	

	/**
	 * ��������λ�÷������ָ�ʽ����ַ���
	 * 
	 * @param position
	 */
	private String setMusicTimeFormat(int maxLength, int position) {
		// ��ǰʱ��
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		return sdf.format(maxLength - position);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (menu_playmode_pop != null && menu_playmode_pop.isShowing()) {
				menu_playmode_pop.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);

	}

	// ���ֲ�����ֹͣ
	private void stop() {
		mediaPlayer.stop();
		try {
			mediaPlayer.prepare();
			mediaPlayer.seekTo(0);
		} catch (IllegalStateException e) {
			LogUtils.e(e.getMessage());
		} catch (IOException e) {
			LogUtils.e(e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		mediaPlayer.release();
		mediaPlayer = null;
		super.onDestroy();
	}

}
