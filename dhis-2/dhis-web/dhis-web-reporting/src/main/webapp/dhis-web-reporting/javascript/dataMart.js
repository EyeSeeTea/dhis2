
var lastNotificationUid = null;
var loaderBarHtml = '<span id="loaderSpan"><img src="../images/ajax-loader-bar.gif"></span>';

$( document ).ready( function() {
	datePickerInRange( 'startDate' , 'endDate' );
	pingNotificationsTimeout();
} );

function startExport()
{
	$( '#notificationTable' ).show().prepend( '<tr><td>' + loaderBarHtml + '</td></tr>' );
	
	var startDate = $( '#startDate' ).val();
	var endDate = $( '#endDate' ).val();
	
	var url = 'startExport.action?startDate=' + startDate + '&endDate=' + endDate;
	
	$( 'input[name="periodType"]').each( function() {
		url += "&periodType=" + $( this ).val();
	} );
	
	$.get( url, pingNotifications );
}

function pingNotifications()
{
	var param = lastNotificationUid ? '&lastUid=' + lastNotificationUid : '';
		
	$.getJSON( '../dhis-web-commons-ajax-json/getNotifications.action?category=DATAMART' + param, function( json )
	{
		var html = '';
		var completedHtml = '<span id="completedSpan"><img src="../images/completed.png"></span>';
		
		$.each( json.notifications, function( i, notification )
		{
			var first = i == 0;
			var loaderHtml = '';			
			
			if ( first )
			{
				lastNotificationUid = notification.uid;
				loaderHtml = loaderBarHtml;
				$( '#loaderSpan' ).replaceWith ( '' ); // Hide previous loader bar
			}		
			
			html += '<tr><td>' + notification.time + '</td><td>' + notification.message + ' &nbsp;';
			html += notification.completed == true ?  completedHtml : loaderHtml;
			html += '</td></tr>';
		} );
		
		$( '#notificationTable' ).prepend( html );
	} );
}

function pingNotificationsTimeout()
{
	pingNotifications();
	setTimeout( "pingNotificationsTimeout()", 2500 );
}
