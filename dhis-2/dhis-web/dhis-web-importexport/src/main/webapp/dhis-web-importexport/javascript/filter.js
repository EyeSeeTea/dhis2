// Global Variables
var filters = [];

// -----------------------------------------------------------------------------
// Load Filters
// -----------------------------------------------------------------------------
jQuery( function loadFilters()
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
                    insertFilterDesign();
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
);

// Create a new Filter form
function submitFilterForm()
{
    $( "input[name='operation']" ).val( "create" );
    $( "#formFilter" ).submit();
}

// Populate the Filter rows with values
function insertFilterDesign()
{
    for ( var i = 0; i < filters.length; i++ )
    {
        var filterId = filters[i].id;
        var filterName = removeWhiteSpace( filters[i].name );
        var design =
                 '<tr id="tr' + filterName + '">'
                +     '<td>' + filters[i].name + '</td>'
                +     '<td>'
                +         '<button id="buttonExport' + filterName + '" type="button" style="background-color: inherit; border: 0px; cursor: pointer;">'
                +             '<img src="../images/start_process.png" title="' + i18n_export + '" alt="' + i18n_export + '" />'
                +         '</button>'
                +         '<button id="buttonEdit' + filterName + '" type="button" style="background-color: inherit; border: 0px; cursor: pointer;">'
                +             '<img src="../images/edit.png" title="' + i18n_edit + '" alt="' + i18n_edit + '" />'
                +         '</button>'
                +         '<button id="buttonRemove' + filterName + '" type="button" style="background-color: inherit; border: 0px; cursor: pointer;">'
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
//    applyFilterButton( filter );
    exportFilterButton( filter );
    editFilterButton( filter );
    removeFilterButton( filter );
}

// Export detailed MetaData by applied Filter
function exportFilterButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonExport" + filterName ).live( "click", function ()
    {
        $( "#exportJson" ).attr( "value", filter.metaDataUids );
        $( "#exportDialog" ).dialog();
    } );
}

// -----------------------------------------------------------------------------
// Export Detailed MetaData
// -----------------------------------------------------------------------------
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

// Apply a Filter to the MetaData Tables
function applyFilterButton( filter )
{
//    var filterName = removeWhiteSpace( filter.name );
//    $( "#buttonApply" + filterName ).live( "click", function ()
//    {
//        selectAllCheckboxes();
//        $( document ).ajaxStop( function ()
//        {
//            applyFilter( filter );
//        } );
//        applyFilter( filter )
//    } );
}

// Apply a Filter to the tables by selecting the uids from the Filter
//function applyFilter( filter )
//{
//    var uids = (filter.metaDataUids).split( ", " );
//    for ( var i = 0; i < uids.length; i++ )
//    {
//        for ( var j = 0; j < metaDataArray.length; j++ )
//        {
//            $( "#available" + metaDataArray[j] + " option" ).each( function ()
//            {
//                var availableUid = $( this ).val();
//                if ( uids[i] == availableUid )
//                {
//                    $( this ).prop( "selected", true );
//                    uids.splice( i, 1 );
//                }
//            } );
//            moveSelectedValuesByCategory( metaDataArray[j] );
//        }
//    }
//}

// -----------------------------------------------------------------------------
// Edit a Filter
// -----------------------------------------------------------------------------
function editFilterButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    var filterId = filter.id;
    $( "#buttonEdit" + filterName ).live( "click", function ()
    {
        for ( var i = 0; i < filters.length; i++ )
        {
            if ( filters[i].id == filterId )
            {
                $( "input[name='name']" ).val( filters[i].name );
                $( "input[name='code']" ).val( filters[i].code );
                $( "input[name='uid']" ).val( filters[i].id );
                $( "input[name='metaDataUids']" ).val( filters[i].metaDataUids );
                $( "input[name='operation']" ).val( "update" );
            }
        }

        $( "#formFilter" ).submit();
    } );
}

//// Get all the selected uids from the tables
//function getSelectedUids()
//{
//    var selectedUidsArray = [];
//    for ( var i = 0; i < metaDataArray.length; i++ )
//    {
//        $( "#selected" + metaDataArray[i] + " option" ).each( function ()
//        {
//            selectedUidsArray.push( $( this ).attr( "value" ) );
//        } );
//    }
//
//    return selectedUidsArray;
//}
//
//// Add the Selected Uids to the existing Uids that a Filter has
//function getAllUids( existingUidsArray, selectedUidsArray )
//{
//    for ( var i = 0; i < existingUidsArray.length; i++ )
//    {
//        for ( var j = 0; j < selectedUidsArray.length; j++ )
//        {
//            if ( selectedUidsArray[j] == existingUidsArray[i] )
//            {
//                selectedUidsArray.splice( j, 1 );
//            }
//        }
//    }
//
//    var finalUidsArray = existingUidsArray.concat( selectedUidsArray );
//    var finalUids = "";
//    for ( var k = 0; k < finalUidsArray.length; k++ )
//    {
//        finalUids += finalUidsArray[k] + ", ";
//    }
//    finalUids = removeLastComma( finalUids );
//
//    return finalUids;
//}

// -----------------------------------------------------------------------------
// Delete a Filter
// -----------------------------------------------------------------------------
function removeFilterButton( filter )
{
    var filterName = removeWhiteSpace( filter.name );
    $( "#buttonRemove" + filterName ).live( "click", function ()
    {
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
                    $( "#tr" + filterName ).remove();
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
