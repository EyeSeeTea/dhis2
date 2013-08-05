
// Set the selection locale as a cookie value and retrieve translations asynchronously.
function selectLanguage(localeSelected)
{
	$.cookie("loginLocale", localeSelected, { expires : 356*10 });
	
	$.getJSON( translationUrl + localeSelected, function( json ) {			
		updateTranslation( json );
	});            	           
}


function updateTranslation(translation)
{            	
	$('#username_label').html(translation.login_username);	// label
	$('#password_label').html(translation.login_password);

	$('#submit').val(translation.button_login);	// input
	$('#reset').val(translation.button_clear);
	$('#forgetPassword').html(translation.forgot_password);  // a

	$('#createAccount').html(translation.create_an_account);
	$('#createAccount_top').html(translation.create_an_account);  // a
	$('#loginMessage').html(translation.wrong_username_or_password);

	$('#powerBy').html(translation.powered_by);       // span
	$('#language').html(translation.login_language);
	
	
	if(translation.applicationTitle != '') 
	{
		$('#titleArea').html(translation.applicationTitle);
	}
	else $('#titleArea').html($('#titleAreaOriginalHdn').val());

	if(translation.keyApplicationIntro != '') $('#introArea').html(translation.keyApplicationIntro);
	else $('#introArea').html($('#introAreaOriginalHdn').val());

	if(translation.keyApplicationNotification != '') $('#notificationArea').html(translation.keyApplicationNotification);
	else $('#notificationArea').html($('#notificationAreaOriginalHdn').html());

	if(translation.keyApplicationFooter != '') $('#applicationFooter').html(translation.keyApplicationFooter);
	else $('#applicationFooter').html($('#applicationFooterOriginalHdn').html());
}
          

function getSynchData(url) {
	return $.parseJSON(
		$.ajax( {
			type: "GET",
			dataType: "json",
			url: url,
			async: false,
		} ).responseText 
	);
}
