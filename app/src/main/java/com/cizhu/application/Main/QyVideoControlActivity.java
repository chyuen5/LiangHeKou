package com.cizhu.application.Main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cizhu.application.ActivityLogin;
import com.cizhu.application.R;
import com.wholeally.audio.AudioPlay;
import com.wholeally.qysdk.QYAlarmConfig;
import com.wholeally.qysdk.QYDayTimes;
import com.wholeally.qysdk.QYDayTimes.TimeBucket;
import com.wholeally.qysdk.QYDisconnectReason;
import com.wholeally.qysdk.QYRecordStatus;
import com.wholeally.qysdk.QYSession.OnCreateView;
import com.wholeally.qysdk.QYSession.OnGetAlarmConfig;
import com.wholeally.qysdk.QYSession.OnGetVideoQuality;
import com.wholeally.qysdk.QYSession.OnSetAlarmConfig;
import com.wholeally.qysdk.QYSession.OnSetVideoOrientation;
import com.wholeally.qysdk.QYSession.OnSetVideoQuality;
import com.wholeally.qysdk.QYView;
import com.wholeally.qysdk.QYView.OnCtrlPTZ;
import com.wholeally.qysdk.QYView.OnStartConnect;
import com.wholeally.qysdk.QYViewDelegate;
import com.cizhu.application.adapter.ResolutionAdapter;
import com.wholeally.util.RecordThread;

