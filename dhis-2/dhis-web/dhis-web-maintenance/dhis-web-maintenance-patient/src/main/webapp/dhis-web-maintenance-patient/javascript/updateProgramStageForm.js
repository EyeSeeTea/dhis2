var duplicate = false;
jQuery( document ).ready( function()
{
	showHideUserGroup();
	validation( 'updateProgramStageForm', function( form ){ 
		if( duplicate==true) 
			return false;
		else
			form.submit();
	}, function(){
		duplicate = false;
		var COLOR_RED = '#ff8a8a';
		jQuery(".daysAllowedSendMessage").each( function( i, a ){ 
			jQuery(".daysAllowedSendMessage").each( function(j, b){ 
				if( i!=j && a.value==b.value){
					jQuery( a ).css( 'background-color', COLOR_RED );
					jQuery( b ).css( 'background-color', COLOR_RED );
					duplicate = true;
				}
			});
		});
		
		var selectedDataElementsValidator = jQuery( "#selectedDataElementsValidator" );
		selectedDataElementsValidator.empty();
		
		var compulsories = jQuery( "#compulsories" );
		compulsories.empty();
		
		var displayInReports = jQuery( "#displayInReports" );
		displayInReports.empty();
		
		var allowDateInFutures = jQuery( "#allowDateInFutures" );
		allowDateInFutures.empty();
		
		var daysAllowedSendMessages = jQuery( "#daysAllowedSendMessages" );
		daysAllowedSendMessages.empty();
		
		var templateMessages = jQuery( "#templateMessages" );
		templateMessages.empty();
		
		var allowProvidedElsewhere = jQuery( "#allowProvidedElsewhere" );
		allowProvidedElsewhere.empty();
		
		var sendTo = jQuery( "#sendTo" );
		sendTo.empty();
		
		var whenToSend = jQuery( "#whenToSend" );
		whenToSend.empty();
		
		var userGroup = jQuery( "#userGroup" );
		userGroup.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			
			selectedDataElementsValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			
			var compulsory = jQuery( item ).find( "input[name='compulsory']:first");
			var checked = compulsory.attr('checked') ? true : false;
			compulsories.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			
			var allowProvided = jQuery( item ).find( "input[name='allowProvided']:first");
			checked = allowProvided.attr('checked') ? true : false;
			allowProvidedElsewhere.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			
			var displayInReport = jQuery( item ).find( "input[name='displayInReport']:first");
			checked = displayInReport.attr('checked') ? true : false;
			displayInReports.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		
			var allowDateInFuture = jQuery( item ).find( "input[name='allowDateInFuture']:first");
			checked = allowDateInFuture.attr('checked') ? true : false;
			allowDateInFutures.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		});
		jQuery(".daysAllowedSendMessage").each( function( i, item ){ 
			daysAllowedSendMessages.append( "<option value='" + item.value + "' selected='true'>" + item.value +"</option>" );
		});
		jQuery(".templateMessage").each( function( i, item ){ 
			templateMessages.append( "<option value='" + item.value + "' selected='true'>" +item.value+"</option>" );
		});
		jQuery(".sendTo").each( function( i, item ){ 
			sendTo.append( "<option value='" + item.value + "' selected='true'>" + item.value +"</option>" );
		});
		jQuery(".whenToSend").each( function( i, item ){ 
			whenToSend.append( "<option value='" + item.value + "' selected='true'>" + item.value +"</option>" );
		});
		jQuery(".userGroup").each( function( i, item ){ 
			userGroup.append( "<option value='" + item.value + "' selected='true'>" + item.value +"</option>" );
		});
	});
	
	checkValueIsExist( "name", "validateProgramStage.action", {id:getFieldValue('programId'), programStageId:getFieldValue('id')});	
	
	jQuery("#availableList").dhisAjaxSelect({
		source: "../dhis-web-commons-ajax-json/getDataElements.action?domain=patient",
		iterator: "dataElements",
		connectedTo: 'selectedDataElementsValidator',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			var flag = false;
			jQuery("#selectedList").find("tr").each( function( k, selectedItem ){ 
				if(selectedItem.id == item.id )
				{
					flag = true;
					return;
				}
			});
			
			if(!flag) return option;
		}
	});
});
function showHideUserGroup()
{
	jQuery(".sendTo").each( function( i, item ){
		var numb = i+1;
		if( item.value == 5){
			showById( 'tr'+numb );
		}
		else
			hideById ( 'tr'+numb );
	});
}