
var CHAIN_INFO = null;

var CHAIN_HANDLE = {
    CHAIN_INFO: null,
    CHAIN_PRICE_INFO: [],
    initChainInfo: function (selectId, chainId) {
        if (CHAIN_HANDLE.CHAIN_INFO == null) {
            $.ajax({
                "type": "get",
                "url": "/sys/chain/listOpbChain?ranparam=" + (new Date()).valueOf(),
                "dataType": "json",
                "success": function (data) {
                    if (data.code == 1) {
                        CHAIN_HANDLE.CHAIN_INFO = data.data;

                        CHAIN_HANDLE.initSelect(chainId, selectId);
                    }
                }
            });
        } else {
            CHAIN_HANDLE.initSelect(chainId, selectId);
        }
    },
    initChainInfoNotAll: function (selectId, chainId) {
        if (CHAIN_HANDLE.CHAIN_INFO == null) {
            $.ajax({
                "type": "get",
                "url": "/sys/chain/listOpbChain?ranparam=" + (new Date()).valueOf(),
                "dataType": "json",
                "success": function (data) {
                    if (data.code == 1) {
                        CHAIN_HANDLE.CHAIN_INFO = data.data;

                        CHAIN_HANDLE.initSelectNotAll(chainId, selectId);
                    }
                }
            });
        } else {
            CHAIN_HANDLE.initSelectNotAll(chainId, selectId);
        }
    },

    initSelect: function (chainId, selectId) {

        var op = '<option value="">All</option>';

        var chain = CHAIN_HANDLE.CHAIN_INFO
        for (var i = 0; i < chain.length; i++) {
            var s = '';
            if (chainId == chain[i].chainId) {
                s = ' selected ';
            }
            op += '<option value="' + chain[i].chainId + '" ' + s + '>' + chain[i].chainName + '</option>';
        }
        $("#" + selectId).html(op);
    },

    initSelectNotAll: function (chainId, selectId) {

        var op = '';

        var chain = CHAIN_HANDLE.CHAIN_INFO
        for (var i = 0; i < chain.length; i++) {
            var s = '';
            if (chainId == chain[i].chainId) {
                s = ' selected ';
            }
            op += '<option value="' + chain[i].chainId + '" ' + s + '>' + chain[i].chainName + '</option>';
        }
        $("#" + selectId).html(op);
    },
    getChainRechargeUnit: function (chainId) {
        if (CHAIN_HANDLE.CHAIN_INFO != null && CHAIN_HANDLE.CHAIN_INFO.length > 0) {
            for (let i = 0; i < CHAIN_HANDLE.CHAIN_INFO.length; i++) {
                if (CHAIN_HANDLE.CHAIN_INFO[i].chainId == chainId) {
                    return CHAIN_HANDLE.CHAIN_INFO[i].rechargeUnit
                }
            }
        }
        return "";
    },
    getRechargeNtt: function (chainId, rechargeGas, showNttId) {
        if (chainId == null || rechargeGas == null || chainId == 0 || rechargeGas == 0) {
            $("#" + showNttId).html("0.000000000000");
            return
        }

        let ntt = CHAIN_HANDLE.getRechargeNtt(chainId);

        if (ntt == null) {
            $.ajax({
                "type": "post",
                "url": "/recharge/getChainPriceRatio/" + chainId,
                "datatype": "json",
                "success": function (data) {
                    if (data.code == 1) {
                        let ntt = data.data

                        CHAIN_HANDLE.CHAIN_PRICE_INFO.push({"chainId": chainId, "ntt": ntt});

                        $("#" + showNttId).html(ntt * rechargeGas);

                    } else {
                        alert_error("", data.msg);
                    }
                },
                "error": function () {
                    $("#" + showNttId).html("0.000000000000");
                }
            });
        } else {
            $("#" + showNttId).html(ntt * rechargeGas);
        }
    },
    getChainPrice: function (chainId) {
        if (CHAIN_HANDLE.CHAIN_PRICE_INFO != null && CHAIN_HANDLE.CHAIN_PRICE_INFO.length > 0) {
            for (let i = 0; i < CHAIN_HANDLE.CHAIN_PRICE_INFO.length; i++) {
                if (chainId == CHAIN_HANDLE.CHAIN_PRICE_INFO[i].chainId) {
                    return CHAIN_HANDLE.CHAIN_PRICE_INFO[i].ntt;
                }
            }
        }
        return null;
    },
    toNonExponential : function (num) {
        return num.replace(/\D/g,'');
    }
};






