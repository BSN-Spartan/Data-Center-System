var EXCEL_HANDLE_ = {
    exportExcel: function (url, searchId, progressId) {
        EXCEL_HANDLE_.exportExcelArray(url, $('#' + searchId).serializeArray(), progressId);
    },
    exportExcelArray: function (url, arr, progressId) {
        var form = $("<form>");
        form.attr('style', 'display:none');
        form.attr('target', '');
        form.attr('method', 'post');
        form.attr('action', url);
        $.each(arr, function (i, v) {
            if (v.value) {
                var input = $('<input>');
                input.attr('type', 'hidden');
                input.attr('name', v.name);
                input.attr('value', $.trim(v.value));
                form.append(input);
            }
        });
        $('body').append(form);
        form.submit();
        form.remove();
        loading();
        taskInfo = window.setInterval(function () {
            EXCEL_HANDLE_.getprogress(progressId);
        }, task_time);
    },
    getprogress: function (progressId) {

        $.ajax({
            "type": "get",
            "url": "/sys/getUtilProgress/" + progressId + "?ranparam=" + (new Date()).valueOf(),
            "datatype": "json",
            "success": function (data) {
                if (data.code == 1) {
                    var progress = data.data;
                    /*var progressHtml = '<div class="progress">' +
                        '    <div class="progress-bar active progress-bar-info progress-bar-striped" style="width:' + progress.percentage + '%">' + progress.progressMsg + '</div>' +
                        '  </div>';
                    $(".zdialog_msg_").html(progressHtml);*/
                    var percentage = progress.percentage;
                    var totalPercentage = progress.totalPercentage;

                    if (percentage == totalPercentage) {
                        stopTask();

                        alerts();
                    }
                }
            }
        });

    }
}


var task_time = 1000;

var taskInfo = null;

function stopTask() {
    clearInterval(taskInfo);
}

function alerts() {
    $.DialogByZ.Close();
}

function loading() {
    $.DialogByZ.Loading('/static/images/loading-0.png');
}

