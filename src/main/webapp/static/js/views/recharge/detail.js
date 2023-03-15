$(document).ready(function () {

    rechargeRecordId = REQ_HANDLE_.getQueryString("getDetail");
    var temp = getDetail(rechargeRecordId);
    if (temp == null) return false;
    // Initialize the page information
    initInfo(temp);

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
            } else {
                alert_error_text(data.msg);
            }
        }
    });

    return salePriceInfo;
}


// Initialize the organization basic information
var initInfo = function (data) {

    $("#rechargeRecordId").val(data.rechargeRecordId);
    $("#chainAddress").html(initStringData(data.chainAddress));
    $("#chainName").html(initStringData(data.chainName));
    $("#gas").html(initGas(data.gas,data.rechargeUnit));
    $("#auditState").html(initStringData(COMMON_HANDLE.getAuditStateName(data.auditState)));

    $("#createUser").html(initStringData(data.createUser));

    $("#createTime").html(initStringData(data.createTime))
    $("#auditor").html(initStringData(data.auditor));
    $("#auditTime").html(initStringData(data.auditTime));
    $("#auditRemarks").html(initStringData(data.auditRemarks));
    $("#rechargeCode").html(initStringData(data.rechargeCode));
    $("#rechargeState").html(initStringData(COMMON_HANDLE.getGasRechargeStateName(data.rechargeState)));
    $("#transHash").html(initStringData(data.txHash));
    $("#rechargeTime").html(initStringData(data.rechargeTime));
    $("#rechargeType").html(initStringData(COMMON_HANDLE.getRechargeType(data.rechargeType)));

    $("#ntt").html(initStringData(data.ntt));
    $("#rechargeReault").html(initStringData(data.rechargeReault));

    if(data.auditState==1 && (data.auditor == null || data.auditor == "")){
        document.getElementById("reviewInfo").style.display="none";
    }


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