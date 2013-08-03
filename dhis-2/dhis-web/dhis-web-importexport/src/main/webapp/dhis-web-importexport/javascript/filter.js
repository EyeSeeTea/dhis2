// Global Variables

// --------------------------------------------------------------
var filters = [];
// --------------------------------------------------------------

// Create a new Filter
function createFilter()
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
                filters = response;
                console.log( "Loaded filters: " + JSON.stringify( filters ) );
                insertFilterDesign( filters );
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
        var filterCreated = filters[i].created;
        var design =
                      '<tr id="tr' + filterName +'" style="margin: 20px;">'
                    +      '<td>'
                    +          '<p style="padding-left: 10px; font-size: 10pt;">' + filters[i].name + '</p>'
                    +      '</td>'
                    +      '<td style="float: left;">'
                    +          '<button id="buttonApply' + filterName + '" value="' + filterId + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/start_process.png" alt="' + i18n_apply + '" )"/>'
                    +          '</button>'
                    +      '</td>'
                    +      '<td style="float: right;">'
                    +          '<form id="form' + filterName + '" action="updateFilterExportForm.action">'
                    +                '<input type="hidden" name="name" value="' + filters[i].name + '" />'
                    +                '<input type="hidden" name="uid" value="' + filterId + '" />'
                    +                '<input type="hidden" name="code" value="' + filterCode + '" />'
                    +                '<input id="metaDataUids' + filterName + '" type="hidden" name="metaDataUids" value="" />'
                    +          '</form>'
                    +          '<button id="buttonEdit' + filterName + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/edit.png" alt="' + i18n_edit + '"/>'
                    +          '</button>'
                    +          '<button id="buttonRemove' + filterName + '" value="' + filterId + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/delete.png" alt="' + i18n_remove + '" />'
                    +          '</button>'
                    +      '</td>'
                    + '</tr>'
            ;

        filterButtonEvents( filters[i] );

        $( "#filterTable" ).append( design );
        if ( i % 2 === 0 )
        {
            $( "#tr" + filterName ).css( "background-color", "#EEF7FA" );
        }
    }
}

// Add Filter button events
function filterButtonEvents( filter )
{
    filterApplyButton( filter );
    filterEditButton( filter );
    filterRemoveButton( filter );
}

// Apply a Filter to the MetaData Tables
function filterApplyButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonApply" + filterName ).live( "click", function ()
    {
        for ( var j = 0; j < metaDataArray.length; j++ )
        {
            if ( $( "#mainDiv" + metaDataArray[j] ).children().length > 0 )
            {
                var id = $( this ).attr( "value" );

                for ( var i = 0; i < filters.length; i++ )
                {
                    if ( id === filters[i].id )
                    {
                        applyFilter( filters[i].metaDataUids );
                    }
                }
                $( "#appliedFilterMessage" ).text( filter.name + " applied !" ).css( {"color": "darkorange"} );
            }
        }
    } );
}

// Apply a Filter to the tables by selecting the uids from the Filter
function applyFilter( data )
{
    var uids = data.split( ", " );

    for ( var i = 0; i < uids.length; i++ )
    {
        for ( var j = 0; j < metaDataArray.length; j++ )
        {
            $( "#available" + metaDataArray[j] + " option" ).each( function ()
            {
                var availableUid = $( this ).val();
                if ( uids[i] === availableUid )
                {
                    $( this ).prop( "selected", true );
                }
            } );
        }
    }

    for ( var k = 0; k < metaDataArray.length; k++ )
    {
        moveSelectedValuesByCategory( metaDataArray[k] );
    }
}

// Edit an existing Filter
function filterEditButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonEdit" + filterName ).live( "click", function ()
    {
        var selectedUids = getSelectedUids();
        $( "#metaDataUids" + filterName ).attr( "value", selectedUids );
        $( "#form" + filterName ).submit();
    } );
}

// Get all the selected uids from the tables
function getSelectedUids()
{
    var selectedUids = "";
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#selected" + metaDataArray[i] + " option" ).each( function ()
        {
            selectedUids += $( this ).attr( "value" ) + ", ";
        } );
    }
    selectedUids = removeLastComma( selectedUids );
    return selectedUids;
}

// Delete a Filter from the database
function filterRemoveButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonRemove" + filterName ).live( "click", function ()
    {
        var id = ($( this ).attr( "value" ));
        for ( var i = 0; i < filters.length; i++ )
        {
            if ( id === filters[i].id )
            {
                var json = replaceIdWithUid( filters[i] );
                console.log( "Deleted json: " + JSON.stringify( json ) );
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
            }
        }
    } );
}
