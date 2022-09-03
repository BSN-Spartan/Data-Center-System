
var dataTable;
var node_list_ = "#node_list_";

let search;
$(document).ready(function () {

    CHAIN_HANDLE.initChainInfo("chainId", null);
    CHAIN_HANDLE.initChainInfo("netApplicationChainType", null);

    $("#state").html(COMMON_HANDLE.getNodeStateSelect());

    $("#netApplicationChainType").change(function () {
        if ($('option:selected', $(this)).val() == 2) {
            $("#nodeAddress_lab").html("<span class=\"xin\">*<\/span>Node Pub Key:");
        } else {
            $("#nodeAddress_lab").html("<span class=\"xin\">*<\/span>Node Address:");
        }

    });

    initNodeList();

    $(".search_").click(function () {
        search();
    });

    $(".reset_").click(function () {
        resetSearch(dataTable);
    });
    $(".net_application_but_").show().click(function () {
        COMMON_HANDLE.checkPasswordExpired();
        $("#net_application_modal").modal("show");
    });
    handleSubmit();
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
            {
                data: "nodeAddress",
                render: COMMON_HANDLE.copyDataBase64,
                title: "Node Address",
                bSearchable: false,
                bSortable: false
            },
            {data: "rpcAddress", title: "RPC URL", bSearchable: false, bSortable: false},
            {data: "nodeCode", render: COMMON_HANDLE.copyData, title: "Node ID", bSearchable: false, bSortable: false},
            {data: "state", render: initStatus, title: "Status", bSearchable: false, bSortable: false},
            {data: "reason", title: "Reason", bSearchable: false, bSortable: false},
            {data: "remarks", render: COMMON_HANDLE.copyData, title: "Remarks", bSearchable: false, bSortable: false},
            {data: "createTime", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "joinTime", title: "Registered Time", bSearchable: false, bSortable: false}
            // {render: initTableBut, title: "Operation", bSortable: false}
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

let initChainType = function (data, type, row) {
    return COMMON_HANDLE.getChainTypeName(data);
};

let initStatus = function (data, type, row) {
    return COMMON_HANDLE.getNodeStateName(data);
};


search = function () {
    setDatatableSearchInfo($("#chainId").val(), "chainId");
    setDatatableSearchInfo($("#state").val(), "state");
    dataTable.ajax.reload();
};

let handleSubmit = function () {
    $('#net_application_detail_content_').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            netApplicationChainType: {
                required: true
            },
            nodeAddress: {
                required: true
            },
            rpcAddress: {
                required: true
            },
            applySign: {
                required: true
            },
            nodeCode: {
                required: true
            }
        },
        messages: {
            netApplicationChainType: {
                required: "Please select the chain"
            },
            nodeAddress: {
                required: "Please enter the node address"
            },
            rpcAddress: {
                required: "Please enter the node RPC URL"
            },
            applySign: {
                required: "Please enter the signature"
            },
            nodeCode: {
                required: "Please confirm the node ID"
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
        submitHandler: function (form) {
            submitNode();
        }
    });
};

function offNetwork(chainId) {
    var data = {};
    data.chainId = $("#rechargeChainType").val();
    $.ajax({
        "type": "post",
        "url": "/node/offNetwork",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Submitted successfully", function () {
                    $("#recharge_modal").modal("hide");
                    document.querySelector('#net_application_detail_content_').reset();
                    search();
                });
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function submitNode() {
    $("#submit").prop('disabled', true)
    var data = {};
    data.chainId = $("#netApplicationChainType").val();
    data.nodeAddress = $("#nodeAddress").val();
    data.applySign = $("#applySign").val();
    data.rpcAddress = $("#rpcAddress").val();
    data.nodeCode = $("#nodeCode").val();
    data.password = $("#password").val();
    data.remarks = $("#remarks").val();
    $.ajax({
        "type": "post",
        "url": "/node/addNode",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Submitted successfully", function () {
                    $("#net_application_modal").modal("hide");
                    document.querySelector('#net_application_detail_content_').reset();
                    search();
                });
            } else {
                alert_error("", data.msg);
            }
            $("#submit").prop('disabled', false)
        }
    });
}