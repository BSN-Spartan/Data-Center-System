var icons = [];
icons.push({code: 'dashboard_', icon: '<i class="menu-icon fa fa-home"></i>'});
icons.push({code: 'ntt_module_', icon: '<i class="menu-icon fa fa-money"></i>'});
icons.push({code: 'node_module_', icon: '<i class="menu-icon fa fa-building"></i>'});
icons.push({code: 'chain_access_module_', icon: '<i class="menu-icon fa fa-share-alt"></i>'});
icons.push({code: 'charge_module_', icon: '<i class="menu-icon fa fa-chain-broken"></i>'});
icons.push({code: 'reward_module_', icon: '<i class="menu-icon fa fa-file-text"></i>'});
icons.push({code: 'user_module_', icon: '<i class="menu-icon fa fa-user"></i>'});
icons.push({code: 'support_menu_', icon: '<i class="menu-icon fa fa-support"></i>'});
icons.push({code: 'financial_module_', icon: '<i class="menu-icon fa fa-credit-card-alt"></i>'});
icons.push({code: 'index_', icon: '<i class="menu-icon fa fa-cogs"></i>'});
icons.push({code: 'portal_config_', icon: '<i class="menu-icon fa fa-pencil-square-o"></i>'});

var resourceList = [
    {
        "rsucId": 6,
        "parentId": 0,
        "rsucCode": "dashboard_",
        "rsucName": "Home",
        "rsucUrl": "/index",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 5,
        "parentId": 0,
        "rsucCode": "node_module_",
        "rsucName": "Node Management",
        "rsucUrl": "javascript:;",
        "priority": 2,
        "rsucType": 1
    },
    {
        "rsucId": 10,
        "parentId": 0,
        "rsucCode": "ntt_module_",
        "rsucName": "NTT History",
        "rsucUrl": "/ntt",
        "priority": 3,
        "rsucType": 1
    },

    {
        "rsucId": 13,
        "parentId": 0,
        "rsucCode": "chain_access_module_",
        "rsucName": "Chain Access Management",
        "rsucUrl": "javascript:;",
        "priority": 4,
        "rsucType": 1
    },
    {
        "rsucId": 70,
        "parentId": 13,
        "rsucCode": "chain_access_config_",
        "rsucName": "Access Configuration",
        "rsucUrl": "/chainAccess",
        "priority": 2,
        "rsucType": 1
    },
    {
        "rsucId": 75,
        "parentId": 13,
        "rsucCode": "user_access_info_",
        "rsucName": "Access Key Management",
        "rsucUrl": "/userAccess",
        "priority": 2,
        "rsucType": 1
    },

    {
        "rsucId": 15,
        "parentId": 0,
        "rsucCode": "charge_module_",
        "rsucName": "Gas Credit Management",
        "rsucUrl": "javascript:;",
        "priority": 4,
        "rsucType": 1
    },
    {
        "rsucId": 40,
        "parentId": 15,
        "rsucCode": "charge_menu_",
        "rsucName": "Gas Credit Management",
        "rsucUrl": "/recharge",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 41,
        "parentId": 15,
        "rsucCode": "price_menu_",
        "rsucName": "Price Settings",
        "rsucUrl": "/price",
        "priority": 2,
        "rsucType": 1
    },
    {
        "rsucId": 30,
        "parentId": 5,
        "rsucCode": "chain_menu_",
        "rsucName": "Chain Info",
        "rsucUrl": "/chain",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 31,
        "parentId": 5,
        "rsucCode": "node_menu_",
        "rsucName": "Node Info",
        "rsucUrl": "/node",
        "priority": 2,
        "rsucType": 1
    },
    {
        "rsucId": 1,
        "parentId": 0,
        "rsucCode": "index_",
        "rsucName": "Configuration",
        "rsucUrl": "/config",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 64,
        "parentId": 0,
        "rsucCode": "portal_config_",
        "rsucName": "Portal Management",
        "rsucUrl": "javascript:;",
        "priority": 6,
        "rsucType": 1
    },
    {
        "rsucId": 65,
        "parentId": 64,
        "rsucCode": "base_info_config_",
        "rsucName": "Basic Information",
        "rsucUrl": "/baseInfo",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 67,
        "parentId": 64,
        "rsucCode": "technical_support_config_",
        "rsucName": "Technical Support ",
        "rsucUrl": "/technicalSupport",
        "priority": 3,
        "rsucType": 1
    },
    {
        "rsucId": 67,
        "parentId": 64,
        "rsucCode": "payment_type_config_",
        "rsucName": "Payment Method",
        "rsucUrl": "/paymentType",
        "priority": 3,
        "rsucType": 1
    },
    {
        "rsucId": 61,
        "parentId": 0,
        "rsucCode": "financial_module_",
        "rsucName": "Financial Management",
        "rsucUrl": "javascript:;",
        "priority": 5,
        "rsucType": 1
    },
    {
        "rsucId": 62,
        "parentId": 61,
        "rsucCode": "order_menu_",
        "rsucName": "Order Management",
        "rsucUrl": "/financial",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 63,
        "parentId": 61,
        "rsucCode": "refund_menu_",
        "rsucName": "Refund Management",
        "rsucUrl": "/refund",
        "priority": 1,
        "rsucType": 1
    },
    {
        "rsucId": 25,
        "parentId": 0,
        "rsucCode": "user_module_",
        "rsucName": "User Management",
        "rsucUrl": "/user",
        "priority": 8,
        "rsucType": 1
    },
    {
        "rsucId": 60,
        "parentId": 0,
        "rsucCode": "support_menu_",
        "rsucName": "BSN Technical Support",
        "rsucUrl": "/support",
        "priority": 9,
        "rsucType": 1
    }
];

