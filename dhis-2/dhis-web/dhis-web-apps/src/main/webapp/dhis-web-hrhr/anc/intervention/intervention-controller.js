//Controller for interventions
trackerCapture.controller('InterventionsController',
        function($scope,
                CurrentSelection,
                TranslationService,
                DHIS2EventFactory,
                storage,
                TransferHandler,
                ExpressionService,
                DialogService,
                orderByFilter) {

    //do translation of the registration page
    TranslationService.translate();      
   
    $scope.$on('sharedData', function(event, args) {
       
        //pick selected orgUnit and program
        var selectedOrgUnit = storage.get('SELECTED_OU');
        var selectedProgram = storage.get('SELECTED_PROGRAM');
        
        //get current event where data is being recorded
        //(or else the latest event in the case of reports)
        $scope.currentEvent = args.currentEvent;
        $scope.depSection = args.depSection;
        var selections = CurrentSelection.get(); 

        //Fetch available events for the selected person
        DHIS2EventFactory.getByEntity(selections.tei, selectedOrgUnit, selectedProgram).then(function(data) {
            
            $scope.dhis2Events = data;   
               
            $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
            $scope.dhis2Events.reverse();            
            
            //Execute actions of interventions
            var con = [], rem = [];                      

            angular.forEach($scope.currentEvent.dataValues, function(dataValue){ 
                
                var de = storage.get(dataValue.dataElement);                
                var actualValue = dataValue.value;
                
                if(angular.isObject(de)){                    
                    
                    if( de.type == 'string'){
                        actualValue = '"' + dataValue.value + '"';
                    }
                    
                    angular.forEach(de.actions, function(eiAction) {                           

                        var val = eiAction.value.replace(new RegExp('#' + de.code + '#', 'g'), actualValue);
                
                        //check if the expression contains some varibales
                        if (val.indexOf('#') != -1) {                            
                            //format the expression, replace varibales with actual value
                            //when replacing value, track back from the latest one.
                            val = ExpressionService.getDataElementExpression(val, $scope.dhis2Events);
                        }              

                        //make sure the expression has no variables - but values
                        if (val.indexOf('#') != -1) {
                            //if the expression still contains some varibales - this means 
                            //the expression requires values which are not yet collected.
                            var dialogOptions = {
                                headerText: 'intervention_error',
                                bodyText: 'intervention_error_text'
                            };
                            DialogService.showDialog({}, dialogOptions);
                            return;                            
                        }
                        else{
                            if ($scope.$eval(val)) {
                                TransferHandler.store(eiAction.task.conditionsComplications, de.code, dataValue.value, con);                                
                                TransferHandler.store(eiAction.task.reminders, de.code, dataValue.value, rem);
                            }                        
                        }
                    });                                      
                }                            
            });            
            $scope.interventions = {conResult: con, remResult: rem};            
        });        
    });   
});