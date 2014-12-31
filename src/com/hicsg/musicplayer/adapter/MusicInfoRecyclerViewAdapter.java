package com.hicsg.musicplayer.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hicsg.musicplayer.R;
import com.hicsg.musicplayer.entity.MusicInfo;

public class MusicInfoRecyclerViewAdapter extends Adapter<ViewHolder> {
	private Context context;  
	private List<MusicInfo> musicInfoList;
	
	public MusicInfoRecyclerViewAdapter(Context context, List<MusicInfo> musicInfoList){
		this.context = context;
		this.musicInfoList = musicInfoList;
	}
	
	public class ListHolder extends RecyclerView.ViewHolder{
		public TextView tv_musicName;
		public TextView tv_rightProgress;
		public ImageView iv_rightResult;
		public ListHolder(View itemView) {
			super(itemView);
			this.tv_musicName = (TextView)itemView.findViewById(R.id.tv_musicInfoList_item_musicName);
			this.tv_rightProgress = (TextView)itemView.findViewById(R.id.tv_rightProgress);
			this.iv_rightResult = (ImageView)itemView.findViewById(R.id.iv_rightResult);
		}
		
	}
	
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return musicInfoList.size();
	}
	/** 
     * ������������ݵİ󶨣�����1��onCreateViewHolder������viewholder������2����item��λ�� 
     */  
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		ListHolder listHolder = (ListHolder)holder;
		listHolder.tv_musicName.setText(musicInfoList.get(position).getMusicName());
		listHolder.tv_musicName.setTag(R.id.tv_musicInfoList_item_musicName, musicInfoList.get(position).getMusicUrl());
	}
	/** 
     * ����view 
     */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
		/*��Ϊֻ��һ����ͼ����û��ʹ��viewtype�����ж�*/
		View v = LayoutInflater.from(parent.getContext()).inflate(  
                R.layout.lv_musicinfolist_item, null); 
        ViewHolder holer = new ListHolder(v);  
		return holer;
	}

}
