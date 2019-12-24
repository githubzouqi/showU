package com.mushiny.www.showU.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 笑话大全实体类
 */
public class JokerCollectionEntity implements Serializable{

    /**
     * reason : Success
     * error_code : 0
     */

    private String reason;
    private ResultBean result;
    private int error_code;

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

    @Override
    public String toString() {
        return "JokerCollectionEntity{" +
                "reason='" + reason + '\'' +
                ", result=" + result +
                ", error_code=" + error_code +
                '}';
    }

    public static class ResultBean implements Serializable {
        private List<DataBean> data;

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "data=" + data +
                    '}';
        }

        public static class DataBean implements Serializable{
            /**
             * content : &nbsp; &nbsp; 小区门口修车师傅生意特好。人实在,只要不换零件，常常不收钱或只收个块把钱辛苦钱。最近发现他心黑了，价钱明显上调,就连充个气也收1块钱。旁边书报亭大妈是知情人，: 唉，多担待一下吧！他家上个月二胎,一窝生了四个带把的。。。。
             * hashId : 2e010657b420dcee335c870bf9a18301
             * unixtime : 1559789702
             * updatetime : 2019-06-06 10:55:02
             */

            private String content;
            private String hashId;
            private int unixtime;
            private String updatetime;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getHashId() {
                return hashId;
            }

            public void setHashId(String hashId) {
                this.hashId = hashId;
            }

            public int getUnixtime() {
                return unixtime;
            }

            public void setUnixtime(int unixtime) {
                this.unixtime = unixtime;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            @Override
            public String toString() {
                return "DataBean{" +
                        "content='" + content + '\'' +
                        ", hashId='" + hashId + '\'' +
                        ", unixtime=" + unixtime +
                        ", updatetime='" + updatetime + '\'' +
                        '}';
            }
        }
    }
}
