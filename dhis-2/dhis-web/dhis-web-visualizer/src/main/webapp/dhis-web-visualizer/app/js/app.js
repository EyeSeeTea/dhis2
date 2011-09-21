Ext.onReady( function() {
    Ext.Ajax.request({
        url: DV.conf.finals.ajax.url_visualizer + 'initialize.action',
        success: function(r) {
            DV.system = { rootNode: Ext.JSON.decode(r.responseText).system.rootNode };            
        
    DV.util = {
        getCmp: function(q) {
            return DV.viewport.query(q)[0];
        },
        getViewportSize: function() {
            var c = DV.util.getCmp('panel[region="center"]');
            return { x: c.getWidth(), y: c.getHeight() };
        },
        multiselect: {
            select: function(a, s, g) {
                var selected = a.getValue();
                if (selected.length) {
                    Ext.Array.each(selected, function(item) {
                        s.store.add({
                            id: item,
                            shortName: a.store.getAt(a.store.find('id', item)).data.shortName,
                            parent: g.getValue()
                        });
                    });
                    
                    a.store.filterBy( function(r) {
                        var filter = true;
                        s.store.each( function(r2) {
                            if (r.data.id === r2.data.id) {
                                filter = false;
                            }
                        });
                        return filter;
                    });
                }
            }
        }
    };
    
    DV.store = {
        dimension: function() {
            return Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    { id: DV.conf.finals.dimension.indicator, name: 'Indicator' },
                    { id: DV.conf.finals.dimension.dataelement, name: 'Data element' },
                    { id: DV.conf.finals.dimension.period, name: 'Period' },
                    { id: DV.conf.finals.dimension.organisationunit, name: 'Org unit' }
                ]
            });
        },
        
        indicator: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'shortName'],
                proxy: {
                    type: 'ajax',
                    baseUrl: DV.conf.finals.ajax.url_commons + 'getIndicators.action',
                    url: DV.conf.finals.ajax.url_commons + 'getIndicators.action',
                    reader: {
                        type: 'json',
                        root: 'indicators'
                    }
                },
                storage: {},
                addToStorage: function(s) {
                    st = this.storage;
                    s.each( function(r) {
                        if (!st[r.data.id]) {
                            st[r.data.id] = { name: r.data.shortName, group: s.param };
                        }
                    });
                },
                listeners: {
                    'load': function(s) {
                        s.addToStorage(s);
                    }
                }
            }),
            
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'parent'],
                data: []
            })
        },
        
        dataElement: Ext.create('Ext.data.Store', {
            fields: ['id', 'shortName'],
            proxy: {
                type: 'ajax',
                baseUrl: DV.conf.finals.ajax.url_commons + 'getDataElements.action',
                url: DV.conf.finals.ajax.url_commons + 'getDataElements.action',
                reader: {
                    type: 'json',
                    root: 'dataElements'
                }
            },
            itemSelector: null,
            addItemSelector: function(s) {
                var fs = DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.dataelement + '"]');
                
                if (s.itemSelector) {
                    fs.remove(s.itemSelector, true);
                }
                
                fs.add({
                    xtype: 'itemselector',
                    name: DV.conf.finals.dimension.dataelement,
                    width: 518,
                    hideNavIcons: true,
                    titleAvailable: 'Available data elements:',
                    titleSelected: 'Selected data elements:',
                    displayField: 'shortName',
                    valueField: 'id',
                    allowBlank: true,
                    msgTarget: 'side',
                    queryMode: 'remote',
                    store: s,
                    listeners: {
                        afterrender: function(is) {
                            s.itemSelector = is;
                        }
                    }
                });
            },
            storage: {},
            addToStorage: function(s) {
                st = this.storage;
                s.each( function(r) {
                    if (!st[r.data.id]) {
                        st[r.data.id] = { name: r.data.shortName, group: s.param };
                    }
                });
            },
            listeners: {
                'load': function(s) {
                    s.addItemSelector(s);
                    s.addToStorage(s);
                }
            }
        }),
        
        period: Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            proxy: {
                type: 'ajax',
                baseUrl: DV.conf.finals.ajax.url_commons + 'getPeriods.action',
                url: DV.conf.finals.ajax.url_commons + 'getPeriods.action',
                reader: {
                    type: 'json',
                    root: 'periods'
                }
            },
            itemSelector: null,
            addItemSelector: function(s) {
                var fs = DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.period + '"]');
                
                if (s.itemSelector) {
                    fs.remove(s.itemSelector, true);
                }
                
                fs.add({
                    xtype: 'itemselector',
                    name: DV.conf.finals.dimension.period,
                    width: 518,
                    hideNavIcons: true,
                    titleAvailable: 'Available periods:',
                    titleSelected: 'Selected periods:',
                    displayField: 'name',
                    valueField: 'id',
                    allowBlank: true,
                    msgTarget: 'side',
                    queryMode: 'remote',
                    store: s,
                    listeners: {
                        afterrender: function(is) {
                            s.itemSelector = is;
                        }
                    }
                });
            },
            storage: {},
            addToStorage: function(s) {
                st = this.storage;
                s.each( function(r) {
                    if (!st[r.data.id]) {
                        st[r.data.id] = { name: r.data.name, group: s.param };
                    }
                });
            },
            listeners: {
                'load': function(s) {
                    s.addItemSelector(s);
                    s.addToStorage(s);
                }
            }
        }),
        
        chart: null,
        
        getChartStore: function() {
            var properties = Ext.Object.getKeys(DV.data.data[0]);
                            
            Ext.define('model1', {
                extend: 'Ext.data.Model',
                fields: properties
            });

            this.chart = Ext.create('Ext.data.Store', {
                model: 'model1',
                data: DV.data.data
            });
            
            this.chart.bottom = properties.slice(0, 1);
            this.chart.left = properties.slice(1, properties.length);
            
            return this.chart;
        }
    };
    
    DV.data = {
        
        values: null,
        
        getValues: function() {            
            var p = '?indicatorIds=52486&indicatorIds=52491&indicatorIds=52487&periodIds=1091452&periodIds=1023570&periodIds=1023571&organisationUnitIds=264';
            
            Ext.Ajax.request({
                url: DV.conf.finals.ajax.url_visualizer + 'getAggregatedIndicatorValues.action' + p,
                success: function(r) {
                    DV.data.values = Ext.JSON.decode(r.responseText).values;
                    DV.data.getData();
                }
            });
        },
        
        data: null,
        
        getData: function() {
            //Ext.Array.each(DV.data.values, function(item, i) {     
                //item[DV.conf.finals.dimension.indicator] = DV.store.indicator.storage[item.i].name;
                //item[DV.conf.finals.dimension.period] = DV.store.period.storage[item.i].name;
            //});
            
            //var dimensions = DV.data.getDimensions(),
                //series = [],
                //columns = [];
                
            
            //for (var i = 0; i < DV.data.values.length; i++) {
                //Ext.Array.include(columns, [DV.data.values[i][dimensions.columns]]);
            //}
            
            //for (var j = 0; j < DV.data.values.length; j++) {
                //for (k = 0; k < columns.length; k++) {
                    //if (DV.data.values[j][dimensions.columns] === columns[k]) {
                        //var obj = {};
                        //obj[DV.data.values[j][dimensions.series]] = DV.data.values[j].v;
                        //columns[k].push(obj);
                    //}
                //}
            //}
                  
            
      
                
            
            //{"v":"187.9", "indicator":"anc1", "period":"okt", "o":"264"},
            //{"v":"113.3", "indicator":"anc2", "period":"nov", "o":"264"},
            //{"v":"150.2", "indicator":"anc3", "period":"okt", "o":"264"},
            //{"v":"110.5", "indicator":"anc3", "period":"des", "o":"264"},
            //{"v":"77.6", "indicator":"anc1", "period":"des", "o":"264"},
            //{"v":"103.9", "indicator":"anc2", "period":"des", "o":"264"},
            //{"v":"139.9", "indicator":"anc2", "period":"okt", "o":"264"},
            //{"v":"149.5", "indicator":"anc3", "period":"nov", "o":"264"},
            //{"v":"94.6", "indicator":"anc1", "period":"nov", "o":"264"}
            
            
            
            
            
            
            
            this.data = [
                { x: 'August 2010', 'anc 1': 12, anc2: 12, anc3: 5, anc4: 3 },
                { x: 'September 2010', 'anc 1': 5, anc2: 23, anc3: 16, anc4: 5 },
                { x: 'October 2010', 'anc 1': 21, anc2: 6, anc3: 2, anc4: 16 },
                { x: 'November 2010', 'anc 1': 15, anc2: 22, anc3: 16, anc4: 5 }
            ];
            return this.data;
        },
        
        dimensions: null,
        
        getDimensions: function() {
            this.dimensions = {
                series: DV.util.getCmp('combobox[name="series"]').getValue(),
                columns: DV.util.getCmp('combobox[name="columns"]').getValue(),
                filter: DV.util.getCmp('combobox[name="filter"]').getValue()
            };
            return this.dimensions;
        }
    };
    
    DV.chart = {
        type: DV.conf.finals.chart.column,
        
        chart: null,
        
        getChart: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DV.util.getViewportSize.x,
                height: DV.util.getViewportSize.y,
                animate: true,
                store: DV.store.chart,
                theme: 'Green',
                legend: {
                    position: 'bottom'
                },
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        grid: true,
                        fields: DV.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer('0,0')
                        }
                    },
                    {
                        title: 'Indicator',
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: DV.store.chart.bottom,
                        yField: DV.store.chart.left
                    }
                ]
            });
            return this.chart;
        },
        
        reload: function() {
            var c = DV.util.getCmp('panel[region="center"]');
            c.removeAll(true);
            c.add(this.chart);
        }
    };
        
    DV.viewport = Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        items: [
            {
                region: 'west',
                bodyStyle: 'padding:10px;',
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
                resizable: true,
                resizeHandles: 'e',
                tbar: [
                    {
                        xtype: 'label',
                        text: 'Chart settings',
                        style: 'font-weight:bold; padding:0 5px'
                    },
                    ' ',
                    {
                        xtype: 'button',
                        text: 'Column',
                        toggleGroup: 'settings',
                        pressed: true,
                        handler: function(b) {
                            if (!b.pressed) {
                                b.toggle();
                            }
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Pie',
                        toggleGroup: 'settings',
                        disabled: true,
                        handler: function(b) {
                            if (!b.pressed) {
                                b.toggle();
                            }
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Line',
                        toggleGroup: 'settings',
                        disabled: true,
                        handler: function(b) {
                            if (!b.pressed) {
                                b.toggle();
                            }
                        }
                    },
                    ' ',' ',
                    {
                        xtype: 'combobox',
                        name: 'series',
                        emptyText: 'Series',
                        queryMode: 'local',
                        editable: false,
                        valueField: 'id',
                        displayField: 'name',
                        width: 90,
                        store: DV.store.dimension(),
                        listeners: {
                            select: function(cb) {
                                var v = cb.getValue(),
                                    c = DV.util.getCmp('combobox[name="columns"]'),
                                    f = DV.util.getCmp('combobox[name="filter"]'),
                                    i = DV.conf.finals.dimension.indicator,
                                    d = DV.conf.finals.dimension.dataelement,
                                    p = DV.conf.finals.dimension.period,
                                    o = DV.conf.finals.dimension.organisationunit,
                                    index = 0;
                                    
                                c.enable();
                                DV.util.getCmp('fieldset[name="' + cb.getValue() + '"]').expand();
                                
                                if (v === i || v === d) {
                                    cb.filter = [false, false, true, true];
                                }
                                else if (v === p) {
                                    cb.filter = [true, true, false, true];
                                }
                                else if (v === o) {
                                    cb.filter = [true, true, true, false];
                                }
                                
                                var fn = function(cmp) {
                                    cmp.store.filterBy( function(r) {
                                        return cb.filter[index++];
                                    });
                                    if (v === cmp.getValue()) {
                                        cmp.clearValue();
                                    }
                                    else if ((v === i || v === d) && (cmp.getValue() === i || cmp.getValue() === d)) {
                                        cmp.clearValue();
                                    }
                                };
                                
                                fn(c);                                    
                                index = 0;
                                fn(f);
                            }
                        }
                    },
                    ' ',
                    {
                        xtype: 'combobox',
                        name: 'columns',
                        emptyText: 'Columns',
                        queryMode: 'local',
                        editable: false,
                        lastQuery: '',
                        valueField: 'id',
                        displayField: 'name',
                        width: 90,
                        disabled: true,
                        store: DV.store.dimension(),
                        listeners: {
                            select: function(cb) {
                                var v = cb.getValue(),
                                    s = DV.util.getCmp('combobox[name="series"]'),
                                    f = DV.util.getCmp('combobox[name="filter"]'),
                                    i = DV.conf.finals.dimension.indicator,
                                    d = DV.conf.finals.dimension.dataelement,
                                    p = DV.conf.finals.dimension.period,
                                    o = DV.conf.finals.dimension.organisationunit,
                                    index = 0;
                                    
                                f.enable();
                                DV.util.getCmp('fieldset[name="' + cb.getValue() + '"]').expand();
                                
                                cb.filter = Ext.Array.clone(s.filter);
                                
                                if (cb.getValue() === i || cb.getValue() === d) {
                                    cb.filter[0] = false;
                                    cb.filter[1] = false;
                                }
                                else if (cb.getValue() === p) {
                                    cb.filter[2] = false;
                                }
                                else if (cb.getValue() === o) {
                                    cb.filter[3] = false;
                                }
                                
                                f.store.filterBy( function(r) {
                                    return cb.filter[index++];
                                });
                                if (v === f.getValue()) {
                                    f.clearValue();
                                }
                                else if ((v === i || v === d) && (f.getValue() === i || f.getValue() === d)) {
                                    f.clearValue();
                                }
                            }
                        }
                    },
                    ' ',
                    {
                        xtype: 'combobox',
                        name: 'filter',
                        emptyText: 'Filter',
                        queryMode: 'local',
                        editable: false,
                        lastQuery: '',
                        valueField: 'id',
                        displayField: 'name',
                        width: 90,
                        disabled: true,
                        store: DV.store.dimension(),
                        listeners: {
                            select: function(cb) {
                                DV.util.getCmp('fieldset[name="' + cb.getValue() + '"]').expand();
                            }
                        }                                    
                    },
                    ' ',' ',
                    {
                        xtype: 'button',
                        text: 'Load..',
                        handler: function() {
                            //DV.data.getValues();
                        }
                    }
                ],
                items: [
                    {
                        xtype: 'fieldset',
                        name: DV.conf.finals.dimension.indicator,
                        title: '<span style="padding:0 5px; font-weight:bold; color:black">Indicators</span>',
                        collapsed: true,
                        collapsible: true,
                        items: [
                            {
                                xtype: 'combobox',
                                style: 'margin-bottom:8px',
                                width: 255,
                                valueField: 'id',
                                displayField: 'name',
                                fieldLabel: 'Group',
                                labelWidth: 50,
                                labelStyle: 'padding-left:7px;',
                                editable: false,
                                queryMode: 'remote',
                                store: Ext.create('Ext.data.Store', {
                                    fields: ['id', 'name'],
                                    proxy: {
                                        type: 'ajax',
                                        url: DV.conf.finals.ajax.url_commons + 'getIndicatorGroups.action',
                                        reader: {
                                            type: 'json',
                                            root: 'indicatorGroups'
                                        }                                                
                                    }
                                }),
                                listeners: {
                                    select: function(cb) {
                                        var store = DV.store.indicator.available;
                                        store.param = cb.getValue();
                                        store.proxy.url = Ext.String.urlAppend(store.proxy.baseUrl, 'id=' + cb.getValue());
                                        store.load();
                                    }
                                }
                            },
                            
                            {
                                xtype: 'panel',
                                layout: 'column',
                                bodyStyle: 'border-style:none',
                                items: [
                                    {
                                        xtype: 'multiselect',
                                        name: 'availableIndicators',
                                        width: 254,
                                        hideNavIcons: true,
                                        displayField: 'shortName',
                                        valueField: 'id',
                                        allowBlank: true,
                                        msgTarget: 'side',
                                        queryMode: 'remote',
                                        ddReorder: true,
                                        store: DV.store.indicator.available,
                                        tbar: [
                                            {
                                                xtype: 'label',
                                                text: 'Available indicators',
                                                style: 'padding-left:5px'
                                            },
                                            '->',
                                            {
                                                xtype: 'button',
                                                text: '>',
                                                handler: function() {
                                                    var ai = DV.util.getCmp('multiselect[name="availableIndicators"]');
                                                    var si = DV.util.getCmp('multiselect[name="selectedIndicators"]');
                                                    var ig = DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.indicator + '"]').query('combobox')[0];
                                                    DV.util.multiselect.select(ai, si, ig);
                                                    
                                                    //var selected = DV.util.getCmp('multiselect[name="availableIndicators"]').getValue().split(',');
                                                    //if (selected.length) {
                                                        //DV.util.multiselect.select(selected, 
                                                    
                                                    //var store = DV.util.getCmp('multiselect[name="availableIndicators"]').store;
                                                    //alert(store);
                                                }                                                    
                                            },
                                            {
                                                xtype: 'button',
                                                text: '>>'
                                            }
                                        ],
                                        listeners: {
                                            afterrender: function() {
                                            }
                                        }
                                    },
                                    
                                    {
                                        xtype: 'multiselect',
                                        name: 'selectedIndicators',
                                        width: 254,
                                        hideNavIcons: true,
                                        displayField: 'shortName',
                                        valueField: 'id',
                                        allowBlank: true,
                                        msgTarget: 'side',
                                        queryMode: 'remote',
                                        ddReorder: true,
                                        store: DV.store.indicator.selected,
                                        tbar: [
                                            {xtype:'button', text:'<<'},
                                            {xtype:'button', text:'<'},
                                            '->',
                                            {
                                                xtype: 'label',
                                                text: 'Selected indicators',
                                                style: 'padding-right:5px'
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    
                    {
                        xtype: 'fieldset',
                        name: DV.conf.finals.dimension.dataelement,
                        title: '<span style="padding:0 5px; font-weight:bold; color:black">Data elements</span>',
                        collapsed: true,
                        collapsible: true,
                        items: [
                            {
                                xtype: 'combobox',
                                style: 'margin-bottom:8px',
                                width: 255,
                                valueField: 'id',
                                displayField: 'name',
                                fieldLabel: 'Group',
                                labelWidth: 50,
                                labelStyle: 'padding-left:7px;',
                                editable: false,
                                queryMode: 'remote',
                                store: Ext.create('Ext.data.Store', {
                                    fields: ['id', 'name'],
                                    proxy: {
                                        type: 'ajax',
                                        url: DV.conf.finals.ajax.url_commons + 'getDataElementGroups.action',
                                        reader: {
                                            type: 'json',
                                            root: 'dataElementGroups'
                                        }                                                
                                    }
                                }),
                                listeners: {
                                    select: function(cb) {
                                        var store = DV.store.dataElement;
                                        store.param = cb.getValue();
                                        store.proxy.url = Ext.String.urlAppend(store.proxy.baseUrl, 'id=' + cb.getValue());
                                        store.load();
                                    }
                                }
                            }                                
                        ]
                    },
                    
                    {
                        xtype: 'fieldset',
                        name: DV.conf.finals.dimension.period,
                        title: '<span style="padding:0 5px; font-weight:bold; color:black">Periods</span>',
                        collapsed: true,
                        collapsible: true,
                        items: [
                            {
                                xtype: 'combobox',
                                style: 'margin-bottom:8px',
                                width: 255,
                                valueField: 'name',
                                displayField: 'displayName',
                                fieldLabel: 'Type',
                                labelWidth: 50,
                                labelStyle: 'padding-left:7px;',
                                editable: false,
                                queryMode: 'remote',
                                store: Ext.create('Ext.data.Store', {
                                    fields: ['name', 'displayName'],
                                    proxy: {
                                        type: 'ajax',
                                        url: DV.conf.finals.ajax.url_commons + 'getPeriodTypes.action',
                                        reader: {
                                            type: 'json',
                                            root: 'periodTypes'
                                        }                                                
                                    }
                                }),
                                listeners: {
                                    select: function(cb) {
                                        var store = DV.store.period;
                                        store.param = cb.getValue();
                                        store.proxy.url = Ext.String.urlAppend(store.proxy.baseUrl, 'name=' + cb.getValue());
                                        store.load();
                                    }
                                }
                            }                                
                        ]
                    },
                    
                    {
                        xtype: 'fieldset',
                        name: DV.conf.finals.dimension.organisationunit,
                        title: '<span style="padding:0 5px; font-weight:bold; color:black">Organisation units</span>',
                        collapsed: true,
                        collapsible: true,
                        items: [
                            {
                                xtype: 'treepanel',
                                height: 300,
                                width: 517,
                                autoScroll: true,
                                multiSelect: true,
                                store: Ext.create('Ext.data.TreeStore', {
                                    proxy: {
                                        type: 'ajax',
                                        url: DV.conf.finals.ajax.url_visualizer + 'getOrganisationUnitChildren.action'
                                    },
                                    root: {
                                        id: DV.system.rootNode.id,
                                        text: DV.system.rootNode.name,
                                        expanded: false
                                    }
                                })
                            }
                        ]
                    }
                ],
                listeners: {
                    collapse: function(p) {                    
                        p.collapsed = true;
                        DV.util.getCmp('button[name="resize"]').setText('>>>');
                    },
                    expand: function(p) {
                        p.collapsed = false;
                        DV.util.getCmp('button[name="resize"]').setText('<<<');
                    }
                }
            },
            {
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding:10px',
                tbar: [
                    {
                        xtype: 'button',
                        name: 'resize',
                        text: '<<<',
                        toolTip: 'Collapse',
                        handler: function() {
                            var p = DV.util.getCmp('panel[region="west"]');
                            if (p.collapsed) {
                                p.expand();
                            }
                            else {
                                p.collapse();
                            }
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'DHIS 2 Data Visualizer',
                        style: 'font-weight:bold; padding:0 2px'
                    },
                    {
                        xtype: 'button',
                        text: 'Load..',
                        handler: function() {
                            DV.data.getData();
                            DV.store.getChartStore();
                            DV.chart.getChart();
                            DV.chart.reload();
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Reload..',
                        handler: function() {
                            DV.data.data = [
                                { x: 'August 2010', 'anc 1': 17, anc2: 5, anc3: 11, anc4: 14 },
                                { x: 'September 2010', 'anc 1': 5, anc2: 13, anc3: 16, anc4: 5 },
                                { x: 'October 2010', 'anc 1': 21, anc2: 6, anc3: 11, anc4: 16 },
                                { x: 'November 2010', 'anc 1': 15, anc2: 22, anc3: 44, anc4: 5 }
                            ];
                            
                            DV.store.getChartStore();                                
                            DV.chart.getChart();                                
                            DV.chart.reload();
                        }
                    },
                    '->',
                    {
                        xtype: 'button',
                        text: 'Exit..',
                        handler: function() {
                            window.location.href = DV.conf.finals.ajax.url_portal + 'redirect.action';
                        }
                    }
                ]
            }
        ],
        listeners: {
            resize: function(vp) {
                vp.query('panel[region="west"]')[0].setWidth(562); //vp.getWidth() / 2
            }
        }
    });
    
    }});
});
