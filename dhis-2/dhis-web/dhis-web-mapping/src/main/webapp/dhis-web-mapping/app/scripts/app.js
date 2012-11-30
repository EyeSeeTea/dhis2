GIS.app = {};
GIS.app.init = {};

GIS.init = {};

GIS.obj = {};

GIS.cmp = {
	region: {}
};

GIS.gui = {};

GIS.logg = [];

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();

    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});

	// Init

	var gis = GIS.getInstance();

	GIS.app.init.onInitialize = function(r) {		
		gis.init = GIS.app.getInits(r);
		gis.util = GIS.app.getUtils();


		gis.store.organisationUnitLevels = GIS.core.OrganisationUnitLevelStore(gis);
	};

	Ext.Ajax.request({
		url: gis.baseUrl + gis.conf.url.path_gis + 'initialize.action',
		success: function(r) {
			GIS.app.init.onInitialize(r);

	GIS.app.init.onRender = function() {
		if (!window.google) {
			gis.layer.openStreetMap.item.setValue(true);
		}
	};

	GIS.app.init.afterRender = function() {
		
		// Mask
		//gis.olmap.mask = new Ext.LoadMask(GIS.cmp.region.center.getEl(), {
			//msg: GIS.i18n.loading
		//});

		// Map tools
		document.getElementsByClassName('zoomInButton')[0].innerHTML = '<img src="images/zoomin_24.png" />';
		document.getElementsByClassName('zoomOutButton')[0].innerHTML = '<img src="images/zoomout_24.png" />';
		document.getElementsByClassName('zoomVisibleButton')[0].innerHTML = '<img src="images/zoomvisible_24.png" />';
		document.getElementsByClassName('measureButton')[0].innerHTML = '<img src="images/measure_24.png" />';

		// Map events
		gis.olmap.events.register('mousemove', null, function(e) {
			gis.olmap.mouseMove.x = e.clientX;
			gis.olmap.mouseMove.y = e.clientY;
		});

		gis.olmap.events.register('click', null, function(e) {
			if (gis.olmap.relocate.active) {
				var el = document.getElementById('mouseposition').childNodes[0],
					coordinates = '[' + el.childNodes[1].data + ',' + el.childNodes[3].data + ']',
					center = GIS.cmp.region.center;

				Ext.Ajax.request({
					url: gis.baseUrl + gis.conf.url.path_gis + 'updateOrganisationUnitCoordinates.action',
					method: 'POST',
					params: {id: gis.olmap.relocate.feature.attributes.id, coordinates: coordinates},
					success: function(r) {
						gis.olmap.relocate.active = false;
						gis.olmap.relocate.widget.cmp.relocateWindow.destroy(); //todo

						gis.olmap.relocate.feature.move({x: parseFloat(e.clientX - center.x), y: parseFloat(e.clientY - 28)});
						gis.olmap.getViewport().style.cursor = 'auto';

						console.log(gis.olmap.relocate.feature.attributes.name + ' relocated to ' + coordinates);
					}
				});
			}
		});

		// Favorite

		var id = gis.util.url.getUrlParam('id');

		if (id) {
			gis.map = {
				id: id
			};
			GIS.core.MapLoader(gis).load();
		}
	};

	GIS.app.getInits = function(r) {
		var init = Ext.decode(r.responseText);
		
		for (var i = 0; i < init.rootNodes.length; i++) {
			init.rootNodes[i].path = '/root/' + init.rootNodes[i].id;
		}

		return init;
	};
		
	GIS.app.getUtils = function() {
		var util = gis.util;

		util.map.getFeaturesByLayers = function(layers) {
			var a = [];
			for (var i = 0; i < layers.length; i++) {
				a = a.concat(layers[i].features);
			}
			return a;
		};

		util.map.getPointsByFeatures = function(features) {
			var a = [];
			for (var i = 0; i < features.length; i++) {
				if (features[i].geometry.CLASS_NAME === gis.conf.finals.openLayers.point_classname) {
					a.push(features[i]);
				}
			}
			return a;
		};

		util.map.getLonLatsByPoints = function(points) {
			var lonLat,
				point,
				a = [];
			for (var i = 0; i < points.length; i++) {
				point = points[i];
				lonLat = new OpenLayers.LonLat(point.geometry.x, point.geometry.y);
				a.push(lonLat);
			}
			return a;
		};

		util.map.hasVisibleFeatures = function() {
			var layers = util.map.getVisibleVectorLayers(),
				layer;

			if (layers.length) {
				for (var i = 0; i < layers.length; i++) {
					layer = layers[i];
					if (layer.features.length) {
						return true;
					}
				}
			}

			return false;
		};

		util.map.getLayersByType = function(layerType) {
			var layers = [];
			for (var i = 0; i < gis.olmap.layers.length; i++) {
				var layer = gis.olmap.layers[i];
				if (layer.layerType === layerType) {
					layers.push(layer);
				}
			}
			return layers;
		};

		util.map.addMapControl = function(name, fn) {
			var panel = GIS.app.MapControlPanel(name, fn);
			gis.olmap.addControl(panel);
			panel.div.className += ' ' + name;
			panel.div.childNodes[0].className += ' ' + name + 'Button';
		};

		util.map.getTransformedPointByXY = function(x, y) {
			var p = new OpenLayers.Geometry.Point(parseFloat(x), parseFloat(y));
			return p.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
		};

		util.map.getLonLatByXY = function(x, y) {
			var point = util.map.getTransformedPointByXY(x, y);
			return new OpenLayers.LonLat(point.x, point.y);
		};

		util.url = {};

		util.url.getUrlParam = function(s) {
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
		};

		util.svg = {};

		util.svg.merge = function(str, strArray) {
			if (strArray.length) {
				str = str || '<svg></svg>';
				for (var i = 0; i < strArray.length; i++) {
					str = str.replace('</svg>', '');
					strArray[i] = strArray[i].substring(strArray[i].indexOf('>') + 1);
					str += strArray[i];
				}
			}
			return str;
		};

		util.svg.getString = function(title, layers) {
			var svgArray = [],
				svg = '',
				namespace,
				titleSVG,
				legendSVG = '',
				x = 20,
				y = 35,
				center = GIS.cmp.region.center;

			if (!layers.length) {
				return false;
			}

			namespace = 'xmlns="http://www.w3.org/2000/svg"';

			svg = '<svg ' + namespace + ' width="' + center.getWidth() + '" height="' + center.getHeight() + '"></svg>';

			titleSVG = '<g id="title" style="display: block; visibility: visible;">' +
					   '<text id="title" x="' + x + '" y="' + y + '" font-size="18" font-weight="bold">' +
					   '<tspan>' + title + '</tspan></text></g>';

			y += 35;

			for (var i = layers.length - 1; i > 0; i--) {
				if (layers[i].base.id === gis.layer.facility.id) {
					layers.splice(i, 1);
					console.log('Facility layer export currently not supported');
				}
			}

			for (var i = 0; i < layers.length; i++) {
				var layer = layers[i],
					id = layer.base.id,
					legendConfig = layer.base.widget.getLegendConfig(),
					imageLegendConfig = layer.base.widget.view.extended.imageLegendConfig,
					what,
					when,
					where,
					legend;

				// SVG
				svgArray.push(layer.div.innerHTML);

				// Legend
				if (id !== gis.layer.boundary.id && id !== gis.layer.facility.id) {
					what = '<g id="indicator" style="display: block; visibility: visible;">' +
						   '<text id="indicator" x="' + x + '" y="' + y + '" font-size="12">' +
						   '<tspan>' + legendConfig.what + '</tspan></text></g>';

					y += 15;

					when = '<g id="period" style="display: block; visibility: visible;">' +
						   '<text id="period" x="' + x + '" y="' + y + '" font-size="12">' +
						   '<tspan>' + legendConfig.when + '</tspan></text></g>';

					y += 15;

					where = '<g id="period" style="display: block; visibility: visible;">' +
						   '<text id="period" x="' + x + '" y="' + y + '" font-size="12">' +
						   '<tspan>' + legendConfig.where + '</tspan></text></g>';

					y += 8;

					legend = '<g>';

					for (var j = 0; j < imageLegendConfig.length; j++) {
						if (j !== 0) {
							y += 15;
						}

						legend += '<rect x="' + x + '" y="' + y + '" height="15" width="30" ' +
								  'fill="' + imageLegendConfig[j].color + '" stroke="#000000" stroke-width="1"/>';

						legend += '<text id="label" x="' + (x + 40) + '" y="' + (y + 12) + '" font-size="12">' +
								  '<tspan>' + imageLegendConfig[j].label + '</tspan></text>';
					}

					legend += '</g>';

					legendSVG += (what + when + where + legend);

					y += 50;
				}
			}

			if (svgArray.length) {
				svg = util.svg.merge(svg, svgArray);
			}

			svg = svg.replace('</svg>', (titleSVG + legendSVG) + '</svg>');

			return svg;
		};

		util.json = {};

		util.json.encodeString = function(str) {
			return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
		};

		util.json.decodeAggregatedValues = function(responseText) {
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

		util.gui.window = {};

		util.gui.window.setPositionTopRight = function(window) {
			var center = GIS.cmp.region.center;
			window.setPosition(GIS.gui.viewport.width - (window.width + 7), center.y + 8);
		};

		util.gui.window.setPositionTopLeft = function(window) {
			window.setPosition(4,35);
		};

		return util;
	};

	GIS.app.getStores = function() {
		var stores = gis.stores;

		stores.indicatorGroups = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: gis.baseUrl + gis.conf.url.path_api + 'indicatorGroups.json?links=false&paging=false',
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
						gis.util.gui.combo.setQueryMode(this.cmp, 'local');
					}
					this.sort('name', 'ASC');
				}
			}
		});

		stores.dataElementGroups = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: gis.baseUrl + gis.conf.url.path_api + 'dataElementGroups.json?links=false&paging=false',
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
						gis.util.gui.combo.setQueryMode(this.cmp, 'local');
					}
					this.sort('name', 'ASC');
				}
			}
		});

		stores.periodTypes = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: gis.conf.period.periodTypes
		});

		stores.infrastructuralPeriodsByType = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: gis.baseUrl + gis.conf.url.path_gis + 'getPeriodsByPeriodType.action',
				reader: {
					type: 'json',
					root: 'periods'
				},
				extraParams: {
					name: gis.init.systemSettings.infrastructuralPeriodType
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

		stores.groupSets = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: gis.baseUrl + gis.conf.url.path_api + 'organisationUnitGroupSets.json?paging=false&links=false',
				reader: {
					type: 'json',
					root: 'organisationUnitGroupSets'
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
		});

		stores.groupsByGroupSet = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'symbol'],
			proxy: {
				type: 'ajax',
				url: '',
				noCache: false,
				reader: {
					type: 'json',
					root: 'organisationUnitGroups'
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
		});

		stores.legendSets = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: gis.baseUrl + gis.conf.url.path_api + 'mapLegendSets.json?links=false&paging=false',
				reader: {
					type: 'json',
					root: 'mapLegendSets'
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
		});

		stores.maps = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'lastUpdated', 'user'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'maps'
				}
			},
			isLoaded: false,
			pageSize: 10,
			page: 1,
			defaultUrl: gis.baseUrl + gis.conf.url.path_api + 'maps.json?viewClass=detailed&links=false',
			loadStore: function(url) {
				this.proxy.url = url || this.defaultUrl;

				this.load({
					params: {
						pageSize: this.pageSize,
						page: this.page
					}
				});
			},
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
		});

		return stores;
	};

    // Objects

    GIS.app.LayerMenu = function(layer, cls) {
		var items = [],
			item;

		item = {
			text: 'Edit layer..', //i18n
			iconCls: 'gis-menu-item-icon-edit',
			cls: 'gis-menu-item-first',
			alwaysEnabled: true,
			handler: function() {
				layer.window.show();
			}
		};
		items.push(item);

		items.push({
			xtype: 'menuseparator',
			alwaysEnabled: true
		});

		item = {
			text: 'Labels..', //i18n
			iconCls: 'gis-menu-item-icon-labels',
			handler: function() {
				if (layer.labelWindow) {
					layer.labelWindow.show();
				}
				else {
					layer.labelWindow = GIS.app.LabelWindow(base);
					layer.labelWindow.show();
				}
			}
		};
		items.push(item);

		if (layer.id !== gis.layer.boundary.id) {
			item = {
				text: 'Filter..', //i18n
				iconCls: 'gis-menu-item-icon-filter',
				handler: function() {
					if (layer.filterWindow) {
						if (layer.filterWindow.isVisible()) {
							return;
						}
						else {
							layer.filterWindow.destroy();
						}
					}

					layer.filterWindow = layer.id === gis.layer.facility.id ?
						GIS.app.FilterFacilityWindow(layer) : GIS.app.FilterWindow(layer);
					layer.filterWindow.show();
				}
			};
			items.push(item);
		}

		item = {
			text: 'Search..', //i18n
			iconCls: 'gis-menu-item-icon-search',
			handler: function() {
				if (layer.searchWindow) {
					if (layer.searchWindow.isVisible()) {
						return;
					}
					else {
						layer.searchWindow.destroy();
					}
				}

				layer.searchWindow = GIS.app.SearchWindow(layer);
				layer.searchWindow.show();
			}
		};
		items.push(item);

		items.push({
			xtype: 'menuseparator',
			alwaysEnabled: true
		});

		item = {
			text: 'Close', //i18n
			iconCls: 'gis-menu-item-icon-clear',
			handler: function() {
				gis.app.interpretationButton.disable();

				layer.core.reset();
			}
		};
		items.push(item);

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
			items: items,
			listeners: {
				afterrender: function() {
					this.getEl().addCls('gis-toolbar-btn-menu');
					if (cls) {
						this.getEl().addCls(cls);
					}
				},
				show: function() {
					if (layer.features.length) {
						this.enableItems();
					}
					else {
						this.disableItems();
					}

					this.doLayout(); // show menu bug workaround
				}
			}
		});
	};

	GIS.app.LayersPanel = function() {
		var layers = gis.layer,
			layer,
			items = [],
			item,
			panel,
			visibleLayer;

		visibleLayerId = window.google ? layers.googleStreets.id : layers.openStreetMap.id;
		for (var i = 0; i < layers.length; i++) { //todo important
			layer = layers[i];

			item = Ext.create('Ext.ux.panel.LayerItemPanel', {
				cls: 'gis-container-inner',
				height: 23,
				layer: layer,
				text: layer.name,
				imageUrl: 'images/' + layer.id + '_14.png',
				value: layer.id === visibleLayerId ? true : false,
				opacity: layer.layerOpacity,
				numberFieldDisabled: layer.id !== visibleLayerId
			});
			layer.item = item;
			items.push(layer.item);
		}

        panel = Ext.create('Ext.panel.Panel', {
			renderTo: 'layerItems',
			layout: 'fit',
			cls: 'gis-container-inner',
			items: {
				cls: 'gis-container-inner',
				items: items
			}
		});

		return panel;
	};

	GIS.app.WidgetWindow = function(base) {
		return Ext.create('Ext.window.Window', {
			autoShow: true,
			title: base.name,
			layout: 'fit',
			iconCls: 'gis-window-title-icon-' + base.id,
			cls: 'gis-container-default',
			closeAction: 'hide',
			width: gis.conf.layout.widget.window_width,
			resizable: false,
			isRendered: false,
			items: base.widget,
			bbar: [
				'->',
				{
					text: 'Update', //i18n
					handler: function() {
						var view = base.widget.getView();

						if (view) {
							var loader = base.core.getLoader(base);
							loader.compare = true;
							loader.zoomToVisibleExtent = true;
							loader.hideMask = true;
							loader.load(view);
						}
					}
				}
			],
			listeners: {
				show: function() {
					if (!this.isRendered) {
						this.isRendered = true;
						this.hide();
					}
					else {
						util.gui.window.setPositionTopLeft(this);
					}
				}
			}
		});
	};

	GIS.app.SearchWindow = function(base) {
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
			alert('Layer has no organisation units'); //todo
			return;
		}

		button = Ext.create('Ext.ux.button.ColorButton', {
			width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width,
			value: '0000ff'
		});

		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.organisationunit_search,
			layout: 'fit',
			iconCls: 'gis-window-title-icon-search',
			cls: 'gis-container-default',
			width: gis.conf.layout.tool.window_width,
			resizable: false,
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
									html: GIS.i18n.highlight_color + ':',
									width: gis.conf.layout.tool.itemlabel_width
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
									width: gis.conf.layout.tool.itemlabel_width
								},
								{
									xtype: 'textfield',
									cls: 'gis-textfield',
									width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width,
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
							width: gis.conf.layout.tool.item_width,
							scroll: 'vertical',
							hideHeaders: true,
							columns: [{
								id: 'name',
								text: 'Organisation units',
								dataIndex: 'name',
								sortable: false,
								width: gis.conf.layout.tool.item_width
							}],
							store: base.widget.store.features,
							listeners: {
								select: function(grid, record) {
									var feature = layer.getFeaturesByAttribute('id', record.data.id)[0],
										color = button.getValue(),
										symbolizer;

									layer.redraw();

									if (feature.geometry.CLASS_NAME === gis.conf.finals.openLayers.point_classname) {
										symbolizer = new OpenLayers.Symbolizer.Point({
											pointRadius: 6,
											fillColor: '#' + color,
											strokeWidth: 1
										});
									}
									else {
										symbolizer = new OpenLayers.Symbolizer.Polygon({
											strokeColor: '#' + color,
											fillColor: '#' + color
										});
									}

									layer.drawFeature(feature, symbolizer);
								}
							}
						}
					]
				}
			],
			listeners: {
				render: function() {
					util.gui.window.setPositionTopLeft(this);
					store.sortStore();
				},
				destroy: function() {
					layer.redraw();
				}
			}
		});

		return window;
	};

	GIS.app.FilterWindow = function(base) {
		var layer = base.layer,
			lowerNumberField,
			greaterNumberField,
			lt,
			gt,
			filter,
			window;

		greaterNumberField = Ext.create('Ext.form.field.Number', {
			width: gis.conf.layout.tool.itemlabel_width,
			value: parseInt(base.core.minVal),
			listeners: {
				change: function() {
					gt = this.getValue();
				}
			}
		});

		lowerNumberField = Ext.create('Ext.form.field.Number', {
			width: gis.conf.layout.tool.itemlabel_width,
			value: parseInt(base.core.maxVal) + 1,
			listeners: {
				change: function() {
					lt = this.getValue();
				}
			}
		});

        filter = function() {
			var cache = base.core.features.slice(0),
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
			width: gis.conf.layout.tool.window_width,
			resizable: false,
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
								width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width
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
								width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width
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
					util.gui.window.setPositionTopLeft(this);
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

	GIS.app.FilterFacilityWindow = function(base) {
		var that = base.widget,
			window,
			multiSelect,
			button,
			filter,
			selection,
			features = [],
			groupSetName = that.view.organisationUnitGroupSet.name,
			store = GIS.store.groupsByGroupSet;

		filter = function() {
			features = [];

			if (!selection.length || !selection[0]) {
				features = that.features;
			}
			else {
				for (var i = 0; i < that.features.length; i++) {
					for (var j = 0; j < selection.length; j++) {
						if (that.features[i].attributes[groupSetName] === selection[j]) {
							features.push(that.features[i]);
						}
					}
				}
			}

			that.layer.removeAllFeatures();
			that.layer.addFeatures(features);
		};

		multiSelect = Ext.create('Ext.ux.form.MultiSelect', {
			hideLabel: true,
			dataFields: ['id', 'name'],
			valueField: 'name',
			displayField: 'name',
			width: 200,
			height: 300,
			store: store
		});

		button = Ext.create('Ext.button.Button', {
			text: 'Filter',
			handler: function() {
				selection = multiSelect.getValue();
				filter();
			}
		});

		window = Ext.create('Ext.window.Window', {
			title: 'Filter by value',
			iconCls: 'gis-window-title-icon-filter',
			cls: 'gis-container-default',
			//width: gis.conf.layout.tool.window_width,
			resizable: false,
			filter: filter,
			items: multiSelect,
			bbar: [
				'->',
				button
			],
			listeners: {
				destroy: function() {
					that.layer.removeAllFeatures();
					that.layer.addFeatures(that.features);
				}
			}
		});

		return window;
	};

	GIS.app.LabelWindow = function(base) {
		var layer = base.layer,
			fontSize,
			strong,
			italic,
			color,
			getValues,
			updateLabels,
			window;

		fontSize = Ext.create('Ext.form.field.Number', {
			width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width,
			allowDecimals: false,
			minValue: 8,
			value: 13,
			emptyText: 13,
			listeners: {
				change: function() {
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

		button = Ext.create('Ext.ux.button.ColorButton', {
			width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width,
			value: '0000ff'
		});

		color = Ext.create('Ext.ux.button.ColorButton', {
			width: gis.conf.layout.tool.item_width - gis.conf.layout.tool.itemlabel_width,
			value: '000000',
			menuHandler: function() {
				updateLabels();
			}
		});

		getLabelConfig = function() {
			return {
				fontSize: fontSize.getValue(),
				strong: strong.getValue(),
				italic: italic.getValue(),
				color: color.getValue()
			};
		};

		updateLabels = function() {
			if (layer.hasLabels) {
				layer.styleMap = GIS.app.StyleMap(base, getLabelConfig());
				base.core.loadLegend(base.widget.getView());
			}
		};

		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.labels,
			iconCls: 'gis-window-title-icon-labels',
			cls: 'gis-container-default',
			width: gis.conf.layout.tool.window_width,
			resizable: false,
			closeAction: 'hide',
			items: {
				layout: 'fit',
				cls: 'gis-container-inner',
				items: [
					//{
						//layout: 'column',
						//cls: 'gis-container-inner',
						//items: [
							//{
								//cls: 'gis-panel-html-label',
								//html: GIS.i18n.font_size,
								//width: gis.conf.layout.tool.itemlabel_width
							//},
							//fontSize
						//]
					//},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: '<b>' + GIS.i18n.bold_ + '</b>:',
								width: gis.conf.layout.tool.itemlabel_width
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
								width: gis.conf.layout.tool.itemlabel_width
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
								width: gis.conf.layout.tool.itemlabel_width
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
							layer.styleMap = GIS.app.StyleMap(base);
						}
						else {
							layer.hasLabels = true;
							layer.styleMap = GIS.app.StyleMap(base, getLabelConfig());
						}
						base.core.loadLegend(base.widget.getView());
					}
				}
			],
			listeners: {
				render: function() {
					util.gui.window.setPositionTopLeft(this);
				}
			}
		});

		return window;
	};

    GIS.app.MapControlPanel = function(name, fn) {
		var button,
			panel;

		button = new OpenLayers.Control.Button({
			displayClass: 'olControlButton',
			trigger: function() {
				fn.call(gis.olmap);
			}
		});

		panel = new OpenLayers.Control.Panel({
			defaultControl: button
		});

		panel.addControls([button]);

		return panel;
	};

	GIS.app.MapWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,

		// Components
			addButton,
			searchTextfield,
			grid,
			prevButton,
			nextButton,
			tbar,
			bbar,
			info,

			nameTextfield,
			systemCheckbox,
			createButton,
			updateButton,
			cancelButton,

			mapWindow;

		GIS.store.maps.on('load', function(store, records) {
			info.setText(records.length + ' favorite' + (records.length !== 1 ? 's' : '') + ' available');
		});

		NameWindow = function(id) {
			var window,
				record = GIS.store.maps.getById(id);

			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 300,
				labelWidth: 70,
				fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb',
				fieldLabel: 'Name', //i18n
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});

			systemCheckbox = Ext.create('Ext.form.field.Checkbox', {
				labelWidth: 70,
				fieldLabel: 'System', //i18n
				style: 'margin-bottom: 0',
				disabled: !gis.init.security.isAdmin,
				checked: !id ? false : (record.data.user ? false : true)
			});

			createButton = Ext.create('Ext.button.Button', {
				text: 'Create', //i18n
				handler: function() {
					var name = nameTextfield.getValue(),
						system = systemCheckbox.getValue(),
						layers = util.map.getVisibleVectorLayers(),
						layer,
						lonlat = gis.olmap.getCenter(),
						views = [],
						view,
						map;

					if (layers.length) {
						if (name) {
							for (var i = 0; i < layers.length; i++) {
								layer = layers[i];
								view = layer.base.widget.getView();

								// add
								view.layer = layer.base.id;

								// remove
								delete view.periodType;
								views.push(view);
							}

							map = {
								name: name,
								longitude: lonlat.lon,
								latitude: lonlat.lat,
								zoom: gis.olmap.getZoom(),
								mapViews: views
							};

							if (!system) {
								map.user = {
									id: 'currentUser'
								};
							}

							Ext.Ajax.request({
								url: gis.baseUrl + gis.conf.url.path_api + 'maps/',
								method: 'POST',
								headers: {'Content-Type': 'application/json'},
								params: Ext.encode(map),
								success: function(r) {
									var id = r.getAllResponseHeaders().location.split('/').pop();

									//gis.olmap.mapViewLoader = GIS.app.MapViewLoader(id);

									GIS.store.maps.loadStore();

									GIS.cmp.interpretationButton.enable();

									window.destroy();
								}
							});
						}
						else {
							alert('Please enter a name');
						}
					}
					else {
						alert('Please create a map first');
					}
				}
			});

			updateButton = Ext.create('Ext.button.Button', {
				text: 'Update', //i18n
				handler: function() {
					var name = nameTextfield.getValue(),
						system = systemCheckbox.getValue();

					Ext.Ajax.request({
						url: gis.baseUrl + gis.conf.url.path_gis + 'renameMap.action?id=' + id + '&name=' + name + '&user=' + !system,
						success: function() {
							GIS.store.maps.loadStore();

							window.destroy();
						}
					});
				}
			});

			cancelButton = Ext.create('Ext.button.Button', {
				text: 'Cancel', //i18n
				handler: function() {
					window.destroy();
				}
			});

			window = Ext.create('Ext.window.Window', {
				title: id ? 'Rename favorite' : 'Create new favorite',
				iconCls: 'gis-window-title-icon-favorite',
				cls: 'gis-container-default',
				resizable: false,
				modal: true,
				items: [
					nameTextfield,
					systemCheckbox
				],
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function() {
						this.setPosition(mapWindow.x + 14, mapWindow.y + 67);
					}
				}
			});

			return window;
		};

		addButton = Ext.create('Ext.button.Button', {
			text: 'Add new', //i18n
			width: 67,
			height: 26,
			style: 'border-radius: 1px;',
			menu: {},
			handler: function() {
				nameWindow = new NameWindow(null, 'create');
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: 340,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 6px; border-radius: 1px; border-color: #bbb',
			emptyText: 'Search for favorites', //i18n
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: function() {
					if (this.getValue() !== this.currentValue) {
						this.currentValue = this.getValue();

						var value = this.getValue(),
							url = value ? gis.baseUrl + gis.conf.url.path_api +  'maps/query/' + value + '.json?links=false' : null,
							store = GIS.store.maps;

						store.page = 1;
						store.loadStore(url);
					}
				}
			}
		});

		prevButton = Ext.create('Ext.button.Button', {
			text: 'Prev', //i18n
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? gis.baseUrl + gis.conf.url.path_api +  'maps/query/' + value + '.json?links=false' : null,
					store = GIS.store.maps;

				store.page = store.page <= 1 ? 1 : store.page - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: 'Next', //i18n
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? gis.baseUrl + gis.conf.url.path_api +  'maps/query/' + value + '.json?links=false' : null,
					store = GIS.store.maps;

				store.page = store.page + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'gis-label-info',
			width: 300,
			height: 22
		});

		grid = Ext.create('Ext.grid.Panel', {
			cls: 'gis-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: 334,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var el = Ext.get(record.data.id);

							if (el) {
								el = el.parent('td');
								el.addClsOnOver('link');
								el.dom.setAttribute('onclick', 'GIS.cmp.mapWindow.destroy(); gis.olmap.mapViewLoader = GIS.app.MapViewLoader("' + record.data.id + '"); gis.olmap.mapViewLoader.load();');
							}
						};

						Ext.defer(fn, 100);

						return '<div id="' + record.data.id + '">' + value + '</div>';
					}
				},
				{
					xtype: 'actioncolumn',
					sortable: false,
					width: 80,
					items: [
						{
							iconCls: 'gis-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								var system = !record.data.user,
									isAdmin = gis.init.security.isAdmin;

								if (isAdmin || (!isAdmin && !system)) {
									return 'tooltip-map-edit';
								}
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									system = !record.data.user,
									isAdmin = gis.init.security.isAdmin;

								if (isAdmin || (!isAdmin && !system)) {
									var id = this.up('grid').store.getAt(rowIndex).data.id;
									nameWindow = new NameWindow(id);
									nameWindow.show();
								}
							}
						},
						{
							iconCls: 'gis-grid-row-icon-overwrite',
							getClass: function(value, metaData, record) {
								var system = !record.data.user,
									isAdmin = gis.init.security.isAdmin;

								if (isAdmin || (!isAdmin && !system)) {
									return 'tooltip-map-overwrite';
								}
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									name = record.data.name,
									layers = util.map.getVisibleVectorLayers(),
									layer,
									lonlat = gis.olmap.getCenter(),
									views = [],
									view,
									map,
									message = 'Overwrite favorite?\n\n' + name;

								if (layers.length) {
									if (confirm(message)) {
										for (var i = 0; i < layers.length; i++) {
											layer = layers[i];
											view = layer.base.widget.getView();

											// add
											view.layer = layer.base.id;

											// remove
											delete view.periodType;
											views.push(view);
										}

										map = {
											longitude: lonlat.lon,
											latitude: lonlat.lat,
											zoom: gis.olmap.getZoom(),
											mapViews: views
										};

										Ext.Ajax.request({
											url: gis.baseUrl + gis.conf.url.path_api + 'maps/' + id,
											method: 'PUT',
											headers: {'Content-Type': 'application/json'},
											params: Ext.encode(map),
											success: function() {
												GIS.store.maps.loadStore();
											}
										});
									}
								}
								else {
									alert('No layers to save'); //i18n
								}
							}
						},
						{
							iconCls: 'gis-grid-row-icon-dashboard',
							getClass: function() {
								return 'tooltip-map-dashboard';
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									name = record.data.name,
									message = 'Add to dashboard?\n\n' + name;

								if (confirm(message)) {
									Ext.Ajax.request({
										url: gis.baseUrl + gis.conf.url.path_gis + 'addMapViewToDashboard.action',
										params: {
											id: id
										}
									});
								}
							}
						},
						{
							iconCls: 'gis-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								var system = !record.data.user,
									isAdmin = gis.init.security.isAdmin;

								if (isAdmin || (!isAdmin && !system)) {
									return 'tooltip-map-delete';
								}
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									name = record.data.name,
									message = 'Delete favorite?\n\n' + name;

								if (confirm(message)) {
									Ext.Ajax.request({
										url: gis.baseUrl + gis.conf.url.path_api + 'maps/' + id,
										method: 'DELETE',
										success: function() {
											GIS.store.maps.loadStore();
										}
									});
								}
							}
						}
					],
					renderer: function(value, metaData, record) {
						if (!gis.init.security.isAdmin && !record.data.user) {
							metaData.tdCls = 'gis-grid-row-icon-disabled';
						}
					}
				},
				{
					sortable: false,
					width: 6
				}
			],
			store: GIS.store.maps,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					GIS.cmp.mapGrid = this;
				},
				render: function() {
					var size = Math.floor((GIS.cmp.region.center.getHeight() - 155) / gis.conf.layout.grid.row_height);
					this.store.pageSize = size;
					this.store.page = 1;
					this.store.loadStore();

					GIS.store.maps.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},

				afterrender: function() {
					var fn = function() {
						var editArray = document.getElementsByClassName('tooltip-map-edit'),
							overwriteArray = document.getElementsByClassName('tooltip-map-overwrite'),
							dashboardArray = document.getElementsByClassName('tooltip-map-dashboard'),
							deleteArray = document.getElementsByClassName('tooltip-map-delete'),
							el;

						for (var i = 0; i < deleteArray.length; i++) {
							el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Rename',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});

							el = overwriteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Overwrite',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});

							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Delete',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < dashboardArray.length; i++) {
							el = dashboardArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Add to dashboard',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
					};

					Ext.defer(fn, 100);
				},
				itemmouseenter: function(grid, record, item) {
					this.currentItem = Ext.get(item);
					this.currentItem.removeCls('x-grid-row-over');
				},
				select: function() {
					this.currentItem.removeCls('x-grid-row-selected');
				},
				selectionchange: function() {
					this.currentItem.removeCls('x-grid-row-focused');
				}
			}
		});

		mapWindow = Ext.create('Ext.window.Window', {
			title: 'Manage favorites',
			iconCls: 'gis-window-title-icon-favorite',
			cls: 'gis-container-default',
			resizable: false,
			modal: true,
			width: 450,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					width: 422,
					cls: 'gis-container-inner',
					items: [
						addButton,
						{
							height: 24,
							width: 1,
							style: 'width: 1px; margin-left: 7px; margin-right: 7px; margin-top: 1px',
							bodyStyle: 'border-left: 1px solid #aaa'
						},
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function() {
					this.setPosition(115, 37);
				}
			}
		});

		return mapWindow;
	};

	GIS.app.LegendSetWindow = function() {

		// Stores
		var legendSetStore,
			legendStore,
			tmpLegendStore,

		// Objects
			LegendSetPanel,
			LegendPanel,

		// Instances
			legendSetPanel,
			legendPanel,

		// Components
			window,
			legendSetName,
			legendName,
			startValue,
			endValue,
			color,
			legendGrid,
			create,
			update,
			cancel,
			info,
			error1Window,
			error2Window,

		// Functions
			showUpdateLegendSet,
			deleteLegendSet,
			deleteLegend,
			getRequestBody,
			reset,
			validateLegends;

		legendSetStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: gis.baseUrl + gis.conf.url.path_api + 'mapLegendSets.json?links=false&paging=false',
				reader: {
					type: 'json',
					root: 'mapLegendSets'
				}
			},
			listeners: {
				load: function(store, records) {
					this.sort('name', 'ASC');

					info.setText(records.length + ' legend set' + (records.length !== 1 ? 's' : '') + ' available');
				}
			}
		});

		legendStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'startValue', 'endValue', 'color'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'mapLegends'
				}
			},
			deleteLegend: deleteLegend,
			listeners: {
				load: function(store, records) {
					var data = [],
						record;

					for (var i = 0; i < records.length; i++) {
						data.push(records[i].data);
					}

					Ext.Array.sort(data, function (a, b) {
						return a.startValue - b.startValue;
					});

					tmpLegendStore.add(data);

					info.setText(records.length + ' legend sets available');
				}
			}
		});

		LegendSetPanel = function() {
			var items,
				addButton;

			addButton = Ext.create('Ext.button.Button', {
				text: 'Add new', //i18n
				height: 26,
				style: 'border-radius: 1px',
				menu: {},
				handler: function() {
					showUpdateLegendSet();
				}
			});

			legendSetGrid = Ext.create('Ext.grid.Panel', {
				cls: 'gis-grid',
				scroll: 'vertical',
				height: true,
				hideHeaders: true,
				currentItem: null,
				columns: [
					{
						dataIndex: 'name',
						sortable: false,
						width: 363
					},
					{
						xtype: 'actioncolumn',
						sortable: false,
						width: 40,
						items: [
							{
								iconCls: 'gis-grid-row-icon-edit',
								getClass: function() {
									return 'tooltip-legendset-edit';
								},
								handler: function(grid, rowIndex, colIndex, col, event) {
									var id = this.up('grid').store.getAt(rowIndex).data.id;
									showUpdateLegendSet(id);
								}
							},
							{
								iconCls: 'gis-grid-row-icon-delete',
								getClass: function() {
									return 'tooltip-legendset-delete';
								},
								handler: function(grid, rowIndex, colIndex, col, event) {
									var record = this.up('grid').store.getAt(rowIndex),
										id = record.data.id,
										name = record.data.name,
										message = 'Delete legend set?\n\n' + name;

									if (confirm(message)) {
										deleteLegendSet(id);
									}
								}
							}
						]
					},
					{
						sortable: false,
						width: 17
					}
				],
				store: legendSetStore,
				listeners: {
					render: function() {
						var that = this,
							maxHeight = GIS.cmp.region.center.getHeight() - 155,
							height;

						this.store.on('load', function() {
							if (Ext.isDefined(that.setHeight)) {
								height = 1 + that.store.getCount() * gis.conf.layout.grid.row_height;
								that.setHeight(height > maxHeight ? maxHeight : height);
								window.doLayout();
							}
						});

						this.store.load();
					},
					afterrender: function() {
						var fn = function() {
							var editArray = document.getElementsByClassName('tooltip-legendset-edit'),
								deleteArray = document.getElementsByClassName('tooltip-legendset-delete'),
								len = editArray.length,
								el;

							for (var i = 0; i < len; i++) {
								el = editArray[i];
								Ext.create('Ext.tip.ToolTip', {
									target: el,
									html: 'Rename',
									'anchor': 'bottom',
									anchorOffset: -14,
									showDelay: 1000
								});

								el = deleteArray[i];
								Ext.create('Ext.tip.ToolTip', {
									target: el,
									html: 'Delete',
									'anchor': 'bottom',
									anchorOffset: -14,
									showDelay: 1000
								});
							}
						};

						Ext.defer(fn, 100);
					},
					itemmouseenter: function(grid, record, item) {
						this.currentItem = Ext.get(item);
						this.currentItem.removeCls('x-grid-row-over');
					},
					select: function() {
						this.currentItem.removeCls('x-grid-row-selected');
					},
					selectionchange: function() {
						this.currentItem.removeCls('x-grid-row-focused');
					}
				}
			});

			items = [
				{
					xtype: 'panel',
					layout: 'hbox',
					cls: 'gis-container-inner',
					style: 'margin-bottom: 5px',
					items: [
						addButton
					]
				},
				legendSetGrid
			];

			return items;
		};

		LegendPanel = function(id) {
			var panel,
				addLegend,
				reset,
				data = [];

			tmpLegendStore = Ext.create('Ext.data.ArrayStore', {
				fields: ['id', 'name', 'startValue', 'endValue', 'color']
			});

			legendSetName = Ext.create('Ext.form.field.Text', {
				cls: 'gis-textfield',
				width: 422,
				height: 25,
				fieldStyle: 'padding-left: 6px; border-color: #bbb',
				fieldLabel: 'Legend set name' //i18n
			});

			legendName = Ext.create('Ext.form.field.Text', {
				cls: 'gis-textfield',
				fieldStyle: 'padding-left: 6px',
				width: 404,
				height: 23,
				fieldLabel: 'Legend name' //i18n
			});

			startValue = Ext.create('Ext.form.field.Number', {
				width: 148,
				height: 23,
				allowDecimals: false,
				fieldStyle: 'padding-left: 6px; border-radius: 1px',
				value: 0
			});

			endValue = Ext.create('Ext.form.field.Number', {
				width: 148,
				height: 23,
				allowDecimals: false,
				fieldStyle: 'padding-left: 6px; border-radius: 1px',
				value: 0,
				style: 'padding-left: 3px'
			});

			color = Ext.create('Ext.ux.button.ColorButton', {
				width: 299,
				height: 23,
				fieldLabel: 'Symbolizer', //i18n
				style: 'border-radius: 1px',
				value: 'e1e1e1'
			});

			addLegend = Ext.create('Ext.button.Button', {
				text: 'Add legend', //i18n
				height: 26,
				style: 'border-radius: 1px',
				handler: function() {
					var date = new Date(),
						id = date.toISOString(),
						ln = legendName.getValue(),
						sv = startValue.getValue(),
						ev = endValue.getValue(),
						co = color.getValue().toUpperCase(),
						items = tmpLegendStore.data.items,
						data = [];

					if (ln && (ev > sv)) {
						for (var i = 0; i < items.length; i++) {
							data.push(items[i].data);
						}

						data.push({
							id: id,
							name: ln,
							startValue: sv,
							endValue: ev,
							color: '#' + co
						});

						Ext.Array.sort(data, function (a, b) {
							return a.startValue - b.startValue;
						});

						tmpLegendStore.removeAll();
						tmpLegendStore.add(data);

						legendName.reset();
						startValue.reset();
						endValue.reset();
						color.reset();
					}
				}
			});

			legendGrid = Ext.create('Ext.grid.Panel', {
				cls: 'gis-grid',
				bodyStyle: 'border-top: 0 none',
				width: 422,
				height: 235,
				scroll: 'vertical',
				hideHeaders: true,
				currentItem: null,
				columns: [
					{
						dataIndex: 'name',
						sortable: false,
						width: 250
					},
					{
						sortable: false,
						width: 45,
						renderer: function(value, metaData, record) {
							return '<span style="color:' + record.data.color + '">Color</span>';
						}
					},
					{
						dataIndex: 'startValue',
						sortable: false,
						width: 45
					},
					{
						dataIndex: 'endValue',
						sortable: false,
						width: 45
					},
					{
						xtype: 'actioncolumn',
						sortable: false,
						width: 20,
						items: [
							{
								iconCls: 'gis-grid-row-icon-delete',
								getClass: function() {
									return 'tooltip-legend-delete';
								},
								handler: function(grid, rowIndex, colIndex, col, event) {
									var id = this.up('grid').store.getAt(rowIndex).data.id;
									deleteLegend(id);
								}
							}
						]
					},
					{
						sortable: false,
						width: 17
					}
				],
				store: tmpLegendStore,
				listeners: {
					itemmouseenter: function(grid, record, item) {
						this.currentItem = Ext.get(item);
						this.currentItem.removeCls('x-grid-row-over');
					},
					select: function() {
						this.currentItem.removeCls('x-grid-row-selected');
					},
					selectionchange: function() {
						this.currentItem.removeCls('x-grid-row-focused');
					},
					afterrender: function() {
						var fn = function() {
							var deleteArray = document.getElementsByClassName('tooltip-legend-delete'),
								len = deleteArray.length,
								el;

							for (var i = 0; i < len; i++) {
								el = deleteArray[i];
								Ext.create('Ext.tip.ToolTip', {
									target: el,
									html: 'Delete',
									'anchor': 'bottom',
									anchorOffset: -14,
									showDelay: 1000
								});
							}
						};

						Ext.defer(fn, 100);
					}
				}
			});

			panel = Ext.create('Ext.panel.Panel', {
				cls: 'gis-container-inner',
				legendSetId: id,
				items: [
					legendSetName,
					{
						cls: 'gis-panel-html-separator'
					},
					{
						html: 'Add legend', //i18n
						cls: 'gis-panel-html-title'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						bodyStyle: 'background-color: #f1f1f1; border: 1px solid #ccc; border-radius: 1px; padding: 8px',
						items: [
							legendName,
							{
								layout: 'hbox',
								bodyStyle: 'background: transparent',
								items: [
									{
										html: 'Start / end value:', //i18n
										width: 105,
										bodyStyle: 'background: transparent; padding-top: 3px'
									},
									startValue,
									endValue
								]
							},
							{
								layout: 'column',
								cls: 'gis-container-inner',
								bodyStyle: 'background: transparent',
								items: [
									{
										cls: 'gis-panel-html-label',
										html: 'Symbolizer:', //i18n
										bodyStyle: 'background: transparent',
										width: gis.conf.layout.widget.itemlabel_width + 10
									},
									color
								]
							},
						]
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						cls: 'gis-container-inner',
						bodyStyle: 'text-align: right',
						width: 422,
						items: addLegend
					},
					{
						html: 'Current legends', //i18n
						cls: 'gis-panel-html-title'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					legendGrid
				]
			});

			if (id) {
				legendStore.proxy.url = gis.baseUrl + gis.conf.url.path_api +  'mapLegendSets/' + id + '.json?links=false&paging=false';
				legendStore.load();

				legendSetName.setValue(legendSetStore.getById(id).data.name);
			}

			return panel;
		};

		showUpdateLegendSet = function(id) {
			legendPanel = new LegendPanel(id);
			window.removeAll();
			window.add(legendPanel);
			info.hide();
			cancel.show();

			if (id) {
				update.show();
			}
			else {
				create.show();
			}
		};

		deleteLegendSet = function(id) {
			if (id) {
				Ext.Ajax.request({
					url: gis.baseUrl + gis.conf.url.path_api + 'mapLegendSets/' + id,
					method: 'DELETE',
					success: function() {
						legendSetStore.load();
						GIS.store.legendSets.load();
					}
				});
			}
		};

		deleteLegend = function(id) {
			tmpLegendStore.remove(tmpLegendStore.getById(id));
		};

		getRequestBody = function() {
			var items = tmpLegendStore.data.items,
				body;

			body = {
				name: legendSetName.getValue(),
				symbolizer: gis.conf.finals.widget.symbolizer_color,
				mapLegends: []
			};

			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				body.mapLegends.push({
					name: item.data.name,
					startValue: item.data.startValue,
					endValue: item.data.endValue,
					color: item.data.color
				});
			}

			return body;
		};

		reset = function() {
			legendPanel.destroy();
			legendSetPanel = new LegendSetPanel();
			window.removeAll();
			window.add(legendSetPanel);

			info.show();
			cancel.hide();
			create.hide();
			update.hide();
		};

		validateLegends = function() {
			var items = tmpLegendStore.data.items,
				item,
				prevItem;

			if (items.length === 0) {
				alert('No legend set name');
				return false;
			}

			for (var i = 1; i < items.length; i++) {
				item = items[i].data;
				prevItem = items[i - 1].data;

				if (item.startValue < prevItem.endValue) {
					var msg = 'Overlapping legends not allowed!\n\n' +
							  prevItem.name + ' (' + prevItem.startValue + ' - ' + prevItem.endValue + ')\n' +
							  item.name + ' (' + item.startValue + ' - ' + item.endValue + ')';
					alert(msg);
					return false;
				}

				if (prevItem.endValue < item.startValue) {
					var msg = 'Legend gaps detected!\n\n' +
							  prevItem.name + ' (' + prevItem.startValue + ' - ' + prevItem.endValue + ')\n' +
							  item.name + ' (' + item.startValue + ' - ' + item.endValue + ')\n\n' +
							  'Proceed anyway?';

					if (!confirm(msg)) {
						return false;
					}
				}
			}

			return true;
		};

		create = Ext.create('Ext.button.Button', {
			text: 'Create', //i18n
			hidden: true,
			handler: function() {
				if (legendSetName.getValue() && validateLegends()) {
					if (legendSetStore.findExact('name', legendSetName.getValue()) !== -1) {
						alert('Name already in use');
						return;
					}

					var body = Ext.encode(getRequestBody());

					Ext.Ajax.request({
						url: gis.baseUrl + gis.conf.url.path_api + 'mapLegendSets/',
						method: 'POST',
						headers: {'Content-Type': 'application/json'},
						params: body,
						success: function() {
							GIS.store.legendSets.load();
							reset();
						}
					});
				}
			}
		});

		update = Ext.create('Ext.button.Button', {
			text: 'Update', //i18n
			hidden: true,
			handler: function() {
				if (legendSetName.getValue() && validateLegends()) {
					var body = getRequestBody(),
						id = legendPanel.legendSetId;
					body.id = id;
					body = Ext.encode(getRequestBody());

					Ext.Ajax.request({
						url: gis.baseUrl + gis.conf.url.path_api + 'mapLegendSets/' + id,
						method: 'PUT',
						headers: {'Content-Type': 'application/json'},
						params: body,
						success: function() {
							reset();
						}
					});
				}
			}
		});

		cancel = Ext.create('Ext.button.Button', {
			text: 'Cancel', //i18n
			hidden: true,
			handler: function() {
				reset();
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'gis-label-info',
			width: 400,
			height: 22
		});

		window = Ext.create('Ext.window.Window', {
			title: 'Legend sets', //i18n
			iconCls: 'gis-window-title-icon-legendset', //todo
			cls: 'gis-container-default',
			resizable: false,
			width: 450,
			modal: true,
			items: new LegendSetPanel(),
			bbar: {
				height: 27,
				items: [
					info,
					cancel,
					'->',
					create,
					update
				]
			},
			listeners: {
				show: function() {
					this.setPosition(185, 37);
				}
			}
		});

		return window;
	};

	GIS.app.DownloadWindow = function() {
		var window,
			textfield,
			button;

		textfield = Ext.create('Ext.form.field.Text', {
			cls: 'gis-textfield',
			height: 26,
			width: 230,
			fieldStyle: 'padding-left: 5px',
			emptyText: 'Enter map title' //i18n
		});

		button = Ext.create('Ext.button.Button', {
			text: 'Download', //i18n
			handler: function() {
				var title = textfield.getValue(),
					svg = util.svg.getString(title, util.map.getVisibleVectorLayers()),
					exportForm = document.getElementById('exportForm');

				if (!svg) {
					alert('Please create a map first'); //todo //i18n
					return;
				}

				document.getElementById('svgField').value = svg;
				document.getElementById('titleField').value = title;
				exportForm.action = '../exportImage.action';
				exportForm.method = 'post';
				exportForm.submit();

				window.destroy();
			}
		});

		window = Ext.create('Ext.window.Window', {
			title: 'Download map as PNG', //i18n
			layout: 'fit',
			iconCls: 'gis-window-title-icon-download',
			cls: 'gis-container-default',
			resizable: true,
			modal: true,
			items: textfield,
			bbar: [
				'->',
				button
			],
			listeners: {
				show: function() {
					this.setPosition(253, 37);
				}
			}
		});

		return window;
	};

	GIS.app.InterpretationWindow = function() {
		var window,
			textarea,
			panel,
			button;

		textarea = Ext.create('Ext.form.field.TextArea', {
			cls: 'gis-textarea',
			height: 130,
			fieldStyle: 'padding-left: 4px; padding-top: 3px',
			emptyText: 'Write your interpretation' //i18n
		});

		panel = Ext.create('Ext.panel.Panel', {
			cls: 'gis-container-inner',
			html: '<b>Link: </b>' + gis.init.contextPath + '/dhis-web-mapping/app/index.html?id=' + gis.olmap.mapViewLoader.id,
			style: 'padding-top: 9px; padding-bottom: 2px'
		});

		button = Ext.create('Ext.button.Button', {
			text: 'Share', //i18n
			handler: function() {
				if (textarea.getValue() && gis.olmap.mapViewLoader) {
					Ext.Ajax.request({
						url: gis.baseUrl + gis.conf.url.path_api + 'interpretations/map/' + gis.olmap.mapViewLoader.id,
						method: 'POST',
						params: textarea.getValue(),
						headers: {'Content-Type': 'text/html'},
						success: function() {
							window.destroy();
						}
					});
				}
			}
		});

		window = Ext.create('Ext.window.Window', {
			title: 'Share interpretation', //i18n
			layout: 'fit',
			iconCls: 'gis-window-title-icon-interpretation',
			cls: 'gis-container-default',
			width: 500,
			resizable: true,
			modal: true,
			items: [
				textarea,
				panel
			],
			bbar: [
				'->',
				button
			],
			listeners: {
				show: function() {
					this.setPosition(325, 37);
				},
				destroy: function() {
					document.body.oncontextmenu = function(){
						return false;
					};
				}

			}
		});

		document.body.oncontextmenu = true; // right click to copy url

		return window;
	};

	GIS.app.CircleLayer = function(features, radius) {
		var points = util.map.getPointsByFeatures(features),
			lonLats = util.map.getLonLatsByPoints(points),
			controls = [],
			control,
			layer = new OpenLayers.Layer.Vector(),
			deactivateControls,
			createCircles,
			params = {};

		radius = radius && Ext.isNumber(parseInt(radius)) ? parseInt(radius) : 5;

		deactivateControls = function() {
			for (var i = 0; i < controls.length; i++) {
				controls[i].deactivate();
			}
		};

		createCircles = function() {
			if (lonLats.length) {
				for (var i = 0; i < lonLats.length; i++) {
					control = new OpenLayers.Control.Circle({
						layer: layer
					});
					control.lonLat = lonLats[i];
					controls.push(control);
				}

				gis.olmap.addControls(controls);

				for (var i = 0; i < controls.length; i++) {
					control = controls[i];
					control.activate();
					control.updateCircle(control.lonLat, radius);
				}
			}
			else {
				alert('No facilities');
			}
		};

		createCircles();

		layer.deactivateControls = deactivateControls;

		return layer;
	};

	// Base extensions
	gis.olmap.base.boundary.menu = GIS.app.LayerMenu(gis.olmap.base.boundary, 'gis-toolbar-btn-menu-first');
	gis.olmap.base.boundary.widget = Ext.create('mapfish.widgets.geostat.Boundary', {
        map: gis.olmap,
        layer: gis.olmap.base.boundary.layer,
        menu: gis.olmap.base.boundary.menu
    });
    gis.olmap.base.boundary.window = GIS.app.WidgetWindow(gis.olmap.base.boundary);

	gis.olmap.base.thematic1.menu = GIS.app.LayerMenu(gis.olmap.base.thematic1);
	gis.olmap.base.thematic1.widget = Ext.create('mapfish.widgets.geostat.Thematic1', {
        map: gis.olmap,
        layer: gis.olmap.base.thematic1.layer,
        menu: gis.olmap.base.thematic1.menu,
        legendDiv: gis.olmap.base.thematic1.legendDiv
    });
    gis.olmap.base.thematic1.window = GIS.app.WidgetWindow(gis.olmap.base.thematic1);

	gis.olmap.base.thematic2.menu = GIS.app.LayerMenu(gis.olmap.base.thematic2);
	gis.olmap.base.thematic2.widget = Ext.create('mapfish.widgets.geostat.Thematic2', {
        map: gis.olmap,
        layer: gis.olmap.base.thematic2.layer,
        menu: gis.olmap.base.thematic2.menu,
        legendDiv: gis.olmap.base.thematic2.legendDiv
    });
    gis.olmap.base.thematic2.window = GIS.app.WidgetWindow(gis.olmap.base.thematic2);

	gis.olmap.base.facility.menu = GIS.app.LayerMenu(gis.olmap.base.facility);
	gis.olmap.base.facility.widget = Ext.create('mapfish.widgets.geostat.Facility', {
        map: gis.olmap,
        layer: gis.olmap.base.facility.layer,
        menu: gis.olmap.base.facility.menu,
        legendDiv: gis.olmap.base.facility.legendDiv
    });
    gis.olmap.base.facility.window = GIS.app.WidgetWindow(gis.olmap.base.facility);

	// User interface

	GIS.gui.viewport = Ext.create('Ext.container.Viewport', {
		layout: 'border',
		items: [
			{
				region: 'east',
				layout: 'anchor',
				width: 200,
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
				items: [
                    {
                        title: 'Layer overview and visibility %', //i18n
                        bodyStyle: 'padding: 6px',
                        items: GIS.app.LayersPanel(),
                        collapsible: true,
                        animCollapse: false
                    },
                    {
                        title: 'Thematic layer 1 legend', //i18n
                        bodyStyle: 'padding: 6px; border: 0 none',
                        collapsible: true,
                        collapsed: true,
                        animCollapse: false,
                        listeners: {
							added: function() {
								gis.olmap.base.thematic1.layer.legendPanel = this;
							}
						}
                    },
                    {
                        title: 'Thematic layer 2 legend', //i18n
                        contentEl: 'thematic2Legend',
                        bodyStyle: 'padding: 6px; border: 0 none',
                        collapsible: true,
                        collapsed: true,
                        animCollapse: false,
                        listeners: {
							added: function() {
								gis.olmap.base.thematic2.layer.legendPanel = this;
							}
						}
                    },
                    {
                        title: 'Facility layer legend', //i18n
                        contentEl: 'facilityLegend',
                        bodyStyle: 'padding: 6px; border: 0 none',
                        collapsible: true,
                        collapsed: true,
                        animCollapse: false,
                        listeners: {
							added: function() {
								gis.olmap.base.facility.layer.legendPanel = this;
							}
						}
                    }
				],
				listeners: {
					added: function() {
						GIS.cmp.region.east = this;
						gis.olmap.legendPanel = this;
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
                map: gis.olmap,
				cmp: {
					tbar: {}
				},
                tbar: {
					defaults: {
						height: 26
					},
					items: function() {
						var a = [];
						a.push({
							iconCls: 'gis-btn-icon-' + gis.olmap.base.boundary.id,
							menu: gis.olmap.base.boundary.menu,
							width: 26
						});
						a.push({
							iconCls: 'gis-btn-icon-' + gis.olmap.base.thematic1.id,
							menu: gis.olmap.base.thematic1.menu,
							width: 26
						});
						a.push({
							iconCls: 'gis-btn-icon-' + gis.olmap.base.thematic2.id,
							menu: gis.olmap.base.thematic2.menu,
							width: 26
						});
						a.push({
							iconCls: 'gis-btn-icon-' + gis.olmap.base.facility.id,
							menu: gis.olmap.base.facility.menu,
							width: 26
						});
						a.push({
							text: 'Favorites', //i18n
							menu: {},
							handler: function() {
								if (GIS.cmp.mapWindow && GIS.cmp.mapWindow.destroy) {
									GIS.cmp.mapWindow.destroy();
								}

								GIS.cmp.mapWindow = GIS.app.MapWindow();
								GIS.cmp.mapWindow.show();
							}
						});
						if (gis.init.security.isAdmin) {
							a.push({
								text: 'Legend', //i18n
								menu: {},
								handler: function() {
									if (GIS.cmp.legendSetWindow && GIS.cmp.legendSetWindow.destroy) {
										GIS.cmp.legendSetWindow.destroy();
									}

									GIS.cmp.legendSetWindow = GIS.app.LegendSetWindow();
									GIS.cmp.legendSetWindow.show();
								}
							});
						}
						a.push({
							xtype: 'tbseparator',
							height: 18,
							style: 'border-color: transparent #d1d1d1 transparent transparent; margin-right: 4px',
						});
						a.push({
							text: 'Download', //i18n
							menu: {},
							disabled: true,
							handler: function() {
								if (GIS.cmp.downloadWindow && GIS.cmp.downloadWindow.destroy) {
									GIS.cmp.downloadWindow.destroy();
								}

								GIS.cmp.downloadWindow = GIS.app.DownloadWindow();
								GIS.cmp.downloadWindow.show();
							},
							xable: function() {
								if (util.map.hasVisibleFeatures()) {
									this.enable();
								}
								else {
									this.disable();
								}
							},
							listeners: {
								added: function() {
									GIS.cmp.downloadButton = this;
								}
							}
						});
						a.push({
							text: 'Share', //i18n
							menu: {},
							disabled: true,
							handler: function() {
								if (GIS.cmp.interpretationWindow && GIS.cmp.interpretationWindow.destroy) {
									GIS.cmp.interpretationWindow.destroy();
								}

								GIS.cmp.interpretationWindow = GIS.app.InterpretationWindow();
								GIS.cmp.interpretationWindow.show();
							},
							listeners: {
								added: function() {
									GIS.cmp.interpretationButton = this;
								}
							}
						});
						a.push('->');
						a.push({
							text: 'Exit', //i18n
							handler: function() {
								window.location.href = '../../dhis-web-commons-about/redirect.action';
							}
						});
						a.push({
							text: '>>>', //i18n
							handler: function() {
								GIS.cmp.region.east.toggleCollapse();
							},
							listeners: {
								render: function() {
									GIS.cmp.region.center.cmp.tbar.resize = this;
								}
							}
						});
						return a;
					}()
				},
				listeners: {
					added: function() {
						GIS.cmp.region.center = this;
					}
				}
            }
		],
		listeners: {
			render: gis.init.onRender,
			afterrender: gis.init.afterRender,
			added: function() {
				gis.olmap.viewport = this;
			}
		}
	});

	}});
});
