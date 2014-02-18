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
            menuItems = undefined, //TODO: If this is change to an object with properties we can eliminate some for loops
            callBacks = [], //Array of callbacks to call when serviced is updated
            onceCallBacks = [];

        /***********************************************************************
         * Private methods
         **********************************************************************/

        function processTranslations(translations) {
            var itemIndex,
                items = dhis2.menu.getApps();

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

        that.updateFavoritesFromList = function (orderedIdList) {
            var apps = that.getApps(),
                newFavsIds = orderedIdList.slice(0, MAX_FAVORITES),
                favorites = [],
                appsOnTop = [],
                appIndex = 0;

            console.log(orderedIdList);

            for (appIndex in apps) {
                if (appIndex < MAX_FAVORITES) {
                    console.log(apps[appIndex].name);
                }
            }

            /*
            var apps = that.getApps(),
                newFavsIds = orderedIdList.slice(0, MAX_FAVORITES),
                favorites = [],
                appsOnTop = [],
                nonFavorites = apps.slice(MAX_FAVORITES),
                orderIndex, appIndex, nonFavoriteIndex;

            //Use the new ordered list for for the new favorites
            for (orderIndex in newFavsIds) {
                for (appIndex in apps) {
                    //If it's a favorite add to the list of favorites else to nonFavorites
                    if (orderIndex < MAX_FAVORITES && orderedIdList[orderIndex] === apps[appIndex].id) {
                        favorites.push(apps[appIndex]);
                    }
                }
            }

            //Remove the item that got added to list of favorites from the non favorites
            for (nonFavoriteIndex in apps) {
                if (orderedIdList[9] === apps[nonFavoriteIndex].id) {
                    console.log(nonFavoriteIndex);
                    console.log(apps.slice(nonFavoriteIndex, nonFavoriteIndex+1));
                    appsOnTop = apps.slice(nonFavoriteIndex, nonFavoriteIndex+1);
                    console.log(appsOnTop);
                }
            }

            //Create a new list of menuItems with the new favorites, the old favorite on top
            // and the rest of the custom items
            menuItems = favorites.concat(appsOnTop).concat(nonFavorites);

            //console.log(favorites);
            //console.log(appsOnTop);
            //console.log(nonFavorites);
            */
            return that;
        }

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

        /**
         * Get the favorite apps
         *
         * @returns {Array}
         */
        that.getFavorites = function () {
            return menuItems.slice(0, MAX_FAVORITES);
        };

        /**
         * Get the current menuItems
         */
        that.getApps = function () {
            return menuItems;
        };

        /**
         * Get non favorite apps
         */
        that.getNonFavoriteApps = function () {
            return menuItems.slice(MAX_FAVORITES);
        };

        return that;
    }();
})(dhis2);

/**
 * jQuery template
 */
(function ($, menu, undefined) {
    var displayOrder = 'custom',
        markup = '',
        selector = 'appsMenu';

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
        var apps = getOrderedAppList();
        $('#' +  selector).html('');
        $('#' +  selector).append($('<ul></ul>').addClass('ui-helper-clearfix'));
        $('#' + selector).addClass('app-menu');
        $.tmpl( "appMenuItemTemplate", apps).appendTo('#' + selector + ' ul');

        //Add favorites icon to all the menu items in the manager
        $('#' + selector + ' ul li').each(function (index, item) {
            $(item).children('a').append($('<i class="fa fa-bookmark"></i>'));
        });
    }

    /**
     * Render the menumanager and the dropdown meny and attach the update handler
     */
    //TODO: Rename this as the name is not very clear to what it does
    function renderMenu() {
        var options = {
                placeholder: 'app-menu-placeholder',
                connectWith: '.app-menu ul',
                update: function (event, ui) {
                    var reorderedApps = $("#" + selector + " ul"). sortable('toArray', {attribute: "data-id"});

                    switch (displayOrder) {
                        case 'name-asc':
                        case 'name-desc':
                            dhis2.menu.updateFavoritesFromList(reorderedApps);
                            break;

                        default:
                            //Update the menu object with the changed order
                            dhis2.menu.orderMenuItemsByList(reorderedApps);
                            break;
                    }

                    //Render the dropdown menu
                    renderDropDownFavorites();
                },
                //Constrict the draggable elements to the parent element
                containment: 'parent'
            };

        renderAppManager(selector);
        renderDropDownFavorites();

        $('.app-menu ul').sortable(options).disableSelection();
    }

    /**
     * Gets the applist based on the current display order
     *
     * @returns {Array} Array of app objects
     */
    function getOrderedAppList() {
        var favApps = dhis2.menu.getFavorites(),
            nonFavApps = dhis2.menu.getNonFavoriteApps();

        switch (displayOrder) {
            case 'name-asc':
                nonFavApps = sortAppsByName(nonFavApps);
                break;
            case 'name-desc':
                nonFavApps = sortAppsByName(nonFavApps, true);
                break;
        }

        return favApps.concat(nonFavApps);;
    }

    //TODO: Function seems complicated and can be improved perhaps
    /**
     * Sort apps (objects with a name property) by name
     *
     * @param apps
     * @param inverse Return the elements in an inverted order (DESC sort)
     * @returns {Array}
     */
    function sortAppsByName(apps, inverse) {
        var smaller = [],
            bigger = [],
            center = Math.floor(apps.length / 2),
            appIndex,
            comparisonResult,
            result;

        //If nothing left to sort return the app list
        if (apps.length <= 1)
            return apps;

        center = apps[center];
        for (appIndex in apps) {
            comparisonResult = center.name.localeCompare(apps[appIndex].name);
            if (comparisonResult === -1) {
                bigger.push(apps[appIndex]);
            }
            if (comparisonResult === 1) {
                smaller.push(apps[appIndex]);
            }
        }

        smaller = sortAppsByName(smaller);
        bigger = sortAppsByName(bigger);

        result = smaller.concat([center]).concat(bigger);
        return inverse ? result.reverse() : result;
    }

    menu.subscribe(renderMenu);

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

        $('#menuOrderBy').change(function (event) {
            var orderBy = $(event.target).val();

            displayOrder = orderBy;

            renderMenu(selector);
        });
    });

})(jQuery, dhis2.menu);
