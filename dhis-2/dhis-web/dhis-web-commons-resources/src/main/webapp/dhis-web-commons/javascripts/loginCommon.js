

function updateTranslationFromCookie(translationUrl)
{
	// On page load, get locale selection cookie value ('en' if no value) 
	// and retrieve translation synchronously before the page load.  
	// Also, set the dropdown selection.
	
	var localeFromCookie = $.cookie("loginLocale");				
					
	if(localeFromCookie == null)
	{
	 	localeFromCookie = 'en';
    }
		
	try
	{
		updateTranslation( getSynchData( translationUrl + localeFromCookie ) );    
	}
	catch(err)
	{
		alert("Error on updateTranslationFromCookie(): " + err);
	}
	
	return localeFromCookie;
}
	
function setLoginLocaleCookie(localeSelected)
{
	$.cookie("loginLocale", localeSelected, { expires : 356*10 });
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
