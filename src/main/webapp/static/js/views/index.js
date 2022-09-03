$(document).ready(function () {

    initSystemConf();

    $(".yzm_img").click(function () {
        changeCode();
    });
    $("#loginBtn").click(function () {
        subLogin();
    });


    $('#login_form_ input').keypress(function (e) {
        if (e.which == 13) {
            subLogin();
        }
    });
});

function initSystemConf() {
    $.ajax({
        "type": "get",
        "url": "/sys/getSysConf",
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                $("title").html(data.data.name);
                $(".system_name").html(data.data.name);

                $("link[rel=icon]").prop("href", data.data.icon);

            } else {
                alert_error("", data.msg);
            }
        }
    });
}


function changeCode() {
    $(".yzm_img").attr("src", $(".yzm_img").attr("src") + "?timestamp=" + (new Date()).valueOf());
}

function subLogin() {
    var email = $("#email").val();
    if (isNull(email)) {
        alert_error_text("Please enter Email");
        return false;
    }
    var password = $("#password").val();
    if (isNull(password)) {
        alert_error_text("Please enter password");
        return false;
    }
    var code = $("#code").val();
    if (isNull(code)) {
        alert_error_text("Please enter Captcha");
        return false;
    }


    var data = {};
    data.email = email;
    data.password = password;
    data.code = code;

    $.ajax({
        "type": "post",
        "url": "/sys/user/login",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                REQ_HANDLE_.location_(data.data.successUrl);
            } else {
                changeCode();
                alert_error("", data.msg);
            }
        }
    });
}

function isNull(str) {
    return str == null || str.length == 0
}