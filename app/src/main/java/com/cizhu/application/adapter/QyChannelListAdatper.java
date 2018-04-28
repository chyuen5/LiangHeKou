package com.cizhu.application.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cizhu.application.R;
import com.wholeally.qysdk.QYChannelInfo;

public class QyChannelListAdatper extends BaseAdapter {

	private List<QYChannelInfo> qyChannelInfo;
	private Context context;

	public QyChannelListAdatper(Context context,
			List<QYChannelInfo> qyChannelInfo) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.qyChannelInfo = qyChannelInfo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return qyChannelInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return qyChannelInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = ((Activity) context).getLayoutInflater().inflate(
					R.layout.qy_activity_test_channel, null);
			viewHolder.text_qyChannelName_texts = (TextView) convertView
					.findViewById(R.id.text_qyChannelName_text);
			viewHolder.text_qyIfOnlinec_texts = (TextView) convertView
					.findViewById(R.id.text_qyIfOnlinec_text);
			viewHolder.text_cloud_texts = (TextView) convertView
					.findViewById(R.id.text_cloud_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		QYChannelInfo channelInfo = qyChannelInfo.get(position);
		viewHolder.text_qyChannelName_texts.setText("通道名称:"
				+ String.valueOf(channelInfo.getChannelID()));
		if (channelInfo.getStatus() == 0) {
			viewHolder.text_qyIfOnlinec_texts.setText("状态:离线");
		} else {
			viewHolder.text_qyIfOnlinec_texts.setText("状态:在线");
		}
		viewHolder.text_cloud_texts.setText("云存状态:"
				+ String.valueOf(channelInfo.getCloud()));
		return convertView;
	}

	static class ViewHolder {
		TextView text_qyChannelName_texts;
		TextView text_qyIfOnlinec_texts;
		TextView text_cloud_texts;
	}
}
