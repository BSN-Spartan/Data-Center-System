var dataTable;
var user_list_ = "#user_list_";

$(document).ready(function () {
    initUserList();

    $(".add_").click(function () {
        REQ_HANDLE_.location_("/user/add");
    });

    $("#addUserForm").validate(
        {
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: false,
            rules: {
                userName: {
                    required: true,
                    minlength: 4,
                    maxlength: 20,
                },
                email: {
                    required: true,
                },
                password: {
                    required: true,
                    minlength: 4,
                    maxlength: 10,
                }

            },
            messages: {
                userName: {
                    required: "Please enter userName"
                },
                email: {
                    required: "Please enter email"
                },
                password: {
                    required: "Please enter password"
                },
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
            }
        },
    );
    $("#add_user_btn").click(function () {
        addUser();
    })

});


var initUserList = function () {
    dataTable = $(user_list_).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/sys/user/queryUserList",
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
            {data: "contactsName", title: "Name", bSearchable: false, bSortable: false},
            {data: "contactsEmail", title: "Email", bSearchable: false, bSortable: false},
            {data: "createDate", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "state", render: initState, title: "Status", bSearchable: false, bSortable: false},
            {render: initTableBut, title: "Options", bSortable: false}
        ]
    });

    cleanDataTableConf(user_list_);
};


var initState = function (data, type, row) {
    return USER_HANDLE.getStateName(data);
};


var initTableBut = function (data, type, row) {
    var state = row.state;

    var userId = row.userId;
    var contactsName = row.contactsName;
    var butStr = '';
    if (state == 0) {
        butStr += '<button type="button" class="btn-info btn-xs" onclick="updateUserState(' + userId + ',\'' + contactsName + '\',\'enable\',1)"><img src="/static/images/btn/btn_able_icon.png">&nbsp;Enable</button>';
    } else {
        butStr += '<button type="button" class="btn-danger btn-xs" onclick="updateUserState(' + userId + ',\'' + contactsName + '\',\'disable\',0)"><img src="/static/images/btn/btn_unable_icon.png">&nbsp;Disable</button>';
    }
    return butStr == "" ? "--" : butStr;
};

function updateUserState(userId, loginName, stateName, state) {
    alert_confirm("", "Confirm to " + stateName + " \"" + loginName + "\"?", function () {
        var data = {};
        data.userId = userId;
        data.userState = state;
        updateState(data)
    }, null);
}

function updateState(data) {
    $.ajax({
        "type": "post",
        "url": "/sys/user/updateUserState",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg);
                search();
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

var addUser = function () {
    $("#add_user_btn").prop('disabled', true)
    if ($("#addUserForm").valid()) {
        const userName = $("#userName").val();
        const email = $("#email").val();
        const password = $("#password").val();
        const phone = $("#phone").val();

        var data = {};
        data.userName = userName;
        data.email = email;
        data.password = password;
        data.phone = phone;

        $.ajax({
            "type": "post",
            "url": "/sys/user/addUser",
            "dataType": "json",
            "contentType": "application/json",
            "data": JSON.stringify(data),
            "success": function (data) {
                if (data.code == 1) {
                    alert_success("", "Submitted successfully", function () {
                        $("#addModal").modal("hide");
                        document.querySelector('#addUserForm').reset();
                        search();
                    });
                } else {
                    alert_error("", data.msg);
                }
                $("#add_user_btn").prop('disabled', false)
            },
            error: function () {
                alert_error("", "Failed to add user");
            }
        });


    }

}

var search = function () {
    dataTable.ajax.reload();
};


var USER_HANDLE = {
    STATE_: [
        {"code": 1, "name": "Enabled"},
        {"code": 0, "name": "Disabled"}
    ],
    getStateSelect: function () {
        var op = '<option value="">All</option>';
        var stateList = USER_HANDLE.STATE_;
        for (var i = 0; i < stateList.length; i++) {
            op += '<option value="' + stateList[i].code + '">' + stateList[i].name + '</option>';
        }
        return op;
    },
    getStateName: function (code) {
        var stateList = USER_HANDLE.STATE_;
        for (var i = 0; i < stateList.length; i++) {
            if (stateList[i].code == code) {
                return stateList[i].name;
            }
        }
        return "";
    }
};