var GIS = {
	map: {},
	layers: {},
	gui: {}
};

Ext.Loader.setConfig({enabled: true, disableCaching: false});
Ext.Loader.setPath('GeoExt', 'scripts/geoext/src/GeoExt');
Ext.require([
	'GeoExt.panel.Map'
]);

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	GIS.map = new OpenLayers.Map({
        controls: [
			//new OpenLayers.Control.Graticule(),
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
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
        layers: [
			new OpenLayers.Layer.OSM("OpenStreetMap")
		]
    });
    
    GIS.layers.boundary = new OpenLayers.Layer.Vector(G.i18n.boundary_layer, {
        strategies: [ new OpenLayers.Strategy.Refresh({force:true}) ],
        'visibility': false,
        'displayInLayerSwitcher': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 0, 'strokeColor': '#000', 'strokeWidth': 1, 'pointRadius': 5},
                    OpenLayers.Feature.Vector.style['default']
                )
            )
        })
    });
    
    GIS.layers.boundary.layerType = 'thematic';//i18n
    GIS.map.addLayer(GIS.layers.boundary);
    
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
							iconCls: 'gis-btn-icon-boundary',
							menu: Ext.create('Ext.menu.Menu', {
								shadow: false,
								showSeparator: false,
								items: [
									{
										text: 'Edit layer..',//i18n
										iconCls: 'gis-menu-item-icon'
									},
									'-',
									{
										text: 'Refresh',//i18n
										iconCls: 'gis-menu-item-icon',
										cls: 'gis-menu-item-afterseparator'
									},
									{
										text: 'Clear',//i18n
										iconCls: 'gis-menu-item-icon'
									},
									'-',
									{
										text: 'Labels..',//i18n
										iconCls: 'gis-menu-item-icon'
									},
									{
										text: 'Filter..',//i18n
										iconCls: 'gis-menu-item-icon'
									},
									{
										text: 'Search..',//i18n
										iconCls: 'gis-menu-item-icon'
									},
									'-',
									{
										text: 'Opacity',//i18n
										iconCls: 'gis-menu-item-icon',
										menu: Ext.create('Ext.menu.Menu', {
											shadow: false,
											showSeparator: false,
											items: []
										})
									},
									{
										text: 'History',//i18n
										iconCls: 'gis-menu-item-icon',
										menu: Ext.create('Ext.menu.Menu', {
											shadow: false,
											showSeparator: false,
											items: []
										})
									}
								],
								listeners: {
									afterrender: function() {
										this.getEl().addCls('gis-vertical-toolbar-btn-menu');
									}
								}
							})
						},
						
						{
							iconCls: 'gis-btn-icon-thematic1',
							listeners: {
								afterrender: function() {
									this.menu = Ext.create('Ext.menu.Menu', {
										items: [
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											}
										]
									});
								}
							}
						},
						
						{
							iconCls: 'gis-btn-icon-thematic2',
							listeners: {
								afterrender: function() {
									this.menu = Ext.create('Ext.menu.Menu', {
										items: [
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											}
										]
									});
								}
							}
						},
						
						{
							iconCls: 'gis-btn-icon-facility',
							listeners: {
								afterrender: function() {
									this.menu = Ext.create('Ext.menu.Menu', {
										items: [
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											}
										]
									});
								}
							}
						},
						
						{
							iconCls: 'gis-btn-icon-symbol',
							listeners: {
								afterrender: function() {
									this.menu = Ext.create('Ext.menu.Menu', {
										items: [
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											},
											{
												text: 'danslion'
											}
										]
									});
								}
							}
						}
					]
				}
            }
		]
	});	
});
