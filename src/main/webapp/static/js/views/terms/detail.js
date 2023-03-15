var html;
var presentLength;
const {createEditor, createToolbar} = window.wangEditor
const {i18nChangeLanguage} = window.wangEditor

$(document).ready(function () {

    serviceAuditId = REQ_HANDLE_.getQueryString("getDetail");
    var temp = getDetail(serviceAuditId);
    if (temp == null) return false;
    // Initialize the page information
    initInfo(temp);

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


// Initialize the organization basic information
var initInfo = function (data) {

    $("#serviceAuditId").val(data.serviceAuditId);
    $("#creator").html(initStringData(data.creator));
    $("#createTime").html(initStringData(data.createTime))
    $("#auditor").html(initStringData(data.auditor));
    $("#auditTime").html(initStringData(data.auditTime));
    $("#auditRemarks").html(initStringData(data.remark));
    $("#state").html(initStringData(COMMON_HANDLE.getAuditStateName(data.auditState)));
    editor.setHtml(data.termsContent)
};

let initStringData = function (data) {
    if (data == null || data == "") {
        return "--";
    }
    return data
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
