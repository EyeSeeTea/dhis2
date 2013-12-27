'use strict';

/* Controllers */
var trackerControllers = angular.module('trackerControllers', [])

//Controller for DHIS2 page
.controller('DHIS2Controller',
        function(CurrentUserProfile,                
                $translate) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });       
        
})

//Controller for home page
.controller('HomeController',
        function(CurrentUserProfile,
                $translate) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });   
})

//Controller for lab page
.controller('ANCLabController',
        function($scope,
                CurrentUserProfile,
                $location,
                $route,
                PersonService,
                DHIS2EventFactory,
                orderByFilter,
                storage,
                $translate) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    }); 
    
    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Pick selected person and fetch profile from server
    $scope.personUid = ($location.search()).personUid;    
    PersonService.getPerson($scope.personUid, $scope.selectedProgram.id).then(function(person){
        $scope.person = person;
    });
      
    //Get registered events for the selected person 
    DHIS2EventFactory.getDHIS2Events($scope.personUid, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data) {
        
        $scope.dhis2Events = data;           
         
        if(!angular.isUndefined($scope.dhis2Events)){
            
            //First identify data elements for lab reporting
            var dataElementIds = [];  
            angular.forEach($scope.dhis2Events, function(dhis2Event){
                
                var programStage = storage.get(dhis2Event.programStage);
                
                angular.forEach(programStage.programStageDataElements, function(pDe){
                    var de = storage.get(pDe.dataElement.id);                    
                    if(de.labResults){                        
                        if(dataElementIds.indexOf(de.id) == -1 ){
                            dataElementIds.push(de.id);
                        }                        
                    }                    
                }); 
            });
            
            //sort data elements based on their sequence on page
            $scope.dataElements = [];
            angular.forEach(dataElementIds, function(de){ 
                $scope.dataElements.push(storage.get(de));                         
            });            
            $scope.dataElements = orderByFilter($scope.dataElements, '-sequenceOnPage');
            $scope.dataElements.reverse();
            
            //prepare to populate lab values
            $scope.labValues = [];
            
            angular.forEach($scope.dhis2Events, function(dhis2Event){               
                
                angular.forEach(dataElementIds, function(de){
                    var vals = []; 
                    
                    //pick if there are previously mapped values
                    if($scope.labValues.hasOwnProperty(de)){ 
                        for(var i = 0; i< $scope.labValues[de].length; i++){
                            vals.push($scope.labValues[de][i]);
                        }                            
                    }
                    
                    //Initially assume value is empty for each lab data element                     
                    var labValue = { value: '', event: dhis2Event.event};     
                    
                    //if a data element has lab value - pick that value
                    angular.forEach(dhis2Event.dataValues, function(dataValue){
                        
                        if(dataValue.dataElement == de ){                            
                            labValue.value = dataValue.value; 
                    
                            //pick appropriate value - in case of custom test results
                            if( angular.isObject(storage.get(de).optionSet) ){
                                angular.forEach(storage.get(de).optionSet, function(option){
                                    if(option.value == dataValue.value){
                                        labValue.value = option.label;
                                    }
                                });  
                            }
                        }
                    });
                    
                    //store new lab value
                    vals.push(labValue);

                    //finally get lab values for html binding
                    $scope.labValues[de] = vals;
                });
                 
            }); 
            
            //generate tabular lab report 
            $scope.cells = [];
            $scope.rows = [];
            angular.forEach($scope.dataElements, function(de){ 
                var cell = $scope.labValues[de.id];
                $scope.rows.push(de.name);
                $scope.cells[de.name] = cell;                              
            });
        }        
    }); 

    $scope.close = function() {
        $location.path('/anc');
        $route.reload();
    };
})

//Controller for summary page
.controller('ANCSummaryController',
        function($scope,
                CurrentUserProfile,
                $location,
                $route,
                storage,
                PersonService,
                $translate) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });  

    $scope.summary = 'this is summary page';
    
    $scope.personUid = ($location.search()).personUid;  
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    PersonService.getPerson($scope.personUid, $scope.selectedProgram.id).then(function(person){
        $scope.person = person;
    });   

    $scope.close = function() {
        $location.path('/anc');
        $route.reload();
    };
})

