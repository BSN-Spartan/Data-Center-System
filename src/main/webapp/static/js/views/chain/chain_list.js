/**
 * Desc：
 * @Author Jimmy
 * @Created by 2022-07-16 20:24
 *
 */
var dataTable;
var chain_list_ = "#chain_list_";

$(document).ready(function () {
    initChainList();

    $("#syncChain").click(function () {
        syncChain();
    });
});

var initChainList = function () {
    dataTable = $(chain_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/sys/chain/queryChainList",
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
            {data: "chainName", title: "Chain Name", bSearchable: false, bSortable: false},
            {
                data: "nttCount",
                render: initPrice,
                title: "Price (GAS Credit : NTT)",
                bSearchable: false,
                bSortable: false
            }
        ]
    });

    cleanDataTableConf(chain_list_);
};


function initPrice(data, type, row) {
    var nttCount = row.nttCount;
    var gas = row.gas;
    if (gas == null || gas.length == 0 || nttCount == null || nttCount.length == 0) {
        return "--";
    }
    return (gas == null ? "--" : parseInt(gas) + " " + row.rechargeUnit) + " : " + (nttCount == null ? "--" : nttCount);
}

var syncChain = function () {
    $.ajax({
        "type": "post",
        "url": "/sys/chain/syncChain?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        cache: false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_back("", data.msg, "/");
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else if (data.code == 1) {
                search();
            } else {
                alert_error_text(data.msg);
            }
        }
    });
};


var search = function () {
    dataTable.ajax.reload();
};

function refreshPrice () {
    $.ajax({
        "type": "post",
        "url": "/sys/chain/queryChainPrice",
        "dataType": "json",
        "success": function (data) {
            if (data.code == 1) {
                dataTable.ajax.reload();
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error_text(data.msg);
            }
        }
    });
}
