DHIS = {};
DHIS.conf = {
    finals: {
        ajax: {
            url_visualizer: '../',
            url_commons: '../../dhis-web-commons-ajax-json/',
            url_portal: '../../dhis-web-portal/',
            url_indicator: 'getAggregatedIndicatorValuesPlugin',
            url_dataelement: 'getAggregatedDataValuesPlugin'
        },        
        dimension: {
            indicator: {
                value: 'indicator',
                rawvalue: 'Indicator'
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: 'Data element'
            },
            period: {
                value: 'period',
                rawvalue: 'Period'
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: 'Organisation unit'
            }
        },        
        chart: {
            x: 'x',
            series: 'series',
            category: 'category',
            filter: 'filter',
            column: 'column',
            column_stacked: 'column_stacked',
            bar: 'bar',
            bar_stacked: 'bar_stacked',
            line: 'line',
            area: 'area',
            pie: 'pie'
        }
    }
};

Ext.onReady( function() {
	
    DHIS.initialize = function() {
        DHIS.store.column = DHIS.store.defaultChartStore;
        DHIS.store.column_stacked = DHIS.store.defaultChartStore;
        DHIS.store.bar_stacked = DHIS.store.bar;
        DHIS.store.line = DHIS.store.defaultChartStore;
        DHIS.store.area = DHIS.store.defaultChartStore;
        DHIS.store.pie = DHIS.store.defaultChartStore;
        
        DHIS.getChart = DHIS.exe.execute;
    };
    
    DHIS.util = {
        dimension: {
            indicator: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each( DHIS.state.conf.indicators, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DHIS.util.getCmp('multiselect[name="selectedIndicators"]').store.each( function(r) {
                        a.push(DHIS.util.chart.getEncodedSeriesName(r.data.shortName));
                    });
                    if (exception && !a.length) {
                        alert('No indicators selected');
                    }
                    return a;
                }
            },
            dataelement: {
                getUrl: function(isFilter) {
                    var a = [];
                    DHIS.state.conf.dataelements.each( function(r) {
                        a.push('dataElementIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DHIS.util.getCmp('multiselect[name="selectedDataElements"]').store.each( function(r) {
                        a.push(DHIS.util.chart.getEncodedSeriesName(r.data.shortName));
                    });
                    if (exception && !a.length) {
                        alert('No data elements selected');
                    }
                    return a;
                }
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.conf.periods, function(r) {
						a.push(r + '=true')
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        cmp = DHIS.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            Ext.Array.each(DHIS.init.system.periods[item.paramName], function(item) {
                                a.push(DHIS.util.chart.getEncodedSeriesName(item.name));
                            });
                        }
                    });
                    if (exception && !a.length) {
                        alert('No periods selected');
                    }
                    return a;
                },
                getNameById: function(id) {
                    for (var obj in DHIS.init.system.periods) {
                        var a = DHIS.init.system.periods[obj];
                        for (var i = 0; i < a.length; i++) {
                            if (a[i].id == id) {
                                return a[i].name;
                            }
                        };
                    }
                }
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.conf.organisationunits, function(r) {
						a.push('organisationUnitIds=' + r)
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        treepanel = DHIS.util.getCmp('treepanel'),
                        selection = treepanel.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [treepanel.getRootNode()];
                        treepanel.selectRoot();
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push(DHIS.util.chart.getEncodedSeriesName(r.data.text));
                    });
                    if (exception && !a.length) {
                        alert('No organisation units selected');
                    }
                    return a;                        
                }
            }
        },
        chart: {
            getEncodedSeriesName: function(text) {
                return text.replace(/\./g,'');
            },
            getLegend: function() {
				var len = DHIS.state.series.data.length;
                return {
                    position: len > 6 ? 'right' : 'top',
                    boxStroke: '#ffffff',
                    boxStrokeWidth: 0
                };
            },
            getGrid: function() {
                return {
                    opacity: 1,
                    fill: '#f1f1f1',
                    stroke: '#aaa',
                    'stroke-width': 0.2
                };
            },
            line: {
                getSeriesArray: function() {
                    var a = [];
                    for (var i = 0; i < DHIS.store.chart.left.length; i++) {
                        a.push({
                            type: 'line',
                            axis: 'left',
                            xField: DHIS.store.chart.bottom,
                            yField: DHIS.store.chart.left[i]
                        });
                    }
                    return a;
                }
            }
        },
        number: {
            isInteger: function(n) {
                var str = new String(n);
                if (str.indexOf('.') > -1) {
                    var d = str.substr(str.indexOf('.') + 1);
                    return (d.length === 1 && d == '0');
                }
                return false;
            },
            allValuesAreIntegers: function(values) {
                for (var i = 0; i < values.length; i++) {
                    if (!this.isInteger(values[i].v)) {
                        return false;
                    }
                }
                return true;
            },
            getChartAxisFormatRenderer: function() {
                return this.allValuesAreIntegers(DHIS.value.values) ? '0' : '0.0';
            }
        },
        string: {
            extendUrl: function(url) {
                if (url.charAt(url.length-1) !== '/') {
                    url += '/';
                }
                return url;
            }
        }
    };
    
    DHIS.store = {
        chart: null,
        getChartStore: function() {
            this[DHIS.state.type]();
        },
        defaultChartStore: function() {
            var keys = [];
            Ext.Array.each(DHIS.chart.data, function(item) {
                keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });
            this.chart = Ext.create('Ext.data.Store', {
                fields: keys,
                data: DHIS.chart.data
            });
            this.chart.bottom = [DHIS.conf.finals.chart.x];
            this.chart.left = keys.slice(0);
            for (var i = 0; i < this.chart.left.length; i++) {
                if (this.chart.left[i] === DHIS.conf.finals.chart.x) {
                    this.chart.left.splice(i, 1);
                }
            }
            
			DHIS.chart.getChart(true);
        },
        bar: function() {
            var properties = Ext.Object.getKeys(DHIS.chart.data[0]);
            this.chart = Ext.create('Ext.data.Store', {
                fields: properties,
                data: DHIS.chart.data
            });
            this.chart.left = properties.slice(0, 1);
            this.chart.bottom = properties.slice(1, properties.length);
            
			DHIS.chart.getChart(true);
        }
    };
    
    DHIS.state = {
		conf: null,
        type: DHIS.conf.finals.chart.column,
        series: {
            dimension: null,
            data: []
        },
        category: {
            dimension: null,
            data: []
        },
        filter: {
            dimension: null,
            data: []
        },
        getState: function(conf) {
            this.resetState(conf);
            
            this.type = conf.type;
            this.series.dimension = conf.series;
            this.category.dimension = conf.category;
            this.filter.dimension = conf.filter;
            
			DHIS.value.getValues();
        },
        getIndiment: function() {
            var i = DHIS.conf.finals.dimension.indicator.value;
            return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i) ?
                DHIS.conf.finals.dimension.indicator : DHIS.conf.finals.dimension.dataelement;
        },
        isIndicator: function() {
            var i = DHIS.conf.finals.dimension.indicator.value;
            return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i);
        },
        resetState: function(conf) {
			this.conf = conf;
            this.indiment = null;
            this.period = null;
            this.organisationunit = null;
            this.series.dimension = null;
            this.series.data = [];
            this.category.dimension = null;
            this.category.data = [];
            this.filter.dimension = null;
            this.filter.data = [];
        }
    };
    
    DHIS.value = {
        values: [],
        getValues: function() {
            var params = [],
                indicator = DHIS.conf.finals.dimension.indicator.value,
                dataelement = DHIS.conf.finals.dimension.dataelement.value,
                series = DHIS.state.series.dimension,
                category = DHIS.state.category.dimension,
                filter = DHIS.state.filter.dimension,
                indiment = DHIS.state.getIndiment().value,
                url = DHIS.state.isIndicator() ? DHIS.conf.finals.ajax.url_indicator : DHIS.conf.finals.ajax.url_dataelement;
                
            params = params.concat(DHIS.util.dimension[series].getUrl());
            params = params.concat(DHIS.util.dimension[category].getUrl());
            params = params.concat(DHIS.util.dimension[filter].getUrl(true));
                        
            var baseUrl = DHIS.util.string.extendUrl(DHIS.state.conf.url) + url + '.action';
            Ext.Array.each(params, function(item) {
                baseUrl = Ext.String.urlAppend(baseUrl, item);
            });
            
            Ext.Ajax.request({
                url: baseUrl,
                success: function(r) {
                    DHIS.value.values = Ext.JSON.decode(r.responseText).values;
                    
                    if (!DHIS.value.values.length) {
                        alert('no data values');
                        return;
                    }
                    
                    Ext.Array.each(DHIS.value.values, function(item) {
						item.indicator = item.in;
						item.dataelement = item.in;
						item.period = item.pn;
						item.organisationunit = item.on;
						
						DHIS.state.series.data.push(item[DHIS.state.series.dimension]);
						DHIS.state.category.data.push(item[DHIS.state.category.dimension]);
						DHIS.state.filter.data.push(item[DHIS.state.filter.dimension]);
                        item.v = parseFloat(item.v);
                    });
                    
					DHIS.chart.getData();
                }
            });
        }
    };
    
    DHIS.chart = {
        data: [],        
        getData: function() {
            this.data = [];
			
            Ext.Array.each(DHIS.state.category.data, function(item) {
                var obj = {};
                obj[DHIS.conf.finals.chart.x] = item;
                DHIS.chart.data.push(obj);
            });
            
            Ext.Array.each(DHIS.chart.data, function(item) {
                for (var i = 0; i < DHIS.state.series.data.length; i++) {
                    for (var j = 0; j < DHIS.value.values.length; j++) {
                        if (DHIS.value.values[j][DHIS.state.category.dimension] === item[DHIS.conf.finals.chart.x] && DHIS.value.values[j][DHIS.state.series.dimension] === DHIS.state.series.data[i]) {
                            item[DHIS.value.values[j][DHIS.state.series.dimension]] = DHIS.value.values[j].v;
                            break;
                        }
                    }
                }
            });
                
			DHIS.store.getChartStore(true);
        },        
        chart: null,
        getChart: function() {
            this[DHIS.state.type]();
        },
        column: function(stacked) {
            this.chart = Ext.create('Ext.chart.Chart', {
				renderTo: DHIS.state.conf.div,
                width: 1400,
                height: 500,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DHIS.store.chart.left,
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        },
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DHIS.store.chart.bottom
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: DHIS.store.chart.bottom,
                        yField: DHIS.store.chart.left,
                        stacked: stacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
        },
        column_stacked: function() {
            this.column(true);
        },
        bar: function(stacked) {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'left',
                        fields: DHIS.store.chart.left
                    },
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'bottom',
                        minimum: 0,
                        fields: DHIS.store.chart.bottom,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    }
                ],
                series: [
                    {
                        type: 'bar',
                        axis: 'bottom',
                        xField: DHIS.store.chart.left,
                        yField: DHIS.store.chart.bottom,
                        stacked: stacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
        },
        bar_stacked: function() {
            this.bar(true);
        },
        line: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DHIS.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DHIS.store.chart.bottom
                    }
                ],
                series: DHIS.util.chart.line.getSeriesArray()
            });
        },
        area: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DHIS.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DHIS.store.chart.bottom
                    }
                ],
                series: [{
                    type: 'area',
                    axis: 'left',
                    xField: DHIS.store.chart.bottom[0],
                    yField: DHIS.store.chart.left,
                    style: {
                        opacity: 0.65
                    }
                }]
            });
        },
        pie: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                shadow: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.category.data.length),
                insetPadding: 60,
                series: [{
                    type: 'pie',
                    field: DHIS.store.chart.left[0],
                    showInLegend: true,
                    tips: {
                        trackMouse: false,
                        width: 160,
                        height: 31,
                        renderer: function(i) {
                            this.setTitle('<span class="DHIS-chart-tips">' + i.data.x + ': <b>' + i.data[DHIS.store.chart.left[0]] + '</b></span>');
                        }
                    },
                    label: {
                        field: DHIS.store.chart.bottom[0]
                    },
                    highlight: {
                        segment: {
                            margin: 10
                        }
                    },
                    style: {
                        opacity: 0.9
                    }
                }]
            });
        },
        reload: function() {
            var c = Ext.getCmp('center');
            c.removeAll(true);
            c.add(this.chart);
        }
    };
    
    DHIS.datatable = {
        datatable: null,
        getDataTable: function(exe) {
            this.datatable = Ext.create('Ext.grid.Panel', {
                height: DHIS.util.viewport.getSize().y - DHIS.conf.layout.east_tbar_height,
                scroll: 'vertical',
                cls: 'DHIS-datatable',
                columns: [
                    {
                        text: DHIS.state.getIndiment().rawvalue,
                        dataIndex: DHIS.state.getIndiment().value,
                        width: 150
                    },
                    {
                        text: DHIS.conf.finals.dimension.period.rawvalue,
                        dataIndex: DHIS.conf.finals.dimension.period.value,
                        width: 100,
                        sortable: false
                    },
                    {
                        text: DHIS.conf.finals.dimension.organisationunit.rawvalue,
                        dataIndex: DHIS.conf.finals.dimension.organisationunit.value,
                        width: 150
                    },
                    {
                        text: 'Value',
                        dataIndex: 'v',
                        width: 80
                    }
                ],
                store: DHIS.store.datatable,
                listeners: {
                    afterrender: function() {
                        DHIS.cmp.datatable = this;
                    }
                }
            });
            
            if (exe) {
                this.reload();
            }
            else {
                return this.datatable;
            }
        },
        reload: function() {
            var c = DHIS.util.getCmp('panel[region="east"]');
            c.removeAll(true);
            c.add(this.datatable);
        }            
    };
    
    DHIS.exe = {
        execute: function(conf) {
			DHIS.state.getState(conf);
		}
    };
    
    DHIS.initialize();
});
