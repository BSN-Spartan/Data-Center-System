$(document).ready(function () {
    handleSubmit();
});
const key = CryptoJS.enc.Utf8.parse('reddatespartan25');
const iv = CryptoJS.enc.Utf8.parse('hongzao25spartan');
var handleSubmit = function () {
    $('#submit_modify_pass').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            oldPassword: {
                required: true
            },
            newPassword: {
                required: true,
                checkPassword: true
            },
            againPassword: {
                required: true,
                equalTo: "#newPassword"
            }
        },
        messages: {
            oldPassword: {
                required: "Original password cannot be empty",
            },
            newPassword: {
                required: "New password cannot be empty"
            },
            againPassword: {
                required: "Confirm password cannot be empty",
                equalTo: "Confirm that the newPassword are the same"
            }
        },

        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },

        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },

        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },

        submitHandler: function (form) {
            submitForm();
        }
    });

};

var submitForm = function () {
    var oldPassword = $("#oldPassword").val();
    var newPassword = $("#newPassword").val();
    var againPassword = $("#againPassword").val();

    var data = {};
    data.oldPassword = getEncryptionData(oldPassword);
    data.newPassword = getEncryptionData(newPassword);
    data.againPassword = getEncryptionData(againPassword);


    $.ajax({
        "type": "post",
        "url": "/sys/user/modifyPass",
        "datatype": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Changed successfully", function () {
                    REQ_HANDLE_.location_("/");
                });
            } else {
                alert_error("", data.msg);
            }
        }
    });

};


function getEncryptionData(data) {
    if (data == "" || data == null) return '';
    var srcs = CryptoJS.enc.Utf8.parse(data + '');
    var encrypted = CryptoJS.AES.encrypt(srcs, key, {
        iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7,
    });
    var encryptedStr = encrypted.ciphertext.toString().toUpperCase();
    return encryptedStr;
}