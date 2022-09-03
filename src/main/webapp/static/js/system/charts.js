function getPieChartOption(text, seriesName, legendData, data) {
    if (data.length < 1) {
        data.push({name: "No Data", value: 0});
        legendData = ["No Data"]
    }
    var option = {
        title: {
            text: text,
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            // orient: 'vertical',
            // top: 'middle',
            bottom: 10,
            left: 'center',
            data: legendData
        },
        series: [
            {
                name: seriesName,
                type: 'pie',
                radius: '65%',
                center: ['50%', '50%'],
                selectedMode: 'single',
                data: data,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    return option;
}


function getBarChartOption(text, seriesName, dataName, data) {
    var option = {
        title: {
            text: text,
            left: 'center'
        },
        toolbox: {
            show: true,
            feature: {
                magicType: {
                    type: ['line', 'bar']
                },
            }
        },
        color: ['#3398DB'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: dataName,
                axisLabel: {
                    interval: 0,
                    rotate: 60,
                    formatter: function (value) {
                        return value.split("(").join("\n(");
                    }
                },
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: seriesName,
                type: 'bar',
                barWidth: '30%',
                data: data
            }
        ]
    };

    return option;
}

function getLineChartOption(text, xName, dataName, data) {
    data = handleLineChartData(data);
    var option = {
        title: {
            text: text,
            left: 'center'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            padding:[40,30,0,30],
            data: dataName
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top:'20%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: xName
        },
        yAxis: {
            type: 'value'
        },
        series: data
    };

    return option;
}

var handleLineChartData = function (data) {
    for (var i = 0; i < data.length; i++) {
        data[i].type = "line"
    }
    return data;
};
