GIS.conf = {
	finals: {
		layer: {
			type_base: 'base',
			type_vector: 'vector'
		},
		feature: {
			type_point_class: 'OpenLayers.Geometry.Point'
		},
		dimension: {
			indicator: {
				id: 'indicator',
				param: 'in'
			},
			dataElement: {
				id: 'dataElement',
				param: 'de'
			},
			period: {
				id: 'period',
				param: 'pe'
			},
			organisationUnit: {
				id: 'organisationUnit',
				param: 'ou'
			}
		},
		widget: {
			value: 'value',
			legendtype_automatic: 'automatic',
			legendtype_predefined: 'predefined'
		},
		mapfish: {
			classify_with_bounds: 1,
			classify_by_equal_intervals: 2,
			classify_by_quantils: 3
		}
	},
	url: {
		path_api: '../../api/',
		path_gis: '../',
		google_terms: 'http://www.google.com/intl/en-US_US/help/terms_maps.html',
		target_blank: '_blank'
	},
	layout: {
		widget: {
			item_width: 262,
			itemlabel_width: 95,
			window_width: 290
		}
	},
	period: {
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
	}
};

GIS.init = {};

GIS.mask;

GIS.util = {
	google: {},
	map: {},
	geojson: {},
	vector: {},
	json: {},
	jsonEncodeString: function(str) {
		return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
	}
};

GIS.map;

GIS.layer = {
	boundary: {
		name: 'boundary',
	},
	thematic1: {
		name: 'thematic1',
	},
	thematic2: {
		name: 'thematic2',
	},
	facility: {
		name: 'facility',
	},
	symbol: {
		name: 'symbol',
	},
	openStreetMap: {
		name: 'OpenStreetMap'
	},
	googleStreets: {
		name: 'Google Streets'
	},
	googleHybrid: {
		name: 'Google Hybrid'
	}
};

GIS.store = {};

GIS.obj = {};

GIS.cmp = {
	menu: {},
	region: {}
};

GIS.gui = {};

GIS.logg = [];

