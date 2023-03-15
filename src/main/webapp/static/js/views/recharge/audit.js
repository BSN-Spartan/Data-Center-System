$(document).ready(function () {

    rechargeRecordId = REQ_HANDLE_.getQueryString("toAudit");
    var temp = getDetail(rechargeRecordId);
    if (temp == null) return false;
    initInfo(temp);

    handleSubmitAuditResult();

});

var getDetail = function (rechargeRecordId) {

    var salePriceInfo = null;

    $.ajax({
        "type": "get",
        "url": "/recharge/detail/" + rechargeRecordId + "?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_login("", data.msg);
                return false;
            } else if (data.code == 1) {
                salePriceInfo = data.data;
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            }  else {
                alert_error_text(data.msg);
            }
        }
    });

    return salePriceInfo;
}


var initInfo = function (data) {
    $("#rechargeRecordId").val(data.rechargeRecordId);
    $("#chainAddress").html(initStringData(data.chainAddress));
    $("#chainName").html(initStringData(data.chainName));
    $("#gas").html(initGas(data.gas,data.rechargeUnit));
    $("#createUser").html(initStringData(data.createUser));
    $("#createTime").html(initStringData(data.createTime))

    $("#checkResult1").attr("checked","checked");
    $("#checkResult2").removeAttr("checked");

};

let initStringData = function (data) {
    if (data == null || data == "") {
        return "--";
    }
    return data
}

let initGas = function (gas,rechargeUnit) {
    return gas + " " + rechargeUnit;
};


var handleSubmitAuditResult = function () {
    $('#save_form').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            checkRemark: {
                required: true,
                maxlength: 200
            }
        },
        messages: {
            checkRemark: {
                required: "Please enter the comment",
                maxlength: "The length of comment cannot exceed 200 characters"
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
            submitFormAuditResult();
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

var submitFormAuditResult = function () {
    var checkRemark = $("#checkRemark").val();
    var checkResult=$("input[name='checkResult']:checked").val();
    // Request Backend System
    var fd = {};
    fd.rechargeRecordId = rechargeRecordId;
    fd.auditRemarks = checkRemark;
    fd.decision = checkResult;
    $.ajax({
        dataType: 'json',
        contentType: "application/json",
        type: 'post',
        data:  JSON.stringify(fd),
        async: false,
        url: "/recharge/audit",
        processData: false,
        success: function (data) {
            if (data.code == 1) {
                alert_success("","Submitted successfully",gotoIndex)
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("","Not Approved："+data.msg)
            }
        }
    });
};

function gotoIndex() {
    REQ_HANDLE_.location_('/recharge');
}
