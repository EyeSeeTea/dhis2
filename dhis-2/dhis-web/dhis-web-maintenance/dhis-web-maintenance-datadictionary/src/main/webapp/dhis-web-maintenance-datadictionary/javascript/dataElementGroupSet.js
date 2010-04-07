// -----------------------------------------------------------------------------
// Validate Update Data Element Group
// -----------------------------------------------------------------------------

function validateUpdateDataElementGroupSet(){

    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateUpdateDataElementGroupSetCompleted );
    request.sendAsPost( "id=" + getFieldValue("id") + "&name=" +  getFieldValue("name"));
    request.send( "validateDataElementGroupSet.action");
	
}

function validateUpdateDataElementGroupSetCompleted( message ){
    var type = message.getAttribute("type");
    if(type=="success"){
        selectAllById("groupMembers");
        document.forms['updateDataElementGroupSet'].submit();
    }else{
        setMessage(message.firstChild.nodeValue);
    }
}

// -----------------------------------------------------------------------------
// Validate Add Data Element Group
// -----------------------------------------------------------------------------

function validateAddDataElementGroupSet()
{	
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateAddDataElementGroupSetCompleted );    
    request.sendAsPost( "name=" +  getFieldValue( "name" ) );
    request.send( "validateDataElementGroupSet.action");
}

function validateAddDataElementGroupSetCompleted( message )
{
    var type = message.getAttribute("type");
	
    if( type == "success" )
    {
        selectAllById( "groupMembers" );
        document.forms[ 'addDataElementGroupSet' ].submit();
    }
    else
    {
        setMessage(message.firstChild.nodeValue);
    }
}

// -----------------------------------------------------------------------------
// Delete Data Element Group
// -----------------------------------------------------------------------------

function deleteDataElementGroupSet( groupSetId, groupSetName ){
	
    removeItem( groupSetId, groupSetName, i18n_confirm_delete, "deleteDataElementGroupSet.action" );
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showDataElementGroupSetDetails( id ){

    var request = new Request();
    request.setResponseTypeXML( 'dataElementGroupSet' );
    request.setCallbackSuccess( showDetailsCompleted );
    request.send( "showDataElementGroupSetDetails.action?id=" + id);	
}


function showDetailsCompleted( dataElementGroupSet ){

    setFieldValue( 'nameField', getElementValue( dataElementGroupSet, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( dataElementGroupSet, 'memberCount' ) );

    showDetails();
}
