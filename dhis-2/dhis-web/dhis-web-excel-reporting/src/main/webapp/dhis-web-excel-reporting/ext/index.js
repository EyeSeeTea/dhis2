var path = "";
var type = ".action";

var PIE_CHART_TYPE = "PIE_CHART_TYPE";
var LINE_CHART_TYPE = "LINE_CHART_TYPE";
var COLUMN_CHART_TYPE = "COLUMN_CHART_TYPE";
var FULL_CHART_TYPE = "FULL_CHART_TYPE";
var AXIS_X_DE = "AXIS_X_DE";
var AXIS_X_IN = "AXIS_X_IN";
Ext.chart.Chart.CHART_URL = 'ext/resources/charts.swf';

Ext.onReady(function()
{

	pieChartRadio.render( 'pie' );
	lineChartRadio.render( 'line' );
	columnChartRadio.render( 'column' );
	fullChartRadio.render( 'full' );

});


function viewFullChart( xTitle, title, colTitle, lineTitle, store )
{
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
	
	new Ext.Window(
	{		
		id:'view-full-chart-window'+random,	
		title:'<span>'+ title +'</span>',	
		layout:'fit',		
		defaults:{layout:'fit',
		bodyStyle:'padding:2px; border:0px'},
		width:600,
		height:500,
		collapsible: true,
        maximizable: true,
		items: new Ext.chart.ColumnChart({				
				id:'full-chart' + random,	
				store: store,				
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',				
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
					},
					xAxis: {
						labelRotation: -60
					}

				},
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle					
				}),							
				tipRenderer : function(chart, record, index, series){
					if(series.yField == 'total'){
						return Ext.util.Format.number(record.data.total, '0,0') + ' in ' + record.data.name;
					}else{
						a = Ext.util.Format.number(record.data.value, '0,0');
						b = Ext.util.Format.number(record.data.total, '0,0');
						return a + "/" + b + ' in ' + record.data.name;
					}
				},
				series: [{
					type: 'column',
					displayName: colTitle,
					yField: 'total',
					style: {
						image:'bar.gif',
						mode: 'stretch',
						color:0x99BBE8
						}
					},{
						type:'line',
						displayName: lineTitle,
						yField: 'value',
						style: {
							color: 0x15428B
						}
				}]

			})
	}).show();
		

}

function viewPieChart( xTitle, title, store )
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


function viewLineChart( xTitle, title, store )
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
				extraStyle: {
				   xAxis: {
						labelRotation: -60
					}
				}
			})
	}).show();
		
}

function viewColumnChart( xTitle, title, store )
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
		items:[ new Ext.chart.ColumnChart({				
				id:'columnchart' + random,	
				store: store,
				yField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle
				}),				
				extraStyle: {
				   xAxis: {
						labelRotation: -60
					}
				}
			}),
			
			]
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

var fullChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'full-chart',
	inputValue: FULL_CHART_TYPE	
});

//	===================================================================================
//	Data Element & Indicator Chart
//	===================================================================================

var dataElementGroupsStore = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var dataElementsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var indicatorGroupsStore = new Ext.data.JsonStore({
	url: path + 'getIndicatorGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'indicatorGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var indicatorsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getIndicators' + type,            
	root: 'indicators',
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
	autoLoad: false
});

var periodsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

//	===================================================================================
//	Period  Chart
//	===================================================================================


var dataElementGroupsStorePc = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var dataElementsStorePc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var indicatorGroupsStorePc = new Ext.data.JsonStore({
	url: path + 'getIndicatorGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'indicatorGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var indicatorsStorePc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getIndicators' + type,            
	root: 'indicators',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
	
var periodTypesStorePc = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStorePc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

//	===================================================================================
//	Organisation Unit  Chart
//	===================================================================================


var dataElementGroupsStoreOc = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var dataElementsStoreOc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var indicatorGroupsStoreOc = new Ext.data.JsonStore({
	url: path + 'getIndicatorGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'indicatorGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var indicatorsStoreOc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getIndicators' + type,            
	root: 'indicators',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
	
var periodTypesStoreOc = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStoreOc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

var organisationUnitStoreOc = new Ext.data.JsonStore({
	url: path + 'getChildrenOrganisationUnit' + type,  
	baseParams: { format: 'json'},	
	root: 'organisationUnits',
	fields: ['id', 'name'],	
	autoLoad: true	
});



function switchAxisxDeIn( a, b)
{
	if($("#"+a).css("display")=='none'){
		$("#"+a).css("display", "block");
	}else{
		$("#"+a).css("display", "none");
	}
	
	if($("#"+b).css("display")=='none'){
		$("#"+b).css("display", "block");
	}else{
		$("#"+b).css("display", "none");
	}
	
}



