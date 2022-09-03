var datatableSearchInfo = {};
var setDatatableSearchInfo = function (value, name) {
    if (value != null && value != "") {
        datatableSearchInfo[name] = value;
    } else {
        datatableSearchInfo[name] = undefined;
    }
};

var resetSearch = function (dataTable) {
    initDatatableSearchInfo();
    dataTable.ajax.reload();
};

var initDatatableSearchInfo = function () {
    datatableSearchInfo = {};
};

/**
 * clean dataTable config
 */
var cleanDataTableConf = function (dataTablesId_) {
    $(dataTablesId_ + "_filter").parent().parent().html("");
    $(dataTablesId_ + "_length").hide();
};
var cleanDataTablePager = function (dataTablesId_) {
    $(dataTablesId_ + "_filter").parent().parent().html("");
    $(dataTablesId_ + "_length").hide();
    $(dataTablesId_ + "_info").hide();
    $(dataTablesId_ + "_paginate").hide();
};


var initToggleColumn = function (tableId, toggleClassId) {
    var str = '';
    $(tableId + " thead tr th").each(function (index) {
        if (index != 0) {
            str += ' - ';
        }
        str += '<a class="toggle-vis" title="Show or hide columns dynamically" data-column="' + index + '">' + $(this).html() + '</a>';
        str += '<a class="toggle-vis" data-column="' + index + '">' + $(this).html() + '</a>';
    });

    $(toggleClassId).html(str);
};


var changeToggleColumn = function (table , toggleClassId) {
    $(toggleClassId + ' a.toggle-vis').on('click', function (e) {
        e.preventDefault();
        var column = table.column($(this).attr('data-column'));
        if (column.visible()) {
            $(this).addClass("toggle-vis-select");
        } else {
            $(this).removeClass("toggle-vis-select");
        }
        column.visible(!column.visible());
    });
};




var deleteDataTableConf = function (dataTablesId_) {
    $(dataTablesId_ + "_info").parent().parent().hide();
    cleanDataTableConf(dataTablesId_);
};

var cleanDataTableTd = function (dataTablesId_) {
    $(dataTablesId_).find("td").each(function () {
        $(this).removeAttr("tabindex");
    });
};


var initPageInfo_ = function (data) {
    data = JSON.parse(data);
    var pageSize = getPageParam(data, "iDisplayLength");
    if (pageSize == null) {
        pageSize = 10;
    }
    var pageStart = getPageParam(data, "iDisplayStart");
    if (pageStart == null) {
        pageStart = 0;
    }

    var pageIndex = pageStart / pageSize + 1;

    return {"pageIndex": pageIndex, "pageSize": pageSize};
};

var dataTablesParam = function (condition, param) {
    var data = {};
    data.condition = condition;
    data.param = initPageInfo_(param);
    return data;
};

var getPageParam = function (data, paramKey) {
    if (data != null && data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            if (data[i].name == paramKey) {
                return data[i].value;
            }
        }
    }
    return null;
};


var handleDataTableResult = function (data,fnCallback) {
    if (data.code != 1) {
        data.data.data = new Array();
    } else {
        if (data.data.data[0] == null) {
            data.data.data = new Array();
        }
    }

    fnCallback(data.data);
};


var initNumber = function (api) {
    var startIndex = api.context[0]._iDisplayStart;
    api.column(0).nodes().each(function (cell, i) {
        cell.innerHTML = startIndex + i + 1;
    });
};





