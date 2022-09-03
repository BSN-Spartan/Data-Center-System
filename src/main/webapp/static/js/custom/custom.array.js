Array.prototype.equals = function (array) {
    if (!array) {
        return false;
    }
    if (this.length != array.length) {
        return false;
    }
    for (var i = 0, l = this.length; i < l; i++) {
        if (!array.check_equals_(this[i])) {
            return false;
        }
    }
    return true;
};


Array.prototype.check_equals_ = function (data) {
    if (this.length == 0) {
        return false;
    }
    for (var i = 0, l = this.length; i < l; i++) {
        if (data == this[i]) {
            return true;
        }
    }
    return false;
};

