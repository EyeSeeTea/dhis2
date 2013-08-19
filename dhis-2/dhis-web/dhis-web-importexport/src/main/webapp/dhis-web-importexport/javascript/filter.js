// Global Variables
var filters = [];

// -----------------------------------------------------------------------------
// Document ready
// -----------------------------------------------------------------------------
jQuery( function ()
    {
        tableSorter( "filterList" );
        loadFilters();
    }
);

// Create a new Filter form
function submitFilterForm()
{
    $( "input[name='operation']" ).val( "addNew" );
    $( "#formFilter" ).submit();
}

// -----------------------------------------------------------------------------
// Load Filters
// -----------------------------------------------------------------------------
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
                filters = response;
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Getting filters process failed." );
            }
        } );
}

// Show Filter details
function showFilterDetails( filterUid )
{
    $("#detailsArea" ).show();
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            setInnerHTML( 'nameField', filters[i].name );
        }
    }
}

// -----------------------------------------------------------------------------
// Export Detailed MetaData
// -----------------------------------------------------------------------------
function exportFilterButton( filterUid )
{
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            $( "#exportJson" ).attr( "value", filters[i].metaDataUids );
            $( "#exportDialog" ).dialog();
        }
    }
}

// Export MetaData
function exportDetailedMetaData()
{
    var json = $( "#exportJson" ).val();
    var url = getURL();
    $.ajax(
        {
            type: "POST",
            url: url,
            data: json,
            contentType: "application/json",
            dataType: "xml",
            success: function ()
            {
                console.log( "Exported JSON: " + json );
                $( "#exportDialog" ).dialog( "close" );
                window.location = "../api/detailedMetaData/getMetaDataFile";
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Export process failed." );
            }
        } );
}

// Generate Export URL
function getURL()
{
    var url = "../api/detailedMetaData";
    var format = $( "#format" ).val();
    var compression = $( "#compression" ).val();
    url += "/set" + format;

    if ( compression == "zip" )
    {
        url += "Zip";
    }
    else if ( compression == "gz" )
    {
        url += "Gz";
    }

    return url;
}

// -----------------------------------------------------------------------------
// Edit a Filter
// -----------------------------------------------------------------------------
function editFilterButton( filterUid )
{
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            $( "input[name='name']" ).val( filters[i].name );
            $( "input[name='code']" ).val( filters[i].code );
            $( "input[name='uid']" ).val( filters[i].id );
            $( "input[name='metaDataUids']" ).val( filters[i].metaDataUids );
            $( "input[name='operation']" ).val( "update" );
        }
    }

    $( "#formFilter" ).submit();
}

// -----------------------------------------------------------------------------
// Delete a Filter
// -----------------------------------------------------------------------------
function removeFilterButton( filterUid )
{
    var filter = {};
    for ( var i = 0; i < filters.length; i++ )
    {
        if ( filters[i].id == filterUid )
        {
            filter = filters[i];
        }
    }

    var json = JSON.stringify( replaceIdWithUid( filter ) );
    $.ajax(
        {
            type: "POST",
            url: "../api/detailedMetaData/deleteFilter",
            contentType: "application/json",
            data: json,
            success: function ()
            {
                console.log( "Filter successfully removed." );
                $( "#tr" + filter.uid ).remove();
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Remove filter process failed." );
            }
        } );
}

// -----------------------------------------------------------------------------
// Utils
// -----------------------------------------------------------------------------

// Replace id with uid
function replaceIdWithUid( object )
{
    object.uid = object.id;
    delete object.id;
    return object;
}
