package com.spartan.dc.core.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @Author : rjx
 * @Date : 2022/10/12 17:07
 **/
@Data
@Builder
public class CreateChargeReqDTO {
    private String name;

    private String description;

    @JsonProperty("pricing_type")
    private String pricingType;

    @JsonProperty("local_price")
    private PricingEntry localPrice;

    private Map<String, Object> metadata;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("cancel_url")
    private String cancelUrl;
}
