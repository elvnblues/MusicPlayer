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
	private int playMode;// 当前的播放模式(默认全部循环播放)

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
	private int playPosition;// 当前播放的音乐进度
	private String playPath;// 当前播放的音乐地址
	private int positionNow;// 当前播放的音乐在ListV

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
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除标题栏
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
		menu_playmode_pop.setBackgroundDrawable(new BitmapDrawable());// 有了这句才可以点击返回（撤销）按钮dismiss()popwindow
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
		musicInfoList = musicInfoDao.getAllMusicInfo();//查詢数据库的数据
		// Toast.makeText(MainActivity.this, "数量="+musicInfoList.size(),
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
				musicNames[i] = listMap.get(MUSICNAME);// 将音乐名称存储到musicNames中
				listValues.add(musicInfo);
			}
			miListAdapter = new musicInfoListAdapter(MainActivity.this,
					listValues);
		}
		/*
		 * 查询SD卡内文件的音乐文件数据，插入到播放列表
		 * 或者提供一个按钮查询文件中音乐是否存在 
		 * 如果存在就只存在一个即可
		 */
		lv_main_musicList.setAdapter(miListAdapter);
//		MusicInfoDao musicInfoDao = new MusicInfoDao(MainActivity.this);
//		musicInfoDao
//				.addMusicInfo(
//						"原声带 - 神探夏洛克片头曲加配乐",
//						"http://nj.baidupcs.com/file/01f6189d40b862895e48a168d2383252?bkt=p2-nj-301&fid=2636535922-250528-880465108811389&time=1420008141&sign=FDTAXERLBH-DCb740ccc5511e5e8fedcff06b081203-f6N5j7ThaDA9CAYM7dYYdpr7Xbs%3D&to=nb&fm=Nan,B,U,ny&newver=1&newfm=1&flow_ver=3&sl=81723466&expires=8h&rt=sh&r=441965610&mlogid=3227330291&vuk=2636535922&vbdid=3587078685&fin=%E5%8E%9F%E5%A3%B0%E5%B8%A6%20-%20%E7%A5%9E%E6%8E%A2%E5%A4%8F%E6%B4%9B%E5%85%8B%E7%89%87%E5%A4%B4%E6%9B%B2%E5%8A%A0%E9%85%8D%E4%B9%90.mp3&fn=%E5%8E%9F%E5%A3%B0%E5%B8%A6%20-%20%E7%A5%9E%E6%8E%A2%E5%A4%8F%E6%B4%9B%E5%85%8B%E7%89%87%E5%A4%B4%E6%9B%B2%E5%8A%A0%E9%85%8D%E4%B9%90.mp3");
//		musicInfoDao
//				.addMusicInfo(
//						"周杰伦-一口气全念对",
//						"http://nj.baidupcs.com/file/7ef9bb35e699b8409a3c44dc95186d45?bkt=p2-nj-301&fid=2636535922-250528-755575043660057&time=1420008223&sign=FDTAXERLBH-DCb740ccc5511e5e8fedcff06b081203-rNN6oEfi38%2FdLqW6lvv%2BZNcJ77M%3D&to=nb&fm=Nan,B,U,ny&newver=1&newfm=1&flow_ver=3&sl=81723466&expires=8h&rt=pr&r=243797683&mlogid=841463741&vuk=2636535922&vbdid=3587078685&fin=%E5%91%A8%E6%9D%B0%E4%BC%A6-%E4%B8%80%E5%8F%A3%E6%B0%94%E5%85%A8%E5%BF%B5%E5%AF%B9.mp3&fn=%E5%91%A8%E6%9D%B0%E4%BC%A6-%E4%B8%80%E5%8F%A3%E6%B0%94%E5%85%A8%E5%BF%B5%E5%AF%B9.mp3");
//		musicInfoDao
//				.addMusicInfo(
//						"莪肚困死锕",
//						"http://lx.cdn.baidupcs.com/file/f298e1d7c3eaf6484e400113fc072ba6?bkt=p2-nb-82&xcode=e56de8f2d65c6622dc3b1b65d3b2060ae177548541a3a5b3d9f439426665a097&fid=2636535922-250528-639174528573646&time=1420008195&sign=FDTAXERLBH-DCb740ccc5511e5e8fedcff06b081203-2yecxWrhJsl6w7HY2znoMTP5q08%3D&to=sc&fm=Nin,B,U,ny&sta_dx=0&sta_cs=3&sta_ft=mp3&sta_ct=5&newver=1&newfm=1&flow_ver=3&sl=81723466&expires=8h&rt=sh&r=522423162&mlogid=3223752192&vuk=2636535922&vbdid=3587078685&fin=%E8%8E%AA%E8%82%9A%E5%9B%B0%E6%AD%BB%E9%94%95.mp3&fn=%E8%8E%AA%E8%82%9A%E5%9B%B0%E6%AD%BB%E9%94%95.mp3");
//
//		musicInfoDao.close();

	}

	/**
	 * 播放音乐
	 */
	private void playMusic() {
		if (mediaPlayer.isPlaying()) {// 如果正在播放
			mediaPlayer.pause();// 暂停
			pause = true;
			ib_music_play.setBackgroundResource(R.drawable.ic_player_play);
		} else {
			if (pause) {// 如果处于暂停状态
				mediaPlayer.start();// 继续播放
				pause = false;
				ib_music_play.setBackgroundResource(R.drawable.ic_player_pause);
			}
		}
	}

	/**
	 * 播放音乐
	 * 
	 * @param playPosition
	 */
	private void play(int playPosition, String musicUrl) {
		try {
			mediaPlayer.reset();// 把各项参数恢复到初始状态

			/**
			 * 通过MediaPlayer.setDataSource()
			 * 的方法,将URL或文件路径以字符串的方式传入.使用setDataSource ()方法时,要注意以下三点:
			 * 1.构建完成的MediaPlayer 必须实现Null 对像的检查.
			 * 2.必须实现接收IllegalArgumentException 与IOException
			 * 等异常,在很多情况下,你所用的文件当下并不存在. 3.若使用URL 来播放在线媒体文件,该文件应该要能支持pragressive
			 * 下载.
			 */
			mediaPlayer.setDataSource(musicUrl);
			mediaPlayer.prepare();// 进行缓冲
			mediaPlayer.setOnPreparedListener(new MyPreparedListener(
					playPosition));
			ib_music_play.setBackgroundResource(R.drawable.ic_player_pause);
			sbar_music.setMax(mediaPlayer.getDuration());// 设置进度条
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过播放当前选中的下标播放音乐
	 * 
	 * @param position
	 */
	private void playMusicByPosition(int position) {
		if (mediaPlayer.isPlaying()) {// 如果正在播放
			mediaPlayer.pause();// 暂停
			pause = true;
			ib_music_play.setBackgroundResource(R.drawable.ic_player_play);
		}
		if (musicInfoList.size() > 0) {// 如果有数据
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
			if (audio.exists()) {// 如果存在
				play(0, playPath);// 播放音乐
				miListAdapter.notifyDataSetChanged();
			} else {// 不存在
				new MyAsyncTask().execute(musicUrl, Const.MUSICPATH, musicName);// 异步下载，下载成功后播放
			}
			
		}
	}

	/**
	 * 只有电话来了之后才暂停音乐的播放
	 */
	private final class MyPhoneListener extends
			android.telephony.PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话来了
				if (mediaPlayer.isPlaying()) {
					playPosition = mediaPlayer.getCurrentPosition();// 获得当前播放位置
					mediaPlayer.stop();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 通话结束
				if (playPosition > 0 && filePath != null) {
					play(playPosition, playPath);
					playPosition = 0;
				}
				break;
			}

		}
	}

	/*
	 * // 当该窗口处于不可见的时候触发
	 * 
	 * @Override protected void onPause() { if (mediaPlayer.isPlaying()) {
	 * playPosition = mediaPlayer.getCurrentPosition();// 获得当前播放位置
	 * mediaPlayer.stop(); } super.onPause(); }
	 * 
	 * // 当该窗口处于重新回到前台时候触发
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
			case R.id.ib_music_play:// 播放按钮
				playMusic();
				break;
			case R.id.title_top_right:// 菜单按钮
				if (menu_playmode_pop.isShowing()) {

					menu_playmode_pop.dismiss();
				}
				menu_playmode_pop.showAsDropDown(v);
				break;
			}

		}
	};
	/*
	 * 一首歌播放完成后(这里设置播放模式)
	 */
	private OnCompletionListener mediaPlayer_OnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (playMode == Const.ALL) {// 顺序播放
				if (positionNow == musicInfoList.size() - 1) {
					positionNow = 0;// 第一首歌
				} else {
					positionNow++;
				}
			} else if (playMode == Const.RANDOM) {// 随机播放
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
			mediaPlayer.start();// 开始播放
			if (playPosition > 0) {
				mediaPlayer.seekTo(playPosition);
			}
		}

	}

	/*
	 * 更新时间TextView的显示
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
	 * 异步下载，下载成功后播放
	 * 
	 * @author Administrator 传入三个参数：String musicUrl , String musicPath , String
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
					Toast.makeText(MainActivity.this, "音乐地址不存在！",
							Toast.LENGTH_SHORT).show();
				} else if (status == 403) {
					Toast.makeText(MainActivity.this, "音乐地址异常，请查看日志！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "音乐地址异常，请查看日志！",
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
					play(0, playPath);// 播放音乐
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

	// 进度条处理
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
		// 开辟Thread 用于定期刷新SeekBar
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
				timeHandler.sendEmptyMessage(0);// 更新剩余时间
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
	 * 传入音乐位置返回音乐格式后的字符串
	 * 
	 * @param position
	 */
	private String setMusicTimeFormat(int maxLength, int position) {
		// 当前时间
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

	// 音乐播放器停止
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
