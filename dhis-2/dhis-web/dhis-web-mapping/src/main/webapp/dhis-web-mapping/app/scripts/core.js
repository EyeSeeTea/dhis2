Ext.onReady( function() {

if (!Ext.isObject(GIS)) {
	GIS = {};
}

GIS.conf = {
	finals: {
		layer: {
			type_base: 'base',
			type_vector: 'vector'
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
			legendtype_predefined: 'predefined',
			symbolizer_color: 'color',
			symbolizer_image: 'image',
			loadtype_organisationunit: 'organisationUnit',
			loadtype_data: 'data',
			loadtype_legend: 'legend'
		},
		openLayers: {
			point_classname: 'OpenLayers.Geometry.Point'
		},
		mapfish: {
			classify_with_bounds: 1,
			classify_by_equal_intervals: 2,
			classify_by_quantils: 3
		},
		base: {
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
			googleStreets: {
				id: 'googleStreets',
				name: 'Google Streets'
			},
			googleHybrid: {
				id: 'googleHybrid',
				name: 'Google Hybrid'
			},
			openStreetMap: {
				id: 'openStreetMap',
				name: 'OpenStreetMap'
			},
			circle: {
				id: 'circle',
				name: 'Circle'
			}
		}
	},
	url: {
		path_api: '../../api/',
		path_gis: '../',
		path_scripts: 'scripts/'
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
		},
		grid: {
			row_height: 27
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

GIS.util = {};
GIS.util.map = {};

GIS.util.map.getVisibleVectorLayers = function(olmap) {
	var layers = [],
		layer;

	for (var i = 0; i < olmap.layers.length; i++) {
		layer = olmap.layers[i];
		if (layer.layerType === GIS.conf.finals.layer.type_vector &&
			layer.visibility &&
			layer.features.length) {
			layers.push(layer);
		}
	}
	return layers;
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

GIS.util.map.zoomToVisibleExtent = function(olmap) {
	var bounds = GIS.util.map.getExtendedBounds(GIS.util.map.getVisibleVectorLayers(olmap));
	if (bounds) {
		GIS.map.zoomToExtent(bounds);
	}
};

GIS.util.geojson = {};

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
				type: parseInt(doc.geojson[i].ty) === 1 ? 'MultiPolygon' : 'Point',
				coordinates: doc.geojson[i].co
			},
			properties: {
				id: doc.geojson[i].uid,
				internalId: doc.geojson[i].iid,
				name: doc.geojson[i].na,
				hcwc: doc.geojson[i].hc,
				path: doc.geojson[i].path,
				parentId: doc.geojson[i].pi,
				parentName: doc.geojson[i].pn,
				hasCoordinatesUp: doc.properties.hasCoordinatesUp
			}
		});
	}

	return geojson;
};

GIS.util.gui = {};
GIS.util.gui.combo = {};

GIS.util.gui.combo.setQueryMode = function(cmpArray, mode) {
	for (var i = 0; i < cmpArray.length; i++) {
		cmpArray[i].queryMode = mode;
	}
};