var storage = window.sessionStorage;
var defaultPageStart = 0;
var pathName = window.location.pathname;
$(document).ready(function () {
    var menuId = $("meta[name=menuId]")[0].content;
    if ($('table').length == 0) {
        return;
    }
    if (storage.getItem(menuId)) {
        if (storage.getItem("pathName") == pathName) {
            var pageInfo = eval('(' + storage.getItem(menuId) + ')');
            $.each(pageInfo.condition, function (i, v) {
                setDatatableSearchInfo($.trim(v.value), v.id);
            })
            if (pageInfo.pageParam) {
                defaultPageStart = pageInfo.pageParam.pageStart;
            }
            setTimeout(function () {
                $.each(pageInfo.condition, function (i, v) {
                    $("#" + v.id).val(v.value);
                })
            }, 100); // How long do you want the delay to be (in milliseconds)?
        }
    } else {
        storage.clear();
        storage.setItem("pathName", pathName);
    }
    $.extend($.fn.DataTable.defaults, {
        "iDisplayStart": defaultPageStart,
        "fnInfoCallback": function (oSettings, json) {
            if ($(".back_").length > 0) {
                return;
            }
            var pageInfo = {};
            if (storage.getItem("pathName") == pathName) {
                if (storage.getItem(menuId)) {
                    pageInfo = eval('(' + storage.getItem(menuId) + ')');
                }
                pageInfo.pageParam = {};
                pageInfo.pageParam.pageStart = oSettings._iDisplayStart;
                pageInfo.pageParam.pageEnd = oSettings._iDisplayLength;
                storage.setItem(menuId, JSON.stringify(pageInfo));
            }
        },
    })

});

$(".search_").click(function () {
    let menuId = $("meta[name=menuId]")[0].content;
    let param = {};
    if (storage.getItem(menuId)) {
        param = eval('(' + storage.getItem(menuId) + ')');
        delete param.condition;
    }
    let form = $("form").children().find("input,select");
    param.condition = new Array();
    for (var i = 0; i < form.length; i++) {
        if (form[i].value) {
            var item = {};
            item.type = form[i].nodeName;
            item.id = form[i].getAttribute("id");
            item.value = form[i].value;
            param.condition.push(item);
        }
    }
    storage.setItem(menuId, JSON.stringify(param));
})
$(".reset_").click(function () {
    storage.removeItem($("meta[name=menuId]")[0].content);
})

var getMenuIcon = function (code) {
    for (var i = 0; i < icons.length; i++) {
        if (icons[i].code == code) {
            return icons[i].icon;
        }
    }
    return "";
};

function getSonResourceList(data, rsucId) {
    var result = [];
    for (var i = 0; i < data.length; i++) {
        if (data[i].parentId == rsucId) {
            result.push(data[i]);
        }
    }
    return result;
}

var initMenu = function () {
    var moduleIds = $("meta[name=moduleId]");
    if (moduleIds != null && moduleIds.length > 0) {
        $(".js__accordion li").each(function (index) {
            $(this).removeClass();
        });
        var menuIds = $("meta[name=menuId]");
        var moduleId = moduleIds[0].content;
        if (moduleId != null && moduleId != "") {
            $("#" + moduleId).addClass("current active");
        }

        if (menuIds != null && menuIds.length > 0) {
            var menuId = menuIds[0].content;
            if (menuId != null && menuId != "") {
                $("#" + menuId).addClass("current");
            }
        }
        $(".content mCustomScrollbar _mCS_1 mCS_no_scrollbar").css("margin-top", "-110px");
    }
};


var butList = [];


var getButParentId = function () {
    if (resourceList == null || resourceList.length == 0) return null;
    if ($("meta[name=menuId]")[0] == null) return null;
    var menuId = $("meta[name=menuId]")[0].content;
    for (var i = 0; i < resourceList.length; i++) {
        if (menuId == resourceList[i].rsucCode) {
            return resourceList[i].rsucId
        }
    }
    return null;
};

var getButList = function () {
    if (butList.length == 0) {
        var butParentId = getButParentId();
        if (butParentId == null) return null;

        for (var i = 0; i < resourceList.length; i++) {
            if (butParentId == resourceList[i].parentId) {
                butList.push(resourceList[i]);
            }
        }
    }
    //return butList;
};

