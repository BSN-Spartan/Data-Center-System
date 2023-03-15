var html;
var presentLength;
const {createEditor, createToolbar} = window.wangEditor
const {i18nChangeLanguage} = window.wangEditor

$(document).ready(function () {

    serviceAuditId = REQ_HANDLE_.getQueryString("toAudit");
    var temp = getDetail(serviceAuditId);
    if (temp == null) return false;
    // Initialize the page information
    initInfo(temp);

    handleSubmitAuditResult();

});

var getDetail = function (serviceAuditId) {

    var termsInfo = null;

    $.ajax({
        "type": "get",
        "url": "/terms/detail/" + serviceAuditId + "?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 2) {
                alert_error_login("", data.msg);
                return false;
            } else if (data.code == 1) {
                termsInfo = data.data;
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error_text(data.msg);
            }
        }
    });

    return termsInfo;
}


var initInfo = function (data) {


    $("#serviceAuditId").val(data.serviceAuditId);
    $("#creator").html(initStringData(data.creator));
    $("#createTime").html(initStringData(data.createTime))

    editor.setHtml(data.termsContent)

    $("#checkResult1").attr("checked","checked");
    $("#checkResult2").removeAttr("checked");

};

let initStringData = function (data) {
    if (data == null || data == "") {
        return "--";
    }
    return data
}


var handleSubmitAuditResult = function () {
    $('#save_form').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            checkRemark: {
                required: true,
                maxlength: 200
            }
        },
        messages: {
            checkRemark: {
                required: "Please enter the comment",
                maxlength: "The length of comment cannot exceed 200 characters"
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
            submitFormAuditResult();
        }
    });

    $('#save_form input').keypress(function (e) {
        if (e.which == 13) {
            if ($('#save_form').validate().form()) {
                $('#save_form').submit();
            }
            return false;
        }
    });
};

var submitFormAuditResult = function () {
    var checkRemark = $("#checkRemark").val();
    var checkResult=$("input[name='checkResult']:checked").val();
    // Request Backend System
    var fd = {};
    fd.serviceAuditId = serviceAuditId;
    fd.remark = checkRemark;
    fd.decision = checkResult;
    $.ajax({
        dataType: 'json',
        contentType: "application/json",
        type: 'post',
        data:  JSON.stringify(fd),
        async: false,
        url: "/terms/audit",
        processData: false,
        success: function (data) {
            if (data.code == 1) {
                alert_success("","Submitted successfully",gotoIndex)
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("","Not Approved："+data.msg)
            }
        }
    });
};

function gotoIndex() {
    REQ_HANDLE_.location_('/paymentTerm');
}



/*Translate into English*/
i18nChangeLanguage('en')
const editorConfig = {
    placeholder: 'Type here...',
    onChange(editor) {
        html = editor.getHtml()
        presentLength = editor.getText().replace(/\s|\t|\r\n/g, "").length
    },
    MENU_CONF: {}
}

editorConfig.maxLength = 60000

/*Menu Font Style Configuration*/
editorConfig.MENU_CONF['fontFamily'] = {
    fontFamilyList: [
        'Arial',
        'Tahoma',
        'Verdana',
        'Times New Roman',
        'Courier New'
    ]
}
const editor = createEditor({
    selector: '#editor-container',
    html: '<p><br></p>',
    config: editorConfig,
    mode: 'default', // or 'simple'
})

const toolbarConfig = {}

/*Toolbar's configuration*/
const toolbar = createToolbar({
    editor,
    selector: '#toolbar-container',
    config: {
        excludeKeys: ["fullScreen", "group-image", "group-video", "emotion"]
    },
    mode: 'default' // or 'simple'
})

