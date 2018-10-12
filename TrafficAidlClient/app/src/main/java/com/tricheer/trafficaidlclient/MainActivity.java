package com.tricheer.trafficaidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tricheer.traffic.AppInfoBean;
import com.tricheer.traffic.IMyAidlInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  String TAG ="yyy";
    private IMyAidlInterface service;
    private Long start;
    private Long end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "oncreat");
        start = toDate("2018-01-16").getTime();
        end = System.currentTimeMillis();
        Log.e("yyyy", "[start]=" + start + "[end]" + end);
        init();
    }

    private void init() {
        //bindService
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.tricheer.traffic", "com.tricheer.traffic.TrafficService"));
        intent.setAction("com.tricheer.traffic.service");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService");
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.e("yyyy", "onServiceDisconnected:" + arg0.getPackageName());
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.e(TAG, "onServiceConnected:" + name.getPackageName());
            // 获取远程Service的onBinder方法返回的对象代理
            service = IMyAidlInterface.Stub.asInterface(binder);
            try {
                Log.e(TAG, (service.getNowData()).size() + "【】");
                List<AppInfoBean> list = service.getNowData();
                for (AppInfoBean app : list) {
                    Log.e("yyyy", "[uid]" + app.getUid() + "[name]=" + app.getName() + "[total]" + app.getTotal());
                }
                Log.e(TAG, "-----------------------------------------");
                Log.e(TAG, (service.getHistoryData(start, end)).size() + "【】");
                List<AppInfoBean> lists = service.getHistoryData(start, end);
                for (AppInfoBean app : lists) {
                    Log.e(TAG, "[histoty]" + "[uid]" + app.getUid() + "[name]=" + app.getName() + "[total]" + app.getTotal());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    public static Date toDate(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

}
