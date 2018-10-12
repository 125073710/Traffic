package com.tricheer.traffic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.net.NetworkTemplate.buildTemplateMobileAll;

/**
 * Created by yangbofeng on 2018/1/24.
 */
public class TrafficService extends Service {
    private  String TAG ="TrafficService";
    private Context mContext;
    Resources res;
    private static String TEST_SUBSCRIBER_PROP = "test.subscriberid";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=  MyApplicationg.getContext();
        res = mContext.getResources();
    }
    IMyAidlInterface.Stub stub = new IMyAidlInterface.Stub(){


        @Override
        public List<AppInfoBean> getNowData() throws RemoteException {
            return inivData();
        }

        @Override
        public List<AppInfoBean> getHistoryData(long start, long end) throws RemoteException {
            return inivDataHistory(start,end);
        }
    };
    public List inivData() {
        INetworkStatsService mStatsService = null;
        INetworkStatsSession mStatsSession = null;
        NetworkTemplate mTemplate = null;
        NetworkStats sta = null;
        List<AppInfoBean>  lists = new ArrayList<>();

        try {
            long start = getStarttime();
            long end =System.currentTimeMillis();
            Log.e(TAG, "[start]=" + start + "[end]=" + end);
            mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
            Log.e(TAG, "[mStatsService]="+mStatsService);
            mStatsSession = mStatsService.openSession();
            Log.e(TAG, "[mStatsSession]="+mStatsSession);
            mTemplate = buildTemplateMobileAll(getActiveSubscriberId(mContext));
            Log.e(TAG, "[getActiveSubscriberId(mContext)]="+getActiveSubscriberId(mContext));
            Log.e(TAG, "[mTemplate]="+mTemplate);
            sta = mStatsSession.getSummaryForAllUid(mTemplate, start, end, true);
            NetworkStats.Entry entry = null;
            int size = sta != null ? sta.size() : 0;
            Log.e(TAG, "[service-size-]" + size);

            for (int i = 0; i < size; i++) {
                entry = sta.getValues(i, entry);
                final int uid = entry.uid;
                long totalapp = entry.rxBytes + entry.txBytes;
                String sumtotal = Formatter.formatFileSize(mContext, totalapp);
                String name = null;
                switch (uid) {
                    case android.os.Process.SYSTEM_UID:
                        name = res.getString(R.string.process_kernel_label);
                        break;
                    case -4:
                        name = res.getString(R.string.data_usage_uninstalled_apps);
                        break;
                    case -5:
                        name = res.getString(R.string.tether_settings_title_all);
                        break;
                    default:

                       if(uid< 9999){
                            name = "null";
                        }else {
                            try {
                                name = getProgramNameByPackageName(mContext, uid);
                            } catch (Exception e) {
                                Log.e(TAG, "package name is null");
                            }
                        }

                        break;

                }
                AppInfoBean appInfo = new AppInfoBean();
                appInfo.setName(name);
                appInfo.setTotal(sumtotal);
                appInfo.setUid(uid);
                lists.add(appInfo);
                Log.e(TAG, "[uid]=" + uid + "[name]=" + name + "[total]=" + sumtotal);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            e.toString();
        }
        return  lists;
    }
    public List inivDataHistory(long start ,long end) {
        INetworkStatsService mStatsService = null;
        INetworkStatsSession mStatsSession = null;
        NetworkTemplate mTemplate = null;
        NetworkStats sta = null;
        List<AppInfoBean>  lists = new ArrayList<>();

        try {
            if(start == 0){
                start = getStarttime();
            }
           if(end == 0){
               end =System.currentTimeMillis();
           }

            Log.e(TAG, "[start]=" + start + "[end]=" + end);
            mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
            Log.e(TAG, "[mStatsService]="+mStatsService);
            mStatsSession = mStatsService.openSession();
            Log.e(TAG, "[mStatsSession]="+mStatsSession);
            mTemplate = buildTemplateMobileAll(getActiveSubscriberId(mContext));
            Log.e(TAG, "[getActiveSubscriberId(mContext)]="+getActiveSubscriberId(mContext));
            Log.e(TAG, "[mTemplate]="+mTemplate);
            sta = mStatsSession.getSummaryForAllUid(mTemplate, start, end, true);
            NetworkStats.Entry entry = null;
            int size = sta != null ? sta.size() : 0;
            Log.e(TAG, "[service-size-]" + size);

            for (int i = 0; i < size; i++) {
                entry = sta.getValues(i, entry);
                final int uid = entry.uid;
                long totalapp = entry.rxBytes + entry.txBytes;
                String sumtotal = Formatter.formatFileSize(mContext, totalapp);
                String name = null;
                switch (uid) {
                    case android.os.Process.SYSTEM_UID:
                        name = res.getString(R.string.process_kernel_label);
                        break;
                    case -4:
                        name = res.getString(R.string.data_usage_uninstalled_apps);
                        break;
                    case -5:
                        name = res.getString(R.string.tether_settings_title_all);
                        break;
                    default:
                        if(uid< 9999){
                            name = "null";
                        }else {
                            try {
                                name = getProgramNameByPackageName(mContext, uid);
                            } catch (Exception e) {
                                Log.e(TAG, "package name is null");
                            }
                        }
                        break;

                }
                AppInfoBean appInfo = new AppInfoBean();
                appInfo.setName(name);
                appInfo.setTotal(sumtotal);
                appInfo.setUid(uid);
                lists.add(appInfo);
                Log.e(TAG, "[uid]=" + uid + "[name]=" + name + "[total]=" + sumtotal);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return  lists;
    }
    /**
     *
     * @Title: getProgramNameByPackageName
     * @Description: TODO(依据包名获取APP名字)
     * @param context
     * @param uid
     * @return 参数
     * @return String 返回类型
     * @throws
     */
    public String getProgramNameByPackageName(Context context, int uid) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(getCallerProcessName(uid), PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
   /* *
            *
            * @Title: getStarttime
    * @Description: TODO(获取每月1号的时间)
    * @return 参数
    * @return long 返回类型
    * @throws
            */
    public long getStarttime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        String first = format.format(c.getTime());
        Date longToDate = toDate(first);
        long start = longToDate.getTime();
        Log.e(TAG, "本月first day==" + longToDate.getTime());
        return start;
    }

    /**
     *
     * @Title: toDate
     * @Description: TODO(从字符串, 获取日期, 如time = "2016-3-16 4:12:16")
     * @param time
     * @return 参数
     * @return Date 返回类型
     * @throws
     */
    public static Date toDate(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }
    /**
     *
     * @Title: getCallerProcessName
     * @Description: TODO 获取包名
     * @param uid
     * @return 参数
     * @return String 返回类型
     * @throws
     */
    private String getCallerProcessName(int uid) {
        String callingApp = mContext.getPackageManager().getNameForUid(uid);
        return callingApp;
    }

    private String getActiveSubscriberId(Context context) {
        final TelephonyManager tele = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String actualSubscriberId = tele.getSubscriberId();
        return SystemProperties.get(TEST_SUBSCRIBER_PROP, actualSubscriberId);
    }
}
