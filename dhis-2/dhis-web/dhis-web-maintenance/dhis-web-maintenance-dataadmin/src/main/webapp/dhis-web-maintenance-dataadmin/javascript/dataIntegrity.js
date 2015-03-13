$( document ).ready( function() {
    showLoader();

    $.ajax({
        url: '../api/dataIntegrity',
        method: 'POST',
        success: pollDataIntegrityCheckFinished,
        error: function( xhr, txtStatus, err ) {
            showErrorMessage( "Data integrity checks cannot be run. Request failed.", 3 );
            throw Error( xhr.responseText );
        }
    } );
} );

var checkFinishedTimeout = null;

function pollDataIntegrityCheckFinished() {
    pingNotifications( 'DATAINTEGRITY', 'notificationsTable', function() {
        $.getJSON( "../../api/system/taskSummaries/dataintegrity", {}, function( json ) {
            hideLoader();
            $( "#di-title" ).hide();
            $( "#di-completed" ).show();
            populateIntegrityItems( json );
            clearTimeout( checkFinishedTimeout );
        } );
    } );
    checkFinishedTimeout = setTimeout( "pollDataIntegrityCheckFinished()", 1500 );
}

var ViolationDisplayTypes = { "list" : 0, "mapList" : 1, "listList" : 2, "map" : 3 };

function populateIntegrityItems( json ) {
    function displayViolationMap( obj, id, lineBreak ) {
        var violationsText = "";
        var isViolations = true;

        if ( typeof obj !== "undefined" ) {
            for ( var o in obj ) {
                if ( obj.hasOwnProperty( o ) ) {
                    violationsText += o + ": " + obj[o];
                    violationsText += "<br>" + ( !!lineBreak ? "<br>" : "" );
                }
            }
        } else {
            isViolations = false;
        }

        displayViolation( id, isViolations, violationsText );
    }

    function displayViolationCollection( obj, id, lineBreak ) {
        var violationsText = "";
        var isViolations = true;

        if ( typeof obj !== "undefined" ) {
            obj.forEach( function( o ) {
                o.forEach( function( s ) {
                    violationsText += s + ", ";
                } );
                violationsText += "<br>" + ( !!lineBreak ? "<br>" : "" );
            } );
        } else {
            isViolations = false;
        }

        displayViolation( id, isViolations, violationsText );
    }

    function displayViolationMapList( obj, id, lineBreak ) {
        var violationsText = "";
        var isViolations = true;

        if ( typeof obj !== "undefined" ) {
            for ( var o in obj ) {
                if( obj.hasOwnProperty( o ) ) {
                    violationsText += o + ": ";
                    obj[o].forEach( function( s ) {
                        violationsText += s + ", ";
                    } );
                    violationsText += "<br>" + ( !!lineBreak ? "<br>" : "" );
                }
            }
        } else {
            isViolations = false;
        }

        displayViolation( id, isViolations, violationsText );
    }

    function displayViolationList( list, id, lineBreak ) {
        var violationsText = "";
        var isViolations = true;

        if ( typeof list !== "undefined" ) {
            list.forEach( function( violation ) {
                violationsText += violation + "<br>" + ( !!lineBreak ? "<br>" : "" );
            } );
        } else {
            isViolations = false;
        }

        displayViolation( id, isViolations, violationsText );
    }


    function displayViolation( id, isViolation, violationsText ) {

        var $button = $( "#" + id + "Button" );
        var $container = $( "#" + id + "Div" );

        if ( isViolation ) {
            $button
                .attr( { src: "../images/down.png", title: "View violations" } )
                .css( { cursor: "pointer" } )
                .click( function() { $container.slideToggle( "fast" ); } );

            $container.html( violationsText );
        } else {
            $button.attr({ src: "../images/check.png", title: "No violations" } );
        }

        $container.hide();
    }

    displayViolationList( json.dataElementsWithoutDataSet, "dataElementsWithoutDataSet", false, ViolationDisplayTypes.list );
    displayViolationList( json.dataElementsWithoutGroups, "dataElementsWithoutGroups", false, "list" );
    displayViolationMapList( json.dataElementsViolatingExclusiveGroupSets, "dataElementsViolatingExclusiveGroupSets", true, "mapList" );
    displayViolationMapList( json.dataElementsInDataSetNotInForm, "dataElementsInDataSetNotInForm", true, "mapList" );
    displayViolationMapList( json.dataElementsAssignedToDataSetsWithDifferentPeriodTypes, "dataElementsAssignedToDataSetsWithDifferentPeriodTypes", true, "mapList" );
    displayViolationMapList( json.categoryOptionCombosNotInDataElementCategoryCombo, "categoryOptionCombosNotInDataElementCategoryCombo", true, "mapList" );
    displayViolationList( json.dataSetsNotAssignedToOrganisationUnits, "dataSetsNotAssignedToOrganisationUnits", false, "list" );
    displayViolationList( json.sectionsWithInvalidCategoryCombinations, "sectionsWithInvalidCategoryCombinations", false, "list" );
    displayViolationCollection( json.indicatorsWithIdenticalFormulas, "indicatorsWithIdenticalFormulas", false, "listList" );
    displayViolationList( json.indicatorsWithoutGroups, "indicatorsWithoutGroups", false, "list" );
    displayViolationMap( json.invalidIndicatorNumerators, "invalidIndicatorNumerators", true, "map" );
    displayViolationMap( json.invalidIndicatorDenominators, "invalidIndicatorDenominators", true, "map" );
    displayViolationMapList( json.indicatorsViolatingExclusiveGroupSets, "indicatorsViolatingExclusiveGroupSets", true, "mapList" );
    displayViolationList( json.duplicatePeriods, "duplicatePeriods", false, "list" );
    displayViolationList( json.organisationUnitsWithCyclicReferences, "organisationUnitsWithCyclicReferences", false, "list" );
    displayViolationList( json.orphanedOrganisationUnits, "orphanedOrganisationUnits", false, "list" );
    displayViolationList( json.organisationUnitsWithoutGroups, "organisationUnitsWithoutGroups", false, "list" );
    displayViolationMapList( json.organisationUnitsViolatingExclusiveGroupSets, "organisationUnitsViolatingExclusiveGroupSets", true, "mapList" );
    displayViolationList( json.organisationUnitGroupsWithoutGroupSets, "organisationUnitGroupsWithoutGroupSets", false, "list" );
    displayViolationList( json.validationRulesWithoutGroups, "validationRulesWithoutGroups", false, "list" );
    displayViolationMapList( json.invalidValidationRuleLeftSideExpressions, "invalidValidationRuleLeftSideExpressions", true, "mapList" );
    displayViolationMapList( json.invalidValidationRuleRightSideExpressions, "invalidValidationRuleRightSideExpressions", true, "mapList" );
}

