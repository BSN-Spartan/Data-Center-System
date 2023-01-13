var dataTable;
var recharge_list = "#gas_recharge_list";

$(document).ready(function () {
    $("#txType").html(COMMON_HANDLE.getTxTypeSelect());

    // init frame info
    CHAIN_HANDLE.initChainInfo("chainId", null);
    // init
    initFrameList();
    startEndDatePicker("#startTime", "#endTime");

    $(".search_").click(function () {
        search();
    });
    $(".reset_").click(function () {
        resetSearch(dataTable);
    });

    if (checkButState("recharge_but_")) {
        $(".recharge_but_").show().click(function () {
            // check keystore password
            COMMON_HANDLE.checkPasswordExpired();

            CHAIN_HANDLE.initChainInfoNotAll("rechargeChainType", 1);

            $("#recharge_modal").modal("show");
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

    CHAIN_HANDLE.initChainInfoNotAll("rechargeChainType", chainId);

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
        "sAjaxSource": "/recharge/list",
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
                data: "rechargeCode",
                render: COMMON_HANDLE.copyData,
                title: "Top-up ID",
                bSearchable: false,
                bSortable: false
            },
            {data: "chainName", title: "Chain Name", bSearchable: false, bSortable: false},
            {data: "chainAddress",render: COMMON_HANDLE.copyData, title: "Wallet Address", bSearchable: false, bSortable: false},
            {data: "gas",render:initGas, title: "Gas Credit", bSearchable: false, bSortable: false},
            {data: "ntt", title: "NTT", bSearchable: false, bSortable: false},
            {data: "reason",render:initReaon, title: "Reason", bSearchable: false, bSortable: false},
            {
                data: "rechargeState",
                render: initRechargeStateType,
                title: "Status",
                bSearchable: false,
                bSortable: false
            },
            {data: "rechargeTime", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "txHash", render: COMMON_HANDLE.copyData, title: "Tx Hash", bSearchable: false, bSortable: false},
            {render: initTableBut, title: "Options", bSortable: false}
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
        return  "--";
    }
    return COMMON_HANDLE.copyData(data);
};
let initRechargeStateType = function (data, type, row) {
    return COMMON_HANDLE.getRechargeStateName(data);
};


// init options
let initTableBut = function (data, type, row) {
    let chainId = row.chainId;
    let chainAddress = row.chainAddress;
    let butStr = '';

    if (checkButState("top_up_but_")) {
        butStr += '<button type="button" onclick="recharge(' + chainId + ',\'' + chainAddress + '\')" class="btn-info btn-xs" >Top Up</button>';
    }
    return butStr == "" ? "--" : butStr;
};


// search
let search = function () {
    setDatatableSearchInfo($.trim($("#chainId").val()), "chainId");
    setDatatableSearchInfo($("#txType").val(), "txType");
    setDatatableSearchInfo($("#startTime").val(), "startTime");
    setDatatableSearchInfo($("#endTime").val(), "endTime");
    setDatatableSearchInfo($("#chainAddress").val(), "chainAddress");
    dataTable.ajax.reload();
};

let handleSubmit = function () {
    $('#recharge_detail_content_').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            rechargeChainType: {
                required: true
            },
            recharge_frameType: {
                required: true
            },
            chainAccountAddress: {
                required: true
            },
            confirm_chainAccountAddress: {
                required: true,
                equalTo: "#chainAccountAddress"
            },
            recharge_amount: {
                required: true,
                digits: true,
                min: 1
            }
        },
        messages: {
            rechargeChainType: {
                required: "Please enter the chain"
            },
            recharge_frameType: {
                required: "Chain Name"
            },
            chainAccountAddress: {
                required: "Please enter the Wallet"
            },
            confirm_chainAccountAddress: {
                required: "Please confirm the Wallet"
            },
            recharge_amount: {
                required: "Please enter the recharge amount",
                min: "The recharge amount should be greater than 0"
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
            submitRecharge();
        }
    });
};

function submitRecharge() {
    var data = {};
    $("#submit").prop('disabled', true)
    data.chainId = $("#rechargeChainType").val();
    data.chainAddress = $("#chainAccountAddress").val();
    data.rechargeType = $("input[name='rechargeType']:checked").val();
    data.gas = $("#recharge_amount").val();
    data.password = $("#password").val();
    $.ajax({
        "type": "post",
        "url": "/recharge/recharge",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Submitted successfully", function () {
                    $("#recharge_modal").modal("hide");
                    document.querySelector('#recharge_detail_content_').reset();
                    search();
                });
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
            $("#submit").prop('disabled', false)
        }
    });
};


