var online = {};
var stripe = {};
var coinbase = {};
var ongoOrderData = [];
var ongoOrderStripeEnableStatus = 0;
var ongoOrderCoinbaseEnableStatus = 0;

var privateKey = "";
var endpointSecret = "";
var apiKey = "";
var apiVersion = "";

var privateKeyState = true;
var endpointSecretState = true;
var apiKeyState = true;
var newPrivateKey = "";
var newEndpointSecret = "";
var newApiKey = "";

$(document).ready(function () {
    queryPayCenter();
    queryOngoorder();
    handleSubmit();
    handleInputParam();
});

function handleInputParam(){
    if(!privateKeyState){
        newPrivateKey = document.getElementById("stripe.privateKey").value;
        if(newPrivateKey.length==0){
            document.getElementById("privateKeyImg").setAttribute("style","display: none");
        }else{
            document.getElementById("privateKeyImg").setAttribute("style","width: 23px;height: 23px;");
        }
    }
    if(!endpointSecretState){
        newEndpointSecret = document.getElementById("stripe.endpointSecret").value;
        if(newEndpointSecret.length==0){
            document.getElementById("endpointSecretImg").setAttribute("style","display: none");
        }else{
            document.getElementById("endpointSecretImg").setAttribute("style","width: 23px;height: 23px;");
        }
    }
    if(!apiKeyState){
        newApiKey = document.getElementById("coinbase.apiKey").value;
        if(newApiKey.length==0){
            document.getElementById("apiKeyImg").setAttribute("style","display: none");
        }else{
            document.getElementById("apiKeyImg").setAttribute("style","width: 23px;height: 23px;");
        }
    }


}


function onlinePayTypeFunction() {
    if ($("#onlinePayType").is(":checked")) {
        document.getElementById('online.bankName').removeAttribute('disabled')
        document.getElementById('online.bankAccount').removeAttribute('disabled')
        document.getElementById('online.bankAddress').removeAttribute('disabled')
        document.getElementById('online.swiftCode').removeAttribute('disabled')
        online.enableStatus = 0
    } else {
        document.getElementById('online.bankName').setAttribute('disabled', 'disabled')
        document.getElementById('online.bankAccount').setAttribute('disabled', 'disabled')
        document.getElementById('online.bankAddress').setAttribute('disabled', 'disabled')
        document.getElementById('online.swiftCode').setAttribute('disabled', 'disabled')
        online.enableStatus = 1
    }
};

function stripePayTypeFunction() {
    if ($("#stripePayType").is(":checked")) {
        if(!privateKeyState){
            document.getElementById('stripe.privateKey').removeAttribute('disabled')
        }
        if(!endpointSecretState){
            document.getElementById('stripe.endpointSecret').removeAttribute('disabled')
        }
        stripe.enableStatus = 0
        ongoOrderStripeEnableStatus=1
    } else {
        document.getElementById('stripe.privateKey').setAttribute('disabled', 'disabled')
        document.getElementById('stripe.endpointSecret').setAttribute('disabled', 'disabled')
        stripe.enableStatus = 1
        ongoOrderStripeEnableStatus=0
    }
};

function coinbasePayTypeFunction() {
    if ($("#coinbasePayType").is(":checked")) {
        if(newApiKey.length==0 || !apiKeyState){
            document.getElementById('coinbase.apiKey').removeAttribute('disabled')
        }

        document.getElementById('coinbase.apiVersion').removeAttribute('disabled')
        coinbase.enableStatus = 0
        ongoOrderCoinbaseEnableStatus=1
    } else {
        document.getElementById('coinbase.apiKey').setAttribute('disabled', 'disabled')
        document.getElementById('coinbase.apiVersion').setAttribute('disabled', 'disabled')
        coinbase.enableStatus = 1
        ongoOrderCoinbaseEnableStatus=0
    }
};

function hideDisplay(elementId,hideContent){

    document.getElementById(elementId).setAttribute('disabled', 'disabled');

    if(hideContent.length>10){
        const temp = hideContent.substr(6,hideContent.length-10);
        const displayContent = hideContent.replace(temp,'******************************');
        return displayContent;
    }else{
        return hideContent;
    }

}

