//@author Ovidiu Rosu <rosu.ovi@gmail.com>

// Global Variables
var metaDataArray = [ "AttributeTypes", "Dimensions", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

// -----------------------------------------------------------------------------
// MetaData Category Accordion
// -----------------------------------------------------------------------------
jQuery( function ()
{
    loadMetaDataCategories();

    $( "#mainDivAccordion" ).accordion(
        {
            active: false,
            collapsible: true,
            clearStyle: true,
            autoHeight: false
        } );

    loadMetaDataAccordionEvents();

    // TODO: Improve performance on applying filters
    if ( $( "#metaDataUids" ).attr( "value" ) != "" )
    {
        selectAllMetaDataCategories();
        $( "body" ).ajaxComplete( function ()
        {
            applyFilter();
        } );
    }
} );

// Collapsed MetaData Category information
function loadMetaDataCategories()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        insertMetaDataCategoryHeadingDesign( metaDataArray[i] );
        preventAccordionCollapse( metaDataArray[i] );

        $( "#checkboxSelectAll" + metaDataArray[i] ).change( function ()
        {
            var metaDataCategoryName = $( this ).attr( "name" );

            if ( $( this ).is( ":checked" ) )
            {
                $( this ).attr( 'checked', true );
                selectAllValuesByCategory( metaDataCategoryName );
            } else
            {
                deselectValuesByCategory( metaDataCategoryName );
            }
        } );
    }
}

// Insert MetaData HTML & CSS heading design
function insertMetaDataCategoryHeadingDesign( metaDataCategoryName )
{
    var design = generateMetaDataHeadingDesign( metaDataCategoryName );
    $( "#heading" + metaDataCategoryName ).append( design );
}

// Insert MetaData HTML & CSS for a Category
function insertMetaDataCategoryDesign( metaDataCategoryName )
{
    if ( $( "#mainDiv" + metaDataCategoryName ).is( ":empty" ) )
    {
        var design = generateMetaDataCategoryDesign( metaDataCategoryName );
        $( "#mainDiv" + metaDataCategoryName ).append( design );
        loadMetaData( metaDataCategoryName );
    } else
    {
        $( "#mainDiv" + metaDataCategoryName ).show();
        deselectAllValues();
    }
}

// Generate MetaData Heading design
function generateMetaDataHeadingDesign( metaDataCategoryName )
{
    var design =
          '<div id="divSelectAll' + metaDataCategoryName + '" style="float: right;">'
        +     '<input id="checkboxSelectAll' + metaDataCategoryName + '" name="' + metaDataCategoryName + '" type="checkbox"/>'
        +     '<label id="labelSelectAll' + metaDataCategoryName + '" for="' + metaDataCategoryName + '" style="font-size: 10pt;">' + i18n_select_all + '</label>'
        + '</div>'
    ;

    return design;
}

// Generate MetaData HTML & CSS for a Category
function generateMetaDataCategoryDesign( metaDataCategoryName )
{
    var i18n_available_metadata = getI18nAvailableMetaData( metaDataCategoryName );
    var i18n_selected_metadata = getI18nSelectedMetaData( metaDataCategoryName );
    var design =
          '<table id="selectionArea'+metaDataCategoryName + '" style="border: 1px solid #ccc; padding: 15px;  margin-top: 10px; margin-bottom: 10px;">'
        + '<colgroup>'
        +    '<col style="width: 500px;"/>'
        +    '<col/>'
        +    '<col style="width: 500px"/>'
        + '</colgroup>'
        + '<thead>'
        +    '<tr>'
        +        '<th>' + i18n_available_metadata + '</th>'
        +        '<th>' + i18n_filter + '</th>'
        +        '<th>' + i18n_selected_metadata + '</th>'
        +    '</tr>'
        + '</thead>'
        +        '<tbody>'
        +            '<tr>'
        +                '<td>'
        +                   '<select id="available' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%;"></select>'
        +               '</td>'
        +               '<td>'
        +                   '<input id="moveSelected' + metaDataCategoryName +'" type="button" value="&gt;" title="' + i18n_move_selected + '" style="width:50px"'
        +                       'onclick="moveSelected( \'' + metaDataCategoryName + '\' );"/><br/>'
        +                   '<input id="removeSelected' + metaDataCategoryName + '" type="button" value="&lt;" title="' + i18n_remove_selected + '" style="width:50px"'
        +                       'onclick="removeSelected( \'' + metaDataCategoryName + '\' );"/><br/>'
        +                   '<input id="select' + metaDataCategoryName + '" type="button" value="&gt;&gt;" title="' + i18n_move_all + '" style="width:50px"'
        +                       'onclick="moveAll( \'' + metaDataCategoryName + '\' );"/><br/>'
        +                   '<input id="deselect' + metaDataCategoryName + '" type="button" value="&lt;&lt;" title="' + i18n_remove_all +  '" style="width:50px"'
        +                       'onclick="removeAll( \'' + metaDataCategoryName + '\' );"/><br/>'
        +               '</td>'
        +               '<td>'
        +                   '<select id="selected' + metaDataCategoryName + '" name="selected' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%; margin-top: 25px;"></select>'
        +               '</td>'
        +           '</tr>'
        +       '</tbody>'
        + '</table>'
    ;

    return design;
}

