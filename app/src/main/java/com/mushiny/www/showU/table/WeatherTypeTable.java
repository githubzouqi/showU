package com.mushiny.www.showU.table;

import org.litepal.crud.LitePalSupport;

/**
 * 数据库表结构：天气种类列表
 */
public class WeatherTypeTable extends LitePalSupport {

    private String wid;
    private String weather;

    public WeatherTypeTable() {
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
