PT.app = {};
PT.app.init = {};

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
	Ext.QuickTips.init();

    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});

	// Init

	var pt = PT.core.getInstance();

	GIS.app.init.onInitialize = function(r) {
		var createViewport;

		createViewport = function() {
			var westRegion,
				centerRegion,

				indicator,
				dataElement,
				dataSet,
				relativePeriod,
				fixedPeriod,
				organisationUnit

				getOrganisationUnitGroupSetItems;

			indicator = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Indicators</div>',
				hideCollapseTool: true,
				items: [
					{
						xtype: 'combobox',
						cls: 'pt-combo',
						style: 'margin-bottom:8px',
						width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_subtractor,





			westRegion = Ext.create('Ext.panel.Panel', {
				region: 'west',
				preventHeader: true,
				collapsible: true,
				collapseMode: 'mini',
				items: [

