var dataTable;
var list = "#payment_terms_list";

$(document).ready(function () {

    console.log("11111");
    // init
    initFrameList();

    if (checkButState("terms_add_but_")) {
        $(".terms_add_but_").show().click(function () {
            REQ_HANDLE_.location_('/terms/add');
        });
    }

});


let initFrameList = function () {
    dataTable = $(list).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/terms/list",
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
            {data: "creator", title: "Creator", bSearchable: false, bSortable: false},
            {data: "createTime", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "auditor", title: "Reviewed by", bSearchable: false, bSortable: false},
            {data: "auditTime", title: "Review Time", bSearchable: false, bSortable: false},
            {
                data: "auditState",
                render: initAuditState,
                title: "Review Status",
                bSearchable: false,
                bSortable: false
            },
            {render: initTableBut, title: "Action", bSortable: false}
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
    cleanDataTableConf(list);
};


// init options
let initTableBut = function (data, type, row) {
    let serviceAuditId = row.serviceAuditId;
    let auditState = row.auditState;
    let butStr = '';
    if (auditState === 0) {
        if (checkButState("terms_audit_but_")) {
            butStr = butStr + '<button type="button" onclick="audit(' + serviceAuditId + ')" class="btn-info btn-xs" >Review</button>';
        }
    }
    if (checkButState("terms_detail_but_")) {
        butStr = butStr + '<button type="button" onclick="detail(' + serviceAuditId + ')" class="btn-info btn-xs" >Details</button>';
    }
    return butStr == "" ? "--" : butStr;
};

function detail(serviceAuditId) {
    REQ_HANDLE_.location_('/terms/getDetail/' + serviceAuditId);
}

function audit(serviceAuditId) {
    REQ_HANDLE_.location_('/terms/toAudit/' + serviceAuditId);
}

let initAuditState = function (data, type, row) {
    return COMMON_HANDLE.getAuditStateName(data);
};

