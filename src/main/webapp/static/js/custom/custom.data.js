String.prototype.Trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "");
};
String.prototype.LTrim = function () {
    return this.replace(/(^\s*)/g, "");
};
String.prototype.RTrim = function () {
    return this.replace(/(\s*$)/g, "");
};

Number.prototype.toFloor = function (num) {
    return Math.floor(this * Math.pow(10, num)) / Math.pow(10, num);
};


Number.prototype.toCeil = function (num) {
    return Math.ceil(this * Math.pow(10, num)) / Math.pow(10, num);
};



var DATA_FORMAT = {

    formatString: function (string, len, reg) {
        if (string == null || string == '') {
            return '--';
        }
        if (string.length > parseInt(len)) {
            var textt = string.substring(0, parseInt(len));
            if (textt.substring(parseInt(len) - 1, parseInt(len)) == reg) {
                textt = textt.substring(0, parseInt(len) - 1);
            }
            textt = textt + "...";
            return '<div title="' + string + '">' + textt + '</div>';
        } else {
            return string;
        }
    },
    subStringCopy: function (string, len, reg, copy) {
        if (string == null || string == '') {
            return '';
        }
        //string = string.replaceAll("\"","&quot;");
        if (string.length > parseInt(len)) {
            var textt = string.substring(0, parseInt(len));
            if (textt.substring(parseInt(len) - 1, parseInt(len)) == reg) {
                textt = textt.substring(0, parseInt(len) - 1);
            }
            textt = textt + "...";
            return '<div title=\'' + string + '\'>' + textt + copy + '</div>';
        } else {
            return string;
        }
    },
    /**
     * @param string
     */
    formatDefaultString: function (string) {
        return DATA_FORMAT.formatString(string, 10, ",")
    },

    formatNumber: function (num) {
        return Number(num).toFixed(2);
    },

    formatDefaultMoney: function (string) {
        return DATA_FORMAT.formatMoney(string, 2);
    },
    formatYuanMoney: function (string) {
        return DATA_FORMAT.formatMoney(string / 100, 2);
    },

    formatNo: function (str) {
        return (str + "").replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },


    formatMoney: function (s, n) {
        n = n > 0 && n <= 20 ? n : 2;
        if (s == null || s == "") s = 0;
        s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
        var symbol = s.substring(0, 1);
        if (symbol == "-") {
            s = s.substring(1, s.length);
        } else {
            symbol = "";
        }
        var l = s.split(".")[0].split("").reverse(),
            r = s.split(".")[1];
        var t = "";
        for (var i = 0; i < l.length; i++) {
            t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");
        }
        return symbol + t.split("").reverse().join("") + "." + r;
    }
};


function clearNoNum(obj) {

    obj.value = obj.value.replace(/[^\d.]/g, "");

    obj.value = obj.value.replace(/^\./g, "");

    obj.value = obj.value.replace(/\.{2,}/g, ".");

    obj.value = obj.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
    if (obj.value.split(".").length > 1 && obj.value.split(".")[1].length > 2) {
        obj.value = obj.value.substring(0, obj.value.length - 1);
    }
}


function clearNoZs(obj) {

    obj.value = obj.value.replace(/[^\d]/g, "");
}