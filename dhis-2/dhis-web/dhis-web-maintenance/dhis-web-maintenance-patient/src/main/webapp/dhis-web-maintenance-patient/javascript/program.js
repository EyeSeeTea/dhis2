
function organisationUnitSelected( orgUnits )
{	
    window.location.href = 'prorgam.action';
}

selection.setListenerFunction( organisationUnitSelected );

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

function moveUp( listId ) 
{

	  var withInList = document.getElementById( listId ); 
	  
	  var index = withInList.selectedIndex;
	  
	  if ( index == -1 ) { return; } 
	  
	  if( index - 1 < 0 ) { return; }//window.alert( 'Item cant be moved up');        
	  
	  var option = new Option( withInList.options[index].text, withInList.options[index].value); 
	  var temp = new Option( withInList.options[index-1].text, withInList.options[index-1].value);
	  
	  withInList.options[index-1] = option;
	  withInList.options[index-1].selected = true;
	  withInList.options[index] = temp;

}

function moveDown( listId ) 
{

	  var withInList = document.getElementById( listId ); 
	  
	  var index = withInList.selectedIndex;
	  
	  if ( index == -1 ) { return; } 
	  
	  if( index + 1 == withInList.options.length ) { return; }//window.alert( 'Item cant be moved down');   
	    
	  var option = new Option( withInList.options[index].text, withInList.options[index].value); 
	  var temp = new Option( withInList.options[index+1].text, withInList.options[index+1].value);
	  
	  withInList.options[index+1] = option;
	  withInList.options[index+1].selected = true;
	  withInList.options[index] = temp;
	  
}

	
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramDetails( programId )
{
    var request = new Request();
    request.setResponseTypeXML( 'program' );
    request.setCallbackSuccess( programReceived );
    request.send( 'getProgram.action?id=' + programId );
}

function programReceived( programElement )
{
	setFieldValue( 'nameField', getElementValue( programElement, 'name' ) );	
    setFieldValue( 'descriptionField', getElementValue( programElement, 'description' ) );
    setFieldValue( 'numberOfDaysField', getElementValue( programElement, 'numberOfDays' ) );      
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Program
// -----------------------------------------------------------------------------

function removeProgram( programId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeProgramCompleted );
        window.location.href = 'removeProgram.action?id=' + programId;
    }
}

function removeProgramCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'program.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add Program
// -----------------------------------------------------------------------------

function validateAddProgram()
{
	
	var url = 'validateProgram.action?' +
			'nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' ) +	                
	        '&numberOfDays=' + getFieldValue( 'numberOfDays' );
	
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
        var form = document.getElementById( 'addProgramForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_program_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
// -----------------------------------------------------------------------------
// Update Program
// -----------------------------------------------------------------------------

function validateUpdateProgram()
{
	
    var url = 'validateProgram.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' ) +	                
	        '&numberOfDays=' + getFieldValue( 'numberOfDays' );
	
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
    	var form = document.getElementById( 'updateProgramForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_program_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}