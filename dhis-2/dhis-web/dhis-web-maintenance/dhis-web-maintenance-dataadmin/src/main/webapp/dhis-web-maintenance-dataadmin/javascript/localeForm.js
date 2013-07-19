
function setName()
{		
	var displayName = "";

	var language = $( '#language option:selected' );
	var country = $( '#country option:selected' );
		
	
	if( language.val() != "" && country.val() != "" )
	{
		displayName = language.text();	
	    
		if( country.val() != "default" )
		{
			displayName = displayName + ", " + country.text();
		}
	}

	setFieldValue('name',displayName);
	
	//$( '#name' ).val( displayName );
}
