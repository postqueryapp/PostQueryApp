package com.app.postqueryapp.dto;

/**
 * 物流信息， 用于构建 物流查询结果的滑动控件的 适配器
 */
public class QueryInformation {

    /**
     * 物流信息
     */
    private String info;

    /**
     * 物流状态，2-途中，3-签收，4-问题件
     */
    private String status;

    /**
     * 信息时间
     */
    private String time;

    public QueryInformation() {

    }

    public QueryInformation(String info,  String time,String status) {
        this.info = info;
        this.status = status;
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
