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
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

/**
 * Class: mapfish.widgets.geostat.Choropleth
 * Use this class to create a widget allowing to display choropleths
 * on the map.
 *
 * Inherits from:
 * - {Ext.FormPanel}
 */

mapfish.widgets.geostat.Choropleth = Ext.extend(Ext.FormPanel, {

    /**
     * APIProperty: layer
     * {<OpenLayers.Layer.Vector>} The vector layer containing the features that
     *      are styled based on statistical values. If none is provided, one will
     *      be created.
     */
    layer: null,

    /**
     * APIProperty: format
     * {<OpenLayers.Format>} The OpenLayers format used to get features from
     *      the HTTP request response. GeoJSON is used if none is provided.
     */
    format: null,

    /**
     * APIProperty: url
     * {String} The URL to the web service. If none is provided, the features
     *      found in the provided vector layer will be used.
     */
    url: null,

    /**
     * APIProperty: featureSelection
     * {Boolean} A boolean value specifying whether feature selection must
     *      be put in place. If true a popup will be displayed when the
     *      mouse goes over a feature.
     */
    featureSelection: true,

    /**
     * APIProperty: nameAttribute
     * {String} The feature attribute that will be used as the popup title.
     *      Only applies if featureSelection is true.
     */
    nameAttribute: null,

    /**
     * APIProperty: indicator
     * {String} (read-only) The feature attribute currently chosen
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicator: null,

    /**
     * APIProperty: indicatorText
     * {String} (read-only) The raw value of the currently chosen indicator
     *     (ie. human readable)
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicatorText: null,

    /**
     * Property: coreComp
     * {<mapfish.GeoStat.ProportionalSymbol>} The core component object.
     */
    coreComp: null,

    /**
     * Property: classificationApplied
     * {Boolean} true if the classify was applied
     */
    classificationApplied: false,

    /**
     * Property: ready
     * {Boolean} true if the widget is ready to accept user commands.
     */
    ready: false,

    /**
     * Property: border
     *     Styling border
     */
    border: false,

    /**
     * APIProperty: loadMask
     *     An Ext.LoadMask config or true to mask the widget while loading (defaults to false).
     */
    loadMask : false,

    /**
     * APIProperty: labelGenerator
     *     Generator for bin labels
     */
    labelGenerator: null,

    /**
     * Constructor: mapfish.widgets.geostat.Choropleth
     *
     * Parameters:
     * config - {Object} Config object.
     */

    /**
     * Method: initComponent
     *    Inits the component
     */
     
    newUrl : false,
    
    initComponent : function() {
    
        indicatorGroupStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getAllIndicatorGroups.action',
            baseParams: { format: 'json' },
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            autoLoad: true
        });
        
        indicatorStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getIndicatorsByIndicatorGroup.action',
            root: 'indicators',
            fields: ['id', 'name'],
            autoLoad: false
        });
        
        periodTypeStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getAllPeriodTypes.action',
            baseParams: { format: 'json' },
            root: 'periodTypes',
            fields: ['id', 'name'],
            autoLoad: true
        });
            
        periodStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getPeriodsByPeriodType.action',
            baseParams: { periodTypeId: '9', format: 'json' },
            root: 'periods',
            fields: ['id', 'startDate'],
            autoLoad: true
        });
            
        mapStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getAllMaps.action',
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'mapLayerPath', 'organisationUnitLevel'],
            autoLoad: true
        }); 
            
        levelStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getOrganisationUnitLevels.action',
            baseParams: { format: 'json' },
            root: 'organisationUnitLevels',
            fields: ['level', 'name'],
            autoLoad: true
        });

        legendStore = new Ext.data.JsonStore({
            url: '/dhis-web-mapping/getLegendMinAndMaxOfIndicator.action',
            root: 'legendSet',
            fields: ['id', 'name'],
            autoLoad: false
        });

        this.items = [
         
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb',
            fieldLabel: 'Indicator group',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Select group',
            selectOnFocus: true,
            width: combo_width,
            store: indicatorGroupStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        Ext.getCmp('indicator_cb').reset();
                        var igId = Ext.getCmp('indicatorgroup_cb').getValue();
                        indicatorStore.baseParams = { indicatorGroupId: igId, format: 'json' };
                        indicatorStore.reload();
                    },
                    scope: this
                }
            }
        },     
        
        {
            xtype: 'combo',
            id: 'indicator_cb',
            fieldLabel: 'Indicator',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Select indicator',
            selectOnFocus: true,
            width: combo_width,
            store: indicatorStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        Ext.getCmp('legend_cb').reset();
                        
                        var iId = Ext.getCmp('indicator_cb').getValue();
                        legendStore.baseParams = { indicatorId: iId, format: 'json' };
                        legendStore.reload();
                        
                        this.classify(false);
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb',
            fieldLabel: 'Period type',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Select period type',
            selectOnFocus: true,
            width: combo_width,
            store: periodTypeStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        var ptid = Ext.getCmp('periodtype_cb').getValue();
                        periodStore.baseParams = { periodTypeId: ptid, format: 'json' };
                        periodStore.reload();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb',
            fieldLabel: 'Period',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'startDate',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Select period',
            selectOnFocus: true,
            width: combo_width,
            store: periodStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        this.classify(false);
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'map_cb',
            fieldLabel: 'Map',
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'mapLayerPath',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Required',
            selectOnFocus: true,
            width: combo_width,
            store: mapStore,
            listeners: {
                'select': {
                    fn: function() {
                        var mlp = Ext.getCmp('map_cb').getValue();
                                     
                        this.newUrl = mlp;
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            fieldLabel: 'Classes',
            id: 'numClasses',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: 5,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7], [8]]
            })
        },
            
        {
            xtype: 'combo',
            id: 'legend_cb',
            fieldLabel: 'Legend set',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Select legend',
            selectOnFocus: true,
            width: combo_width,
            store: legendStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        var iId = Ext.getCmp('indicator_cb').getValue();
                        var url = '/dhis-web-mapping/getLegendMinAndMaxOfIndicator.action';
                        var format = 'json';
                        
                        Ext.Ajax.request({
                          
                          url: url,
                          method: 'GET',
                          params: { indicatorId: iId, format: format },
                          
                          success: function( responseObject )
                          {
                              var data = Ext.util.JSON.decode(responseObject.responseText);
                              var color1 = "#" + data.legendSet[0]["min-color"];
                              var color2 = "#" + data.legendSet[0]["max-color"];

                              Ext.getCmp('colorA_cf').setValue(color1);
                              Ext.getCmp('colorB_cf').setValue(color2);
                          },
                          failure: function()
                          {
                              alert( 'Status', 'Error while retrieving data' );
                          } 
                        });
                        
                    },
                    scope: this
                }
            }

        },
        
        {
            xtype: 'colorfield',
            fieldLabel: 'Color',
            id: 'colorA_cf',
            width: 100,
            allowBlank: false,
            width: combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            fieldLabel: 'Color',
            id: 'colorB_cf',
            width: 100,
            allowBlank: false,
            width: combo_width,
            value: "#FF0000"
        },
        
        {
            xtype: 'button',
            text: 'Load map',
            handler: function()
            {
                this.layer.setVisibility(true);
                this.classify(true);
            },
            scope: this
        }
        
        ];

        mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
    
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    /**
     * Method: requestSuccess
     *      Calls onReady callback function and mark the widget as ready.
     *      Called on Ajax request success.
     */
    requestSuccess: function(request) {
        this.ready = true;
        this.classify(false);

        // if widget is rendered, hide the optional mask
        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    /**
     * Method: requestFailure
     *      Displays an error message on the console.
     *      Called on Ajax request failure.
     */
    requestFailure: function(request) {
        OpenLayers.Console.error('Ajax request failed');
    },

    /**
     * Method: getColors
     *    Retrieves the colors from form elements
     *
     * Returns:
     * {Array(<mapfish.Color>)} an array of two colors (start, end)
     */
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },

    /**
     * Method: classify
     *
     * Parameters:
     * exception - {Boolean} If true show a message box to user if either
     *      the widget isn't ready, or no indicator is specified, or no
     *      method is specified.
     */
    classify: function(exception)
    {
        if (!this.ready) {
            Ext.MessageBox.alert('Error', 'Component init not complete');
            return;
        }
        
        if (this.newUrl) {
            var url = this.newUrl;
            this.newUrl = false;
            this.setUrl('../../../geoserver/wfs?request=GetFeature&typename=' + url + '&outputformat=json&version=1.0.0');
        }
        
        if (!Ext.getCmp('indicator_cb').getValue() ||
            !Ext.getCmp('period_cb').getValue() ||
            !Ext.getCmp('map_cb').getValue()) {
                if (exception) {
                    Ext.MessageBox.alert('Error', 'You must choose indicator, period and level');
                }
                return;
        }
        
        loadMapData('choropleth');
        
        var options = {};
        
        // hidden
        this.indicator = 'value';
        this.indicatorText = 'Indicator';
        options.indicator = this.indicator;
        
        options.method = 1;
        options.numClasses = Ext.getCmp('numClasses').getValue();
        options.colors = this.getColors();
        
        this.coreComp.updateOptions(options);
        this.coreComp.applyClassification();
        this.classificationApplied = true;
    },

    /**
     * Method: onRender
     * Called by EXT when the component is rendered.
     */
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap,
                    this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        };

        this.coreComp = new mapfish.GeoStat.Choropleth(this.map, coreOptions);
    }   
});

Ext.reg('choropleth', mapfish.widgets.geostat.Choropleth);