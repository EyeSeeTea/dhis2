DV.plugin = {};
DV.plugin.init = {};

Ext.onReady(function() {
	Ext.Ajax.method = 'GET';

	document.body.oncontextmenu = function() {
		return false;
	};
	
	css += '.x-mask-msg { \n padding: 0; \n	border: 0 none; \n background-image: none; \n background-color: transparent; \n } \n';
	css += '.x-mask-msg div { \n background-position: 11px center; \n } \n';
	css += '.x-mask-msg .x-mask-loading { \n border: 0 none; \n	background-color: #000; \n color: #fff; \n border-radius: 2px; \n padding: 12px 14px 12px 30px; \n opacity: 0.65; \n } \n';	
	
	Ext.util.CSS.createStyleSheet(css);
	
	DV.plugin.getChart = function(config) {
		var validateConfig,
			afterRender,
			createViewport,
			dv,
			initialize;
			
		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Chart configuration is not an object');
				return;
			}
			
			if (!Ext.isString(config.el)) {
				console.log('No element id provided');
				return;
			}
			
			if (!Ext.isString(config.uid)) {
				console.log('No chart uid provided');
				return;
			}
			
			return true;
		};
			
		afterRender = function() {};
			
		createViewport = function() {
			var el = Ext.get(dv.el),
				setFavorite,
				centerRegion,
				width,
				height;
				
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
			if (!validateConfig(config)) {
				return;
			}
			
			dv = DV.core.getInstance({
				baseUrl: config.url,
				el: config.el
			});
			
			dv.viewport = createViewport();

			Ext.data.JsonP.request({
				url: dv.baseUrl + '/dhis-web-visualizer/initialize.action',
				success: function(r) {
					dv.init = r;
					
					dv.util.chart.loadChart(config.uid, true);	
				}
			});
		}();
	};
});
