function showToolTip( e, value){
	
	var tooltipDiv = byId('tooltip');
	tooltipDiv.style.display = 'block';
	
	var posx = 0;
    var posy = 0;
	
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX;
        posy = e.clientY;
    }
	
	tooltipDiv.style.left= posx  + 8 + 'px';
	tooltipDiv.style.top = posy  + 8 + 'px';
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +   value;
}

function hideToolTip(){
	byId('tooltip').style.display = 'none';
}

function initList() 
{
	var list = byId('availableGroups');
	var id;

	for (id in availableGroups) {		
		var option = new Option( availableGroups[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        list.add( option, null );  
		
	}

	list = byId('availableIndicators');

	for (id in availableIndicators) {		
		var option = new Option( availableIndicators[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        list.add( option, null );  
	}
	
}

/*==============================================================================
 *Move selected indicator 
 *==============================================================================*/

function addSelectedGroups()
{
    var list = byId( 'availableGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        assignedGroups[id] = availableGroups[id];

    }

    filterAssignedGroups();   
}

function removeSelectedGroups()
{
    var list = byId( 'assignedGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;      

		availableGroups[id] = assignedGroups[id];		

        delete assignedGroups[id];
    }

    filterAssignedGroups();    
}

/*==============================================================================
 * Get Indicator Groups contain indicator
 *==============================================================================*/

function getAssignedIndicatorGroups( indicatorId )
{
	var request = new Request();
    request.setResponseTypeXML( 'indicatorGroups' );
    request.setCallbackSuccess( getAssignedIndicatorGroupsCompleted );
    request.send( 'getAssignedIndicatorGroups.action?indicatorId=' + indicatorId );    
}

function getAssignedIndicatorGroupsCompleted( indicatorGroups )
{
	assignedGroups = new Object();
	
	var availableIndicatorGroups = indicatorGroups.getElementsByTagName( 'indicatorGroup' );
	
	for( var i=0;i<availableIndicatorGroups.length;i++)
	{
		var id = availableIndicatorGroups.item(i).getElementsByTagName( 'id' )[0].firstChild.nodeValue;
		var name = availableIndicatorGroups.item(i).getElementsByTagName( 'name' )[0].firstChild.nodeValue;
		assignedGroups[id] = name;		
	}
	var list = byId('availableIndicators');
	byId( 'groupNameView' ).innerHTML = list[list.selectedIndex].text;
	
	filterAssignedGroups();
}

/*==============================================================================
 *	Filter Select List
 *==============================================================================*/

function filterAssignedGroups()
{
    var filter = byId( 'assignedGroupsFilter' ).value;
    var list = byId( 'assignedGroups' );

    list.options.length = 0;

    for ( var id in assignedGroups )
    {
        var value = assignedGroups[id];

        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
			var option = new Option( value, id );
			option.onmousemove  = function(e){
				showToolTip( e, value);				
			}
            list.add( option, null );			

        }
    }
}
function filterAvailableGroups()
{
    var filter = byId( 'availableGroupsFilter' ).value;
    var list = byId( 'availableGroups' );
    
    list.options.length = 0;
    
    for ( var id in availableGroups )
    {
        var value = availableGroups[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            var option = new Option( value, id );
			option.onmousemove  = function(e){
				showToolTip( e, this.text);
			}
	        list.add( option, null );  
        }
    }
}

function filterAvailableIndicators()
{
    var filter = byId( 'availableIndicatorsFilter' ).value;
    var list = byId( 'availableIndicators' );
    
    list.options.length = 0;
    
    for ( var id in availableIndicators )
    {
        var value = availableIndicators[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            var option = new Option( value, id );
			option.onmousemove  = function(e){
				showToolTip( e, this.text);
			}
	        list.add( option, null );  
        }
    }
}

/*==============================================================================
 *  New  Indicator Group
 *==============================================================================*/

function showAddIndicatorGroupForm()
{
	byId( 'groupName' ).value='';    
    byId( 'addRenameGroupButton' ).onclick=validateAddIndicatorGroup;
    showPopupWindowById( 'addIndicatorGroupForm', 450, 70 );
}

function validateAddIndicatorGroup()
{
	$.postJSON(
		"validateIndicatorGroup.action",
		{
			"name": getFieldValue( 'groupName' )
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				createNewGroup();
			}
			else
			{
				alert(json.message);
			}
		}
	);
}

function createNewGroup()
{
	var name = byId( 'groupName' ).value;    
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( createNewGroupReceived );
	request.sendAsPost( "name=" + name + "&mode=editor" );
    request.send( 'addIndicatorGroupEditor.action' );    
}

function createNewGroupReceived( xmlObject )
{       
    var id = xmlObject.getElementsByTagName( "id" )[0].firstChild.nodeValue;
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    var list = byId( 'availableGroups' );
    var option = new Option( name, id );
	option.selected = true;
	option.onmousemove  = function(e){
		showToolTip( e, this.text);
	}
	list.add(option , null );
	byId( 'groupNameView' ).innerHTML = name;
    hideById( 'addIndicatorGroupForm' );
    unLockScreen();  
}

/*==============================================================================
 * Update Indicator Group
 *==============================================================================*/

function showRenameIndicatorGroupForm()
{
	var list = byId('availableGroups');
	
	if( list.value== '' )
	{
		setHeaderDelayMessage(i18n_select_indicator_group);
	}
	else
	{
		byId( 'groupName' ).value = list.options[ list.selectedIndex ].text
		byId( 'addRenameGroupButton' ).onclick=validateRenameIndicatorGroup;
		showPopupWindowById( 'addIndicatorGroupForm', 450, 70 );
	}	
} 

function validateRenameIndicatorGroup()
{
	$.postJSON(
		"validateIndicatorGroup.action",
		{
			"name": getFieldValue( 'groupName' )
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				renameGroup();
			}
			else
			{
				alert(json.message);
			}
		}
	);	

}

function renameGroup()
{
	var name = byId( 'groupName' ).value;    
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( createNewGroupReceived );
	var params = "name=" + name +  "&mode=editor&id=" +  byId('availableGroups').value;
	request.sendAsPost( params );
    request.send( 'renameIndicatorGroupEditor.action');	
}


/*==============================================================================
 * Update Member of Indicator Group
 *==============================================================================*/
 
function assignGroupsForIndicator()
{
	try
	{		
	    var indicatorId = byId('availableIndicators').value;	
		
	    var request = new Request();

	    var requestString = 'asignGroupsForIndicator.action';

	    var params = "indicatorId=" + indicatorId;
		params += "&mode=editor";

	    var selectedGroups = byId( 'assignedGroups' );

	    for ( var i = 0; i < selectedGroups.options.length; ++i)
	    {
	        params += '&indicatorGroups=' + selectedGroups.options[i].value;
	    }   
	    request.sendAsPost( params );
	    request.setResponseTypeXML( 'xmlObject' );  
	    request.setCallbackSuccess( assignGroupsForIndicatorReceived );
	    request.send( requestString );  
	}
	catch( e )
	{
		setHeaderDelayMessage( i18n_select_indicator_group );
	}
}

function assignGroupsForIndicatorReceived( xmlObject )
{	
    setHeaderDelayMessage( i18n_update_success );
}

/*==============================================================================
 * Delete Indicator Group
 *==============================================================================*/

function deleteIndicatorGroup()
{
	var list = byId('availableGroups');
	
	try {
		var id = list.options[ list.selectedIndex ].value;
		var name = list.options[ list.selectedIndex ].text;

		if ( window.confirm( i18n_confirm_delete ) )
		{
			$.getJSON
			(
				'deleteIndicatorGroupEditor.action',
				{
					"id": list.value
				},
				function( json )
				{
					if ( json.response == "success" )
					{
						var list = byId('availableGroups');
						list.remove( list.selectedIndex );
					}
					else if ( json.response == "error" )
					{
						setFieldValue( 'warningArea', json.message );
			
						showWarning();
					}
				}
			);
		}
	}
	catch(e)
	{
		setHeaderDelayMessage(i18n_select_indicator_group);
	}
}