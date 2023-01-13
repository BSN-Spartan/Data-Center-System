var dataTable;
var recharge_list = "#gas_recharge_list";

$(document).ready(function () {
    $("#txType").html(COMMON_HANDLE.getTxTypeSelect());

    CHAIN_HANDLE.initChainInfo("chainId", null);
    initFrameList();
    startEndDatePicker("#refundStartTime", "#refundEndTime");

    $(".search_").click(function () {
        search();
    });
    $(".reset_").click(function () {
        resetSearch(dataTable);
    });
    if (checkButState("export_refund_")) {
        $(".export_refund_").show().click(function () {
            exportRefundExcel();
        });
    }

    $("#rechargeChainType").change(function () {
        var chainId = $(this).val();

        if (chainId == null || chainId == "") {
            $("#recharge_ntt").html("0.000000000000");
            $("#rechargeUnit").html("GAS");
            return;
        }
        $("#rechargeUnit").html(CHAIN_HANDLE.getChainRechargeUnit(chainId));

        CHAIN_HANDLE.getRechargeNtt(chainId, $("#recharge_amount").val(), "recharge_ntt");
    });

    $("#recharge_amount").keyup(function () {

        $("#recharge_amount").val(CHAIN_HANDLE.toNonExponential($("#recharge_amount").val()));

        var chainId = $("#rechargeChainType").val();
        if (chainId == "" || chainId == null) {
            $("#recharge_ntt").html("0.000000000000");
            $("#rechargeUnit").html("GAS");
            return;
        }
        CHAIN_HANDLE.getRechargeNtt(chainId, $("#recharge_amount").val(), "recharge_ntt");
    });


    handleSubmit();


});


let recharge = function (chainId, chainAddress) {
    $("#recharge_modal").modal("show");

    CHAIN_HANDLE.initChainInfo("rechargeChainType", chainId);

    $("#rechargeUnit").html(CHAIN_HANDLE.getChainRechargeUnit(chainId));

    $("#recharge_ntt").html("0.000000000000");
    $("#recharge_amount").val(0);

    $("#chainAccountAddress").val(chainAddress);
    $("#confirm_chainAccountAddress").val(chainAddress);

}


let initFrameList = function () {
    dataTable = $(recharge_list).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/ground/parameters/query/dcpaymentrefund",
        "fnServerData": function (sSource, aoData, fnCallback) {
            let temp = dataTablesParam(datatableSearchInfo, JSON.stringify(aoData));
            $.ajax({
                "type": "post",
                "url": sSource,
                "dataType": "json",
                "contentType": "application/json",
                "data": JSON.stringify(temp),
                "success": function (data) {
                    if (data.code == 3) {
                        alert_success_login(data.msg);
                    } else {
                        handleDataTableResult(data, fnCallback);
                    }
                }
            });
        },
        "deferRender": true,
        "bSort": false,
        "fnDrawCallback": function () {
            initNumber(this.api());
        },
        "columns": [
            {data: null, "targets": 0, title: "No.", bSearchable: false, bSortable: false},
            {
                data: "tradeNo",
                title: "Refund Order No.",
                render: COMMON_HANDLE.copyData,
                bSearchable: false,
                bSortable: false
            },
            {
                data: "otherTradeNo",
                render: COMMON_HANDLE.copyData,
                title: "Refund Transaction No.",
                bSearchable: false,
                bSortable: false
            },
            {
                data: "dprTradeNo",
                title: "Order No.",
                render: COMMON_HANDLE.copyData,
                bSearchable: false,
                bSortable: false
            },
            {data: "accountAddress", title: "Wallet Address", bSearchable: false, bSortable: false},
            {data: "payAmount", title: "Amount", bSearchable: false, bSortable: false},
            {data: "currency", title: "Unit", bSearchable: false, bSortable: false},
            {data: "payChannelName", title: "Payment Method", bSearchable: false, bSortable: false},
            {
                data: "refundState",
                title: "Refund Status",
                render: initRefundState,
                bSearchable: false,
                bSortable: false
            },
            {data: "operator", title: "Operator", bSearchable: false, bSortable: false},
            {data: "refundTime", title: "Refund Time", bSearchable: false, bSortable: false}
        ],
        "columnDefs": [{
            "targets": "_all",
            "defaultContent": "",
            "render": function (data, type, row) {
                if (data == null || data == "") {
                    return "--";
                }
                return data
            }
        }]
    });
    cleanDataTableConf(recharge_list);
};
let initGas = function (data, type, row) {
    var rechargeUnit = row.rechargeUnit;
    return data + " " + rechargeUnit;
};
let initReaon = function (data, type, row) {
    var rechargeState = row.rechargeState;
    if (rechargeState == 1) {
        return "--";
    }
    return COMMON_HANDLE.copyData(data);
};


let initPayType = function (data, type, row) {
    return COMMON_HANDLE.getPayTypeName(data);
};
let initRefundState = function (data, type, row) {
    return COMMON_HANDLE.getRefundStateName(data);
};

// search
let search = function () {
    setDatatableSearchInfo($("#tradeNo").val(), "tradeNo");
    setDatatableSearchInfo($("#otherTradeNo").val(), "otherTradeNo");
    setDatatableSearchInfo($("#dprTradeNo").val(), "dprTradeNo");
    setDatatableSearchInfo($("#accountAddress").val(), "accountAddress");
    setDatatableSearchInfo($("#updateStartTime").val(), "updateStartTime");
    setDatatableSearchInfo($("#updateEndTime").val(), "updateEndTime");
    setDatatableSearchInfo($("#refundStartTime").val(), "refundStartTime");
    setDatatableSearchInfo($("#refundEndTime").val(), "refundEndTime");
    setDatatableSearchInfo($("#refundState").val(), "refundState");
    dataTable.ajax.reload();
};

function exportRefundExcel() {
    let dcPaymentRefundReqVO = {};
    dcPaymentRefundReqVO.tradeNo = $("#tradeNo").val();
    dcPaymentRefundReqVO.otherTradeNo = $("#otherTradeNo").val();
    dcPaymentRefundReqVO.dprTradeNo = $("#dprTradeNo").val();
    dcPaymentRefundReqVO.accountAddress = $("#accountAddress").val();
    dcPaymentRefundReqVO.refundState = $("#refundState").val();
    dcPaymentRefundReqVO.updateStartTime = $("#updateStartTime").val();
    dcPaymentRefundReqVO.updateEndTime = $("#updateEndTime").val();
    dcPaymentRefundReqVO.refundStartTime = $("#refundStartTime").val();
    dcPaymentRefundReqVO.refundEndTime = $("#refundEndTime").val();
    let reqUrl = "/ground/parameters/export/dcpaymentrefund?data=" + encodeURIComponent(JSON.stringify(dcPaymentRefundReqVO));
    COMMON_HANDLE.exportExcel(reqUrl);
}
