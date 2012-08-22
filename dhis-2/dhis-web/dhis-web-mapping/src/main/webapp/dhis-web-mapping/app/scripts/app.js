GIS.conf = {
	finals: {
		layer: {
			layertype_base: 'base',
			layertype_vector: 'vector'
		},
		widget: {
			valuetype_indicator: 'indicator',
			valuetype_dataelement: 'dataelement',
			legendtype_automatic: 'automatic',
			legendtype_predefined: 'predefined'
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
			combo_width: 262,
			combolabel_width: 95
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

GIS.util = {
	google: {},	
	jsonEncodeString: function(str) {
		return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
	}
};

GIS.map = {};

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
	menu: {}
};

GIS.gui = {};

Ext.onReady( function() {
	Ext.Loader.setConfig({enabled: true, disableCaching: false});
	Ext.Loader.setPath('GeoExt', 'scripts/geoext/src/GeoExt');
	Ext.require([
		'GeoExt.panel.Map'
	]);
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	Ext.Ajax.request({
		url: GIS.conf.url.path_gis + 'initialize.action',
		success: function(r) {
			var init = Ext.decode(r.responseText);
			GIS.init.rootNodes = init.rootNodes;
	
	/* Init */
	
	GIS.init.onRender = function() {
		//GIS.layer.googleStreets.layer.setVisibility(false);
	};
	
	GIS.init.afterRender = function() {
		document.getElementsByClassName('olControlButtonItemActive')[0].innerHTML = '+';
	};
	
	/* Util */
	
	GIS.util.google.openTerms = function() {
		window.open('http://www.google.com/intl/en-US_US/help/terms_maps.html', '_blank');
	};
	
	/* Map */
	
	GIS.map = new OpenLayers.Map({
        controls: [
			new OpenLayers.Control.Navigation(),
			//new OpenLayers.Control.LayerSwitcher(),
			new OpenLayers.Control.MousePosition({
				prefix: '<span class="el-opacity-1"><span class="text-mouseposition-lonlat">LON </span>',
				separator: '<span class="text-mouseposition-lonlat">&nbsp;&nbsp;LAT </span>',
				suffix: '<div id="google-logo" onclick="javascript:GIS.util.google.openTerms();"></div></span>'
			}),
			new OpenLayers.Control.Permalink()
		],
        displayProjection: new OpenLayers.Projection('EPSG:4326'),
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508)
    });
    
    GIS.map.layerController = {};
    GIS.map.layerController.button = new OpenLayers.Control.Button({displayClass: 'olControlButton', trigger: function() {alert('clicky');}});
    GIS.map.layerController.panel = new OpenLayers.Control.Panel({defaultControl: GIS.map.layerController.button});
    GIS.map.layerController.panel.addControls([GIS.map.layerController.button]);    
    GIS.map.addControl(GIS.map.layerController.panel);
    
    /* Base layers */
    
    if (window.google) {
        GIS.layer.googleStreets.layer = new OpenLayers.Layer.Google(
            GIS.layer.googleStreets.name,
            {numZoomLevels: 20, animationEnabled: true}
        );        
        GIS.layer.googleStreets.layer.layerType = GIS.conf.finals.layer.layertype_base;
        GIS.map.addLayer(GIS.layer.googleStreets.layer);
        
        GIS.layer.googleHybrid.layer = new OpenLayers.Layer.Google(
            GIS.layer.googleHybrid.name,
            {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20, animationEnabled: true}
        );        
        GIS.layer.googleHybrid.layer.layerType = GIS.conf.finals.layer.layertype_base;
        GIS.map.addLayer(GIS.layer.googleHybrid.layer);
    }
    
    GIS.layer.openStreetMap.layer = new OpenLayers.Layer.OSM(GIS.layer.openStreetMap.name);
    GIS.layer.openStreetMap.layer.layerType = GIS.conf.finals.layer.layertype_base;
    GIS.map.addLayer(GIS.layer.openStreetMap.layer);
    
    /* Vector layers */
    
    GIS.layer.boundary.layer = new OpenLayers.Layer.Vector(GIS.i18n.boundary_layer, {
        strategies: [
			new OpenLayers.Strategy.Refresh({force:true})
		],
        visibility: false,
        displayInLayerSwitcher: false,
        styleMap: new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
					{
						fillOpacity: 0,
						strokeColor: '#000',
						strokeWidth: 1,
						pointRadius: 5
					},
					OpenLayers.Feature.Vector.style['default']
				)
            )
        }),
        layerType: GIS.conf.finals.layertype_vector
    });            
    GIS.map.addLayer(GIS.layer.boundary.layer);
    
    GIS.layer.thematic1.layer = new OpenLayers.Layer.Vector(GIS.i18n.thematic_layer_1, {
        strategies: [
			new OpenLayers.Strategy.Refresh({force:true})
		],
        visibility: false,
        displayInLayerSwitcher: false,
        styleMap: new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
					{
						fillOpacity: 0,
						strokeColor: '#000',
						strokeWidth: 1,
						pointRadius: 5
					},
					OpenLayers.Feature.Vector.style['default']
				)
            )
        }),
        layerType: GIS.conf.finals.layertype_vector
    });            
    GIS.map.addLayer(GIS.layer.thematic1.layer);
    
    /* Stores */
    
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
			}
		}
	});
	
	GIS.store.periodTypes = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		data: GIS.conf.period.periodTypes
	}),
    
    GIS.store.organisationUnitLevels = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'organisationUnitLevels.json?links=false&paging=false',
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
			}
		}
	});
    
    /* Objects */
    
    GIS.obj.LayerMenu = function(cmpRef, cls) {
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
						GIS.layer.thematic1.window.show();
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
					GIS.cmp.menu[cmpRef] = this;
				},
				afterrender: function() {
					this.getEl().addCls('gis-toolbar-btn-menu');					
					if (cls) {
						this.getEl().addCls('gis-toolbar-btn-menu-first');
					}
					
					this.itemsXableAlways();
				}
			}
		});
	};
    
	/* Graphical user interface */
	
	GIS.layer.thematic1.widget = Ext.create('mapfish.widgets.geostat.Thematic1', {
        map: GIS.map,
        layer: GIS.layer.thematic1.layer,
        featureSelection: false,
        legendDiv: 'thematic1Legend',
        defaults: {width: 130},
        listeners: {
            'expand': function() {
                //GIS.vars.activePanel.setPolygon();
            },
            'afterrender': function() {
                //this.layer.widget = this;
            }
        }
    });
    
    GIS.layer.thematic1.window = Ext.create('Ext.window.Window', {
		title: '<span id="window-thematic1-title">' + GIS.i18n.thematic_layer + ' 1' + '</span>',
		layout: 'fit',
		cls: 'gis-container-default',
        closeAction: 'hide',
        width: 570,
        items: GIS.layer.thematic1.widget,
        bbar: [
			'->',
			{
				text: '<<'
			},
			{
				text: 'Update'
			}
		]
	});
	
	GIS.gui.viewport = Ext.create('Ext.container.Viewport', {
		layout: 'border',		
		items: [
			{
				region: 'east',
				width: 200,
				items: [
                    {
                        title: 'Thematic layer 1 legend', //i18n
                        contentEl: 'thematic1Legend',
                        id: 'nissa',
                        html: 'East'
                    }
				]
			},
            {
                xtype: 'gx_mappanel',
                region: 'center',
                map: GIS.map,
                height: 31,
                tbar: {
					defaults: {
						height: 26
					},
					items: [
						{
							iconCls: 'gis-btn-icon-' + GIS.layer.boundary.name,
							menu: new GIS.obj.LayerMenu(GIS.layer.boundary.name, 'gis-menu-first'),
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
							text: 'Legends', //i18n
							menu: {}
						},
						{
							text: 'Download', //i18n
							menu: {}
						}							
					]
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
