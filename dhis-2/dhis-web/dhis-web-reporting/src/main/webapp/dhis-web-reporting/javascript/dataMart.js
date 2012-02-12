
$( document ).ready( function() {
	datePickerInRange( 'startDate' , 'endDate' );
} );

function startExport()
{
	var url = 'startExport.action';
	var startDate = $( '#startDate' ).val();
	var endDate = $( '#endDate' ).val();
	
	$.get( url, { startDate:startDate, endDate:endDate }, function() {
		} );
}