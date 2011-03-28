/*
 * Copyright (C) 2007-2008  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires OpenLayers/Layer/Vector.js
 * @requires OpenLayers/Popup/AnchoredBubble.js
 * @requires OpenLayers/Feature/Vector.js
 * @requires OpenLayers/Format/GeoJSON.js
 * @requires OpenLayers/Control/SelectFeature.js
 * @requires OpenLayers/Ajax.js
 */

mapfish.GeoStat = OpenLayers.Class({

    layer: null,

    format: null,

    url: null,

    requestSuccess: function(request) {},

    requestFailure: function(request) {},

    featureSelection: true,

    nameAttribute: null,

    indicator: null,

    defaultSymbolizer: {},

    selectSymbolizer: {'strokeColor': '#000000'},

    legendDiv: null,

    initialize: function(map, options) {
        this.map = map;
        this.addOptions(options);
        if (!this.layer) {
            var styleMap = new OpenLayers.StyleMap({
                'default': new OpenLayers.Style(
                    OpenLayers.Util.applyDefaults(
                        this.defaultSymbolizer,
                        OpenLayers.Feature.Vector.style['default']
                    )
                ),
                'select': new OpenLayers.Style(this.selectSymbolizer)
            });
            var layer = new OpenLayers.Layer.Vector('geostat', {
                'displayInLayerSwitcher': false,
                'visibility': false,
                'styleMap': styleMap
            });
            map.addLayer(layer);
            this.layer = layer;
        }

        this.setUrl(this.url);
        this.legendDiv = Ext.get(options.legendDiv);
    },
 
    setUrl: function(url, params) {
        this.url = url;
        if (this.url) {
            OpenLayers.loadURL(this.url, '', this, this.onSuccess, this.onFailure);
        }
    },

    onSuccess: function(request) {
        var doc = request.responseXML;
        if (!doc || !doc.documentElement) {
            doc = request.responseText;
        }
        var format = this.format || new OpenLayers.Format.GeoJSON();
        this.layer.removeFeatures(this.layer.features);
        this.layer.addFeatures(format.read(doc));
		this.layer.features = G.util.getTransformedFeatureArray(this.layer.features);
        this.requestSuccess(request);

        if (G.vars.activePanel.isPolygon()) {
            if (!choropleth.formValidation.validateForm.call(choropleth)) {
                G.vars.mask.hide();
            }
            choropleth.classify(false);
        }
        else if (G.vars.activePanel.isPoint()) {
            if (!symbol.formValidation.validateForm.call(symbol)) {
                G.vars.mask.hide();
            }
            symbol.classify(false);
        }
    },

    onFailure: function(request) {
        this.requestFailure(request);
    },

    addOptions: function(newOptions) {
        if (newOptions) {
            if (!this.options) {
                this.options = {};
            }
            // update our copy for clone
            OpenLayers.Util.extend(this.options, newOptions);
            // add new options to this
            OpenLayers.Util.extend(this, newOptions);
        }
    },

    extendStyle: function(rules, symbolizer, context) {
        var style = this.layer.styleMap.styles['default'];
        if (rules) {
            style.rules = rules;
        }
        if (symbolizer) {
            style.setDefaultStyle(
                OpenLayers.Util.applyDefaults(
                    symbolizer,
                    style.defaultStyle
                )
            );
        }
        if (context) {
            if (!style.context) {
                style.context = {};
            }
            OpenLayers.Util.extend(style.context, context);
        }
    },

    applyClassification: function(options) {
        this.layer.renderer.clear();
        this.layer.redraw();
        this.updateLegend();
        this.layer.setVisibility(true);
    },

    showDetails: function(obj) {},

    hideDetails: function(obj) {},

    CLASS_NAME: "mapfish.GeoStat"
});

