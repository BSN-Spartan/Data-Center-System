var ROLE_HANDLE = {
    STATE_: [
        {"code": 1, "name": "Enabled"},
        {"code": 2, "name": "Disabled"}
    ],
    getStateSelect: function () {
        var op = '<option value="">All</option>';
        var stateList = ROLE_HANDLE.STATE_;
        for (var i = 0; i < stateList.length; i++) {
            op += '<option value="' + stateList[i].code + '">' + stateList[i].name + '</option>';
        }
        return op;
    },
    getStateName: function (code) {
        var stateList = ROLE_HANDLE.STATE_;
        for (var i = 0; i < stateList.length; i++) {
            if (stateList[i].code == code) {
                return stateList[i].name;
            }
        }
        return "";
    },
    getRoleInfo: function (roleId) {
        var roleInfo = null;
        $.ajax({
            "type": "get",
            "url": "/sys/role/get/" + roleId + "?ranparam=" + (new Date()).valueOf(),
            "dataType": "json",
            cache: false,
            "async": false,
            "success": function (data) {
                if (data.code == 2) {
                    alert_error_back("", data.msg, "/role");
                } else if (data.code == 1) {
                    roleInfo = data.data;
                } else if (data.code == 3) {
                    alert_success_login(data.msg);
                } else {
                    alert_error_text(data.msg);
                }
            }
        });
        return roleInfo;
    }
};