function dataSetSelected()
{
	var dataSetId = $( '#dataSetId' ).val();	
	var periodFrom = $( '#sDateLB' ).val();
	var periodTo = $( '#eDateLB' ).val();
	
	if ( dataSetId && dataSetId != 0 )
	{
		var url = 'loadPeriods.action?dataSetId=' + dataSetId;

	    clearListById( 'sDateLB' );
		clearListById( 'eDateLB' );
	    
	    addOptionById( 'sDateLB', '', '[' + i18n_please_select + ']' );
		addOptionById( 'eDateLB', '', '[' + i18n_please_select + ']' );
		
	    $.getJSON( url, function( json ) {
			
	    	for ( i in json.periods ) {
	    		addOptionById( 'sDateLB', i, json.periods[i].name );
				addOptionById( 'eDateLB', i, json.periods[i].name );
	    	}
	    
			enable('previousPeriodForStartBtn');
			enable('nextPeriodForStartBtn');
			enable('previousPeriodForEndBtn');
			enable('nextPeriodForEndBtn');
	
	    } );
		
	}
	else
	{
		disable('previousPeriodForStartBtn');
		disable('nextPeriodForStartBtn');
		disable('previousPeriodForEndBtn');
		disable('nextPeriodForEndBtn');
	}
	
}

function getPreviousPeriodForStart() 
{
	var index = byId('sDateLB').options[byId('sDateLB').selectedIndex].value;
	jQuery.postJSON('previousPeriods.action?startField=true&index=' + index, {}, responseListPeriodForStartReceived );	
}

function getNextPeriodForStart() 
{
	var index = byId('sDateLB').options[byId('sDateLB').selectedIndex].value;
	jQuery.postJSON('nextPeriods.action?startField=true&index=' + index, {}, responseListPeriodForStartReceived );	
}

function responseListPeriodForStartReceived( json ) 
{	
	clearListById('sDateLB');
	
	jQuery.each( json.periods, function(i, item ){
		addOptionById('sDateLB', i, item.name);
	});
}

function getPreviousPeriodForEnd() 
{
	var index = byId('eDateLB').options[byId('eDateLB').selectedIndex].value;
	jQuery.postJSON('previousPeriods.action?startField=false&index=' + index, {}, responseListPeriodForEndReceived );	
}

function getNextPeriodForEnd() 
{
	var index = byId('eDateLB').options[byId('eDateLB').selectedIndex].value;
	jQuery.postJSON('nextPeriods.action?startField=false&index=' + index, {}, responseListPeriodForEndReceived );	
}

function responseListPeriodForEndReceived( json ) 
{	
	clearListById('eDateLB');
	
	jQuery.each( json.periods, function(i, item ){
		addOptionById('eDateLB', i, item.name );
	});
}
	
function validationCaseAggregation( )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElementGroup' );
    request.setCallbackSuccess( validationCaseAggregationCompleted );
	
	var url  = "sDateLB=" + byId('sDateLB').value;
		url += "&eDateLB=" + byId('eDateLB').value;
		
	request.sendAsPost(url);
    request.send( 'validateCaseAggregation.action' );
}

function validationCaseAggregationCompleted( message )
{
    var type = message.getAttribute("type");
	
    if( type == "success" )
    {
        document.forms[ 'caseAggregationForm' ].submit();
    }
    else
    {
        setMessage(message.firstChild.nodeValue);
    }
}

function viewResultDetails( orgunitId, periodId, aggregationConditionId ) 
{
	var url = 'caseAggregationResultDetails.action?';
		url+= 'orgunitId=' + orgunitId;
		url+= '&periodId=' + periodId;
		url+= '&aggregationConditionId=' + aggregationConditionId;
		
	$('#contentDetails').dialog('destroy').remove();
    $('<div id="contentDetails">' ).load(url).dialog({
        title: '',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
        height: 400
    });
}

