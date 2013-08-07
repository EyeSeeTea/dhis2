
function recoverAccount()
{
	var username = $.trim( $( "#username" ).val() );
	
	if ( username.length == 0 )
	{
		return false;
	}
	
	$.ajax( {
		url: "../../api/account/recovery",
		data: {
			username: username
		},
		type: "post",
		success: function( data ) {
			$( "#recoveryForm" ).hide();
			$( "#recoverySuccessMessage" ).fadeIn();
		},
		error: function( data ) {
			$( "#recoveryForm" ).hide();
			$( "#recoveryErrorMessage" ).fadeIn();
		}
	} );
}

function updateTranslation(translation)
{	
	$('#account_recovery').html(translation.account_recovery);
	$('#recovery_user_name').html(translation.user_name);
	$('#recoveryButton').val(translation.recover);
	$('#recoverySuccessMessage').html(translation.recover_success_message);
	$('#recoveryErrorMessage').html(translation.recover_error_message);
}
