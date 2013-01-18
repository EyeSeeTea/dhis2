PT = {};

PT.core = {};

Ext.onReady( function() {

PT.core.getConfigs = function() {
	var conf = {};

	conf.finals = {
        ajax: {
            path_pivot: '../',
            path_pivot_static: 'dhis-web-pivot/',
            path_api: '../../api/',
            path_commons: '../../dhis-web-commons-ajax-json/',
            path_lib: '../../dhis-web-commons/javascripts/',
            path_images: 'images/',
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
                rawvalue: 'Data', //i18n PT.i18n.data,
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_ind_de
				}
            },
            indicator: {
                value: 'indicator',
                rawvalue: 'Indicator', //i18n PT.i18n.indicator,
                paramname: 'in'
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: 'Data element', //i18n PT.i18n.data_element,
                paramname: 'de'
            },
            dataset: {
				value: 'dataset',
                rawvalue: 'Data set', //i18n PT.i18n.dataset,
                paramname: 'ds'
			},
            period: {
                value: 'period',
                rawvalue: 'Period', //i18n PT.i18n.period,
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_period
				}
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: 'Organisation unit', //i18n PT.i18n.organisation_unit,
                paramname: 'ou',
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_orgunit
				}
            },
            organisationunitgroup: {
				value: 'organisationunitgroup'
			}
        },
        root: {
			id: 'root'
		}
	};

	conf.period = {
		relativeperiodunits: {
			reportingMonth: 1,
			last3Months: 3,
			last12Months: 12,
			reportingQuarter: 1,
			last4Quarters: 4,
			lastSixMonth: 1,
			last2SixMonths: 2,
			thisYear: 1,
			lastYear: 1,
			last5Years: 5
		},
		periodtypes: [
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
        west_fieldset_width: 410,
        west_width_padding: 18,
        west_fill: 117,
        west_fill_accordion_indicator: 77,
        west_fill_accordion_dataelement: 77,
        west_fill_accordion_dataset: 45,
        west_fill_accordion_fixedperiod: 77,
        west_fill_accordion_organisationunit: 103,
        west_maxheight_accordion_indicator: 478,
        west_maxheight_accordion_dataelement: 478,
        west_maxheight_accordion_dataset: 478,
        west_maxheight_accordion_relativeperiod: 423,
        west_maxheight_accordion_fixedperiod: 478,
        west_maxheight_accordion_organisationunit: 756,
        west_maxheight_accordion_organisationunitgroup: 298,
        west_maxheight_accordion_options: 449,
        east_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 55,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
        window_share_width: 500,
        grid_favorite_width: 420,
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

    util.getUrlParam = function(s) {
		var output = '';
		var href = window.location.href;
		if (href.indexOf('?') > -1 ) {
			var query = href.substr(href.indexOf('?') + 1);
			var query = query.split('&');
			for (var i = 0; i < query.length; i++) {
				if (query[i].indexOf('=') > -1) {
					var a = query[i].split('=');
					if (a[0].toLowerCase() === s) {
						output = a[1];
						break;
					}
				}
			}
		}
		return unescape(output);
	};

	util.viewport = {
		getSize: function() {
			return {x: PT.cmp.region.center.getWidth(), y: PT.cmp.region.center.getHeight()};
		},
		getXY: function() {
			return {x: PT.cmp.region.center.x + 15, y: PT.cmp.region.center.y + 43};
		},
		getPageCenterX: function(cmp) {
			return ((screen.width/2)-(cmp.width/2));
		},
		getPageCenterY: function(cmp) {
			return ((screen.height/2)-((cmp.height/2)-100));
		},
		resizeDimensions: function() {
			var a = [PT.cmp.dimension.indicator.panel, PT.cmp.dimension.dataelement.panel, PT.cmp.dimension.dataset.panel,
					PT.cmp.dimension.relativeperiod.panel, PT.cmp.dimension.fixedperiod.panel, PT.cmp.dimension.organisationUnit.panel,
					PT.cmp.dimension.organisationUnitGroup.panel, PT.cmp.options.panel];
			for (var i = 0; i < a.length; i++) {
				if (!a[i].collapsed) {
					a[i].fireEvent('expand');
				}
			}
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
		selectAll: function(a, s) {
			var array = [];
			a.store.each( function(r) {
				array.push({id: r.data.id, name: r.data.name});
			});
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

	util.treepanel = {
		getHeight: function() {
			var h1 = PT.cmp.region.west.getHeight();
			var h2 = PT.cmp.options.panel.getHeight();
			var h = h1 - h2 - PT.conf.layout.treepanel_fill_default;
			var mx = PT.conf.layout.treepanel_maxheight;
			var mn = PT.conf.layout.treepanel_minheight;
			return h > mx ? mx : h < mn ? mn : h;
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
		showMask: function(cmp, str) {
			if (PT.mask) {
				PT.mask.destroy();
			}
			PT.mask = new Ext.LoadMask(cmp, {msg: str});
			PT.mask.show();
		},
		hideMask: function() {
			if (PT.mask) {
				PT.mask.hide();
			}
		}
	};

	util.checkbox = {
		setRelativePeriods: function(rp) {
			if (rp) {
				for (var r in rp) {
					var cmp = PT.util.getCmp('checkbox[paramName="' + r + '"]');
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
			var a = PT.cmp.dimension.relativeperiod.checkbox;
			for (var i = 0; i < a.length; i++) {
				a[i].setValue(false);
			}
		},
		isAllFalse: function() {
			var a = PT.cmp.dimension.relativeperiod.checkbox;
			for (var i = 0; i < a.length; i++) {
				if (a[i].getValue()) {
					return false;
				}
			}
			return true;
		}
	};

	util.pivot = {
		getPivotTable: function(pt) {
			var extendSettings,
				extendResponse,
				extendDims,
				getDims,
				extendRowDims,
				getEmptyItem,
				getColItems,
				getRowItems,
				createTableArray,
				initialize;

			extendSettings = function(pt) {
				var settings = pt.settings,
					col = settings.col,
					row = settings.row;

				settings.dimensions = Ext.clone(col);
				Ext.apply(settings.dimensions, row);
			};

			extendResponse = function(pt) {
				var response = pt.response,
					settings = pt.settings,
					dimensions = settings.dimensions,
					headers = response.headers,
					header,
					rows = response.rows,
					settingsDims = [],
					items;

				response.nameHeaderMap = {};
				response.idValueMap = {};

				// Header items, size. Response nameHeaderMap.
				for (var i = 0; i < headers.length; i++) {
					header = headers[i];
					header.index = i;
					items = [];

					for (var j = 0; j < rows.length; j++) {
						items.push(rows[j][header.index]);
					}

					header.items = Ext.Array.unique(items);
					header.size = header.items.length;

					response.nameHeaderMap[header.name] = header;
				}

				// Header items. SettingsDims array.
				for (var dim in dimensions) {
					if (dimensions.hasOwnProperty(dim)) {
						settingsDims.push(dim);

						response.nameHeaderMap[dim].items = dimensions[dim];
					}
				}

				// Response idValueMap
				for (var i = 0, id, valueIndex = response.nameHeaderMap.value.index; i < rows.length; i++) {
					id = '';

					for (var j = 0, dimIndex; j < settingsDims.length; j++) {
						dimIndex = response.nameHeaderMap[settingsDims[j]].index;
						id += rows[i][dimIndex];
					}

					response.idValueMap[id] = rows[i][valueIndex];
				}
			};

			extendDims = function(aUniqueItems) {
				//aUniqueItems	= [ [de1, de2, de3],
				//					[p1],
				//					[ou1, ou2, ou3, ou4] ]

				var nCols = 1,
					aNumCols = [],
					aAccNumCols = [],
					aSpan = [],
					aGuiItems = [],
					aAllItems = [],
					aColIds = [];

				for (var i = 0, dim; i < aUniqueItems.length; i++) {
					nNumCols = aUniqueItems[i].length;

					aNumCols.push(nNumCols);
					nCols = nCols * nNumCols;
					aAccNumCols.push(nCols);
				}

			console.log("");
			console.log("aNumCols", aNumCols);
			console.log("nCols", nCols);
			console.log("aAccNumCols", aAccNumCols);

				//aNumCols		= [3, 1, 4]
				//nCols			= 12 (3 * 1 * 4)
				//aAccNumCols	= [3, 3, 12]

				for (var i = 0; i < aUniqueItems.length; i++) {
					aSpan.push(aNumCols[i] === 1 ? nCols : nCols / aAccNumCols[i]); //if one, span all
				}

			console.log("aSpan", aSpan);

				//aSpan			= [10, 2, 1]

				aGuiItems.push(aUniqueItems[0]);

				if (aUniqueItems.length > 1) {
					for (var i = 1, a, n; i < aUniqueItems.length; i++) {
						a = [];
						n = aNumCols[i] === 1 ? 1 : aAccNumCols[i-1];

						for (var j = 0; j < n; j++) {
							a = a.concat(aUniqueItems[i]);
						}

						aGuiItems.push(a);
					}
				}

			console.log("aGuiItems", aGuiItems);
				//aGuiItems	= [ [d1, d2, d3], (3)
				//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
				//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
				//		  	  ]


				for (var i = 0, dimItems, span; i < aUniqueItems.length; i++) {
					dimItems = [];
					span = aSpan[i];

					if (i === 0) {
						for (var j = 0; j < aUniqueItems[i].length; j++) {
							for (var k = 0; k < span; k++) {
								dimItems.push(aUniqueItems[i][j]);
							}
						}
					}
					else {
						var factor = nCols / aUniqueItems[i].length;

						for (var k = 0; k < factor; k++) {
							dimItems = dimItems.concat(aUniqueItems[i]);
						}
					}

					aAllItems.push(dimItems);
				}

			console.log("aAllItems", aAllItems);
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

			console.log("aColIds", aColIds);
				//aColIds	= [ aaaaaaaaBBBBBBBBccccccc, aaaaaaaaaccccccccccbbbbbbbbbb, ... ]


			console.log("");
				return {
					items: {
						unique: aUniqueItems,
						gui: aGuiItems,
						all: aAllItems
					},
					ids: aColIds,
					span: aSpan,
					dims: aUniqueItems.length,
					size: nCols
				};
			};

			getDims = function(pt) {
				var response = pt.response,
					settings = pt.settings,
					col = settings.col,
					row = settings.row,
					getUniqueColsArray,
					getUniqueRowsArray;

				getUniqueColsArray = function() {
					var a = [];

					for (var dim in col) {
						if (col.hasOwnProperty(dim)) {
							a.push(response.nameHeaderMap[dim].items);
						}
					}

					return a;
				};

				getUniqueRowsArray = function() {
					var a = [];

					for (var dim in row) {
						if (row.hasOwnProperty(dim)) {
							a.push(response.nameHeaderMap[dim].items);
						}
					}

					return a;
				};

				// aUniqueCols ->  [[p1, p2, p3], [ou1, ou2, ou3, ou4]]

				return {
					cols: extendDims(getUniqueColsArray()),
					rows: extendDims(getUniqueRowsArray())
				};
			};

			extendRowDims = function(rows) {
				var all = rows.items.all,
					allObjects = [];

				for (var i = 0, allRow; i < all.length; i++) {
					allRow = [];

					for (var j = 0; j < all[i].length; j++) {
						allRow.push({
							id: all[i][j]
						});
					}

					allObjects.push(allRow);
				}

				for (var i = 0; i < allObjects.length; i++) {
					for (var j = 0, object; j < allObjects[i].length; j += rows.span[i]) {
						object = allObjects[i][j];
						object.rowSpan = rows.span[i];
					}
				}

				rows.items.allObjects = allObjects;
			};

			getEmptyItem = function(pt) {
				return {
					colspan: pt.config.rows.dims,
					rowspan: pt.config.cols.dims,
					baseCls: 'empty'
				};
			};

			getColItems = function(pt) {
				var response = pt.response,
					cols = pt.config.cols,
					colItems = [];

				for (var i = 0, dimItems, colSpan; i < cols.dims; i++) {
					dimItems = cols.items.gui[i];
					colSpan = cols.span[i];

					for (var j = 0, id; j < dimItems.length; j++) {
						id = dimItems[j];
						colItems.push({
							html: response.metaData[id],
							colspan: colSpan,
							baseCls: 'dim'
						});

						if (i === 0 && j === (dimItems.length - 1)) {
							colItems.push({
								html: 'Total',
								rowspan: cols.dims,
								baseCls: 'dimtotal'
							});
						}
					}
				}

				return colItems;
			};

			getRowItems = function(pt) {
				var response = pt.response,
					rows = pt.config.rows,
					cols = pt.config.cols,
					size = rows.size,
					dims = rows.dims,
					allObjects = rows.items.allObjects,
					dimHtmlItems = [],
					valueItems = [],
					valueHtmlItems = [],
					totalRowItems = [],
					totalRowHtmlItems = [],
					totalColItems = [],
					totalColHtmlItems = [],
					grandTotalItem = 0,
					grandTotalHtmlItem;

				// Value items
				for (var i = 0, row; i < size; i++) {
					row = [];

					for (var j = 0, id, value, row; j < pt.config.cols.size; j++) {
						id = cols.ids[j] + rows.ids[i];
						value = parseFloat(response.idValueMap[id]);
						row.push(value);
					}

					valueItems.push(row);
				}

				// Value html items
				for (var i = 0, row; i < valueItems.length; i++) {
					row = [];

					for (var j = 0, id, value, cls; j < valueItems[i].length; j++) {
						id = cols.ids[j] + rows.ids[i];
						value = valueItems[i][j];
						//cls = value < 333 ? 'bad' : (value < 666 ? 'medium' : 'good'); //simplistic legendset

						row.push({
							id: id,
							value: value,
							html: value.toString(),
							baseCls: 'value'
							//cls: cls
						});
					}

					valueHtmlItems.push(row);
				}

				// Total row items
				for (var i = 0, rowSum; i < valueItems.length; i++) {
					rowSum = Ext.Array.sum(valueItems[i]);
					totalRowItems.push(rowSum);
				}

				// Total row html items
				for (var i = 0, rowSum; i < totalRowItems.length; i++) {
					rowSum = totalRowItems[i];

					totalRowHtmlItems.push({
						value: rowSum,
						html: rowSum.toString(),
						baseCls: 'valuetotal'
					});
				}

				// Total col items
				for (var i = 0, colSum; i < valueItems[0].length; i++) {
					colSum = 0;

					for (var j = 0; j < valueItems.length; j++) {
						colSum += valueItems[j][i];
					}

					totalColItems.push(colSum);
				}

				// Total col html items
				for (var i = 0, colSum; i < totalColItems.length; i++) {
					colSum = totalColItems[i];

					totalColHtmlItems.push({
						value: colSum,
						html: colSum.toString(),
						baseCls: 'valuetotal'
					});
				}

				// Grand total item
				grandTotalItem = Ext.Array.sum(totalColItems);

				// Grand total html item
				grandTotalHtmlItem = {
					value: grandTotalItem,
					html: grandTotalItem.toString(),
					baseCls: 'valuegrandtotal'
				};

				// GUI

				// Dim html items
				for (var i = 0; i < size; i++) {
					for (var j = 0, object; j < dims; j++) {
						object = allObjects[j][i];

						if (object.rowSpan) {
							dimHtmlItems.push({
								html: response.metaData[object.id],
								rowspan: object.rowSpan,
								baseCls: 'dim'
							});
						}
					}

					dimHtmlItems = dimHtmlItems.concat(valueHtmlItems[i]);
					dimHtmlItems = dimHtmlItems.concat(totalRowHtmlItems[i]);
				}

				// Final row
				dimHtmlItems.push({
					html: 'Total',
					colspan: rows.dims,
					baseCls: 'dimtotal'
				});

				dimHtmlItems = dimHtmlItems.concat(totalColHtmlItems);

				dimHtmlItems.push(grandTotalHtmlItem);

				return dimHtmlItems;
			};

			createTableArray = function(pt) {
				var rowItems = [],
					panel = Ext.create('Ext.panel.Panel', {
						renderTo: Ext.get('pivottable'),
						layout: {
							type: 'table',
							columns: pt.config.cols.size + pt.config.rows.dims + 1
						},
						defaults: {
							baseCls: 'td'
						}
					});

				return util;
			};

			initialize = function() {

				var params = '?&dimension=J5jldMd8OHv:CXw2yu5fodb,tDZVQ1WtwpA&dimension=de:fbfJHSPpUQD,cYeuwXTCPkU,Jtf34kNZhzP,hfdmMSPBgLG&dimension=pe:201201,201202,201203,201204';

				Ext.Ajax.request({
					method: 'GET',
					url: 'http://localhost:8080/api/analytics' + params,
					headers: {'Content-Type': 'application/json'},
					params: {
						filter: 'ou:ImspTQPwCqd',
						categories: false
					},
					success: function(r) {
						pt.response = Ext.decode(r.responseText);

						pt.settings = {
							col: {
								de: ['fbfJHSPpUQD', 'cYeuwXTCPkU', 'Jtf34kNZhzP', 'hfdmMSPBgLG']
							},
							row: {
								'J5jldMd8OHv': ['CXw2yu5fodb', 'tDZVQ1WtwpA'],
								pe: ['201201', '201202', '201203', '201204']
							}
						};

						extendSettings(pt);
						extendResponse(pt);

						pt.config = getDims(pt);
						console.log(pt);

						extendRowDims(pt.config.rows);

						var panel = createTableArray(pt);
					}
				});

			}();

			panel.add(getEmptyItem(pt));

			panel.add(getColItems(pt));

			panel.add(getRowItems(pt));
		}
	};

	return util;
};

PT.core.getInstance = function(config) {
	var pt = {};

	pt.baseUrl = config && config.baseUrl ? config.baseUrl : '../../';
	pt.el = config && config.el ? config.el : null;

	pt.conf = PT.core.getConfigs();
	pt.util = PT.core.getUtils(pt);

	return pt;
};

});