// Move all selected items
function moveSelected( metaDataCategoryName )
{
    dhisAjaxSelect_moveAllSelected( "available" + metaDataCategoryName );

    if ( $( "#selected" + metaDataCategoryName + " option" ).length > 0 )
    {
        $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
    }

    if ( $( "#available" + metaDataCategoryName + " option" ).length == 0 )
    {
        $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", true );
    }
}

// Remove all selected items
function removeSelected( metaDataCategoryName )
{
    dhisAjaxSelect_moveAllSelected( "selected" + metaDataCategoryName );
    if ( $( "#selected" + metaDataCategoryName + " option" ).length == 0 )
    {
        $( "#heading" + metaDataCategoryName ).css( "background", "" );
    }

    if ( $( "#available" + metaDataCategoryName + " option" ).length > 0 )
    {
        $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", false );
    }
}

// Move all items
function moveAll( metaDataCategoryName )
{
    dhisAjaxSelect_moveAll( "available" + metaDataCategoryName );
    $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", true );
    $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
}

// Remove all items
function removeAll( metaDataCategoryName )
{
    dhisAjaxSelect_moveAll( "selected" + metaDataCategoryName );
    $( "#checkboxSelectAll" + metaDataCategoryName ).attr( "checked", false );
    $( "#heading" + metaDataCategoryName ).css( "background", "" );
}

// -----------------------------------------------------------------------------
// Filter Object
// -----------------------------------------------------------------------------

// Filter
function Filter()
{
    this.name = $( "#name" ).attr( "value" );
    this.uid = $( "#uid" ).attr( "value" );
    this.code = $( "#code" ).attr( "value" );
    this.metaDataUids = $( "#metaDataUids" ).attr( "value" );
}

// -----------------------------------------------------------------------------
// Load MetaData by Category
// -----------------------------------------------------------------------------

// Load MetaData by Category
function loadMetaData( metaDataCategoryName )
{
    $( "#available" + metaDataCategoryName ).dhisAjaxSelect(
        {
            source: "../api/" + lowercaseFirstLetter( metaDataCategoryName ) + ".json?links=false&paging=false",
            iterator: lowercaseFirstLetter( metaDataCategoryName ),
            connectedTo: "selected" + metaDataCategoryName,
            handler: function ( item )
            {
                var option = jQuery( "<option/>" );
                option.text( item.name );
                option.attr( "name", item.name );
                option.attr( "value", item.id );

                return option;
            }
        } );
}

// -----------------------------------------------------------------------------
// MetaData Category Accordion Commands
// -----------------------------------------------------------------------------

// Load MetaData Category Accordion Events
function loadMetaDataAccordionEvents()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#heading" + metaDataArray[i] ).click( {metaDataCategoryName: metaDataArray[i]}, selectMetaDataCategoryByHeading );
    }
}

// Select a MetaData Category from the MetaData accordion by heading
function selectMetaDataCategoryByHeading( categoryName )
{
    var metaDataCategoryName = categoryName.data.metaDataCategoryName;
    if ( $( "#mainDiv" + metaDataCategoryName ).children().length == 0 )
    {
        insertMetaDataCategoryDesign( metaDataCategoryName );
    }
}

// Get all selected Uids
function getSelectedUidsJson()
{
    var metaDataUidsJson = {};
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        var metaDataCategoryValues = [];
        var values = $( "#selected" + metaDataArray[i] ).val();
        if ( values != undefined )
        {
            metaDataCategoryValues = values;
        }

        if ( metaDataCategoryValues.length != 0 )
        {
            var metaDataCategory = lowercaseFirstLetter( metaDataArray[i] );
            metaDataUidsJson[metaDataCategory] = metaDataCategoryValues;
        }
    }

    return metaDataUidsJson;
}

// Select a MetaData Category from the MetaData accordion
function selectMetaDataCategory( metaDataCategoryName )
{
    if ( $( "#mainDiv" + metaDataCategoryName ).children().length == 0 )
    {
        insertMetaDataCategoryDesign( metaDataCategoryName );
    }
}

// Select all MetaData categories from the accordion
function selectAllMetaDataCategories()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( $( "#mainDiv" + metaDataArray[i] ).children().length == 0 )
        {
            insertMetaDataCategoryDesign( metaDataArray[i] );
        }
    }
}

