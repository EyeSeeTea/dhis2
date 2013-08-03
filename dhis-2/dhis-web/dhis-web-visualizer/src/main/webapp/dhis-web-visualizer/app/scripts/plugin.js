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
			
		afterRender = function() {
			
		};
			
		createViewport = function() {
			var setFavorite,
				centerRegion,
				el = Ext.get(dv.el),
				width,
				height,
				viewportConfig;
				
			width = el.getWidth() - parseInt(el.getStyle('border-left-width')) - parseInt(el.getStyle('border-right-width'));
			height = el.getHeight() - parseInt(el.getStyle('border-top-width')) - parseInt(el.getStyle('border-bottom-width'));				
				
			setFavorite = function(layout) {
				dv.util.chart.createChart(layout, dv);
			};
			
			centerRegion = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				bodyStyle: 'border: 0 none',
				width: config.width || width,
				height: config.height || height,
				layout: 'fit',
				listeners: {
					afterrender: function() {
						afterRender();
					}
				}
			});
			
			return {
				setFavorite: setFavorite,
				centerRegion: centerRegion
			};
		};
		
		initialize = function() {
			
			dv = DV.core.getInstance({
				baseUrl: config.url,
				el: config.el
			});

			Ext.data.JsonP.request({
				url: dv.baseUrl + '/dhis-web-visualizer/initialize.action',
				success: function(r) {
					dv.init = r;
			
					dv.viewport = createViewport();
					
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
