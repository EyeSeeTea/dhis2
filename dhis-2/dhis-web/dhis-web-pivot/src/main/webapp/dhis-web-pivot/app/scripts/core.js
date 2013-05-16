PT.core = {};

Ext.onReady( function() {

PT.core.getConfigs = function() {
	var conf = {},
		dim;

	conf.finals = {
        ajax: {
            path_pivot: '../',
            path_api: '../../api/',
            initialize: 'initialize.action',
            redirect: 'dhis-web-commons-about/redirect.action',
            data_get: 'chartValues.json',
            indicator_get: 'indicatorGroups/',
            indicator_getall: 'indicators.json?paging=false&links=false',
            indicatorgroup_get: 'indicatorGroups.json?paging=false&links=false',
            dataelement_get: 'dataElementGroups/',
            dataelement_getall: 'dataElements.json?paging=false&links=false',
            dataelementgroup_get: 'dataElementGroups.json?paging=false&links=false',
            dataset_get: 'dataSets.json?paging=false&links=false',
            organisationunit_getbygroup: 'getOrganisationUnitPathsByGroup.action',
            organisationunit_getbylevel: 'getOrganisationUnitPathsByLevel.action',
            organisationunit_getbyids: 'getOrganisationUnitPaths.action',
            organisationunitgroup_getall: 'organisationUnitGroups.json?paging=false&links=false',
            organisationunitgroupset_get: 'getOrganisationUnitGroupSetsMinified.action',
            organisationunitlevel_getall: 'organisationUnitLevels.json?paging=false&links=false&viewClass=detailed',
            organisationunitchildren_get: 'getOrganisationUnitChildren.action',
            favorite_addorupdate: 'addOrUpdateChart.action',
            favorite_addorupdatesystem: 'addOrUpdateSystemChart.action',
            favorite_updatename: 'updateChartName.action',
            favorite_get: 'charts/',
            favorite_getall: 'getSystemAndCurrentUserCharts.action',
            favorite_delete: 'deleteCharts.action'
        },
        dimension: {
            data: {
                value: 'data',
                name: PT.i18n.data,
                dimensionName: 'dx',
                objectName: 'dx',
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_ind_de
				}
            },
            category: {
				name: PT.i18n.categories,
				dimensionName: 'co',
                objectName: 'co',
			},
            indicator: {
                value: 'indicators',
                name: PT.i18n.indicators,
                dimensionName: 'dx',
                objectName: 'in'
            },
            dataElement: {
                value: 'dataElements',
                name: PT.i18n.data_elements,
                dimensionName: 'dx',
                objectName: 'de'
            },
            operand: {
				value: 'operand',
				name: 'Operand',
				dimensionName: 'dx',
				objectName: 'dc'
			},
            dataSet: {
				value: 'dataSets',
                name: PT.i18n.data_sets,
                dimensionName: 'dx',
                objectName: 'ds'
			},
            period: {
                value: 'period',
                name: PT.i18n.periods,
                dimensionName: 'pe',
                objectName: 'pe'
            },
            fixedPeriod: {
				value: 'periods'
			},
			relativePeriod: {
				value: 'relativePeriods'
			},
            organisationUnit: {
                value: 'organisationUnits',
                name: PT.i18n.organisation_units,
                dimensionName: 'ou',
                objectName: 'ou'
            },
            dimension: {
				value: 'dimension'
				//objectName: 'di'
			},
			value: {
				value: 'value'
			}
        },
        root: {
			id: 'root'
		}
	};

	dim = conf.finals.dimension;

	dim.objectNameMap = {};
	dim.objectNameMap[dim.data.objectName] = dim.data;
	dim.objectNameMap[dim.indicator.objectName] = dim.indicator;
	dim.objectNameMap[dim.dataElement.objectName] = dim.dataElement;
	dim.objectNameMap[dim.operand.objectName] = dim.operand;
	dim.objectNameMap[dim.dataSet.objectName] = dim.dataSet;
	dim.objectNameMap[dim.category.objectName] = dim.category;
	dim.objectNameMap[dim.period.objectName] = dim.period;
	dim.objectNameMap[dim.organisationUnit.objectName] = dim.organisationUnit;
	dim.objectNameMap[dim.dimension.objectName] = dim.dimension;

	conf.period = {
		relativePeriods: {
			'LAST_WEEK': 1,
			'LAST_4_WEEKS': 4,
			'LAST_12_WEEKS': 12,
			'LAST_MONTH': 1,
			'LAST_3_MONTHS': 3,
			'LAST_BIMONTH': 1,
			'LAST_6_BIMONTHS': 6,
			'LAST_12_MONTHS': 12,
			'LAST_QUARTER': 1,
			'LAST_4_QUARTERS': 4,
			'LAST_SIX_MONTH': 1,
			'LAST_2_SIXMONTHS': 2,
			'LAST_FINANCIAL_YEAR': 1,
			'LAST_5_FINANCIAL_YEARS': 6,
			'THIS_YEAR': 1,
			'LAST_YEAR': 1,
			'LAST_5_YEARS': 5
		},
		relativePeriodValueKeys: {
			'LAST_WEEK': 'lastWeek',
			'LAST_4_WEEKS': 'last4Weeks',
			'LAST_12_WEEKS': 'last12Weeks',
			'LAST_MONTH': 'lastMonth',
			'LAST_3_MONTHS': 'last3Months',
			'LAST_12_MONTHS': 'last12Months',
			'LAST_BIMONTH': 'lastBimonth',
			'LAST_6_BIMONTHS': 'last6BiMonths',
			'LAST_QUARTER': 'lastQuarter',
			'LAST_4_QUARTERS': 'last4Quarters',
			'LAST_SIX_MONTH': 'lastSixMonth',
			'LAST_2_SIXMONTHS': 'last2SixMonths',
			'LAST_FINANCIAL_YEAR': 'lastFinancialYear',
			'LAST_5_FINANCIAL_YEARS': 'last5FinancialYears',
			'THIS_YEAR': 'thisYear',
			'LAST_YEAR': 'lastYear',
			'LAST_5_YEARS': 'last5Years'
		},
		relativePeriodParamKeys: {
			'lastWeek': 'LAST_WEEK',
			'last4Weeks': 'LAST_4_WEEKS',
			'last12Weeks': 'LAST_12_WEEKS',
			'lastMonth': 'LAST_MONTH',
			'last3Months': 'LAST_3_MONTHS',
			'last12Months': 'LAST_12_MONTHS',
			'lastBimonth': 'LAST_BIMONTH',
			'last6BiMonths': 'LAST_6_BIMONTHS',
			'lastQuarter': 'LAST_QUARTER',
			'last4Quarters': 'LAST_4_QUARTERS',
			'lastSixMonth': 'LAST_SIX_MONTH',
			'last2SixMonths': 'LAST_2_SIXMONTHS',
			'lastFinancialYear': 'LAST_FINANCIAL_YEAR',
			'last5FinancialYears': 'LAST_5_FINANCIAL_YEARS',
			'thisYear': 'THIS_YEAR',
			'lastYear': 'LAST_YEAR',
			'last5Years': 'LAST_5_YEARS'
		},
		periodTypes: [
			{id: 'Daily', name: 'Daily'},
			{id: 'Weekly', name: 'Weekly'},
			{id: 'Monthly', name: 'Monthly'},
			{id: 'BiMonthly', name: 'BiMonthly'},
			{id: 'Quarterly', name: 'Quarterly'},
			{id: 'SixMonthly', name: 'SixMonthly'},
			{id: 'Yearly', name: 'Yearly'},
			{id: 'FinancialOct', name: 'FinancialOct'},
			{id: 'FinancialJuly', name: 'FinancialJuly'},
			{id: 'FinancialApril', name: 'FinancialApril'}
		]
	};

	conf.layout = {
        west_width: 424,
        west_fieldset_width: 416,
        west_width_padding: 4,
        west_fill: 2,
        west_fill_accordion_indicator: 59,
        west_fill_accordion_dataelement: 59,
        west_fill_accordion_dataset: 33,
        west_fill_accordion_period: 296,
        west_fill_accordion_organisationunit: 62,
        west_maxheight_accordion_indicator: 400,
        west_maxheight_accordion_dataelement: 400,
        west_maxheight_accordion_dataset: 400,
        west_maxheight_accordion_period: 513,
        west_maxheight_accordion_organisationunit: 900,
        west_maxheight_accordion_group: 340,
        west_maxheight_accordion_options: 449,
        west_scrollbarheight_accordion_indicator: 300,
        west_scrollbarheight_accordion_dataelement: 300,
        west_scrollbarheight_accordion_dataset: 300,
        west_scrollbarheight_accordion_period: 450,
        west_scrollbarheight_accordion_organisationunit: 450,
        west_scrollbarheight_accordion_group: 300,
        east_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 55,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
        window_share_width: 500,
        grid_favorite_width: 420,
        grid_row_height: 27,
        treepanel_minheight: 135,
        treepanel_maxheight: 400,
        treepanel_fill_default: 310,
        treepanel_toolbar_menu_width_group: 140,
        treepanel_toolbar_menu_width_level: 120,
        multiselect_minheight: 100,
        multiselect_maxheight: 250,
        multiselect_fill_default: 345,
        multiselect_fill_reportingrates: 315
    };

	conf.pivot = {
		digitGroupSeparator: {
			'comma': ',',
			'space': ' '
		},
		displayDensity: {
			'compact': '3px',
			'normal': '5px',
			'comfortable': '10px'
		},
		fontSize: {
			'small': '10px',
			'normal': '11px',
			'large': '13px'
		}
	};

    conf.util = {
		jsonEncodeString: function(str) {
			return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
		},
		jsonEncodeArray: function(a) {
			for (var i = 0; i < a.length; i++) {
				a[i] = pt.conf.util.jsonEncodeString(a[i]);
			}
			return a;
		}
	};

	return conf;
};

PT.core.getUtils = function(pt) {
	var util = {};

	util.object = {
		getLength: function(object) {
			var size = 0;

			for (var key in object) {
				if (object.hasOwnProperty(key)) {
					size++;
				}
			}

			return size;
		}
	};

	util.multiselect = {
		select: function(a, s) {
			var selected = a.getValue();
			if (selected.length) {
				var array = [];
				Ext.Array.each(selected, function(item) {
					array.push({id: item, name: a.store.getAt(a.store.findExact('id', item)).data.name});
				});
				s.store.add(array);
			}
			this.filterAvailable(a, s);
		},
		selectAll: function(a, s, doReverse) {
			var array = [];
			a.store.each( function(r) {
				array.push({id: r.data.id, name: r.data.name});
			});
			if (doReverse) {
				array.reverse();
			}
			s.store.add(array);
			this.filterAvailable(a, s);
		},
		unselect: function(a, s) {
			var selected = s.getValue();
			if (selected.length) {
				Ext.Array.each(selected, function(item) {
					s.store.remove(s.store.getAt(s.store.findExact('id', item)));
				});
				this.filterAvailable(a, s);
			}
		},
		unselectAll: function(a, s) {
			s.store.removeAll();
			a.store.clearFilter();
			this.filterAvailable(a, s);
		},
		filterAvailable: function(a, s) {
			a.store.filterBy( function(r) {
				var keep = true;
				s.store.each( function(r2) {
					if (r.data.id == r2.data.id) {
						keep = false;
					}

				});
				return keep;
			});
			a.store.sortStore();
		},
		setHeight: function(ms, panel, fill) {
			for (var i = 0; i < ms.length; i++) {
				ms[i].setHeight(panel.getHeight() - fill);
			}
		}
	};

	util.store = {
		addToStorage: function(s, records) {
			s.each( function(r) {
				if (!s.storage[r.data.id]) {
					s.storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: s.parent};
				}
			});
			if (records) {
				Ext.Array.each(records, function(r) {
					if (!s.storage[r.data.id]) {
						s.storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: s.parent};
					}
				});
			}
		},
		loadFromStorage: function(s) {
			var items = [];
			s.removeAll();
			for (var obj in s.storage) {
				if (s.storage[obj].parent === s.parent) {
					items.push(s.storage[obj]);
				}
			}
			s.add(items);
			s.sort('name', 'ASC');
		},
		containsParent: function(s) {
			for (var obj in s.storage) {
				if (s.storage[obj].parent === s.parent) {
					return true;
				}
			}
			return false;
		}
	};

	util.mask = {
		showMask: function(cmp, msg) {
			cmp = cmp || pt.viewport;
			msg = msg || 'Loading..';

			if (pt.viewport.mask) {
				pt.viewport.mask.destroy();
			}
			pt.viewport.mask = new Ext.create('Ext.LoadMask', cmp, {
				id: 'pt-loadmask',
				shadow: false,
				msg: msg,
				style: 'box-shadow:0',
				bodyStyle: 'box-shadow:0'
			});
			pt.viewport.mask.show();
		},
		hideMask: function() {
			if (pt.viewport.mask) {
				pt.viewport.mask.hide();
			}
		}
	};

	util.checkbox = {
		setRelativePeriods: function(rp) {
			if (rp) {
				for (var r in rp) {
					var cmp = pt.util.getCmp('checkbox[relativePeriodId="' + r + '"]');
					if (cmp) {
						cmp.setValue(rp[r]);
					}
				}
			}
			else {
				PT.util.checkbox.setAllFalse();
			}
		},
		setAllFalse: function() {
			var a = pt.cmp.dimension.relativePeriod.checkbox;
			for (var i = 0; i < a.length; i++) {
				a[i].setValue(false);
			}
		},
		isAllFalse: function() {
			var a = pt.cmp.dimension.relativePeriod.checkbox;
			for (var i = 0; i < a.length; i++) {
				if (a[i].getValue()) {
					return false;
				}
			}
			return true;
		}
	};

	util.array = {
		sortDimensions: function(dimensions, key) {
			key = key || 'dimensionName';

			// Sort object order
			Ext.Array.sort(dimensions, function(a,b) {
				if (a[key] < b[key]) {
					return -1;
				}
				if (a[key] > b[key]) {
					return 1;
				}
				return 0;
			});

			// Sort object items order
			for (var i = 0, dim; i < dimensions.length; i++) {
				dim = dimensions[i];

				if (dim.items) {
					dimensions[i].items.sort();
				}
			}

			return dimensions;
		},

		sortObjectsByString: function(array, key) {
			key = key || 'name';
			array.sort( function(a, b) {
				var nameA = a[key].toLowerCase(),
					nameB = b[key].toLowerCase();

				if (nameA < nameB) {
					return -1;
				}
				if (nameA > nameB) {
					return 1;
				}
				return 0;
			});
			return array;
		}
	};

	util.number = {
		getNumberOfDecimals: function(x) {
			var tmp = new String(x);
			return (tmp.indexOf(".") > -1) ? (tmp.length - tmp.indexOf(".") - 1) : 0;
		},

		roundIf: function(x, fix) {
			if (Ext.isString(x)) {
				x = parseFloat(x);
			}

			if (Ext.isNumber(x) && Ext.isNumber(fix)) {
				var dec = pt.util.number.getNumberOfDecimals(x);
				return parseFloat(dec > fix ? x.toFixed(fix) : x);
			}
			return x;
		},

		pp: function(x, nf) {
			nf = nf || 'space';

			if (nf === 'none') {
				return x;
			}

			return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, pt.conf.pivot.digitGroupSeparator[nf]);
		}
	};

	util.pivot = {
		extendLayout: function(layout) {
			var xLayout = Ext.clone(layout),
				dimConf = pt.conf.finals.dimension,

				axisDimensions = [].concat(Ext.clone(xLayout.columns) || [], Ext.clone(xLayout.rows) || []),
				axisDimensionNames = [],
				axisObjectNames = [],
				axisItems = [],

				filterDimensions = Ext.clone(xLayout.filters) || [],
				filterDimensionNames = [],
				filterObjectNames = [],
				filterItems = [],

				objectNameDimensionMap = {},
				dimensionNameItemsMap = {},

				columns,
				columnsDimensionNames = [],
				rows,
				rowsDimensionNames = [],
				filters,
				filtersDimensionNames = [],

				objectNameRecordsMap = {};

			xLayout.extended = {};

			// Axis
			for (var i = 0, dim, items; i < axisDimensions.length; i++) {
				dim = axisDimensions[i];
				items = [];

				dim.dimensionName = pt.conf.finals.dimension.objectNameMap[dim.dimension].dimensionName;
				dim.objectName = dim.dimension;

				axisDimensionNames.push(dim.dimensionName);
				axisObjectNames.push(dim.objectName);

				for (var j = 0; j < dim.items.length; j++) {
					items.push(dim.items[j].id);
				}

				dim.items = items;

				axisItems = axisItems.concat(items);
			}

			// Filter
			for (var i = 0, dim, items; i < filterDimensions.length; i++) {
				dim = filterDimensions[i];
				items = [];

				dim.dimensionName = pt.conf.finals.dimension.objectNameMap[dim.dimension].dimensionName;
				dim.objectName = dim.dimension;

				filterDimensionNames.push(dim.dimensionName);
				filterObjectNames.push(dim.objectName);

				for (var j = 0; j < dim.items.length; j++) {
					items.push(dim.items[j].id);
				}

				dim.items = items;

				filterItems = filterItems.concat(items);
			}

			// Axis
			xLayout.extended.axisDimensions = axisDimensions;
			xLayout.extended.axisDimensionNames = Ext.Array.unique(axisDimensionNames);
			xLayout.extended.axisObjectNames = axisObjectNames;
			xLayout.extended.axisItems = axisItems;

			// Filter
			xLayout.extended.filterDimensions = filterDimensions;
			xLayout.extended.filterDimensionNames = Ext.Array.unique(filterDimensionNames);
			xLayout.extended.filterObjectNames = filterObjectNames;
			xLayout.extended.filterItems = filterItems;

			// All
			xLayout.extended.dimensions = [].concat(axisDimensions, filterDimensions);
			xLayout.extended.dimensionNames = Ext.Array.unique([].concat(axisDimensionNames, filterDimensionNames));
			xLayout.extended.objectNames = [].concat(axisObjectNames, filterObjectNames);
			xLayout.extended.items = [].concat(axisItems, filterItems);

			// Sorted axis
			xLayout.extended.sortedAxisDimensions = pt.util.array.sortDimensions(Ext.clone(axisDimensions));
			xLayout.extended.sortedAxisDimensionNames = Ext.Array.unique(Ext.clone(axisDimensionNames).sort());
			xLayout.extended.sortedAxisObjectNames = Ext.clone(axisObjectNames).sort();
			xLayout.extended.sortedAxisItems = Ext.clone(axisItems).sort();

			// Sorted filter
			xLayout.extended.sortedFilterDimensions = pt.util.array.sortDimensions(Ext.clone(filterDimensions));
			xLayout.extended.sortedFilterDimensionNames = Ext.Array.unique(Ext.clone(filterDimensionNames).sort());
			xLayout.extended.sortedFilterObjectNames = Ext.clone(filterObjectNames).sort();
			xLayout.extended.sortedFilterItems = Ext.clone(filterItems).sort();

			// Sorted all
			xLayout.extended.sortedDimensions = [].concat(xLayout.extended.sortedAxisDimensions, xLayout.extended.sortedFilterDimensions);
			xLayout.extended.sortedDimensionNames = Ext.Array.unique([].concat(xLayout.extended.sortedAxisDimensionNames, xLayout.extended.sortedFilterDimensionNames));
			xLayout.extended.sortedObjectNames = [].concat(xLayout.extended.sortedAxisObjectNames, xLayout.extended.sortedFilterObjectNames);
			xLayout.extended.sortedItems = [].concat(xLayout.extended.sortedAxisItems, xLayout.extended.sortedFilterItems);

			// Maps

			// Add dimensionName keys
			for (var i = 0, name; i < xLayout.extended.dimensionNames.length; i++) {
				name = xLayout.extended.dimensionNames[i];
				dimensionNameItemsMap[name] = [];
			}

			// Add dimensions and items
			for (var i = 0, dim; i < xLayout.extended.dimensions.length; i++) {
				dim = xLayout.extended.dimensions[i];

				// objectName : object
				objectNameDimensionMap[dim.objectName] = dim;

				// dimensionName : items
				dimensionNameItemsMap[dim.dimensionName] = dimensionNameItemsMap[dim.dimensionName].concat(Ext.clone(dim.items));
			}

			xLayout.extended.objectNameDimensionMap = objectNameDimensionMap;
			xLayout.extended.dimensionNameItemsMap = dimensionNameItemsMap;

			// Columns, rows, filters
			columns = Ext.clone(xLayout.columns) || [];
			for (var i = 0, dim; i < columns.length; i++) {
				dim = columns[i];
				dim.objectName = dim.dimension;
				dim.dimensionName = dimConf.objectNameMap[dim.objectName].dimensionName;
				dim.records = Ext.clone(dim.items);
				dim.items = xLayout.extended.objectNameDimensionMap[dim.objectName].items;

				objectNameRecordsMap[dim.objectName] = dim.records;
			}

			rows = Ext.clone(xLayout.rows) || [];
			for (var i = 0, dim; i < rows.length; i++) {
				dim = rows[i];
				dim.objectName = dim.dimension;
				dim.dimensionName = dimConf.objectNameMap[dim.objectName].dimensionName;
				dim.records = Ext.clone(dim.items);
				dim.items = xLayout.extended.objectNameDimensionMap[dim.objectName].items;

				objectNameRecordsMap[dim.objectName] = Ext.clone(dim.records);
			}

			filters = Ext.clone(xLayout.filters) || [];
			for (var i = 0, dim; i < filters.length; i++) {
				dim = filters[i];
				dim.objectName = dim.dimension;
				dim.dimensionName = dimConf.objectNameMap[dim.objectName].dimensionName;
				dim.records = Ext.clone(dim.items);
				dim.items = xLayout.extended.objectNameDimensionMap[dim.objectName].items;

				objectNameRecordsMap[dim.objectName] = Ext.clone(dim.records);
			}

			xLayout.extended.columns = columns;
			xLayout.extended.rows = rows;
			xLayout.extended.filters = filters;

			xLayout.extended.objectNameRecordsMap = objectNameRecordsMap;

			// columnsDimensionNames, rowsDimensionNames, filtersDimensionNames
			for (var i = 0; i < xLayout.extended.columns.length; i++) {
				columnsDimensionNames.push(xLayout.extended.columns[i].dimensionName);
			}

			for (var i = 0; i < xLayout.extended.rows.length; i++) {
				rowsDimensionNames.push(xLayout.extended.rows[i].dimensionName);
			}

			for (var i = 0; i < xLayout.extended.filters.length; i++) {
				filtersDimensionNames.push(xLayout.extended.filters[i].dimensionName);
			}

			xLayout.extended.columnsDimensionNames = columnsDimensionNames;
			xLayout.extended.rowsDimensionNames = rowsDimensionNames;
			xLayout.extended.filtersDimensionNames = filtersDimensionNames;

			return xLayout;
		},

		createTable: function(layout, pt) {
			var extendLayout,
				getSyncronizedXLayout,
				getParamString,
				validateResponse,
				extendResponse,
				getDimensionObjects,
				extendAxis,
				validateUrl,
				getTableHtml,
				initialize,

				dimConf = pt.conf.finals.dimension;

			getSyncronizedXLayout = function(xLayout, response) {
				var getHeaderNames,

					headerNames,
					newLayout;

				getHeaderNames = function() {
					var a = [];

					for (var i = 0; i < response.headers.length; i++) {
						a.push(response.headers[i].name);
					}

					return a;
				};

				removeDimensionFromLayout = function(dimensionName) {
					var getCleanAxis;

					getAxis = function(axis) {
						var axis = Ext.clone(axis),
							dimension;

						for (var i = 0; i < axis.length; i++) {
							if (axis[i].dimensionName === dimensionName) {
								dimension = axis[i];
							}
						}

						if (dimension) {
							Ext.Array.remove(axis, dimension);
						}

						return axis;
					};

					if (layout.columns) {
						layout.columns = getAxis(layout.columns);
					}
					if (layout.rows) {
						layout.rows = getAxis(layout.rows);
					}
					if (layout.filters) {
						layout.filters = getAxis(layout.filters);
					}
				};

				headerNames = getHeaderNames();

				// remove co from layout if it does not exist in response
				if (Ext.Array.contains(xLayout.extended.axisDimensionNames, dimConf.category.dimensionName) && !(Ext.Array.contains(headerNames, dimConf.category.dimensionName))) {
					removeDimensionFromLayout(dimConf.category.dimensionName);

					newLayout = pt.api.layout.classes.Layout(layout);

					if (!newLayout) {
						return;
					}

					return pt.util.pivot.extendLayout(newLayout);
				}
				else {
					return xLayout;
				}
			};

			getParamString = function(xLayout) {
				var sortedAxisDimensionNames = xLayout.extended.sortedAxisDimensionNames,
					sortedFilterDimensions = xLayout.extended.sortedFilterDimensions,
					paramString = '?',
					dimConf = pt.conf.finals.dimension,
					addCategoryDimension = false,
					map = xLayout.extended.dimensionNameItemsMap,
					dx = dimConf.indicator.dimensionName,
					items;

				for (var i = 0, dimensionName; i < sortedAxisDimensionNames.length; i++) {
					dimensionName = sortedAxisDimensionNames[i];

					paramString += 'dimension=' + dimensionName;

					items = Ext.clone(xLayout.extended.dimensionNameItemsMap[dimensionName]).sort();

					if (dimensionName === dx) {
						for (var j = 0, index; j < items.length; j++) {
							index = items[j].indexOf('-');

							if (index > 0) {
								addCategoryDimension = true;
								items[j] = items[j].substr(0, index);
							}
						}

						items = Ext.Array.unique(items);
					}

					paramString += ':' + items.join(';');

					if (i < (sortedAxisDimensionNames.length - 1)) {
						paramString += '&';
					}
				}

				if (addCategoryDimension) {
					paramString += '&dimension=' + pt.conf.finals.dimension.category.dimensionName;
				}

				if (Ext.isArray(sortedFilterDimensions) && sortedFilterDimensions.length) {
					for (var i = 0, dim; i < sortedFilterDimensions.length; i++) {
						dim = sortedFilterDimensions[i];

						paramString += '&filter=' + dim.dimensionName + ':' + dim.items.join(';');
					}
				}

				return paramString;
			};

			validateResponse = function(response) {
				if (!(response && Ext.isObject(response))) {
					alert('Data invalid');
					return false;
				}

				if (!(response.headers && Ext.isArray(response.headers) && response.headers.length)) {
					alert('Data invalid');
					return false;
				}

				if (!(Ext.isNumber(response.width) && response.width > 0 &&
					  Ext.isNumber(response.height) && response.height > 0 &&
					  Ext.isArray(response.rows) && response.rows.length > 0)) {
					alert('No values found');
					return false;
				}

				if (response.headers.length !== response.rows[0].length) {
					alert('Data invalid');
					return false;
				}

				return true;
			};

			extendResponse = function(response, xLayout) {
				response.nameHeaderMap = {};
				response.idValueMap = {};

				var extendHeaders = function() {

					// Extend headers: index, items, size
					for (var i = 0, header; i < response.headers.length; i++) {
						header = response.headers[i];
						header.index = i;

						if (header.meta) {

							// categories
							if (header.name === pt.conf.finals.dimension.category.dimensionName) {
								header.items = [].concat(response.metaData[pt.conf.finals.dimension.category.dimensionName]);
							}
							// periods
							else if (header.name === pt.conf.finals.dimension.period.dimensionName) {
								header.items = [].concat(response.metaData[pt.conf.finals.dimension.period.dimensionName]);
							}
							else {
								header.items = xLayout.extended.dimensionNameItemsMap[header.name];
							}

							header.size = header.items.length;
						}
					}

					// nameHeaderMap (headerName: header)
					for (var i = 0, header; i < response.headers.length; i++) {
						header = response.headers[i];

						response.nameHeaderMap[header.name] = header;
					}
				}();

				var createValueIds = function() {
					var valueHeaderIndex = response.nameHeaderMap[pt.conf.finals.dimension.value.value].index,
						dimensionNames = xLayout.extended.axisDimensionNames,
						idIndexOrder = [];

					// idIndexOrder
					for (var i = 0; i < dimensionNames.length; i++) {
						idIndexOrder.push(response.nameHeaderMap[dimensionNames[i]].index);
					}

					// idValueMap
					for (var i = 0, row, id; i < response.rows.length; i++) {
						row = response.rows[i];
						id = '';

						for (var j = 0; j < idIndexOrder.length; j++) {
							id += row[idIndexOrder[j]];
						}

						response.idValueMap[id] = row[valueHeaderIndex];
					}
				}();

				return response;
			};

			getDimensionObjects = function(dimensionNames) {
				var a = [];
				dimensionNames = Ext.Array.unique(dimensionNames);

				for (var i = 0; i < dimensionNames.length; i++) {
					a.push({
						dimensionName: dimensionNames[i]
					});
				}

				return a;
			};

			extendAxis = function(type, axis, xResponse) {
				if (!axis || (Ext.isArray(axis) && !axis.length)) {
					return;
				}

				var axis = Ext.clone(axis),
					spanType = type === 'col' ? 'colSpan' : 'rowSpan',
					nCols = 1,
					aNumCols = [],
					aAccNumCols = [],
					aSpan = [],
					aGuiItems = [],
					aAllItems = [],
					aColIds = [],
					aAllObjects = [],
					aUniqueIds;

				aUniqueIds = function() {
					var a = [];

					for (var i = 0, dim; i < axis.length; i++) {
						dim = axis[i];

						a.push(xResponse.nameHeaderMap[dim.dimensionName].items);
					}

					return a;
				}();
//aUniqueIds	= [ [de1, de2, de3],
//					[p1],
//					[ou1, ou2, ou3, ou4] ]


				for (var i = 0, dim; i < aUniqueIds.length; i++) {
					nNumCols = aUniqueIds[i].length;

					aNumCols.push(nNumCols);
					nCols = nCols * nNumCols;
					aAccNumCols.push(nCols);
				}
	//aNumCols		= [3, 1, 4]
	//nCols			= (12) [3, 3, 12] (3 * 1 * 4)
	//aAccNumCols	= [3, 3, 12]

	//nCols			= 12

				for (var i = 0; i < aUniqueIds.length; i++) {
					if (aNumCols[i] === 1) {
						if (i === 0) {
							aSpan.push(nCols); //if just one item and top level, span all
						}
						else {
							if (layout.hideEmptyRows && type === 'row') {
								aSpan.push(nCols / aAccNumCols[i]);
							}
							else {
								aSpan.push(aSpan[0]); //if just one item and not top level, span same as top level
							}
						}
					}
					else {
						aSpan.push(nCols / aAccNumCols[i]);
					}
				}
	//aSpan			= [4, 12, 1]


				aGuiItems.push(aUniqueIds[0]);

				if (aUniqueIds.length > 1) {
					for (var i = 1, a, n; i < aUniqueIds.length; i++) {
						a = [];
						n = aNumCols[i] === 1 ? aNumCols[0] : aAccNumCols[i-1];

						for (var j = 0; j < n; j++) {
							a = a.concat(aUniqueIds[i]);
						}

						aGuiItems.push(a);
					}
				}
	//aGuiItems	= [ [d1, d2, d3], (3)
	//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
	//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
	//		  	  ]


				for (var i = 0, aAllRow, aUniqueRow, span, factor; i < aUniqueIds.length; i++) {
					aAllRow = [];
					aUniqueRow = aUniqueIds[i];
					span = aSpan[i];
					factor = nCols / (span * aUniqueRow.length);

					for (var j = 0; j < factor; j++) {
						for (var k = 0; k < aUniqueRow.length; k++) {
							for (var l = 0; l < span; l++) {
								aAllRow.push(aUniqueRow[k]);
							}
						}
					}

					aAllItems.push(aAllRow);
				}
	//aAllItems	= [ [d1, d1, d1, d1, d1, d1, d1, d1, d1, d1, d2, d2, d2, d2, d2, d2, d2, d2, d2, d2, d3, d3, d3, d3, d3, d3, d3, d3, d3, d3], (30)
	//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (30)
	//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2] (30)
	//		  	  ]


				for (var i = 0, id; i < nCols; i++) {
					id = '';

					for (var j = 0; j < aAllItems.length; j++) {
						id += aAllItems[j][i];
					}

					aColIds.push(id);
				}
	//aColIds	= [ aaaaaaaaBBBBBBBBccccccc, aaaaaaaaaccccccccccbbbbbbbbbb, ... ]



				// allObjects

				for (var i = 0, allRow; i < aAllItems.length; i++) {
					allRow = [];

					for (var j = 0; j < aAllItems[i].length; j++) {
						allRow.push({
							id: aAllItems[i][j]
						});
					}

					aAllObjects.push(allRow);
				}

				// add span and children
				for (var i = 0; i < aAllObjects.length; i++) {
					for (var j = 0, obj; j < aAllObjects[i].length; j += aSpan[i]) {
						obj = aAllObjects[i][j];

						// span
						obj[spanType] = aSpan[i];

						// children
						obj.children = Ext.isDefined(aSpan[i + 1]) ? aSpan[i] / aSpan[i + 1] : 0;

						if (i === 0) {
							obj.root = true;
						}
					}
				}

				// add parents
				if (aAllObjects.length > 1) {
					for (var i = 1, allRow; i < aAllObjects.length; i++) {
						allRow = aAllObjects[i];

						for (var j = 0, obj, sizeCount = 0, span = aSpan[i - 1], parentObj = aAllObjects[i - 1][0]; j < allRow.length; j++) {
							obj = allRow[j];
							obj.parent = parentObj;
							sizeCount++;

							if (sizeCount === span) {
								parentObj = aAllObjects[i - 1][j + 1];
								sizeCount = 0;
							}
						}
					}
				}

				return {
					type: type,
					items: axis,
					xItems: {
						unique: aUniqueIds,
						gui: aGuiItems,
						all: aAllItems
					},
					objects: {
						all: aAllObjects
					},
					ids: aColIds,
					span: aSpan,
					dims: aUniqueIds.length,
					size: nCols
				};
			};

			validateUrl = function(url) {
				if (!Ext.isString(url) || url.length > 2000) {
					var percent = ((url.length - 2000) / url.length) * 100;
					alert('Too many parameters selected. Please reduce the number of parameters by at least ' + percent.toFixed(0) + '%.');
					return;
				}

				return true;
			};

			getTableHtml = function(xColAxis, xRowAxis, xResponse) {
				var getTdHtml,
					doSubTotals,
					doTotals,
					getColAxisHtmlArray,
					getRowHtmlArray,
					rowAxisHtmlArray,
					getColTotalHtmlArray,
					getGrandTotalHtmlArray,
					getTotalHtmlArray,
					getHtml,

					getUniqueFactor = function(xAxis) {
						if (!xAxis) {
							return null;
						}

						var unique = xAxis.xItems.unique;

						if (unique) {
							if (unique.length < 2) {
								return 1;
							}
							else {
								return xAxis.size / unique[0].length;
							}
						}

						return null;
					},

					colUniqueFactor = getUniqueFactor(xColAxis),
					rowUniqueFactor = getUniqueFactor(xRowAxis),

					valueItems = [],
					valueObjects = [],
					totalColObjects = [],
					htmlArray;

				getTdHtml = function(config) {
					var cls,
						colSpan,
						rowSpan,
						htmlValue,
						displayDensity,
						fontSize;

					if (!(config && Ext.isObject(config))) {
						return '';
					}

					cls = config.cls ? config.cls : '';
					cls += config.hidden ? ' td-hidden' : '';
					cls += config.collapsed ? ' td-collapsed' : '';
					colSpan = config.colSpan ? 'colspan="' + config.colSpan + '"' : '';
					rowSpan = config.rowSpan ? 'rowspan="' + config.rowSpan + '"' : '';
					htmlValue = config.collapsed ? '&nbsp;' : config.htmlValue || config.value || '&nbsp;';
					htmlValue = config.type !== 'dimension' ? pt.util.number.pp(htmlValue, layout.digitGroupSeparator) : htmlValue;
					displayDensity = pt.conf.pivot.displayDensity[config.displayDensity] || pt.conf.pivot.displayDensity[layout.displayDensity];
					fontSize = pt.conf.pivot.fontSize[config.fontSize] || pt.conf.pivot.fontSize[layout.fontSize];

					return '<td class="' + cls + '" ' + colSpan + ' ' + rowSpan + ' style="padding:' + displayDensity + '; font-size:' + fontSize + ';">' + htmlValue + '</td>';
				};

				doSubTotals = function(xAxis) {
					return !!layout.showSubTotals && xAxis && xAxis.dims > 1;

					//var multiItemDimension = 0,
						//unique;

					//if (!(layout.showSubTotals && xAxis && xAxis.dims > 1)) {
						//return false;
					//}

					//unique = xAxis.xItems.unique;

					//for (var i = 0; i < unique.length; i++) {
						//if (unique[i].length > 1) {
							//multiItemDimension++;
						//}
					//}

					//return (multiItemDimension > 1);
				};

				doTotals = function() {
					return !!layout.showTotals;
				};

				getColAxisHtmlArray = function() {
					var a = [],
						getEmptyHtmlArray;

					getEmptyHtmlArray = function() {
						return (xColAxis && xRowAxis) ? getTdHtml({cls: 'pivot-dim-empty', colSpan: xRowAxis.dims, rowSpan: xColAxis.dims}) : '';
					};

					if (!(xColAxis && Ext.isObject(xColAxis))) {
						return a;
					}

					for (var i = 0, dimItems, colSpan, dimHtml; i < xColAxis.dims; i++) {
						dimItems = xColAxis.xItems.gui[i];
						colSpan = xColAxis.span[i];
						dimHtml = [];

						if (i === 0) {
							dimHtml.push(getEmptyHtmlArray());
						}

						for (var j = 0, id; j < dimItems.length; j++) {
							id = dimItems[j];
							dimHtml.push(getTdHtml({
								type: 'dimension',
								cls: 'pivot-dim',
								colSpan: colSpan,
								htmlValue: xResponse.metaData.names[id]
							}));

							if (doSubTotals(xColAxis) && i === 0) {
								dimHtml.push(getTdHtml({
									type: 'dimensionSubtotal',
									cls: 'pivot-dim-subtotal',
									rowSpan: xColAxis.dims
								}));
							}

							if (doTotals()) {
								if (i === 0 && j === (dimItems.length - 1)) {
									dimHtml.push(getTdHtml({
										type: 'dimensionTotal',
										cls: 'pivot-dim-total',
										rowSpan: xColAxis.dims,
										htmlValue: 'Total'
									}));
								}
							}
						}

						a.push(dimHtml);
					}

					return a;
				};

				getRowHtmlArray = function() {
					var a = [],
						axisObjects = [],
						xValueObjects,
						totalValueObjects = [],
						mergedObjects = [],
						valueItemsCopy,
						colSize = xColAxis ? xColAxis.size : 1,
						rowSize = xRowAxis ? xRowAxis.size : 1,
						recursiveReduce;

					recursiveReduce = function(obj) {
						if (!obj.children) {
							obj.collapsed = true;

							if (obj.parent) {
								obj.parent.children = obj.parent.children - 1;
							}
						}

						if (obj.parent) {
							recursiveReduce(obj.parent);
						}
					};

					// Populate dim objects
					if (xRowAxis) {
						for (var i = 0, row; i < xRowAxis.objects.all[0].length; i++) {
							row = [];

							for (var j = 0, obj, newObj; j < xRowAxis.objects.all.length; j++) {
								obj = xRowAxis.objects.all[j][i];
								obj.type = 'dimension';
								obj.cls = 'pivot-dim td-nobreak';
								obj.noBreak = true;
								obj.hidden = !(obj.rowSpan || obj.colSpan);
								obj.htmlValue = xResponse.metaData.names[obj.id];

								row.push(obj);
							}

							axisObjects.push(row);
						}
					}

					// Value objects
					for (var i = 0, valueItemsRow, valueObjectsRow, map = Ext.clone(xResponse.idValueMap); i < rowSize; i++) {
						valueItemsRow = [];
						valueObjectsRow = [];

						for (var j = 0, id, value, htmlValue, empty; j < colSize; j++) {
							id = (xColAxis ? xColAxis.ids[j] : '') + (xRowAxis ? xRowAxis.ids[i] : '');
							empty = false;

							if (map[id]) {
								value = parseFloat(map[id]);
								htmlValue = pt.util.number.roundIf(map[id], 1).toString();
							}
							else {
								value = 0;
								htmlValue = '&nbsp;';
								empty = true;
							}

							valueItemsRow.push(value);
							valueObjectsRow.push({
								type: 'value',
								cls: 'pivot-value',
								value: value,
								htmlValue: htmlValue,
								empty: empty
							});
						}

						valueItems.push(valueItemsRow);
						valueObjects.push(valueObjectsRow);
					}

					// Value total objects
					if (xColAxis && doTotals()) {
						for (var i = 0, empty = [], total = 0; i < valueObjects.length; i++) {
							for (j = 0, obj; j < valueObjects[i].length; j++) {
								obj = valueObjects[i][j];

								empty.push(obj.empty);
								total += obj.value;
							}

							totalValueObjects.push({
								type: 'valueTotal',
								cls: 'pivot-value-total',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? pt.util.number.roundIf(total, 1).toString() : '&nbsp;',
								empty: !Ext.Array.contains(empty, false)
							});

							empty = [];
							total = 0;
						}
					}

					// Hide empty rows (dims/values/totals)
					if (xColAxis && xRowAxis) {
						if (layout.hideEmptyRows) {
							for (var i = 0, valueRow, empty, parent; i < valueObjects.length; i++) {
								valueRow = valueObjects[i];
								empty = [];

								for (var j = 0; j < valueRow.length; j++) {
									empty.push(!!valueRow[j].empty);
								}

								if (!Ext.Array.contains(empty, false) && xRowAxis) {

									// Hide values
									for (var j = 0; j < valueRow.length; j++) {
										valueRow[j].collapsed = true;
									}

									// Hide total
									if (doTotals()) {
										totalValueObjects[i].collapsed = true;
									}

									// Hide/reduce parent dim span
									parent = axisObjects[i][xRowAxis.dims-1];
									recursiveReduce(parent);
								}
							}
						}
					}

					xValueObjects = Ext.clone(valueObjects);

					// Col subtotals
					if (doSubTotals(xColAxis)) {
						var tmpValueObjects = [];

						for (var i = 0, row, rowSubTotal, colCount; i < xValueObjects.length; i++) {
							row = [];
							rowSubTotal = 0;
							colCount = 0;

							for (var j = 0, item, collapsed = [], empty = []; j < xValueObjects[i].length; j++) {
								item = xValueObjects[i][j];
								rowSubTotal += item.value;
								empty.push(!!item.empty);
								collapsed.push(!!item.collapsed);
								colCount++;

								row.push(item);

								if (colCount === colUniqueFactor) {
									rowSubTotal = pt.util.number.roundIf(rowSubTotal, 1);

									row.push({
										type: 'valueSubtotal',
										cls: 'pivot-value-subtotal',
										value: rowSubTotal,
										htmlValue: Ext.Array.contains(empty, false) ? rowSubTotal.toString() : '&nbsp',
										empty: !Ext.Array.contains(empty, false),
										collapsed: !Ext.Array.contains(collapsed, false)
									});

									colCount = 0;
									rowSubTotal = 0;
									empty = [];
									collapsed = [];
								}
							}

							tmpValueObjects.push(row);
						}

						xValueObjects = tmpValueObjects;
					}

					// Row subtotals
					if (doSubTotals(xRowAxis)) {
						var tmpAxisObjects = [],
							tmpValueObjects = [],
							tmpTotalValueObjects = [],
							getAxisSubTotalRow;

						getAxisSubTotalRow = function(collapsed) {
							var row = [];

							for (var i = 0, obj; i < xRowAxis.dims; i++) {
								obj = {};
								obj.type = 'dimensionSubtotal';
								obj.cls = 'pivot-dim-subtotal';
								obj.collapsed = Ext.Array.contains(collapsed, true);

								if (i === 0) {
									obj.htmlValue = '&nbsp;';
									obj.colSpan = xRowAxis.dims;
								}
								else {
									obj.hidden = true;
								}

								row.push(obj);
							}

							return row;
						};

						// tmpAxisObjects
						for (var i = 0, row, collapsed = []; i < axisObjects.length; i++) {
							tmpAxisObjects.push(axisObjects[i]);
							collapsed.push(!!axisObjects[i][0].collapsed);

							// Insert subtotal after last objects
							if (!Ext.isArray(axisObjects[i+1]) || !!axisObjects[i+1][0].root) {
								tmpAxisObjects.push(getAxisSubTotalRow(collapsed));

								collapsed = [];
							}
						}

						// tmpValueObjects
						for (var i = 0; i < tmpAxisObjects.length; i++) {
							tmpValueObjects.push([]);
						}

						for (var i = 0; i < xValueObjects[0].length; i++) {
							for (var j = 0, rowCount = 0, tmpCount = 0, subTotal = 0, empty = [], collapsed, item; j < xValueObjects.length; j++) {
								item = xValueObjects[j][i];
								tmpValueObjects[tmpCount++].push(item);
								subTotal += item.value;
								empty.push(!!item.empty);
								rowCount++;

								if (axisObjects[j][0].root) {
									collapsed = !!axisObjects[j][0].collapsed;
								}

								if (!Ext.isArray(axisObjects[j+1]) || axisObjects[j+1][0].root) {
									tmpValueObjects[tmpCount++].push({
										type: item.type === 'value' ? 'valueSubtotal' : 'valueSubtotalTotal',
										value: subTotal,
										htmlValue: Ext.Array.contains(empty, false) ? pt.util.number.roundIf(subTotal, 1).toString() : '&nbsp;',
										collapsed: collapsed,
										cls: item.type === 'value' ? 'pivot-value-subtotal' : 'pivot-value-subtotal-total'
									});
									rowCount = 0;
									subTotal = 0;
									empty = [];
								}
							}
						}

						// tmpTotalValueObjects
						for (var i = 0, obj, collapsed = [], empty = [], subTotal = 0, count = 0; i < totalValueObjects.length; i++) {
							obj = totalValueObjects[i];
							tmpTotalValueObjects.push(obj);

							collapsed.push(!!obj.collapsed);
							empty.push(!!obj.empty);
							subTotal += obj.value;
							count++;

							if (count === xRowAxis.span[0]) {
								tmpTotalValueObjects.push({
									type: 'valueTotalSubgrandtotal',
									cls: 'pivot-value-total-subgrandtotal',
									value: subTotal,
									htmlValue: Ext.Array.contains(empty, false) ? pt.util.number.roundIf(subTotal, 1).toString() : '&nbsp;',
									empty: !Ext.Array.contains(empty, false),
									collapsed: !Ext.Array.contains(collapsed, false)
								});

								collapsed = [];
								empty = [];
								subTotal = 0;
								count = 0;
							}
						}

						axisObjects = tmpAxisObjects;
						xValueObjects = tmpValueObjects;
						totalValueObjects = tmpTotalValueObjects;
					}

					// Merge dim, value, total
					for (var i = 0, row; i < xValueObjects.length; i++) {
						row = [];

						if (xRowAxis) {
							row = row.concat(axisObjects[i]);
						}

						row = row.concat(xValueObjects[i]);

						if (xColAxis) {
							row = row.concat(totalValueObjects[i]);
						}

						mergedObjects.push(row);
					}

					// Create html items
					for (var i = 0, row; i < mergedObjects.length; i++) {
						row = [];

						for (var j = 0; j < mergedObjects[i].length; j++) {
							row.push(getTdHtml(mergedObjects[i][j]));
						}

						a.push(row);
					}

					return a;
				};

				getColTotalHtmlArray = function() {
					var a = [];

					if (xRowAxis && doTotals()) {
						var xTotalColObjects;

						// Total col items
						for (var i = 0, total = 0, empty = []; i < valueObjects[0].length; i++) {
							for (var j = 0, obj; j < valueObjects.length; j++) {
								obj = valueObjects[j][i];

								total += obj.value;
								empty.push(!!obj.empty);
							}

							totalColObjects.push({
								type: 'valueTotal',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? pt.util.number.roundIf(total, 1).toString() : '&nbsp;',
								empty: !Ext.Array.contains(empty, false),
								cls: 'pivot-value-total'
							});

							total = 0;
							empty = [];
						}

						xTotalColObjects = Ext.clone(totalColObjects);

						if (xColAxis && doSubTotals(xColAxis)) {
							var tmp = [];

							for (var i = 0, item, subTotal = 0, empty = [], colCount = 0; i < xTotalColObjects.length; i++) {
								item = xTotalColObjects[i];
								tmp.push(item);
								subTotal += item.value;
								empty.push(!!item.empty);
								colCount++;

								if (colCount === colUniqueFactor) {
									tmp.push({
										type: 'valueTotalSubgrandtotal',
										value: subTotal,
										htmlValue: Ext.Array.contains(empty, false) ? pt.util.number.roundIf(subTotal, 1).toString() : '&nbsp;',
										empty: !Ext.Array.contains(empty, false),
										cls: 'pivot-value-total-subgrandtotal'
									});

									subTotal = 0;
									colCount = 0;
								}
							}

							xTotalColObjects = tmp;
						}

						// Total col html items
						for (var i = 0; i < xTotalColObjects.length; i++) {
							a.push(getTdHtml(xTotalColObjects[i]));
						}
					}

					return a;
				};

				getGrandTotalHtmlArray = function() {
					var total = 0,
						empty = [],
						a = [];

					if (doTotals()) {
						for (var i = 0, obj; i < totalColObjects.length; i++) {
							obj = totalColObjects[i];

							total += obj.value;
							empty.push(obj.empty);
							//values.push(totalColObjects[i].value);
						}

						if (xColAxis && xRowAxis) {
							a.push(getTdHtml({
								type: 'valueGrandTotal',
								cls: 'pivot-value-grandtotal',
								htmlValue: Ext.Array.contains(empty, false) ? pt.util.number.roundIf(total, 1).toString() : '&nbsp;',
								empty: !Ext.Array.contains(empty, false)
							}));
						}
					}

					return a;
				};

				getTotalHtmlArray = function() {
					var dimTotalArray,
						colTotal = getColTotalHtmlArray(),
						grandTotal = getGrandTotalHtmlArray(),
						row,
						a = [];

					if (doTotals()) {
						if (xRowAxis) {
							dimTotalArray = [getTdHtml({
								type: 'dimensionSubtotal',
								cls: 'pivot-dim-total',
								colSpan: xRowAxis.dims,
								htmlValue: 'Total'
							})];
						}

						row = [].concat(dimTotalArray || [], Ext.clone(colTotal) || [], Ext.clone(grandTotal) || []);

						a.push(row);
					}

					return a;
				};

				getHtml = function() {
					var s = '<table id="' + pt.el + '" class="pivot">';

					for (var i = 0; i < htmlArray.length; i++) {
						s += '<tr>' + htmlArray[i].join('') + '</tr>';
					}

					return s += '</table>';
				};

				htmlArray = [].concat(getColAxisHtmlArray(), getRowHtmlArray(), getTotalHtmlArray());
				htmlArray = Ext.Array.clean(htmlArray);

				return getHtml(htmlArray);
			};

			initialize = function() {
				var url,
					xLayout,
					xResponse,
					xColAxis,
					xRowAxis;

				// Extend layout
				xLayout = pt.util.pivot.extendLayout(layout);

				pt.paramString = getParamString(xLayout);
				url = pt.init.contextPath + '/api/analytics.json' + pt.paramString;

				if (!validateUrl(url)) {
					return;
				}

				pt.util.mask.showMask(pt.viewport);

				Ext.Ajax.request({
					method: 'GET',
					url: url,
					callbackName: 'analytics',
					timeout: 60000,
					headers: {
						'Content-Type': 'application/json',
						'Accept': 'application/json'
					},
					disableCaching: false,
					failure: function(r) {
						pt.util.mask.hideMask();
						alert(r.responseText);
					},
					success: function(r) {
						var html,
							response = Ext.decode(r.responseText);

						if (!validateResponse(response)) {
							pt.util.mask.hideMask();
							return;
						}

						// Synchronize xLayout with response
						xLayout = getSyncronizedXLayout(xLayout, response);

						if (!xLayout) {
							pt.util.mask.hideMask();
							return;
						}

						// Extend response
						xResponse = extendResponse(response, xLayout);

						// Dimension objects
						xLayout.columns = getDimensionObjects(xLayout.extended.columnsDimensionNames);
						xLayout.rows = getDimensionObjects(xLayout.extended.rowsDimensionNames);

						// Extend axes
						xColAxis = extendAxis('col', xLayout.columns, xResponse);
						xRowAxis = extendAxis('row', xLayout.rows, xResponse);

						// Create html
						html = getTableHtml(xColAxis, xRowAxis, xResponse);

						// Update viewport
						pt.viewport.centerRegion.removeAll(true);
						pt.viewport.centerRegion.update(html);

						// After table success
						pt.util.mask.hideMask();

						if (pt.viewport.downloadButton) {
							pt.viewport.downloadButton.enable();
						}

						pt.xLayout = xLayout;
						pt.xResponse = xResponse;
console.log("xResponse", xResponse);
console.log("xLayout", xLayout);
					}
				});

			}();
		},

		loadTable: function(id) {
			if (!Ext.isString(id)) {
				alert('Invalid id');
				return;
			}

			Ext.Ajax.request({
				url: pt.baseUrl + '/api/reportTables/' + id + '.json?links=false',
				method: 'GET',
				failure: function(r) {
					pt.util.mask.hideMask();
					alert(r.responseText);
				},
				success: function(r) {
					var config = Ext.decode(r.responseText);
					pt.viewport.setFavorite(config);
				}
			});
		}
	};

	return util;
};

