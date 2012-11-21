Ext.onReady( function() {

	/*
	CONFIG              			TYPE            DEFAULT             DESCRIPTION

	uid                 			string                              (Optional) A chart uid. If provided, only 'el' and 'url' are required.

	indicator          				string								(Required*) Indicator uid. *Required if no data element is provided.
	dataelement	        			string								(Required*) Data element uid. *Required if no indicator is provided.
	period							string								(Required) Fixed period ISO.
	organisationunit				string								(Required) Organisation unit uid.
	series              			string          'data'              (Optional) Series: 'data', 'period' or 'organisationunit'.
	category            			string          'period'            (Optional) Category: 'indicator', 'dataelement', 'period' or 'organisationunit'.
	filter              			string          'organisationunit'  (Optional) Filter: 'indicator', 'dataelement', 'period' or 'organisationunit'.
	orgUnitIsParent					boolean			false				(Optional) If true, the children of the provided orgunit are displayed.
	showData						boolean			false				(Optional) If true, the exact data are displayed on the chart.
	trendLine						boolean			false				(Optional) If true, trend line(s) are added.
	hideLegend						boolean			false				(Optional) If true, the legend is not visible.
	hideSubtitle					boolean			false				(Optional) If true, the subtitle is not visible.
	userOrganisationUnit			boolean			false				(Optional) If true, the provided orgunits are replaced by the user orgunit.
	userOrganisationUnitChildren	boolean			false				(Optional) If true, the provided orgunits are replaced by the user orgunit children.
	targetLineValue					integer								(Optional) The value of the target line.
	targetLineLabel					string								(Optional) The legend name of the target line. Requires targetLineValue, otherwise ignored.
	baseLineValue					integer								(Optional) The value of the base line.
	baseLineLabel					string								(Optional) The legend name of the base line. Requires baseLineValue, otherwise ignored.
	domainAxisLabel					string								(Optional) Domain axis (usually the x axis) label.
	rangeAxisLabel					string								(Optional) Range axis (usually the y axis) label.
	legendPosition      			string          'top'               (Optional) Positions: 'top', 'right', 'bottom' or 'left'.
	el                  			string                              (Required) The element id to render the chart.
	width               			integer         <el width>          (Optional) Chart width. Default is the element width.
	height              			integer         <el height>         (Optional) Chart height. Default is the element height.
	url                 			string                              (Required) The base url of the DHIS instance.
	*/

	GIS.getMap = function(config) {
		var getView;

		getView = function(config) {
			var indicator = GIS.conf.finals.dimension.indicator.id,
				dataElement = GIS.conf.finals.dimension.dataElement.id,
				automatic = GIS.conf.finals.widget.legendtype_automatic,
				predefined = GIS.conf.finals.widget.legendtype_predefined,
				view = {
					layer: config.layer || 'thematic1',
					valueType: config.indicator ? indicator : dataElement,
					indicator: {
						id: config.indicator
					},
					dataElement: {
						id: config.dataelement
					},
					period: {
						id: config.period
					},
					legendType: config.legendSet ? predefined : automatic,
					legendSet: config.legendSet,
					classes: parseInt(config.classes) || 5,
					method: parseInt(config.method) || 2,
					colorLow: config.colorLow || 'ff0000',
					colorHigh: config.colorHigh || '00ff00',
					radiusLow: parseInt(config.radiusLow) || 5,
					radiusHigh: parseInt(config.radiusHigh) || 15,
					organisationUnitLevel: {
						level: parseInt(config.level) || 2
					},
					parentOrganisationUnit: {
						id: config.parent
					},
					opacity: parseFloat(config.opacity) || 0.8
				};

			return view;
		};
	};


