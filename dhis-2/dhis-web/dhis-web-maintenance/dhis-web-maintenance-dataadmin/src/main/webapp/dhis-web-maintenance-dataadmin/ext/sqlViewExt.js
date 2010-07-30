//----------------------------------------------------------------------------
// Ext Stores
//----------------------------------------------------------------------------

	// init arrays
	Ext.namespace('Ext.resourcetables');
	Ext.namespace('Ext.sorttypes');

	Ext.resourcetables.names = [
		['orgunitstructure', 'orgunitstructure'],
		['orgunitgroupsetstructure', 'orgunitgroupsetstructure'],
		['_dataelementgroupsetstructure', '_dataelementgroupsetstructure'],
		['_indicatorgroupsetstructure', '_indicatorgroupsetstructure'],
		['_organisationunitgroupsetstructure', '_organisationunitgroupsetstructure'],
		['_categorystructure', '_categorystructure'],
		['categoryoptioncomboname', 'categoryoptioncomboname'] ];
	
	Ext.sorttypes.names = [
		['ASC', 'ASCENDING'],
		['DESC', 'DESCENDING'] ];
	
	// set up array store
	var resourceTablesComboStore = new Ext.data.SimpleStore({
		fields: ['id', 'name'],
		data : Ext.resourcetables.names
	});
	
	// set up array store
	var sortTypesComboStore = new Ext.data.SimpleStore({
		fields: ['id', 'name'],
		data : Ext.sorttypes.names
	});

	var resourcePropertiesComboStore = new Ext.data.JsonStore({
		url: 'getResourceProperties.action',            
		root: 'resourceProperties',
		fields: ['name'],
		sortInfo: { field: 'name', direction: 'ASC' },
		autoLoad: false
	});

	//----------------------------------------------------------------------------
	// Ext Components & Methods
	//----------------------------------------------------------------------------
	
	function createPropertiesStore()
	{
		var propertiesComboStoreInstance = new Ext.data.JsonStore({
			url: 'getResourceProperties.action',            
			root: 'resourceProperties',
			fields: ['name'],
			sortInfo: { field: 'name', direction: 'ASC' },
			autoLoad: false
		});
		
		return propertiesComboStoreInstance;
	}
	
	function createTableComboboxExt( tableComboId, propertyComboId, aliasFieldId, sortComboId, groupbyCheckBoxId )
	{
		var propertiesComboStoreInstance = createPropertiesStore();
		var sortComboInstance = createSortTypeComboboxExt( sortComboId );
		var propertiesComboInstance = createPropertyComboboxExt( propertyComboId, propertiesComboStoreInstance, aliasFieldId, sortComboInstance, groupbyCheckBoxId );
		
		var resourceTablesCombo = new Ext.form.ComboBox({
			store: resourceTablesComboStore,
			displayField: 'name',
			valueField: 'id',
			autoWidth: true,
			resizable: true,
			allowBlank: false,
			listWidth: 150,
			width: 150,
			mode: 'local',
			triggerAction: 'all',
			emptyText: i18n_resourcetables,
			selectOnFocus: true,
			enableKeyEvents: true,
			applyTo: tableComboId,
			listeners: {
				'select': {
					fn: function() {					
						propertiesComboInstance.reset();
						propertiesComboStoreInstance.baseParams = { name: resourceTablesCombo.getValue() };
						propertiesComboStoreInstance.reload();
					},
					scope: this
				}
			}
		});
	}
	
	function createPropertyComboboxExt( propertyComboId, propertyComboStoreInstance, aliasFieldId, sortComboInstance, groupbyCheckBoxId )
	{
		var groupbyElement = byId(groupbyCheckBoxId);
		var aliasElement = byId(aliasFieldId);
		var resourcePropertiesCombo = new Ext.form.ComboBox({
			store: propertyComboStoreInstance,
			displayField: 'name',
			valueField: 'name',
			typeAhead: true,
			autoWidth: true,
			resizable: true,
			allowBlank: false,
			listWidth: 150,
			width: 150,
			mode: 'local',
			triggerAction: 'all',
			emptyText: i18n_properties,
			selectOnFocus: true,
			enableKeyEvents: true,
			regex: /^[a-zA-Z0-9\s()*].*$/,
			regexText: i18n_properties_welformed,
			applyTo: propertyComboId,
			listeners: {
				'keyup': function(comboBox, e) {
				
					var value = this.getRawValue();
				
					if ( regexStar.test(value) || regexCountOther.test(value) || regexSumOther.test(value) 
					|| regexMinOther.test(value) || regexMaxOther.test(value) || regexAvgOther.test(value) 
					|| regexAverageOther.test(value) )
					{
						sortComboInstance.reset();
						sortComboInstance.disable();
						groupbyElement.checked = false;
						groupbyElement.disabled = true;
						
						if ( regexStar.test(value) )
						{
							aliasElement.value = "";
							aliasElement.disabled = true;
						}
						
						if ( regexSumStar.test(value) || regexMinStar.test(value) || regexMaxStar.test(value) 
						|| regexAvgStar.test(value) || regexAverageStar.test(value) )
						{
							comboBox.setValue( value.replace(/\*/g, "") );
						}
					}
					else
					{
						sortComboInstance.enable();
						groupbyElement.disabled = false;
						aliasElement.disabled = false;
					}
				},
				'blur': function(comboBox, e) {
				
					var value = this.getRawValue().replace(/\s+/g, " ");
					var keywordLen = 3;
					var found = value.search(regexCount);
					
					if ( found >= 0 )
					{
						keywordLen = 5;
						validateKeywordField(comboBox, value, found, keywordLen, "COUNT(*)", sortComboInstance, groupbyElement);
					}
					else
					{
						found = value.search(regexSum);
						if ( found >= 0 )
						{
							validateKeywordField(comboBox, value, found, keywordLen, "SUM()", sortComboInstance, groupbyElement);
						}
						else
						{
							found = value.search(regexMin);
							if ( found >= 0 )
							{
								validateKeywordField(comboBox, value, found, keywordLen, "MIN()", sortComboInstance, groupbyElement);
							}
							else
							{
								found = value.search(regexMax);
								if ( found >= 0 )
								{
									validateKeywordField(comboBox, value, found, keywordLen, "MAX()", sortComboInstance, groupbyElement);
								}
								else
								{
									found = value.search(regexAvg);
									if ( found >= 0 )
									{
										validateKeywordField(comboBox, value, found, keywordLen, "AVG()", sortComboInstance, groupbyElement);
									}
									else
									{
										found = value.search(regexAverage);
										if ( found >= 0 )
										{
											keywordLen = 7;
											validateKeywordField(comboBox, value, found, keywordLen, "AVG()", sortComboInstance, groupbyElement);

		}	}	}	}	}	}	}	}	});
		
		return resourcePropertiesCombo;
	}
	
	function createSortTypeComboboxExt( sortComboId )
	{
		var sortTypeCombo = new Ext.form.ComboBox({
			store: sortTypesComboStore,
			displayField: 'name',
			valueField: 'id',
			typeAhead: true,
			autoWidth: true,
			resizable: true,
			listWidth: 150,
			width: 150,
			mode: 'local',
			triggerAction: 'all',
			emptyText: i18n_sorttypes,
			selectOnFocus: true,
			applyTo: sortComboId
		});
		
		return sortTypeCombo;
	}

	function resetAppropriateProperties( propertyComboId )
	{
		Ext.getCmp( propertyComboId ).reset();
	}
	
	function validateKeywordField(comboBox, value, found, keywordLen, replaceString, sortComboInstance, groupbyElement)
	{
		var headPos = value.charAt(found - 1).trim();
		var tailPos = value.charAt(found + keywordLen).trim();
		
		if ( (headPos == "") && (tailPos == "") )
		{
			if ( !regexCountStar.test(replaceString) )
			{
				alert( i18n_syntax_error_parameter_required );
				comboBox.focus(true, true);
			}
			
			comboBox.setValue( replaceString );
			sortComboInstance.reset();
			sortComboInstance.disable();
			groupbyElement.checked = false;
			groupbyElement.disabled = true;
		}
	}

	Ext.onReady(function() {

		Ext.QuickTips.init();
		
		/**
			The Regex global variables
			Carefully, using the Regex with test() method if pattern has //g
			pattern.test(field_1) --> true
			pattern.test(field_2) --> false
			
			In which, field_1 IS field_2
		*/
		regexStar = /^\s*\*\s*$/;
		
		regexCountStar = /\s*count\s*\(\s*\*\s*\)\s*/i;
		regexSumStar = /\s*sum\s*\(.*\*.*/i;
		regexMinStar = /\s*min\s*\(.*\*.*/i;
		regexMaxStar = /\s*max\s*\(.*\*.*/i;
		regexAvgStar = /\s*avg\s*\(.*\*.*/i;
		regexAverageStar = /\s*average\s*\(.*\*.*/i;

		regexCountOther = /\s*count\s*\(\s*/i;
		regexSumOther = /\s*sum\s*\(\s*/i;
		regexMinOther = /\s*min\s*\(\s*/i;
		regexMaxOther = /\s*max\s*\(\s*/i;
		regexAvgOther = /\s*avg\s*\(\s*/i;
		regexAverageOther = /\s*average\s*\(\s*/i;
		
		regexCount = /count/i;
		regexSum = /sum/i;
		regexMin = /min/i;
		regexMax = /max/i;
		regexAvg = /avg/i;
		regexAverage = /average/i;
		
		// inits the relative combos
		createTableComboboxExt("resource-table-combo1", "resource-property-combo1", "alias-property-text1", "sort-property-combo1", "groupby-property-checkbox1");
		
	});	