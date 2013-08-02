DV.plugin = {};
DV.plugin.init = {};

Ext.onReady(function() {
	Ext.Ajax.method = 'GET';

	document.body.oncontextmenu = function() {
		return false;
	};
	
	DV.plugin.getChart = function(config) {
		var afterRender,
			createViewport,
			dv,
			initialize;
			
		createViewport = function() {
			var viewport,
				centerRegion,
				el = Ext.get(dv.el),
				viewportConfig;
				
			//viewportConfing = {
				//renderTo: el
				
			viewport = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				width: el.getWidth(),
				height: el.getHeight(),
				layout: 'fit',
				setFavorite: function(layout) {
					dv.util.chart.createChart(layout, dv);
				},
				listeners: {
					afterrender: function() {
						afterRender();
					}
				}
			});
			
			return viewport;
		};
		
		initialize = function() {
			
			dv = DV.core.getInstance({
				baseUrl: config.url,
				el: config.url
			});
			
			dv.viewport = createViewport();

			Ext.data.JsonP.request({
				url: dv.baseUrl + '/dhis-web-visualizer/initialize.action',
				success: function(r) {
					dv.init = r;
					
					if (Ext.isString(config.uid)) {
						dv.util.chart.loadChart(config.uid, true);
						return;
					}
					
					console.log("nothing to do");					
				}
			});
		}();
	};
});