GIS.store = {};

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
	autoLoad: true,
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
	getRecordByLevel: function(level) {
		return this.getAt(this.findExact('level', level));
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

GIS.core = {};

//GIS.core.BaseCollection = function() {
	//return
//};

GIS.core.StyleMap = function(base, labelConfig) {
	var defaults = {
			fillOpacity: 1,
			strokeColor: '#fff',
			strokeWidth: 1
		},
		select = {
			strokeColor: '#000000',
			strokeWidth: 2,
			cursor: 'pointer'
		};

	if (base.id === GIS.conf.finals.base.boundary.id) {
		defaults.fillOpacity = 0;
		defaults.strokeColor = '#000';

		select.fillColor = '#000';
		select.fillOpacity = 0.2;
		select.strokeWidth = 1;
	}

	if (labelConfig) {
		defaults.label = '\${label}';
		defaults.fontFamily = 'arial,sans-serif,ubuntu,consolas';
		defaults.fontSize = labelConfig.fontSize ? labelConfig.fontSize + 'px' : '13px';
		defaults.fontWeight = labelConfig.strong ? 'bold' : 'normal';
		defaults.fontStyle = labelConfig.italic ? 'italic' : 'normal';
		defaults.fontColor = labelConfig.color ? '#' + labelConfig.color : '#000000';
	}

	return new OpenLayers.StyleMap({
		'default': new OpenLayers.Style(
			OpenLayers.Util.applyDefaults(defaults),
			OpenLayers.Feature.Vector.style['default']),
		select: new OpenLayers.Style(select)
	});
};

GIS.core.VectorLayer = function(base, config) {
	var layer = new OpenLayers.Layer.Vector(base.name, {
		strategies: [
			new OpenLayers.Strategy.Refresh({
				force:true
			})
		],
		styleMap: GIS.core.StyleMap(base),
		visibility: false,
		displayInLayerSwitcher: false,
		layerType: GIS.conf.finals.layer.type_vector,
		layerOpacity: config ? config.opacity || 1 : 1,
		setLayerOpacity: function(number) {
			if (number) {
				this.layerOpacity = parseFloat(number);
			}
			this.setOpacity(this.layerOpacity);
		},
		hasLabels: false
	});
	layer.base = base;

	return layer;
};

GIS.core.MeasureWindow = function() {
	var window,
		label,
		handleMeasurements,
		control,
		styleMap;

	styleMap = new OpenLayers.StyleMap({
		'default': new OpenLayers.Style()
	});

	control = new OpenLayers.Control.Measure( OpenLayers.Handler.Path, {
		persist: true,
		immediate: true,
		handlerOption: {
			layerOptions: {
				styleMap: styleMap
			}
		}
	});

	handleMeasurements = function(e) {
		if (e.measure) {
			label.setText(e.measure.toFixed(2) + ' ' + e.units);
		}
	};

	GIS.map.addControl(control);

	control.events.on({
		measurepartial: handleMeasurements,
		measure: handleMeasurements
	});

	control.geodesic = true;
	control.activate();

	label = Ext.create('Ext.form.Label', {
		style: 'height: 20px',
		text: '0 km'
	});

	window = Ext.create('Ext.window.Window', {
		title: 'Measure distance', //i18n
		layout: 'fit',
		cls: 'gis-container-default',
		bodyStyle: 'text-align: center',
		width: 130,
		minWidth: 130,
		resizable: false,
		items: label,
		listeners: {
			show: function() {
				var x = GIS.cmp.region.east.x - this.getWidth() - 5,
					y = 60;
				this.setPosition(x, y);
			},
			destroy: function() {
				control.deactivate();
				GIS.map.removeControl(control);
			}
		}
	});

	return window;
};

GIS.obj.MapViewLoader = function() {
	var getMap,
		setMap,
		map,
		callbackRegister = [],
		loader;

	getMap = function(id) {
		if (!id) {
			alert('No favorite id provided');
			return;
		}
		if (!Ext.isString(id)) {
			alert('Favorite id must be a string');
			return;
		}

		Ext.Ajax.request({
			url: GIS.conf.url.path_api + 'maps/' + id + '.json?links=false',
			success: function(r) {
				map = Ext.decode(r.responseText);
				setMap(map);
			}
		});
	};

	setMap = function(map) {
		var views = Ext.isDefined(map.mapViews) ? map.mapViews : [],
			view,
			center,
			lonLat;

		if (!views.length) {
			alert('Favorite has no layers and is probably outdated'); //i18n
			return;
		}
		GIS.util.map.closeAllLayers();

		for (var i = 0; i < views.length; i++) {
			view = views[i];
			GIS.base[view.layer].widget.execute(view);
		}

		lonLat = new OpenLayers.LonLat(map.longitude, map.latitude);
		GIS.map.setCenter(lonLat, map.zoom);
	};

	loader = {
		id: id,
		load: function(id) {
			this.id = id || this.id;
			getMap(this.id);
		},
		callBack: function(widget) {
			callbackRegister.push(widget);

			if (callbackRegister.length === map.mapViews.length) {
				GIS.cmp.interpretationButton.enable();
			}
		}
	};

	return loader;
};
//GIS.core.MapLoader = function() {

GIS.core.OLMap = function() {
	var olmap,
		addControl,
		addLayers;

	addControl = function(name, fn) {
		var button,
			panel;

		button = new OpenLayers.Control.Button({
			displayClass: 'olControlButton',
			trigger: function() {
				fn.call(map);
			}
		});

		panel = new OpenLayers.Control.Panel({
			defaultControl: button
		});

		panel.addControls([button]);

		olmap.addControl(panel);

		panel.div.className += ' ' + name;
		panel.div.childNodes[0].className += ' ' + name + 'Button';
	};

	addLayers = function(olmap) {
		var base;

		if (window.google) {
			base = olmap.base.googleStreets;
			base.layer = new OpenLayers.Layer.Google(base.name, {
				numZoomLevels: 20,
				animationEnabled: true,
				layerType: GIS.conf.finals.layer.type_base,
				base: base,
				layerOpacity: 1,
				setLayerOpacity: function(number) {
					if (number) {
						this.layerOpacity = parseFloat(number);
					}
					this.setOpacity(this.layerOpacity);
				}
			});
			olmap.addLayer(base.layer);

			base = olmap.base.googleHybrid;
			base.layer = new OpenLayers.Layer.Google(base.name, {
				type: google.maps.MapTypeId.HYBRID,
				numZoomLevels: 20,
				animationEnabled: true,
				layerType: GIS.conf.finals.layer.type_base,
				base: base,
				layerOpacity: 1,
				setLayerOpacity: function(number) {
					if (number) {
						this.layerOpacity = parseFloat(number);
					}
					this.setOpacity(this.layerOpacity);
				}
			});
			olmap.addLayer(base.layer);
		}
		else {
			base = olmap.base.openStreetMap;
			base.layer = new OpenLayers.Layer.OSM(base.name);
			base.layer.layerType = GIS.conf.finals.layer.type_base;
			base.layer.base = base;
			base.layer.layerOpacity = 1;
			base.layer.setLayerOpacity = function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			};
			olmap.addLayer(base.layer);
		}

		base = olmap.base.boundary;
		base.layer = GIS.core.VectorLayer(base);
		olmap.addLayer(base.layer);
		base.core = new mapfish.GeoStat.Boundary(olmap, {
			layer: base.layer,
			base: base
		});

		base = olmap.base.thematic1;
		base.layer = GIS.core.VectorLayer(base, {opacity: 0.8});
		olmap.addLayer(base.layer);
		base.core = new mapfish.GeoStat.Thematic1(olmap, {
			layer: base.layer,
			base: base,
			legendDiv: base.legendDiv
		});

		base = olmap.base.thematic2;
		base.layer = GIS.core.VectorLayer(base, {opacity: 0.8});
		olmap.addLayer(base.layer);
		base.core = new mapfish.GeoStat.Thematic1(olmap, {
			layer: base.layer,
			base: base,
			legendDiv: base.legendDiv
		});

		base = olmap.base.facility;
		base.layer = GIS.core.VectorLayer(base);
		olmap.addLayer(base.layer);
		base.core = new mapfish.GeoStat.Facility(olmap, {
			layer: base.layer,
			base: base,
			legendDiv: base.legendDiv
		});
	};

	olmap = new OpenLayers.Map({
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
		maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
		mouseMove: {}, // Track all mouse moves
		relocate: {} // Relocate organisation units
	});

	addControl('zoomIn', olmap.zoomIn);
	addControl('zoomOut', olmap.zoomOut);
	addControl('zoomVisible', olmap.zoomToVisibleExtent);
	addControl('measure', function() {
		var measureWindow = GIS.core.MeasureWindow();
		measureWindow.show();
	});

	olmap.base = GIS.conf.finals.base;

	addLayers(olmap);

	olmap.zoomToVisibleExtent = function() {
		GIS.util.map.zoomToVisibleExtent(this);
	};

	return olmap;
};

});
