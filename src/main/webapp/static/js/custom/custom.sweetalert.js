
function alert_base(title, text) {
    swal({
        title: title,
        text: text,
        confirmButtonColor: "#00aeff"
    });
}

function alert_success(title, text, callback) {
    swal({
        title: title,
        text: text,
        type: "success",
        confirmButtonColor: "#00aeff"
    }, function (t) {
        if (callback != null && !(callback === undefined)) {
            callback(t)
        }
    })
}

function alert_success_login(title, text) {
    swal({
        title: title,
        text: text,
        type: "error",
        confirmButtonText: "Login",
        confirmButtonColor: "#00aeff",
        cancelButtonText: "Cancel", //cancel
        showCancelButton: 0,
    }, function (t) {
        if (t) {
            jumpLoginPage();
        }
    })
}

function alert_error_login(title, text) {
    swal({
        title: title,
        text: text,
        type: "error",
        confirmButtonText: "Login",
        confirmButtonColor: "#00aeff",
        showCancelButton: false,
    }, function (t) {
        if (t) {
            jumpLoginPage();
        }
    })
}


function jumpLoginPage() {
    REQ_HANDLE_.location_("/");
}

function alert_error_back(title, text, url) {
    swal({
        title: title,
        text: text,
        type: "error",
        confirmButtonColor: "#f60e0e"
    }, function (t) {
        if (t) {
            REQ_HANDLE_.location_(url);
        }
    })
}

function alert_error_back(title, text) {
    swal({
        title: title,
        text: text,
        type: "error",
        confirmButtonColor: "#f60e0e"
    }, function (t) {
        if (t) {
            history.go(-1);
        }
    })
}

function alert_error_text(text) {
    swal({
        title: "",
        text: text,
        type: "error",
        confirmButtonColor: "#00aeff",
        confirmButtonText: "ok"
    })
}

function alert_error(title, text) {
    swal({
        title: title,
        text: text,
        type: "error",
        confirmButtonColor: "#00aeff",
        confirmButtonText: "ok"
    })
}

function alert_confirm(title, text, suFn, closeFn) {
    var closeOnCancelType = !0;
    if (closeFn != null && closeFn != "") {
        closeOnCancelType = !1;
    }
    swal({
        title: title,
        text: text,
        type: "warning",
        showCancelButton: !0,
        confirmButtonColor: "#00aeff",
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel", //cancel
        closeOnConfirm: !1,
        closeOnCancel: closeOnCancelType
    }, function (t) {
        if (t) {
            executeFn(suFn);
        } else {
            executeFn(closeFn);
        }
    });
}


function executeFn(functionName) {
    if (functionName != null && functionName != "") {
        if (typeof functionName == "function") {
            functionName();
        } else {
            alert_base("", functionName);
        }
    }
}


function alert_success_auto_close(title, text, time) {
    swal({
        title: title,
        text: text,
        type: "success",
        showCancelButton: false,
        showConfirmButton: false,
        buttons: false,
        timer: time,
    })
}