//Controller for registration page
.controller('RegistrationController',
        function($scope,
                $location,
                $route,
                CurrentUserProfile,
                PersonAttributesFactory,
                PersonFactory,
                $translate,
                storage,
                DialogService) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });       
    
    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Get attributes for registration
    $scope.registrationAttributes = '';
    PersonAttributesFactory.getRegistrationAttributes().then(function(data) {        
        $scope.registrationAttributes = data.personAttributeTypes;
    });   

    $scope.saveAndRegisterPregnancy = function() {    
        
        $scope.outerRegistrationForm.submitted = true;

        if ($scope.outerRegistrationForm.$invalid) {
            return false;
        }           

        var attributes = [];
        angular.forEach($scope.registrationAttributes, function(registrationAttribute) {
            if (!angular.isUndefined(registrationAttribute.value)) {
                var attribute = {type: registrationAttribute.id, value: registrationAttribute.value};
                attributes.push(attribute);
            }
        });

        $scope.person.attributes = attributes;
        $scope.person.orgUnit = $scope.selectedOrgUnit.id;           

        PersonFactory.registerPerson($scope.person).success(function(data) {
            if (data.status == 'SUCCESS') {               
                $location.path('/enrollment').search({personUid: data.reference});
                $route.reload();
            }
            else { 
                $scope.registrationStatus = false;
                var dialogOptions = {
                    headerText: 'registration_error',
                    bodyText: data.description
                };
                DialogService.showDialog({}, dialogOptions);
            }
        });      
    };   
    
    $scope.cancel = function() {        
        $location.path('/anc');
        $route.reload();
    };

})

//Controller for person edit
.controller('PersonEditController',
        function($scope,
                $location,
                $route,
                CurrentUserProfile,
                $translate,
                PersonFactory,
                storage) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });  

    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Pick selected person and fetch profile from server
    $scope.personUid = ($location.search()).personUid;
    PersonFactory.getPerson($scope.personUid).success(function(person) {
       $scope.person = person;       
    });

    $scope.cancel = function() {
        $location.path('/anc');
        $route.reload();
    };
})

//Controller for enrollment
.controller('EnrollmentController',
        function($scope,
                $location,
                $route,
                CurrentUserProfile,
                $translate,
                ProgramFactory,
                PersonFactory,
                EnrollmentFactory,
                DialogService,
                $filter,
                storage) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });   

    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Pick selected person and fetch profile from server
    $scope.personUid = ($location.search()).personUid;
    $scope.person = '';
    PersonFactory.getPerson($scope.personUid).success(function(person) {
        $scope.person = person;
    });

    //Get enrollment program and required attributes
    ProgramFactory.getProgram($scope.selectedProgram.id).then(function(program) {
        $scope.program = program;

        angular.forEach(program.attributes, function(enrollmentAttribute) {
            if (angular.isObject(enrollmentAttribute.personAttributeOptions)) {
                angular.forEach(enrollmentAttribute.personAttributeOptions, function(attributeOption) {
                    attributeOption.value = attributeOption.name;
                });
            }
        });
    });


    $scope.enroll = function() {

        $scope.outerEnrollmentForm.submitted = true;

        if ($scope.outerEnrollmentForm.$invalid) {
            return false;
        }

        var attributes = [];       

        //Get enrollment attributes
        angular.forEach($scope.program.attributes, function(enrollmentAttribute) {
            if (!angular.isUndefined(enrollmentAttribute.value)) {
                var attribute = {
                    type: enrollmentAttribute.id,
                    value: enrollmentAttribute.value
                };
                attributes.push(attribute);
            }
        });       

        var enrollment = {person: $scope.person.person,
            program: $scope.program.id,
            status: 'ACTIVE',
            dateOfEnrollment: $filter('date')(new Date(), 'yyyy-MM-dd'),
            dateOfIncident: $filter('date')($scope.program.dateOfIncident, 'yyyy-MM-dd'),
            attributes: attributes
        };
        
        //Enroll person
        EnrollmentFactory.enrollPerson(enrollment).success(function(enrollment) {
            if (enrollment.status == 'SUCCESS') {
                
               $location.path('/anc/dashboard').search({personUid: $scope.person.person});       
               $route.reload();
            }
            else{
                var dialogOptions = {
                    headerText: 'enrollment_error',
                    bodyText: enrollment.description
                };

                DialogService.showDialog({}, dialogOptions);
            }
        });      
    };

    $scope.cancel = function() {
        $location.path('/anc');
        $route.reload();
    };
})

