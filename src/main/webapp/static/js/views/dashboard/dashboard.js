var dataTable;
var node_list_ = "#node_list_";

var rechargeDataTable;
var recharge_list_ = "#recharge_list_";

var MIN_GAS_ = 100000000000000 * 100;

$(document).ready(function () {
    initNodeList();
    initRechargeList();
    initTxSum();
    initAccountInfo();
    initNttReward();

    $("#metaData_tx_but").show().click(function () {
        COMMON_HANDLE.checkPasswordExpired();

        $("#metaDataTxModal").modal("show");
    });

    $('#metaDataTxModal').on('hidden.bs.modal', function (e) {
        $("#metaTxSign_div_from").find('form').trigger('reset');
        $("#gas_amount").val("");
        $("#metaTxSign_div").show()
        $("#metaTxSign_div_from").css("display", "none")
    })

    $("#gas_amount").keyup(function () {

        $("#gas_amount").val(CHAIN_HANDLE.toNonExponential($("#gas_amount").val()));

        // 1:Spartan-I Chain (Powered by NC Ethereum)
        let chainId = 1;
        CHAIN_HANDLE.getRechargeNtt(chainId, $("#gas_amount").val(), "recharge_ntt");
    });

    handleSubmit();


    // init top up url
    $("#top_up_ntt").attr('href', COMMON_HANDLE.FOUNDATION_WEBSITE_);
    $("#submit_foundation").attr('href', COMMON_HANDLE.ENERGY_RECHARGE_);

    showReminder();
    handleReminderSubmit();
});
var initNodeList = function () {
    dataTable = $(node_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/node/queryNodeList",
        "fnServerData": function (sSource, aoData, fnCallback) {

            var temp = dataTablesParam(datatableSearchInfo, JSON.stringify(aoData));
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
            {data: "chainName", title: "Chain Name", bSearchable: false, bSortable: false},
            {data: "nodeCode", render: COMMON_HANDLE.copyData, title: "Node ID", bSearchable: false, bSortable: false},
            {data: "rpcAddress", title: "RPC URL", bSearchable: false, bSortable: false},
            {data: "state", render: initStatus, title: "Status", bSearchable: false, bSortable: false},
            {data: "nodeId", render: getBlockNumber, title: "Block Height", bSearchable: false, bSortable: false},
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

    cleanDataTableConf(node_list_);
};


let initStatus = function (data, type, row) {
    return COMMON_HANDLE.getNodeStateName(data);
};

let getBlockNumber = function (data, type, row, index) {
    if (row.chainId != null) {
        index.row += 1;
        var temp = {};
        temp.chainId = row.chainId;
        temp.rpcAddr = row.rpcAddress;
        $.ajax({
            "type": "post",
            "url": "/dashboard/getBlockNumber",
            "datatype": "json",
            "contentType": "application/json",
            "data": JSON.stringify(temp),
            "success": function (res) {
                if (res.code == 1) {
                    $(node_list_).find("tr:eq(" + index.row + ")").find("td:eq(" + index.col + ")").html(res.data == null ? "--" : res.data);
                } else if (data.code == 3) {
                    alert_success_login(data.msg);
                } else {
                    $(node_list_).find("tr:eq(" + index.row + ")").find("td:eq(" + index.col + ")").html("--");
                }
            },
            "error": function () {
                $(node_list_).find("tr:eq(" + index.row + ")").find("td:eq(" + index.col + ")").html("--");
            }
        });
    } else {
        $(node_list_).find("tr:eq(" + index.row + ")").find("td:eq(" + index.col + ")").html("--");
    }
};

var initRechargeList = function () {
    rechargeDataTable = $(recharge_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/dashboard/listGasRecharge",
        "fnServerData": function (sSource, aoData, fnCallback) {

            var temp = dataTablesParam(datatableSearchInfo, JSON.stringify(aoData));
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
                title: "Recharge Code",
                bSearchable: false,
                bSortable: false
            },
            {data: "chainName", title: "Chain Name", bSearchable: false, bSortable: false},
            {data: "chainAddress", title: "Wallet Address", bSearchable: false, bSortable: false},
            {data: "gas", render: initGas, title: "Gas Credit", bSearchable: false, bSortable: false},
            {data: "ntt", title: "NTT", bSearchable: false, bSortable: false},
            {
                data: "rechargeState",
                render: initRechargeStateType,
                title: "Status",
                bSearchable: false,
                bSortable: false
            },
            {data: "rechargeTime", title: "Created Time", bSearchable: false, bSortable: false}
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
    cleanDataTablePager(recharge_list_);
};

let initRechargeStateType = function (data, type, row) {
    return COMMON_HANDLE.getRechargeStateName(data);
};

let initRechargeCode = function (data, type, row) {
    if (null === data) {
        return "--";
    } else {
        return data;
    }
}

function initTxSum() {
    $.ajax({
        "type": "post",
        "url": "/dashboard/getNTTDealsSummary",
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                $("#flowIn").html(data.data.flowIn);
                $("#flowOut").html(data.data.flowOut);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function initAccountInfo() {
    $.ajax({
        "type": "get",
        "url": "/dashboard/getNttAddress",
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                var address = data.data;
                $("#nttWallet").html(address);
                $("#nttWallet_input").html(address);
                getNttBalance(address);
                getGasBalance(address);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });

}


function getNttBalance(address) {
    if (address == null || address == "") {
        return;
    }
    $.ajax({
        "type": "post",
        "url": "/dashboard/getNttBalance/" + address,
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                $("#ntt").html(data.data.nttBalance);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        },
        "error": function () {
            $("#ntt").html("0.00");
        }
    });
}

function getGasBalance(address) {
    if (address == null || address == "") {
        return;
    }
    $.ajax({
        "type": "post",
        "url": "/dashboard/getGasBalance/" + address,
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                var gas_ = data.data;
                $("#gas").html(gas_);
                if (gas_ <= MIN_GAS_) {
                    $("#gas_recharge_remind_").show();
                    $("#metaData_tx_but").attr("disabled", false);
                }

            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        },
        "error": function () {
            $("#gas").html("0.00");
        }
    });
}

function initNttReward() {
    $.ajax({
        "type": "post",
        "url": "/dashboard/getNTTRecentAward",
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                $("#nttReward").html(data.data.nttCount);
                $("#nttRewardTime").html(data.data.txTime);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        },
        "error": function () {
            $("#nttReward").html("0.00");
        }
    });
}

let handleSubmit = function () {
    $('#metaTx_content_').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            nttWallet_input: {
                required: true
            },
            gas_amount: {
                required: true,
                digits: true,
                min: 1
            }
        },
        messages: {
            recharge_frameType: {
                required: "The NTT wallet address cannot be empty"
            },
            chainAccountAddress: {
                required: "Gas credit cannot be empty"
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
            element.parent('div').append(error);
        },
        submitHandler: function () {
            submitMetaDataTx();
        }
    });
};

initGas = function (data, type, row) {
    var rechargeUnit = row.rechargeUnit;
    return data + " " + rechargeUnit;
};

function submitMetaDataTx() {
    var data = {};
    data.nttWallet = $("#nttWallet_input").html();
    data.nttCount = $("#gas_amount").val();
    data.password = $("#password").val();
    $.ajax({
        type: "post",
        url: "/recharge/metaDataTx",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            if (data.code == 1) {
                var dcAcc = data.data.dcAcc;
                var receiver = data.data.receiver;
                var engAmt = data.data.engAmt;
                var chainID = data.data.chainID;
                var nonce = data.data.nonce;
                var deadline = data.data.deadline;
                var DOMAIN_SEPARATOR = data.data.domainSeparator;
                var MetaTransfer_TYPEHASH = data.data.metaTransferTypeHash;
                var sign = getMetaTransferDigest(dcAcc, receiver, engAmt.toString(), chainID, nonce, deadline, DOMAIN_SEPARATOR, MetaTransfer_TYPEHASH);
                metaTxSign(data.data, sign);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }

        }, error: function () {
            alert_error("", "metaTxSign error");
        }
    });
};

