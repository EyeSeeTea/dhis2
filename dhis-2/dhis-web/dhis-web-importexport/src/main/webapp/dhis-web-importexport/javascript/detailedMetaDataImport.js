//@author Ovidiu Rosu <rosu.ovi@gmail.com>

// Global Variables
var metaDataArray = [ "AttributeTypes", "Dimensions", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

var metaData;

// -----------------------------------------------------------------------------
// MetaData Category Accordion
// -----------------------------------------------------------------------------
jQuery( function ()
{
    metaData = JSON.parse( $( "#metaDataJson" ).val() );
    if ( !jQuery.isEmptyObject( metaData ) )
    {
        loadMetaDataCategories( metaData );
        $( "#mainDivAccordion" ).accordion(
            {
                active: false,
                collapsible: true,
                clearStyle: true,
                autoHeight: false
            } );

        selectAllCheckboxes();
        selectAllValues();
        loadMetaDataAccordionEvents( metaData );
    }

    loadFile();
} );

// Collapsed MetaData Category information
function loadMetaDataCategories( metaData )
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( metaData.hasOwnProperty( lowercaseFirstLetter( metaDataArray[i] ) ) )
        {
            $( "#heading" + metaDataArray[i] ).show();
            $( "#div" + metaDataArray[i] ).show();
            $("#metaDataCommands" ).show();

            insertMetaDataCategoryDesign( metaDataArray[i] );

            $( "#checkbox" + metaDataArray[i] ).change( function ()
            {
                var metaDataCategoryName = $( this ).attr( "name" );

                if ( $( this ).is( ":checked" ) )
                {
                    insertMetaDataDesign( metaDataCategoryName );
                } else
                {
                    removeMetaDataDesign( metaDataCategoryName );
                }
            } );

            $( "#checkboxSelectAll" + metaDataArray[i] ).change( function ()
            {
                var metaDataCategoryName = $( this ).attr( "name" );

                if ( $( this ).is( ":checked" ) )
                {
                    selectAllValuesByCategory( metaDataCategoryName );
                } else
                {
                    deselectValuesByCategory( metaDataCategoryName );
                }
            } );
        }
    }
}

// Insert MetaData HTML & CSS Checkbox
function insertMetaDataCategoryDesign( metaDataCategoryName )
{
    var design = generateMetaDataCategoryDesign( metaDataCategoryName );
    $( "#div" + metaDataCategoryName ).append( design );
}

// Insert MetaData HTML & CSS for a Category
function insertMetaDataDesign( metaDataCategoryName )
{
    $( "#label" + metaDataCategoryName ).css( {"color": "lime"} );
    $( "#divSelectAll" + metaDataCategoryName ).show();

    if ( $( "#mainDiv" + metaDataCategoryName ).is( ":empty" ) )
    {
        var design = generateMetaDataDesign( metaDataCategoryName );
        $( "#mainDiv" + metaDataCategoryName ).append( design );
        loadMetaData( metaDataCategoryName );
    } else
    {
        $( "#mainDiv" + metaDataCategoryName ).show();
        deselectAllValues();
    }
}

// Generate MetaData Checkboxes
function generateMetaDataCategoryDesign( metaDataCategoryName )
{
    var metadataName = getI18nMetaDataName( metaDataCategoryName );
    var selectAllMetadataName = getI18nMetaDataSelectAllName( metaDataCategoryName );
    var design =
            '<div style="float: left; width: 200px;">'
                +     '<input id="checkbox' + metaDataCategoryName + '" name="' + metaDataCategoryName + '" type="checkbox"/>'
                +     '<label id="label' + metaDataCategoryName + '" for="' + metaDataCategoryName + '" style="font-size: 10pt;">' + metadataName + '</label>'
                + '</div>'
                + '<div id="divSelectAll' + metaDataCategoryName + '" style="display: none;">'
                +     '<input id="checkboxSelectAll' + metaDataCategoryName + '" name="' + metaDataCategoryName + '" type="checkbox"/>'
                +     '<label id="labelSelectAll' + metaDataCategoryName + '" for="' + metaDataCategoryName + '" style="font-size: 10pt;">' + selectAllMetadataName + '</label>'
                + '</div>'
                + '<div id="mainDiv' + metaDataCategoryName + '"></div>'
        ;

    return design;
}

