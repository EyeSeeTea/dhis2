trackerCapture.controller('ProfileController',
        function($scope,       
                $location,
                $filter,
                orderByFilter,
                storage,
                DHIS2EventFactory,
                TEIService,
                DialogService,
                CurrentSelection,
                AttributesFactory,
                TranslationService) {

    TranslationService.translate();
    
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
    $scope.gestationalAge = {displayName: 'Gestational Age', value: 'UNKNOWN', code: 'UNKNOWN'};
    
    $scope.$on('selectedEntity', function(event, args) {  

        var selections = CurrentSelection.get();                  
        $scope.pregnantWoman = selections.tei;      
        $scope.selectedProgram = selections.pr;     

        AttributesFactory.getAll().then(function(atts){
            angular.forEach(atts, function(att){
                $scope.attributes[att.id] = att;
                $scope.attributes[att.code] = att;
            }); 
            $scope.processTeiAttributes();
        });
        
        //Fetch available events for the selected person
        DHIS2EventFactory.getByEntity($scope.pregnantWoman, $scope.selectedOrgUnit, $scope.selectedProgram).then(function(data) {
            $scope.dhis2Events = data;        

            if(!angular.isUndefined(data)){
                angular.forEach(data, function(dhis2Event){
                    dhis2Event.eventDate = moment(dhis2Event.eventDate, 'YYYY-MM-DD')._d;
                    dhis2Event.eventDate = Date.parse(dhis2Event.eventDate);
                    dhis2Event.eventDate = $filter('date')(dhis2Event.eventDate, 'yyyy-MM-dd');                    
                });

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();

                for(var i=0; i<$scope.dhis2Events.length; i++){
                    if(angular.isObject($scope.dhis2Events[i].dataValues)){
                        for(var j=0; j<$scope.dhis2Events[i].dataValues.length; j++){
                            var dv = $scope.dhis2Events[i].dataValues[j];

                            if(!angular.isUndefined(dv.dataElement)){                                
                                var de = storage.get(dv.dataElement);

                                if(angular.isObject(de)){
                                    //get gestational age - first try ultrasound
                                    if(de.code == 'MMD_GES_WK3'){
                                        $scope.gestationalAge.value = Math.floor( dv.value / 7 ) + '+' + dv.value % 7;
                                        $scope.gestationalAge.displayName = de.name + ' (weeks + days)';
                                        $scope.gestationalAge.code = de.code;                                
                                    }

                                    //if no ultrasound, try LMP
                                    if(de.code == 'MMD_LMP_DAT' && $scope.gestationalAge.code != 'MMD_GES_WK3'){

                                        var age = moment().diff(moment(dv.value, 'YYYY-MM-DD'),'days');               
                                        $scope.gestationalAge.value = Math.floor( age / 7 ) + '+' + age % 7;
                                        $scope.gestationalAge.displayName = storage.get('MMD_GES_WK2').name + ' (weeks + days)';
                                        $scope.gestationalAge.code = de.code;                                
                                    }

                                    //if no LMP, try clinical estimation
                                    if(de.code == 'MMD_GES_WK1' && $scope.gestationalAge.code != 'MMD_GES_WK3' && $scope.gestationalAge.code != 'MMD_LMP_DAT'){
                                        $scope.gestationalAge.value = Math.floor( dv.value / 7 ) + '+' + dv.value % 7;
                                        $scope.gestationalAge.displayName = de.name + ' (weeks + days)';
                                        $scope.gestationalAge.code = de.code;                                
                                    }
                                }                            
                            }                            
                        }
                    }                    
                }
            }
        });
    });       
    
    //display only those attributes that belong the selected program
    //if no program, display attributesInNoProgram
    $scope.processTeiAttributes = function(){
       
        angular.forEach(storage.get('TRACKED_ENTITIES'), function(te){
            if($scope.pregnantWoman.trackedEntity === te.id){
                $scope.trackedEntity = te;
            }
        });
        
        angular.forEach($scope.pregnantWoman.attributes, function(att){
            if(att.type === 'number' && !isNaN(parseInt(att.value))){
                att.value = parseInt(att.value);
            }
        });        
        
        AttributesFactory.getLocalAttributes().then(function(localAttributes){ 
                
            //assume every tei has values for the attributes - initially all are empty values
            var newAttributes = [];
            angular.forEach(localAttributes.pregnantWoman, function(localAttribute){   
                var att = $scope.attributes[localAttribute.code];                    
                var newAttribute = {attribute: att.id,
                                    code: att.code, 
                                    displayName: att.name, 
                                    mandatoryToDisplay: localAttribute.mandatoryToDisplay,
                                    type: att.valueType,
                                    value: ''};
                angular.forEach($scope.pregnantWoman.attributes, function(attribute){
                    if(attribute.attribute === newAttribute.attribute){
                        newAttribute.value = attribute.value;
                    }                               
                });                                            
                newAttributes.push(newAttribute);
            }); 

            $scope.pregnantWoman.attributes = newAttributes;                

            for(var i=0; i<$scope.pregnantWoman.attributes.length; i++){
                $scope.pregnantWoman.attributes[i].show = false;
                var processedForDisplay = false;
                for(var j=0; j<newAttributes.length && !processedForDisplay; j++){
                    if($scope.pregnantWoman.attributes[i].attribute === newAttributes[j].attribute){
                        processedForDisplay = true;
                        $scope.pregnantWoman.attributes[i].show = true;
                    }
                }                                   
            }
        }); 
    };
    
    $scope.cancel = function(){
        $scope.pregnantWoman.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };   
       
    $scope.personDetails = function(pregnantWoman) {        
        $scope.minimal = !$scope.minimal;
        if(angular.isObject( pregnantWoman.relationships) ){
            $scope.contact = pregnantWoman.relationships[0].displayName;
            
            PersonService.getContactPerson(pregnantWoman.relationships[0].person).then(function(contactPerson){
                $scope.contactPerson = contactPerson;
            });            
        }        
    };
});
