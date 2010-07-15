/**
 * Resource Viewer
 */

function validateAddUpdateResourceViewer( mode )
{
	var name = $("#name" ).val(); 
	var sqlquery = $("#sqlquery").val(); 

	$.getJSON(
		"validateAddUpdateResourceViewer.action",
		{
			"name": name,
			"sqlquery": sqlquery,
			"mode": mode
		},
		function( json )
		{
			if ( json.response == "success" )
			{			
				if ( mode == "add" )
				{
					byId("addResourceViewerForm").submit();
					return;
				}
				byId("updateResourceViewerForm").submit();
			}
			else if ( json.response == "input" )
			{
				setMessage( json.message );
			}
		}
	);

}
 
function removeResourceViewerObject( viewId, viewName )
{
	removeItem( viewId, viewName, i18n_confirm_delete, 'removeResourceViewerObject.action' );
}

function showResourceViewerDetails( viewId )
{
    var request = new Request();
    request.setResponseTypeXML( 'resourceViewerObject' );
    request.setCallbackSuccess( resourceViewerDetailsReceived );
    request.send( 'getResourceViewerObject.action?id=' + viewId );
}

function resourceViewerDetailsReceived( viewElement )
{
    setFieldValue( 'nameField', getElementValue( viewElement, 'name' ) );
    
	var description = getElementValue( viewElement, 'description' );
    setFieldValue( 'descriptionField', description ? description : '[' + i18n_none + ']' );
    setFieldValue( 'sqlQueryField', getElementValue( viewElement, 'sqlquery' ) );
    
    showDetails();
}

/**
 * Execute query to create a new viewer
 * 
 * @param viewId the item identifier.
 */
function runResourceViewerQuery( viewId )
{
	$.getJSON(
		"executeResourceViewerQuery.action",
		{
			"id": viewId   
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				setMessage( json.message );
			}
			else if ( json.response == "error" )
			{
				setFieldValue( 'warningArea', json.message );
	
				showWarning();
			}
		}
	);
}

function selectALL( checkingStatus )
{
	var listRadio = document.getElementsByName('resourceTableCheckBox');
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		listRadio.item(i).checked = checkingStatus;
	}
}

function regenerateResourceTableAndViewerTables()
{
	var organisationUnit = byId( "organisationUnit" ).checked;
    var groupSet = byId( "groupSet" ).checked;
    var dataElementGroupSetStructure = byId( "dataElementGroupSetStructure" ).checked;
    var indicatorGroupSetStructure = byId( "indicatorGroupSetStructure" ).checked;
    var organisationUnitGroupSetStructure = byId( "organisationUnitGroupSetStructure" ).checked;
    var categoryStructure = byId( "categoryStructure" ).checked;
    var categoryOptionComboName = byId( "categoryOptionComboName" ).checked;
    
    if ( organisationUnit || groupSet || dataElementGroupSetStructure || indicatorGroupSetStructure || 
        organisationUnitGroupSetStructure || categoryStructure || categoryOptionComboName )
    {
        setWaitMessage( i18n_regenerating_resource_tables_and_viewers );
		
        var url = "dropAllResourceViewerTables.action";
        
        var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( regenerateResourceTableAndViewerTablesReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function regenerateResourceTableAndViewerTablesReceived( xmlObject )
{
	if ( xmlObject.getAttribute( 'type' ) == 'success' )
	{
		// Regenerating Resource tables
		generateResourceTableForViewers();
	}
	else
	{
		alert( i18n_regenerating_failed );
	}
}

function generateResourceTableForViewers()
{
    var organisationUnit = byId( "organisationUnit" ).checked;
    var groupSet = byId( "groupSet" ).checked;
    var dataElementGroupSetStructure = byId( "dataElementGroupSetStructure" ).checked;
    var indicatorGroupSetStructure = byId( "indicatorGroupSetStructure" ).checked;
    var organisationUnitGroupSetStructure = byId( "organisationUnitGroupSetStructure" ).checked;
    var categoryStructure = byId( "categoryStructure" ).checked;
    var categoryOptionComboName = byId( "categoryOptionComboName" ).checked;
    
    if ( organisationUnit || groupSet || dataElementGroupSetStructure || indicatorGroupSetStructure || 
        organisationUnitGroupSetStructure || categoryStructure || categoryOptionComboName )
    {
        setWaitMessage( i18n_generating_resource_tables );
            
        var params = "organisationUnit=" + organisationUnit + 
            "&groupSet=" + groupSet + 
            "&dataElementGroupSetStructure=" + dataElementGroupSetStructure +
            "&indicatorGroupSetStructure=" + indicatorGroupSetStructure +
            "&organisationUnitGroupSetStructure=" + organisationUnitGroupSetStructure +
            "&categoryStructure=" + categoryStructure +
            "&categoryOptionComboName=" + categoryOptionComboName;
            
        var url = "generateResourceTable.action";
        
        var request = new Request();
        request.sendAsPost( params );
        request.setCallbackSuccess( generateResourceTableForViewersReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function generateResourceTableForViewersReceived()
{
	generateAllResourceViewerTables();
}

function generateAllResourceViewerTables()
{
	$.getJSON(
		"regenerateAllResourceViewerTables.action",
		{
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				setMessage( json.message );
			}
			else if ( json.response == "error" )
			{
				setFieldValue( 'warningArea', json.message );
	
				showWarning();
			}
		}
	);

}