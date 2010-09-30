﻿/*
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
 * @requires core/GeoStat/Symbol.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Symbol = Ext.extend(Ext.FormPanel, {

    layer: null,

    format: null,

    url: null,

    featureSelection: true,

    nameAttribute: null,

    indicator: null,

    indicatorText: null,

    coreComp: null,

    classificationApplied: false,

    ready: false,

    border: false,

    loadMask: false,

    labelGenerator: null,

    colorInterpolation: false,

    newUrl: false,

    legend: false,

	imageLegend: false,

	bounds: false,

    parentId: false,

    mapView: false,

    mapData: false,
    
    labels: false,

    initComponent: function() {
        this.legend = {};
        this.legend.type = map_legend_type_automatic;
        this.legend.method = 2;
        this.legend.classes = 5;
        
        this.mapData = {};
    
        mapViewStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMapViews' + type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: {field: 'name', direction: 'ASC'},
            autoLoad: true
        });
    
        indicatorGroupStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllIndicatorGroups' + type,
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
        
        indicatorStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getIndicatorsByIndicatorGroup' + type,
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    scope: this,
                    fn: function() {
                        indicatorStore2.each(
                            function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            }
                        );
                        
                        Ext.getCmp('indicator_cb2').clearValue();

                        if (this.mapView) {
                            Ext.getCmp('indicator_cb2').setValue(this.mapView.indicatorId);

                            if (this.mapView.mapDateType == map_date_type_fixed) {
                                Ext.getCmp('periodtype_cb2').showField();
                                Ext.getCmp('period_cb2').showField();
                                Ext.getCmp('startdate_df2').hideField();
                                Ext.getCmp('enddate_df2').hideField();
                                
                                Ext.getCmp('periodtype_cb2').setValue(this.mapView.periodTypeId);
                                periodStore2.setBaseParam('name', this.mapView.periodTypeId);
                                periodStore2.load();
                            }
                            else if (this.mapView.mapDateType == map_date_type_start_end) {
                                Ext.getCmp('periodtype_cb2').hideField();
                                Ext.getCmp('period_cb2').hideField();
                                Ext.getCmp('startdate_df2').showField();
                                Ext.getCmp('enddate_df2').showField();

                                Ext.getCmp('startdate_df2').setValue(new Date(this.mapView.startDate));
                                Ext.getCmp('enddate_df2').setValue(new Date(this.mapView.endDate));
                                
                                if (MAPSOURCE == map_source_type_database) {
                                    Ext.Ajax.request({
                                        url: path_commons + 'getOrganisationUnit' + type,
                                        method: 'POST',
                                        params: {id:this.mapView.mapSource},
                                        scope: this,
                                        success: function(r) {
                                            var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                            Ext.getCmp('map_tf2').setValue(name);
                                            Ext.getCmp('map_tf2').value = this.mapView.mapSource;
                                            this.loadFromDatabase(this.mapView.mapSource);
                                        },
                                        failure: function() {
                                            alert('Error: getOrganisationUnit');
                                        }
                                    });
                                }
                                else {
                                    Ext.getCmp('map_cb2').setValue(this.mapView.mapSource);
                                    this.loadFromFile(this.mapView.mapSource);
                                }
                            }
                        }
                    }
                }
            }
        });
		
		dataElementGroupStore2 = new Ext.data.JsonStore({
			url: path_mapping + 'getAllDataElementGroups' + type,
            root: 'dataElementGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
		
		dataElementStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getDataElementsByDataElementGroup' + type,
            root: 'dataElements',
            fields: ['id', 'name', 'shortName'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    scope: this,
                    fn: function() {
                        dataElementStore2.each(
                            function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            }
                        );
                        
                        Ext.getCmp('dataelement_cb2').clearValue();

                        if (this.mapView) {
                            Ext.getCmp('dataelement_cb2').setValue(this.mapView.dataElementId);
                            
                            if (this.mapView.mapDateType == map_date_type_fixed) {
                                Ext.getCmp('periodtype_cb2').showField();
                                Ext.getCmp('period_cb2').showField();
                                Ext.getCmp('startdate_df2').hideField();
                                Ext.getCmp('enddate_df2').hideField();
                                
                                Ext.getCmp('periodtype_cb2').setValue(this.mapView.periodTypeId);
                                periodStore2.setBaseParam('name', this.mapView.periodTypeId);
                                periodStore2.load();
                            }
                            else if (this.mapView.mapDateType == map_date_type_start_end) {
                                Ext.getCmp('periodtype_cb2').hideField();
                                Ext.getCmp('period_cb2').hideField();
                                Ext.getCmp('startdate_df2').showField();
                                Ext.getCmp('enddate_df2').showField();
                                
                                Ext.getCmp('startdate_df2').setValue(new Date(this.mapView.startDate));
                                Ext.getCmp('enddate_df2').setValue(new Date(this.mapView.endDate));
                                
                                if (MAPSOURCE == map_source_type_database) {
                                    Ext.Ajax.request({
                                        url: path_commons + 'getOrganisationUnit' + type,
                                        method: 'POST',
                                        params: {id:this.mapView.mapSource},
                                        scope: this,
                                        success: function(r) {
                                            var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                            Ext.getCmp('map_tf2').setValue(name);
                                            Ext.getCmp('map_tf2').value = this.mapView.mapSource;
                                            this.loadFromDatabase(this.mapView.mapSource);
                                        },
                                        failure: function() {
                                            alert('Error: getOrganisationUnit');
                                        }
                                    });
                                }
                                else {
                                    Ext.getCmp('map_cb2').setValue(this.mapView.mapSource);
                                    this.loadFromFile(this.mapView.mapSource);
                                }
                            }
                        }
                    }
                }
            }
        });
        
        periodTypeStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllPeriodTypes' + type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: true
        });
            
        periodStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getPeriodsByPeriodType' + type,
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            listeners: {
                'load': {
                    scope: this,
                    fn: function() {
                        if (this.mapView) {
                            Ext.getCmp('period_cb2').setValue(this.mapView.periodId);

                            Ext.Ajax.request({
                                url: path_mapping + 'setMapUserSettings' + type,
                                method: 'POST',
                                params: {mapSourceType: this.mapView.mapSourceType, mapDateType: MAPDATETYPE },
								success: function(r) {
                                    Ext.getCmp('map_cb2').getStore().load();
                                    Ext.getCmp('maps_cb').getStore().load();
                                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                },
                                failure: function() {
                                    alert( 'Error: setMapSourceTypeUserSetting' );
                                }
                            });
                        }
                    }
                }
            }
        });
            
        mapStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            idProperty: 'mapLayerPath',
            autoLoad: true,
            listeners: {
                'load': {
                    scope: this,
                    fn: function() {
                        if (this.mapView) {
                            if (MAPSOURCE == map_source_type_database) {
                                Ext.Ajax.request({
                                    url: path_commons + 'getOrganisationUnit' + type,
                                    method: 'POST',
                                    params: {id:this.mapView.mapSource},
                                    scope: this,
                                    success: function(r) {
                                        var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                        Ext.getCmp('map_tf2').setValue(name);
                                        Ext.getCmp('map_tf2').value = this.mapView.mapSource;
                                        this.loadFromDatabase(this.mapView.mapSource);
                                    },
                                    failure: function() {
                                        alert('Error: getOrganisationUnit');
                                    }
                                });
                            }
                            else {
                                Ext.getCmp('map_cb2').setValue(this.mapView.mapSource);
                                this.loadFromFile(this.mapView.mapSource);
                            }
                        }
                    }
                }
            }
        });
		
		predefinedMapLegendSetStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getMapLegendSetsByType' + type,
            baseParams: {type: map_legend_type_predefined},
            root: 'mapLegendSets',
            fields: ['id', 'name'],
            autoLoad: true,
            listeners: {
                'load': {
                    scope: this,
                    fn: function() {
						if (this.mapView) {
							Ext.Ajax.request({
								url: path_mapping + 'getMapLegendSet' + type,
								method: 'POST',
								params: {id: this.mapView.mapLegendSetId},
                                scope: this,
								success: function(r) {
									var mls = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
									Ext.getCmp('maplegendset_cb2').setValue(mls.id);
									this.applyPredefinedLegend();
								},
								failure: function() {
									alert('Error: getMapLegendSet');
								}
							});
						}
                    }
                }
            }
        });
        
        this.items = [
         
        {
            xtype: 'combo',
            id: 'mapview_cb2',
            fieldLabel: i18n_favorite,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: i18n_optional,
            selectOnFocus: true,
			labelSeparator: labelseparator,
            width: combo_width,
            store: mapViewStore2,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        var mId = Ext.getCmp('mapview_cb2').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapView' + type,
                            method: 'POST',
                            params: {id: mId},
                            scope: this,
                            success: function(r) {
                                this.mapView = GLOBALS.util.getNumericMapView(Ext.util.JSON.decode(r.responseText).mapView[0]);
								MAPSOURCE = this.mapView.mapSourceType;
                                MAPDATETYPE = this.mapView.mapDateType;
                                Ext.getCmp('mapdatetype_cb').setValue(MAPDATETYPE);
                                
                                Ext.getCmp('mapvaluetype_cb2').setValue(this.mapView.mapValueType);
								VALUETYPE.point = this.mapView.mapValueType;
                                
                                if (this.mapView.mapValueType == map_value_type_indicator) {
                                    Ext.getCmp('indicatorgroup_cb2').showField();
                                    Ext.getCmp('indicator_cb2').showField();
                                    Ext.getCmp('dataelementgroup_cb2').hideField();
                                    Ext.getCmp('dataelement_cb2').hideField();
                                    
                                    Ext.getCmp('indicatorgroup_cb2').setValue(this.mapView.indicatorGroupId);
                                    indicatorStore2.setBaseParam('indicatorGroupId', this.mapView.indicatorGroupId);
                                    indicatorStore2.load();
                                }
                                else if (this.mapView.mapValueType == map_value_type_dataelement) {
                                    Ext.getCmp('indicatorgroup_cb2').hideField();
                                    Ext.getCmp('indicator_cb2').hideField();
                                    Ext.getCmp('dataelementgroup_cb2').showField();
                                    Ext.getCmp('dataelement_cb2').showField();
                                    
                                    Ext.getCmp('dataelementgroup_cb2').setValue(this.mapView.dataElementGroupId);
                                    dataElementStore2.setBaseParam('dataElementGroupId', this.mapView.dataElementGroupId);
                                    dataElementStore2.load();
                                }                                        
								
								if (this.mapView.mapLegendType == map_legend_type_automatic) {
                                    this.legend.type = map_legend_type_automatic;
									Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_automatic);
                                    Ext.getCmp('maplegendset_cb2').hideField();
									Ext.getCmp('method_cb2').showField();
                                    Ext.getCmp('method_cb2').setValue(this.mapView.method);
                                    Ext.getCmp('colorA_cf2').showField();
									Ext.getCmp('colorA_cf2').setValue(this.mapView.colorLow);
                                    Ext.getCmp('colorB_cf2').showField();
									Ext.getCmp('colorB_cf2').setValue(this.mapView.colorHigh);
                                    
                                    if (this.mapView.method == classify_with_bounds) {
                                        Ext.getCmp('numClasses_cb2').hideField();
                                        Ext.getCmp('bounds_tf2').showField();
                                        Ext.getCmp('bounds_tf2').setValue(this.mapView.bounds);
                                    }
                                    else {
                                        Ext.getCmp('bounds_tf2').hideField();
                                        Ext.getCmp('numClasses_cb2').showField();
                                        Ext.getCmp('numClasses_cb2').setValue(this.mapView.classes);
                                    }
								}
								else if (this.mapView.mapLegendType == map_legend_type_predefined) {
                                    this.legend.type = map_legend_type_predefined;
									Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_predefined);
									Ext.getCmp('method_cb2').hideField();
									Ext.getCmp('bounds_tf2').hideField();
									Ext.getCmp('numClasses_cb2').hideField();
									Ext.getCmp('colorA_cf2').hideField();
									Ext.getCmp('colorB_cf2').hideField();
									Ext.getCmp('maplegendset_cb2').showField();
									
                                    Ext.getCmp('maplegendset_cb2').setValue(this.mapView.mapLegendSetId);
                                    this.applyPredefinedLegend();
								}
                            },
                            failure: function() {
                                alert(i18n_status, i18n_error_while_retrieving_data);
                            } 
                        });
                    }
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
			id: 'mapvaluetype_cb2',
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: combo_width,
			value: map_value_type_indicator,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data: [[map_value_type_indicator, 'Indicators'], [map_value_type_dataelement, 'Data elements']]
            }),
			listeners: {
				'select': {
                    scope: this,
					fn: function() {
						if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_indicator) {
							Ext.getCmp('indicatorgroup_cb2').showField();
							Ext.getCmp('indicator_cb2').showField();
							Ext.getCmp('dataelementgroup_cb2').hideField();
							Ext.getCmp('dataelement_cb2').hideField();
							VALUETYPE.point = map_value_type_indicator;
						}
						else if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_dataelement) {
							Ext.getCmp('indicatorgroup_cb2').hideField();
							Ext.getCmp('indicator_cb2').hideField();
							Ext.getCmp('dataelementgroup_cb2').showField();
							Ext.getCmp('dataelement_cb2').showField();
							VALUETYPE.point = map_value_type_dataelement;
						}
                        
                        this.classify(false, true);
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb2',
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorGroupStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
						
						Ext.getCmp('indicator_cb2').clearValue();
						indicatorStore2.setBaseParam('indicatorGroupId', this.getValue());
                        indicatorStore2.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb2',
            fieldLabel: i18n_indicator ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorStore2,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
 
                        var iId = Ext.getCmp('indicator_cb2').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByIndicator' + type,
                            method: 'POST',
                            params: {indicatorId: iId},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_predefined);
                                    Ext.getCmp('maplegendset_cb2').showField();
                                    Ext.getCmp('maplegendset_cb2').setValue(mapLegendSet.id);
                                    Ext.getCmp('method_cb2').hideField();
                                    Ext.getCmp('numClasses_cb2').hideField();
                                    Ext.getCmp('colorA_cf2').hideField();
                                    Ext.getCmp('colorB_cf2').hideField();

                                    this.applyPredefinedLegend();
                                }
                                else {
                                    if (this.legend.type == map_legend_type_predefined) {
                                        this.legend.type = map_legend_type_automatic;
                                        Ext.getCmp('maplegendtype_cb2').setValue(this.legend.type);
                                        Ext.getCmp('method_cb2').showField();
                                        if (Ext.getCmp('method_cb2').getValue() == classify_with_bounds) {
                                            Ext.getCmp('bounds_tf2').showField();
                                            Ext.getCmp('numClasses_cb2').hideField();
                                        }
                                        else {
                                            Ext.getCmp('bounds_tf2').hideField();
                                            Ext.getCmp('numClasses_cb2').showField();
                                        }
                                        Ext.getCmp('colorA_cf2').showField();
                                        Ext.getCmp('colorB_cf2').showField();
                                        Ext.getCmp('maplegendset_cb2').hideField();  
                                    }

                                    this.classify(false, true);
                                }
                            },
                            failure: function() {
                                alert(i18n_status, i18n_error_while_retrieving_data);
                            } 
                        });
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'dataelementgroup_cb2',
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementGroupStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        Ext.getCmp('dataelement_cb2').clearValue();
						dataElementStore2.setBaseParam('dataElementGroupId', this.getValue());
                        dataElementStore2.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'dataelement_cb2',
            fieldLabel: i18n_dataelement,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementStore2,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        
                        var deId = Ext.getCmp('dataelement_cb2').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByDataElement' + type,
                            method: 'POST',
                            params: {dataElementId: deId},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_predefined);
                                    Ext.getCmp('maplegendset_cb2').showField();
                                    Ext.getCmp('maplegendset_cb2').setValue(mapLegendSet.id);
                                    Ext.getCmp('method_cb2').hideField();
                                    Ext.getCmp('numClasses_cb2').hideField();
                                    Ext.getCmp('colorA_cf2').hideField();
                                    Ext.getCmp('colorB_cf2').hideField();

                                    this.applyPredefinedLegend();
                                }
                                else {
                                    if (this.legend.type == map_legend_type_predefined) {
                                        this.legend.type = map_legend_type_automatic;
                                        Ext.getCmp('maplegendtype_cb2').setValue(this.legend.type);
                                        Ext.getCmp('method_cb2').showField();
                                        if (Ext.getCmp('method_cb2').getValue() == classify_with_bounds) {
                                            Ext.getCmp('bounds_tf2').showField();
                                            Ext.getCmp('numClasses_cb2').hideField();
                                        }
                                        else {
                                            Ext.getCmp('bounds_tf2').hideField();
                                            Ext.getCmp('numClasses_cb2').showField();
                                        }
                                        Ext.getCmp('colorA_cf2').showField();
                                        Ext.getCmp('colorB_cf2').showField();
                                        Ext.getCmp('maplegendset_cb2').hideField();  
                                    }

                                    this.classify(false, true);
                                }
                            },
                            failure: function() {
                                alert(i18n_status, i18n_error_while_retrieving_data);
                            } 
                        });
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb2',
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodTypeStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        
                        Ext.getCmp('period_cb2').clearValue();
                        Ext.getCmp('period_cb2').getStore().setBaseParam('name', this.getValue());
                        Ext.getCmp('period_cb2').getStore().load();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb2',
            fieldLabel: i18n_period ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodStore2,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'startdate_df2',
            fieldLabel: i18n_start_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: emptytext,
			labelSeparator: labelseparator,
            width: combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        Ext.getCmp('enddate_df2').setMinValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'enddate_df2',
            fieldLabel: i18n_end_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: emptytext,
			labelSeparator: labelseparator,
            width: combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        Ext.getCmp('startdate_df2').setMaxValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },  
        
        {
            xtype: 'combo',
            id: 'map_cb2',
            fieldLabel: i18n_map ,
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: mapStore2,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        
                        if (Ext.getCmp('map_cb2').getValue() != this.newUrl) {
                            this.loadFromFile(Ext.getCmp('map_cb2').getValue());
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'map_tf2',
            fieldLabel: i18n_parent_orgunit,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            listeners: {
                'focus': {
                    scope: this,
                    fn: function() {
                        function showTree(scope) {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'orgunit_w2',
                                title: 'Select parent organisation unit',
                                closeAction: 'hide',
                                autoScroll: true,
                                width: 280,
                                autoHeight: true,
                                height: 'auto',
                                boxMaxHeight: 500,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        id: 'orgunit_tp2',
                                        bodyStyle: 'padding:7px',
                                        height: GLOBALS.util.getMultiSelectHeight(),
                                        autoScroll: true,
                                        loader: new Ext.tree.TreeLoader({
                                            dataUrl: path_mapping + 'getOrganisationUnitChildren' + type
                                        }),
                                        root: {
                                            id: TOPLEVELUNIT.id,
                                            text: TOPLEVELUNIT.name,
                                            hasChildrenWithCoordinates: TOPLEVELUNIT.hasChildrenWithCoordinates,
                                            nodeType: 'async',
                                            draggable: false,
                                            expanded: true
                                        },
                                        listeners: {
                                            'click': {
                                                fn: function(n) {
                                                    if (n.hasChildNodes()) {
                                                        Ext.getCmp('map_tf2').setValue(n.attributes.text);
                                                        Ext.getCmp('map_tf2').value = n.attributes.id;
                                                        Ext.getCmp('map_tf2').node = n;
                                                    }
                                                }
                                            },
                                            'expandnode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w2').syncSize();
                                                }
                                            },
                                            'collapsenode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w2').syncSize();
                                                }
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'table',
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Select',
                                                width: 133,
                                                scope: scope,
                                                handler: function() {
                                                    if (Ext.getCmp('map_tf2').getValue() && Ext.getCmp('map_tf2').getValue() != this.parentId) {
                                                        this.loadFromDatabase(Ext.getCmp('map_tf2').value);
                                                    }
                                                    Ext.getCmp('orgunit_w2').hide();
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                text: 'Cancel',
                                                width: 133,
                                                handler: function() {
                                                    Ext.getCmp('orgunit_w2').hide();
                                                }
                                            }
                                        ]
                                    }
                                ]
                            });
                            
                            var x = Ext.getCmp('center').x + 15;
                            var y = Ext.getCmp('center').y + 41;
                            w.setPosition(x,y);
                            w.show();
                        }

                        if (TOPLEVELUNIT.id) {
                            showTree(this);
                        }
                        else {
                            Ext.Ajax.request({
                                url: path_commons + 'getOrganisationUnits' + type,
                                params: { level: 1 },
                                method: 'POST',
                                scope: this,
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    TOPLEVELUNIT.id = rootNode.id;
                                    TOPLEVELUNIT.name = rootNode.name;
                                    TOPLEVELUNIT.hasChildrenWithCoordinates = rootNode.hasChildrenWithCoordinates;
                                    showTree(this);
                                },
                                failure: function(r) {
                                    alert('getOrganisationUnits');
                                }
                            });
                        }
                    }
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_type,
            id: 'maplegendtype_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: this.legend.type,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[map_legend_type_automatic, i18n_automatic],
					[map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_predefined && Ext.getCmp('maplegendtype_cb2').getValue() != this.legend.type ) {
							this.legend.type = map_legend_type_predefined;
							Ext.getCmp('method_cb2').hideField();
							Ext.getCmp('bounds_tf2').hideField();
                            Ext.getCmp('numClasses_cb2').hideField();
							Ext.getCmp('colorA_cf2').hideField();
							Ext.getCmp('colorB_cf2').hideField();
							Ext.getCmp('maplegendset_cb2').showField();
							
							if (Ext.getCmp('maplegendset_cb2').getValue()) {
								this.applyPredefinedLegend();
							}
                        }
                        else if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_automatic && Ext.getCmp('maplegendtype_cb2').getValue() != this.legend.type) {
							this.legend.type = map_legend_type_automatic;
							Ext.getCmp('method_cb2').showField();
							if (Ext.getCmp('method_cb2').getValue() == 0) {
								Ext.getCmp('bounds_tf2').showField();
								Ext.getCmp('numClasses_cb2').hideField();
							}
							else {
								Ext.getCmp('bounds_tf2').hideField();
								Ext.getCmp('numClasses_cb2').showField();
							}
							Ext.getCmp('colorA_cf2').showField();
							Ext.getCmp('colorB_cf2').showField();
							Ext.getCmp('maplegendset_cb2').hideField();
                            
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_set,
            id: 'maplegendset_cb2',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            triggerAction: 'all',
            width: combo_width,
			hidden: true,
            store: predefinedMapLegendSetStore2,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
						this.applyPredefinedLegend();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            fieldLabel: i18n_method,
            id: 'method_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: this.legend.method,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[2, i18n_equal_intervals],
					[3, i18n_equal_group_count],
					[1, i18n_fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('method_cb2').getValue() == classify_with_bounds && Ext.getCmp('method_cb2').getValue() != this.legend.method) {
							this.legend.method = classify_with_bounds;
                            Ext.getCmp('bounds_tf2').showField();
                            Ext.getCmp('numClasses_cb2').hideField();
                        }
                        else if (Ext.getCmp('method_cb2').getValue() != this.legend.method) {
							this.legend.method = Ext.getCmp('method_cb2').getValue();
                            Ext.getCmp('bounds_tf2').hideField();
                            Ext.getCmp('numClasses_cb2').showField();
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds_tf2',
            fieldLabel: i18n_bounds,
			labelSeparator: labelseparator,
            emptyText: i18n_comma_separated_values,
            isFormField: true,
            width: combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            fieldLabel: i18n_classes,
			labelSeparator: labelseparator,
            id: 'numClasses_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: this.legend.classes,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
						
						if (Ext.getCmp('numClasses_cb2').getValue() != this.legend.classes) {
							this.legend.classes = Ext.getCmp('numClasses_cb2').getValue();
							this.classify(false, true);
						}
                    }
                }
            }
        },

        {
            xtype: 'colorfield',
            fieldLabel: i18n_low_color,
			labelSeparator: labelseparator,
            id: 'colorA_cf2',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            fieldLabel: i18n_high_color,
			labelSeparator: labelseparator,
            id: 'colorB_cf2',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
            text: i18n_refresh,
			cls: 'aa_med',
            isFormField: true,
            fieldLabel: '',
            labelSeparator: '',
            scope: this,
            handler: function() {
                if (this.validateForm()) {
                    this.layer.setVisibility(true);
                    this.classify(true, true);
                }
                else {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
            }
        }

        ];
	
		mapfish.widgets.geostat.Symbol.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        this.ready = true;

        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    requestFailure: function(request) {
        OpenLayers.Console.error( i18n_ajax_request_failed );
    },
    
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf2').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf2').getValue());
        return [colorA, colorB];
    },
	
	applyPredefinedLegend: function() {
        this.legend.type = map_legend_type_predefined;
		var mls = Ext.getCmp('maplegendset_cb2').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: path_mapping + 'getMapLegendsByMapLegendSet' + type,
			method: 'POST',
			params: {mapLegendSetId: mls},
            scope: this,
			success: function(r) {
				var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends;
				var colors = [];
				var bounds = [];
				for (var i = 0; i < mapLegends.length; i++) {
					if (bounds[bounds.length-1] != mapLegends[i].startValue) {
						if (bounds.length != 0) {
							colors.push(new mapfish.ColorRgb(240,240,240));
						}
						bounds.push(mapLegends[i].startValue);
					}
					colors.push(new mapfish.ColorRgb());
					colors[colors.length-1].setFromHex(mapLegends[i].color);
					bounds.push(mapLegends[i].endValue);
				}

				this.colorInterpolation = colors;
				this.bounds = bounds;
				this.classify(false, true);
			},
			failure: function() {
				alert('Error: getMapLegendsByMapLegendSet');
			}
		});
	},
    
    loadFromDatabase: function(id, isDrillDown) {
        if (isDrillDown) {
            load(this);
        }
        else if (id != this.parentId || this.mapView) {
            if (!this.mapView) {
                if (!Ext.getCmp('map_tf2').node.attributes.hasChildrenWithCoordinates) {
                    Ext.message.msg(false, i18n_no_coordinates_found);
                    Ext.getCmp('map_tf2').setValue(Ext.getCmp('orgunit_tp2').getNodeById(this.parentId).attributes.text);                    
                    Ext.getCmp('map_tf2').value = this.parentId;
                    Ext.getCmp('map_tf2').node = Ext.getCmp('orgunit_tp2').getNodeById(this.parentId);
                    return;
                }
            }
            load(this);
        }
            
        function load(scope) {
            MASK.msg = i18n_loading_geojson;
            MASK.show();
            
            scope.parentId = id;
            scope.setUrl(path_mapping + 'getGeoJson.action?parentId=' + scope.parentId);
        }
    },
    
    loadFromFile: function(url) {
        if (url != this.newUrl) {
            this.newUrl = url;

            if (MAPSOURCE == map_source_type_geojson) {
                this.setUrl(path_mapping + 'getGeoJsonFromFile.action?name=' + url);
            }
			else if (MAPSOURCE == map_source_type_shapefile) {
				this.setUrl(path_geoserver + wfs + url + output);
			}
        }
        else {
            this.classify(false, true);
        }
    },
    
    displayMapLegendTypeFields: function() {
        if (this.legend.type == map_legend_type_automatic) {
			Ext.getCmp('maplegendset_cb2').hideField();
		}
		else if (this.legend.type == map_legend_type_predefined) {
			Ext.getCmp('maplegendset_cb2').showField();
		}
    },
    
    validateForm: function(exception) {
        if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_indicator) {
            if (!Ext.getCmp('indicator_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_dataelement) {
            if (!Ext.getCmp('dataelement_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        if (MAPDATETYPE == map_date_type_fixed) {
            if (!Ext.getCmp('period_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else {
            if (!Ext.getCmp('startdate_df2').getValue() || !Ext.getCmp('enddate_df2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        var cmp = MAPSOURCE == map_source_type_database ? Ext.getCmp('map_tf2') : Ext.getCmp('map_cb2');
         if (!cmp.getValue()) {
            if (exception) {
                Ext.message.msg(false, i18n_form_is_not_complete);
            }
            return false;
        }
        
        return true;
    },
    
    getIndicatorOrDataElementId: function() {
        return VALUETYPE.point == map_value_type_indicator ?
            Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
    },
    
    applyValues: function() {
        var options = {};
        this.indicator = 'value';
        options.indicator = this.indicator;
        options.method = Ext.getCmp('method_cb2').getValue();
        options.numClasses2 = Ext.getCmp('numClasses_cb2').getValue();
        options.colors = this.getColors();
        
        this.coreComp.updateOptions(options);
        this.coreComp.applyClassification();
        this.classificationApplied = true;
    
        MASK.hide();
    },

    classify: function(exception, position) {
        if (MAPSOURCE == map_source_type_database) {
            this.classifyDatabase(exception, position);
        }
        else {
            this.classifyFile(exception, position);
        }
    },
    
    classifyDatabase: function(exception, position) {
		this.displayMapLegendTypeFields();
        if (this.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();        
            
            this.mapData.name = Ext.getCmp('map_tf2').getValue();
            this.mapData.nameColumn = 'name';
            this.mapData.longitude = BASECOORDINATE.longitude;
            this.mapData.latitude = BASECOORDINATE.latitude;
            this.mapData.zoom = 7;
            
            if (!position) {
                MAP.zoomToExtent(this.layer.getDataExtent());
            }
            
            if (this.mapView) {
                if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                }
                else {
                    MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude), this.mapData.zoom);
                }
                this.mapView = false;
            }
            
            FEATURE[thematicMap2] = this.layer.features;
            
            var indicatorOrDataElementId = VALUETYPE.point == map_value_type_indicator ?
                Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
            var dataUrl = VALUETYPE.point == map_value_type_indicator ?
                'getIndicatorMapValuesByParentOrganisationUnit' : 'getDataMapValuesByParentOrganisationUnit';
            var params = {};
            if (MAPDATETYPE == map_date_type_fixed) {
                params.periodId = Ext.getCmp('period_cb2').getValue();
            }
            else {
                params.startDate = new Date(Ext.getCmp('startdate_df2').getValue()).format('Y-m-d');
                params.endDate = new Date(Ext.getCmp('enddate_df2').getValue()).format('Y-m-d');
            }
            params.id = indicatorOrDataElementId;
            params.parentId = this.parentId;
            
            Ext.Ajax.request({
                url: path_mapping + dataUrl + type,
                method: 'POST',
                params: params,
                scope: this,
                success: function(r) {
                    var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                    EXPORTVALUES = GLOBALS.util.getExportDataValueJSON(mapvalues);
                    
                    if (mapvalues.length == 0) {
                        Ext.message.msg(false, i18n_current_selection_no_data );
                        MASK.hide();
                        return;
                    }
                    
                    for (var i = 0; i < mapvalues.length; i++) {
                        for (var j = 0; j < FEATURE[thematicMap2].length; j++) {
                            if (mapvalues[i].orgUnitName == FEATURE[thematicMap2][j].attributes.name) {
                                FEATURE[thematicMap2][j].attributes.value = parseFloat(mapvalues[i].value);
                                FEATURE[thematicMap2][j].attributes.labelString = FEATURE[thematicMap2][j].attributes.name + ' (' + FEATURE[thematicMap2][j].attributes.value + ')';
                                break;
                            }
                        }
                    }
                    
                    this.applyValues();
                },
                failure: function(r) {
                    alert('Error: ' + dataUrl);
                }
            });
        }
    },
    
    classifyFile: function(exception, position) {
		this.displayMapLegendTypeFields();
        if (this.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();
            
            Ext.Ajax.request({
                url: path_mapping + 'getMapByMapLayerPath' + type,
                method: 'POST',
                params: {mapLayerPath: this.newUrl},
                scope: this,
                success: function(r) {
                    this.mapData = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    this.mapData.organisationUnitLevel = parseFloat(this.mapData.organisationUnitLevel);
                    this.mapData.longitude = parseFloat(this.mapData.longitude);
                    this.mapData.latitude = parseFloat(this.mapData.latitude);
                    this.mapData.zoom = parseFloat(this.mapData.zoom);
                    
                    if (!position) {
                        if (this.mapData.zoom != MAP.getZoom()) {
                            MAP.zoomTo(this.mapData.zoom);
                        }
                        MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude));
                    }
                    
                    if (this.mapView) {
                        if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                            MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                        }
                        else {
                            MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude), this.mapData.zoom);
                        }
                        this.mapView = false;
                    }
            
                    FEATURE[thematicMap2] = this.layer.features;
                    
                    var indicatorOrDataElementId = VALUETYPE.point == map_value_type_indicator ?
                        Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
                    var dataUrl = VALUETYPE.point == map_value_type_indicator ?
                        'getIndicatorMapValuesByMap' : 'getDataMapValuesByMap';
                    var periodId = Ext.getCmp('period_cb2').getValue();
                    var mapLayerPath = this.newUrl;
                    
                    Ext.Ajax.request({
                        url: path_mapping + dataUrl + type,
                        method: 'POST',
                        params: {id:indicatorOrDataElementId, periodId:periodId, mapLayerPath:mapLayerPath},
                        scope: this,
                        success: function(r) {
                            var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                            EXPORTVALUES = GLOBALS.util.getExportDataValueJSON(mapvalues);
                            var mv = new Array();
                            var mour = new Array();
                            var nameColumn = this.mapData.nameColumn;
                            var options = {};
                            
                            if (mapvalues.length == 0) {
                                Ext.message.msg(false, i18n_current_selection_no_data );
                                MASK.hide();
                                return;
                            }
                            
                            for (var i = 0; i < mapvalues.length; i++) {
                                mv[mapvalues[i].orgUnitName] = mapvalues[i].orgUnitName ? mapvalues[i].value : '';
                            }
                            
                            Ext.Ajax.request({
                                url: path_mapping + 'getAvailableMapOrganisationUnitRelations' + type,
                                method: 'POST',
                                params: { mapLayerPath: mapLayerPath },
                                scope: this,
                                success: function(r) {
                                    var relations = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelations;
                                   
                                    for (var i = 0; i < relations.length; i++) {
                                        mour[relations[i].featureId] = relations[i].organisationUnit;
                                    }

                                    for (var j = 0; j < FEATURE[thematicMap2].length; j++) {
                                        var value = mv[mour[FEATURE[thematicMap2][j].attributes[nameColumn]]];
                                        FEATURE[thematicMap2][j].attributes.value = value ? parseFloat(value) : '';
                                        FEATURE[thematicMap2][j].data.id = FEATURE[thematicMap2][j].attributes[nameColumn];
                                        FEATURE[thematicMap2][j].data.name = FEATURE[thematicMap2][j].attributes[nameColumn];
                                        FEATURE[thematicMap2][j].attributes.labelString = FEATURE[thematicMap2][j].attributes[nameColumn] + ' (' + FEATURE[thematicMap2][j].attributes.value + ')';
                                    }
                                    
                                    this.applyValues();
                                }
                            });
                        }
                    });
                }
            });
        }
    },
            
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Symbol.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
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

        this.coreComp = new mapfish.GeoStat.Symbol(this.map, coreOptions);
    }   
});

Ext.reg('proportionalSymbol', mapfish.widgets.geostat.Symbol);