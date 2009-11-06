// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRelationshipTypeDetails( relationshipTypeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'relationshipType' );
    request.setCallbackSuccess( relationshipTypeReceived );
    request.send( 'getRelationshipTYpe.action?id=' + relationshipTypeId );
}

function relationshipTypeReceived( relationshipTypeElement )
{
	setFieldValue( 'idField', getElementValue( relationshipTypeElement, 'id' ) );
	setFieldValue( 'aIsToBField', getElementValue( relationshipTypeElement, 'aIsToB' ) );	
	setFieldValue( 'bIsToAField', getElementValue( relationshipTypeElement, 'bIsToA' ) );       
	setFieldValue( 'descriptionField', getElementValue( programElement, 'description' ) );
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove RelationshipType
// -----------------------------------------------------------------------------		 
function removeRelationshipType( relationshipTypeId, aIsToB, bIsToA )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + aIsToB + ' ' + bIsToA );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeRelationshipTypeCompleted );
        window.location.href = 'removeRelationshipType.action?id=' + relationshipTypeId;
    }
}

function removeRelationshipTypeCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'relationshipType.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add RelationshipType
// -----------------------------------------------------------------------------

function validateAddRelationshipType()
{
	
	var url = 'validateRelationshipType.action?' +
			'aIsToB=' + getFieldValue( 'aIsToB' ) +			
	        '&bIsToA=' + getFieldValue( 'bIsToA' ) +
	        '&description=' + getFieldValue( 'description' );
	
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
        var form = document.getElementById( 'addRelationshipTypeForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_atttibute_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
// -----------------------------------------------------------------------------
// Update RelationshipType
// -----------------------------------------------------------------------------

function validateUpdateRelationshipType()
{
	
    var url = 'validateRelationshipType.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&aIsToB=' + getFieldValue( 'aIsToB' ) +			
	        '&bIsToA=' + getFieldValue( 'bIsToA' ) +
	        '&description=' + getFieldValue( 'description' );
	
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
    	var form = document.getElementById( 'updateRelationshipTypeForm' );        
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

//------------------------------------------------------------------------------
//Add Relationship
//------------------------------------------------------------------------------

function addRelationship( patientId )
{
	window.location = "showAddRelationshipForm.action?id=" + patientId;
}