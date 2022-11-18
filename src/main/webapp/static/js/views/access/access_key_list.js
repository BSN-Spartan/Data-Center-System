var dataTable;
var access_key_list_ = "#access_key_list_";

var ACCESS_HANDLE = {
    _STATE_: [
        {"type": 1, "name": "Accessed"},
        {"type": 2, "name": "Access Failed"}
    ],
    getStateName: function (type) {
        return ACCESS_HANDLE.getName(type, ACCESS_HANDLE._STATE_);
    },
    getStateSelect: function () {
        return ACCESS_HANDLE.getSelect(ACCESS_HANDLE._STATE_);
    },
    getSelect: function (list) {
        var op = '<option value="">All</option>';
        for (var i = 0; i < list.length; i++) {
            op += '<option value="' + list[i].type + '">' + list[i].name + '</option>';
        }
        return op;
    },
    getName: function (type, list) {
        for (var i = 0; i < list.length; i++) {
            if (list[i].type == type) {
                return list[i].name;
            }
        }
        return "--";
    },
}

$(document).ready(function () {
    $("#state").html(ACCESS_HANDLE.getStateSelect());

    initAccessKeyList();

    $(".search_").click(function () {
        search();
    });

    $(".reset_").click(function () {
        resetSearch(dataTable);
    });
});


var initAccessKeyList = function () {
    dataTable = $(access_key_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/sys/access/key/queryList",
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
            {data: "email", title: "Email", bSearchable: false, bSortable: false},
            {data: "accessKey", title: "Access Key", bSearchable: false, bSortable: false},
            {data: "tps", render: initTps, title: "TPS", bSearchable: false, bSortable: false},
            {data: "tpd", render: initTpd, title: "TPD", bSearchable: false, bSortable: false},
            {data: "createTime", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "notifyState",render: ACCESS_HANDLE.getStateName,  title: "Status", bSearchable: false, bSortable: false},
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

    cleanDataTableConf(access_key_list_);
};

var initTps = function (data, type, row) {
    if (data == -2) {
        return row.defaultTps;
    } else if (data == -1) {
        return "--";
    } else {
        return data;
    }
}
var initTpd = function (data, type, row) {
    if (data == -2) {
        return row.defaultTpd;
    } else if (data == -1) {
        return "--";
    } else {
        return data;
    }
}


search = function () {
    setDatatableSearchInfo($("#accessKey").val(), "accessKey");
    setDatatableSearchInfo($("#state").val(), "notifyState");
    dataTable.ajax.reload();
};
