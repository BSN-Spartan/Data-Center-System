var html;
var presentLength;
const {createEditor, createToolbar} = window.wangEditor
const {i18nChangeLanguage} = window.wangEditor

$(document).ready(function () {
    queryDataCenter();
});

function queryDataCenter() {
    $.ajax({
        "type": "get",
        "url": "/terms/query/queryNewestTreaty",
        "dataType": "json",
        "contentType": "application/json",
        "success": function (data) {
            if (data.code == 1) {
                if (data.data != null && data.data != '') {
                    editor.setHtml(data.data)
                }
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
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

function updateTreaty() {
    var sysDataCenter = {};
    sysDataCenter.termsContent = html;
    $("#treatyButton").prop('disabled', true)
    if (presentLength <= 0) {
        alert_error("", "Terms of service cannot be empty")
        $("#treatyButton").prop('disabled', false)
    } else {
        $.ajax({
            "type": "post",
            "url": "/terms/add",
            "dataType": "json",
            "contentType": "application/json",
            "data": JSON.stringify(sysDataCenter),
            "success": function (data) {
                if (data.code == 1) {
                    alert_success("","Submitted successfully",gotoIndex)
                } else if (data.code == 3) {
                    alert_success_login(data.msg);
                } else {
                    alert_error("", data.msg);
                    $("#treatyButton").prop('disabled', false)
                }
            }
        });
    }

}

function gotoIndex() {
    REQ_HANDLE_.location_('/paymentTerm');
}


function getTermsTemp () {
    $.ajax({
        "type": "get",
        "url": "/terms/query/treatyTemp",
        "dataType": "json",
        "contentType": "application/json",
        "success": function (data) {
            if (data.code == 1) {
                if (data.data != null && data.data != '') {
                    editor.setHtml(data.data)
                }
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
        }
    });
}

function resetTerms () {
    editor.setHtml('');
}
