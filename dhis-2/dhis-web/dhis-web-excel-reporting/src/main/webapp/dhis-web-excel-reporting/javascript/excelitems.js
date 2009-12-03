// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================

// ========================================================================================================================
// Open Add Excel item form
// ========================================================================================================================

function openAddExcelItem(){
	enable("name");
	$("#divExcelitem").showAtCenter( true );
}

// ========================================================================================================================
// Open Update Excel item form
// ========================================================================================================================

function openUpdateExcelItem( id ){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( openUpdateExcelItemReceived );	
	request.send( "getExcelItem.action?id=" + id );	
	
	/* $.post("getExcelItem.action",{id:id},
	function ( xmlObject ){
		
		$("#id").val(id);
		$("#name").val( xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue );
		$("#expression").val( xmlObject.getElementsByTagName('expression')[0].firstChild.nodeValue);
		$("#row").val( xmlObject.getElementsByTagName('row')[0].firstChild.nodeValue  );
		$("#column").val( xmlObject.getElementsByTagName('column')[0].firstChild.nodeValue  );
		$("#sheetNo").val( xmlObject.getElementsByTagName('sheetNo')[0].firstChild.nodeValue  );
		
		$("#divExcelitem").showAtCenter( true );
		$("#name").attr("disabled", true);
		
	},'xml');	*/
}

function openUpdateExcelItemReceived(xmlObject){
	
	byId("id").value = xmlObject.getElementsByTagName('id')[0].firstChild.nodeValue;
	byId("name").value = xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue;
	byId("expression").value = xmlObject.getElementsByTagName('expression')[0].firstChild.nodeValue;
	byId("row").value = xmlObject.getElementsByTagName('row')[0].firstChild.nodeValue;
	byId("column").value = xmlObject.getElementsByTagName('column')[0].firstChild.nodeValue;
	byId("sheetNo").value = xmlObject.getElementsByTagName('sheetNo')[0].firstChild.nodeValue;
	
	$("#divExcelitem").showAtCenter( true );
	disable("name");
}
// ========================================================================================================================
// Validate Update Excel item group
// ========================================================================================================================

function validateExcelItem(){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( validateExcelItemReceived );
	var url = "validateExcelItem.action?name=" + byId("name").value;
	url += "&expression=" + byId("expression").value;
	url += "&row=" + byId("row").value;
	url += "&column=" + byId("column").value;
	url += "&sheetNo=" + byId("sheetNo").value;
	
	request.send( url );	
	
	
	/* $.post("validateExcelItem.action",{
		name:$("#name").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),
		sheetNo:$("#sheetNo").val()
	},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{		
			if(mode == 'add'){
				addExcelItem();
			}else{
				updateExcelItem();
			}
			
		}
	},'xml');	*/
}

function validateExcelItemReceived(xmlObject){
	
	var type = xmlObject.getAttribute( 'type' );
	if(type=='error')
	{
		setMessage(xmlObject.firstChild.nodeValue);
	}else if(type=='success')
	{		
		if(mode == 'add'){
			addExcelItem();
		}else{
			updateExcelItem();
		}
		
	}
}
// ========================================================================================================================
// Add Excel item
// ========================================================================================================================

function addExcelItem(){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( Completed );
	var params = "name=" + byId("name").value;	
	params += "&expression=" + byId("expression").value; 
	params += "&row=" + byId("row").value; 
	params += "&column=" + byId("column").value; 
	params += "&sheetNo=" + byId("sheetNo").value; 
	params += "&excelItemGroupId=" + getParamByURL("excelItemGroupId"); 
	request.sendAsPost( params );
	request.send( "addExcelItem.action" );	
	
	
	/* $.post("addExcelItem.action",{
		name:$("#name").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),
		sheetNo:$("#sheetNo").val(),
		excelItemGroupId:getParamByURL("excelItemGroupId")
	},function(data){
		window.location.reload();
	},'xml');	*/
}

