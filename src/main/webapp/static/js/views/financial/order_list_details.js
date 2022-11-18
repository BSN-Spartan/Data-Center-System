$(document).ready(function () {
    detailsss(sessionStorage.getItem("detailsOrderId"))
});

let detailsss = function (orderId) {
    var memberPaymentOrderDetailsReqVO = {};
    memberPaymentOrderDetailsReqVO.orderId = orderId;
    $("#tradeNo").val();

    $.ajax({
        "type": "post",
        "url": "/ground/parameters/query/dcpaymentorder/details",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(memberPaymentOrderDetailsReqVO),
        "success": function (data) {
            data.data.payType = data.data.payChannelName
            data.data.payState = COMMON_HANDLE.getPayStateName(data.data.payState)
            data.data.gasRechargeState = COMMON_HANDLE.getGasRechargeStateName(data.data.gasRechargeState)
            data.data.refundState = COMMON_HANDLE.getRefundStateName(data.data.refundState)

            $("#payType").html(data.data.payType);
            $("#payState").html(data.data.payState);
            $("#gasRechargeState").html(data.data.gasRechargeState);
            $("#refundState").html(data.data.refundState)

            $("#tradeNo").html((data.data.tradeNo == null || data.data.tradeNo.length == 0) ? "--" : data.data.tradeNo);
            $("#accountAddress").html((data.data.accountAddress == null || data.data.accountAddress.length == 0) ? "--" : data.data.accountAddress);
            $("#payAmount").html((data.data.currency == null || data.data.currency.length == 0) ? "--" : data.data.currency);
            $("#gasCount").html((data.data.gasCount == null || data.data.gasCount.length == 0) ? "--" : data.data.gasCount);
            $("#otherTradeNo").html((data.data.otherTradeNo == null || data.data.otherTradeNo.length == 0) ? "--" : data.data.otherTradeNo);
            $("#payTime").html((data.data.payTime == null || data.data.payTime.length == 0) ? "--" : data.data.payTime);
            $("#txHash").html((data.data.txHash == null || data.data.txHash.length == 0) ? "--" : data.data.txHash);
            $("#gasTxTime").html((data.data.gasTxTime == null || data.data.gasTxTime.length == 0) ? "--" : data.data.gasTxTime)
            $("#gasTxHash").html((data.data.gasTxHash == null || data.data.gasTxHash.length == 0) ? "--" : data.data.gasTxHash)
            $("#dprTradeNo").html((data.data.dprTradeNo == null || data.data.dprTradeNo.length == 0) ? "--" : data.data.dprTradeNo)
            $("#dprOtherTradeNo").html((data.data.dprOtherTradeNo == null || data.data.dprOtherTradeNo.length == 0) ? "--" : data.data.dprOtherTradeNo)
            $("#operator").html((data.data.operator == null || data.data.operator.length == 0) ? "--" : data.data.operator)
            $("#operationTime").html((data.data.operationTime == null || data.data.operationTime.length == 0) ? "--" : data.data.operationTime)
            $("#refundTime").html((data.data.refundTime == null || data.data.refundTime.length == 0) ? "--" : data.data.refundTime)
            $("#isRefund").html((data.data.isRefund == null || data.data.isRefund.length == 0) ? "--" : data.data.isRefund)
            $("#remarks").html((data.data.remarks == null || data.data.remarks.length == 0) ? "--" : data.data.remarks)
        }
    });
}


