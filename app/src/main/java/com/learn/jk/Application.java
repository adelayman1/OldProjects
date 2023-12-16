package com.learn.jk;

import android.content.Context;

public class Application extends android.app.Application {
    public static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext=getApplicationContext();
    }
}
