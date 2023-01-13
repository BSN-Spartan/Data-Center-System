var userId = null;
var roleList = null;

$(document).ready(function () {

    userId = REQ_HANDLE_.getQueryString("edit");

    var temp = USER_HANDLE.getUserInfo(userId);
    if (temp == null) return false;

    initUserInfo(temp);

    initAllRoleList();

    handleSubmit();


    $(".back_").click(function () {
        REQ_HANDLE_.location_("/user");
    });

});


var initUserInfo = function (data) {

    var user = data.user;

    $("#userName").val(user.contactsName);
    $("#email").val(user.contactsEmail);
    $("#phone").val(user.contactsPhone);

    roleList = data.roleList;
};



var initAllRoleList = function () {
    $.ajax({
        "type": "get",
        "url": "/sys/role/getAll?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        cache: false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_back("", data.msg, "/user");
            } else if (data.code == 1) {
                var roleListStr = '';
                if (data.data != null && data.data.length > 0) {
                    for (var i = 0; i < data.data.length; i++) {
                        var temp = data.data[i];
                        var isChecked = getUserRoleState(temp.roleId);
                        var checkedState = '';
                        if (isChecked) {
                            checkedState = 'checked';
                        }
                        roleListStr += '<li>' +
                            '<div class="checkbox info">' +
                            '<input type="checkbox" id="role_' + temp.roleId + '" ' + checkedState + ' name="roles" value="' + temp.roleId + '">' +
                            '<label for="role_' + temp.roleId + '">' + temp.roleName + '</label>' +
                            '</div>' +
                            '</li>';
                    }

                }
                $("#roleList").html(roleListStr);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error_text(data.msg);
            }
        }
    });
};

function getUserRoleState(roleId) {
    if (roleList != null && roleList.length > 0) {
        for (var i = 0; i < roleList.length; i++) {
            if (roleList[i].roleId == roleId) {
                return true;
            }
        }
    }
    return false;
}

var handleSubmit = function () {
    $('#save_form').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            userName: {
                required: true,
                minlength: 4,
                maxlength: 50,
            },
            email: {
                required: true,
                email: true
            },
            phone: {
                required: true,
                checkPhone: true
            }

        },
        messages: {
            userName: {
                required: "Please enter userName"
            },
            email: {
                required: "Please enter email",
                email: "Please enter a valid email address"
            },
            password: {
                required: "Please enter password"
            },
            phone: {
                required: "Please enter phone"
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

            submitForm();
        }
    });

    $('#save_form input').keypress(function (e) {
        if (e.which == 13) {
            if ($('#save_form').validate().form()) {
                $('#save_form').submit();
            }
            return false;
        }
    });
};


var submitForm = function () {
    var roles = new Array();
    var userRole = new Array();
    $('input[name="roles"]:checked').each(function (i) {
        roles[i] = $(this).val();
        var json = {'roleId': $(this).val()};
        userRole.push(json);
    });
    if (roles == null || roles.length == 0) {
        alert_error("Please select the User role");
        return false;
    }



    var data = new Object();
    var user = new Object();
    user.userId = userId;
    user.contactsName = $("#userName").val();
    user.contactsEmail = $("#email").val();
    user.contactsPhone = $("#phone").val();
    data.user = user;
    data.userRole = userRole;
    $.ajax({
        "type": "post",
        "url": "/sys/user/editUser",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg,function () {
                    REQ_HANDLE_.location_("/user");
                });
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });

};


