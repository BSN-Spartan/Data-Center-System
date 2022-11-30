package com.spartan.dc.core.enums;

/**
 *
 *
 * @author syt
 * @version V1.0
 * @date 2022/7/21 14:17
 */
public enum MsgCodeEnum {

    EMAIL_TEMPLATE("email_template", "Email Templates"),
    EMAIL_CONTACT_TEMPLATE_("contact_template_", "Email Contact Template"),
    USER_JOIN_CAPTCHA_("user_join_captcha_", "User access to get verification code"),
    GAS_RECHARGE_CAPTCHA_("gas_recharge_captcha_", "Verification code of GAS top-up"),
    ORDER_SUBMIT_SUCCESS("order_submit_success_", "Order placed successfully"),
    PAYMENT_SUCCESS("payment_success_", "Payment successful"),
    NTT_ARRIVED_SUCCESS("ntt_arrived_success_", "NTT arrival"),
    KEY_STORE_PASSWORD_RESET("keystorePasswordReset", "keystorePasswordReset"),

    KEY_STORE_PASSWORD_CONFIG("keystorePasswordConfig", "keystorePasswordConfig"),

    ORDER_SUBMITTED_OFFLINE("order_submit_offline_", "Remittance order placed successfully"),
    GAS_RECHARGE_SUCCESS("gas_recharge_success_", "Gas recharge success");


    private String code;

    private String name;

    MsgCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MsgCodeEnum getEnumByCode(String code) {
        if (code == null) {
            return null;
        }
        for (MsgCodeEnum e : MsgCodeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