//Controller for settings page
.controller('SettingsController',
        function($scope,
                CurrentUserProfile,
                $translate,
                EIFactory,
                OrgUnitFactory,
                ProgramFactory,
                DialogService,
                storage) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });   

    //Get orgunits for the logged in user
    OrgUnitFactory.getMyOrgUnits().success(function(orgUnits) {
        $scope.orgUnits = orgUnits;
    });

    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selecteProgram = storage.get('SELECTED_PROGRAM');

    if (!angular.isObject($scope.selectedOrgUnit)) {
        $scope.selectedOrgUnit = {name: 'not_selected'};
    }

    if (!angular.isObject($scope.selecteProgram)) {
        $scope.selecteProgram = {label: 'not_selected'};
    }

    if (angular.isObject($scope.selectedOrgUnit)) {

        $scope.programs = [];

        ProgramFactory.getMyPrograms().then(function(data) {

            angular.forEach(data.organisationUnits, function(ou) {
                if (ou.id == $scope.selectedOrgUnit.id) {
                    angular.forEach(ou.programs, function(p) {
                        $scope.programs.push(p);
                        if (p.id == $scope.selecteProgram.id) {
                            $scope.selecteProgram = ou.programs[ou.programs.indexOf(p)];
                        }
                    });
                }
            });
        });
    }

    $scope.$watch('orgunit.currentNode', function(newObj, oldObj) {

        if ($scope.orgunit && angular.isObject($scope.orgunit.currentNode)) {
            $scope.selectedOrgUnit = $scope.orgunit.currentNode;

            $scope.programs = [];
            var selectedProgramIndex = '';

            ProgramFactory.getMyPrograms().then(function(prs) {
                angular.forEach(prs.organisationUnits, function(ou) {
                    if (ou.id == $scope.selectedOrgUnit.id) {
                        angular.forEach(ou.programs, function(p) {
                            $scope.programs.push(p);

                            if (p.id == $scope.selecteProgram.id) {
                                selectedProgramIndex = ou.programs.indexOf(p);
                                $scope.selecteProgram = ou.programs[selectedProgramIndex];
                            }
                        });
                    }
                });
            });
        }
    }, false);

    $scope.saveSettings = function() {

        if (!angular.isObject($scope.selectedOrgUnit) ||
                $scope.selectedOrgUnit.name == 'not_selected' ||
                !angular.isObject($scope.selecteProgram) ||
                $scope.selecteProgram.label == 'not_selected') {
            
            var dialogOptions = {
                    headerText: 'settings_error',
                    bodyText: 'please_select_orgunit_or_program'
                };

            DialogService.showDialog({}, dialogOptions);
        }

        if (angular.isObject($scope.selectedOrgUnit) &&
                $scope.selectedOrgUnit.name != 'not_selected' &&
                angular.isObject($scope.selecteProgram) &&
                $scope.selecteProgram.label != 'not_selected') {

            storage.set('SELECTED_ORGUNIT', $scope.selectedOrgUnit);
            storage.set('SELECTED_PROGRAM', $scope.selecteProgram);
            
            //Load EIs
            EIFactory.getEI().then(function(data) {
                angular.forEach(data, function(ei) {
                    storage.set(ei.code, ei);
                });
            });            

            //Load program, programstage and dataelements
            ProgramFactory.getProgram( $scope.selecteProgram.id).then(function(program){                
                
                storage.set('PROGRAM', program);   
                
                angular.forEach(program.programStages, function(programStage){
                    
                    storage.set(programStage.id, programStage);
                    
                    angular.forEach(programStage.programStageDataElements, function(prDataElement) {
                        var de = storage.get(prDataElement.dataElement.code);
                        de.id = prDataElement.dataElement.id;
                        storage.set(de.id, de);
                        storage.set(de.code, de);
                    }); 
                });
                
                var dialogOptions = {headerText: 'success',
                                     bodyText: 'settings_saved'};
                DialogService.showDialog({}, dialogOptions); 
            });
        }
    };
})

