
function organisationUnitSelected( orgUnits )
{		
    window.location.href = 'patient.action';    
}

selection.setListenerFunction( organisationUnitSelected );


//------------------------------------------------------------------------------
//Popup window
//------------------------------------------------------------------------------

function editWindow( url, patientId ) 
{
	var url = url + '?id=' + patientId;	
	var width = 800
    var height = 500;
    var left = parseInt( ( screen.availWidth/2 ) - ( width/2 ) );
    var top = parseInt( ( screen.availHeight/2 ) - ( height/2 ) );
    var windowFeatures = 'width=' + width + ',height=' + height + ',scrollbars=yes, resizable=yes,left=' + left + ',top=' + top + 'screenX=' + left + ',screenY=' + top;
    
    window.open( url, '_blank_', windowFeatures);
}

//------------------------------------------------------------------------------
// Validate EnrollmentDate
//------------------------------------------------------------------------------

function validateProgramEnrollment()
{	
	
	var url = 'validatePatientProgramEnrollment.action?' +
			'enrollmentDate=' + getFieldValue( 'enrollmentDate' ) +
			'&dateOfIncident=' + getFieldValue( 'dateOfIncident' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( programEnrollmentValidationCompleted );    
    request.send( url );        

    return false;
}

function programEnrollmentValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'programEnrollmentForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_program_enrollment_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

//-----------------------------------------------------------------------------
//Save
//-----------------------------------------------------------------------------

function saveDueDate( programInstanceStageId, programInstanceStageName )
{
	var field = document.getElementById( 'value[' + programInstanceStageId + '].date' );
	
	field.style.backgroundColor = '#ffffcc';
	
	var dateSaver = new DateSaver( programInstanceStageId, field.value, '#ccffcc' );
	dateSaver.save();
  
}


//-----------------------------------------------------------------------------
//Saver objects
//-----------------------------------------------------------------------------

function DateSaver( programInstanceStageId_, dueDate_, resultColor_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ccccff';
	
	var programInstanceStageId = programInstanceStageId_;	
	var dueDate = dueDate_;
	var resultColor = resultColor_;	

	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'saveDueDate.action?programInstanceStageId=' + programInstanceStageId + '&dueDate=' + dueDate );
	};

	function handleResponse( rootElement )
	{
		var codeElement = rootElement.getElementsByTagName( 'code' )[0];
		var code = parseInt( codeElement.firstChild.nodeValue );
   
		if ( code == 0 )
		{
			markValue( resultColor );                   
		}
		else
		{
			markValue( ERROR );
			window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
		}
	}

	function handleHttpError( errorCode )
	{
		markValue( ERROR );
		window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
	}   

	function markValue( color )
	{       
   
		var element = document.getElementById( 'value[' + programInstanceStageId + '].date' );	
           
		element.style.backgroundColor = color;
	}
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patient' );
    request.setCallbackSuccess( patientReceived );
    request.send( 'getPatient.action?id=' + patientId );
}

function patientReceived( patientElement )
{
	setFieldValue( 'identifierField', getElementValue( patientElement, 'identifier' ) );
	setFieldValue( 'firstNameField', getElementValue( patientElement, 'firstName' ) );
    setFieldValue( 'middleNameField', getElementValue( patientElement, 'middleName' ) );
    setFieldValue( 'lastNameField', getElementValue( patientElement, 'lastName' ) );
    setFieldValue( 'genderField', getElementValue( patientElement, 'gender' ) );  
    setFieldValue( 'birthDateField', getElementValue( patientElement, 'birthDate' ) );
    //setFieldValue( 'enrolledProgramField', getElementValue( patientElement, 'enrolledProgram' ) );    
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, firstName, lastName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + lastName + ',' + firstName );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removePatientCompleted );
        window.location.href = 'removePatient.action?id=' + patientId;
    }
}

function removePatientCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'patientform.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function validateSearchPatient()
{	
	
	var url = 'validateSearchPatient.action?' +
			'searchText=' + getFieldValue( 'searchText' );	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
 	request.setCallbackSuccess( searchValidationCompleted );    
 	request.send( url );        

 	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if ( type == 'success' )
	{
		var form = document.getElementById( 'searchPatientForm' );        
		form.submit();
	}
	else if ( type == 'error' )
	{
		window.alert( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		document.getElementById( 'message' ).innerHTML = message;
		document.getElementById( 'message' ).style.display = 'block';
	}
}


// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function validateAddPatient()
{
	
	var url = 'validatePatient.action?' +
			'identifier=' + getFieldValue( 'identifier' ) +
			'&firstName=' + getFieldValue( 'firstName' ) +
	        '&middleName=' + getFieldValue( 'middleName' ) +
	        '&lastName=' + getFieldValue( 'lastName' ) +
	        '&gender=' + getFieldValue( 'gender' ) +
	        '&birthDate=' + getFieldValue( 'birthDate' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );    
    request.send( url );        

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addPatientForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update Patient
// -----------------------------------------------------------------------------

function validateUpdatePatient()
{
    var url = 'validatePatient.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&identifier=' + getFieldValue( 'identifier' ) +
    		'&firstName=' + getFieldValue( 'firstName' ) +
	        '&middleName=' + getFieldValue( 'middleName' ) +
	        '&lastName=' + getFieldValue( 'lastName' ) +
	        '&gender=' + getFieldValue( 'gender' ) +
	        '&birthDate=' + getFieldValue( 'birthDate' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    
    request.send( url );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updatePatientForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}


//-----------------------------------------------------------------------------
//Move members
//-----------------------------------------------------------------------------
var selectedList;
var availableList;

function move( listId ) {
	
	var fromList = document.getElementById(listId);
	
	if ( fromList.selectedIndex == -1 ) { return; }
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById( 'availableList' );
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById( 'selectedList' );
	}
	
	var toList = ( fromList == availableList ? selectedList : availableList );
	
	while ( fromList.selectedIndex > -1 ) {
		
		option = fromList.options.item(fromList.selectedIndex);
		fromList.remove(fromList.selectedIndex);
		toList.add(option, null);
	}
}

function submitForm() {
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById('availableList');
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById('selectedList');
	}
	
	selectAll( selectedList );
	
	return false;
}

function selectAll( list ) 
{
	for ( var i = 0, option; option = list.options.item(i); i++ ) 
	{
		option.selected = true;
	}
}
