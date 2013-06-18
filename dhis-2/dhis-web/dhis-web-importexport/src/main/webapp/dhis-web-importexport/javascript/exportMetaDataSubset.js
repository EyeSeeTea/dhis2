
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
    alert('ShowTable');
    if($('#attributeTypes').is(':checked')) {
        $('#attributeTypesTable').show();
    } else {
        $('#attributeTypesTable').hide();
    }

    if($('#categories').is(':checked')) {
        $('#categoriesTable').show();
    } else {
        $('#categoriesTable').hide();
    }

    if($('#documents').is(':checked')) {
        $('#documentsTable').show();
    } else {
        $('#documentsTable').hide();
    }
}
