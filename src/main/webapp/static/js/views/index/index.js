$(document).ready(function () {

    initDataCenter();

    initReminder();

    handleSubmit();

    handleSubmitKeystore();

    checkWalletExists();

    handleSubmitKeystorePassword();

    handleReminderSubmit();
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
                if (dc != null) {
                    $("#email").val(dc.contactsEmail).attr("readonly", "readonly");
                    $("#dcCode").val(dc.dcCode).attr("readonly", "readonly");
                    $("#token").val(dc.token.substr(0, 6) + "******" + dc.token.substr(-6)).attr("readonly", "readonly");
                    $("#nttAccountAddress").val(dc.nttAccountAddress).attr("readonly", "readonly");
                    $("#confirm_chainAccountAddress").hide();
                    $("#submit_div").hide();
                }else {
                    $("#submit_div").show();
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
    $('#submit_dc_div').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        messages: {
            email: {
                required: "Email address cannot be empty",
                email: "Please enter a valid email address"
            },
            dcCode: {
                required: "Data Center ID cannot be empty"
            },
            token: {
                required: "Token cannot be empty"
            },
            nttAccountAddress: {
                required: "NTT wallet address cannot be empty"
            },
            confirmNttAccountAddress: {
                required: "Confirm that the NTT account address is not empty.",
                equalTo: 'NTT wallet addresses are not the same'
            }
        },
        rules: {
            email: {
                required: true,
                email: true
            },
            dcCode: {
                required: true
            },
            token: {
                required: true
            },
            nttAccountAddress: {
                required: true
            },
            confirmNttAccountAddress: {
                required: true,
                equalTo: '#nttAccountAddress'
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        submitHandler: function (form) {
            submitForm();
        }
    });
};

var submitForm = function () {
    var dataCenter = new Object();
    dataCenter.email = $("#email").val();
    dataCenter.dcCode = $("#dcCode").val();
    dataCenter.token = $("#token").val();
    dataCenter.nttAccountAddress = $("#confirmNttAccountAddress").val();

    $.ajax({
        "type": "post",
        "url": "/sys/dc/addDataCenter",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(dataCenter),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg, function () {
                    REQ_HANDLE_.location_("/config");
                });
            } else {
                alert_error("", data.msg);
            }
        }
    });

};

var handleSubmitKeystore = function () {
    $('#submit_keyStore').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            password: {
                required: true
            },
            confirmPassword: {
                required: true,
                equalTo: '#password'
            },
            privateKey: {
                required: true
            },
            confirmPrivateKey: {
                required: true,
                equalTo: '#privateKey'
            }
        },
        messages: {
            password: {
                required: "please enter password not empty"
            },
            confirmPassword: {
                required: "please confirm password not empty",
                equalTo: 'The two passwords are not the same'
            },
            privateKey: {
                required: "please enter privateKey not empty"
            },
            confirmPrivateKey: {
                required: "please confirm privateKey not empty",
                equalTo: 'The two private keys are not the same'
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
            submitKeyStoreForm();
        }
    });
};

function submitKeyStoreForm() {
    let keystore = new Object();
    keystore.password = $("#confirmPassword").val();
    keystore.privateKey = $("#confirmPrivateKey").val();
    $.ajax({
        "type": "post",
        "url": "/wallet/generateNewWalletFile",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(keystore),
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

function checkWalletExists() {
    $.ajax({
        "type": "post",
        "url": "/wallet/checkWalletExists",
        "datatype": "json",
        "contentType": "application/json",
        "success": function (data) {
            if (data.code == 1) {
                checkKeystorePassword();
                $("#keystore_info").hide();
                $("#keystore_info_already_exists").show();
                if (checkButState("delete_keystore_")) {
                    $("#deleteKeystore").show();
                }
            } else {
                $("#keystore_info").show();
                $("#keystore_info_already_exists").hide();
                $("#submit_keyStore_password").hide();
                $("#deleteKeystore").hide();
            }
        }
    });
};

function checkKeystorePassword(){
    $.ajax({
        "type": "post",
        "url": "/recharge/checkPasswordExpired",
        "dataType": "json",
        "contentType": "application/json",
        "success": function (data) {
            if (data.code == 1) {
                $("#submit_keyStore_password").hide();
            } else {
                $("#submit_keyStore_password").show();
            }
        }
    });
}

var handleSubmitKeystorePassword = function () {
    $('#submit_keyStore_password').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            keystorePassword: {
                required: true
            },
            confirmKeystorePassword: {
                required: true,
                equalTo: '#keystorePassword'
            }
        },
        messages: {
            keystorePassword: {
                required: "please enter password not empty"
            },
            confirmKeystorePassword: {
                required: "please confirm password not empty",
                equalTo: 'The two passwords are not the same'
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
        submitHandler: function () {
            submitKeyStorePasswordForm();
        }
    });
};

function submitKeyStorePasswordForm() {
    let keystorePassword = new Object();
    keystorePassword.keystorePassword = $("#confirmKeystorePassword").val();
    $.ajax({
        "type": "post",
        "url": "/wallet/updateKeystorePassword",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(keystorePassword),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg, function () {
                    $("#submit_keyStore_password").hide();
                });
            } else {
                alert_error("", data.msg);
            }
        }
    });
};

let handleReminderSubmit = function () {
    $('#reminder_config').validate({
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
            if (element.attr('id') == 'chainSalePrice') {
                element.parent('div').parent('div').append(error);
            } else {
                element.parent('div').append(error);
            }
        },
        submitHandler: function (form) {
            submitReminder();
        }
    });
};

function submitReminder() {
    var whetherHint;
    if ($("#remind1").is(":checked")) {
        whetherHint = $("#remind1").val();
    } else {
        whetherHint = $("#remind2").val();
    }

    var data = {};
    data.whetherHint = whetherHint;
    $.ajax({
        type: "post",
        url: "/wallet/setWhetherHintImport",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            if (data.code == 1) {
                alert_success("", "Submitted successfully");
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function initReminder() {
    $.ajax({
        type: "post",
        url: "/wallet/getWhetherHintImport",
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            if (data.code == 1) {
                if (data.data != null) {
                    $("input[name=remindRadio][value=" + data.data.confValue +"]").attr("checked", true);
                }
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function deleteKeystore() {
    alert_confirm("", "Are you sure you want to delete the keystore information?", function () {
        $.ajax({
            type: "post",
            url: "/wallet/deleteWallet",
            dataType: "json",
            contentType: "application/json",
            success: function (data) {
                console.log(data)
                if (data.code == 1) {
                    if (data.data) {
                        REQ_HANDLE_.reload_();
                    }
                } else if (data.code == 3) {
                    alert_success_login(data.msg);
                } else {
                    alert_error("", data.msg);
                }
            }
        });
    }, null);
}