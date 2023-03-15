var nttMonitor = {};
var gasMonitor = {};

$(document).ready(function () {
    $("input[name=balanceType]").parents(".balance_type_select").find("input,textarea").not("[type=checkbox]").attr("disabled", "disabled");
    queryMonitor();
    handleSubmit();
});

function queryMonitor() {
    $.ajax({
        "type": "get",
        "url": "/monitor/list",
        "datatype": "json",
        "success": function (data) {
            for (const datum of data.data) {

                if (datum.monitorType == 1) {
                    if (datum.state == 1) {
                        $("#nttState").attr("checked", "checked")
                        selectBalanceType($("#nttState"));
                    }

                    document.getElementById("nttLimit").value = datum.balanceLimit
                    document.getElementById("nttEmail").value = datum.reminderEmail

                }
                if (datum.monitorType == 2) {
                    if (datum.state == 1) {
                        $("#gasState").attr("checked", "checked")
                        selectBalanceType($("#gasState"));
                    }
                    document.getElementById("gasLimit").value = datum.balanceLimit
                    document.getElementById("gasEmail").value = datum.reminderEmail
                }
            }

        }
    });
};


var handleSubmit = function () {
    $('#submit_dc_div').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {},
        messages: {},

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

            submitQueryForm();
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

var submitQueryForm = function () {

    var dcMonitorConfList = new Array();

    nttMonitor.monitorType = 1;
    if ($("#nttState").is(":checked")) {
        nttMonitor.state = 1
    } else {
        nttMonitor.state = 0

    }
    nttMonitor.balanceLimit = $('#nttLimit').val();
    nttMonitor.reminderEmail = $('#nttEmail').val().replace(/\s*/g, "");
    dcMonitorConfList.push(nttMonitor)


    gasMonitor.monitorType = 2;
    if ($("#gasState").is(":checked")) {
        gasMonitor.state = 1
    } else {
        gasMonitor.state = 0

    }
    gasMonitor.balanceLimit = $('#gasLimit').val();
    gasMonitor.reminderEmail = $('#gasEmail').val().replace(/\s*/g, "");
    dcMonitorConfList.push(gasMonitor)

    for (const dcMonitorConf of dcMonitorConfList) {
        if (dcMonitorConf.state == 1) {
            if (dcMonitorConf.balanceLimit == null || dcMonitorConf.balanceLimit == "") {
                if (dcMonitorConf.monitorType == 1) {
                    alert_error_text("Please enter the NTT balance alert value");
                    return;
                } else {
                    alert_error_text("Please enter the Gas Credit balance alert value");
                    return;
                }
            }
            if (dcMonitorConf.reminderEmail == null || dcMonitorConf.reminderEmail == '') {
                alert_error_text("Please enter the email address");
                return;
            }
        }

        var reminderEmailList = dcMonitorConf.reminderEmail;

        if (reminderEmailList.length > 0) {
            if (reminderEmailList.substr(-1) == ";") {
                reminderEmailList = reminderEmailList.substr(0, reminderEmailList.length - 1);
            } else {
                dcMonitorConf.reminderEmail = reminderEmailList + ";";
            }


            var reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
            for (const checkEmail of reminderEmailList.split(";")) {
                isok = reg.test(checkEmail);
                if (!isok) {
                    alert_error_text("Invalid email format");
                    return;
                }
            }

            if ((dcMonitorConf.reminderEmail.split(";").length - 1) > 10) {
                alert_error_text("You can set up to 10 recipients for email alerts");
                return;
            }

            if (dcMonitorConf.reminderEmail.length > 1000) {
                alert_error_text("The recipients cannot exceed 1000 characters");
                return;
            }

        }

    }

    $.ajax({
        "type": "post",
        "url": "/monitor/update",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(dcMonitorConfList),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg);
                queryMonitor()
            } else {
                alert_error("", data.msg);
            }
        }
    });

}


function selectBalanceType(_this) {
    var parentEle = $(_this).parents(".balance_type_select");
    if ($(_this).is(":checked")) {
        $(parentEle).find("input,textarea").not("[type=checkbox]").removeAttr("disabled");
    } else {
        $(parentEle).find("input,textarea").not("[type=checkbox]").attr("disabled", "disabled");
    }
}