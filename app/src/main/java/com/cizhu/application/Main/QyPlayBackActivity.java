package com.cizhu.application.Main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cizhu.application.ActivityLogin;
import com.cizhu.application.R;
import com.wholeally.qysdk.QYDaysIndex;
import com.wholeally.qysdk.QYDaysIndex.Day;
import com.wholeally.qysdk.QYSession.OnCreateView;
import com.wholeally.qysdk.QYSession.OnGetStoreFileListDayIndex;
import com.wholeally.qysdk.QYTimeIndex;
import com.wholeally.qysdk.QYTimeIndex.TimeBucket;
import com.wholeally.qysdk.QYView;
import com.wholeally.qysdk.QYView.OnCtrlReplayTime;
import com.wholeally.qysdk.QYView.OnGetStoreFileIndex;
import com.wholeally.qysdk.QYView.OnStartConnect;

import com.cizhu.application.adapter.QyDayListAdapter;
import com.cizhu.application.adapter.QyTimesListAdapter;

public class QyPlayBackActivity extends Activity {

	/** 天列表数据 */
	private ListView list_day_lists;
	/** 时间列表数据 */
	private ListView list_time_lists;
	
	/** 画布 */
	private SurfaceView surface_playback_views;
	
	/** 回放名称 */
	private String playBackName;
	/** 回放名称 */
	private TextView text_playback_names;
	
	/** 天概要索引*/
	private QYDaysIndex qyDayIndex;
	/** 天列表数据 */
	private ArrayList<Day> qyDaysIndexList;
	/** 天列表适配器 */
	private QyDayListAdapter qyDayListAdapter;

	/** 24小时索引适配器 */
	private QyTimesListAdapter qyTimesListAdapter;
	/** 24小时索引列表数据 */
	private ArrayList<TimeBucket> timeList;

	/** 创建回放观看视频 */
	private QYView qyApplyWatch;
	
	private Context context;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qy_activity_play_back);
		initView();
		initData();
		initEvent();
	}

	/**
	 * 初始化view
	 */
	public void initView() {
		context = this;
		if (getIntent().getExtras() != null) {
			playBackName = getIntent().getExtras().getString("playBackName");
		}
		list_day_lists = (ListView) findViewById(R.id.list_day_list);
		list_time_lists = (ListView) findViewById(R.id.list_time_list);
		surface_playback_views = (SurfaceView) findViewById(R.id.surface_playback_view);
		text_playback_names = (TextView) findViewById(R.id.text_playback_name);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					if (null != qyTimesListAdapter) {// 24小时索引listView更新数据
						qyTimesListAdapter.notifyDataSetChanged();
					}
					break;

				default:
					break;
				}
			}
		};
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		
		// 天概要索引列表初始化
		if (null != qyDaysIndexList) {
			qyDaysIndexList.clear();
		}
		qyDaysIndexList = new ArrayList<Day>();
		text_playback_names.setText(playBackName);

		// 24小时索引列表初始化
		timeList = new ArrayList<TimeBucket>();
		qyTimesListAdapter = new QyTimesListAdapter(context, timeList);
		list_time_lists.setAdapter(qyTimesListAdapter);

		
		// 获取天概要索引数据：一个月当中有哪几天是有回放数据的
		if (null != ActivityLogin.session) {
			/**
			 * 参数说明：
			 * 1、设备id
			 * 2、年
			 * 3、月
			 * 4、天
			 * 5、查询结果的回调函数
			 */
			ActivityLogin.session.GetStoreFileListDayIndex(// 查询2015年12月份的数据
					Long.valueOf(playBackName), 2015, 12, 0,
					new OnGetStoreFileListDayIndex() {
						@Override
						public void on(int ret, QYDaysIndex qyDayIndexs) {
							/**
							 * 返回结果说明：
							 * 1、ret 大于0--成功，小于0--失败
							 * 2、QYDaysIndex 天概要索引对象
							 */
							if (ret >= 0) {// 成功
								qyDayIndex = qyDayIndexs;
								for (Day dIndex : qyDayIndex.getDays()) {
									if (dIndex.getYear() > 0) {
										System.out.println("=year=:"
												+ dIndex.getYear()
												+ ";=month=:"
												+ dIndex.getMonth() + ";=day=:"
												+ dIndex.getDay());
										dIndex.setYear(dIndex.getYear());
										dIndex.setMonth(dIndex.getMonth());
										dIndex.setDay(dIndex.getDay());
										qyDaysIndexList.add(dIndex);
									}
								}
								qyDayListAdapter = new QyDayListAdapter(
										context, qyDaysIndexList);
								list_day_lists.setAdapter(qyDayListAdapter);

							} else {// 失败
								showToast("获取天列表数据失败" + String.valueOf(ret));
							}
						}
					});
		} else {
			showToast("session为null");
		}
	}

	/**
	 * listView点击事件
	 */
	public void initEvent() {
		// 天列表,点击获取当天的24小时索引数据
		list_day_lists.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (null != timeList && timeList.size() > 0) {
					timeList.clear();
				}
				
				/**
				 * 参数说明：
				 * 1、Day对象，天概要索引列表可以获取到
				 * 2、回调函数
				 */
				qyApplyWatch.GetStoreFileIndex(qyDaysIndexList.get(position),
						new OnGetStoreFileIndex() {
							@Override
							public void on(int ret, QYTimeIndex qyTimeIndex) {
								/**
								 * 返回结果说明：
								 * 1、ret 大于0--成功，小于0--失败
								 * 2、QYTimeIndex 24小时索引对象
								 */
								if (ret >= 0) {
									showToast("获取24小时索引成功");
									timeList.addAll(qyTimeIndex.getTimes());
									Message message = handler.obtainMessage();
									message.what = 0;
									handler.sendMessage(message);

								} else {
									showToast("获取24小时索引失败:"
											+ String.valueOf(ret));
								}
							}
						});
			}
		});

		// 时间列表
		list_time_lists.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 实现回放视频播放请求操作.....
				
				if (null != qyApplyWatch) {
					// 传入画布显示视频监控画面
					qyApplyWatch.SetCanvas(surface_playback_views.getHolder());
					
					/**
					 *参数说明：
					 *1、开始观看时间，可从24小时索引列表获取
					 *2、ctrl 控制模式，0--停止 ，1--播放
					 *3、callback 请求结果回调
					 */
					qyApplyWatch.CtrlReplayTime(timeList.get(position)
							.getStart(), 1, new OnCtrlReplayTime() {

						@Override
						public void on(int ret) {// 请求观看结果
							System.out.println("!!!!!===watch reply result:"
									+ ret);
							if (ret >= 0) {

							} else {

							}
						}

					});
				}
			}
		});
	}

	protected void onResume() {
		super.onResume();
		
		// 创建并连接回放观看房间，只有连接成功后才能进行回放观看
		if (null != ActivityLogin.session) {
			ActivityLogin.session.CreateRePlayHandle(
					Long.valueOf(playBackName), 0, new OnCreateView() {
						public void on(int ret, QYView qyReplay) {
							// TODO Auto-generated method stub
							if (ret >= 0) {
								showToast("创建回放房间成功");
								qyApplyWatch = qyReplay;
								qyApplyWatch.StartConnect(new OnStartConnect() {
									@Override
									public void on(int ret) {
										// TODO Auto-generated method stub
										if (ret >= 0) {
											showToast("连接回放房间成功");
										} else {
											showToast("连接回放房间失败:"
													+ String.valueOf(ret));
										}
									}
								});
							} else {
								showToast("创建回放房间失败:" + String.valueOf(ret));
							}
						};
					});
		}
	}

	public void showToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
