package com.tricheer.traffic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yangbofeng on 2018/1/24.
 */
public class AppInfoBean implements Parcelable {
    private String name;
    private String total;
    private int uid;
    public AppInfoBean() {

    }
    public AppInfoBean(int uid, String name, String total) {
        super();
        this.uid = uid;
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeString(name);
        parcel.writeString(total);
    }

    // 添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Creator<AppInfoBean> CREATOR
            = new Creator<AppInfoBean>() {
        @Override
        public AppInfoBean createFromParcel(Parcel source) {
            // 从Parcel中读取数据，返回Person对象
            return new AppInfoBean(source.readInt()
                    , source.readString()
                    , source.readString());
        }

        @Override
        public AppInfoBean[] newArray(int size) {
            return new AppInfoBean[size];
        }
    };
}