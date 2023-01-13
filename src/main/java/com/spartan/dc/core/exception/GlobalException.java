package com.spartan.dc.core.exception;


public class GlobalException extends RuntimeException {

    private int code;

    private String msg;

    public GlobalException(String message) {
        super(message);
        this.msg = message;
        this.code = -1;
    }
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
}
