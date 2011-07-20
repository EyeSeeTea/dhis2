// Identifiers for which zero values are, insignificant, also used in entry.js, populated in select.vm
var significantZeros = [];

// Associative array with [indicator id, expression] for indicators in form, also used in entry.js, populated in select.vm
var indicatorFormulas = [];

// Array with associative arrays for each data set, populated in select.vm
var dataSets = [];

// Indicates whether any data entry form has been loaded
var dataEntryFormIsLoaded = false;

// Currently selected organisation unit identifier
var currentOrganisationUnitId = null;

// Currently selected data set identifier
var currentDataSetId = null;

// Current offset, next or previous corresponding to increasing or decreasing value with one
var currentPeriodOffset = 0;

// Period type object
var periodTypeFactory = new PeriodType();

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#ffffff';

function addEventListeners()
{
    $( '[name="entryfield"]' ).focus( valueFocus );
    $( '[name="entryselect"]' ).focus( valueFocus );
}

function clearPeriod()
{
    clearListById( 'selectedPeriodId' );
    clearEntryForm();
}

function clearEntryForm()
{
    $( '#contentDiv' ).html( '' );
    
	currentPeriodOffset = 0;
	
    dataEntryFormIsLoaded = false;
}

// -----------------------------------------------------------------------------
// OrganisationUnit Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits )
{
	currentOrganisationUnitId = orgUnits[0];
	
    $( '#selectedDataSetId' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

    var url = 'loadDataSets.action';

    clearListById( 'selectedDataSetId' );

    $.getJSON( url, { dataSetId:dataSetId },  function( json )
    {
        $( '#selectedOrganisationUnit' ).val( json.organisationUnit.name );
        $( '#currentOrganisationUnit' ).html( json.organisationUnit.name );

        addOptionById( 'selectedDataSetId', '-1', '[ ' + i18n_select_data_set + ' ]' );

        for ( i in json.dataSets )
        {
            addOptionById( 'selectedDataSetId', json.dataSets[i].id, json.dataSets[i].name );
        }

        if ( json.dataSetValid && dataSetId != null )
        {
            $( '#selectedDataSetId' ).val( dataSetId );

            if ( periodId && periodId != -1 && dataEntryFormIsLoaded ) //TODO if period valid
            {
                showLoader();
                loadDataValues();
            }
        } 
        else
        {
            clearPeriod();
        }
    } );
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// Next/Previous Periods Selection
// -----------------------------------------------------------------------------

function nextPeriodsSelected()
{
    currentPeriodOffset++;
    displayPeriodsInternal();
}

function previousPeriodsSelected()
{
    currentPeriodOffset--;
    displayPeriodsInternal();
}

function displayPeriodsInternal()
{
    var dataSetId = $( '#selectedDataSetId' ).val();    
    var periodType = dataSets[dataSetId].periodType;
    var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

	clearListById( 'selectedPeriodId' );

	addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );

    for ( i in periods )
    {
        addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
    }
}

// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

function dataSetSelected()
{
    $( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();
    var periodType = dataSets[dataSetId].periodType;
	var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

    if ( dataSetId && dataSetId != -1 )
    {
        clearListById( 'selectedPeriodId' );

        addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );

        for ( i in periods )
        {
            addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
        }
        
        var previousPeriodType = currentDataSetId ? dataSets[currentDataSetId].periodType : null;

        if ( periodId && periodId != -1 && previousPeriodType && previousPeriodType == periodType ) //TODO if periodValid
        {
            showLoader();
            $( '#selectedPeriodId' ).val( periodId );
            $( '#contentDiv' ).load( 'loadForm.action', { periodId:periodId, dataSetId:dataSetId }, loadDataValues );
        } 
        else
        {
            clearEntryForm();
        }
        
    	currentDataSetId = dataSetId;
    }
}

// -----------------------------------------------------------------------------
// DisplayMode Selection
// -----------------------------------------------------------------------------

function displayModeSelected()
{
    showLoader();

    var url = 'loadForm.action?displayMode=' + $( "input[name='displayMode']:checked" ).val();

    $( '#contentDiv' ).load( url, loadDataValues );
}

// -----------------------------------------------------------------------------
// Period Selection
// -----------------------------------------------------------------------------

function periodSelected()
{
    var periodName = $( '#selectedPeriodId :selected' ).text();
    var dataSetId = $( '#selectedDataSetId' ).val();

    $( '#currentPeriod' ).html( periodName );

    var periodId = $( '#selectedPeriodId' ).val();
    
    if ( periodId && periodId != -1 )
    {
        showLoader();
        
        if ( dataEntryFormIsLoaded )
        {
        	loadDataValues();
        }
        else
        {
        	$( '#contentDiv' ).load( 'loadForm.action', { periodId:periodId, dataSetId:dataSetId }, loadDataValues );
        }
    }
}

// -----------------------------------------------------------------------------
// Form
// -----------------------------------------------------------------------------

function loadDataValues()
{
	insertDataValues();
	displayEntryFormCompleted();
}

function insertDataValues()
{
	var valueMap = new Array();
	
	var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();
	
	// Clear existing values and colors
	
	$( '[name="entryfield"]' ).val( '' );
	$( '[name="entryselect"]' ).val( '' );
	
	$( '[name="entryfield"]' ).css( 'background-color', COLOR_WHITE );
	$( '[name="entryselect"]' ).css( 'background-color', COLOR_WHITE );
	
	$( '[name="min"]' ).html( '' );
	$( '[name="max"]' ).html( '' );
	
	$.getJSON( 'getDataValues.action', { periodId:periodId, dataSetId:dataSetId }, function( json ) 
	{
		// Set data values, works for select lists too as data value = select value
	
		$.each( json.dataValues, function( i, value )
		{
			var fieldId = '#' + value.id + '-val';
			
			if ( $( fieldId ) )
			{
				$( fieldId ).val( value.val );
			}
			
			valueMap[value.id] = value.val;
		} );
		
		// Set min-max values and colorize violation fields
		
		$.each( json.minMaxDataElements, function( i, value )
		{
			var minFieldId = '#' + value.id + '-min';
			var maxFieldId = '#' + value.id + '-max';
			var valFieldId = '#' + value.id + '-val';
			
			if ( $( minFieldId ) )
			{
				$( minFieldId ).html( value.min );
			}
			
			if ( $( maxFieldId ) )
			{
				$( maxFieldId ).html( value.max );
			}
			
			var dataValue = valueMap[value.id];
			
			if ( dataValue && ( ( value.min && new Number( dataValue ) < new Number( value.min ) ) 
				|| ( value.max && new Number( dataValue ) > new Number( value.max ) ) ) )
			{
				$( valFieldId ).css( 'background-color', COLOR_ORANGE );
			}
		} );
		
		// Update indicator values in form
		
		updateIndicators();
	} );
}

function displayEntryFormCompleted()
{
    addEventListeners();
    enable( 'validationButton' );
    dataEntryFormIsLoaded = true;
    hideLoader();
}

function valueFocus( e )
{
	var id = e.target.id;

	var dataElementId = id.split( '-' )[0];
	var optionComboId = id.split( '-' )[1];
	
	var dataElementName = $( '#' + dataElementId + '-dataelement' ).text();
	var optionComboName = $( '#' + optionComboId + '-optioncombo' ).text();
	
	$( "#currentDataElement" ).html( dataElementName + ' ' + optionComboName );
}

function keyPress( event, field )
{
    var key = event.keyCode || event.charCode || event.which;

    var focusField = ( key == 13 || key == 40 ) ? getNextEntryField( field )
            : ( key == 38 ) ? getPreviousEntryField( field ) : false;

    if ( focusField )
    {
        focusField.focus();
    }

    return true;
}

function getNextEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );

    field = $( 'input[name="entryfield"][tabindex="' + ( ++index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="entryfield"][tabindex="' + ( ++index ) + '"]' );
        } 
        else
        {
            return field;
        }
    }
}

function getPreviousEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );

    field = $( 'input[name="entryfield"][tabindex="' + ( --index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="entryfield"][tabindex="' + ( --index ) + '"]' );
        } 
        else
        {
            return field;
        }
    }
}

// -----------------------------------------------------------------------------
// Data completeness
// -----------------------------------------------------------------------------

function validateCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_complete );

    if ( confirmed )
    {
    	var dataSetId = $( '#selectedDataSetId' ).val();
		var periodId = $( '#selectedPeriodId' ).val();
		
        $( '#completeButton' ).attr( 'disabled', 'disabled' );
        $( '#undoButton' ).removeAttr( 'disabled' );

        $.getJSON( 'getValidationViolations.action', { periodId:periodId, dataSetId:dataSetId }, registerCompleteDataSet ).error( function()
        {
            $( '#completeButton' ).removeAttr( 'disabled' );
            $( '#undoButton' ).attr( 'disabled', 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    }
}

function registerCompleteDataSet( json )
{
    if ( json.response == 'success' )
    {
    	var dataSetId = $( '#selectedDataSetId' ).val();
		var periodId = $( '#selectedPeriodId' ).val();
		
        $.getJSON( 'registerCompleteDataSet.action', { periodId:periodId, dataSetId:dataSetId }, function()
        {
        } ).error( function()
        {
            $( '#completeButton' ).removeAttr( 'disabled' );
            $( '#undoButton' ).attr( 'disabled', 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    } 
    else
    {
        window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
    }
}

function undoCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_undo );

    if ( confirmed )
    {
    	var dataSetId = $( '#selectedDataSetId' ).val();
		var periodId = $( '#selectedPeriodId' ).val();
		
        $( '#completeButton' ).removeAttr( 'disabled' );
        $( '#undoButton' ).attr( 'disabled', 'disabled' );

        $.getJSON( 'undoCompleteDataSet.action', { periodId:periodId, dataSetId:dataSetId }, function()
        {
        } ).error( function()
        {
            $( '#completeButton' ).attr( 'disabled', 'disabled' );
            $( '#undoButton' ).removeAttr( 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    }
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function viewHist( dataElementId, optionComboId )
{
	var periodId = $( '#selectedPeriodId' ).val();
	
    window.open( 'viewHistory.action?dataElementId=' + dataElementId + '&optionComboId=' + optionComboId
            + '&periodId=' + periodId + '&showComment=true', '_blank', 'width=580,height=710,scrollbars=yes' );
}

function closeCurrentSelection()
{
    $( '#currentSelection' ).fadeOut();
}
