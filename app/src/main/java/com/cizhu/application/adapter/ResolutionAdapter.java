package com.cizhu.application.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cizhu.application.R;

public class ResolutionAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Integer> list;
	
	public ResolutionAdapter(Context context,ArrayList<Integer> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.item_istview_resolution, null);
			holder.resolutionType_tv = (TextView) convertView.findViewById(R.id.resolution_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
			int modules = list.get(position);
			if(modules==1){
				holder.resolutionType_tv.setText("普清画质");
			}
			else if(modules==2){
				holder.resolutionType_tv.setText("标清画质");
			}
			else if(modules==3){
				holder.resolutionType_tv.setText("高清画质");
			}
			else if(modules==4){
				holder.resolutionType_tv.setText("超清画质");
			}
		
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView resolutionType_tv;
	}
}
