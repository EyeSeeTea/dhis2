DV.app = {};
DV.app.init = {};

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
	Ext.QuickTips.init();

	document.body.oncontextmenu = function() {
		return false;
	};

    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});

	// Init

	var dv = DV.core.getInstance();

	DV.app.getInit = function(r) {
		var init = Ext.decode(r.responseText);

		for (var i = 0; i < init.rootNodes.length; i++) {
			init.rootNodes[i].path = '/' + dv.conf.finals.root.id + '/' + init.rootNodes[i].id;
		}

		// Ougs
		for (var i = 0, dim = dv.conf.finals.dimension, oug; i < init.ougs.length; i++) {
			oug = init.ougs[i];
			oug.dimensionName = oug.id;
			oug.objectName = dv.conf.finals.dimension.organisationUnitGroupSet.objectName;
			dim.objectNameMap[oug.id] = oug;
		}

		// Degs
		for (var i = 0, dim = dv.conf.finals.dimension, deg; i < init.degs.length; i++) {
			deg = init.degs[i];
			deg.dimensionName = deg.id;
			deg.objectName = dv.conf.finals.dimension.dataElementGroupSet.objectName;
			dim.objectNameMap[deg.id] = deg;
		}

		init.afterRender = function() {

			// Resize event handler
			dv.viewport.westRegion.on('resize', function() {
				var panel = dv.util.dimension.panel.getExpanded();

				if (panel) {
					panel.onExpand();
				}
			});

			// Left gui
			var viewportHeight = dv.viewport.westRegion.getHeight(),
				numberOfTabs = dv.init.ougs.length + dv.init.degs.length + 5,
				tabHeight = 28,
				minPeriodHeight = 380,
				settingsHeight = 92;

			if (viewportHeight > numberOfTabs * tabHeight + minPeriodHeight + settingsHeight) {
				if (!Ext.isIE) {
					dv.viewport.accordion.setAutoScroll(false);
					dv.viewport.westRegion.setWidth(pt.conf.layout.west_width);
					dv.viewport.accordion.doLayout();
				}
			}
			else {
				dv.viewport.westRegion.hasScrollbar = true;
			}

			dv.cmp.dimension.panels[0].expand();

			// Load favorite from url
			var id = dv.util.url.getUrlParam('id');

			if (id) {
				dv.util.pivot.loadTable(id);
			}

			// Fade in
			Ext.defer( function() {
				Ext.getBody().fadeIn({
					duration: 400
				});
			}, 500 );
		};

		return init;
	};

	DV.app.getStores = function() {
		var store = dv.store || {};

		store.indicatorAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'indicators'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			listeners: {
				load: function(s) {
					dv.util.store.addToStorage(s);
					dv.util.multiselect.filterAvailable({store: s}, {store: store.indicatorSelected});
				}
			}
		});

		store.indicatorSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.dataElementAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'dataElements'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			listeners: {
				load: function(s) {
					dv.util.store.addToStorage(s);
					dv.util.multiselect.filterAvailable({store: s}, {store: store.dataElementSelected});
				}
			}
		});

		store.dataElementSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.dataSetAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: dv.conf.finals.ajax.path_api + dv.conf.finals.ajax.dataset_get,
				reader: {
					type: 'json',
					root: 'dataSets'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			isLoaded: false,
			listeners: {
				load: function(s) {
					this.isLoaded = true;
					dv.util.store.addToStorage(s);
					dv.util.multiselect.filterAvailable({store: s}, {store: store.dataSetSelected});
				}
			}
		});

		store.dataSetSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.periodType = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: dv.conf.period.periodTypes
		});

		store.fixedPeriodAvailable = Ext.create('Ext.data.Store', {
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
		});

		store.fixedPeriodSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.charts = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'lastUpdated', 'access'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'charts'
				}
			},
			isLoaded: false,
			pageSize: 10,
			page: 1,
			defaultUrl: pt.baseUrl + '/api/charts.json?links=false',
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
				load: function(s) {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}

					this.sort('name', 'ASC');
				}
			}
		});

		return store;
	};
