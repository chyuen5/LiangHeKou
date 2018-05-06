package com.cizhu.application;

import android.app.Application;
import android.content.Context;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

public class App extends Application
{
    private static String m_str_deviceToken;

    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = getApplicationContext();

        //UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
        UMConfigure.init(this, "5adb001ff29d981f21000059", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "5152f5b388deeeac6e824a6583a414e1");

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback()
        {
            @Override
            public void onSuccess(String deviceToken)
            {
                //注册成功会返回device token
                m_str_deviceToken = deviceToken;
                android.util.Log.i("cjwsjy", "--------deviceToken1="+deviceToken+"-------");
                android.util.Log.i("cjwsjy", "--------deviceToken2="+m_str_deviceToken+"-------");
            }

            @Override
            public void onFailure(String s, String s1)
            {
                android.util.Log.i("cjwsjy", "--------error=1013------youMeng");

            }
        });
    }

    public static Context getInstance() {
        return mContext;
    }
}