// Generate MetaData HTML & CSS for a Category
function generateMetaDataDesign( metaDataCategoryName )
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
                +                   '<select id="available' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%; margin-top: 10px;"></select>'
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
                +                   '<select id="selected' + metaDataCategoryName + '" name="selected' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%; margin-top: 10px;"></select>'
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
    $( "#available" + metaDataCategoryName + " option:selected" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#selected" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    if ( $( "#selected" + metaDataCategoryName + " option" ).length > 0 )
    {
        $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
    }
}

// Remove all selected items
function removeSelected( metaDataCategoryName )
{
    $( "#selected" + metaDataCategoryName + " option:selected" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#available" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    if ( $( "#selected" + metaDataCategoryName + " option" ).length == 0 )
    {
        $( "#heading" + metaDataCategoryName ).css( "background", "" );
    }
}

// Move all items
function moveAll( metaDataCategoryName )
{
    $( "#available" + metaDataCategoryName + " option" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#selected" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
}

// Remove all items
function removeAll( metaDataCategoryName )
{
    $( "#selected" + metaDataCategoryName + " option" ).each( function ()
    {
        var option = jQuery( "<option/>" );
        option.text( $( this ).attr( "name" ) );
        option.attr( "name", $( this ).attr( "name" ) );
        option.attr( "value", $( this ).attr( "value" ) );
        option.attr( "selected", "selected" );

        $( "#available" + metaDataCategoryName ).append( option );
        $( this ).remove();
    } );

    $( "#heading" + metaDataCategoryName ).css( "background", "" );
}

// Remove MetaData HTML and CSS from a Category
function removeMetaDataDesign( metaDataCategoryName )
{
    $( "#label" + metaDataCategoryName ).css( {"color": "black"} );
    $( "#labelSelectAll" + metaDataCategoryName ).css( {"color": "black"} );
    $( "#checkboxSelectAll" + metaDataCategoryName ).prop( "checked", false );
    deselectValuesByCategory( metaDataCategoryName );

    $( "#divSelectAll" + metaDataCategoryName ).hide();
    $( "#mainDiv" + metaDataCategoryName ).hide();
}

// -----------------------------------------------------------------------------
// Load MetaData by Category
// -----------------------------------------------------------------------------

// Load MetaData by Category
function loadMetaData( metaDataCategoryName )
{
    var metaDataCategoryProperty = lowercaseFirstLetter( metaDataCategoryName );
    if ( metaData.hasOwnProperty( metaDataCategoryProperty ) )
    {
        var metaDataCategoryArray = metaData[metaDataCategoryProperty];
        for ( var i = 0; i < metaDataCategoryArray.length; i++ )
        {
            var option = jQuery( "<option/>" );
            option.text( metaDataCategoryArray[i].name );
            option.attr( "name", metaDataCategoryArray[i].name );
            option.attr( "value", metaDataCategoryArray[i].id );
            option.attr( "selected", "selected" );

            $( "#selected" + metaDataCategoryName ).append( option );
            $( "#heading" + metaDataCategoryName ).css( "background", "#CFFFB3 50% 50% repeat-x" );
        }
    }
}

// -----------------------------------------------------------------------------
// MetaData Category Accordion Commands
// -----------------------------------------------------------------------------

// Load MetaData Category Accordion Events
function loadMetaDataAccordionEvents( metaData )
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( metaData.hasOwnProperty( lowercaseFirstLetter( metaDataArray[i] ) ) )
        {
            $( "#heading" + metaDataArray[i] ).click( {metaDataCategoryName: metaDataArray[i]}, selectMetaDataCategory );
        }
    }
}

// Select a MetaData Category from the MetaData accordion
function selectMetaDataCategory( categoryName )
{
    var metaDataCategoryName = categoryName.data.metaDataCategoryName;
    if ( !$( "#checkbox" + metaDataCategoryName ).is( ":checked" ) )
    {
        $( "#checkbox" + metaDataCategoryName ).prop( "checked", true );
        insertMetaDataDesign( metaDataCategoryName );
    }
}

