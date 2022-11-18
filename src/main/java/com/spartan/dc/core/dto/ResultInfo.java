package com.spartan.dc.core.dto;


/**
 * Desc：Result Info
 *
 * @Created by 2018-05-27 10:06
 */
public class ResultInfo<T> {

    /**
     * code
     */
    private int code;

    /**
     * msg
     */
    private String msg;

    /**
     * return data
     */
    private T data;


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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResultInfo(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    public ResultInfo(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
