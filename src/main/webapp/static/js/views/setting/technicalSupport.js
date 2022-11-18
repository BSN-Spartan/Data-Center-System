$(document).ready(function () {

    var temp = getDetail();
    if (temp == null) return false;

    initInfo(temp);

    handleSubmitResult();

});

var getDetail = function () {

    var techSupportInfo = null;

    $.ajax({
        "type": "get",
        "url": "/ground/portalconfiguration/query/systemconf",
        "dataType": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_login("", data.msg);
                return false;
            } else if (data.code == 1) {
                techSupportInfo = data.data;
            } else {
                alert_error_text(data.msg);
            }
        }
    });

    return techSupportInfo;
}

var initInfo = function (data) {


    for (i in data) {
            $("#" + data[i].confCode.toLowerCase()).val(data[i].confValue);
    }

};


var handleSubmitResult = function () {
    $('#submit_dc_div').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            checkRemark: {
                required: true,
            }
        },
        messages: {
            checkRemark: {
                required: "Please enter the remark"
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
            submitFormResult();
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

var submitFormResult = function () {

    var data = new Array();


    data.push(
        {
            "confCode":"Telegram",
            "confValue": $("#telegram").val()
        },
        {
            "confCode":"Whatsapp",
            "confValue": $("#whatsapp").val()
        },
        {
            "confCode":"Reddit",
            "confValue": $("#reddit").val()
        },
        {
            "confCode":"Discord",
            "confValue": $("#discord").val()
        },
        {
            "confCode":"Twitter",
            "confValue":  $("#twitter").val()
        },
        {
            "confCode":"address",
            "confValue":  $("#address").val()
        },
        {
            "confCode":"email",
            "confValue":  $("#email").val()
        }, {
            "confCode":"phone",
            "confValue":  $("#phone").val()
        }
    )
    var postFormData = new FormData();
    var blob = new Blob([JSON.stringify(data)], {type: 'application/json'});
    postFormData.append("dcSystemConfReqVOList", blob);

    $.ajax({
        "type": "post",
        "url": "/ground/portalconfiguration/update/technicalsupport",
        "datatype": "json",
        "data": postFormData,
        "processData": false,
        "contentType": false,
        success: function (data) {
            if (data.code == 1) {
                alert_success("", "Configuration successful")
            } else {
                alert_error("", "Configuration Failed：" + data.msg)
            }
        }
    });
};


