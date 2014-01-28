"use strict";
/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Menu service
 */
(function (dhis2, undefined) {
    /**
     * Simple service object that calls callbacks when the object is updated
     */
    dhis2.service = function (serviceFor) {
        var that = {},
            serviced = serviceFor || undefined,
            callBacks = [], //Array of callbacks to call when serviced is updated
            onceCallBacks = [];

        /***********************************************************************
         * Private methods
         **********************************************************************/

        /**
         * Execute any callbacks that are set onto the callbacks array
         */
        function executeCallBacks() {
            var onceCallBack, callBackIndex;

            //If there is nothing to service we don't do anything
            if (serviced === undefined)
                return false;

            //Execute the single time callbacks
            while (onceCallBacks.length !== 0) {
                onceCallBack = onceCallBacks.pop();
                onceCallBack(serviced);
            }

            for (callBackIndex in callBacks) {
                callBacks[callBackIndex](serviced);
            }
        };

        /***********************************************************************
         * Public methods
         **********************************************************************/

        /**
         * Set a new serviced object
         *
         * @param array sets new modules array
         */
        that.setServiced = function (newServiced) {
            serviced = newServiced;
            executeCallBacks();
        };

        that.getServiced = function () {
            return serviced;
        }

        /**
         * Subscribe to the service
         *
         * @param callback {function} Function that should be run when service gets updated
         * @param onlyOnce {boolean} Callback should only be run once on the next update
         * @returns boolean Returns false when callback is not a function
         */
        that.subscribe = function (callback, onlyOnce) {
            var once = onlyOnce ? true : false;
            if (typeof callback !== 'function')
                return false;

            if (serviced !== undefined) {
                callback(serviced);
            }

            if (true === once) {
                onceCallBacks.push(callback);
            } else {
                callBacks.push(callback);
            }
            return true;
        };

        return that;
    };
})(dhis2);

/**
 * jQuery template
 */
(function ($, dhis2) {
    var menu = dhis2.menu = dhis2.menu || {},
        markup = '';

    markup += '<li data-app-name="${name}" data-app-action="${defaultAction}">';
    markup += '<img src="${icon}">';
    markup += '<span>${name}</span>';
    markup += '<p>${description}</p>';
    markup += '</li>';

    $.template('appMenuItemTemplate', markup);

    menu.service = menu.service || dhis2.service([]);
    menu.selector = '#appsMenu';

    menu.getFavorites = function () {
        return menu.service.getServiced().slice(0,9);
    }
    menu.getApps = function () {
        return menu.service.getServiced().slice(9);
    }

    function renderFavorites(appList) {
        var favorites = [];
        favorites = dhis2.menu.getFavorites();

        return $.tmpl( "appMenuItemTemplate", favorites).appendTo(dhis2.menu.selector + '_favorites');
    }

    function renderNotFavorites(appList) {
        var apps = [];
        apps = dhis2.menu.getApps();
        return $.tmpl( "appMenuItemTemplate", apps).appendTo(dhis2.menu.selector);
    }

    //Subscribe to the list and run the callbacks only once on the first update
    menu.service.subscribe(renderFavorites, true);
    menu.service.subscribe(renderNotFavorites, true);

})(jQuery, dhis2);

/**
 * jQuery event hookups
 */
(function ($, dhis2, undefined) {
    var service = dhis2.menu.service ? dhis2.menu.service : dhis2.menu.service = dhis2.service([]);
    /**
     * jQuery events that communicate with the web api
     * TODO: Check the urls (they seem to be specific to the dev location atm)
     */
    $(function () {
        $.ajax('../dhis-web-commons/menu/getModules.action').success(function (data) {
            if (typeof data.modules === 'object') {
                dhis2.translate
                service.setServiced(data.modules);
            }
        }).error(function () {
                alert('Can not load apps from server.');
            });
    });
})(jQuery, dhis2);
