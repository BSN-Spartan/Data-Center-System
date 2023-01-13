var USER_HANDLE = {
    STATE_: [
        {"code": 1, "name": "Enabled"},
        {"code": 0, "name": "Disabled"}
    ],
    getStateSelect: function () {
        var op = '<option value="">All</option>';
        var stateList = USER_HANDLE.STATE_;
        for (var i = 0; i < stateList.length; i++) {
            op += '<option value="' + stateList[i].code + '">' + stateList[i].name + '</option>';
        }
        return op;
    },
    getStateName: function (code) {
        var stateList = USER_HANDLE.STATE_;
        for (var i = 0; i < stateList.length; i++) {
            if (stateList[i].code == code) {
                return stateList[i].name;
            }
        }
        return "";
    },

    getUserInfo: function (userId) {
        var userInfo = null;
        $.ajax({
            "type": "get",
            "url": "/sys/user/get/" + userId + "?ranparam=" + (new Date()).valueOf(),
            "dataType": "json",
            cache: false,
            "async": false,
            "success": function (data) {
                if (data.code == 2) {
                    alert_error_back("", data.msg, "/user");
                } else if (data.code == 1) {
                    userInfo = data.data;
                } else if (data.code == 3) {
                    alert_success_login(data.msg);
                } else {
                    alert_error_text(data.msg);
                }
            }
        });
        return userInfo;
    }
};


jQuery.validator.addMethod("checkPhone", function (value, element) {
    var falg = false;
    var p = /^[0-9]*$/;
    // var re = /^0\d{2,3}-?\d{7,8}$/;
    if (p.test(value)) {
        falg = true;
    }

    return falg;
}, "Incorrect mobile number");


jQuery.validator.addMethod("checkPwd", function(value,element) {
    var falg = false;
    var p = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/
    if(p.test(value)){
        falg = true;
    }
    return falg;
},"The password must contain 6-20 characters, with the combination of letters and numbers");