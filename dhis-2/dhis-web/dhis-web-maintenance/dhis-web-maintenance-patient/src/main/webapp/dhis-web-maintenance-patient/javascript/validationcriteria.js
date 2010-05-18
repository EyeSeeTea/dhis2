//-----------------------------------------------------------------------------
// Add Criteria
//-----------------------------------------------------------------------------

function validateCriteria()
{
	var params = 'name=' + getFieldValue( 'name' );
	if(byId('id') != null)
	{
		params += '&id=' + getFieldValue( 'id' );
	}
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validationCompleted ); 
	request.sendAsPost( params );
	request.send( "validateValidationCriteria.action");
}

function validationCompleted( xmlObject )
{
	 var type = xmlObject.getAttribute( 'type' );
	 var message = xmlObject.firstChild.nodeValue;
	 
	 if ( type == 'input' )
	 {
	     setMessage(message);
		 return;
	 }
	 document.forms['validationCriteriaForm'].submit();

}

// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeCriteria( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeValidationCriteria.action' );
}
