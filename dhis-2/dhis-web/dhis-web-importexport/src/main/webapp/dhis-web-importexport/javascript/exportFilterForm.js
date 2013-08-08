// Create a Filter object
function Filter()
{
    this.name = $( "#name" ).attr( "value" );
    this.code = $( "#code" ).attr( "value" );
    this.metaDataUids = $( "#metaDataUids" ).attr( "value" );
}

// Save the Filter in the database
function saveFilter()
{
    var json = JSON.stringify( new Filter() );
    if ( validateFilter() )
    {
        $.ajax(
            {
                type: "POST",
                url: "../api/detailedMetaData/saveFilter",
                contentType: "application/json",
                data: json,
                success: function ()
                {
                    console.log( "Filter successfully saved." );
                    window.location = "dxf2DetailedMetaDataExport.action";
                },
                error: function ( request, status, error )
                {
                    console.log( request.responseText );
                    console.log( arguments );
                    alert( "Save filter process failed." );
                }
            } );
    } else
    {
        setHeaderDelayMessage( i18n_validate_filter );
    }
}

// Update an existing Filter in the database
function updateFilter()
{
    var  filter = new Filter();
    filter.uid = $( "#uid" ).attr( "value" );
//    filter.created = $( "#created" ).attr( "value" );

    var json = JSON.stringify( filter );

    if ( validateFilter() )
    {
        $.ajax(
            {
                type: "POST",
                url: "../api/detailedMetaData/updateFilter",
                contentType: "application/json",
                data: json,
                success: function ()
                {
                    console.log( "Filter successfully saved." );
                    window.location = "dxf2DetailedMetaDataExport.action";
                },
                error: function ( request, status, error )
                {
                    console.log( request.responseText );
                    console.log( arguments );
                    alert( "Update filter process failed." );
                }
            } );
    } else
    {
        setHeaderDelayMessage( i18n_validate_filter );
    }
}

// Validate the Filter before saving it to the database
function validateFilter()
{
    return ($( "#name" ).attr( "value" ) != "");
}
