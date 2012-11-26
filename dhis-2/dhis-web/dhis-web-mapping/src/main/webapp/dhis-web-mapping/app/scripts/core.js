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

GIS.util.google = {};

GIS.util.google.openTerms = function() {
	window.open('http://www.google.com/intl/en-US_US/help/terms_maps.html', '_blank');
};

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

GIS.util.geojson.decode = function(doc) {
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

GIS.core.MapLoader = function(olmap) {
	var getMap,
		setMap,
		afterLoad,
		map,
		mapViews,
		callBack,
		register = [],
		loader,
		base;

	getMap = function(config) {
		Ext.data.JsonP.request({
			url: config + 'maps/' + config.id + '.json?links=false',
			success: function(r) {
				if (!r) {
					alert('Uid not recognized' + (config.el ? ' (' + config.el + ')' : ''));
					return;
				}

				map = r;
				setMap(map);
			}
		});
	};

	setMap = function(map) {
		var mapView,
			center,
			lonLat,
			loader;

		mapViews = Ext.isDefined(map.mapViews) ? map.mapViews : [];

		if (!mapViews.length) {
			alert('Favorite is outdated - please create a new one'); //i18n
			return;
		}

		olmap.closeAllLayers();

		for (var i = 0; i < mapViews.length; i++) {
			mapView = mapViews[i];
			base = olmap.base[mapView.layer];
			loader = base.core.getLoader();
			loader.updateGui = true;
			loader.callBack = callBack;
			loader.load(mapView, loader);
		}
	};

	callBack = function(layer) {
		register.push(layer);

		if (register.length === mapViews.length) {
			afterLoad();
		}
	};

	afterLoad = function() {
		if (map.longtitude && Ext.isNumber(map.longitude) && map.latitude && Ext.isNumber(map.latitude) && map.zoom && Ext.isNumber(map.zoom)) {
			olmap.setCenter(new OpenLayers.LonLat(map.longitude, map.latitude), map.zoom);
		}
		else {
			olmap.zoomToVisibleExtent();
		}

		olmap.mask.hide();
		//interpretation
	};

	loader = {
		load: function(config) {
			if (config.id) {
				getMap(config);
			}
			else {
				setMap(config);
			}
		}
	};

	return loader;
};

GIS.core.ThematicLoader = function(base) {
	var layer = base.layer,
		core = base.core,
		widget = base.widget,
		olmap = layer.map,
		compareView,
		loadOrganisationUnits,
		loadData,
		loadLegend,
		afterLoad,
		loader;

	compareView = function(view, doExecute) {
		var src = base.core.view;

		if (!src) {
			if (doExecute) {
				loadOrganisationUnits(view);
			}
			return GIS.conf.finals.widget.loadtype_organisationunit;
		}

		if (view.organisationUnitLevel.id !== src.organisationUnitLevel.id) {
			if (doExecute) {
				loadOrganisationUnits(view);
			}
			return GIS.conf.finals.widget.loadtype_organisationunit;
		}

		if (view.parentOrganisationUnit.id !== src.parentOrganisationUnit.id) {
			if (doExecute) {
				loadOrganisationUnits(view);
			}
			return GIS.conf.finals.widget.loadtype_organisationunit;
		}

		if (view.valueType !== src.valueType) {
			if (doExecute) {
				loadData(view);
			}
			return GIS.conf.finals.widget.loadtype_organisationunit;
		}
		else {
			if (view.valueType === GIS.conf.finals.dimension.indicator.id && view.indicator.id !== src.indicator.id) {
				if (doExecute) {
					loadData(view);
				}
				return GIS.conf.finals.widget.loadtype_data;
			}
			if (view.valueType === GIS.conf.finals.dimension.dataElement.id && view.dataElement.id !== src.dataElement.id) {
				if (doExecute) {
					loadData(view);
				}
				return GIS.conf.finals.widget.loadtype_data;
			}
		}

		if (view.period.id !== src.period.id) {
			if (doExecute) {
				loadData(view);
			}
			return GIS.conf.finals.widget.loadtype_data;
		}

		if (view.legendType !== src.legendType) {
			if (doExecute) {
				loadLegend(view);
			}
			return GIS.conf.finals.widget.loadtype_legend;
		}
		else {
			if (view.legendType === GIS.conf.finals.widget.legendtype_automatic) {
				if (view.classes !== src.classes || view.method !== src.method || view.colorLow !== src.colorLow || view.radiusLow !== src.radiusLow ||	view.colorHigh !== src.colorHigh || view.radiusHigh !== src.radiusHigh) {
					if (doExecute) {
						loadLegend(view);
					}
					return GIS.conf.finals.widget.loadtype_legend;
				}
			}

			if (view.legendType === GIS.conf.finals.widget.legendtype_predefined && view.legendSet.id !== src.legendSet.id) {
				if (doExecute) {
					loadLegend(view);
				}
				return GIS.conf.finals.widget.loadtype_legend;
			}
		}
	};

    loadOrganisationUnits = function(view) {
		Ext.Ajax.request({
			url: GIS.conf.url.path_gis + 'getGeoJson.action',
			params: {
				parentId: view.parentOrganisationUnit.id,
				level: view.organisationUnitLevel.id
			},
			scope: this,
			disableCaching: false,
			success: function(r) {
				var geojson = GIS.util.geojson.decode(r.responseText),
					format = new OpenLayers.Format.GeoJSON(),
					features = GIS.util.vector.getTransformedFeatureArray(format.read(geojson));

				if (!Ext.isArray(features)) {
					alert('Invalid coordinates');
					olmap.mask.hide();
					return;
				}

				if (!features.length) {
					alert('No valid coordinates found'); //todo //i18n
					olmap.mask.hide();
					return;
				}

				loadData(view, features);
			}
		});
    };

    loadData = function(view, features) {
		var type = view.valueType,
			dataUrl = 'mapValues/' + GIS.conf.finals.dimension[type].param + '.json',
			indicator = GIS.conf.finals.dimension.indicator,
			dataElement = GIS.conf.finals.dimension.dataElement,
			period = GIS.conf.finals.dimension.period,
			organisationUnit = GIS.conf.finals.dimension.organisationUnit,
			params = {};

		features = features || layer.features;

		params[type === indicator.id ? indicator.param : dataElement.param] = view[type].id;
		params[period.param] = view.period.id;
		params[organisationUnit.param] = view.parentOrganisationUnit.id;
		params.le = view.organisationUnitLevel.id;

		Ext.Ajax.request({
			url: GIS.conf.url.path_api + dataUrl,
			params: params,
			disableCaching: false,
			scope: this,
			success: function(r) {
				var values = Ext.decode(r.responseText),
					featureMap = {},
					valueMap = {},
					newFeatures = [];

				if (values.length === 0) {
					alert('No aggregated data values found'); //todo //i18n
					GIS.mask.hide();
					return;
				}

				for (var i = 0; i < features.length; i++) {
					var iid = features[i].attributes.internalId;
					featureMap[iid] = true;
				}
				for (var i = 0; i < values.length; i++) {
					var iid = values[i].organisationUnitId,
						value = values[i].value;
					valueMap[iid] = value;
				}

				for (var i = 0; i < features.length; i++) {
					var feature = features[i],
						iid = feature.attributes.internalId;
					if (featureMap.hasOwnProperty(iid) && valueMap.hasOwnProperty(iid)) {
						feature.attributes.value = valueMap[iid];
						feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
						newFeatures.push(feature);
					}
				}

				layer.removeFeatures(layer.features);
				layer.addFeatures(newFeatures);

				core.features = layer.features.slice(0);

				loadLegend(view);
			}
		});
	};

	loadLegend = function(view) {
		var options,
			that = this,
			predefined = GIS.conf.finals.widget.legendtype_predefined,
			classificationType = mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS,
			method = view.legendType === predefined ? classificationType : view.method,
			predefinedBounds,
			legend,
			fn;

		fn = function() {
			options = {
				indicator: GIS.conf.finals.widget.value,
				method: method,
				numClasses: view.classes,
				bounds: predefinedBounds,
				colors: core.getColors(view.colorLow, view.colorHigh),
				minSize: view.radiusLow,
				maxSize: view.radiusHigh
			};

			core.view = view;
			core.applyClassification(options);

			afterLoad(view);
		};

		if (view.legendType === GIS.conf.finals.widget.legendtype_predefined) {
			var colors = [],
				bounds = [],
				names = [],
				legends;

			Ext.Ajax.request({
				url: GIS.conf.url.path_api + 'mapLegendSets/' + view.legendSet.id + '.json?links=false&paging=false',
				scope: this,
				success: function(r) {
					legends = Ext.decode(r.responseText).mapLegends;

					Ext.Array.sort(legends, function (a, b) {
						return a.startValue - b.startValue;
					});

					for (var i = 0; i < legends.length; i++) {
						if (bounds[bounds.length-1] !== legends[i].startValue) {
							if (bounds.length !== 0) {
								colors.push(new mapfish.ColorRgb(240,240,240));
								names.push('');
							}
							bounds.push(legends[i].startValue);
						}
						colors.push(new mapfish.ColorRgb());
						colors[colors.length - 1].setFromHex(legends[i].color);
						names.push(legends[i].name);
						bounds.push(legends[i].endValue);
					}

					core.colorInterpolation = colors; //todo improve
					predefinedBounds = bounds;
					view.legendSet.names = names;

					fn();
				}
			});
		}
		else {
			fn();
		}
	};

	afterLoad = function(view) {
		olmap.legendRegion.doLayout();
		layer.legendPanel.expand();

		if (loader.updateGui) {
			widget.setGui(view);
		}

		if (loader.zoomToVisibleExtent) {
			olmap.zoomToVisibleExtent();
		}

		if (loader.hideMask) {
			olmap.mask.hide();
		}

		if (loader.callBack) {
			loader.callBack(layer);
		}
	};

	loader = {
		compare: false,
		updateGui: false,
		zoomToVisibleExtent: false,
		hideMask: false,
		callBack: null,
		load: function(view) {
			if (this.compare) {
				compareView(view, true);
			}
			else {
				loadOrganisationUnits(view);
			}
		}
	};

	return loader;
};

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

	olmap.closeAllLayers = function() {
		olmap.base.boundary.core.reset();
		olmap.base.thematic1.core.reset();
		olmap.base.thematic2.core.reset();
		olmap.base.facility.core.reset();
	};

	return olmap;
};

});
