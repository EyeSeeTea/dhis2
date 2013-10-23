Ext.onReady( function() {

	// ext config
	Ext.Ajax.method = 'GET';

	// namespace
	PT = {};
	var NS = PT;

	NS.instances = [];
	NS.i18n = {};
	NS.isDebug = false;
	NS.isSessionStorage = ('sessionStorage' in window && window['sessionStorage'] !== null);

	NS.getCore = function(init) {
        var conf = {},
            api = {},
            support = {},
            service = {},
            web = {},
            dimConf;

		// conf
		(function() {
			conf.finals = {
				url: {
					path_module: '/dhis-web-pivot/',
					organisationunitchildren_get: 'getOrganisationUnitChildren.action'
				},
				dimension: {
					data: {
						value: 'data',
						name: NS.i18n.data,
						dimensionName: 'dx',
						objectName: 'dx',
						warning: {
							filter: '...'//NS.i18n.wm_multiple_filter_ind_de
						}
					},
					category: {
						name: NS.i18n.categories,
						dimensionName: 'co',
						objectName: 'co',
					},
					indicator: {
						value: 'indicators',
						name: NS.i18n.indicators,
						dimensionName: 'dx',
						objectName: 'in'
					},
					dataElement: {
						value: 'dataElements',
						name: NS.i18n.data_elements,
						dimensionName: 'dx',
						objectName: 'de'
					},
					operand: {
						value: 'operand',
						name: 'Operand',
						dimensionName: 'dx',
						objectName: 'dc'
					},
					dataSet: {
						value: 'dataSets',
						name: NS.i18n.data_sets,
						dimensionName: 'dx',
						objectName: 'ds'
					},
					period: {
						value: 'period',
						name: NS.i18n.periods,
						dimensionName: 'pe',
						objectName: 'pe'
					},
					fixedPeriod: {
						value: 'periods'
					},
					relativePeriod: {
						value: 'relativePeriods'
					},
					organisationUnit: {
						value: 'organisationUnits',
						name: NS.i18n.organisation_units,
						dimensionName: 'ou',
						objectName: 'ou'
					},
					dimension: {
						value: 'dimension'
						//objectName: 'di'
					},
					value: {
						value: 'value'
					}
				},
				root: {
					id: 'root'
				}
			};

			dimConf = conf.finals.dimension;

			dimConf.objectNameMap = {};
			dimConf.objectNameMap[dimConf.data.objectName] = dimConf.data;
			dimConf.objectNameMap[dimConf.indicator.objectName] = dimConf.indicator;
			dimConf.objectNameMap[dimConf.dataElement.objectName] = dimConf.dataElement;
			dimConf.objectNameMap[dimConf.operand.objectName] = dimConf.operand;
			dimConf.objectNameMap[dimConf.dataSet.objectName] = dimConf.dataSet;
			dimConf.objectNameMap[dimConf.category.objectName] = dimConf.category;
			dimConf.objectNameMap[dimConf.period.objectName] = dimConf.period;
			dimConf.objectNameMap[dimConf.organisationUnit.objectName] = dimConf.organisationUnit;
			dimConf.objectNameMap[dimConf.dimension.objectName] = dimConf.dimension;

			conf.period = {
				periodTypes: [
					{id: 'Daily', name: 'Daily'},
					{id: 'Weekly', name: 'Weekly'},
					{id: 'Monthly', name: 'Monthly'},
					{id: 'BiMonthly', name: 'BiMonthly'},
					{id: 'Quarterly', name: 'Quarterly'},
					{id: 'SixMonthly', name: 'SixMonthly'},
					{id: 'Yearly', name: 'Yearly'},
					{id: 'FinancialOct', name: 'FinancialOct'},
					{id: 'FinancialJuly', name: 'FinancialJuly'},
					{id: 'FinancialApril', name: 'FinancialApril'}
				]
			};

			conf.layout = {
				west_width: 424,
				west_fieldset_width: 416,
				west_width_padding: 4,
				west_fill: 2,
				west_fill_accordion_indicator: 59,
				west_fill_accordion_dataelement: 59,
				west_fill_accordion_dataset: 33,
				west_fill_accordion_period: 296,
				west_fill_accordion_organisationunit: 62,
				west_maxheight_accordion_indicator: 400,
				west_maxheight_accordion_dataelement: 400,
				west_maxheight_accordion_dataset: 400,
				west_maxheight_accordion_period: 513,
				west_maxheight_accordion_organisationunit: 900,
				west_maxheight_accordion_group: 340,
				west_maxheight_accordion_options: 449,
				west_scrollbarheight_accordion_indicator: 300,
				west_scrollbarheight_accordion_dataelement: 300,
				west_scrollbarheight_accordion_dataset: 300,
				west_scrollbarheight_accordion_period: 450,
				west_scrollbarheight_accordion_organisationunit: 450,
				west_scrollbarheight_accordion_group: 300,
				east_tbar_height: 31,
				east_gridcolumn_height: 30,
				form_label_width: 55,
				window_favorite_ypos: 100,
				window_confirm_width: 250,
				window_share_width: 500,
				grid_favorite_width: 420,
				grid_row_height: 27,
				treepanel_minheight: 135,
				treepanel_maxheight: 400,
				treepanel_fill_default: 310,
				treepanel_toolbar_menu_width_group: 140,
				treepanel_toolbar_menu_width_level: 120,
				multiselect_minheight: 100,
				multiselect_maxheight: 250,
				multiselect_fill_default: 345,
				multiselect_fill_reportingrates: 315
			};

			conf.pivot = {
				digitGroupSeparator: {
					'comma': ',',
					'space': ' '
				},
				displayDensity: {
					'compact': '3px',
					'normal': '5px',
					'comfortable': '10px',
				},
				fontSize: {
					'small': '10px',
					'normal': '11px',
					'large': '13px'
				}
			};
		}());

		// api
		(function() {
			api.layout = {};

			api.layout.Record = function(config) {
				var config = Ext.clone(config);

				// id: string

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Record: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.id)) {
						alert('Record: id is not text: ' + config);
						return;
					}

					config.id = config.id.replace('.', '-');

					return config;
				}();
			};

			api.layout.Dimension = function(config) {
				var config = Ext.clone(config);

				// dimension: string

				// items: [Record]

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Dimension: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.dimension)) {
						console.log('Dimension: name is not a string: ' + config);
						return;
					}

					if (config.dimension !== conf.finals.dimension.category.objectName) {
						var records = [];

						if (!Ext.isArray(config.items)) {
							console.log('Dimension: items is not an array: ' + config);
							return;
						}

						for (var i = 0; i < config.items.length; i++) {
							records.push(api.layout.Record(config.items[i]));
						}

						config.items = Ext.Array.clean(records);

						if (!config.items.length) {
							console.log('Dimension: has no valid items: ' + config);
							return;
						}
					}

					return config;
				}();
			};

			api.layout.Layout = function(config) {
				var config = Ext.clone(config),
					layout = {},
					getValidatedDimensionArray,
					validateSpecialCases;

				// columns: [Dimension]

				// rows: [Dimension]

				// filters: [Dimension]

				// showTotals: boolean (true)

				// showSubTotals: boolean (true)

				// hideEmptyRows: boolean (false)

				// showHierarchy: boolean (false)

				// displayDensity: string ('normal') - 'compact', 'normal', 'comfortable'

				// fontSize: string ('normal') - 'small', 'normal', 'large'

				// digitGroupSeparator: string ('space') - 'none', 'comma', 'space'

				// legendSet: object

				// parentGraphMap: object

				// reportingPeriod: boolean (false) //report tables only

				// organisationUnit: boolean (false) //report tables only

				// parentOrganisationUnit: boolean (false) //report tables only

				// regression: boolean (false)

				// cumulative: boolean (false)

				// sortOrder: integer (0) //-1, 0, 1

				// topLimit: integer (100) //5, 10, 20, 50, 100

				getValidatedDimensionArray = function(dimensionArray) {
					var dimensionArray = Ext.clone(dimensionArray);

					if (!(dimensionArray && Ext.isArray(dimensionArray) && dimensionArray.length)) {
						return;
					}

					for (var i = 0; i < dimensionArray.length; i++) {
						dimensionArray[i] = api.layout.Dimension(dimensionArray[i]);
					}

					dimensionArray = Ext.Array.clean(dimensionArray);

					return dimensionArray.length ? dimensionArray : null;
				};

				validateSpecialCases = function() {
					var dimConf = conf.finals.dimension,
						dimensions,
						objectNameDimensionMap = {};

					if (!layout) {
						return;
					}

					dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

					for (var i = 0; i < dimensions.length; i++) {
						objectNameDimensionMap[dimensions[i].dimension] = dimensions[i];
					}

					if (layout.filters && layout.filters.length) {
						for (var i = 0; i < layout.filters.length; i++) {

							// Indicators as filter
							if (layout.filters[i].dimension === dimConf.indicator.objectName) {
								util.message.alert(NS.i18n.indicators_cannot_be_specified_as_filter || 'Indicators cannot be specified as filter');
								return;
							}

							// Categories as filter
							if (layout.filters[i].dimension === dimConf.category.objectName) {
								util.message.alert(NS.i18n.categories_cannot_be_specified_as_filter || 'Categories cannot be specified as filter');
								return;
							}

							// Data sets as filter
							if (layout.filters[i].dimension === dimConf.dataSet.objectName) {
								util.message.alert(NS.i18n.data_sets_cannot_be_specified_as_filter || 'Data sets cannot be specified as filter');
								return;
							}
						}
					}

					// dc and in
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.indicator.objectName]) {
						util.message.alert('Indicators and detailed data elements cannot be specified together');
						return;
					}

					// dc and de
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataElement.objectName]) {
						util.message.alert('Detailed data elements and totals cannot be specified together');
						return;
					}

					// dc and ds
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataSet.objectName]) {
						util.message.alert('Data sets and detailed data elements cannot be specified together');
						return;
					}

					// dc and co
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.category.objectName]) {
						util.message.alert('Categories and detailed data elements cannot be specified together');
						return;
					}

					return true;
				};

				return function() {
					var objectNames = [],
						dimConf = conf.finals.dimension,
						dims;

					config.columns = getValidatedDimensionArray(config.columns);
					config.rows = getValidatedDimensionArray(config.rows);
					config.filters = getValidatedDimensionArray(config.filters);

					// Config must be an object
					if (!(config && Ext.isObject(config))) {
						alert('Layout: config is not an object (' + init.el + ')');
						return;
					}

					// At least one dimension specified as column or row
					if (!(config.columns || config.rows)) {
						alert(NS.i18n.at_least_one_dimension_must_be_specified_as_row_or_column);
						return;
					}

					// Get object names
					for (var i = 0, dims = Ext.Array.clean([].concat(config.columns || [], config.rows || [], config.filters || [])); i < dims.length; i++) {

						// Object names
						if (api.layout.Dimension(dims[i])) {
							objectNames.push(dims[i].dimension);
						}
					}

					// At least one period
					if (!Ext.Array.contains(objectNames, dimConf.period.objectName)) {
						alert(NS.i18n.at_least_one_period_must_be_specified_as_column_row_or_filter);
						return;
					}

					// Layout
					layout.columns = config.columns;
					layout.rows = config.rows;
					layout.filters = config.filters;

					// Properties
					layout.showTotals = Ext.isBoolean(config.totals) ? config.totals : (Ext.isBoolean(config.showTotals) ? config.showTotals : true);
					layout.showSubTotals = Ext.isBoolean(config.subtotals) ? config.subtotals : (Ext.isBoolean(config.showSubTotals) ? config.showSubTotals : true);
					layout.hideEmptyRows = Ext.isBoolean(config.hideEmptyRows) ? config.hideEmptyRows : false;

					layout.showHierarchy = Ext.isBoolean(config.showHierarchy) ? config.showHierarchy : false;

					layout.displayDensity = Ext.isString(config.displayDensity) && !Ext.isEmpty(config.displayDensity) ? config.displayDensity : 'normal';
					layout.fontSize = Ext.isString(config.fontSize) && !Ext.isEmpty(config.fontSize) ? config.fontSize : 'normal';
					layout.digitGroupSeparator = Ext.isString(config.digitGroupSeparator) && !Ext.isEmpty(config.digitGroupSeparator) ? config.digitGroupSeparator : 'space';
					layout.legendSet = Ext.isObject(config.legendSet) && Ext.isString(config.legendSet.id) ? config.legendSet : null;

					layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;

					layout.reportingPeriod = Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramReportingPeriod) ? config.reportParams.paramReportingPeriod : (Ext.isBoolean(config.reportingPeriod) ? config.reportingPeriod : false);
					layout.organisationUnit =  Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramOrganisationUnit) ? config.reportParams.paramOrganisationUnit : (Ext.isBoolean(config.organisationUnit) ? config.organisationUnit : false);
					layout.parentOrganisationUnit =  Ext.isObject(config.reportParams) && Ext.isBoolean(config.reportParams.paramParentOrganisationUnit) ? config.reportParams.paramParentOrganisationUnit : (Ext.isBoolean(config.parentOrganisationUnit) ? config.parentOrganisationUnit : false);

					layout.regression = Ext.isBoolean(config.regression) ? config.regression : false;
					layout.cumulative = Ext.isBoolean(config.cumulative) ? config.cumulative : false;
					layout.sortOrder = Ext.isNumber(config.sortOrder) ? config.sortOrder : 0;
					layout.topLimit = Ext.isNumber(config.topLimit) ? config.topLimit : 0;

					if (!validateSpecialCases()) {
						return;
					}

					return layout;
				}();
			};

			api.response = {};

			api.response.Header = function(config) {
				var config = Ext.clone(config);

				// name: string

				// meta: boolean

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Header: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.name)) {
						console.log('Header: name is not a string: ' + config);
						return;
					}

					if (!Ext.isBoolean(config.meta)) {
						console.log('Header: meta is not boolean: ' + config);
						return;
					}

					return config;
				}();
			};

			api.response.Response = function(config) {
				var config = Ext.clone(config);

				// headers: [Header]

				return function() {
					if (!(config && Ext.isObject(config))) {
						console.log('Response: config is not an object');
						return;
					}

					if (!(config.headers && Ext.isArray(config.headers))) {
						console.log('Response: headers is not an array');
						return;
					}

					for (var i = 0, header; i < config.headers.length; i++) {
						config.headers[i] = api.response.Header(config.headers[i]);
					}

					config.headers = Ext.Array.clean(config.headers);

					if (!config.headers.length) {
						console.log('Response: no valid headers');
						return;
					}

					if (!(Ext.isArray(config.rows) && config.rows.length > 0)) {
						alert('No values found');
						return;
					}

					if (config.headers.length !== config.rows[0].length) {
						console.log('Response: headers.length !== rows[0].length');
					}

					return config;
				}();
			};
		}());

		// support
		(function() {

			// prototype
			support.prototype = {};

				// array
			support.prototype.array = {};

			support.prototype.array.getLength = function(array, suppressWarning) {
				if (!Ext.isArray(array)) {
					if (!suppressWarning) {
						console.log('support.prototype.array.getLength: not an array');
					}

					return null;
				}

				return array.length;
			};

			support.prototype.array.sortObjectsByObjectKey = function(array, key) {
				if (!support.prototype.array.getLength(array)) {
					return null;
				}

				key = key || 'name';

				array.sort( function(a, b) {
					var nameA = a[key].toLowerCase(),
						nameB = b[key].toLowerCase();

					if (nameA < nameB) {
						return -1;
					}
					if (nameA > nameB) {
						return 1;
					}
					return 0;
				});

				return array;
			};

				// object
			support.prototype.object = {};

			support.prototype.object.getLength = function(object, suppressWarning) {
				if (!Ext.isObject(object)) {
					if (!suppressWarning) {
						console.log('support.prototype.object.getLength: not an object');
					}

					return null;
				}

				var size = 0;

				for (var key in object) {
					if (object.hasOwnProperty(key)) {
						size++;
					}
				}

				return size;
			};

			support.prototype.object.hasObject = function(object, property, value) {
				if (!support.prototype.object.getLength(object)) {
					return null;
				}

				for (var key in object) {
					var record = object[key];

					if (object.hasOwnProperty(key) && record[property] === value) {
						return true;
					}
				}

				return null;
			};

				// str
			support.prototype.str = {};

			support.prototype.str.replaceAll = function(str, find, replace) {
				return str.replace(new RegExp(find, 'g'), replace);
			};

				// number
			support.prototype.number = {};

			support.prototype.number.getNumberOfDecimals = function(number) {
				var str = new String(number);
				return (str.indexOf('.') > -1) ? (str.length - str.indexOf('.') - 1) : 0;
			};

			support.prototype.number.roundIf = function(number, precision) {
				number = parseFloat(number);
				precision = parseFloat(precision);

				if (Ext.isNumber(number) && Ext.isNumber(precision)) {
					var numberOfDecimals = support.prototype.number.getNumberOfDecimals(number);
					return numberOfDecimals > precision ? Ext.Number.toFixed(number, precision) : number;
				}

				return number;
			};

			support.prototype.number.prettyPrint = function(number, separator) {
				separator = separator || 'space';

				if (separator === 'none') {
					return number;
				}

				return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, conf.pivot.digitGroupSeparator[separator]);
			};

			// color
			support.color = {};

			support.color.hexToRgb = function(hex) {
				var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i,
					result;

				hex = hex.replace(shorthandRegex, function(m, r, g, b) {
					return r + r + g + g + b + b;
				});

				result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);

				return result ? {
					r: parseInt(result[1], 16),
					g: parseInt(result[2], 16),
					b: parseInt(result[3], 16)
				} : null;
			};

			// message
			support.message = {};

			support.message.alert = function(message) {
				alert(message);
			};
		}());

		// service
		(function() {

			// layout
			service.layout = {};

			service.layout.cleanDimensionArray = function(dimensionArray) {
				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				var array = [];

				for (var i = 0; i < dimensionArray.length; i++) {
					array.push(api.layout.Dimension(dimensionArray[i]));
				}

				array = Ext.Array.clean(array);

				return array.length ? array : null;
			};

			service.layout.sortDimensionArray = function(dimensionArray, key) {
				if (!support.prototype.array.getLength(dimensionArray, true)) {
					return null;
				}

				// Clean dimension array
				dimensionArray = service.layout.cleanDimensionArray(dimensionArray);

				if (!dimensionArray) {
					console.log('service.layout.sortDimensionArray: no valid dimensions');
					return null;
				}

				key = key || 'dimensionName';

				// Dimension order
				Ext.Array.sort(dimensionArray, function(a,b) {
					if (a[key] < b[key]) {
						return -1;
					}
					if (a[key] > b[key]) {
						return 1;
					}
					return 0;
				});

				// Sort object items, ids
				for (var i = 0, items; i < dimensionArray.length; i++) {
					dimensionArray[i].items = support.prototype.array.sortObjectsByObjectKey(dimensionArray[i].items, 'id');

					if (support.prototype.array.getLength(dimensionArray[i].ids)) {
						dimensionArray[i].ids.sort();
					}
				}

				return dimensionArray;
			};

			service.layout.getObjectNameDimensionMapFromDimensionArray = function(dimensionArray) {
				var map = {};

				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				for (var i = 0, dimension; i < dimensionArray.length; i++) {
					dimension = api.layout.Dimension(dimensionArray[i]);

					if (dimension) {
						map[dimension.dimension] = dimension;
					}
				}

				support.prototype.object.getLength(map) ? map : null;
			};

			service.layout.getObjectNameDimensionItemsMapFromDimensionArray = function(dimensionArray) {
				var map = {};

				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				for (var i = 0, dimension; i < dimensionArray.length; i++) {
					dimension = api.layout.Dimension(dimensionArray[i]);

					if (dimension) {
						map[dimension.dimension] = dimension.items;
					}
				}

				support.prototype.object.getLength(map) ? map : null;
			};

			service.layout.getExtendedLayout = function(layout) {
				var layout = Ext.clone(layout),
					xLayout;

				xLayout = {
					columns: [],
					rows: [],
					filters: [],

					columnObjectNames: [],
					columnDimensionNames: [],
					rowObjectNames: [],
					rowDimensionNames: [],

					// Axis
					axisDimensions: [],
					axisObjectNames: [],
					axisDimensionNames: [],

						// For param string
					sortedAxisDimensionNames: [],

					// Filter
					filterDimensions: [],
					filterObjectNames: [],
					filterDimensionNames: [],

						// For param string
					sortedFilterDimensions: [],

					// All
					dimensions: [],
					objectNames: [],
					dimensionNames: [],

					// Object name maps
					objectNameDimensionsMap: {},
					objectNameItemsMap: {},
					objectNameIdsMap: {},

					// Dimension name maps
					dimensionNameDimensionsMap: {},
					dimensionNameItemsMap: {},
					dimensionNameIdsMap: {},

						// For param string
					dimensionNameSortedIdsMap: {}
				};

				Ext.applyIf(xLayout, layout);

				// Columns, rows, filters
				if (layout.columns) {
					for (var i = 0, dim, items, xDim; i < layout.columns.length; i++) {
						dim = layout.columns[i];
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

						if (items) {
							xDim.items = items;
							xDim.ids = [];

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.columns.push(xDim);

						xLayout.columnObjectNames.push(xDim.objectName);
						xLayout.columnDimensionNames.push(xDim.dimensionName);

						xLayout.axisDimensions.push(xDim);
						xLayout.axisObjectNames.push(xDim.objectName);
						xLayout.axisDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				if (layout.rows) {
					for (var i = 0, dim, items, xDim; i < layout.rows.length; i++) {
						dim = Ext.clone(layout.rows[i]);
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

						if (items) {
							xDim.items = items;
							xDim.ids = [];

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.rows.push(xDim);

						xLayout.rowObjectNames.push(xDim.objectName);
						xLayout.rowDimensionNames.push(xDim.dimensionName);

						xLayout.axisDimensions.push(xDim);
						xLayout.axisObjectNames.push(xDim.objectName);
						xLayout.axisDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				if (layout.filters) {
					for (var i = 0, dim, items, xDim; i < layout.filters.length; i++) {
						dim = layout.filters[i];
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap[dim.dimension].dimensionName;

						if (items) {
							xDim.items = items;
							xDim.ids = [];

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.filters.push(xDim);

						xLayout.filterDimensions.push(xDim);
						xLayout.filterObjectNames.push(xDim.objectName);
						xLayout.filterDimensionNames.push(dimConf.objectNameMap[xDim.objectName].dimensionName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				// Unique dimension names
				xLayout.axisDimensionNames = Ext.Array.unique(xLayout.axisDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

				xLayout.columnDimensionNames = Ext.Array.unique(xLayout.columnDimensionNames);
				xLayout.rowDimensionNames = Ext.Array.unique(xLayout.rowDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

					// For param string
				xLayout.sortedAxisDimensionNames = Ext.clone(xLayout.axisDimensionNames).sort();
				xLayout.sortedFilterDimensions = service.layout.sortDimensionArray(Ext.clone(xLayout.filterDimensions));

				// All
				xLayout.dimensions = [].concat(xLayout.axisDimensions, xLayout.filterDimensions);
				xLayout.objectNames = [].concat(xLayout.axisObjectNames, xLayout.filterObjectNames);
				xLayout.dimensionNames = [].concat(xLayout.axisDimensionNames, xLayout.filterDimensionNames);

				// Dimension name maps
				for (var i = 0, dimName; i < xLayout.dimensionNames.length; i++) {
					dimName = xLayout.dimensionNames[i];

					xLayout.dimensionNameDimensionsMap[dimName] = [];
					xLayout.dimensionNameItemsMap[dimName] = [];
					xLayout.dimensionNameIdsMap[dimName] = [];
				}

				for (var i = 0, xDim; i < xLayout.dimensions.length; i++) {
					xDim = xLayout.dimensions[i];

					xLayout.dimensionNameDimensionsMap[xDim.dimensionName].push(xDim);
					xLayout.dimensionNameItemsMap[xDim.dimensionName] = xLayout.dimensionNameItemsMap[xDim.dimensionName].concat(xDim.items);
					xLayout.dimensionNameIdsMap[xDim.dimensionName] = xLayout.dimensionNameIdsMap[xDim.dimensionName].concat(xDim.ids);
				}

					// For param string
				for (var key in xLayout.dimensionNameIdsMap) {
					if (xLayout.dimensionNameIdsMap.hasOwnProperty(key)) {
						xLayout.dimensionNameSortedIdsMap[key] = Ext.clone(xLayout.dimensionNameIdsMap[key]).sort();
					}
				}

				return xLayout;
			};

			service.layout.getSyncronizedXLayout = function(xLayout, response) {
				var removeDimensionFromXLayout,
					getHeaderNames,
					dimensions = Ext.Array.clean([].concat(xLayout.columns || [], xLayout.rows || [], xLayout.filters || []));

				removeDimensionFromXLayout = function(objectName) {
					var getUpdatedAxis;

					getUpdatedAxis = function(axis) {
						var dimension;
						axis = Ext.clone(axis);

						for (var i = 0; i < axis.length; i++) {
							if (axis[i].dimension === objectName) {
								dimension = axis[i];
							}
						}

						if (dimension) {
							Ext.Array.remove(axis, dimension);
						}

						return axis;
					};

					if (xLayout.columns) {
						xLayout.columns = getUpdatedAxis(xLayout.columns);
					}
					if (xLayout.rows) {
						xLayout.rows = getUpdatedAxis(xLayout.rows);
					}
					if (xLayout.filters) {
						xLayout.filters = getUpdatedAxis(xLayout.filters);
					}
				};

				getHeaderNames = function() {
					var headerNames = [];

					for (var i = 0; i < response.headers.length; i++) {
						headerNames.push(response.headers[i].name);
					}

					return headerNames;
				};

				return function() {
					var headerNames = getHeaderNames(),
						xOuDimension = xLayout.objectNameDimensionsMap[dimConf.organisationUnit.objectName],
						isUserOrgunit = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT'),
						isUserOrgunitChildren = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT_CHILDREN'),
						isUserOrgunitGrandChildren = xOuDimension && Ext.Array.contains(xOuDimension.ids, 'USER_ORGUNIT_GRANDCHILDREN'),
						isLevel = function() {
							if (xOuDimension && Ext.isArray(xOuDimension.ids)) {
								for (var i = 0; i < xOuDimension.ids.length; i++) {
									if (xOuDimension.ids[i].substr(0,5) === 'LEVEL') {
										return true;
									}
								}
							}

							return false;
						}(),
						isGroup = function() {
							if (xOuDimension && Ext.isArray(xOuDimension.ids)) {
								for (var i = 0; i < xOuDimension.ids.length; i++) {
									if (xOuDimension.ids[i].substr(0,8) === 'OU_GROUP') {
										return true;
									}
								}
							}

							return false;
						}(),
						co = dimConf.category.objectName,
						ou = dimConf.organisationUnit.objectName,
						layout;

					// Set items from init/metaData/xLayout
					for (var i = 0, dim, metaDataDim, items; i < dimensions.length; i++) {
						dim = dimensions[i];
						dim.items = [];
						metaDataDim = response.metaData[dim.objectName];

						// If ou and children
						if (dim.dimensionName === ou) {
							if (isUserOrgunit || isUserOrgunitChildren || isUserOrgunitGrandChildren) {
								var userOu,
									userOuc,
									userOugc;

								if (isUserOrgunit) {
									userOu = [{
										id: ns.init.user.ou,
										name: getItemName(ns.init.user.ou, response)
									}];
								}
								if (isUserOrgunitChildren) {
									userOuc = [];

									for (var j = 0; j < ns.init.user.ouc.length; j++) {
										userOuc.push({
											id: ns.init.user.ouc[j],
											name: getItemName(ns.init.user.ouc[j], response)
										});
									}

									userOuc = ns.support.prototype.array.sortObjectsByObjectKey(userOuc);
								}
								if (isUserOrgunitGrandChildren) {
									var userOuOuc = [].concat(ns.init.user.ou, ns.init.user.ouc),
										responseOu = response.metaData[ou];

									userOugc = [];

									for (var j = 0, id; j < responseOu.length; j++) {
										id = responseOu[j];

										if (!Ext.Array.contains(userOuOuc, id)) {
											userOugc.push({
												id: id,
												name: getItemName(id, response)
											});
										}
									}

									userOugc = ns.support.prototype.array.sortObjectsByObjectKey(userOugc);
								}

								dim.items = [].concat(userOu || [], userOuc || [], userOugc || []);
							}
							else if (isLevel || isGroup) {
								for (var j = 0, responseOu = response.metaData[ou], id; j < responseOu.length; j++) {
									id = responseOu[j];

									dim.items.push({
										id: id,
										name: getItemName(id, response)
									});
								}

								dim.items = ns.support.prototype.array.sortObjectsByObjectKey(dim.items);
							}
							else {
								dim.items = Ext.clone(xLayout.dimensionNameItemsMap[dim.dimensionName]);
							}
						}
						else {
							// Items: get ids from metadata -> items
							if (Ext.isArray(metaDataDim) && metaDataDim.length) {
								var ids = Ext.clone(response.metaData[dim.dimensionName]);
								for (var j = 0; j < ids.length; j++) {
									dim.items.push({
										id: ids[j],
										name: response.metaData.names[ids[j]]
									});
								}
							}
							// Items: get items from xLayout
							else {
								dim.items = Ext.clone(xLayout.objectNameItemsMap[dim.objectName]);
							}
						}
					}

					// Remove dimensions from layout that do not exist in response
					for (var i = 0, dimensionName; i < xLayout.axisDimensionNames.length; i++) {
						dimensionName = xLayout.axisDimensionNames[i];
						if (!Ext.Array.contains(headerNames, dimensionName)) {
							removeDimensionFromXLayout(dimensionName);
						}
					}

					// Re-layout
					layout = api.layout.Layout(xLayout);

					if (layout) {
						dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

						for (var i = 0, idNameMap = response.metaData.names, dimItems; i < dimensions.length; i++) {
							dimItems = dimensions[i].items;

							if (Ext.isArray(dimItems) && dimItems.length) {
								for (var j = 0, item; j < dimItems.length; j++) {
									item = dimItems[j];

									if (Ext.isObject(item) && Ext.isString(idNameMap[item.id]) && !Ext.isString(item.name)) {
										item.name = idNameMap[item.id] || '';
									}
								}
							}
						}

						return service.layout.getExtendedLayout(layout);
					}

					return null;
				}();
			};

			service.layout.getExtendedAxis = function(type, dimensionNames, xResponse) {
				if (!dimensionNames || (Ext.isArray(dimensionNames) && !dimensionNames.length)) {
					return;
				}

				var dimensionNames = Ext.clone(dimensionNames),
					mDimensions = [],
					spanType = type === 'col' ? 'colSpan' : 'rowSpan',
					nCols = 1,
					aNumCols = [],
					aAccNumCols = [],
					aSpan = [],
					aGuiItems = [],
					aAllItems = [],
					aColIds = [],
					aAllObjects = [],
					aUniqueIds;

				for (var i = 0; i < dimensionNames.length; i++) {
					mDimensions.push({
						dimensionName: dimensionNames[i]
					});
				}

				aUniqueIds = function() {
					var a = [];

					for (var i = 0, dim; i < mDimensions.length; i++) {
						dim = mDimensions[i];

						a.push(xResponse.nameHeaderMap[dim.dimensionName].items);
					}

					return a;
				}();
//aUniqueIds	= [ [de1, de2, de3],
//					[p1],
//					[ou1, ou2, ou3, ou4] ]


				for (var i = 0, dim; i < aUniqueIds.length; i++) {
					nNumCols = aUniqueIds[i].length;

					aNumCols.push(nNumCols);
					nCols = nCols * nNumCols;
					aAccNumCols.push(nCols);
				}
	//aNumCols		= [3, 1, 4]
	//nCols			= (12) [3, 3, 12] (3 * 1 * 4)
	//aAccNumCols	= [3, 3, 12]

	//nCols			= 12

				for (var i = 0; i < aUniqueIds.length; i++) {
					if (aNumCols[i] === 1) {
						if (i === 0) {
							aSpan.push(nCols); //if just one item and top level, span all
						}
						else {
							if (layout.hideEmptyRows && type === 'row') {
								aSpan.push(nCols / aAccNumCols[i]);
							}
							else {
								aSpan.push(aSpan[0]); //if just one item and not top level, span same as top level
							}
						}
					}
					else {
						aSpan.push(nCols / aAccNumCols[i]);
					}
				}
	//aSpan			= [4, 12, 1]


				aGuiItems.push(aUniqueIds[0]);

				if (aUniqueIds.length > 1) {
					for (var i = 1, a, n; i < aUniqueIds.length; i++) {
						a = [];
						n = aNumCols[i] === 1 ? aNumCols[0] : aAccNumCols[i-1];

						for (var j = 0; j < n; j++) {
							a = a.concat(aUniqueIds[i]);
						}

						aGuiItems.push(a);
					}
				}
	//aGuiItems	= [ [d1, d2, d3], (3)
	//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
	//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
	//		  	  ]

				for (var i = 0, aAllRow, aUniqueRow, span, factor; i < aUniqueIds.length; i++) {
					aAllRow = [];
					aUniqueRow = aUniqueIds[i];
					span = aSpan[i];
					factor = nCols / (span * aUniqueRow.length);

					for (var j = 0; j < factor; j++) {
						for (var k = 0; k < aUniqueRow.length; k++) {
							for (var l = 0; l < span; l++) {
								aAllRow.push(aUniqueRow[k]);
							}
						}
					}

					aAllItems.push(aAllRow);
				}
	//aAllItems	= [ [d1, d1, d1, d1, d1, d1, d1, d1, d1, d1, d2, d2, d2, d2, d2, d2, d2, d2, d2, d2, d3, d3, d3, d3, d3, d3, d3, d3, d3, d3], (30)
	//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (30)
	//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2] (30)
	//		  	  ]


				for (var i = 0, id; i < nCols; i++) {
					id = '';

					for (var j = 0; j < aAllItems.length; j++) {
						id += aAllItems[j][i];
					}

					aColIds.push(id);
				}
	//aColIds	= [ abc, bcd, ... ]


				// allObjects

				for (var i = 0, allRow; i < aAllItems.length; i++) {
					allRow = [];

					for (var j = 0; j < aAllItems[i].length; j++) {
						allRow.push({
							id: aAllItems[i][j],
							uuid: Ext.data.IdGenerator.get('uuid').generate(),
							dim: i,
							axis: type
						});
					}

					aAllObjects.push(allRow);
				}

				// add span and children
				for (var i = 0; i < aAllObjects.length; i++) {
					for (var j = 0, obj; j < aAllObjects[i].length; j += aSpan[i]) {
						obj = aAllObjects[i][j];

						// span
						obj[spanType] = aSpan[i];

						// children
						obj.children = Ext.isDefined(aSpan[i + 1]) ? aSpan[i] / aSpan[i + 1] : 0;

						if (i === 0) {
							obj.root = true;
						}
					}
				}

				// add parents
				if (aAllObjects.length > 1) {
					for (var i = 1, allRow; i < aAllObjects.length; i++) {
						allRow = aAllObjects[i];

						for (var j = 0, obj, sizeCount = 0, span = aSpan[i - 1], parentObj = aAllObjects[i - 1][0]; j < allRow.length; j++) {
							obj = allRow[j];
							obj.parent = parentObj;

							sizeCount++;

							if (sizeCount === span) {
								parentObj = aAllObjects[i - 1][j + 1];
								sizeCount = 0;
							}
						}
					}
				}

				// add uuids array to leaves
				if (aAllObjects.length) {

					// Span is second-last in aSpan - or axis size (instead of 1 - to highlight all leaves when dim == 1 )
					var span = aAllObjects.length > 1 ? aSpan[aAllObjects.length - 2] : nCols;
					//var span = aAllObjects.length > 1 ? aSpan[aAllObjects.length - 2] : 1;

					for (var i = 0, leaf, parentUuids, obj, leafUuids = []; i < aAllObjects[aAllObjects.length - 1].length; i++) {
						leaf = aAllObjects[aAllObjects.length - 1][i];
						leafUuids.push(leaf.uuid);
						parentUuids = [];
						obj = leaf;

						// get parent uuids
						while (obj.parent) {
							obj = obj.parent;
							parentUuids.push(obj.uuid);
						}

						// add parent uuids
						leaf.uuids = Ext.clone(parentUuids);

						// add uuid for all leaves
						if (leafUuids.length === span) {
							for (var j = (i - span) + 1, leaf; j <= i; j++) {
								leaf = aAllObjects[aAllObjects.length - 1][j];
								leaf.uuids = leaf.uuids.concat(Ext.clone(leafUuids));
							}

							leafUuids = [];
						}
					}
				}

				// populate uuid-object map
				for (var i = 0; i < aAllObjects.length; i++) {
					for (var j = 0, object; j < aAllObjects[i].length; j++) {
						object = aAllObjects[i][j];
//console.log(object.uuid, object);
						uuidObjectMap[object.uuid] = object;
					}
				}

//console.log("aAllObjects", aAllObjects);

				return {
					type: type,
					items: mDimensions,
					xItems: {
						unique: aUniqueIds,
						gui: aGuiItems,
						all: aAllItems
					},
					objects: {
						all: aAllObjects
					},
					ids: aColIds,
					span: aSpan,
					dims: aUniqueIds.length,
					size: nCols
				};
			};

			// response
			service.response = {};

			service.response.getExtendedResponse = function(response, xLayout) {
				var ids = [];

				response.nameHeaderMap = {};
				response.idValueMap = {};

				// extend headers
				(function() {

					// Extend headers: index, items, size
					for (var i = 0, header; i < response.headers.length; i++) {
						header = response.headers[i];

						// Index
						header.index = i;

						if (header.meta) {

							// Items
							header.items = Ext.clone(xLayout.dimensionNameIdsMap[header.name]) || [];

							// Size
							header.size = header.items.length;

							// Collect ids, used by extendMetaData
							ids = ids.concat(header.items);
						}
					}

					// nameHeaderMap (headerName: header)
					for (var i = 0, header; i < response.headers.length; i++) {
						header = response.headers[i];

						response.nameHeaderMap[header.name] = header;
					}
				}());

				// extend metadata
				(function() {
					for (var i = 0, id, splitId ; i < ids.length; i++) {
						id = ids[i];

						if (id.indexOf('-') !== -1) {
							splitId = id.split('-');
							response.metaData.names[id] = response.metaData.names[splitId[0]] + ' ' + response.metaData.names[splitId[1]];
						}
					}
				}());

				// create value id map
				(function() {
					var valueHeaderIndex = response.nameHeaderMap[conf.finals.dimension.value.value].index,
						coHeader = response.nameHeaderMap[conf.finals.dimension.category.dimensionName],
						dx = dimConf.data.dimensionName,
						co = dimConf.category.dimensionName,
						axisDimensionNames = xLayout.axisDimensionNames,
						idIndexOrder = [];

					// idIndexOrder
					for (var i = 0; i < axisDimensionNames.length; i++) {
						idIndexOrder.push(response.nameHeaderMap[axisDimensionNames[i]].index);

						// If co exists in response and is not added in layout, add co after dx
						if (coHeader && !Ext.Array.contains(axisDimensionNames, co) && axisDimensionNames[i] === dx) {
							idIndexOrder.push(coHeader.index);
						}
					}

					// idValueMap
					for (var i = 0, row, id; i < response.rows.length; i++) {
						row = response.rows[i];
						id = '';

						for (var j = 0; j < idIndexOrder.length; j++) {
							id += row[idIndexOrder[j]];
						}

						response.idValueMap[id] = row[valueHeaderIndex];
					}
				}());

				return response;
			};
		}());

		// web
		(function() {

			// mask
			web.mask = {};

			web.mask.show = function(component, message) {
				if (!Ext.isObject(component)) {
					console.log('support.gui.mask.show: component not an object');
					return null;
				}

				message = message || 'Loading..';

				if (component.mask) {
					component.mask.destroy();
					component.mask = null;
				}

				component.mask = new Ext.create('Ext.LoadMask', component, {
					shadow: false,
					message: message,
					style: 'box-shadow:0',
					bodyStyle: 'box-shadow:0'
				});

				component.mask.show();
			};

			web.mask.hide = function(component) {
				if (!Ext.isObject(component)) {
					console.log('support.gui.mask.hide: component not an object');
					return null;
				}

				if (component.mask) {
					component.mask.destroy();
					component.mask = null;
				}
			};

			// analytics
			web.analytics = {};

			web.analytics.getParamString = function(xLayout, isSorted) {
				var axisDimensionNames = isSorted ? xLayout.sortedAxisDimensionNames : xLayout.axisDimensionNames,
					filterDimensions = isSorted ? xLayout.sortedFilterDimensions : xLayout.filterDimensions,
					dimensionNameIdsMap = isSorted ? xLayout.dimensionNameSortedIdsMap : xLayout.dimensionNameIdsMap,
					paramString = '?',
					addCategoryDimension = false,
					map = xLayout.dimensionNameItemsMap,
					dx = dimConf.indicator.dimensionName,
					co = dimConf.category.dimensionName;

				for (var i = 0, dimName, items; i < axisDimensionNames.length; i++) {
					dimName = axisDimensionNames[i];

					paramString += 'dimension=' + dimName;

					items = Ext.clone(dimensionNameIdsMap[dimName]);

					if (dimName === dx) {
						for (var j = 0, index; j < items.length; j++) {
							index = items[j].indexOf('-');

							if (index > 0) {
								addCategoryDimension = true;
								items[j] = items[j].substr(0, index);
							}
						}

						items = Ext.Array.unique(items);
					}

					if (dimName !== co) {
						paramString += ':' + items.join(';');
					}

					if (i < (axisDimensionNames.length - 1)) {
						paramString += '&';
					}
				}

				if (addCategoryDimension) {
					paramString += '&dimension=' + conf.finals.dimension.category.dimensionName;
				}

				if (Ext.isArray(filterDimensions) && filterDimensions.length) {
					for (var i = 0, dim; i < filterDimensions.length; i++) {
						dim = filterDimensions[i];

						paramString += '&filter=' + dim.dimensionName + ':' + dim.ids.join(';');
					}
				}

				if (xLayout.showHierarchy) {
					paramString += '&hierarchyMeta=true';
				}

				return paramString;
			};

			// pivot
			web.pivot = {};

			web.pivot.create = function(xColAxis, xRowAxis, xResponse) {
				var getRoundedHtmlValue,
					getTdHtml,
					doSubTotals,
					doTotals,
					getColAxisHtmlArray,
					getRowHtmlArray,
					rowAxisHtmlArray,
					getColTotalHtmlArray,
					getGrandTotalHtmlArray,
					getTotalHtmlArray,
					getHtml,
					getUniqueFactor = function(xAxis) {
						if (!xAxis) {
							return null;
						}

						var unique = xAxis.xItems.unique;

						if (unique) {
							if (unique.length < 2) {
								return 1;
							}
							else {
								return xAxis.size / unique[0].length;
							}
						}

						return null;
					},
					colUniqueFactor = getUniqueFactor(xColAxis),
					rowUniqueFactor = getUniqueFactor(xRowAxis),
					valueItems = [],
					valueObjects = [],
					totalColObjects = [],
					htmlArray;

				getRoundedHtmlValue = function(value, dec) {
					dec = dec || 2;
					return parseFloat(support.prototype.number.roundIf(value, 2)).toString();
				};

				getTdHtml = function(config) {
					var bgColor,
						mapLegends,
						colSpan,
						rowSpan,
						htmlValue,
						displayDensity,
						fontSize,
						isLegendSet = Ext.isObject(legendSet) && Ext.isArray(legendSet.mapLegends) && legendSet.mapLegends.length,
						isNumeric = Ext.isObject(config) && Ext.isString(config.type) && config.type.substr(0,5) === 'value' && !config.empty,
						isValue = Ext.isObject(config) && Ext.isString(config.type) && config.type === 'value' && !config.empty,
						cls = '',
						html = '';

					if (!Ext.isObject(config)) {
						return '';
					}

					// Background color from legend set
					if (isNumeric && isLegendSet) {
						mapLegends = legendSet.mapLegends;

						for (var i = 0, value; i < mapLegends.length; i++) {
							value = parseFloat(config.value);

							if (Ext.Number.constrain(value, mapLegends[i].sv, mapLegends[i].ev) === value) {
								bgColor = mapLegends[i].color;
							}
						}
					}

					colSpan = config.colSpan ? 'colspan="' + config.colSpan + '" ' : '';
					rowSpan = config.rowSpan ? 'rowspan="' + config.rowSpan + '" ' : '';
					htmlValue = config.collapsed ? '' : config.htmlValue || config.value || '';
					htmlValue = config.type !== 'dimension' ? support.prototype.number.prettyPrint(htmlValue, layout.digitGroupSeparator) : htmlValue;
					displayDensity = conf.pivot.displayDensity[config.displayDensity] || conf.pivot.displayDensity[layout.displayDensity];
					fontSize = conf.pivot.fontSize[config.fontSize] || conf.pivot.fontSize[layout.fontSize];

					cls += config.hidden ? ' td-hidden' : '';
					cls += config.collapsed ? ' td-collapsed' : '';
					cls += isValue ? ' pointer' : '';
					cls += bgColor ? ' legend' : (config.cls ? ' ' + config.cls : '');

					html += '<td ' + (config.uuid ? ('id="' + config.uuid + '" ') : '') + ' class="' + cls + '" ' + colSpan + rowSpan;

					if (bgColor) {
						html += '>';
						html += '<div class="legendCt">';
						html += '<div class="number ' + config.cls + '" style="padding:' + displayDensity + '; padding-right:3px; font-size:' + fontSize + '">' + htmlValue + '</div>';
						html += '<div class="arrowCt ' + config.cls + '">';
						html += '<div class="arrow" style="border-bottom:8px solid transparent; border-right:8px solid ' + bgColor + '">&nbsp;</div>';
						html += '</div></div></div></td>';

						//cls = 'legend';
						//cls += config.hidden ? ' td-hidden' : '';
						//cls += config.collapsed ? ' td-collapsed' : '';

						//html += '<td class="' + cls + '" ';
						//html += colSpan + rowSpan + '>';
						//html += '<div class="legendCt">';
						//html += '<div style="display:table-cell; padding:' + displayDensity + '; font-size:' + fontSize + '"';
						//html += config.cls ? ' class="' + config.cls + '">' : '';
						//html += htmlValue + '</div>';
						//html += '<div class="legendColor" style="background-color:' + bgColor + '">&nbsp;</div>';
						//html += '</div></td>';
					}
					else {
						html += 'style="padding:' + displayDensity + '; font-size:' + fontSize + ';"' + '>' + htmlValue + '</td>';
					}

					return html;
				};

				doSubTotals = function(xAxis) {
					return !!layout.showSubTotals && xAxis && xAxis.dims > 1;

					//var multiItemDimension = 0,
						//unique;

					//if (!(layout.showSubTotals && xAxis && xAxis.dims > 1)) {
						//return false;
					//}

					//unique = xAxis.xItems.unique;

					//for (var i = 0; i < unique.length; i++) {
						//if (unique[i].length > 1) {
							//multiItemDimension++;
						//}
					//}

					//return (multiItemDimension > 1);
				};

				doTotals = function() {
					return !!layout.showTotals;
				};

				getColAxisHtmlArray = function() {
					var a = [],
						getEmptyHtmlArray;

					getEmptyHtmlArray = function() {
						return (xColAxis && xRowAxis) ? getTdHtml({
							cls: 'pivot-dim-empty',
							colSpan: xRowAxis.dims,
							rowSpan: xColAxis.dims
						}) : '';
					};

					if (!(xColAxis && Ext.isObject(xColAxis))) {
						return a;
					}

					for (var i = 0, dimHtml; i < xColAxis.dims; i++) {
						dimHtml = [];

						if (i === 0) {
							dimHtml.push(getEmptyHtmlArray());
						}

						for (var j = 0, obj, spanCount = 0; j < xColAxis.size; j++) {
							spanCount++;

							obj = xColAxis.objects.all[i][j];
							obj.type = 'dimension';
							obj.cls = 'pivot-dim';
							obj.noBreak = false;
							obj.hidden = !(obj.rowSpan || obj.colSpan);
							obj.htmlValue = getItemName(obj.id, xResponse, true);

							dimHtml.push(getTdHtml(obj));

							if (i === 0 && spanCount === xColAxis.span[i] && doSubTotals(xColAxis) ) {
								dimHtml.push(getTdHtml({
									type: 'dimensionSubtotal',
									cls: 'pivot-dim-subtotal',
									rowSpan: xColAxis.dims
								}));

								spanCount = 0;
							}

							if (i === 0 && (j === xColAxis.size - 1) && doTotals()) {
								dimHtml.push(getTdHtml({
									type: 'dimensionTotal',
									cls: 'pivot-dim-total',
									rowSpan: xColAxis.dims,
									htmlValue: 'Total'
								}));
							}
						}

						a.push(dimHtml);
					}

					return a;
				};

				getRowHtmlArray = function() {
					var a = [],
						axisObjects = [],
						xValueObjects,
						totalValueObjects = [],
						mergedObjects = [],
						valueItemsCopy,
						colSize = xColAxis ? xColAxis.size : 1,
						rowSize = xRowAxis ? xRowAxis.size : 1,
						recursiveReduce;

					recursiveReduce = function(obj) {
						if (!obj.children) {
							obj.collapsed = true;

							if (obj.parent) {
								obj.parent.children = obj.parent.children - 1;
							}
						}

						if (obj.parent) {
							recursiveReduce(obj.parent);
						}
					};

					// Populate dim objects
					if (xRowAxis) {
						for (var i = 0, row; i < xRowAxis.size; i++) {
							row = [];

							for (var j = 0, obj, newObj; j < xRowAxis.dims; j++) {
								obj = xRowAxis.objects.all[j][i];
								obj.type = 'dimension';
								obj.cls = 'pivot-dim td-nobreak' + (isHierarchy(obj.id, xResponse) ? ' align-left' : '');
								obj.noBreak = true;
								obj.hidden = !(obj.rowSpan || obj.colSpan);
								obj.htmlValue = getItemName(obj.id, xResponse, true);

								row.push(obj);
							}

							axisObjects.push(row);
						}
					}

					// Value objects
					for (var i = 0, valueItemsRow, valueObjectsRow, idValueMap = Ext.clone(xResponse.idValueMap); i < rowSize; i++) {
						valueItemsRow = [];
						valueObjectsRow = [];

						for (var j = 0, id, value, htmlValue, empty, uuid, uuids; j < colSize; j++) {
							empty = false;
							uuids = [];

							// meta data uid
							id = (xColAxis ? support.prototype.str.replaceAll(xColAxis.ids[j], '-', '') : '') + (xRowAxis ? support.prototype.str.replaceAll(xRowAxis.ids[i], '-', '') : '');

							// value html element id
							uuid = Ext.data.IdGenerator.get('uuid').generate();

							// col and row dim element ids
							if (xColAxis) {
								uuids = uuids.concat(xColAxis.objects.all[xColAxis.dims - 1][j].uuids);
							}
							if (xRowAxis) {
								uuids = uuids.concat(xRowAxis.objects.all[xRowAxis.dims - 1][i].uuids);
							}

							if (idValueMap[id]) {
								value = parseFloat(idValueMap[id]);
								htmlValue = value.toString();
							}
							else {
								value = 0;
								htmlValue = '';
								empty = true;
							}

							valueItemsRow.push(value);
							valueObjectsRow.push({
								uuid: uuid,
								type: 'value',
								cls: 'pivot-value',
								value: value,
								htmlValue: htmlValue,
								empty: empty,
								uuids: uuids
							});

							// Map element id to dim element ids
							uuidDimUuidsMap[uuid] = uuids;
						}

						valueItems.push(valueItemsRow);
						valueObjects.push(valueObjectsRow);
					}

					// Value total objects
					if (xColAxis && doTotals()) {
						for (var i = 0, empty = [], total = 0; i < valueObjects.length; i++) {
							for (j = 0, obj; j < valueObjects[i].length; j++) {
								obj = valueObjects[i][j];

								empty.push(obj.empty);
								total += obj.value;
							}

							totalValueObjects.push({
								type: 'valueTotal',
								cls: 'pivot-value-total',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
								empty: !Ext.Array.contains(empty, false)
							});

							empty = [];
							total = 0;
						}
					}

					// Hide empty rows (dims/values/totals)
					if (xColAxis && xRowAxis) {
						if (layout.hideEmptyRows) {
							for (var i = 0, valueRow, empty, parent; i < valueObjects.length; i++) {
								valueRow = valueObjects[i];
								empty = [];

								for (var j = 0; j < valueRow.length; j++) {
									empty.push(!!valueRow[j].empty);
								}

								if (!Ext.Array.contains(empty, false) && xRowAxis) {

									// Hide values
									for (var j = 0; j < valueRow.length; j++) {
										valueRow[j].collapsed = true;
									}

									// Hide total
									if (doTotals()) {
										totalValueObjects[i].collapsed = true;
									}

									// Hide/reduce parent dim span
									parent = axisObjects[i][xRowAxis.dims-1];
									recursiveReduce(parent);
								}
							}
						}
					}

					xValueObjects = Ext.clone(valueObjects);

					// Col subtotals
					if (doSubTotals(xColAxis)) {
						var tmpValueObjects = [];

						for (var i = 0, row, rowSubTotal, colCount; i < xValueObjects.length; i++) {
							row = [];
							rowSubTotal = 0;
							colCount = 0;

							for (var j = 0, item, collapsed = [], empty = []; j < xValueObjects[i].length; j++) {
								item = xValueObjects[i][j];
								rowSubTotal += item.value;
								empty.push(!!item.empty);
								collapsed.push(!!item.collapsed);
								colCount++;

								row.push(item);

								if (colCount === colUniqueFactor) {
									row.push({
										type: 'valueSubtotal',
										cls: 'pivot-value-subtotal',
										value: rowSubTotal,
										htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(rowSubTotal) : '',
										empty: !Ext.Array.contains(empty, false),
										collapsed: !Ext.Array.contains(collapsed, false)
									});

									colCount = 0;
									rowSubTotal = 0;
									empty = [];
									collapsed = [];
								}
							}

							tmpValueObjects.push(row);
						}

						xValueObjects = tmpValueObjects;
					}

					// Row subtotals
					if (doSubTotals(xRowAxis)) {
						var tmpAxisObjects = [],
							tmpValueObjects = [],
							tmpTotalValueObjects = [],
							getAxisSubTotalRow;

						getAxisSubTotalRow = function(collapsed) {
							var row = [];

							for (var i = 0, obj; i < xRowAxis.dims; i++) {
								obj = {};
								obj.type = 'dimensionSubtotal';
								obj.cls = 'pivot-dim-subtotal';
								obj.collapsed = Ext.Array.contains(collapsed, true);

								if (i === 0) {
									obj.htmlValue = '';
									obj.colSpan = xRowAxis.dims;
								}
								else {
									obj.hidden = true;
								}

								row.push(obj);
							}

							return row;
						};

						// tmpAxisObjects
						for (var i = 0, row, collapsed = []; i < axisObjects.length; i++) {
							tmpAxisObjects.push(axisObjects[i]);
							collapsed.push(!!axisObjects[i][0].collapsed);

							// Insert subtotal after last objects
							if (!Ext.isArray(axisObjects[i+1]) || !!axisObjects[i+1][0].root) {
								tmpAxisObjects.push(getAxisSubTotalRow(collapsed));

								collapsed = [];
							}
						}

						// tmpValueObjects
						for (var i = 0; i < tmpAxisObjects.length; i++) {
							tmpValueObjects.push([]);
						}

						for (var i = 0; i < xValueObjects[0].length; i++) {
							for (var j = 0, rowCount = 0, tmpCount = 0, subTotal = 0, empty = [], collapsed, item; j < xValueObjects.length; j++) {
								item = xValueObjects[j][i];
								tmpValueObjects[tmpCount++].push(item);
								subTotal += item.value;
								empty.push(!!item.empty);
								rowCount++;

								if (axisObjects[j][0].root) {
									collapsed = !!axisObjects[j][0].collapsed;
								}

								if (!Ext.isArray(axisObjects[j+1]) || axisObjects[j+1][0].root) {
									tmpValueObjects[tmpCount++].push({
										type: item.type === 'value' ? 'valueSubtotal' : 'valueSubtotalTotal',
										value: subTotal,
										htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
										collapsed: collapsed,
										cls: item.type === 'value' ? 'pivot-value-subtotal' : 'pivot-value-subtotal-total'
									});
									rowCount = 0;
									subTotal = 0;
									empty = [];
								}
							}
						}

						// tmpTotalValueObjects
						for (var i = 0, obj, collapsed = [], empty = [], subTotal = 0, count = 0; i < totalValueObjects.length; i++) {
							obj = totalValueObjects[i];
							tmpTotalValueObjects.push(obj);

							collapsed.push(!!obj.collapsed);
							empty.push(!!obj.empty);
							subTotal += obj.value;
							count++;

							if (count === xRowAxis.span[0]) {
								tmpTotalValueObjects.push({
									type: 'valueTotalSubgrandtotal',
									cls: 'pivot-value-total-subgrandtotal',
									value: subTotal,
									htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
									empty: !Ext.Array.contains(empty, false),
									collapsed: !Ext.Array.contains(collapsed, false)
								});

								collapsed = [];
								empty = [];
								subTotal = 0;
								count = 0;
							}
						}

						axisObjects = tmpAxisObjects;
						xValueObjects = tmpValueObjects;
						totalValueObjects = tmpTotalValueObjects;
					}

					// Merge dim, value, total
					for (var i = 0, row; i < xValueObjects.length; i++) {
						row = [];

						if (xRowAxis) {
							row = row.concat(axisObjects[i]);
						}

						row = row.concat(xValueObjects[i]);

						if (xColAxis) {
							row = row.concat(totalValueObjects[i]);
						}

						mergedObjects.push(row);
					}

					// Create html items
					for (var i = 0, row; i < mergedObjects.length; i++) {
						row = [];

						for (var j = 0; j < mergedObjects[i].length; j++) {
							row.push(getTdHtml(mergedObjects[i][j]));
						}

						a.push(row);
					}

					return a;
				};

				getColTotalHtmlArray = function() {
					var a = [];

					if (xRowAxis && doTotals()) {
						var xTotalColObjects;

						// Total col items
						for (var i = 0, total = 0, empty = []; i < valueObjects[0].length; i++) {
							for (var j = 0, obj; j < valueObjects.length; j++) {
								obj = valueObjects[j][i];

								total += obj.value;
								empty.push(!!obj.empty);
							}

							totalColObjects.push({
								type: 'valueTotal',
								value: total,
								htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
								empty: !Ext.Array.contains(empty, false),
								cls: 'pivot-value-total'
							});

							total = 0;
							empty = [];
						}

						xTotalColObjects = Ext.clone(totalColObjects);

						if (xColAxis && doSubTotals(xColAxis)) {
							var tmp = [];

							for (var i = 0, item, subTotal = 0, empty = [], colCount = 0; i < xTotalColObjects.length; i++) {
								item = xTotalColObjects[i];
								tmp.push(item);
								subTotal += item.value;
								empty.push(!!item.empty);
								colCount++;

								if (colCount === colUniqueFactor) {
									tmp.push({
										type: 'valueTotalSubgrandtotal',
										value: subTotal,
										htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(subTotal) : '',
										empty: !Ext.Array.contains(empty, false),
										cls: 'pivot-value-total-subgrandtotal'
									});

									subTotal = 0;
									colCount = 0;
								}
							}

							xTotalColObjects = tmp;
						}

						// Total col html items
						for (var i = 0; i < xTotalColObjects.length; i++) {
							a.push(getTdHtml(xTotalColObjects[i]));
						}
					}

					return a;
				};

				getGrandTotalHtmlArray = function() {
					var total = 0,
						empty = [],
						a = [];

					if (doTotals()) {
						for (var i = 0, obj; i < totalColObjects.length; i++) {
							obj = totalColObjects[i];

							total += obj.value;
							empty.push(obj.empty);
						}

						if (xColAxis && xRowAxis) {
							a.push(getTdHtml({
								type: 'valueGrandTotal',
								cls: 'pivot-value-grandtotal',
								htmlValue: Ext.Array.contains(empty, false) ? getRoundedHtmlValue(total) : '',
								empty: !Ext.Array.contains(empty, false)
							}));
						}
					}

					return a;
				};

				getTotalHtmlArray = function() {
					var dimTotalArray,
						colTotal = getColTotalHtmlArray(),
						grandTotal = getGrandTotalHtmlArray(),
						row,
						a = [];

					if (doTotals()) {
						if (xRowAxis) {
							dimTotalArray = [getTdHtml({
								type: 'dimensionSubtotal',
								cls: 'pivot-dim-total',
								colSpan: xRowAxis.dims,
								htmlValue: 'Total'
							})];
						}

						row = [].concat(dimTotalArray || [], Ext.clone(colTotal) || [], Ext.clone(grandTotal) || []);

						a.push(row);
					}

					return a;
				};

				getHtml = function() {
					var s = '<table id="' + tableUuid + '" class="pivot">';

					for (var i = 0; i < htmlArray.length; i++) {
						s += '<tr>' + htmlArray[i].join('') + '</tr>';
					}

					return s += '</table>';
				};

				htmlArray = [].concat(getColAxisHtmlArray(), getRowHtmlArray(), getTotalHtmlArray());
				htmlArray = Ext.Array.clean(htmlArray);

				return getHtml(htmlArray);
			};
		}());

		// init
		(function() {
			// sort and extend dynamic dimensions
			if (init.dimensions) {
				init.dimensions = support.prototype.array.sortObjectsByObjectKey(init.dimensions);

				for (var i = 0, dim; i < init.dimensions.length; i++) {
					dim = init.dimensions[i];
					dim.dimensionName = dim.id;
					dim.objectName = conf.finals.dimension.dimension.objectName;
					conf.finals.dimension.objectNameMap[dim.id] = dim;
				}
			}

			// legend set map
			init.idLegendSetMap = {};

			for (var i = 0, set; i < init.legendSets.length; i++) {
				set = init.legendSets[i];
				init.idLegendSetMap[set.id] = set;
			}
		}());

		// todo
		(function() {




			pivot.setSessionStorage = function(session, obj, url) {
				if (NS.isSessionStorage) {
					var dhis2 = JSON.parse(sessionStorage.getItem('dhis2')) || {};
					dhis2[session] = obj;
					sessionStorage.setItem('dhis2', JSON.stringify(dhis2));

					if (Ext.isString(url)) {
						window.location.href = url;
					}
				}
			};

			pivot.createTable = function(layout, ns, updateGui, isFavorite) {
				var legendSet = layout.legendSet ? ns.init.idLegendSetMap[layout.legendSet.id] : null,
					isHierarchy,
					getItemName,
					validateUrl,
					setMouseHandlers,
					initialize,
					afterLoad,
					tableUuid = ns.init.el + '_' + Ext.data.IdGenerator.get('uuid').generate(),
					uuidDimUuidsMap = {},
					uuidObjectMap = {};

				isHierarchy = function(id, response) {
					return layout.showHierarchy && Ext.isObject(response.metaData.ouHierarchy) && response.metaData.ouHierarchy.hasOwnProperty(id);
				};

				getItemName = function(id, response, isHtml) {
					var metaData = response.metaData,
						name = '';

					if (isHierarchy(id, response)) {
						var a = Ext.clean(metaData.ouHierarchy[id].split('/'));
						a.shift();

						for (var i = 0; i < a.length; i++) {
							name += (isHtml ? '<span class="text-weak">' : '') + metaData.names[a[i]] + (isHtml ? '</span>' : '') + ' / ';
						}
					}

					name += metaData.names[id];

					return name;
				};

				validateUrl = function(url) {
					if (!Ext.isString(url) || url.length > 2000) {
						var percent = ((url.length - 2000) / url.length) * 100;
						alert('Too many parameters selected. Please reduce the number of parameters by at least ' + Ext.Number.toFixed(percent, 0) + '%.');
						return;
					}

					return true;
				};

				setMouseHandlers = function() {
					var valueElement;

					for (var key in uuidDimUuidsMap) {
						if (uuidDimUuidsMap.hasOwnProperty(key)) {
							valueElement = Ext.get(key);

							if (parseFloat(valueElement.dom.textContent)) {
								valueElement.dom.ns = ns;
								valueElement.dom.setAttribute('onclick', 'this.ns.pivot.onMouseClick(this.id, this.ns);');
							}
						}
					}
				};

				afterLoad = function(layout, xLayout, xResponse) {

					if (ns.isPlugin) {

						// Resize render elements
						var baseEl = Ext.get(ns.init.el),
							baseElBorderW = parseInt(baseEl.getStyle('border-left-width')) + parseInt(baseEl.getStyle('border-right-width')),
							baseElBorderH = parseInt(baseEl.getStyle('border-top-width')) + parseInt(baseEl.getStyle('border-bottom-width')),
							baseElPaddingW = parseInt(baseEl.getStyle('padding-left')) + parseInt(baseEl.getStyle('padding-right')),
							baseElPaddingH = parseInt(baseEl.getStyle('padding-top')) + parseInt(baseEl.getStyle('padding-bottom')),
							el = Ext.get(tableUuid);

						ns.viewport.centerRegion.setWidth(el.getWidth());
						ns.viewport.centerRegion.setHeight(el.getHeight());
						baseEl.setWidth(el.getWidth() + baseElBorderW + baseElPaddingW);
						baseEl.setHeight(el.getHeight() + baseElBorderH + baseElPaddingH);
					}
					else {
						if (NS.isSessionStorage) {
							setMouseHandlers();
							pivot.setSessionStorage('table', layout);
						}

						ns.viewport.setGui(layout, xLayout, updateGui, isFavorite);
					}

					// Hide mask
					web.mask.hide(ns.viewport.centerRegion);

					// Add uuid maps to instance
					ns.uuidDimUuidsMap = uuidDimUuidsMap;
					ns.uuidObjectMap = uuidObjectMap;

					// Add objects to instance
					ns.layout = layout;
					ns.xLayout = xLayout;
					ns.xResponse = xResponse;

					if (NS.isDebug) {
						console.log("xResponse", xResponse);
						console.log("xLayout", xLayout);
					}
				};

                initialize = function() {
                    var xLayout = service.layout.getExtendedLayout(layout),
                        paramString = web.analytics.getParamString(xLayout, true),
						method = 'GET',
						success;

					success = function(response) {
						var xResponse,
							chart,
							html,
							response = ns.api.response.Response(response);

						if (!response) {
							web.mask.mask.hide(ns.viewport.centerRegion);
							return;
						}

						// Synchronize xLayout
						xLayout = service.layout.getSyncronizedXLayout(xLayout, response);

						if (!xLayout) {
							web.mask.hide(ns.viewport.centerRegion);
							return;
						}

						// Extended response
						xResponse = service.response.getExtendedResponse(response, xLayout);

						// Extended axes
						xColAxis = service.layout.getExtendedAxis('col', xLayout.columnDimensionNames, xResponse);
						xRowAxis = service.layout.getExtendedAxis('row', xLayout.rowDimensionNames, xResponse);

						// Create html
						html = web.pivot.create(xColAxis, xRowAxis, xResponse);

						// Update viewport
						ns.viewport.centerRegion.removeAll(true);
						ns.viewport.centerRegion.update(html);

						ns.paramString = paramString;

						afterLoad(layout, xLayout, xResponse);
					};

					// Validate request size
                    if (!validateUrl(init.contextPath + '/api/analytics.jsonp' + paramString)) {
                        return;
                    }

					// Show load mask
                    web.mask.show(ns.viewport.centerRegion);

                    if (ns.isPlugin) {
						Ext.data.JsonP.request({
							method: method,
							url: init.contextPath + '/api/analytics.jsonp' + paramString,
							disableCaching: false,
							success: success
						});
					}
					else {
						Ext.Ajax.request({
							method: method,
							url: init.contextPath + '/api/analytics.json' + paramString,
							timeout: 60000,
							headers: {
								'Content-Type': 'application/json',
								'Accens': 'application/json'
							},
							disableCaching: false,
							failure: function(r) {
								web.mask.hide(ns.viewport.centerRegion);
								alert(r.responseText);
							},
							success: function(r) {
								success(Ext.decode(r.responseText));
							}
						});
					}
                }();
			};

			pivot.loadTable = function(id, ns, updateGui, isFavorite) {
				var url = init.contextPath + '/api/reportTables/' + id,
					params = '?viewClass=dimensional&links=false',
					method = 'GET',
					success,
					failure;

				if (!Ext.isString(id)) {
					alert('Invalid uid');
					return;
				}

				success = function(layoutConfig) {
					var layout = api.layout.Layout(layoutConfig);

					if (layout) {
						ns.favorite = Ext.clone(layout);
						ns.favorite.id = layoutConfig.id;
						ns.favorite.name = layoutConfig.name;

						pivot.createTable(layout, ns, updateGui, isFavorite);
					}
				};

				failure = function(responseText) {
					web.mask.hide(ns.viewport.centerRegion);
					alert(responseText);
				};

				if (ns.isPlugin) {
					Ext.data.JsonP.request({
						url: url + '.jsonp' + params,
						method: method,
						failure: function(r) {
							failure(r);
						},
						success: function(r) {
							success(r);
						}
					});
				}
				else {
					Ext.Ajax.request({
						url: url + '.json' + params,
						method: method,
						failure: function(r) {
							failure(r.responseText);
						},
						success: function(r) {
							success(Ext.decode(r.responseText));
						}
					});
				}
			};

			pivot.onMouseHover = function(uuid, event, param, ns) {
				var dimUuids;

				if (param === 'chart') {
					if (Ext.isString(uuid) && Ext.isArray(ns.uuidDimUuidsMap[uuid])) {
						dimUuids = ns.uuidDimUuidsMap[uuid];

						for (var i = 0, el; i < dimUuids.length; i++) {
							el = Ext.get(dimUuids[i]);

							if (el) {
								if (event === 'mouseover') {
									el.addCls('highlighted');
								}
								else if (event === 'mouseout') {
									el.removeCls('highlighted');
								}
							}
						}
					}
				}
			};

			pivot.onMouseClick = function(uuid, ns) {
				var that = this,
					uuids = ns.uuidDimUuidsMap[uuid],
					layoutConfig = Ext.clone(ns.layout),
					objects = [],
					parentGraphMap = ns.viewport.treePanel.getParentGraphMap(),
					menu;

				// modify layout dimension items based on uuid objects

				// get objects
				for (var i = 0; i < uuids.length; i++) {
					objects.push(ns.uuidObjectMap[uuids[i]]);
				}

				// clear layoutConfig dimension items
				for (var i = 0, a = [].concat(layoutConfig.columns || [], layoutConfig.rows || []); i < a.length; i++) {
					a[i].items = [];
				}

				// add new items
				for (var i = 0, obj, axis; i < objects.length; i++) {
					obj = objects[i];
					axis = obj.axis === 'col' ? layoutConfig.columns || [] : layoutConfig.rows || [];

					if (axis.length) {
						axis[obj.dim].items.push({
							id: obj.id,
							name: ns.xResponse.metaData.names[obj.id]
						});
					}
				}

				// parent graph map
				layoutConfig.parentGraphMap = {};

				for (var i = 0, id; i < objects.length; i++) {
					id = objects[i].id;

					if (parentGraphMap.hasOwnProperty(id)) {
						layoutConfig.parentGraphMap[id] = parentGraphMap[id];
					}
				}

				// menu

				menu = Ext.create('Ext.menu.Menu', {
					shadow: true,
					showSeparator: false,
					items: [
						{
							text: 'Open selection as chart' + '&nbsp;&nbsp;', //i18n
							iconCls: 'ns-button-icon-chart',
							param: 'chart',
							handler: function() {
								that.setSessionStorage('analytical', layoutConfig, ns.init.contextPath + '/dhis-web-visualizer/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										that.onMouseHover(uuid, 'mouseover', 'chart', ns);
									});

									this.getEl().on('mouseout', function() {
										that.onMouseHover(uuid, 'mouseout', 'chart', ns);
									});
								}
							}
						},
						{
							text: 'Open selection as map' + '&nbsp;&nbsp;', //i18n
							iconCls: 'ns-button-icon-map',
							param: 'map',
							disabled: true,
							handler: function() {
								that.setSessionStorage('analytical', layoutConfig, ns.init.contextPath + '/dhis-web-mapping/app/index.html?s=analytical');
							},
							listeners: {
								render: function() {
									this.getEl().on('mouseover', function() {
										that.onMouseHover(uuid, 'mouseover', 'map', ns);
									});

									this.getEl().on('mouseout', function() {
										that.onMouseHover(uuid, 'mouseout', 'map', ns);
									});
								}
							}
						}
					]
				});

				menu.showAt(function() {
					var el = Ext.get(uuid),
						xy = el.getXY();

					xy[0] += el.getWidth() - 5;
					xy[1] += el.getHeight() - 5;

					return xy;
				}());
			};
		}());

		// instance
		NS.instances.push({
			conf: conf,
			init: init,
			api: api,
			support: support,
			service: service,
			web: web
		});

        return NS.instances[NS.instances.length - 1];
	};
});
