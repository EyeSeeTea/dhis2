// Global Variables
// --------------------------------------------------------------
var filters = [];
// --------------------------------------------------------------

// Load Filters
function loadFilters()
{
    $.ajax(
        {
            type: "GET",
            url: "../api/detailedMetaData/getFilters",
            dataType: "json",
            success: function(response)
            {
                filters = response;
                console.log("Filters: " + JSON.stringify(filters));
                insertFilterDesign(filters);
            },
            error: function (request, status, error)
            {
                console.log(request.responseText);
                console.log(arguments);
                alert("Getting filters process failed.");
            }
        })
}

// Populate the Filter rows with values
function insertFilterDesign(filters)
{
    for( var i = 0; i < filters.length; i++)
    {
        var design =
                '<tr id="' + filters[i].name +'" style="margin: 20px;">'
                    +      '<td>'
                    +          '<p style="padding-left: 10px; font-size: 10pt;">' + filters[i].name + '</p>'
                    +      '</td>'
                    +      '<td style="float: left;">'
                    +          '<button id="buttonApply' + filters[i].name + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/start_process.png" alt="' + i18n_apply + '" )"/>'
                    +          '</button>'
                    +      '</td>'
                    +      '<td style="float: right;">'
                    +          '<button id="buttonSave' + filters[i].name + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/add.png" alt="' + i18n_save + '" )"/>'
                    +          '</button>'
                    +          '<button id="buttonEdit' + filters[i].name + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/edit.png" alt="' + i18n_edit + '" )"/>'
                    +          '</button>'
                    +          '<button id="buttonRemove' + filters[i].name + '" " value="' + filters[i].id + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/delete.png" alt="' + i18n_remove + '" )"/>'
                    +          '</button>'
                    +      '</td>'
                    + '</tr>'
            ;
        filterButtonEvents(filters[i]);
        $("#filterTable").append(design);

        if( i % 2 === 0)
        {
            $("#" + filters[i].name).css("background-color", "#EEF7FA");
        }
    }
}

// Add filter button events
function filterButtonEvents(filter)
{
    filterApplyButton(filter);
    filterSaveButton(filter);
    filterRemoveButton(filter);
}

// Apply a Filter to the MetaData Tables
function filterApplyButton(filter)
{
    $("#buttonApply" + filter.name).live("click", function ()
    {
        alert(filter.name + " APPLIED");
    })
}

// Save a Filter to the database
function filterSaveButton(filter)
{
    $("#buttonSave" + filter.name).live("click", function ()
    {
        alert(filter.name + " SAVED");
    })
}

// Delete a Filter from the database
function filterRemoveButton(filter)
{
    $("#buttonRemove" + filter.name).live("click", function ()
    {
        var id = ($(this).attr("value"));
        for (var i = 0; i < filters.length; i++)
        {
            if (id === filters[i].id) {
                var json = filters[i];
                $.ajax(
                    {
                        type: "POST",
                        url: "../api/detailedMetaData/removeFilter",
                        contentType: "application/json",
                        data: JSON.stringify(json),
                        success: function () {
                            console.log("Filter successfully removed.");
                        },
                        error: function (request, status, error) {
                            console.log(request.responseText);
                            console.log(arguments);
                            alert("Remove filter process failed.");
                        }
                    }
                )
            }
        }
    })
}
