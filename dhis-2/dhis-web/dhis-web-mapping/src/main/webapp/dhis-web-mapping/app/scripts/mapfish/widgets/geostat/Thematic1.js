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
	
	// Ext panel
	cls: 'gis-form-widget el-border-0',
    border: false,

	// Mapfish
    layer: null,
    format: null,
    url: null,
    indicator: null,
    coreComp: null,
    classificationApplied: false,
    ready: false,
    loadMask: false,
    labelGenerator: null,
    
    // Properties
    
    model: null,
    
    config: null,
    
    cmp: {},
    
    toggler: {},
    
    features: [],
    
    update: {
		isOrganisationUnit: false,
		isData: false,
		isLegend: false,
		reset: function() {
			this.isOrganisationUnit = false;
			this.isData = false;
			this.isLegend = false;
		}
	},
	
	organisationUnitSelection: {
		parent: {
			id: null,
			name: null,
			level: null
		},
		level: {
			level: null,
			name: null
		},
		setValues: function(parentId, parentName, parentLevel, levelLevel, levelName) {
			this.parent.id = parentId || this.parent.id;
			this.parent.name = parentName || this.parent.name;
			this.parent.level = parentLevel || this.parent.level;
			this.level.level = levelLevel || this.level.level;
			this.level.name = levelName || this.level.name;
		},
		setValuesOnDrillDown: function(parentId, parentName) {
			this.parent.id = parentId;
			this.parent.name = parentName;
			this.parent.level = this.level.level;
			this.level.level++;
			this.level.name = GIS.store.organisationUnitLevel.getAt(
				GIS.store.organisationUnitLevels.find('level', this.level.level)).data.name;
		},
		reset: function() {
			this.parent.id = null;
			this.parent.name = null;
			this.parent.level = null;
			this.level.level = null;
			this.level.name = null;
		}			
	},
    
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
					else {
						return false;
					}
				},
				load: function() {
					if (!this.isLoaded) {
						//GIS.init.afterLoad();
						this.isLoaded = true;
					}
					this.sort('name', 'ASC');
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
					else {
						return false;
					}
				},
				load: function() {
					if (!this.isLoaded) {
						//GIS.init.afterLoad();
						this.isLoaded = true;
					}
					this.sort('name', 'ASC');
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
        this.coreComp.setUrl(this.url, this.success);
    },
    
    success: function(request) {
        var doc = request.responseXML,
			format = new OpenLayers.Format.GeoJSON(),
			that = this.widget;
			
        if (!doc || !doc.documentElement) {
            doc = request.responseText;
        }                
        if (doc.length) {
            doc = GIS.util.geojson.decode(doc);
        }
        else {
			//todo alert error message
		}
        
        that.layer.removeFeatures(that.layer.features);
        that.layer.addFeatures(format.read(doc));
		that.layer.features = GIS.util.vector.getTransformedFeatureArray(that.layer.features);
        that.features = that.layer.features.slice(0);
        that.requestSuccess(request);
        that.loadData();
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
        startColor.setFromHex(this.cmp.colorLow.getValue());
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(this.cmp.colorHigh.getValue());
        return [startColor, endColor];
    },
    
    initComponent: function() {
		this.createUtils();
		
		this.createItems();
		
		this.addItems();
		
		mapfish.widgets.geostat.Thematic1.superclass.initComponent.apply(this);
    },
    
    createUtils: function() {
		var that = this;
		
		this.toggler.valueType = function(valueType) {
			if (valueType === GIS.conf.finals.widget.valuetype_indicator) {
				that.cmp.indicatorGroup.show();
				that.cmp.indicator.show();
				that.cmp.dataElementGroup.hide();
				that.cmp.dataElement.hide();
			}
			else if (valueType === GIS.conf.finals.widget.valuetype_dataelement) {
				that.cmp.indicatorGroup.hide();
				that.cmp.indicator.hide();
				that.cmp.dataElementGroup.show();
				that.cmp.dataElement.show();
			}
		};
		
		this.toggler.legendType = function(legendType) {
			if (legendType === GIS.conf.finals.widget.legendtype_automatic) {
				that.cmp.methodPanel.show();
				that.cmp.lowPanel.show();
				that.cmp.highPanel.show();
				that.cmp.legendSet.hide();
			}
			else if (legendType === GIS.conf.finals.widget.legendtype_predefined) {
				that.cmp.methodPanel.hide();
				that.cmp.lowPanel.hide();
				that.cmp.highPanel.hide();
				that.cmp.legendSet.show();
			}
		};
	},
    
    createItems: function() {
		
		// Data options
		
        this.cmp.valueType = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: 'Value type', //i18n
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            value: GIS.conf.finals.widget.valuetype_indicator,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id', 'name'],
                data: [
                    [GIS.conf.finals.dimension.indicator.id, 'Indicator'], //i18n
                    [GIS.conf.finals.dimension.dataElement.id, 'Data element'] //i18n
                ]
            }),
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						this.toggler.valueType(cb.getValue());
                    }
                }
            }
        });
        
        this.cmp.indicatorGroup = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.indicator_group,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            store: GIS.store.indicatorGroups,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.indicator.clearValue();
                        this.store.indicatorsByGroup.param = cb.getValue();
                        this.store.indicatorsByGroup.load({
							scope: this,
							callback: function() {
								this.cmp.indicator.selectFirst();
							}
						});
                    }
                }
            }
        });
        
        this.cmp.indicator = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.indicator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            store: this.store.indicatorsByGroup,
            selectFirst: function() {
				this.setValue(this.store.getAt(0).data.id);
			},
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						this.update.isData = true;
						
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
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            hidden: true,
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
            fieldLabel: GIS.i18n.dataelement,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            hidden: true,
            store: this.store.dataElementsByGroup,
            listeners: {
				select: {
					scope: this,
					fn: function() {
						this.update.isData = true;
					}
				}
			}
        });
        
        this.cmp.periodType = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            queryMode: 'local',
            width: 116,
            store: GIS.store.periodTypes,
			periodOffset: 0,
            listeners: {
                select: {
                    scope: this,
                    fn: function() {
						var pt = new PeriodType();
						var periods = pt.reverse( pt.filterFuturePeriods( pt.get(this.cmp.periodType.getValue()).generatePeriods(this.cmp.periodType.periodOffset) ) );
						this.store.periodsByType.setIndex(periods);
						this.store.periodsByType.loadData(periods);
						
                        this.cmp.period.selectFirst();
                    }
                }
            }
        });
        
        this.cmp.period = Ext.create('Ext.form.field.ComboBox', {
			fieldLabel: GIS.i18n.period,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            selectFirst: function() {
				this.update.isData = true;
				this.setValue(this.store.getAt(0).data.id);
			},
            store: this.store.periodsByType
        });
        
        this.cmp.periodPrev = Ext.create('Ext.button.Button', {
			xtype: 'button',
			text: '<',
			width: 20,
			style: 'margin-left: 3px',
			scope: this,
			handler: function() {
				if (this.cmp.periodType.getValue()) {
					this.cmp.periodType.periodOffset--;
					this.cmp.periodType.fireEvent('select');
				}
			}
		});
        
        this.cmp.periodNext = Ext.create('Ext.button.Button', {
			xtype: 'button',
			text: '>',
			width: 20,
			style: 'margin-left: 3px',
			scope: this,
			handler: function() {
				if (this.cmp.periodType.getValue() && this.cmp.periodType.periodOffset < 0) {
					this.cmp.periodType.periodOffset++;
					this.cmp.periodType.fireEvent('select');
				}
			}
		});
		
		// Legend options
        
        this.cmp.legendType = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            fieldLabel: GIS.i18n.legend_type,
            value: GIS.conf.finals.widget.legendtype_automatic,
            queryMode: 'local',
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            store: Ext.create('Ext.data.ArrayStore', {
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
						this.toggler.legendType(cb.getValue());
                    }
                }
            }
        });
        
        this.cmp.legendSet = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.legendset,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            hidden: true,
            store: GIS.store.predefinedColorMapLegendSet,
            listeners: {
				select: {
					scope: this,
					fn: function() {
						this.update.isLegend = true;
					}
				}
			}
        });
        
        this.cmp.classes = Ext.create('Ext.form.field.Number', {
            editable: false,
            valueField: 'id',
            displayField: 'id',
            queryMode: 'local',
            value: 5,
            minValue: 1,
            maxValue: 7,
            width: 50,
            style: 'margin-right: 3px',
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.update.isLegend = true;
					}
				}
			}
        });
        
        this.cmp.method = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            value: 2,
            width: 109,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id', 'name'],
                data: [
                    [2, 'By class range'],
                    [3, 'By class count'] //i18n
                ]
            }),
            listeners: {
				select: {
					scope: this,
					fn: function() {
						this.update.isLegend = true;
					}
				}
			}
        });
        
        this.cmp.colorLow = Ext.create('Ext.button.Button', {
			width: 109,
			height: 22,
			style: 'margin-right: 3px',
			value: 'ff0000',
			getValue: function() {
				return this.value;
			},
			menu: {
				showSeparator: false,
				items: {
					xtype: 'colorpicker',
					closeAction: 'hide',
					listeners: {
						scope: this,
						select: function(cp, color) {
							this.cmp.colorLow.getEl().dom.style.background = '#' + color;
							this.cmp.colorLow.value = color;
							this.cmp.colorLow.menu.hide();
							
							this.update.isLegend = true;
						}
					}
				}
			},
			listeners: {
				render: function() {
					this.getEl().dom.style.background = '#' + this.value;
				}
			}
		});
        
        this.cmp.colorHigh = Ext.create('Ext.button.Button', {
			width: 109,
			height: 22,
			style: 'margin-right: 3px',
			value: '00ff00',
			getValue: function() {
				return this.value;
			},
			menu: {
				showSeparator: false,
				items: {
					xtype: 'colorpicker',
					closeAction: 'hide',
					//colors: ['888888', '993300', '333300', '003300'],
					listeners: {
						scope: this,
						select: function(cp, color) {
							this.cmp.colorHigh.getEl().dom.style.background = '#' + color;
							this.cmp.colorHigh.value = color;
							this.cmp.colorHigh.menu.hide();
							
							this.update.isLegend = true;
						}
					}
				}
			},
			listeners: {
				render: function() {
					this.getEl().dom.style.background = '#' + this.value;
				}
			}
		});
        
        this.cmp.radiusLow = Ext.create('Ext.form.field.Number', {
            width: 50,
            allowDecimals: false,
            minValue: 1,
            value: 3,
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.update.isLegend = true;
					}
				}
			}
        });
        
        this.cmp.radiusHigh = Ext.create('Ext.form.field.Number', {
            width: 50,
            allowDecimals: false,
            minValue: 1,
            value: 15,
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.update.isLegend = true;
					}
				}
			}
        });
        
        // Organisation unit options
        
        this.cmp.level = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.level,
            editable: false,
            valueField: 'level',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            style: 'margin-bottom: 4px',
            store: GIS.store.organisationUnitLevels,
			listeners: {
				select: {
					scope: this,
					fn: function() {
						this.update.isOrganisationUnit = true;
					}
				}
			}
        });
        
        this.cmp.parent = Ext.create('Ext.tree.Panel', {
            autoScroll: true,
            lines: false,
			rootVisible: false,
			multiSelect: false,
			width: GIS.conf.layout.widget.item_width,
			height: 220,
			bodyStyle: 'border: 1px solid #ccc !important; border-radius: 2px; padding: 3px 0 0px 3px',
			store: Ext.create('Ext.data.TreeStore', {
				proxy: {
					type: 'ajax',
					url: GIS.conf.url.path_gis + 'getOrganisationUnitChildren.action',
					reader: 'json'
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
			}),
			listeners: {
				select: {
					scope: this,
					fn: function() {
						this.update.isOrganisationUnit = true;
					}
				}
			}
        });
        
        // Custom panels
        
        this.cmp.periodTypePanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'Period type:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.periodType,
				this.cmp.periodPrev,
				this.cmp.periodNext
			]
		});			
        
        this.cmp.methodPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'Classes / method:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.classes,
				this.cmp.method
			]
		});
        
        this.cmp.lowPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'Low color / size:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.colorLow,
				this.cmp.radiusLow
			]
		});
        
        this.cmp.highPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'High color / size:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.colorHigh,
				this.cmp.radiusHigh
			]
		});
    },
    
    addItems: function() {
        
        this.items = [
            {
                xtype: 'form',
				cls: 'el-border-0',
                width: 270,
                items: [
					{
						html: GIS.i18n.data_options,
						cls: 'gis-form-subtitle-first'
					},
					this.cmp.valueType,
					this.cmp.indicatorGroup,
					this.cmp.indicator,
					this.cmp.dataElementGroup,
					this.cmp.dataElement,
					this.cmp.periodTypePanel,
					this.cmp.period,
					{
						html: GIS.i18n.legend_options,
						cls: 'gis-form-subtitle'
					},
					this.cmp.legendType,
					this.cmp.legendSet,
					this.cmp.methodPanel,
					this.cmp.lowPanel,
					this.cmp.highPanel,
					{
						html: 'Organisation unit level / parent', //i18n
						cls: 'gis-form-subtitle'
					},
					this.cmp.level,
					this.cmp.parent
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
    
    //prepareMapViewValueType: function() {
        //var obj = {};
        //if (this.valueType.isIndicator()) {
            //this.cmp.indicatorGroup.show();
            //this.cmp.indicator.show();
            //this.cmp.dataElementGroup.hide();
            //this.cmp.dataElement.hide();
            //obj.components = {
                //valueTypeGroup: this.cmp.indicatorGroup,
                //valueType: this.cmp.indicator
            //};
            //obj.stores = {
                //valueTypeGroup: GIS.stores.indicatorGroup,
                //valueType: this.stores.indicatorsByGroup
            //};
            //obj.mapView = {
                //valueTypeGroup: 'indicatorGroupId',
                //valueType: 'indicatorId'
            //};
        //}
        //else if (this.valueType.isDataElement()) {
            //this.cmp.indicatorGroup.hide();
            //this.cmp.indicator.hide();
            //this.cmp.dataElementGroup.show();
            //this.cmp.dataElement.show();
            //obj.components = {
                //valueTypeGroup: this.cmp.dataElementGroup,
                //valueType: this.cmp.dataElement
            //};
            //obj.stores = {
                //valueTypeGroup: GIS.stores.dataElementGroup,
                //valueType: this.stores.dataElementsByGroup
            //};
            //obj.mapView = {
                //valueTypeGroup: 'dataElementGroupId',
                //valueType: 'dataElementId'
            //};
        //}
        //return obj;
    //},
    
    //prepareMapViewPeriod: function() {
        //var obj = {};        
        //this.cmp.periodType.show();
        //this.cmp.period.show();
        //obj.components = {
            //c1: this.cmp.periodType,
            //c2: this.cmp.period
        //};
        //obj.stores = {
            //c1: GIS.stores.periodType,
            //c2: this.stores.periodsByType
        //};
        //obj.mapView = {
            //c1: 'periodTypeId',
            //c2: 'periodId'
        //};
        //return obj;
    //},
    
    //prepareMapViewLegend: function() {
        //this.cmp.legendType.setValue(this.legend.value);
        
        //if (this.legend.value == GIS.conf.map_legendset_type_automatic) {
            //this.cmp.method.show();
            //this.cmp.colorPanel.show();
            //this.cmp.legendSet.hide();
            
            //if (this.legend.method == GIS.conf.classify_with_bounds) {
                //this.cmp.classes.hide();
                //this.cmp.bounds.show();
            //}
            //else {
                //this.cmp.classes.show();
                //this.cmp.bounds.hide();
            //}                
        //}
        //else if (this.legend.value == GIS.conf.map_legendset_type_predefined) {
            //this.cmp.method.hide();
            //this.cmp.classes.hide();
            //this.cmp.bounds.hide();
            //this.cmp.colorPanel.hide();
            //this.cmp.legendSet.show();
        //}
    //},
    
    //setMapView: function() {
        //var obj = this.prepareMapViewValueType();
        
        //function valueTypeGroupStoreCallback() {
            //obj.components.valueTypeGroup.setValue(this.mapView[obj.mapView.valueTypeGroup]);
            //obj.stores.valueType.setBaseParam(obj.mapView.valueTypeGroup, obj.components.valueTypeGroup.getValue());
            //obj.stores.valueType.load({scope: this, callback: function() {
                //obj.components.valueType.setValue(this.mapView[obj.mapView.valueType]);
                //obj.components.valueType.currentValue = this.mapView[obj.mapView.valueType];
                
                //obj = this.prepareMapViewPeriod();
                //if (obj.stores.c1.isLoaded) {
                    //dateTypeGroupStoreCallback.call(this);
                //}
                //else {
                    //obj.stores.c1.load({scope: this, callback: function() {
                        //dateTypeGroupStoreCallback.call(this);
                    //}});
                //}
            //}});
        //}
        
        //function dateTypeGroupStoreCallback() {
            //obj.components.c1.setValue(this.mapView[obj.mapView.c1]);
            
            //obj.stores.c2.setBaseParam('name', this.mapView[obj.mapView.c1]);
            //obj.stores.c2.load({scope: this, callback: function() {
                //obj.components.c2.setValue(this.mapView[obj.mapView.c2]);
                //obj.components.c2.currentValue = this.mapView[obj.mapView.c2];
                //obj.components.c2.lockPosition = true;
                
                //this.setMapViewLegend();
            //}});
        //}

        //if (obj.stores.valueTypeGroup.isLoaded) {
            //valueTypeGroupStoreCallback.call(this);
        //}
        //else {
            //obj.stores.valueTypeGroup.load({scope: this, callback: function() {
                //valueTypeGroupStoreCallback.call(this);
            //}});
        //}
    //},
    
    //setMapViewLegend: function() {
        //this.prepareMapViewLegend();

        //function predefinedMapLegendSetStoreCallback() {
            //this.cmp.legendSet.setValue(this.mapView.mapLegendSetId);
            //this.applyPredefinedLegend(true);
        //}
        
        //this.cmp.radiusLow.setValue(this.mapView.radiusLow || GIS.conf.defaultLowRadius);
        //this.cmp.radiusHigh.setValue(this.mapView.radiusHigh || GIS.conf.defaultHighRadius);
        
        //if (this.legend.value == GIS.conf.map_legendset_type_automatic) {
            //this.cmp.method.setValue(this.mapView.method);
            //this.cmp.startColor.setValue(this.mapView.colorLow);
            //this.cmp.endColor.setValue(this.mapView.colorHigh);

            //if (this.legend.method == GIS.conf.classify_with_bounds) {
                //this.cmp.bounds.setValue(this.mapView.bounds);
            //}
            //else {
                //this.cmp.classes.setValue(this.mapView.classes);
            //}

            //this.setMapViewMap();
        //}
        //else if (this.legend.value == GIS.conf.map_legendset_type_predefined) {
            //if (GIS.stores.predefinedColorMapLegendSet.isLoaded) {
                //predefinedMapLegendSetStoreCallback.call(this);
            //}
            //else {
                //GIS.stores.predefinedColorMapLegendSet.load({scope: this, callback: function() {
                    //predefinedMapLegendSetStoreCallback.call(this);
                //}});
            //}
        //}
    //},
    
    //setMapViewMap: function() {
        //this.organisationUnitSelection.setValues(this.mapView.parentOrganisationUnitId, this.mapView.parentOrganisationUnitName,
            //this.mapView.parentOrganisationUnitLevel, this.mapView.organisationUnitLevel, this.mapView.organisationUnitLevelName);
            
        //this.cmp.parent.reset();
        
        //this.cmp.parent.selectedNode = {attributes: {
            //id: this.mapView.parentOrganisationUnitId,
            //text: this.mapView.parentOrganisationUnitName,
            //level: this.mapView.parentOrganisationUnitLevel
        //}};
            
        //GIS.stores.organisationUnitLevel.load({scope: this, callback: function() {
            //this.cmp.level.setValue(this.mapView.organisationUnitLevel);
            //GIS.vars.activePanel.setPolygon();
            //this.loadGeoJson();
        //}});
    //},
	
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
    
    //formValidation: {
        //validateForm: function() {
            //if (this.cmp.valueType.getValue() == GIS.conf.map_value_type_indicator) {
                //if (!this.cmp.indicator.getValue()) {
                    //this.window.cmp.apply.disable();
                    //return false;
                //}
            //}
            //else if (this.cmp.valueType.getValue() == GIS.conf.map_value_type_dataelement) {
                //if (!this.cmp.dataElement.getValue()) {
                    //this.window.cmp.apply.disable();
                    //return false;
                //}
            //}
            
            //if (!this.cmp.period.getValue()) {
                //this.window.cmp.apply.disable();
                //return false;
            //}

            //if (!this.cmp.parent.selectedNode || !this.cmp.level.getValue()) {
                //this.window.cmp.apply.disable();
                //return false;
            //}
            
            //if (this.cmp.parent.selectedNode.attributes.level > this.cmp.level.getValue()) {
                //this.window.cmp.apply.disable();
                //return false;
            //}

            //if (this.cmp.legendType.getValue() == GIS.conf.map_legendset_type_automatic) {
                //if (this.cmp.method.getValue() == GIS.conf.classify_with_bounds) {
                    //if (!this.cmp.bounds.getValue()) {
                        //this.window.cmp.apply.disable();
                        //return false;
                    //}
                //}
            //}
            //else if (this.cmp.legendType.getValue() == GIS.conf.map_legendset_type_predefined) {
                //if (!this.cmp.legendSet.getValue()) {
                    //this.window.cmp.apply.disable();
                    //return false;
                //}
            //}
            
            //if (!this.cmp.radiusLow.getValue() || !this.cmp.radiusHigh.getValue()) {
                //this.window.cmp.apply.disable();
                //return false;
            //}
            
            //if (this.requireUpdate) {
                //if (this.window.isUpdate) {
                    //this.window.cmp.apply.disable();
                    //this.requireUpdate = false;
                    //this.window.isUpdate = false;
                //}
                //else {
                    //this.window.cmp.apply.enable();
                //}
            //}
            
            //return true;
        //}
    //},
    
    //formValues: {
		//getAllValues: function() {
			//return {
                //mapValueType: this.cmp.valueType.getValue(),
                //indicatorGroupId: this.valueType.isIndicator() ? this.cmp.indicatorGroup.getValue() : null,
                //indicatorId: this.valueType.isIndicator() ? this.cmp.indicator.getValue() : null,
				//indicatorName: this.valueType.isIndicator() ? this.cmp.indicator.getRawValue() : null,
                //dataElementGroupId: this.valueType.isDataElement() ? this.cmp.dataElementGroup.getValue() : null,
                //dataElementId: this.valueType.isDataElement() ? this.cmp.dataElement.getValue() : null,
				//dataElementName: this.valueType.isDataElement() ? this.cmp.dataElement.getRawValue() : null,
                //periodTypeId: this.cmp.periodType.getValue(),
                //periodId: this.cmp.period.getValue(),
                //periodName: this.cmp.period.getRawValue(),
                //parentOrganisationUnitId: this.organisationUnitSelection.parent.id,
                //parentOrganisationUnitLevel: this.organisationUnitSelection.parent.level,
                //parentOrganisationUnitName: this.organisationUnitSelection.parent.name,
                //organisationUnitLevel: this.organisationUnitSelection.level.level,
                //organisationUnitLevelName: this.organisationUnitSelection.level.name,
                //mapLegendType: this.cmp.legendType.getValue(),
                //method: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.method.getValue() : null,
                //classes: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.classes.getValue() : null,
                //bounds: this.legend.value == GIS.conf.map_legendset_type_automatic && this.legend.method == GIS.conf.classify_with_bounds ? this.cmp.bounds.getValue() : null,
                //colorLow: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.startColor.getValue() : null,
                //colorHigh: this.legend.value == GIS.conf.map_legendset_type_automatic ? this.cmp.endColor.getValue() : null,
                //mapLegendSetId: this.legend.value == GIS.conf.map_legendset_type_predefined ? this.cmp.legendSet.getValue() : null,
				//radiusLow: this.cmp.radiusLow.getValue(),
				//radiusHigh: this.cmp.radiusHigh.getValue(),
                //longitude: GIS.vars.map.getCenter().lon,
                //latitude: GIS.vars.map.getCenter().lat,
                //zoom: parseFloat(GIS.vars.map.getZoom())
			//};
		//},
        
        //getLegendInfo: function() {
            //return {
                //name: this.model.valueType === 'indicator' ? this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
                //time: this.cmp.period.getRawValue(),
                //map: this.organisationUnitSelection.level.name + ' / ' + this.organisationUnitSelection.parent.name
            //};
        //},
        
        //getImageExportValues: function() {
			//return {
				//mapValueTypeValue: this.cmp.valueType.getValue() == GIS.conf.map_value_type_indicator ?
					//this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
				//dateValue: this.cmp.period.getRawValue()
			//};
		//},
        
        //clearForm: function(clearLayer) {
            //this.cmp.mapview.clearValue();
            
            //this.cmp.valueType.setValue(GIS.conf.map_value_type_indicator);
            //this.valueType.setIndicator();
            //this.prepareMapViewValueType();
            //this.cmp.indicatorGroup.clearValue();
            //this.cmp.indicator.clearValue();
            //this.cmp.dataElementGroup.clearValue();
            //this.cmp.dataElement.clearValue();
            
            //this.prepareMapViewPeriod();
            //this.cmp.periodType.clearValue();
            //this.cmp.period.clearValue();
            
            //this.cmp.level.clearValue();
            //this.cmp.parent.reset();
            
            //this.legend.reset();
            //this.prepareMapViewLegend();
            //this.cmp.method.setValue(this.legend.method);
            //this.cmp.classes.setValue(this.legend.classes);
            //this.cmp.bounds.reset();
            
            //this.cmp.startColor.setValue('#FF0000');
            //this.cmp.endColor.setValue('#FFFF00');
            
            //this.cmp.radiusLow.reset();
            //this.cmp.radiusHigh.reset();
            
            //this.window.cmp.apply.disable();
            
            //if (clearLayer) {            
                //document.getElementById(this.legendDiv).innerHTML = '';                
                //this.layer.destroyFeatures();
                //this.layer.setVisibility(false);
            //}
        //}
	//},
	
    execute: function() {
		GIS.mask.msg = GIS.i18n.loading;
		GIS.mask.show();
		
		this.getModel();
		
		if (this.update.isOrganisationUnit) {
			this.loadOrganisationUnits();
		}
		else if (this.update.isData) {
			this.loadData();
		}
		else if (this.update.isLegend) {
			this.loadLegend();
		}
		else {
			GIS.mask.hide();
		}
	},
    	
	getModel: function() {
		var level = this.cmp.level,
			parent = this.cmp.parent.getSelectionModel().getSelection();
			
			console.log(parent);return;
		
		var model = {
			valueType: this.cmp.valueType.getValue(),
			indicatorGroup: this.cmp.indicatorGroup.getValue(),
			indicator: this.cmp.indicator.getValue(),
			dataElementGroup: this.cmp.dataElementGroup.getValue(),
			dataElement: this.cmp.dataElement.getValue(),
			periodType: this.cmp.periodType.getValue(),
			period: this.cmp.period.getValue(),
			legendType: this.cmp.legendType.getValue(),
			legendSet: this.cmp.legendSet.getValue(),
			classes: this.cmp.classes.getValue(),
			method: this.cmp.method.getValue(),
			colorLow: this.cmp.colorLow.getValue(),
			colorHigh: this.cmp.colorHigh.getValue(),
			colors: this.getColors(),
			radiusLow: parseInt(this.cmp.radiusLow.getValue()),
			radiusHigh: parseInt(this.cmp.radiusHigh.getValue()),
			level: level.getValue(),
			levelName: level.getRawValue(),
			parentId: parent.length ? parent[0].data.id : null,
			parentName: parent.length ? parent[0].data.text : null,
			parentLevel: parent.length ? parent[0].data.level : null,
			updateOrganisationUnit: false,
			updateData: false,
			updateLegend: false
		};
		
		model.valueType = this.config.valueType || model.valueType;
		model.indicatorGroup = this.config.indicatorGroup || model.indicatorGroup;
		model.indicator = this.config.indicator || model.indicator;
		model.dataElementGroup = this.config.dataElementGroup || model.dataElementGroup;
		model.dataElement = this.config.valueType || model.dataElement;
		model.periodType = this.config.valueType || model.periodType;
		model.period = this.config.valueType || model.period;
		model.legendType = this.config.legendType || model.legendType;
		model.legendSet = this.config.legendSet || model.legendSet;
		model.classes = this.config.classes || model.classes;
		model.method = this.config.method || model.method;
		model.colorLow = this.config.colorLow || model.colorLow;
		model.colorHigh = this.config.colorHigh || model.colorHigh;
		model.radiusLow = this.config.radiusLow || model.radiusLow;
		model.radiusHigh = this.config.radiusHigh || model.radiusHigh;
		model.level = this.config.level || model.level;
		model.levelName = this.config.levelName || model.levelName;
		model.parentId = this.config.valueType || model.parentId;
		model.parentName = this.config.valueType || model.parentName;
		model.parentLevel = this.config.valueType || model.parentLevel;
		model.updateOrganisationUnit = this.config.updateOrganisationUnit || false;
		model.updateData = this.config.updateData || false;
		model.updateLegend = this.config.updateLegend || false;
		console.log(model);return;
		
		if (!this.validateModel(model)) {
			return;
		}
		
		this.model = model;
		
		this.config = {};
		
		return this.model;
	},
	
	validateModel: function(model) {
		if (model.valueType === GIS.conf.finals.dimension.indicator.id) {
			if (!model.indicatorGroup || !Ext.isString(model.indicatorGroup)) {
				GIS.logg.push(model.indicatorGroup, this.xtype + '.indicatorGroup: string');
				return false;
			}
			if (!model.indicator || !Ext.isString(model.indicator)) {
				GIS.logg.push(model.indicator, this.xtype + '.indicator: string');
				return false;
			}
		}
		else if (model.valueType === GIS.conf.finals.dimension.dataElement.id) {
			if (!model.dataElementGroup || !Ext.isString(model.dataElementGroup)) {
				GIS.logg.push(model.dataElementGroup, this.xtype + '.dataElementGroup: string');
				return false;
			}
			if (!model.dataElement || !Ext.isString(model.dataElement)) {
				GIS.logg.push(model.dataElement, this.xtype + '.dataElement: string');
				return false;
			}
		}
		
		if (!model.periodType || !Ext.isString(model.periodType)) {
			GIS.logg.push(model.periodType, this.xtype + '.periodType: string');
			return false;
		}
		if (!model.period || !Ext.isString(model.period)) {
			GIS.logg.push(model.period, this.xtype + '.period: string');
			return false;
		}
		
		if (model.legendType === GIS.conf.finals.widget.legendtype_automatic) {
			if (!model.classes || !Ext.isNumber(model.classes)) {
				GIS.logg.push(model.classes, this.xtype + '.classes: number');
				return false;
			}
			if (!model.method || !Ext.isString(model.method)) {
				GIS.logg.push(model.method, this.xtype + '.method: string');
				return false;
			}
			if (!model.colorLow || !Ext.isString(model.colorLow)) {
				GIS.logg.push(model.colorLow, this.xtype + '.colorLow: string');
				return false;
			}
			if (!model.radiusLow || !Ext.isString(model.radiusLow)) {
				GIS.logg.push(model.radiusLow, this.xtype + '.radiusLow: string');
				return false;
			}
			if (!model.colorHigh || !Ext.isString(model.colorHigh)) {
				GIS.logg.push(model.colorHigh, this.xtype + '.colorHigh: string');
				return false;
			}
			if (!model.radiusHigh || !Ext.isString(model.radiusHigh)) {
				GIS.logg.push(model.radiusHigh, this.xtype + '.radiusHigh: string');
				return false;
			}
		}
		else if (model.legendType === GIS.conf.finals.widget.legendtype_predefined) {			
			if (!model.legendSet || !Ext.isString(model.legendSet)) {
				GIS.logg.push(model.legendSet, this.xtype + '.legendSet: string');
				return false;
			}
		}
		
		if (!model.level || !Ext.isNumber(model.level)) {
			GIS.logg.push(model.level, this.xtype + '.level: number');
			return false;
		}
		if (!model.levelName || !Ext.isString(model.levelName)) {
			GIS.logg.push(model.levelName, this.xtype + '.levelName: string');
			return false;
		}
		if (!model.parentId || !Ext.isString(model.parentId)) {
			GIS.logg.push(model.parentId, this.xtype + '.parentId: string');
			return false;
		}
		if (!model.parentName || !Ext.isString(model.parentName)) {
			GIS.logg.push(model.parentName, this.xtype + '.parentName: string');
			return false;
		}
		if (!model.parentLevel || !Ext.isNumber(model.parentLevel)) {
			GIS.logg.push(model.parentLevel, this.xtype + '.parentLevel: number');
			return false;
		}
		
		return true;
	},
	
    loadOrganisationUnits: function() {        
        var url = GIS.conf.url.path_gis + 'getGeoJson.action?' +
            'parentId=' + this.organisationUnitSelection.parent.id +
            '&level=' + this.organisationUnitSelection.level.level;
        this.setUrl(url);
    },
    
    loadData: function() {
		var type = this.model.valueType,
			dataUrl = '../api/mapValues/' + GIS.conf.finals.dimension[type].param + '.json',
			indicator = GIS.conf.finals.dimension.indicator,
			dataElement = GIS.conf.finals.dimension.dataElement,
			period = GIS.conf.finals.dimension.period,
			organisationUnit = GIS.conf.finals.dimension.organisationUnit,
			params = {};
		
		params[type === indicator.id ? indicator.param : dataElement.param] = this.model[type];
		params[period.param] = this.model.period;
		params[organisationUnit.param] = this.model.parent;
		params.level = this.model.level;
		
		Ext.Ajax.request({
			url: GIS.conf.url.path_gis + dataUrl,
			params: params,
			disableCaching: false,
			scope: this,
			success: function(r) {
				var values = Ext.decode(r.responseText),
					featureMap = {},
					valueMap = {};
					
				if (values.length === 0) {
					alert("no data"); //todo Ext.message.msg(false, GIS.i18n.current_selection_no_data);
					GIS.mask.hide();
					return;
				}
				
				this.layer.features = this.features.slice(0);
				
				for (var i = 0; i < this.layer.features.length; i++) { // feature map (orgunitid : array index)
					featureMap[this.layer.features[i].attributes.id] = i;
				}
				
				var allZeros = true;
				for (var i = 0; i < values.length; i++) { // value map (orgunitid : value)
					if (featureMap.hasOwnProperty(values[i].organisationUnitId)) {
						valueMap[values[i].organisationUnitId] = values[i][GIS.conf.finals.widget.value];
						allZeros = false;
					}
				}
				
				if (allZeros) {
					alert("zero values only");
					GIS.mask.hide();
					return;
				}
				
				for (var f in featureMap) { // set feature value and label string
					if (featureMap.hasOwnProperty(f)) {
						var feature = this.layer.features[featureMap[f]];
						feature.attributes[GIS.conf.finals.widget.value] = valueMap[f];
						feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
					}
				}
				
				this.loadLegend();
			}
		});
		
	},
	
	loadLegend: function() {         
		var options = {
            indicator: GIS.conf.finals.widget.value,
            method: this.model.method,
            numClasses: this.model.classes,
            colors: this.model.colors,
            minSize: this.model.radiusLow,
            maxSize: this.model.radiusHigh
        };
        
        this.coreComp.applyClassification(options, this);
        this.classificationApplied = true;
        
        if (this.update.isOrganisationUnit) {
			GIS.util.map.zoomToVisibleExtent();
		}
        
        this.afterLoad();		
	},
	
	afterLoad: function() {
		this.layer.setLayerOpacity();
		
		this.update.reset();
		
		GIS.cmp.region.east.doLayout();
		
        GIS.mask.hide();
	},

    //classify: function(exception, lockPosition, loaded) {
        //todo if (this.formValidation.validateForm.apply(this, [exception])) {
            //if (!this.layer.features.length && !loaded) {
                //this.loadGeoJson();
            //}
            
            //todo GIS.vars.lockPosition = lockPosition;
            
            //todo if (this.mapView) {
                //if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    //var point = GIS.util.getTransformedPointByXY(this.mapView.longitude, this.mapView.latitude);
                    //GIS.vars.map.setCenter(new OpenLayers.LonLat(point.x, point.y), this.mapView.zoom);
                    //GIS.vars.lockPosition = true;
                //}
                //this.mapView = false;
            //}
            
            //if (this.updateValues) {
    //},
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Thematic1.superclass.onRender.apply(this, arguments);
		
		this.coreComp = new mapfish.GeoStat.Thematic1(this.map, {
            layer: this.layer,
            format: this.format,
            url: this.url,
            requestSuccess: Ext.Function.bind(this.requestSuccess, this),
            requestFailure: Ext.Function.bind(this.requestFailure, this),
            legendDiv: this.legendDiv,
            labelGenerator: this.labelGenerator,
            widget: this
        });
    }
});
