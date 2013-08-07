var validationRules = {
	rules: {
		firstName: {
			required: true,
			rangelength: [ 2, 80 ]
		},
		surname: {
			required: true,
			rangelength: [ 2, 80 ]
		},
		username: {
			required: true,
			rangelength: [ 4, 80 ],
			remote: "../../api/account/username"
		},
		password: {
			required: true,
			rangelength: [ 8, 80 ],
			password: true,
			notequalto : "#username",
		},
		retypePassword: {
			required: true,
			equalTo: "#password",
		},
		email: {
			required: true,
			email: true,
			rangelength: [ 4, 80 ]
		},
		phoneNumber: {
			required: true,
			rangelength: [ 6, 30 ]			
		},
		employer: {
			required: true,
			rangelength: [ 2, 80 ]
		}
	}
};


var accountTranslationUrl = 'accountStrings.action?page=account&language=';

$( document ).ready( function() {
		
	updateTranslationFromCookie(accountTranslationUrl);	
	
	Recaptcha.create( "6LcM6tcSAAAAANwYsFp--0SYtcnze_WdYn8XwMMk", "recaptchaDiv", {
		theme: "white"
	} );
	
	$( "#accountForm" ).validate( {
		rules: validationRules.rules,
		submitHandler: accountSubmitHandler,
		errorPlacement: function( error, element ) {
			element.parent( "td" ).append( "<br>" ).append( error );
		}
	} );
} );


function updateTranslation(translation)
{	
	$('#create_new_account').html(translation.create_new_account);
	$('#new_user_account_name').html(translation.name);
	$('#firstName').attr("placeholder", translation.first_name);
	$('#surname').attr("placeholder", translation.last_name);
	$('#new_user_account_user_name').html(translation.user_name);
	$('#new_user_account_password').html(translation.password);
	$('#new_user_account_confirm_password').html(translation.confirm_password);
	$('#new_user_account_email').html(translation.email);
	$('#new_user_account_mobile_phone').html(translation.mobile_phone);
	$('#new_user_account_employer').html(translation.employer);
	$('#new_user_account_prove_not_robot').html(translation.prove_not_robot);
	$('#cant_read_words').html(translation.cant_read_words);
	$('#submitButton').val(translation.create);
}


function accountSubmitHandler()
{	
	if ( $.trim( $( "#recaptcha_challenge_field" ).val() ).length == 0 ||
		$.trim( $( "#recaptcha_response_field" ).val() ).length == 0 )
	{
		$( "#messageSpan" ).show().text( "Please enter a value for the word verification above" );		
		return false;
	}
	
	$( "#submitButton" ).attr( "disabled", "disabled" );
	
	$.ajax( {
		url: "../../api/account",
		data: $( "#accountForm" ).serialize(),
		type: "post",
		success: function( data ) {
			window.location.href = "../../dhis-web-commons-about/redirect.action";
		},
		error: function( jqXHR, textStatus, errorThrown ) {
			$( "#messageSpan" ).show().text( jqXHR.responseText );			
			Recaptcha.reload();
			$( "#submitButton" ).removeAttr( "disabled" );
		}
	} );
}

function reloadRecaptcha()
{
	Recaptcha.reload();
}
