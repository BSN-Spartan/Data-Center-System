var dataTable;
var ntt_list_ = "#ntt_list_";

$(document).ready(function () {

    initReceiveNttList();

    $("#txType").html(COMMON_HANDLE.getNttTxTypeSelect());
    startEndDatePicker("#startTime", "#endTime");

    $(".search_").click(function () {
        search();
    });

    $(".reset_").click(function () {
        resetSearch(dataTable);
    });
    initAccountInfo();
});

function initAccountInfo() {

    $.ajax({
        "type": "get",
        "url": "/dashboard/getNttAddress",
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {

                var address = data.data;
                $("#accountAddress").html(address);

                getNttBalance(address);
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
                $("#nttBalance").html(data.data.nttBalance);
            } else {
                alert_error("", data.msg);
            }
        },
        "error": function () {
            $("#nttBalance").html("0.00");
        }
    });
}

var initReceiveNttList = function () {
    dataTable = $(ntt_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/ntt/queryNttList",
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
                data: "txHash",
                render: COMMON_HANDLE.copyData,
                title: "Transaction Hash",
                bSearchable: false,
                bSortable: false
            },
            {data: "type", render: initType, title: "Transaction Type", bSearchable: false, bSortable: false},
            {data: "nttCount", render: initNttCount, title: "NTT Amount", bSearchable: false, bSortable: false},
            {data: "nttBalance", title: "NTT Balance", bSearchable: false, bSortable: false},
            {data: "txTime", title: "Transaction Time", bSearchable: false, bSortable: false}
        ],
        "columnDefs": [{
            "targets": "_all",
            "defaultContent": "",
            "render": function (data, type, row) {
                if (data == null || data == "") {
                    return "-";
                }
                return data
            }
        }]
    });
    cleanDataTableConf(ntt_list_);
};

var initType = function (data, type, row) {
    return COMMON_HANDLE.getNttTxTypeName(data);
};
var initNttCount = function (data, type, row) {
    if (row.digestType == 1) {
        return "(" + data + ")"
    }
    return data;
};

var search = function () {
    setDatatableSearchInfo($("#txHash").val(), "txHash");
    setDatatableSearchInfo($("#txType").val(), "type");
    setDatatableSearchInfo($("#startTime").val(), "startTime");
    setDatatableSearchInfo($("#endTime").val(), "endTime");
    dataTable.ajax.reload();
};
