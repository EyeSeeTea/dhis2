// -----------------------------------------------------------------------------
// Section details form
// -----------------------------------------------------------------------------

function showSectionDetails( sectionId )
{
	jQuery.get( 'getSection.action', { sectionId: sectionId }, function ( json ) {
		setInnerHTML( 'nameField', json.section.name );
		setInnerHTML( 'dataSetField', json.section.dataSet );
		setInnerHTML( 'categoryComboField', json.section.categoryCombo );
		setInnerHTML( 'dataElementCountField', json.section.dataElementCount );  
		
		showDetails();
	});
}

function sortOrderSubmit() 
{
	var datasetId = document.getElementById('dataSetId').value;

	if ( datasetId == "null" ) 
	{
		window.alert( i18n_please_select_dataset );
	} else 
	{
		window.location.href = "showSortSectionForm.action?dataSetId=" + datasetId;
	}
}

function getSectionByDataSet(dataSetId) 
{
	window.location.href = "section.action?dataSetId=" + dataSetId;
}

function removeSection(sectionId, sectionName) 
{
	removeItem(sectionId, sectionName, i18n_confirm_delete,	"removeSection.action");
}

function addSectionSubmit() 
{
	var dataSetId = document.getElementById('dataSetId').value;
	var categoryComboId = document.getElementById('categoryComboId').value;

	if ( dataSetId == "null" || dataSetId == "" || categoryComboId == "null" || categoryComboId == "" ) 
	{
		showWarningMessage( i18n_please_select_dataset_categorycombo );
	} 
	else 
	{
		window.location.href = "getSectionOptions.action?dataSetId=" + dataSetId + "&categoryComboId=" + categoryComboId;
	}
}

function toggle( dataElementId, optionComboId ) 
{
	var elementId = '[' + dataElementId;

	if (optionComboId != '') 
	{
		elementId = elementId + ']_[' + optionComboId;
	}

	elementId = elementId + ']';

	if (byId(elementId + '.text').disabled == true) 
	{
		byId(elementId + '.text').disabled = false;
		byId(elementId + '.button').value = i18n_disable;
	} 
	else 
	{
		byId(elementId + '.text').disabled = true;
		byId(elementId + '.button').value = i18n_enable;
	}
}

// -----------------------------------------------------------------------------
// Grey/Ungrey Fields
// -----------------------------------------------------------------------------

function saveGreyStatus( sectionId_, dataElementId_, optionComboId_ ) 
{
	var sectionId = sectionId_;
	var dataElementId = dataElementId_;
	var optionComboId = optionComboId_;
	var isGreyed;

	var elementId = '[' + dataElementId;	

	if ( optionComboId != '') {
		elementId = elementId + ']_[' + optionComboId;
	}

	elementId = elementId + ']';
	
	var txtElementId = elementId + '.txt';
	var btnElementId = elementId + '.btn';

	if ( document.getElementById( txtElementId ).disabled == true ) 
	{
		document.getElementById( txtElementId ).disabled = false;
		document.getElementById( btnElementId ).value = i18n_disable;

		isGreyed = false;		
	} 
	else 
	{
		document.getElementById( txtElementId ).disabled = true;
		document.getElementById( btnElementId ).value = i18n_enable;

		isGreyed = true;
	}
	
	// TODO check result
	
	$.post( 'saveSectionGreyStatus.action',
		{ sectionId:sectionId, dataElementId:dataElementId, optionComboId:optionComboId, isGreyed:isGreyed }, 
		function() {} );
}
