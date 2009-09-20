function getPeriods() {
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;

    if ( periodTypeId != null ) {
        var url = "../dhis-web-commons-ajax/getPeriodsForLock.action?name=" + periodTypeId;
        $.ajax({
            url: url,
            cache: false,
            success: function(response){
                dom = parseXML(response);
                $( '#periodId >option' ).remove();
                $(dom).find('period').each(function(){
                    $('#periodId').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                });
                document.getElementById( "periodId" ).disabled = false;
            }
        });
    }
}

function parseXML( xml ) {
    if( window.ActiveXObject && window.GetObject ) {
        var dom = new ActiveXObject( 'Microsoft.XMLDOM' );
        dom.loadXML( xml );
        return dom;
    }
    if( window.DOMParser )
        return new DOMParser().parseFromString( xml, 'text/xml' );
    throw new Error( 'No XML parser available' );
}

function getDataSets() {
    var periodList = document.getElementById( "periodId" );
    var periodId = periodList.options[ periodList.selectedIndex ].value;

    if ( periodId != null ) {
        var url = "getDataSetsForOrguntwiseLocking.action?periodId=" + periodId;
        $.ajax({
            url:url,
            cache: false,
            success: function(response){
                $( '#unlockedDataSets >option' ).remove();
                $( '#lockedDataSets >option' ).remove();
                $(response).find('dataSet').each(function(){
                    if($(this).find('locked').text() == 'false'){
                        $('#unlockedDataSets').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                    }
                    else {
                        $('#lockedDataSets').append("<option value="+$(this).find('id').text()+">" +$(this).find('name').text()+ "</option>");
                    }
                });            
                document.getElementById( "unlockedDataSets" ).disabled = false;
                document.getElementById( "lockedDataSets" ).disabled = false;
                LoadEmptyOrgUnitTree();
            }
        });
    }
}
    
function cancilSelection() {    
     window.location.href = "displayLockingForm.action";   
}

function updateDataSets() {
    if ( validateLocking() )  {
        selectAllById( "lockedDataSets" );
        selectAllById( "unlockedDataSets" );      
        document.getElementById( "lockingForm" ).submit();
     }
}

function LoadOrgUnitTree() {   
	 desableLockOptionButtons();    
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;
          
     iframeForOUTree.location.href='orgunitWiseSetupAssociationsTree.action?selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;		 
}

function LoadEmptyOrgUnitTree(){  
	 desableLockOptionButtons();              
     iframeForOUTree.location.href='emptyOrgunitSetupAssociationsTree.action';		 
}

function enableLockOptionButtons(){
	 parent.document.getElementById( "submitButton1" ).disabled = false;
	 parent.document.getElementById( "submitButton2" ).disabled = false;
	 parent.document.getElementById( "submitButton3" ).disabled = false;
     parent.document.getElementById( "submitButton4" ).disabled = false; 
	 parent.document.getElementById( "levelList" ).disabled = false;
	//document.getElementById( "submitButton" ).disabled = false;
    //document.getElementById( "submitButton5" ).disabled = false;
}
		
function desableLockOptionButtons(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
    parent.document.getElementById( "submitButton2" ).disabled = true;
    parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
	parent.document.getElementById( "levelList" ).disabled = true;
	//document.getElementById( "submitButton" ).disabled = true;
	//document.getElementById( "submitButton5" ).disabled = true; 
}
		
function desableLockOptionButtonsForApplyLockOnAll(){
	parent.document.getElementById( "submitButton2" ).disabled = true;
	parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
	parent.document.getElementById( "levelList" ).disabled = true;
    //document.getElementById( "submitButton" ).disabled = true;
	//document.getElementById( "submitButton5" ).disabled = true; 
}
		
function desableLockOptionButtonsForRemoveAllLocks(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
	parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
    parent.document.getElementById( "levelList" ).disabled = true;
	//document.getElementById( "submitButton" ).disabled = true;
	//document.getElementById( "submitButton5" ).disabled = true; 
}
		
function desableLockOptionButtonsForLockAtLevel(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
	parent.document.getElementById( "submitButton2" ).disabled = true;
	parent.document.getElementById( "submitButton4" ).disabled = true; 
	parent.document.getElementById( "levelList" ).disabled = true;
	//document.getElementById( "submitButton" ).disabled = true;
	//document.getElementById( "submitButton5" ).disabled = true; 
}
		
function desableLockOptionButtonsForUnlockAtLevel(){
	parent.document.getElementById( "submitButton1" ).disabled = true;
	parent.document.getElementById( "submitButton2" ).disabled = true;
	parent.document.getElementById( "submitButton3" ).disabled = true;
	parent.document.getElementById( "levelList" ).disabled = true;
	//document.getElementById( "submitButton" ).disabled = true;
	//document.getElementById( "submitButton5" ).disabled = true; 
}
    
function ApplyAll(){
     desableLockOptionButtonsForApplyLockOnAll();
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;           
     iframeForOUTree.location.href ='selectAll.action?selectedLockedDataSetId=' + selectedLockedDataSetId;
}
    
function RemoveAll(){
	 desableLockOptionButtonsForRemoveAllLocks();       
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;       
     iframeForOUTree.location.href='unselectAll.action?selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}
    
function lockAllAtLevel(){
	 desableLockOptionButtonsForLockAtLevel();
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var list = document.getElementById( 'levelList' );         
     var level = list.options[ list.selectedIndex ].value; 
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;           
     iframeForOUTree.location.href ='selectLevel.action?level=' + level + '&selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}

function unLockAllAtLevel(){
     desableLockOptionButtonsForUnlockAtLevel();
     var periodList = document.getElementById( "periodId" );
     var periodId = periodList.options[ periodList.selectedIndex ].value;
     var list = document.getElementById( 'levelList' );         
     var level = list.options[ list.selectedIndex ].value;
     var lockedDataSetList = document.getElementById('lockedDataSets');
     var selectedLockedDataSetId = lockedDataSetList.options[ lockedDataSetList.selectedIndex ].value;
     iframeForOUTree.location.href = 'unselectLevel.action?level=' + level + '&selectedLockedDataSetId=' + selectedLockedDataSetId + '&periodId=' + periodId;
}

function updateDataSetsOrgunitwise(){
     if ( validateLocking() )  {
        selectAllById( "unlockedDataSets" );       
        document.getElementById( "lockingForm" ).submit();            
     }
}
    
function validateLocking(){
    if ( getListValue( "periodTypeId" ) == "null" ) {
        setMessage( i18n_select_a_period_type );
        return false;
    }

    if ( getListValue( "periodId" ) == "null" ) {
        setMessage( i18n_select_a_period );
        return false;
    }
    return true;
}