function metaTxSign(p, sign) {
    $("#metaTxSign_but").prop('disabled', true)
    var data = {};
    data.nttWallet = $("#nttWallet_input").html();
    data.metaTxSign = sign;
    data.rechargeMetaId = p.mateTxId;
    $.ajax({
        type: "post",
        url: "/recharge/metaTxSign",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            if (data.code == 1) {
                var sign = data.data
                $("#metaTXAddress").val(p.dcAcc)
                $("#metaTxGas").val(p.engAmt.toString())
                $("#metaTxDeadLine").val(p.deadline)
                $("#metaTxNonce").val(p.nonce)
                $("#mataTxSignature").val(sign)
                $("#metaTxSign_div").hide()
                $("#metaTxSign_div_from").css("display", "block")
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
            $("#metaTxSign_but").prop('disabled', false)
        }, error: function () {
            alert_error("", "metaTxSign error");
            $("#metaTxSign_but").prop('disabled', false)
        }
    });
}

function getMetaTransferDigest(
    dcAcc,
    receiver,
    engAmt,
    chainID,
    nonce,
    deadline,
    DOMAIN_SEPARATOR,
    TYPEHASH
) {
    return ethers.utils.keccak256(
        ethers.utils.solidityPack(
            ['bytes1', 'bytes1', 'bytes32', 'bytes32'],
            [
                '0x19',
                '0x01',
                DOMAIN_SEPARATOR,
                ethers.utils.keccak256(
                    ethers.utils.defaultAbiCoder.encode(
                        ['bytes32', 'address', 'string', 'uint256', 'uint8', 'uint256', 'uint256'],
                        [TYPEHASH, dcAcc, receiver, engAmt, chainID, nonce, deadline]
                    )
                )
            ]
        )
    )
}

function showReminder() {
    $.ajax({
        type: "post",
        url: "/wallet/promptInputKeystore",
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            if (data.code == 1) {
                if (data.data) {
                    $("#reminder_modal").modal("show");
                }
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

let handleReminderSubmit = function () {
    $('#reminder_content_').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {

        },
        messages: {

        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            if (element.attr('id') == 'chainSalePrice') {
                element.parent('div').parent('div').append(error);
            } else {
                element.parent('div').append(error);
            }
        },
        submitHandler: function (form) {
            submitReminder();
        }
    });
};

function submitReminder() {
    var show = $("#showReminder").prop("checked");
    var whetherHint = show ? 1 : 0;
    var data = {};
    data.whetherHint = whetherHint;
    $.ajax({
        type: "post",
        url: "/wallet/setWhetherHintImport",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            if (data.code == 1) {
                $("#reminder_modal").modal("hide");
                REQ_HANDLE_.location_("/config");
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}