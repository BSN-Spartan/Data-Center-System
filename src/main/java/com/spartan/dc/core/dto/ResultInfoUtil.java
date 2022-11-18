package com.spartan.dc.core.dto;



public class ResultInfoUtil {

    public static final int SUCCESS_CODE = 1;

    public static final int ERROR_CODE = 0;

    public static final int SYSTEM_ERROR_CODE = 3;

    public static ResultInfo errorResult(String message) {
        return errorResult(message, "");
    }

    public static ResultInfo errorResult(String message, Object data) {
        return createResult(ERROR_CODE, message, data);
    }

    private static ResultInfo createResult(int code, String message, Object data) {
        return new ResultInfo(code, message, data);
    }

    public static ResultInfo sysErrorResult(String message) {
        return createResult(SYSTEM_ERROR_CODE, message, "");
    }

    public static ResultInfo errorResult(int code, String message) {
        return createResult(code, message, null);
    }

    public static ResultInfo successResult(Object data) {
        return createResult(SUCCESS_CODE, "SUCCESS", data);
    }

    public static ResultInfo successResult() {
        return successResult(null);
    }

    public static <T> ResultInfo<T> success() {
        return new ResultInfo(SUCCESS_CODE, "SUCCESS");
    }

    public static <T> ResultInfo<T> failure(String messageCode) {
        return new ResultInfo(ERROR_CODE, messageCode);
    }

}
