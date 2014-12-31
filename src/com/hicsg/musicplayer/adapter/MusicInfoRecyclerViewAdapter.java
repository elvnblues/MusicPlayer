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
     * 在这里进行数据的绑定，参数1是onCreateViewHolder创建的viewholder，参数2是你item的位置 
     */  
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		ListHolder listHolder = (ListHolder)holder;
		listHolder.tv_musicName.setText(musicInfoList.get(position).getMusicName());
		listHolder.tv_musicName.setTag(R.id.tv_musicInfoList_item_musicName, musicInfoList.get(position).getMusicUrl());
	}
	/** 
     * 创建view 
     */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
		/*因为只有一个视图所以没有使用viewtype进行判断*/
		View v = LayoutInflater.from(parent.getContext()).inflate(  
                R.layout.lv_musicinfolist_item, null); 
        ViewHolder holer = new ListHolder(v);  
		return holer;
	}

}
