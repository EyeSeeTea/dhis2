
function organisationUnitSelected( orgUnits )
{	
    window.location.href = 'householdform.action';
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showHouseHoldDetails( houseHoldId )
{
    var request = new Request();
    request.setResponseTypeXML( 'houseHold' );
    request.setCallbackSuccess( houseHoldReceived );
    request.send( 'getHouseHold.action?id=' + houseHoldId );
}

function houseHoldReceived( houseHoldElement )
{
	setFieldValue( 'idField', getElementValue( houseHoldElement, 'id' ) );
	setFieldValue( 'houseNumberField', getElementValue( houseHoldElement, 'houseNumber' ) );
	setFieldValue( 'registeringUnitField', getElementValue( houseHoldElement, 'registeringUnit' ) );
    setFieldValue( 'landMarkField', getElementValue( houseHoldElement, 'landMark' ) );
    setFieldValue( 'addressField', getElementValue( houseHoldElement, 'address' ) );
    setFieldValue( 'memberCountField', getElementValue( houseHoldElement, 'memberCount' ) );      
   
    showDetails();
}	

// -----------------------------------------------------------------------------
// Remove houseHold
// -----------------------------------------------------------------------------

function removeHouseHold( houseHoldId, houseNumber )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + houseNumber );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeHouseHoldCompleted );
        window.location.href = 'removeHouseHold.action?id=' + houseHoldId;
    }
}

function removeHouseHoldCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'household.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add HouseHold
// -----------------------------------------------------------------------------

function validateAddHouseHold()
{
	
	var url = 'validateHouseHold.action?' +
			'houseNumber=' + getFieldValue( 'houseNumber' ) +
			//'&registeringUnit=' + getFieldValue( 'registeringUnit' ) +
	        '&landMark=' + getFieldValue( 'landMark' ) +	                
	        '&address=' + getFieldValue( 'address' );
	
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
        var form = document.getElementById( 'addHouseHoldForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_house_hold_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

//-----------------------------------------------------------------------------
// Search HouseHold Members
//-----------------------------------------------------------------------------

function searchHouseHoldMembers()
{
	var url = "searchHouseHoldMembers.action?searchText=" + getFieldValue( 'searchText' );
	
	var request = new Request();
	request.setResponseTypeXML( 'member' );
    request.setCallbackSuccess( getMembersReceived );
    request.send( url );		
}

function getMembersReceived( xmlObject )
{	
	var availableList = document.getElementById( "availableList" );
		
	clearList( availableList );
	
	var members = xmlObject.getElementsByTagName( "member" );
	
	for ( var i = 0; i < members.length; i++ )
	{
		var id 		   = members[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var givenName  = members[ i ].getElementsByTagName( "firstName" )[0].firstChild.nodeValue ;
		var middleName = members[ i ].getElementsByTagName( "middleName" )[0].firstChild.nodeValue ;
		var familyName = members[ i ].getElementsByTagName( "lastName" )[0].firstChild.nodeValue ;
		
		var option = document.createElement( "option" );
		
		option.value = id;
		option.text = givenName + ' ' + middleName + ' ' + familyName;
		option.title = givenName + ' ' + middleName + ' ' + familyName;
		
		availableList.add( option, null );		
	}
	
	if( members.length == 0 )
	{
		var option = document.createElement( "option" );
		
		option.value = "";
		option.text = i18n_empty_search_result;
		option.title = i18n_empty_search_result;
		option.disabled = "disabled";
		
		availableList.add( option, null );
	}
}	

// -----------------------------------------------------------------------------
// Update HouseHold
// -----------------------------------------------------------------------------

function validateUpdateHouseHold()
{
	
    var url = 'validateHouseHold.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&houseNumber=' + getFieldValue( 'houseNumber' ) +	        
	        '&landMark=' + getFieldValue( 'landMark' ) +	        	        
	        '&address=' + getFieldValue( 'address' ) ;
	
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
    	var form = document.getElementById( 'updateHouseHoldForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_house_hold_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function refreshParentWindow()
{
	
	var form = document.getElementById( 'updateHouseHoldMemberForm' );        
    form.submit();
    
	window.close();
	
	if ( window.opener && !window.opener.closed ) 
	{
		window.opener.location.reload();
	} 
	
}