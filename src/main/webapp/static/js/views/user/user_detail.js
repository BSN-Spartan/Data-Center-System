var userId = null;
$(document).ready(function () {

    userId = REQ_HANDLE_.getQueryString("check");

    var temp = USER_HANDLE.getUserInfo(userId);
    if (temp == null) return false;

    initUserInfo(temp);



    $(".back_").click(function () {
        REQ_HANDLE_.location_("/user");
    });

});

var initUserInfo = function (data) {
  var user = data.user;

  $("#userName").html(user.contactsName);
  $("#userEmail").html(user.contactsEmail);
  $("#phone").html(user.contactsPhone);
  $("#createDate").html(user.create_time);
  $("#userState").html(USER_HANDLE.getStateName(user.state));

  var roleList = data.roleList;
  var roleNameList = '';
  if(roleList == null || roleList.length == 0) {
      roleNameList = 'No roles are assigned to the current user';
  } else {
      for (var i = 0; i < roleList.length; i++) {
          roleNameList += roleList[i].roleName;
          if (i != roleList.length - 1) {
              roleNameList += '&nbsp;&nbsp;,&nbsp;&nbsp;';
          }
      }
  }

  $("#roleList").html(roleNameList);
};


