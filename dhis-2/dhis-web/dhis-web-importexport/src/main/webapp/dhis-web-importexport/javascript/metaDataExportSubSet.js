// Global Variables
// --------------------------------------------------------------

var metadataArray = [ "AttributeTypes", "Dimensions", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

// ---------------------------------------------------------------

// Collapse Metadata Category information
jQuery(function () {
    for(var i = 0; i < metadataArray.length; i++) {
        var metadataId = "#" + lowercaseFirstLetter(metadataArray[i]);
        $(metadataId).change(function () {
            var metadataCategoryName = $(this).attr("name");

            if ($(this).is(":checked")) {
                $("#label" + metadataCategoryName).css({"color" : "lime"});
                insertMetadataDesign(metadataCategoryName);
                loadMetadata(metadataCategoryName);
            } else {
                removeMetadataDesign(metadataCategoryName);
                $("#label" + metadataCategoryName).css({"color" : "black"});
            }
        });
    }
});

// Metadata Category Accordion
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

    for(var i = 0; i < metadataArray.length; i++) {
        $("#label" + metadataArray[i]).css({"color" : "lime"});
        insertMetadataDesign(metadataArray[i]);
        loadMetadata(metadataArray[i]);
    }
}

// Deselect all checkboxes
function selectNone() {
    $("#exportForm").find("input:checkbox").attr("checked", false);

    for(var i = 0; i < metadataArray.length; i++) {
        removeMetadataDesign(metadataArray[i]);
        $("#label" + metadataArray[i]).css({"color" : "black"});
    }
}

// Make the first letter lowercase
function lowercaseFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

// Insert Metadata HTML & CSS for a Category
function insertMetadataDesign(metadataCategoryName) {
    var design = generateMetadataDesign(metadataCategoryName);
    $("#mainDiv" + metadataCategoryName).append(design);
}

// Generate Metadata HTML & CSS for a Category
function generateMetadataDesign(metadataCategoryName) {
    var i18n_available_metadata = getI18nAvailableMetadata(metadataCategoryName);
    var i18n_selected_metadata = getI18nSelectedMetadata(metadataCategoryName);
    var design =
        '<table id="selectionArea'+metadataCategoryName + '" style="border: 1px solid #ccc; padding: 15px;  margin-top: 10px; margin-bottom: 10px;">' +
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
                            '<select id="available' + metadataCategoryName + '" multiple="multiple" style="height: 200px; width: 100%;"></select>' +
                        '</td>' +
                        '<td>' +
                            '<input type="button" value="&gt;" title="' + i18n_move_selected + '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAllSelected( \'available' + metadataCategoryName + '\' );"/><br/>' +
                            '<input type="button" value="&lt;" title="' + i18n_remove_selected + '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAllSelected( \'selected' + metadataCategoryName + '\' );"/><br/>' +
                            '<input type="button" value="&gt;&gt;" title="' + i18n_move_all + '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAll( \'available' + metadataCategoryName + '\' );"/><br/>' +
                            '<input type="button" value="&lt;&lt;" title="' + i18n_remove_all +  '" style="width:50px"' +
                                'onclick="dhisAjaxSelect_moveAll( \'selected' + metadataCategoryName + '\' );"/>' +
                        '</td>' +
                        '<td>' +
                            '<select id="selected' + metadataCategoryName + '" name="selectedAttributes" multiple="multiple"' +
                            'style="height: 200px; width: 100%; margin-top: 45px;"></select>' +
                        '</td>' +
                    '</tr>' +
                '</tbody>' +
            '</table>'
    ;

    return design;
}


// Remove Metadata HTML and CSS from a Category
function removeMetadataDesign(metadataCategoryName) {
    $("#mainDiv" + metadataCategoryName).empty();
}

// Load MetaData for a Category
function loadMetadata(metadataCategoryName) {
    jQuery("#available" + metadataCategoryName).dhisAjaxSelect({
        source: "../api/" + lowercaseFirstLetter(metadataCategoryName) + ".json?links=false&paging=false",
        iterator: lowercaseFirstLetter(metadataCategoryName),
        connectedTo: "selected" + metadataCategoryName,
        handler: function (item) {
            var option = jQuery("<option data-uid='" + item.groups + "' />");
            option.text(item.name);
            option.attr("value", item.id);

            return option;
        }
    });
}

function getI18nAvailableMetadata(metadataCategoryName) {
    switch (metadataCategoryName) {
        case "Attributes":
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

function getI18nSelectedMetadata(metadataCategoryName) {
    switch (metadataCategoryName) {
        case "Attributes":
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