//Controller for first page
.controller('SearchController',
        function($scope,
                $location,
                $route,
                CurrentUserProfile,
                $translate,
                PersonFactory,
                PersonAttributesFactory,
                DialogService,
                storage) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });   

    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');   

    $scope.searchPerson = function(listAll) {

        if (!angular.isObject($scope.selectedOrgUnit) || $scope.selectedOrgUnit.name === 'not_selected') {
            
            var dialogOptions = {
                    headerText: 'settings_error',
                    bodyText: 'please_select_orgunit_or_program'
                };

            DialogService.showDialog({}, dialogOptions);
        }
        if (angular.isObject($scope.selectedOrgUnit) && $scope.selectedOrgUnit.name !== 'not_selected') {

            if (listAll) {
                PersonFactory.getAllPersons($scope.selectedOrgUnit.id).success(function(data) {
                    $scope.persons = data.personList;                  
                    
                    //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                    //But we need to show all attributes in any way.                   
                    PersonAttributesFactory.getRegistrationAttributes().then(function(registrationAttributes){
                        $scope.registrationAttributes = registrationAttributes.personAttributeTypes;
               
                        angular.forEach($scope.persons, function(person){   
                            
                            //assume every person has values for the attributes - initially all are empty values
                            var newAttributes = [];
                            angular.forEach($scope.registrationAttributes, function(registrationAttribute){                                        
                                var newAttribute = {displayName: registrationAttribute.description, type: registrationAttribute.id, value: ''};
                                newAttributes[registrationAttribute.id] = newAttribute;
                            });  
                            
                            person.registrationAttributes = newAttributes;                            
                            angular.forEach(person.attributes, function(attribute){
                                person.registrationAttributes[attribute.type] = attribute;
                            });                            
                            
                            person.attributes = [];
                            angular.forEach($scope.registrationAttributes, function(registrationAttribute){                                        
                                person.attributes.push(person.registrationAttributes[registrationAttribute.id]);                                
                            }); 
                            
                            person.registrationAttributes = '';
                            
                        });
                    });
                });
            }
            else {
                //do the search, based on user input
            }
        }
    };

    $scope.showRegisterPerson = function() {

        if (!angular.isObject($scope.selectedOrgUnit) || $scope.selectedOrgUnit.name === 'not_selected') {
            var dialogOptions = {
                    headerText: 'settings_error',
                    bodyText: 'please_select_orgunit_or_program'
                };

            DialogService.showDialog({}, dialogOptions);
        }

        if (angular.isObject($scope.selectedOrgUnit) && $scope.selectedOrgUnit.name !== 'not_selected') {
            $location.path('/registration');
            $route.reload();
        }
    };
})

