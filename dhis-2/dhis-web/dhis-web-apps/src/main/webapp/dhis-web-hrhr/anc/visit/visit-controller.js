//Controller for managing visits
trackerCapture.controller('VisitController',
        function($rootScope,
                $scope,
                $timeout,
                $location,
                DateFormatService,
                InterventionService,
                CurrentSelection,
                TEIService,
                ProgramStageFactory,
                DHIS2EventFactory,
                orderByFilter,
                $filter,
                storage) {
  
    
    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');
    
    //by default editing is disabled
    $scope.editingDisabled = true;
    
    $scope.otherPleaseSpecify = false;  
    $scope.previousPregnancy = {exists: false, count: 0};
    
    $scope.MMD_OBS = 'MMD_OBS';
    $scope.MMD_OBS_PAR = 'MMD_OBS_PAR';
    $scope.MMD_OBS_OUT = 'MMD_OBS_OUT';
    $scope.MMD_OBS_DEL = 'MMD_OBS_DEL';
    $scope.MMD_OBS_GRA_DAT = 'MMD_OBS_GRA_DAT';
    
    //by default use datepicker, on change and what is selected from there is always valid
    $scope.invalidDate = false;
    
    //get the selected event and person
    $scope.eventUid = ($location.search()).event;   
    $scope.selectedEntityId = ($location.search()).tei;
    $scope.selectedEnrollment = ($location.search()).enrollment;
    
    $scope.dhis2Events = [];
    $scope.currentEvent = '';
    $scope.person = '';
    $scope.sections = []; 
    $scope.depSection = {name: 'Management', dataElements: []}; 
    $scope.programStage = '';    
    
    if($scope.selectedEntityId && $scope.selectedOrgUnit && $scope.eventUid && $scope.selectedProgram){

        //Fetch the selected entity
        TEIService.get($scope.selectedEntityId).then(function(tei){     

            $scope.pregnantWoman = tei;
            if(!angular.isUndefined($scope.pregnantWoman.relationships)){
                TEIService.get($scope.pregnantWoman.relationships[0].trackedEntityInstanceB).then(function(contact){
                    
                    CurrentSelection.set({tei: tei, contact: contact, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
                    $timeout(function() { 
                        $rootScope.$broadcast('selectedEntity', {});
                        $rootScope.$broadcast('noteController', {});
                    }, 100);
                });
            }
            else{
                CurrentSelection.set({tei: tei, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
                $timeout(function() { 
                    $rootScope.$broadcast('selectedEntity', {});
                    $rootScope.$broadcast('noteController', {});
                }, 100);
            }
            
            //fetch events for the selected person
            DHIS2EventFactory.getByEntity($scope.pregnantWoman, $scope.selectedOrgUnit, $scope.selectedProgram).then(function(data) {   

                angular.forEach(data, function(ev){
                    ev.eventDate = DateFormatService.convertFromApi(ev.eventDate);                             
                    if(ev.event == $scope.eventUid){
                        $scope.currentEvent = ev;
                    }            
                    else{
                        $scope.dhis2Events.push(ev);
                    }
                });        

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();  

                //enable editing if event is less than 24 hours old.
                var millisecondsPerHour = 1000 * 60 * 60;
                var now = new Date();
                var diff = Math.floor((now - $scope.currentEvent.eventDate)/millisecondsPerHour);

                if( diff <= 24 ){
                    $scope.editingDisabled = false;
                }

                //Fetch program stage of the current event
                ProgramStageFactory.get($scope.currentEvent.programStage).then(function(data){
                    $scope.programStage = data;
                    $scope.sections = [];      

                    //get interventions from the current visit
                    var interventions = InterventionService.getResults([$scope.currentEvent]);     
                    angular.forEach($scope.programStage.programStageSections, function(section){            
                        
                        //fill dep section
                        if(section.name == 'Management'){                

                            if(angular.isObject(interventions.depResult)){

                                angular.forEach(interventions.depResult, function(de) {
                                    //associate data elements and their values
                                    de = storage.get(de);
                                    if(angular.isObject(de)){
                                        angular.forEach($scope.currentEvent.dataValues, function(dataValue){
                                            if(angular.isObject(de)){
                                                if(de.id == dataValue.dataElement){ 
                                                    de.value = dataValue.value;  

                                                    //associate labels and values (for custom values)
                                                    if( angular.isObject(de.optionSet) ){
                                                        angular.forEach(de.optionSet, function(option){
                                                            if(option.value == dataValue.value){
                                                                de.value = option.label;
                                                            }
                                                        });  
                                                    }                                
                                                }
                                            }                                            
                                        });                                                
                                        $scope.depSection.dataElements.push(de);
                                    }                                    
                                });                  
                            }                
                        } 
                        
                        //fill the other sections
                        else{                
                            var dataElements = [];
                            angular.forEach(section.programStageDataElements, function(de) {
                                //associate data elements and their values
                                de = storage.get(de.dataElement.id);
                                if(angular.isObject(de)){
                                    angular.forEach($scope.currentEvent.dataValues, function(dataValue){      

                                        if(de.id == dataValue.dataElement){ 
                                            de.value = dataValue.value;  

                                            //associate labels and values (for custom values)
                                            if( angular.isObject(de.optionSet) ){
                                                var customValueAssigned = false;
                                                angular.forEach(de.optionSet, function(option){
                                                    if(option.label == dataValue.value){
                                                        de.value = option.label;
                                                        customValueAssigned = true;
                                                    }     
                                                });  
                                                if(!customValueAssigned){
                                                    var option = {label: dataValue.value};
                                                    de.optionSet.push(option);
                                                }
                                            } 

                                            if( de.code == 'MMD_OBS_GRA'){
                                                if(de.value > 0){
                                                    $scope.pregnancyTableHeadings = [storage.get('MMD_OBS_GRA_DAT').name,
                                                                    storage.get('MMD_OBS_PAR').name,
                                                                    storage.get('MMD_OBS_OUT').name,
                                                                    storage.get('MMD_OBS_DEL').name
                                                                    ]
                                                    $scope.gra = []; $scope.par = []; $scope.out = []; $scope.del = [];
                                                    for(var j=0; j<de.value; j++){
                                                        $scope.gra[j] = storage.get('MMD_OBS_GRA_DAT');$scope.gra[j].type = 'date';
                                                        $scope.par[j] = storage.get('MMD_OBS_PAR');$scope.gra[j].type = 'date';
                                                        $scope.out[j] = storage.get('MMD_OBS_OUT');
                                                        $scope.del[j] = storage.get('MMD_OBS_DEL');
                                                    }
                                                    $scope.previousPregnancy = {exists: true, count: de.value};                                    
                                                }
                                                else{
                                                    $scope.previousPregnancy = {exists: false, count: 0};
                                                }                
                                            }
                                        }                
                                    });

                                    if( de.code == 'MMD_OBS'){
                                         $scope.mmdObs = de;
                                    }

                                    dataElements.push(de);  
                                }
                                         
                            });  
                            $scope.sections.push({name: section.name, dataElements: dataElements});
                        }            
                    });        

                    //get follow-up interventions
                    interventions = InterventionService.getResults($scope.dhis2Events); 
                    if(angular.isObject(interventions.depResult)){
                        angular.forEach(interventions.depResult, function(de) { 
                            $scope.depSection.dataElements.push(storage.get(de));
                        });                 
                    }
                    
                    //broadcast the selected event for others to pick
                    $rootScope.$broadcast('noteController', {currentEvent: $scope.currentEvent});
                    $rootScope.$broadcast('sharedData', {currentPerson: $scope.currentEvent.trackedEntityInstance, 
                                                         currentEvent: $scope.currentEvent,
                                                         depSection: $scope.depSection
                                                        });                    
                });                
            });   
        });
    }
    
    //save or update event data value
    $scope.currentDataElement = '';       
    $scope.saveValue = function(dataElement, intervention) {

        $scope.currentDataElement = dataElement.id;
        if( !angular.isUndefined(dataElement.value) ){

            if(dataElement.type == 'date' && dataElement.value != "") {
                var rawDate = $filter('date')(dataElement.value, 'dd.MM.yyyy'); 
                var convertedDate = moment(dataElement.value, 'DD.MM.YYYY')._d;
                convertedDate = $filter('date')(convertedDate, 'dd.MM.yyyy'); 
                
                if(rawDate !== convertedDate){
                    $scope.invalidDate = true;
                    return false;
                }                
                $scope.invalidDate = false;
            }
            
            if( dataElement.code == 'MMD_OBS_GRA'){
                if(dataElement.value > 0){
                    
                    $scope.pregnancyTableHeadings = [storage.get('MMD_OBS_GRA_DAT').name,
                                                    storage.get('MMD_OBS_PAR').name,
                                                    storage.get('MMD_OBS_OUT').name,
                                                    storage.get('MMD_OBS_DEL').name
                                                    ]
                    $scope.gra = []; $scope.par = []; $scope.out = []; $scope.del = [];
                    for(var j=0; j<dataElement.value; j++){
                        $scope.gra[j] = storage.get('MMD_OBS_GRA_DAT');$scope.gra[j].type = 'date';
                        $scope.par[j] = storage.get('MMD_OBS_PAR');$scope.gra[j].type = 'date';
                        $scope.out[j] = storage.get('MMD_OBS_OUT');
                        $scope.del[j] = storage.get('MMD_OBS_DEL');
                    }
                    $scope.previousPregnancy = {exists: true, count: dataElement.value};                    
                }
                else{
                    $scope.previousPregnancy = {exists: false, count: 0};
                }                
            }
            
            if( dataElement.type == 'string' && dataElement.value.indexOf('- please specify') != -1){
                $scope.otherPleaseSpecify = true;
                dataElement.value = '';
            }            
            
            else{                
                /*if($scope.otherPleaseSpecify && dataElement.type == 'string'){
                    var option = {label: dataElement.value};
                    dataElement.optionSet.push(option);
                }*/
                $scope.otherPleaseSpecify = false;
                
                if(angular.isUndefined($scope.currentEvent.dataValues)){
                    $scope.currentEvent.dataValues = [];
                }
                
                if( $scope.currentEvent.dataValues.length >= 1 ){

                    angular.forEach($scope.currentEvent.dataValues, function(dataValue){

                        if(dataValue.dataElement === dataElement.id){
                            dataValue.value = dataElement.value;    
                        }
                        else{
                            var eventDataValue = {value: dataElement.value, dataElement: dataElement.id};
                            $scope.currentEvent.dataValues.push(eventDataValue);
                        }
                    });               
                }
                else{
                    var eventDataValue = {value: dataElement.value, dataElement: dataElement.id};
                    $scope.currentEvent.dataValues.push(eventDataValue);
                }

                DHIS2EventFactory.update($scope.currentEvent).then(function(data){  

                    //immediately update gestational age
                    if(dataElement.code == 'MMD_LMP_DAT' || dataElement.code == 'MMD_GES_WK1' || dataElement.code == 'MMD_ULS_DAT'){
                        $timeout(function() { 
                            $rootScope.$broadcast('selectedEntity', {forGa: true});
                        }, 100);
                    }
                    
                    //immediately update interventions
                    $rootScope.$broadcast('sharedData', 
                                        {currentPerson: $scope.currentEvent.person, 
                                         currentEvent: $scope.currentEvent, 
                                         depSection: $scope.depSection, 
                                         intervention: intervention, 
                                         dataElement: dataElement
                                        });
                });                
            }           
        }       
    };    

    $scope.displayGuideline = function(dataElement) {  
        dataElement.showGuideline = !dataElement.showGuideline;
    };
    
    //save or update event data value - previous pregnancy       
    $scope.savePreviousPregnancy = function(dataElement, index, count, intervention) {        
        
        if( dataElement.code == 'MMD_OBS_GRA_DAT'){
            console.log('It is gra', $scope.gra[index]);
        }
        if( dataElement.code == 'MMD_OBS_PAR'){
            console.log('It is par', $scope.par[index]);
        }
        if( dataElement.code == 'MMD_OBS_OUT'){
            console.log('It is out', $scope.out[index]);
        }
        if( dataElement.code == 'MMD_OBS_DEL'){
            console.log('It is del', $scope.del[index]);
        }
        
        if(!angular.isUndefined(dataElement.value) ) {

            if(angular.isUndefined($scope.currentEvent.dataValues)){
                $scope.currentEvent.dataValues = [];
            }

            if( $scope.currentEvent.dataValues.length >= 1 ){

                angular.forEach($scope.currentEvent.dataValues, function(dataValue){

                    if(dataValue.dataElement === dataElement.id){
                        dataValue.value = dataElement.value;    
                    }
                    else{
                        var eventDataValue = {value: dataElement.value, dataElement: dataElement.id};
                        $scope.currentEvent.dataValues.push(eventDataValue);
                    }
                });               
            }
            else{
                var eventDataValue = {value: dataElement.value, dataElement: dataElement.id};
                $scope.currentEvent.dataValues.push(eventDataValue);
            }

            /*DHIS2EventFactory.update($scope.currentEvent).then(function(data){  

                $rootScope.$broadcast('sharedData', 
                                    {currentPerson: $scope.currentEvent.person, 
                                     currentEvent: $scope.currentEvent, 
                                     depSection: $scope.depSection, 
                                     intervention: intervention, 
                                     dataElement: dataElement
                                    });
            }); */                        
        }
               
    };    

});