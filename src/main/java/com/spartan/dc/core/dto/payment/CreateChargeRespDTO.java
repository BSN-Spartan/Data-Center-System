package com.spartan.dc.core.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonRootName("data")
public class CreateChargeRespDTO {

    private String id;

    private String resource;

    private String code;

    private String name;

    private String description;

    @JsonProperty("hosted_url")
    private String hostedUrl;

    @JsonProperty("support_email")
    String supportEmail;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("expires_at")
    private Date expiresAt;

    @JsonProperty("confirmed_at")
    private Date confirmedAt;

    private Object checkout;

    @JsonProperty("exchange_rates")
    private Map<String, String> exchangeRates;

    private List<Timeline> timeline;

    private Map<String, Object> metadata;

    @JsonProperty("pricing_type")
    private String pricingType;

    private Pricing pricing;

    private PaymentThreshold payment_threshold;

    private PricingEntry applied_threshold;

    private String applied_threshold_type;

    private List<Payment> payments;

    private Addresses addresses;

    @Data
    public static class Timeline {
        public Date time;
        public String status;
        public String context;

    }

    @Data
    public static class Pricing {
        private PricingEntry local;
        private PricingEntry bitcoin;
        private PricingEntry ethereum;

    }

    @Data
    public static class PaymentThreshold {
        private PricingEntry overpayment_absolute_threshold;
        private String overpayment_relative_threshold;
        private PricingEntry underpayment_absolute_threshold;
        private String underpayment_relative_threshold;

    }

    @Data
    public static class Value {
        private PricingEntry local;
        private PricingEntry crypto;

    }

    @Data
    public static class Block {
        private int height;
        private String hash;
        private int confirmations_accumulated;
        private int confirmations_required;

    }

    @Data
    public static class Payment {
        private String network;
        @JsonProperty("transaction_id")
        private String transactionId;
        private String status;
        @JsonProperty("detected_at")
        private Date detectedAt;
        private Value value;
        private Block block;

    }

    @Data
    public static class Addresses {
        private String bitcoin;
        private String ethereum;

    }

}