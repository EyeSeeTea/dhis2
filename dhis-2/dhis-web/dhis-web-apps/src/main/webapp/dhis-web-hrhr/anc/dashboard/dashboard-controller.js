trackerCapture.controller('DashboardController',
        function($rootScope,
                $scope,
                $timeout,
                CurrentSelection,  
                DateFormatService,
                DHIS2EventFactory,
                EnrollmentService,
                TEIService,
                storage,
                $filter,
                InterventionService,
                $location,
                DialogService,
                orderByFilter) {
  
    
    $scope.dhis2Events = [];
    $scope.isFirstEvent = false;

    //selections  
    $scope.selectedEntity = null;
    $scope.selectedEntityId = ($location.search()).tei; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');
    
    //get dashboard for the selected entities
    if($scope.selectedEntityId && $scope.selectedProgram && $scope.selectedOrgUnit){  
        //Fetch the selected entity
        TEIService.get($scope.selectedEntityId).then(function(tei){
            
            $scope.selectedEntity = tei;
            $scope.selectedEnrollment = null;
            
            EnrollmentService.getByEntityAndProgram($scope.selectedEntity.trackedEntityInstance, $scope.selectedProgram.id, 'ACTIVE').then(function(data){
                if(data.enrollments.length === 1){        
                    

                    //Fetch available events for the selected person
                    DHIS2EventFactory.getByEntity($scope.selectedEntity, $scope.selectedOrgUnit, $scope.selectedProgram).then(function(data) {
                            
                        $scope.dhis2Events = data;        
                        $scope.selectedEnrollment = data.enrollments[0].enrollment;                    
                        if(!angular.isUndefined($scope.selectedEntity.relationships)){
                            TEIService.get($scope.selectedEntity.relationships[0].trackedEntityInstanceB).then(function(contact){
                                CurrentSelection.set({tei: tei, contact: contact, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment, event: $scope.dhis2Events});
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
                        if(angular.isUndefined($scope.dhis2Events)){
                            $scope.isFirstEvent = true;
                        }
                        else{            
                            $scope.isFirstEvent = false;    

                            angular.forEach($scope.dhis2Events, function(dhis2Event){                        
                                dhis2Event.eventDate = DateFormatService.convertFromApi(dhis2Event.eventDate);
                            });

                            $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                            $scope.dhis2Events.reverse();                 
                            $scope.interventions = InterventionService.getResults($scope.dhis2Events);
                        }                
                    });            
                }
            });                        
        });        
    }   
    
    //create new ANC visit
    $scope.createNewANCVisit = function() {      
        
        if($scope.selectedEntityId && $scope.selectedProgram && $scope.selectedOrgUnit){
            $scope.selectedProgramStage = $scope.selectedProgram.programStages[0]; 
            if ( !$scope.isFirstEvent ) {
                angular.forEach($scope.selectedProgram.programStages, function(programStage){                    
                    if( programStage.repeatable ){
                        $scope.selectedProgramStage = programStage;
                    }//if no repeatable is found, program stage is set to first stage
                });
            }
        }              

        $scope.DHIS2Event = {program: $scope.selectedProgram.id,
            programStage: $scope.selectedProgramStage.id,
            orgUnit: $scope.selectedOrgUnit.id,
            status: 'ACTIVE',
            notes: [],
            trackedEntityInstance: $scope.selectedEntityId,
            eventDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            dataValues: []
        };
        
        DHIS2EventFactory.create($scope.DHIS2Event).then(function(data) {
            if (data.importSummaries[0].status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'consultation_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                $scope.DHIS2Event.event = data.importSummaries[0].reference;               
                $scope.DHIS2Event.name = $scope.selectedProgramStage.name;
                storage.set(data.importSummaries[0].reference, $scope.DHIS2Event);                  
               
                $location.path('/visit').search({tei: $scope.selectedEntityId, eventUid: data.importSummaries[0].reference});                
            }
        });
    };
    
    $scope.cancel = function() {
        $location.path('/anc');
    };
});