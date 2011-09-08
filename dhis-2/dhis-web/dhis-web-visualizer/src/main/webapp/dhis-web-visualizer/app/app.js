Ext.onReady( function() {
    
    Ext.override(Ext.data.Store, {
            setExtraParam: function (name, value) {
            this.proxy.extraParams = this.proxy.extraParams || {};
            this.proxy.extraParams[name] = value;
            this.proxy.applyEncoding(this.proxy.extraParams);
        }
    });
    
    DV = {};
    
    DV.conf = {
        finals: {
            ajax: {
                url_visualizer: '../',
                url_commons: '../../dhis-web-commons-ajax-json/',
                url_portal: '../../dhis-web-portal/',
                action: '.action'
            },
            
            dimension: {
                indicator: 'indicator',
                dataelement: 'dataelement',
                period: 'period',
                organisationunit: 'organisationunit'
            }
        }
    };
    
    DV.conf.store = {
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
        }
    };
        
    DV.app = {
        
        util: {
            getCmp: function(q) {
                return DV.app.viewport.query(q)[0];
            },
            getViewportSize: function() {
                var c = DV.app.getCmp('panel[region="center"]');
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
                    width: DV.app.util.getViewportSize.x,
                    height: DV.app.util.getViewportSize.y,
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
                var c = DV.app.util.getCmp('panel[region="center"]');
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
                            store: DV.conf.store.dimension(),
                            listeners: {
                                select: function(cb) {
                                    var v = cb.getValue();
                                    var i = DV.conf.finals.dimension.indicator;
                                    var d = DV.conf.finals.dimension.dataelement;
                                    var c = DV.app.util.getCmp('combobox[name="columns"]');
                                    var f = DV.app.util.getCmp('combobox[name="filter"]');
                                    var fn = function(r) {
                                        if (((v === i) || (v === d)) && ((r.data.id === i) || (r.data.id === d))) {
                                            return false;
                                        }
                                        else {
                                            if (v === r.data.id) {
                                                return false;
                                            }
                                        }
                                        return true;
                                    };
                                    c.store.filterBy(fn);                                    
                                    if (v === c.getValue()) {
                                        c.clearValue();
                                    }
                                    f.store.filterBy(fn);
                                    if (v === f.getValue()) {
                                        f.clearValue();
                                    }
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
                            store: DV.conf.store.dimension()
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
                            store: DV.conf.store.dimension()
                        }
                    ],
                    items: [
                        //{
                            //xtype: 'fieldset',
                            //title: '<span style="padding:0 5px">Indicator</span>',
                            //collapsible: true,
                            //items: [
                                {
                                    xtype: 'combobox',
                                    name: 'indicatorgroup',
                                    valueField: 'id',
                                    displayField: 'name',
                                    fieldLabel: 'Indicator group',
                                    editable: false,
                                    queryMode: 'remote',
                                    store: Ext.create('Ext.data.Store', {
                                        fields: ['id', 'name'],
                                        proxy: {
                                            type: 'ajax',
                                            url: DV.conf.finals.path_commons + 'getIndicatorGroups' + DV.conf.finals.action,
                                            reader: {
                                                type: 'json',
                                                root: 'indicatorGroups'
                                            }                                                
                                        }
                                    }),
                                    listeners: {
                                        select: function(cb) {
                                            var store = DV.app.util.getCmp('store[name="indicator"]');
                                            store.setExtraParam('indicatorGroupId', cb.getValue());
                                            store.load();
                                        }
                                    }
                                }
                                //{
                                    //xtype: 'itemselector',
                                    //name: 'indicator',
                                    //valueField: 'id',
                                    //displayField: 'name',
                                    //fieldLabel: 'Indicators',
                                    //editable: false,
                                    //queryMode: 'remote',
                                    //store: Ext.create('Ext.data.Store', {
                                        //fields: ['id', 'name'],
                                        //proxy: {
                                            //type: 'ajax',
                                            //url: DV.conf.finals.path_visualizer + 'getIndicatorsByIndicatorGroup' + DV.conf.finals.action,
                                            //reader: {
                                                //type: 'json',
                                                //root: 'indicators'
                                            //}                                                
                                        //}
                                    //})
                                //}
                            //]
                        //}
                    ],
                    listeners: {
                        collapse: function(p) {                    
                            p.collapsed = true;
                            DV.app.util.getCmp('button[name="resize"]').setText('>>');
                        },
                        expand: function(p) {
                            p.collapsed = false;
                            DV.app.util.getCmp('button[name="resize"]').setText('<<');
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
                                var p = DV.app.util.getCmp('panel[region="west"]');
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
                                DV.app.chart.getData();                                
                                DV.app.chart.getStore();                                
                                DV.app.chart.getChart();                                
                                DV.app.chart.reload();
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Reload..',
                            handler: function() {
                                DV.app.chart.data = [
                                    { x: 'August 2010', 'anc 1': 17, anc2: 5, anc3: 11, anc4: 14 },
                                    { x: 'September 2010', 'anc 1': 5, anc2: 13, anc3: 16, anc4: 5 },
                                    { x: 'October 2010', 'anc 1': 21, anc2: 6, anc3: 11, anc4: 16 },
                                    { x: 'November 2010', 'anc 1': 15, anc2: 22, anc3: 44, anc4: 5 }
                                ];
                                
                                DV.app.chart.getStore();                                
                                DV.app.chart.getChart();                                
                                DV.app.chart.reload();
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
                resize: function(v) {
                    v.query('panel[region="west"]')[0].setWidth(v.getWidth() / 2);
                }
            }
        })
    };
    
    //DV.app.conf.data = [
        //{ id: DV.app.conf.finals.dimension_indicator, name: 'Indicator' },
        //{ id: DV.app.conf.finals.dimension_dataelement, name: 'Data element' },
        //{ id: DV.app.conf.finals.dimension_period, name: 'Period' },
        //{ id: DV.app.conf.finals.dimension_organisationunit, name: 'Organisation unit' }
    //];
    
    //DV.app.util.getCmp('combobox').store.loadData(DV.app.conf.data);
});























