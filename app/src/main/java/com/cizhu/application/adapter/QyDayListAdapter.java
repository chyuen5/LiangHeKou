package com.cizhu.application.adapter;

import java.util.List;

import com.cizhu.application.R;
import com.wholeally.qysdk.QYDaysIndex;
import com.wholeally.qysdk.QYDaysIndex.Day;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/** 天列表数据 */
public class QyDayListAdapter extends BaseAdapter {

	private List<Day> qyDaysIndex;
	private Context context;

	private Day d;

	public QyDayListAdapter(Context context, List<Day> qyDaysIndex) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.qyDaysIndex = qyDaysIndex;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return qyDaysIndex.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return qyDaysIndex.get(position);
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
					R.layout.qy_activity_day_item, null);
			viewHolder.text_qyday_texts = (TextView) convertView
					.findViewById(R.id.text_qyday_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Day days = qyDaysIndex.get(position);
		viewHolder.text_qyday_texts.setText("天列表:"
				+ String.valueOf(days.getYear()) + "年-"
				+ String.valueOf(days.getMonth()) + "月-"
				+ String.valueOf(days.getDay()) + "日");

		return convertView;
	}

	static class ViewHolder {
		TextView text_qyday_texts;
	}

}
