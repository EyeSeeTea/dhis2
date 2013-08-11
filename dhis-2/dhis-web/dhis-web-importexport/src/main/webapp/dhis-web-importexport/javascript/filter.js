// Submit the Create Filter Form
function submitCreateFilterForm()
{
    $( "#formCreateFilter" ).submit();
}

// Load Filters from the database
function loadFilters()
{
    $.ajax(
        {
            type: "GET",
            url: "../api/detailedMetaData/getFilters",
            dataType: "json",
            success: function ( response )
            {
                console.log( "Loaded filters: " + JSON.stringify( response ) );
                insertFilterDesign( response );
                setTableStyles();
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Getting filters process failed." );
            }
        } );
}

// Populate the Filter rows with values
function insertFilterDesign( filters )
{
    for ( var i = 0; i < filters.length; i++ )
    {
        var filterId = filters[i].id;
        var filterName = removeWhiteSpace( filters[i].name );
        var filterCode = filters[i].code;
        var filterMetaDataUids = filters[i].metaDataUids;
        var design =
                 '<tr id="tr' + filterName + '">'
                +     '<td>' + filters[i].name + '</td>'
                +     '<td>'
                +         '<button id="buttonApply' + filterName + '" value="' + filterId + '" type="button" style="background-color: inherit; border: 0px;">'
                +             '<img src="../images/success_small.png" title="' + i18n_apply + '" alt="' + i18n_apply + '" />'
                +         '</button>'
                +         '<form id="form' + filterName + '" method="POST" action="updateFilterExportForm.action" style="float: left;">'
                +             '<input type="hidden" name="name" value="' + filters[i].name + '" />'
                +             '<input type="hidden" name="uid" value="' + filterId + '" />'
                +             '<input type="hidden" name="code" value="' + filterCode + '" />'
                +             '<input type="hidden" id="metaDataUids' + filterName + '" name="metaDataUids" value="' + filterMetaDataUids + '" />'
                +         '</form>'
                +         '<button id="buttonEdit' + filterName + '" type="button" style="background-color: inherit; border: 0px;">'
                +             '<img src="../images/edit.png" title="' + i18n_edit + '" alt="' + i18n_edit + '" />'
                +         '</button>'
                +         '<button id="buttonRemove' + filterName + '" value="' + filterId + '" type="button" style="background-color: inherit; border: 0px;">'
                +             '<img src="../images/delete.png" title="' + i18n_remove + '" alt="' + i18n_remove + '" />'
                +         '</button>'
                +     '</td>'
                + '</tr>'
        ;

        filterButtonEvents( filters[i] );
        $( "#filterTableBody" ).append( design );
    }
}

// Add Filter button events
function filterButtonEvents( filter )
{
    applyFilterButton( filter );
    editFilterButton( filter );
    removeFilterButton( filter );
}

// Apply a Filter to the MetaData Tables
function applyFilterButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonApply" + filterName ).live( "click", function ()
    {
        applyFilter( filter );
    } );
}

// Apply a Filter to the tables by selecting the uids from the Filter
function applyFilter( filter )
{
    var uids = (filter.metaDataUids).split( ", " );
    var uidsLength = uids.length;
    var filterName = filter.name;
    for ( var i = 0; i < uidsLength; i++ )
    {
        for ( var j = 0; j < metaDataArray.length; j++ )
        {
            $( "#available" + metaDataArray[j] + " option" ).each( function ()
            {
                var availableUid = $( this ).val();
                if ( uids[i] == availableUid )
                {
                    $( this ).prop( "selected", true );
                    uids.splice( i, 1 );
                }
            } );
            moveSelectedValuesByCategory( metaDataArray[j] );
        }
    }
    filterStatus( filterName, uids, uidsLength );
}

// Filter status after being applied
function filterStatus( filterName, uids, uidsLength )
{
    var remainingUidsLength = uids.length;
    if ( remainingUidsLength == 0 )
    {
        $( "#appliedFilterMessage" ).text( filterName + " applied !" ).css( {"color": "darkorange"} );
        $( "#unappliedUids" ).text( "" );
    } else if ( remainingUidsLength < uidsLength )
    {
        $( "#appliedFilterMessage" ).text( filterName + " partially applied !" ).css( {"color": "darkorange"} );
        $( "#unappliedUids" ).text( "Remaining Filter Uids: " + uidsToString( uids ) );
    } else if ( remainingUidsLength == uidsLength )
    {
        $( "#appliedFilterMessage" ).text( i18n_no_filter_applied ).css( {"color": "black"} );
        $( "#unappliedUids" ).text( "" );
    }
}

// Convert an Uid array to string
function uidsToString( uids )
{
    var uidsString = "";
    for ( var i = 0; i < uids.length; i++ )
    {
        uidsString += uids[i] + ", ";
    }

    return removeLastComma( uidsString );
}

// Edit an existing Filter
function editFilterButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonEdit" + filterName ).live( "click", function ()
    {
        var existingUidsArray = (filter.metaDataUids).split( ", " );
        existingUidsArray = removeEmptyStringFromArray( existingUidsArray );
        var selectedUidsArray = getSelectedUids();
        var metaDataUids = getAllUids( existingUidsArray, selectedUidsArray );

        $( "#metaDataUids" + filterName ).attr( "value", metaDataUids );
        $( "#form" + filterName ).submit();
    } );
}

// Get all the selected uids from the tables
function getSelectedUids()
{
    var selectedUidsArray = [];
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#selected" + metaDataArray[i] + " option" ).each( function ()
        {
            selectedUidsArray.push( $( this ).attr( "value" ) );
        } );
    }

    return selectedUidsArray;
}

// Add the Selected Uids to the existing Uids that a Filter has
function getAllUids( existingUidsArray, selectedUidsArray )
{
    for ( var i = 0; i < existingUidsArray.length; i++ )
    {
        for ( var j = 0; j < selectedUidsArray.length; j++ )
        {
            if ( selectedUidsArray[j] == existingUidsArray[i] )
            {
                selectedUidsArray.splice( j, 1 );
            }
        }
    }

    var finalUidsArray = existingUidsArray.concat( selectedUidsArray );
    var finalUids = "";
    for ( var k = 0; k < finalUidsArray.length; k++ )
    {
        finalUids += finalUidsArray[k] + ", ";
    }
    finalUids = removeLastComma( finalUids );

    return finalUids;
}

// Delete a Filter from the database
function removeFilterButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonRemove" + filterName ).live( "click", function ()
    {
        var json = replaceIdWithUid( filter );
        $.ajax(
            {
                type: "POST",
                url: "../api/detailedMetaData/deleteFilter",
                contentType: "application/json",
                data: JSON.stringify( json ),
                success: function ()
                {
                    $( "#tr" + filterName ).remove();
                    console.log( "Filter successfully removed." );
                },
                error: function ( request, status, error )
                {
                    console.log( request.responseText );
                    console.log( arguments );
                    alert( "Remove filter process failed." );
                }
            } );
    } );
}
