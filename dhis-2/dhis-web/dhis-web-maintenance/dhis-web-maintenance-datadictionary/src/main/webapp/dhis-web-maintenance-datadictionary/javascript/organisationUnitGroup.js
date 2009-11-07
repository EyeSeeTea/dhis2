
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitGroupDetails( unitId )
{
    var request = new Request();
    request.setResponseTypeXML( 'organisationUnitGroup' );
    request.setCallbackSuccess( organisationUnitGroupReceived );
    request.send( 'getOrganisationUnitGroup.action?id=' + unitId );
}

function organisationUnitGroupReceived( unitGroupElement )
{
    setFieldValue( 'nameField', getElementValue( unitGroupElement, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( unitGroupElement, 'memberCount' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit group
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroup( unitGroupId, unitGroupName )
{
    var result = window.confirm( confirm_to_delete_org_unit_group + '\n\n' + unitGroupName );
    
    if ( result )
    {
        window.location.href = 'removeOrganisationUnitGroup.action?id=' + unitGroupId;
    }
}

// -----------------------------------------------------------------------------
// Add organisation unit group
// -----------------------------------------------------------------------------

function validateAddOrganisationUnitGroup()
{	
	$.post("validateOrganisationUnitGroup.action",{		
		name:$("#name").val()
	},function( message ){
		var messageElement = message.getElementsByTagName('message')[0];
		var type = messageElement.getAttribute( 'type' );
		var message = messageElement.firstChild.nodeValue;

		if ( type == 'success' )
		{
			document.forms['addOrganisationUnitGroupForm'].submit();
		}
		else if ( type == 'error' )
		{
			setMessage(message);
		}
		else if ( type == 'input' )
		{
			setMessage(message);
		}
	},'xml');   
}

// -----------------------------------------------------------------------------
// Update organisation unit group
// -----------------------------------------------------------------------------

function validateUpdateOrganisationUnitGroup()
{	
	
	$.post("validateOrganisationUnitGroup.action",{
		id:$("#id").val(),
		name:$("#name").val()
	},function( message ){
		var messageElement = message.getElementsByTagName('message')[0];
		var type = messageElement.getAttribute( 'type' );
		var message = messageElement.firstChild.nodeValue;

		if ( type == 'success' )
		{
			document.forms['updateOrganisationUnitGroupForm'].submit();
		}
		else if ( type == 'error' )
		{
			setMessage(message);
		}
		else if ( type == 'input' )
		{
			setMessage(message);
		}
	},'xml');   
}
