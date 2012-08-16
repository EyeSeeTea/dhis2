var GIS = {
	conf: {
		finals: {
			layertype_base: 'base',
			layertype_vector: 'vector'
		}
	},
	init: {},
	map: {},
	layers: {
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
	},
	obj: {},
	cmp: {
		menu: {}
	},
	gui: {}
};

Ext.onReady( function() {
	Ext.Loader.setConfig({enabled: true, disableCaching: false});
	Ext.Loader.setPath('GeoExt', 'scripts/geoext/src/GeoExt');
	Ext.require([
		'GeoExt.panel.Map'
	]);
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	/* Init */
	
	GIS.init.onRender = function() {
		GIS.layers.openStreetMap.layer.setVisibility(false);
	};
	
	/* Map */
	
	GIS.map = new OpenLayers.Map({
        controls: [
			new OpenLayers.Control.Navigation(),
			new OpenLayers.Control.LayerSwitcher(),
			new OpenLayers.Control.MousePosition({
				prefix: '<span class="el-opacity-1"><span class="text-mouseposition-lonlat">LON </span>',
				separator: '<span class="text-mouseposition-lonlat">&nbsp;&nbsp;LAT </span>',
				suffix: '</span>'
			}),
			new OpenLayers.Control.Permalink()
		],
        displayProjection: new OpenLayers.Projection('EPSG:4326'),
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508)
    });
    
    /* Base layers */
    
    if (window.google) {
        GIS.layers.googleStreets.layer = new OpenLayers.Layer.Google(
            GIS.layers.googleStreets.name,
            {numZoomLevels: 20, animationEnabled: true}
        );        
        GIS.layers.googleStreets.layer.layerType = GIS.conf.finals.layertype_base;
        GIS.map.addLayer(GIS.layers.googleStreets.layer);
        
        GIS.layers.googleHybrid.layer = new OpenLayers.Layer.Google(
            GIS.layers.googleHybrid.name,
            {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20, animationEnabled: true}
        );        
        GIS.layers.googleHybrid.layer.layerType = GIS.conf.finals.layertype_base;
        GIS.map.addLayer(GIS.layers.googleHybrid.layer);
    }
    
    GIS.layers.openStreetMap.layer = new OpenLayers.Layer.OSM('OpenStreetMap');
    GIS.layers.openStreetMap.layer.layerType = GIS.conf.finals.layertype_base;
    GIS.map.addLayer(GIS.layers.openStreetMap.layer);
    
    /* Vector layers */
    
    GIS.layers.boundary.layer = new OpenLayers.Layer.Vector(G.i18n.boundary_layer, {
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
            
    GIS.map.addLayer(GIS.layers.boundary.layer);
    
    /* Objects */
    
    GIS.obj.LayerMenu = function(cmpRef) {
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
					alwaysEnabled: true
				},
				{
					xtype: 'menuseparator',
					alwaysEnabled: true
				},
				{
					text: 'Refresh',//i18n
					iconCls: 'gis-menu-item-icon-refresh',
					cls: 'gis-menu-item-afterseparator'
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
					this.getEl().addCls('gis-vertical-toolbar-btn-menu');
					
					this.itemsXableAlways();
				}
			}
		});
	};
    
	/* Graphical user interface */
	GIS.gui.viewport = Ext.create('Ext.container.Viewport', {
		layout: 'border',		
		items: [
			{
				region: 'east',
				items: {
					html: 'east'
				}
			},
            {
                xtype: 'gx_mappanel',
                region: 'center',
                height: 1000,
                width: 800,
                map: GIS.map,
                zoom: 3,
                lbar: {
					defaults: {
						height: 30,
						width: 30,
						menuAlign: 'tr'
					},
					items: [
						{
							iconCls: 'gis-btn-icon-' + GIS.layers.boundary.name,
							menu: new GIS.obj.LayerMenu(GIS.layers.boundary.name)
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layers.thematic1.name,
							menu: new GIS.obj.LayerMenu(GIS.layers.thematic1.name)
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layers.thematic2.name,
							menu: new GIS.obj.LayerMenu(GIS.layers.thematic2.name)
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layers.facility.name,
							menu: new GIS.obj.LayerMenu(GIS.layers.facility.name)
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.layers.symbol.name,
							menu: new GIS.obj.LayerMenu(GIS.layers.symbol.name)
						}
					]
				}
            }
		],
		listeners: {
			render: GIS.init.onRender
		}
	});	
});
