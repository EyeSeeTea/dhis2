function selectAll() {
    $("#exportForm").find("input:checkbox").attr("checked", true);
};

function selectNone() {
    $("#exportForm").find("input:checkbox").attr("checked", false);
};

function lowercaseFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}


function exportMetaDataSubSet() {

    if ($('#dataSets').is(':checked')) {
        $('#sections').attr('checked', true);
    }
    else {
        $('#sections').attr('checked', false);
    }

    if ($('#categories').is(':checked')) {
        $('#categoryCombos').attr('checked', true);
        $('#categoryOptionCombos').attr('checked', true);
        $('#categoryOptions').attr('checked', true);
    }
    else {
        $('#categoryCombos').attr('checked', false);
        $('#categoryOptionCombos').attr('checked', false);
        $('#categoryOptions').attr('checked', false);
    }

    if ($('#mapLegendSets').is(':checked')) {
        $('#mapLegends').attr('checked', true);
    }
    else {
        $('#mapLegends').attr('checked', false);
    }

    if ($('#maps').is(':checked')) {
        $('#mapViews').attr('checked', true);
    }
    else {
        $('#mapViews').attr('checked', false);
    }

    var url = "../api/metaData";
    var format = $("#format").val();
    var compression = $("#compression").val();

    url += "." + format;

    if (compression == "zip") {
        url += ".zip";
    }
    else if (compression == "gz") {
        url += ".gz";
    }

    url += "?assumeTrue=false&" + $("#exportForm").serialize();

    log("url" + url);

    alert(url);
    window.location = url;
};

//function sendInformation() {
//    var data;
//    if ($('#attributeTypes').is(':checked')) {
//        data = {dataElementAttribute: "false"};
//    }
//    $.ajax(
//        {
//            url: "../api/metaData.xml.zip",
//            type: "GET",
//            data: data,
//            dataType: "text",
//            success: function () {
//                alert("A MERS OVIDIU");
//            },
//            error: function (data, status, er) {
//                alert("error: " + data + " status: " + status + " er:" + er);
//            }
//        }
//    )
//};

// Collapse Metadata Category information
jQuery(function () {
    $('#attributes').click(function () {
        if ($('#attributes').is(':checked')) {
            insertMetadataDesign("Attributes");
            loadMetadata("Attributes");
            $('#selectionAreaAttributes').show();
        } else {
            $('#selectionAreaAttributes').show();
            removeMetadataDesign("Attributes");
        }
    });

    $('#dataSets').click(function () {
        if ($('#dataSets').is(':checked')) {
            insertMetadataDesign("DataSets");
            loadMetadata("DataSets");
            $('#selectionAreaDataSets').show();
        } else {
            $('#selectionAreaDataSets').show();
            removeMetadataDesign("DataSets");
        }
    })
});

// Deselect all Metadata categories combo boxes
jQuery(function () {
    selectNone();
});

// Insert Metadata HTML & CSS for a Category
function insertMetadataDesign(metadataCategoryName) {

    if( metadataCategoryName == "Attributes") {
        var i18n_available_metadata = i18n_available_attributes;
        var i18n_selected_metadata = i18n_selected_attributes;
    }

    if( metadataCategoryName == "DataSets") {
        var i18n_available_metadata = i18n_available_dataSets;
        var i18n_selected_metadata = i18n_selected_dataSets;
    }

    var design =
        '<table id="selectionArea'+metadataCategoryName + '">' +
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
                '<td><input type="text" id="startDate" name="startDate" value="$!startDate" style="width:230px"></td>' +
                    '<td></td>' +
                    '<td><input type="text" id="endDate" name="endDate" value="$!endDate" style="width:230px"></td>' +
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
                                'onclick="dhisAjaxSelect_moveAll( \'available' + metadataCategoryName + ' );"/><br/>' +
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
};

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
};