function Completed(xmlObject){
	window.location.reload();
}

// ========================================================================================================================
// Update Excel Item
// ========================================================================================================================

function updateExcelItem(){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( Completed );
	
	var params = "id=" + byId("id").value; 
	params += "&name=" + byId("name").value;	
	params += "&expression=" + byId("expression").value; 
	params += "&row=" + byId("row").value; 
	params += "&column=" + byId("column").value; 
	params += "&sheetNo=" + byId("sheetNo").value; 
	params += "&excelItemGroupId=" + getParamByURL("excelItemGroupId"); 
	request.sendAsPost( params );
	request.send( "updateExcelItem.action" );	
	
}

// ========================================================================================================================
// Delete Excel Item
// ========================================================================================================================

function deleteExcelItem(id){
	if(window.confirm(i18n_confirm_delete)){
		
		var request = new Request();
		request.setResponseTypeXML( 'datalement' );
		request.setCallbackSuccess( Completed );
		request.send( "deleteExcelItem.action?id=" + id );
		
	}
}

// ========================================================================================================================
// Get parram from URL
// ========================================================================================================================

function getParamByURL(param){
	var name = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );

	return ( results == null ) ? "" : results[1];
}

// ===============================================================================
// Open Expression Form
// ===============================================================================

function openExpressionBuild(){
	
	byId("formula").value = byId("expression").value;
	getALLDataElementGroup();
	getDataElementsByGroup();
	enable("dataElementGroup");
	enable("availableDataElements");
	
	$("#availableDataElements").change(getOptionCombos);		
	$("#divExpression").showAtCenter( true );
	
}

// ===============================================================================
// Get all Dataelement Group
// ===============================================================================

function getALLDataElementGroup(){
	var list = byId('dataElementGroup');
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	for ( id in dataElementGroups )
	{
		list.add( new Option( dataElementGroups[id], id ), null );
	}
}

// ===============================================================================
// Get DataElements by Group
// ===============================================================================

function getDataElementsByGroup( )
{		
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataElementsByGroupCompleted );
    var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + byId("dataElementGroup").value;
	request.send( url );	
}

function getDataElementsByGroupCompleted( xmlObject ){

	var dataElementList = byId( "availableDataElements" );
		
	dataElementList.options.length = 0;
	
	var dataelements = xmlObject.getElementsByTagName( "dataElement" );
	
	for ( var i = 0; i < dataelements.length; i++)
	{
		var id = dataelements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var elementName = dataelements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = id ;
		option.text = elementName;
		dataElementList.add( option, null );	
	}
}

// ===============================================================================
// Get OptionCombos by DataElement
// ===============================================================================

function getOptionCombos(){
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getOptionCombosReceived);
	request.send( "getOptionCombos.action?dataElementId=" + byId("availableDataElements").value );	
}

function getOptionCombosReceived(xmlObject){
	
	xmlObject = xmlObject.getElementsByTagName('categoryOptions')[0];		
	var optionComboList = byId( "optionCombos" );			
	optionComboList.options.length = 0;		
	var optionCombos = xmlObject.getElementsByTagName( "categoryOption" );		
	for ( var i = 0; i < optionCombos.length; i++)
	{
		var id = optionCombos[ i ].getAttribute('id');
		var name = optionCombos[ i ].firstChild.nodeValue;			
		var option = document.createElement( "option" );
		option.value = id ;
		option.text = name;
		optionComboList.add( option, null );	
	}
}

// ===============================================================================
// Insert dataelement's id into the Formular textbox
// ===============================================================================

function insertDataElementId(){
	var dataElementComboId = "[" + byId("availableDataElements").value + "." + byId("optionCombos").value + "]";
	byId("formula").value += dataElementComboId;
}

// ===============================================================================
// Insert operators into the Formular textbox
// ===============================================================================

function insertOperation(target, value ){
	byId(target).value += value;
}

