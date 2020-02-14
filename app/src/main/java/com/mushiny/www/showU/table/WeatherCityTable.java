package com.mushiny.www.showU.table;

import org.litepal.crud.LitePalSupport;

/**
 *  数据库中的表：保存支持的城市列表数据
 */
public class WeatherCityTable extends LitePalSupport {

    private String district_id;// 地区的id标识

    private String province;// 省份

    private String city;// 城市

    private String district;// 区或县

    public WeatherCityTable() {
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
