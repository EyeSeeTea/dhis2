// -----------------------------------------------------------------------------
// Search users
// -----------------------------------------------------------------------------
function searchUserName()
{
	var params = 'key=' + getFieldValue( 'key' );
    
    var url = 'searchUser.action?' + params;
    
    if( getFieldValue( 'key' ) != null && getFieldValue( 'key' ) != '' ) 
    {
		$( '#content' ).load( url, null, unLockScreen );
    	lockScreen();
    }
    else 
    {
    	window.location.href='alluser.action?' + params;
    }
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUserDetails( userId )
{
    var request = new Request();
    request.setResponseTypeXML( 'user' );
    request.setCallbackSuccess( userReceived );
    request.send( 'getUser.action?id=' + userId );
}

function userReceived( userElement )
{
    setInnerHTML( 'usernameField', getElementValue( userElement, 'username' ) );
    setInnerHTML( 'surnameField', getElementValue( userElement, 'surname' ) );
    setInnerHTML( 'firstNameField', getElementValue( userElement, 'firstName' ) );

    var email = getElementValue( userElement, 'email' );
    setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

    var phoneNumber = getElementValue( userElement, 'phoneNumber' );
	setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );

	var numberOrgunit = getElementValue( userElement, 'numberOrgunit' );
	setInnerHTML( 'numberOrgunitField', numberOrgunit ? numberOrgunit : '[' + i18n_none + ']' );
	
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( userId, username )
{
	removeItem( userId, username, i18n_confirm_delete, 'removeUser.action' );
}