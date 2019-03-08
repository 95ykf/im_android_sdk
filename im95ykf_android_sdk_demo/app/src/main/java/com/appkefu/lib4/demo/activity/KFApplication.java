package com.appkefu.lib4.demo.activity;

import android.app.Application;

import com.appkefu.lib.interfaces.KFAPIs;


public class KFApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //开启调试模式, 正式上线应关闭
        KFAPIs.DEBUG = true;
    }
    }
