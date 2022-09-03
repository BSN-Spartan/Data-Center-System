$(document).ready(function () {
    initDataCenter();

    handleSubmit();

});


var initDataCenter = function () {
    $.ajax({
        "type": "get",
        "url": "/sys/dc/get?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        cache: false,
        "async": false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_back("", data.msg, "/");
            } else if (data.code == 1) {
                var dc = data.data;
                if (dc != null && dc.state == 1) {
                    $("#dc_div").show();
                    $("#contactsEmail").html(dc.contactsEmail);
                    $("#token").html(dc.token);
                    $("#nttAccountAddress_").html(dc.nttAccountAddress);
                    $("#createTime").html(dc.createTime);
                } else {
                    $("#submit_dc_div").show();

                    if (dc != null) {
                        $("#email").val(dc.contactsEmail);
                        $("#nttAccountAddress").val(dc.nttAccountAddress);
                    }


                    $(".captcha").click(function () {
                        getCaptcha();
                    });
                }
            } else {
                alert_error_text(data.msg);
            }
        }
    });
};


var getCaptcha = function () {
    var email = $("#email").val();
    if (email == null || email.length == 0) {
        alert_error_text("The mailbox cannot be empty");
        return false;
    }
    $.ajax({
        "type": "post",
        "url": "/sys/dc/getCaptcha?ranparam=" + (new Date()).valueOf(),
        "contentType": "application/json",
        "data": JSON.stringify({"email": email}),
        "success": function (data) {
            if (data.code == 2) {
                alert_error_back("", data.msg, "/");
            } else if (data.code == 1) {

                alert_success("", "Get success, please check in your email");

            } else {
                alert_error_text(data.msg);
            }
        }
    });
};


var handleSubmit = function () {
    $('#submit_dc_div').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            email: {
                required: true,
                email: true
            },
            code: {
                required: true
            },
            nttAccountAddress: {
                required: true
            },
            nttAccountAddress2: {
                required: true,
                equalTo: '#nttAccountAddress'
            }
        },
        messages: {
            email: {
                required: "Email address cannot be empty",
                email: "Please enter a valid email address"
            },
            code: {
                required: "Verification code can not be empty"
            },
            nttAccountAddress: {
                required: "Default chain wallet address can not be empty"
            },
            nttAccountAddress2: {
                required: "Ensure that the governance chain account address cannot be empty",
                equalTo: 'Ensure that the address of the governance chain account is the same as that of the governance chain account'
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
            if ($(element).attr("id") == "code") {
                $("#code_msg_div").show();
                $("#code_msg").html($(error).removeClass("help-block"));
            } else {
                element.parent('div').append(error);
            }

        },

        submitHandler: function (form) {

            submitForm();
        }
    });

    $('#submit_dc_div input').keypress(function (e) {
        if (e.which == 13) {
            if ($('#submit_dc_div').validate().form()) {
                $('#submit_dc_div').submit();
            }
            return false;
        }
    });
};

var submitForm = function () {
    var user = new Object();
    user.email = $("#email").val();
    user.code = $("#code").val();
    user.nttAccountAddress = $("#nttAccountAddress").val();

    $.ajax({
        "type": "post",
        "url": "/sys/dc/insertDc",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(user),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg, function () {
                    REQ_HANDLE_.location_("/index");
                });
            } else {
                alert_error("", data.msg);
            }
        }
    });

};
