/**
 * Sql View
 */

function validateAddUpdateSqlView( mode )
{
	var name = $("#name" ).val(); 
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

// -------------------------------------------------------------------------------------
// Design query
// -------------------------------------------------------------------------------------

function showOrHideDesignQueryDiv()
{
	// if true its means div is showing
	if ( !advanceStatus )
	{
		$("#mainDesignQueryDiv").show("fast");
		$("#advance_button").val( i18n_hide_advance );
	}
	else
	{
		$("#mainDesignQueryDiv").hide("fast");
		$("#advance_button").val( i18n_show_advance );
	}
	advanceStatus = !advanceStatus;
}

function setUpQuery()
{
	var selectQuery = "SELECT ";
	var fromQuery = "FROM ";
	var whereQuery = "WHERE ";
	var sortQuery = "ORDER BY ";
	var havingbyQuery = "HAVING ";
	var groupbyQuery = "GROUP BY ";
	
	var table = "";
	var field = "";
	var alias = "";
	var sorttype = "";
	
	var shows = document.getElementsByName("show-checkbox");
	
	for (var i = 1; i <= shows.length; i++)
	{
		table = $("#resource-table-combo"+i).val().trim();
		field = checkFieldValid( table, $("#resource-property-combo"+i).val().trim() );
		alias = $("#alias-property-text"+i).val().trim();
		criteriaAND = $("#criteria_and_property"+i).val().trim();
		criteriaOR = $("#criteria_or_property"+i).val().trim();
		sorttype = $("#sort-property-combo"+i).val().trim();
		groupby = $("#groupby-property-checkbox"+i).is(':checked');
		
		/**
		 * SELECT keyword
		 */
		if ( shows[i-1].checked )
		{
			if ( selectQuery != "SELECT " )
			{
				selectQuery += ", ";
			}
			selectQuery += field + ((alias != "") == true ? " AS " + alias : alias);
		}
		
		if ( fromQuery != "FROM " )
		{
			fromQuery += ", ";
		}
		fromQuery += table;
		
		/**
		 * WHERE and/or HAVING keyword
		 */
		if ( criteriaAND != "" )
		{
			if ( regexCountOther.test(field) || regexSumOther.test(field) || regexMinOther.test(field) || regexMaxOther.test(field) || regexAvgOther.test(field) || regexAverageOther.test(field) )
			{
				if ( havingbyQuery != "HAVING " )
				{
					havingbyQuery += " AND ";
				}
				havingbyQuery += "("+ field + " " + criteriaAND +")" ;
			}
			else
			{
				if ( whereQuery != "WHERE " )
				{
					whereQuery += " AND ";
				}
				whereQuery += "("+ field + " " + criteriaAND +")" ;
			}
		}
		if ( criteriaOR != "" )
		{
			if ( regexCountOther.test(field) || regexSumOther.test(field) || regexMinOther.test(field) || regexMaxOther.test(field) || regexAvgOther.test(field) || regexAverageOther.test(field) )
			{
				if ( havingbyQuery != "HAVING " )
				{
					havingbyQuery += " OR ";
				}
				havingbyQuery += "("+ field + " " + criteriaOR +")" ;
			}
			else
			{
				if ( whereQuery != "WHERE " )
				{
					whereQuery += " OR ";
				}
				whereQuery += "("+ field + " " + criteriaOR +")" ;
			}
		}
		
		/**
		 * ORDER BY keyword
		 */
		if ( (sortQuery == "ORDER BY ") && ((sorttype == "ASCENDING") || (sorttype == "DESCENDING")) )
		{
			sortQuery += field + " " + ((sorttype == "ASCENDING") == true ? "ASC" : "DESC");
		}
		
		/**
		 * GROUP BY keyword
		 */
		if ( groupby )
		{
			if ( groupbyQuery != "GROUP BY " )
			{
				groupbyQuery += ", ";
			}
			groupbyQuery += "(" + field + ")";
		}
	}
	
	var curValue = $("#sqlquery").val().trim();
	
	var result = selectQuery + "\n" + fromQuery + "\n";
	result += (whereQuery == "WHERE ") == true ? "" : whereQuery + "\n";
	result += (sortQuery == "ORDER BY ") == true ? "" : sortQuery + "\n";
	result += (havingbyQuery == "HAVING ") == true ? "" : havingbyQuery + "\n";
	result += (groupbyQuery == "GROUP BY ") == true ? "" : groupbyQuery + "\n";
	
	if ( curValue != "SELECT" )
	{
		$("#sqlquery").html (curValue + "; \n\n" + result);
	}
	else
	{
		$("#sqlquery").html (result);
	}
}

function checkFieldValid( tableName, fieldValue )
{
	if ( regexCountStar.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexCountStar, "COUNT(*)") );
	}
	else if ( regexCountOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexCountOther, "COUNT(") );
	}
	
	if ( regexSumOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexSumOther, "SUM(") );
	}
	else if ( regexMinOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexMinOther, "MIN(") );
	}
	else if ( regexMaxOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexMaxOther, "MAX(") );
	}
	else if ( regexAvgOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexAvgOther, "AVG(") );
	}
	else if (  regexAverageOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexAverageOther, "AVG(") );
	}

	return tableName + "." + fieldValue;
}

