$(document).ready(function () {

    initAllResourceList();

    handleSubmit();

    $(".back_").click(function () {
        REQ_HANDLE_.location_("/role");
    });
});

var initAllResourceList = function () {
    $.ajax({
        "type": "get",
        "url": "/sys/resource/getAll?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        cache: false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_back("", data.msg, "/user");
            } else if (data.code == 1) {
                if (data.data != null && data.data.length > 0) {
                    $('#resourceList').treeview({
                        data: eval(data.data),
                        showIcon: true,
                        color: '#505458',
                        selectedColor: '#00aeff',
                        selectedBackColor: "#F5F5F5",
                        showCheckbox: true,
                        onNodeChecked: nodeChecked,
                        onNodeUnchecked: nodeUnchecked
                    });
                }
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error_text(data.msg);
            }
        }
    });
};


var handleSubmit = function () {
    $('#save_form').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            roleName: {
                required: true,
                rangelength: [1, 20]
            },
            description: {
                required: true,
                rangelength: [1, 200]
            }
        },
        messages: {
            roleName: {
                required: "Please enter the role name",
                rangelength: "Role name must be between 1 to 20 characters."
            },
            description: {
                required: "Please enter the role description",
                rangelength: "Role description must be between 1 to 200 characters."
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
    var checkedNodes = $('#resourceList').treeview('getChecked');
    if (checkedNodes == null || checkedNodes.length == 0) {
        alert_error("Please select the role's permission");
        return false;
    }
    var roleResource = new Array();
    for (var i = 0; i < checkedNodes.length; i++) {
        var json = {'rsucId': checkedNodes[i].id};
        roleResource.push(json);
    }
    var data = new Object();
    var role = new Object();
    role.roleName = $("#roleName").val();
    role.description = $("#description").val();
    data.role = role;
    data.roleResource = roleResource;
    $.ajax({
        "type": "post",
        "url": "/sys/role/insertRole",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg,function () {
                    REQ_HANDLE_.location_("/role");
                });
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
};
