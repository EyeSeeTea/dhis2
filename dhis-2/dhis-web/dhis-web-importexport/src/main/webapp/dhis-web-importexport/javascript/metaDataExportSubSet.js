// Global Variables

var metadataArray = ["Attributes", "DataElements", "DataSets", "Indicators"];

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

            '<!-- TIMESTAMP FILTER -->' +
            '<tr>' +
                '<th>' + i18n_start_date + '</th>' +
                '<td></td>' +
                '<th>' + i18n_end_date + '</th>' +
            '</tr>' +
            '<tr>' +
                '<td><input type="text" id="startDate" name="startDate" value="' + startDate + '" style="width:230px"></td>' +
                    '<td></td>' +
                    '<td><input type="text" id="endDate" name="endDate" value="' + endDate + '" style="width:230px"></td>' +
                    '</tr>' +
                    '<tr>' +
                        '<td colspan="3" height="15"></td>' +
                    '</tr>' +

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

    $("#mainDiv" + metadataCategoryName).append(design);
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

    if( metadataCategoryName == "DataElements") {
        return i18n_available_dataElements;
    }

    if( metadataCategoryName == "DataSets") {
        return i18n_available_dataSets;
    }

    if( metadataCategoryName == "Indicators") {
        return i18n_available_indicators;
    }
}

function getI18nSelectedMetadata(metadataCategoryName) {
    if( metadataCategoryName == "Attributes") {
        return i18n_selected_attributes;
    }

    if( metadataCategoryName == "DataElements") {
        return i18n_selected_dataElements;
    }

    if( metadataCategoryName == "DataSets") {
        return i18n_selected_dataSets;
    }

    if( metadataCategoryName == "Indicators") {
        return i18n_selected_indicators;
    }
}









