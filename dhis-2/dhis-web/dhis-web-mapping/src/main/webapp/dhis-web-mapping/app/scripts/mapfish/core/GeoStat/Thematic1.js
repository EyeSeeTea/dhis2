/*
 * Copyright (C) 2007  Camptocamp
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
 * @requires core/GeoStat.js
 */

mapfish.GeoStat.Thematic1 = OpenLayers.Class(mapfish.GeoStat, {

    colors: [
        new mapfish.ColorRgb(120, 120, 0),
        new mapfish.ColorRgb(255, 0, 0)
    ],

    method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,

    numClasses: 5,

    bounds: null,

	minSize: 3,

	maxSize: 20,

	minVal: null,

	maxVal: null,

    defaultSymbolizer: {'fillOpacity': 1},

    classification: null,

    colorInterpolation: null,

    view: null,

    widget: null,

    initialize: function(map, options) {
        mapfish.GeoStat.prototype.initialize.apply(this, arguments);
    },

    loadOrganisationUnits: function(view) {
		Ext.Ajax.request({
			url: GIS.conf.url.path_gis + 'getGeoJson.action',
			params: {
				parentId: view.parentOrganisationUnit.id,
				level: view.organisationUnitLevel.id
			},
			scope: this,
			disableCaching: false,
			success: function(r) {
				var geojson = GIS.util.geojson.decode(r.responseText, this),
					format = new OpenLayers.Format.GeoJSON(),
					features = GIS.util.vector.getTransformedFeatureArray(format.read(geojson));

				if (!features.length) {
					alert('No valid coordinates found'); //todo //i18n
					GIS.mask.hide();
					return;
				}

				this.loadData(view, features);
			}
		});
    },

    loadData: function(view, features) {
		var type = view.valueType,
			dataUrl = 'mapValues/' + GIS.conf.finals.dimension[type].param + '.json',
			indicator = GIS.conf.finals.dimension.indicator,
			dataElement = GIS.conf.finals.dimension.dataElement,
			period = GIS.conf.finals.dimension.period,
			organisationUnit = GIS.conf.finals.dimension.organisationUnit,
			params = {};

		features = features || this.layer.features;

		params[type === indicator.id ? indicator.param : dataElement.param] = view[type].id;
		params[period.param] = view.period.id;
		params[organisationUnit.param] = view.parentOrganisationUnit.id;
		params.le = view.organisationUnitLevel.id;

		Ext.Ajax.request({
			url: GIS.conf.url.path_api + dataUrl,
			params: params,
			disableCaching: false,
			scope: this,
			success: function(r) {
				var values = Ext.decode(r.responseText),
					featureMap = {},
					valueMap = {},
					newFeatures = [];

				if (values.length === 0) {
					alert('No aggregated data values found'); //todo //i18n
					GIS.mask.hide();
					return;
				}

				for (var i = 0; i < features.length; i++) {
					var iid = features[i].attributes.internalId;
					featureMap[iid] = true;
				}
				for (var i = 0; i < values.length; i++) {
					var iid = values[i].organisationUnitId,
						value = values[i].value;
					valueMap[iid] = value;
				}

				for (var i = 0; i < features.length; i++) {
					var feature = features[i],
						iid = feature.attributes.internalId;
					if (featureMap.hasOwnProperty(iid) && valueMap.hasOwnProperty(iid)) {
						feature.attributes.value = valueMap[iid];
						feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
						newFeatures.push(feature);
					}
				}

				this.layer.removeFeatures(this.layer.features);
				this.layer.addFeatures(newFeatures);

				//if (this.tmpView.extended.updateOrganisationUnit) {
					//this.layer.features = GIS.util.vector.getTransformedFeatureArray(this.layer.features);
				//}

				this.features = this.layer.features.slice(0);

				this.loadLegend(view);
			}
		});
	},

	loadLegend: function(view) {
		var options,
			that = this,
			predefined = GIS.conf.finals.widget.legendtype_predefined,
			classificationType = mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS,
			method = view.legendType === predefined ? classificationType : view.method,
			bounds,
			legend,
			fn = function() {
				options = {
					indicator: GIS.conf.finals.widget.value,
					method: view.legendType === predefined ? mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS : view.method,
					numClasses: view.classes,
					bounds: bounds,
					colors: that.getColors(view.colorLow, view.colorHigh),
					minSize: view.radiusLow,
					maxSize: view.radiusHigh
				};

				that.applyClassification(options);
				that.widget.classificationApplied = true;

				that.afterLoad();
			};

		//this.tmpView.extended.legendConfig = {
			//what: this.tmpView.valueType === 'indicator' ? this.tmpView.indicator.name : this.tmpView.dataElement.name,
			//when: this.tmpView.period.id, //todo name
			//where: this.tmpView.organisationUnitLevel.name + ' / ' + this.tmpView.parentOrganisationUnit.name
		//};

		if (view.legendType === GIS.conf.finals.widget.legendtype_predefined) {
			legend = this.getPredefinedLegend(view);

			bounds = legend.bounds;
			this.colorInterpolation = legend.interpolation;
			view.legendSet.names = legend.names;
		}

		this.view = view;

		fn();
	},

	getPredefinedLegend: function(view) {
		var colors = [],
			bounds = [],
			names = [],
			legends;

		Ext.Ajax.request({
			url: GIS.conf.url.path_api + 'mapLegendSets/' + view.legendSet.id + '.json?links=false&paging=false',
			scope: this,
			success: function(r) {
				legends = Ext.decode(r.responseText).mapLegends;

				Ext.Array.sort(legends, function (a, b) {
					return a.startValue - b.startValue;
				});

				for (var i = 0; i < legends.length; i++) {
					if (bounds[bounds.length-1] !== legends[i].startValue) {
						if (bounds.length !== 0) {
							colors.push(new mapfish.ColorRgb(240,240,240));
                            names.push('');
						}
						bounds.push(legends[i].startValue);
					}
					colors.push(new mapfish.ColorRgb());
					colors[colors.length - 1].setFromHex(legends[i].color);
                    names.push(legends[i].name);
					bounds.push(legends[i].endValue);
				}

				return {
					interpolation: colors,
					bounds: bounds,
					names: names
				};
			}
		});
	},

	afterLoad: function() {
		this.widget.setGui(this.view);

		// Legend
		GIS.cmp.region.east.doLayout();
		this.layer.legend.expand();

        // Zoom to visible extent if not set by a favorite
        //if (GIS.map.mapViewLoader) {
			//GIS.map.mapViewLoader.callBack(this);
		//}
		//else {
			GIS.util.map.zoomToVisibleExtent();
		//}

        GIS.mask.hide();
	},

    updateOptions: function(newOptions) {
        var oldOptions = OpenLayers.Util.extend({}, this.options);
        this.addOptions(newOptions);
        if (newOptions) {
            this.setClassification();
        }
    },

    createColorInterpolation: function() {
        var numColors = this.classification.bins.length;

        if (this.view.legendType === GIS.conf.finals.widget.legendtype_automatic) {
			this.colorInterpolation = mapfish.ColorRgb.getColorsArrayByRgbInterpolation(this.colors[0], this.colors[1], numColors);
		}

		this.view.imageLegend = [];

        for (var i = 0; i < this.classification.bins.length; i++) {
			this.view.imageLegend.push({
                label: this.classification.bins[i].label.replace('&nbsp;&nbsp;', ' '),
                color: this.colorInterpolation[i].toHexString()
            });
        }
    },

    setClassification: function() {
        var values = [];
        for (var i = 0; i < this.layer.features.length; i++) {
            values.push(this.layer.features[i].attributes[this.indicator]);
        }

        var distOptions = {
            labelGenerator: this.options.labelGenerator
        };
        var dist = new mapfish.GeoStat.Distribution(values, distOptions);

		this.minVal = dist.minVal;
        this.maxVal = dist.maxVal;

        if (this.view.legendType === GIS.conf.finals.widget.legendtype_predefined) {
			if (this.bounds[0] > this.minVal) {
				this.bounds.unshift(this.minVal);
                //if (this.widget == centroid) { this.widget.symbolizerInterpolation.unshift('blank');
				this.colorInterpolation.unshift(new mapfish.ColorRgb(240,240,240));
			}

			if (this.bounds[this.bounds.length-1] < this.maxVal) {
				this.bounds.push(this.maxVal);
                //todo if (this.widget == centroid) { G.vars.activeWidget.symbolizerInterpolation.push('blank');
				this.colorInterpolation.push(new mapfish.ColorRgb(240,240,240));
			}
		}

        this.classification = dist.classify(
            this.method,
            this.numClasses,
            this.bounds
        );

        this.createColorInterpolation();
    },

    applyClassification: function(options, legend) {
        this.updateOptions(options, legend);

		var calculateRadius = OpenLayers.Function.bind(
			function(feature) {
				var value = feature.attributes[this.indicator];
                var size = (value - this.minVal) / (this.maxVal - this.minVal) *
					(this.maxSize - this.minSize) + this.minSize;
                return size || this.minSize;
            },	this
		);
		this.extendStyle(null, {'pointRadius': '${calculateRadius}'}, {'calculateRadius': calculateRadius});

        var boundsArray = this.classification.getBoundsArray();
        var rules = new Array(boundsArray.length - 1);
        for (var i = 0; i < boundsArray.length - 1; i++) {
            var rule = new OpenLayers.Rule({
                symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.BETWEEN,
                    property: this.indicator,
                    lowerBoundary: boundsArray[i],
                    upperBoundary: boundsArray[i + 1]
                })
            });
            rules[i] = rule;
        }

        this.extendStyle(rules);
        mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
    },

    updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }

		var	element,
			legendType = this.view.legendType,
			automatic = GIS.conf.finals.widget.legendtype_automatic,
			predefined = GIS.conf.finals.widget.legendtype_predefined,
			legendNames = this.view.legendSet.names,
			config = [
				this.view.valueType === GIS.conf.finals.dimension.indicator.id ? this.view.indicator.name : this.view.dataElement.name,
				this.view.period.id, //todo name
				this.view.organisationUnitLevel.name + ' / ' + this.view.parentOrganisationUnit.name
			];

        this.legendDiv.update("");

        for (var i = 0; i < config.length; i++) {
			element = document.createElement("div");
			element.style.height = "14px";
			element.style.overflow = "hidden";
			element.title = config[i];
			element.innerHTML = config[i];
			this.legendDiv.appendChild(element);

			element = document.createElement("div");
			element.style.clear = "left";
			this.legendDiv.appendChild(element);
		}

        element = document.createElement("div");
        element.style.width = "1px";
        element.style.height = "5px";
        this.legendDiv.appendChild(element);

        if (legendType === automatic) {
            for (var i = 0; i < this.classification.bins.length; i++) {
                var element = document.createElement("div");
                element.style.backgroundColor = this.colorInterpolation[i].toHexString();
                element.style.width = "30px";
                element.style.height = "15px";
                element.style.cssFloat = "left";
                element.style.marginRight = "8px";
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.innerHTML = this.classification.bins[i].label;
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.clear = "left";
                this.legendDiv.appendChild(element);
            }
        }
        else if (legendType === predefined) {
            for (var i = 0; i < this.classification.bins.length; i++) {
                var element = document.createElement("div");
                element.style.backgroundColor = this.colorInterpolation[i].toHexString();
                element.style.width = "30px";
                element.style.height = legendNames[i] ? "25px" : "20px";
                element.style.cssFloat = "left";
                element.style.marginRight = "8px";
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.lineHeight = legendNames[i] ? "12px" : "7px";
                element.innerHTML = '<b style="color:#222; font-size:10px !important">' + (legendNames[i] || '') + '</b><br/>' + this.classification.bins[i].label;
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.clear = "left";
                this.legendDiv.appendChild(element);
            }
        }
    },

    CLASS_NAME: "mapfish.GeoStat.Thematic1"
});
