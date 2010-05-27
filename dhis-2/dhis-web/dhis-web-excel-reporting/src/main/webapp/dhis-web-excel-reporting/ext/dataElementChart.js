Ext.onReady(function(){
	Ext.QuickTips.init();
	
	pieChartRadio.render( 'pie' );
	lineChartRadio.render( 'line' );
	columnChartRadio.render( 'column' );
	

	
	//chartTypeRadioGroup.render( 'chart-type' );
	
	
	
	
	var periodsCombo = new Ext.form.ComboBox({
		store: periodsStore,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_period,
		selectOnFocus:true,
		applyTo: 'periods'		
	});
	
	var periodTypesCombo = new Ext.form.ComboBox({
		store: periodTypesStore,
		autoWidth :true,
		typeAhead: true,
		displayField: 'name',
		valueField: 'name',
		typeAhead: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_period_type,
		selectOnFocus:true,
		applyTo: 'period-types',
		listeners: {
				'select': {
					fn: function() {						
						periodsCombo.reset();
						periodsStore.baseParams = { name: periodTypesCombo.getValue() };
						periodsStore.reload();							
					},
					scope: this
				}
		}
	});
	
	
   
	var dataElementGroupCombo = new Ext.form.ComboBox({
		store: dataElementGroupsStore,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		typeAhead: true,
		autoWidth :true,
		listWidth:400,
		width:400,
		resizable: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_dataelement_groups,
		selectOnFocus:true,
		applyTo: 'data-element-groups',
		listeners: {
				'select': {
					fn: function() {						
						Ext.getCmp('selected-dataelement').reset();
						dataElementsStore.baseParams = { id: dataElementGroupCombo.getValue() };
						dataElementsStore.reload();							
					},
					scope: this
				}
		}
	});
	
	var dataElementsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default
		layout: 'column',
		renderTo: 'data-elements',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-dataelement',
            name: 'selected',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: dataElementsStore,
                displayField: 'name',
                valueField: 'id'				
            },{
                width: 250,
                height: 300,
                store: emptyStore , 
				displayField: 'name',
                valueField: 'id'	
            }]
        }]
	});
	
	var dataElementChartB = new Ext.Button({
		text: 'View Chart',
		renderTo: 'view-dataelement-chart',
		handler: function() {
			var dataElements = Ext.getCmp('selected-dataelement').getValue();
			
			var period = periodsCombo.getValue();	
			
			var store = new Ext.data.JsonStore({
				url: path + 'getDataElementChart' + type,
				baseParams: { format: 'json', xaxis: dataElements.split(','), yaxis: period},	
				root: 'data',		
				fields: ['name', 'value'],
				sortInfo: { field: 'name', direction: 'ASC' },
				autoLoad: true	
			});
			
			
			var chartType = columnChartRadio.getGroupValue();
			
			if( chartType == PIE_CHART_TYPE ){
				viewPieChart("Data Element", "Values", "Column Chart", store	);
			}else if( chartType == LINE_CHART_TYPE ){
				viewLineChart("Data Element", "Values", "Column Chart", store	);
			}else{
				viewColumnChart("Data Element", "Values", "Column Chart", store	);
			}
		
		}
	});


	
});