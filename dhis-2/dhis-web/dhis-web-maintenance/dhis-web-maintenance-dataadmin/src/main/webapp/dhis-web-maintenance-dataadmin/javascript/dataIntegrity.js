
$( document ).ready( function()
{
    showLoader();
    runDataIntegrityTask();
    //$.getJSON( "getDataIntegrity.action", {}, populateIntegrityItems );
    //pingNotificationsTimeout();
} );

function runDataIntegrityTask() {
    $.ajax({
        url: '../api/dataIntegrity',
        method: 'POST',
        success: registerDataIntegrityTimeout,
        error: function( response ) {
            throw Error( "Data integrity checks cannot be run. Request failed." );
        }
    } );
}

var pingTimeout = null;

function registerDataIntegrityTimeout() {
    pingNotifications( 'DATAINTEGRITY', 'notificationsTable', getDataIntegrityReport );
    pingTimeout = setTimeout( "registerDataIntegrityTimeout()", 2500 );
}

function getDataIntegrityReport() {
    console.log( "Getting data integrity report" );
    $.getJSON( "getDataIntegrityReport.action", {}, function( json ) {
        console.log( json );
        populateIntegrityItems( json );
    } );
}

function populateIntegrityItems( json )
{
    hideLoader();

    displayViolationList( json.dataElementsWithoutDataSet, "dataElementsWithoutDataSet", false );
    displayViolationList( json.dataElementsWithoutGroups, "dataElementsWithoutGroups", false );
    displayViolationList( json.dataElementsViolatingExclusiveGroupSets, "dataElementsViolatingExclusiveGroupSets", true );
    displayViolationList( json.dataElementsInDataSetNotInForm, "dataElementsInDataSetNotInForm", true );
    displayViolationList( json.dataElementsAssignedToDataSetsWithDifferentPeriodTypes, "dataElementsAssignedToDataSetsWithDifferentPeriodTypes", true );
    displayViolationList( json.categoryOptionCombosNotInDataElementCategoryCombo, "categoryOptionCombosNotInDataElementCategoryCombo", true );
    displayViolationList( json.dataSetsNotAssignedToOrganisationUnits, "dataSetsNotAssignedToOrganisationUnits", false );
    displayViolationList( json.sectionsWithInvalidCategoryCombinations, "sectionsWithInvalidCategoryCombinations", false );
    displayViolationList( json.indicatorsWithIdenticalFormulas, "indicatorsWithIdenticalFormulas", false );
    displayViolationList( json.indicatorsWithoutGroups, "indicatorsWithoutGroups", false );
    displayViolationList( json.invalidIndicatorNumerators, "invalidIndicatorNumerators", true );
    displayViolationList( json.invalidIndicatorDenominators, "invalidIndicatorDenominators", true );
    displayViolationList( json.indicatorsViolatingExclusiveGroupSets, "indicatorsViolatingExclusiveGroupSets", true );
    displayViolationList( json.organisationUnitsWithCyclicReferences, "organisationUnitsWithCyclicReferences", false );
    displayViolationList( json.orphanedOrganisationUnits, "orphanedOrganisationUnits", false );
    displayViolationList( json.organisationUnitsWithoutGroups, "organisationUnitsWithoutGroups", false );
    displayViolationList( json.organisationUnitsViolatingExclusiveGroupSets, "organisationUnitsViolatingExclusiveGroupSets", true );
    displayViolationList( json.organisationUnitGroupsWithoutGroupSets, "organisationUnitGroupsWithoutGroupSets", false );
    displayViolationList( json.duplicatePeriods, "duplicatePeriods", false );
    displayViolationList( json.validationRulesWithoutGroups, "validationRulesWithoutGroups", false );
    displayViolationList( json.invalidValidationRuleLeftSideExpressions, "invalidValidationRuleLeftSideExpressions", true );
    displayViolationList( json.invalidValidationRuleRightSideExpressions, "invalidValidationRuleRightSideExpressions", true );
}

function displayViolationList( list, id, lineBreak )
{
    if ( list.length > 0 )
    {
        // Display image "drop-down" button
        $( "#" + id + "Button" )
           .attr({ src: "../images/down.png", title: "View violations" })
           .css({ cursor: "pointer" })
           .click( function() { $( "#" + id + "Div" ).slideToggle( "fast" ); } );

        // Populate violation div

        var violations = "";
        
        for ( var i = 0; i < list.length; i++ )
        {
            violations += list[i] + "<br>";
            violations += !!lineBreak ? "<br>" : "";
        }
        
        $( "#" + id + "Div" ).html( violations );
    }
    else
    {
        // Display image "check" button

        $( "#" + id + "Button" ).attr({ src: "../images/check.png", title: "No violations" });
    }
        
    $( "#" + id + "Div" ).hide();
}

function displayTest() {
    console.log("Async task is finished" );
    $.getJSON( "getDataIntegrityReport.action", {}, populateIntegrityItems );
}

function pingNotificationsTimeout() {
    console.log("pingnotificationstimeout");
    pingNotifications( 'DATAINTEGRITY', 'notificationsTable', displayTest );
}