var NS4 = (navigator.appName == "Netscape" && parseInt(navigator.appVersion) < 5);

function addValue( theSel, theText, theValue ) {
    var newOpt = new Option( theText, theValue );
    var selLength = theSel.length;
    theSel.options[ selLength ] = newOpt;
}

function deleteValue( theSel, theIndex ) {
    var selLength = theSel.length;
    if(selLength>0) {
        theSel.options[ theIndex ] = null;
    }
}

/**
 * Moves selected options in the source list to the target list.
 *
 * @param fromListId the id of the source list.
 * @param targetListId the id of the target list.
 */
function moveSelectedById( fromListId, targetListId ) {
    var fromList = document.getElementById( fromListId );
    var targetList = document.getElementById( targetListId );
    moveSelected( fromList, targetList );
}

/**
 * Moves selected options in the source list to the target list.
 *
 * @param fromList the source list.
 * @param targetList the target list.
 */
function moveSelected( fromList, targetList ) {

    if ( fromList.selectedIndex == -1 ) {
        return;
    }
    else {
        var selLength = fromList.length;
        var selectedText = new Array();
        var selectedValues = new Array();
        var selectedCount = 0;
        var i;

        for(i=selLength-1; i>=0; i--) {
            if(fromList.options[i].selected) {
                selectedText[selectedCount] = fromList.options[i].text;
                selectedValues[selectedCount] = fromList.options[i].value;
                deleteValue(fromList, i);
                selectedCount++;
            }
        }

        for(i=selectedCount-1; i>=0; i--) {
            addValue(targetList, selectedText[i], selectedValues[i]);
        }

        if(NS4) history.go(0)
    }
}

/**
 * Moves all elements in a list to the target list
 *
 * @param fromListId the id of the source list.
 * @param targetListId the id of target list.
 */
function moveAllById( fromListId, targetListId ) {
    $('#'+targetListId).append($('#'+fromListId+' >option'));
}

/**
 * Clears the list.
 *
 * @param listId the id of the list.
 */
function clearListById( listId ) {
    var list = document.getElementById( listId );
    clearList( list );
}

/**
 * Clears the list.
 * @param list the list.
 */
function clearList( list ) {
    list.options.length = 0;
}

/**
 * Tests whether the list contains the value.
 *
 * @param listId the id of the list.
 * @param value the value.
 */
function listContainsById( listId, value ) {
    var list = document.getElementById( listId );
    return listContains( list, value );
}

/**
 * Tests whether the list contains the value.
 *
 * @param list the list.
 * @param value the value.
 */
function listContains( list, value ) {
    for ( var i = 0; i < list.options.length; i++ ) {
        if ( list.options[i].value == value ) {
            return true;
        }
    }
    return false;
}

/**
 * Marks all elements in a list as selected.
 * @param listId the id of the list.
 */
function selectAllById( listId ) {
    var list = document.getElementById( listId );
    selectAll( list );
}

/**
 * Marks all elements in a list as selected.
 * @param list the list.
 */
function selectAll( list ) {
    for ( var i = 0; i < list.options.length; i++ ) {
        list.options[i].selected = true;
    }
}

/**
 * Marks all elements in a list as not selected.
 * @param listId the id of the list.
 */
function deselectAllById( listId ) {
    var list = document.getElementById( listId );
    deselectAll( list );
}

/**
 * Marks all elements in a list as not selected.
 * @param list the list.
 */
function deselectAll( list ) {
    for ( var i = 0; i < list.options.length; i++ ) {
        list.options[i].selected = false;
    }
}

/**
 * Adds an option to a select list.
 *
 * @param listId the id of the list.
 * @param text the text of the option.
 * @param value the value of the option.
 */
function addOption( listId, text, value ) {
    var list = document.getElementById( listId );
    var option = new Option( text, value );
    list.add( option, null );
}

/**
 * Removes the selected option from a select list.
 * @param listId the id of the list.
 */
function removeSelectedOption( listId ) {
    var list = document.getElementById( "levelNames" );
    for ( var i = list.length - 1; i >= 0; i-- ) {
        if ( list.options[ i ].selected ) {
            list.remove( i );
        }
    }
}

/**
 * Moves the selected option in a select list up one position.
 * @param listId the id of the list.
 */
function moveUpSelectedOption( listId ){
    var list = document.getElementById( listId );
    for ( var i = 0; i < list.length; i++ ) {
        if ( list.options[ i ].selected ) {
            if ( i > 0 ) {// Cannot move up the option at the top
                var precedingOption = new Option( list.options[ i - 1 ].text, list.options[ i - 1 ].value );
                var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );

                list.options[ i - 1 ] = currentOption; // Swapping place in the list
                list.options[ i - 1 ].selected = true;
                list.options[ i ] = precedingOption;
            }
        }
    }
}