// Select all values
function selectAllValues()
{
    selectAllMetaDataCategories();
    $( "body" ).ajaxComplete( function ()
    {
        for ( var i = 0; i < metaDataArray.length; i++ )
        {
            $( "#select" + metaDataArray[i] ).click();
        }
    } );

    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#select" + metaDataArray[i] ).click();
    }
}

// Deselect all values
function deselectAllValues()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        $( "#deselect" + metaDataArray[i] ).click();
        $( "#available" + metaDataArray[i] ).find( "option" ).each( function ()
        {
            $( this ).prop( "selected", false );
        } );
    }
}

// Select all values by category
function selectAllValuesByCategory( metaDataCategoryName )
{
    selectMetaDataCategory( metaDataCategoryName );

    $( "body" ).ajaxComplete( function ()
    {
        $( "#select" + metaDataCategoryName ).click();
    } );

    $( "#select" + metaDataCategoryName ).click();
}

// Deselect all values by category
function deselectValuesByCategory( metaDataCategoryName )
{
    $( "#deselect" + metaDataCategoryName ).click();

    $( "#available" + metaDataCategoryName ).find( "option" ).each( function ()
    {
        $( this ).prop( "selected", false );
    } );
}

// Move selected values by category
function moveSelectedValuesByCategory( metaDataCategoryName )
{
    $( "#moveSelected" + metaDataCategoryName ).click();
}

// -----------------------------------------------------------------------------
// Apply Filter
// -----------------------------------------------------------------------------

// Apply Filter
function applyFilter()
{
    var metaDataUids = JSON.parse( $( "#metaDataUids" ).attr( "value" ) );
    var metaDataCategory = "";
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        metaDataCategory = lowercaseFirstLetter( metaDataArray[i] );
        if ( metaDataUids.hasOwnProperty( metaDataCategory ) )
        {
            var uidValues = metaDataUids[metaDataCategory];

            for ( var j = 0; j < uidValues.length; j++ )
            {
                $( "#available" + metaDataArray[i] + " option" ).each( function ()
                {
                    var availableUid = $( this ).val();
                    if ( uidValues[j] == availableUid )
                    {
                        $( this ).prop( "selected", true );
                        uidValues.splice( i, 1 );
                    }
                } );
            }

            moveSelectedValuesByCategory( metaDataArray[i] );
        }
    }
}

// -----------------------------------------------------------------------------
// Save Filter
// -----------------------------------------------------------------------------

// Save a Filter
function saveFilter()
{
    $( "#metaDataUids" ).attr( "value", JSON.stringify( getSelectedUidsJson() ) );
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
        //TODO : Input validation
    }
}

// -----------------------------------------------------------------------------
// Update Filter
// -----------------------------------------------------------------------------

// Update a Filter
function updateFilter()
{
    $( "#metaDataUids" ).attr( "value", JSON.stringify( getSelectedUidsJson() ) );
    var json = JSON.stringify( new Filter() );
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
        //TODO : Input validation
    }
}

// -----------------------------------------------------------------------------
// Export MetaData
// -----------------------------------------------------------------------------

// Start MetaData export
function startExport()
{
    var metaDataUids = {};
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        var values = [];
        $( "#selected" + metaDataArray[i] + " option" ).each( function ()
        {
            values.push( $( this ).attr( "value" ) );
        } );

        if ( values.length > 0 )
        {
            metaDataUids[lowercaseFirstLetter( metaDataArray[i] )] = values;
        }
    }

    $( "#exportJson" ).attr( "value", JSON.stringify( metaDataUids ) );
    jQuery( "#exportDialog" ).dialog( {
        title: i18n_export,
        modal: true
    } );
}

// Export MetaData
function exportDetailedMetaData()
{
    var exportJson = {};
    exportJson.exportDependencies = $( "#exportDependencies" ).is( ":checked" ).toString();
    exportJson.metaDataUids = $( "#exportJson" ).val();
    var url = getURL();
    $.ajax(
        {
            type: "POST",
            url: url,
            data: JSON.stringify( exportJson ),
            contentType: "application/json",
            dataType: "xml",
            success: function ()
            {
                console.log( "Exported JSON: " + JSON.stringify( exportJson ) );
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
// Validate Filter
// -----------------------------------------------------------------------------

// Validate Filter
function validateFilter()
{
    return ($( "#name" ).attr( "value" ) != "");
}

// -----------------------------------------------------------------------------
// Utils
// -----------------------------------------------------------------------------

// Stop accordion collapse
function preventAccordionCollapse( metaDataCategoryName )
{
    $( "#heading" + metaDataCategoryName ).find( "input" ).click( function ( e )
    {
        e.stopPropagation();
    } );
}
