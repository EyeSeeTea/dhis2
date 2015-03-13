$( document ).ready( function() {
    showLoader();

    $.ajax({
        url: '../api/dataIntegrity',
        method: 'POST',
        //success: pollDataIntegrityCheckFinished,
        success: pollDataIntegrityCheckFinished2,
        error: function( xhr, txtStatus, err ) {
            showErrorMessage( "Data integrity checks cannot be run. Request failed.", 3 );
            throw Error( xhr.responseText );
        }
    } );
} );

var checkFinishedTimeout = null;

function pollDataIntegrityCheckFinished() {
    pingNotifications( 'DATAINTEGRITY', 'notificationsTable', function() {
        $.getJSON( "getDataIntegrityReport.action", {}, function( json ) {
            hideLoader();
            $( "#di-title" ).hide();
            $( "#di-completed" ).show();
            populateIntegrityItems( json );
            clearTimeout( checkFinishedTimeout );
        } );
    } );
    checkFinishedTimeout = setTimeout( "pollDataIntegrityCheckFinished()", 1500 );
}

function pollDataIntegrityCheckFinished2() {
    pingNotifications( 'DATAINTEGRITY', 'notificationsTable', function() {
        $.getJSON( "../../api/system/taskSummaries/dataintegrity", {}, function( json ) {
            hideLoader();
            $( "#di-title" ).hide();
            $( "#di-completed" ).show();
            populateIntegrityItems2( json );
            clearTimeout( checkFinishedTimeout );
        } );
    } );
    checkFinishedTimeout = setTimeout( "pollDataIntegrityCheckFinished2()", 1500 );
}

function populateIntegrityItems2( json ) {
    console.log( json );

    displayViolationList2( json.dataElementsWithoutDataSet, "dataElementsWithoutDataSet", false );
    displayViolationList2( json.dataElementsWithoutGroups, "dataElementsWithoutGroups", false );
    displayViolationList2( json.dataElementsViolatingExclusiveGroupSets, "dataElementsViolatingExclusiveGroupSets", true );
    displayViolationList2( json.dataElementsInDataSetNotInForm, "dataElementsInDataSetNotInForm", true );
    displayViolationList2( json.dataElementsAssignedToDataSetsWithDifferentPeriodTypes, "dataElementsAssignedToDataSetsWithDifferentPeriodTypes", true );
    displayViolationList2( json.categoryOptionCombosNotInDataElementCategoryCombo, "categoryOptionCombosNotInDataElementCategoryCombo", true );
    displayViolationList2( json.dataSetsNotAssignedToOrganisationUnits, "dataSetsNotAssignedToOrganisationUnits", false );
    displayViolationList2( json.sectionsWithInvalidCategoryCombinations, "sectionsWithInvalidCategoryCombinations", false );
    displayViolationList2( json.indicatorsWithIdenticalFormulas, "indicatorsWithIdenticalFormulas", false );
    displayViolationList2( json.indicatorsWithoutGroups, "indicatorsWithoutGroups", false );
    displayViolationList2( json.invalidIndicatorNumerators, "invalidIndicatorNumerators", true );
    displayViolationList2( json.invalidIndicatorDenominators, "invalidIndicatorDenominators", true );
    displayViolationList2( json.indicatorsViolatingExclusiveGroupSets, "indicatorsViolatingExclusiveGroupSets", true );
    displayViolationList2( json.organisationUnitsWithCyclicReferences, "organisationUnitsWithCyclicReferences", false );
    displayViolationList2( json.orphanedOrganisationUnits, "orphanedOrganisationUnits", false );
    displayViolationList2( json.organisationUnitsWithoutGroups, "organisationUnitsWithoutGroups", false );
    displayViolationList2( json.organisationUnitsViolatingExclusiveGroupSets, "organisationUnitsViolatingExclusiveGroupSets", true );
    displayViolationList2( json.organisationUnitGroupsWithoutGroupSets, "organisationUnitGroupsWithoutGroupSets", false );
    displayViolationList2( json.duplicatePeriods, "duplicatePeriods", false );
    displayViolationList2( json.validationRulesWithoutGroups, "validationRulesWithoutGroups", false );
    displayViolationList2( json.invalidValidationRuleLeftSideExpressions, "invalidValidationRuleLeftSideExpressions", true );
    displayViolationList2( json.invalidValidationRuleRightSideExpressions, "invalidValidationRuleRightSideExpressions", true );

}

function displayViolationList2( list, id, lineBreak ) {
    var renderWithViolations = function( jqButton, jqContainer ) {
        jqButton
            .attr( { src: "../images/down.png", title: "View violations" } )
            .css( { cursor: "pointer" } )
            .click( function() { jqContainer.slideToggle( "fast" ); } );
    };

    var renderNoViolations = function( jqButton ) {
        jqButton.attr({ src: "../images/check.png", title: "No violations" });
    };

    var $button = $( "#" + id + "Button" );
    var $container = $( "#" + id + "Div" );

    if ( typeof list !== "undefined" ) {
        renderWithViolations( $button, $container );

        var violations = "";

        if( Array.isArray( list ) && list.length > 0 ) { // TODO Check length elsewhere
            list.forEach( function( violation ) {
                violations += violation + "<br>"
                violations += !!lineBreak ? "<br>" : "";
            } );
        } else if ( list !== null && typeof list === "object" ) {
            console.log( "id: " + id );
        }

        $container.html( violations );
    } else {
        renderNoViolations( $button );
    }

    $container.hide();
}

function displayViolationMap2() {

}

function displayViolationCollection2() {

}

function displayViolationWithMapList2() {

}

function populateIntegrityItems( json ) {
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

function displayViolationList( list, id, lineBreak ) {
    var $button = $( "#" + id + "Button" );
    var $container = $( "#" + id + "Div" );

    if ( list.length > 0 ) {
        // Display image "drop-down" button
        $button
           .attr( { src: "../images/down.png", title: "View violations" } )
           .css( { cursor: "pointer" } )
           .click( function() { $container.slideToggle( "fast" ); } );

        // Populate violation div
        var violations = "";
        
        for ( var i = 0; i < list.length; i++ ) {
            violations += list[i] + "<br>";
            violations += !!lineBreak ? "<br>" : "";
        }
        
        $container.html( violations );
    }
    else
    {
        // Display image "check" button
        $button.attr({ src: "../images/check.png", title: "No violations" });
    }
        
    $container.hide();
}