//function displayViolationMap( obj, id, lineBreak ) {
//    var violationsText = "";
//    var isViolations = true;
//
//    if ( typeof obj !== "undefined" ) {
//        for ( var o in obj ) {
//            if ( obj.hasOwnProperty( o ) ) {
//                violationsText += o + ": " + obj[o];
//                violationsText += "<br>" + ( !!lineBreak ? "<br>" : "" );
//            }
//        }
//    } else {
//        isViolations = false;
//    }
//
//    displayViolation( id, isViolations, violationsText );
//}
//
//function displayViolationCollection( obj, id, lineBreak ) {
//    var violationsText = "";
//    var isViolations = true;
//
//    if ( typeof obj !== "undefined" ) {
//        obj.forEach( function( o ) {
//            o.forEach( function( s ) {
//                violationsText += s + ", ";
//            } );
//            violationsText += "<br>" + ( !!lineBreak ? "<br>" : "" );
//        } );
//    } else {
//        isViolations = false;
//    }
//
//    displayViolation( id, isViolations, violationsText );
//}
//
//function displayViolationMapList( obj, id, lineBreak ) {
//    var violationsText = "";
//    var isViolations = true;
//
//    if ( typeof obj !== "undefined" ) {
//        for ( var o in obj ) {
//            if( obj.hasOwnProperty( o ) ) {
//                violationsText += o + ": ";
//                obj[o].forEach( function( s ) {
//                    violationsText += s + ", ";
//                } );
//                violationsText += "<br>" + ( !!lineBreak ? "<br>" : "" );
//            }
//        }
//    } else {
//        isViolations = false;
//    }
//
//    displayViolation( id, isViolations, violationsText );
//}
//
//function displayViolationList( list, id, lineBreak ) {
//    var violationsText = "";
//    var isViolations = true;
//
//    if ( typeof list !== "undefined" ) {
//        list.forEach( function( violation ) {
//            violationsText += violation + "<br>" + ( !!lineBreak ? "<br>" : "" );
//        } );
//    } else {
//        isViolations = false;
//    }
//
//    displayViolation( id, isViolations, violationsText );
//}
//
//
//function displayViolation( id, isViolation, violationsText ) {
//
//    var $button = $( "#" + id + "Button" );
//    var $container = $( "#" + id + "Div" );
//
//    if ( isViolation ) {
//        $button
//            .attr( { src: "../images/down.png", title: "View violations" } )
//            .css( { cursor: "pointer" } )
//            .click( function() { $container.slideToggle( "fast" ); } );
//
//        $container.html( violationsText );
//    } else {
//        $button.attr({ src: "../images/check.png", title: "No violations" } );
//    }
//
//    $container.hide();
//}
