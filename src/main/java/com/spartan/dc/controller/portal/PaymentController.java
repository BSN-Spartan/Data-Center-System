package com.spartan.dc.controller.portal;

import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.RefundStateEnum;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.model.vo.req.*;
import com.spartan.dc.model.vo.resp.*;
import com.spartan.dc.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

/**
 * @Author : rjx
 * @Date : 2022/10/10 17:29
 **/
@RestController
@RequestMapping("/v1/payment/")
@Api(tags = "Payment related interfaces", value = "Payment related interfaces")
@Slf4j
public class PaymentController extends BaseController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("create/order")
    @ApiOperation("Create order")
    public ResultInfo<PaymentRespVO> createOrder(@RequestBody @Validated PaymentReqVO paymentReqVO, HttpServletRequest request) {
        PaymentRespVO paymentRespVO = paymentService.createOrder(paymentReqVO, request);
        return ResultInfoUtil.successResult(paymentRespVO);
    }

    @PostMapping("type")
    @ApiOperation("Payment method")
    public ResultInfo<List<PaymentTypeRespVO>> paymentType() {
        List<PaymentTypeRespVO> paymentTypeRespVOS = paymentService.paymentType();
        return ResultInfoUtil.successResult(paymentTypeRespVOS);
    }

    @PostMapping("calc/gasPrice")
    @ApiOperation("Calculate gas price")
    public ResultInfo<CalcGasPriceRespVO> calcNttPrice(@RequestBody @Validated CalcGasPriceReqVO calcGasPriceReqVO) {
        CalcGasPriceRespVO calcGasPriceRespVO = paymentService.calcGasPrice(calcGasPriceReqVO);
        return ResultInfoUtil.successResult(calcGasPriceRespVO);
    }

    @PostMapping("gas/exchangeRate")
    @ApiOperation("gas ratio")
    public ResultInfo<GasExchangeRateRespVO> exchangeRate(@RequestBody @Validated GasExchangeRateReqVO gasExchangeRateReqVO) {
        GasExchangeRateRespVO gasExchangeRateRespVO = paymentService.gasExchangeRate(gasExchangeRateReqVO);
        return ResultInfoUtil.successResult(gasExchangeRateRespVO);
    }

    @PostMapping("refund")
    @ApiOperation("Refund")
    public ResultInfo<RefundRespVO> refund(@RequestBody @Validated RefundReqVO refundReqVO, HttpSession session) {
        UserLoginInfo userInfo = this.getUserInfo(session);
        if (Objects.isNull(userInfo)) {
            return ResultInfoUtil.errorResult("Please login");
        }
        RefundRespVO response = paymentService.refund(refundReqVO, userInfo.getUserId());
        if(response.getStatus().equals(RefundStateEnum.ERROR.getCode())){
            return ResultInfoUtil.errorResult(response.getMessage());
        }
        return ResultInfoUtil.successResult(response);
    }

    @PostMapping("result/query")
    @ApiOperation("Payment result inquiry")
    public ResultInfo<Boolean> queryPayResult(@RequestBody @Validated QueryPayResultReqVO reqVO) {
        boolean response = paymentService.queryPayResult(reqVO);
        return ResultInfoUtil.successResult(response);
    }

    @PostMapping("stripe/callback")
    @ApiOperation("Stripe payment callback")
    @ApiIgnore
    public ResponseEntity stripeCallback(@RequestBody String json, HttpServletRequest request) {
        log.info("Stripe payment callback:{}", json);
        String reqSignature = request.getHeader("Stripe-Signature");
        ResponseEntity response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (StringUtils.isEmpty(reqSignature) || StringUtils.isEmpty(json)) {
            log.error("Stripe payment callback:json-->{},header--->{}", json, reqSignature);
            return response;
        }
        boolean callbackResult = paymentService.stripeCallback(json, reqSignature);
        if (callbackResult) {
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        return response;
    }

}
