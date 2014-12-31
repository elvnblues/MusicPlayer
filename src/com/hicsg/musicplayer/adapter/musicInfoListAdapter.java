package com.hicsg.musicplayer.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hicsg.musicplayer.R;
import com.hicsg.musicplayer.constrant.Const;
import com.hicsg.musicplayer.entity.MusicInfo;

public class musicInfoListAdapter extends BaseAdapter {

	private Context context;
	private List<MusicInfo> musicInfoList;
	private static final int TAG_MUSIC_URL = 100;
	private static int position = 0;
	private static int progress = 0;
	private static boolean isDown = false;
	
	private static boolean TAG_UPDATE_STATUS = false;//����״̬����
	private static boolean TAG_UPDATE_PROGRESS = false;// ���н��ȷ���
//	public int showCount = Const.SHOWCOUNT;
	
	public musicInfoListAdapter(Context context, List<MusicInfo> musicInfoList) {
		this.context = context;
		this.musicInfoList = musicInfoList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return musicInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return musicInfoList.get(position).getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.lv_musicinfolist_item, null);
			holder.tv_musicName = (TextView)convertView.findViewById(R.id.tv_musicInfoList_item_musicName);
			holder.tv_rightProgress = (TextView)convertView.findViewById(R.id.tv_rightProgress);
			holder.iv_rightResult = (ImageView)convertView.findViewById(R.id.iv_rightResult);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv_musicName.setText(musicInfoList.get(position).getMusicName());
		holder.tv_musicName.setTag(R.id.tv_musicInfoList_item_musicName, musicInfoList.get(position).getMusicUrl());
		if(TAG_UPDATE_PROGRESS && musicInfoListAdapter.position==position){//�������������ؽ��ȸ����߼�����
			if(musicInfoListAdapter.progress <= 99){//����Ҫ����
				holder.tv_rightProgress.setVisibility(View.VISIBLE);
				holder.iv_rightResult.setVisibility(View.GONE);
				holder.tv_rightProgress.setText(musicInfoListAdapter.progress + "%");
			}else{//�Ѿ����سɹ�
				holder.tv_rightProgress.setVisibility(View.INVISIBLE);
				holder.iv_rightResult.setVisibility(View.VISIBLE);
				holder.iv_rightResult
						.setBackgroundResource(R.drawable.ico_download_success);
				TAG_UPDATE_PROGRESS = false;
			}
		}else{//���г�ʼ������״̬
			String musicPath = Const.MUSICPATH+musicInfoList.get(position).getMusicName()+".mp3";
			if(cheakFile(musicPath)){//�������
				holder.tv_rightProgress.setVisibility(View.INVISIBLE);
				holder.iv_rightResult.setVisibility(View.VISIBLE);
				holder.iv_rightResult
						.setBackgroundResource(R.drawable.ico_download_success);
			}else{//������
				holder.tv_rightProgress.setVisibility(View.INVISIBLE);
				holder.iv_rightResult.setVisibility(View.VISIBLE);
				holder.iv_rightResult
				.setBackgroundResource(R.drawable.ico_download_wait);
			}
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_musicName;
		TextView tv_rightProgress;
		ImageView iv_rightResult;
	}
	/**
	 * ���µ�ǰ���ֵĽ���
	 * @param position
	 * @param progress
	 */
	public static void updateProgress(int position,int progress){
		TAG_UPDATE_PROGRESS = true;
		musicInfoListAdapter.position = position;
		musicInfoListAdapter.progress = progress;
	}
	/**
	 * ����ļ��Ƿ����
	 * @param path
	 * @return
	 */
	private boolean cheakFile(String path){
		File audio = new File(Environment.getExternalStorageDirectory(),
				path);
		return audio.exists();
	}
}
