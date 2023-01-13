var dataTable;
var recharge_list = "#gas_recharge_list";

var offline_order_id;
var offline_pary_type;
var online_order_id;
var online_pary_type;
var registration_id;
var refundTradeNo;
var refreshId;

$(document).ready(function () {
    $("#txType").html(COMMON_HANDLE.getTxTypeSelect());

    CHAIN_HANDLE.initChainInfo("chainId", null);
    initFrameList();
    startEndDatePicker("#createStartTime", "#createEndTime");
    startEndDatePicker("#payStartTime", "#payEndTime");

    $(".search_").click(function () {
        search();
    });
    $(".reset_").click(function () {
        resetSearch(dataTable);
    });

    if (checkButState("export_order_but")) {
        $(".exportOrders_").show().click(function () {
            exportOrderExcel();
        });
    }

    $(".registration_").show().click(function () {
        registrations();
    });
    $(".cancel_registration_").show().click(function () {
        $("#registration_modal").modal("hide");
    });

    /*Offline Refund Closed*/
    $(".cancellation_refund").show().click(function () {
        $("#offlinerefund_modal").modal("hide");
    });

    /*Online Refund Closed*/
    $(".online_cancellation_refund").show().click(function () {
        $("#onlinerefund_modal").modal("hide");
    });

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
    handleSubmitOffline();
    registrationSubmitOffline();
});


let recharge = function (chainId, chainAddress) {
    $("#runde_modal").modal("show");
}


/*Offline Refund Page*/
let offlinerefund = function (orderId, payType, tradeNo) {

    if (payType == 3 || payType == 2) {
        $("#offlinerefund_modal").modal("show");
        offline_order_id = orderId;
        offline_pary_type = payType;
    } else if (payType == 1) {
        $("#onlinerefund_modal").modal("show");
        online_order_id = orderId;
        online_pary_type = payType;
        refundTradeNo = tradeNo;
    } else {
        alert_error("", "error");
    }

}


/*Remittance Page*/
let registration = function (orderId) {
    registration_id = orderId
    $("#registration_modal").modal("show");
}