// ===============================================================================
// Copy selected items Form
// ===============================================================================

function copySelectedExcelItemForm() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( copySelectedExcelItemFormReceived);
	request.send( "getAllExcelItemGroup.action" );
}

function copySelectedExcelItemFormReceived(xmlObject){
	
	var groups = xmlObject.getElementsByTagName("excelitemgroup");
	var selectList = document.getElementById("targetExcelItemGroup");
	var options = selectList.options;
	options.length = 0;
	for(i=0;i<groups.length;i++){
		var id = groups[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = groups[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	$("#copyToExcelItem").showAtCenter( true );
}

// ===============================================================================
// Validate copy Excel Items
// ===============================================================================

sheetId = 0;
reportItemIds = null;
excelItemsCurTarget = null;
excelItemsDuplicated = null;

function validateCopyExcelItems() {

	excelItemsCurTarget = new Array();
	excelItemsDuplicated = new Array();

	sheetId	= byId("targetExcelItemGroupSheetNo").value;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateCopyExcelItemsReceived);
	request.send( "getExcelItemsByGroup.action?excelItemGroupId=" + byId("targetExcelItemGroup").value + "&sheetNo=" + sheetId);
	
}

function validateCopyExcelItemsReceived(xmlObject){
	
	var items = xmlObject.getElementsByTagName('excelitem');
	
	for (var i = 0 ;  i < items.length ; i ++) {
	
		excelItemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedItems();
	
	saveCopyExcelItems();
}

function splitDuplicatedItems() {

	var flag = -1;
	var reportItemsChecked = new Array();
	var listRadio = document.getElementsByName('reportItemCheck');
	
	reportItemIds = null;
	reportItemIds = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
		if ( listRadio.item(i).checked ) {
			reportItemsChecked.push( listRadio.item(i).getAttribute("reportItemID") + "#" + listRadio.item(i).getAttribute("reportItemName"));
		}
	}
	
	for (var i in reportItemsChecked)
	{
		flag = i;
		
		for (var j in excelItemsCurTarget)
		{
			if ( reportItemsChecked[i].split("#")[1] == excelItemsCurTarget[j] )
			{
				flag = -1;
				excelItemsDuplicated.push( reportItemsChecked[i].split("#")[1] );
				break;
			}
		}
		
		if ( flag != -1 )
		{
			reportItemIds.push( reportItemsChecked[i].split("#")[0] );
		}
	}
}

function saveCopyExcelItems() {
	
	var excelItemsDuplicatedList = '';
	
	if (excelItemsDuplicated.length > 0) {
	
		excelItemsDuplicatedList = "Sheet [" + sheetId + "] - " + i18n_copy_items_duplicated + "<br>";
		
		for (var i in excelItemsDuplicated) {
		
			excelItemsDuplicatedList += "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;" + excelItemsDuplicated[i] + "<br>";
		}
	
	}
	
	if (reportItemIds.length > 0) {
	
		var request = new Request();
    	request.setResponseTypeXML( 'xmlObject' );
    	var url = "copyExcelItems.action?excelItemGroupId=" + byId("targetExcelItemGroup").value;
    	url += "&sheetNo=" + byId('targetExcelItemGroupSheetNo').value;
    	
    	for (var i=0; i<reportItemIds.length; i++) {
    		url += "&reportItemIds=" + reportItemIds[i];
    	}
    	
		request.send( url );	
	
	}
	
	if(reportItemIds.length == 0){
		setMessage( excelItemsDuplicatedList );
	}else{
		if (excelItemsDuplicated.length > 0)
			 excelItemsDuplicatedList += "<br>==========<br>" + i18n_copy_successful;
		else
			excelItemsDuplicatedList += i18n_copy_successful;
		setMessage( excelItemsDuplicatedList);
	}
	
	$("#copyToExcelItem").hide();
	deleteDivEffect();
}