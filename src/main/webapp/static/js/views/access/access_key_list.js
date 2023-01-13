var dataTable;
var access_key_list_ = "#access_key_list_";
var oldTps;
var oldTpd;
var tps;
var tpd;

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

    handleSubmit();
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
            {data: "state",render: ACCESS_HANDLE.getStateName,  title: "Status", bSearchable: false, bSortable: false},
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

    cleanDataTableConf(access_key_list_);
};

var initTps = function (data, type, row) {
    if (data == -2) {
        if (row.defaultTps == -1) {
            return "--";
        }else{
            return row.defaultTps;
        }
    } else if (data == -1) {
        return "--";
    } else {
        return data;
    }
}
var initTpd = function (data, type, row) {
    if (data == -2) {
        if (row.defaultTpd == -1) {
            return "--";
        }else{
            return row.defaultTpd;
        }
    } else if (data == -1) {
        return "--";
    } else {
        return data;
    }
}


var initTpsDefault = function (data, defaultData) {
    if (data == -2) {
        if (defaultData == -1) {
            return "";
        }else{
            return defaultData;
        }
    } else if (data == -1) {
        return "";
    } else {
        return data;
    }
}


// init options
let initTableBut = function (data, type, row) {
    let chainAccessId = row.chainAccessId;
    let email = row.email;
    let state = row.state;
    let accessKey = row.accessKey;
    var editJson = {
        "oldTpd": row.tpd,
        "oldTps": row.tps,
        "tps": initTpsDefault(row.tps, row.defaultTps),
        "tpd": initTpsDefault(row.tpd, row.defaultTpd),
        "accessKey": accessKey,
        "email": email,
        "chainAccessId": chainAccessId
    }

    let butStr = '';

    if (state === 2) {
        if (checkButState("access_key_enable_but_")) {
            butStr += '<button type="button" class="btn-info btn-xs" onclick="updateAccessState(' + chainAccessId + ',\'' + email + '\',\'' + accessKey+ '\',\'enable\',1)"><img src="/static/images/btn/btn_able_icon.png">&nbsp;Enable</button>';
        }
    } else {
        if (checkButState("access_key_disable_but_")) {
            butStr += '<button type="button" class="btn-danger btn-xs" onclick="updateAccessState(' + chainAccessId + ',\'' + email + '\',\'' + accessKey+ '\',\'disable\',2)"><img src="/static/images/btn/btn_unable_icon.png">&nbsp;Disable</button>';
        }
    }

    if (checkButState("access_key_update_but_")) {
        butStr = butStr + "<button type='button' onclick='edit(" + JSON.stringify(editJson) + ")' class='btn-info btn-xs' ><img src='/static/images/btn/btn_edit_icon.png'>&nbsp;Edit</button>";
    }
    return butStr;
};


function updateAccessState(chainAccessId, email ,accessKey,stateName, state) {
    alert_confirm("", "Are you sure you want to " + stateName + " the access key of \"" + email + "\" ?", function () {
        var data = {};
        data.chainAccessId = chainAccessId;
        data.contactsEmail = email;
        data.state = state;
        data.accessKey = accessKey;
        updateState(data)
    }, null);
}


function updateState(data) {
    $.ajax({
        "type": "post",
        "url": "/sys/access/key/update/accessinformation",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg);
                search();
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}


let edit = function (editJson) {

    $("#edit_accesskey_modal").modal("show");

    $("#chainAccessId").html(editJson.chainAccessId);

    $("#email").html(editJson.email);

    $("#userAccessKey").html(editJson.accessKey);

    $("#chainAccessId").val(editJson.chainAccessId);

    $("#email").val(editJson.email);

    $("#userAccessKey").val(editJson.accessKey);

    $("#tps").val(editJson.tps);
    $("#tpd").val(editJson.tpd);

    tps = editJson.tps;
    tpd = editJson.tpd;
    oldTps = editJson.oldTps;
    oldTpd = editJson.oldTpd;

}



let handleSubmit = function () {
    $('#edit_accesskey_content_').validate({
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
                element.parent('div').append(error);
        },
        submitHandler: function (form) {
            submitEdit();
        }
    });
};

function submitEdit() {

    var newTps = $("#tps").val()===""?-1:$("#tps").val();
    var newTpd = $("#tpd").val()===""?-1:$("#tpd").val();
    console.log(tps)
    console.log(tpd)
    if(tps==newTps && tpd==newTpd){
        alert_error("", "The information on the page has not been changed.");
        return false;
    }

    if(tps==newTps && oldTps == -2){
        newTps = oldTps;
    }

    if(tpd==newTpd && oldTpd == -2){
        newTpd = oldTpd;
    }

    var data = {};
    $("#submit").prop('disabled', true)
    data.chainAccessId = $("#chainAccessId").val();
    data.contactsEmail = $("#email").val();
    data.accessKey = $("#userAccessKey").val();
    data.tps = newTps;
    data.tpd = newTpd;

    $.ajax({
        "type": "post",
        "url": "/sys/access/key/update/accessinformation",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Submitted successfully", function () {
                    $("#edit_accesskey_modal").modal("hide");
                    document.querySelector('#edit_accesskey_content_').reset();
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



search = function () {
    setDatatableSearchInfo($("#accessKey").val(), "accessKey");
    setDatatableSearchInfo($("#state").val(), "state");
    dataTable.ajax.reload();
};