let details = function (orderId) {
    $("#order_details_modal").modal("show");
    REQ_HANDLE_.location_('/orderListDetails');
    sessionStorage.setItem("detailsOrderId", orderId)
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
        "sAjaxSource": "/ground/parameters/query/dcpaymentorder",
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

            {data: "tradeNo",
                title: "Order No.",
                render: COMMON_HANDLE.copyData, bSearchable: false, bSortable: false},
            {
                data: "otherTradeNo",
                title: "Third Party Transaction No.",
                render: COMMON_HANDLE.copyData,
                bSearchable: false,
                bSortable: false
            },
            {data: "chainName", title: "Chain Name", bSearchable: false, bSortable: false},
            {
                data: "accountAddress",
                render: COMMON_HANDLE.copyData,
                title: "Wallet Address",
                bSearchable: false,
                bSortable: false
            },
            {data: "payAmount", title: "Payment Amount", bSearchable: false, bSortable: false},
            {data: "currency", title: "Unit", bSearchable: false, bSortable: false},
            {data: "payChannelName", title: "Payment Method", bSearchable: false, bSortable: false},
            {
                data: "payState",
                render: payStateStyle,
                width: 150,
                title: "Payment Status",
                bSearchable: false,
                bSortable: false
            },
            {
                data: "gasRechargeState",
                render: initGasRechargeState,
                title: "Gas Credit Top-up Status",
                bSearchable: false,
                bSortable: false
            },
            {data: "isRefund", render: initIsRefundState, title: "Refund", bSearchable: false, bSortable: false},
            {data: "createTime", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "payTime", title: "Payment Time", bSearchable: false, bSortable: false},
            {render: initTableBut, title: "Options", bSortable: false, width: 160}
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

let initPayState = function (data, type, row) {
    return COMMON_HANDLE.getPayStateName(data);
};
let initIsRefundState = function (data, type, row) {
    return COMMON_HANDLE.getIsRefundName(data);
};
let initPayType = function (data, type, row) {
    return COMMON_HANDLE.getPayTypeName(data);
};
let initGasRechargeState = function (data, type, row) {
    return COMMON_HANDLE.getGasRechargeStateName(data);
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
let initRechargeStateType = function (data, type, row) {
    return COMMON_HANDLE.getRechargeStateName(data);
};

function payStateStyle(data, type, row) {
    if (data == 0) {
        if (row.payType == 1 || row.payType == 2) {
            refreshId = row.orderId
            $('#refreshId').text(row.orderId)
            return '<span id="activity" >' + COMMON_HANDLE.getPayStateName(row.payState) + '</span>' +
                '<a type=\'button\' class="ico-item fa fa-refresh" style="font-size: 16px; margin-left: 4px;" onclick="refresh(this)" value="'+row.orderId+'">'
                '</a>';
        }else {
            return '<span id="activity" >' + COMMON_HANDLE.getPayStateName(row.payState) + '</span>';
        }
    } else {
        return '<span id="activity" >' + COMMON_HANDLE.getPayStateName(row.payState) + '</span>';
    }

};

// init options
let initTableBut = function (data, type, row) {
    let orderId = row.orderId;
    let payType = row.payType;
    let tradeNo = row.tradeNo;
    let butStr = ''
    if (checkButState("detail_but_")) {
        butStr += '<button type="button" onClick="details(' + orderId + ')" class="btn-info btn-xs">Details</button>';
    }
    if (row.isRefund == 0 && row.payState == 1) {
        if (row.gasRechargeState == 2) {
            if (checkButState("refund_but_")) {
                butStr += '<button type="button" onclick="offlinerefund(' + orderId + ',\'' + payType + '\',\'' + tradeNo + '\')" class="btn-info btn-xs" >Refund</button>'
            }
        }
    }
    if (row.payType == 3) {
        if (row.payState == 0) {
            if (checkButState("remittance_but_")) {
                butStr += '<button type="button" onclick="registration(' + orderId + ')" class="btn-info btn-xs" >Remittance</button>'
            }
        }
    }

    return butStr;
};

function refresh(row) {
    var queryPayResultReqVO={}
    queryPayResultReqVO.orderId=$(row).attr("value")
    $.ajax({
        "type": "post",
        "url": "/v1/payment/result/query",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(queryPayResultReqVO),
        "success": function (data) {
                alert_success("", data.msg);
                initFrameList();
        }
    });
}

// search
let search = function () {
    setDatatableSearchInfo($("#tradeNo").val(), "tradeNo");
    setDatatableSearchInfo($("#chainId").val(), "chainId");
    setDatatableSearchInfo($("#otherTradeNo").val(), "otherTradeNo");
    setDatatableSearchInfo($("#accountAddress").val(), "accountAddress");
    setDatatableSearchInfo($("#createStartTime").val(), "createStartTime");
    setDatatableSearchInfo($("#createEndTime").val(), "createEndTime");
    setDatatableSearchInfo($("#payStartTime").val(), "payStartTime");
    setDatatableSearchInfo($("#payEndTime").val(), "payEndTime");
    setDatatableSearchInfo($("#payState").val(), "payState");
    setDatatableSearchInfo($("#gasRechargeState").val(), "gasRechargeState");
    setDatatableSearchInfo($("#isRefund").val(), "isRefund");
    dataTable.ajax.reload();
};
let handleSubmit = function () {
    $('#online_from').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            onlinetextarea: {
                required: true
            }
        },
        messages: {
            onlinetextarea: {
                required: "Please enter the remark"
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            if (element.attr('id') == 'recharge_amount') {
                element.parent('div').parent('div').append(error);
            } else {
                element.parent('div').append(error);
            }
        },
        submitHandler: function (form) {
            submitOnline();
        }
    });
};