function unHideDisplay(displayContent){

    switch(displayContent){
        case 'stripe.privateKey':
            if(privateKeyState){
                document.getElementById("stripe.privateKey").value = newPrivateKey
                if ($("#stripePayType").is(":checked")) {
                    document.getElementById('stripe.privateKey').removeAttribute('disabled', 'disabled')
                }

                privateKeyState = false;
                document.getElementById("privateKeyImg").setAttribute("src","/static/images/btn/btn_undisplay_icon.png");
            }else{
                newPrivateKey = document.getElementById("stripe.privateKey").value;
                document.getElementById("stripe.privateKey").value = hideDisplay("stripe.privateKey",newPrivateKey)
                privateKeyState = true;
                document.getElementById("privateKeyImg").setAttribute("src","/static/images/btn/btn_display_icon.png");
            }
            break;
        case 'stripe.endpointSecret':
            if(endpointSecretState){
                document.getElementById("stripe.endpointSecret").value = newEndpointSecret
                if ($("#stripePayType").is(":checked")) {
                    document.getElementById('stripe.endpointSecret').removeAttribute('disabled', 'disabled')
                }
                endpointSecretState = false;
                document.getElementById("endpointSecretImg").setAttribute("src","/static/images/btn/btn_undisplay_icon.png");
            }else{
                newEndpointSecret = document.getElementById("stripe.endpointSecret").value;
                document.getElementById("stripe.endpointSecret").value = hideDisplay("stripe.endpointSecret",newEndpointSecret)
                endpointSecretState = true;
                document.getElementById("endpointSecretImg").setAttribute("src","/static/images/btn/btn_display_icon.png");

            }
            break;
        case 'coinbase.apiKey':
            if(apiKeyState){
                document.getElementById("coinbase.apiKey").value = newApiKey
                if ($("#coinbasePayType").is(":checked")) {
                    document.getElementById('coinbase.apiKey').removeAttribute('disabled', 'disabled')
                }
                apiKeyState = false;
                document.getElementById("apiKeyImg").setAttribute("src","/static/images/btn/btn_undisplay_icon.png");
            }else{
                newApiKey = document.getElementById("coinbase.apiKey").value;
                document.getElementById("coinbase.apiKey").value = hideDisplay("coinbase.apiKey",newApiKey)
                apiKeyState = true;
                document.getElementById("apiKeyImg").setAttribute("src","/static/images/btn/btn_display_icon.png");
            }
            break;
    }
}


