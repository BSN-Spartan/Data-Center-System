
function load_DropDownList_Get(element, api, value, display, name, selectId, callback) {
    $(element).empty();
    $(element).append("<option value=''>Loading...</option>");
    $.ajax({
        url: api,
        type: "GET",
        dataType: "json",
        success: function (data) {
            $(element).empty();
            if (name != null && name != undefined) {
                $("<option value=''>" + name + "</option>").appendTo(element);
            }
            for (var i = 0; i < data.data.length; ++i) {
                if (data.data[i][value] == selectId) {
                    var html = '<option value=' + data.data[i][value] + ' selected>' + data.data[i][display] + '</option>';
                } else {
                    var html = '<option value=' + data.data[i][value] + '>' + data.data[i][display] + '</option>';
                }

                $(html).appendTo(element);
            }
            if (typeof(callback) == 'function') {
                callback(data);
            }
        },
        error: function (xhr, status, error) {
            $(element).empty();
            $("<option value=''>ErrorOfLoad</option>").appendTo(element);
            console.log(status + ' : ' + error);
        }
    });
}