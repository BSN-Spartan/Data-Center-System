$(document).ready(function () {
    initBaseInfo();

    handleSubmit();

    $("#uploadLogo").click(function () {
        $('#logoInputFile').click();
    });

});

var initBaseInfo = function () {
    $.ajax({
        "type": "get",
        "url": "/ground/portalconfiguration/query/systemconf",
        "dataType": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_login("", data.msg);
                return false;
            } else if (data.code == 1) {
                echoData(data.data);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            }  else {
                alert_error_text(data.msg);
            }
        }
    });

}

var echoData = function (data) {
    for (i in data) {
        if ("logo" == data[i].confCode) {
            if (data[i].confValue != null) {
                $("#logoImg").attr("src", data[i].confValue);
            }
        } else {
            $("#" + data[i].confCode).val(data[i].confValue);
        }
    }

}


var logoUpload = function (obj) {
    var file = obj.files[0];
    let size = file.size;
    if (size > 64 *1024) {
        alert_error_text("Cannot exceed 64k");
        return ;
    }
    verificationPicFile(obj);

    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function (e) {
        console.log(e.target.result)
        $("#logoImg").attr("src", e.target.result);
    };


};


// Validation of type
var verificationPicFile = function (file) {
    var fileTypes = [".jpg", ".png", ".jpeg", ".svg"];
    var filePath = file.value;
    if(filePath){
        var isNext = false;
        var fileEnd = filePath.substring(filePath.indexOf("."));
        for (var i = 0; i < fileTypes.length; i++) {
            if (fileTypes[i] == fileEnd) {
                isNext = true;
                break;
            }
        }
        if (!isNext){
            alert_error_text('Invalid file type');
            file.value = "";
            return false;
        }
    }else {
        return false;
    }
}


var handleSubmit = function () {
    $('#submit_dc_div').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            title: {
                required: true,
                maxlength: 100
            },
            headline: {
                required: true,
                maxlength: 100
            },
            introduce: {
                required: true,
                maxlength: 1000
            },
            copyright: {
                required: true,
                maxlength: 300
            }
        },
        messages: {
            title: {
                required: "Title cannot be empty",
                maxlength: "Cannot exceed 100 characters"
            },
            headline: {
                required: "Portal title can not be empty",
                maxlength: "Cannot exceed 100 characters"
            },
            introduce: {
                required: "Introduce can not be empty",
                maxlength: "Cannot exceed 1000 characters"
            },
            copyright: {
                required: "Copyright cannot be empty",
                maxlength: "Cannot exceed 300 characters"
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
            if ($(element).attr("id") == "code") {
                $("#code_msg_div").show();
                $("#code_msg").html($(error).removeClass("help-block"));
            } else {
                element.parent('div').append(error);
            }

        },

        submitHandler: function (form) {

            submitForm();
        }
    });

    $('#submit_dc_div input').keypress(function (e) {
        if (e.which == 13) {
            if ($('#submit_dc_div').validate().form()) {
                $('#submit_dc_div').submit();
            }
            return false;
        }
    });
};

var submitForm = function () {
    var logoFile = $("#logoInputFile").get(0).files[0];
    var postFormData = new FormData();
    if (logoFile != null && logoFile != undefined) {
        postFormData.append("file", logoFile);
    } else {
        postFormData.append("file", null);
    }

    var baseInfo = new Array();
    var title = $("#title").val();
    var headline = $("#headline").val();
    var introduce = $("#introduce").val();
    var copyright = $("#copyright").val();
    var logo = $("#logoInputFile").val();

    baseInfo.push({"confCode": "title", "confValue": title});
    baseInfo.push({"confCode": "headline", "confValue": headline});
    baseInfo.push({"confCode": "introduce", "confValue": introduce});
    baseInfo.push({"confCode": "copyright", "confValue": copyright});
    baseInfo.push({"confCode": "logo", "confValue": "logo"});
    var blob = new Blob([JSON.stringify(baseInfo)], {type: 'application/json'});
    postFormData.append("dcSystemConfReqVO", blob);

    $.ajax({
        "type": "post",
        "url": "/ground/portalconfiguration/update/dcsystemconf",
        "datatype": "json",
        "data": postFormData,
        "processData": false,
        "contentType": false,
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", data.msg);
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });

};
