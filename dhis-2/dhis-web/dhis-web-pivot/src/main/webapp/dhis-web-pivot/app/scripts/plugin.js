PT.plugin = {};

Ext.onReady(function() {
	Ext.Ajax.method = 'GET';

	document.body.oncontextmenu = function() {
		return false;
	};
	
	css = 'table.pivot { \n font-family: arial,sans-serif,ubuntu,consolas; \n } \n';
	css += '.td-nobreak { \n white-space: nowrap; \n } \n';
	css += '.td-hidden { \n display: none; \n } \n';
	css += '.td-collapsed { \n display: none; \n } \n';
	css += 'table.pivot { \n border-collapse: collapse; \n border-spacing: 0px; \n border: 0 none; \n } \n';
	css += '.pivot td { \n padding: 5px; \n border: \n 1px solid #b2b2b2; \n } \n';
	css += '.pivot-dim { \n background-color: #dae6f8; \n text-align: center; \n } \n';
	css += '.pivot-dim.highlighted { \n	background-color: #c5d8f6; \n } \n';
	css += '.pivot-dim-subtotal { \n background-color: #cad6e8; \n text-align: center; \n } \n';
	css += '.pivot-dim-total { \n background-color: #bac6d8; \n text-align: center; \n } \n';
	css += '.pivot-dim-empty { \n background-color: #dae6f8; \n text-align: center; \n } \n';
	css += '.pivot-value { \n background-color: #fff; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-subtotal { \n background-color: #f4f4f4; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-subtotal-total { \n background-color: #e7e7e7; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-total { \n background-color: #e4e4e4; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-total-subgrandtotal { \n background-color: #d8d8d8; \n white-space: nowrap; \n text-align: right; \n } \n';
	css += '.pivot-value-grandtotal { \n background-color: #c8c8c8; \n white-space: nowrap; \n text-align: right; \n } \n';
	
	Ext.util.CSS.createStyleSheet(css);
	
	
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
				bodyStyle: 'border: 0 none; padding: 1px',
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
