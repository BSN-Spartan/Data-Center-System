var roleId = null;
$(document).ready(function () {
    roleId = REQ_HANDLE_.getQueryString("check");
    var temp = ROLE_HANDLE.getRoleInfo(roleId);
    if (temp == null) return false;

    initRoleInfo(temp);

    initAllResourceList();

    $(".back_").click(function () {
        REQ_HANDLE_.location_("/role");
    });

});

var initRoleInfo = function (data) {
  var role = data.role;

  $("#roleName").html(role.roleName);
  $("#roleCode").html(role.roleCode);
  $("#createDate").html(role.createDate);
  $("#description").html(role.description);
  $("#state").html(ROLE_HANDLE.getStateName(role.state));

};


var initAllResourceList = function () {
    $.ajax({
        "type": "get",
        "url": "/sys/resource/getByRoleId/" + roleId + "?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        cache: false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_back("", data.msg, "/role");
            } else if (data.code == 1) {
                if (data.data != null && data.data.length > 0) {
                    $('#resourceList').treeview({
                        data: eval(data.data),
                        showIcon: true,
                        color: '#505458',
                        selectedColor: '#00aeff',
                        selectedBackColor:"#F5F5F5",
                        showCheckbox:false
                    });
                }
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error_text(data.msg);
            }
        }
    });
};