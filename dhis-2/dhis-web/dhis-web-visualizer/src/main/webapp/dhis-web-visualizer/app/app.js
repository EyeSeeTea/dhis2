DV.conf = {
    init: {
		example: {
			series: ['Series 1', 'Series 2', 'Series 3', 'Series 4'],
			category: ['Category 1', 'Category 2', 'Category 3'],
			filter: [DV.i18n.example_chart],
			values: [84, 77, 87, 82, 91, 69, 82, 78, 83, 76, 73, 85],
			setState: function() {
				DV.c.type = DV.conf.finals.chart.column;
				DV.c.dimension.series = DV.conf.finals.dimension.data.value;
				DV.c.dimension.category = DV.conf.finals.dimension.period.value;
				DV.c.dimension.filter = DV.conf.finals.dimension.organisationunit.value;
				DV.c.series = DV.c.data = {names: this.series};
				DV.c.category = DV.c.period = {names: this.category};
				DV.c.filter = DV.c.organisationunit = {names: this.filter};
				DV.c.targetlinevalue = 80;
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
				var obj = {system: {rootnode: {id: r.rn[0], name: r.rn[1], level: 1}, periods: {}, user: {id: r.user.id, isadmin: r.user.isAdmin, organisationunit: {id: r.user.ou[0], name: r.user.ou[1]}},organisationunitgroupsets:r.ougs}};
				for (var relative in r.p) {
					obj.system.periods[relative] = [];
					for (var i = 0; i < r.p[relative].length; i++) {
						obj.system.periods[relative].push({id: r.p[relative][i][0], name: r.p[relative][i][1]});
					}
				}
				return obj;
			}
		}
    },
    finals: {
        ajax: {
            path_visualizer: '../',
            path_commons: '../../dhis-web-commons-ajax-json/',
            path_api: '../../api/',
            path_portal: '../../dhis-web-portal/',
            path_images: 'images/',
            path_lib: '../../dhis-web-commons/javascripts/',
            initialize: 'initialize.action',
            redirect: 'redirect.action',
            data_get: 'getAggregatedValues.action',
            indicator_get: 'getIndicatorsMinified.action',
            indicatorgroup_get: 'getIndicatorGroupsMinified.action',
            dataelement_get: 'getDataElementsMinified.action',
            dataelementgroup_get: 'getDataElementGroupsMinified.action',
            dataelement_get: 'getDataElementsMinified.action',
            dataset_get: 'getDataSetsMinified.action',
            organisationunitgroupset_get: 'getOrganisationUnitGroupSets.action',
            organisationunitchildren_get: 'getOrganisationUnitChildren.action',
            favorite_addorupdate: 'addOrUpdateChart.action',
            favorite_addorupdatesystem: 'addOrUpdateSystemChart.action',            
            favorite_get: 'charts/',
            favorite_getall: 'getSystemAndCurrentUserCharts.action',
            favorite_delete: 'deleteCharts.action'
        },
        dimension: {
            data: {
                value: 'data',
                rawvalue: DV.i18n.data,
                warning: {
					filter: DV.i18n.wm_multiple_filter_ind_de
				}
            },
            indicator: {
                value: 'indicator',
                rawvalue: DV.i18n.indicator
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: DV.i18n.data_element
            },
            dataset: {
				value: 'dataset',
                rawvalue: DV.i18n.dataset
			},
            period: {
                value: 'period',
                rawvalue: DV.i18n.period,
                warning: {
					filter: DV.i18n.wm_multiple_filter_period
				}
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: DV.i18n.organisation_unit,
                warning: {
					filter: DV.i18n.wm_multiple_filter_orgunit
				}
            },
            organisationunitgroup: {
				value: 'organisationunitgroup'
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
        }
    },
    chart: {
        style: {
            inset: 30,
            font: 'arial,sans-serif,ubuntu,consolas'
        },
        theme: {
            dv1: ['#94ae0a', '#0b3b68', '#a61120', '#ff8809', '#7c7474', '#a61187', '#ffd13e', '#24ad9a', '#a66111', '#414141', '#4500c4', '#1d5700']
        }
    },
    statusbar: {
		icon: {
			error: 'error_s.png',
			warning: 'warning.png',
			ok: 'ok.png'
		}
	},
    layout: {
        west_width: 424,
        west_fieldset_width: 402,
        west_width_subtractor: 18,
        west_fill: 117,
        west_fill_accordion_indicator: 77,
        west_fill_accordion_dataelement: 77,
        west_fill_accordion_dataset: 45,
        west_fill_accordion_organisationunit: 75,
        west_maxheight_accordion_indicator: 450,
        west_maxheight_accordion_dataelement: 450,
        west_maxheight_accordion_dataset: 450,
        west_maxheight_accordion_period: 340,
        west_maxheight_accordion_organisationunit: 700,
        west_maxheight_accordion_options: 367,
        center_tbar_height: 31,
        east_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 55,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
        grid_favorite_width: 420,
        treepanel_minheight: 135,
        treepanel_maxheight: 400,
        treepanel_fill_default: 310,
        multiselect_minheight: 100,
        multiselect_maxheight: 250,
        multiselect_fill_default: 345,
        multiselect_fill_reportingrates: 315
    }
};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', DV.conf.finals.ajax.path_lib + 'ext-ux');
Ext.require('Ext.ux.form.MultiSelect');

Ext.onReady( function() {
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
    Ext.QuickTips.init();
    document.body.oncontextmenu = function(){return false;}; 
    
    Ext.Ajax.request({
        url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.initialize,
        success: function(r) {
            
    DV.init = DV.conf.init.ajax.jsonfy(r);    
    DV.init.initialize = function() {
		DV.c = DV.chart.model;
        DV.util.combobox.filter.category();
        
        DV.init.cmd = DV.util.getUrlParam(DV.conf.finals.cmd.urlparam) || DV.conf.finals.cmd.init;
        DV.exe.execute(DV.init.cmd);
    };
    
    DV.cmp = {
        region: {},
        charttype: [],
        settings: {},
        dimension: {
            indicator: {},
            dataelement: {},
            dataset: {},
            period: {
				checkbox: []
			},
            organisationunit: {}
        },
        options: {},
        toolbar: {
            menuitem: {}
        },
        statusbar: {},
        favorite: {
            rename: {}
        }
    };
    
    DV.util = {
        getCmp: function(q) {
            return DV.viewport.query(q)[0];
        },
        getUrlParam: function(s) {
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
        },
        viewport: {
            getSize: function() {
                return {x: DV.cmp.region.center.getWidth(), y: DV.cmp.region.center.getHeight()};
            },
            getXY: function() {
                return {x: DV.cmp.region.center.x + 15, y: DV.cmp.region.center.y + 43};
            },
            getPageCenterX: function(cmp) {
                return ((screen.width/2)-(cmp.width/2));
            },
            getPageCenterY: function(cmp) {
                return ((screen.height/2)-((cmp.height/2)-100));
            },
            resizeDimensions: function() {
				var a = [DV.cmp.dimension.indicator.panel, DV.cmp.dimension.dataelement.panel, DV.cmp.dimension.dataset.panel,
						DV.cmp.dimension.period.panel, DV.cmp.dimension.organisationunit.panel, DV.cmp.options.panel];
				for (var i = 0; i < a.length; i++) {
					if (!a[i].collapsed) {
						a[i].fireEvent('expand');
					}
				}
			}
        },
        multiselect: {
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
            },
            filterAvailable: function(a, s) {
                a.store.filterBy( function(r) {
                    var filter = true;
                    s.store.each( function(r2) {
                        if (r.data.id === r2.data.id) {
                            filter = false;
                        }
                    });
                    return filter;
                });
                a.store.sort('name', 'ASC');
            },
            setHeight: function(ms, panel, fill) {
				for (var i = 0; i < ms.length; i++) {
					ms[i].setHeight(panel.getHeight() - fill);
				}
			}
        },
        treepanel: {
			getHeight: function() {
				var h1 = DV.cmp.region.west.getHeight();
				var h2 = DV.cmp.options.panel.getHeight();
				var h = h1 - h2 - DV.conf.layout.treepanel_fill_default;
				var mx = DV.conf.layout.treepanel_maxheight;
				var mn = DV.conf.layout.treepanel_minheight;
				return h > mx ? mx : h < mn ? mn : h;
			}
		},
        button: {
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
        },
        store: {
            addToStorage: function(s, records) {
                s.each( function(r) {
                    if (!s.storage[r.data.id]) {
                        s.storage[r.data.id] = {id: r.data.id, name: DV.util.string.getEncodedString(r.data.name), parent: s.parent};
                    }
                });
                if (records) {
                    Ext.Array.each(records, function(r) {
                        if (!s.storage[r.data.id]) {
                            s.storage[r.data.id] = {id: r.data.id, name: DV.util.string.getEncodedString(r.data.name), parent: s.parent};
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
        },
        dimension: {
            indicator: {
                getObjects: function() {
                    var a = [];
                    DV.cmp.dimension.indicator.selected.store.each( function(r) {
                        a.push({id: r.data.id, name: DV.util.string.getEncodedString(r.data.name)});
                    });
                    return a;
                },
                getIds: function() {
					var obj = DV.c.indicator.objects,
						a = [];
					for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].id);
					}
					return a;
				}
            },
            dataelement: {
                getObjects: function() {
					var a = [];
					DV.cmp.dimension.dataelement.selected.store.each( function(r) {
						a.push({id: r.data.id, name: DV.util.string.getEncodedString(r.data.name)});
					});
					return a;
                },
                getIds: function() {
					var obj = DV.c.dataelement.objects,
						a = [];
					for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].id);
					}
					return a;
				}
            },
            dataset: {
                getObjects: function() {
					var a = [];
					DV.cmp.dimension.dataset.selected.store.each( function(r) {
						a.push({id: r.data.id, name: DV.util.string.getEncodedString(r.data.name)});
					});
					return a;
                },
                getIds: function() {
					var obj = DV.c.dataset.objects,
						a = [];
					for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].id);
					}
					return a;
				}
            },
            data: {
                getNames: function(exception, isFilter) {
					var obj = DV.c.data.objects,
						a = [];
                    for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].name);
					}
                    if (exception && isFilter && a.length > 1) {
						DV.chart.warnings.push(DV.i18n.wm_multiple_filter_ind_de_ds + ' ' + DV.i18n.wm_first_filter_used);
					}
					return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getUrl: function(isFilter) {
					var obj = DV.c.indicator.objects,
						a = [];
                    for (var i = 0; i < obj.length; i++) {
						a.push('indicatorIds=' + obj[i].id);
					}
					obj = DV.c.dataelement.objects;
                    for (var i = 0; i < obj.length; i++) {
						a.push('dataElementIds=' + obj[i].id);
					}
					obj = DV.c.dataset.objects;
                    for (var i = 0; i < obj.length; i++) {
						a.push('dataSetIds=' + obj[i].id);
					}
					return (isFilter && a.length > 1) ? a.slice(0,1) : a;
				}
            },
            period: {
                getObjects: function() {
                    var a = [],
                        cmp = DV.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            Ext.Array.each(DV.init.system.periods[item.paramName], function(item) {
                                a.push({id: item.id, name: DV.util.string.getEncodedString(item.name)});
                            });
                        }
                    });
                    return a;
                },
                getObjectsByRelativePeriods: function(rp) {
                    var relatives = [],
                        a = [];
                    for (var r in rp) {
                        if (rp[r]) {
                            relatives.push(r);
                        }
                    }
                    for (var i = 0; i < relatives.length; i++) {
                        var r = DV.init.system.periods[relatives[i]] || [];
                        for (var j = 0; j < r.length; j++) {
                            a.push({id: r[j].id, name: r[j].name});
                        }
                    }
                    return a;
                },
                getNames: function(exception, isFilter) {
					var obj = DV.c.period.objects,
						a = [];
                    for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].name);
					}
                    if (exception && isFilter && a.length > 1) {
						DV.chart.warnings.push(DV.i18n.wm_multiple_filter_period + ' ' + DV.i18n.wm_first_filter_used);
					}
					return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getUrl: function(isFilter) {
					var obj = DV.c.period.objects,
						a = [];
                    for (var i = 0; i < obj.length; i++) {
						a.push('periodIds=' + obj[i].id);
					}
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getIds: function() {
					var obj = DV.c.period.objects,
						a = [];
					for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].id);
					}
					return a;
				},
                getNameById: function(id) {
                    for (var obj in DV.init.system.periods) {
                        var a = DV.init.system.periods[obj];
                        for (var i = 0; i < a.length; i++) {
                            if (a[i].id == id) {
                                return a[i].name;
                            }
                        };
                    }
                },
                getRelativePeriodObject: function(exception) {
                    var a = {},
                        cmp = DV.cmp.dimension.period.checkbox,
                        valid = false;
                    Ext.Array.each(cmp, function(item) {
                        a[item.paramName] = item.getValue();
                        valid = item.getValue() ? true : valid;
                    });
                    if (exception && !valid) {
						DV.util.notification.error(DV.i18n.et_no_periods, DV.i18n.em_no_periods);
                    }
                    return a;
                }   
            },
            organisationunit: {
                getObjects: function() {
                    var a = [],
                        tp = DV.cmp.dimension.organisationunit.treepanel,
                        selection = tp.getSelectionModel().getSelection();
					if (!selection.length) {
						selection = [tp.getRootNode()];
						tp.selectRoot();
					}
					Ext.Array.each(selection, function(r) {
						a.push({id: r.data.id, name: DV.util.string.getEncodedString(r.data.text)});
					});
					return a;
                },
                getNames: function(exception, isFilter) {
					var ou = DV.c.organisationunit,
						a = [];
					if (ou.groupsetid) {
						var groups = DV.init.system.organisationunitgroupsets[ou.groupsetid];
						for (var i = 0; i < groups.length; i++) {
							a.push(groups[i].name);
						}
					}
					else {
						if (DV.c.userorganisationunit) {
							a.push(DV.init.system.user.organisationunit.name);
						}
						else {
							for (var i = 0; i < ou.objects.length; i++) {
								a.push(ou.objects[i].name);
							}
						}
					}
					if (exception && isFilter && a.length > 1) {
						DV.chart.warnings.push(DV.i18n.wm_multiple_filter_orgunit + ' ' + DV.i18n.wm_first_filter_used);
					}
					return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getUrl: function(isFilter) {
					var ou = DV.c.organisationunit,
						a = [];
					if (DV.c.userorganisationunit) {
						a.push('organisationUnitIds=' + DV.init.system.user.organisationunit.id);
					}
					else {
						for (var i = 0; i < ou.objects.length; i++) {
							a.push('organisationUnitIds=' + ou.objects[i].id);
						}
						if ((isFilter || ou.groupsetid) && a.length > 1) {
							a = a.slice(0,1);
						}
					}
					if (ou.groupsetid) {
						a.push('organisationUnitGroupSetId=' + ou.groupsetid);
					}
					return a;
                },
                getIds: function() {
					var obj = DV.c.organisationunit.objects,
						a = [];
					for (var i = 0; i < obj.length; i++) {
						a.push(obj[i].id);
					}
					return a;
				},
                getGroupSetId: function() {
					var value = DV.cmp.dimension.organisationunit.panel.groupsets.getValue();
					return !value || value === DV.i18n.none || value === DV.conf.finals.cmd.none ? null : value;
				},
				getGroupNameByGroupId: function(id) {
					var gs = DV.init.system.organisationunitgroupsets;
					for (var k in gs) {
						for (var i = 0; i < gs[k].length; i++) {
							if (gs[k][i].id == id) {
								return gs[k][i].name;
							}
						}
					}
					return null;
				}
            },
            panel: {
				setHeight: function(mx) {
					var h = DV.cmp.region.west.getHeight() - DV.conf.layout.west_fill;
					DV.cmp.dimension.panel.setHeight(h > mx ? mx : h);
				}
			}
        },
        notification: {
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
			}				
		},
        mask: {
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
        },
        chart: {
			def: {
				getChart: function(axes, series) {
					return Ext.create('Ext.chart.Chart', {
						animate: true,
						store: DV.store.chart,
						insetPadding: DV.conf.chart.style.inset,
						items: DV.c.hidesubtitle ? false : DV.util.chart.def.getTitle(),
						legend: DV.c.hidelegend ? false : DV.util.chart.def.getLegend(),
						axes: axes,
						series: series,
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
						text: DV.c.filter.names[0],
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
								this.update('' + item.value[1]);
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
								stroke: '#041423'
							},
							markerConfig: {
								type: 'circle',
								radius: 0
							},
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
							markerConfig: {
								type: 'circle',
								radius: 0
							},
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
									lineWidth: 3
								},
								markerConfig: {
									type: 'circle',
									radius: 4
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
								this.update('' + item.value[0]);
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
						var a = [];
						for (var i = 0; i < DV.c.series.names.length; i++) {
							a.push({
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
							});
						}
						return a;
					},
					setTheme: function() {
						var colors = DV.conf.chart.theme.dv1.slice(0, DV.c.series.names.length);
						if (DV.c.trendline) {
							colors = colors.concat(colors);
						}
						if (DV.c.targetlinevalue) {
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
                            text: DV.c.filter.names[0],
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
								this.update(item.data[DV.conf.finals.data.domain] + '<br/><b>' + item.data[DV.c.series.names[0]] + '</b>');
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
        },
        combobox: {
            filter: {
                category: function() {
                    var cbs = DV.cmp.settings.series,
                        cbc = DV.cmp.settings.category,
                        cbf = DV.cmp.settings.filter,
                        v = cbs.getValue(),
                        d = DV.conf.finals.dimension.data.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbc);
                    this.clearValue(v, cbf);
                    
                    cbc.filterArray = [!(v === d), !(v === p), !(v === o)];
                    cbc.store.filterBy( function(r) {
                        return cbc.filterArray[index++];
                    });
                    
                    if (!cbc.getValue() && cbf.getValue()) {
						cbc.setValue(this.getAutoSelectOption(cbs.getValue(), cbf.getValue()));
                    }
                    
                    this.filter();
                },                
                filter: function() {
                    var cbs = DV.cmp.settings.series,
                        cbc = DV.cmp.settings.category,
                        cbf = DV.cmp.settings.filter,
                        v = cbc.getValue(),
                        d = DV.conf.finals.dimension.data.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbf);
                        
                    cbf.filterArray = Ext.Array.clone(cbc.filterArray);
                    cbf.filterArray[0] = cbf.filterArray[0] ? !(v === d) : false;
                    cbf.filterArray[1] = cbf.filterArray[1] ? !(v === p) : false;
                    cbf.filterArray[2] = cbf.filterArray[2] ? !(v === o) : false;
                    
                    cbf.store.filterBy( function(r) {
                        return cbf.filterArray[index++];
                    });                    
                    
                    if (!cbf.getValue()) {
						cbf.setValue(this.getAutoSelectOption(cbs.getValue(), cbc.getValue()));
                    }
                },
                clearValue: function(v, cb, i, d) {
                    if (v === cb.getValue()) {
                        cb.clearValue();
                    }
                },
                getAutoSelectOption: function(o1, o2) {
					var a = [DV.conf.finals.dimension.data.value, DV.conf.finals.dimension.period.value, DV.conf.finals.dimension.organisationunit.value];
					for (var i = 0; i < a.length; i++) {
						if (a[i] != o1 && a[i] != o2) {
							return a[i];
						}
					}
				}
            }
        },
        checkbox: {
            setRelativePeriods: function(rp) {
                for (var r in rp) {
                    var cmp = DV.util.getCmp('checkbox[paramName="' + r + '"]');
                    if (cmp) {
                        cmp.setValue(rp[r]);
                    }
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
                return this.allValuesAreIntegers(DV.value.values) ? '0' : '0.0';
            }
        },
        variable: {
			hasValue: function(str) {
				return (str !== 0 && str !== '0' && str !== '');
			}
		},
       /*FIXME:This is probably not going to work as intended with UNICODE?*/
        string: {
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            }
        },
        value: {
            jsonfy: function(r) {
                r = Ext.JSON.decode(r.responseText),
                values = [];
                for (var i = 0; i < r.length; i++) {
					var t = r[i][1];
                    values.push({
						value: r[i][0],
						type: r[i][1] === 'in' ? DV.conf.finals.dimension.indicator.value :
							  r[i][1] === 'de' ? DV.conf.finals.dimension.dataelement.value :
							  r[i][1] === 'ds' ? DV.conf.finals.dimension.dataset.value : t,
						dataid: r[i][2],
						periodid: r[i][3],
						organisationunitid: r[i][4],
						organisationunitgroupid: r[i][5]
					});
                }
                return values;
            }
        },
        crud: {
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
                        success: function() {
                            DV.store.favorite.load({callback: function() {
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
                        url: DV.conf.finals.ajax.path_visualizer + url,
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
        }
    };
    
    DV.store = {
        dimension: function() {
            return Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    {id: DV.conf.finals.dimension.data.value, name: DV.conf.finals.dimension.data.rawvalue},
                    {id: DV.conf.finals.dimension.period.value, name: DV.conf.finals.dimension.period.rawvalue},
                    {id: DV.conf.finals.dimension.organisationunit.value, name: DV.conf.finals.dimension.organisationunit.rawvalue}
                ]
            });
        },
        indicator: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.indicator_get,
                    reader: {
                        type: 'json',
                        root: 'indicators'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: []
            })
        },
        dataelement: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataelement_get,
                    reader: {
                        type: 'json',
                        root: 'dataElements'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: []
            })
        },
        dataset: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataset_get,
                    reader: {
                        type: 'json',
                        root: 'dataSets'
                    }
                },
                storage: {},
                isloaded: false,
                listeners: {
                    load: function(s) {
						this.isloaded = true;
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: []
            })
        },
        chart: null,
        getChartStore: function(exe) {
            var keys = [];
            Ext.Array.each(DV.chart.data, function(item) {
				keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });
            this.chart = Ext.create('Ext.data.Store', {
                fields: keys,
                data: DV.chart.data
            });
            
            this.chart.range = keys.slice(0);
            for (var i = 0; i < this.chart.range.length; i++) {
                if (this.chart.range[i] === DV.conf.finals.data.domain) {
                    this.chart.range.splice(i, 1);
                }
            }
            
            if (exe) {
                DV.chart.getChart(true);
            }
            else {
                return this.chart;
            }
        },
        datatable: null,
        getDataTableStore: function(exe) {
            this.datatable = Ext.create('Ext.data.Store', {
                fields: [
					DV.c.dimension.series,
					DV.c.dimension.category,
                    'value'
                ],
                data: DV.value.values
            });
            
            if (exe) {
                DV.datatable.getDataTable(true);
            }
            else {
                return this.datatable;
            }
            
        },
        favorite: Ext.create('Ext.data.Store', {
            fields: ['id', 'name', 'lastUpdated', 'userId'],
            proxy: {
                type: 'ajax',
                url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_getall,
                reader: {
                    type: 'json',
                    root: 'charts'
                }
            },
            isloaded: false,
            sorting: {
                field: 'name',
                direction: 'ASC'
            },
            sortStore: function() {
                this.sort(this.sorting.field, this.sorting.direction);
            },
            listeners: {
                load: function(s) {
					s.isloaded = !s.isloaded ? true : false;
					
                    s.sortStore();
                    s.each(function(r) {
                        r.data.lastUpdated = r.data.lastUpdated.substr(0,16);
                        r.data.icon = '<img src="' + DV.conf.finals.ajax.path_images + 'favorite.png" />';
                        r.commit();
                    });
                }
            }
        }),
        groupset: Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			proxy: {
				type: 'ajax',
				url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.organisationunitgroupset_get,
				reader: {
					type: 'json',
					root: 'organisationUnitGroupSets'
				}
			},
			isloaded: false,
			listeners: {
				load: function() {
					this.isloaded = true;
					this.add({id: DV.conf.finals.cmd.none, name: DV.i18n.none, index: -1});
					this.sort('index', 'ASC');
				}
			}
		})
    };
    
    DV.state = {
        setChart: function(exe, id) {
			DV.chart.reset();
						
			if (id) {
                Ext.Ajax.request({
                    url: DV.conf.finals.ajax.path_api + DV.conf.finals.ajax.favorite_get + id + '.json?links=false',
                    scope: this,
                    success: function(r) {
						if (!this.validation.response(r)) {
							return;
						}
						
						var f = Ext.JSON.decode(r.responseText);
						
						if (!this.validation.favorite(f)) {
							return;
						}
						
                        DV.c.type = f.type.toLowerCase();
                        DV.c.dimension.series = f.series.toLowerCase();
                        DV.c.dimension.category = f.category.toLowerCase();
                        DV.c.dimension.filter = f.filter.toLowerCase();
                        
                        DV.c.indicator.objects = [];
                        DV.c.dataelement.objects = [];
                        DV.c.dataset.objects = [];
                        DV.c.period.objects = [];
                        DV.c.organisationunit.objects = [];
                        
                        if (f.indicators) {
							for (var i = 0; i < f.indicators.length; i++) {
								DV.c.indicator.objects.push({id: f.indicators[i].internalId, name: DV.util.string.getEncodedString(f.indicators[i].name)});
							}
						}
						
						if (f.dataElements) {
							for (var i = 0; i < f.dataElements.length; i++) {
								DV.c.dataelement.objects.push({id: f.dataElements[i].internalId, name: DV.util.string.getEncodedString(f.dataElements[i].name)});
							}
						}
						if (f.dataSets) {
							for (var i = 0; i < f.dataSets.length; i++) {
								DV.c.dataset.objects.push({id: f.dataSets[i].internalId, name: DV.util.string.getEncodedString(f.dataSets[i].name)});
							}
						}						
						DV.c.period.rp = f.relativePeriods;
						for (var i = 0; i < f.organisationUnits.length; i++) {
							DV.c.organisationunit.objects.push({id: f.organisationUnits[i].internalId, name: DV.util.string.getEncodedString(f.organisationUnits[i].name)});
						}
						DV.c.organisationunit.groupsetid = f.organisationUnitGroupSet ? f.organisationUnitGroupSet.internalId : null;
						
                        DV.c.hidesubtitle = f.hideSubtitle;
                        DV.c.hidelegend = f.hideLegend;
                        DV.c.trendline = f.regression;
                        DV.c.userorganisationunit = f.userOrganisationUnit;
                        DV.c.domainaxislabel = f.domainAxisLabel;
                        DV.c.rangeaxislabel = f.rangeAxisLabel;
                        DV.c.targetlinevalue = f.targetLineValue ? parseFloat(f.targetLineValue) : null;
                        DV.c.targetlinelabel = f.targetLineLabel ? f.targetLineLabel : null;
                        DV.c.baselinevalue = f.baseLineValue ? parseFloat(f.baseLineValue) : null;
                        DV.c.baselinelabel = f.baseLineLabel ? f.baseLineLabel : null;
                        
                        if (exe) {
							this.expandChart(exe, id);
						}
					}
				});
			}
			else {
				DV.c.type = DV.util.button.type.getValue();
				DV.c.dimension.series = DV.cmp.settings.series.getValue();
				DV.c.dimension.category = DV.cmp.settings.category.getValue();
				DV.c.dimension.filter = DV.cmp.settings.filter.getValue();
				DV.c.indicator.objects = DV.util.dimension.indicator.getObjects();
				DV.c.dataelement.objects = DV.util.dimension.dataelement.getObjects();
				DV.c.dataset.objects = DV.util.dimension.dataset.getObjects();
				DV.c.period.rp = DV.util.dimension.period.getRelativePeriodObject();
				DV.c.organisationunit.objects = DV.util.dimension.organisationunit.getObjects();
				DV.c.organisationunit.groupsetid = DV.util.dimension.organisationunit.getGroupSetId();
				this.setOptions();
                        
				if (exe) {
					this.expandChart(exe);
				}
			}
		},
		expandChart: function(exe, id) {
			DV.chart.warnings = [];
			
			if (!this.validation.dimensions()) {
				return;
			}
			
			DV.c.data = {};
			DV.c.data.objects = [];
			DV.c.data.objects = DV.c.data.objects.concat(DV.c.indicator.objects);
			DV.c.data.objects = DV.c.data.objects.concat(DV.c.dataelement.objects);
			DV.c.data.objects = DV.c.data.objects.concat(DV.c.dataset.objects);
			
			DV.c.period.objects = DV.util.dimension.period.getObjectsByRelativePeriods(DV.c.period.rp);
			
			if (!this.validation.objects()) {
				return;
			}
			
			DV.c.series = DV.c[DV.c.dimension.series];
			DV.c.category = DV.c[DV.c.dimension.category];
			DV.c.filter = DV.c[DV.c.dimension.filter];
			
			DV.c.series.dimension = DV.conf.finals.chart.series;
			DV.c.category.dimension = DV.conf.finals.chart.category;
			DV.c.filter.dimension = DV.conf.finals.chart.filter;
			
			DV.c.series.names = DV.util.dimension[DV.c.dimension.series].getNames(true);
			DV.c.category.names = DV.util.dimension[DV.c.dimension.category].getNames(true);
			DV.c.filter.names = DV.util.dimension[DV.c.dimension.filter].getNames(true, true);
			
			DV.c.series.url = DV.util.dimension[DV.c.dimension.series].getUrl();
			DV.c.category.url = DV.util.dimension[DV.c.dimension.category].getUrl();
			DV.c.filter.url = DV.util.dimension[DV.c.dimension.filter].getUrl(true);
			
			DV.c.indicator.ids = DV.util.dimension.indicator.getIds();
			DV.c.dataelement.ids = DV.util.dimension.dataelement.getIds();
			DV.c.dataset.ids = DV.util.dimension.dataset.getIds();
			DV.c.period.ids = DV.util.dimension.period.getIds();
			DV.c.organisationunit.ids = DV.util.dimension.organisationunit.getIds();
						
			if (!this.validation.categories()) {
				return;
			}
            
            this.validation.trendline();
            
            this.validation.targetline();
            
            this.validation.baseline();
            
            this.validation.render();
            
            if (id) {
				this.setUI();
			}
            
            if (exe) {
                DV.value.getValues(true);
            }
        },
        setOptions: function() {
            DV.c.hidesubtitle = DV.cmp.favorite.hidesubtitle.getValue();
            DV.c.hidelegend = DV.cmp.favorite.hidelegend.getValue();
            DV.c.trendline = DV.cmp.favorite.trendline.getValue();
            DV.c.userorganisationunit = DV.cmp.favorite.userorganisationunit.getValue();
            DV.c.domainaxislabel = DV.cmp.favorite.domainaxislabel.getValue();
            DV.c.rangeaxislabel = DV.cmp.favorite.rangeaxislabel.getValue();
            DV.c.targetlinevalue = parseFloat(DV.cmp.favorite.targetlinevalue.getValue());
            DV.c.targetlinelabel = DV.cmp.favorite.targetlinelabel.getValue();
            DV.c.baselinevalue = parseFloat(DV.cmp.favorite.baselinevalue.getValue());
            DV.c.baselinelabel = DV.cmp.favorite.baselinelabel.getValue();
		},
        getParams: function() {
            var p = {};
            p.type = DV.c.type.toUpperCase();
            p.series = DV.c.dimension.series.toUpperCase();
            p.category = DV.c.dimension.category.toUpperCase();
            p.filter = DV.c.dimension.filter.toUpperCase();
			p.hideSubtitle = DV.c.hidesubtitle;
			p.hideLegend = DV.c.hidelegend;
			p.trendLine = DV.c.trendline;
			p.userOrganisationUnit = DV.c.userorganisationunit;
			if (DV.c.domainaxislabel) {
				p.domainAxisLabel = DV.c.domainaxislabel;
			}
			if (DV.c.rangeaxislabel) {
				p.rangeAxisLabel = DV.c.rangeaxislabel;
			}
			if (DV.c.targetlinevalue) {
				p.targetLineValue = DV.c.targetlinevalue;
			}
			if (DV.c.targetlinelabel) {
				p.targetLineLabel = DV.c.targetlinelabel;
			}
			if (DV.c.baselinevalue) {
				p.baseLineValue = DV.c.baselinevalue;
			}
			if (DV.c.baselinelabel) {
				p.baseLineLabel = DV.c.baselinelabel;
			}
            p.indicatorIds = DV.c.indicator.ids;
            p.dataElementIds = DV.c.dataelement.ids;
            p.dataSetIds = DV.c.dataset.ids;
            p = Ext.Object.merge(p, DV.c.period.rp);
            p.organisationUnitIds = DV.c.organisationunit.ids;
            if (DV.c.organisationunit.groupsetid) {
				p.organisationUnitGroupSetId = DV.c.organisationunit.groupsetid;
			}
            return p;
        },
        setUI: function() {
			DV.util.button.type.setValue(DV.c.type);			
			DV.cmp.favorite.hidesubtitle.setValue(DV.c.hidesubtitle);
			DV.cmp.favorite.hidelegend.setValue(DV.c.hidelegend);
			DV.cmp.favorite.trendline.setValue(DV.c.trendline);
			DV.cmp.favorite.userorganisationunit.setValue(DV.c.userorganisationunit);
			DV.cmp.favorite.domainaxislabel.setValue(DV.c.domainaxislabel);
			DV.cmp.favorite.rangeaxislabel.setValue(DV.c.rangeaxislabel);
			DV.cmp.favorite.targetlinevalue.setValue(DV.c.targetlinevalue);
			DV.cmp.favorite.targetlinelabel.xable();
			DV.cmp.favorite.targetlinelabel.setValue(DV.c.targetlinelabel);
			DV.cmp.favorite.baselinevalue.setValue(DV.c.baselinevalue);
			DV.cmp.favorite.baselinelabel.xable();
			DV.cmp.favorite.baselinelabel.setValue(DV.c.baselinelabel);
			
			DV.cmp.settings.series.setValue(DV.conf.finals.dimension[DV.c.dimension.series].value);
			DV.util.combobox.filter.category();                        
			DV.cmp.settings.category.setValue(DV.conf.finals.dimension[DV.c.dimension.category].value);
			DV.util.combobox.filter.filter();                        
			DV.cmp.settings.filter.setValue(DV.conf.finals.dimension[DV.c.dimension.filter].value);
			
			DV.store.indicator.selected.removeAll();
			if (DV.c.indicator.objects) {
				DV.store.indicator.selected.add(DV.c.indicator.objects);
				DV.util.store.addToStorage(DV.store.indicator.available, DV.c.indicator.objects);
				DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
			}
			
			DV.store.dataelement.selected.removeAll();
			if (DV.c.dataelement.objects) {
				DV.store.dataelement.selected.add(DV.c.dataelement.objects);
				DV.util.store.addToStorage(DV.store.dataelement.available, DV.c.dataelement.objects);
				DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
			}
												
			DV.store.dataset.selected.removeAll();
			if (DV.c.dataset.objects) {
				DV.store.dataset.selected.add(DV.c.dataset.objects);
				DV.util.store.addToStorage(DV.store.dataset.available, DV.c.dataset.objects);
				DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
			}
			
			DV.util.checkbox.setRelativePeriods(DV.c.period.rp);
			
			DV.cmp.dimension.organisationunit.treepanel.addToStorage(DV.c.organisationunit.objects);
			
			if (DV.c.organisationunit.groupsetid) {
				if (DV.store.groupset.isloaded) {
					DV.cmp.dimension.organisationunit.panel.groupsets.setValue(DV.c.organisationunit.groupsetid);
				}
				else {
					DV.store.groupset.load({
						callback: function() {
							DV.cmp.dimension.organisationunit.panel.groupsets.setValue(DV.c.organisationunit.groupsetid);
						}
					});
				}
			}
			else {
				DV.cmp.dimension.organisationunit.panel.groupsets.setValue(DV.store.isloaded ? DV.conf.finals.cmd.none : DV.i18n.none);
			}
		},
        validation: {
			dimensions: function() {
				if (!DV.c.dimension.series || !DV.c.dimension.category || !DV.c.dimension.filter) {
					DV.util.notification.error(DV.i18n.et_invalid_dimension_setup, DV.i18n.em_invalid_dimension_setup);
					return false;
				}
				return true;
			},
			objects: function() {
				if (!DV.c.data.objects.length) {
					DV.util.notification.error(DV.i18n.et_no_indicators_dataelements_datasets, DV.i18n.em_no_indicators_dataelements_datasets);
					return false;
				}
				if (!DV.c.period.objects.length) {
					DV.util.notification.error(DV.i18n.et_no_periods, DV.i18n.em_no_periods);
					return false;
				}
				if (!DV.c.organisationunit.objects.length) {
					DV.util.notification.error(DV.i18n.et_no_orgunits, DV.i18n.em_no_orgunits);
					return false;
				}
				return true;
			},
			categories: function() {
				if (DV.c.category.names.length < 2 && (DV.c.type === DV.conf.finals.chart.line || DV.c.type === DV.conf.finals.chart.area)) {
					DV.util.notification.error(DV.i18n.et_line_area_categories, DV.i18n.em_line_area_categories);
					return false;
				}
				return true;
			},
			trendline: function() {
				if (DV.c.trendline) {
					var reasons = [];
					if (DV.c.type === DV.conf.finals.chart.stackedcolumn || DV.c.type === DV.conf.finals.chart.stackedbar || DV.c.type === DV.conf.finals.chart.area) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_stacked_chart);
						DV.c.trendline = false;
					}
					else if (DV.c.type === DV.conf.finals.chart.pie) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_pie_chart);
						DV.c.trendline = false;
					}
					
					if (DV.c.category.names.length < 2) {
						reasons.push(DV.i18n.wm_required_categories);
						DV.c.trendline = false;
					}
					
					if (reasons.length) {
						var text = DV.i18n.wm_trendline_deactivated + ' (';
						for (var i = 0; i < reasons.length; i++) {
							text += i > 0 ? ' + ' : '';
							text += reasons[i];
						}
						text += ').';
						DV.chart.warnings.push(text);
					}
				}
			},
			targetline: function() {
				if (DV.c.targetlinevalue) {
					var reasons = [];
					if (DV.c.type === DV.conf.finals.chart.stackedcolumn || DV.c.type === DV.conf.finals.chart.stackedbar || DV.c.type === DV.conf.finals.chart.area) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_stacked_chart);
						DV.c.targetlinevalue = null;
					}
					else if (DV.c.type === DV.conf.finals.chart.pie) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_pie_chart);
						DV.c.targetlinevalue = null;
					}
					
					if (DV.c.category.names.length < 2) {
						reasons.push(DV.i18n.wm_required_categories);
						DV.c.targetlinevalue = null;
					}
					
					if (reasons.length) {
						var text = DV.i18n.wm_targetline_deactivated + ' (';
						for (var i = 0; i < reasons.length; i++) {
							text += i > 0 ? ' + ' : '';
							text += reasons[i];
						}
						text += ').';
						DV.chart.warnings.push(text);
					}
				}
			},
			baseline: function() {
				if (DV.c.baselinevalue) {
					var reasons = [];
					if (DV.c.type === DV.conf.finals.chart.stackedcolumn || DV.c.type === DV.conf.finals.chart.stackedbar || DV.c.type === DV.conf.finals.chart.area) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_stacked_chart);
						DV.c.baselinevalue = null;
					}
					else if (DV.c.type === DV.conf.finals.chart.pie) {
						reasons.push(DV.i18n.wm_not_applicable + ' ' + DV.i18n.wm_pie_chart);
						DV.c.baselinevalue = null;
					}
					
					if (DV.c.category.names.length < 2) {
						reasons.push(DV.i18n.wm_required_categories);
						DV.c.baselinevalue = null;
					}
					
					if (reasons.length) {
						var text = DV.i18n.wm_baseline_deactivated + ' (';
						for (var i = 0; i < reasons.length; i++) {
							text += i > 0 ? ' + ' : '';
							text += reasons[i];
						}
						text += ').';
						DV.chart.warnings.push(text);
					}
				}
			},
			render: function() {
				if (!DV.c.isrendered) {
					DV.cmp.toolbar.datatable.enable();
					DV.c.isrendered = true;
				}
			},
			response: function(r) {
				if (!r.responseText) {
					DV.util.mask.hideMask();
					DV.util.notification.error(DV.i18n.et_invalid_uid, DV.i18n.em_invalid_uid);
					return false;
				}
				return true;
			},
			favorite: function(f) {				
				if (!f.organisationUnits || !f.organisationUnits.length) {
					alert(DV.i18n.favorite_no_orgunits);
					return false;
				}
				return true;
			},
			value: function() {
				if (!DV.value.values.length) {
					DV.util.mask.hideMask();
					DV.util.notification.error(DV.i18n.et_no_data, DV.i18n.em_no_data);
					return false;
				}
				return true;
			}
		}
    };
    
    DV.value = {
        values: [],
        getValues: function(exe) {
            DV.util.mask.showMask(DV.cmp.region.center, DV.i18n.loading);
            
            var params = [];
            params = params.concat(DV.c.data.url);
            params = params.concat(DV.c.period.url);
            params = params.concat(DV.c.organisationunit.url);
            
            var baseurl = DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.data_get;
            Ext.Array.each(params, function(item) {
                baseurl = Ext.String.urlAppend(baseurl, item);
            });
            
            Ext.Ajax.request({
                url: baseurl,
                disableCaching: false,
                success: function(r) {
                    DV.value.values = DV.util.value.jsonfy(r);                    
                    
                    if (!DV.state.validation.value()) {
						return;
					}
					
                    Ext.Array.each(DV.value.values, function(item) {
                        item[DV.conf.finals.dimension.data.value] = DV.util.string.getEncodedString(DV.store[item.type].available.storage[item.dataid].name);
                        item[DV.conf.finals.dimension.period.value] = DV.util.string.getEncodedString(DV.util.dimension.period.getNameById(item.periodid));
                        item[DV.conf.finals.dimension.organisationunit.value] = DV.util.variable.hasValue(item.organisationunitgroupid) ?
							DV.util.dimension.organisationunit.getGroupNameByGroupId(item.organisationunitgroupid) : DV.cmp.dimension.organisationunit.treepanel.findNameById(item.organisationunitid);
                        item[DV.conf.finals.dimension.organisationunitgroup.value] = DV.util.variable.hasValue(item.organisationunitgroupid) ? DV.util.dimension.organisationunit.getGroupNameByGroupId(item.organisationunitgroupid) : null;
                        item.value = parseFloat(item.value);
                    });
                    
                    if (exe) {
                        DV.chart.getData(true);
                    }
                    else {
                        return DV.value.values;
                    }
                }
            });
        }
    };
    
    DV.chart = {
		model: {
			type: DV.conf.finals.chart.column,
			dimension: {},
			indicator: {},
			dataelement: {},
			dataset: {},
			period: {},
			organisationunit: {},
			hidesubtitle: false,
			hidelegend: false,
			trendline: false,
			userorganisationunit: false,
			domainaxislabel: null,
			rangeaxislabel: null,
			targetlinevalue: null,
			targetlinelabel: null,
			baselinevalue: null,
			baselinelabel: null,
			isrendered: false
		},
		reset: function() {
			this.model.type = DV.conf.finals.chart.column;
			this.model.dimension = {};
			this.model.series = null;
			this.model.category = null;
			this.model.filter = null;
			this.model.indicator = {};
			this.model.dataelement = {};
			this.model.dataset = {};
			this.model.period = {};
			this.model.organisationunit = {};
			this.model.hidesubtitle = false;
			this.model.hidelegend = false;
			this.model.trendline = false;
			this.model.userorganisationunit = false;
			this.model.domainaxislabel = null;
			this.model.rangeaxislabel = null;
			this.model.targetlinevalue = null;
			this.model.targetlinelabel = null;
			this.model.baselinevalue = null;
			this.model.baselinelabel = null;
		},
        data: [],
        getData: function(exe) {
            this.data = [];
            Ext.Array.each(DV.c.category.names, function(item) {
                var obj = {};
                obj[DV.conf.finals.data.domain] = item;
                DV.chart.data.push(obj);
            });
            
            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.c.series.names.length; i++) {
                    item[DV.c.series.names[i]] = 0;
                }
            });

            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.c.series.names.length; i++) {
                    for (var j = 0; j < DV.value.values.length; j++) {
                        if (DV.value.values[j][DV.c.dimension.category] === item[DV.conf.finals.data.domain] && DV.value.values[j][DV.c.dimension.series] === DV.c.series.names[i]) {
							item[DV.c.series.names[i]] = DV.value.values[j].value;
                            break;
                        }
                    }
                }
            });
            
			if (DV.c.trendline) {
				DV.chart.trendline = [];
				for (var i = 0; i < DV.c.series.names.length; i++) {
					var s = DV.c.series.names[i],
						reg = new SimpleRegression();
					for (var j = 0; j < DV.chart.data.length; j++) {
						reg.addData(j, DV.chart.data[j][s]);
					}
					var key = DV.conf.finals.data.trendline + s;
					for (var j = 0; j < DV.chart.data.length; j++) {
						var n = reg.predict(j);
						DV.chart.data[j][key] = parseFloat(reg.predict(j).toFixed(1));
					}
					DV.chart.trendline.push({
						key: key,
						name: DV.i18n.trend + ' (' + s + ')'
					});
				}
			}

			if (DV.c.targetlinevalue) {
				Ext.Array.each(DV.chart.data, function(item) {
					item[DV.conf.finals.data.targetline] = DV.c.targetlinevalue;
				});
			}

			if (DV.c.baselinevalue) {
				Ext.Array.each(DV.chart.data, function(item) {
					item[DV.conf.finals.data.baseline] = DV.c.baselinevalue;
				});
			}
            
            if (exe) {
                DV.store.getChartStore(true);
            }
            else {
                return this.data;
            }
        },
        chart: null,
        getChart: function(exe) {
            this[this.model.type]();
            if (exe) {
                this.reload();
            }
            else {
                return this.chart;
            }
        },
        column: function(stacked) {
			var series = [];
			if (DV.c.trendline) {
				var a = DV.util.chart.def.series.getTrendLineArray();
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			series.push({
				type: 'column',
				axis: 'left',
				xField: DV.conf.finals.data.domain,
				yField: DV.c.series.names,
				stacked: stacked,
				style: {
					opacity: 0.8,
					stroke: '#333'
				},				
				tips: DV.util.chart.def.series.getTips()
			});
			if (DV.c.targetlinevalue) {
				series.push(DV.util.chart.def.series.getTargetLine());
			}
			if (DV.c.baselinevalue) {
				series.push(DV.util.chart.def.series.getBaseLine());
			}
			
			var axes = [];
			var numeric = DV.util.chart.def.axis.getNumeric(stacked);
			axes.push(numeric);
			axes.push(DV.util.chart.def.axis.getCategory());
			
			DV.util.chart.def.series.setTheme();
			this.chart = DV.util.chart.def.getChart(axes, series);
        },
        stackedcolumn: function() {
            this.column(true);
        },
        bar: function(stacked) {
			var series = [];
			if (DV.c.trendline) {
				var a = DV.util.chart.bar.series.getTrendLineArray();
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			series.push({
				type: 'bar',
				axis: 'bottom',
				xField: DV.conf.finals.data.domain,
				yField: DV.c.series.names,
				stacked: stacked,
				style: {
					opacity: 0.8,
					stroke: '#333'
				},
				tips: DV.util.chart.def.series.getTips()
			});
			if (DV.c.targetlinevalue) {
				series.push(DV.util.chart.bar.series.getTargetLine());
			}
			if (DV.c.baselinevalue) {
				series.push(DV.util.chart.bar.series.getBaseLine());
			}
			
			var axes = [];
			var numeric = DV.util.chart.bar.axis.getNumeric(stacked);
			axes.push(numeric);
			axes.push(DV.util.chart.bar.axis.getCategory());
			
			DV.util.chart.def.series.setTheme();			
			this.chart = DV.util.chart.def.getChart(axes, series);
        },
        stackedbar: function() {
            this.bar(true);
        },
        line: function() {
			var series = [];
			if (DV.c.trendline) {
				var a = DV.util.chart.def.series.getTrendLineArray();
				for (var i = 0; i < a.length; i++) {
					series.push(a[i]);
				}
			}
			series = series.concat(DV.util.chart.line.series.getArray());
			if (DV.c.targetlinevalue) {
				series.push(DV.util.chart.def.series.getTargetLine());
			}
			if (DV.c.baselinevalue) {
				series.push(DV.util.chart.def.series.getBaseLine());
			}
			
			var axes = [];
			var numeric = DV.util.chart.def.axis.getNumeric();
			axes.push(numeric);
			axes.push(DV.util.chart.def.axis.getCategory());
			
			DV.util.chart.line.series.setTheme();
			this.chart = DV.util.chart.def.getChart(axes, series);
        },
        area: function() {
			var series = [];
			series.push({
				type: 'area',
				axis: 'left',
				xField: DV.conf.finals.data.domain,
				yField: DV.c.series.names,
				style: {
					opacity: 0.65,
					stroke: '#555'
				}
			});
			
			var axes = [];
			var numeric = DV.util.chart.def.axis.getNumeric();
			axes.push(numeric);
			axes.push(DV.util.chart.def.axis.getCategory());
			
			DV.util.chart.line.series.setTheme();
			this.chart = DV.util.chart.def.getChart(axes, series);
        },
        pie: function() {
			DV.util.chart.pie.series.setTheme();
            this.chart = Ext.create('Ext.chart.Chart', {
                animate: true,
                shadow: true,
                store: DV.store.chart,
                insetPadding: 60,
                items: DV.c.hidesubtitle ? false : DV.util.chart.pie.getTitle(),
                legend: DV.c.hidelegend ? false : DV.util.chart.def.getLegend(DV.c.category.names.length),
                series: [{
                    type: 'pie',
                    field: DV.c.series.names[0],
                    showInLegend: true,
                    label: {
                        field: DV.conf.finals.data.domain
                    },
                    highlight: {
                        segment: {
                            margin: 10
                        }
                    },
                    style: {
                        opacity: 0.9,
						stroke: '#555'
                    },
                    tips: DV.util.chart.pie.series.getTips()
                }],
                theme: 'dv1'
            });
        },
        warnings: [],
        getWarnings: function() {
			var t = '';
			for (var i = 0; i < this.warnings.length; i++) {
				if (i > 0) {
					t += '<img src="' + DV.conf.finals.ajax.path_images + DV.conf.statusbar.icon.warning + '" style="padding:0 5px 0 8px" />';
				}
				t += this.warnings[i] + ' ';
			}
			return t;
		},
        reload: function() {
            DV.cmp.region.center.removeAll(true);
            DV.cmp.region.center.add(this.chart);
            
			DV.util.mask.hideMask();
            
            if (DV.chart.warnings.length) {
				DV.util.notification.warning(this.getWarnings());
			}
			else {
				DV.util.notification.ok();
			}
            
            if (DV.init.cmd !== DV.conf.finals.cmd.init) {
                DV.store.getDataTableStore(true);
            }
            
            DV.init.cmd = false;
        }
    };
    
    DV.datatable = {
        datatable: null,
        getDataTable: function(exe) {
            this.datatable = Ext.create('Ext.grid.Panel', {
                height: DV.util.viewport.getSize().y - 1,
                scroll: 'vertical',
                cls: 'dv-datatable',
                columns: [
                    {
                        text: DV.conf.finals.dimension[DV.c.dimension.series].rawvalue,
                        dataIndex: DV.conf.finals.dimension[DV.c.dimension.series].value,
                        width: 150,
                        height: DV.conf.layout.east_gridcolumn_height,
                        sortable: DV.c.dimension.series != DV.conf.finals.dimension.period.value
                    },
                    {
                        text: DV.conf.finals.dimension[DV.c.dimension.category].rawvalue,
                        dataIndex: DV.conf.finals.dimension[DV.c.dimension.category].value,
                        width: 150,
                        height: DV.conf.layout.east_gridcolumn_height,
                        sortable: DV.c.dimension.category != DV.conf.finals.dimension.period.value
                    },
                    {
                        text: DV.i18n.value,
                        dataIndex: 'value',
                        width: 80,
                        height: DV.conf.layout.east_gridcolumn_height
                    }
                ],
                store: DV.store.datatable
            });
            
            if (exe) {
                this.reload();
            }
            else {
                return this.datatable;
            }
        },
        reload: function() {
            DV.cmp.region.east.removeAll(true);
            DV.cmp.region.east.add(this.datatable);
        }            
    };
    
    DV.exe = {
		execute: function(cmd) {
			if (cmd) {
				if (cmd === DV.conf.finals.cmd.init) {
					this.init();
				}
				else {
					this.favorite(cmd);
				}
			}
			else {
				this.update();
			}
		},
		init: function() {
			DV.conf.init.example.setState();
			DV.conf.init.example.setValues();
			DV.chart.getData(true);
		},			
		update: function() {
			DV.state.setChart(true);
		},
		favorite: function(id) {
			DV.state.setChart(true, id);
		},
		datatable: function() {
			DV.store.getDataTableStore(true);
		}
    };
        
    DV.viewport = Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        isrendered: false,
        items: [
            {
                region: 'west',
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
                items: [
                    {
                        xtype: 'toolbar',
                        height: 45,
                        style: 'padding-top:1px; border-style:none',
                        defaults: {
                            height: 40,
                            toggleGroup: 'chartsettings',
                            handler: DV.util.button.type.toggleHandler,
                            listeners: {
                                afterrender: function(b) {
                                    if (b.xtype === 'button') {
                                        DV.cmp.charttype.push(b);
                                    }
                                }
                            }
                        },
                        items: [
                            {
                                xtype: 'label',
                                text: DV.i18n.chart_type,
                                style: 'font-size:11px; font-weight:bold; padding:13px 8px 0 10px'
                            },
                            {
								xtype: 'button',
                                icon: 'images/column.png',
                                name: DV.conf.finals.chart.column,
                                tooltip: DV.i18n.column_chart,
								width: 40,
                                pressed: true
                            },
                            {
								xtype: 'button',
                                icon: 'images/column-stacked.png',
                                name: DV.conf.finals.chart.stackedcolumn,
                                tooltip: DV.i18n.stacked_column_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/bar.png',
                                name: DV.conf.finals.chart.bar,
                                tooltip: DV.i18n.bar_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/bar-stacked.png',
                                name: DV.conf.finals.chart.stackedbar,
                                tooltip: DV.i18n.stacked_bar_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/line.png',
                                name: DV.conf.finals.chart.line,
                                tooltip: DV.i18n.line_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/area.png',
                                name: DV.conf.finals.chart.area,
                                tooltip: DV.i18n.area_chart,
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/pie.png',
                                name: DV.conf.finals.chart.pie,
                                tooltip: DV.i18n.pie_chart,
								width: 40
                            }
                        ]
                    },
                    {
                        xtype: 'toolbar',
                        id: 'chartsettings_tb',
                        height: 48,
                        items: [
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: DV.i18n.series,
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.series,
                                        emptyText: DV.i18n.series,
                                        queryMode: 'local',
                                        editable: false,
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.data.value,
                                        listeners: {
                                            added: function() {
                                                DV.cmp.settings.series = this;
                                            },
                                            select: function() {
                                                DV.util.combobox.filter.category();
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: DV.i18n.category,
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.category,
                                        emptyText: DV.i18n.category,
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.period.value,
                                        listeners: {
                                            added: function(cb) {
                                                DV.cmp.settings.category = this;
                                            },
                                            select: function(cb) {
                                                DV.util.combobox.filter.filter();
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Filter',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.filter,
                                        emptyText: DV.i18n.filter,
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.organisationunit.value,
                                        listeners: {
                                            added: function(cb) {
                                                DV.cmp.settings.filter = this;
                                            }
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        bodyStyle: 'border-style:none; border-top:2px groove #eee; padding:10px 10px 0 10px;',
                        layout: 'fit',
                        items: [
							{
								xtype: 'panel',
								layout: 'accordion',
								activeOnTop: true,
								cls: 'dv-accordion',
								bodyStyle: 'border:0 none',
								height: 430,
								items: [
									{
										title: '<div style="height:17px">' + DV.i18n.indicators + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'combobox',
												cls: 'dv-combo',
												style: 'margin-bottom:8px',
												width: DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor,
												valueField: 'id',
												displayField: 'name',
												fieldLabel: DV.i18n.select_group,
												labelStyle: 'padding-left:7px;',
												labelWidth: 90,
												editable: false,
												queryMode: 'remote',
												store: Ext.create('Ext.data.Store', {
													fields: ['id', 'name', 'index'],
													proxy: {
														type: 'ajax',
														url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.indicatorgroup_get,
														reader: {
															type: 'json',
															root: 'indicatorGroups'
														}
													},
													listeners: {
														load: function(s) {
															s.add({id: 0, name: DV.i18n.all_indicator_groups, index: -1});
															s.sort('index', 'ASC');
														}
													}
												}),
												listeners: {
													select: function(cb) {
														var store = DV.store.indicator.available;
														store.parent = cb.getValue();
														
														if (DV.util.store.containsParent(store)) {
															DV.util.store.loadFromStorage(store);
															DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
														}
														else {
															store.load({params: {id: cb.getValue()}});
														}
													}
												}
											},
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													{
														xtype: 'multiselect',
														name: 'availableIndicators',
														cls: 'dv-toolbar-multiselect-left',
														width: (DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor) / 2,
														valueField: 'id',
														displayField: 'name',
														queryMode: 'remote',
														store: DV.store.indicator.available,
														tbar: [
															{
																xtype: 'label',
																text: DV.i18n.available,
																cls: 'dv-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.select(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.selectAll(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
																}
															},
															' '
														],
														listeners: {
															added: function() {
																DV.cmp.dimension.indicator.available = this;
															},
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	DV.util.multiselect.select(this, DV.cmp.dimension.indicator.selected);
																}, this);
															}
														}
													},                                            
													{
														xtype: 'multiselect',
														name: 'selectedIndicators',
														cls: 'dv-toolbar-multiselect-right',
														width: (DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'local',
														store: DV.store.indicator.selected,
														tbar: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.unselectAll(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.unselect(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
																}
															},
															'->',
															{
																xtype: 'label',
																text: DV.i18n.selected,
																cls: 'dv-toolbar-multiselect-right-label'
															}
														],
														listeners: {
															added: function() {
																DV.cmp.dimension.indicator.selected = this;
															},
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	DV.util.multiselect.unselect(DV.cmp.dimension.indicator.available, this);
																}, this);
															}
														}
													}
												]
											}
										],
										listeners: {
											added: function() {
												DV.cmp.dimension.indicator.panel = this;
											},
											expand: function() {
												DV.util.dimension.panel.setHeight(DV.conf.layout.west_maxheight_accordion_indicator);
												DV.util.multiselect.setHeight(
													[DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected],
													DV.cmp.dimension.indicator.panel,
													DV.conf.layout.west_fill_accordion_indicator
												);
											}
										}
									},
									{
										title: '<div style="height:17px">' + DV.i18n.data_elements + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'combobox',
												cls: 'dv-combo',
												style: 'margin-bottom:8px',
												width: DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor,
												valueField: 'id',
												displayField: 'name',
												fieldLabel: DV.i18n.select_group,
												labelStyle: 'padding-left:7px;',
												labelWidth: 90,
												editable: false,
												queryMode: 'remote',
												store: Ext.create('Ext.data.Store', {
													fields: ['id', 'name', 'index'],
													proxy: {
														type: 'ajax',
														url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataelementgroup_get,
														reader: {
															type: 'json',
															root: 'dataElementGroups'
														}
													},
													listeners: {
														load: function(s) {
															s.add({id: 0, name: '[ All data element groups ]', index: -1});
															s.sort('index', 'ASC');
														}
													}
												}),
												listeners: {
													select: function(cb) {
														var store = DV.store.dataelement.available;
														store.parent = cb.getValue();
														
														if (DV.util.store.containsParent(store)) {
															DV.util.store.loadFromStorage(store);
															DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
														}
														else {
															store.load({params: {id: cb.getValue()}});
														}
													}
												}
											},                                    
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													Ext.create('Ext.ux.form.MultiSelect', {
														name: 'availableDataElements',
														cls: 'dv-toolbar-multiselect-left',
														width: (DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														queryMode: 'remote',
														store: DV.store.dataelement.available,
														tbar: [
															{
																xtype: 'label',
																text: DV.i18n.available,
																cls: 'dv-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.select(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.selectAll(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
																}
															},
															' '
														],
														listeners: {
															added: function() {
																DV.cmp.dimension.dataelement.available = this;
															},                                                                
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	DV.util.multiselect.select(this, DV.cmp.dimension.dataelement.selected);
																}, this);
															}
														}
													}),                                            
													{
														xtype: 'multiselect',
														name: 'selectedDataElements',
														cls: 'dv-toolbar-multiselect-right',
														width: (DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'remote',
														store: DV.store.dataelement.selected,
														tbar: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.unselectAll(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.unselect(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
																}
															},
															'->',
															{
																xtype: 'label',
																text: DV.i18n.selected,
																cls: 'dv-toolbar-multiselect-right-label'
															}
														],
														listeners: {
															added: function() {
																DV.cmp.dimension.dataelement.selected = this;
															},          
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	DV.util.multiselect.unselect(DV.cmp.dimension.dataelement.available, this);
																}, this);
															}
														}
													}
												]
											}
										],
										listeners: {
											added: function() {
												DV.cmp.dimension.dataelement.panel = this;
											},
											expand: function() {
												DV.util.dimension.panel.setHeight(DV.conf.layout.west_maxheight_accordion_dataelement);
												DV.util.multiselect.setHeight(
													[DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected],
													DV.cmp.dimension.dataelement.panel,
													DV.conf.layout.west_fill_accordion_dataelement
												);
											}
										}
									},
									{
										title: '<div style="height:17px">' + DV.i18n.reporting_rates + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													Ext.create('Ext.ux.form.MultiSelect', {
														name: 'availableDataSets',
														cls: 'dv-toolbar-multiselect-left',
														width: (DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														queryMode: 'remote',
														store: DV.store.dataset.available,
														tbar: [
															{
																xtype: 'label',
																text: DV.i18n.available,
																cls: 'dv-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.select(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.selectAll(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
																}
															},
															' '
														],
														listeners: {
															added: function() {
																DV.cmp.dimension.dataset.available = this;
															},                                                                
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	DV.util.multiselect.select(this, DV.cmp.dimension.dataset.selected);
																}, this);
															}
														}
													}),                                            
													{
														xtype: 'multiselect',
														name: 'selectedDataSets',
														cls: 'dv-toolbar-multiselect-right',
														width: (DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'remote',
														store: DV.store.dataset.selected,
														tbar: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.unselectAll(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	DV.util.multiselect.unselect(DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected);
																}
															},
															'->',
															{
																xtype: 'label',
																text: DV.i18n.selected,
																cls: 'dv-toolbar-multiselect-right-label'
															}
														],
														listeners: {
															added: function() {
																DV.cmp.dimension.dataset.selected = this;
															},          
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	DV.util.multiselect.unselect(DV.cmp.dimension.dataset.available, this);
																}, this);
															}
														}
													}
												]
											}
										],
										listeners: {
											added: function() {
												DV.cmp.dimension.dataset.panel = this;
											},
											expand: function() {
												DV.util.dimension.panel.setHeight(DV.conf.layout.west_maxheight_accordion_dataset);
												DV.util.multiselect.setHeight(
													[DV.cmp.dimension.dataset.available, DV.cmp.dimension.dataset.selected],
													DV.cmp.dimension.dataset.panel,
													DV.conf.layout.west_fill_accordion_dataset
												);
												
												if (!DV.store.dataset.available.isloaded) {
													DV.store.dataset.available.load();
												}
											}
										}
									},
									{
										title: '<div style="height:17px">' + DV.i18n.periods + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													{
														xtype: 'panel',
														layout: 'anchor',
														bodyStyle: 'border-style:none; padding:0 0 0 10px',
														defaults: {
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		DV.cmp.dimension.period.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: DV.i18n.months,
																cls: 'dv-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'lastMonth',
																boxLabel: DV.i18n.last_month
															},
															{
																xtype: 'checkbox',
																paramName: 'last12Months',
																boxLabel: DV.i18n.last_12_months,
																checked: true
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'anchor',
														bodyStyle: 'border-style:none; padding:0 0 0 32px',
														defaults: {
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		DV.cmp.dimension.period.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: DV.i18n.quarters,
																cls: 'dv-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'lastQuarter',
																boxLabel: DV.i18n.last_quarter
															},
															{
																xtype: 'checkbox',
																paramName: 'last4Quarters',
																boxLabel: DV.i18n.last_4_quarters
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'anchor',
														bodyStyle: 'border-style:none; padding:0 0 0 32px',
														defaults: {
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		DV.cmp.dimension.period.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: DV.i18n.six_months,
																cls: 'dv-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'lastSixMonth',
																boxLabel: DV.i18n.last_six_month
															},
															{
																xtype: 'checkbox',
																paramName: 'last2SixMonths',
																boxLabel: DV.i18n.last_two_six_month
															}
														]
													}
												]
											},
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													{
														xtype: 'panel',
														layout: 'anchor',
														bodyStyle: 'border-style:none; padding:5px 0 0 10px',
														defaults: {
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		DV.cmp.dimension.period.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'label',
																text: DV.i18n.years,
																cls: 'dv-label-period-heading'
															},
															{
																xtype: 'checkbox',
																paramName: 'thisYear',
																boxLabel: DV.i18n.this_year
															},
															{
																xtype: 'checkbox',
																paramName: 'lastYear',
																boxLabel: DV.i18n.last_year
															},
															{
																xtype: 'checkbox',
																paramName: 'last5Years',
																boxLabel: DV.i18n.last_5_years
															}
														]
													}
												]
											}
										],
										listeners: {
											added: function() {
												DV.cmp.dimension.period.panel = this;
											},
											expand: function() {
												DV.util.dimension.panel.setHeight(DV.conf.layout.west_maxheight_accordion_period);
											}
										}
									},
									{
										title: '<div style="height:17px">' + DV.i18n.organisation_units + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'combobox',
												cls: 'dv-combo',
												style: 'margin-bottom:8px',
												width: DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor,
												valueField: 'id',
												displayField: 'name',
												fieldLabel: DV.i18n.group_sets,
												labelWidth: 85,
												labelStyle: 'padding-left:7px;',
												editable: false,
												queryMode: 'remote',
												value: DV.i18n.none,
												store: DV.store.groupset,
												listeners: {
													added: function() {
														this.up('panel').groupsets = this;
													}
												}
											},
											{
												xtype: 'treepanel',
												cls: 'dv-tree',
												width: DV.conf.layout.west_fieldset_width - DV.conf.layout.west_width_subtractor,
												autoScroll: true,
												multiSelect: true,
												isrendered: false,
												storage: {},
												addToStorage: function(objects) {
													for (var i = 0; i < objects.length; i++) {
														this.storage[objects[i].id] = objects[i];
													}
												},
												selectRoot: function() {
													if (this.isrendered) {
														if (!this.getSelectionModel().getSelection().length) {
															this.getSelectionModel().select(this.getRootNode());
														}
													}
												},
												findNameById: function(id) {
													var name = this.store.getNodeById(id) ? this.store.getNodeById(id).data.text : null;
													if (!name) {
														for (var k in this.storage) {
															if (k == id) {
																name = this.storage[k].name;
															}
														}
													}
													return name;
												},
												store: Ext.create('Ext.data.TreeStore', {
													proxy: {
														type: 'ajax',
														url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.organisationunitchildren_get
													},
													root: {
														id: DV.init.system.rootnode.id,
														text: DV.init.system.rootnode.name,
														expanded: false
													}
												}),
												listeners: {
													added: function() {
														DV.cmp.dimension.organisationunit.treepanel = this;
													},
													itemcontextmenu: function(v, r, h, i, e) {
														if (v.menu) {
															v.menu.destroy();
														}
														v.menu = Ext.create('Ext.menu.Menu', {
															id: 'treepanel-contextmenu',
															showSeparator: false
														});
														if (!r.data.leaf) {
															v.menu.add({
																id: 'treepanel-contextmenu-item',
																text: DV.i18n.select_all_children,
																icon: 'images/node-select-child.png',
																handler: function() {
																	r.expand(false, function() {
																		v.getSelectionModel().select(r.childNodes, true);
																		v.getSelectionModel().deselect(r);
																	});
																}
															});
														}
														else {
															return;
														}
														
														v.menu.showAt(e.xy);
													}
												}
											}
										],
										listeners: {
											added: function() {
												DV.cmp.dimension.organisationunit.panel = this;
											},
											expand: function() {
												DV.util.dimension.panel.setHeight(DV.conf.layout.west_maxheight_accordion_organisationunit);
												DV.cmp.dimension.organisationunit.treepanel.setHeight(DV.cmp.dimension.organisationunit.panel.getHeight() - DV.conf.layout.west_fill_accordion_organisationunit);
											}
										}
									},
									{
										title: '<div style="height:17px">' + DV.i18n.chart_options + '</div>',
										hideCollapseTool: true,
										cls: 'dv-accordion-options',
										items: [
											{
												xtype: 'panel',
												bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
												items: [
													{
														xtype: 'panel',
														layout: 'column',
														bodyStyle: 'border-style:none; background-color:transparent; padding-bottom:15px',
														items: [
															{
																xtype: 'checkbox',
																cls: 'dv-checkbox-alt1',
																style: 'margin-right:23px',
																boxLabel: DV.i18n.hide_subtitle,
																labelWidth: DV.conf.layout.form_label_width,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.hidesubtitle = this;
																	}
																}
															},
															{
																xtype: 'checkbox',
																cls: 'dv-checkbox-alt1',
																style: 'margin-right:23px',
																boxLabel: DV.i18n.hide_legend,
																labelWidth: DV.conf.layout.form_label_width,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.hidelegend = this;
																	}
																}
															},
															{
																xtype: 'checkbox',
																cls: 'dv-checkbox-alt1',
																style: 'margin-right:23px',
																boxLabel: DV.i18n.trend_line,
																labelWidth: DV.conf.layout.form_label_width,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.trendline = this;
																	}
																}
															},
															{
																xtype: 'checkbox',
																cls: 'dv-checkbox-alt1',
																boxLabel: DV.i18n.user_orgunit,
																labelWidth: DV.conf.layout.form_label_width,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.userorganisationunit = this;
																	}
																}
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'column',
														bodyStyle: 'border:0 none; background-color:transparent; padding-bottom:8px',
														items: [
															{
																xtype: 'textfield',
																cls: 'dv-textfield-alt1',
																style: 'margin-right:6px',
																fieldLabel: DV.i18n.domain_axis_label,
																labelAlign: 'top',
																labelSeparator: '',
																maxLength: 100,
																enforceMaxLength: true,
																labelWidth: DV.conf.layout.form_label_width,
																width: 187,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.domainaxislabel = this;
																	}
																}
															},
															{
																xtype: 'textfield',
																cls: 'dv-textfield-alt1',
																fieldLabel: DV.i18n.range_axis_label,
																labelAlign: 'top',
																labelSeparator: '',
																maxLength: 100,
																enforceMaxLength: true,
																labelWidth: DV.conf.layout.form_label_width,
																width: 187,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.rangeaxislabel = this;
																	}
																}
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'column',
														bodyStyle: 'border:0 none; background-color:transparent; padding-bottom:8px',
														items: [
															{
																xtype: 'numberfield',
																cls: 'dv-textfield-alt1',
																style: 'margin-right:6px',
																hideTrigger: true,
																fieldLabel: DV.i18n.target_line_value,
																labelAlign: 'top',
																labelSeparator: '',
																maxLength: 100,
																enforceMaxLength: true,
																width: 187,
																spinUpEnabled: true,
																spinDownEnabled: true,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.targetlinevalue = this;
																	},
																	change: function() {
																		DV.cmp.favorite.targetlinelabel.xable();
																	}
																}
															},
															{
																xtype: 'textfield',
																cls: 'dv-textfield-alt1',
																fieldLabel: DV.i18n.target_line_label,
																labelAlign: 'top',
																labelSeparator: '',
																maxLength: 100,
																enforceMaxLength: true,
																width: 187,
																disabled: true,
																xable: function() {
																	if (DV.cmp.favorite.targetlinevalue.getValue()) {
																		this.enable();
																	}
																	else {
																		this.disable();
																	}
																},
																listeners: {
																	added: function() {
																		DV.cmp.favorite.targetlinelabel = this;
																	}
																}
															}
														]
													},
													{
														xtype: 'panel',
														layout: 'column',
														bodyStyle: 'border:0 none; background-color:transparent; padding-bottom:5px',
														items: [
															{
																xtype: 'numberfield',
																cls: 'dv-textfield-alt1',
																style: 'margin-right:6px',
																hideTrigger: true,
																fieldLabel: DV.i18n.base_line_value,
																labelAlign: 'top',
																labelSeparator: '',
																maxLength: 100,
																enforceMaxLength: true,
																width: 187,
																spinUpEnabled: true,
																spinDownEnabled: true,
																listeners: {
																	added: function() {
																		DV.cmp.favorite.baselinevalue = this;
																	},
																	change: function() {
																		DV.cmp.favorite.baselinelabel.xable();
																	}
																}
															},
															{
																xtype: 'textfield',
																cls: 'dv-textfield-alt1',
																fieldLabel: DV.i18n.base_line_label,
																labelAlign: 'top',
																labelSeparator: '',
																maxLength: 100,
																enforceMaxLength: true,
																width: 187,
																disabled: true,
																xable: function() {
																	if (DV.cmp.favorite.baselinevalue.getValue()) {
																		this.enable();
																	}
																	else {
																		this.disable();
																	}
																},
																listeners: {
																	added: function() {
																		DV.cmp.favorite.baselinelabel = this;
																	}
																}
															}
														]
													}
												]
											}
										],
										listeners: {
											added: function() {
												DV.cmp.options.panel = this;
											},
											expand: function() {
												DV.util.dimension.panel.setHeight(DV.conf.layout.west_maxheight_accordion_options);
											}
										}
									}
								],
								listeners: {
									added: function() {
										DV.cmp.dimension.panel = this;
									}
								}
							}
						]
					}					
				],
                listeners: {
                    added: function() {
                        DV.cmp.region.west = this;
                    },
                    collapse: function() {                    
                        this.collapsed = true;
                        DV.cmp.toolbar.resizewest.setText('>>>');
                    },
                    expand: function() {
                        this.collapsed = false;
                        DV.cmp.toolbar.resizewest.setText('<<<');
                    }
                }
            },
            {
                id: 'center',
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding-top:5px',
                tbar: {
                    xtype: 'toolbar',
                    cls: 'dv-toolbar',
                    height: DV.conf.layout.center_tbar_height,
                    defaults: {
                        height: 26
                    },
                    items: [
                        {
                            xtype: 'button',
                            name: 'resizewest',
							cls: 'dv-toolbar-btn-2',
                            text: '<<<',
                            tooltip: DV.i18n.show_hide_chart_settings,
                            handler: function() {
                                var p = DV.cmp.region.west;
                                if (p.collapsed) {
                                    p.expand();
                                }
                                else {
                                    p.collapse();
                                }
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.resizewest = this;
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-1',
                            text: DV.i18n.update,
                            handler: function() {
                                DV.exe.execute();
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: DV.i18n.favorites + '..',
                            listeners: {
                                afterrender: function(b) {
                                    this.menu = Ext.create('Ext.menu.Menu', {
                                        margin: '2 0 0 0',
                                        shadow: false,
                                        showSeparator: false,
                                        items: [
                                            {
                                                text: DV.i18n.manage_favorites,
                                                iconCls: 'dv-menu-item-edit',
                                                handler: function() {
                                                    if (DV.cmp.favorite.window) {
                                                        DV.cmp.favorite.window.show();
                                                    }
                                                    else {
                                                        DV.cmp.favorite.window = Ext.create('Ext.window.Window', {
                                                            title: DV.i18n.manage_favorites,
                                                            iconCls: 'dv-window-title-favorite',
                                                            bodyStyle: 'padding:8px; background-color:#fff',
															width: DV.conf.layout.grid_favorite_width,
                                                            closeAction: 'hide',
                                                            resizable: false,
                                                            modal: true,
                                                            resetForm: function() {
                                                                DV.cmp.favorite.name.setValue('');
                                                                DV.cmp.favorite.system.setValue(false);
                                                            },
                                                            items: [
                                                                {
                                                                    xtype: 'form',
                                                                    bodyStyle: 'border-style:none',
                                                                    items: [
                                                                        {
                                                                            xtype: 'textfield',
                                                                            cls: 'dv-textfield',
                                                                            fieldLabel: 'Name',
                                                                            maxLength: 160,
                                                                            enforceMaxLength: true,
                                                                            labelWidth: DV.conf.layout.form_label_width,
                                                                            width: DV.conf.layout.grid_favorite_width - 28,
                                                                            listeners: {
                                                                                added: function() {
                                                                                    DV.cmp.favorite.name = this;
                                                                                },
                                                                                change: function() {
                                                                                    DV.cmp.favorite.system.check();
                                                                                    DV.cmp.favorite.save.xable();
                                                                                }
                                                                            }
                                                                        },
                                                                        {
                                                                            xtype: 'checkbox',
                                                                            cls: 'dv-checkbox',
                                                                            style: 'padding-bottom:2px',
                                                                            fieldLabel: DV.i18n.system,
                                                                            labelWidth: DV.conf.layout.form_label_width,
                                                                            disabled: !DV.init.system.user.isadmin,
                                                                            check: function() {
                                                                                if (!DV.init.system.user.isadmin) {
                                                                                    if (DV.store.favorite.findExact('name', DV.cmp.favorite.name.getValue()) === -1) {
                                                                                        this.setValue(false);
                                                                                    }
                                                                                }
                                                                            },
                                                                            listeners: {
                                                                                added: function() {
                                                                                    DV.cmp.favorite.system = this;
                                                                                }
                                                                            }
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    xtype: 'grid',
                                                                    width: DV.conf.layout.grid_favorite_width - 28,
                                                                    scroll: 'vertical',
                                                                    multiSelect: true,
                                                                    columns: [
                                                                        {
                                                                            dataIndex: 'name',
                                                                            width: DV.conf.layout.grid_favorite_width - 139,
                                                                            style: 'display:none'
                                                                        },
                                                                        {
                                                                            dataIndex: 'lastUpdated',
                                                                            width: 111,
                                                                            style: 'display:none'
                                                                        }
                                                                    ],
                                                                    setHeightInWindow: function(store) {
                                                                        var h = (store.getCount() * 23) + 30,
                                                                            sh = DV.util.viewport.getSize().y * 0.6;
                                                                        this.setHeight(h > sh ? sh : h);
                                                                        this.doLayout();
                                                                        this.up('window').doLayout();
                                                                    },
                                                                    store: DV.store.favorite,
                                                                    tbar: {
                                                                        id: 'favorite_t',
                                                                        cls: 'dv-toolbar',
                                                                        height: 30,
                                                                        defaults: {
                                                                            height: 24
                                                                        },
                                                                        items: [
                                                                            {
                                                                                text: DV.i18n.sort_by + '..',
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.sortby = this;
                                                                                    },
                                                                                    afterrender: function(b) {
                                                                                        this.addCls('dv-menu-togglegroup');
                                                                                        this.menu = Ext.create('Ext.menu.Menu', {
                                                                                            shadowOffset: 1,
                                                                                            showSeparator: false,
                                                                                            width: 109,
                                                                                            height: 70,
                                                                                            items: [
                                                                                                {
                                                                                                    xtype: 'radiogroup',
                                                                                                    cls: 'dv-radiogroup',
                                                                                                    columns: 1,
                                                                                                    vertical: true,
                                                                                                    items: [
                                                                                                        {
                                                                                                            boxLabel: DV.i18n.name,
                                                                                                            name: 'sortby',
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'name';
                                                                                                                    store.sorting.direction = 'ASC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        },
                                                                                                        {
                                                                                                            boxLabel: DV.i18n.system,
                                                                                                            name: 'sortby',
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'userId';
                                                                                                                    store.sorting.direction = 'ASC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        },
                                                                                                        {
                                                                                                            boxLabel:  DV.i18n.last_updated,
                                                                                                            name: 'sortby',
                                                                                                            checked: true,
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'lastUpdated';
                                                                                                                    store.sorting.direction = 'DESC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    ]
                                                                                                }
                                                                                            ]
                                                                                        });
                                                                                    }
                                                                                }
                                                                            },
                                                                            '->',
                                                                            {
                                                                                text: DV.i18n.rename,
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                disabled: true,
                                                                                xable: function() {
                                                                                    if (DV.cmp.favorite.grid.getSelectionModel().getSelection().length == 1) {
                                                                                        DV.cmp.favorite.rename.button.enable();
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.rename.button.disable();
                                                                                    }
                                                                                },
                                                                                handler: function() {
                                                                                    var selected = DV.cmp.favorite.grid.getSelectionModel().getSelection()[0];
                                                                                    var w = Ext.create('Ext.window.Window', {
                                                                                        title: DV.i18n.rename_favorite,
                                                                                        layout: 'fit',
                                                                                        width: DV.conf.layout.window_confirm_width,
                                                                                        bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                        modal: true,
                                                                                        cmp: {},
                                                                                        items: [
                                                                                            {
                                                                                                xtype: 'textfield',
                                                                                                cls: 'dv-textfield',
                                                                                                maxLength: 160,
                                                                                                enforceMaxLength: true,
                                                                                                value: selected.data.name,
                                                                                                listeners: {
                                                                                                    added: function() {
                                                                                                        this.up('window').cmp.name = this;
                                                                                                    },
                                                                                                    change: function() {
                                                                                                        this.up('window').cmp.rename.xable();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        ],
                                                                                        bbar: [
																							{
																								xtype: 'label',
																								style: 'padding-left:2px; line-height:22px; font-size:10px; color:#666; width:50%',
																								listeners: {
																									added: function() {
																										DV.cmp.favorite.rename.label = this;
																									}
																								}
																							},
																							'->',
                                                                                            {
                                                                                                text: DV.i18n.cancel,
                                                                                                handler: function() {
                                                                                                    this.up('window').close();
                                                                                                }
                                                                                            },
                                                                                            {
                                                                                                text: DV.i18n.rename,
                                                                                                disabled: true,
                                                                                                xable: function() {
                                                                                                    var value = this.up('window').cmp.name.getValue();
                                                                                                    if (value) {
																										if (DV.store.favorite.findExact('name', value) == -1) {
																											this.enable();
																											DV.cmp.favorite.rename.label.setText('');
																											return;
																										}
																										else {
																											DV.cmp.favorite.rename.label.setText(DV.i18n.name_already_in_use);
																										}
																									}
																									this.disable();
                                                                                                },
                                                                                                handler: function() {
                                                                                                    DV.util.crud.favorite.updateName(this.up('window').cmp.name.getValue());
                                                                                                },
                                                                                                listeners: {
                                                                                                    afterrender: function() {
                                                                                                        this.up('window').cmp.rename = this;
                                                                                                    },
                                                                                                    change: function() {
                                                                                                        this.xable();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        ],
                                                                                        listeners: {
                                                                                            afterrender: function() {
                                                                                                DV.cmp.favorite.rename.window = this;
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                    w.show();
                                                                                },
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.rename.button = this;
                                                                                    }
                                                                                }
                                                                            },
                                                                            {
                                                                                text: DV.i18n.delete_object,
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                disabled: true,
                                                                                xable: function() {
                                                                                    if (DV.cmp.favorite.grid.getSelectionModel().getSelection().length) {
                                                                                        DV.cmp.favorite.del.enable();
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.del.disable();
                                                                                    }
                                                                                },
                                                                                handler: function() {
                                                                                    var sel = DV.cmp.favorite.grid.getSelectionModel().getSelection();
                                                                                    if (sel.length) {
                                                                                        var str = '';
                                                                                        for (var i = 0; i < sel.length; i++) {
                                                                                            var out = sel[i].data.name.length > 35 ? (sel[i].data.name.substr(0,35) + '...') : sel[i].data.name;
                                                                                            str += '<br/>' + out;
                                                                                        }
                                                                                        var w = Ext.create('Ext.window.Window', {
                                                                                            title: DV.i18n.delete_favorite,
                                                                                            width: DV.conf.layout.window_confirm_width,
                                                                                            bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                            modal: true,
                                                                                            items: [
                                                                                                {
                                                                                                    html: DV.i18n.are_you_sure,
                                                                                                    bodyStyle: 'border-style:none'
                                                                                                },
                                                                                                {
                                                                                                    html: str,
                                                                                                    cls: 'dv-window-confirm-list'
                                                                                                }                                                                                                    
                                                                                            ],
                                                                                            bbar: [
                                                                                                {
                                                                                                    text: DV.i18n.cancel,
                                                                                                    handler: function() {
                                                                                                        this.up('window').close();
                                                                                                    }
                                                                                                },
                                                                                                '->',
                                                                                                {
                                                                                                    text: DV.i18n.delete_object,
                                                                                                    handler: function() {
                                                                                                        this.up('window').close();
                                                                                                        DV.util.crud.favorite.del(function() {
                                                                                                            DV.cmp.favorite.name.setValue('');
                                                                                                            DV.cmp.favorite.window.down('grid').setHeightInWindow(DV.store.favorite);
                                                                                                        });                                                                                                        
                                                                                                    }
                                                                                                }
                                                                                            ]
                                                                                        });
                                                                                        w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                        w.show();
                                                                                    }
                                                                                },
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.del = this;
                                                                                    }
                                                                                }
                                                                            }
                                                                        ]
                                                                    },
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.grid = this;
                                                                        },
                                                                        itemclick: function(g, r) {
                                                                            DV.cmp.favorite.name.setValue(r.data.name);
                                                                            DV.cmp.favorite.system.setValue(r.data.userId ? false : true);
                                                                            DV.cmp.favorite.rename.button.xable();
                                                                            DV.cmp.favorite.del.xable();
                                                                        },
                                                                        itemdblclick: function() {
                                                                            if (DV.cmp.favorite.save.xable()) {
                                                                                DV.cmp.favorite.save.handler();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            ],
                                                            bbar: [
                                                                {
                                                                    xtype: 'label',
                                                                    style: 'padding-left:2px; line-height:22px; font-size:10px; color:#666; width:70%',
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.label = this;
                                                                        }
                                                                    }
                                                                },																
                                                                '->',
                                                                {
                                                                    text: DV.i18n.save,
                                                                    disabled: true,
                                                                    xable: function() {
                                                                        if (DV.c.isrendered) {
                                                                            if (DV.cmp.favorite.name.getValue()) {
                                                                                var index = DV.store.favorite.findExact('name', DV.cmp.favorite.name.getValue());
                                                                                if (index != -1) {
                                                                                    if (DV.store.favorite.getAt(index).data.userId || DV.init.system.user.isadmin) {
                                                                                        this.enable();
                                                                                        DV.cmp.favorite.label.setText('');
                                                                                        return true;
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.label.setText(DV.i18n.system_favorite_overwrite_not_allowed);
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    this.enable();
                                                                                    DV.cmp.favorite.label.setText('');
                                                                                    return true;
                                                                                }
                                                                            }
                                                                            else {
                                                                                DV.cmp.favorite.label.setText('');
                                                                            }
                                                                        }
                                                                        else {
                                                                            if (DV.cmp.favorite.name.getValue()) {
                                                                                DV.cmp.favorite.label.setText(DV.i18n.example_chart_cannot_be_saved);
                                                                            }
                                                                            else {
                                                                                DV.cmp.favorite.label.setText('');
                                                                            }																				
                                                                        }
                                                                        this.disable();
                                                                        return false;
                                                                    },
                                                                    handler: function() {
                                                                        if (this.xable()) {
                                                                            var value = DV.cmp.favorite.name.getValue();
                                                                            if (DV.store.favorite.findExact('name', value) != -1) {
                                                                                var item = value.length > 40 ? (value.substr(0,40) + '...') : value;
                                                                                var w = Ext.create('Ext.window.Window', {
                                                                                    title: DV.i18n.save_favorite,
                                                                                    width: DV.conf.layout.window_confirm_width,
                                                                                    bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                    modal: true,
                                                                                    items: [
                                                                                        {
                                                                                            html: DV.i18n.are_you_sure,
                                                                                            bodyStyle: 'border-style:none'
                                                                                        },
                                                                                        {
                                                                                            html: '<br/>' + item,
                                                                                            cls: 'dv-window-confirm-list'
                                                                                        }
                                                                                    ],
                                                                                    bbar: [
                                                                                        {
                                                                                            text: DV.i18n.cancel,
                                                                                            handler: function() {
                                                                                                this.up('window').close();
                                                                                            }
                                                                                        },
                                                                                        '->',
                                                                                        {
                                                                                            text: DV.i18n.overwrite,
                                                                                            handler: function() {
                                                                                                this.up('window').close();
                                                                                                DV.util.crud.favorite.update(function() {
                                                                                                    DV.cmp.favorite.window.resetForm();
                                                                                                });
                                                                                                
                                                                                            }
                                                                                        }
                                                                                    ]
                                                                                });
                                                                                w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                w.show();
                                                                            }
                                                                            else {
                                                                                DV.util.crud.favorite.create(function() {
                                                                                    DV.cmp.favorite.window.resetForm();
                                                                                    DV.cmp.favorite.window.down('grid').setHeightInWindow(DV.store.favorite);
                                                                                });
                                                                            }                                                                                    
                                                                        }
                                                                    },
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.save = this;
                                                                        }
                                                                    }
                                                                }
                                                            ],
                                                            listeners: {
                                                                show: function() {                                               
                                                                    DV.cmp.favorite.save.xable();
                                                                    this.down('grid').setHeightInWindow(DV.store.favorite);
                                                                }
                                                            }
                                                        });
                                                        var w = DV.cmp.favorite.window;
                                                        w.setPosition((screen.width/2)-(DV.conf.layout.grid_favorite_width/2), DV.conf.layout.window_favorite_ypos, true);
                                                        w.show();
                                                    }
                                                },
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.toolbar.menuitem.datatable = this;
                                                    }
                                                }
                                            },
                                            '-',
                                            {
                                                xtype: 'grid',
                                                cls: 'dv-menugrid',
                                                width: 420,
                                                scroll: 'vertical',
                                                columns: [
                                                    {
                                                        dataIndex: 'icon',
                                                        width: 25,
                                                        style: 'display:none'
                                                    },
                                                    {
                                                        dataIndex: 'name',
                                                        width: 285,
                                                        style: 'display:none'
                                                    },
                                                    {
                                                        dataIndex: 'lastUpdated',
                                                        width: 110,
                                                        style: 'display:none'
                                                    }
                                                ],
                                                setHeightInMenu: function(store) {
                                                    var h = store.getCount() * 26,
                                                        sh = DV.util.viewport.getSize().y * 0.6;
                                                    this.setHeight(h > sh ? sh : h);
                                                    this.doLayout();
                                                    this.up('menu').doLayout();
                                                },
                                                store: DV.store.favorite,
                                                listeners: {
                                                    itemclick: function(g, r) {
                                                        g.getSelectionModel().select([], false);
                                                        this.up('menu').hide();
                                                        DV.exe.execute(r.data.id);
                                                    }
                                                }
                                            }
                                        ],
                                        listeners: {
                                            show: function() {
                                                if (!DV.store.favorite.isloaded) {
                                                    DV.store.favorite.load({scope: this, callback: function() {
                                                        this.down('grid').setHeightInMenu(DV.store.favorite);
                                                    }});
                                                }
                                                else {
                                                    this.down('grid').setHeightInMenu(DV.store.favorite);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: DV.i18n.download + '..',
                            execute: function(type) {
                                var svg = document.getElementsByTagName('svg');
                                
                                if (svg.length < 1) {
									DV.util.notification.error(DV.i18n.et_svg_browser, DV.i18n.em_svg_browser);
                                    return;
                                }
                                
                                document.getElementById('titleField').value = DV.c.filter.names[0] || 'Example chart';
                                document.getElementById('svgField').value = svg[0].parentNode.innerHTML;
                                document.getElementById('typeField').value = type;
                                
                                var exportForm = document.getElementById('exportForm');
                                exportForm.action = '../exportImage.action';
                                
                                if (svg[0].parentNode.innerHTML && type) {
                                    exportForm.submit();
                                }
                                else {
                                    alert(DV.i18n.no_svg_format);
                                }
                            },
                            listeners: {
                                afterrender: function(b) {
                                    this.menu = Ext.create('Ext.menu.Menu', {
                                        margin: '2 0 0 0',
                                        shadow: false,
                                        showSeparator: false,
                                        items: [
                                            {
                                                text: DV.i18n.image_png,
                                                iconCls: 'dv-menu-item-png',
                                                minWidth: 105,
                                                handler: function() {
                                                    b.execute(DV.conf.finals.image.png);
                                                }
                                            },
                                            {
                                                text: 'PDF',
                                                iconCls: 'dv-menu-item-pdf',
                                                minWidth: 105,
                                                handler: function() {
                                                    b.execute(DV.conf.finals.image.pdf);
                                                }
                                            }
                                        ]                                            
                                    });
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: DV.i18n.data_table,
                            disabled: true,
                            handler: function() {
                                var p = DV.cmp.region.east;
                                if (p.collapsed && p.items.length) {
                                    p.expand();
                                    DV.cmp.toolbar.resizeeast.show();
                                    DV.exe.datatable();
                                }
                                else {
                                    p.collapse();
                                    DV.cmp.toolbar.resizeeast.hide();
                                }
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.datatable = this;
                                }
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: 'Exit',
                            handler: function() {
                                window.location.href = DV.conf.finals.ajax.path_portal + DV.conf.finals.ajax.redirect;
                            }
                        },
                        {
                            xtype: 'button',
                            name: 'resizeeast',
							cls: 'dv-toolbar-btn-2',
                            text: '>>>',
                            tooltip: DV.i18n.hide_data_table,
                            hidden: true,
                            handler: function() {
                                DV.cmp.region.east.collapse();
                                this.hide();
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.resizeeast = this;
                                }
                            }
                        }
                    ]
                },
                bbar: {
					items: [
						{
							xtype: 'panel',
							cls: 'dv-statusbar',
							height: 24,
							listeners: {
								added: function() {
									DV.cmp.statusbar.panel = this;
								}
							}
						}
					]
				},					
                listeners: {
                    added: function() {
                        DV.cmp.region.center = this;
                    },
                    resize: function() {
						if (DV.cmp.statusbar.panel) {
							DV.cmp.statusbar.panel.setWidth(DV.cmp.region.center.getWidth());
						}
					}
                }
            },
            {
                region: 'east',
                preventHeader: true,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                width: 398,
                listeners: {
                    afterrender: function() {
                        DV.cmp.region.east = this;
                    }
                }
            }
        ],
        listeners: {
            afterrender: function(vp) {
                DV.init.initialize(vp);
            },
            resize: function(vp) {
                DV.cmp.region.west.setWidth(DV.conf.layout.west_width);
                
				DV.util.viewport.resizeDimensions();
                
                if (DV.datatable.datatable) {
                    DV.datatable.datatable.setHeight(DV.util.viewport.getSize().y - DV.conf.layout.east_tbar_height);
                }
            }
        }
    });
    
    }});
});
