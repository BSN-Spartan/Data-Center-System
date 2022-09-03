var REQ_HANDLE_ = {
    location_: function (url) {
        var connector = "&";
        if (url.indexOf("?") == -1) {
            connector = "?"
        }
        url = url + connector + "ranparam=" + (new Date()).valueOf();
        window.location.href = url;
    },
    goback_: function () {
        window.history.go(-1);
    },
    back_: function () {
        window.history.back();
    },
    reload_: function () {
        window.location.reload();
    },
    exportExcel: function (url, datas) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", url);
        if (datas != null && datas.length > 0) {
            for (var i = 0; i < datas.length; i++) {
                var data = datas[i];
                var input = $("<input>");
                input.attr("type", "hidden");
                input.attr("name", data.name);
                input.attr("value", data.value);
                form.append(input);
            }
        }
        $("body").append(form);
        form.submit();
    },
    exportFile: function (url, datas) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", url);
        if (datas != null) {
            for (var name in datas) {
                var input = $("<input>");
                input.attr("type", "hidden");
                input.attr("name", name);
                input.attr("value", datas[name]);
                form.append(input);
            }
        }
        $("body").append(form);
        form.submit();
    },
    getQueryString: function (paramName) {
        var reg = new RegExp("(^|&)" + paramName + "=([^&]*)(&|$)", "i");
        var reg_rewrite = new RegExp("(^|/)" + paramName + "/([^/]*)(/|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        var q = window.location.pathname.substr(1).match(reg_rewrite);
        if (r != null) {
            return unescape(r[2]);
        } else if (q != null) {
            return unescape(q[2]);
        } else {
            return null;
        }
    }
};
