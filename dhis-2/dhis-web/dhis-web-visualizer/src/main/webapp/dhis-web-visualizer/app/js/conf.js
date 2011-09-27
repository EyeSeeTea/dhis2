Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'lib/ext-ux');
Ext.require(['Ext.form.Panel', 'Ext.ux.form.MultiSelect', 'Ext.ux.form.ItemSelector']);

Ext.onReady( function() {
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
});
    
DV = {};

DV.conf = {
    finals: {
        ajax: {
            url_visualizer: '../',
            url_commons: '../../dhis-web-commons-ajax-json/',
            url_portal: '../../dhis-web-portal/'
        },
        
        dimension: {
            indicator: 'indicator',
            dataelement: 'dataelement',
            period: 'period',
            organisationunit: 'organisationunit'
        },
        
        chart: {
            column: 'column',
            line: 'line',
            pie: 'pie'
        }
    },
    style: {
        label: {
            period: 'font:bold 11px arial,ubuntu; color:#444; line-height:20px',
        }
    },
    layout: {
        west_cmp_width: 380
    }
};
