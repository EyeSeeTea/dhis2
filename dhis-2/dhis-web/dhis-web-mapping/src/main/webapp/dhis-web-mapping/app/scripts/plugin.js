Ext.onReady( function() {
	/*
	CONFIG              			TYPE            DEFAULT             DESCRIPTION

	url                 			string                              (Required) The base url of the DHIS instance.
	el                  			string                              (Required) The element id to render the map.
	id								string                              (Optional) A map uid. If provided, only 'el' and 'url' are required.
	longitude						number			<zoom visible>		(Optional) Initial map center longitude.
	latitude						number			<zoom visible>		(Optional) Initial map center latitude.
	zoom							number			<zoom visible>		(Optional) Initial zoom level.
	mapViews						[object]							(Required*) Array of mapViews. *Required if no map id is provided.

	layer							string			'thematic1'			(Optional) 'boundary', 'thematic1', 'thematic2' or 'facility'.
	indicator          				string								(Required*) Indicator uid. *Required if no data element is provided.
	dataelement	        			string								(Required*) Data element uid. *Required if no indicator is provided.
	period							string								(Required) Fixed period ISO.
	level							number			2					(Optional) Organisation unit level.
	parent							string								(Required) Parent organisation unit uid.
	legendSet						string								(Optional) Legend set uid.
	classes							number			5					(Optional) Automatic legend set, number of classes.
	method							number			2					(Optional) Automatic legend set, method. 2 = by class range. 3 = by class count.
	colorLow						string			'ff0000' (red)		(Optional) Automatic legend set, low color.
	colorHigh						string			'00ff00' (green)	(Optional) Automatic legend set, high color.
	radiusLow						number			5					(Optional) Automatic legend set, low radius for points.
	radiusHigh						number			15					(Optional) Automatic legend set, high radius for points.
	*/

	GIS.getMap = function(config) {



	//};



		var validateConfig,
			fifo,
			getViews,
			createViewport,
			map,
			olmap,
			initialize;


		validateConfig = function() {
			if (!config.url || !Ext.isString(config.url)) {
				alert('Invalid url (' + config.el + ')');
				return false;
			}

			if (!config.el || !Ext.isString(config.el)) {
				alert('Invalid html element id (' + config.el + ')');
				return false;
			}

			if (config.id) {
				if (Ext.isString(config.id)) {
					return true;
				}
				else {
					alert('Invalid map id (' + config.el + ')');
					return false;
				}
			}
			else {
				if (!config.indicator && !config.dataelement) {
					alert('No indicator or data element (' + config.el + ')');
					return false;
				}
				else {
					if (config.indicator && !Ext.isString(config.indicator)) {
						alert('Invalid indicator id (' + config.el + ')');
						return false;
					}
					if (config.dataelement && !Ext.isString(config.dataelement)) {
						alert('Invalid dataelement id (' + config.el + ')');
						return false;
					}
				}

				if (!config.period) {
					alert('No period (' + config.el + ')');
					return false;
				}

				if (!config.level || !Ext.isNumber(config.level)) {
					alert('Invalid organisation unit level (' + config.el + ')');
					return false;
				}

				if (!config.parent || !Ext.isNumber(config.parent)) {
					alert('Invalid parent organisation unit (' + config.el + ')');
					return false;
				}
			}
		};

		getViews = function() {
			var view,
				views = [],
				indicator = GIS.conf.finals.dimension.indicator.id,
				dataElement = GIS.conf.finals.dimension.dataElement.id,
				automatic = GIS.conf.finals.widget.legendtype_automatic,
				predefined = GIS.conf.finals.widget.legendtype_predefined;

			config.mapViews = config.mapViews || [];

			for (var i = 0; i < config.mapViews.length; i++) {
				view = config.mapViews[i];

				view = {
					layer: view.layer || 'thematic1',
					valueType: view.indicator ? indicator : dataElement,
					indicator: {
						id: view.indicator
					},
					dataElement: {
						id: view.dataelement
					},
					period: {
						id: view.period
					},
					legendType: view.legendSet ? predefined : automatic,
					legendSet: view.legendSet,
					classes: parseInt(view.classes) || 5,
					method: parseInt(view.method) || 2,
					colorLow: view.colorLow || 'ff0000',
					colorHigh: view.colorHigh || '00ff00',
					radiusLow: parseInt(view.radiusLow) || 5,
					radiusHigh: parseInt(view.radiusHigh) || 15,
					organisationUnitLevel: {
						level: parseInt(view.level) || 2
					},
					parentOrganisationUnit: {
						id: view.parent
					},
					opacity: parseFloat(view.opacity) || 0.8
				};
			}

			return view;
		};

		createViewport = function() {
			var panel,
				el = Ext.get(map.el);

			panel = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				style: 'padding:0, margin:0',
				bodyStyle: 'padding:0, margin:0',
				width: el.getWidth(),
				height: el.getHeight(),
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items: [
					{
						xtype: 'gx_mappanel',
						map: olmap,
						bodyStyle: 'border:0 none',
						width: el.getWidth() - 200,
						height: el.getHeight()
					},
					{
						xtype: 'panel',
						layout: 'anchor',
						width: 200,
						preventHeader: true,
						defaults: {
							bodyStyle: 'padding: 6px; border: 0 none',
							collapsible: true,
							collapsed: true,
							animCollapse: false
						},
						items: [
							{
								title: 'Thematic layer 1 legend', //i18n
								listeners: {
									added: function() {
										olmap.base.thematic1.layer.legendPanel = this;
									}
								}
							},
							{
								title: 'Thematic layer 2 legend', //i18n
								contentEl: olmap.base.thematic2.core.legendDiv,
								listeners: {
									added: function() {
										olmap.base.thematic2.layer.legendPanel = this;
									}
								}
							},
							{
								title: 'Facility layer legend', //i18n
								contentEl: olmap.base.facility.core.legendDiv,
								listeners: {
									added: function() {
										olmap.base.facility.layer.legendPanel = this;
									}
								}
							}
						],
						listeners: {
							added: function() {
								olmap.legendRegion = this;
							}
						}
					}
				],
				afterrender: function() {
					//document.getElementsByClassName('zoomInButton')[0].innerHTML = '<img src="images/zoomin_24.png" />';
					//document.getElementsByClassName('zoomOutButton')[0].innerHTML = '<img src="images/zoomout_24.png" />';
					//document.getElementsByClassName('zoomVisibleButton')[0].innerHTML = '<img src="images/zoomvisible_24.png" />';
					//document.getElementsByClassName('measureButton')[0].innerHTML = '<img src="images/measure_24.png" />';
				}
			});

			return panel;
		};

		fifo = {
			//queue: [],
			//ready: true,
			//load: function() {

			//callBack: function() {

			//kickAndRun: function(map) {
				//this.queue.push(map);

				//if (this.ready) {
					//var loader = GIS.core.MapLoader(olmap);
					//loader.load(config);
				//}

			//}
		};

		initialize = function() {
			if (!validateConfig()) {
				return;
			}

			GIS.conf.url.base = config.url;
			GIS.store.organisationUnitLevels.load();

			map = {
				url: config.url,
				el: config.el,
				id: config.id,
				longitude: config.longitude,
				latitude: config.latitude,
				zoom: config.zoom,
				mapViews: getViews()
			};

			olmap = GIS.core.OLMap();

			createViewport();

			var loader = GIS.core.MapLoader(olmap);
			loader.load(config);
		}();
	};
});
