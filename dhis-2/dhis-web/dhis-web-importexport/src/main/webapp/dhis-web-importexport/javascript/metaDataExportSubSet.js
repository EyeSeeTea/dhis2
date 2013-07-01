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
    $('#attributeTypes').click(function () {
        if ($('#attributeTypes').is(':checked')) {
            $('#attributeTypesSelectionArea').show();
            loadMetadata("Attributes");
        } else {
            $('#attributeTypesSelectionArea').show();
        }
    });

    $('#dataSets').click(function () {
        if ($('#dataSets').is(':checked')) {
            $('#dataSetsSelectionArea').show();
            loadMetadata("DataSets");
        } else {
            $('#dataSetsSelectionArea').show();
        }
    })
});

// Deselect all Metadata categories combo boxes
jQuery(function () {
    selectNone();
});

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










