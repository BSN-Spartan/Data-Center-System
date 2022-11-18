function datePicker(selector) {
    $(selector).datepicker(
        {
            language: "en",
            autoclose: true,
            startView: 0,
            format: "yyyy-mm-dd",
            clearBtn: true,
            todayBtn: false,
            endDate: new Date()
        }).on('changeDate', function (ev) {
        if (ev.date) {

        }
    })
}
function datePickerStart(selector) {
    $(selector).datepicker(
        {
            language: "en",
            autoclose: true,
            startView: 0,
            format: "yyyy-mm-dd",
            clearBtn: true,
            todayBtn: false,
            startDate: new Date((new Date()).getTime() + 24*60*60*1000)
        }).on('changeDate', function (ev) {
        if (ev.date) {

        }
    })
}
function datePickerStartToday(selector) {
    $(selector).datepicker(
        {
            language: "en",
            autoclose: true,
            startView: 0,
            format: "yyyy-mm-dd",
            clearBtn: true,
            todayBtn: false,
            startDate: new Date((new Date()).getTime())
        }).on('changeDate', function (ev) {
        if (ev.date) {

        }
    })
}

function startEndDatePicker(startSelector, endSelector) {
    $(startSelector).datepicker({
        language: "en",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd",
        clearBtn: true,
        todayBtn: false,
        endDate: new Date()
    }).on('changeDate', function (ev) {
        var $startDate = $( startSelector );
        var $endDate = $(endSelector);
        var endDate = $endDate.datepicker( 'getDate' );
        var startDate = $startDate.datepicker( 'getDate' );
        if(endDate < startDate){
            $endDate.datepicker('setDate',null);
        }
        $endDate.datepicker( "option", "minDate",startDate);
    });
    $(endSelector).datepicker({
        language: "en",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd",
        clearBtn: true,
        todayBtn: false,
        endDate: new Date()
    });
    $(startSelector).datepicker().on('changeDate', function (e) {

        var endTimeStart = $(startSelector).val();

        $(endSelector).datepicker('setStartDate', endTimeStart);
    });

    $(endSelector).datepicker().on('changeDate', function (e) {

        var endTimeStart = $(startSelector).val();

        $(endSelector).datepicker('setStartDate', endTimeStart);
    });
}

function startEndDateTimePicker(startSelector, endSelector) {
    $(startSelector).datetimepicker({
        forceParse: 0,
        language: "en",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd hh:ii:00",
        clearBtn: true,
        todayBtn: false,
        pickTime: true,
        endDate: new Date()
    }).on('changeDate', function (ev) {
        let $startDate = $( startSelector );
        let $endDate = $(endSelector);
        let endDate = $endDate.datepicker( 'getDate' );
        let startDate = $startDate.datepicker( 'getDate' );
        if(endDate < startDate){
            $endDate.datetimepicker('setDate',null);
        }
        $endDate.datetimepicker( "option", "minDate",startDate);
    });
    $(endSelector).datetimepicker({
        forceParse: 0,
        language: "en",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd hh:ii:00",
        clearBtn: true,
        todayBtn: false,
        pickTime: true,
        endDate: new Date()
    });
    $(startSelector).datetimepicker().on('changeDate', function (e) {

        let startTimeStart = $(startSelector).val();

        $(endSelector).datetimepicker('setStartDate', startTimeStart);
    });

    $(endSelector).datetimepicker().on('changeDate', function (e) {

        let endTimeStart = $(startSelector).val();

        $(endSelector).datetimepicker('setStartDate', endTimeStart);
    });
}
/**
 * Initialize the time by ID
 * @param clas
 */
function initDatepickerAndClearById(id) {
    $("#" + id).datepicker({
        language: "zh-CN",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd",
        clearBtn: true,
        todayBtn: false
    });
}


// Used for adding management fee, the end date must be greater than the start date and the current date
function startEndNewDatePicker(startSelector, endSelector) {
    $(startSelector).datepicker({
        language: "zh-CN",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd",
        clearBtn: true,
        todayBtn: false
    }).on('changeDate', function (ev) {
        var $startDate = $( startSelector );
        var $endDate = $(endSelector);
        var endDate = $endDate.datepicker( 'getDate' );
        var startDate = $startDate.datepicker( 'getDate' );
        if(endDate < startDate){
            $endDate.datepicker('setDate',null);
        }
        $endDate.datepicker( "option", "minDate",startDate);
    });
    $(endSelector).datepicker({
        language: "zh-CN",
        autoclose: true,
        startView: 0,
        format: "yyyy-mm-dd",
        clearBtn: true,
        todayBtn: false
    });
    $(startSelector).datepicker().on('changeDate', function (e) {
        // Get the selected start time
        var endTimeStart = $(startSelector).val();
        // Set the end time
        if(new Date(endTimeStart)>new Date()){
            $(endSelector).datepicker('setStartDate', endTimeStart);
        }else{
            $(endSelector).datepicker('setStartDate', new Date());
        }
    });
}

