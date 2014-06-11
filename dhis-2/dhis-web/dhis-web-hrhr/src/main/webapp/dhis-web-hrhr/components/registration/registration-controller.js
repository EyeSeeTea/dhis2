trackerCapture.controller('RegistrationController', 
        function($scope,
                $location,
                $filter,
                AttributesFactory,
                TEIService,
                EnrollmentService,
                DialogService,
                storage,
                TranslationService) {

    //do translation of the registration page
    TranslationService.translate();   
    
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');
    $scope.selectedRelationship = storage.get('RELATIONSHIP_TYPES')[0];

    $scope.enrollment = {enrollmentDate: $filter('date')(new Date(), 'yyyy-MM-dd'), incidentDate: $filter('date')(new Date(), 'yyyy-MM-dd')};   
    
    if($scope.selectedProgram && $scope.selectedOrgUnit){
        
        AttributesFactory.getAttributesForPregnantWoman().then(function(attributes){            
            $scope.pregnantWomanAttributes = attributes;
        });
        
        AttributesFactory.getAttributesForContactPerson().then(function(attributes){            
            $scope.contactPersonAttributes = attributes;
        });
    }
    
    $scope.registerEntity = function(showDashboard){       
        
        if($scope.selectedProgram && $scope.selectedOrgUnit && $scope.selectedRelationship){
            //check for form validity
            $scope.outerForm.submitted = true;        
            if( $scope.outerForm.$invalid ){
                return false;
            }            
            
            //form is valid, continue the registration
            //get selected entity
            var selectedTrackedEntity = $scope.selectedProgram.trackedEntity.id;
            
            //get registration attributes for pregnant woman
            var pwAttributes = [];
            angular.forEach($scope.pregnantWomanAttributes, function(pwAttribute) {
                if (!angular.isUndefined(pwAttribute.value)) {
                    var attribute = {attribute: pwAttribute.id, value: pwAttribute.value};
                    if(pwAttribute.valueType === "date"){
                        attribute.value = moment(attribute.value, 'DD.MM.YYYY')._d;
                        attribute.value = Date.parse(attribute.value);            
                        attribute.value = $filter('date')(attribute.value, 'yyyy-MM-dd');    
                    }
                    pwAttributes.push(attribute);
                }
            });
            $scope.pregnantWoman = {trackedEntity: selectedTrackedEntity, attributes: pwAttributes, orgUnit: $scope.selectedOrgUnit.id};         

            //get registration attributes for contact person
            var cpAttributes = [];
            angular.forEach($scope.contactPersonAttributes, function(cpAttribute) {
                if (!angular.isUndefined(cpAttribute.value)) {
                    var attribute = {attribute: cpAttribute.id, value: cpAttribute.value};
                    if(cpAttribute.valueType === "date"){
                        attribute.value = moment(attribute.value, 'DD.MM.YYYY')._d;
                        attribute.value = Date.parse(attribute.value);            
                        attribute.value = $filter('date')(attribute.value, 'yyyy-MM-dd');    
                    }
                    cpAttributes.push(attribute);
                }
            });            
            $scope.contactPerson = {trackedEntity: selectedTrackedEntity, attributes: cpAttributes, orgUnit: $scope.selectedOrgUnit.id}; 

            //first register contact person (as it is needed for relationship)
            TEIService.register($scope.contactPerson).then(function(cp){

                if(cp.status === 'SUCCESS'){
                    
                    //assign relationship for pregnant woman
                    var relationship = [{relationship: $scope.selectedRelationship.id, trackedEntityInstance: cp.reference}];
                    $scope.pregnantWoman.relationships = relationship;
                    
                    //register pregnant woman
                    TEIService.register($scope.pregnantWoman).then(function(pw){
                        if(pw.status === 'SUCCESS'){
                            //enroll pregnant woman
                            var enrollment = {trackedEntityInstance: pw.reference,
                                        program: $scope.selectedProgram.id,
                                        status: 'ACTIVE',
                                        dateOfEnrollment: $scope.enrollment.enrollmentDate,
                                        dateOfIncident: $scope.enrollment.incidentDate
                                    };
                            EnrollmentService.enroll(enrollment).then(function(data){
                                if(data.status != 'SUCCESS'){
                                    //enrollment has failed
                                    console.log('the error is:  ', data.description);
                                    var dialogOptions = {
                                            headerText: 'enrollment_error',
                                            bodyText: data.description
                                        };
                                    DialogService.showDialog({}, dialogOptions);
                                    return;
                                }
                            });
                        }
                        else{
                            //registration has failed
                            var dialogOptions = {
                                    headerText: 'registration_error',
                                    bodyText: pw.description
                                };
                            DialogService.showDialog({}, dialogOptions);
                            return;
                        }
                        
                        if(showDashboard){
                            $location.path('/dashboard').search({tei: pw.reference});
                        }
                        else{
                            angular.forEach($scope.pregnantWomanAttributes, function(attribute){
                                attribute.value = ''; 
                            });

                            angular.forEach($scope.contactPersonAttributes, function(attribute){
                                attribute.value = ''; 
                            });
                        }                        
                    });
                }
                else{
                    //registration has failed
                    var dialogOptions = {
                            headerText: 'registration_error',
                            bodyText: cp.description
                        };
                    DialogService.showDialog({}, dialogOptions);
                    return;
                }
            });            
        }        
    };
    
    $scope.cancel = function(){
        $location.path('/anc');
    };
});