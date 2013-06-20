function selectAll()
{
    $("#exportForm").find("input:checkbox").attr("checked", true);
}

function selectNone()
{
    $("#exportForm").find("input:checkbox").attr("checked", false);
}

function exportMetaData()
{
    alert('Merge');
}

function showTable() {
    if($('#attributeTypes').is(':checked')) {
        $('#availableAttributes').show();
    } else {
        $('#availableAttributes').hide();
    }

    if($('#categories').is(':checked')) {
        $('#availableDataElementCategories').show();
    } else {
        $('#availableDataElementCategories').hide();
    }

    if($('#documents').is(':checked')) {
        $('#documentsTable').show();
    } else {
        $('#documentsTable').hide();
    }
}
