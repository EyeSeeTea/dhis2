trackerCapture.controller('ProfileController',
        function($scope,       
                $location,
                storage,
                TEIService,
                DialogService,
                CurrentSelection,
                AttributesFactory,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;  
    
    //selections
    $scope.selectedEntityId = null;
    $scope.selectedProgramId = null;
    
    $scope.selectedEntityId = ($location.search()).tei; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    
     $scope.$on('selectedEntity', function(event, args) {  
        
        var selections = CurrentSelection.get();                  
        $scope.selectedEntity = selections.tei;      
        $scope.selectedProgram = selections.pr;     
        
        console.log('the tei is:  ', $scope.selectedEntity);
        
        AttributesFactory.getAll().then(function(atts){
            angular.forEach(atts, function(att){
                $scope.attributes[att.id] = att;
                $scope.attributes[att.code] = att;
            }); 
            $scope.processTeiAttributes();
        });
      
    });       

    
    //display only those attributes that belong the selected program
    //if no program, display attributesInNoProgram
    $scope.processTeiAttributes = function(){
       
        angular.forEach(storage.get('TRACKED_ENTITIES'), function(te){
            if($scope.selectedEntity.trackedEntity === te.id){
                $scope.trackedEntity = te;
            }
        });
        
        angular.forEach($scope.selectedEntity.attributes, function(att){
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
                angular.forEach($scope.selectedEntity.attributes, function(attribute){
                    if(attribute.attribute === newAttribute.attribute){
                        newAttribute.value = attribute.value;
                    }                               
                });                                            
                newAttributes.push(newAttribute);
            }); 

            $scope.selectedEntity.attributes = newAttributes;                

            for(var i=0; i<$scope.selectedEntity.attributes.length; i++){
                $scope.selectedEntity.attributes[i].show = false;
                var processedForDisplay = false;
                for(var j=0; j<newAttributes.length && !processedForDisplay; j++){
                    if($scope.selectedEntity.attributes[i].attribute === newAttributes[j].attribute){
                        processedForDisplay = true;
                        $scope.selectedEntity.attributes[i].show = true;
                    }
                }                                   
            }
        }); 
    };
    
    $scope.enableEdit = function(){
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
        $scope.editProfile = !$scope.editProfile; 
    };
    
    $scope.save = function(){
        
        var tei = angular.copy($scope.selectedEntity);
        tei.attributes = [];
        
        //prepare to update the tei on the server side 
        angular.forEach($scope.selectedEntity.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value) && attribute.value !== ""){
                tei.attributes.push({attribute: attribute.attribute, value: attribute.value});
            } 
        });        

        TEIService.update(tei).then(function(updateResponse){
            
            if(updateResponse.status !== 'SUCCESS'){//update has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: updateResponse.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }            
        });
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedEntity.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});



/*trackerCapture.controller('ProfileController',
        function($scope,       
                $location,
                storage,
                ProgramFactory,
                TEIService,
                DialogService,
                CurrentSelection,
                AttributesFactory,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;  
    
    //selections
    $scope.selectedEntityId = null;
    $scope.selectedProgramId = null;
    
    $scope.selectedEntityId = ($location.search()).tei; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
    if( $scope.selectedEntityId ){
        
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei; 
        $scope.selectedProgram = selections.pr;         
        
        AttributesFactory.getAll().then(function(atts){
            angular.forEach(atts, function(att){
                $scope.attributes[att.id] = att;
                $scope.attributes[att.code] = att;
            }); 
            $scope.processTeiAttributes();
        });       
    }
    
    //display only those attributes that belong the selected program
    //if no program, display attributesInNoProgram
    $scope.processTeiAttributes = function(){
       
        angular.forEach(storage.get('TRACKED_ENTITIES'), function(te){
            if($scope.selectedEntity.trackedEntity === te.id){
                $scope.trackedEntity = te;
            }
        });
        
        angular.forEach($scope.selectedEntity.attributes, function(att){
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
                                    type: att.valueType,
                                    value: ''};
                angular.forEach($scope.selectedEntity.attributes, function(attribute){
                    if(attribute.attribute === newAttribute.attribute){
                        newAttribute.value = attribute.value;
                    }                               
                });                                            
                newAttributes.push(newAttribute);
            }); 

            $scope.selectedEntity.attributes = newAttributes;                

            for(var i=0; i<$scope.selectedEntity.attributes.length; i++){
                $scope.selectedEntity.attributes[i].show = false;
                var processedForDisplay = false;
                for(var j=0; j<newAttributes.length && !processedForDisplay; j++){
                    if($scope.selectedEntity.attributes[i].attribute === newAttributes[j].attribute){
                        processedForDisplay = true;
                        $scope.selectedEntity.attributes[i].show = true;
                    }
                }                                   
            }
        }); 
    };
    
    $scope.enableEdit = function(){
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
        $scope.editProfile = !$scope.editProfile; 
    };
    
    $scope.save = function(){
        
        var tei = angular.copy($scope.selectedEntity);
        tei.attributes = [];
        
        //prepare to update the tei on the server side 
        angular.forEach($scope.selectedEntity.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value) && attribute.value != ""){
                tei.attributes.push({attribute: attribute.attribute, value: attribute.value});
            } 
        });
        
        console.log('the new attribtues are:  ', tei);
        
        TEIService.update(tei).then(function(updateResponse){
            
            if(updateResponse.status !== 'SUCCESS'){//update has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: updateResponse.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }            
        });
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedEntity.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});*/
