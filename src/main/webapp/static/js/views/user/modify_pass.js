$(document).ready(function () {
    handleSubmit();

});

var handleSubmit = function () {
    $('#submit_modify_pass').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            oldPassword: {
                required: true
            },
            newPassword: {
                required: true,
                checkPassword: true
            },
            againPassword: {
                required: true,
                equalTo: "#newPassword"
            }
        },
        messages: {
            oldPassword: {
                required: "Original password cannot be empty",
            },
            newPassword: {
                required: "New password cannot be empty"
            },
            againPassword: {
                required: "Confirm password cannot be empty",
                equalTo: "Confirm that the newPassword are the same"
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

};

var submitForm = function () {
    var oldPassword = $("#oldPassword").val();
    var newPassword = $("#newPassword").val();
    var againPassword = $("#againPassword").val();

    var data = {};
    data.oldPassword = oldPassword;
    data.newPassword = newPassword;
    data.againPassword = againPassword;


    $.ajax({
        "type": "post",
        "url": "/sys/user/modifyPass",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Changed successfully", function () {
                    REQ_HANDLE_.location_("/");
                });
            } else {
                alert_error("", data.msg);
            }
        }
    });

};