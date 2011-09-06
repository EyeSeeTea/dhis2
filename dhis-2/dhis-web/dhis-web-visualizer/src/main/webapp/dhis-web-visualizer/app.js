Ext.onReady( function() {
    
    Ext.define('simple', {
        extend: 'Ext.data.Model',
        fields: ['id', 'name']
    });
        
    DV = {
        conf: {
            finals: {
                dimension_indicator: 'indicator',
                dimension_dataelement: 'dataelement',
                dimension_period: 'period',
                dimension_organisationunit: 'organisationunit'
            }
        },
        
        util: {
            getCmp: function(q) {
                return DV.viewport.query(q)[0];
            },
            getViewportSize: function() {
                var c = DV.getCmp('panel[region="center"]');
                return { x: c.getWidth(), y: c.getHeight() };
            }
        },
        
        chart: {            
            data: null,            
            getData: function() {
                this.data = [
                    { x: 'August 2010', 'anc 1': 12, anc2: 12, anc3: 5, anc4: 3 },
                    { x: 'September 2010', 'anc 1': 5, anc2: 23, anc3: 16, anc4: 5 },
                    { x: 'October 2010', 'anc 1': 21, anc2: 6, anc3: 2, anc4: 16 },
                    { x: 'November 2010', 'anc 1': 15, anc2: 22, anc3: 16, anc4: 5 }
                ];
                
                return this.data;
            },
            
            store: null,
            getStore: function() {
                var properties = [];
                for (var p in this.data[0]) {
                    properties.push(p);
                }
                                
                Ext.define('model1', {
                    extend: 'Ext.data.Model',
                    fields: properties
                });

                this.store = Ext.create('Ext.data.Store', {
                    model: 'model1',
                    data: this.data
                });
                
                this.store.bottom = properties.slice(0, 1);
                this.store.left = properties.slice(1, properties.length);
                
                return this.store;
            },
            
            chart: null,        
            getChart: function() {
                this.chart = Ext.create('Ext.chart.Chart', {
                    width: DV.util.getViewportSize.x,
                    height: DV.util.getViewportSize.y,
                    animate: true,
                    store: this.store,
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
                            fields: this.store.left,
                            label: {
                                renderer: Ext.util.Format.numberRenderer('0,0')
                            }
                        },
                        {
                            title: 'Indicator',
                            type: 'Category',
                            position: 'bottom',
                            fields: this.store.bottom
                        }
                    ],
                    series: [
                        {
                            type: 'column',
                            axis: 'left',
                            xField: this.store.bottom,
                            yField: this.store.left
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
        },
        
        viewport: Ext.create('Ext.container.Viewport', {
            layout: 'border',
            renderTo: Ext.getBody(),
            items: [
                {
                    region: 'west',
                    bodyStyle: 'padding:8px;',
                    minWidth: 250,
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
                        '',
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
                            handler: function(b) {
                                if (!b.pressed) {
                                    b.toggle();
                                }
                            }
                        }
                    ],
                    items: [
                        {
                            xtype: 'panel',
                            layout: 'column',
                            bodyStyle: 'border-style:none; padding-bottom:5px',
                            items: [
                                {
                                    xtype: 'label',
                                    text: 'Series:',
                                    width: 50,
                                    style: 'line-height:21px;'
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:10px;',
                                    text: 'Indicator',
                                    toggleGroup: 'series',
                                    pressed: true,
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Data element',
                                    toggleGroup: 'series',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Period',
                                    toggleGroup: 'series',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Organisation unit',
                                    toggleGroup: 'series',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'column',
                            bodyStyle: 'border-style:none; padding-bottom:5px',
                            items: [
                                {
                                    xtype: 'label',
                                    text: 'Column:',
                                    width: 50,
                                    style: 'line-height:21px;'
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:10px;',
                                    text: 'Indicator',
                                    toggleGroup: 'column',
                                    pressed: true,
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Data element',
                                    toggleGroup: 'column',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Period',
                                    toggleGroup: 'column',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Organisation unit',
                                    toggleGroup: 'column',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'column',
                            bodyStyle: 'border-style:none;',
                            items: [
                                {
                                    xtype: 'label',
                                    text: 'Filter:',
                                    width: 50,
                                    style: 'line-height:21px;'
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:10px;',
                                    text: 'Indicator',
                                    toggleGroup: 'filter',
                                    pressed: true,
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Data element',
                                    toggleGroup: 'filter',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Period',
                                    toggleGroup: 'filter',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    style: 'margin-left:5px;',
                                    text: 'Organisation unit',
                                    toggleGroup: 'filter',
                                    handler: function(b) {
                                        if (!b.pressed) {
                                            b.toggle();
                                        }
                                    }
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
                                DV.chart.getData();                                
                                DV.chart.getStore();                                
                                DV.chart.getChart();                                
                                DV.chart.reload();
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Reload..',
                            handler: function() {
                                DV.chart.data = [
                                    { x: 'August 2010', 'anc 1': 17, anc2: 5, anc3: 11, anc4: 14 },
                                    { x: 'September 2010', 'anc 1': 5, anc2: 13, anc3: 16, anc4: 5 },
                                    { x: 'October 2010', 'anc 1': 21, anc2: 6, anc3: 11, anc4: 16 },
                                    { x: 'November 2010', 'anc 1': 15, anc2: 22, anc3: 44, anc4: 5 }
                                ];
                                
                                DV.chart.getStore();                                
                                DV.chart.getChart();                                
                                DV.chart.reload();
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
                            text: 'Exit..',
                            handler: function() {
                                window.location.href = '../dhis-web-portal/redirect.action';
                            }
                        }
                    ]
                }
            ],
            listeners: {
                resize: function(v) {
                    v.query('panel[region="west"]')[0].setWidth(v.getWidth() / 2);
                }
            }
        })
    };
    
    //DV.conf.data = [
        //{ id: DV.conf.finals.dimension_indicator, name: 'Indicator' },
        //{ id: DV.conf.finals.dimension_dataelement, name: 'Data element' },
        //{ id: DV.conf.finals.dimension_period, name: 'Period' },
        //{ id: DV.conf.finals.dimension_organisationunit, name: 'Organisation unit' }
    //];
    
    //DV.util.getCmp('combobox').store.loadData(DV.conf.data);
});























