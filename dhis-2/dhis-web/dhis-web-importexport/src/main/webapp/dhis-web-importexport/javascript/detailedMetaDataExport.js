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

    // TODO: Improve performance on applying filters
    if ( $( "#metaDataUids" ).attr( "value" ) != "" )
    {
//        var metaDataUids = JSON.parse( $( "#metaDataUids" ).val() );
//        var metaDataCategories = [];
//
//        for ( var metaDataCategory in metaDataUids )
//        {
//            metaDataCategories.push( metaDataCategory );
//        }
//
//        for ( var i = 0; i < metaDataArray.length; i++ )
//        {
//            for ( var j = 0; j < metaDataCategories.length; j++ )
//            {
//                if ( metaDataCategories[j] == lowercaseFirstLetter( metaDataArray[i] ) )
//                {
//                    selectCheckbox( metaDataArray[i] );
//                }
//            }
//        }
        selectAllCheckboxes();
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
        +                   '<select id="selected' + metaDataCategoryName + '" name="selected' + metaDataCategoryName + '" multiple="multiple"'
        +                   'style="height: 200px; width: 100%; margin-top: 25px;"></select>'
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
    $( "#header" + metaDataCategoryName ).css( "background", "#CFFFB3" );
}

// Remove all selected items
function removeSelected( metaDataCategoryName )
{
    dhisAjaxSelect_moveAllSelected( "selected" + metaDataCategoryName );
    if ( $( "#selected" + metaDataCategoryName + " option" ).length == 0 )
    {
        $( "#header" + metaDataCategoryName ).css( "background", "" );
    }
}

// Move all items
function moveAll( metaDataCategoryName )
{
    dhisAjaxSelect_moveAll( "available" + metaDataCategoryName );
    $( "#header" + metaDataCategoryName ).css( "background", "#CFFFB3" );
}

