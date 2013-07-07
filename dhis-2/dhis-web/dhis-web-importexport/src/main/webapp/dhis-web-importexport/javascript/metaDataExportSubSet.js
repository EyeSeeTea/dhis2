// Global Variables
// --------------------------------------------------------------

var metadataArray = [ "Attributes", "DataElementCategories", "Charts", "Concepts", "Constants", "DataDictionaries", "DataElementGroupSets",
    "DataElementGroups", "DataElements", "DataSets", "Documents", "IndicatorGroupSets", "IndicatorGroups", "Indicators",
    "IndicatorTypes", "MapLegendSets", "Maps", "OptionSets", "OrganisationUnitGroupSets", "OrganisationUnitGroups",
    "OrganisationUnitLevels", "OrganisationUnits", "ReportTables", "Reports", "SqlViews", "UserGroups", "UserRoles",
    "Users", "ValidationRuleGroups", "ValidationRules" ];

// ---------------------------------------------------------------

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

// Deselect all Metadata categories checkboxes
jQuery(function () {
    selectNone();
});

// Make the first letter lowercase
function lowercaseFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

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
        source: "../dhis-web-commons-ajax-json/get" + metadataCategoryName + ".action",
        iterator: lowercaseFirstLetter(metadataCategoryName),
        connectedTo: "selected" + metadataCategoryName,
        handler: function (item) {
            var option = jQuery("<option data-id='" + item.groups + "' />");
            option.text(item.name);
            option.attr("value", item.id);

            return option;
        }
    });
}

function getI18nAvailableMetadata(metadataCategoryName) {

    if( metadataCategoryName == "Attributes") {
        return i18n_available_attributes;
    }

    if( metadataCategoryName == "Categories") {
        return i18n_available_categories;
    }

    if( metadataCategoryName == "Charts") {
        return i18n_available_charts;
    }

    if( metadataCategoryName == "Concepts") {
        return i18n_available_concepts;
    }

    if( metadataCategoryName == "Constants") {
        return i18n_available_constants;
    }

    if( metadataCategoryName == "DataDictionaries") {
        return i18n_available_dataDictionaries;
    }

    if( metadataCategoryName == "DataElementGroupSets") {
        return i18n_available_dataElementGroupSets;
    }

    if( metadataCategoryName == "DataElementGroups") {
        return i18n_available_dataElementGroups;
    }

    if( metadataCategoryName == "DataElements") {
        return i18n_available_dataElements;
    }

    if( metadataCategoryName == "DataSets") {
        return i18n_available_dataSets;
    }

    if( metadataCategoryName == "Documents") {
        return i18n_available_documents;
    }

    if( metadataCategoryName == "IndicatorGroupSets") {
        return i18n_available_indicatorGroupSets;
    }

    if( metadataCategoryName == "IndicatorGroups") {
        return i18n_available_indicatorGroups;
    }

    if( metadataCategoryName == "Indicators") {
        return i18n_available_indicators;
    }

    if( metadataCategoryName == "IndicatorTypes") {
        return i18n_available_indicatorTypes;
    }

    if( metadataCategoryName == "MapLegendSets") {
        return i18n_available_mapLegendSets;
    }

    if( metadataCategoryName == "Maps") {
        return i18n_available_maps;
    }

    if( metadataCategoryName == "OptionSets") {
        return i18n_available_optionSets;
    }

    if( metadataCategoryName == "OrganisationUnitGroupSets") {
        return i18n_available_organisationUnitGroupSets;
    }

    if( metadataCategoryName == "OrganisationUnitLevels") {
        return i18n_available_organisationUnitLevels;
    }

    if( metadataCategoryName == "OrganisationUnits") {
        return i18n_available_organisationUnits;
    }

    if( metadataCategoryName == "ReportTables") {
        return i18n_available_reportTables;
    }

    if( metadataCategoryName == "Reports") {
        return i18n_available_reports;
    }

    if( metadataCategoryName == "SqlViews") {
        return i18n_available_sqlViews;
    }

    if( metadataCategoryName == "UserGroups") {
        return i18n_available_userGroups;
    }

    if( metadataCategoryName == "UserRoles") {
        return i18n_available_userRoles;
    }

    if( metadataCategoryName == "Users") {
        return i18n_available_users;
    }

    if( metadataCategoryName == "ValidationRuleGroups") {
        return i18n_available_validationRuleGroups;
    }

    if( metadataCategoryName == "ValidationRules") {
        return i18n_available_validationRules;
    }
}

function getI18nSelectedMetadata(metadataCategoryName) {

    if( metadataCategoryName == "Attributes") {
        return i18n_selected_attributes;
    }

    if( metadataCategoryName == "Categories") {
        return i18n_selected_categories;
    }

    if( metadataCategoryName == "Charts") {
        return i18n_selected_charts;
    }

    if( metadataCategoryName == "Concepts") {
        return i18n_selected_concepts;
    }

    if( metadataCategoryName == "Constants") {
        return i18n_selected_constants;
    }

    if( metadataCategoryName == "DataDictionaries") {
        return i18n_selected_dataDictionaries;
    }

    if( metadataCategoryName == "DataElementGroupSets") {
        return i18n_selected_dataElementGroupSets;
    }

    if( metadataCategoryName == "DataElementGroups") {
        return i18n_selected_dataElementGroups;
    }

    if( metadataCategoryName == "DataElements") {
        return i18n_selected_dataElements;
    }

    if( metadataCategoryName == "DataSets") {
        return i18n_selected_dataSets;
    }

    if( metadataCategoryName == "Documents") {
        return i18n_selected_documents;
    }

    if( metadataCategoryName == "IndicatorGroupSets") {
        return i18n_selected_indicatorGroupSets;
    }

    if( metadataCategoryName == "IndicatorGroups") {
        return i18n_selected_indicatorGroups;
    }

    if( metadataCategoryName == "Indicators") {
        return i18n_selected_indicators;
    }

    if( metadataCategoryName == "IndicatorTypes") {
        return i18n_selected_indicatorTypes;
    }

    if( metadataCategoryName == "MapLegendSets") {
        return i18n_selected_mapLegendSets;
    }

    if( metadataCategoryName == "Maps") {
        return i18n_selected_maps;
    }

    if( metadataCategoryName == "OptionSets") {
        return i18n_selected_optionSets;
    }

    if( metadataCategoryName == "OrganisationUnitGroupSets") {
        return i18n_selected_organisationUnitGroupSets;
    }

    if( metadataCategoryName == "OrganisationUnitLevels") {
        return i18n_selected_organisationUnitLevels;
    }

    if( metadataCategoryName == "OrganisationUnits") {
        return i18n_selected_organisationUnits;
    }

    if( metadataCategoryName == "ReportTables") {
        return i18n_selected_reportTables;
    }

    if( metadataCategoryName == "Reports") {
        return i18n_selected_reports;
    }

    if( metadataCategoryName == "SqlViews") {
        return i18n_selected_sqlViews;
    }

    if( metadataCategoryName == "UserGroups") {
        return i18n_selected_userGroups;
    }

    if( metadataCategoryName == "UserRoles") {
        return i18n_selected_userRoles;
    }

    if( metadataCategoryName == "Users") {
        return i18n_selected_users;
    }

    if( metadataCategoryName == "ValidationRuleGroups") {
        return i18n_selected_validationRuleGroups;
    }

    if( metadataCategoryName == "ValidationRules") {
        return i18n_selected_validationRules;
    }
}









