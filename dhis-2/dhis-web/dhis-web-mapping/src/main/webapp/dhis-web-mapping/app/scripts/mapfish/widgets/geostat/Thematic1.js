/*
 * Copyright (C) 2007-2008  Camptocamp|
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat/Thematic1.js
 * @requires core/Color.js
 */

Ext.define('mapfish.widgets.geostat.Thematic1', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.thematic1',

    layer: null,
    format: null,
    url: null,
    featureSelection: true,
    nameAttribute: null,
    indicator: null,
    indicatorText: null,
    coreComp: null,
    classificationApplied: false,
    ready: false,
    border: false,
    loadMask: false,
    labelGenerator: null,
    
    cmp: {},
    
    store: {
		indicatorsByGroup: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'indicators'
				}
			},
			isLoaded: false,
			param: null,
			listeners: {
				beforeload: function(store) {
					if (store.param) {
						store.proxy.url = GIS.conf.url.path_api +  'indicatorGroups/' + store.param + '.json?links=false&paging=false';
					}
				},
				load: function() {
					if (!this.isLoaded) {
						//GIS.init.afterLoad();
						this.isLoaded = true;
					}
				}
			}
		}),
		
		dataElementsByGroup: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'dataElements'
				}
			},
			isLoaded: false,
			param: null,
			listeners: {
				beforeload: function(store) {
					if (store.param) {
						store.proxy.url = GIS.conf.url.path_api +  'dataElementGroups/' + store.param + '.json?links=false&paging=false';
					}
				},
				load: function() {
					if (!this.isLoaded) {
						//GIS.init.afterLoad();
						this.isLoaded = true;
					}
				}
			}
		}),
		
		periodsByType: Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			data: [],
			setIndex: function(periods) {
				for (var i = 0; i < periods.length; i++) {
					periods[i].index = i;
				}
			},
			sortStore: function() {
				this.sort('index', 'ASC');
			}
		})
	},
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        this.ready = true;

        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    requestFailure: function(request) {
        OpenLayers.Console.error(GIS.i18n.ajax_request_failed);
    },
    
    getColors: function() {
        var startColor = new mapfish.ColorRgb();
        startColor.setFromHex(this.cmp.startColor.getValue());
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(this.cmp.endColor.getValue());
        return [startColor, endColor];
    },
    
    initComponent: function() {
		this.createItems();
		
		this.addItems();
		
		mapfish.widgets.geostat.Thematic1.superclass.initComponent.apply(this);
    },
    
    createItems: function() {
        
        this.cmp.valueType = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.mapvaluetype,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            value: GIS.conf.finals.widget.valuetype_indicator,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id', 'name'],
                data: [
                    [GIS.conf.finals.widget.valuetype_indicator, 'Indicator'], //i18n
                    [GIS.conf.finals.widget.valuetype_dataelement, 'Data element'] //i18n
                ]
            }),
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        //this.valueType.value = cb.getValue();
                        //this.prepareMapViewValueType();
                        //this.classify(false, true);
                    }
                }
            }
        });
        
        this.cmp.indicatorGroup = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.indicator_group,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: GIS.store.indicatorGroups,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.indicator.clearValue();
                        this.store.indicatorsByGroup.param = cb.getValue();
                    }
                }
            }
        });
        
        this.cmp.indicator = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.indicator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: this.store.indicatorsByGroup,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						
                        //Ext.Ajax.request({
                            //url: GIS.conf.path_mapping + 'getMapLegendSetByIndicator' + GIS.conf.type,
                            //params: {indicatorId: cb.getValue()},
                            //scope: this,
                            //success: function(r) {
                                //var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                //if (mapLegendSet.id) {
                                    //this.legend.value = GIS.conf.map_legendset_type_predefined;
                                    //this.prepareMapViewLegend();
                                    
                                    //if (!GIS.stores.predefinedColorMapLegendSet.isLoaded) {
                                        //GIS.stores.predefinedColorMapLegendSet.load({scope: this, callback: function() {
                                            //cb.reloadStore.call(this, mapLegendSet.id);
                                        //}});
                                    //}
                                    //else {
                                        //cb.reloadStore.call(this, mapLegendSet.id);
                                    //}
                                //}
                                //else {
                                    //this.legend.value = GIS.conf.map_legendset_type_automatic;
                                    //this.prepareMapViewLegend();
                                    //this.classify(false, cb.lockPosition);
                                    //GIS.util.setLockPosition(cb);
                                //}
                            //}
                        //});
                    }
                }
            }
        });
        
        this.cmp.dataElementGroup = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.dataelement_group,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: GIS.store.dataElementGroup,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.dataElement.clearValue();
                        this.store.dataElementsByGroup.param = cb.getValue();
                    }
                }
            }
        });
        
        this.cmp.dataElement = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.dataElement,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: this.store.dataElementsByGroup,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						
                        //Ext.Ajax.request({
                            //url: GIS.conf.path_mapping + 'getMapLegendSetByDataElement' + GIS.conf.type,
                            //params: {dataElementId: cb.getValue()},
                            //scope: this,
                            //success: function(r) {
                                //var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                //if (mapLegendSet.id) {
                                    //this.legend.value = GIS.conf.map_legendset_type_predefined;
                                    //this.prepareMapViewLegend();
                                    
                                    //function load() {
                                        //this.cmp.legendSet.setValue(mapLegendSet.id);
                                        //this.applyPredefinedLegend();
                                    //}
                                    
                                    //if (!GIS.stores.predefinedMapLegendSet.isLoaded) {
                                        //GIS.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            //load.call(this);
                                        //}});
                                    //}
                                    //else {
                                        //load.call(this);
                                    //}
                                //}
                                //else {
                                    //this.legend.value = GIS.conf.map_legendset_type_automatic;
                                    //this.prepareMapViewLegend();
                                    //this.classify(false, cb.lockPosition);
                                    //GIS.util.setLockPosition(cb);
                                //}
                            //}
                        //});
                    }
                }
            }
        });
        
        this.cmp.periodType = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.period_type,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: GIS.store.periodTypes,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.period.clearValue();

						var pt = new PeriodType();
						var periods = pt.reverse( pt.filterFuturePeriods( pt.get(this.getValue()).generatePeriods(this.periodOffset) ) );
						this.store.periodsByType.setIndex(periods);
						this.store.periodsByType.loadData(periods);
                    }
                }
            }
        });
        
        this.cmp.period = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.period,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: this.store.periodsByType,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						
                    }
                }
            }
        });
        
        this.cmp.legendType = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            fieldLabel: GIS.i18n.legend_type,
            value: GIS.conf.finals.widget.legendtype_automatic,
            triggerAction: 'all',
            width: GIS.conf.layout.widget.combo_width,
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    [GIS.conf.finals.widget.legendtype_automatic, GIS.i18n.automatic],
                    [GIS.conf.finals.widget.legendtype_predefined, GIS.i18n.predefined]
                ]
            }),
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        //if (cb.getValue() == GIS.conf.map_legendset_type_predefined && cb.getValue() != this.legend.value) {
                            //this.legend.value = GIS.conf.map_legendset_type_predefined;                            
                            //this.prepareMapViewLegend();
                            
                        //}
                        //else if (cb.getValue() == GIS.conf.map_legendset_type_automatic && cb.getValue() != this.legend.value) {
                            //this.legend.value = GIS.conf.map_legendset_type_automatic;
                            //this.prepareMapViewLegend();
                        //}
                    }
                }
            }
        });
        
        this.cmp.legendSet = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.legendset,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            width: GIS.conf.layout.widget.combo_width,
            hidden: true,
            store: GIS.store.predefinedColorMapLegendSet
        });
                
        this.cmp.method = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.method,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            value: 2,
            width: GIS.conf.layout.widget.combo_width,
            store: Ext.create('Ext.data.Store', {
                fields: ['value', 'text'],
                data: [
                    [2, GIS.i18n.equal_intervals],
                    [3, GIS.i18n.equal_group_count],
                    [1, GIS.i18n.fixed_intervals]
                ]
            })
        });
        
        this.cmp.bounds = Ext.create('Ext.form.field.Text', {
            fieldLabel: GIS.i18n.bounds,
            width: GIS.conf.layout.widget.combo_width,
            hidden: true
        });
        
        this.cmp.classes = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.classes,
            editable: false,
            valueField: 'id',
            displayField: 'id',
            mode: 'local',
            value: 5,
            width: GIS.conf.layout.widget.combo_width,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            })
        });

        //this.cmp.startColor = new Ext.ux.ColorField({
            //fieldLabel: GIS.i18n.low_color,
            //allowBlank: false,
            //width: 73,
            //value: "#FF0000",
            //listeners: {
                //'select': {
                    //scope: this,
                    //fn: function() {
                        //this.classify(false, true);
                    //}
                //}
            //}
        //});
        
        //this.cmp.endColor = new Ext.ux.ColorField({
            //allowBlank: false,
            //width: 73,
            //value: "#FFFF00",
            //listeners: {
                //'select': {
                    //scope: this,
                    //fn: function() {
                        //this.classify(false, true);
                    //}
                //}
            //}
        //});
        
        this.cmp.radiusLow = Ext.create('Ext.form.field.Number', {
            fieldLabel: GIS.i18n.low_point_size,
            width: 73,
            allowDecimals: false,
            minValue: 1,
            value: 5
        });
        
        this.cmp.radiusHigh = Ext.create('Ext.form.field.Number', {
            fieldLabel: GIS.i18n.low_point_size,
            width: 73,
            allowDecimals: false,
            minValue: 1,
            value: 5
        });
        
        this.cmp.level = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.level,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.combo_width,
            store: GIS.store.organisationUnitLevels
        });
        
        this.cmp.parent = Ext.create('Ext.tree.Panel', {
            cls: 'treepanel-layer-border',
            autoScroll: true,
            lines: false,
			rootVisible: false,
			multiSelect: false,
			store: Ext.create('Ext.data.TreeStore', {
				proxy: {
					type: 'ajax',
					url: GIS.conf.url.path_gis + 'getOrganisationUnitChildren.action'
				},
				root: {
					id: 'root',
					expanded: true,
					children: GIS.init.rootNodes
				},
				listeners: {
					load: function(s, node, r) {
						for (var i = 0; i < r.length; i++) {
							r[i].data.text = GIS.util.jsonEncodeString(r[i].data.text);
						}
					}
				}
			})
        });
        
        this.cmp.colorPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			style: 'padding-bottom:4px',
			items: [
				{
					html: 'Low / high color:',
					width: 107,
					style: 'padding:3px 0 0 4px; color:#444'
				},
				//this.cmp.startColor,
				{
					style: 'width:4px'
				},
				//this.cmp.endColor,
				{
					style: 'height:4px'
				}
			]
		});
		
		this.cmp.radiusPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					xtype: 'panel',
					html: 'Low / high radius:',
					width: 107,
					style: 'padding:3px 0 0 4px; color:#444'
				},
				this.cmp.radiusLow,
				{
					style: 'width:4px'
				},
				this.cmp.radiusHigh
			]
		});
    },
    
    addItems: function() {
        
        this.items = [
            {
                xtype: 'panel',
                layout: 'column',
                width: 570,
                style: 'padding-bottom:3px',
                items: [
                    {
                        xtype: 'form',
                        width: 270,
                        items: [
                            { html: '<div class="window-info">' + GIS.i18n.data_options + '</div>' },
                            this.cmp.valueType,
                            this.cmp.indicatorGroup,
                            this.cmp.indicator,
                            this.cmp.dataElementGroup,
                            this.cmp.dataElement,
                            this.cmp.periodType,
                            this.cmp.period,
                            { html: '<div class="thematic-br">' },
                            { html: '<div class="window-info">' + GIS.i18n.legend_options + '</div>' },
                            this.cmp.legendType,
                            this.cmp.legendSet,
                            this.cmp.method,
                            this.cmp.bounds,
                            this.cmp.classes,
                            this.cmp.colorPanel,
                            this.cmp.radiusPanel
                        ]
                    },
                    {
                        xtype: 'panel',
                        width: 270,
                        bodyStyle: 'padding:0 0 0 8px;',
                        items: [
                            { html: '<div class="window-info">' + GIS.i18n.organisation_unit_level + '</div>' },                            
                            {
                                xtype: 'panel',
                                //layout: 'form',
                                items: [
                                    this.cmp.level
                                ]
                            },                            
                            { html: '<div class="thematic-br"></div>' },                            
                            { html: '<div class="window-info">' + GIS.i18n.parent_organisation_unit + '</div>' },
                            this.cmp.parent
                        ]
                    }
                ]
            }
        ];
    },
    
    createSelectFeatures: function() {
        var scope = this;
        
        var onHoverSelect = function onHoverSelect(feature) {
            if (feature.attributes.fixedName) {
                document.getElementById('featuredatatext').innerHTML =
                    '<div style="' + GIS.conf.feature_data_style_name + '">' + feature.attributes.fixedName + '</div>' +
                    '<div style="' + GIS.conf.feature_data_style_value + '">' + feature.attributes.value + '</div>';
            }
            else {
                document.getElementById('featuredatatext').innerHTML = '';
            }
        };
        
        var onHoverUnselect = function onHoverUnselect(feature) {
            if (feature.attributes.name) {
                document.getElementById('featuredatatext').innerHTML = 
                    '<div style="' + GIS.conf.feature_data_style_empty + '">' + GIS.i18n.no_feature_selected + '</div>';
            }
            else {
                document.getElementById('featuredatatext').innerHTML = '';
            }
        };
        
        var onClickSelect = function onClickSelect(feature) {
						
			function drill() {
				if (GIS.vars.locateFeatureWindow) {
					GIS.vars.locateFeatureWindow.destroy();
				}
						 
				scope.updateValues = true;
				scope.isDrillDown = true;
				
				function organisationUnitLevelCallback() {
					this.organisationUnitSelection.setValuesOnDrillDown(feature.attributes.id, feature.attributes.name);
					
					this.cmp.parent.reset();
					this.cmp.parent.selectedNode = {attributes: {
						id: this.organisationUnitSelection.parent.id,
						text: this.organisationUnitSelection.parent.name,
						level: this.organisationUnitSelection.parent.level
					}};
					
					this.cmp.level.setValue(this.organisationUnitSelection.level.level);
					this.loadGeoJson();
				}
				
				if (GIS.stores.organisationUnitLevel.isLoaded) {
					organisationUnitLevelCallback.call(scope);
				}
				else {
					GIS.stores.organisationUnitLevel.load({scope: scope, callback: function() {
						organisationUnitLevelCallback.call(this);
					}});
				}
			}
			
            if (feature.geometry.CLASS_NAME == GIS.conf.map_feature_type_point_class_name) {
                if (scope.featureOptions.menu) {
                    scope.featureOptions.menu.destroy();
                }
                
                scope.featureOptions.menu = new Ext.menu.Menu({
                    showInfo: function() {
                        Ext.Ajax.request({
                            url: GIS.conf.path_mapping + 'getFacilityInfo' + GIS.conf.type,
                            params: {id: feature.attributes.id},
                            success: function(r) {
                                var ou = Ext.util.JSON.decode(r.responseText);
                                
                                if (scope.featureOptions.info) {
                                    scope.featureOptions.info.destroy();
                                }
                                
                                scope.featureOptions.info = new Ext.Window({
                                    title: '<span class="window-information-title">Facility information sheet</span>',
                                    layout: 'table',
                                    width: GIS.conf.window_width + 178,
                                    height: GIS.util.getMultiSelectHeight() + 100,
                                    bodyStyle: 'background-color:#fff',
                                    defaults: {
                                        bodyStyle: 'vertical-align:top',
                                        labelSeparator: GIS.conf.labelseparator,
                                        emptyText: GIS.conf.emptytext
                                    },
                                    layoutConfig: {
                                        columns: 2
                                    },
                                    items: [
                                        {
                                            xtype: 'panel',
                                            layout: 'anchor',
                                            bodyStyle: 'padding:8px 4px 8px 8px',
                                            width: 160,
                                            items: [
                                                {html: '<div class="window-info">' + GIS.i18n.name + '<p style="font-weight:normal">' + feature.attributes.name + '</p></div>'},
                                                {html: '<div class="window-info">' + GIS.i18n.type + '<p style="font-weight:normal">' + ou.ty + '</p></div>'},
                                                {html: '<div class="window-info">' + GIS.i18n.code + '<p style="font-weight:normal">' + ou.co + '</p></div>'},
                                                {html: '<div class="window-info">' + GIS.i18n.address + '<p style="font-weight:normal">' + ou.ad + '</p></div>'},
                                                {html: '<div class="window-info">' + GIS.i18n.contact_person + '<p style="font-weight:normal">' + ou.cp + '</p></div>'},
                                                {html: '<div class="window-info">' + GIS.i18n.email + '<p style="font-weight:normal">' + ou.em + '</p></div>'},
                                                {html: '<div class="window-info">' + GIS.i18n.phone_number + '<p style="font-weight:normal">' + ou.pn + '</p></div>'}
                                            ]
                                        },
                                        {
                                            xtype: 'form',
                                            bodyStyle: 'padding:8px 8px 8px 4px',
                                            width: GIS.conf.window_width + 20,
                                            labelWidth: GIS.conf.label_width,
                                            items: [
                                                {html: '<div class="window-info">' + GIS.i18n.infrastructural_data + '</div>'},
                                                {
                                                    xtype: 'combo',
                                                    name: 'period',
                                                    fieldLabel: GIS.i18n.period,
                                                    typeAhead: true,
                                                    editable: false,
                                                    valueField: 'id',
                                                    displayField: 'name',
                                                    mode: 'remote',
                                                    forceSelection: true,
                                                    triggerAction: 'all',
                                                    selectOnFocus: true,
                                                    width: GIS.conf.combo_width,
                                                    store: GIS.stores.infrastructuralPeriodsByType,
                                                    lockPosition: false,
                                                    listeners: {
                                                        'select': function(cb) {
                                                            scope.infrastructuralPeriod = cb.getValue();
                                                            scope.stores.infrastructuralDataElementMapValue.setBaseParam('periodId', cb.getValue());
                                                            scope.stores.infrastructuralDataElementMapValue.setBaseParam('organisationUnitId', feature.attributes.id);
                                                            scope.stores.infrastructuralDataElementMapValue.load();
                                                        }
                                                    }
                                                },
                                                {html: '<div style="padding:4px 0 0 0"></div>'},
                                                {
                                                    xtype: 'grid',
                                                    height: GIS.util.getMultiSelectHeight(),
                                                    width: 242,
                                                    cm: new Ext.grid.ColumnModel({
                                                        columns: [
                                                            {id: 'dataElementName', header: 'Data element', dataIndex: 'dataElementName', sortable: true, width: 150},
                                                            {id: 'value', header: 'Value', dataIndex: 'value', sortable: true, width: 50}
                                                        ]
                                                    }),
                                                    disableSelection: true,
                                                    viewConfig: {forceFit: true},
                                                    store: scope.stores.infrastructuralDataElementMapValue
                                                }
                                            ]
                                        }
                                    ]
                                });
            
                                if (scope.infrastructuralPeriod) {
                                    scope.featureOptions.info.find('name', 'period')[0].setValue(scope.infrastructuralPeriod);
                                    scope.stores.infrastructuralDataElementMapValue.setBaseParam('periodId', scope.infrastructuralPeriod);
                                    scope.stores.infrastructuralDataElementMapValue.setBaseParam('organisationUnitId', feature.attributes.id);
                                    scope.stores.infrastructuralDataElementMapValue.load();
                                }
                                scope.featureOptions.info.setPagePosition(Ext.getCmp('east').x - (scope.featureOptions.info.width + 15), Ext.getCmp('center').y + 41);
                                scope.featureOptions.info.show();
                                scope.featureOptions.menu.destroy();
                            }
                        });
                    },
                    showRelocate: function() {
                        if (scope.featureOptions.coordinate) {
                            scope.featureOptions.coordinate.destroy();
                        }
                        
                        scope.featureOptions.coordinate = new Ext.Window({
                            title: '<span class="window-relocate-title">' + feature.attributes.name + '</span>',
							bodyStyle: 'padding:8px; background-color:#fff',
                            layout: 'fit',
                            width: GIS.conf.window_width,
                            items: [
                                {
                                    xtype: 'panel',
                                    items: [
                                        {html: GIS.i18n.select_new_location_on_map}
                                    ]
                                }
                            ],
                            bbar: [
                                '->',
                                {
                                    xtype: 'button',
                                    iconCls: 'icon-cancel',
                                    hideLabel: true,
                                    text: GIS.i18n.cancel,
                                    handler: function() {
                                        GIS.vars.relocate.active = false;
                                        scope.featureOptions.coordinate.destroy();
                                        document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'auto';
                                    }
                                }
                            ],
                            listeners: {
                                'close': function() {
                                    GIS.vars.relocate.active = false;
                                    document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'auto';
                                }
                            }
                        });
                        scope.featureOptions.coordinate.setPagePosition(Ext.getCmp('east').x - (scope.featureOptions.coordinate.width + 15), Ext.getCmp('center').y + 41);
                        scope.featureOptions.coordinate.show();                        
                    },
                    items: [
                        {
                            text: GIS.i18n.show_information_sheet,
                            iconCls: 'menu-featureoptions-info',
                            handler: function(item) {
                                if (GIS.stores.infrastructuralPeriodsByType.isLoaded) {
                                    item.parentMenu.showInfo();
                                }
                                else {
                                    GIS.stores.infrastructuralPeriodsByType.setBaseParam('name', GIS.system.infrastructuralPeriodType);
                                    GIS.stores.infrastructuralPeriodsByType.load({callback: function() {
                                        item.parentMenu.showInfo();
                                    }});
                                }
                            }
                        },
                        {
                            text: GIS.i18n.relocate,
                            iconCls: 'menu-featureoptions-relocate',
                            disabled: !GIS.user.isAdmin,
                            handler: function(item) {
                                GIS.vars.relocate.active = true;
                                GIS.vars.relocate.widget = scope;
                                GIS.vars.relocate.feature = feature;
                                document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'crosshair';
                                item.parentMenu.showRelocate();
                            }
                        }
                    ]
                });
                
                if (feature.attributes.hcwc) {
					scope.featureOptions.menu.add({
						text: 'Drill down',
						iconCls: 'menu-featureoptions-drilldown',
						scope: this,
						handler: function() {
							drill.call(this);
						}
					});
				}
                
                scope.featureOptions.menu.showAt([GIS.vars.mouseMove.x, GIS.vars.mouseMove.y]);
            }
            else {
                if (feature.attributes.hcwc) {
					drill.call(this);
                }
                else {
                    Ext.message.msg(false, GIS.i18n.no_coordinates_found);
                }
            }
        };
        
        this.selectFeatures = new OpenLayers.Control.newSelectFeature(
            this.layer, {
                onHoverSelect: onHoverSelect,
                onHoverUnselect: onHoverUnselect,
                onClickSelect: onClickSelect
            }
        );
        
        GIS.vars.map.addControl(this.selectFeatures);
        this.selectFeatures.activate();
    },
    
    prepareMapViewValueType: function() {
        var obj = {};
        if (this.valueType.isIndicator()) {
            this.cmp.indicatorGroup.show();
            this.cmp.indicator.show();
            this.cmp.dataElementGroup.hide();
            this.cmp.dataElement.hide();
            obj.components = {
                valueTypeGroup: this.cmp.indicatorGroup,
                valueType: this.cmp.indicator
            };
            obj.stores = {
                valueTypeGroup: GIS.stores.indicatorGroup,
                valueType: this.stores.indicatorsByGroup
            };
            obj.mapView = {
                valueTypeGroup: 'indicatorGroupId',
                valueType: 'indicatorId'
            };
        }
        else if (this.valueType.isDataElement()) {
            this.cmp.indicatorGroup.hide();
            this.cmp.indicator.hide();
            this.cmp.dataElementGroup.show();
            this.cmp.dataElement.show();
            obj.components = {
                valueTypeGroup: this.cmp.dataElementGroup,
                valueType: this.cmp.dataElement
            };
            obj.stores = {
                valueTypeGroup: GIS.stores.dataElementGroup,
                valueType: this.stores.dataElementsByGroup
            };
            obj.mapView = {
                valueTypeGroup: 'dataElementGroupId',
                valueType: 'dataElementId'
            };
        }
        return obj;
    },
    
    prepareMapViewPeriod: function() {
        var obj = {};        
        this.cmp.periodType.show();
        this.cmp.period.show();
        obj.components = {
            c1: this.cmp.periodType,
            c2: this.cmp.period
        };
        obj.stores = {
            c1: GIS.stores.periodType,
            c2: this.stores.periodsByType
        };
        obj.mapView = {
            c1: 'periodTypeId',
            c2: 'periodId'
        };
        return obj;
    },
    
    prepareMapViewLegend: function() {
        this.cmp.legendType.setValue(this.legend.value);
        
        if (this.legend.value == GIS.conf.map_legendset_type_automatic) {
            this.cmp.method.show();
            this.cmp.colorPanel.show();
            this.cmp.legendSet.hide();
            
            if (this.legend.method == GIS.conf.classify_with_bounds) {
                this.cmp.classes.hide();
                this.cmp.bounds.show();
            }
            else {
                this.cmp.classes.show();
                this.cmp.bounds.hide();
            }                
        }
        else if (this.legend.value == GIS.conf.map_legendset_type_predefined) {
            this.cmp.method.hide();
            this.cmp.classes.hide();
            this.cmp.bounds.hide();
            this.cmp.colorPanel.hide();
            this.cmp.legendSet.show();
        }
    },
    
    setMapView: function() {
        var obj = this.prepareMapViewValueType();
        
        function valueTypeGroupStoreCallback() {
            obj.components.valueTypeGroup.setValue(this.mapView[obj.mapView.valueTypeGroup]);
            obj.stores.valueType.setBaseParam(obj.mapView.valueTypeGroup, obj.components.valueTypeGroup.getValue());
            obj.stores.valueType.load({scope: this, callback: function() {
                obj.components.valueType.setValue(this.mapView[obj.mapView.valueType]);
                obj.components.valueType.currentValue = this.mapView[obj.mapView.valueType];
                
                obj = this.prepareMapViewPeriod();
                if (obj.stores.c1.isLoaded) {
                    dateTypeGroupStoreCallback.call(this);
                }
                else {
                    obj.stores.c1.load({scope: this, callback: function() {
                        dateTypeGroupStoreCallback.call(this);
                    }});
                }
            }});
        }
        
        function dateTypeGroupStoreCallback() {
            obj.components.c1.setValue(this.mapView[obj.mapView.c1]);
            
            obj.stores.c2.setBaseParam('name', this.mapView[obj.mapView.c1]);
            obj.stores.c2.load({scope: this, callback: function() {
                obj.components.c2.setValue(this.mapView[obj.mapView.c2]);
                obj.components.c2.currentValue = this.mapView[obj.mapView.c2];
                obj.components.c2.lockPosition = true;
                
                this.setMapViewLegend();
            }});
        }

        if (obj.stores.valueTypeGroup.isLoaded) {
            valueTypeGroupStoreCallback.call(this);
        }
        else {
            obj.stores.valueTypeGroup.load({scope: this, callback: function() {
                valueTypeGroupStoreCallback.call(this);
            }});
        }
    },
    
    setMapViewLegend: function() {
        this.prepareMapViewLegend();

        function predefinedMapLegendSetStoreCallback() {
            this.cmp.legendSet.setValue(this.mapView.mapLegendSetId);
            this.applyPredefinedLegend(true);
        }
        
        this.cmp.radiusLow.setValue(this.mapView.radiusLow || GIS.conf.defaultLowRadius);
        this.cmp.radiusHigh.setValue(this.mapView.radiusHigh || GIS.conf.defaultHighRadius);
        
        if (this.legend.value == GIS.conf.map_legendset_type_automatic) {
            this.cmp.method.setValue(this.mapView.method);
            this.cmp.startColor.setValue(this.mapView.colorLow);
            this.cmp.endColor.setValue(this.mapView.colorHigh);

            if (this.legend.method == GIS.conf.classify_with_bounds) {
                this.cmp.bounds.setValue(this.mapView.bounds);
            }
            else {
                this.cmp.classes.setValue(this.mapView.classes);
            }

            this.setMapViewMap();
        }
        else if (this.legend.value == GIS.conf.map_legendset_type_predefined) {
            if (GIS.stores.predefinedColorMapLegendSet.isLoaded) {
                predefinedMapLegendSetStoreCallback.call(this);
            }
            else {
                GIS.stores.predefinedColorMapLegendSet.load({scope: this, callback: function() {
                    predefinedMapLegendSetStoreCallback.call(this);
                }});
            }
        }
    },
    
    setMapViewMap: function() {
        this.organisationUnitSelection.setValues(this.mapView.parentOrganisationUnitId, this.mapView.parentOrganisationUnitName,
            this.mapView.parentOrganisationUnitLevel, this.mapView.organisationUnitLevel, this.mapView.organisationUnitLevelName);
            
        this.cmp.parent.reset();
        
        this.cmp.parent.selectedNode = {attributes: {
            id: this.mapView.parentOrganisationUnitId,
            text: this.mapView.parentOrganisationUnitName,
            level: this.mapView.parentOrganisationUnitLevel
        }};
            
        GIS.stores.organisationUnitLevel.load({scope: this, callback: function() {
            this.cmp.level.setValue(this.mapView.organisationUnitLevel);
            GIS.vars.activePanel.setPolygon();
            this.loadGeoJson();
        }});
    },
	
	applyPredefinedLegend: function(isMapView) {
        this.legend.value = GIS.conf.map_legendset_type_predefined;
		var mls = this.cmp.legendSet.getValue();
		Ext.Ajax.request({
			url: GIS.conf.path_mapping + 'getMapLegendsByMapLegendSet' + GIS.conf.type,
			params: {mapLegendSetId: mls},
            scope: this,
			success: function(r) {
				var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends,
                    colors = [],
                    bounds = [],
                    names = [];
				for (var i = 0; i < mapLegends.length; i++) {
					if (bounds[bounds.length-1] != mapLegends[i].startValue) {
						if (bounds.length !== 0) {
							colors.push(new mapfish.ColorRgb(240,240,240));
                            names.push('');
						}
						bounds.push(mapLegends[i].startValue);
					}
					colors.push(new mapfish.ColorRgb());
					colors[colors.length-1].setFromHex(mapLegends[i].color);
                    names.push(mapLegends[i].name);
					bounds.push(mapLegends[i].endValue);
				}

				this.colorInterpolation = colors;
				this.bounds = bounds;
                this.legendNames = names;
                
                if (isMapView) {
                    this.setMapViewMap();
                }
                else {
                    this.classify(false, true);
                }
			}
		});
	},
    
    formValidation: {
        validateForm: function() {
            if (this.cmp.valueType.getValue() == GIS.conf.map_value_type_indicator) {
                if (!this.cmp.indicator.getValue()) {
                    this.window.cmp.apply.disable();
                    return false;
                }
            }
            else if (this.cmp.valueType.getValue() == GIS.conf.map_value_type_dataelement) {
                if (!this.cmp.dataElement.getValue()) {
                    this.window.cmp.apply.disable();
                    return false;
                }
            }
            
            if (!this.cmp.period.getValue()) {
                this.window.cmp.apply.disable();
                return false;
            }

            if (!this.cmp.parent.selectedNode || !this.cmp.level.getValue()) {
                this.window.cmp.apply.disable();
                return false;
            }
            
            if (this.cmp.parent.selectedNode.attributes.level > this.cmp.level.getValue()) {
                this.window.cmp.apply.disable();
                return false;
            }

            if (this.cmp.legendType.getValue() == GIS.conf.map_legendset_type_automatic) {
                if (this.cmp.method.getValue() == GIS.conf.classify_with_bounds) {
                    if (!this.cmp.bounds.getValue()) {
                        this.window.cmp.apply.disable();
                        return false;
                    }
                }
            }
            else if (this.cmp.legendType.getValue() == GIS.conf.map_legendset_type_predefined) {
                if (!this.cmp.legendSet.getValue()) {
                    this.window.cmp.apply.disable();
                    return false;
                }
            }
            
            if (!this.cmp.radiusLow.getValue() || !this.cmp.radiusHigh.getValue()) {
                this.window.cmp.apply.disable();
                return false;
            }
            
            if (this.requireUpdate) {
                if (this.window.isUpdate) {
                    this.window.cmp.apply.disable();
                    this.requireUpdate = false;
                    this.window.isUpdate = false;
                }
                else {
                    this.window.cmp.apply.enable();
                }
            }
            
            return true;
        }
    },
    
    formValues: {
		getAllValues: function() {
			return {
                mapValueType: this.cmp.valueType.getValue(),
                indicatorGroupId: this.valueType.isIndicator() ? this.cmp.indicatorGroup.getValue() : null,
                indicatorId: this.valueType.isIndicator() ? this.cmp.indicator.getValue() : null,
				indicatorName: this.valueType.isIndicator() ? this.cmp.indicator.getRawValue() : null,
                dataElementGroupId: this.valueType.isDataElement() ? this.cmp.dataElementGroup.getValue() : null,
                dataElementId: this.valueType.isDataElement() ? this.cmp.dataElement.getValue() : null,
				dataElementName: this.valueType.isDataElement() ? this.cmp.dataElement.getRawValue() : null,
                periodTypeId: this.cmp.periodType.getValue(),
                periodId: this.cmp.period.getValue(),
                periodName: this.cmp.period.getRawValue(),
                parentOrganisationUnitId: this.organisationUnitSelection.parent.id,
                parentOrganisationUnitLevel: this.organisationUnitSelection.parent.level,
                parentOrganisationUnitName: this.organisationUnitSelection.parent.name,
                organisationUnitLevel: this.organisationUnitSelection.level.level,
                organisationUnitLevelName: this.organisationUnitSelection.level.name,
                mapLegendType: this.cmp.legendType.getValue(),
                method: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.method.getValue() : null,
                classes: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.classes.getValue() : null,
                bounds: this.legend.value == GIS.conf.map_legendset_type_automatic && this.legend.method == GIS.conf.classify_with_bounds ? this.cmp.bounds.getValue() : null,
                colorLow: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.startColor.getValue() : null,
                colorHigh: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.endColor.getValue() : null,
                mapLegendSetId: this.legend.value == GIS.conf.map_legendset_type_predefined ? this.cmp.legendSet.getValue() : null,
				radiusLow: this.cmp.radiusLow.getValue(),
				radiusHigh: this.cmp.radiusHigh.getValue(),
                longitude: GIS.vars.map.getCenter().lon,
                latitude: GIS.vars.map.getCenter().lat,
                zoom: parseFloat(GIS.vars.map.getZoom())
			};
		},
        
        getLegendInfo: function() {
            return {
                name: this.valueType.isIndicator() ? this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
                time: this.cmp.period.getRawValue(),
                map: this.organisationUnitSelection.level.name + ' / ' + this.organisationUnitSelection.parent.name
            };
        },
        
        getImageExportValues: function() {
			return {
				mapValueTypeValue: this.cmp.valueType.getValue() == GIS.conf.map_value_type_indicator ?
					this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
				dateValue: this.cmp.period.getRawValue()
			};
		},
        
        clearForm: function(clearLayer) {
            this.cmp.mapview.clearValue();
            
            this.cmp.valueType.setValue(GIS.conf.map_value_type_indicator);
            this.valueType.setIndicator();
            this.prepareMapViewValueType();
            this.cmp.indicatorGroup.clearValue();
            this.cmp.indicator.clearValue();
            this.cmp.dataElementGroup.clearValue();
            this.cmp.dataElement.clearValue();
            
            this.prepareMapViewPeriod();
            this.cmp.periodType.clearValue();
            this.cmp.period.clearValue();
            
            this.cmp.level.clearValue();
            this.cmp.parent.reset();
            
            this.legend.reset();
            this.prepareMapViewLegend();
            this.cmp.method.setValue(this.legend.method);
            this.cmp.classes.setValue(this.legend.classes);
            this.cmp.bounds.reset();
            
            this.cmp.startColor.setValue('#FF0000');
            this.cmp.endColor.setValue('#FFFF00');
            
            this.cmp.radiusLow.reset();
            this.cmp.radiusHigh.reset();
            
            this.window.cmp.apply.disable();
            
            if (clearLayer) {            
                document.getElementById(this.legendDiv).innerHTML = '';                
                this.layer.destroyFeatures();
                this.layer.setVisibility(false);
            }
        }
	},
    
    loadGeoJson: function() {
        GIS.vars.mask.msg = GIS.i18n.loading;
        GIS.vars.mask.show();
        GIS.vars.activeWidget = this;
        this.updateValues = true;
        
        var url = GIS.conf.path_mapping + 'getGeoJson.action?' +
            'parentId=' + this.organisationUnitSelection.parent.id +
            '&level=' + this.organisationUnitSelection.level.level;
        this.setUrl(url);
    },

    classify: function(exception, lockPosition, loaded) {
        if (this.formValidation.validateForm.apply(this, [exception])) {
            if (!this.layer.features.length && !loaded) {
                this.loadGeoJson();
            }
            
            GIS.vars.mask.msg = GIS.i18n.loading;
            GIS.vars.mask.show();
            
            GIS.vars.lockPosition = lockPosition;
            
            if (this.mapView) {
                if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    var point = GIS.util.getTransformedPointByXY(this.mapView.longitude, this.mapView.latitude);
                    GIS.vars.map.setCenter(new OpenLayers.LonLat(point.x, point.y), this.mapView.zoom);
                    GIS.vars.lockPosition = true;
                }
                this.mapView = false;
            }
            
            if (this.updateValues) {
                var dataUrl = this.valueType.isIndicator() ? 'getIndicatorMapValues' : 'getDataElementMapValues';
                var params = {
                    id: this.valueType.isIndicator() ? this.cmp.indicator.getValue() : this.cmp.dataElement.getValue(),
                    periodId: this.cmp.period.getValue(),
                    parentId: this.organisationUnitSelection.parent.id,
                    level: this.organisationUnitSelection.level.level
                };
                
                Ext.Ajax.request({
                    url: GIS.conf.path_mapping + dataUrl + GIS.conf.type,
                    params: params,
                    disableCaching: false,
                    scope: this,
                    success: function(r) {
                        var mapvalues = GIS.util.mapValueDecode(r);
                        this.layer.features = this.featureStorage.slice(0);
                        
                        if (mapvalues.length === 0) {
                            Ext.message.msg(false, GIS.i18n.current_selection_no_data);
                            GIS.vars.mask.hide();
                            return;
                        }
                        
                        for (var i = 0; i < this.layer.features.length; i++) {
                            this.layer.features[i].attributes.value = 0;
                            for (var j = 0; j < mapvalues.length; j++) {
                                if (this.layer.features[i].attributes.id == mapvalues[j].oi) {
                                    this.layer.features[i].attributes.value = parseFloat(mapvalues[j].v);
                                    break;
                                }
                            }
                        }
                        
                        this.updateValues = false;
                        this.applyValues();
                    }
                });
            }
            else {
                this.applyValues();
            }
        }
    },
    
    applyValues: function() {
        for (var i = 0; i < this.layer.features.length; i++) {
            var f = this.layer.features[i];
            if (!f.attributes.value) {
                this.layer.features.splice(i,1);
                i--;
            }
            else {
                f.attributes.labelString = f.attributes.name + ' (' + f.attributes.value + ')';
                f.attributes.fixedName = GIS.util.cutString(f.attributes.name, 30);
            }
        }
        if (!this.layer.features.length) {
            GIS.vars.mask.hide();
            Ext.message.msg(false, GIS.i18n.no_values_found);
            return;
        }
        
        this.button.menu.find('name','history')[0].addItem(this);
        
		var options = {
            indicator: 'value',
            method: this.cmp.method.getValue(),
            numClasses: this.cmp.classes.getValue(),
            colors: this.getColors(),
            minSize: parseInt(this.cmp.radiusLow.getValue()),
            maxSize: parseInt(this.cmp.radiusHigh.getValue())
        };
        
        GIS.vars.activeWidget = this;
        this.coreComp.applyClassification(options, this);
        this.classificationApplied = true;
        
        GIS.vars.mask.hide();
    },
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Thematic1.superclass.onRender.apply(this, arguments);
		
		this.coreComp = new mapfish.GeoStat.Thematic1(this.map, {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': Ext.Function.bind(this.requestSuccess, this),
            'requestFailure': Ext.Function.bind(this.requestFailure, this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        });
    }
});
