﻿/**
 * Sql View
 */

function validateAddUpdateSqlView( mode )
{
	var name = $("#name").val(); 
	var sqlquery = $("#sqlquery").val(); 

	$.getJSON(
		"validateAddUpdateSqlView.action",
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
					byId("addSqlViewForm").submit();
					return;
				}
				byId("updateSqlViewForm").submit();
			}
			else if ( json.response == "input" )
			{
				setMessage( json.message );
			}
		}
	);
}
 
function removeSqlViewObject( viewId, viewName )
{
	removeItem( viewId, viewName, i18n_confirm_delete, 'removeSqlViewObject.action' );
}

function showSqlViewDetails( viewId )
{
    var request = new Request();
    request.setResponseTypeXML( 'sqlViewObject' );
    request.setCallbackSuccess( sqlViewDetailsReceived );
    request.send( 'getSqlViewObject.action?id=' + viewId );
}

function sqlViewDetailsReceived( viewElement )
{
    setFieldValue( 'nameField', getElementValue( viewElement, 'name' ) );
    
	var description = getElementValue( viewElement, 'description' );
    setFieldValue( 'descriptionField', description ? description : '[' + i18n_none + ']' );
    setFieldValue( 'sqlQueryField', getElementValue( viewElement, 'sqlquery' ) );
    
    showDetails();
}

/**
 * Execute query to create a new view table
 * 
 * @param viewId the item identifier.
 */
function runSqlViewQuery( viewId )
{
	$.getJSON(
		"executeSqlViewQuery.action",
		{
			"id": viewId   
		},
		function( json )
		{
			setHeaderDelayMessage( json.message );
		}
	);
}

function selectOrUnselectALL()
{
	var listRadio = document.getElementsByName('resourceTableCheckBox');
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		listRadio.item(i).checked = checkingStatus;
	}
	
	// If true, its means the items unselected yet
	if ( checkingStatus )
	{
		$("#selectAllButton").val( i18n_unselect_all );
	}
	else
	{
		$("#selectAllButton").val( i18n_select_all );
	}
	checkingStatus = !checkingStatus;
}

// -----------------------------------------------------------------------
// Re-generating for the resource tables and the view ones
// -----------------------------------------------------------------------

function regenerateResourceTableAndViewTables()
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
        setWaitMessage( i18n_regenerating_resource_tables_and_views );
		
        var url = "dropAllSqlViewTables.action";
        
        var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( regenerateResourceTableAndViewTablesReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function regenerateResourceTableAndViewTablesReceived( xmlObject )
{
	if ( xmlObject.getAttribute( 'type' ) == 'success' )
	{
		generateResourceTableForViews();
	}
	else
	{
		alert( i18n_regenerating_failed );
	}
}

function generateResourceTableForViews()
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
        request.setCallbackSuccess( generateResourceTableForViewsReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function generateResourceTableForViewsReceived()
{
	generateAllSqlViewTables();
}

function generateAllSqlViewTables()
{
	$.getJSON(
		"regenerateAllSqlViewTables.action",
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
				setHeaderDelayMessage( json.message );
				
				hideMessage();
			}
		}
	);
}

// -----------------------------------------------------------------------
// View data from the specified view table
// -----------------------------------------------------------------------

function showDataSqlViewForm( viewId )
{
	$.getJSON(
		"checkViewTableExistence.action",
		{
			"id": viewId
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				window.location.href = "showDataSqlViewForm.action?viewTableName=" + json.message;
			}
			else if ( json.response == "error" )
			{
				alert( json.message );
			}
		}
	);
}
