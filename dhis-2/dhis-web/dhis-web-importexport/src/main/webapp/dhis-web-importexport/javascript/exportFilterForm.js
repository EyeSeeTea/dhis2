// Save the Filter in the database
function saveFilter()
{
    var json = JSON.stringify( createFilter() );

    $.ajax(
        {
            type: "POST",
            url: "../api/detailedMetaData/saveFilter",
            contentType: "application/json",
            data: json,
            success: function ()
            {
                console.log( "Filter successfully saved." );
                alert( "Filter saved !" );
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Save filter process failed." );
            }
        } );
}

// Update an existing Filter in the database
function updateFilter()
{
    var filter = createFilter();
    filter.uid = $( "#uid" ).attr( "value" );
//    filter.created = $( "#created" ).attr( "value" );

    var json = JSON.stringify( filter );

    $.ajax(
        {
            type: "POST",
            url: "../api/detailedMetaData/updateFilter",
            contentType: "application/json",
            data: json,
            success: function ()
            {
                console.log( "Filter successfully saved." );
                alert( "Filter updated !" );
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Update filter process failed." );
            }
        } );
}

// Create a Filter object
function createFilter()
{
    var filter = {};
    filter.name = $( "#name" ).attr( "value" );
    filter.code = $( "#code" ).attr( "value" );
    filter.metaDataUids = $( "#metaDataUids" ).attr( "value" );

    return filter;
}
