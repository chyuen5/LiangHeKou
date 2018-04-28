package com.cizhu.application.Main;

import java.util.ArrayList;

import com.cizhu.application.ActivityLogin;
import com.cizhu.application.R;

import com.wholeally.qysdk.QYChannelInfo;
import com.wholeally.qysdk.QYDeviceInfo;
import com.wholeally.qysdk.QYSession.OnGetChannelList;
import com.wholeally.qysdk.QYSession.OnGetDeviceList;

import com.cizhu.application.adapter.QyChannelListAdatper;
import com.cizhu.application.adapter.QyDeviceListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/** 设备列表对应通道列表 */
public class QyDeviceActivity extends Activity {

	private Context context;
	/** 设备列表 */
	private ListView list_device_lists; 
	/** 设备列表数据 */
	private ArrayList<QYDeviceInfo> devListInfo;
	/** 设备列表适配器 */
	private QyDeviceListAdapter qyDeviceAdapter;

	/** 通道列表 */
	private ListView list_channel_lists;
	/** 通道列表 */
	private ArrayList<QYChannelInfo> channelListInfo;
	private QyChannelListAdatper qyChannelAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qy_activity_device);
		initView();
		initData();
		initEvent();
	}

	public void initView() {
		context = this;
		list_device_lists = (ListView) findViewById(R.id.list_device_list);
		list_channel_lists = (ListView) findViewById(R.id.list_channel_list);
	}

	public void initData() {
		devListInfo = new ArrayList<QYDeviceInfo>();
		channelListInfo = new ArrayList<QYChannelInfo>();
		if (null != ActivityLogin.session) {
			/**
			 * 第一个参数:页码
			 * 第二个参数:页的大小
			 * 第三个参数:设备列表回调函数 大于等于0为成功 否则为失败
			 **/
			ActivityLogin.session.GetDeviceList(0, 10, new OnGetDeviceList() {
				@Override
				public void on(int ret, ArrayList<QYDeviceInfo> deviceList) {
					devListInfo = deviceList;
					if (ret >= 0) {
						System.out.println("===deviceRet===:" + ret
								+ ";==deviceSize==:" + deviceList.size());
						qyDeviceAdapter = new QyDeviceListAdapter(context,
								deviceList);
						list_device_lists.setAdapter(qyDeviceAdapter);
					} else {
						showToast("获取设备列表失败:"+String.valueOf(ret));
					}
				}
			});
		} else {
			showToast("获取设备列表session==null");
		}
	}

	public void initEvent() {
		list_device_lists.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (channelListInfo.size() > 0) {
					channelListInfo.clear();
				}
				if (null != ActivityLogin.session) {
					/**
					 * 第一个参数:设备号
					 * 第二个参数:通道列表回调函数 大于或等于0为成功 否则为失败
					 **/
					ActivityLogin.session.GetChannelList(
							devListInfo.get(position).getDeviceID(),
							new OnGetChannelList() {
								@Override
								public void on(int ret,
										ArrayList<QYChannelInfo> channelList) {
									// TODO Auto-generated method stub
									if (ret >= 0 ) {
										channelListInfo = channelList;
										qyChannelAdapter = new QyChannelListAdatper(
												context, channelListInfo);
										list_channel_lists
												.setAdapter(qyChannelAdapter);
									} else {
										showToast("获取通道列表失败"+String.valueOf(ret));
									}
									qyChannelAdapter.notifyDataSetChanged();
								}
							});
				} else {
					showToast("获取通道列表session==null");
				}
			}
		});

		list_channel_lists.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(QyDeviceActivity.this,
						QyVideoControlActivity.class);
				intent.putExtra("channelName", String.valueOf(channelListInfo
						.get(position).getChannelID()));
				startActivity(intent);
			}
		});
	}

	public void showToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
