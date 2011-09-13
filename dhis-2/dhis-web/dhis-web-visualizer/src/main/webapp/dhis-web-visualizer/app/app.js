Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'ext-ux');
Ext.require([
    'Ext.form.Panel',
    'Ext.ux.form.MultiSelect',
    'Ext.ux.form.ItemSelector'
]);

Ext.onReady( function() {
    
    //Ext.override(Ext.data.Store, {
            //setExtraParam: function (name, value) {
            //this.proxy.extraParams = this.proxy.extraParams || {};
            //this.proxy.extraParams[name] = value;
            //this.proxy.applyEncoding(this.proxy.extraParams);
        //}
    //});
    
    var ds = Ext.create('Ext.data.ArrayStore', {
        data: [[123,'One Hundred Twenty Three'],
            ['1', 'One'], ['2', 'Two'], ['3', 'Three'], ['4', 'Four'], ['5', 'Five'],
            ['6', 'Six'], ['7', 'Seven'], ['8', 'Eight'], ['9', 'Nine']],
        fields: ['value','text'],
        sortInfo: {
            field: 'value',
            direction: 'ASC'
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
                                    var v = cb.getValue(),
                                        c = DV.app.util.getCmp('combobox[name="columns"]'),
                                        f = DV.app.util.getCmp('combobox[name="filter"]'),
                                        i = DV.conf.finals.dimension.indicator,
                                        d = DV.conf.finals.dimension.dataelement,
                                        p = DV.conf.finals.dimension.period,
                                        o = DV.conf.finals.dimension.organisationunit,
                                        index = 0;
                                        
                                    c.enable();
                                    
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
                            store: DV.conf.store.dimension(),
                            listeners: {
                                select: function(cb) {
                                    var v = cb.getValue(),
                                        s = DV.app.util.getCmp('combobox[name="series"]'),
                                        f = DV.app.util.getCmp('combobox[name="filter"]'),
                                        i = DV.conf.finals.dimension.indicator,
                                        d = DV.conf.finals.dimension.dataelement,
                                        p = DV.conf.finals.dimension.period,
                                        o = DV.conf.finals.dimension.organisationunit,
                                        index = 0;
                                        
                                    f.enable();
                                    
                                    cb.filter = s.filter.slice(0);
                                    
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
                            store: DV.conf.store.dimension()
                        }
                    ],
                    items: [
                        {
                            xtype: 'fieldset',
                            title: '<span style="padding:0 5px; font-weight:bold; color:black">Indicators</span>',
                            collapsible: true,
                            defaults: {
                                style: 'margin-bottom:10px'
                            },
                            items: [
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
                                            url: DV.conf.finals.ajax.url_commons + 'getIndicatorGroups' + DV.conf.finals.ajax.action,
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
                                },
                                {            
                                    xtype: 'itemselector',
                                    name: 'itemselector',
                                    width: 518,
                                    imagePath: '',
                                    buttons: [],

                                    store: ds,
                                    displayField: 'text',
                                    valueField: 'value',
                                    value: ['3', '4', '6'],

                                    allowBlank: false,
                                    // minSelections: 2,
                                    // maxSelections: 3,
                                    msgTarget: 'side'
                                }
                            ]
                        }
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
    
    //});
});