let handleSubmitOffline = function () {
    $('#offline_form').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            offlinetradeno: {
                required: true
            }, offlinetextarea: {
                required: true
            }
        },
        messages: {
            offlinetradeno: {
                required: "Please enter the refund transaction number"
            },
            offlinetextarea: {
                required: "Please enter the remark"
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            if (element.attr('id') == 'recharge_amount') {
                element.parent('div').parent('div').append(error);
            } else {
                element.parent('div').append(error);
            }
        },
        submitHandler: function (form) {
            submitOffline();
        }
    });
};

let registrationSubmitOffline = function () {
    $('#registration_modal_from').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            registrationOtherTradeNo: {
                required: true
            }, textareaRemarks: {
                required: true
            }
        },
        messages: {
            registrationOtherTradeNo: {
                required: "Please enter the third Party Transaction No."
            },
            textareaRemarks: {
                required: "Please enter the remark"
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            if (element.attr('id') == 'recharge_amount') {
                element.parent('div').parent('div').append(error);
            } else {
                element.parent('div').append(error);
            }
        },
        submitHandler: function (form) {
            submitRegistration();
        }
    });
};


function submitRegistration() {
    var data = {};
    data.orderId = registration_id;
    data.otherTradeNo = $("#registrationOtherTradeNo").val();
    data.remarks = $("#textareaRemarks").val();
    $.ajax({
        "type": "post",
        "url": "/ground/parameters/remittance/registration",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 0) {
                alert_error("", data.msg);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                registration_id = ''
                alert_success("", data.msg);
                initFrameList()
                $("#registration_modal").modal("hide");
            }

        }
    });
}

function submitOnline() {
    let refundReqVO = {};
    refundReqVO.channelCode = "STRIPE";
    refundReqVO.tradeNo = refundTradeNo;
    // Required
    refundReqVO.reason = $("#onlinetextarea").val();
    $.ajax({
        "type": "post",
        "url": "/v1/payment/refund",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(refundReqVO),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.message);
                initFrameList();
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
            $("#onlinerefund_modal").modal("hide");
        }
    });
};

function submitOffline() {
    var dcPaymentStartRefundReqVO = {}
    dcPaymentStartRefundReqVO.orderId = offline_order_id;
    dcPaymentStartRefundReqVO.otherTradeNo = $("#offlinetradeno").val();
    dcPaymentStartRefundReqVO.remarks = $("#offlinetextarea").val();
    dcPaymentStartRefundReqVO.payType = offline_pary_type;
    $.ajax({
        "type": "get",
        "url": "/sys/user/getUserResource",
        "datatype": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 1) {
                dcPaymentStartRefundReqVO.operator = data.data.userId
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            }
        }
    });
    $.ajax({
        "type": "post",
        "url": "/ground/parameters/refund/processing",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(dcPaymentStartRefundReqVO),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.message);
                initFrameList();
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
            offline_order_id = '';
            offline_pary_type = '';
            $("#offlinerefund_modal").modal("hide");
        }
    });
};

function exportOrderExcel() {
    let dcPaymentOrderReqVO = {};
    dcPaymentOrderReqVO.tradeNo = $("#tradeNo").val();
    dcPaymentOrderReqVO.otherTradeNo = $("#otherTradeNo").val();
    dcPaymentOrderReqVO.chainId = $("#chainId").val();
    dcPaymentOrderReqVO.accountAddress = $("#accountAddress").val();
    dcPaymentOrderReqVO.createStartTime = $("#createStartTime").val();
    dcPaymentOrderReqVO.createEndTime = $("#createEndTime").val();
    dcPaymentOrderReqVO.payStartTime = $("#payStartTime").val();
    dcPaymentOrderReqVO.payEndTime = $("#payEndTime").val();
    dcPaymentOrderReqVO.payState = $("#payState").val();
    dcPaymentOrderReqVO.gasRechargeState = $("#gasRechargeState").val();
    dcPaymentOrderReqVO.isRefund = $("#isRefund").val();
    let reqUrl = "/ground/parameters/export/dcpaymentorder?data=" + encodeURIComponent(JSON.stringify(dcPaymentOrderReqVO));
    COMMON_HANDLE.exportExcel(reqUrl);
}