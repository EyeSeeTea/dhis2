
function pingNotificationsTimeout()
{
	pingNotifications( 'DATAVALUE_IMPORT', 'notificationTable' );
	setTimeout( "pingNotificationsTimeout()", 2500 );
}
