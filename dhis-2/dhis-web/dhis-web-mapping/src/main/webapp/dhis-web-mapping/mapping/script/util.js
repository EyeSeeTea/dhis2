/* Detect mapview parameter in URL */
function getUrlParam(strParamName) {
    var output = '';
    var strHref = window.location.href;
    if (strHref.indexOf('?') > -1 ) {
        var strQueryString = strHref.substr(strHref.indexOf('?')).toLowerCase();
        var aQueryString = strQueryString.split('&');
        for (var iParam = 0; iParam < aQueryString.length; iParam++) {
            if (aQueryString[iParam].indexOf(strParamName.toLowerCase() + '=') > -1) {
                var aParam = aQueryString[iParam].split('=');
                output  =aParam[1];
                break;
            }
        }
    }
    return unescape(output);
}

/* Get all properties in an object */
function getKeys(obj) {
    var temp = [];
    for (var k in obj) {
        if (obj.hasOwnProperty(k)) {
            temp.push(k);
        }
    }
    return temp;
}

/* Input validation */
function validateInputNameLength(name) {
    return (name.length <= 25);
}

/* Decide multiselect height based on screen resolution */
function getMultiSelectHeight() {
    var h = screen.height;
    return h <= 800 ? 220 :
        h <= 1050 ? 310 :
            h <= 1200 ? 470 : 900;
}

/* Make map view numbers numeric */
function getNumericMapView(mapView) {
    mapView.id = parseFloat(mapView.id);
    mapView.indicatorGroupId = parseFloat(mapView.indicatorGroupId);
    mapView.indicatorId = parseFloat(mapView.indicatorId);
    mapView.periodId = parseFloat(mapView.periodId);
    mapView.method = parseFloat(mapView.method);
    mapView.classes = parseFloat(mapView.classes);
    mapView.mapLegendSetId = parseFloat(mapView.mapLegendSetId);
    mapView.longitude = parseFloat(mapView.longitude);
    mapView.latitude = parseFloat(mapView.latitude);
    mapView.zoom = parseFloat(mapView.zoom);
    return mapView;
}

/* Get number of decimals */
function getNumberOfDecimals(x,dec_sep) {
    var tmp = new String();
    tmp = x;
    return tmp.indexOf(dec_sep) > -1 ? tmp.length-tmp.indexOf(dec_sep) - 1 : 0;
}

/* Toggle feature labels */
function getActivatedOpenLayersStyleMap() {
    return new OpenLayers.StyleMap({
        'default' : new OpenLayers.Style(
            OpenLayers.Util.applyDefaults({
                'fillOpacity': 1,
                'strokeColor': '#222222',
                'strokeWidth': 1,
                'label': '${labelString}',
                'fontFamily': 'arial,lucida sans unicode',
                'fontWeight': 'bold',
                'fontSize': 14
            },
            OpenLayers.Feature.Vector.style['default'])
        ),
        'select': new OpenLayers.Style({
            'strokeColor': '#000000',
            'strokeWidth': 2,
            'cursor': 'pointer'
        })
    });
}

function getDeactivatedOpenLayersStyleMap() {
    return new OpenLayers.StyleMap({
        'default': new OpenLayers.Style(
            OpenLayers.Util.applyDefaults({
                'fillOpacity': 1,
                'strokeColor': '#222222',
                'strokeWidth': 1
            },
            OpenLayers.Feature.Vector.style['default'])
        ),
        'select': new OpenLayers.Style({
            'strokeColor': '#000000',
            'strokeWidth': 2,
            'cursor': 'pointer'
        })
    });
}

function toggleFeatureLabelsPolygons(layer) {
    function activateLabels() {
        layer.styleMap = getActivatedOpenLayersStyleMap();
        LABELS[thematicMap] = true;
    }
    function deactivateLabels() {
        layer.styleMap = getDeactivatedOpenLayersStyleMap();
        LABELS[thematicMap] = false;
    }
    
    if (LABELS[thematicMap]) {
        deactivateLabels();
    }
    else {
        activateLabels();
    }
    
    FEATURE[thematicMap] = layer.features;
    choropleth.applyValues();
}

function toggleFeatureLabelsPoints(layer) {
    function activateLabels() {
        layer.styleMap = getActivatedOpenLayersStyleMap(MAPDATA[thematicMap2].nameColumn);
        LABELS[thematicMap2] = true;
    }
    function deactivateLabels() {
        layer.styleMap = getDeactivatedOpenLayersStyleMap();
        LABELS[thematicMap2] = false;
    }
    
    if (LABELS[thematicMap2]) {
        deactivateLabels();
    }
    else {
        activateLabels();
    }
    
    FEATURE[thematicMap2] = layer.features;
    proportionalSymbol.applyValues();
}

function toggleFeatureLabelsAssignment(classify, layer) {
    function activateLabels() {
        layer.styleMap = getActivatedOpenLayersStyleMap(MAPDATA[organisationUnitAssignment].nameColumn);
        LABELS[organisationUnitAssignment] = true;
    }
    function deactivateLabels() {
        layer.styleMap = getDeactivatedOpenLayersStyleMap();
        LABELS[organisationUnitAssignment] = false;
    }
    
    if (classify) {
        if (LABELS[organisationUnitAssignment]) {
            deactivateLabels();
        }
        else {
            activateLabels();
        }
        mapping.classify(false,true);
    }
    else {
        if (LABELS[organisationUnitAssignment]) {
            activateLabels();
        }
    }
}

/* Sort values */
function sortByValue(a,b) {
    return b.value-a.value;
}

/* Create JSON for map export */
function getExportDataValueJSON(mapvalues) {
    var json = '{';
    json += '"datavalues": ';
    json += '[';
    mapvalues.sort(sortByValue);
    for (var i = 0; i < mapvalues.length; i++) {
        json += '{';
        json += '"organisation": "' + mapvalues[i].orgUnitId + '",';
        json += '"value": "' + mapvalues[i].value + '" ';
        json += i < mapvalues.length - 1 ? '},' : '}'
    }
    json += ']';
    json += '}';
    return json
}

function getLegendsJSON(){
    var widget = ACTIVEPANEL == thematicMap ? choropleth : proportionalSymbol;
    var json = '{';
	json += '"legends":';
	json += '[';
	for(var i = 0; i < widget.imageLegend.length; i++) {
		json += '{';
		json += '"label": "' + widget.imageLegend[i].label + '",';
		json += '"color": "' + widget.imageLegend[i].color + '" ';
		json += i < widget.imageLegend.length-1 ? '},' : '}';
	}
	json += ']';
	json += '}';
	return json;
}