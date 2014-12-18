//Controller for dependency page
trackerCapture.controller('DependencyController',
        function($scope,
                DHIS2EventFactory,
                CurrentSelection,
                storage,
                TransferHandler,
                ExpressionService,
                DialogService,
                orderByFilter) {
 
   
    $scope.$on('sharedData', function(event, args) {
        
        //pick selected orgUnit and program
        var selectedOrgUnit = storage.get('SELECTED_OU');
        var selectedProgram = storage.get('SELECTED_PROGRAM');
        
        //get current event where data is being recorded
        //(or else the latest event in the case of reports)
        $scope.currentEvent = args.currentEvent;
        $scope.depSection = args.depSection;
        var selections = CurrentSelection.get(); 

        
        //get current event where data is being recorded
        //(or else the latest event in the case of reports)
        $scope.currentEvent = args.currentEvent;
        $scope.depSection = args.depSection;
        $scope.intervention = args.intervention;
        $scope.currentDataElement = args.dataElement;
        
        if(angular.isObject($scope.currentDataElement)){
            
            //Fetch available events for the selected person
            DHIS2EventFactory.getByEntity(selections.tei, selectedOrgUnit, selectedProgram).then(function(data) {

                $scope.dhis2Events = data;   

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();            

                //Execute actions of interventions
                var dep = [];

                $scope.depResult = '';               

                //var de = storage.get($scope.currentDataElement.dataElement);                
                var actualValue = $scope.currentDataElement.value;

                if(angular.isObject($scope.currentDataElement)){  

                    if( $scope.currentDataElement.type == 'string'){
                        actualValue = '"' + $scope.currentDataElement.value + '"';
                    }
                    angular.forEach($scope.currentDataElement.actions, function(eiAction) {                           

                        var val = eiAction.value.replace(new RegExp('#' + $scope.currentDataElement.code + '#', 'g'), actualValue);

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
                                TransferHandler.store(eiAction.task.dependencies, $scope.currentDataElement.code, $scope.currentDataElement.value, dep);                            
                            }                        
                        }
                    });                                 
                }
                //populate dep page
                if(angular.isObject($scope.depSection) && $scope.intervention ){
                    for(var i=0; i < dep.length; i++){
                        $scope.depSection.dataElements.push(storage.get(dep[i]));
                    }
                }
            });            
        }                
    });   
});