Ext.onReady( function() {	
	Ext.removeNode(document.getElementById('slow')); // remove element when ext is loaded
	
	Ext.Loader.setConfig({enabled: true, disableCaching: false});
	Ext.Loader.setPath('GeoExt', 'scripts/geoext/src/GeoExt');
	Ext.require([
		'GeoExt.panel.Map'
	]);
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	// Init
	
	GIS.init.onInitialize = function(r) {
		var init = Ext.decode(r.responseText);
		
		GIS.init.rootNodes = init.rootNodes;
		
		GIS.init.systemSettings = {
			infrastructuralDataElementGroup: init.systemSettings.infrastructuralDataElementGroup,
			infrastructuralPeriodType: init.systemSettings.infrastructuralPeriodType
		};
		
		GIS.init.security = {
			isAdmin: init.security.isAdmin
		};
	};
	
	Ext.Ajax.request({
		url: GIS.conf.url.path_gis + 'initialize.action',
		success: function(r) {
			GIS.init.onInitialize(r);	
	
	GIS.init.onRender = function() {
		//GIS.layer.googleStreets.layer.setVisibility(false);
	};
	
	GIS.init.afterRender = function() {	
		// Map tools
		document.getElementsByClassName('zoomInButton')[0].innerHTML = '<img src="images/zoomin24.png" />';
		document.getElementsByClassName('zoomOutButton')[0].innerHTML = '<img src="images/zoomout24.png" />';
		document.getElementsByClassName('zoomVisibleButton')[0].innerHTML = '<img src="images/zoomvisible24.png" />';
		document.getElementsByClassName('layersButton')[0].innerHTML = '<img src="images/layers24.png" />';
		
		// Map events
		GIS.map.events.register('mousemove', null, function(e) {
			GIS.map.mouseMove.x = e.clientX;
			GIS.map.mouseMove.y = e.clientY;
		});
                
		GIS.map.events.register('click', null, function(e) {
			if (GIS.map.relocate.active) {
				var el = document.getElementById('mouseposition').childNodes[0],
					coordinates = '[' + el.childNodes[1].data + ',' + el.childNodes[3].data + ']',
					center = GIS.cmp.region.center;

				Ext.Ajax.request({
					url: GIS.conf.url.path_gis + 'updateOrganisationUnitCoordinates.action',
					method: 'POST',
					params: {id: GIS.map.relocate.feature.attributes.id, coordinates: coordinates},
					success: function(r) {
						GIS.map.relocate.active = false;
						GIS.map.relocate.widget.cmp.relocateWindow.destroy();
														
						GIS.map.relocate.feature.move({x: parseFloat(e.clientX - center.x), y: parseFloat(e.clientY - 28)});
						GIS.map.getViewport().style.cursor = 'auto';
						
						console.log(GIS.map.relocate.feature.attributes.name + ' relocated to ' + coordinates);
					}
				});
			}
		});
	};
	
	// Mask
	
	GIS.mask = new Ext.LoadMask(Ext.getBody(), {
		msg: GIS.i18n.loading
	});
	
	// Util
	
	GIS.util.google.openTerms = function() {
		window.open('http://www.google.com/intl/en-US_US/help/terms_maps.html', '_blank');
	};
	
	GIS.util.map.getVisibleVectorLayers = function() {
		var a = [];
		for (var i = 0; i < GIS.map.layers.length; i++) {
			if (GIS.map.layers[i].layerType === GIS.conf.finals.layer.type_vector && GIS.map.layers[i].visibility) {
				a.push(GIS.map.layers[i]);
			}
		}
		return a;
	};
	
	GIS.util.map.getExtendedBounds = function(layers) {
		var bounds = null;
		if (layers.length) {
			bounds = layers[0].getDataExtent();
			if (layers.length > 1) {
				for (var i = 1; i < layers.length; i++) {
					bounds.extend(layers[i].getDataExtent());
				}
			}
		}
		return bounds;
	};
	
	GIS.util.map.zoomToVisibleExtent = function() {
		var bounds = GIS.util.map.getExtendedBounds(GIS.util.map.getVisibleVectorLayers());
		if (bounds) {
			GIS.map.zoomToExtent(bounds);
		}
	};
	
	GIS.util.geojson.decode = function(doc, widget) {
		var geojson = {};
        doc = Ext.decode(doc);
        geojson.type = 'FeatureCollection';
        geojson.crs = {
            type: 'EPSG',
            properties: {
                code: '4326'
            }
        };
        geojson.features = [];
        for (var i = 0; i < doc.geojson.length; i++) {
            geojson.features.push({
                geometry: {
                    type: doc.geojson[i].ty == 1 ? 'MultiPolygon' : 'Point',
                    coordinates: doc.geojson[i].co
                },
                properties: {
                    id: doc.geojson[i].uid,
                    internalId: doc.geojson[i].iid,
                    name: doc.geojson[i].na,
                    value: doc.geojson[i].va || null,
                    hcwc: doc.geojson[i].hc,
                    path: doc.geojson[i].pa
                }
            });
        }
        
        if (widget) {
			widget.tmpModel.hasCoordinatesUp = doc.properties.hasCoordinatesUp;
		}
			
        return geojson;
    };
    
    GIS.util.json.decodeAggregatedValues = function(responseText) {
		responseText = Ext.decode(responseText);
		var values = [];
		
        for (var i = 0; i < responseText.length; i++) {
            values.push({
                oi: responseText[i][0],
                v: responseText[i][1]
            });
        }
        return values;        
    };
    
    GIS.util.vector.getTransformedFeatureArray = function(features) {
        var sourceProjection = new OpenLayers.Projection("EPSG:4326"),
			destinationProjection = new OpenLayers.Projection("EPSG:900913");
        for (var i = 0; i < features.length; i++) {
            features[i].geometry.transform(sourceProjection, destinationProjection);
        }
        return features;
    };
	
	/* Map */
	
	GIS.map = new OpenLayers.Map({
        controls: [
			new OpenLayers.Control.Navigation({
				documentDrag: true
			}),
			new OpenLayers.Control.MousePosition({
				id: 'mouseposition',
				prefix: '<span class="el-fontsize-10"><span class="text-mouseposition-lonlat">LON </span>',
				separator: '<span class="text-mouseposition-lonlat">&nbsp;&nbsp;LAT </span>',
				suffix: '<div id="google-logo" onclick="javascript:GIS.util.google.openTerms();"></div></span>'
			}),
			new OpenLayers.Control.Permalink()
		],
        displayProjection: new OpenLayers.Projection('EPSG:4326'),
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508)
    });
    
    // Track all mouse moves
    GIS.map.mouseMove = {};
    
    // Relocate organisation units
    GIS.map.relocate = {};
    
    // Map tools
    GIS.map.layerController = {};    
    // Zoom in
    GIS.map.layerController.zoomIn = new OpenLayers.Control.Button({
		displayClass: 'olControlButton',
		trigger: function() {
			GIS.map.zoomIn();
		}
	});
    GIS.map.layerController.zoomInPanel = new OpenLayers.Control.Panel({
		defaultControl: GIS.map.layerController.zoomIn
	});	
    GIS.map.layerController.zoomInPanel.addControls([GIS.map.layerController.zoomIn]);
    GIS.map.addControl(GIS.map.layerController.zoomInPanel);
    GIS.map.layerController.zoomInPanel.div.className += ' zoomIn';
    GIS.map.layerController.zoomInPanel.div.childNodes[0].className += ' zoomInButton';
    
    // Zoom out
    GIS.map.layerController.zoomOut = new OpenLayers.Control.Button({
		displayClass: 'olControlButton',
		trigger: function() {
			GIS.map.zoomOut();
		}
	});
    GIS.map.layerController.zoomOutPanel = new OpenLayers.Control.Panel({
		defaultControl: GIS.map.layerController.zoomOut
	});	
    GIS.map.layerController.zoomOutPanel.addControls([GIS.map.layerController.zoomOut]);
    GIS.map.addControl(GIS.map.layerController.zoomOutPanel);
    GIS.map.layerController.zoomOutPanel.div.className += ' zoomOut';
    GIS.map.layerController.zoomOutPanel.div.childNodes[0].className += ' zoomOutButton';
    
    // Zoom to visible extent
    GIS.map.layerController.zoomVisible = new OpenLayers.Control.Button({
		displayClass: 'olControlButton',
		trigger: function() {
			GIS.util.map.zoomToVisibleExtent();
		}
	});
    GIS.map.layerController.zoomVisiblePanel = new OpenLayers.Control.Panel({
		defaultControl: GIS.map.layerController.zoomVisible
	});	
    GIS.map.layerController.zoomVisiblePanel.addControls([GIS.map.layerController.zoomVisible]);
    GIS.map.addControl(GIS.map.layerController.zoomVisiblePanel);
    GIS.map.layerController.zoomVisiblePanel.div.className += ' zoomVisible';
    GIS.map.layerController.zoomVisiblePanel.div.childNodes[0].className += ' zoomVisibleButton';
    
    // Layers
    GIS.map.layerController.layers = new OpenLayers.Control.Button({
		displayClass: 'olControlButton',
		trigger: function() {
			alert('clicky');
		}
	});
    GIS.map.layerController.layersPanel = new OpenLayers.Control.Panel({
		defaultControl: GIS.map.layerController.layers
	});	
    GIS.map.layerController.layersPanel.addControls([GIS.map.layerController.layers]);
    GIS.map.addControl(GIS.map.layerController.layersPanel);
    GIS.map.layerController.layersPanel.div.className += ' layers';
    GIS.map.layerController.layersPanel.div.childNodes[0].className += ' layersButton';
    
    // Base layers
    
    if (window.google) {
        GIS.layer.googleStreets.layer = new OpenLayers.Layer.Google(GIS.layer.googleStreets.name, {
			numZoomLevels: 20,
			animationEnabled: true
		});        
        GIS.layer.googleStreets.layer.layerType = GIS.conf.finals.layer.type_base;
        GIS.map.addLayer(GIS.layer.googleStreets.layer);
        
        GIS.layer.googleHybrid.layer = new OpenLayers.Layer.Google(GIS.layer.googleHybrid.name, {
			type: google.maps.MapTypeId.HYBRID,
			numZoomLevels: 20,
			animationEnabled: true
		});        
        GIS.layer.googleHybrid.layer.layerType = GIS.conf.finals.layer.type_base;
        GIS.map.addLayer(GIS.layer.googleHybrid.layer);
    }
    
    GIS.layer.openStreetMap.layer = new OpenLayers.Layer.OSM(GIS.layer.openStreetMap.name);
    GIS.layer.openStreetMap.layer.layerType = GIS.conf.finals.layer.type_base;
    GIS.map.addLayer(GIS.layer.openStreetMap.layer);
    
    // Vector layers
    
    //GIS.layer.boundary.layer = new OpenLayers.Layer.Vector(GIS.i18n.boundary_layer, {
        //strategies: [
			//new OpenLayers.Strategy.Refresh({
				//force:true
			//})
		//],
        //styleMap: new OpenLayers.StyleMap({
            //'default': new OpenLayers.Style(
                //OpenLayers.Util.applyDefaults(
					//{
						//fillOpacity: 0,
						//strokeColor: '#000',
						//strokeWidth: 1,
						//pointRadius: 5
					//},
					//OpenLayers.Feature.Vector.style['default']
				//)
            //)
        //}),
        //visibility: false,
        //displayInLayerSwitcher: false,
        //layerType: GIS.conf.finals.layer.type_vector,
        //opacity: 1
    //});
    //GIS.map.addLayer(GIS.layer.boundary.layer);
    
    GIS.layer.thematic1.layer = new OpenLayers.Layer.Vector(GIS.i18n.thematic_layer_1, {
        strategies: [
			new OpenLayers.Strategy.Refresh({
				force:true
			})
		],
        styleMap: new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {
						fillOpacity: 1,
						strokeColor: '#fff',
						strokeWidth: 1,
						pointRadius: 5
					},
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            select: new OpenLayers.Style(
                {
					strokeColor: '#fff',
					strokeWidth: 3,
					cursor: 'pointer'
				}
            )
        }),
        visibility: false,
        displayInLayerSwitcher: false,
        layerType: GIS.conf.finals.layer.type_vector,
        layerOpacity: 0.8,
        setLayerOpacity: function(number) {
			if (number) {
				this.layerOpacity = number;
			}
			this.setOpacity(parseFloat(this.layerOpacity));
		}
    });
    GIS.map.addLayer(GIS.layer.thematic1.layer);
    
    // Stores
    
    GIS.store.indicatorGroups = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'indicatorGroups.json?links=false&paging=false',
			reader: {
				type: 'json',
				root: 'indicatorGroups'
			}
		},
		isLoaded: false,
		listeners: {
			load: function() {
				if (!this.isLoaded) {
					//GIS.init.afterLoad();
					this.isLoaded = true;
				}
				this.sort('name', 'ASC');
			}
		}
	});
    
    GIS.store.dataElementGroups = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'dataElementGroups.json?links=false&paging=false',
			reader: {
				type: 'json',
				root: 'dataElementGroups'
			}
		},
		isLoaded: false,
		listeners: {
			load: function() {
				if (!this.isLoaded) {
					//GIS.init.afterLoad();
					this.isLoaded = true;
				}
				this.sort('name', 'ASC');
			}
		}
	});
	
	GIS.store.periodTypes = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		data: GIS.conf.period.periodTypes
	}),
    
    GIS.store.organisationUnitLevels = Ext.create('Ext.data.Store', {
		fields: ['id', 'name', 'level'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'organisationUnitLevels.json?viewClass=detailed&links=false&paging=false',
			reader: {
				type: 'json',
				root: 'organisationUnitLevels'
			}
		},
		isLoaded: false,
		listeners: {
			load: function() {
				if (!this.isLoaded) {
					//GIS.init.afterLoad();
					this.isLoaded = true;
				}
				this.sort('level', 'ASC');
			}
		}
	});
        
    GIS.store.infrastructuralPeriodsByType = new Ext.data.JsonStore({
        url: GIS.conf.url.path_gis + 'getPeriodsByPeriodType.action',
        root: 'periods',
        fields: ['id', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
			load: function() {
				if (!this.isLoaded) {
					this.isLoaded = true;
				}
			}
        }
    });
    
    // Objects
    
    GIS.obj.LayerMenu = function(layerName, cls) {
		return Ext.create('Ext.menu.Menu', {
			shadow: false,
			showSeparator: false,
			itemsXableAlways: function() {
				Ext.Array.each(this.items.items, function(item) {
					if (!item.alwaysEnabled) {
						item.disable();
					}
				});
			},
			itemsXableHistory: function() {
				Ext.each(this.items, function(item) {
					if (!item.alwaysEnabled && !item.historyEnabled) {
						item.disable();
					}
				});
			},
			items: [
				{
					text: 'Edit layer..',//i18n
					iconCls: 'gis-menu-item-icon-edit',
					cls: 'gis-menu-item-first',
					alwaysEnabled: true,
					handler: function() {
						GIS.layer[layerName].window.show();
					}
				},
				{
					xtype: 'menuseparator',
					alwaysEnabled: true
				},
				{
					text: 'Refresh',//i18n
					iconCls: 'gis-menu-item-icon-refresh'
				},
				{
					text: 'Clear',//i18n
					iconCls: 'gis-menu-item-icon-clear'
				},
				{
					xtype: 'menuseparator',
					alwaysEnabled: true
				},
				{
					text: 'Labels..',//i18n
					iconCls: 'gis-menu-item-icon-labels'
				},
				{
					text: 'Filter..',//i18n
					iconCls: 'gis-menu-item-icon-filter'
				},
				{
					text: 'Search..',//i18n
					iconCls: 'gis-menu-item-icon-search'
				},
				{
					xtype: 'menuseparator',
					alwaysEnabled: true
				},
				{
					text: 'Opacity',//i18n
					iconCls: 'gis-menu-item-icon-opacity',
					cls: 'gis-menu-item-last',
					menu: Ext.create('Ext.menu.Menu', {
						shadow: false,
						showSeparator: false
					})
				}
			],
			listeners: {
				added: function() {
					GIS.cmp.menu[layerName] = this;
				},
				afterrender: function() {
					this.getEl().addCls('gis-toolbar-btn-menu');
					if (cls) {
						this.getEl().addCls(cls);
					}
										
					this.itemsXableAlways();
				}
			}
		});
	};
    
	// User interface
	
	GIS.layer.thematic1.widget = Ext.create('mapfish.widgets.geostat.Thematic1', {
        map: GIS.map,
        layer: GIS.layer.thematic1.layer,
        legendDiv: 'thematic1Legend'
    });
    
    GIS.layer.thematic1.window = Ext.create('Ext.window.Window', {
		title: '<span id="window-thematic1-title">' + GIS.i18n.thematic_layer + ' 1' + '</span>',
		layout: 'fit',
		cls: 'gis-container-default',
        closeAction: 'hide',
        width: GIS.conf.layout.widget.window_width,
        resizable: false,
        isRendered: false,
        isCollapsed: false,
        items: GIS.layer.thematic1.widget,
        bbar: [
			'->',
			{
				text: 'Update',
				handler: function() {
					GIS.layer.thematic1.widget.execute();
				}
			}
		],
		listeners: {
			show: function(w) {
				if (!this.isRendered) {
					this.setPosition(4,35);
					this.isRendered = true;
				}
			}
		}
	});
	
	GIS.gui.viewport = Ext.create('Ext.container.Viewport', {
		layout: 'border',		
		items: [
			{
				region: 'east',
				width: 200,
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
				items: [
                    {
                        title: 'Thematic layer 1 legend', //i18n
                        contentEl: 'thematic1Legend',
                        bodyStyle: 'padding: 6px; border: 0 none'
                    },
                    {
                        title: 'Thematic layer 2 legend', //i18n
                        contentEl: 'thematic2Legend',
                        bodyStyle: 'padding: 6px; border: 0 none'
                    }
				],
				listeners: {
					added: function() {
						GIS.cmp.region.east = this;
					},
                    collapse: function() {
                        GIS.cmp.region.center.cmp.tbar.resize.setText('<<<');
                    },
                    expand: function() {
                        GIS.cmp.region.center.cmp.tbar.resize.setText('>>>');
                    }
				}
			},
            {
                xtype: 'gx_mappanel',
                region: 'center',
                map: GIS.map,
                height: 31,
                cmp: {
					tbar: {}
				},
                tbar: {
					defaults: {
						height: 26
					},
					items: [
						{
							iconCls: 'gis-btn-icon-' + GIS.layer.boundary.name,
							menu: new GIS.obj.LayerMenu(GIS.layer.boundary.name, 'gis-toolbar-btn-menu-first'),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layer.thematic1.name,
							menu: new GIS.obj.LayerMenu(GIS.layer.thematic1.name),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layer.thematic2.name,
							menu: new GIS.obj.LayerMenu(GIS.layer.thematic2.name),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layer.facility.name,
							menu: new GIS.obj.LayerMenu(GIS.layer.facility.name),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layer.symbol.name,
							menu: new GIS.obj.LayerMenu(GIS.layer.symbol.name),
							width: 26
						},
						{
							text: 'Favorites', //i18n
							menu: {}
						},
						{
							text: 'Legend', //i18n
							menu: {}
						},
						{
							text: 'Download', //i18n
							menu: {}
						},
						{
							text: 'expand', //i18n
							handler: function() {
								GIS.layer.thematic1.widget.cmp.parent.selectPath('/root/ImspTQPwCqd/Vth0fbpFcsO/CF243RPvNY7', 'id');
							}
						},
						'->',
						{
							text: 'Exit', //i18n
							handler: function() {
								alert('Exit');
							}
						},
						{
							text: '>>>', //i18n
							handler: function() {
								GIS.cmp.region.east.toggleCollapse();
							},
							listeners: {
								render: function() {
									GIS.cmp.region.center.cmp.tbar.resize = this;
								}
							}
						}
						
					]
				},
				listeners: {
					added: function() {
						GIS.cmp.region.center = this;
					}
				}
            }
		],
		listeners: {
			render: GIS.init.onRender,
			afterrender: GIS.init.afterRender
		}
	});
	
	}});
});
