$(document).ready(function () {


    var temp = getDetail();
    if (temp == null) return false;

    initInfo(temp);

    handleSubmitResult();

});


var getDetail = function () {

    var chainAccessInfo = null;

    $.ajax({
        "type": "post",
        "url": "/chainAccess/get",
        "dataType": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_login("", data.msg);
                return false;
            } else if (data.code == 1) {
                chainAccessInfo = data.data;
            }  else {
                alert_error_text(data.msg);
                return false;
            }
        }
    });

    return chainAccessInfo;
}

var initInfo = function (data) {

    var nodeConfigList = data.nodeConfigs;

    $("#gateway").val(!data.gatewayUrl?"":data.gatewayUrl);
    $("#wsGateway").val(!data.wsGatewayUrl?"":data.wsGatewayUrl);
    $("#grpcGateway").val(!data.grpcGatewayUrl?"":data.grpcGatewayUrl);


    $("#tps").val(!data.tps?"":data.tps);
    $("#tpd").val(!data.tpd?"":data.tpd);

    for (var i = 0; i < nodeConfigList.length; i++) {

        var nodeConfig = nodeConfigList[i];
        if (nodeConfig.chainId ==1){
            if(nodeConfig.gatewayType ==1){
                $("#frameType1").attr("checked","checked");
            }else{
                document.getElementById('chainCode1').setAttribute('disabled', 'disabled')
                document.getElementById('accessType11').setAttribute('disabled', 'disabled')
                document.getElementById('accessType12').setAttribute('disabled', 'disabled')

            }
            $("#chainCode1").val(!nodeConfig.chainCode?"":nodeConfig.chainCode);
            if (nodeConfig.jsonRpc ==1){
                $("#accessType11").attr("checked","checked");
            }
            if (nodeConfig.webSocket ==1){
                $("#accessType12").attr("checked","checked");
            }

        }
        if (nodeConfig.chainId ==2){
            if(nodeConfig.gatewayType ==1) {
                $("#frameType2").attr("checked", "checked");
            }else{
                document.getElementById('chainCode2').setAttribute('disabled', 'disabled')
                document.getElementById('accessType21').setAttribute('disabled', 'disabled')
                document.getElementById('accessType22').setAttribute('disabled', 'disabled')
                document.getElementById('accessType23').setAttribute('disabled', 'disabled')

            }
            $("#chainCode2").val(!nodeConfig.chainCode?"":nodeConfig.chainCode);
            if (nodeConfig.jsonRpc == 1) {
                $("#accessType21").attr("checked", "checked");
            }
            if (nodeConfig.webSocket == 1) {
                $("#accessType22").attr("checked", "checked");
            }
            if (nodeConfig.grpc == 1) {
                $("#accessType23").attr("checked", "checked");
            }
        }
        if (nodeConfig.chainId ==3){
            if(nodeConfig.gatewayType ==1) {
                $("#frameType3").attr("checked", "checked");
            }else{
                document.getElementById('chainCode3').setAttribute('disabled', 'disabled')
                document.getElementById('accessType31').setAttribute('disabled', 'disabled')
                document.getElementById('accessType32').setAttribute('disabled', 'disabled')

            }
            $("#chainCode3").val(!nodeConfig.chainCode?"":nodeConfig.chainCode);
            if (nodeConfig.jsonRpc == 1) {
                $("#accessType31").attr("checked", "checked");
            }
            if (nodeConfig.webSocket == 1) {
                $("#accessType32").attr("checked", "checked");
            }
        }

    }

};

function selectFrameType1(){
    if($("#frameType1").is(":checked")){
        document.getElementById('chainCode1').removeAttribute('disabled')
        document.getElementById('accessType11').removeAttribute('disabled')
        document.getElementById('accessType12').removeAttribute('disabled')
    }else {
        document.getElementById('chainCode1').setAttribute('disabled', 'disabled')
        document.getElementById('accessType11').setAttribute('disabled', 'disabled')
        document.getElementById('accessType12').setAttribute('disabled', 'disabled')
    }
};

function selectFrameType2(){
    if($("#frameType2").is(":checked")){
        document.getElementById('chainCode2').removeAttribute('disabled')
        document.getElementById('accessType21').removeAttribute('disabled')
        document.getElementById('accessType22').removeAttribute('disabled')
        document.getElementById('accessType23').removeAttribute('disabled')

    }else {
        document.getElementById('chainCode2').setAttribute('disabled', 'disabled')
        document.getElementById('accessType21').setAttribute('disabled', 'disabled')
        document.getElementById('accessType22').setAttribute('disabled', 'disabled')
        document.getElementById('accessType23').setAttribute('disabled', 'disabled')

    }
};


