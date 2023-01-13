$(document).ready(function () {

    salePriceId = REQ_HANDLE_.getQueryString("toAudit");
    var temp = getDetail(salePriceId);
    if (temp == null) return false;
    initInfo(temp);

    handleSubmitAuditResult();

});

var getDetail = function (salePriceId) {

    var salePriceInfo = null;

    $.ajax({
        "type": "get",
        "url": "/price/detail/" + salePriceId + "?ranparam=" + (new Date()).valueOf(),
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
    var salePriceDetail = data.salePriceDetail;
    $("#salePriceId").val(salePriceDetail.salePriceId);
    $("#chainName").html(initStringData(salePriceDetail.chainName));
    $("#salePrice").html(initStringData(initGasPrice(salePriceDetail.gas,salePriceDetail.salePrice)));
    $("#startDate").html(initStringData(salePriceDetail.startDate));
    $("#createUserName").html(initStringData(salePriceDetail.createUserName));
    $("#createTime").html(initStringData(salePriceDetail.createTime))

    $("#checkResult1").attr("checked","checked");
    $("#checkResult2").removeAttr("checked");

};

let initGasPrice = function (gas,salePrice) {
    var gasPrice = salePrice + " USD : "+ Math.round(gas*100);
    return gasPrice;
};

let initStringData = function (data) {
    if (data == null || data == "") {
        return "--";
    }
    return data
}


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
    var fd = new FormData();
    fd.append("checkRemark",checkRemark);
    fd.append("salePriceId",salePriceId);
    fd.append("checkResult",checkResult);
    $.ajax({
        dataType: 'json',
        type: 'post',
        data: fd,
        async: false,
        url: "/price/audit",
        processData: false,
        contentType: false,
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
    REQ_HANDLE_.location_('/price');
}
