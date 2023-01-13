var dataTable;
var user_list_ = "#role_list_";

$(document).ready(function () {

    if(checkButState("insert_but_")) {
        $(".insert_but_").show();
    }

    $("#state").html(ROLE_HANDLE.getStateSelect());

    initRoleList();

    $(".search_").click(function () {
        search();
    });

    $(".reset_").click(function () {
        resetSearch(dataTable);
    });

    $(".add_").click(function () {
        REQ_HANDLE_.location_("/role/add");
    });
});


var initRoleList = function () {
    dataTable = $(user_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/sys/role/queryRoleList",
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
            {data: "roleCode", title: "Role ID", bSearchable: false, bSortable: false},
            {data: "roleName", title: "Role Name", bSearchable: false, bSortable: false},
            {data: "description", render: initValue, title: "Role Description", bSearchable: false, bSortable: false},
            {data: "state", render: initState, title: "Status", bSearchable: false, bSortable: false},
            {render: initTableBut, title: "Options", bSortable: false}
        ]
    });

    cleanDataTableConf(user_list_);
};


var initState = function (data, type, row) {
    return ROLE_HANDLE.getStateName(data);
};

var initValue = function (data, type, row) {
    return DATA_FORMAT.formatDefaultString(data);
};

var initTableBut = function (data, type, row) {
    var state = row.state;

    var roleId = row.roleId;
    var roleName = row.roleName;
    var butStr = '';
    if (state == 2) {
        if (checkButState("enable_but_")) {
            butStr += '<button type="button" class="btn-info btn-xs" onclick="updateRoleState(' + roleId + ',\'' + roleName + '\',\'enable\',1)"><img src="/static/images/btn/btn_able_icon.png">&nbsp;Enabled</button>';
        }
    } else {
        if (checkButState("disable_but_")) {
            butStr += '<button type="button" class="btn-danger btn-xs" onclick="updateRoleState(' + roleId + ',\'' + roleName + '\',\'disable\',2)"><img src="/static/images/btn/btn_unable_icon.png">&nbsp;Disabled</button>';
        }
    }
    if (checkButState("update_but_")) {
        butStr += '<button type="button" class="btn-info btn-xs" onclick="editRole(' + roleId + ')"><img src="/static/images/btn/btn_edit_icon.png">&nbsp;Edit</button>';
    }
    if (checkButState("check_but_")) {
        butStr += '<button type="button" class="btn-info btn-xs" onclick="checkRole(' + roleId + ')"><img src="/static/images/btn/btn_look_icon.png">&nbsp;Details</button>';
    }
    return butStr == "" ? "--" : butStr;
};

var editRole = function (roleId) {
    REQ_HANDLE_.location_("/role/edit/" + roleId);
};
var checkRole = function (userId) {
    REQ_HANDLE_.location_("/role/check/" + userId);
};

function updateRoleState(roleId, loginName, stateName, state) {
    alert_confirm("", "Are you sure you want to " + stateName  + " the role of \"" + loginName + "\"？", function () {
        var data = {};
        data.roleId = roleId;
        data.state = state;
        updateState(data)
    }, null);
}
function updateState(data) {
    $.ajax({
        "type": "post",
        "url": "/sys/role/updateRoleState",
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



var search = function () {
    setDatatableSearchInfo($("#state").val(), "state");
    setDatatableSearchInfo($.trim($("#roleName").val()), "roleName");
    dataTable.ajax.reload();
};