Ext.onReady( function() {
    
    DV = {
        
        chart: null,
        
        cmp: {},
        
        viewport: Ext.create('Ext.container.Viewport', {
            layout: 'border',
            renderTo: Ext.getBody(),
            items: [
                {
                    region: 'north',
                    height: 28,
                    bodyStyle: 'border-bottom-style:none',
                    tbar: [
                        {
                            xtype: 'label',
                            text: 'DHIS 2 Data Visualizer',
                            style: 'font-weight:bold; padding:0 5px'
                        },
                        {
                            xtype: 'button',
                            text: 'Edit chart..',
                            handler: function() {
                                var c = DV.viewport.query('panel[region="center"]')[0];
                                DV.create(c.getWidth(), c.getHeight());
                                c.add(DV.chart);
                            }
                        }
                    ]
                },
                {
                    region: 'center',
                    layout: 'fit',
                    bodyStyle: 'border-style:none; padding:10px',
                    items: []
                }
            ]
        }),
        
        create: function(w, h) {
alert(w);alert(h);            
            Ext.define('WeatherPoint', {
                extend: 'Ext.data.Model',
                fields: ['temperature', 'date']
            });
            
            var store = Ext.create('Ext.data.Store', {
                model: 'WeatherPoint',
                data: [
                    { temperature: 4, date: 'Q1' },
                    { temperature: 21, date: 'Q2' },
                    { temperature: 19, date: 'Q3' },
                    { temperature: 5, date: 'Q4' }
                ]
            });
            
            this.chart = Ext.create('Ext.chart.Chart', {
                width: w,
                height: h,
                store: store,
                axes: [
                    {
                        title: 'Temperature',
                        type: 'Numeric',
                        position: 'left',
                        fields: ['temperature'],
                        minimum: 0
                    },
                    {
                        title: 'Time',
                        type: 'Category',
                        position: 'bottom',
                        fields: ['date']
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        highlight: true,
                        tips: {
                            trackMouse: true,
                            width: 140,
                            height: 28,
                            renderer: function(storeItem, item) {
                               this.setTitle(storeItem.get('date') + ': ' + storeItem.get('temperature') + ' $');
                            }
                        },
                        label: {
                            display: 'insideEnd',
                            'text-anchor': 'middle',
                            field: 'temperature',
                            renderer: Ext.util.Format.numberRenderer('0'),
                            orientation: 'vertical',
                            color: '#333'
                        },
                        xField: 'date',
                        yField: 'temperature'
                    }
                ]
            });
        }
    };    
});
