package com.cizhu.application.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cizhu.application.R;
import com.wholeally.qysdk.QYTimeIndex.TimeBucket;

/** 天列表数据 */
public class QyTimesListAdapter extends BaseAdapter {

	private ArrayList<TimeBucket> timeList;
	private Context context;

	public QyTimesListAdapter(Context context, ArrayList<TimeBucket> timeList) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.timeList = timeList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return timeList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return timeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = ((Activity) context).getLayoutInflater().inflate(
					R.layout.qy_activity_time_item, null);
			viewHolder.text_qytime_texts = (TextView) convertView
					.findViewById(R.id.text_qytime_start_text);
			viewHolder.text_qytime_end_texts = (TextView) convertView.findViewById(R.id.text_qytime_end_text);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		TimeBucket times = timeList.get(position);
		if(null!=times){
			viewHolder.text_qytime_texts.setText(times.getStart()+""); 
			viewHolder.text_qytime_end_texts.setText("        结束："+times.getEnd());
		}else{
			System.out.println("!!!!!!!!!!==times为空"); 
		}
		

		return convertView;
	}

	static class ViewHolder {
		TextView text_qytime_texts;
		TextView text_qytime_end_texts;
	}

}
