var GIS = {
	map: {},
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
			new OpenLayers.Control.Navigation()
		],
        displayProjection: new OpenLayers.Projection('EPSG:4326'),
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
        layers: [
			new OpenLayers.Layer.OSM("OpenStreetMap")
		]
    });
    
	/* Graphical user interface */
	GIS.gui.viewport = Ext.create('Ext.container.Viewport', {
		layout: 'border',		
		items: [
			{
				region: 'east',
				bodyStyle: 'background: yellow',
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
                zoom: 3
            }
		]
	});	
});
