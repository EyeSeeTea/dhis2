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
// Add Relationship
//------------------------------------------------------------------------------

function showAddRelationship()
{
	window.location = "showAddRelationshipForm.action";
}

//-----------------------------------------------------------------------------
// Search Relationship Partner
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
	
	var url = 'saveRelationship.action?' + 
		'patientBId=' + partnerId + 
		'&relationshipTypeId=' + relationshipTypeId ;
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( saveRelationshipCompleted );    
	request.send( url );
	
	return false;
	
}

function saveRelationshipCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if ( type == 'success' )
	{
		window.location = "getRelationshipList.action";
	}	
	else if ( type == 'error' )
	{
		window.alert( i18n_adding_relationship_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		document.getElementById( 'message' ).innerHTML = message;
		document.getElementById( 'message' ).style.display = 'block';
	}
}

//------------------------------------------------------------------------------
// Remove Relationship
//------------------------------------------------------------------------------

function removeRelationship( relationshipId, patientA, aIsToB, patientB )
{	
	
    var result = window.confirm( i18n_confirm_delete_relationship + '\n\n' + patientA + ' is ' + aIsToB + ' to ' + patientB );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeRelationshipCompleted );
        request.send( 'removeRelationship.action?relationshipId=' + relationshipId );         
    }
}

function removeRelationshipCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	window.location = "getRelationshipList.action";
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

//------------------------------------------------------------------------------
// Save Representative Relationship
//------------------------------------------------------------------------------
function saveRepresentativeRelationship()
{	
	var possibleRepresentative = document.relationshipList.representative;
	
	var representativeId;

	for( i=0; i < possibleRepresentative.length; i++)
	{
		if( possibleRepresentative[i].checked == true )
		{
			representativeId = possibleRepresentative[i].value			
			break;
		}
	}
	
	if( representativeId == null )
	{
		window.alert( i18n_please_select_a_representative );
		
		return;
	}
	
	else
	{
		window.alert( ' the representing relationship is:  ' + representativeId );
	}
	
}
