$(document).ready(function () {

    salePriceId = REQ_HANDLE_.getQueryString("getDetail");
    var temp = getDetail(salePriceId);
    if (temp == null) return false;
    // Initialize the page information
    initInfo(temp);

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
            }  else {
                alert_error_text(data.msg);
            }
        }
    });

    return salePriceInfo;
}


// Initialize the organization basic information
var initInfo = function (data) {

    var salePriceDetail = data.salePriceDetail;

    $("#salePriceId").val(salePriceDetail.salePriceId);
    $("#chainName").html(initStringData(salePriceDetail.chainName));
    $("#state").html(initStringData(COMMON_HANDLE.getSalePriceStateName(salePriceDetail.state)));
    $("#salePrice").html(initStringData(initGasPrice(salePriceDetail.gas,salePriceDetail.salePrice)));
    $("#checkUserName").html(initStringData(salePriceDetail.checkUserName));
    $("#startDate").html(initStringData(salePriceDetail.startDate));
    $("#checkTime").html(initStringData(salePriceDetail.checkTime));
    $("#createUserName").html(initStringData(salePriceDetail.createUserName));
    $("#checkRemark").html(initStringData(salePriceDetail.checkRemark));
    $("#createTime").html(initStringData(salePriceDetail.createTime))

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
