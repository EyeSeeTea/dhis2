trackerCapture.controller('ProfileController',
        function($rootScope,       
                $scope,
                $location,
                ExpressionService,
                storage,                
                CurrentSelection,
                AttributesFactory) {

    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;  
    
    //profile view mode
    $scope.minimal = true; 
    
    //selections
    $scope.pregnantWomanId = null;
    $scope.selectedProgramId = null;
    
    $scope.pregnantWomanId = ($location.search()).tei; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $rootScope.gestationalAge = {displayName: 'Gestational Age', value: 'UNKNOWN', code: 'UNKNOWN'};
    
    $scope.$on('selectedEntity', function(event, args) {  
        var selections = CurrentSelection.get();                  
        $scope.pregnantWoman = selections.tei;     
        $scope.contactPerson = selections.contact ? selections.contact : null;
        $scope.selectedProgram = selections.pr;

        if(args.forGa){            
            //get gestational age for the selected person
            ExpressionService.getGestationalAge($scope.pregnantWoman, $scope.selectedOrgUnit, $scope.selectedProgram).then(function(data){
                $scope.gestationalAge = data;
            });
        }
        else{
            AttributesFactory.formatPregnantWomanAttributes(selections.tei).then(function(pregnantWoman){
                $scope.pregnantWoman = pregnantWoman;
            });
            
            //get gestational age for the selected person
            ExpressionService.getGestationalAge($scope.pregnantWoman, $scope.selectedOrgUnit, $scope.selectedProgram).then(function(data){
                $scope.gestationalAge = data;
            });
        }
    });    
 
    $scope.cancel = function(){
        $scope.pregnantWoman.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };   
       
    $scope.personDetails = function() {        
        $scope.minimal = !$scope.minimal;
        if(angular.isObject( $scope.pregnantWoman.relationships) && $scope.contactPerson){
            
            $scope.contact = $scope.pregnantWoman.relationships[0].displayName;
            
            AttributesFactory.formatContactPersonAttributes($scope.contactPerson).then(function(contactPerson){
                $scope.contactPerson = contactPerson;
            });
        }        
    };
});
