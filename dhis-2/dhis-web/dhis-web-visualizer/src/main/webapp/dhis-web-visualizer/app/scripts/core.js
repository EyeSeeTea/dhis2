DV.core = {
	instances: []
};

Ext.onReady( function() {

DV.core.getConfig = function() {
	var conf = {};

	conf.init = {
		example: {
			series: ['Series 1', 'Series 2', 'Series 3', 'Series 4'],
			category: ['Category 1', 'Category 2', 'Category 3'],
			filter: [DV.i18n.example_chart],
			values: [84, 73, 87, 82, 91, 69, 82, 75, 83, 76, 73, 85],
			setState: function() {
				DV.c.type = DV.conf.finals.chart.column;
				DV.c.dimension.series = DV.conf.finals.dimension.data.value;
				DV.c.dimension.category = DV.conf.finals.dimension.period.value;
				DV.c.dimension.filter = DV.conf.finals.dimension.organisationunit.value;
				DV.c.series = DV.c.data = {names: this.series};
				DV.c.category = DV.c.period = {names: this.category};
				DV.c.filter = DV.c.organisationunit = {names: this.filter};
				DV.c.targetlinevalue = 78;
				DV.c.targetlinelabel = 'Target label';
				DV.c.rangeaxislabel = 'Range axis label';
				DV.c.domainaxislabel = 'Domain axis label';
			},
			setValues: function() {
				var obj1 = {}, obj2 = {}, obj3 = {}, obj4 = {}, obj5 = {}, obj6 = {}, obj7 = {}, obj8 = {}, obj9 = {}, obj10 = {}, obj11 = {}, obj12 = {};
				var s = DV.c.dimension.series, c = DV.c.dimension.category;
				obj1[s] = this.series[0];
				obj1[c] = this.category[0];
				obj1.value = this.values[0];
				obj2[s] = this.series[1];
				obj2[c] = this.category[0];
				obj2.value = this.values[1];
				obj3[s] = this.series[2];
				obj3[c] = this.category[0];
				obj3.value = this.values[2];
				obj4[s] = this.series[3];
				obj4[c] = this.category[0];
				obj4.value = this.values[3];
				obj5[s] = this.series[0];
				obj5[c] = this.category[1];
				obj5.value = this.values[4];
				obj6[s] = this.series[1];
				obj6[c] = this.category[1];
				obj6[c] = this.category[1];
				obj6.value = this.values[5];
				obj7[s] = this.series[2];
				obj7[c] = this.category[1];
				obj7.value = this.values[6];
				obj8[s] = this.series[3];
				obj8[c] = this.category[1];
				obj8.value = this.values[7];
				obj9[s] = this.series[0];
				obj9[c] = this.category[2];
				obj9.value = this.values[8];
				obj10[s] = this.series[1];
				obj10[c] = this.category[2];
				obj10.value = this.values[9];
				obj11[s] = this.series[2];
				obj11[c] = this.category[2];
				obj11.value = this.values[10];
				obj12[s] = this.series[3];
				obj12[c] = this.category[2];
				obj12.value = this.values[11];
				DV.value.values = [obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9, obj10, obj11, obj12];
			}
		},
		ajax: {
			jsonfy: function(r) {
				r = Ext.JSON.decode(r.responseText);
				var obj = {
					system: {
						rootnodes: [],
						ougs: r.system.ougs
					},
					user: {
						id: r.user.id,
						isAdmin: r.user.isAdmin,
						ou: r.user.ou,
						ouc: r.user.ouc
					},
					contextPath: r.contextPath
				};
				for (var i = 0; i < r.system.rn.length; i++) {
					obj.system.rootnodes.push({id: r.system.rn[i][0], text: r.system.rn[i][1], level: 1});
				}
				return obj;
			}
		}
    };

    conf.finals = {
        ajax: {
            path_visualizer: '../',
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
            font: 'arial,sans-serif,ubuntu,consolas'
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

DV.core.getUtil = function() {
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

	util.tmp = {
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

				return response;
			};

			getDefaultStore = function(xResponse, xLayout) {
				var pe = dv.conf.finals.dimension.period.dimensionName,

					data = [],
					series = xLayout.col[0].dimensionName === pe ? xResponse.metaData.pe : xLayout.col[0].items,
					categories = xLayout.row[0].dimensionName === pe ? xResponse.metaData.pe : xLayout.row[0].items,
					trendLineFields = [],
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

				store = Ext.create('Ext.data.Store', {
					fields: function() {
						var fields = Ext.clone(series);
						fields.push(dv.conf.finals.data.domain);
						fields = fields.concat(trendLineFields);

						return fields;
					}(),
					data: data
				});

				store.rangeFields = series;
				store.domainFields = [dv.conf.finals.data.domain];
				store.trendLineFields = trendLineFields;

console.log("data", data);
console.log("rangeFields", store.rangeFields);
console.log("domainFields", store.domainFields)
console.log("trendLineFields", store.trendLineFields);

				return store;
			};

			getDefaultNumericAxis = function(store) {
				return  {
					type: 'Numeric',
					position: 'left',
					fields: store.rangeFields,
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
			};

			getDefaultCategoryAxis = function(store) {
				return {
					type: 'Category',
					position: 'bottom',
					fields: store.domainFields,
					label: {
						rotate: {
							degrees: 330
						}
					}
				};
			};

			getDefaultSeries = function(store, xResponse) {
				var main = {
					type: 'column',
					axis: 'left',
					xField: store.domainFields,
					yField: store.rangeFields,
					title: function() {
						var a = [];

						for (var i = 0, id; i < store.rangeFields.length; i++) {
							id = store.rangeFields[i];

							a.push(xResponse.metaData.names[id]);
						}

						return a;
					}(),
					style: {
						opacity: 0.8,
						lineWidth: 3
					},
					markerConfig: {
						type: 'circle',
						radius: 4
					}
				};

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
						//tips: DV.util.chart.def.series.getTips(),
						title: xResponse.metaData.names[store.trendLineFields[i]]
					});
				}

				return a;
			};

			getDefaultChart = function(store, axes, series) {
				return Ext.create('Ext.chart.Chart', {
					store: store,
					axes: axes,
					series: series,
					legend: {
						position: 'top',
						labelFont: '13px Arial',
						boxStroke: '#ffffff',
						boxStrokeWidth: 0,
						padding: 0
					},
					animate: true,
					shadow: false,
					insetPadding: 1,
					width: dv.viewport.centerRegion.getWidth(),
					height: dv.viewport.centerRegion.getHeight() - 75
				});
			};

			getTitle = function(store, xResponse, xLayout) {
				var typeConf = dv.conf.finals.chart,
					paddingLeft = '30px',
					textAlign = 'center',
					html = '';

				// Style
				if (xLayout.type === typeConf.bar || xLayout.type === typeConf.stackedBar) {
					paddingLeft = '85px';
				}
				else if (xLayout.type === typeConf.pie) {
					textAlign = 'left';
				}

				// Text
				if (Ext.isArray(xLayout.filterItems) && xLayout.filterItems.length) {
					for (var i = 0; i < xLayout.filterItems.length; i++) {
						html += xResponse.metaData.names[xLayout.filterItems[i]];
						html += i < xLayout.filterItems.length - 1 ? ', ' : '';
					}
				}

				if (xLayout.type === typeConf.pie) {
					html += ', ' + xResponse.metaData.names[xLayout.col[0].items[0]];
				}

				return {
					xtype: 'panel',
					width: '100%',
					bodyStyle: 'padding:10px 0 4px ' + paddingLeft + '; border:0 none; text-align:' + textAlign + '; font-weight:bold; font-size:20px',
					html: html
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

			generator.column = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store),
					categoryAxis = getDefaultCategoryAxis(store),
					axes = [numericAxis, categoryAxis],
					series = [getDefaultSeries(store, xResponse)];

				if (xLayout.options.showTrendLine) {
					series = getDefaultTrendLines(store, xResponse).concat(series);
				}
console.log("series", series);

				return getDefaultChart(store, axes, series);
			};

			generator.stackedColumn = function(xResponse, xLayout) {
				var chart = this.column(xResponse, xLayout);
				chart.series.items[0].stacked = true;

				return chart;
			};

			generator.bar = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store),
					categoryAxis = getDefaultCategoryAxis(store),
					axes,
					series = getDefaultSeries(store, xResponse),
					chart;

				numericAxis.position = 'bottom';
				categoryAxis.position = 'left';
				axes = [numericAxis, categoryAxis];

				series.type = 'bar';
				series.axis = 'bottom';
				series = [series];

				return getDefaultChart(store, axes, series);
			};

			generator.stackedBar = function(xResponse, xLayout) {
				var chart = this.bar(xResponse, xLayout);
				chart.series.items[0].stacked = true;

				return chart;
			};

			generator.line = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store),
					categoryAxis = getDefaultCategoryAxis(store),
					axes = [numericAxis, categoryAxis],
					series = [],
					chart;

				for (var i = 0; i < store.rangeFields.length; i++) {
					series.push({
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
						//tips: DV.util.chart.def.series.getTips()
						title: xResponse.metaData.names[store.rangeFields[i]]
					});
				}

				if (xLayout.options.showTrendLine) {
					series = getDefaultTrendLines(store, xResponse).concat(series);
				}
console.log("series", series);

				return getDefaultChart(store, axes, series);
			};

			generator.area = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					numericAxis = getDefaultNumericAxis(store),
					categoryAxis = getDefaultCategoryAxis(store),
					axes = [numericAxis, categoryAxis],
					series = getDefaultSeries(store, xResponse);

				series.type = 'area';
				series.style.opacity = 0.55;
				series.style.lineWidth = 0;
				series = [series];

				return getDefaultChart(store, axes, series);
			};

			generator.pie = function(xResponse, xLayout) {
				var store = getDefaultStore(xResponse, xLayout),
					series = [{
						type: 'pie',
						field: store.rangeFields[0],
						showInLegend: true,
						label: {
							field: dv.conf.finals.data.domain
						},
						highlight: {
							segment: {
								margin: 8
							}
						},
						style: {
							opacity: 0.9,
							stroke: '#555'
						}
						//tips: DV.util.chart.pie.series.getTips()
					}],
					chart = getDefaultChart(store, null, series);

				chart.legend.position = 'right';
				chart.legend.isVertical = true;

				chart.insetPadding = 20;
				chart.margin = '50 0 0 50';
				chart.shadow = true;
				chart.width = dv.viewport.centerRegion.getWidth() - 100;
				chart.height = dv.viewport.centerRegion.getHeight() - 200;

				return chart;
			};

			initialize = function() {
				var url,
					xLayout,
					xResponse,
					chart,
					title;

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

console.log("xResponse", xResponse);
console.log("xLayout", xLayout);

						chart = generator[xLayout.type](xResponse, xLayout);
console.log("chart", chart);
						title = getTitle(chart.store, xResponse, xLayout);

						dv.viewport.centerRegion.removeAll(true);
						dv.viewport.centerRegion.add([title, chart]);

						// After table success
						dv.util.mask.hideMask();

						if (dv.viewport.downloadButton) {
							dv.viewport.downloadButton.enable();
						}

						dv.chart = chart;
						dv.xLayout = xLayout;
						dv.xResponse = xResponse;
					}
				});

			}();
		}
	};

	util.chart = {
		def: {
			getChart: function(axes, series) {
				return Ext.create('Ext.chart.Chart', {
					animate: true,
					shadow: false,
					store: DV.store.chart,
					insetPadding: DV.conf.chart.style.inset,
					items: DV.c.hidesubtitle ? false : DV.util.chart.def.getTitle(),
					legend: DV.c.hidelegend ? false : DV.util.chart.def.getLegend(),
					axes: axes,
					series: series,
					//labels: ['nissa'],
					theme: 'dv1'
				});
			},
			getLegend: function(len) {
				len = len ? len : DV.store.chart.range.length;
				return {
					position: len > 5 ? 'right' : 'top',
					labelFont: '15px ' + DV.conf.chart.style.font,
					boxStroke: '#ffffff',
					boxStrokeWidth: 0,
					padding: 0
				};
			},
			getTitle: function() {
				return {
					type: 'text',
					text: DV.c.currentFavorite ? DV.c.currentFavorite.name + ' (' + DV.c.filter.names[0] + ')' : DV.c.filter.names[0],
					font: 'bold 15px ' + DV.conf.chart.style.font,
					fill: '#222',
					width: 300,
					height: 20,
					x: 28,
					y: 16
				};
			},
			label: {
				getCategory: function() {
					return {
						font: '14px ' + DV.conf.chart.style.font,
						rotate: {
							degrees: 330
						}
					};
				},
				getNumeric: function() {
					return {
						font: '13px ' + DV.conf.chart.style.font,
						renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
					};
				}
			},
			axis: {
				getNumeric: function(stacked) {
					var axis = {
						type: 'Numeric',
						position: 'left',
						title: DV.c.rangeaxislabel || false,
						labelTitle: {
							font: '17px ' + DV.conf.chart.style.font
						},
						minimum: 0,
						fields: stacked ? DV.c.series.names : DV.store.chart.range,
						label: DV.util.chart.def.label.getNumeric(),
						grid: {
							odd: {
								opacity: 1,
								fill: '#fefefe',
								stroke: '#aaa',
								'stroke-width': 0.1
							},
							even: {
								opacity: 1,
								fill: '#f1f1f1',
								stroke: '#aaa',
								'stroke-width': 0.1
							}
						}
					};
					if (DV.init.cmd === DV.conf.finals.cmd.init) {
						axis.maximum = 100;
						axis.majorTickSteps = 10;
					}
					return axis;
				},
				getCategory: function() {
					return {
						type: 'Category',
						position: 'bottom',
						title: DV.c.domainaxislabel || false,
						labelTitle: {
							font: '17px ' + DV.conf.chart.style.font
						},
						fields: DV.conf.finals.data.domain,
						label: DV.util.chart.def.label.getCategory()
					};
				}
			},
			series: {
				getTips: function() {
					return {
						trackMouse: true,
						cls: 'dv-chart-tips',
						renderer: function(si, item) {
							this.update('<span style="font-size:11px">' + si.data[DV.conf.finals.data.domain] + '</span>' + '<br/>' + '<b>' + item.value[1] + '</b>');
						}
					};
				},
				getTargetLine: function() {
					var title = DV.c.targetlinelabel || DV.i18n.target;
					title += ' (' + DV.c.targetlinevalue + ')';
					return {
						type: 'line',
						axis: 'left',
						xField: DV.conf.finals.data.domain,
						yField: DV.conf.finals.data.targetline,
						style: {
							opacity: 1,
							lineWidth: 3,
							'stroke-width': 2,
							stroke: '#041423'
						},
						showMarkers: false,
						title: title
					};
				},
				getBaseLine: function() {
					var title = DV.c.baselinelabel || DV.i18n.base;
					title += ' (' + DV.c.baselinevalue + ')';
					return {
						type: 'line',
						axis: 'left',
						xField: DV.conf.finals.data.domain,
						yField: DV.conf.finals.data.baseline,
						style: {
							opacity: 1,
							lineWidth: 3,
							stroke: '#041423'
						},
						showMarkers: false,
						title: title
					};
				},
				getTrendLineArray: function() {
					var a = [];
					for (var i = 0; i < DV.chart.trendline.length; i++) {
						a.push({
							type: 'line',
							axis: 'left',
							xField: DV.conf.finals.data.domain,
							yField: DV.chart.trendline[i].key,
							style: {
								opacity: 0.8,
								lineWidth: 3,
								'stroke-dasharray': 8
							},
							markerConfig: {
								type: 'circle',
								radius: 0
							},
							tips: DV.util.chart.def.series.getTips(),
							title: DV.chart.trendline[i].name
						});
					}
					return a;
				},
				setTheme: function() {
					var colors = DV.conf.chart.theme.dv1.slice(0, DV.c.series.names.length);
					if (DV.c.targetlinevalue || DV.c.baselinevalue) {
						colors.push('#051a2e');
					}
					if (DV.c.targetlinevalue) {
						colors.push('#051a2e');
					}
					if (DV.c.baselinevalue) {
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
				}
			}
		},
		bar: {
			label: {
				getCategory: function() {
					return {
						font: '14px ' + DV.conf.chart.style.font
					};
				}
			},
			axis: {
				getNumeric: function() {
					var num = DV.util.chart.def.axis.getNumeric();
					num.position = 'bottom';
					return num;
				},
				getCategory: function() {
					var cat = DV.util.chart.def.axis.getCategory();
					cat.position = 'left';
					cat.label = DV.util.chart.bar.label.getCategory();
					return cat;
				}
			},
			series: {
				getTips: function() {
					return {
						trackMouse: true,
						cls: 'dv-chart-tips',
						renderer: function(si, item) {
							this.update('<span style="font-size:11px">' + si.data[DV.conf.finals.data.domain] + '</span>' + '<br/>' + '<b>' + item.value[1] + '</b>');
						}
					};
				},
				getTargetLine: function() {
					var line = DV.util.chart.def.series.getTargetLine();
					line.axis = 'bottom';
					line.xField = DV.conf.finals.data.targetline;
					line.yField = DV.conf.finals.data.domain;
					return line;
				},
				getBaseLine: function() {
					var line = DV.util.chart.def.series.getBaseLine();
					line.axis = 'bottom';
					line.xField = DV.conf.finals.data.baseline;
					line.yField = DV.conf.finals.data.domain;
					return line;
				},
				getTrendLineArray: function() {
					var a = [];
					for (var i = 0; i < DV.chart.trendline.length; i++) {
						a.push({
							type: 'line',
							axis: 'bottom',
							xField: DV.chart.trendline[i].key,
							yField: DV.conf.finals.data.domain,
							style: {
								opacity: 0.8,
								lineWidth: 3
							},
							markerConfig: {
								type: 'circle',
								radius: 4
							},
							tips: DV.util.chart.bar.series.getTips(),
							title: DV.chart.trendline[i].name
						});
					}
					return a;
				}
			}
		},
		line: {
			series: {
				getArray: function() {
					var series = [];
					for (var i = 0; i < DV.c.series.names.length; i++) {
						var main = {
							type: 'line',
							axis: 'left',
							xField: DV.conf.finals.data.domain,
							yField: DV.c.series.names[i],
							style: {
								opacity: 0.8,
								lineWidth: 3
							},
							markerConfig: {
								type: 'circle',
								radius: 4
							},
							tips: DV.util.chart.def.series.getTips()
						};
						series.push(main);
					}
					return series;
				},
				setTheme: function() {
					var colors = DV.conf.chart.theme.dv1.slice(0, DV.c.series.names.length);
					if (DV.c.trendline) {
						colors = colors.concat(colors);
					}
					if (DV.c.targetlinevalue) {
						colors.push('#051a2e');
					}
					if (DV.c.baselinevalue) {
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
				}
			}
		},
		pie: {
			getTitle: function() {
				return [
					{
						type: 'text',
						text: DV.c.currentFavorite ? DV.c.currentFavorite.name + ' (' + DV.c.filter.names[0] + ')' : DV.c.filter.names[0],
						font: 'bold 15px ' + DV.conf.chart.style.font,
						fill: '#222',
						width: 300,
						height: 20,
						x: 28,
						y: 16
					},
					{
						type: 'text',
						text: DV.c.series.names[0],
						font: '13px ' + DV.conf.chart.style.font,
						fill: '#444',
						width: 300,
						height: 20,
						x: 28,
						y: 36
					}
				];
			},
			series: {
				getTips: function() {
					return {
						trackMouse: true,
						cls: 'dv-chart-tips-pie',
						renderer: function(item) {
							this.update('<span style="font-size:11px">' + item.data[DV.conf.finals.data.domain] + '</span>' + '<br/>' + '<b>' + item.data[DV.c.series.names[0]] + '</b>');
						}
					};
				},
				setTheme: function() {
					var colors = DV.conf.chart.theme.dv1.slice(0, DV.c.category.names.length);
					Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
						constructor: function(config) {
							Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
								seriesThemes: colors,
								colors: colors
							}, config));
						}
					});
				}
			}
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
				hideChartSubtitle: false,
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
			options.hideChartSubtitle = Ext.isDefined(options.hideChartSubtitle) ? options.hideChartSubtitle : defaultOptions.hideChartSubtitle;
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
