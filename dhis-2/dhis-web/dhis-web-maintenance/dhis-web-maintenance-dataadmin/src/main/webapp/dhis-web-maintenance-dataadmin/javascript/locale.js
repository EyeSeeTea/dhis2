// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showLocaleDetails( i18nLocaleId )
{	
	jQuery.getJSON( "getLocale.action", {
		id:i18nLocaleId
	}, function(json){
		setInnerHTML( 'nameField', json.i18nLocale.name );
		setInnerHTML( 'descriptionField', json.i18nLocale.description );
		setInnerHTML( 'languageField', json.i18nLocale.language );
		setInnerHTML( 'countryField', json.i18nLocale.country );
		
		showDetails();
	});   
}

// -----------------------------------------------------------------------------
// Remove I18nLocale
// -----------------------------------------------------------------------------

function removeLocale( i18nLocaleId, name )
{
	removeItem( i18nLocaleId, name, i18n_confirm_delete, 'removeLocale.action' );
}

function localeOnChange()
{
	var type = getFieldValue('type');
	/*
	// anonymous
	if(type == "3")
	{
		disable('dateOfEnrollmentDescription');
		disable("displayIncidentDate");
		disable("dateOfIncidentDescription");
		disable("generatedByEnrollmentDate");
		disable("availablePropertyIds");
		disable('ignoreOverdueEvents');
		hideById("selectedList");
		jQuery("[name=displayed]").attr("disabled", true);
		jQuery("[name=displayed]").removeAttr("checked");
		
		jQuery("[name=nonAnonymous]").hide();
	}
	else{
		jQuery("[name=displayed]").prop("disabled", false);
		enable("availablePropertyIds");
		showById("selectedList");
		enable("generatedByEnrollmentDate");
		enable('dateOfEnrollmentDescription');
		enable("displayIncidentDate");
		enable('ignoreOverdueEvents');
		
		jQuery("[name=nonAnonymous]").show();
		if( type == 2 ){
			disable('ignoreOverdueEvents');
		}
		
		if(byId('displayIncidentDate').checked){
			enable("dateOfIncidentDescription");
		}
		else {
			disable("dateOfIncidentDescription");
		}
	}
	*/
}

// -----------------------------------------------------------------------------
// select identifiers / attributes
// -----------------------------------------------------------------------------

function selectProperties()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availablePropertyIds").children().each(function(i, item){
		if( item.selected ){
			html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectProperties( this )'><td onmousedown='select(event,this)'>" + item.text + "</td>";
			html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
			if( item.value.match("^attr_")=="attr_" )
			{
				html += " style='display:none' ";
			}
			html += "></td></tr>";
			selectedList.append( html );
			jQuery( item ).remove();
		}
	});
	
	if(getFieldValue('type') == "3")
	{
		jQuery("[name=displayed]").attr("disabled", true);
	}
}


function selectAllProperties()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availablePropertyIds").children().each(function(i, item){
		html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onmousedown='select(this)'>" + item.text + "</td>";
		html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
		if( item.value.match("^attr_")=="attr_" )
		{
			html += " style='display:none' ";
		}
		html += "'></td></tr>";
		selectedList.append( html );
		jQuery( item ).remove();
	});
}

function unSelectProperties()
{
	var availableList = jQuery("#availablePropertyIds");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{		
			availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
			item.remove();
		}
	});
}


function unSelectAllProperties()
{
	var availableList = jQuery("#availablePropertyIds");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
		item.remove();
	});
}

function select( event, element )
{
	if ( !getKeyCode( event ) )// Ctrl
	{
		jQuery("#selectedList .selected").removeClass( 'selected' );
	}
	
	element = jQuery( element ).parent();
	if( element.hasClass( 'selected') ) element.removeClass( 'selected' );
	else element.addClass( 'selected' );
}

function getKeyCode(e)
{
	var ctrlPressed=0;

	if (parseInt(navigator.appVersion)>3) {

		var evt = e ? e:window.event;

		if (document.layers && navigator.appName=="Netscape"
		&& parseInt(navigator.appVersion)==4) {
			// NETSCAPE 4 CODE
			var mString =(e.modifiers+32).toString(2).substring(3,6);
			ctrlPressed =(mString.charAt(1)=="1");
		}
		else {
			// NEWER BROWSERS [CROSS-PLATFORM]
			ctrlPressed=evt.ctrlKey;
		}
	}
	return ctrlPressed;
}

//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------

function moveUpPropertyList()
{
	var selectedList = jQuery("#selectedList");

	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{
			var prev = item.prev('#selectedList tr');
			if (prev.length == 1) 
			{ 
				prev.before(item);
			}
		}
	});
}

function moveDownPropertyList()
{
	var selectedList = jQuery("#selectedList");
	var items = new Array();
	jQuery("#selectedList").find("tr").each( function( i, item ){
		items.push(jQuery(item));
	});
	
	for( var i=items.length-1;i>=0;i--)
	{	
		var item = items[i];
		if( item.hasClass("selected") )
		{
			var next = item.next('#selectedList tr');
			if (next.length == 1) 
			{ 
				next.after(item);
			}
		}
	}
}