package com.spartan.dc.core.enums;

/**
 * Response status code enumeration
 *
 * @author xingjie
 * @date 2019-12-24
 */
public enum ApiRespCodeEnum {

    SUCCESS("0000", "SUCCESS"),

    REQ_CODE_NULL("1000", "Request ID cannot be empty"),
    EMAIL_NULL("MSG_1001", "Mailbox cannot be empty"),
    NTT_ADDRESS_NULL("1002", "NTT account address cannot be empty"),
    CAPTCHA_NULL("1003", "Verification code cannot be empty"),

    REQ_CODE_FORMAT("2000", "Request unique ID format is incorrect"),
    EMAIL_FORMAT("MSG_2001", "Incorrect mailbox format"),
    NTT_ACCOUNT_FORMAT("2002", "NTT account address is not in the correct format"),

    REQ_CODE_REPEAT("3001", "Verification code has been sent"),
    REQ_CODE_INVALID("3002", "Verification code is no longer valid"),
    REQ_CODE_FALSE("3003", "Incorrect verification code"),

    EMAIL_EXIST_FALSE("3004", "The mailbox is already registered"),
    REQ_ERROR("MSG_5000", "Invalid request"),
    DATA_CENTER_EXISTS("MSG_4001", "The data center party already exists"),
    DATA_CENTER_NO_EXISTS("MSG_4002", "Data center party does not exist"),




    ;


    private final String code;

    private final String message;

    ApiRespCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public static String getApiResultStateEnum(String code) {
        for (ApiRespCodeEnum c : ApiRespCodeEnum.values()) {
            if (c.code.equals(code)) {
                return c.getMessage();
            }
        }
        return null;
    }

}
