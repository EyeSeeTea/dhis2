GIS.util = {};
GIS.util.map = {};

GIS.core = {};

GIS.util.map.getVisibleVectorLayers = function(openLayersMap) {
	var layers = [],
		layer;

	for (var i = 0; i < openLayersMap.layers.length; i++) {
		layer = openLayersMap.layers[i];
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

GIS.util.map.zoomToVisibleExtent = function(openLayersMap) {
	var bounds = GIS.util.map.getExtendedBounds(GIS.util.map.getVisibleVectorLayers(openLayersMap));
	if (bounds) {
		GIS.map.zoomToExtent(bounds);
	}
};

GIS.core.BaseCollection = function() {
	return {
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
	};
};

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

	if (base.id === GIS.base.boundary.id) {
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

GIS.core.OpenLayersMap = function() {
	var map,
		addMapControl;

	map = new OpenLayers.Map({
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

	map.zoomToVisibleExtent = function() {
		GIS.util.map.zoomToVisibleExtent(this);
	};

	addMapControl = function(name, fn) {
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

		map.addControl(panel);

		panel.div.className += ' ' + name;
		panel.div.childNodes[0].className += ' ' + name + 'Button';
	};

	addMapControl('zoomIn', map.zoomIn);
	addMapControl('zoomOut', map.zoomOut);
	addMapControl('zoomVisible', map.zoomToVisibleExtent);
	addMapControl('measure', function() {
		var measureWindow = GIS.core.MeasureWindow();
		measureWindow.show();
	});

	return map;
};

GIS.core.addLayers = function(openLayersMap, baseCollection) {
	var base;

	if (window.google) {
		base = baseCollection.googleStreets;
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
		openLayersMap.addLayer(base.layer);

		base = baseCollection.googleHybrid;
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
		openLayersMap.addLayer(base.layer);
	}
	else {
		base = baseCollection.openStreetMap;
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
		openLayersMap.addLayer(base.layer);
	}

	base = baseCollection.boundary;
	base.layer = GIS.core.VectorLayer(base);
	openLayersMap.addLayer(base.layer);
	base.core = new mapfish.GeoStat.Boundary(openLayersMap, {
		layer: base.layer,
	});

	base = baseCollection.thematic1;
	base.layer = GIS.core.VectorLayer(base, {opacity: 0.8});
	openLayersMap.addLayer(base.layer);
	base.core = new mapfish.GeoStat.Thematic1(openLayersMap, {
		layer: base.layer,
	});

	base = baseCollection.thematic2;
	base.layer = GIS.core.VectorLayer(base, {opacity: 0.8});
	openLayersMap.addLayer(base.layer);
	base.core = new mapfish.GeoStat.Thematic1(openLayersMap, {
		layer: base.layer,
	});

	base = baseCollection.facility;
	base.layer = GIS.core.VectorLayer(base);
	openLayersMap.addLayer(base.layer);
	base.core = new mapfish.GeoStat.Facility(openLayersMap, {
		layer: base.layer,
	});
};
