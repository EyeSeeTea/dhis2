Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'lib/ext-ux');
Ext.require(['Ext.form.Panel', 'Ext.ux.form.MultiSelect', 'Ext.ux.form.ItemSelector']);
    
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
    }
};
