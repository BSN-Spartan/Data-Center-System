
var regular = {
    url_: /(http|https):\/\/\S*/,
    check_: function (val, regular_) {
        return regular_.test(val);
    }
};


jQuery.validator.addMethod("checkUrl", function (value, element) {
    return regular.check_(value, regular.url_);
}, "");