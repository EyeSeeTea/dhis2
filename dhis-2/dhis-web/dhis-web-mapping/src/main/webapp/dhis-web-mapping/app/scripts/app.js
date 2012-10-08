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
		},
		tool: {
			item_width: 222,
			itemlabel_width: 95,
			window_width: 250
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
	},
	opacity: {
		items: [
			{text: '0.1', iconCls: 'gis-menu-item-icon-opacity10'},
			{text: '0.2', iconCls: 'gis-menu-item-icon-opacity20'},
			{text: '0.3', iconCls: 'gis-menu-item-icon-opacity30'},
			{text: '0.4', iconCls: 'gis-menu-item-icon-opacity40'},
			{text: '0.5', iconCls: 'gis-menu-item-icon-opacity50'},
			{text: '0.6', iconCls: 'gis-menu-item-icon-opacity60'},
			{text: '0.7', iconCls: 'gis-menu-item-icon-opacity70'},
			{text: '0.8', iconCls: 'gis-menu-item-icon-opacity80'},
			{text: '0.9', iconCls: 'gis-menu-item-icon-opacity90'},
			{text: '1.0', iconCls: 'gis-menu-item-icon-opacity100'}
		]
	}
};

GIS.init = {};

GIS.mask;

GIS.util = {
	google: {},
	map: {},
	store: {},
	geojson: {},
	vector: {},
	json: {},
	jsonEncodeString: function(str) {
		return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
	},
	gui: {
		window: {},
		combo: {}
	}
};

GIS.map;

GIS.base = {
	boundary: {
		id: 'boundary',
		name: 'Boundary layer', //i18n
		legendDiv: 'boundaryLegend'
	},
	thematic1: {
		id: 'thematic1',
		name: 'Thematic 1 layer', //i18n
		legendDiv: 'thematic1Legend'
	},
	thematic2: {
		id: 'thematic2',
		name: 'Thematic 2 layer', //i18n
		legendDiv: 'thematic2Legend'
	},
	facility: {
		id: 'facility',
		name: 'Facility layer', //i18n
		legendDiv: 'facilityLegend'
	},
	symbol: {
		id: 'symbol',
		name: 'Symbol layer', //i18n
		legendDiv: 'symbolLegend'
	},
	openStreetMap: {
		id: 'OpenStreetMap'
	},
	googleStreets: {
		id: 'Google Streets'
	},
	googleHybrid: {
		id: 'Google Hybrid'
	}
};

GIS.store = {};

GIS.obj = {};

