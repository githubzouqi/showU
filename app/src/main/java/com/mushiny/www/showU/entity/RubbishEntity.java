package com.mushiny.www.showU.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 垃圾分类查询返回数据实体
 */
public class RubbishEntity implements Serializable {

    private static final long serialVersionUID = 2753979973567214247L;

    /**
     * code : 1
     * msg : 数据返回成功！
     * data : {"aim":{"goodsName":"馒头","goodsType":"湿垃圾"},"recommendList":[{"goodsName":"馒头",
     * "goodsType":"湿垃圾"},{"goodsName":"馒头垫纸","goodsType":"干垃圾"}]}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * aim : {"goodsName":"馒头","goodsType":"湿垃圾"}
         * recommendList : [{"goodsName":"馒头","goodsType":"湿垃圾"},{"goodsName":"馒头垫纸","goodsType":"干垃圾"}]
         */

        private AimBean aim;
        private List<RecommendListBean> recommendList;

        public AimBean getAim() {
            return aim;
        }

        public void setAim(AimBean aim) {
            this.aim = aim;
        }

        public List<RecommendListBean> getRecommendList() {
            return recommendList;
        }

        public void setRecommendList(List<RecommendListBean> recommendList) {
            this.recommendList = recommendList;
        }

        public static class AimBean {
            /**
             * goodsName : 馒头
             * goodsType : 湿垃圾
             */

            private String goodsName;
            private String goodsType;

            public String getGoodsName() {
                return goodsName;
            }

            public void setGoodsName(String goodsName) {
                this.goodsName = goodsName;
            }

            public String getGoodsType() {
                return goodsType;
            }

            public void setGoodsType(String goodsType) {
                this.goodsType = goodsType;
            }
        }

        public static class RecommendListBean {
            /**
             * goodsName : 馒头
             * goodsType : 湿垃圾
             */

            private String goodsName;
            private String goodsType;

            public String getGoodsName() {
                return goodsName;
            }

            public void setGoodsName(String goodsName) {
                this.goodsName = goodsName;
            }

            public String getGoodsType() {
                return goodsType;
            }

            public void setGoodsType(String goodsType) {
                this.goodsType = goodsType;
            }
        }
    }
}
