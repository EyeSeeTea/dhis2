/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @class Ext.picker.Time
 * @extends Ext.view.BoundList
 * <p>A time picker which provides a list of times from which to choose. This is used by the
 * {@link Ext.form.field.Time} class to allow browsing and selection of valid times, but could also be used
 * with other components.</p>
 * <p>By default, all times starting at midnight and incrementing every 15 minutes will be presented.
 * This list of available times can be controlled using the {@link #minValue}, {@link #maxValue}, and
 * {@link #increment} configuration properties. The format of the times presented in the list can be
 * customized with the {@link #format} config.</p>
 * <p>To handle when the user selects a time from the list, you can subscribe to the {@link #selectionchange}
 * event.</p>
 *
 * {@img Ext.picker.Time/Ext.picker.Time.png Ext.picker.Time component}
 *
 * ## Code
     new Ext.create('Ext.picker.Time', {
        width: 60,
        minValue: Ext.Date.parse('04:30:00 AM', 'h:i:s A'),
        maxValue: Ext.Date.parse('08:00:00 AM', 'h:i:s A'),
        renderTo: Ext.getBody()
    });
 *
 */
Ext.define('Ext.picker.Time', {
    extend: 'Ext.view.BoundList',
    alias: 'widget.timepicker',
    requires: ['Ext.data.Store', 'Ext.Date'],

    /**
     * @cfg {Date} minValue
     * The minimum time to be shown in the list of times. This must be a Date object (only the time fields
     * will be used); no parsing of String values will be done. Defaults to undefined.
     */

    /**
     * @cfg {Date} maxValue
     * The maximum time to be shown in the list of times. This must be a Date object (only the time fields
     * will be used); no parsing of String values will be done. Defaults to undefined.
     */

    /**
     * @cfg {Number} increment
     * The number of minutes between each time value in the list (defaults to 15).
     */
    increment: 15,

    /**
     * @cfg {String} format
     * The default time format string which can be overriden for localization support. The format must be
     * valid according to {@link Ext.Date#parse} (defaults to 'g:i A', e.g., '3:15 PM'). For 24-hour time
     * format try 'H:i' instead.
     */
    format : "g:i A",

    /**
     * @hide
     * The field in the implicitly-generated Model objects that gets displayed in the list. This is
     * an internal field name only and is not useful to change via config.
     */
    displayField: 'disp',

    /**
     * @private
     * Year, month, and day that all times will be normalized into internally.
     */
    initDate: [2008,1,1],

    componentCls: Ext.baseCSSPrefix + 'timepicker',

    /**
     * @hide
     */
    loadMask: false,

    initComponent: function() {
        var me = this,
            dateUtil = Ext.Date,
            clearTime = dateUtil.clearTime,
            initDate = me.initDate.join('/');

        // Set up absolute min and max for the entire day
        me.absMin = clearTime(new Date(initDate));
        me.absMax = dateUtil.add(clearTime(new Date(initDate)), 'mi', (24 * 60) - 1);

        me.store = me.createStore();
        me.updateList();

        this.callParent();
    },

    /**
     * Set the {@link #minValue} and update the list of available times. This must be a Date
     * object (only the time fields will be used); no parsing of String values will be done.
     * @param {Date} value
     */
    setMinValue: function(value) {
        this.minValue = value;
        this.updateList();
    },

    /**
     * Set the {@link #maxValue} and update the list of available times. This must be a Date
     * object (only the time fields will be used); no parsing of String values will be done.
     * @param {Date} value
     */
    setMaxValue: function(value) {
        this.maxValue = value;
        this.updateList();
    },

    /**
     * @private
     * Sets the year/month/day of the given Date object to the {@link #initDate}, so that only
     * the time fields are significant. This makes values suitable for time comparison.
     * @param {Date} date
     */
    normalizeDate: function(date) {
        var initDate = this.initDate;
        date.setFullYear(initDate[0], initDate[1] - 1, initDate[2]);
        return date;
    },

    /**
     * Update the list of available times in the list to be constrained within the
     * {@link #minValue} and {@link #maxValue}.
     */
    updateList: function() {
        var me = this,
            min = me.normalizeDate(me.minValue || me.absMin),
            max = me.normalizeDate(me.maxValue || me.absMax);

        me.store.filterBy(function(record) {
            var date = record.get('date');
            return date >= min && date <= max;
        });
    },

    /**
     * @private
     * Creates the internal {@link Ext.data.Store} that contains the available times. The store
     * is loaded with all possible times, and it is later filtered to hide those times outside
     * the minValue/maxValue.
     */
    createStore: function() {
        var me = this,
            utilDate = Ext.Date,
            times = [],
            min = me.absMin,
            max = me.absMax;

        while(min <= max){
            times.push({
                disp: utilDate.dateFormat(min, me.format),
                date: min
            });
            min = utilDate.add(min, 'mi', me.increment);
        }

        return Ext.create('Ext.data.Store', {
            fields: ['disp', 'date'],
            data: times
        });
    }

});