function queryPayCenter() {
    privateKeyState = true;
    endpointSecretState = true;
    apiKeyState = true;
    $.ajax({
        "type": "get",
        "url": "/ground/portalconfiguration/query/paycenter",
        "datatype": "json",
        "success": function (data) {
            online.enableStatus = 1;
            coinbase.enableStatus = 1;
            stripe.enableStatus = 1;
            for (const datum of data.data) {
                if (datum.payType == 1) {
                    ongoOrderStripeEnableStatus = datum.enableStatus
                    privateKey = datum.privateKey
                    endpointSecret = datum.endpointSecret
                    newPrivateKey = datum.privateKey
                    newEndpointSecret = datum.endpointSecret
                }
                if (datum.payType == 2) {
                    ongoOrderCoinbaseEnableStatus = datum.enableStatus
                    apiKey = datum.apiKey
                    newApiKey = datum.apiKey
                    apiVersion = datum.apiVersion
                }
            }
            if (data.data.length == 0) {
                alert_error("No payment method is configured")
                document.getElementById('online.bankName').setAttribute('disabled', 'disabled')
                document.getElementById('online.bankAccount').setAttribute('disabled', 'disabled')
                document.getElementById('online.bankAddress').setAttribute('disabled', 'disabled')
                document.getElementById('online.swiftCode').setAttribute('disabled', 'disabled')
                online.enableStatus = 1

                document.getElementById('coinbase.apiKey').setAttribute('disabled', 'disabled')
                document.getElementById('coinbase.apiVersion').setAttribute('disabled', 'disabled')
                coinbase.enableStatus = 1

                document.getElementById('stripe.privateKey').setAttribute('disabled', 'disabled')
                document.getElementById('stripe.endpointSecret').setAttribute('disabled', 'disabled')
                stripe.enableStatus = 1

            }
            for (const datum of data.data) {
                if (datum.payType == 3) {//Offline
                    if (datum.enableStatus == 0) {
                        document.getElementById("onlinePayType").value = datum.payType
                        document.getElementById("online.bankName").value = datum.bankName
                        document.getElementById("online.bankAccount").value = datum.bankAccount
                        document.getElementById("online.bankAddress").value = datum.bankAddress
                        document.getElementById("online.swiftCode").value = datum.swiftCode
                        online.enableStatus = datum.enableStatus
                        online.paymentTypeId = datum.paymentTypeId
                        online = datum;
                        $("#onlinePayType").attr("checked", "checked")
                    } else {
                        online.paymentTypeId = datum.paymentTypeId
                        document.getElementById("onlinePayType").value = datum.payType
                        document.getElementById("online.bankName").value = datum.bankName
                        document.getElementById("online.bankAccount").value = datum.bankAccount
                        document.getElementById("online.bankAddress").value = datum.bankAddress
                        document.getElementById("online.swiftCode").value = datum.swiftCode
                    }
                }
                if (datum.payType == 2) {//Coinbase
                    if (datum.enableStatus == 0) {
                        document.getElementById("coinbasePayType").value = datum.payType
                        document.getElementById("coinbase.apiKey").value = hideDisplay("coinbase.apiKey",datum.apiKey)
                        document.getElementById("coinbase.apiVersion").value = datum.apiVersion
                        coinbase.enableStatus = datum.enableStatus
                        coinbase.paymentTypeId = datum.paymentTypeId
                        coinbase = datum
                        $("#coinbasePayType").attr("checked", "checked")
                    } else {
                        coinbase.paymentTypeId = datum.paymentTypeId
                        document.getElementById("coinbasePayType").value = datum.payType
                        document.getElementById("coinbase.apiKey").value = hideDisplay("coinbase.apiKey",datum.apiKey)
                        document.getElementById("coinbase.apiVersion").value = datum.apiVersion
                    }
                }

                if (datum.payType == 1) {//Stripe
                    if (datum.enableStatus == 0) {
                        document.getElementById("stripePayType").value = datum.payType
                        document.getElementById("stripe.privateKey").value = hideDisplay("stripe.privateKey",datum.privateKey)
                        document.getElementById("stripe.endpointSecret").value = hideDisplay("stripe.endpointSecret",datum.endpointSecret)
                        stripe.enableStatus = datum.enableStatus
                        stripe.paymentTypeId = datum.paymentTypeId
                        stripe = datum;
                        $("#stripePayType").attr("checked", "checked")
                    } else {
                        stripe.paymentTypeId = datum.paymentTypeId
                        document.getElementById("stripePayType").value = datum.payType
                        document.getElementById("stripe.privateKey").value = hideDisplay("stripe.privateKey",datum.privateKey)
                        document.getElementById("stripe.endpointSecret").value = hideDisplay("stripe.endpointSecret",datum.endpointSecret)
                    }
                }


            }

            if (online.enableStatus == 1) {
                document.getElementById('online.bankName').setAttribute('disabled', 'disabled')
                document.getElementById('online.bankAccount').setAttribute('disabled', 'disabled')
                document.getElementById('online.bankAddress').setAttribute('disabled', 'disabled')
                document.getElementById('online.swiftCode').setAttribute('disabled', 'disabled')
                online.enableStatus = 1
            }
            if (coinbase.enableStatus == 1) {
                document.getElementById('coinbase.apiKey').setAttribute('disabled', 'disabled')
                document.getElementById('coinbase.apiVersion').setAttribute('disabled', 'disabled')
                coinbase.enableStatus = 1
            }
            if (stripe.enableStatus == 1) {
                document.getElementById('stripe.privateKey').setAttribute('disabled', 'disabled')
                document.getElementById('stripe.endpointSecret').setAttribute('disabled', 'disabled')
                stripe.enableStatus = 1
            }

            if(newApiKey.length==0){
                apiKeyState = false;
                document.getElementById("apiKeyImg").setAttribute("style","display: none");
                document.getElementById("apiKeyImg").setAttribute("src","/static/images/btn/btn_undisplay_icon.png");

            }
            if(newPrivateKey.length==0){
                privateKeyState = false;
                document.getElementById("privateKeyImg").setAttribute("style","display: none");
                document.getElementById("privateKeyImg").setAttribute("src","/static/images/btn/btn_undisplay_icon.png");

            }
            if(newEndpointSecret.length==0){
                endpointSecretState = false;
                document.getElementById("endpointSecretImg").setAttribute("style","display: none");
                document.getElementById("endpointSecretImg").setAttribute("src","/static/images/btn/btn_undisplay_icon.png");
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

            // submitQueryForm();
            deletePost()
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

function deletePost(postId) {
    submitQueryForm()
}

function queryOngoorder() {
    $.ajax({
        "type": "get",
        "url": "/ground/portalconfiguration/query/ongoorder",
        "contentType": "application/json",
        "success": function (data) {
            ongoOrderData = data.data
        }
    });
}

var submitQueryForm = function () {
    var stripeNum = 0;
    var coinbaseNum = 0;

    var dcPaymentTypeReqVOS = new Array();

    online.bankName = document.getElementById("online.bankName").value
    online.bankAccount = document.getElementById("online.bankAccount").value
    online.bankAddress = document.getElementById("online.bankAddress").value
    online.swiftCode = document.getElementById("online.swiftCode").value
    online.payType = document.getElementById("onlinePayType").value
    dcPaymentTypeReqVOS.push(online)

    stripe.privateKey = newPrivateKey;
    stripe.endpointSecret = newEndpointSecret;
    stripe.payType = document.getElementById("stripePayType").value
    dcPaymentTypeReqVOS.push(stripe)

    coinbase.apiKey = newApiKey;
    coinbase.apiVersion = document.getElementById("coinbase.apiVersion").value
    coinbase.payType = document.getElementById("coinbasePayType").value
    dcPaymentTypeReqVOS.push(coinbase)

    for (const dcPaymentTypeReqVO of dcPaymentTypeReqVOS) {
        if (dcPaymentTypeReqVO.enableStatus == 0) {
            if (dcPaymentTypeReqVO.payType == 1) {
                if (stripe.privateKey == null || stripe.endpointSecret == null || stripe.privateKey == "" || stripe.endpointSecret == "") {
                    alert_error_text("Please complete the Sripe information");
                    return;
                }
            }
            if (dcPaymentTypeReqVO.payType == 2) {
                if (coinbase.apiKey == null || coinbase.apiVersion == null || coinbase.apiKey == "" || coinbase.apiVersion == "") {
                    alert_error_text("Please complete the Coinbase information");
                    return;
                }
            }
            if (dcPaymentTypeReqVO.payType == 3) {
                if (online.bankName == null || online.bankAccount == null || online.bankAddress == null || online.swiftCode == null || online.bankName == "" || online.bankAccount == "" || online.bankAddress == "" || online.swiftCode == "") {
                    alert_error_text("Please complete the Online information");
                    return;
                }
            }
        }
    }


    if (ongoOrderStripeEnableStatus == 1 && ongoOrderCoinbaseEnableStatus == 1) {
        $.ajax({
            "type": "post",
            "url": "/ground/portalconfiguration/update/paycenter",
            "dataType": "json",
            "contentType": "application/json",
            "data": JSON.stringify(dcPaymentTypeReqVOS),
            "success": function (data) {
                if (data.code == 1) {
                    alert_success("", data.msg);
                    queryPayCenter()
                } else if (data.code == 3) {
                    alert_success_login(data.msg);
                } else {
                    alert_error("", data.msg);
                }
            }
        });
    } else if (ongoOrderStripeEnableStatus != 1 && ongoOrderCoinbaseEnableStatus != 1 && stripe.enableStatus == 1 && coinbase.enableStatus == 1) {
        $.ajax({
            "type": "get",
            "url": "/ground/portalconfiguration/query/ongoorder",
            "contentType": "application/json",
            "success": function (data) {
                for (const datum of data.data) {
                    if (datum.payType == 1) {
                        stripeNum++;
                    } else if (datum.payType == 2) {
                        coinbaseNum++;
                    }
                }
                if (stripeNum == 0 && coinbaseNum == 0) {
                    submitForm(dcPaymentTypeReqVOS)
                } else {
                    var stripeText = "";
                    if (coinbaseNum != 0) {
                        stripeText = "stripe" + " " + stripeNum + " " + "Top-up Order Record";
                    }
                    if (coinbaseNum != 0) {
                        stripeText += '\n' + "coinbase have" + " " + coinbaseNum + " " + "Top-up Order Record";
                    }

                    var title = "You have orders in the paytment progress, please be careful to modify.";
                    alert_confirm(title, stripeText, function () {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    });
                }
            }
        });
        return;
    } else if (ongoOrderStripeEnableStatus != 1 && stripe.enableStatus == 1) {
        if (stripe.enableStatus == ongoOrderStripeEnableStatus){
            $.ajax({
                "type": "post",
                "url": "/ground/portalconfiguration/update/paycenter",
                "dataType": "json",
                "contentType": "application/json",
                "data": JSON.stringify(dcPaymentTypeReqVOS),
                "success": function (data) {
                    if (data.code == 1) {
                        alert_success("", data.msg);
                        queryPayCenter()
                    } else if (data.code == 3) {
                        alert_success_login(data.msg);
                    } else {
                        alert_error("", data.msg);
                    }
                }
            });
        }else {
            $.ajax({
                "type": "get",
                "url": "/ground/portalconfiguration/query/ongoorder",
                "contentType": "application/json",
                "success": function (data) {
                    for (const datum of data.data) {
                        if (datum.payType == 1) {
                            stripeNum++;
                        } else if (datum.payType == 2) {
                            coinbaseNum++;
                        }
                    }
                    if (stripeNum == 0 && coinbaseNum == 0) {
                        submitForm(dcPaymentTypeReqVOS)
                    } else {
                        var stripeText = "";
                        if (stripeNum != 0) {
                            stripeText = "stripe have" + " " + stripeNum + " " + "Top-up Order Record";
                            var title = "You have orders in the paytment progress, please be careful to modify.";
                            alert_confirm(title, stripeText, function () {
                                $.ajax({
                                    "type": "post",
                                    "url": "/ground/portalconfiguration/update/paycenter",
                                    "dataType": "json",
                                    "contentType": "application/json",
                                    "data": JSON.stringify(dcPaymentTypeReqVOS),
                                    "success": function (data) {
                                        if (data.code == 1) {
                                            alert_success("", data.msg);
                                            queryPayCenter()
                                        } else {
                                            alert_error("", data.msg);
                                        }
                                    }
                                });

                            });
                        } else {
                            $.ajax({
                                "type": "post",
                                "url": "/ground/portalconfiguration/update/paycenter",
                                "dataType": "json",
                                "contentType": "application/json",
                                "data": JSON.stringify(dcPaymentTypeReqVOS),
                                "success": function (data) {
                                    if (data.code == 1) {
                                        alert_success("", data.msg);
                                        queryPayCenter()
                                    } else if (data.code == 3) {
                                        alert_success_login(data.msg);
                                    } else {
                                        alert_error("", data.msg);
                                    }
                                }
                            });
                        }

                    }
                }
            });
            return
        }
    } else if (ongoOrderCoinbaseEnableStatus != 1 && coinbase.enableStatus == 1) {
        $.ajax({
            "type": "get",
            "url": "/ground/portalconfiguration/query/ongoorder",
            "contentType": "application/json",
            "success": function (data) {
                for (const datum of data.data) {
                    if (datum.payType == 1) {
                        stripeNum++;
                    } else if (datum.payType == 2) {
                        coinbaseNum++;
                    }
                }
                if (stripeNum == 0 && coinbaseNum == 0) {
                    submitForm(dcPaymentTypeReqVOS)
                } else {
                    var stripeText = "";
                    if (coinbaseNum != 0) {
                        stripeText = "coinbase have" + " " + coinbaseNum + " " + "Top-up Order Record";
                        var title = "You have orders in the paytment progress, please be careful to modify.";
                        alert_confirm(title, stripeText, function () {
                            $.ajax({
                                "type": "post",
                                "url": "/ground/portalconfiguration/update/paycenter",
                                "dataType": "json",
                                "contentType": "application/json",
                                "data": JSON.stringify(dcPaymentTypeReqVOS),
                                "success": function (data) {
                                    if (data.code == 1) {
                                        alert_success("", data.msg);
                                        queryPayCenter()
                                    } else {
                                        alert_error("", data.msg);
                                    }
                                }
                            });
                        });
                    } else {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    }


                }
            }
        });
        return;
    } else {
        submitForm(dcPaymentTypeReqVOS)
    }

}

var submitForm = function (dcPaymentTypeReqVOS) {
    var stripeNum = 0;
    var coinbaseNum = 0;

    if ((stripe.privateKey != privateKey || stripe.endpointSecret != endpointSecret) && (coinbase.apiKey != apiKey || coinbase.apiVersion != apiVersion)){
        $.ajax({
            "type": "get",
            "url": "/ground/portalconfiguration/query/ongoorder",
            "contentType": "application/json",
            "success": function (data) {
                for (const datum of data.data) {
                    if (datum.payType == 1) {
                        stripeNum++;
                    } else if (datum.payType == 2) {
                        coinbaseNum++;
                    }
                }
                if (stripeNum != 0 && coinbaseNum != 0){
                    var stripeText = "";
                    stripeText = "stripe have" + " " + stripeNum + " " + "Top-up Order Record";
                    stripeText += '\n' + "coinbase have" + " " + coinbaseNum + " " + "Top-up Order Record";
                    var title = "You have orders in the paytment progress, please be careful to modify.";
                    alert_confirm(title, stripeText, function () {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    });
                } else if (stripeNum != 0) {
                    var stripeText = "";
                    stripeText = "stripe have" + " " + stripeNum + " " + "Top-up Order Record";
                    var title = "You have orders in the paytment progress, please be careful to modify.";
                    alert_confirm(title, stripeText, function () {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    });
                } else if (coinbaseNum != 0) {
                    var stripeText = "";
                    stripeText = "coinbase have" + " " + coinbaseNum + " " + "Top-up Order Record";
                    var title = "You have orders in the paytment progress, please be careful to modify.";
                    alert_confirm(title, stripeText, function () {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    });
                }else {
                    $.ajax({
                        "type": "post",
                        "url": "/ground/portalconfiguration/update/paycenter",
                        "dataType": "json",
                        "contentType": "application/json",
                        "data": JSON.stringify(dcPaymentTypeReqVOS),
                        "success": function (data) {
                            if (data.code == 1) {
                                alert_success("", data.msg);
                                queryPayCenter()
                            } else {
                                alert_error("", data.msg);
                            }
                        }
                    });
                }

            }
        });
    }
    else if (stripe.privateKey != privateKey || stripe.endpointSecret != endpointSecret) {
        $.ajax({
            "type": "get",
            "url": "/ground/portalconfiguration/query/ongoorder",
            "contentType": "application/json",
            "success": function (data) {
                for (const datum of data.data) {
                    if (datum.payType == 1) {
                        stripeNum++;
                    } else if (datum.payType == 2) {
                        coinbaseNum++;
                    }
                }
                if (stripeNum != 0) {
                    var stripeText = "";
                    stripeText = "stripe have" + " " + stripeNum + " " + "Top-up Order Record";
                    var title = "You have orders in the paytment progress, please be careful to modify.";
                    alert_confirm(title, stripeText, function () {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    });
                } else {
                    $.ajax({
                        "type": "post",
                        "url": "/ground/portalconfiguration/update/paycenter",
                        "dataType": "json",
                        "contentType": "application/json",
                        "data": JSON.stringify(dcPaymentTypeReqVOS),
                        "success": function (data) {
                            if (data.code == 1) {
                                alert_success("", data.msg);
                                queryPayCenter()
                            } else if (data.code == 3) {
                                alert_success_login(data.msg);
                            } else {
                                alert_error("", data.msg);
                            }
                        }
                    });
                }

            }
        });


    }
    else if (coinbase.apiKey != apiKey || coinbase.apiVersion != apiVersion) {
        $.ajax({
            "type": "get",
            "url": "/ground/portalconfiguration/query/ongoorder",
            "contentType": "application/json",
            "success": function (data) {
                for (const datum of data.data) {
                    if (datum.payType == 1) {
                        stripeNum++;
                    } else if (datum.payType == 2) {
                        coinbaseNum++;
                    }
                }
                if (coinbaseNum != 0) {
                    var stripeText = "";
                    stripeText = "coinbase have" + " " + coinbaseNum + " " + "Top-up Order Record";
                    var title = "You have orders in the paytment progress, please be careful to modify.";
                    alert_confirm(title, stripeText, function () {
                        $.ajax({
                            "type": "post",
                            "url": "/ground/portalconfiguration/update/paycenter",
                            "dataType": "json",
                            "contentType": "application/json",
                            "data": JSON.stringify(dcPaymentTypeReqVOS),
                            "success": function (data) {
                                if (data.code == 1) {
                                    alert_success("", data.msg);
                                    queryPayCenter()
                                } else {
                                    alert_error("", data.msg);
                                }
                            }
                        });
                    });
                } else {
                    $.ajax({
                        "type": "post",
                        "url": "/ground/portalconfiguration/update/paycenter",
                        "dataType": "json",
                        "contentType": "application/json",
                        "data": JSON.stringify(dcPaymentTypeReqVOS),
                        "success": function (data) {
                            if (data.code == 1) {
                                alert_success("", data.msg);
                                queryPayCenter()
                            } else if (data.code == 3) {
                                alert_success_login(data.msg);
                            } else {
                                alert_error("", data.msg);
                            }
                        }
                    });
                }
            }
        });
    } else {
        $.ajax({
            "type": "post",
            "url": "/ground/portalconfiguration/update/paycenter",
            "dataType": "json",
            "contentType": "application/json",
            "data": JSON.stringify(dcPaymentTypeReqVOS),
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


};
