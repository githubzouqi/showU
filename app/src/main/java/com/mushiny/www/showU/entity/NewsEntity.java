package com.mushiny.www.showU.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 新闻类型 - 新闻列表
 */
public class NewsEntity implements Serializable {

    private static final long serialVersionUID = -3761368026437740705L;

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : 为什么满大街都是手机专卖店，没人买也不会倒闭？其实里面水很深
         * imgList : ["http://bjnewsrec-cv.ws.126.net/three918a834a953j00qb51z10013c000hs00alc.jpg"]
         * source : 针灸匠小嫚
         * newsId : FDT460KJ0517N88S
         * digest :
         * postTime : 2020-05-30 17:49:50
         * videoList : null
         */

        private String title;
        private String source;
        private String newsId;
        private String digest;
        private String postTime;
        private Object videoList;
        private List<String> imgList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getNewsId() {
            return newsId;
        }

        public void setNewsId(String newsId) {
            this.newsId = newsId;
        }

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public String getPostTime() {
            return postTime;
        }

        public void setPostTime(String postTime) {
            this.postTime = postTime;
        }

        public Object getVideoList() {
            return videoList;
        }

        public void setVideoList(Object videoList) {
            this.videoList = videoList;
        }

        public List<String> getImgList() {
            return imgList;
        }

        public void setImgList(List<String> imgList) {
            this.imgList = imgList;
        }
    }
}