// Select all MetaData type checkboxes (selected values are reset)
function selectAllCheckboxes()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( !$( "#checkbox" + metaDataArray[i] ).is( ":checked" ) )
        {
            $( "#checkbox" + metaDataArray[i] ).prop( "checked", true );
            insertMetaDataDesign( metaDataArray[i] );
        }
    }

    deselectAllValues();
}

// Deselect all MetaData type checkboxes
function deselectAllCheckboxes()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( $( "#checkbox" + metaDataArray[i] ).is( ":checked" ) )
        {
            $( "#checkbox" + metaDataArray[i] ).prop( "checked", false );
            removeMetaDataDesign( metaDataArray[i] );
        }
    }

    deselectAllValues();
}

// Select all values
function selectAllValues()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( $( "#checkbox" + metaDataArray[i] ).is( ":checked" ) )
        {
            $( "#select" + metaDataArray[i] ).click();
        }
    }
}

// Deselect all values
function deselectAllValues()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        if ( $( "#checkbox" + metaDataArray[i] ).is( ":checked" ) )
        {
            $( "#deselect" + metaDataArray[i] ).click();
        }
        $( "#available" + metaDataArray[i] ).find( "option" ).each( function ()
        {
            $( this ).prop( "selected", false );
        } );
    }
}

// Select all values by category
function selectAllValuesByCategory( metaDataCategoryName )
{
    $( "#labelSelectAll" + metaDataCategoryName ).css( {color: "lime"} );
    $( "#select" + metaDataCategoryName ).click();
}

// Deselect all values by category
function deselectValuesByCategory( metaDataCategoryName )
{
    $( "#labelSelectAll" + metaDataCategoryName ).css( {color: "black"} );
    $( "#deselect" + metaDataCategoryName ).click();

    $( "#available" + metaDataCategoryName ).find( "option" ).each( function ()
    {
        $( this ).prop( "selected", false );
    } );
}

// -----------------------------------------------------------------------------
// Process file
// -----------------------------------------------------------------------------

// Load file
function loadFile()
{
    $( "#upload" ).change( function ()
    {
        $( "#loadFile" ).submit();
    } );
}

// -----------------------------------------------------------------------------
// Process import MetaData Json
// -----------------------------------------------------------------------------

// Process import MetaData Json
function processImportMetaDataJson()
{
    var importMetaDataJson = JSON.parse( JSON.stringify( metaData ) );
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        var metaDataCategoryProperty = lowercaseFirstLetter( metaDataArray[i] );

        if ( importMetaDataJson.hasOwnProperty( metaDataCategoryProperty ) )
        {
            var metaDataCategoryArray = importMetaDataJson[metaDataCategoryProperty];
            for ( var j = 0; j < metaDataCategoryArray.length; j++ )
            {
                var existsMetaDataValue = $( "#selected" + metaDataArray[i] + " option[value='" + metaDataCategoryArray[j].id + "']" ).length !== 0;

                if ( !existsMetaDataValue )
                {
                    (importMetaDataJson[metaDataCategoryProperty]).splice( j, 1 );
                    j--;
                }
            }

            if ( importMetaDataJson[metaDataCategoryProperty].length == 0 )
            {
                delete importMetaDataJson[metaDataCategoryProperty];
            }
        }
    }

    return importMetaDataJson;
}

// -----------------------------------------------------------------------------
// Import
// -----------------------------------------------------------------------------

// Import detailed MetaData
function importDetailedMetaData()
{
    var importMetaDataJson = processImportMetaDataJson();
}

// Display summary link
function displaySummaryLink()
{
    var html = '<tr><td></td><td><a href="javascript:displaySummary()">Display import summary</a></td></tr>';
    $( '#notificationTable' ).prepend( html );
}

// Display summary
function displaySummary()
{
    $( '#notificationDiv' ).hide();
    $( '#importSummaryDiv' ).show( 'fast' ).load( 'getMetaDataImportSummary.action' );
}
