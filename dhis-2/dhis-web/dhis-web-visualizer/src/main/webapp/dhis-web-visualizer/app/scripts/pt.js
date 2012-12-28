Ext.onReady( function() {

var pt = {};

pt.response = {
    "headers": [
		{
			"name": "de",
			"column": "de",
			"type": "java.lang.String",
			"hidden": false,
			"meta": true
		}, {
			"name": "coc",
			"column": "coc",
			"type": "java.lang.String",
			"hidden": false,
			"meta": true
		}, {
			"name": "pe",
			"column": "pe",
			"type": "java.lang.String",
			"hidden": false,
			"meta": true
		}, {
			"name": "ou",
			"column": "ou",
			"type": "java.lang.String",
			"hidden": false,
			"meta": true
		}, {
			"name": "J5jldMd8OHv",
			"column": "J5jldMd8OHv",
			"type": "java.lang.String",
			"hidden": false,
			"meta": true,
			//index: 4
			//items: ['ImspTQPwCqd']
			//size: 1
		}, {
			"name": "value",
			"column": "Value",
			"type": "java.lang.Double",
			"hidden": false,
			"meta": false
		}
    ],
    "width": 6,
    "height": 18,
    "rows": [
        ["YtbsuPPo010", "V6L425pT3A0", "201201", "ImspTQPwCqd", "uYxK4wmcPqA", "456.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201202", "ImspTQPwCqd", "uYxK4wmcPqA", "876.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201203", "ImspTQPwCqd", "uYxK4wmcPqA", "821.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201204", "ImspTQPwCqd", "uYxK4wmcPqA", "567.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201201", "Jdj9kdfn93n", "uYxK4wmcPqA", "674.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201202", "Jdj9kdfn93n", "uYxK4wmcPqA", "866.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201203", "Jdj9kdfn93n", "uYxK4wmcPqA", "334.0"],
        ["YtbsuPPo010", "V6L425pT3A0", "201204", "Jdj9kdfn93n", "uYxK4wmcPqA", "254.0"],

        ["Vdjeu38jejd", "V6L425pT3A0", "201201", "ImspTQPwCqd", "uYxK4wmcPqA", "294.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201202", "ImspTQPwCqd", "uYxK4wmcPqA", "663.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201203", "ImspTQPwCqd", "uYxK4wmcPqA", "374.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201204", "ImspTQPwCqd", "uYxK4wmcPqA", "765.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201201", "Jdj9kdfn93n", "uYxK4wmcPqA", "375.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201202", "Jdj9kdfn93n", "uYxK4wmcPqA", "287.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201203", "Jdj9kdfn93n", "uYxK4wmcPqA", "699.0"],
        ["Vdjeu38jejd", "V6L425pT3A0", "201204", "Jdj9kdfn93n", "uYxK4wmcPqA", "883.0"],

        ["Wdsd99jdmmf", "V6L425pT3A0", "201201", "ImspTQPwCqd", "uYxK4wmcPqA", "475.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201202", "ImspTQPwCqd", "uYxK4wmcPqA", "264.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201203", "ImspTQPwCqd", "uYxK4wmcPqA", "233.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201204", "ImspTQPwCqd", "uYxK4wmcPqA", "445.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201201", "Jdj9kdfn93n", "uYxK4wmcPqA", "788.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201202", "Jdj9kdfn93n", "uYxK4wmcPqA", "854.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201203", "Jdj9kdfn93n", "uYxK4wmcPqA", "732.0"],
        ["Wdsd99jdmmf", "V6L425pT3A0", "201204", "Jdj9kdfn93n", "uYxK4wmcPqA", "726.0"]
    ],
    "metaData": {
        "YtbsuPPo010": "ANC 1 visit",
        "Vdjeu38jejd": "ANC 2 visit",
        "Wdsd99jdmmf": "ANC 3 visit",
        "V6L425pT3A0": "Default",
        "201201": "Jan 2012",
        "201202": "Feb 2012",
        "201203": "Mar 2012",
        "201204": "Apr 2012",
        "ImspTQPwCqd": "Kailahun CHPC",
        "Jdj9kdfn93n": "Bo CHPC",
        "uYxK4wmcPqA": "Public ownership"
    },
    //"nameHeaderMap": {
	//	"de": <header.de>
	//}
};

pt.settings = {
	col: ['de', 'pe'],
	row: ['ou']
};

var extendResponse = function(pt) {
	var response = pt.response,
		settings = pt.settings,
		headers = response.headers,
		header,
		rows = response.rows,
		settingsDims = settings.col.concat(settings.row),
		items;

	response.nameHeaderMap = {};
	response.idValueMap = {};

	for (var i = 0; i < headers.length; i++) {
		header = headers[i];
		header.index = i;
		items = [];

		for (var j = 0; j < rows.length; j++) {
			items.push(rows[j][header.index]);
		}

		header.items = Ext.Array.unique(items);
		header.size = header.items.length;

		response.nameHeaderMap[header.name] = header;
	}

	for (var i = 0, id, valueIndex = response.nameHeaderMap.value.index; i < rows.length; i++) {
		id = '';

		for (var j = 0, dimIndex; j < settingsDims.length; j++) {
			dimIndex = response.nameHeaderMap[settingsDims[j]].index;
			id += rows[i][dimIndex];
		}

		response.idValueMap[id] = rows[i][valueIndex];
	}
};

var extendDims = function(aUniqueCols) {

	//aUniqueCols	= [ [de1, de2, de3], [p1, p2, p3, p4, p5], [ou1, ou2] ]
	//aUniqueCols	= [ [de1, de2, de3], [p1], [ou1, ou2, ou3, ou4] ]

	var nCols = 1,
		aNumCols = [],
		aAccNumCols = [],
		aSpan = [],
		aItems = [];

	for (var i = 0, dim; i < aUniqueCols.length; i++) {
		nNumCols = aUniqueCols[i].length;

		aNumCols.push(nNumCols);
		nCols = nCols * nNumCols;
		aAccNumCols.push(nCols);
	}

console.log("");
console.log("aNumCols", aNumCols);
console.log("nCols", nCols);
console.log("aAccNumCols", aAccNumCols);

	//aNumCols		= [3, 1, 4]
	//nCols			= 12 (3 * 1 * 4)
	//aAccNumCols	= [3, 3, 12]

	for (var i = 0; i < aUniqueCols.length; i++) {
		aSpan.push(aNumCols[i] === 1 ? nCols : nCols / aAccNumCols[i]); //if only one, span all
	}

console.log("aSpan", aSpan);

	//aSpan		= [10, 2, 1]

	aItems.push(aUniqueCols[0]);

	if (aUniqueCols.length > 1) {
		for (var i = 1, a, n; i < aUniqueCols.length; i++) {
			a = [];
			n = aNumCols[i] === 1 ? 1 : aAccNumCols[i-1];

			for (var j = 0; j < n; j++) {
				a = a.concat(aUniqueCols[i]);
			}

			aItems.push(a);
		}
	}

console.log("aItems", aItems);

	//aItems	= [ [d1, d2, d3], (3)
	//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
	//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
	//		  	  ]

console.log("");
	return {
		items: aItems,
		span: aSpan,
		dims: aItems.length,
		size: nCols
	};
};

var getDims = function(pt) {
	var response = pt.response,
		settings = pt.settings,
		getUniqueColsArray,
		getUniqueRowsArray;

	getUniqueColsArray = function(response, settings) {
		var a = [];

		for (var i = 0, header; i < settings.col.length; i++) {
			header = settings.col[i];
			a.push(response.nameHeaderMap[header].items);
		}

		return a;
	};

	getUniqueRowsArray = function(response, settings) {
		var a = [];

		for (var i = 0, header; i < settings.row.length; i++) {
			header = settings.row[i];
			a.push(response.nameHeaderMap[header].items);
		}

		return a;
	};

	// aUniqueCols ->  [[p1, p2, p3], [ou1, ou2, ou3, ou4]]

	return {
		cols: extendDims(getUniqueColsArray(response, settings)),
		rows: extendDims(getUniqueRowsArray(response, settings))
	};
};

var getTablePanel = function(pt) {
	return Ext.create('Ext.panel.Panel', {
		renderTo: Ext.get('pivottable'),
		layout: {
			type: 'table',
			columns: pt.config.cols.size + pt.config.rows.dims
		},
		defaults: {
			baseCls: 'td'
		}
	});
};

var getEmptyItem = function(pt) {
	return {
		html: 'empty',
		colspan: pt.config.rows.dims,
		rowspan: pt.config.cols.dims,
		baseCls: 'empty'
	};
};

var getColItems = function(pt) {
	var response = pt.response,
		cols = pt.config.cols,
		colItems = [];

	for (var i = 0, dimItems, colSpan; i < cols.dims; i++) {
		dimItems = cols.items[i];
		colSpan = cols.span[i];

		for (var j = 0, id; j < dimItems.length; j++) {
			id = dimItems[j];
			colItems.push({
				html: response.metaData[id],
				colspan: colSpan,
				baseCls: 'td-dim'
			});
		}
	}

	return colItems;
};

var getRowItems = function(pt) {
	var rows = pt.config.rows;

	//for (var i = 0; i < rows.size; i++) {



};

var createTableArray = function(pt) {
	var panel = getTablePanel(pt);

	panel.add(getEmptyItem(pt));

	panel.add(getColItems(pt));
};


var initialize = function() {
	extendResponse(pt, pt);

	pt.config = getDims(pt, pt);
	console.log(pt);

	var panel = createTableArray(pt);

	//generate();


}();

});
