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

mapfish.GeoStat.Symbol = OpenLayers.Class(mapfish.GeoStat, {

    classification: null,

    initialize: function(map, options) {
        mapfish.GeoStat.prototype.initialize.apply(this, arguments);
    },

    updateOptions: function(newOptions) {
        //var oldOptions = OpenLayers.Util.extend({}, this.options);
        this.addOptions(newOptions);
        //if (newOptions) {
            //this.setClassification();
        //}
    },

    applyClassification: function(options) {
        this.updateOptions(options);
console.log(document);        
    
        //var boundsArray = this.classification.getBoundsArray();
        
        //var boundsArray = ['Dispensary', 'Hospital', 'Private Medical Clinic'];
        var boundsArray = [24000, 24300, 24500];
        var imgLink = ['dispensary.png', 'clinic.png', 'hospital.png'];
        
        var rules = new Array(boundsArray.length);
        for (var i = 0; i < boundsArray.length; i++) {
            var rule = new OpenLayers.Rule({
                
                symbolizer: {
                    'pointRadius': 8,
                    'externalGraphic': imgLink[i]
                },
                
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

        this.legendDiv.update("");
        //for (var i = 0; i < this.classification.bins.length; i++) {
        for (var i = 0; i < 3; i++) {
            var element = document.createElement("div");
            element.style.backgroundColor = '#555';
            element.style.width = "30px";
            element.style.height = "15px";
            element.style.cssFloat = "left";
            element.style.marginRight = "10px";
            this.legendDiv.appendChild(element);

            element = document.createElement("div");
            element.innerHTML = 'jeje';
            this.legendDiv.appendChild(element);

            element = document.createElement("div");
            element.style.clear = "left";
            this.legendDiv.appendChild(element);
        }
    },

    CLASS_NAME: "mapfish.GeoStat.Symbol"
});
