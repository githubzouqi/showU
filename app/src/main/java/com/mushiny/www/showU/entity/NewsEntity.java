package com.mushiny.www.showU.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 新闻头条
 */
public class NewsEntity implements Serializable {

    private String reason;// 返回说明
    private ResultBean result;
    private int error_code;// 0 表示成功，非 0 值表示错误

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class ResultBean implements Serializable{

        private String stat;
        private List<DataBean> data;

        public String getStat() {
            return stat;
        }

        public void setStat(String stat) {
            this.stat = stat;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean implements Serializable{
            /**
             * uniquekey : 55c02075a094ff5cbd8ec7209ff617f7
             * title : 日本参议员人均申报财产为17万美元 创历史新低
             * date : 2020-01-06 21:37
             * category : 国际
             * author_name : 环球网
             * url : http://mini.eastday.com/mobile/200106213708835.html
             * thumbnail_pic_s : http://00imgmini.eastday.com/mobile/20200106/20200106213708_642c0ba65c63853c707eb47149ce5edc_1_mwpm_03200403.jpg
             * thumbnail_pic_s02 : http://08imgmini.eastday.com/mobile/20200106/20200106213516_09365c5c05d6a26d6197f7d5f692d04a_1_mwpm_03200403.jpg
             * thumbnail_pic_s03 : http://08imgmini.eastday.com/mobile/20200106/20200106213516_09365c5c05d6a26d6197f7d5f692d04a_2_mwpm_03200403.jpg
             */

            private String uniquekey;
            private String title;// 标题
            private String date;// 日期
            private String category;// 标签
            private String author_name;// 作者名
            private String url;// 新闻访问链接

            // 图片
            private String thumbnail_pic_s;
            private String thumbnail_pic_s02;
            private String thumbnail_pic_s03;

            public String getUniquekey() {
                return uniquekey;
            }

            public void setUniquekey(String uniquekey) {
                this.uniquekey = uniquekey;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getAuthor_name() {
                return author_name;
            }

            public void setAuthor_name(String author_name) {
                this.author_name = author_name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getThumbnail_pic_s() {
                return thumbnail_pic_s;
            }

            public void setThumbnail_pic_s(String thumbnail_pic_s) {
                this.thumbnail_pic_s = thumbnail_pic_s;
            }

            public String getThumbnail_pic_s02() {
                return thumbnail_pic_s02;
            }

            public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
                this.thumbnail_pic_s02 = thumbnail_pic_s02;
            }

            public String getThumbnail_pic_s03() {
                return thumbnail_pic_s03;
            }

            public void setThumbnail_pic_s03(String thumbnail_pic_s03) {
                this.thumbnail_pic_s03 = thumbnail_pic_s03;
            }
        }
    }
}