function selectFrameType3(){
    if($("#frameType3").is(":checked")){
        document.getElementById('chainCode3').removeAttribute('disabled')
        document.getElementById('accessType31').removeAttribute('disabled')
        document.getElementById('accessType32').removeAttribute('disabled')
    }else {
        document.getElementById('chainCode3').setAttribute('disabled', 'disabled')
        document.getElementById('accessType31').setAttribute('disabled', 'disabled')
        document.getElementById('accessType32').setAttribute('disabled', 'disabled')
    }
};



var handleSubmitResult = function () {
    $('#submit_dc_div').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            gateway: {
                required: true,
            }
        },
        messages: {
            gateway: {
                required: "Please enter the gateway URL"
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


    var gateway = $("#gateway").val();
    var wsGatewayUrl = $("#wsGateway").val();
    var grpcGatewayUrl = $("#grpcGateway").val();

    var tps = $("#tps").val();
    var tpd = $("#tpd").val();
    var nodeConfigs = new Array();

    var frameType1=$("input[name='frameType1']:checked").val();
    var frameType2=$("input[name='frameType2']:checked").val();
    var frameType3=$("input[name='frameType3']:checked").val();

    if(!frameType1 && !frameType2 && !frameType3) {
        alert_error("","Please select at least one chain");
        return false;
    }

    var chainCode1 = $("#chainCode1").val();
    var accessType11 = $("input[name='accessType11']:checked").val();
    var accessType12 = $("input[name='accessType12']:checked").val();

    if (frameType1) {
        if (chainCode1.length ===0){
            alert_error_text("Please enter the request ID of Spartan-I Chain");
            return false;
        }
        if (!accessType11 && !accessType12) {
            alert_error("Please enter the access method of Spartan-I Chain");
            return false;
        }
    }
    nodeConfigs.push(
        {
            "chainId": 1,
            "chainCode":chainCode1,
            "jsonRpc":!accessType11?0:1,
            "webSocket":!accessType12?0:1,
            "grpc":0,
            "gateWayType":!frameType1?0:1
        });

    var chainCode2 = $("#chainCode2").val();
    var accessType21 = $("input[name='accessType21']:checked").val();
    var accessType22 = $("input[name='accessType22']:checked").val();
    var accessType23 = $("input[name='accessType23']:checked").val();


    if (frameType2) {
        if (chainCode2.length ===0){
            alert_error_text("Please enter the request ID of Spartan-II Chain");
            return false;
        }
        if(grpcGatewayUrl.length===0 && accessType23){
            alert_error("Configure the GRPC Gateway URL");
            return false;
        }

        if (!accessType21 && !accessType22 && !accessType23){
            alert_error("Please enter the access method of Spartan-II Chain");
            return false;
        }

    }
    nodeConfigs.push(
        {
            "chainId": 2,
            "chainCode":chainCode2,
            "jsonRpc":!accessType21?0:1,
            "webSocket":!accessType22?0:1,
            "grpc":!accessType23?0:1,
            "gateWayType":!frameType2?0:1
        });

    var chainCode3 = $("#chainCode3").val();
    var accessType31 = $("input[name='accessType31']:checked").val();
    var accessType32 = $("input[name='accessType32']:checked").val();

    if (frameType3) {
        if (chainCode3.length ===0){
            alert_error_text("Please enter the request ID of Spartan-III Chain");
            return false;
        }
        if (typeof(accessType31)=='undefined' && typeof(accessType32)=='undefined') {
            alert_error("Please enter the access method of Spartan-III Chain");
            return false;
        }
    }
    nodeConfigs.push(
        {
            "chainId": 3,
            "chainCode":chainCode3,
            "jsonRpc":!accessType31?0:1,
            "webSocket":!accessType32?0:1,
            "grpc":0,
            "gateWayType":!frameType3?0:1
        });
    if(wsGatewayUrl.length<=0){
        if ((frameType1 && accessType12) || (frameType2 && accessType22) || (frameType3 && accessType32)){
            alert_error("Configure the WebSocket gateway address");
            return false;
        }
    }





    var data = new Object();
    data.gatewayUrl = gateway;
    data.wsGatewayUrl = wsGatewayUrl;
    data.grpcGateWayUrl = grpcGatewayUrl;

    data.tps = tps?tps:-1;
    data.tpd = tpd?tpd:-1;

    data.nodeConfigs = nodeConfigs;

    var title = "Are you sure you want to modify it?";
    var stripeText="After modification, the previously configured parameters are unavailable";
    alert_confirm(title, stripeText, function () {
        $.ajax({
            dataType: 'json',
            type: 'post',
            url: "/chainAccess/add",
            data: JSON.stringify(data),
            dataType: "json",
            async: false,
            processData: false,
            contentType: false,
            success: function (data) {
                if (data.code == 1) {
                    alert_success("","Configuration Successful")
                } else {
                    alert_error("","Configuration Failed："+data.msg)
                    return false;
                }
            }
        });
    });
};