PT.core.getAPI = function(pt) {
	var dimConf = pt.conf.finals.dimension,
		api = {
			dimension: {
				Dimension: null,
				classes: {
					Indicator: null,
					DataElement: null,
					Operand: null,
					DataSet: null,
					Period: null,
					OrganisationUnit: null,
					Dimension: null
				},
				objectNameClassMap: {}
			},
			layout: {
				classes: {
					Layout: null
				}
			}
		};

	// Dimension

	api.dimension.Dimension = function() {
		return {
			dimension: null, // string

			items: null // array of records
		};
	};

	api.dimension.classes.Indicator = function(config) {
		var indicator = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Indicator config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Indicator dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Indicator items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Indicator has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			indicator.dimension = config.dimension;
			indicator.dimensionName = dimConf.indicator.dimensionName;
			indicator.objectName = dimConf.indicator.objectName;
			indicator.items = Ext.clone(config.items);

			return indicator;
		}();
	};

	api.dimension.classes.DataElement = function(config) {
		var dataElement = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Data element config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Data element dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Data element items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Data element has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			dataElement.dimension = config.dimension;
			dataElement.dimensionName = dimConf.dataElement.dimensionName;
			dataElement.objectName = dimConf.dataElement.objectName;
			dataElement.items = Ext.clone(config.items);

			return dataElement;
		}();
	};

	api.dimension.classes.Operand = function(config) {
		var operand = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Operand config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Operand dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Operand items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Operand has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			operand.dimension = config.dimension;
			operand.dimensionName = dimConf.operand.dimensionName;
			operand.objectName = dimConf.operand.objectName;
			operand.items = Ext.clone(config.items);

			// Replace operand id characters
			for (var i = 0, id; i < operand.items.length; i++) {
				id = operand.items[i].id;

				if (id.indexOf('.') !== -1) {
					id = id.replace('.', '-');
					operand.items[i].id = id;
				}
			}

			return operand;
		}();
	};

	api.dimension.classes.DataSet = function(config) {
		var dataSet = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Data set config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Data set dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Data set items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Data set has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			dataSet.dimension = config.dimension;
			dataSet.dimensionName = dimConf.dataSet.dimensionName;
			dataSet.objectName = dimConf.dataSet.objectName;
			dataSet.items = Ext.clone(config.items);

			return dataSet;
		}();
	};

	api.dimension.classes.Period = function(config) {
		var period = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Period config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Period dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Period items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Period has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			period.dimension = config.dimension;
			period.dimensionName = dimConf.period.dimensionName;
			period.objectName = dimConf.period.objectName;
			period.items = Ext.clone(config.items);

			return period;
		}();
	};

	api.dimension.classes.OrganisationUnit = function(config) {
		var organisationUnit = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Organisation unit config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Organisation unit dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Organisation unit items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Organisation unit has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			organisationUnit.dimension = config.dimension;
			organisationUnit.dimensionName = dimConf.organisationUnit.dimensionName;
			organisationUnit.objectName = dimConf.organisationUnit.objectName;
			organisationUnit.items = Ext.clone(config.items);

			return organisationUnit;
		}();
	};

	api.dimension.classes.Dimension = function(config) {
		var dimension = api.dimension.Dimension(),
			validateConfig;

		validateConfig = function() {
			if (!Ext.isObject(config)) {
				alert('Dimension config is not an object');
				return;
			}

			if (!Ext.isString(config.dimension)) {
				alert('Dimension name is illegal');
				return;
			}

			if (!Ext.isArray(config.items)) {
				alert('Dimension items is not an array');
				return;
			}

			if (!config.items.length) {
				alert('Dimension has no items');
				return;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			dimension.dimension = config.dimension;
			dimension.dimensionName = config.dimension;
			dimension.objectName = config.dimension;
			dimension.items = Ext.clone(config.items);

			return dimension;
		}();
	};

	api.dimension.objectNameClassMap[dimConf.indicator.objectName] = api.dimension.classes.Indicator;
	api.dimension.objectNameClassMap[dimConf.dataElement.objectName] = api.dimension.classes.DataElement;
	api.dimension.objectNameClassMap[dimConf.operand.objectName] = api.dimension.classes.Operand;
	api.dimension.objectNameClassMap[dimConf.dataSet.objectName] = api.dimension.classes.DataSet;
	api.dimension.objectNameClassMap[dimConf.period.objectName] = api.dimension.classes.Period;
	api.dimension.objectNameClassMap[dimConf.organisationUnit.objectName] = api.dimension.classes.OrganisationUnit;

	// Layout

	api.layout.classes.Layout = function(config) {
		var layout = {
			columns: null, // array of {dimension: <objectName>, items: [{id, name, code}]}

			rows: null, // array of {dimension: <objectName>, items: [{id, name, code}]}

			filters: null, // array of {dimension: <objectName>, items: [{id, name, code}]}

			showTotals: true, // boolean

			showSubTotals: true, // boolean

			hideEmptyRows: false, // boolean

			displayDensity: 'normal', // string - 'compact', 'normal', 'comfortable'

			fontSize: 'normal', // string - 'small', 'normal', 'large'

			digitGroupSeparator: 'space', // string - 'none', 'comma', 'space'

			userOrganisationUnit: false, // boolean

			userOrganisationUnitChildren: false, // boolean

			reportingPeriod: false, // boolean (report tables only)

			organisationUnit: false, // boolean (report tables only)

			parentOrganisationUnit: false // boolean (report tables only)
		};

		var validateConfig = function() {
			var removeEmptyDimensions,
				getValidatedAxis,
				a = [],
				objectNames = [],
				dimConf = pt.conf.finals.dimension;

			removeEmptyDimensions = function(axis) {
				if (!axis) {
					return;
				}

				for (var i = 0, dim, remove; i < axis.length; i++) {
					remove = false;
					dim = axis[i];

					if (dim.dimension !== pt.conf.finals.dimension.category.objectName) {
						if (!(Ext.isArray(dim.items) && dim.items.length)) {
							remove = true;
						}
						else {
							for (var j = 0; j < dim.items.length; j++) {
								if (!Ext.isString(dim.items[j].id)) {
									remove = true;
								}
							}
						}
					}

					if (remove) {
						axis = Ext.Array.erase(axis, i, 1);
						i = i - 1;
					}
				}

				return axis;
			};

			getValidatedAxis = function(axis) {
				if (!(axis && Ext.isArray(axis) && axis.length)) {
					return;
				}

				for (var i = 0, dimension; i < axis.length; i++) {
					dimension = axis[i];

					if (!(Ext.isObject(dimension) && Ext.isString(dimension.dimensionName))) {
						return;
					}
				}

				axis = removeEmptyDimensions(axis);

				return axis.length ? axis : null;
			};

			config.columns = getValidatedAxis(config.columns);
			config.rows = getValidatedAxis(config.rows);
			config.filters = getValidatedAxis(config.filters);

			// Config must be an object
			if (!(config && Ext.isObject(config))) {
				alert(pt.el + ': Layout config is not an object');
				return;
			}

			// At least one dimension specified as column or row
			if (!(config.columns || config.rows)) {
				alert(PT.i18n.at_least_one_dimension_must_be_specified_as_row_or_column);
				return;
			}

			// At least one period specified
			a = [].concat(config.columns, config.rows, config.filters);
			for (var i = 0; i < a.length; i++) {
				if (a[i]) {
					objectNames.push(a[i].dimension);
				}
			}

			if (!Ext.Array.contains(objectNames, dimConf.period.objectName)) {
				alert(PT.i18n.at_least_one_period_must_be_specified_as_column_row_or_filter);
				return;
			}

			// Properties
			if (!Ext.isBoolean(config.showTotals)) {
				config.showTotals = layout.showTotals;
			}
			if (!Ext.isBoolean(config.showSubTotals)) {
				config.showSubTotals = layout.showSubTotals;
			}
			if (!Ext.isBoolean(config.hideEmptyRows)) {
				config.hideEmptyRows = layout.hideEmptyRows;
			}

			if (!Ext.isString(config.displayDensity) || Ext.isEmpty(config.displayDensity)) {
				config.displayDensity = layout.displayDensity;
			}
			if (!Ext.isString(config.fontSize) || Ext.isEmpty(config.fontSize)) {
				config.fontSize = layout.fontSize;
			}
			if (!Ext.isString(config.digitGroupSeparator) || Ext.isEmpty(config.digitGroupSeparator)) {
				config.digitGroupSeparator = layout.digitGroupSeparator;
			}

			if (!Ext.isBoolean(config.userOrganisationUnit)) {
				config.userOrganisationUnit = layout.userOrganisationUnit;
			}
			if (!Ext.isBoolean(config.userOrganisationUnitChildren)) {
				config.userOrganisationUnitChildren = layout.userOrganisationUnitChildren;
			}
			if (!Ext.isBoolean(config.reportingPeriod)) {
				config.reportingPeriod = layout.reportingPeriod;
			}
			if (!Ext.isBoolean(config.organisationUnit)) {
				config.organisationUnit = layout.organisationUnit;
			}
			if (!Ext.isBoolean(config.parentOrganisationUnit)) {
				config.parentOrganisationUnit = layout.parentOrganisationUnit;
			}

			return true;
		};

		return function() {
			if (!validateConfig()) {
				return;
			}

			for (var key in config) {
				if (config.hasOwnProperty(key)) {
					layout[key] = config[key];
				}
			}

			return Ext.clone(layout);
		}();
	};

	return api;
};

PT.core.getInstance = function(config) {
	var pt = {};

	pt.baseUrl = config && config.baseUrl ? config.baseUrl : '../../';
	pt.el = config && config.el ? config.el : 'app';

	pt.conf = PT.core.getConfigs();
	pt.util = PT.core.getUtils(pt);
	pt.api = PT.core.getAPI(pt);

	return pt;
};

});