function makeUpField( fieldValue )
{
	return fieldValue.replace(/\s+/g, " ").replace(/\s*\)/g, ")" );
}

function applyAutocompleteSupporting(data, fieldElementId, customWidth)
{
	jQuery("#"+fieldElementId).autocomplete(data, {
		multiple: true,
		//autoFill: true,
		width: customWidth
	});
}

function generateQueryColumn( mainRowId )
{
	columnNo ++;
	curAppendedColumnId = "appendedColumn"+columnNo;
	
	htmlString = "<td id=\""+curAppendedColumnId+"\"><table>";
	htmlString += "<tr><td align=\"right\">";
	htmlString += "<a href=\"javascript:closeAppendedField('"+curAppendedColumnId+"')\" title='"+i18n_close+"'>";
	htmlString += "<img src=\"../images/close.png\" alt='"+i18n_close+"'></a></td></tr>";	
	htmlString += "<tr><td><input id=\"resource-table-combo"+columnNo+"\"/></td></tr>";
	htmlString += "<tr><td><input id=\"resource-property-combo"+columnNo+"\"/></td></tr>";
	htmlString += "<tr><td><input id=\"alias-property-text"+columnNo+"\"/></td></tr>";
	htmlString += "<tr><td align=\"center\"><input type=\"checkbox\" name=\"show-checkbox\"/></td></tr>";
	htmlString += "<tr><td><input id=\"sort-property-combo"+columnNo+"\"/></td></tr>";
	htmlString += "<tr><td><input type=\"text\" id=\"criteria_and_property"+columnNo+"\"/></td></tr>";
	htmlString += "<tr><td><input type=\"text\" id=\"criteria_or_property"+columnNo+"\"/></td></tr>";
	htmlString += "<tr><td align=\"center\"><input type=\"checkbox\" id=\"groupby-property-checkbox"+columnNo+"\"/></td></tr>";
	htmlString += "</table></td>";
	
	$("#"+mainRowId).append(htmlString);
	
	createTableComboboxExt("resource-table-combo"+columnNo, "resource-property-combo"+columnNo, "alias-property-text"+columnNo, "sort-property-combo"+columnNo, "groupby-property-checkbox"+columnNo);
	applyAutocompleteSupporting(basic_operators, "resource-property-combo"+columnNo, $("#resource-property-combo"+columnNo).width() );
	applyAutocompleteSupporting(criteria_operators, "criteria_and_property"+columnNo, $("#criteria_and_property"+columnNo).width()+50 );
	applyAutocompleteSupporting(criteria_operators, "criteria_or_property"+columnNo, $("#criteria_or_property"+columnNo).width()+50 );

}

function removeAllQueryColumn()
{
	if ( columnNo == 1 )
	{
		alert( i18n_cannot_remove_any_more );
	}
	else
	{
		while ( columnNo > 1 )
		{
			if ( byId(curAppendedColumnId) != null )
			{
				$("#"+curAppendedColumnId).remove();
			}
			columnNo --;
			curAppendedColumnId = "appendedColumn"+columnNo;
		}
	}
}

function closeAppendedField( appendedColumnId )
{
	$("#"+appendedColumnId).remove();
}