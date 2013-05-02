DV.core = {
	instances: []
};

Ext.onReady( function() {

DV.core.getConfig = function() {
	var conf = {};

    conf.finals = {
        ajax: {
            path_visualizer: '../',
            path_api: '../../api/',
            path_commons: '../../dhis-web-commons-ajax-json/',
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
                name: DV.i18n.data,
                dimensionName: 'dx',
                objectName: 'dx'
            },
            category: {
				name: DV.i18n.categories,
				dimensionName: 'co',
                objectName: 'co',
			},
            indicator: {
                value: 'indicator',
                name: DV.i18n.indicator,
                dimensionName: 'dx',
                objectName: 'in'
            },
            dataElement: {
                value: 'dataelement',
                name: DV.i18n.data_element,
                dimensionName: 'dx',
                objectName: 'de'
            },
            dataSet: {
				value: 'dataset',
                name: DV.i18n.dataset,
                dimensionName: 'dx',
                objectName: 'ds'
			},
            period: {
                value: 'period',
                name: DV.i18n.period,
                dimensionName: 'pe',
                objectName: 'pe',
            },
            fixedPeriod: {
				value: 'periods'
			},
			relativePeriod: {
				value: 'relativePeriods'
			},
            organisationUnit: {
                value: 'organisationUnits',
                name: DV.i18n.organisation_units,
                dimensionName: 'ou',
                objectName: 'ou',
            },
            dimension: {
				value: 'dimension',
				objectName: 'dim'
			},
			value: {
				value: 'value'
			}
        },
        chart: {
            series: 'series',
            category: 'category',
            filter: 'filter',
            column: 'column',
            stackedColumn: 'stackedColumn',
            bar: 'bar',
            stackedBar: 'stackedBar',
            line: 'line',
            area: 'area',
            pie: 'pie'
        },
        data: {
			domain: 'domain_',
			targetLine: 'targetline_',
			baseLine: 'baseline_',
			trendLine: 'trendline_'
		},
		gui: {
			totals: 'totals',
			details: 'details'
		},
        image: {
            png: 'png',
            pdf: 'pdf'
        },
        cmd: {
            init: 'init_',
            none: 'none_',
			urlparam: 'id'
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

    conf.chart = {
        style: {
            inset: 30,
            fontFamily: 'Arial,Sans-serif,Lucida Grande,Ubuntu'
        },
        theme: {
            dv1: ['#94ae0a', '#0b3b68', '#a61120', '#ff8809', '#7c7474', '#a61187', '#ffd13e', '#24ad9a', '#a66111', '#414141', '#4500c4', '#1d5700']
        }
    };

    conf.statusbar = {
		icon: {
			error: 'error_s.png',
			warning: 'warning.png',
			ok: 'ok.png'
		}
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
        west_maxheight_accordion_indicator: 350,
        west_maxheight_accordion_dataelement: 350,
        west_maxheight_accordion_dataset: 350,
        west_maxheight_accordion_period: 513,
        west_maxheight_accordion_organisationunit: 500,
        west_maxheight_accordion_group: 350,
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

	return conf;
};

DV.core.getUtil = function(dv) {
	var util = {};

	util.window = {
		setAnchorPosition: function(w, target) {
			var vpw = dv.viewport.getWidth(),
				targetx = target ? target.getPosition()[0] : 4,
				winw = w.getWidth(),
				y = target ? target.getPosition()[1] + target.getHeight() + 4 : 33;

			if ((targetx + winw) > vpw) {
				w.setPosition((vpw - winw - 2), y);
			}
			else {
				w.setPosition(targetx, y);
			}
		},
		addHideOnBlurHandler: function(w) {
			var el = Ext.get(Ext.query('.x-mask')[0]);

			el.on('click', function() {
				if (w.hideOnBlur) {
					w.hide();
				}
			});

			w.hasHideOnBlurHandler = true;
		},
		addDestroyOnBlurHandler: function(w) {
			var el = Ext.get(Ext.query('.x-mask')[0]);

			el.on('click', function() {
				if (w.destroyOnBlur) {
					w.destroy();
				}
			});

			w.hasDestroyOnBlurHandler = true;
		}
	};

	util.mask = {
		showMask: function(cmp, str) {
			if (DV.mask) {
				DV.mask.destroy();
			}
			DV.mask = new Ext.LoadMask(cmp, {msg: str});
			DV.mask.show();
		},
		hideMask: function() {
			if (DV.mask) {
				DV.mask.hide();
			}
		}
	};

	util.chart = {
		createChart: function(layout, dv) {
			var options = layout.options,
				extendLayout,
				getSyncronizedXLayout,
				getParamString,
				validateResponse,
				extendResponse,
				getDefaultStore,
				getDefaultNumericAxis,
				getDefaultCategoryAxis,
				getDefaultSeries,
				getDefaultTrendLines,
				getDefaultTargetLine,
				getDefaultBaseLine,
				getDefaultTips,
				getDefaultTitle,
				getDefaultChart,
				validateUrl,
				generator = {},
				initialize,

				dimConf = dv.conf.finals.dimension;

			extendLayout = function(layout) {
				var xLayout = Ext.clone(layout),
					addDimensions,
					addDimensionNames,
					addSortedDimensions,
					addSortedFilterDimensions,
					addFilterItems;

				addDimensions = function() {
					xLayout.dimensions = [].concat(Ext.clone(xLayout.col) || [], Ext.clone(xLayout.row) || []);
				}();

				addDimensionNames = function() {
					var a = [],
						dimensions = Ext.clone(xLayout.dimensions) || [];

					for (var i = 0; i < dimensions.length; i++) {
						a.push(dimensions[i].dimensionName);
					}

					xLayout.dimensionNames = a;
				}();

				addSortedDimensions = function() {
					xLayout.sortedDimensions = dv.util.array.sortDimensions(Ext.clone(xLayout.dimensions) || []);
				}();

				addSortedFilterDimensions = function() {
						xLayout.sortedFilterDimensions = dv.util.array.sortDimensions(Ext.clone(xLayout.filter) || []);
				}();

				addNameItemsMap = function() {
					var map = {},
						dimensions = Ext.clone(xLayout.dimensions) || [];

					for (var i = 0, dim; i < dimensions.length; i++) {
						dim = dimensions[i];

						map[dim.dimensionName] = dim.items || [];
					}

					xLayout.nameItemsMap = map;
				}();

				addFilterItems = function() {
					if (Ext.isArray(xLayout.filter) && xLayout.filter.length) {
						xLayout.filterItems = [];

						for (var i = 0; i < xLayout.filter.length; i++) {
							xLayout.filterItems = xLayout.filterItems.concat(xLayout.filter[i].items);
						}
					}
				}();

				return xLayout;
			};

			getParamString = function(xLayout) {
				var sortedDimensions = xLayout.sortedDimensions,
					sortedFilterDimensions = xLayout.sortedFilterDimensions,
					paramString = '?';

				for (var i = 0, sortedDim; i < sortedDimensions.length; i++) {
					sortedDim = sortedDimensions[i];

					paramString += 'dimension=' + sortedDim.dimensionName;

					if (sortedDim.dimensionName !== dv.conf.finals.dimension.category.dimensionName) {
						paramString += ':' + sortedDim.items.join(';');
					}

					if (i < (sortedDimensions.length - 1)) {
						paramString += '&';
					}
				}

				if (sortedFilterDimensions) {
					for (var i = 0, sortedFilterDim; i < sortedFilterDimensions.length; i++) {
						sortedFilterDim = sortedFilterDimensions[i];

						paramString += '&filter=' + sortedFilterDim.dimensionName + ':' + sortedFilterDim.items.join(';');
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
							if (header.name === dv.conf.finals.dimension.category.dimensionName) {
								header.items = [].concat(response.metaData[dv.conf.finals.dimension.category.dimensionName]);
							}
							// periods
							else if (header.name === dv.conf.finals.dimension.period.dimensionName) {
								header.items = [].concat(response.metaData[dv.conf.finals.dimension.period.dimensionName]);
							}
							else {
								header.items = xLayout.nameItemsMap[header.name];
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
					var valueHeaderIndex = response.nameHeaderMap[dv.conf.finals.dimension.value.value].index,
						dimensionNames = xLayout.dimensionNames,
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

						response.idValueMap[id] = parseFloat(row[valueHeaderIndex]);
					}
				}();

				var getMinMax = function() {
					var valueIndex = response.nameHeaderMap.value.index,
						values = [];

					for (var i = 0; i < response.rows.length; i++) {
						values.push(parseFloat(response.rows[i][valueIndex]));
					}

					response.min = Ext.Array.min(values);
					response.max = Ext.Array.max(values);
				}();

				return response;
			};

			validateUrl = function(url) {
				if (!Ext.isString(url) || url.length > 2000) {
					var percent = ((url.length - 2000) / url.length) * 100;
					alert('Too many parameters selected. Please reduce the number of parameters by at least ' + percent.toFixed(0) + '%.');
					return;
				}

				return true;
			};

			getDefaultStore = function(xResponse, xLayout) {
				var pe = dv.conf.finals.dimension.period.dimensionName,

					data = [],
					series = xLayout.col[0].dimensionName === pe ? xResponse.metaData.pe : xLayout.col[0].items,
					categories = xLayout.row[0].dimensionName === pe ? xResponse.metaData.pe : xLayout.row[0].items,
					trendLineFields = [],
					targetLineFields = [],
					baseLineFields = [],
					store;

				// Data
				for (var i = 0, obj, category; i < categories.length; i++) {
					obj = {};
					category = categories[i];

					obj[dv.conf.finals.data.domain] = xResponse.metaData.names[category];

					for (var j = 0, id; j < series.length; j++) {
						id = series[j] + categories[i];
						obj[series[j]] = xResponse.idValueMap[id];
					}

					data.push(obj);
				}

				// Trend lines
				if (xLayout.options.showTrendLine) {
					for (var i = 0, regression, key; i < series.length; i++) {
						regression = new SimpleRegression();
						key = dv.conf.finals.data.trendLine + series[i];

						for (var j = 0; j < data.length; j++) {
							regression.addData(j, data[j][series[i]]);
						}

						for (var j = 0; j < data.length; j++) {
							data[j][key] = parseFloat(regression.predict(j).toFixed(1));
						}

						trendLineFields.push(key);
						xResponse.metaData.names[key] = DV.i18n.trend + ' (' + xResponse.metaData.names[series[i]] + ')';
					}
				}

				// Target line
				if (Ext.isNumber(xLayout.options.targetLineValue) || Ext.isNumber(parseFloat(xLayout.options.targetLineValue))) {
					for (var i = 0; i < data.length; i++) {
						data[i][dv.conf.finals.data.targetLine] = parseFloat(xLayout.options.targetLineValue);
					}

					targetLineFields.push(dv.conf.finals.data.targetLine);
				}

				// Base line
				if (Ext.isNumber(xLayout.options.baseLineValue) || Ext.isNumber(parseFloat(xLayout.options.baseLineValue))) {
					for (var i = 0; i < data.length; i++) {
						data[i][dv.conf.finals.data.baseLine] = parseFloat(xLayout.options.baseLineValue);
					}

					baseLineFields.push(dv.conf.finals.data.baseLine);
				}

				store = Ext.create('Ext.data.Store', {
					fields: function() {
						var fields = Ext.clone(series);
						fields.push(dv.conf.finals.data.domain);
						fields = fields.concat(trendLineFields, targetLineFields, baseLineFields);

						return fields;
					}(),
					data: data
				});

				store.rangeFields = series;
				store.domainFields = [dv.conf.finals.data.domain];
				store.trendLineFields = trendLineFields;
				store.targetLineFields = targetLineFields;
				store.baseLineFields = baseLineFields;
				store.numericFields = [].concat(store.rangeFields, store.trendLineFields, store.targetLineFields, store.baseLineFields);

				store.getMaximum = function() {
					var maximums = [];

					for (var i = 0; i < store.numericFields.length; i++) {
						maximums.push(store.max(store.numericFields[i]));
					}

					return Ext.Array.max(maximums);
				};

				store.getMinimum = function() {
					var minimums = [];

					for (var i = 0; i < store.numericFields.length; i++) {
						minimums.push(store.max(store.numericFields[i]));
					}

					return Ext.Array.min(minimums);
				};

				store.getMaximumSum = function() {
					var sums = [],
						recordSum = 0;

					store.each(function(record) {
						recordSum = 0;

						for (var i = 0; i < store.rangeFields.length; i++) {
							recordSum += record.data[store.rangeFields[i]];
						}

						sums.push(recordSum);
					});

					return Ext.Array.max(sums);
				};


console.log("data", data);
console.log("rangeFields", store.rangeFields);
console.log("domainFields", store.domainFields);
console.log("trendLineFields", store.trendLineFields);
console.log("targetLineFields", store.targetLineFields);
console.log("baseLineFields", store.baseLineFields);

				return store;
			};

			getDefaultNumericAxis = function(store, xResponse, xLayout) {
				var typeConf = dv.conf.finals.chart,
					minimum = store.getMinimum(),
					maximum,
					axis;

				// Set maximum if stacked + extra line
				if ((xLayout.type === typeConf.stackedColumn || xLayout.type === typeConf.stackedBar) &&
					(xLayout.options.showTrendLine || xLayout.options.targetLineValue || xLayout.options.baseLineValue)) {
					var a = [store.getMaximum(), store.getMaximumSum()];
					maximum = Math.ceil(Ext.Array.max(a) * 1.1);
					maximum = Math.floor(maximum / 10) * 10;
				}

				axis = {
					type: 'Numeric',
					position: 'left',
					fields: store.numericFields,
					minimum: minimum < 0 ? minimum : 0,
					label: {
						renderer: Ext.util.Format.numberRenderer('0,0')
					},
					grid: {
						odd: {
							opacity: 1,
							stroke: '#aaa',
							'stroke-width': 0.1
						},
						even: {
							opacity: 1,
							stroke: '#aaa',
							'stroke-width': 0.1
						}
					}
				};

				if (maximum) {
					axis.maximum = maximum;
				}

				if (xLayout.options.rangeAxisTitle) {
					axis.title = xLayout.options.rangeAxisTitle;
				}

				return axis;
			};

			getDefaultCategoryAxis = function(store, xLayout) {
				var axis = {
					type: 'Category',
					position: 'bottom',
					fields: store.domainFields,
					label: {
						rotate: {
							degrees: 330
						}
					}
				};

				if (xLayout.options.domainAxisTitle) {
					axis.title = xLayout.options.domainAxisTitle;
				}

				return axis;
			};

			getDefaultSeries = function(store, xResponse, xLayout) {
				var main = {
					type: 'column',
					axis: 'left',
					xField: store.domainFields,
					yField: store.rangeFields,
					style: {
						opacity: 0.8,
						lineWidth: 3
					},
					markerConfig: {
						type: 'circle',
						radius: 4
					},
					tips: getDefaultTips(),
					title: function() {
						var a = [];

						for (var i = 0; i < store.rangeFields.length; i++) {
							a.push(xResponse.metaData.names[store.rangeFields[i]]);
						}

						return a;
					}()
				};

				if (xLayout.options.showValues) {
					main.label = {
						display: 'outside',
						'text-anchor': 'middle',
						field: store.rangeFields,
						font: dv.conf.chart.style.fontFamily
					};
				}

				return main;
			};

			getDefaultTrendLines = function(store, xResponse) {
				var a = [];

				for (var i = 0; i < store.trendLineFields.length; i++) {
					a.push({
						type: 'line',
						axis: 'left',
						xField: store.domainFields,
						yField: store.trendLineFields[i],
						style: {
							opacity: 0.8,
							lineWidth: 3,
							'stroke-dasharray': 8
						},
						markerConfig: {
							type: 'circle',
							radius: 0
						},
						title: xResponse.metaData.names[store.trendLineFields[i]]
					});
				}

				return a;
			};

			getDefaultTargetLine = function(store, xLayout) {
				return {
					type: 'line',
					axis: 'left',
					xField: store.domainFields,
					yField: store.targetLineFields,
					style: {
						opacity: 1,
						lineWidth: 2,
						'stroke-width': 1,
						stroke: '#041423'
					},
					showMarkers: false,
					title: (Ext.isString(xLayout.options.targetLineTitle) ? xLayout.options.targetLineTitle : DV.i18n.target) + ' (' + xLayout.options.targetLineValue + ')'
				};
			};

			getDefaultBaseLine = function(store, xLayout) {
				return {
					type: 'line',
					axis: 'left',
					xField: store.domainFields,
					yField: store.baseLineFields,
					style: {
						opacity: 1,
						lineWidth: 2,
						'stroke-width': 1,
						stroke: '#041423'
					},
					showMarkers: false,
					title: (Ext.isString(xLayout.options.baseLineTitle) ? xLayout.options.baseLineTitle : DV.i18n.base) + ' (' + xLayout.options.baseLineValue + ')'
				};
			};

			getDefaultTips = function() {
				return {
					trackMouse: true,
					cls: 'dv-chart-tips',
					renderer: function(si, item) {
						this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">' + item.value[1] + '</div><div style="font-size:10px">' + si.data[dv.conf.finals.data.domain] + '</div></div>');
					}
				};
			};

			setDefaultTheme = function(store, xLayout) {
				var colors = dv.conf.chart.theme.dv1.slice(0, store.rangeFields.length);

				if (xLayout.options.targetLineValue || xLayout.options.baseLineValue) {
					colors.push('#051a2e');
				}

				if (xLayout.options.targetLineValue) {
					colors.push('#051a2e');
				}

				if (xLayout.options.baseLineValue) {
					colors.push('#051a2e');
				}

				Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
					constructor: function(config) {
						Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
							seriesThemes: colors,
							colors: colors
						}, config));
					}
				});
			};

			getDefaultLegend = function(store, xResponse) {
				var itemLength = 30,
					charLength = 7,
					numberOfItems = store.rangeFields.length,
					numberOfChars = 0,
					str = '',
					width,
					isVertical = false,
					position = 'top',
					padding = 0;

				for (var i = 0; i < store.rangeFields.length; i++) {
					str += xResponse.metaData.names[store.rangeFields[i]];
				}

				numberOfChars = str.length;

				width = (numberOfItems * itemLength) + (numberOfChars * charLength);

				if (width > dv.viewport.centerRegion.getWidth() - 50) {
					isVertical = true;
					position = 'right';
				}

				if (position === 'right') {
					padding = 5;
				}

				return Ext.create('Ext.chart.Legend', {
					position: position,
					isVertical: isVertical,
					labelFont: '13px ' + dv.conf.chart.style.fontFamily,
					boxStroke: '#ffffff',
					boxStrokeWidth: 0,
					padding: padding
				});
			};

			getDefaultTitle = function(store, xResponse, xLayout) {
				var text = '';

				if (Ext.isArray(xLayout.filterItems) && xLayout.filterItems.length) {
					for (var i = 0; i < xLayout.filterItems.length; i++) {
						text += xResponse.metaData.names[xLayout.filterItems[i]];
						text += i < xLayout.filterItems.length - 1 ? ', ' : '';
					}
				}

				if (xLayout.options.chartTitle) {
					text = xLayout.options.chartTitle;
				}

				return Ext.create('Ext.draw.Sprite', {
					type: 'text',
					text: text,
					font: 'bold 19px ' + dv.conf.chart.style.fontFamily,
					fill: '#111',
					height: 20,
					y: 	20
				});
			};

			getDefaultChartSizeHandler = function() {
				return function() {
					this.animate = false;
					this.setWidth(dv.viewport.centerRegion.getWidth());
					this.setHeight(dv.viewport.centerRegion.getHeight() - 25);
					this.animate = true;
				};
			};

			getDefaultTitlePositionHandler = function() {
				return function() {
					if (this.items) {
						var title = this.items[0],
							legend = this.legend,
							legendCenterX,
							titleX;

						if (this.legend.position === 'top') {
							legendCenterX = legend.x + (legend.width / 2);
							titleX = legendCenterX - (title.el.getWidth() / 2);
						}
						else {
							var legendWidth = legend ? legend.width : 0;
							titleX = (this.width / 2) - (title.el.getWidth() / 2);
						}

						title.setAttributes({
							x: titleX
						}, true);
					}
				};
			};

			getDefaultChart = function(store, axes, series, xResponse, xLayout) {
				var chart,
					config = {
						store: store,
						axes: axes,
						series: series,
						animate: true,
						shadow: false,
						insetPadding: 35,
						width: dv.viewport.centerRegion.getWidth(),
						height: dv.viewport.centerRegion.getHeight() - 25,
						theme: 'dv1'
					};

				// Legend
				if (!xLayout.options.hideChartLegend) {
					config.legend = getDefaultLegend(store, xResponse);

					if (config.legend.position === 'right') {
						config.insetPadding = 40;
					}
				}

				// Title
				if (!xLayout.options.hideChartTitle) {
					config.items = [getDefaultTitle(store, xResponse, xLayout)];
				}
				else {
					config.insetPadding = 10;
				}

				chart = Ext.create('Ext.chart.Chart', config);

				chart.setChartSize = getDefaultChartSizeHandler();
				chart.setTitlePosition = getDefaultTitlePositionHandler();

				chart.onViewportResize = function() {
					chart.setChartSize();
					chart.redraw();
					chart.setTitlePosition();
				};

				chart.on('afterrender', function() {
					chart.setTitlePosition();
				});

				return chart;
			};

			generator.column = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store, xResponse, xLayout),
					categoryAxis = getDefaultCategoryAxis(store, xLayout),
					axes = [numericAxis, categoryAxis],
					series = [getDefaultSeries(store, xResponse, xLayout)];

				// Options
				if (xLayout.options.showTrendLine) {
					series = getDefaultTrendLines(store, xResponse).concat(series);
				}

				if (xLayout.options.targetLineValue) {
					series.push(getDefaultTargetLine(store, xLayout));
				}

				if (xLayout.options.baseLineValue) {
					series.push(getDefaultBaseLine(store, xLayout));
				}

				// Theme
				setDefaultTheme(store, xLayout);

				return getDefaultChart(store, axes, series, xResponse, xLayout);
			};

			generator.stackedColumn = function(xResponse, xLayout) {
				var chart = this.column(xResponse, xLayout);

				for (var i = 0, item; i < chart.series.items.length; i++) {
					item = chart.series.items[i];

					if (item.type === dv.conf.finals.chart.column) {
						item.stacked = true;
					}
				}

				return chart;
			};

			generator.bar = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store, xResponse, xLayout),
					categoryAxis = getDefaultCategoryAxis(store, xLayout),
					axes,
					series = getDefaultSeries(store, xResponse, xLayout),
					trendLines,
					targetLine,
					baseLine,
					chart;

				// Axes
				numericAxis.position = 'bottom';
				categoryAxis.position = 'left';
				axes = [numericAxis, categoryAxis];

				// Series
				series.type = 'bar';
				series.axis = 'bottom';

				// Options
				if (xLayout.options.showValues) {
					series.label = {
						display: 'outside',
						'text-anchor': 'middle',
						field: store.rangeFields
					};
				}

				series = [series];

				if (xLayout.options.showTrendLine) {
					trendLines = getDefaultTrendLines(store, xResponse);

					for (var i = 0; i < trendLines.length; i++) {
						trendLines[i].axis = 'bottom';
						trendLines[i].xField = store.trendLineFields[i];
						trendLines[i].yField = store.domainFields;
					}

					series = trendLines.concat(series);
				}

				if (xLayout.options.targetLineValue) {
					targetLine = getDefaultTargetLine(store, xLayout);
					targetLine.axis = 'bottom';
					targetLine.xField = store.targetLineFields;
					targetLine.yField = store.domainFields;

					series.push(targetLine);
				}

				if (xLayout.options.baseLineValue) {
					baseLine = getDefaultBaseLine(store, xLayout);
					baseLine.axis = 'bottom';
					baseLine.xField = store.baseLineFields;
					baseLine.yField = store.domainFields;

					series.push(baseLine);
				}

				// Theme
				setDefaultTheme(store, xLayout);

				return getDefaultChart(store, axes, series, xResponse, xLayout);
			};

			generator.stackedBar = function(xResponse, xLayout) {
				var chart = this.bar(xResponse, xLayout);

				for (var i = 0, item; i < chart.series.items.length; i++) {
					item = chart.series.items[i];

					if (item.type === dv.conf.finals.chart.bar) {
						item.stacked = true;
					}
				}

				return chart;
			};

			generator.line = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store, xResponse, xLayout),
					categoryAxis = getDefaultCategoryAxis(store, xLayout),
					axes = [numericAxis, categoryAxis],
					series = [],
					colors = dv.conf.chart.theme.dv1.slice(0, store.rangeFields.length);

				// Series
				for (var i = 0, line; i < store.rangeFields.length; i++) {
					line = {
						type: 'line',
						axis: 'left',
						xField: store.domainFields,
						yField: store.rangeFields[i],
						style: {
							opacity: 0.8,
							lineWidth: 3
						},
						markerConfig: {
							type: 'circle',
							radius: 4
						},
						tips: getDefaultTips(),
						title: xResponse.metaData.names[store.rangeFields[i]]
					};

					//if (xLayout.options.showValues) {
						//line.label = {
							//display: 'rotate',
							//'text-anchor': 'middle',
							//field: store.rangeFields[i]
						//};
					//}

					series.push(line);
				}

				// Options, theme colors
				if (xLayout.options.showTrendLine) {
					series = getDefaultTrendLines(store, xResponse).concat(series);

					colors = colors.concat(colors);
				}

				if (xLayout.options.targetLineValue) {
					series.push(getDefaultTargetLine(store, xLayout));

					colors.push('#051a2e');
				}

				if (xLayout.options.baseLineValue) {
					series.push(getDefaultBaseLine(store, xLayout));

					colors.push('#051a2e');
				}

				// Theme
				Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
					constructor: function(config) {
						Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
							seriesThemes: colors,
							colors: colors
						}, config));
					}
				});

				return getDefaultChart(store, axes, series, xResponse, xLayout);
			};

			generator.area = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store, xResponse, xLayout),
					categoryAxis = getDefaultCategoryAxis(store, xLayout),
					axes = [numericAxis, categoryAxis],
					series = getDefaultSeries(store, xResponse, xLayout);

				series.type = 'area';
				series.style.opacity = 0.55;
				series.style.lineWidth = 0;
				delete series.label;
				delete series.tips;
				series = [series];

				// Options
				if (xLayout.options.showTrendLine) {
					series = getDefaultTrendLines(store, xResponse).concat(series);
				}

				if (xLayout.options.targetLineValue) {
					series.push(getDefaultTargetLine(store, xLayout));
				}

				if (xLayout.options.baseLineValue) {
					series.push(getDefaultBaseLine(store, xLayout));
				}

				// Theme
				setDefaultTheme(store, xLayout);

				return getDefaultChart(store, axes, series, xResponse, xLayout);
			};

			generator.pie = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					series = [{
						type: 'pie',
						field: store.rangeFields[0],
						lengthField: store.rangeFields[0],
						donut: 7,
						showInLegend: true,
						highlight: {
							segment: {
								margin: 5
							}
						},
						label: {
							field: dv.conf.finals.data.domain,
							display: 'middle',
							contrast: true,
							font: '14px ' + dv.conf.chart.style.fontFamily,
							renderer: function(value) {
								var record = store.getAt(store.findExact(dv.conf.finals.data.domain, value));

								return record.data[store.rangeFields[0]];
							}
						},
						style: {
							opacity: 0.8,
							stroke: '#555'
						},
						tips: {
							trackMouse: true,
							cls: 'dv-chart-tips',
							renderer: function(item) {
								this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">' + item.data[store.rangeFields[0]] + '</div><div style="font-size:10px">' + item.data[dv.conf.finals.data.domain] + '</div></div>');
							}
						}
					}],
					colors,
					chart;

				// Theme
				colors = dv.conf.chart.theme.dv1.slice(0, xResponse.nameHeaderMap[xLayout.row[0].dimensionName].items.length);

				Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
					constructor: function(config) {
						Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
							seriesThemes: colors,
							colors: colors
						}, config));
					}
				});

				// Chart
				chart = getDefaultChart(store, null, series, xResponse, xLayout);

				chart.legend.position = 'right';
				chart.legend.isVertical = true;
				chart.insetPadding = 40;
				chart.shadow = true;

				return chart;
			};

			initialize = function() {
				var url,
					xLayout,
					xResponse,
					chart;

				xLayout = extendLayout(layout);

				dv.paramString = getParamString(xLayout);
				url = dv.init.contextPath + '/api/analytics.json' + dv.paramString;

				if (!validateUrl(url)) {
					return;
				}

				dv.util.mask.showMask(dv.viewport);

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
						dv.util.mask.hideMask();
						alert(r.responseText);
					},
					success: function(r) {
						var html,
							response = Ext.decode(r.responseText);

						if (!validateResponse(response)) {
							dv.util.mask.hideMask();
							return;
						}

						//xLayout = getSyncronizedXLayout(xLayout, response);

						if (!xLayout) {
							dv.util.mask.hideMask();
							return;
						}

						xResponse = extendResponse(response, xLayout);

						chart = generator[xLayout.type](xResponse, xLayout);

						dv.viewport.centerRegion.removeAll(true);
						dv.viewport.centerRegion.add(chart);

						// After table success
						dv.util.mask.hideMask();

						if (dv.viewport.downloadButton) {
							dv.viewport.downloadButton.enable();
						}

						dv.chart = chart;
						dv.xLayout = xLayout;
						dv.xResponse = xResponse;

console.log("xResponse", xResponse);
console.log("xLayout", xLayout);
console.log("chart", chart);
					}
				});

			}();
		},

		loadChart: function(id) {
			if (!Ext.isString(id)) {
				alert('Invalid id');
				return;
			}

			Ext.Ajax.request({
				url: dv.baseUrl + '/api/charts/' + id + '.json?links=false',
				method: 'GET',
				failure: function(r) {
					dv.util.mask.hideMask();
					alert(r.responseText);
				},
				success: function(r) {
					var response = Ext.decode(r.responseText);
					dv.viewport.setFavorite(response);
				}
			});
		}
	};

	util.number = {
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
				if (!this.isInteger(values[i].value)) {
					return false;
				}
			}
			return true;
		},
		getChartAxisFormatRenderer: function() {
			return this.allValuesAreIntegers(DV.value.values) ? '0' : '0.0';
		}
	};

	util.value = {
		jsonfy: function(values) {
			var a = [];
			for (var i = 0; i < values.length; i++) {
				var v = {
					value: parseFloat(values[i][0]),
					data: values[i][1],
					period: values[i][2],
					organisationunit: values[i][3]
				};
				a.push(v);
			}
			return a;
		}
	};

	util.crud = {
		favorite: {
			create: function(fn, isupdate) {
				DV.util.mask.showMask(DV.cmp.favorite.window, DV.i18n.saving + '...');

				var p = DV.state.getParams();
				p.name = DV.cmp.favorite.name.getValue();

				if (isupdate) {
					p.uid = DV.store.favorite.getAt(DV.store.favorite.findExact('name', p.name)).data.id;
				}

				var url = DV.cmp.favorite.system.getValue() ? DV.conf.finals.ajax.favorite_addorupdatesystem : DV.conf.finals.ajax.favorite_addorupdate;
				Ext.Ajax.request({
					url: DV.conf.finals.ajax.path_visualizer + url,
					method: 'POST',
					params: p,
					success: function(r) {
						DV.store.favorite.load({callback: function() {
							var id = r.responseText;
							var name = DV.store.favorite.getAt(DV.store.favorite.findExact('id', id)).data.name;
							DV.c.currentFavorite = {
								id: id,
								name: name
							};
							DV.cmp.toolbar.share.xable();
							DV.util.mask.hideMask();
							if (fn) {
								fn();
							}
						}});
					}
				});
			},
			update: function(fn) {
				DV.util.crud.favorite.create(fn, true);
			},
			updateName: function(name) {
				if (DV.store.favorite.findExact('name', name) != -1) {
					return;
				}
				DV.util.mask.showMask(DV.cmp.favorite.window, DV.i18n.renaming + '...');
				var r = DV.cmp.favorite.grid.getSelectionModel().getSelection()[0];
				var url = DV.cmp.favorite.system.getValue() ? DV.conf.finals.ajax.favorite_addorupdatesystem : DV.conf.finals.ajax.favorite_addorupdate;
				Ext.Ajax.request({
					url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_updatename,
					method: 'POST',
					params: {uid: r.data.id, name: name},
					success: function() {
						DV.store.favorite.load({callback: function() {
							DV.cmp.favorite.rename.window.close();
							DV.util.mask.hideMask();
							DV.cmp.favorite.grid.getSelectionModel().select(DV.store.favorite.getAt(DV.store.favorite.findExact('name', name)));
							DV.cmp.favorite.name.setValue(name);
						}});
					}
				});
			},
			del: function(fn) {
				DV.util.mask.showMask(DV.cmp.favorite.window, DV.i18n.deleting + '...');
				var baseurl = DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_delete,
					selection = DV.cmp.favorite.grid.getSelectionModel().getSelection();
				Ext.Array.each(selection, function(item) {
					baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
				});
				Ext.Ajax.request({
					url: baseurl,
					method: 'POST',
					success: function() {
						DV.store.favorite.load({callback: function() {
							DV.util.mask.hideMask();
							if (fn) {
								fn();
							}
						}});
					}
				});
			}
		}
	};

	return util;
};

