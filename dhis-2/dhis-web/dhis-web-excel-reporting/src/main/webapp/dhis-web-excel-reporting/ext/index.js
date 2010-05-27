var path = "";
var type = ".action";

var PIE_CHART_TYPE = "PIE_CHART_TYPE";
var LINE_CHART_TYPE = "LINE_CHART_TYPE";
var COLUMN_CHART_TYPE = "COLUMN_CHART_TYPE";
Ext.chart.Chart.CHART_URL = 'ext/resources/charts.swf';


Ext.onReady(function()
{

	// basic tabs 1, built from existing content
    new Ext.TabPanel({
        renderTo: 'tabs',
        width:'100%',
        activeTab: 0,
        frame:true,
        defaults:{autoHeight: true, autoScroll: true},
        items:[
            {contentEl:'data-element-chart', title: i18n_dataelement_chart},
            {contentEl:'period-chart', title: i18n_period_chart}
        ]
    });
	
	new Ext.TabPanel({
        renderTo: 'tabs-chart-type',
        width:'100%',
        activeTab: 0,
        frame:true,
        defaults:{autoHeight: true, autoScroll: true},
        items:[
            {contentEl:'tab-chart-type', title: i18n_select_chart_type}
        ]
    });
	
	
	
	
	
	
	

});

function viewPieChart( xTitle, yTitile, title, store )
{
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
	
	new Ext.Window(
	{		
		id:'view-pie-chart-window'+random,	
		title:'<span>'+ title +'</span>',	
		layout:'fit',		
		defaults:{layout:'fit',
		bodyStyle:'padding:2px; border:0px'},
		width:600,
		height:500,
		collapsible: true,
        maximizable: true,
		items: new Ext.chart.PieChart({				
				id:'pie-chart' + random,	
				store: store,
				dataField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				categoryField: 'name',				
				extraStyle: {
				   legend:
					{
						display: 'bottom',
						padding: 5,
						font:
						{
							family: 'Tahoma',
							size: 13
						}
					}

				}
			})
	}).show();
		
}


function viewLineChart( xTitle, yTitile, title, store )
{
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
	
	new Ext.Window(
	{		
		id:'view-line-chart-window'+random,	
		title:'<span>'+ title +'</span>',	
		layout:'fit',		
		defaults:{layout:'fit',
		bodyStyle:'padding:2px; border:0px'},
		width:600,
		height:500,
		collapsible: true,
        maximizable: true,
		items: new Ext.chart.LineChart({				
				id:'linechart' + random,	
				store: store,
				yField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle
				}),
				yAxis: new Ext.chart.NumericAxis({
					title: yTitile
				}),	
				extraStyle: {
				   xAxis: {
						labelRotation: -60
					}
				}
			})
	}).show();
		
}

function viewColumnChart( xTitle, yTitile, title, store )
{
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
	
	new Ext.Window(
	{		
		id:'view-column-chart-window'+random,	
		title:'<span>'+ title +'</span>',	
		layout:'fit',		
		defaults:{layout:'fit',
		bodyStyle:'padding:2px; border:0px'},
		width:600,
		height:500,
		collapsible: true,
        maximizable: true,
		items: new Ext.chart.ColumnChart({				
				id:'columnchart' + random,	
				store: store,
				yField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle
				}),
				yAxis: new Ext.chart.NumericAxis({
					title: yTitile
				}),	
				extraStyle: {
				   xAxis: {
						labelRotation: -60
					}
				}
			})
	}).show();
		
}



var pieChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'pie-chart',
	inputValue: PIE_CHART_TYPE
});

var lineChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'line-chart',
	inputValue: LINE_CHART_TYPE
});


var columnChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'column-chart',
	inputValue: COLUMN_CHART_TYPE,
	checked: true
});


var dataElementGroupsStore = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: true
});

var dataElementsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
	
var periodTypesStore = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: true
});

var periodsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var emptyStore = new Ext.data.JsonStore({
	data: {empty:[{id:0, name:''}]},
	root: 'empty',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
