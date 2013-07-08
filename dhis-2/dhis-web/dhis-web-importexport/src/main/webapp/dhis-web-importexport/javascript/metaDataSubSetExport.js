// Global Variables
// --------------------------------------------------------------

var metaDataArray = [ "AttributeTypes", "Dimensions", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

// ---------------------------------------------------------------

// Collapse MetaData Category information
jQuery(function () {
    for(var i = 0; i < metaDataArray.length; i++) {
        var metadataId = "#" + lowercaseFirstLetter(metaDataArray[i]);
        $(metadataId).change(function () {
            var metaDataCategoryName = $(this).attr("name");

            if ($(this).is(":checked")) {
                $("#label" + metaDataCategoryName).css({"color" : "lime"});
                insertMetaDataDesign(metaDataCategoryName);
                loadMetaData(metaDataCategoryName);
            } else {
                removeMetaDataDesign(metaDataCategoryName);
                $("#label" + metaDataCategoryName).css({"color" : "black"});
            }
        });
    }
});

// MetaData Category Accordion
jQuery(function () {
    selectNone();
    $("#mainDivAccordion").accordion({
        active: false,
        collapsible: true,
        clearStyle: true,
        autoHeight: false
    });
});

// Select all checkboxes
function selectAll() {
    $("#exportForm").find("input:checkbox").attr("checked", true);

    for(var i = 0; i < metaDataArray.length; i++) {
        $("#label" + metaDataArray[i]).css({"color" : "lime"});
        insertMetaDataDesign(metaDataArray[i]);
        loadMetaData(metaDataArray[i]);
    }
}

// Deselect all checkboxes
function selectNone() {
    $("#exportForm").find("input:checkbox").attr("checked", false);

    for(var i = 0; i < metaDataArray.length; i++) {
        removeMetaDataDesign(metaDataArray[i]);
        $("#label" + metaDataArray[i]).css({"color" : "black"});
    }
}

// Make the first letter lowercase
function lowercaseFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

// Insert MetaData HTML & CSS for a Category
function insertMetaDataDesign(metaDataCategoryName) {
    var design = generateMetaDataDesign(metaDataCategoryName);
    $("#mainDiv" + metaDataCategoryName).append(design);
}

// Generate MetaData HTML & CSS for a Category
function generateMetaDataDesign(metaDataCategoryName) {
    var i18n_available_metadata = getI18nAvailableMetaData(metaDataCategoryName);
    var i18n_selected_metadata = getI18nSelectedMetaData(metaDataCategoryName);
    var design =
        '<table id="selectionArea'+metaDataCategoryName + '" style="border: 1px solid #ccc; padding: 15px;  margin-top: 10px; margin-bottom: 10px;">' +
        '<colgroup>' +
            '<col style="width: 500px;"/>' +
            '<col/>' +
            '<col style="width: 500px"/>' +
        '</colgroup>' +
        '<thead>' +
            '<!-- DATA -->' +
            '<tr>' +
                '<th>' + i18n_available_metadata + '</th>' +
                '<th>' + i18n_filter + '</th>' +
                '<th>' + i18n_selected_metadata + '</th>' +
            '</tr>' +
        '</thead>' +
                '<tbody>' +
                    '<tr>' +
                        '<td>' +
                            '<select id="available' + metaDataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%;"></select>' +
                        '</td>' +
                        '<td>' +
                            '<input type="button" value="&gt;" title="' + i18n_move_selected + '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAllSelected( \'available' + metaDataCategoryName + '\' );"/><br/>' +
                            '<input type="button" value="&lt;" title="' + i18n_remove_selected + '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAllSelected( \'selected' + metaDataCategoryName + '\' );"/><br/>' +
                            '<input type="button" value="&gt;&gt;" title="' + i18n_move_all + '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAll( \'available' + metaDataCategoryName + '\' );"/><br/>' +
                            '<input type="button" value="&lt;&lt;" title="' + i18n_remove_all +  '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAll( \'selected' + metaDataCategoryName + '\' );"/>' +
                        '</td>' +
                        '<td>' +
                            '<select id="selected' + metaDataCategoryName + '" name="selected' + metaDataCategoryName + '" multiple="multiple"' +
                            'style="height: 200px; width: 100%; margin-top: 45px;"></select>' +
                        '</td>' +
                    '</tr>' +
                '</tbody>' +
            '</table>'
    ;

    return design;
}


// Remove MetaData HTML and CSS from a Category
function removeMetaDataDesign(metaDataCategoryName) {
    $("#mainDiv" + metaDataCategoryName).empty();
}

// Load MetaData for a Category
function loadMetaData(metaDataCategoryName) {
    jQuery("#available" + metaDataCategoryName).dhisAjaxSelect({
        source: "../api/" + lowercaseFirstLetter(metaDataCategoryName) + ".json?links=false&paging=false",
        iterator: lowercaseFirstLetter(metaDataCategoryName),
        connectedTo: "selected" + metaDataCategoryName,
        handler: function (item) {
            var option = jQuery("<option/>");
            option.text(item.name);
            option.attr("value", item.id);

            return option;
        }
    });
}

// Export MetaDataSubSet
function exportMetaDataSubSet() {
    for(var i = 0; i < metaDataArray; i++) {
        selectAllById( "selected" + metaDataArray[i] );
    }

    document.getElementById( "exportForm").submit();
}

// Get Available Metadata
function getI18nAvailableMetaData(metaDataCategoryName) {
    switch (metaDataCategoryName) {
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
    }
}

// Get Selected Metadata
function getI18nSelectedMetaData(metaDataCategoryName) {
    switch (metaDataCategoryName) {
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
    }
}









