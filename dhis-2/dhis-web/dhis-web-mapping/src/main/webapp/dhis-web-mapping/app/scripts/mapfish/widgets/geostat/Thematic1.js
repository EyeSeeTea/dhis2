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
    loadMask: false,
    labelGenerator: null,
    
    // Properties
        
    config: {},
    
    tmpModel: {},
    
    model: {},
    
    cmp: {},
    
    togglers: {},
    
    features: [],
    
    selectHandlers: {},
	
	//organisationUnitSelection: {
		//parent: {
			//id: null,
			//name: null,
			//level: null
		//},
		//level: {
			//level: null,
			//name: null
		//},
		//setValues: function(parentId, parentName, parentLevel, levelLevel, levelName) {
			//this.parent.id = parentId || this.parent.id;
			//this.parent.name = parentName || this.parent.name;
			//this.parent.level = parentLevel || this.parent.level;
			//this.level.level = levelLevel || this.level.level;
			//this.level.name = levelName || this.level.name;
		//},
		//setValuesOnDrillDown: function(parentId, parentName) {
			//this.parent.id = parentId;
			//this.parent.name = parentName;
			//this.parent.level = this.level.level;
			//this.level.level++;
			//this.level.name = GIS.store.organisationUnitLevel.getAt(
				//GIS.store.organisationUnitLevels.find('level', this.level.level)).data.name;
		//},
		//reset: function() {
			//this.parent.id = null;
			//this.parent.name = null;
			//this.parent.level = null;
			//this.level.level = null;
			//this.level.name = null;
		//}			
	//},
    
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
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			listeners: {
				load: function() {
					if (!this.isLoaded) {
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
			listeners: {
				beforeload: function() {
					if (this.param) {
						this.proxy.url = GIS.conf.url.path_api +  'dataElementGroups/' + this.param + '.json?links=false&paging=false';
					}
					else {
						return false;
					}
				},
				load: function() {
					if (!this.isLoaded) {
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
		}),
		
		infrastructuralDataElementValues: Ext.create('Ext.data.Store', {
			fields: ['dataElementName', 'value'],
			proxy: {
				type: 'ajax',
				url: '../getInfrastructuralDataElementMapValues.action',
				reader: {
					type: 'json',
					root: 'mapValues'
				}
			},
			//sortInfo: {field: 'dataElementName', direction: 'ASC'},
			autoLoad: false,
			isLoaded: false,
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}
				}
			}
		})
	},
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        var doc = request.responseXML,
			format = new OpenLayers.Format.GeoJSON();
			
        if (!doc || !doc.documentElement) {
            doc = request.responseText;
        }
        
        if (doc.length) {
            doc = GIS.util.geojson.decode(doc, this);
        }
        else {
			alert("no coordinates"); //todo //i18n
		}
        
        this.layer.removeFeatures(this.layer.features);
        this.layer.addFeatures(format.read(doc));
		this.layer.features = GIS.util.vector.getTransformedFeatureArray(this.layer.features);
        this.features = this.layer.features.slice(0);
        
        this.loadData();
    },

    requestFailure: function(request) {
        GIS.logg.push(request.status, request.statusText);        
        console.log(request.status, request.statusText);
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
		
		this.createSelectHandlers();
		
		this.coreComp = new mapfish.GeoStat.Thematic1(this.map, {
            layer: this.layer,
            format: this.format,
            url: this.url,
            requestSuccess: Ext.bind(this.requestSuccess, this),
            requestFailure: Ext.bind(this.requestFailure, this),
            legendDiv: this.legendDiv,
            labelGenerator: this.labelGenerator,
            widget: this
        });
		
		mapfish.widgets.geostat.Thematic1.superclass.initComponent.apply(this);
    },
    
    createUtils: function() {
		var that = this;
		
		this.togglers.valueType = function(valueType) {
			if (valueType === GIS.conf.finals.dimension.indicator.id) {
				that.cmp.indicatorGroup.show();
				that.cmp.indicator.show();
				that.cmp.dataElementGroup.hide();
				that.cmp.dataElement.hide();
			}
			else if (valueType === GIS.conf.finals.dimension.dataElement.id) {
				that.cmp.indicatorGroup.hide();
				that.cmp.indicator.hide();
				that.cmp.dataElementGroup.show();
				that.cmp.dataElement.show();
			}
		};
		
		this.togglers.legendType = function(legendType) {
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
            value: GIS.conf.finals.dimension.indicator.id,
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
						this.togglers.valueType(cb.getValue());
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
				added: function() {
					this.store.cmp.push(this);
				},
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.indicator.clearValue();
                        
                        var store = this.cmp.indicator.store;
                        store.proxy.url = GIS.conf.url.path_api +  'indicatorGroups/' + cb.getValue() + '.json?links=false&paging=false';
                        store.load({
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
            queryMode: 'local',
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            listConfig: {loadMask: false},
            selectFirst: function() {
				this.setValue(this.store.getAt(0).data.id);
				this.scope.config.updateData = true;
			},
            store: this.store.indicatorsByGroup,
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						this.config.updateData = true;
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
            store: GIS.store.dataElementGroups,
            listeners: {
				added: function() {
					this.store.cmp.push(this);
				},
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.indicator.clearValue();
                        
                        var store = this.cmp.dataElement.store;
                        store.proxy.url = GIS.conf.url.path_api +  'dataElementGroups/' + cb.getValue() + '.json?links=false&paging=false';
                        store.load({
							scope: this,
							callback: function() {
								this.cmp.dataElement.selectFirst();
							}
						});
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
            listConfig: {loadMask: false},
            hidden: true,
            store: this.store.dataElementsByGroup,
            selectFirst: function() {
				this.setValue(this.store.getAt(0).data.id);
				this.scope.config.updateData = true;
			},
            listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateData = true;
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
						var pt = new PeriodType(),
							periodType = this.cmp.periodType.getValue();
						
						var periods = pt.get(periodType).generatePeriods({
							offset: this.cmp.periodType.periodOffset,
							filterFuturePeriods: true,
							reversePeriods: true
						});
						
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
            store: this.store.periodsByType,
            scope: this,
            selectFirst: function() {
				this.setValue(this.store.getAt(0).data.id);
				this.scope.config.updateData = true;
			},
			listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateData = true;
					}
				}
			}	
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
						this.togglers.legendType(cb.getValue());
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
						this.config.updateLegend = true;
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
						this.config.updateLegend = true;
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
						this.config.updateLegend = true;
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
							
							this.config.updateLegend = true;
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
							
							this.config.updateLegend = true;
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
            value: 5,
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.config.updateLegend = true;
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
						this.config.updateLegend = true;
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
						this.config.updateOrganisationUnit = true;
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
			isRendered: false,
			path: null,
			selectTreePath: function(path) {
				if (this.isRendered) {
					this.selectPath(path);
				}
				else {
					this.path = path;
				}
			},				
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
			}),
			listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateOrganisationUnit = true;
					}
				},
				afterrender: function() {
					this.isRendered = true;
					
					if (this.path) {
						this.selectPath(this.path);
					}
					else {
						this.getSelectionModel().select(0);
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
    
    createSelectHandlers: function() {
        var that = this,
			window,
			menu,
			infrastructuralPeriod,
			onHoverSelect,
			onHoverUnselect,
			onClickSelect;
        
        onHoverSelect = function fn(feature) {
			if (window) {
				window.destroy();
			}
			window = Ext.create('Ext.window.Window', {
				cls: 'gis-window-widget-feature',
				preventHeader: true,
				shadow: false,
				resizable: false,
				items: {
					html: feature.attributes.label
				}
			});
			
			window.show();
			
			var x = window.getPosition()[0];
			window.setPosition(x, 32);
        };
        
        onHoverUnselect = function fn(feature) {
			window.destroy();
        };
        
        onClickSelect = function fn(feature) {
			var showInfo,				
				showRelocate,
				drill,
				menu,
				isPoint = feature.geometry.CLASS_NAME === GIS.conf.finals.feature.type_point_class;
			
			// Relocate
			showRelocate = function() {
				if (that.cmp.relocateWindow) {
					that.cmp.relocateWindow.destroy();
				}
				
				that.cmp.relocateWindow = Ext.create('Ext.window.Window', {
					title: 'Relocate facility',
					layout: 'fit',
					iconCls: 'gis-window-title-icon-relocate',
					cls: 'gis-container-default',
					items: {
						html: feature.attributes.name,
						cls: 'gis-container-inner'
					},
					bbar: [
						'->',
						{
							xtype: 'button',
							hideLabel: true,
							text: GIS.i18n.cancel,
							handler: function() {
								GIS.map.relocate.active = false;
								that.cmp.relocateWindow.destroy();
								GIS.map.getViewport().style.cursor = 'auto';
							}
						}
					],
					listeners: {
						close: function() {
							GIS.map.relocate.active = false;
							GIS.map.getViewport().style.cursor = 'auto';
						}
					}
				});
					
				that.cmp.relocateWindow.show();
				
				var minWidth = 220,
					width = window.getWidth();
				window.setWidth(width < minWidth ? minWidth : width);
				
				GIS.util.gui.window.setPositionTopRight(that.cmp.relocateWindow);
			};
			
			// Infrastructural data
			showInfo = function() {
				Ext.Ajax.request({
					url: GIS.conf.url.path_gis + 'getFacilityInfo.action',
					params: {
						id: feature.attributes.id
					},
					success: function(r) {
						var ou = Ext.decode(r.responseText);
						
						if (that.cmp.infrastructuralWindow) {
							that.cmp.infrastructuralWindow.destroy();
						}
						
						that.cmp.infrastructuralWindow = Ext.create('Ext.window.Window', {
							title: 'Facility information sheet', //i18n
							layout: 'column',
							iconCls: 'gis-window-title-icon-information',
							cls: 'gis-container-default',
							//width: GIS.conf.layout.widget.window_width + 178,
							width: 460,
							height: 460, //todo
							isRendered: false,
							period: null,
							items: [
								{
									cls: 'gis-container-inner',
									columnWidth: 0.5,
									bodyStyle: 'padding-right:4px',
									items: [
										{
											html: GIS.i18n.name,
											cls: 'gis-panel-html-title'
										},
										{
											html: feature.attributes.name,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.type,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.ty,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.code,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.co,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.address,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.ad,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.contact_person,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.cp,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.email,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.em,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.phone_number,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.pn,
											cls: 'gis-panel-html'
										}
									]
								},
								{
									xtype: 'form',
									cls: 'gis-container-inner gis-form-widget',
									columnWidth: 0.5,
									bodyStyle: 'padding-left:4px',
									items: [
										{
											html: GIS.i18n.infrastructural_data,
											cls: 'gis-panel-html-title'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											xtype: 'combo',
											fieldLabel: GIS.i18n.period,
											editable: false,
											valueField: 'id',
											displayField: 'name',
											forceSelection: true,
											width: 212, //todo
											labelWidth: 70,
											store: GIS.store.infrastructuralPeriodsByType,
											lockPosition: false,
											listeners: {
												select: function() {
													infrastructuralPeriod = this.getValue();
													
													that.store.infrastructuralDataElementValues.load({
														params: {
															periodId: infrastructuralPeriod,
															organisationUnitId: feature.attributes.internalId
														}
													});
												}
											}
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											xtype: 'gridpanel',
											cls: 'gis-grid',
											height: 300, //todo
											width: 212,
											scroll: 'vertical',
											columns: [
												{
													id: 'dataElementName',
													text: 'Data element',
													dataIndex: 'dataElementName',
													sortable: true,
													width: 160
												},
												{
													id: 'value',
													header: 'Value',
													dataIndex: 'value',
													sortable: true,
													width: 52
												}
											],
											disableSelection: true,
											store: that.store.infrastructuralDataElementValues
										}
									]
								}
							],
							listeners: {
								show: function() {									
									if (infrastructuralPeriod) {
										this.down('combo').setValue(infrastructuralPeriod);
										that.store.infrastructuralDataElementValues.load({
											params: {
												periodId: infrastructuralPeriod,
												organisationUnitId: feature.attributes.internalId
											}
										});
									}
								}
							}
						});
						
						that.cmp.infrastructuralWindow.show();
						GIS.util.gui.window.setPositionTopRight(that.cmp.infrastructuralWindow);
					}
				});
			};
			
			// Drill or float
			drill = function(direction) {
				//if (GIS.vars.locateFeatureWindow) {
					//GIS.vars.locateFeatureWindow.destroy();
				//}
				
				var callback = function() {
					var store = GIS.store.organisationUnitLevels;
					
					if (direction === 'up') {
						var rootNode = GIS.init.rootNodes[0];
						
						that.config.level = that.model.level - 1;
						that.config.levelName = store.getAt(store.find('level', that.config.level)).data.name;
						that.config.parentId = rootNode.id;
						that.config.parentName = rootNode.text;
						that.config.parentLevel = rootNode.level;
						
						that.cmp.parent.selectTreePath('/root/' + GIS.init.rootNodes[0].id);
					}
					else if (direction === 'down') {
						that.config.level = that.model.level + 1;
						that.config.levelName = store.getAt(store.find('level', that.config.level)).data.name;
						that.config.parentId = feature.attributes.id;
						that.config.parentName = feature.attributes.name;
						that.config.parentLevel = that.model.level;
						
						that.cmp.parent.selectTreePath('/root' + feature.attributes.path);
					}					
					
					that.cmp.level.setValue(that.config.level);					
					that.config.updateOrganisationUnit = true;
					
					that.execute();
				};
				
				if (GIS.store.organisationUnitLevels.isLoaded) {
					callback();
				}
				else {
					GIS.store.organisationUnitLevels.load({callback: function() {
						callback();
					}});
				}
			};
			
			// Menu
			var menuItems = [
				Ext.create('Ext.menu.Item', {
					text: 'Drill down',
					iconCls: 'gis-menu-item-icon-drill',
					cls: 'gis-menu-item-first',
					disabled: !feature.attributes.hcwc,
					scope: this,
					handler: function() {
						drill('down');
					}
				}),
				Ext.create('Ext.menu.Item', {
					text: 'Float up',
					iconCls: 'gis-menu-item-icon-float',
					disabled: !that.model.hasCoordinatesUp,
					scope: this,
					handler: function() {
						drill('up');
					}
				})
			];
			
			if (isPoint) {
				menuItems.push({
					xtype: 'menuseparator'
				});
				
				menuItems.push( Ext.create('Ext.menu.Item', {
					text: GIS.i18n.relocate,
					iconCls: 'gis-menu-item-icon-relocate',
					disabled: !GIS.init.security.isAdmin,
					handler: function(item) {
						GIS.map.relocate.active = true;
						GIS.map.relocate.widget = that;
						GIS.map.relocate.feature = feature;
						GIS.map.getViewport().style.cursor = 'crosshair';
						showRelocate();
					}
				}));
				
				menuItems.push( Ext.create('Ext.menu.Item', {
					text: 'Show information', //i18n
					iconCls: 'gis-menu-item-icon-information',
					handler: function(item) {
						if (GIS.store.infrastructuralPeriodsByType.isLoaded) {
							showInfo();
						}
						else {
							GIS.store.infrastructuralPeriodsByType.load({
								params: {
									name: GIS.init.systemSettings.infrastructuralPeriodType
								},
								callback: function() {
									showInfo();
								}
							});
						}
					}
				}));
			}
			
			menuItems[menuItems.length - 1].addCls('gis-menu-item-last');
			
			menu = new Ext.menu.Menu({
				shadow: false,
				showSeparator: false,
				defaults: {
					bodyStyle: 'padding-right:6px'
				},
				items: menuItems,
				listeners: {
					afterrender: function() {
						this.getEl().addCls('gis-toolbar-btn-menu');
					}
				}
			});
            
            menu.showAt([GIS.map.mouseMove.x, GIS.map.mouseMove.y]);
        };
        
        this.selectHandlers = new OpenLayers.Control.newSelectFeature(this.layer, {
			onHoverSelect: onHoverSelect,
			onHoverUnselect: onHoverUnselect,
			onClickSelect: onClickSelect
		});
        
        GIS.map.addControl(this.selectHandlers);
        this.selectHandlers.activate();
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
    
    formValues: {
        getLegendInfo: function() {
            return {
                name: this.tmpModel.valueType === 'indicator' ? this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
                time: this.cmp.period.getRawValue(),
                map: this.tmpModel.levelName + ' / ' + this.tmpModel.parentName
            };
        }
        //,
        
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
	},
	
	setConfig: function(config) {
		this.config.valueType = config.valueType;
		this.config.indicatorGroup = config.indicatorGroup;
		this.config.indicator = config.indicator;
		this.config.dataElementGroup = config.dataElementGroup;
		this.config.dataElement = config.dataElement;
		this.config.periodType = config.periodType;
		this.config.period = config.period;
		this.config.legendType = config.legendType;
		this.config.legendSet = config.legendSet;
		this.config.classes = config.classes;
		this.config.method = config.method;
		this.config.colorLow = config.colorLow;
		this.config.colorHigh = config.colorHigh;
		this.config.radiusLow = config.radiusLow;
		this.config.radiusHigh = config.radiusHigh;
		this.config.level = config.level;
		this.config.levelName = config.levelName;
		this.config.parentId = config.parentId;
		this.config.parentName = config.parentName;
		this.config.parentLevel = config.parentLevel;
		this.config.updateOrganisationUnit = true;
		this.config.updateData = false;
		this.config.updateLegend = false;		
		this.config.updateGui = true;
	},
	
	setGui: function() {
		var model = this.tmpModel;
		
		// Value type
		this.cmp.valueType.setValue(model.valueType);
		
		// Indicator and data element
		this.togglers.valueType(model.valueType);
		
		var	indeGroupView = model.valueType === GIS.conf.finals.dimension.indicator.id ? this.cmp.indicatorGroup : this.cmp.dataElementGroup,
			indeGroupStore = indeGroupView.store,
			indeGroupValue = model.valueType === GIS.conf.finals.dimension.indicator.id ? model.indicatorGroup : model.dataElementGroup,
			
			indeStore = model.valueType === GIS.conf.finals.dimension.indicator.id ? this.store.indicatorsByGroup : this.store.dataElementsByGroup,
			indeView = model.valueType === GIS.conf.finals.dimension.indicator.id ? this.cmp.indicator : this.cmp.dataElement,
			indeValue = model.valueType === GIS.conf.finals.dimension.indicator.id ? model.indicator : model.dataElement;
			
		indeGroupStore.loadFn( function() {
			indeGroupView.setValue(indeGroupValue);
		});
		
		indeStore.proxy.url = GIS.conf.url.path_api + model.valueType + 'Groups/' + indeGroupValue + '.json?links=false&paging=false';
		indeStore.loadFn( function() {
			indeView.setValue(indeValue);
		});
	},
    	
	getModel: function() {
		var level = this.cmp.level,
			parent = this.cmp.parent.getSelectionModel().getSelection() || [];
		
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
			parentId: parent.length ? parent[0].raw.id : null,
			parentName: parent.length ? parent[0].raw.text : null,
			parentLevel: parent.length ? parent[0].raw.level : null,
			updateOrganisationUnit: false,
			updateData: false,
			updateLegend: false,
			updateGui: false
		};
		
		model.valueType = this.config.valueType || model.valueType;
		model.indicatorGroup = this.config.indicatorGroup || model.indicatorGroup;
		model.indicator = this.config.indicator || model.indicator;
		model.dataElementGroup = this.config.dataElementGroup || model.dataElementGroup;
		model.dataElement = this.config.dataElement || model.dataElement;
		model.periodType = this.config.periodType || model.periodType;
		model.period = this.config.period || model.period;
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
		model.parentId = this.config.parentId || model.parentId;
		model.parentName = this.config.parentName || model.parentName;
		model.parentLevel = this.config.parentLevel || model.parentLevel;
		model.updateOrganisationUnit = this.config.updateOrganisationUnit === undefined ? false : this.config.updateOrganisationUnit;
		model.updateData = this.config.updateData === undefined ? false : this.config.updateData;
		model.updateLegend = this.config.updateLegend === undefined ? false : this.config.updateLegend;
		model.updateGui = this.config.updateGui === undefined ? false : this.config.updateGui;
		
		return model;
	},
	
	validateModel: function(model) {
		if (model.valueType === GIS.conf.finals.dimension.indicator.id) {
			if (!model.indicatorGroup || !Ext.isString(model.indicatorGroup)) {
				GIS.logg.push([model.indicatorGroup, this.xtype + '.indicatorGroup: string']);
				return false;
			}
			if (!model.indicator || !Ext.isString(model.indicator)) {
				GIS.logg.push([model.indicator, this.xtype + '.indicator: string']);
				return false;
			}
		}
		else if (model.valueType === GIS.conf.finals.dimension.dataElement.id) {
			if (!model.dataElementGroup || !Ext.isString(model.dataElementGroup)) {
				GIS.logg.push([model.dataElementGroup, this.xtype + '.dataElementGroup: string']);
				return false;
			}
			if (!model.dataElement || !Ext.isString(model.dataElement)) {
				GIS.logg.push([model.dataElement, this.xtype + '.dataElement: string']);
				return false;
			}
		}
		
		if (!model.periodType || !Ext.isString(model.periodType)) {
			GIS.logg.push([model.periodType, this.xtype + '.periodType: string']);
			return false;
		}
		if (!model.period || !Ext.isString(model.period)) {
			GIS.logg.push([model.period, this.xtype + '.period: string']);
			return false;
		}
		
		if (model.legendType === GIS.conf.finals.widget.legendtype_automatic) {
			if (!model.classes || !Ext.isNumber(model.classes)) {
				GIS.logg.push([model.classes, this.xtype + '.classes: number']);
				return false;
			}
			if (!model.method || !Ext.isNumber(model.method)) {
				GIS.logg.push([model.method, this.xtype + '.method: number']);
				return false;
			}
			if (!model.colorLow || !Ext.isString(model.colorLow)) {
				GIS.logg.push([model.colorLow, this.xtype + '.colorLow: string']);
				return false;
			}
			if (!model.radiusLow || !Ext.isNumber(model.radiusLow)) {
				GIS.logg.push([model.radiusLow, this.xtype + '.radiusLow: number']);
				return false;
			}
			if (!model.colorHigh || !Ext.isString(model.colorHigh)) {
				GIS.logg.push([model.colorHigh, this.xtype + '.colorHigh: string']);
				return false;
			}
			if (!model.radiusHigh || !Ext.isNumber(model.radiusHigh)) {
				GIS.logg.push([model.radiusHigh, this.xtype + '.radiusHigh: number']);
				return false;
			}
		}
		else if (model.legendType === GIS.conf.finals.widget.legendtype_predefined) {			
			if (!model.legendSet || !Ext.isString(model.legendSet)) {
				GIS.logg.push([model.legendSet, this.xtype + '.legendSet: string']);
				return false;
			}
		}
		
		if (!model.level || !Ext.isNumber(model.level)) {
			GIS.logg.push([model.level, this.xtype + '.level: number']);
			return false;
		}
		if (!model.levelName || !Ext.isString(model.levelName)) {
			GIS.logg.push([model.levelName, this.xtype + '.levelName: string']);
			return false;
		}
		if (!model.parentId || !Ext.isString(model.parentId)) {
			GIS.logg.push([model.parentId, this.xtype + '.parentId: string']);
			return false;
		}
		if (!model.parentName || !Ext.isString(model.parentName)) {
			GIS.logg.push([model.parentName, this.xtype + '.parentName: string']);
			return false;
		}
		if (!model.parentLevel || !Ext.isNumber(model.parentLevel)) {
			GIS.logg.push([model.parentLevel, this.xtype + '.parentLevel: number']);
			return false;
		}
		
		if (!model.updateOrganisationUnit && !model.updateData && !model.updateLegend) {			
			GIS.logg.push([model.updateOrganisationUnit, model.updateData, model.updateLegend, this.xtype + '.update ou/data/legend: true || true || true']);
			return false;
		}
		
		return true;
	},
	
    loadOrganisationUnits: function() {
        var url = GIS.conf.url.path_gis + 'getGeoJson.action?' +
            'parentId=' + this.tmpModel.parentId +
            '&level=' + this.tmpModel.level;
        this.setUrl(url);
    },
    
    loadData: function() {
		var type = this.tmpModel.valueType,
			dataUrl = '../api/mapValues/' + GIS.conf.finals.dimension[type].param + '.json',
			indicator = GIS.conf.finals.dimension.indicator,
			dataElement = GIS.conf.finals.dimension.dataElement,
			period = GIS.conf.finals.dimension.period,
			organisationUnit = GIS.conf.finals.dimension.organisationUnit,
			params = {};
		
		params[type === indicator.id ? indicator.param : dataElement.param] = this.tmpModel[type];
		params[period.param] = this.tmpModel.period;
		params[organisationUnit.param] = this.tmpModel.parentId;
		params.level = this.tmpModel.level;
		
		Ext.Ajax.request({
			url: GIS.conf.url.path_gis + dataUrl,
			params: params,
			disableCaching: false,
			scope: this,
			success: function(r) {
				var values = Ext.decode(r.responseText),
					featureMap = {},
					valueMap = {},
					features = [];
					
				if (values.length === 0) {
					alert("no data"); //todo Ext.message.msg(false, GIS.i18n.current_selection_no_data);
					GIS.mask.hide();
					return;
				}
				
				for (var i = 0; i < this.layer.features.length; i++) {
					var iid = this.layer.features[i].attributes.internalId;
					featureMap[iid] = true;
				}
				for (var i = 0; i < values.length; i++) {
					var iid = values[i].organisationUnitId,
						value = values[i].value;						
					valueMap[iid] = value;
				}
				
				for (var i = 0; i < this.layer.features.length; i++) {
					var feature = this.layer.features[i],
						iid = feature.attributes.internalId;						
					if (featureMap.hasOwnProperty(iid) && valueMap.hasOwnProperty(iid)) {
						feature.attributes.value = valueMap[iid];
						feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
						features.push(feature);
					}
				}
				
				this.layer.features = features;
				
				this.loadLegend();
			}
		});
	},
	
	loadLegend: function() {
		var options = {
            indicator: GIS.conf.finals.widget.value,
            method: this.tmpModel.method,
            numClasses: this.tmpModel.classes,
            colors: this.tmpModel.colors,
            minSize: this.tmpModel.radiusLow,
            maxSize: this.tmpModel.radiusHigh
        };

        this.coreComp.applyClassification(options, this);
        this.classificationApplied = true;
        
        this.afterLoad();		
	},
	
    execute: function() {
		this.tmpModel = this.getModel();
		
		if (!this.validateModel(this.tmpModel)) {
			alert("validation failed"); //todo
			return;
		}
				
		GIS.mask.msg = GIS.i18n.loading;
		GIS.mask.show();
		
		if (this.tmpModel.updateGui) {
			this.setGui();
		}
		
		if (this.tmpModel.updateOrganisationUnit) {
			this.loadOrganisationUnits();
		}
		else if (this.tmpModel.updateData) {
			this.loadData();
		}
		else if (this.tmpModel.updateLegend) {
			this.loadLegend();
		}
	},
	
	afterLoad: function() {		
		this.model = this.tmpModel;
		this.config = {};
        
        if (this.model.updateOrganisationUnit) {
			GIS.util.map.zoomToVisibleExtent();
		}
		
		this.layer.setLayerOpacity();
		
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
		
		//console.log(ct, position);
        mapfish.widgets.geostat.Thematic1.superclass.onRender.apply(this, arguments);
		
		//this.coreComp = new mapfish.GeoStat.Thematic1(this.map, {
            //layer: this.layer,
            //format: this.format,
            //url: this.url,
            //requestSuccess: Ext.Function.bind(this.requestSuccess, this),
            //requestFailure: Ext.Function.bind(this.requestFailure, this),
            //legendDiv: this.legendDiv,
            //labelGenerator: this.labelGenerator,
            //widget: this
        //});
    }
});
