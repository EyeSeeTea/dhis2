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
					dv.viewport.westRegion.setWidth(dv.conf.layout.west_width);
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
			defaultUrl: dv.baseUrl + '/api/charts.json?links=false',
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

	DV.app.OptionsWindow = function() {
		var showTrendLine,
			targetLineValue,
			targetLineTitle,
			baseLineValue,
			baseLineTitle,

			showValues,
			hideChartLegend,
			hideChartSubtitle,
			domainAxisTitle,
			rangeAxisTitle,

			data,
			style,

			window;

		showTrendLine = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: DV.i18n.trend_line,
			style: 'margin-bottom:4px',
			checked: true
		});
		dv.viewport.showTrendLine = showTrendLine;

		targetLineValue = Ext.create('Ext.form.field.Number', {
			//cls: 'gis-numberfield',
			width: 60,
			height: 18,
			fieldLabel: 'Target line value/title', //i18n
			listeners: {
				change: function(nf) {
					targetLineTitle.xable();
				}
			}
		});
		dv.viewport.targetLineValue = targetLineValue;

		targetLineTitle = Ext.create('Ext.form.field.Text', {
			//cls: 'dv-textfield-alt1',
			style: 'margin-left:2px',
			maxLength: 100,
			enforceMaxLength: true,
			xable: function() {
				this.setDisabled(!targetLineValue.getValue());
			}
		});
		dv.viewport.targetLineTitle = targetLineTitle;

		baseLineValue = Ext.create('Ext.form.field.Number', {
			//cls: 'gis-numberfield',
			width: 60,
			height: 18,
			fieldLabel: 'Base line value/title', //i18n
			listeners: {
				change: function(nf) {
					baseLineTitle.xable();
				}
			}
		});
		dv.viewport.baseLineValue = baseLineValue;

		baseLineTitle = Ext.create('Ext.form.field.Text', {
			//cls: 'dv-textfield-alt1',
			style: 'margin-left:2px',
			maxLength: 100,
			enforceMaxLength: true,
			xable: function() {
				this.setDisabled(!baseLineValue.getValue());
			}
		});
		dv.viewport.baseLineTitle = baseLineTitle;

		showValues = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: DV.i18n.show_values,
			style: 'margin-bottom:4px',
			checked: true
		});
		dv.viewport.showValues = showValues;

		hideChartLegend = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: DV.i18n.hide_chart_legend,
			style: 'margin-bottom:4px',
			checked: true
		});
		dv.viewport.hideChartLegend = hideChartLegend;

		hideChartSubtitle = Ext.create('Ext.form.field.Checkbox', {
			boxLabel: DV.i18n.hide_chart_subtitle,
			style: 'margin-bottom:4px',
			checked: true
		});
		dv.viewport.hideChartSubtitle = hideChartSubtitle;

		domainAxisTitle = Ext.create('Ext.form.field.Text', {
			//cls: 'dv-textfield-alt1',
			style: 'margin-left:2px',
			fieldLabel: DV.i18n.domain_axis_label,
			maxLength: 100,
			enforceMaxLength: true
		});
		dv.viewport.domainAxisTitle = domainAxisTitle;

		rangeAxisTitle = Ext.create('Ext.form.field.Text', {
			//cls: 'dv-textfield-alt1',
			style: 'margin-left:2px',
			fieldLabel: DV.i18n.domain_axis_label,
			maxLength: 100,
			enforceMaxLength: true
		});
		dv.viewport.rangeAxisTitle = rangeAxisTitle;

		data = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showTrendLine,
				{
					layout: 'column',
					items: [
						targetLineValue,
						targetLineTitle
					]
				},
				{
					layout: 'column',
					items: [
						baseLineValue,
						baseLineTitle
					]
				}
			]
		};

		style = {
			bodyStyle: 'border:0 none',
			style: 'margin-left:14px',
			items: [
				showValues,
				hideChartLegend,
				hideChartSubtitle,
				domainAxisTitle,
				rangeAxisTitle
			]
		};

		window = Ext.create('Ext.window.Window', {
			title: DV.i18n.table_options,
			bodyStyle: 'background-color:#fff; padding:8px 8px 8px',
			closeAction: 'hide',
			autoShow: true,
			modal: true,
			resizable: false,
			hideOnBlur: true,
			getOptions: function() {
				return {
					showTrendLine: showTrendLine.getValue(),
					targetLineValue: targetLineValue.getValue(),
					targetLineTitle: targetLineTitle.getValue(),
					baseLineValue: baseLineValue.getValue(),
					baseLineTitle: baseLineTitle.getValue(),
					showValues: showValues.getValue(),
					hideChartLegend: hideChartLegend.getValue(),
					hideChartSubtitle: hideChartSubtitle.getValue(),
					domainAxisTitle: domainAxisTitle.getValue(),
					rangeAxisTitle: rangeAxisTitle.getValue()
				};
			},
			items: [
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px',
					html: DV.i18n.data
				},
				data,
				{
					bodyStyle: 'border:0 none; padding:7px'
				},
				{
					bodyStyle: 'border:0 none; color:#222; font-size:12px; font-weight:bold',
					style: 'margin-bottom:6px',
					html: DV.i18n.style
				},
				style
			],
			bbar: [
				'->',
				{
					text: DV.i18n.hide,
					handler: function() {
						window.hide();
					}
				},
				{
					text: '<b>' + DV.i18n.update + '</b>',
					handler: function() {
						dv.viewport.updateViewport();
						window.hide();
					}
				}
			],
			listeners: {
				show: function(w) {
					dv.util.window.setAnchorPosition(w, dv.viewport.optionsButton);

					if (!w.hasHideOnBlurHandler) {
						dv.util.window.addHideOnBlurHandler(w);
					}
				}
			}
		});

		return window;
	};

	DV.app.FavoriteWindow = function() {

		// Objects
		var NameWindow,

		// Instances
			nameWindow,

		// Functions
			getBody,

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
			createButton,
			updateButton,
			cancelButton,
			favoriteWindow,

		// Vars
			windowWidth = 500,
			windowCmpWidth = windowWidth - 22;

		DV.store.charts.on('load', function(store, records) {
			var pager = store.proxy.reader.jsonData.pager;

			info.setText('Page ' + pager.page + ' of ' + pager.pageCount);

			prevButton.enable();
			nextButton.enable();

			if (pager.page === 1) {
				prevButton.disable();
			}

			if (pager.page === pager.pageCount) {
				nextButton.disable();
			}
		});

		getBody = function() {
			var favorite;

			if (DV.c.rendered) {
				favorite = {};

				favorite.type = DV.c.type;
				favorite.series = DV.c.dimension.series;
				favorite.category = DV.c.dimension.category;
				favorite.filter = DV.c.dimension.filter;
				favorite.hideLegend = DV.c.hidelegend;
				favorite.hideSubtitle = DV.c.hidesubtitle;
				favorite.showData = DV.c.showdata;
				favorite.regression = DV.c.trendline;
				favorite.userOrganisationUnit = DV.c.userorganisationunit;
				favorite.userOrganisationUnitChildren = DV.c.userorganisationunitchildren;

				// Options
				if (DV.c.domainaxislabel) {
					favorite.domainAxisLabel = DV.c.domainaxislabel;
				}
				if (DV.c.rangeaxislabel) {
					favorite.rangeAxisLabel = DV.c.rangeaxislabel;
				}
				if (DV.c.targetlinevalue) {
					favorite.targetLineValue = DV.c.targetlinevalue;
				}
				if (DV.c.targetlinelabel) {
					favorite.targetLineLabel = DV.c.targetlinelabel;
				}
				if (DV.c.baselinevalue) {
					favorite.baseLineValue = DV.c.baselinevalue;
				}
				if (DV.c.baselinelabel) {
					favorite.baseLineLabel = DV.c.baselinelabel;
				}

				// Indicators
				if (Ext.isObject(DV.c.indicator) && Ext.isArray(DV.c.indicator.records) && DV.c.indicator.records.length) {
					favorite.indicators = [];

					for (var i = 0, r; i < DV.c.indicator.records.length; i++) {
						r = Ext.clone(DV.c.indicator.records[i]);
						favorite.indicators.push({id: r.id, name: r.name});
					}
				}

				// Data elements
				if (Ext.isObject(DV.c.dataelement) && Ext.isArray(DV.c.dataelement.records) && DV.c.dataelement.records.length) {
					favorite.dataElements = [];

					for (var i = 0, r; i < DV.c.dataelement.records.length; i++) {
						r = Ext.clone(DV.c.dataelement.records[i]);
						favorite.dataElements.push({id: r.id, name: r.name});
					}
				}

				// Data sets
				if (Ext.isObject(DV.c.dataset) && Ext.isArray(DV.c.dataset.records) && DV.c.dataset.records.length) {
					favorite.dataSets = [];

					for (var i = 0, r; i < DV.c.dataset.records.length; i++) {
						r = Ext.clone(DV.c.dataset.records[i]);
						favorite.dataSets.push({id: r.id, name: r.name});
					}
				}

				// Fixed periods
				if (Ext.isObject(DV.c.fixedperiod) && Ext.isArray(DV.c.fixedperiod.records) && DV.c.fixedperiod.records.length) {
					favorite.periods = [];

					for (var i = 0, r; i < DV.c.period.records.length; i++) {
						r = Ext.clone(DV.c.period.records[i]);
						favorite.periods.push({id: r.id, name: r.name});
					}
				}

				// Relative periods
				favorite.relativePeriods = {};

				if (Ext.isObject(DV.c.relativeperiod)) {
					favorite.rewindRelativePeriods = !!DV.c.relativeperiod.rewind;

					if (Ext.isObject(DV.c.relativeperiod.rp)) {
						for (var key in DV.c.relativeperiod.rp) {
							if (DV.c.relativeperiod.rp.hasOwnProperty(key) && !!DV.c.relativeperiod.rp[key]) {
								favorite.relativePeriods[key] = true;
							}
						}
					}
				}

				// Organisation units
				if (Ext.isObject(DV.c.organisationunit)) {
					if (Ext.isString(DV.c.organisationunit.groupsetid)) {
						favorite.organisationUnitGroupSetId = DV.c.organisationunit.groupsetid;
					}

					if (Ext.isArray(DV.c.organisationunit.records) && DV.c.organisationunit.records.length) {
						favorite.organisationUnits = Ext.clone(DV.c.organisationunit.records);
					}
				}
			}

			return favorite;
		};

		NameWindow = function(id) {
			var window,
				record = DV.store.favorite.getById(id);

			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 371,
				fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
				style: 'margin-bottom:0',
				emptyText: 'Favorite name',
				value: id ? record.data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});

			createButton = Ext.create('Ext.button.Button', {
				text: 'Create', //i18n
				handler: function() {
					var favorite = getBody();
					favorite.name = nameTextfield.getValue();

					if (favorite && favorite.name) {
						Ext.Ajax.request({
							url: DV.init.contextPath + '/api/charts/',
							method: 'POST',
							headers: {'Content-Type': 'application/json'},
							params: Ext.encode(favorite),
							failure: function(r) {
								DV.util.mask.hideMask();
								alert(r.responseText);
							},
							success: function(r) {
								var id = r.getAllResponseHeaders().location.split('/').pop();

								DV.c.currentFavorite = {
									id: id,
									name: favorite.name
								};
								DV.cmp.toolbar.share.xable();
								DV.store.favorite.loadStore();
								window.destroy();
							}
						});
					}
				}
			});

			updateButton = Ext.create('Ext.button.Button', {
				text: 'Update', //i18n
				handler: function() {
					var name = nameTextfield.getValue(),
						favorite;

					if (id && name) {
						Ext.Ajax.request({
							url: DV.init.contextPath + '/api/charts/' + id + '.json?links=false',
							method: 'GET',
							failure: function(r) {
								DV.util.mask.hideMask();
								alert(r.responseText);
							},
							success: function(r) {
								favorite = Ext.decode(r.responseText);
								favorite.name = name;

								Ext.Ajax.request({
									url: DV.init.contextPath + '/api/charts/' + favorite.id,
									method: 'PUT',
									headers: {'Content-Type': 'application/json'},
									params: Ext.encode(favorite),
									failure: function(r) {
										DV.util.mask.hideMask();
										alert(r.responseText);
									},
									success: function(r) {
										DV.store.favorite.loadStore();
										window.destroy();
									}
								});
							}
						});
					}
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
				//iconCls: 'dv-window-title-icon-favorite',
				bodyStyle: 'padding:2px; background:#fff',
				resizable: false,
				modal: true,
				items: nameTextfield,
				//destroyOnBlur: true,
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function(w) {
						DV.util.window.setAnchorPosition(w, addButton);

						//if (!w.hasDestroyBlurHandler) {
							//pt.util.window.addDestroyOnBlurHandler(w);
						//}

						//pt.viewport.favoriteWindow.destroyOnBlur = false;
					},
					destroy: function() {
						//pt.viewport.favoriteWindow.destroyOnBlur = true;
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
			disabled: !DV.c.rendered,
			handler: function() {
				nameWindow = new NameWindow(null, 'create');
				nameWindow.show();
			}
		});

		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: windowCmpWidth - addButton.width - 11,
			height: 26,
			fieldStyle: 'padding-right: 0; padding-left: 6px; border-radius: 1px; border-color: #bbb; font-size:11px',
			emptyText: 'Search for favorites', //i18n
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: function() {
					if (this.getValue() !== this.currentValue) {
						this.currentValue = this.getValue();

						var value = this.getValue(),
							url = value ? DV.init.contextPath + '/api/charts/query/' + value + '.json?links=false' : null,
							store = DV.store.favorite;

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
					url = value ? DV.init.contextPath + '/api/charts/query/' + value + '.json?links=false' : null,
					store = DV.store.favorite;

				store.page = store.page <= 1 ? 1 : store.page - 1;
				store.loadStore(url);
			}
		});

		nextButton = Ext.create('Ext.button.Button', {
			text: 'Next', //i18n
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? DV.init.contextPath + '/api/charts/query/' + value + '.json?links=false' : null,
					store = DV.store.favorite;

				store.page = store.page + 1;
				store.loadStore(url);
			}
		});

		info = Ext.create('Ext.form.Label', {
			cls: 'dv-label-info',
			width: 300,
			height: 22
		});

		grid = Ext.create('Ext.grid.Panel', {
			cls: 'dv-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: windowCmpWidth - 88,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var element = Ext.get(record.data.id);
							if (element) {
								element = element.parent('td');
								element.addClsOnOver('link');
								element.load = function() {
									favoriteWindow.hide();
									DV.exe.execute(record.data.id);
								};
								element.dom.setAttribute('onclick', 'Ext.get(this).load();');
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
							iconCls: 'dv-grid-row-icon-edit',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-edit' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.update) {
									nameWindow = new NameWindow(record.data.id);
									nameWindow.show();
								}
							}
						},
						{
							iconCls: 'dv-grid-row-icon-overwrite',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-overwrite' + (!record.data.access.update ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message,
									favorite;

								if (record.data.access.update) {
									message = 'Overwrite favorite?\n\n' + record.data.name;
									favorite = getBody();

									if (favorite) {
										favorite.name = record.data.name;

										if (confirm(message)) {
											Ext.Ajax.request({
												url: DV.init.contextPath + '/api/charts/' + record.data.id,
												method: 'PUT',
												headers: {'Content-Type': 'application/json'},
												params: Ext.encode(favorite),
												success: function() {
													DV.cmp.toolbar.share.enable();
													DV.c.currentFavorite = {
														id: record.data.id,
														name: favorite.name
													};
													DV.store.favorite.loadStore();
												}
											});
										}
									}
								}
								else {
									alert('Please create a table first'); //i18n
								}
							}
						},
						{
							iconCls: 'dv-grid-row-icon-sharing',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-sharing' + (!record.data.access.manage ? ' disabled' : '');
							},
							handler: function(grid, rowIndex) {
								var record = this.up('grid').store.getAt(rowIndex);

								if (record.data.access.manage) {
									Ext.Ajax.request({
										url: DV.init.contextPath + '/api/sharing?type=chart&id=' + record.data.id,
										method: 'GET',
										failure: function(r) {
											DV.util.mask.hideMask();
											alert(r.responseText);
										},
										success: function(r) {
											var sharing = Ext.decode(r.responseText),
												window = DV.app.SharingWindow(sharing);
											window.show();
										}
									});
								}
							}
						},
						{
							iconCls: 'dv-grid-row-icon-delete',
							getClass: function(value, metaData, record) {
								return 'tooltip-favorite-delete' + (!record.data.access['delete'] ? ' disabled' : '');
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									message;

								if (record.data.access['delete']) {
									message = 'Delete favorite?\n\n' + record.data.name;

									if (confirm(message)) {
										Ext.Ajax.request({
											url: DV.init.contextPath + '/api/charts/' + record.data.id,
											method: 'DELETE',
											success: function() {
												DV.store.favorite.loadStore();
											}
										});
									}
								}
							}
						}
					]
				},
				{
					sortable: false,
					width: 6
				}
			],
			store: DV.store.favorite,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					DV.viewport.favoriteGrid = this;
				},
				render: function() {
					var size = Math.floor((DV.viewport.centerRegion.getHeight() - 155) / DV.conf.layout.grid_row_height);
					this.store.pageSize = size;
					this.store.page = 1;
					this.store.loadStore();

					DV.store.favorite.on('load', function() {
						if (this.isVisible()) {
							this.fireEvent('afterrender');
						}
					}, this);
				},
				afterrender: function() {
					var fn = function() {
						var editArray = Ext.query('.tooltip-favorite-edit'),
							overwriteArray = Ext.query('.tooltip-favorite-overwrite'),
							//dashboardArray = Ext.query('.tooltip-favorite-dashboard'),
							sharingArray = Ext.query('.tooltip-favorite-sharing'),
							deleteArray = Ext.query('.tooltip-favorite-delete'),
							el;

						for (var i = 0; i < editArray.length; i++) {
							var el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Rename', //i18n
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < overwriteArray.length; i++) {
							el = overwriteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Overwrite', //i18n
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < sharingArray.length; i++) {
							el = sharingArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Share with other people', //i18n
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}

						for (var i = 0; i < deleteArray.length; i++) {
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Delete', //i18n
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

		favoriteWindow = Ext.create('Ext.window.Window', {
			title: 'Manage favorites',
			//iconCls: 'dv-window-title-icon-favorite',
			bodyStyle: 'padding:5px; background-color:#fff',
			resizable: false,
			modal: true,
			width: windowWidth,
			destroyOnBlur: true,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					bodyStyle: 'border:0 none',
					items: [
						addButton,
						{
							height: 24,
							width: 1,
							style: 'width:1px; margin-left:5px; margin-right:5px; margin-top:1px',
							bodyStyle: 'border-left: 1px solid #aaa'
						},
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function(w) {
					DV.util.window.setAnchorPosition(w, DV.cmp.toolbar.favorite);

					//if (!w.hasDestroyOnBlurHandler) {
						//pt.util.window.addDestroyOnBlurHandler(w);
					//}
				}
			}
		});

		return favoriteWindow;
	};