mapfish.GeoStat.Distribution = OpenLayers.Class({

    labelGenerator: function(bin, binIndex, nbBins, maxDec) {
        return this.defaultLabelGenerator(bin, binIndex, nbBins, maxDec);
    },

    values: null,

    nbVal: null,

    minVal: null,

    maxVal: null,

    initialize: function(values, options) {
        OpenLayers.Util.extend(this, options);
        this.values = values;
        this.nbVal = values.length;
        this.minVal = this.nbVal ? mapfish.Util.min(this.values) : 0;
        this.maxVal = this.nbVal ? mapfish.Util.max(this.values) : 0;
    },

    defaultLabelGenerator: function(bin, binIndex, nbBins, maxDec) {
        lower = parseFloat(bin.lowerBound).toFixed(maxDec);
        upper = parseFloat(bin.upperBound).toFixed(maxDec);
        return lower + ' - ' + upper + '&nbsp;&nbsp; ( ' + bin.nbVal + ' )';
    },

    classifyWithBounds: function(bounds) {
        var bins = [];
        var binCount = [];
        var sortedValues = [];
        for (var i = 0; i < this.values.length; i++) {
            sortedValues.push(this.values[i]);
        }
        sortedValues.sort(function(a,b) {return a-b;});
        var nbBins = bounds.length - 1;

        for (var j = 0; j < nbBins; j++) {
            binCount[j] = 0;
        }

        for (var k = 0; k < nbBins - 1; k) {
            if (sortedValues[0] < bounds[k + 1]) {
                binCount[k] = binCount[k] + 1;
                sortedValues.shift();
            } else {
                k++;
            }
        }

        binCount[nbBins - 1] = this.nbVal - mapfish.Util.sum(binCount);
		
		var imageLegend = [];
        var maxDec = 0;
        
        for (var l = 0; l < bounds.length; l++) {
            var dec = G.util.getNumberOfDecimals(bounds[l].toString(), ".");
            maxDec = dec > maxDec ? dec : maxDec;
        }
        
        maxDec = maxDec > 3 ? 3 : maxDec;
		
        for (var m = 0; m < nbBins; m++) {
            bins[m] = new mapfish.GeoStat.Bin(binCount[m], bounds[m], bounds[m + 1], m == (nbBins - 1));
            var labelGenerator = this.labelGenerator || this.defaultLabelGenerator;
            bins[m].label = labelGenerator(bins[m], m, nbBins, maxDec);
			imageLegend[m] = {};
			imageLegend[m].label = bins[m].label.replace('&nbsp;&nbsp;', ' ');
        }
        
        if (G.vars.activePanel.isPolygon()) {
            choropleth.imageLegend = imageLegend;
        }
        else if (G.vars.activePanel.isPoint()) {
            symbol.imageLegend = imageLegend;
        }
        
        return new mapfish.GeoStat.Classification(bins);
    },

    classifyByEqIntervals: function(nbBins) {
        var bounds = [];

        for (var i = 0; i <= nbBins; i++) {
            bounds[i] = this.minVal + i*(this.maxVal - this.minVal) / nbBins;
        }

        return this.classifyWithBounds(bounds);
    },

    classifyByQuantils: function(nbBins) {
        var values = this.values;
        values.sort(function(a,b) {return a-b;});
        var binSize = Math.round(this.values.length / nbBins);

        var bounds = [];
        var binLastValPos = (binSize === 0) ? 0 : binSize;

        if (values.length > 0) {
            bounds[0] = values[0];
            for (i = 1; i < nbBins; i++) {
                bounds[i] = values[binLastValPos];
                binLastValPos += binSize;
            }
            bounds.push(values[values.length - 1]);
        }
        
        for (var j = 0; j < bounds.length; j++) {
            bounds[j] = parseFloat(bounds[j]);
        }

        return this.classifyWithBounds(bounds);
    },

    sturgesRule: function() {
        return Math.floor(1 + 3.3 * Math.log(this.nbVal, 10));
    },
	
    classify: function(method, nbBins, bounds) {
        var mlt = G.vars.activePanel.isPolygon() ?
            choropleth.legend.value : G.vars.activePanel.isPoint() ?
                symbol.legend.value : G.conf.map_legend_type_automatic;
    
		if (mlt == G.conf.map_legend_type_automatic) {
			if (method == mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS) {
				var str = G.vars.activePanel.isPolygon() ? choropleth.form.findField('bounds').getValue() : symbol.form.findField('bounds').getValue();
				
				for (var i = 0; i < str.length; i++) {
					str = str.replace(' ','');
				}
				
				if (str.charAt(str.length-1) == ',') {
					str = str.substring(0, str.length-1);
				}
				
				bounds = [];
				bounds = str.split(',');
				
				for (var j = 0; j < bounds.length; j++) {
					if (!Ext.num(parseFloat(bounds[j]), false)) {
						bounds.remove(bounds[j]);
						j--;
					}
				}
				
				var newInput = bounds.join(',');
                
                if (G.vars.activePanel.isPolygon()) {
                    choropleth.form.findField('bounds').setValue(newInput);
                }
                else {
                    symbol.form.findField('bounds').setValue(newInput);
                }
				
				for (var k = 0; k < bounds.length; k++) {
					bounds[k] = parseFloat(bounds[k]);
					if (bounds[k] < this.minVal || bounds[k] > this.maxVal) {
						Ext.message.msg(false, 'Class breaks must be higher than <span class="x-msg-hl">' + this.minVal + '</span> and lower than <span class="x-msg-hl">' + this.maxVal + '</span>.');
					}
				}
				
				bounds.unshift(this.minVal);
				bounds.push(this.maxVal);
			}
		}
		else if (mlt == G.conf.map_legend_type_predefined) {
			bounds = G.vars.activePanel.isPolygon() ? choropleth.bounds : symbol.bounds;

			if (bounds[0] > this.minVal) {
				bounds.unshift(this.minVal);
                if (G.vars.activePanel.isPolygon()) {
                    choropleth.colorInterpolation.unshift(new mapfish.ColorRgb(240,240,240));
                }
                else {
                    symbol.colorInterpolation.unshift(new mapfish.ColorRgb(240,240,240));
                }
			}

			if (bounds[bounds.length-1] < this.maxVal) {
				bounds.push(this.maxVal);
                if (G.vars.activePanel.isPolygon()) {
                    choropleth.colorInterpolation.push(new mapfish.ColorRgb(240,240,240));
                }
                else {
                    symbol.colorInterpolation.push(new mapfish.ColorRgb(240,240,240));
                }
			}
			
			method = mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS;
		}
        
        var classification = null;
        if (!nbBins) {
            nbBins = this.sturgesRule();
        }

        switch (parseFloat(method)) {
        case mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS :
            classification = this.classifyWithBounds(bounds);
            break;
        case mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS :
            classification = this.classifyByEqIntervals(nbBins);
            break;
        case mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS :
            classification = this.classifyByQuantils(nbBins);
            break;
        default:
            OpenLayers.Console.error("Unsupported or invalid classification method");
        }
        return classification;
    },

    CLASS_NAME: "mapfish.GeoStat.Distribution"
});

mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS = 1;

mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS = 2;

mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS = 3;

mapfish.GeoStat.Bin = OpenLayers.Class({
    label: null,
    nbVal: null,
    lowerBound: null,
    upperBound: null,
    isLast: false,

    initialize: function(nbVal, lowerBound, upperBound, isLast) {
        this.nbVal = nbVal;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.isLast = isLast;
    },

    CLASS_NAME: "mapfish.GeoStat.Bin"
});

mapfish.GeoStat.Classification = OpenLayers.Class({
    bins: [],

    initialize: function(bins) {
        this.bins = bins;
    },

    getBoundsArray: function() {
        var bounds = [];
        for (var i = 0; i < this.bins.length; i++) {
            bounds.push(this.bins[i].lowerBound);
        }
        if (this.bins.length > 0) {
            bounds.push(this.bins[this.bins.length - 1].upperBound);
        }
        return bounds;
    },

    CLASS_NAME: "mapfish.GeoStat.Classification"
});
