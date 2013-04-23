DV.core = {};

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
            organisationUnitGroupSet: {
				value: 'organisationUnitGroupSets',
				objectName: 'ougs'
			},
            dataElementGroupSet: {
				value: 'dataElementGroupSets',
				objectName: 'degs'
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
            stackedcolumn: 'stackedcolumn',
            bar: 'bar',
            stackedbar: 'stackedbar',
            line: 'line',
            area: 'area',
            pie: 'pie'
        },
        data: {
			domain: 'domain_',
			targetline: 'targetline_',
			baseline: 'baseline_',
			trendline: 'trendline_'
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
	dim.objectNameMap[dim.organisationUnitGroupSet.objectName] = dim.organisationUnitGroupSet;
	dim.objectNameMap[dim.dataElementGroupSet.objectName] = dim.dataElementGroupSet;

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

	util.button = {
		type: {
			getValue: function() {
				for (var i = 0; i < DV.cmp.charttype.length; i++) {
					if (DV.cmp.charttype[i].pressed) {
						return DV.cmp.charttype[i].name;
					}
				}
			},
			setValue: function(type) {
				for (var i = 0; i < DV.cmp.charttype.length; i++) {
					DV.cmp.charttype[i].toggle(DV.cmp.charttype[i].name === type);
				}
			},
			toggleHandler: function(b) {
				if (!b.pressed) {
					b.toggle();
				}
			}
		}
	};

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

	util.dimension = {
		indicator: {
			getRecords: function() {
				var a = [];
				DV.cmp.dimension.indicator.selected.store.each( function(r) {
					a.push({id: r.data.id, name: r.data.name});
				});
				return a;
			},
			getIds: function() {
				var obj = DV.c.indicator.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(obj[i].id);
				}
				return a;
			}
		},
		dataelement: {
			getRecords: function() {
				var a = [];
				DV.cmp.dimension.dataelement.selected.store.each( function(r) {
					a.push({id: r.data.id, name: r.data.name});
				});
				return a;
			},
			getIds: function() {
				var obj = DV.c.dataelement.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(obj[i].id);
				}
				return a;
			}
		},
		dataset: {
			getRecords: function() {
				var a = [];
				DV.cmp.dimension.dataset.selected.store.each( function(r) {
					a.push({id: r.data.id, name: r.data.name});
				});
				return a;
			},
			getIds: function() {
				var obj = DV.c.dataset.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(obj[i].id);
				}
				return a;
			}
		},
		data: {
			getRecords: function() {
				var records = DV.c.indicator.records;
				records = records.concat(DV.c.dataelement.records);
				records = records.concat(DV.c.dataset.records);
				return records;
			},
			getUrl: function(isFilter) {
				var obj = DV.c.indicator.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(DV.conf.finals.dimension.indicator.paramname + '=' + obj[i].id);
				}
				obj = DV.c.dataelement.records;
				for (var i = 0; i < obj.length; i++) {
					a.push(DV.conf.finals.dimension.dataelement.paramname + '=' + obj[i].id);
				}
				obj = DV.c.dataset.records;
				for (var i = 0; i < obj.length; i++) {
					a.push(DV.conf.finals.dimension.dataset.paramname + '=' + obj[i].id);
				}
				return (isFilter && a.length > 1) ? a.slice(0,1) : a;
			}
		},
		relativeperiod: {
			getRecordsByRelativePeriods: function(rp) {
				var a = [],
					count = 0;
				for (var key in rp) {
					if (rp[key] && Ext.isNumber(DV.conf.period.relativePeriods[key])) {
						count += DV.conf.period.relativePeriods[key];
					}
				}
				for (var i = 0; i < count; i++) {
					a.push({});
				}
				return a;
			},
			getIds: function() {
				var obj = DV.c.relativeperiod.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(obj[i].id);
				}
				return a;
			},
			getRelativePeriodObject: function() {
				var a = {},
					cmp = DV.cmp.dimension.relativeperiod.checkbox;
				Ext.Array.each(cmp, function(item) {
					a[item.relativePeriodId] = item.getValue();
				});
				return a;
			},
			relativePeriodObjectIsValid: function(obj) {
				for (var rp in obj) {
					if (obj[rp]) {
						return true;
					}
				}
				return false;
			}
		},
		fixedperiod: {
			getRecords: function() {
				var a = [];
				DV.cmp.dimension.fixedperiod.selected.store.each( function(r) {
					a.push({id: r.data.id, name: r.data.name});
				});
				return a;
			},
			getIds: function() {
				var obj = DV.c.fixedperiod.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(obj[i].id);
				}
				return a;
			}
		},
		period: {
			getRecords: function() {
				var a = DV.util.dimension.relativeperiod.getRecordsByRelativePeriods(DV.c.relativeperiod.rp);
				return a.concat(DV.c.fixedperiod.records);
			},
			getUrl: function() {
				var a = [],
					rp = DV.c.relativeperiod.rp,
					param;
				for (var key in rp) {
					if (rp.hasOwnProperty(key) && rp[key]) {
						key = DV.conf.period.relativePeriodsUrl[key] ? DV.conf.period.relativePeriodsUrl[key] : key;
						a.push(key + '=true');
					}
				}

				var array = DV.c.fixedperiod.records;
				for (var i = 0; i < array.length; i++) {
					a.push('p=' + array[i].id);
				}

				return a;
			}
		},
		organisationunit: {
			getRecords: function() {
				var a = [],
				tp = DV.cmp.dimension.organisationunit.treepanel,
				selection = tp.getSelectionModel().getSelection();
				if (!selection.length) {
					var root = tp.selectRootIf();
					selection = [root];
				}
				Ext.Array.each(selection, function(r) {
					a.push({id: r.data.id, name: r.data.text});
				});
				return a;
			},
			getUrl: function(isFilter) {
				var ou = DV.c.organisationunit,
					a = [];
				for (var i = 0; i < ou.records.length; i++) {
					a.push(DV.conf.finals.dimension.organisationunit.paramname + '=' + ou.records[i].id);
				}
				if (isFilter && a.length > 1) {
					a = a.slice(0,1);
				}
				return a;
			},
			getIds: function() {
				var obj = DV.c.organisationunit.records,
					a = [];
				for (var i = 0; i < obj.length; i++) {
					a.push(obj[i].id);
				}
				return a;
			},
			getGroupSetId: function() {
				var value = DV.cmp.dimension.organisationunitgroup.panel.groupsets.getValue();
				return !value || value === DV.i18n.none || value === DV.conf.finals.cmd.none ? null : value;
			}
		},
		panel: {
			setHeight: function(mx) {
				var h = DV.cmp.region.west.getHeight() - DV.conf.layout.west_fill;
				DV.cmp.dimension.panel.setHeight(h > mx ? mx : h);
			}
		}
	};

	util.notification = {
		error: function(title, text) {
			title = title || '';
			text = text || '';
			Ext.create('Ext.window.Window', {
				title: title,
				cls: 'dv-messagebox',
				iconCls: 'dv-window-title-messagebox',
				modal: true,
				width: 300,
				items: [
					{
						xtype: 'label',
						width: 40,
						text: text
					}
				]
			}).show();
			DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
			DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.error + '" style="padding:0 5px 0 0"/>' + text);
		},
		warning: function(text) {
			text = text || '';
			DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
			DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.warning + '" style="padding:0 5px 0 0"/>' + text);
		},
		ok: function() {
			DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
			DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.ok + '" style="padding:0 5px 0 0"/>&nbsp;&nbsp;');
		},
		interpretation: function(text) {
			DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
			DV.cmp.statusbar.panel.update('<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.ok + '" style="padding:0 5px 0 0"/>' + text);
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

DV.core.getAPI = function(pt) {
	var api = {};

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
