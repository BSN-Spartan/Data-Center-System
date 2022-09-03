var COMMON_HANDLE = {
    ENERGY_RECHARGE_: "https://bsn.foundation/ntt-account-energy-recharge",
    FOUNDATION_WEBSITE_: "https://bsn.foundation",
    RECHARGE_STATE: [
        {"type": 0, "name": "In Progress"},
        {"type": 1, "name": "Success"},
        {"type": 2, "name": "Failed"},
        {"type": 3, "name": "Pending"},
    ],
    CHAIN_TYPE: [
        {"type": 1, "name": "Ethereum"},
        {"type": 2, "name": "Cosmos"},
        {"type": 3, "name": "Polygon"}
    ],
    CHAIN_STATE: [
        {"type": 1, "name": "Normal operation"},
        {"type": 2, "name": "Offline"}
    ],
    TX_TYPE: [
        {"type": 0, "name": "Recharge"},
        {"type": 1, "name": "Recharge succeeded"},
        {"type": 2, "name": "Recharge failed"}
    ], NTT_TX_TYPE: [
        {"type": 0, "name": "Node Establishment Incentive Program"},
        {"type": 1, "name": "Data Center Monthly Incentive Program"},
        {"type": 3, "name": "Top up Gas Credit"},
        {"type": 4, "name": "Top up NTT"},
        {"type": 7, "name": "Refund for Gas Credit top-up failed"},
        {"type": 8, "name": "NTT Collection"},
        {"type": 9, "name": "NTT Present"},
        {"type": 10, "name": "Emergency Gas Credit top-up commission charge"}
    ],
    NODE_STATE: [
        {"type": 0, "name": "Pending Registration"},
        {"type": 1, "name": "Registration In Progress"},
        {"type": 2, "name": "Registration Testing"},
        {"type": 3, "name": "Registration failed"},
        {"type": 4, "name": "Registration complete"}
    ],
    getRechargeStateSelect: function () {
        return COMMON_HANDLE.getSelect(COMMON_HANDLE.RECHARGE_STATE);
    },
    getRechargeStateName: function (type) {
        return COMMON_HANDLE.getName(type, COMMON_HANDLE.RECHARGE_STATE);
    },
    getChainTypeSelect: function () {
        return COMMON_HANDLE.getSelect(COMMON_HANDLE.CHAIN_TYPE);
    },
    getChainTypeName: function (type) {
        return COMMON_HANDLE.getName(type, COMMON_HANDLE.CHAIN_TYPE);
    },
    getChainStateSelect: function () {
        return COMMON_HANDLE.getSelect(COMMON_HANDLE.CHAIN_STATE);
    },
    getChainStateName: function () {
        return COMMON_HANDLE.getName(type, COMMON_HANDLE.CHAIN_STATE);
    },
    getNodeStateName: function (type) {
        return COMMON_HANDLE.getName(type, COMMON_HANDLE.NODE_STATE);
    },
    getNodeStateSelect: function (type) {
        return COMMON_HANDLE.getSelect(COMMON_HANDLE.NODE_STATE);
    },
    getTxTypeSelect: function () {
        return COMMON_HANDLE.getSelect(COMMON_HANDLE.RECHARGE_STATE);
    },
    getTxTypeName: function (type) {
        return COMMON_HANDLE.getName(type, COMMON_HANDLE.TX_TYPE);
    }
    ,
    getNttTxTypeSelect: function () {
        return COMMON_HANDLE.getSelect(COMMON_HANDLE.NTT_TX_TYPE);
    },
    getNttTxTypeName: function (type) {
        return COMMON_HANDLE.getName(type, COMMON_HANDLE.NTT_TX_TYPE);
    },
    getSelect: function (list) {
        var op = '<option value="">All</option>';
        for (var i = 0; i < list.length; i++) {
            op += '<option value="' + list[i].type + '">' + list[i].name + '</option>';
        }
        return op;
    },
    getName: function (type, list) {
        for (var i = 0; i < list.length; i++) {
            if (list[i].type == type) {
                return list[i].name;
            }
        }
        return "--";
    },
    initFrameInfo: function (frameId, frameType) {
        var url = '/sys/frame/listAllFrame';
        if (frameType != null && frameType.length > 0) {
            url = '/sys/frame/getFrameListByType/' + frameType;
        }
        $.ajax({
            "type": "get",
            "url": url + "?ranparam=" + (new Date()).valueOf(),
            "dataType": "json",
            "success": function (data) {
                if (data.code == 1) {
                    var frame = data.data;
                    var op = '<option value="">All</option>';
                    for (var i = 0; i < frame.length; i++) {
                        op += '<option value="' + frame[i].frameId + '">' + FRAME_HANDLE.initFrameNameByFrame(frame[i]) + '</option>';
                    }
                    $("#" + frameId).html(op);
                }
            }
        });
    },
    initFrameTypeInfo: function (selectId) {
        var url = '/sys/frame/listAllFrameType';
        $.ajax({
            "type": "get",
            "url": url + "?ranparam=" + (new Date()).valueOf(),
            "dataType": "json",
            "success": function (data) {
                if (data.code == 1) {
                    var frame = data.data;
                    var op = '<option value="">All</option>';
                    for (var i = 0; i < frame.length; i++) {
                        op += '<option value="' + frame[i].frameType + '">' + frame[i].frameType + '</option>';
                    }
                    $("#" + selectId).html(op);
                }
            }
        });
    },
    checkPasswordExpired: function () {
        $.ajax({
            "type": "post",
            "url": "/recharge/checkPasswordExpired",
            "dataType": "json",
            "contentType": "application/json",
            "success": function (data) {
                if (data.code == 1) {
                    $("#password_div").hide();
                } else {
                    $("#password_div").show();
                }
            }
        });
    },
    initFormatDefaultSubStringCopy: function (data, copy) {
        return DATA_FORMAT.subStringCopy(data, 15, ",", copy);
    },
    copyData: function (data) {
        if (data != null && data != '') {
            var copy = '&nbsp;&nbsp;<a onclick="COMMON_HANDLE.copy(\'' + data + '\')" href="javascript:void(0);" title="copy" ><i class="menu-icon fa fa-copy"></i></a>';
            return COMMON_HANDLE.initFormatDefaultSubStringCopy(data, copy)
        }
        return "--";
    },
    copyDataBase64: function (data) {
        if (data != null && data != '') {
            var base64Data = $.base64.encode(data);
            var copy = '&nbsp;&nbsp;<a onclick="COMMON_HANDLE.copyBase64(\'' + base64Data + '\')" href="javascript:void(0);" title="copy" ><i class="menu-icon fa fa-copy"></i></a>';
            return COMMON_HANDLE.initFormatDefaultSubStringCopy(data, copy)
        }
        return "--";
    },
    copy: function (data) {
        var copyTxtInput = $("<input style='position: absolute;top: 0;left: 0;opacity: 0;z-index: -10;' type='text' id='copyTxt'/>");
        $('body').append(copyTxtInput);

        var copyTxt = $("#copyTxt");
        copyTxt.val(data);
        copyTxt.select();
        document.execCommand("Copy");
        alert_success_auto_close("", "Copy Success", 1000)
    },
    copyBase64: function (data) {
        var copyTxtInput = $("<input style='position: absolute;top: 0;left: 0;opacity: 0;z-index: -10;' type='text' id='copyTxt'/>");
        $('body').append(copyTxtInput);

        var copyTxt = $("#copyTxt");
        copyTxt.val($.base64.decode(data));
        copyTxt.select();
        document.execCommand("Copy");
        alert_success_auto_close("", "Copy Success", 1000)
    }
};



