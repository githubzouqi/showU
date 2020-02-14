package com.mushiny.www.showU.entity;

import java.util.List;

/**
 * 天气查询 支持的城市列表
 */
public class WeatherCityListEntity {


    /**
     * reason : 查询成功
     * error_code : 0
     */

    private String reason;// 返回数据 成功说明 或 失败原因
    private int error_code;// 返回 0 表示成功，其他则为失败
    private List<ResultBean> result;// 城市列表结果集

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 1
         * province : 北京
         * city : 北京
         * district : 北京
         */

        private String id;// 某个城市的具体 id
        private String province;// 省
        private String city;// 市
        private String district;// 区 或 县

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
}