var checkButState = function (butCode) {
    if (butList != null && butList.length > 0) {
        for (var i = 0; i < butList.length; i++) {
            if (butList[i].rsucCode == butCode) return true;
        }
    }
    return false;
};

var getButState = function (butCode) {
    if (butList != null && butList.length > 0) {
        for (var i = 0; i < butList.length; i++) {

            if (butList[i].rsucCode == butCode)
                return butList[i];
        }
    }
    return null;
};


var initMainMenu_ = function () {
    var header = '<a href="/index" class="logo">' +
        '<img id="logo_" style="display: none;text-align:left;margin-left: -50px" src="logo_" width="180px" height="80px"></a>' +
        '<div class="fixed-navbar">' +
        '<div class="pull-right">' +
        '<a href="https://bsn.foundation" class="ico-item" target=\'_blank\' style="font-size: 16px"> Foundation Website </a>' +
        '<a href="https://github.com/BSN-Spartan/Data-Center-System" class="ico-item" target=\'_blank\' style="font-size: 16px"><img src="/static/images/system/icon_github.png" style="width: 25px;height: 25px;">&nbsp;GitHub</a>' +
        '<a href="https://bsn.foundation/static/quick-start/2gettingStarted/2-1-1.html" class="ico-item" target=\'_blank\' style="font-size: 16px"><img src="/static/images/system/icon_start.png" style="width: 25px;height: 25px">&nbsp;Quick Start</a>' +
        '<a href="javascript:;" class="ico-item" onclick="updatedInstructions()"  style="font-size: 16px"><img src="/static/images/system/icon_version.png" style="width: 25px;height: 25px">&nbsp;Version</a>' +
        '<a href="javascript:;" class="ico-item fa fa-lock" onclick="modifyPass()" title="Change Password"></a>' +
        '<a href="javascript:;" class="ico-item fa fa-power-off logout_" onclick="logOut()" title="Logout"></a>' +
        '</div>' +
        '</div>' +
        '<div class="user">' +
        '<a href="javascript:;" style="max-width: 260px;position: absolute;top: 40px;left: 20px;color: #435966">' +
        '<img src="/static/images/system/user.png" style="margin-right: 10px">' +
        '<span class="user_name"></span>，Welcome' +
        '</a>' +
        '</div>';
    $(".header").html(header);
    $.ajax({
        "type": "get",
        "url": "/sys/user/getUserResource",
        "datatype": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 1) {
                $(".user_name").html(data.data.userName);
                /*var systemName = data.data.systemName;
                if (systemName.length > 12) {
                    $(".system_name").css("font-size", "14px");
                }
                $(".system_name").html(systemName);*/
                if (data.data.systemLogo != null && data.data.systemLogo.length > 0) {
                    $("#logo_").attr("src", data.data.systemLogo).show();
                }

                $("title").html(data.data.systemName);
                $("link[rel=icon]").prop("href", data.data.systemIcon);


                if (resourceList != null && resourceList.length > 0) {


                    getButList();


                    var menuList = '';
                    for (var i = 0; i < resourceList.length; i++) {
                        var temp = resourceList[i];
                        if (temp.parentId == 0) {

                            var sonResu = getSonResourceList(resourceList, temp.rsucId);
                            if (sonResu.length == 0) {
                                menuList += '<li id="' + temp.rsucCode + '">' +
                                    '<a class="waves-effect" href="' + temp.rsucUrl + '">' +
                                    getMenuIcon(temp.rsucCode) +
                                    '<span>' + temp.rsucName + '</span></a></li>';
                            } else {
                                menuList += '<li id="' + temp.rsucCode + '">' +
                                    '<a class="waves-effect parent-item js__control" href="' + temp.rsucUrl + '">' +
                                    getMenuIcon(temp.rsucCode) +
                                    '<span>' + temp.rsucName + '</span>' +
                                    '<span class="menu-arrow fa fa-angle-down"></span>' +
                                    '</a>' +
                                    '<ul class="sub-menu js__content">';

                                for (var j = 0; j < sonResu.length; j++) {
                                    var menu = sonResu[j];
                                    menuList += '<li id="' + menu.rsucCode + '"><a href="' + menu.rsucUrl + '">' + menu.rsucName + '</a></li>';
                                }

                                menuList += '</ul></li>';
                            }
                        }
                    }
                    $('.menu_').html(menuList);
                }
            } else {
                alert_error_login("", data.msg);
            }
        }
    });

};

var initHeader = function () {
    initMenu();
};


var logOut = function () {
    alert_confirm("", "Confirm logout?", function () {
        storage.clear();
        logOut_();
    }, null);
};

function logOut_() {
    $.ajax({
        "type": "get",
        "url": "/sys/user/logout",
        "success": function (data) {
            if (data.code == 1) {
                REQ_HANDLE_.location_("/");
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

var modifyPass = function () {
    REQ_HANDLE_.location_("/modifyPass");
}


var updatedInstructions = function () {
    REQ_HANDLE_.location_("/version");
};

