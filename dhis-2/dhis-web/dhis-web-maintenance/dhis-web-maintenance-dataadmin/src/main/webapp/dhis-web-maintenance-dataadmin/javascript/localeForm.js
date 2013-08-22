function setName()
{		
	var displayName = "";

	var language = $( '#language option:selected' );
	var country = $( '#country option:selected' );
		
	
	if( language.val() != "")
	{
		displayName = language.text();	
	    
		if( country.val() != "" )
		{
			displayName = displayName + ", " + country.text();
		}
	}

	setFieldValue('name',displayName);
}