/**
 * Moves the selected option in a list down one position.
 *
 * @param listId the id of the list.
 */
function moveDownSelectedOption( listId ) {
    var list = document.getElementById( listId );

    for ( var i = list.options.length - 1; i >= 0; i-- ) {
        if ( list.options[ i ].selected ) {
            if ( i <= list.options.length - 1 ) { // Cannot move down the option at the bottom
                var subsequentOption = new Option( list.options[ i + 1 ].text, list.options[ i + 1 ].value );
                var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );

                list.options[ i + 1 ] = currentOption; // Swapping place in the list
                list.options[ i + 1 ].selected = true;
                list.options[ i ] = subsequentOption;
            }
        }
    }
}

/**
 * Moves the selected options to the top of the list.
 *
 * @param listId the id of the list.
 */
function moveSelectedOptionToTop( listId ) {
    var list = document.getElementById( listId );
    var listLength = list.options.length;

    // Copy selected options to holding list and remove from main list

    var moveOptions = [];
    for ( var i = listLength - 1; i >= 0; i-- ) {
        if ( list.options[ i ].selected ) {
            moveOptions.unshift( new Option( list.options[ i ].text, list.options[ i ].value ) );
            list.remove( i );
        }
    }

    // Move options in main list down a number of positions equal to selected options

    list.options.length = listLength;

    for ( var j = listLength - moveOptions.length - 1; j >= 0; j-- ) {
        var moveOption = new Option( list.options[ j ].text, list.options[ j ].value );
        list.options[ j + moveOptions.length ] = moveOption;
    }

    // Insert options in holding list at the top of main list

    for ( var k = 0; k < moveOptions.length; k++ ) {
        list.options[ k ] = moveOptions[ k ];
        list.options[ k ].selected = true;
    }
}

/**
 * Moves the selected options to the bottom of the list.
 *
 * @param listId the id of the list.
 */
function moveSelectedOptionToBottom( listId ) {
    var list = document.getElementById( listId );

    // Copy selected options to holding list and remove from main list

    var moveOptions = [];
    for ( var i = list.options.length - 1; i >= 0; i-- ) {
        if ( list.options[ i ].selected ) {
            moveOptions.unshift( new Option( list.options[ i ].text, list.options[ i ].value ) );

            list.remove( i );
        }
    }

    // Insert options in holding list to the end of main list

    for ( var j = 0; j < moveOptions.length; j++ ) {
        list.add( moveOptions[ j ], null );
        list.options[ list.options.length - 1 ].selected = true;
    }
}

/**
 * Filters out options in a select list that don't match the filter string by
 * hiding them.
 *
 * @param filter the filter string.
 * @param listId the id of the list to filter.
 */
function filterList( filter, listId ) {
    var list = document.getElementById( listId );

    for ( var i=0; i<list.options.length; i++ ) {
        var value = list.options[i].text;

        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 ) {
            list.options[i].style.display = "block";
        }
        else {
            list.options[i].style.display = "none";
        }
    }
}

/**
 * Clears a filter and resets the filtered select list.
 *
 * @param filterId the id of the filter input box.
 * @param listId the id of the list reset after being filtered.
 */
function clearFilter( filterId, listId ) {
    document.getElementById( filterId ).value = "";
    filterList( "", listId );
}

/**
 * Check if list has option.
 *
 * @param obj is list object
 */
function hasOptions( obj ) {
    if ( obj != null && obj.options != null ) {
        return true;
    }
    return false;
}

/**
 * Sort list by name.
 *
 * @param id is id of list
 * @param type is type for sort ASC:ascending && DES: desending
 */
function sortList( id, type ) {
    var obj = document.getElementById( id );
    var o = new Array();
    if (!hasOptions(obj)) {
        return;
    }
    for (var i=0; i<obj.options.length; i++) {
        o[o.length] = new Option( obj.options[i].text, obj.options[i].value ) ;
    }
    if (o.length==0) {
        return;
    }
    if(type=='ASC'){
        o = o.sort(
            function(a,b) {
                if ((a.text+"") < (b.text+"")) {
                    return -1;
                }
                if ((a.text+"") > (b.text+"")) {
                    return 1;
                }
                return 0;
            });
    }
    if(type=='DES'){
        o = o.sort(
            function(a,b) {
                if ((a.text+"") < (b.text+"")) {
                    return 1;
                }
                if ((a.text+"") > (b.text+"")) {
                    return -1;
                }
                return 0;
            });
    }
    for (var i=0; i<o.length; i++) {
        obj.options[i] = new Option(o[i].text, o[i].value);
    }
}