//Controller first ANC start page
.controller('ANCDashboardController',
        function($rootScope,
                $scope,
                CurrentUserProfile,
                PersonService,
                DHIS2EventFactory,
                storage,
                $filter,
                $translate,
                $location,
                $route,
                DialogService,
                orderByFilter) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });  

    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Pick selected person and fetch profile from server
    $scope.personUid = ($location.search()).personUid;  
    
    PersonService.getPerson($scope.personUid, $scope.selectedProgram.id).then(function(person){
        $scope.person = person;
    });  
    
    $scope.dhis2Events;
    $scope.isFirstEvent;
    //Fetch available events for the selected person
    DHIS2EventFactory.getDHIS2Events($scope.personUid, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data) {
        $scope.dhis2Events = data;        
        if(angular.isUndefined($scope.dhis2Events)){
            $scope.isFirstEvent = true;
        }
        else{            
            $scope.isFirstEvent = false;    
            
            $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
            $scope.dhis2Events.reverse();
        }
    });

    //create new ANC visit
    $scope.createNewANCVisit = function(isFirstEvent) {        

        
        //Get available visits for the selected person
        $scope.personUid = ($location.search()).personUid;

        $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
        $scope.selectedProgram = storage.get('SELECTED_PROGRAM');
        $scope.program = storage.get('PROGRAM');
        $scope.programStage = $scope.program.programStages[0].id; 
        
        if ( !isFirstEvent ) {
            angular.forEach($scope.program.programStages, function(programStage){
                if( programStage.repeatable ){
                    $scope.programStage = programStage.id;
                }//if no repeatable is found, program stage is set to first stage
            });
        }
       

        $scope.DHIS2Event = {program: $scope.selectedProgram.id,
            programStage: $scope.programStage,
            orgUnit: $scope.selectedOrgUnit.id,
            status: 'ACTIVE',
            notes: [],
            person: $scope.personUid,
            eventDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            dataValues: []
        };

        DHIS2EventFactory.postDHIS2Event($scope.DHIS2Event).then(function(data) {
            if (data.importSummaries[0].status == 'ERROR') {
                var dialogOptions = {
                    headerText: 'consultation_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                $scope.DHIS2Event.event = data.importSummaries[0].reference;
                storage.set(data.importSummaries[0].reference, $scope.DHIS2Event);
                
                $rootScope.$broadcast('currentEvent', {currentEvent: $scope.DHIS2Event.event});
                $rootScope.$broadcast('currentPerson', {currentPerson: $scope.DHIS2Event.person, currentEvent: $scope.DHIS2Event});
                
                $location.path('/anc/dataentry').search({personUid: $scope.personUid, eventUid: data.importSummaries[0].reference});
                $route.reload();
            }
        });
    };
    $scope.cancel = function() {
        $location.path('/anc');
        $route.reload();
    };
})

//Controller for managing visits
.controller('ANCVisitController',
        function($rootScope,
                $scope,
                $translate,
                CurrentUserProfile,
                $location,
                PersonService,
                DHIS2EventFactory,
                storage) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });  
    
    //pick selected program
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //get the selected event
    $scope.eventUid = ($location.search()).eventUid;    
    DHIS2EventFactory.getDHIS2Event($scope.eventUid).then(function(dhis2Event){
        $scope.dhis2Event = dhis2Event;      
        
        //Pick selected person UID and fetch full person profile from server
        $scope.personUid = $scope.dhis2Event.person;
        PersonService.getPerson($scope.personUid, $scope.selectedProgram.id).then(function(person){
            $scope.person = person;
        });
        
        //Fetch program stage of the selected event
        $scope.programStage = storage.get($scope.dhis2Event.programStage);
        $scope.prStageDataElements = $scope.programStage.programStageDataElements;
        
        angular.forEach($scope.prStageDataElements, function(prStDe){  
            
            prStDe.dataElement = storage.get(prStDe.dataElement.id);
            
            angular.forEach(dhis2Event.dataValues, function(dataValue){      
                
                //associate data elements and their values
                if(prStDe.dataElement.id == dataValue.dataElement){ 
                    prStDe.dataElement.value = dataValue.value;  
                    
                    //associate labels and values for custom values
                    if( angular.isObject(prStDe.dataElement.optionSet) ){
                        angular.forEach(prStDe.dataElement.optionSet, function(option){
                            if(option.value == dataValue.value){
                                prStDe.dataElement.value = option.label;
                            }
                        });  
                    }
                    
                }
                
            });            
        });        

        //broadcast the selected event for others to pick
        $rootScope.$broadcast('currentEvent', {currentEvent: $scope.dhis2Event.event}); 
        $rootScope.$broadcast('currentPerson', {currentPerson: $scope.dhis2Event.person, currentEvent: $scope.dhis2Event});

    });      
})

