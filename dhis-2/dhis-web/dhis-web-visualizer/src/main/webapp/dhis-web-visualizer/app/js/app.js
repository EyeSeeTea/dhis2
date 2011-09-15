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
        
        indicator: Ext.create('Ext.data.Store', {
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
            itemSelector: null,
            addItemSelector: function(s) {
                var fs = DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.indicator + '"]');
                
                if (s.itemSelector) {
                    fs.remove(s.itemSelector, true);
                }
                
                fs.add({
                    xtype: 'itemselector',
                    width: 518,
                    hideNavIcons: true,
                    titleAvailable: 'Available indicators:',
                    titleSelected: 'Selected indicators:',
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
            listeners: {
                'load': function(s) {
                    s.addItemSelector(s);
                }
            }
        }),
        
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
            listeners: {
                'load': function(s) {
                    s.addItemSelector(s);
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
            listeners: {
                'load': function(s) {
                    s.addItemSelector(s);
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
        data: null,
        
        getData: function() {
            this.data = [
                { x: 'August 2010', 'anc 1': 12, anc2: 12, anc3: 5, anc4: 3 },
                { x: 'September 2010', 'anc 1': 5, anc2: 23, anc3: 16, anc4: 5 },
                { x: 'October 2010', 'anc 1': 21, anc2: 6, anc3: 2, anc4: 16 },
                { x: 'November 2010', 'anc 1': 15, anc2: 22, anc3: 16, anc4: 5 }
            ];
            return this.data;
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
                                valueField: 'id',
                                displayField: 'name',
                                fieldLabel: 'Indicator group',
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
                                        var store = DV.store.indicator;
                                        store.proxy.url = Ext.String.urlAppend(store.proxy.baseUrl, 'id=' + cb.getValue());
                                        store.load();
                                    }
                                }
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
                                valueField: 'id',
                                displayField: 'name',
                                fieldLabel: 'Data element group',
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
                                valueField: 'name',
                                displayField: 'displayName',
                                fieldLabel: 'Period type',
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
                        DV.util.getCmp('button[name="resize"]').setText('>>');
                    },
                    expand: function(p) {
                        p.collapsed = false;
                        DV.util.getCmp('button[name="resize"]').setText('<<');
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
                        text: '<<',
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
                            window.location.href = DV.conf.finals.url_portal + 'redirect' + DV.conf.finals.action;
                        }
                    }
                ]
            }
        ],
        listeners: {
            resize: function(vp) {
                vp.query('panel[region="west"]')[0].setWidth(565); //vp.getWidth() / 2
            }
        }
    });
    
    }});
});























