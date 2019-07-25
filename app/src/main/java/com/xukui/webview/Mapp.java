package com.xukui.webview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class Mapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
    }

    /**
     * 注册内存泄漏检查器
     */
    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

}