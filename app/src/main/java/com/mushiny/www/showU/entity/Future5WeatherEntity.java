package com.mushiny.www.showU.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 未来5天天气实体
 */
public class Future5WeatherEntity implements Parcelable {

    private String future_date;// 日期：2020-02-11
    private String future_temperature;// 早晚温度：-3/11℃
    private String future_weather;// 天气状况：晴转雾
    private String future_direct;// 风向：南风转北风

    public Future5WeatherEntity() {
    }

    public String getFuture_date() {
        return future_date;
    }

    public void setFuture_date(String future_date) {
        this.future_date = future_date;
    }

    public String getFuture_temperature() {
        return future_temperature;
    }

    public void setFuture_temperature(String future_temperature) {
        this.future_temperature = future_temperature;
    }

    public String getFuture_weather() {
        return future_weather;
    }

    public void setFuture_weather(String future_weather) {
        this.future_weather = future_weather;
    }

    public String getFuture_direct() {
        return future_direct;
    }

    public void setFuture_direct(String future_direct) {
        this.future_direct = future_direct;
    }

    @Override
    public String toString() {
        return "Future5WeatherEntity{" +
                "future_date='" + future_date + '\'' +
                ", future_temperature='" + future_temperature + '\'' +
                ", future_weather='" + future_weather + '\'' +
                ", future_direct='" + future_direct + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.future_date);
        dest.writeString(this.future_temperature);
        dest.writeString(this.future_weather);
        dest.writeString(this.future_direct);
    }

    protected Future5WeatherEntity(Parcel in) {
        this.future_date = in.readString();
        this.future_temperature = in.readString();
        this.future_weather = in.readString();
        this.future_direct = in.readString();
    }

    public static final Parcelable.Creator<Future5WeatherEntity> CREATOR = new Parcelable.Creator<Future5WeatherEntity>() {
        @Override
        public Future5WeatherEntity createFromParcel(Parcel source) {
            return new Future5WeatherEntity(source);
        }

        @Override
        public Future5WeatherEntity[] newArray(int size) {
            return new Future5WeatherEntity[size];
        }
    };
}
