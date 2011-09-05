Ext.onReady( function() {
    
    DV = {
        
        chart: null,
        
        viewport: Ext.create('Ext.container.Viewport', {
            layout: 'border',
            renderTo: Ext.getBody(),
            items: [
                {
                    region: 'west',
                    width: 300,
                    preventHeader: true,
                    collapsible: true,
                    collapseMode: 'mini',
                    tbar: [
                        {
                            xtype: 'label',
                            text: 'Chart settings',
                            style: 'font-weight:bold; padding:0 5px'
                        },
                        {
                            xtype: 'button',
                            text: 'Column',
                            toggleGroup: 'settings'
                        },
                        {
                            xtype: 'button',
                            text: 'Pie',
                            toggleGroup: 'settings'
                        },
                        {
                            xtype: 'button',
                            text: 'Line',
                            toggleGroup: 'settings'
                        }
                    ],
                    listeners: {
                        collapse: function(p) {
                            p.collapsed = true;
                            DV.getCmp('button[name="resize"]').setText('>>');
                        },
                        expand: function(p) {
                            p.collapsed = false;
                            DV.getCmp('button[name="resize"]').setText('<<');
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
                            handler: function() {
                                var p = DV.getCmp('panel[region="west"]');
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
                            style: 'font-weight:bold; padding:0 5px'
                        },
                        {
                            xtype: 'button',
                            text: 'Export..',
                            handler: function() {
                                var c = DV.getCmp('panel[region="center"]');
                                var sd = [
                                    { anc1: 12, anc2: 12, anc3: 5, anc4: 3, p: 'August 2010'},
                                    { anc1: 5, anc2: 23, anc3: 16, anc4: 5, p: 'September 2010'},
                                    { anc1: 21, anc2: 6, anc3: 11, anc4: 16, p: 'October 2010'},
                                    { anc1: 15, anc2: 22, anc3: 16, anc4: 5, p: 'November 2010'},
                                ];
                                DV.create(c.getWidth(), c.getHeight(), sd);
                                c.add(DV.chart);
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
            ]
        }),
        
        getCmp: function(q) {
            return this.viewport.query(q)[0];
        },
        
        create: function(w, h, storeData) {
            var sf = [];
            for (var r in storeData[0]) {
                sf.push(r);
            }
            
            Ext.define('WeatherPoint', {
                extend: 'Ext.data.Model',
                fields: sf
            });
            
            var store = Ext.create('Ext.data.Store', {
                model: 'WeatherPoint',
                data: storeData
            });
            
            var lf = sf.slice(0,sf.length-1);
            var bf = sf.slice(sf.length-1,sf.length);
            
            this.chart = Ext.create('Ext.chart.Chart', {
                width: w,
                height: h,
                store: store,
                legend: {
                    position: 'bottom'
                },
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        fields: lf,
                        minimum: 0,
                        grid: true,
                        label: {
                            renderer: Ext.util.Format.numberRenderer('0,0')
                        }
                    },
                    {
                        title: 'Indicator',
                        type: 'Category',
                        position: 'bottom',
                        fields: bf
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: bf,
                        yField: lf
                    }
                ]
            });
        }
    };    
});