DV.core.getAPI = function(dv) {
	var api = {};

	api.Layout = function(config) {
		var col,
			row,
			filter,

			removeEmptyDimensions,
			getValidatedAxis,
			getValidatedOptions,
			validateLayout,

			defaultOptions = {
				showTrendLine: false,
				targetLineValue: null,
				targetLineTitle: null,
				baseLineValue: null,
				baseLineTitle: null,
				showValues: false,
				hideChartLegend: false,
				hideChartTitle: false,
				chartTitle: null,
				domainAxisTitle: null,
				rangeAxisTitle: null
			};

		removeEmptyDimensions = function(axis) {
			if (!axis) {
				return;
			}

			for (var i = 0, dimension, remove; i < axis.length; i++) {
				remove = false;
				dimension = axis[i];

				if (dimension.dimensionName !== dv.conf.finals.dimension.category.dimensionName) {
					if (!(Ext.isArray(dimension.items) && dimension.items.length)) {
						remove = true;
					}
					else {
						for (var j = 0; j < dimension.items.length; j++) {
							if (!Ext.isString(dimension.items[j])) {
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

		getValidatedOptions = function(options) {
			if (!(options && Ext.isObject(options))) {
				return defaultOptions;
			}

			options.showTrendLine = Ext.isDefined(options.showTrendLine) ? options.showTrendLine : defaultOptions.showTrendLine;
			options.targetLineValue = options.targetLineValue || defaultOptions.targetLineValue;
			options.targetLineTitle = options.targetLineTitle || defaultOptions.targetLineTitle;
			options.baseLineValue = options.baseLineValue || defaultOptions.baseLineValue;
			options.baseLineTitle = options.baseLineTitle || defaultOptions.baseLineTitle;
			options.showValues = Ext.isDefined(options.showValues) ? options.showValues : defaultOptions.showValues;
			options.hideChartLegend = Ext.isDefined(options.hideChartLegend) ? options.hideChartLegend : defaultOptions.hideChartLegend;
			options.hideChartTitle = Ext.isDefined(options.hideChartTitle) ? options.hideChartTitle : defaultOptions.hideChartTitle;
			options.chartTitle = options.chartTitle || defaultOptions.chartTitle;
			options.domainAxisTitle = options.domainAxisTitle || defaultOptions.domainAxisTitle;
			options.rangeAxisTitle = options.rangeAxisTitle || defaultOptions.rangeAxisTitle;

			return options;
		};

		validateLayout = function() {
			var a = [].concat(Ext.clone(col), Ext.clone(row), Ext.clone(filter)),
				dimensionNames = [],
				dimConf = dv.conf.finals.dimension;

			if (!(col || row)) {
				alert(DV.i18n.at_least_one_dimension_must_be_specified_as_row_or_column);
				return;
			}

			// Selected dimension names
			for (var i = 0; i < a.length; i++) {
				if (a[i]) {
					dimensionNames.push(a[i].dimensionName);
				}
			}

			if (!Ext.Array.contains(dimensionNames, dimConf.period.dimensionName)) {
				alert(DV.i18n.at_least_one_period_must_be_specified_as_column_row_or_filter);
				return;
			}

			return true;
		};

		return function() {
			var obj = {};

			if (!(config && Ext.isObject(config))) {
				console.log('Layout config is not an object');
				return;
			}

			col = getValidatedAxis(config.col);
			row = getValidatedAxis(config.row);
			filter = getValidatedAxis(config.filter);

			if (!validateLayout()) {
				return;
			}

			if (col) {
				obj.col = col;
			}
			if (row) {
				obj.row = row;
			}
			if (filter) {
				obj.filter = filter;
			}

			obj.type = config.type;
			obj.objects = config.objects;

			obj.options = getValidatedOptions(config.options);

			return obj;
		}();
	};

	return api;
};

DV.core.getInstance = function(config) {
	var dv = {};

	dv.baseUrl = config && config.baseUrl ? config.baseUrl : '../../';
	dv.el = config && config.el ? config.el : 'app';

	dv.conf = DV.core.getConfig();
	dv.util = DV.core.getUtil(dv);
	dv.api = DV.core.getAPI(dv);

	return dv;
};

});