// Remove all items
function removeAll( metaDataCategoryName )
{
    dhisAjaxSelect_moveAll( "selected" + metaDataCategoryName );
    $( "#header" + metaDataCategoryName ).css( "background", "" );
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

// Select a MetaData type checkbox
function selectCheckbox( metaDataCategoryName )
{
    if ( !$( "#checkbox" + metaDataCategoryName ).is( ":checked" ) )
    {
        $( "#checkbox" + metaDataCategoryName ).prop( "checked", true );
        insertMetaDataDesign( metaDataCategoryName );
    }
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

                moveSelectedValuesByCategory( metaDataArray[i] );
            }
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
        setHeaderDelayMessage( i18n_validate_filter );
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
        setHeaderDelayMessage( i18n_validate_filter );
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
    $( "#exportDialog" ).dialog();
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

// Make the first letter lowercase
function lowercaseFirstLetter( string )
{
    return string.charAt( 0 ).toLowerCase() + string.slice( 1 );
}

// Get MetaData Name
function getI18nMetaDataName( metaDataCategoryName )
{
    switch ( metaDataCategoryName )
    {
        case "AttributeTypes":
            return i18n_attributeTypes;
        case "Dimensions":
            return i18n_categories;
        case "Charts":
            return i18n_charts;
        case "Concepts":
            return i18n_concepts;
        case "Constants":
            return i18n_constants;
        case "DataDictionaries":
            return i18n_dataDictionaries;
        case "DataElementGroupSets":
            return i18n_dataElementGroupSets;
        case "DataElementGroups":
            return i18n_dataElementGroups;
        case "DataElements":
            return i18n_dataElements;
        case "DataSets":
            return i18n_dataSets;
        case "Documents":
            return i18n_documents;
        case "IndicatorGroupSets":
            return i18n_indicatorGroupSets;
        case "IndicatorGroups":
            return i18n_indicatorGroups;
        case "IndicatorTypes":
            return i18n_indicatorTypes;
        case "Indicators":
            return i18n_indicators;
        case "MapLegendSets":
            return i18n_mapLegendSets;
        case "Maps":
            return i18n_maps;
        case "OptionSets":
            return i18n_optionSets;
        case "OrganisationUnitGroupSets":
            return i18n_organisationUnitGroupSets;
        case "OrganisationUnitGroups":
            return i18n_organisationUnitGroups;
        case "OrganisationUnitLevels":
            return i18n_organisationUnitLevels;
        case "OrganisationUnits":
            return i18n_organisationUnits;
        case "ReportTables":
            return i18n_reportTables;
        case "Reports":
            return i18n_reports;
        case "SqlViews":
            return i18n_sqlViews;
        case "UserGroups":
            return i18n_userGroups;
        case "UserRoles":
            return i18n_userRoles;
        case "Users":
            return i18n_users;
        case "ValidationRuleGroups":
            return i18n_validationRuleGroups;
        case "ValidationRules":
            return i18n_validationRules;
        default :
            return "";
    }
}

// Get MetaData Select all Name
function getI18nMetaDataSelectAllName( metaDataCategoryName )
{
    switch ( metaDataCategoryName )
    {
        case "AttributeTypes":
            return i18n_select_all_attributeTypes;
        case "Dimensions":
            return i18n_select_all_categories;
        case "Charts":
            return i18n_select_all_charts;
        case "Concepts":
            return i18n_select_all_concepts;
        case "Constants":
            return i18n_select_all_constants;
        case "DataDictionaries":
            return i18n_select_all_dataDictionaries;
        case "DataElementGroupSets":
            return i18n_select_all_dataElementGroupSets;
        case "DataElementGroups":
            return i18n_select_all_dataElementGroups;
        case "DataElements":
            return i18n_select_all_dataElements;
        case "DataSets":
            return i18n_select_all_dataSets;
        case "Documents":
            return i18n_select_all_documents;
        case "IndicatorGroupSets":
            return i18n_select_all_indicatorGroupSets;
        case "IndicatorGroups":
            return i18n_select_all_indicatorGroups;
        case "IndicatorTypes":
            return i18n_select_all_indicatorTypes;
        case "Indicators":
            return i18n_select_all_indicators;
        case "MapLegendSets":
            return i18n_select_all_mapLegendSets;
        case "Maps":
            return i18n_select_all_maps;
        case "OptionSets":
            return i18n_select_all_optionSets;
        case "OrganisationUnitGroupSets":
            return i18n_select_all_organisationUnitGroupSets;
        case "OrganisationUnitGroups":
            return i18n_select_all_organisationUnitGroups;
        case "OrganisationUnitLevels":
            return i18n_select_all_organisationUnitLevels;
        case "OrganisationUnits":
            return i18n_select_all_organisationUnits;
        case "ReportTables":
            return i18n_select_all_reportTables;
        case "Reports":
            return i18n_select_all_reports;
        case "SqlViews":
            return i18n_select_all_sqlViews;
        case "UserGroups":
            return i18n_select_all_userGroups;
        case "UserRoles":
            return i18n_select_all_userRoles;
        case "Users":
            return i18n_select_all_users;
        case "ValidationRuleGroups":
            return i18n_select_all_validationRuleGroups;
        case "ValidationRules":
            return i18n_select_all_validationRules;
        default :
            return "";
    }
}

// Get Available Metadata
function getI18nAvailableMetaData( metaDataCategoryName )
{
    switch ( metaDataCategoryName )
    {
        case "AttributeTypes":
            return i18n_available_attributeTypes;
        case "Dimensions":
            return i18n_available_categories;
        case "Charts":
            return i18n_available_charts;
        case "Concepts":
            return i18n_available_concepts;
        case "Constants":
            return i18n_available_constants;
        case "DataDictionaries":
            return i18n_available_dataDictionaries;
        case "DataElementGroupSets":
            return i18n_available_dataElementGroupSets;
        case "DataElementGroups":
            return i18n_available_dataElementGroups;
        case "DataElements":
            return i18n_available_dataElements;
        case "DataSets":
            return i18n_available_dataSets;
        case "Documents":
            return i18n_available_documents;
        case "IndicatorGroupSets":
            return i18n_available_indicatorGroupSets;
        case "IndicatorGroups":
            return i18n_available_indicatorGroups;
        case "Indicators":
            return i18n_available_indicators;
        case "IndicatorTypes":
            return i18n_available_indicatorTypes;
        case "MapLegendSets":
            return i18n_available_mapLegendSets;
        case "Maps":
            return i18n_available_maps;
        case "OptionSets":
            return i18n_available_optionSets;
        case "OrganisationUnitGroupSets":
            return i18n_available_organisationUnitGroupSets;
        case "OrganisationUnitGroups":
            return i18n_available_organisationUnitGroups;
        case "OrganisationUnitLevels":
            return i18n_available_organisationUnitLevels;
        case "OrganisationUnits":
            return i18n_available_organisationUnits;
        case "ReportTables":
            return i18n_available_reportTables;
        case "Reports":
            return i18n_available_reports;
        case "SqlViews":
            return i18n_available_sqlViews;
        case "UserGroups":
            return i18n_available_userGroups;
        case "UserRoles":
            return i18n_available_userRoles;
        case "Users":
            return i18n_available_users;
        case "ValidationRuleGroups":
            return i18n_available_validationRuleGroups;
        case "ValidationRules":
            return i18n_available_validationRules;
        default :
            return "";
    }
}

// Get Selected Metadata
function getI18nSelectedMetaData( metaDataCategoryName )
{
    switch ( metaDataCategoryName )
    {
        case "AttributeTypes":
            return i18n_selected_attributeTypes;
        case "Dimensions":
            return i18n_selected_categories;
        case "Charts":
            return i18n_selected_charts;
        case "Concepts":
            return i18n_selected_concepts;
        case "Constants":
            return i18n_selected_constants;
        case "DataDictionaries":
            return i18n_selected_dataDictionaries;
        case "DataElementGroupSets":
            return i18n_selected_dataElementGroupSets;
        case "DataElementGroups":
            return i18n_selected_dataElementGroups;
        case "DataElements":
            return i18n_selected_dataElements;
        case "DataSets":
            return i18n_selected_dataSets;
        case "Documents":
            return i18n_selected_documents;
        case "IndicatorGroupSets":
            return i18n_selected_indicatorGroupSets;
        case "IndicatorGroups":
            return i18n_selected_indicatorGroups;
        case "Indicators":
            return i18n_selected_indicators;
        case "IndicatorTypes":
            return i18n_selected_indicatorTypes;
        case "MapLegendSets":
            return i18n_selected_mapLegendSets;
        case "Maps":
            return i18n_selected_maps;
        case "OptionSets":
            return i18n_selected_optionSets;
        case "OrganisationUnitGroupSets":
            return i18n_selected_organisationUnitGroupSets;
        case "OrganisationUnitGroups":
            return i18n_selected_organisationUnitGroups;
        case "OrganisationUnitLevels":
            return i18n_selected_organisationUnitLevels;
        case "OrganisationUnits":
            return i18n_selected_organisationUnits;
        case "ReportTables":
            return i18n_selected_reportTables;
        case "Reports":
            return i18n_selected_reports;
        case "SqlViews":
            return i18n_selected_sqlViews;
        case "UserGroups":
            return i18n_selected_userGroups;
        case "UserRoles":
            return i18n_selected_userRoles;
        case "Users":
            return i18n_selected_users;
        case "ValidationRuleGroups":
            return i18n_selected_validationRuleGroups;
        case "ValidationRules":
            return i18n_selected_validationRules;
        default :
            return "";
    }
}
