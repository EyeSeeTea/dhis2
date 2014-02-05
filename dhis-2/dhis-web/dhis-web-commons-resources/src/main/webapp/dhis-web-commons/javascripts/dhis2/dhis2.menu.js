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
    var MAX_FAVORITES = 9;

    dhis2.util.namespace( 'dhis2.menu' );

    dhis2.menu = function () {
        var that = {},
            menuReady = false,
            menuItems = undefined,
            callBacks = [], //Array of callbacks to call when serviced is updated
            onceCallBacks = [];

        /***********************************************************************
         * Private methods
         **********************************************************************/

        function processTranslations(translations) {
            var itemIndex,
                items = dhis2.menu.getItems();

            for (itemIndex in items) {
                if (translations[items[itemIndex].id]) {
                    items[itemIndex].name = translations.get(items[itemIndex].id);
                }
                if (items[itemIndex].description === '' && translations.get('intro_' + items[itemIndex].id) !== items[itemIndex].id) {
                    items[itemIndex].description = translations['intro_' + items[itemIndex].id];
                }
            }
            setReady();
        }

        function setReady() {
            menuReady = true;
            executeCallBacks();
        }

        function isReady() {
            return menuReady;
        }

        /**
         * Execute any callbacks that are set onto the callbacks array
         */
        function executeCallBacks() {
            var onceCallBack, callBackIndex;

            //If not ready or no menu items
            if ( ! isReady() || menuItems === undefined)
                return false;

            //Execute the single time callbacks
            while (onceCallBacks.length !== 0) {
                onceCallBack = onceCallBacks.pop();
                onceCallBack(menuItems);
            }

            for (callBackIndex in callBacks) {
                callBacks[callBackIndex](menuItems);
            }
        }

        /***********************************************************************
         * Public methods
         **********************************************************************/

        /**
         * Get the max number of favorites
         */
        that.getMaxFavorites = function () {
            return MAX_FAVORITES;
        }

        /**
         * Get the current menuItems
         */
        that.getItems = function () {
            return menuItems;
        };

        /**
         * Order the menuItems by a given list
         *
         * @param orderedIdList
         * @returns {{}}
         */
        that.orderMenuItemsByList = function (orderedIdList) {
            var apps = dhis2.menu.getApps(),
                reorderedApps = [],
                appIndex, orderIndex;

            for (orderIndex in orderedIdList) {
                for (appIndex in apps) {
                    if (orderedIdList[orderIndex] === apps[appIndex].id) {
                        reorderedApps.push(apps[appIndex]);
                    }
                }
            }

            menuItems = reorderedApps;

            return that;
        };

        /**
         * Adds the menu items given to the menu
         */
        that.addMenuItems = function (items) {
            var itemIndex,
                currentItem,
                keysToTranslate = [];

            for (itemIndex in items) {
                currentItem = items[itemIndex];
                currentItem.id = currentItem.name;
                keysToTranslate.push(currentItem.name);
                if (currentItem.description === "") {
                    keysToTranslate.push( "intro_" + currentItem.name );
                }
            }

            menuItems = items;

            dhis2.translate.get(keysToTranslate, processTranslations);
        };

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

            if (menuItems !== undefined) {
                callback(menuItems);
            }

            if (true === once) {
                onceCallBacks.push(callback);
            } else {
                callBacks.push(callback);
            }
            return true;
        };

        that.getFavorites = function () {
            return menuItems.slice(0, MAX_FAVORITES);
        }
        that.getApps = function () {
            return menuItems;
        }

        return that;
    }();
})(dhis2);

/**
 * jQuery template
 */
(function ($, menu, undefined) {
    var markup = '';

    markup += '<li data-id="${id}" data-app-name="${name}" data-app-action="${defaultAction}">';
    markup += '  <a href="${defaultAction}" class="app-menu-item" title="${name}">';
    markup += '    <img src="${icon}">';
    markup += '    <span>${name}</span>';
    markup += '    <div class="app-menu-item-description"><span>${name}</span><i class="fa fa-arrows"></i>${description}</div>';
    markup += '  </a>';
    markup += '</li>';

    $.template('appMenuItemTemplate', markup);

    function renderDropDownFavorites() {
        var selector = '#menuDropDown1 .menuDropDownBox',
            favorites = dhis2.menu.getFavorites();

        $(selector).parent().addClass('app-menu-dropdown ui-helper-clearfix');
        $(selector).html('');
        return $.tmpl( "appMenuItemTemplate", favorites).appendTo(selector);
    }

    function renderAppManager(selector) {
        var apps = dhis2.menu.getApps();
        $('#' +  selector).append($('<ul></ul>').addClass('ui-helper-clearfix'));
        $('#' + selector).addClass('app-menu');
        $.tmpl( "appMenuItemTemplate", apps).appendTo('#' + selector + ' ul');

        //Add favorites icon to all the menu items in the manager
        $('#' + selector + ' ul li').each(function (index, item) {
            $(item).children('a').append($('<i class="fa fa-bookmark"></i>'));
        });
    }

    function renderMenu() {
        var selector = 'appsMenu',
            options = {
                placeholder: 'app-menu-placeholder',
                connectWith: '.app-menu ul',
                update: function (event, ui) {
                    var reorderedApps = $("#" + selector + " ul"). sortable('toArray', {attribute: "data-id"});
                    dhis2.menu.orderMenuItemsByList(reorderedApps);

                    renderDropDownFavorites();
                },
                containment: 'parent'
            };

        renderAppManager(selector);
        renderDropDownFavorites();

        $('.app-menu ul').sortable(options).disableSelection();
    }

    //Subscribe to the list and run the callbacks only once on the first update
    menu.subscribe(renderMenu, true);

})(jQuery, dhis2.menu);

/**
 * jQuery event hookups
 */
(function ($, menu, undefined) {
    /**
     * jQuery events that communicate with the web api
     * TODO: Check the urls (they seem to be specific to the dev location atm)
     */
    $(function () {
        $.ajax('../dhis-web-commons/menu/getModules.action').success(function (data) {
            if (typeof data.modules === 'object') {
                menu.addMenuItems(data.modules);
            }
        }).error(function () {
                alert('Can not load apps from server.');
            });
    });

})(jQuery, dhis2.menu);
