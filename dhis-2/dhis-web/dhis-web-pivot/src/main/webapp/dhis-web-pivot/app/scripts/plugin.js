PT.plugin = {};
PT.plugin.init = {};

Ext.onReady(function() {
	Ext.Ajax.method = 'GET';

	document.body.oncontextmenu = function() {
		return false;
	};
	
	PT.plugin.getTable = function(config) {
		var validateConfig,
			afterRender,
			createViewport,
			pt,
			initialize;
			
		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Invalid report table configuration');
				return;
			}
			
			if (!Ext.isString(config.el)) {
				console.log('No element provided');
				return;
			}
			
			if (!Ext.isString(config.uid)) {
				console.log('No report table uid provided');
				return;
			}
			
			return true;
		};
			
		afterRender = function() {};
			
		createViewport = function() {
			var el = Ext.get(pt.el),
				setFavorite,
				centerRegion,
				width,
				height;
				
			width = el.getWidth() - parseInt(el.getStyle('border-left-width')) - parseInt(el.getStyle('border-right-width'));
			height = el.getHeight() - parseInt(el.getStyle('border-top-width')) - parseInt(el.getStyle('border-bottom-width'));				
				
			setFavorite = function(layout) {
				pt.util.pivot.createTable(layout, pt);
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
			
			pt = PT.core.getInstance({
				baseUrl: config.url,
				el: config.el
			});
			
			pt.viewport = createViewport();

			Ext.data.JsonP.request({
				url: pt.baseUrl + '/dhis-web-pivot/initialize.action',
				success: function(r) {
					pt.init = r;
					
					pt.util.pivot.loadTable(config.uid, true);	
				}
			});
		}();
	};
});
