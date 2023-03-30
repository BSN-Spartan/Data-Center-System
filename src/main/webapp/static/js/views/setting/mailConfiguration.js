var dataD = [];
var hideData = {};
var emailRegExp =  /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
var judgeFn = /\s+/g;

$(document).ready(function () {
    queryConf();

    $(".col-sm-4").on("click", ".fa-eye-slash", function () {
        $(this).removeClass("fa-eye-slash").addClass("fa-eye");
        $(this).prev().attr("type", "text");
    });

    $(".col-sm-4").on("click", ".fa-eye", function () {
        $(this).removeClass("fa-eye").addClass("fa-eye-slash");
        $(this).prev().attr("type", "password");
    });
    onkeyup="this.value=this.value.replace(/\s+/g,'')"
});

function selectSendType(_this) {
    var parentEle = $(_this).parents(".send_type_select");
    $(parentEle).siblings(".send_type_select").find("input,textarea").not("[name=sendType]").attr("disabled", "disabled");
    $(parentEle).find("input,textarea").not("[name=sendType]").removeAttr("disabled");
}


function queryConf() {
    $.ajax({
        "type": "get",
        "url": "/ground/mail/query/conf",
        "datatype": "json",
        "success": function (data) {
            if (data.code == 1) {
                dataEcho(data.data.data)
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function dataEcho(dataD) {
    if (dataD == null || dataD.length == 0 || (dataD.length == 1 && dataD[0].type == 1 && dataD[0].mailHost == null)) {
        $("#properties").val("{\"mail.smtp.auth\":true,\"mail.smtp.starttls.enable\":true,\"mail.smtp.quitwait\":false,\"mail.smtp.socketFactory.fallback\":false}");

        let radios = $("input[type=radio]");
        for (let i = 0; i < radios.length; i++) {
            $(radios[i]).parents(".send_type_select").find("input,textarea").not("[name=sendType]").attr("disabled", "disabled");
        }
    }

    for (const v of dataD) {
        var parent = $("#sendType" + v.type).parents(".send_type_select");

        for (const key in v) {
            if ($("#" + key).length != 0) {
                $(parent).find("#" + key).val(v[key]);
            }

            if (v.type == 2 && (key == "accessKey" || key == "secretKey")) {
                var hideEle = $(parent).find("#" + key).parent().next();
                unHideDisplay(hideEle);
                hideData[key] = v[key];
            }
        }

        if (v.state == 0) {
            $("#sendType" + v.type).attr("checked", "checked");
            selectSendType($("#sendType" + v.type));
        }
    }


}

function saveData() {
    var dataArr = getFormData();
    if (dataArr == undefined || dataArr.length == 0) {
        return;
    }
    for (const dataJsonElement of dataArr) {
        let emailLength = split(dataJsonElement.mailUserName)
        if (emailLength > 1) {
            alert_error("You can only configure one email address.")
            return;
        }
        if (isNaN(dataJsonElement.mailPort)) {
            alert_error("Invalid port format")
            return;
        }
        if (!emailRegExp.test(dataJsonElement.mailUserName)) {
            alert_error("Invalid email format")
            return;
        }
        if (dataJsonElement.type == 1) {
            let hostLength = split(dataJsonElement.mailHost)
            if (hostLength > 1) {
                alert_error("Incorrect host configuration")
                return;
            }
            if (dataJsonElement.mailPort.length >= 10) {
                alert_error("Port is too long")
                return;
            }

            let properties = dataJsonElement.properties;
            if ((properties != '' || properties != undefined) && !isJSON(properties)) {
                alert_error("Incorrect json format")
                return;
            }
        }
    }
    $.ajax({
        "type": "post",
        "url": "/ground/mail/update/send",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(dataArr),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function getFormData() {
    var dataArr = [];
    let radios = $("input[type=radio]");
    for (let i = 0; i < radios.length; i++) {
        var radioEle = $(radios[i]);
        if (radioEle.is(":checked")) {
            var json = {
                "mailHost": "",
                "mailPort": "",
                "mailUserName": "",
                "mailPassWord": "",
                "properties": "",
                "type": 0,
                "accessKey": "",
                "secretKey": "",
                "regionStatic": "",
                "receiver": "",
                "cc": "",
            };
            json["type"] = radioEle.val();
            eles = radioEle.parents(".send_type_select").find("input,textarea").not("[name=sendType]");
            for (let j = 0; j < eles.length; j++) {
                var key = $(eles[j]).attr("id");
                var value = $(eles[j]).val();

                if (value == '' || value == undefined) {
                    let name = $(eles[j]).parent().prev().html().replace(":", "");
                    name=name.replace('<span class="xin">*</span>','');
                    alert_error_text(name + " cannot be empty");
                    return;
                }

                if (key == "accessKey" || key == "secretKey") {
                    json[key] = hideData[key];
                } else {
                    json[key] = value;
                }
            }
            dataArr.push(json)
        }
    }
    if (dataArr == undefined || dataArr.length == 0) {
        alert_error("Please configure the email settings")
        return;
    }
    return dataArr;
}

function hideDisplay(elementId, hideContent) {
    $(elementId).attr("disabled", "disabled");
    if (hideContent.length > 10) {
        const temp = hideContent.substr(6, hideContent.length - 10);
        const displayContent = hideContent.replace(temp, '******************************');
        return displayContent;
    } else {
        return hideContent;
    }
}

function unHideDisplay(_this) {
    let ele = $(_this).prev().find("input");
    let inputId = ele.attr("id");
    if ($(_this).attr("hideDisplay") == 1) {
        if (ele.prev().find("input").is(":checked")) {
            ele.removeAttr("disabled");
        }
        ele.val(hideData[inputId]);
        $(_this).find(".privateKeyImg").attr("src", "/static/images/btn/btn_display_icon.png");
        $(_this).attr("hideDisplay", "0");
    } else {
        hideData[inputId] = ele.val();
        ele.val(hideDisplay(inputId, ele.val()));
        $(_this).find(".privateKeyImg").attr("src", "/static/images/btn/btn_undisplay_icon.png");
        $(_this).attr("hideDisplay", "1");
    }
}

function changeInput(_this) {
    hideData[$(_this).attr("id")] = $(_this).val();
}

function showTestModel() {
    let formData = getFormData();
    if (formData == undefined) {
        return;
    }
    let dataJson = formData[0];
    if (dataJson["type"] == 1 && (dataJson["properties"] != '' || dataJson["properties"] != undefined) && !isJSON(dataJson["properties"])) {
        alert_error("Incorrect json format of Properties")
        return;
    }

    $('#recipient').val('');
    $('#cc').val('');
    $("#test_send_modal").modal("show");
}

function sendTest() {
    var recipient = $("#recipient").val();
    var cc = $("#cc").val();
    if (recipient == '' || recipient == undefined) {
        alert_error_text("Recipient cannot be empty");
        return;
    }

    var dataArr = getFormData();
    let dataJson = dataArr[0];
    dataJson["receiver"] = recipient;
    dataJson["cc"] = cc;

    if (!emailRegExp.test(dataJson.receiver)) {
        alert_error("Invalid email format")
        return;
    }

    if (cc != '' && cc != undefined) {
        if (!emailRegExp.test(dataJson.cc)) {
            alert_error("Invalid email format")
            return;
        }
    }

    $.ajax({
        "type": "post",
        "url": "/ground/mail/send/test",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(dataJson),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("","The email has been sent, please check the recipient's mailbox. If you haven't received the email in 5 minutes, please check the email settings.");
                $("#test_send_modal").modal("hide");
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });

}

function split(data) {
    let res = data.split(";");
    return res.length
}

function showReference() {
    $("#smtp_reference").modal("show");
}

function isJSON(str) {
    if (typeof str == 'string') {
        try {
            JSON.parse(str);
            return true;
        } catch(e) {
            console.log(e);
            return false;
        }
    }
}