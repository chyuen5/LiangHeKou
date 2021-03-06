package com.cizhu.application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;

public class App extends Application
{
    private static String m_str_deviceToken;

    private static Context mContext;

    private SharedPreferences sp;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = getApplicationContext();

        sp = getSharedPreferences("userdata", 0);

        //UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
        UMConfigure.init(this, "5adb001ff29d981f21000059", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "5152f5b388deeeac6e824a6583a414e1");

        PushAgent mPushAgent = PushAgent.getInstance(this);

        HuaWeiRegister.register(this);

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback()
        {
            @Override
            public void onSuccess(String deviceToken)
            {
                //注册成功会返回device token
                m_str_deviceToken = deviceToken;
                //android.util.Log.i("cjwsjy", "--------deviceToken1="+deviceToken+"-------");
                //android.util.Log.i("cjwsjy", "--------deviceToken2="+m_str_deviceToken+"-------");

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("deviceToken_id", m_str_deviceToken);
                editor.commit();
            }

            @Override
            public void onFailure(String s, String s1)
            {
                //android.util.Log.i("cjwsjy", "--------error=1013------youMeng");

            }
        });

        UmengMessageHandler messageHandler = new UmengMessageHandler()
        {
            //通知的回调方法（通知送达时会回调）
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。
                super.dealWithNotificationMessage(context, msg);
            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler()
        {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    public static Context getInstance() {
        return mContext;
    }
}
