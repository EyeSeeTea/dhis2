// Make the first letter lowercase
function lowercaseFirstLetter( string )
{
    return string.charAt( 0 ).toLowerCase() + string.slice( 1 );
}

// Remove all white spaces from a String
function removeWhiteSpace( string )
{
    return string.replace( / /g, '' )
}

// Remove empty strings from an array
function removeEmptyStringFromArray( array )
{
    for ( var i = 0; i < array.length; i++ )
    {
        if ( array[i] === "" )
        {
            array.splice( i, 1 );
        }
    }

    return array;
}

// Remove the last white space + comma from the metaDataUids list
function removeLastComma( string )
{
    var length = string.length;
    return string.substring( 0, length - 2 );
}

// Replace id with uid
function replaceIdWithUid( object )
{
    var tempValue = object.id;
    object.uid = tempValue;
    delete object.id;
    return object;
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
