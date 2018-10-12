package com.tricheer.traffic;

import android.app.Application;
import android.content.Context;

/**
 * Created by yangbofeng on 2018/1/24.
 */
public class MyApplicationg extends Application {
    public static  Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static Context getContext(){
        return  mContext;
    }
}
