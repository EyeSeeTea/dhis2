function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {   	
        return false;
    }
    
    return true;
}

//-----------------------------------------------------------------------------
// Move members
//-----------------------------------------------------------------------------


var selectedList;
var availableList;

function move( listId ) {
	
	var fromList = document.getElementById(listId);
	
	if ( fromList.selectedIndex == -1 ) { return; }
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById( 'availableList' );
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById( 'selectedList' );
	}
	
	var toList = ( fromList == availableList ? selectedList : availableList );
	
	while ( fromList.selectedIndex > -1 ) {
		
		option = fromList.options.item(fromList.selectedIndex);
		fromList.remove(fromList.selectedIndex);
		toList.add(option, null);
	}
}

function submitForm() {
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById('availableList');
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById('selectedList');
	}
	
	selectAll( selectedList );
	
	return false;
}

function selectAll( list ) 
{
	for ( var i = 0, option; option = list.options.item(i); i++ ) 
	{
		option.selected = true;
	}
}

	
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramStageDetails( programStageId )
{
    var request = new Request();
    request.setResponseTypeXML( 'programStage' );
    request.setCallbackSuccess( programStageReceived );
    request.send( 'getProgramStage.action?id=' + programStageId );
}

function programStageReceived( programStageElement )
{
	setFieldValue( 'idField', getElementValue( programStageElement, 'id' ) );
	setFieldValue( 'nameField', getElementValue( programStageElement, 'name' ) );	
    setFieldValue( 'descriptionField', getElementValue( programStageElement, 'description' ) );
    setFieldValue( 'stageInProgramField', getElementValue( programStageElement, 'stageInProgram' ) );   
    setFieldValue( 'minDaysFromStartField', getElementValue( programStageElement, 'minDaysFromStart' ) );
    setFieldValue( 'maxDaysFromStartField', getElementValue( programStageElement, 'maxDaysFromStart' ) );
    setFieldValue( 'dataElementCountField', getElementValue( programStageElement, 'dataElementCount' ) );   
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove ProgramStage
// -----------------------------------------------------------------------------

function removeProgramStage( programStageId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeProgramStageCompleted );
        window.location.href = 'removeProgramStage.action?id=' + programStageId;
    }
}

function removeProgramStageCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'programStage.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add ProgramStage
// -----------------------------------------------------------------------------

function validateAddProgramStage()
{
	
	if( !isInt( getFieldValue( 'stageInProgram' ) ) )//|| !isInt( getFieldValue( 'minDaysFromStart' ) ) || !isInt( getFieldValue( 'maxDaysFromStart' ) ) )
	{
		window.alert( i18n_value_must_integer );
		
		return false;
	}	
	
	var url = 'validateProgramStage.action?' +
		'nameField=' + getFieldValue( 'nameField' ) +			
		'&description=' + getFieldValue( 'description' ) +	                
		'&stageInProgram=' + getFieldValue( 'stageInProgram' ) +
		'&minDaysFromStart=' + getFieldValue( 'minDaysFromStart' ) +	                
		'&maxDaysFromStart=' + getFieldValue( 'maxDaysFromStart' );

	var request = new Request();
		request.setResponseTypeXML( 'message' );
		request.setCallbackSuccess( addValidationCompleted );    
		request.send( url );        

	return false;
    
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addProgramStageForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_program_stage_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
// -----------------------------------------------------------------------------
// Update ProgramStage
// -----------------------------------------------------------------------------

function validateUpdateProgramStage()
{
	
    var url = 'validateProgramStage.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' ) +	                
	        '&stageInProgram=' + getFieldValue( 'stageInProgram' ) +
	        '&minDaysFromStart=' + getFieldValue( 'minDaysFromStart' ) +	                
	        '&maxDaysFromStart=' + getFieldValue( 'maxDaysFromStart' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    
    request.send( url );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updateProgramStageForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_updating_program_stage_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}