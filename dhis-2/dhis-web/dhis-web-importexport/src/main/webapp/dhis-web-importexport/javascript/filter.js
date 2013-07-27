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
            success: function (response)
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
        });
}

// Populate the Filter rows with values
function insertFilterDesign(filters)
{
    for( var i = 0; i < filters.length; i++)
    {
        var filterId = filters[i].id;
        var filterName = removeWhiteSpace(filters[i].name);
        var design =
                      '<tr id="' + filterId +'" style="margin: 20px;">'
                    +      '<td>'
                    +          '<p style="padding-left: 10px; font-size: 10pt;">' + filters[i].name + '</p>'
                    +      '</td>'
                    +      '<td style="float: left;">'
                    +          '<button id="buttonApply' + filterName + '" " value="' + filterId + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/start_process.png" alt="' + i18n_apply + '" )"/>'
                    +          '</button>'
                    +      '</td>'
                    +      '<td style="float: right;">'
                    +          '<button id="buttonSave' + filterName + ' " value="' + filterId + '"" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/add.png" alt="' + i18n_save + '" )"/>'
                    +          '</button>'
                    +          '<button id="buttonEdit' + filterName + '" " value="' + filterId + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/edit.png" alt="' + i18n_edit + '" )"/>'
                    +          '</button>'
                    +          '<button id="buttonRemove' + filterName + '" " value="' + filterId + '" type="button" style="background-color: inherit;">'
                    +              '<img src="../images/delete.png" alt="' + i18n_remove + '" )"/>'
                    +          '</button>'
                    +      '</td>'
                    + '</tr>'
            ;

        filterButtonEvents(filters[i]);
        $("#filterTable").append(design);
        if ( i % 2 === 0 )
        {
            $("#" + filterId).css("background-color", "#EEF7FA");
        }
    }
}

// Add Filter button events
function filterButtonEvents(filter)
{
    filterApplyButton(filter);
    filterSaveButton(filter);
    filterRemoveButton(filter);
}

// Apply a Filter to the MetaData Tables
function filterApplyButton(filter)
{
    var filterName = removeWhiteSpace( filter.name );
    $("#buttonApply" + filterName).live("click", function ()
    {
        selectAll();
        var id = $(this).attr("value");

        for ( var i = 0; i < filters.length; i++ )
        {
            if ( id === filters[i].id )
            {
                applyFilter( filters[i].metaDataUids );
            }
        }
    });
}

// Apply a Filter to the tables by selecting the uids from the Filter
function applyFilter(data)
{
    var uids = data.split(", ");

    for ( var i = 0; i < uids.length; i++ )
    {
        for ( var j = 0; j < metaDataArray.length; j++ )
        {
            $("#available" + metaDataArray[j] + " option").each(function ()
            {
                var availableUid = $(this).val();
                if ( uids[i] === availableUid )
                {
                    $(this).prop("selected", true);
                }
            });
        }
    }

    for( var i = 0; i < metaDataArray.length; i++)
    {
        moveSelectedValuesByCategory(metaDataArray[i]);
    }
}

// Save a Filter to the database
function filterSaveButton(filter)
{
    var filterName = removeWhiteSpace( filter.name );
    $("#buttonSave" + filterName).live("click", function ()
    {
    });
}

// Delete a Filter from the database
function filterRemoveButton(filter)
{
    var filterName = removeWhiteSpace( filter.name );
    $("#buttonRemove" + filterName).live("click", function ()
    {
        var id = ($(this).attr("value"));
        for ( var i = 0; i < filters.length; i++ )
        {
            if ( id === filters[i].id )
            {
                var json = filters[i];
                $.ajax(
                    {
                        type: "POST",
                        url: "../api/detailedMetaData/removeFilter",
                        contentType: "application/json",
                        data: JSON.stringify(json),
                        success: function ()
                        {
                            console.log("Filter successfully removed.");
                        },
                        error: function (request, status, error)
                        {
                            console.log(request.responseText);
                            console.log(arguments);
                            alert("Remove filter process failed.");
                        }
                    });
            }
        }
    });
}
