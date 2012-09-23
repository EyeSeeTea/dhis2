isAjax = true;
function organisationUnitSelected( orgUnits, orgUnitNames )
{
    showLoader();
	setInnerHTML( 'contentDiv','' );
	jQuery.getJSON( "getReportPrograms.action",{}, 
		function( json ) 
		{    
			setFieldValue( 'orgunitname', orgUnitNames[0] );
			
			clearListById('programId');
			if( json.programs.length == 0)
			{
				disable('programId');
				disable('startDate');
				disable('endDate');
				disable('generateBtn');
			}
			else
			{
				addOptionById( 'programId', "", i18n_please_select_a_program );
				
				for ( var i in json.programs ) 
				{
					addOptionById( 'programId', json.programs[i].id, json.programs[i].name );
				} 
				enable('programId');
				enable('startDate');
				enable('endDate');
				enable('generateBtn');
			}
			
			hideLoader();
		});
}

selection.setListenerFunction( organisationUnitSelected );

function loadGeneratedReport()
{
	showLoader();

	jQuery( "#contentDiv" ).load( "generateReport.action",
	{
		programId: getFieldValue( 'programId' ),
		startDate: getFieldValue( 'startDate' ),
		endDate: getFieldValue( 'endDate' ),
		facilityLB: $('input[name=facilityLB]:checked').val()
	}, function() 
	{ 
		jQuery( "[name=newEncounterBtn]" ).addClass("hidden");
		jQuery( "[name=newEncounterBtn]" ).removeClass("show-new-event");
		jQuery( "[status=3]" ).attr("disabled", true);
		jQuery( "[status=4]" ).attr("disabled", true);
		hideLoader();
		hideById( 'message' );
		showById( 'contentDiv' );
		resize();
	});
}

function loadDataEntry( programStageInstanceId ) 
{
	jQuery('#patientInforTB').val('');
	jQuery('#viewRecordsDiv' )
		.load( 'viewProgramStageRecords.action?programStageInstanceId=' + programStageInstanceId
		,function(){
			jQuery("#viewRecordsDiv :input" ).attr("disabled", true);
			jQuery("#viewRecordsDiv :input" ).datepicker("destroy");
			jQuery(".ui-combobox" ).hide();
			hideById('inputCriteriaDiv');
			
			var programStageInstance = jQuery("#ps_" + programStageInstanceId);
			var header = "<h4>" + i18n_records_for + " " + programStageInstance.attr("orgunit")
					+ " " + i18n_at + " " + programStageInstance.attr("psname")
					+ " " + i18n_on_date + " " + programStageInstance.attr("reportDate") + "</h4><hr>";
			setInnerHTML('patientInforTB', header );
		})
		.dialog({
			title: i18n_reports,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 840,
			height: 400
		});
}

function entryFormContainerOnReady(){}
