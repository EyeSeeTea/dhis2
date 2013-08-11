// Global Variables

// --------------------------------------------------------------

var metaDataArray = [ "AttributeTypes", "Dimensions", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

// ---------------------------------------------------------------

// MetaData Category Accordion
jQuery( function ()
{
    loadMetaDataCategories();
    loadFilters();
    $( "#mainDivAccordion" ).accordion(
        {
            active: false,
            collapsible: true,
            clearStyle: true,
            autoHeight: false
        } );
} );

// Collapse MetaData Category information
function loadMetaDataCategories()
{
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
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

// Export DetailedMetaData AJAX
function exportDetailedMetaData()
{
    var json = JSON.stringify( createFilterJSON() );
    var url = getURL();
    console.log( "Exported JSON:" + json );
    $.ajax(
        {
            type: "POST",
            url: url,
            data: json,
            contentType: "application/json",
            dataType: "xml",
            success: function ( response )
            {
                console.log( "Response" + response );
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

// Create Filter Object
function createFilterJSON()
{
    var filter = {};
    for ( var i = 0; i < metaDataArray.length; i++ )
    {
        var filterValues = [];
        var values = $( "#selected" + metaDataArray[i] ).val();
        if ( values != undefined )
        {
            filterValues = values;
        }

        if ( filterValues.length != 0 )
        {
            var metaDataCategory = lowercaseFirstLetter( metaDataArray[i] );
            filter[metaDataCategory] = filterValues;
        }
    }

    return filter;
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
    $( "#appliedFilterMessage" ).text( i18n_no_filter_applied ).css( {"color": "black"} );
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

// Move selected values by MetaData Category
function moveSelectedValuesByCategory( metaDataCategoryName )
{
    $( "#moveSelected" + metaDataCategoryName ).click();
}

// Generate MetaData Checkboxes
function generateMetaDataCategoryDesign( metaDataCategoryName )
{
    var metadataName = getI18nMetaDataName( metaDataCategoryName );
    var selectAllMetadataName = getI18nMetaDataSelectAllName( metaDataCategoryName );
    var design =
          '<div style="float: left; width: 200px;">'
        +     '<input id="checkbox' + metaDataCategoryName + '" class="metadataCheckbox" name="' + metaDataCategoryName + '" type="checkbox"/>'
        +     '<label id="label' + metaDataCategoryName + '" for="' + metaDataCategoryName + '" style="font-size: 10pt;">' + metadataName + '</label>'
        + '</div>'
        + '<div id="divSelectAll' + metaDataCategoryName + '" style="display: none;">'
        +     '<input id="checkboxSelectAll' + metaDataCategoryName + '" class="metadataCheckbox" name="' + metaDataCategoryName + '" type="checkbox"/>'
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
        +                   '<select id="available' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%;"></select>'
        +               '</td>'
        +               '<td>'
        +                   '<input id="moveSelected' + metaDataCategoryName +'" type="button" value="&gt;" title="' + i18n_move_selected + '" style="width:50px"'
        +                       'onclick="dhisAjaxSelect_moveAllSelected( \'available' + metaDataCategoryName + '\' );"/><br/>'
        +                   '<input id="removeSelected' + metaDataCategoryName + '" type="button" value="&lt;" title="' + i18n_remove_selected + '" style="width:50px"'
        +                       'onclick="dhisAjaxSelect_moveAllSelected( \'selected' + metaDataCategoryName + '\' );"/><br/>'
        +                   '<input id="select' + metaDataCategoryName + '" type="button" value="&gt;&gt;" title="' + i18n_move_all + '" style="width:50px"'
        +                       'onclick="dhisAjaxSelect_moveAll( \'available' + metaDataCategoryName + '\' );"/><br/>'
        +                   '<input id="deselect' + metaDataCategoryName + '" type="button" value="&lt;&lt;" title="' + i18n_remove_all +  '" style="width:50px"'
        +                       'onclick="dhisAjaxSelect_moveAll( \'selected' + metaDataCategoryName + '\' );"/>'
        +               '</td>'
        +               '<td>'
        +                   '<select id="selected' + metaDataCategoryName + '" name="selected' + metaDataCategoryName + '" multiple="multiple"'
        +                   'style="height: 200px; width: 100%; margin-top: 25px;"></select>'
        +               '</td>'
        +           '</tr>'
        +       '</tbody>'
        + '</table>'
    ;

    return design;
}

// Load MetaData for a Category
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
            },
            error: function ( request, status, error )
            {
                console.log( request.responseText );
                console.log( arguments );
                alert( "Fetching metadata process failed." );
            }
        } );
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
