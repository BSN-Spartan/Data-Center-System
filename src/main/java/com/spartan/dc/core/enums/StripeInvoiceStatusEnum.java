package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/10/18 11:06
 * @description stripe invoice status
 */
public enum StripeInvoiceStatusEnum {
    DRAFT("draft"),
    OPEN("open"),
    PAID("paid"),
    VOID_INVOICE("void"),
    UN_COLLECTIBLE("uncollectible");

    private final String value;

    private StripeInvoiceStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
