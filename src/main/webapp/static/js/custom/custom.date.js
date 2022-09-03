
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
};

var DATE_FORMAT_ = {
    zh_time_hms: "yyyy-MM-dd hh:mm:ss",
    zh_time_hm: "yyyy-MM-dd hh:mm",
    zh_date: "yyyy-MM-dd",
    _time_hms: "yyyy-MM-dd hh:mm:ss",
    _time_hm: "yyyy-MM-dd hh:mm",
    _date: "yyyy-MM-dd"
};

function getAfterDateByDay(date, AddDayCount) {
    var dd = dateStrToDate(date);
    dd.setDate(dd.getDate() + AddDayCount);
    return formatDefaultDate(dd);
}

function getAfterDateByYear(date, AddYearCount) {
    var dd = dateStrToDate(date);
    dd.setFullYear(dd.getFullYear() + AddYearCount);
    return formatDefaultDate(dd);
}



function getAfterDateByMonth(date, AddMonthCount) {
    var dd = dateStrToDate(date);
    dd.setMonth(dd.getMonth() + AddMonthCount);
    return formatDefaultDate(dd);
}

function dateStrToDate(dateStr) {
    return new Date(date.replace(/-/g, "/"));
}

function formatDefaultDate(date) {
    return date.format(DATE_FORMAT_._date).toLocaleString();
}





function dateDiffIncludeToday(startDate, endDate) {
    return parseInt((endDate - startDate ) / 1000 / 60 / 60 / 24);
};