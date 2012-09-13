
function orgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatientDiv");
	clearListById('programIdAddPatient');
	$('#contentDataRecord').html('');
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
	jQuery.get("getPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddPatient').append( '<option value="">' + i18n_please_select + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
		});
}

selection.setListenerFunction( orgunitSelected );

function showActitityList()
{
	setFieldValue('listAll', "true");
	hideById('listPatientDiv');
	contentDiv = 'listPatientDiv';
	$('#contentDataRecord').html('');
	
	var programId = getFieldValue('programIdAddPatient');
	var searchTexts = "stat_" + programId + "_3_" 
					+ getFieldValue('startDueDate')
					+ "_" + getFieldValue('endDueDate')
					+ "_" + getFieldValue('orgunitId');
	
	showLoader();
	jQuery('#listPatientDiv').load('getActivityPlanRecords.action',
		{
			programId:programId,
			listAll:false,
			searchBySelectedOrgunit: false,
			searchTexts: searchTexts
		}, 
		function()
		{
			showById('colorHelpLink');
			showById('listPatientDiv');
			hideLoader();
		});
}

function eventFlowToggle( programInstanceId )
{
	jQuery("#tb_" + programInstanceId + " .stage-object").each( function(){
			var programStageInstance = this.id.split('_')[1];
			jQuery('#arrow_' + programStageInstance ).toggle();
			jQuery('#td_' + programStageInstance ).toggle();
			jQuery(this).removeClass("stage-object-selected");
		});
	
	jQuery("#tb_" + programInstanceId + " .arrow-left").toggle();
	jQuery("#tb_" + programInstanceId + " .arrow-right").toggle();
	if( jQuery("#tb_" + programInstanceId + " .searched").length>0)
	{	
		var id = jQuery("#tb_" + programInstanceId + " .searched").attr('id').split('_')[1];
		showById("arrow_" + id);
		showById("td_" + id );
	}
}

// --------------------------------------------------------------------
// Patient program tracking
// --------------------------------------------------------------------

function loadDataEntry( programStageInstanceId ) 
{
	jQuery("#patientList input[name='programStageBtn']").each(function(i,item){
		jQuery(item).removeClass('stage-object-selected');
	});
	jQuery( '#' + prefixId + programStageInstanceId ).addClass('stage-object-selected');
	setFieldValue('programStageInstanceId', programStageInstanceId);
	
	$('#contentDataRecord' ).load("viewProgramStageRecords.action",
		{
			programStageInstanceId: programStageInstanceId
		}).dialog(
		{
			title:i18n_program_stage,
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:1000,
			height:500
		});
}

function statusEventOnChange()
{
	var statusEvent = getFieldValue("statusEvent");
	
	if( statusEvent == '3' ){
		disable('showEventSince');
		enable('showEventUpTo');
		setDateRangeUpTo( getFieldValue("showEventUpTo") );
	}
	else{
		enable('showEventSince');
		disable('showEventUpTo');
		setDateRangeSince( getFieldValue("showEventSince") );
	}
}

function setDateRangeSince( days )
{
	if(days == "")
		return;
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();
	
	var startDate = "";
	if( days == 'ALL'){
		startDate = jQuery.datepicker.formatDate( dateFormat, new Date(y-100, m, d) ) ;
	}
	else{
		d = d + eval(days);
		startDate = jQuery.datepicker.formatDate( dateFormat, new Date(y, m, d) ) ;
	}
	
	var endDate = jQuery.datepicker.formatDate( dateFormat, new Date() );
	jQuery("#startDueDate").val(startDate);
	jQuery("#endDueDate").val(endDate);
}

function setDateRangeUpTo( days )
{
	if(days == "")
		return;
		
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();
	
	var startDate = jQuery.datepicker.formatDate( dateFormat, new Date() );
	var endDate = "";
	if( days == 'ALL'){
		endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y+100, m, d) ) ;
	}
	else{
		d = d + eval(days);
		endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y, m, d) ) ;
	}
	
	jQuery("#startDueDate").val(startDate);
	jQuery("#endDueDate").val(endDate);
}