//Controller for managing Data Entry page
.controller('ANCDataEntryController',
        function($rootScope,
                $scope,                
                PersonService,
                storage,
                $location,
                $translate,
                CurrentUserProfile,
                DependencyPageService,
                DHIS2EventFactory) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });  
    
    //data entry starts with gen page and empty dep page.
    $scope.ancPage = 'GEN';
    DependencyPageService.setDepPage('');
    
    //get selected orgunit and program
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //get selected person, and fetch full profile from server
    $scope.personUid = ($location.search()).personUid;
    PersonService.getPerson($scope.personUid, $scope.selectedProgram.id).then(function(person){
        $scope.person = person;
    });
    
    //get selected event uid, and fetch its content
    $scope.eventUid = ($location.search()).eventUid;    
    $scope.currentEvent = storage.get($scope.eventUid);
    
    $rootScope.$broadcast('currentEvent', {currentEvent: $scope.currentEvent.event});
    $rootScope.$broadcast('currentPerson', {currentPerson: $scope.currentEvent.person, currentEvent: $scope.currentEvent});
    
    //get program stage for the selected event
    $scope.programStage = storage.get($scope.currentEvent.programStage);   
  
    //Load data elements with intervention for the selected program stage
    $scope.dataElements = [];    
    var de;    
    angular.forEach($scope.programStage.programStageSections, function(section){
        if(section.code == 'GEN'){
            angular.forEach(section.programStageDataElements, function(pr) {
                de = storage.get(pr.dataElement.id);        
                $scope.dataElements.push(de);
            });
        }
    });      
    
    //save or update event data value
    $scope.saveValue = function(dataElement) {       
        
        if( !angular.isUndefined(dataElement.value) ){           
            
            if( $scope.currentEvent.dataValues.length > 0 ){

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
                     
            DHIS2EventFactory.updateDHIS2Event($scope.currentEvent).then(function(data){
                $rootScope.$broadcast('currentEvent', {currentEvent: $scope.currentEvent.event}); 
                $rootScope.$broadcast('currentPerson', {currentPerson: $scope.currentEvent.person, currentEvent: $scope.currentEvent});
            });                   
        }        
    };

    $scope.showDepPage = function() {

        //which page to show
        $scope.ancPage = 'DEP';
       
        //Load DEP data elements for dep page
        $scope.dataElements = [];
        var depPage = DependencyPageService.getDepPage();        
        for(var i=0; i < depPage.length; i++){
            $scope.dataElements.push(storage.get(depPage[i]));
        }
    };   
})

