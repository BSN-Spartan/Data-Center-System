var dataTable;
var list = "#chain_price_list";

$(document).ready(function () {
    $("#state").html(COMMON_HANDLE.getSalePriceStateSelect());

    // init frame info
    CHAIN_HANDLE.initChainInfo("chainId", null);
    // init
    initFrameList();
    startEndDatePicker("#startTime", "#endTime");

    $(".search_").click(function () {
        search();
    });
    $(".reset_").click(function () {
        resetSearch(dataTable);
    });
    // reset chainId
    var chainId = 1;
    if (checkButState("add_price_but_")) {
        $(".add_price_but_").show().click(function () {

            CHAIN_HANDLE.initChainInfoNotAll("rechargeChainType", chainId);
            // get the chain price
            var nttCount = getChainPrice(chainId);
            var chainPrice;
            if (nttCount == 0) {
                chainPrice="";
            }else{
                chainPrice = "Default: 1 USD &#8776; "+ Math.round(1/nttCount)+ " "+ CHAIN_HANDLE.getChainRechargeUnit(chainId);
            }
            $("#chainPriceDiv").show();
            $("#chainPrice").html(chainPrice);
            $("#rechargeUnit").html(CHAIN_HANDLE.getChainRechargeUnit(chainId));

            datePickerStartToday("#priceStartDate");
            // startEndNewDatePicker("#priceStartDate", "#priceEndDate");
            $("#chainSalePrice").val("");
            $("#priceStartDate").val("");
            $("#recharge_modal").modal("show");
        });
    }

    $("#rechargeChainType").change(function () {
        chainId = $(this).val();
        // get the chain price
        var nttCount = getChainPrice(chainId);
        var chainPrice;
        if (nttCount == 0) {
            $("#chainPriceDiv").hide();
            chainPrice="";
        }else{
            chainPrice = "Default: 1 USD &#8776; "+ Math.round(1/nttCount)+ " "+ CHAIN_HANDLE.getChainRechargeUnit(chainId);
        }
        $("#chainPrice").html(chainPrice);
        $("#rechargeUnit").html(CHAIN_HANDLE.getChainRechargeUnit(chainId));

    });


    handleSubmit();
});


var getChainPrice = function (chainId) {

    var nttPrice = null;

    $.ajax({
        "type": "post",
        "url": "/recharge/getChainPriceRatio/" + chainId + "?ranparam=" + (new Date()).valueOf(),
        "dataType": "json",
        "async": false,
        "success": function (data) {
            if (data.code == 1) {
                nttPrice = data.data;
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                nttPrice = 0;
            }
        }
    });

    return nttPrice;
}

let initFrameList = function () {
    dataTable = $(list).DataTable({
        "responsive": true,
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "stateSave": false,
        "scrollCollapse": true,
        "bAutoWidth": false,
        "sAjaxSource": "/price/list",
        "fnServerData": function (sSource, aoData, fnCallback) {
            let temp = dataTablesParam(datatableSearchInfo, JSON.stringify(aoData));
            $.ajax({
                "type": "post",
                "url": sSource,
                "dataType": "json",
                "contentType": "application/json",
                "data": JSON.stringify(temp),
                "success": function (data) {
                    if (data.code == 3) {
                        alert_success_login(data.msg);
                    } else {
                        handleDataTableResult(data, fnCallback);
                    }
                }
            });
        },
        "deferRender": true,
        "bSort": false,
        "fnDrawCallback": function () {
            initNumber(this.api());
        },
        "columns": [
            {data: null, "targets": 0, title: "No.", bSearchable: false, bSortable: false},
            {data: "chainName", title: "Chain Name", bSearchable: false, bSortable: false},
            {data: "gasPrice", render: initGasPrice, title: "Price Information", bSearchable: false, bSortable: false},
            {data: "contactsName", title: "Creator", bSearchable: false, bSortable: false},
            {data: "createTime", title: "Created Time", bSearchable: false, bSortable: false},
            {data: "startDate", title: "Effective Date", bSearchable: false, bSortable: false},
            {
                data: "state",
                render: initSalePriceStateType,
                title: "Status",
                bSearchable: false,
                bSortable: false
            },
            {render: initTableBut, title: "Action", bSortable: false}
        ],
        "columnDefs": [{
            "targets": "_all",
            "defaultContent": "",
            "render": function (data, type, row) {
                if (data == null || data == "") {
                    return "--";
                }
                return data
            }
        }]
    });
    cleanDataTableConf(list);
};
let initGasPrice = function (data, type, row) {
    var gasPrice = row.salePrice + " USD : "+ Math.round(row.gas*100);
    return gasPrice;
};


let initSalePriceStateType = function (data, type, row) {
    return COMMON_HANDLE.getSalePriceStateName(data);
};


// init options
let initTableBut = function (data, type, row) {
    let salePriceId = row.salePriceId;
    let state = row.state;
    let butStr = '';
    if (state === 1) {
        if (checkButState("audit_but_")) {
            butStr = butStr + '<button type="button" onclick="audit(' + salePriceId + ')" class="btn-info btn-xs" >Review</button>';
        }
    }
    if (checkButState("detail_but_")) {
        butStr = butStr + '<button type="button" onclick="detail(' + salePriceId + ')" class="btn-info btn-xs" >Details</button>';
    }
    return butStr == "" ? "--" : butStr;
};

function detail(salePriceId) {
    REQ_HANDLE_.location_('/price/getDetail/' + salePriceId);
}

function audit(salePriceId) {
    REQ_HANDLE_.location_('/price/toAudit/' + salePriceId);
}


// search
let search = function () {
    setDatatableSearchInfo($.trim($("#chainId").val()), "chainId");
    setDatatableSearchInfo($("#state").val(), "state");
    setDatatableSearchInfo($("#startTime").val(), "startTime");
    setDatatableSearchInfo($("#endTime").val(), "endTime");
    dataTable.ajax.reload();
};

let handleSubmit = function () {
    $('#recharge_detail_content_').validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: false,
        rules: {
            rechargeChainType: {
                required: true
            },
            chainSalePrice: {
                required: true,

            },
            priceStartDate: {
                required: true
            }
        },
        messages: {
            rechargeChainType: {
                required: "Please enter the chain"
            },
            chainSalePrice: {
                required: "Please enter the price info"
            },
            priceStartDate: {
                required: "Please enter the start date"
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
            if (element.attr('id') == 'chainSalePrice') {
                element.parent('div').parent('div').append(error);
            } else {
                element.parent('div').append(error);
            }
        },
        submitHandler: function (form) {
            submitPrice();
        }
    });
};

function submitPrice() {
    var data = {};
    $("#submit").prop('disabled', true)
    data.chainId = $("#rechargeChainType").val();
    data.salePrice = $("#chainSalePrice").val();
    data.startDate = $("#priceStartDate").val();
    $.ajax({
        "type": "post",
        "url": "/price/add",
        "dataType": "json",
        "contentType": "application/json",
        "data": JSON.stringify(data),
        "success": function (data) {
            if (data.code == 1) {
                alert_success("", "Submitted successfully", function () {
                    $("#recharge_modal").modal("hide");
                    document.querySelector('#recharge_detail_content_').reset();
                    search();
                });
            } else if (data.code == 3) {
                alert_success_login(data.msg);
            } else {
                alert_error("", data.msg);
            }
            $("#submit").prop('disabled', false)
        }
    });
};


