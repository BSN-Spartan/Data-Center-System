package com.spartan.dc.model.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : rjx
 * @Date : 2022/10/11 16:51
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRespVO {

    private String tradeNo;

    private String otherTradeNo;

    private String orderUrl;

}
