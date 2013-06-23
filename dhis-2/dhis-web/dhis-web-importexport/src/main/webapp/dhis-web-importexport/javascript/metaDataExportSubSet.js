function selectAll() {
    $("#exportForm").find("input:checkbox").attr("checked", true);
}

function selectNone() {
    $("#exportForm").find("input:checkbox").attr("checked", false);
}

function displayMetaData() {

    if ($('#attributeTypes').is(':checked')) {
        $('#availableAttributes').show();
    } else {
        $('#availableAttributes').hide();
    }

    if ($('#categories').is(':checked')) {
        $('#availableDataElementCategories').show();
    } else {
        $('#availableDataElementCategories').hide();
    }

    if ($('#dataSets').is(':checked')) {
        $('#availableDataSets').show();
    } else {
        $('#availableDataSets').hide();
    }

    if ($('#dataElements').is(':checked')) {
        $('#availableDataElements').show();
    } else {
        $('#availableDataElements').hide();
    }
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
}
