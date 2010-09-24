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
// ========================================================================
function initAllList()
{
    var id;

    for ( id in dataElementGroups )
    {
		var option = new Option( dataElementGroups[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        $("#dataElementGroups").append(option);
    }

    var list = byId( 'availableDataElements' );

    for ( id in availableDataElements )
    {
		var option = new Option( availableDataElements[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        $("#availableDataElements").append(option);
    }

    if(list.selectedIndex==-1)
    {
        list.disabled = true;
    }
}

function addSelectedDataElements()
{
    var list = byId( 'availableDataElements' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        selectedDataElements[id] = availableDataElements[id];

    }

    filterSelectedDataElements();
    filterAvailableDataElements();
}

function removeSelectedDataElements()
{
    var list = byId( 'selectedDataElements' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        //availableDataElements[id] = selectedDataElements[id];

        delete selectedDataElements[id];
    }

    filterSelectedDataElements();
    filterAvailableDataElements();
}

function filterAvailableDataElements()
{
    var filter = byId( 'availableDataElementsFilter' ).value;
    var list = byId( 'availableDataElements' );

    list.options.length = 0;

    for ( var id in availableDataElements )
    {
        var value = availableDataElements[id];

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

function filterSelectedDataElements()
{
    var filter = byId( 'selecteDataElementsFilter' ).value;
    var list = byId( 'selectedDataElements' );

    list.options.length = 0;

    for ( var id in selectedDataElements )
    {
        var value = selectedDataElements[id];

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

function getDataElementGroup( dataElementGroupList )
{
    selectedDataElements = new Object();
    var id = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataElementGroupCompleted );
    request.send( 'getDataElementGroupEditor.action?id=' + id );
}

function getDataElementGroupCompleted( xmlObject )
{
    var selectedList = byId( 'selectedDataElements' );
    selectedList.length = 0;
    name = xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue;
    var dataElementList = xmlObject.getElementsByTagName('dataElement');

    for ( var i = 0; i < dataElementList.length; i++ )
    {
        dataElement = dataElementList.item(i);
        var id = dataElement.getAttribute('id');
        var value = dataElement.firstChild.nodeValue;
        selectedDataElements[id] = value;
    }
	byId( 'groupNameView' ).innerHTML = name;
	
    filterSelectedDataElements();
    byId('availableDataElements').disabled=false;
	visableAvailableDataElements();	
}

function visableAvailableDataElements()
{
	var selectedList = byId( 'selectedDataElements' );
	var availableList = byId( 'availableDataElements' );
	var selectedOptions = selectedList.options;
	var availableOptions = availableList.options;
	
	for(var i=0;i<availableOptions.length;i++){
		availableList.options[i].style.display='block';
		for(var j=0;j<selectedOptions.length;j++){			
			if(availableOptions[i].value==selectedOptions[j].value){				
				availableList.options[i].style.display='none';
			}
		}
	}
}

function updateDataElementGroupMembers()
{
    var dataElementGroupsSelect = byId( 'dataElementGroups' );
    var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;

    var request = new Request();

    var requestString = 'updateDataElementGroupEditor.action';

    var params = "id=" + id;

    var selectedDataElementMembers = byId( 'selectedDataElements' );

    for ( var i = 0; i < selectedDataElementMembers.options.length; ++i)
    {
        params += '&groupMembers=' + selectedDataElementMembers.options[i].value;
    }
    request.sendAsPost( params );
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( updateDataElementGroupMembersReceived );
    request.send( requestString );
}

function updateDataElementGroupMembersReceived( xmlObject )
{
    var dataElementGroupsSelect = byId( 'dataElementGroups' );
    setHeaderDelayMessage( i18n_update_success + " : " + dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text );
}

function deleteDataElementGroup()
{
    var dataElementGroupsSelect = byId( 'dataElementGroups' );

    try
    {
        var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
        var name =  dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
		
        if ( window.confirm( i18n_confirm_delete + '\n\n' + name ) )
        {
		    $.getJSON
			(
				'deleteDataElemenGroupEditor.action',
				{
					"id": id
				},
				function( json )
				{
					if ( json.response == "success" )
					{
						var dataElementGroupsSelect = byId( 'dataElementGroups' );
						dataElementGroupsSelect.remove( dataElementGroupsSelect.selectedIndex );
						byId( 'groupNameView' ).innerHTML = "";
						selectedDataElements = new Object();
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
        setHeaderDelayMessage( i18n_select_dataelement_group );
    }
}

function showRenameDataElementGroupForm()
{
    var dataElementGroupsSelect = byId( 'dataElementGroups' );

    try
    {
        var name =  dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
        byId( 'addRenameGroupButton' ).onclick=validateRenameDataElementGroup;
        showPopupWindowById( 'addDataElementGroupForm', 450, 70 );
		byId( 'groupName' ).value = name;
    }
    catch(e)
    {
        setHeaderDelayMessage(i18n_select_dataelement_group);
    }
}

function validateRenameDataElementGroup()
{
    var dataElementGroupsSelect = byId( 'dataElementGroups' );
    var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
    var name = byId( 'groupName' ).value;
    var request = new Request();
	
	$.postJSON(
		"validateDataElementGroup.action",
		{ "id": id, "name": name },
		function( json )
		{
			if ( json.response == "success" )
			{
				renameDataElementGroup();
			}
			else
			{
				alert(json.message);
			}
		}
	);
}

function renameDataElementGroup()
{
    var dataElementGroupsSelect = byId( 'dataElementGroups' );
    var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
    var name = byId( 'groupName' ).value;
    var request = new Request();
	
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( renameDataElementGroupReceived );
	request.sendAsPost('id=' + id + '&name=' + name);
    request.send( 'renameDataElementGroupEditor.action' );

}

function renameDataElementGroupReceived( xmlObject )
{
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    var list = byId( 'dataElementGroups' );
    list.options[ list.selectedIndex ].text = name;
    byId( 'groupNameView' ).innerHTML = name;
    hideById( 'addDataElementGroupForm' );
    unLockScreen();
}

function showAddDataElementGroupForm()
{
    byId( 'groupName' ).value='';
    byId( 'addRenameGroupButton' ).onclick=validateAddDataElementGroup;
    showPopupWindowById( 'addDataElementGroupForm', 450, 70 );
}

function validateAddDataElementGroup()
{
	$.postJSON(
		"validateDataElementGroup.action",
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
	request.sendAsPost('name=' + name);
    request.send( 'addDataElementGroupEditor.action' );
}

function createNewGroupReceived( xmlObject )
{
    var id = xmlObject.getElementsByTagName( "id" )[0].firstChild.nodeValue;
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    var list = byId( 'dataElementGroups' );
    var option = new Option( name, id );
	option.selected = true;
	option.onmousemove  = function(e){
		showToolTip( e, name);				
	}
    list.add(option , null);
    byId( 'groupNameView' ).innerHTML = name;
    hideById( 'addDataElementGroupForm' );
    unLockScreen();
}