//Controller for summary page
.controller('ANCInterventionsController',
        function($scope,
                CurrentUserProfile,
                DHIS2EventFactory,
                storage,
                TransferHandler,
                ExpressionService,
                DependencyPageService,
                orderByFilter,
                $translate) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });    
   
    $scope.$on('currentPerson', function(event, args) {
        
        //pick selected orgUnit and program
        var selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
        var selectedProgram = storage.get('SELECTED_PROGRAM');
        
        //Fetch available events for the selected person
        DHIS2EventFactory.getDHIS2Events(args.currentPerson, selectedOrgUnit.id, selectedProgram.id).then(function(data) {
            
            $scope.dhis2Events = data;   
            
            $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
            $scope.dhis2Events.reverse();
            
            $scope.currentEvent = args.currentEvent;
            
            //Execute actions of interventions
            var dep = [], con = [], smr = [], rem = [], mes = [], sch = [], lab = [];

            $scope.depResult = '';
            $scope.conResult = '';
            $scope.smrResult = '';
            $scope.remResult = '';
            $scope.mesResult = '';
            $scope.schResult = '';
            $scope.labResult = '';

            angular.forEach($scope.currentEvent.dataValues, function(dataValue){                  
                var de = storage.get(dataValue.dataElement);
                var actualValue = dataValue.value;      
                
                if( angular.isObject(de.optionSet) ){
                    angular.forEach(de.optionSet, function(option){
                        if(option.value == dataValue.value){
                            actualValue = option.label;
                        }
                    });  
                }        
                angular.forEach(de.actions, function(eiAction) {                           
                    
                    var val = eiAction.value.replace(new RegExp('#' + de.code + '#', 'g'), dataValue.value);

                    //console.log('the expression is before:  ', val, ' and the value', actualValue);

                    if (val.indexOf('#') != -1) {
                        val = ExpressionService.getDataElementExpression(val, $scope.dhis2Events);
                    }

                    //console.log('the expression is after:  ', val);

                    if ($scope.$eval(val)) {
                        //console.log('the expression is true:  ', val);

                        TransferHandler.store(eiAction.task.dependencies, de.code, actualValue, dep);
                        TransferHandler.store(eiAction.task.conditionsComplications, de.code, actualValue, con);
                        TransferHandler.store(eiAction.task.summary, de.code, actualValue, smr);
                        TransferHandler.store(eiAction.task.reminders, de.code, actualValue, rem);
                        TransferHandler.store(eiAction.task.messaging, de.code, actualValue, mes);
                        TransferHandler.store(eiAction.task.scheduling, de.code, actualValue, sch);
                    }

                });

                if (de.labResults) {
                    if (lab.indexOf(de.labResults) != -1) {
                        lab.push(de.labResults);
                    }
                }            
            });       

            // Dep is special need to be shared with data entry controller
            DependencyPageService.setDepPage(dep);            
            
            $scope.conResult = con;
            $scope.smrResult = smr;
            $scope.remResult = rem;
            $scope.mesResult = mes;
            $scope.schResult = sch;
            $scope.labResult = lab;
            
        });       
        
    });   
})

//Controller for summary page
.controller('ANCVisitNotesController',
        function($rootScope,
                $scope,
                CurrentUserProfile,
                DHIS2EventFactory,
                orderByFilter,
                NotesDialogService,
                $translate) {

    //Get current locale            
    CurrentUserProfile.getProfile().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });      
    
    $scope.$on('currentEvent', function(event, args) {  
        DHIS2EventFactory.getDHIS2Event(args.currentEvent).then(function(data){    
            $scope.currentEvent = data;
            $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');
        });                
    });
   
    $scope.searchNoteField = false;
    $scope.addNoteField = false;    

    
    $scope.showAddNote = function() {
        $scope.addNoteField = true;
    };
    
    $scope.addNote = function(){
        
        var newNote = {value: $scope.note};

        if(angular.isUndefined( $scope.currentEvent.notes) ){
            $scope.currentEvent.notes = [newNote];
        }
        else{
            $scope.currentEvent.notes.splice(0,0,newNote);
        }
        
        var e = $scope.currentEvent;
        e.notes = [newNote];
        DHIS2EventFactory.updateDHIS2Event(e).then(function(data){               
            
            $scope.note = '';  
            
            $scope.addNoteField = false; //note is added, hence no need to show note field.
            $rootScope.$broadcast('currentEvent', {currentEvent: $scope.currentEvent.event});
        });
    };
    
    $scope.displayNote = function(note){       
        
        var dialogOptions = {
            headerText: 'consultation_note',
            bodyText: note,
            note: note.value,
            created_by: note.storedBy,
            date: note.storedDate
        };

        NotesDialogService.showDialog({}, dialogOptions);

    };
    
    $scope.closeAddNote = function(){
         $scope.addNoteField = false;
         $scope.note = '';           
    };
    
    $scope.searchNote = function(){        
        $scope.searchNoteField = $scope.searchNoteField === false? true : false;
        $scope.noteSearchText = '';
    };
});