GIS.cmp = {
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
		GIS.init.rootNodes[0].path = '/root/' + GIS.init.rootNodes[0].id;
		
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
		//GIS.base.googleStreets.layer.setVisibility(false);
	};
	
	GIS.init.afterRender = function() {
		
		// Map tools
		document.getElementsByClassName('zoomInButton')[0].innerHTML = '<img src="images/zoomin_24.png" />';
		document.getElementsByClassName('zoomOutButton')[0].innerHTML = '<img src="images/zoomout_24.png" />';
		document.getElementsByClassName('zoomVisibleButton')[0].innerHTML = '<img src="images/zoomvisible_24.png" />';
		document.getElementsByClassName('layersButton')[0].innerHTML = '<img src="images/layers_24.png" />';
		
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
		
		// Load favorite
		var config = {
			classes: 5,
			colorHigh: "ffff00",
			colorLow: "0000ff",
			dataElement: null,
			dataElementGroup: null,
			indicator: "Uvn6LCg7dVU",
			indicatorGroup: "AoTB60phSOH",
			legendSet: null,
			legendType: "automatic",
			level: 3,
			levelName: "Chiefdom",
			method: 2,
			parentId: "fdc6uOvgoji",
			parentLevel: 2,
			parentName: "Bombali",
			parentPath: "/ImspTQPwCqd/fdc6uOvgoji",
			period: "2012",
			periodType: "Yearly",
			radiusHigh: 15,
			radiusLow: 5,
			updateData: false,
			updateLegend: false,
			updateOrganisationUnit: true,
			valueType: "indicator"
		};
		
		//GIS.base.thematic1.widget.setConfig(config);
		//GIS.base.thematic1.widget.execute();
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
	
	GIS.util.map.getTransformedPointByXY = function(x, y) {
		var p = new OpenLayers.Geometry.Point(parseFloat(x), parseFloat(y));
        return p.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    };
    
    GIS.util.map.getLonLatByXY = function(x, y) {
		var point = GIS.util.map.getTransformedPointByXY(x, y);		
		return new OpenLayers.LonLat(point.x, point.y);
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
                    hcwc: doc.geojson[i].hc,
                    path: doc.geojson[i].path,
                    parentId: doc.geojson[i].pi,
                    parentName: doc.geojson[i].pn
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
    
    GIS.util.vector.getDefaultStyleMap = function(base) {
		var isBoundary = base.id === GIS.base.boundary.id;
		return new OpenLayers.StyleMap({
			'default': new OpenLayers.Style(
				OpenLayers.Util.applyDefaults({
					fillOpacity: isBoundary ? 0 : 1,
					strokeColor: isBoundary ? '#000' : '#fff',
					strokeWidth: 1
				},
				OpenLayers.Feature.Vector.style['default'])
			),
			select: new OpenLayers.Style({
				strokeColor: '#000000',
				strokeWidth: 2,
				cursor: 'pointer'
			})
		});
	};
    
    GIS.util.vector.getLabelStyleMap = function(base, config) {
		var isBoundary = base.id === GIS.base.boundary.id;
		return new OpenLayers.StyleMap({
			'default' : new OpenLayers.Style(
				OpenLayers.Util.applyDefaults({
					fillOpacity: isBoundary ? 0 : 1,
					strokeColor: isBoundary ? '#000' : '#fff',
					strokeWidth: 1,
					label: '\${label}',
					fontFamily: 'arial,sans-serif,ubuntu,consolas',
					fontSize: config.fontSize ? config.fontSize + 'px' : '13px',
					fontWeight: config.strong ? 'bold' : 'normal',
					fontStyle: config.italic ? 'italic' : 'normal',
					fontColor: config.color ? '#' + config.color : '#000000'
				},
				OpenLayers.Feature.Vector.style['default'])
			),
			select: new OpenLayers.Style({
				strokeColor: '#000000',
				strokeWidth: 2,
				cursor: 'pointer'
			})
		});
	};
    
    GIS.util.gui.window.setPositionTopRight = function(window) {		
		var east = GIS.cmp.region.east,
			center = GIS.cmp.region.center;
				
		window.setPosition((east.x + east.width) - (window.getWidth() + 7), center.y + 8);
	};
	
	GIS.util.gui.window.setPositionTopLeft = function(window) {
		window.setPosition(4,35);
	};
	
	GIS.util.gui.combo.setQueryMode = function(cmpArray, mode) {
		for (var i = 0; i < cmpArray.length; i++) {
			cmpArray[i].queryMode = mode;
		}
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
        GIS.base.googleStreets.layer = new OpenLayers.Layer.Google(GIS.base.googleStreets.id, {
			numZoomLevels: 20,
			animationEnabled: true
		});        
        GIS.base.googleStreets.layer.layerType = GIS.conf.finals.layer.type_base;
        GIS.map.addLayer(GIS.base.googleStreets.layer);
        
        GIS.base.googleHybrid.layer = new OpenLayers.Layer.Google(GIS.base.googleHybrid.id, {
			type: google.maps.MapTypeId.HYBRID,
			numZoomLevels: 20,
			animationEnabled: true
		});        
        GIS.base.googleHybrid.layer.layerType = GIS.conf.finals.layer.type_base;
        GIS.map.addLayer(GIS.base.googleHybrid.layer);
    }
    
    GIS.base.openStreetMap.layer = new OpenLayers.Layer.OSM(GIS.base.openStreetMap.id);
    GIS.base.openStreetMap.layer.layerType = GIS.conf.finals.layer.type_base;
    GIS.map.addLayer(GIS.base.openStreetMap.layer);
    
    // Vector layers
    
    //GIS.base.boundary.layer = new OpenLayers.Layer.Vector(GIS.i18n.boundary_layer, {
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
    //GIS.map.addLayer(GIS.base.boundary.layer);
    
    GIS.base.thematic1.layer = new OpenLayers.Layer.Vector(GIS.i18n.thematic_layer_1, {
        strategies: [
			new OpenLayers.Strategy.Refresh({
				force:true
			})
		],
		styleMap: GIS.util.vector.getDefaultStyleMap(GIS.base.thematic1),
        visibility: false,
        displayInLayerSwitcher: false,
        layerType: GIS.conf.finals.layer.type_vector,
        layerOpacity: 0.8,
        setLayerOpacity: function(number) {
			if (number) {
				this.layerOpacity = parseFloat(number);
			}
			this.setOpacity(this.layerOpacity);
		},
		hasLabels: false
		
    });
    GIS.map.addLayer(GIS.base.thematic1.layer);
    
    GIS.base.thematic2.layer = new OpenLayers.Layer.Vector(GIS.i18n.thematic_layer_2, {
        strategies: [
			new OpenLayers.Strategy.Refresh({
				force:true
			})
		],
		styleMap: GIS.util.vector.getDefaultStyleMap(GIS.base.thematic2),
        visibility: false,
        displayInLayerSwitcher: false,
        layerType: GIS.conf.finals.layer.type_vector,
        layerOpacity: 0.8,
        setLayerOpacity: function(number) {
			if (number) {
				this.layerOpacity = parseFloat(number);
			}
			this.setOpacity(this.layerOpacity);
		},
		hasLabels: false
		
    });
    GIS.map.addLayer(GIS.base.thematic2.layer);
    
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
		cmp: [],
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
					GIS.util.gui.combo.setQueryMode(this.cmp, 'local');
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
		cmp: [],
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
					GIS.util.gui.combo.setQueryMode(this.cmp, 'local');
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
		cmp: [],
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
					GIS.util.gui.combo.setQueryMode(this.cmp, 'local');
				}
				this.sort('level', 'ASC');
			}
		}
	});
        
    GIS.store.infrastructuralPeriodsByType = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_gis + 'getPeriodsByPeriodType.action',
			reader: {
				type: 'json',
				root: 'periods'
			},
			extraParams: {
				name: GIS.init.systemSettings.infrastructuralPeriodType
			}
		},
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
    
    GIS.obj.LayerMenu = function(base, cls) {
		var layer = base.layer;
		return Ext.create('Ext.menu.Menu', {
			shadow: false,
			showSeparator: false,
			enableItems: function() {
				Ext.each(this.items.items, function(item) {
					item.enable();
				});
			},
			disableItems: function() {
				Ext.Array.each(this.items.items, function(item) {
					if (!item.alwaysEnabled) {
						item.disable();
					}
				});
			},
			items: [
				{
					text: 'Edit layer..', //i18n
					iconCls: 'gis-menu-item-icon-edit',
					cls: 'gis-menu-item-first',
					alwaysEnabled: true,
					handler: function() {
						GIS.base[base.id].window.show();
					}
				},
				{
					xtype: 'menuseparator',
					alwaysEnabled: true
				},
				{
					text: 'Labels..', //i18n
					iconCls: 'gis-menu-item-icon-labels',
					handler: function() {
						if (base.widget.cmp.labelWindow) {
							base.widget.cmp.labelWindow.show();
						}
						else {
							base.widget.cmp.labelWindow = new GIS.obj.LabelWindow(base);
							base.widget.cmp.labelWindow.show();
						}
					}
				},
				{
					text: 'Filter..', //i18n
					iconCls: 'gis-menu-item-icon-filter',
					handler: function() {
						if (base.widget.cmp.filterWindow) {
							if (base.widget.cmp.filterWindow.isVisible()) {
								return;
							}
							else {
								base.widget.cmp.filterWindow.destroy();
							}
						}
					
						base.widget.cmp.filterWindow = new GIS.obj.FilterWindow(base);
						base.widget.cmp.filterWindow.show();
					}
				},
				{
					text: 'Search..', //i18n
					iconCls: 'gis-menu-item-icon-search',
					handler: function() {
						if (base.widget.cmp.searchWindow) {
							if (base.widget.cmp.searchWindow.isVisible()) {
								return;
							}
							else {
								base.widget.cmp.searchWindow.destroy();
							}
						}
					
						base.widget.cmp.searchWindow = new GIS.obj.SearchWindow(base);
						base.widget.cmp.searchWindow.show();
					}
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
						showSeparator: false,
						defaults: {
							minWidth: 70
						},
						items: GIS.conf.opacity.items,
						listeners: {
							click: function(menu, item) {
								layer.setOpacity(item.text);
							}
						}
					})
				},
				{
					xtype: 'menuseparator',
					alwaysEnabled: true
				},
				{
					text: 'Close',//i18n
					iconCls: 'gis-menu-item-icon-clear',
					handler: function() {
						base.widget.reset();
					}
				}
			],
			listeners: {
				afterrender: function() {
					this.getEl().addCls('gis-toolbar-btn-menu');
					if (cls) {
						this.getEl().addCls(cls);
					}
				},
				show: function() {
					if (base.layer.features.length) {
						this.enableItems();
					}
					else {
						this.disableItems();
					}
				}
			}
		});
	};
	
	GIS.obj.WidgetWindow = function(base) {
		return Ext.create('Ext.window.Window', {
			title: base.name,
			layout: 'fit',
			iconCls: 'gis-window-title-icon-' + base.id,
			cls: 'gis-container-default',
			closeAction: 'hide',
			width: GIS.conf.layout.widget.window_width,
			resizable: false,
			isCollapsed: false,
			items: base.widget,
			bbar: [
				'->',
				{
					text: 'Update', //i18n
					handler: function() {
						base.widget.execute();
					}
				}
			],
			listeners: {
				show: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
				}
			}
		});
	};
	
	GIS.obj.SearchWindow = function(base) {
		var layer = base.layer,
			data = [],
			store = base.widget.store.features,
			button,
			window;
			
		for (var i = 0; i < layer.features.length; i++) {
			data.push([layer.features[i].data.id, layer.features[i].data.name]);
		}
		
		if (!data.length) {
			GIS.logg.push([data, base.widget.xtype + '.search.data: feature ids/names']);
			alert("no features"); //todo
			return;
		}
		
		button = Ext.create('Ext.button.Button', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			height: 22,
			value: '0000ff',
			fieldLabel: GIS.i18n.highlight_color,
			getValue: function() {
				return this.value;
			},
			setValue: function(color) {
				this.value = color;
				if (Ext.isDefined(this.getEl())) {
					this.getEl().dom.style.background = '#' + color;
				}
			},
			menu: {
				showSeparator: false,
				items: {
					xtype: 'colorpicker',
					closeAction: 'hide',
					listeners: {
						select: function(cp, color) {
							button.setValue(color);
							button.menu.hide();
						}
					}
				}
			},
			listeners: {
				added: function() {
					this.defaultValue = this.value;
				},
				render: function() {
					this.setValue(this.value);
				}
			}
		});
		
		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.organisationunit_search,
			layout: 'fit',
			iconCls: 'gis-window-title-icon-search',
			cls: 'gis-container-default',
			width: GIS.conf.layout.tool.window_width,
			height: 400,
			items: [
				{
					cls: 'gis-container-inner',
					items: [
						{
							layout: 'column',
							cls: 'gis-container-inner',
							items: [
								{
									cls: 'gis-panel-html-label',
									html: 'Highlight color:',
									width: GIS.conf.layout.tool.itemlabel_width
								},
								button
							]
						},
						{
							cls: 'gis-panel-html-separator'
						},
						{
							layout: 'column',
							cls: 'gis-container-inner',
							items: [
								{
									cls: 'gis-panel-html-label',
									html: GIS.i18n.text_filter + ':',
									width: GIS.conf.layout.tool.itemlabel_width
								},								
								{
									xtype: 'textfield',
									cls: 'gis-textfield',
									width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
									enableKeyEvents: true,
									listeners: {
										keyup: function() {
											store.clearFilter();
											if (this.getValue()) {
												store.filter('name', this.getValue());
											}
											store.sortStore();
										}
									}
								}
							]
						},
						{
							xtype: 'grid',
							cls: 'gis-grid',
							height: 290,
							width: GIS.conf.layout.tool.item_width,
							scroll: 'vertical',
							hideHeaders: true,
							columns: [{
								id: 'name',
								text: 'Organisation units',
								dataIndex: 'name',
								sortable: false,
								width: GIS.conf.layout.tool.item_width
							}],
							store: base.widget.store.features,
							listeners: {
								select: function(grid, record, index, e) {
									var feature = layer.getFeaturesByAttribute('id', record.data.id)[0],
										color = button.getValue(),
										symbolizer;
									
									layer.redraw();
									
									if (feature.geometry.CLASS_NAME === GIS.conf.finals.feature.type_point_class) {
										symbolizer = new OpenLayers.Symbolizer.Point({
											'pointRadius': 7,
											'fillColor': '#' + color
										});
									}
									else {
										symbolizer = new OpenLayers.Symbolizer.Polygon({
											'strokeColor': '#' + color,
											'fillColor': '#' + color
										});
									}

									layer.drawFeature(feature,symbolizer);
								}
							}
						}
					]
				}
			],
			listeners: {
				render: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
					store.sortStore();
				},
				destroy: function() {
					layer.redraw();
				}
			}
		});
		
		return window;
	};
	
	GIS.obj.FilterWindow = function(base) {
		var layer = base.layer,
			lowerNumberField,
			greaterNumberField,
			lt,
			gt,
			filter,
			window;
		
		greaterNumberField = Ext.create('Ext.form.field.Number', {
			width: GIS.conf.layout.tool.itemlabel_width,
			value: parseInt(base.widget.coreComp.minVal),
			listeners: {
				change: function() {
					gt = this.getValue();
				}
			}
		});
		
		lowerNumberField = Ext.create('Ext.form.field.Number', {
			width: GIS.conf.layout.tool.itemlabel_width,
			value: parseInt(base.widget.coreComp.maxVal) + 1,
			listeners: {
				change: function() {
					lt = this.getValue();
				}
			}
		});
		
        filter = function() {
			var cache = base.widget.features.slice(0),
				features = [];
				
            if (!gt && !lt) {
                features = cache;
            }
            else if (gt && lt) {
                for (var i = 0; i < cache.length; i++) {
                    if (gt < lt && (cache[i].attributes.value > gt && cache[i].attributes.value < lt)) {
                        features.push(cache[i]);
                    }
                    else if (gt > lt && (cache[i].attributes.value > gt || cache[i].attributes.value < lt)) {
                        features.push(cache[i]);
                    }
                    else if (gt === lt && cache[i].attributes.value === gt) {
                        features.push(cache[i]);
                    }
                }
            }
            else if (gt && !lt) {
                for (var i = 0; i < cache.length; i++) {
                    if (cache[i].attributes.value > gt) {
                        features.push(cache[i]);
                    }
                }
            }
            else if (!gt && lt) {
                for (var i = 0; i < cache.length; i++) {
                    if (cache[i].attributes.value < lt) {
                        features.push(cache[i]);
                    }
                }
            }
            
            layer.removeAllFeatures();
            layer.addFeatures(features);
            
            base.widget.store.features.loadFeatures(layer.features);
        };
		
		window = Ext.create('Ext.window.Window', {
			title: 'Filter by value',
			iconCls: 'gis-window-title-icon-filter',
			cls: 'gis-container-default',
			width: GIS.conf.layout.tool.window_width,
			filter: filter,
			items: {
				layout: 'fit',
				cls: 'gis-container-inner',
				items: [
					{
						cls: 'gis-container-inner',
						html: 'Show organisation units with values..'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: 'Greater than:',
								width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width
							},
							greaterNumberField
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: 'And/or lower than:',
								width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width
							},
							lowerNumberField
						]
					}
				]
			},
			bbar: [
				'->',
				{
					xtype: 'button',
					text: GIS.i18n.update,
					handler: function() {
						filter();
					}
				}
			],
			listeners: {
				render: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
				},				
				destroy: function() {
					layer.removeAllFeatures();
					layer.addFeatures(base.widget.features);
					base.widget.store.features.loadFeatures(layer.features);
				}
			}
		});
		
		return window;
	};
	
	GIS.obj.LabelWindow = function(base) {
		var layer = base.layer,
			fontSize,
			strong,
			italic,
			color,
			getValues,
			updateLabels,
			window;
			
		fontSize = Ext.create('Ext.form.field.Number', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			allowDecimals: false,
			minValue: 8,
			value: 13,
			emptyText: 13,
			disabled: true,
			listeners: {
				change: function() {
					alert(1);
					updateLabels();
				}
			}
		});
		
		strong = Ext.create('Ext.form.field.Checkbox', {
			listeners: {
				change: function() {
					updateLabels();
				}
			}
		});
		
		italic = Ext.create('Ext.form.field.Checkbox', {
			listeners: {
				change: function() {
					updateLabels();
				}
			}
		});
		
		color = Ext.create('Ext.button.Button', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			height: 22,
			value: '000000',
			fieldLabel: GIS.i18n.highlight_color,
			getValue: function() {
				return this.value;
			},
			setValue: function(color) {
				this.value = color;
				if (Ext.isDefined(this.getEl())) {
					this.getEl().dom.style.background = '#' + color;
				}
				this.fireEvent('change');
			},
			menu: {
				showSeparator: false,
				items: {
					xtype: 'colorpicker',
					closeAction: 'hide',
					listeners: {
						select: function(cp, value) {
							color.setValue(value);
							color.menu.hide();
						}
					}
				}
			},
			listeners: {
				render: function() {
					this.setValue(this.value);
				},
				change: function() {
					updateLabels();
				}
			}
		});
		
		getValues = function() {
			return {
				fontSize: fontSize.getValue(),
				strong: strong.getValue(),
				italic: italic.getValue(),
				color: color.getValue()
			};
		};		
		
		updateLabels = function() {
			if (layer.hasLabels) {
				layer.styleMap = GIS.util.vector.getLabelStyleMap(base, getValues());				
				base.widget.config.updateLegend = true;
				base.widget.execute();
			}
		};
		
		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.labels,
			iconCls: 'gis-window-title-icon-labels',
			cls: 'gis-container-default',
			width: GIS.conf.layout.tool.window_width,
			closeAction: 'hide',
			items: {
				layout: 'fit',
				cls: 'gis-container-inner',
				items: [
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: GIS.i18n.font_size,
								width: GIS.conf.layout.tool.itemlabel_width
							},
							fontSize
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: '<b>' + GIS.i18n.bold_ + '</b>:',
								width: GIS.conf.layout.tool.itemlabel_width
							},
							strong
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: '<i>' + GIS.i18n.italic + '</i>:',
								width: GIS.conf.layout.tool.itemlabel_width
							},
							italic
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: 'Color:', //i18n
								width: GIS.conf.layout.tool.itemlabel_width
							},
							color
						]
					}
				]
			},
			bbar: [
				'->',
				{
					xtype: 'button',
					text: 'Show / hide', //i18n
					handler: function() {
						if (layer.hasLabels) {
							layer.hasLabels = false;
							layer.styleMap = GIS.util.vector.getDefaultStyleMap(base);
						}
						else {
							layer.hasLabels = true;
							layer.styleMap = GIS.util.vector.getLabelStyleMap(base, getValues());
						}
						
						base.widget.config.updateLegend = true;
						base.widget.execute();
					}
				}
			],
			listeners: {
				render: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
				}
			}
		});
		
		return window;
	};
    
	// User interface
	
	GIS.base.thematic1.menu = new GIS.obj.LayerMenu(GIS.base.thematic1);	
	GIS.base.thematic1.widget = Ext.create('mapfish.widgets.geostat.Thematic1', {
        map: GIS.map,
        layer: GIS.base.thematic1.layer,
        menu: GIS.base.thematic1.menu,
        legendDiv: GIS.base.thematic1.legendDiv
    });    
    GIS.base.thematic1.window = new GIS.obj.WidgetWindow(GIS.base.thematic1);
	
	GIS.base.thematic2.menu = new GIS.obj.LayerMenu(GIS.base.thematic2);	
	GIS.base.thematic2.widget = Ext.create('mapfish.widgets.geostat.Thematic2', {
        map: GIS.map,
        layer: GIS.base.thematic2.layer,
        menu: GIS.base.thematic2.menu,
        legendDiv: GIS.base.thematic2.legendDiv
    });    
    GIS.base.thematic2.window = new GIS.obj.WidgetWindow(GIS.base.thematic2);    
	
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
							iconCls: 'gis-btn-icon-' + GIS.base.boundary.id,
							menu: new GIS.obj.LayerMenu(GIS.base.boundary, 'gis-toolbar-btn-menu-first'),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.thematic1.id,
							menu: GIS.base.thematic1.menu,
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.thematic2.id,
							menu: new GIS.obj.LayerMenu(GIS.base.thematic2),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.facility.id,
							menu: new GIS.obj.LayerMenu(GIS.base.facility),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.symbol.id,
							menu: new GIS.obj.LayerMenu(GIS.base.symbol),
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
							text: 'fav()', //i18n
							handler: function() {
								var config = {
									classes: 5,
									colorHigh: "00ff00",
									colorLow: "ff0000",
									dataElement: null,
									dataElementGroup: null,
									indicator: "Uvn6LCg7dVU",
									indicatorGroup: "AoTB60phSOH",
									legendSet: null,
									legendType: "automatic",
									level: 3,
									levelName: "Chiefdom",
									method: 2,
									parentId: "fdc6uOvgoji",
									parentLevel: 2,
									parentName: "Bombali",
									parentPath: "/ImspTQPwCqd/fdc6uOvgoji",
									period: "2012",
									periodType: "Yearly",
									radiusHigh: 15,
									radiusLow: 5,
									updateData: false,
									updateLegend: false,
									updateOrganisationUnit: true,
									valueType: "indicator"
								};
								
								GIS.base.thematic1.widget.setConfig(config);
								GIS.base.thematic1.widget.execute();
							}
						},
						{
							text: 'log()', //i18n
							handler: function() {
								
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
