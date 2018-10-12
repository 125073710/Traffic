// IMyAidlInterface.aidl
package com.tricheer.traffic;

// Declare any non-default types here with import statements
import com.tricheer.traffic.AppInfoBean;
interface IMyAidlInterface {
   List<AppInfoBean> getNowData();
   List<AppInfoBean> getHistoryData(long start,long end);
}
