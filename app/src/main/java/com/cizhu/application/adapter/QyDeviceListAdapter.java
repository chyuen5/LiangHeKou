package com.cizhu.application.adapter;

import java.util.List;

import com.cizhu.application.R;
import com.wholeally.qysdk.QYDeviceInfo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/** 设备列表适配器 */
public class QyDeviceListAdapter extends BaseAdapter {

	private List<QYDeviceInfo> qyDevInfo;
	private Context context;

	public QyDeviceListAdapter(Context context, List<QYDeviceInfo> qyDevInfo) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.qyDevInfo = qyDevInfo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return qyDevInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return qyDevInfo.get(position);
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
					R.layout.qy_device_list_item, null);
			viewHolder.text_qyDeviceName_texts = (TextView) convertView
					.findViewById(R.id.text_qyDeviceName_text);
			viewHolder.text_qyIfOnline_texts = (TextView) convertView
					.findViewById(R.id.text_qyIfOnline_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		QYDeviceInfo deviceInfo = qyDevInfo.get(position);
		viewHolder.text_qyDeviceName_texts.setText("设备名称:"+String.valueOf(deviceInfo
				.getDeviceID()));
		if (deviceInfo.getStatus() == 0) {
			viewHolder.text_qyIfOnline_texts.setText("状态:离线");
		} else {
			viewHolder.text_qyIfOnline_texts.setText("状态:在线");
		}
		return convertView;
	}

	static class ViewHolder {
		TextView text_qyDeviceName_texts;
		TextView text_qyIfOnline_texts;
	}

}
