'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($rootScope,
                $scope,
                ModalService,
                $location,
                Paginator,
                TranslationService, 
                storage,
                EIFactory,
                ProgramFactory,
                ProgramStageFactory,
                AttributesFactory,
                EntityQueryFactory,
                ContextMenuSelectedItem,
                CurrentSelection,
                TEIService) {   
   
    //Selection
    $scope.selectedOrgUnit = '';
    $scope.selectedProgram = '';
    $scope.ouModes = [
                    {name: 'SELECTED', id: 1}, 
                    {name: 'CHILDREN', id: 2}, 
                    {name: 'DESCENDANTS', id: 3}
                  ];                  
    $scope.ouMode = $scope.ouModes[0];
    
    //Paging
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;   
    
    //EntityList
    $scope.showTrackedEntityDiv = false;
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchText = null;
    $scope.emptySearchText = false;
    $scope.searchFilterExists = false;    
    
    $scope.searchMode = { 
                            listAll: 'LIST_ALL', 
                            freeText: 'FREE_TEXT', 
                            attributeBased: 'ATTRIBUTE_BASED'
                        };
    
    //Registration
    $scope.showRegistrationDiv = false;

    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {           

        if( angular.isObject($scope.selectedOrgUnit)){   
            
            storage.set('SELECTED_OU', $scope.selectedOrgUnit);
            
            $scope.trackedEntityList = [];
            $scope.selectedProgram = '';
            
            
            AttributesFactory.getLocalAttributes().then(function(localAttributes){
                storage.set('LOCAL_ATTRIBUTES', localAttributes);                
                //apply translation
                TranslationService.translate();

                $scope.loadPrograms($scope.selectedOrgUnit); 
            });
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {        

        $scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;
        
        if (angular.isObject($scope.selectedOrgUnit)) {   

            ProgramFactory.getAll().then(function(programs){
                $scope.programs = [];
                angular.forEach(programs, function(program){                            
                    if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id)){                                
                        $scope.programs.push(program);
                    }
                });
                
                if(angular.isObject($scope.programs) && $scope.programs.length === 1){
                    $scope.selectedProgram = $scope.programs[0];
                    
                    storage.set('SELECTED_PROGRAM', $scope.selectedProgram);
                    
                    //Load EIs
                    EIFactory.getEI().then(function(data) {
                        angular.forEach(data, function(ei) {
                            storage.set(ei.code, ei);
                        });
                    
                        //save program stage dataelements
                        angular.forEach($scope.selectedProgram.programStages, function(pst){

                            ProgramStageFactory.get(pst.id).then(function(data){
                                var programStage = data; 
                                angular.forEach(programStage.programStageDataElements, function(prDataElement) {                            
                                    var de = storage.get(prDataElement.dataElement.code);
                                    if(angular.isObject(de)){
                                        de.id = prDataElement.dataElement.id;
                                        de.name = prDataElement.dataElement.name;
                                        de.description = prDataElement.dataElement.description;
                                        storage.set(de.id, de);
                                        storage.set(de.code, de); 
                                    }                                                               
                                });                        
                            });  
                        });
                                        
                        AttributesFactory.getAttributesForPregnantWoman().then(function(atts){
                            $scope.attributes = atts;
                            $scope.search($scope.searchMode.listAll);
                        });
                    });
                }                
            });
        }        
    };
    
    $scope.search = function(mode){ 

        $scope.emptySearchText = false;
        $scope.emptySearchAttribute = false;
        $scope.showSearchDiv = false;
        $scope.showRegistrationDiv = false;                          
        $scope.trackedEntityList = null; 
        
        var queryUrl = null, 
            programUrl = null, 
            attributeUrl = {url: null, hasValue: false};
    
        if($scope.selectedProgram){
            programUrl = 'program=' + $scope.selectedProgram.id;
        }        
        
        //check search mode
        if( mode === $scope.searchMode.freeText ){     
            if(!$scope.searchText){                
                $scope.emptySearchText = true;
                return;
            }       
            
            $scope.showTrackedEntityDiv = true;      
            queryUrl = 'query=' + $scope.searchText;                     
        }
        else if( mode === $scope.searchMode.attributeBased ){
            $scope.showTrackedEntityDiv = true;                  
            attributeUrl = EntityQueryFactory.getQueryForAttributes($scope.attributes);
            
            if(!attributeUrl.hasValue && !$scope.selectedProgram){
                $scope.emptySearchAttribute = true;
                $scope.showSearchDiv = true;
                return;
            }
        }
        else if( mode === $scope.searchMode.listAll ){   
            $scope.showTrackedEntityDiv = true;    
        }      
        
        $scope.generateGridColumns($scope.attributes);

        //get events for the specified parameters
        TEIService.search($scope.selectedOrgUnit.id, 
                                            $scope.ouMode.name,
                                            queryUrl,
                                            programUrl,
                                            attributeUrl.url).then(function(data){
            $scope.trackedEntityList = data;            
        });
    };
    
    //generate grid columns from teilist attributes
    $scope.generateGridColumns = function(attributes){
        $scope.gridColumns = [];        
        var atts = {};
        angular.forEach(attributes, function(att){
            atts[att.code] = att;
        });        
        AttributesFactory.getLocalAttributes().then(function(localAttributes){ 
            
            angular.forEach(localAttributes.pregnantWoman, function(localAttribute){   
                var att = atts[localAttribute.code];                    
                $scope.gridColumns.push({id: att.id, name: att.name, type: att.valueType, show: true});
            });       
            return $scope.gridColumns; 
        });               
    };
    
    $scope.jumpToPage = function(){
        $scope.search($scope.searchMode.listAll);
    };
    
    $scope.clearEntities = function(){
        $scope.trackedEntityList = null;
    };
    
    $scope.showSearch = function(){   
        $scope.showSearchDiv = !$scope.showSearchDiv;
        $scope.showRegistrationDiv = false;
        $scope.selectedProgram = '';
        $scope.emptySearchAttribute = false;
    };
    
    $scope.hideSearch = function(){        
        $scope.showSearchDiv = false;
        $rootScope.showAdvancedSearchDiv = false;
    };
    
    $scope.closeSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };
    
    $scope.showDashboard = function(){
        var tei = ContextMenuSelectedItem.getSelectedItem();  
        CurrentSelection.set({tei: tei, pr: $scope.selectedProgram ? $scope.selectedProgram: null});
        $location.path('/dashboard').search({tei: tei.id});                                    
    };
    
    $scope.showRegistration = function(){        
        $location.path('/registration').search({});  
    }; 
    
    $scope.editPersonProfile = function(){
        var tei = ContextMenuSelectedItem.getSelectedItem();  
        CurrentSelection.set({tei: tei, pr: $scope.selectedProgram ? $scope.selectedProgram: null});
        $location.path('/registration').search({tei: tei.id});                                    
    };
    
    $scope.removePerson = function(){
        var tei = ContextMenuSelectedItem.getSelectedItem();  
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'remove',
            headerText: 'remove',
            bodyText: 'are_you_sure_to_remove'
        };

        ModalService.showModal({}, modalOptions).then(function(result){

            TEIService.delete(tei.id).then(function(data){
                
                var continueLoop = true, index = -1;
                for(var i=0; i< $scope.trackedEntityList.length && continueLoop; i++){                    
                    if($scope.trackedEntityList[i].id === tei.id ){                        
                        continueLoop = false;
                        index = i;
                    }
                }
                $scope.trackedEntityList.splice(index,1);
            });
        });                               
    };
       
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };    
})

//Controller for the header section
.controller('HeaderController',
        function($scope,                
                DHIS2URL,
                TranslationService) {

    TranslationService.translate();
    
    $scope.home = function(){        
        window.location = DHIS2URL;
    };    
});