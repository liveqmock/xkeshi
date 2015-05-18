package com.xkeshi.pojo.vo;

/**
 * 用于api返回结果
 * <p/>
 * Created by david-y on 2015/1/19.
 */
public class Result {
    public String res;
    public String description;
    public Object result;

    public Result() {
    }

    public Result(String description, String res) {
        this.description = description;
        this.res = res;
    }

    public Result(String res, String description, Object result) {
        this.res = res;
        this.description = description;
        this.result = result;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