/** 视频监控画面 */
public class QyVideoControlActivity extends Activity implements
		OnClickListener, OnTouchListener {
	/** 通道名称 */
	private String channelName;
	/** 通道名称文本 */
	private TextView text_video_name_texts;
	/** 监控画布 */
	private SurfaceView surface_video_views;
	/** 对讲linearlayout */
	private LinearLayout layout_talk_layouts;
	/** 对讲linearlayout关闭按钮 */
	private ImageView image_talk_close_images;
	/** 对讲按住 */
	private ImageView image_talk_switch_images;
	/** 云台linearlayout */
	private LinearLayout linear_buttom_linears;
	/** 对讲按钮 */
	private ImageView image_talk_images;
	/** 云台控制上 */
	private ImageView imageView_videoControl_ups;
	/** 云台控制左 */
	private ImageView imageView_videoControl_lefts;
	/** 云台控制下 */
	private ImageView imageView_videoControl_downs;
	/** 云台控制右 */
	private ImageView imageView_videoControl_rights;
	/** 云台控制回位 */
	private ImageView imageView_videoControl_centers;
	/** 云台控制变倍减 */
	private ImageView imageView_video_zoom_reduces;
	/** 云台控制变倍加 */
	private ImageView imageView_video_zoom_adds;
	/** 云台控制光圈减 */
	private ImageView imageView_video_aperture_reduces;
	/** 云台控制光圈加 */
	private ImageView imageView_video_aperture_adds;
	/** 云台控制焦距减 */
	private ImageView imageView_video_focus_reduces;
	/** 云台控制焦距加 */
	private ImageView imageView_video_focus_adds;
	/** 回放 */
	private ImageView image_apply_images;
	/** 画质 */
	private ImageView image_huazhi_image;
	/** 布防 */
	private ImageView image_bufang_image;
	/** 翻转 */
	private ImageView image_fanzhuang_image;

	/** 创建观看视频 */
	private QYView qyViewWatch;
	/** 创建对讲房间 */
	private QYView qyViewTalk;

	private RecordThread recordThread;

	// 当前设备通道画质列表
	private ArrayList<Integer> quality_list = new ArrayList<Integer>();
	// 画质信息
	private TextView tv_ctl_huazhi;
	// 布防信息
	private TextView tv_ctl_bufang;

	private Handler handler;
	private Context mContext;

	/** 自定义AlertDialog--画质选择 */
	private AlertDialog customAlertDialogResolution;
	// 画质模式列表的适配器
	private ResolutionAdapter resolutionAdapter;
	// 要设置的画质模式
	private int module;

	/** 自定义AlertDialog--翻转 */
	private AlertDialog customAlertDialogFlip;
	/** 水平翻转CheckBox */
	private CheckBox checkBox_flip_horizontal;
	/** 垂直翻转CheckBox */
	private CheckBox checkBox_flip_vertical;
	/** 翻转--取消 */
	private Button button_flip_cancel;
	/** 翻转--确定 */
	private Button button_flip_confirm;

	/** 布防状态:0--布防中，1--撤防中 */
	private int qyAlarmState;
	/** 布防配置信息 */
	private QYAlarmConfig qyAlarmObj;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qy_activity_test_video_control);
		mContext = this;
		initView();
		initData();
		initEvent();
	}

	protected void onResume() {
		super.onResume();
		qyViewWatchVideo();
		getProtectAbility();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		AudioPlay.getInstance().stop();
	}

	public void initView() {
		if (getIntent().getExtras() != null) {
			channelName = getIntent().getExtras().getString("channelName");
		}
		image_apply_images = (ImageView) findViewById(R.id.image_apply_image);
		text_video_name_texts = (TextView) findViewById(R.id.text_video_name_text);
		surface_video_views = (SurfaceView) findViewById(R.id.surface_video_view);
		layout_talk_layouts = (LinearLayout) findViewById(R.id.layout_talk_layout);
		image_talk_close_images = (ImageView) findViewById(R.id.image_talk_close_image);
		image_talk_switch_images = (ImageView) findViewById(R.id.image_talk_switch_image);
		linear_buttom_linears = (LinearLayout) findViewById(R.id.linear_buttom_linear);
		image_talk_images = (ImageView) findViewById(R.id.image_talk_image);
		imageView_videoControl_ups = (ImageView) findViewById(R.id.imageView_videoControl_up);
		imageView_videoControl_lefts = (ImageView) findViewById(R.id.imageView_videoControl_left);
		imageView_videoControl_downs = (ImageView) findViewById(R.id.imageView_videoControl_down);
		imageView_videoControl_rights = (ImageView) findViewById(R.id.imageView_videoControl_right);
		imageView_videoControl_centers = (ImageView) findViewById(R.id.imageView_videoControl_center);
		imageView_video_zoom_reduces = (ImageView) findViewById(R.id.imageView_video_zoom_reduce);
		imageView_video_zoom_adds = (ImageView) findViewById(R.id.imageView_video_zoom_add);
		imageView_video_aperture_reduces = (ImageView) findViewById(R.id.imageView_video_aperture_reduce);
		imageView_video_aperture_adds = (ImageView) findViewById(R.id.imageView_video_aperture_add);
		imageView_video_focus_reduces = (ImageView) findViewById(R.id.imageView_video_focus_reduce);
		imageView_video_focus_adds = (ImageView) findViewById(R.id.imageView_video_focus_add);

		image_huazhi_image = (ImageView) findViewById(R.id.image_huazhi_image);
		image_bufang_image = (ImageView) findViewById(R.id.image_bufang_image);
		image_fanzhuang_image = (ImageView) findViewById(R.id.image_fanzhuang_image);

		tv_ctl_huazhi = (TextView) findViewById(R.id.tv_ctl_huazhi);
		tv_ctl_bufang = (TextView) findViewById(R.id.tv_ctl_bufang);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:// 弹出自定义的画质选择框
					showCustomAlertDialogResolution(quality_list);
					break;

				case 1:// 设置画质
					setQuality();
					break;

				case 3:// 翻转
					setFilp();
					break;

				default:
					break;
				}
			}
		};
	}

	public void initData() {
		text_video_name_texts.setText(channelName);
	}

	public void initEvent() {
		// onClick事件
		image_talk_close_images.setOnClickListener(this);
		image_talk_images.setOnClickListener(this);
		image_apply_images.setOnClickListener(this);
		image_huazhi_image.setOnClickListener(this);
		image_bufang_image.setOnClickListener(this);
		image_fanzhuang_image.setOnClickListener(this);

		// onTouch事件
		image_talk_switch_images.setOnTouchListener(this);
		imageView_videoControl_ups.setOnTouchListener(this);
		imageView_videoControl_lefts.setOnTouchListener(this);
		imageView_videoControl_downs.setOnTouchListener(this);
		imageView_videoControl_rights.setOnTouchListener(this);
		imageView_videoControl_centers.setOnTouchListener(this);
		imageView_video_zoom_reduces.setOnTouchListener(this);
		imageView_video_zoom_adds.setOnTouchListener(this);
		imageView_video_aperture_reduces.setOnTouchListener(this);
		imageView_video_aperture_adds.setOnTouchListener(this);
		imageView_video_focus_reduces.setOnTouchListener(this);
		imageView_video_focus_adds.setOnTouchListener(this);

	}

	/**
	 * 调用创建观看视频方法
	 **/
	public void qyViewWatchVideo() {
		if (null != ActivityLogin.session) {
			/**
			 * 第一个参数:通道号 第二个参数:观看视频回调函数 ret大于或等于0为成功 否则为失败
			 **/
			ActivityLogin.session.CreateView(Long.valueOf(channelName),
					new OnCreateView() {
						@Override
						public void on(int ret, QYView view) {
							qyViewWatch = view;
							// TODO Auto-generated method stub
							if (ret >= 0) {
								showToast("创建观看成功");
								AudioPlay.getInstance().start(); // 播放音频声音
								if (null != qyViewWatch) {
									qyViewWatch.SetCanvas(surface_video_views
											.getHolder()); // 传入画布显示视频监控画面
									qyViewWatch
											.StartConnect(new OnStartConnect() { // 连接监控视频回调函数
												// ret大于或等于为成功
												// 否则为失败
												@Override
												public void on(int ret) {
													if (ret >= 0) {
														showToast("连接观看成功");
													} else {
														showToast("连接观看失败"
																+ String.valueOf(ret));
													}
												}
											});
								} else {
									showToast("创建观看qyViewWatch==null");
								}
							} else {
								showToast("创建观看失败" + String.valueOf(ret));
							}
							qyViewWatch.SetEventDelegate(new QYViewDelegate() { // 当前监控视频
										// 事件回调函数(如:surfaceView宽高)
										@Override
										// 连接断开
										public void onDisConnect(
												QYDisconnectReason reason) {
											switch (reason) {
											case Unknown:// 未知原类
												break;
											case Initiative:// 主动退出
												break;
											case Passive:// 被踢下线
												break;
											}
										}

										@Override
										// 录像事件
										public void onRecordStatus(
												QYRecordStatus status) {
											switch (status) {
											case Start:// 开始
												break;
											case End:// 结束
												break;
											case WriteError:// 写入失败
												break;
											}
										}

										@Override
										// 回放时间戳
										public void onReplayTimeChange(long tm) {
										}

										@Override
										// 视频大小改变(surfaceView画布宽高)
										public void onVideoSizeChange(
												int width, int height) {
											System.out
													.println("===video_width=====:"
															+ width
															+ ";===video_height===:"
															+ height);
										}

										@Override
										// 对讲时音量变化
										public void onVolumeChange(
												double volumeValue) {
											System.out
													.println("===volume=====:"
															+ volumeValue);
										}
									});
						}
					});
		} else {
			showToast("QYSession==null");
		}
	}

	/**
	 * 调用创建对讲房间方法
	 **/
	public void createTalkRoom() {
		layout_talk_layouts.setVisibility(View.VISIBLE);
		linear_buttom_linears.setVisibility(View.GONE);
		if (null != ActivityLogin.session) {
			/**
			 * 第一个参数：通道号 第二个参数：创建对讲回调函数 ret大于等于0为成功 否则为失败
			 **/
			ActivityLogin.session.CreateTalkHandle(Long.valueOf(channelName),
					new OnCreateView() {
						@Override
						public void on(int ret, QYView view) {
							qyViewTalk = view;
							if (ret >= 0) {
								showToast("创建对讲成功");
								if (null != qyViewTalk) {
									qyViewTalk
											.StartConnect(new OnStartConnect() {// 创建成功完
												// 还要进行
												// 连接对讲房间
												// 回调函数
												// ret大于等于0为成功
												// 否则为失败
												@Override
												public void on(int ret) {
													// TODO Auto-generated
													// method stub
													if (ret >= 0) {
														showToast("连接对讲成功");
													} else {
														showToast("连接对讲失败"
																+ String.valueOf(ret));
													}
												}
											});
								} else {
									showToast("创建对讲qyViewTalk==null");
								}
							} else {
								showToast("创建对讲失败" + String.valueOf(ret));
							}
						}
					});
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.image_talk_close_image:// 对讲linearlayout关闭按钮
			layout_talk_layouts.setVisibility(View.GONE);
			linear_buttom_linears.setVisibility(View.VISIBLE);
			if (recordThread != null) {
				recordThread.pause();
			}
			if (qyViewTalk != null) {
				qyViewTalk.Relase();
			}
			break;

		case R.id.image_talk_image:// 对讲按钮
			// System.out.println("===getPlayerId==:"+getPlayerId.playerID);
			createTalkRoom();
			break;

		case R.id.image_apply_image:// 回放按钮
			Intent intent = new Intent(QyVideoControlActivity.this,
					QyPlayBackActivity.class);
			intent.putExtra("playBackName", channelName);
			startActivity(intent);
			break;

		case R.id.image_fanzhuang_image:// 翻转按钮
			showCustomAlertDialogFlip();
			break;

		case R.id.image_huazhi_image:// 画质按钮
			getQuality();
			break;

		case R.id.image_bufang_image:// 布防按钮
			setAlarmShow();
			break;

		default:
			break;
		}
	}

	/**
	 * 获取画质信息
	 */
	private void getQuality() {

		if (null != quality_list && quality_list.size() > 0) {
			quality_list.clear();
		}

		if (null != ActivityLogin.session) {
			/**
			 * 参数说明： 1、通道id 2、回调接口
			 */
			ActivityLogin.session.GetVideoQuality(Long.valueOf(channelName),
					new OnGetVideoQuality() {

						@Override
						public void on(int ret, int cur, ArrayList<Integer> list) {
							/**
							 * 返回结果说明 1、ret 大于0--成功，小于0--失败 2、cur 当前画质模式
							 * 3、当前通道拥有的画质选项
							 */
							if (ret >= 0) {
								if (cur == 1) {
									tv_ctl_huazhi.setText("当前画质：(普清)");
								} else if (cur == 2) {
									tv_ctl_huazhi.setText("当前画质：(标清)");
								} else if (cur == 3) {
									tv_ctl_huazhi.setText("当前画质：(高清)");
								} else if (cur == 4) {
									tv_ctl_huazhi.setText("当前画质：(超清)");
								}
								quality_list.addAll(list);
								Message message = handler.obtainMessage();
								message.what = 0;
								handler.sendMessage(message);
							} else {
								showToast("获取画质信息失败");
							}
						}

					});
		}
	}

	/**
	 * 设置画质
	 */
	private void setQuality() {
		if (null != ActivityLogin.session) {
			/**
			 * 参数说明： 1、通道id 2、选择的画质模式 3、回调接口
			 */
			ActivityLogin.session.SetVideoQuality(Long.valueOf(channelName),
					module, new OnSetVideoQuality() {

						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("画质设置成功");
								if (module == 1) {
									tv_ctl_huazhi.setText("当前画质：(普清)");
								} else if (module == 2) {
									tv_ctl_huazhi.setText("当前画质：(标清)");
								} else if (module == 3) {
									tv_ctl_huazhi.setText("当前画质：(高清)");
								} else if (module == 4) {
									tv_ctl_huazhi.setText("当前画质：(超清)");
								}
							} else {
								showToast("画质设置失败");
							}
						}

					});
		}
	}

	/**
	 * 显示自定义的画质选择AlertDialog
	 */
	private void showCustomAlertDialogResolution(
			final ArrayList<Integer> resolutionList) {
		customAlertDialogResolution = new AlertDialog.Builder(this).create();
		customAlertDialogResolution.show();
		customAlertDialogResolution.setCancelable(false);// 让点击对话框外面和按返回键时对话框不会消失
		Window window = customAlertDialogResolution.getWindow();
		window.setContentView(R.layout.custom_alert_dialog_resolution);
		ListView listView = (ListView) window
				.findViewById(R.id.resolution_listview);
		resolutionAdapter = new ResolutionAdapter(mContext, resolutionList);
		listView.setAdapter(resolutionAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				module = resolutionList.get(position);
				Message message = handler.obtainMessage();
				message.what = 1;
				handler.sendMessage(message);

				customAlertDialogResolution.cancel();
			}
		});

		TextView textView_resolution_cancel = (TextView) window
				.findViewById(R.id.textView_VideoControlActivity_resolution_cancel);// 画质--取消
		textView_resolution_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (null != customAlertDialogResolution) {

					customAlertDialogResolution.cancel();
				}
			}
		});
	}

	/**
	 * 设置翻转
	 */
	private void setFilp() {

		if (checkBox_flip_horizontal.isChecked()
				&& checkBox_flip_vertical.isChecked()) {
			/**
			 * 参数说明: 1、channelID为视频通道id，2、mode为要设置的模式（0:水平
			 * 1:垂直），3、callback为请求结果回调函数
			 */
			ActivityLogin.session.SetVideoOrientation(
					Long.valueOf(channelName), 0, new OnSetVideoOrientation() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("水平翻转成功");
							} else {
								showToast("水平翻转失败");
							}
						}
					});

			ActivityLogin.session.SetVideoOrientation(
					Long.valueOf(channelName), 1, new OnSetVideoOrientation() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("垂直翻转成功");
							} else {
								showToast("垂直翻转失败");
							}
						}
					});
		} else if (checkBox_flip_horizontal.isChecked()
				&& !checkBox_flip_vertical.isChecked()) {
			ActivityLogin.session.SetVideoOrientation(
					Long.valueOf(channelName), 0, new OnSetVideoOrientation() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("水平翻转成功");
							} else {
								showToast("水平翻转失败");
							}
						}
					});
		} else if (!checkBox_flip_horizontal.isChecked()
				&& checkBox_flip_vertical.isChecked()) {
			ActivityLogin.session.SetVideoOrientation(
					Long.valueOf(channelName), 1, new OnSetVideoOrientation() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("垂直翻转成功");
							} else {
								showToast("垂直翻转失败");
							}
						}
					});
		} else {
			showToast("没有选择翻转模式");
		}

		customAlertDialogFlip.cancel();

	}

	/**
	 * 显示自定义的翻转AlertDialog
	 */
	private void showCustomAlertDialogFlip() {

		customAlertDialogFlip = new AlertDialog.Builder(this).create();
		customAlertDialogFlip.show();
		customAlertDialogFlip.setCancelable(false);// 让点击对话框外面和按返回键时对话框不会消失
		Window window = customAlertDialogFlip.getWindow();
		window.setContentView(R.layout.custom_alert_dialog_flip);
		// 增加下面这两行设置，否则当点击EditText时自定义AlertDialog无法弹出输入法
		window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		// 自定义AlertDialog里面的控件的取得和点击事件的设置需在AlertDialog弹出后才能开始获取和设置，否则会出现NullPointerException异常

		checkBox_flip_horizontal = (CheckBox) window
				.findViewById(R.id.checkBox_VideoControlActivity_flip_horizontal);// 水平翻转

		checkBox_flip_vertical = (CheckBox) window
				.findViewById(R.id.checkBox_VideoControlActivity_flip_vertical);// 垂直翻转

		button_flip_cancel = (Button) window
				.findViewById(R.id.button_custom_alert_dialog_flip_cancel);// 取消
		button_flip_confirm = (Button) window
				.findViewById(R.id.button_custom_alert_dialog_flip_confirm);// 确定

		button_flip_cancel.setOnClickListener(new OnClickListener() {// 取消监听

					@Override
					public void onClick(View v) {

						customAlertDialogFlip.cancel();
					}
				});
		button_flip_confirm.setOnClickListener(new OnClickListener() {// 确定监听

					@Override
					public void onClick(View v) {

						Message message = handler.obtainMessage();
						message.what = 3;
						handler.sendMessage(message);
					}
				});
	}

	/** 获取布防能力 */
	private void getProtectAbility() {
		/** 获取布防状态 */
		if (null != ActivityLogin.session) {
			ActivityLogin.session.GetAlarmConfig(Long.valueOf(channelName),
					0, new OnGetAlarmConfig() {
						@Override
						public void on(int ret, QYAlarmConfig qyAlarm) {
							/**
							 * 返回结果说明
							 * 1、ret 大于0--成功，小于0--失败
							 * 2、QYAlarmConfig 布防配置信息
							 */
							if (ret >= 0) {
								qyAlarmObj = qyAlarm;
								qyAlarmState = qyAlarm.getEnable();
								if(0==qyAlarmState){
									if(null!=tv_ctl_bufang){
										tv_ctl_bufang.setText("当前布防状态：撤防中");
									}
								}else{
									if(null!=tv_ctl_bufang){
										tv_ctl_bufang.setText("当前布防状态：布防中");
									}
								}
								
							} else {
								showToast("获取布防信息失败");
							}
						}
					});
		}
	}
	
	/**
	 * 设置布防
	 */
	private void setAlarmShow() {
		if (null != ActivityLogin.session) {
			
			if (null != qyAlarmObj) {// 获取播放时长
				for (QYDayTimes dIndex : qyAlarmObj.getDays()) {
					if (dIndex.getTimes().length > 0) {
						System.out.println("===Week_size===:"
								+ dIndex.getTimes().length);
						for (TimeBucket bucket : dIndex.getTimes()) {
							System.out.println("=start_click_getprotection=:"
									+ bucket.getStart() + ";=end=:"
									+ bucket.getEnd());
						}
					}
				}

				if(0==qyAlarmState){
					qyAlarmObj.setEnable(1);
					qyAlarmState=1;
				}else{
					qyAlarmObj.setEnable(0);
					qyAlarmState=0;
				}

				ActivityLogin.session.SetAlarmConfig(
						Long.valueOf(channelName), 0, qyAlarmObj,
						new OnSetAlarmConfig() {
							@Override
							public void on(int ret) {
								if (ret >= 0) {
									if(0==qyAlarmObj.getEnable()){
										if(null!=tv_ctl_bufang){
											tv_ctl_bufang.setText("当前布防状态：撤防中");
										}
									}else{
										if(null!=tv_ctl_bufang){
											tv_ctl_bufang.setText("当前布防状态：布防中");
										}
									}
									showToast("设置成功");
									
								} else {
									showToast("设置失败");
								}
							}
						});
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.image_talk_switch_image:// 对讲按住
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (null != qyViewTalk) {
					qyViewTalk.CtrlTalk(false);
				} else {
					showToast("===按住对讲qyViewTalk==null=========");
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				image_talk_switch_images
						.setImageResource(R.drawable.icon_talk_initial);
				if (null != qyViewTalk) {
					qyViewTalk.CtrlTalk(true);
				} else {
					showToast("===松开对讲qyViewTalk==null=========");
				}
			}
			break;

		case R.id.imageView_videoControl_up:// 云台控制---上
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_videoControl_ups
						.setImageResource(R.drawable.up_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_MOVE_UP,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住上成功");
									} else {
										showToast("按住上失败" + String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_videoControl_ups
						.setImageResource(R.drawable.up_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止上成功");
							} else {
								showToast("停止上失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_videoControl_left:// 云台控制---左
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_videoControl_lefts
						.setImageResource(R.drawable.left_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_MOVE_LEFT,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住左成功");
									} else {
										showToast("按住左失败" + String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_videoControl_lefts
						.setImageResource(R.drawable.left_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止左成功");
							} else {
								showToast("停止左失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_videoControl_down:// 云台控制---下
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_videoControl_downs
						.setImageResource(R.drawable.down_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_MOVE_DOWN,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住下成功");
									} else {
										showToast("按住下失败" + String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_videoControl_downs
						.setImageResource(R.drawable.down_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止下成功");
							} else {
								showToast("停止下失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_videoControl_right:// 云台控制---右
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_videoControl_rights
						.setImageResource(R.drawable.right_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_MOVE_RIGHT,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住右成功");
									} else {
										showToast("按住右失败" + String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_videoControl_rights
						.setImageResource(R.drawable.right_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止右成功");
							} else {
								showToast("停止右失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_videoControl_center:// 云台控制---回位
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_videoControl_centers
						.setImageResource(R.drawable.reset_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_MOVE_CENTER,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住回位成功");
									} else {
										showToast("按住回位失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_videoControl_centers
						.setImageResource(R.drawable.reset_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止回位成功");
							} else {
								showToast("停止回位失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_video_zoom_reduce:// 云台控制变倍 ---减
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_video_zoom_reduces
						.setImageResource(R.drawable.icon_yuntai_reduce_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_ZOOM_OUT,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住变倍减成功");
									} else {
										showToast("按住变倍减失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_video_zoom_reduces
						.setImageResource(R.drawable.icon_yuntai_reduce_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止变倍减成功");
							} else {
								showToast("停止变倍减失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_video_zoom_add:// 云台控制变倍 ---加
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_video_zoom_adds
						.setImageResource(R.drawable.icon_yuntai_add_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_ZOOM_IN,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住变倍加成功");
									} else {
										showToast("按住变倍加失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_video_zoom_adds
						.setImageResource(R.drawable.icon_yuntai_add_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止变倍加成功");
							} else {
								showToast("停止变倍加失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_video_aperture_reduce:// 云台控制光圈 ---减
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_video_aperture_reduces
						.setImageResource(R.drawable.icon_yuntai_reduce_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_IRIS_OUT,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住光圈减成功");
									} else {
										showToast("按住光圈减失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_video_aperture_reduces
						.setImageResource(R.drawable.icon_yuntai_reduce_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止光圈减成功");
							} else {
								showToast("停止光圈减失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_video_aperture_add:// 云台控制光圈 ---加
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_video_aperture_adds
						.setImageResource(R.drawable.icon_yuntai_add_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_IRIS_IN,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住光圈加成功");
									} else {
										showToast("按住光圈加失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_video_aperture_adds
						.setImageResource(R.drawable.icon_yuntai_add_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止光圈加成功");
							} else {
								showToast("停止光圈加失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_video_focus_reduce:// 云台控制焦距---减
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_video_focus_reduces
						.setImageResource(R.drawable.icon_yuntai_reduce_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_FOCUS_FAR,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住焦距减成功");
									} else {
										showToast("按住焦距减失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_video_focus_reduces
						.setImageResource(R.drawable.icon_yuntai_reduce_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止焦距减成功");
							} else {
								showToast("停止焦距减失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;

		case R.id.imageView_video_focus_add:// 云台控制焦距---加
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView_video_focus_adds
						.setImageResource(R.drawable.icon_yuntai_add_pressed);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_FOCUS_NEAR,
							new OnCtrlPTZ() {
								@Override
								public void on(int ret) {
									if (ret >= 0) {
										showToast("按住焦距加成功");
									} else {
										showToast("按住焦距加失败"
												+ String.valueOf(ret));
									}
								}
							});
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				imageView_video_focus_adds
						.setImageResource(R.drawable.icon_yuntai_add_normal);
				if (null != qyViewWatch) {
					qyViewWatch.CtrlPTZ(0, QYView.ACTION_STOP, new OnCtrlPTZ() {
						@Override
						public void on(int ret) {
							if (ret >= 0) {
								showToast("停止焦距加成功");
							} else {
								showToast("停止焦距加失败" + String.valueOf(ret));
							}
						}
					});
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

	public void showToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
