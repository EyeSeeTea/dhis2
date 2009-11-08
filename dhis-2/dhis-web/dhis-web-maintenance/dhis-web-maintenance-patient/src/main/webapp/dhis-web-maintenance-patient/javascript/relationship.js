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

function showAddRelationship( patientId )
{
	window.location = "showAddRelationshipForm.action?patientAId=" + patientId;
}

//-----------------------------------------------------------------------------
//Search Relationship Partner
//-----------------------------------------------------------------------------

function validateSearchPartner()
{	
	
	var url = 'validateSearch.action?' +
			'searchText=' + getFieldValue( 'searchText' );	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( searchValidationCompleted );    
	request.send( url );        

	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if ( type == 'success' )
	{
		var form = document.getElementById( 'relationshipSelectForm' );        
		form.submit();
	}
	else if ( type == 'error' )
	{
		window.alert( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		document.getElementById( 'message' ).innerHTML = message;
		document.getElementById( 'message' ).style.display = 'block';
	}
}

function addRelationship() 
{	
	var relationshipType = document.getElementById( 'relationshipTypeId' );
	var relationshipTypeId = relationshipType.options[relationshipType.selectedIndex].value;
	
	var partnerList = document.getElementById( 'availablePartnersList' );
	var partnerId = -1;
	
	if( partnerList.selectedIndex >= 0 )
	{		
		partnerId = partnerList.options[partnerList.selectedIndex].value;		
	}	
	
	if( relationshipTypeId == "null" || relationshipTypeId == "" )
	{
		window.alert( i18n_please_select_relationship_type );
		
		return;
	}
	
	if( partnerId == "null" || partnerId == "" || partnerId < 0 )
	{
		window.alert( i18n_please_select_partner );
		
		return;
	}			
	
	window.alert( 'the selected partner is:  ' + partnerId + ' and realtionship is:  ' + relationshipTypeId );
}

