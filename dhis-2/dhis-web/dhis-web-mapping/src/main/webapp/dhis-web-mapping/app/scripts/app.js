var GIS = {};

GIS.gui = {};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'scripts/ext-ux/');
Ext.require('Ext.ux.ColorField');

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
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
				region: 'center',
				bodyStyle: 'background: gray',
				items: {
					xtype: 'colorfield',
					allowBlank: false,
					width: 100,
					value: '#FF0000'
				}					
			}
		]
	});	
});
