'use strict';

/* Services */

var trackerFactory = angular.module('trackerServices', ['ngResource'])

/* Factory to fetch programs */
.factory('ProgramFactory', function($http, $rootScope) {
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;   
    
    var program, programPromise;
    var programs, programsPromise;
    
    return {
        
        get: function(uid){
            if( program != uid && !programPromise ){
                programPromise = $http.get(dhis2Url + '/api/programs/' + uid + '.json?viewClass=extended&paging=false').then(function(response){
                   program = response.data.id;
                   return response.data;
                });
            }
            return programPromise;
        },       
        
        getMine: function(){ 
            if( !programs || !programsPromise ){
                programsPromise = $http.get(dhis2Url + '/api/me/programs').then(function(response){
                   programs = response.data;
                   return programs;
                });
            }
            return programsPromise;    
        }
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($http, $rootScope) {  
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;
    
    var programStage, promise;   
    return {        
        get: function(uid){
            if( programStage !== uid ){
                promise = $http.get( dhis2Url + '/api/programStages/' + uid + '.json?viewClass=extended&paging=false').then(function(response){
                   programStage = response.data.id;
                   return response.data;
                });
            }
            return promise;
        }
    };    
})

/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http, $rootScope) { 
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get(dhis2Url + '/api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;           
        }
    };    
})

/* Factory to enroll person in a program */
.service('EnrollmentFactory', function($http, $rootScope) {
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
    
    var EnrollmentFactory = {};

    EnrollmentFactory.enrollPerson = function( enrollment ) {
        return $http.post( dhis2Url + '/api/enrollments', enrollment );
    };
    
    EnrollmentFactory.getEnrollment = function( orgUnit, program, person, status ){
        return $http.get( dhis2Url + '/api/enrollments?orgunit=' + orgUnit + '&program=' + program + '&person=' + person + '&status=' + status);
    };

    return EnrollmentFactory;
})

/* Factory for loading Essential Interventions */
.factory('EIFactory', function($http) {
    
    var ei, promise;
    return {
        getEI: function() {            
            if( !ei || !promise ){
                promise = $http.get('json/EI_All.json').then(function(response){                    
                    ei = response.data;
                    return ei;
                });
            }
            return promise;            
        }
    };
})

/* Factory for loading Essential Interventions - new*/
.factory('EIFormatter', function($http, $rootScope) {  
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
        
    return {
        getEI: function() {            
            var ei, promise;
            if( !ei || !promise ){
                promise = $http.get(dhis2Url + '/api/dataElements.json?viewClass=extended&paging=false').then(function(response){
                    ei = response.data;                    
                    return response.data;
                });
            }
            return promise;            
        },
        
        formatEI: function(){
            var ei, promise;
            var dataElements = [];
            if( !ei || !promise ){
                promise = $http.get(dhis2Url + '/api/dataElements.json?viewClass=extended&paging=false').then(function(response){
                    
                    angular.forEach(response.data.dataElements, function(de) {
                        
                        var dataElement;
                        
                        dataElement = {name: de.name,
                                       code: de.code,
                                       type: de.type,
                                       id: de.id,
                                       pageOfEntry: ['ANC1_GEN', 'ANC1_DEP', 'ANCnn_GEN', 'ANCnn_DEP'],
                                       sequenceOnPage: '',
                                       optionSet: '',
                                       actions: [{
                                               value: '',
                                               task: {
                                                   dependencies: [], 
                                                   conditionsComplications: [],
                                                   summary: [],
                                                   reminders: [],
                                                   messaging: [],
                                                   scheduling: []
                                               }
                                           }],
                                       labResults: ''};
                        
                        
                        if(angular.isObject(de.optionSet)){
                            var options = [];
                            angular.forEach(de.optionSet.options, function(option){
                                option = {label: option, value: option};
                                options.push(option);
                            });                            
                            dataElement.optionSet = options;                            
                        }  
                        dataElements.push(dataElement);
                    }); 
                    return dataElements;
                });
            }
            return promise;            
        }
    };
})

/* Factory for loading OrgUnit */
.factory('OrgUnitFactory', function($http, $rootScope) {
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;   
    
    var orgUnit, orgUnitPromise, myOrgUnits, myOrgUnitsPromise, allOrgUnits, allOrgUnitsPromise;
    
    return {
        get: function(uid){
            
            if(orgUnit != uid || !orgUnitPromise ){
                orgUnitPromise = $http.get(dhis2Url + '/api/organisationUnits/' + uid + '.json').then(function(response){
                    orgUnit = response.data.id;
                    return response.data;
                });
            }
            return orgUnitPromise;
        },
        
        getMine: function(){
            if(!myOrgUnitsPromise){
                myOrgUnitsPromise = $http.get(dhis2Url + '/api/me/organisationUnits').then(function(response){
                    return response.data;
                });
            }
            return myOrgUnitsPromise;
        },
        
        getAll: function(){
            if(!allOrgUnitsPromise){
                allOrgUnitsPromise = http.get(dhis2Url + '/api/organisationUnits.json').then(function(response){
                    return response.data;
                });
            }
            return allOrgUnitsPromise;
        }
    }; 
})

/* Factory for getting person */
.factory('PersonFactory', function($http, $rootScope, PersonAttributesFactory) {
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
    
    return {
        get: function(uid){
            var promise = $http.get(dhis2Url + '/api/persons/' + uid + '.json').then(function(response){                
                var person = response.data;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                PersonAttributesFactory.getRegistrationAttributes().then(function(data){
                    var registrationAttributes = data.personAttributeTypes;

                    //assume every person has values for the attributes - initially all are empty values
                    var newAttributes = [];
                    angular.forEach(registrationAttributes, function(registrationAttribute){                                        
                        var newAttribute = {displayName: registrationAttribute.description, type: registrationAttribute.id, value: ''};
                        newAttributes[registrationAttribute.id] = newAttribute;
                    });  

                    person.registrationAttributes = newAttributes;                            
                    angular.forEach(person.attributes, function(attribute){
                        person.registrationAttributes[attribute.type] = attribute;
                    });                            

                    person.attributes = [];
                    angular.forEach(registrationAttributes, function(registrationAttribute){                                        
                        person.attributes.push(person.registrationAttributes[registrationAttribute.id]);                                
                    }); 

                    person.registrationAttributes = '';

                });
                
                return person;
            });            
            return promise;
        },
        
        getAll: function(orgUnitUid){
            var promise = $http.get(dhis2Url + '/api/persons?orgUnit=' + orgUnitUid + '&paging=false').then(function(response){
                
                var personList = response.data.personList;
                
                //This is is to have consistent display of person and attributes - because every person might not have value for every attribute. 
                //But we need to show all attributes in any way.                   
                PersonAttributesFactory.getRegistrationAttributes().then(function(data){
                    var registrationAttributes = data.personAttributeTypes;

                    angular.forEach(personList, function(person){   
                         
                        //assume every person has values for the attributes - initially all are empty values
                        var newAttributes = [];
                        angular.forEach(registrationAttributes, function(registrationAttribute){                                        
                            var newAttribute = {displayName: registrationAttribute.description, type: registrationAttribute.id, value: ''};
                            newAttributes[registrationAttribute.id] = newAttribute;
                        });  

                        person.registrationAttributes = newAttributes;                            
                        angular.forEach(person.attributes, function(attribute){
                            person.registrationAttributes[attribute.type] = attribute;
                        });                            

                        person.attributes = [];
                        angular.forEach(registrationAttributes, function(registrationAttribute){                                        
                            person.attributes.push(person.registrationAttributes[registrationAttribute.id]);                                
                        }); 

                        person.registrationAttributes = '';

                    });
                });
                
                return personList;
            }); 
            
            return promise;
        },
        
        register: function(person){
            var promise = $http.post(dhis2Url + '/api/persons', person).then(function(response){
                return response.data;
            });
            return promise;
        },
        
        update: function(person){
            var promise = $http.put(dhis2Url + '/api/persons/' + person.person , person).then(function(response){
                return response.data;
            });
            return promise;
        }
    };
})

/* Service for getting person profile - including program related attribtues */
.factory('PersonService', function($http, $rootScope, PersonAttributesFactory){
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
          
    var person, programAttributes, promise; 
    return {
        getPerson: function(personUid, programUid){     

            //if(!promise){
                
                promise = $http.get(dhis2Url + '/api/persons/' + personUid + '.json').then(function(response){              
                    person = response.data;                
                    PersonAttributesFactory.getEnrollmentAttributes(programUid).then(function(data) {
                        var enrollmentAttributes = [];
                        var registrationAttributes = person.attributes;
                        programAttributes = data.personAttributeTypes;   
                        angular.forEach(person.attributes, function(personAttribute) {
                            angular.forEach(programAttributes, function(programAttribute){                           
                                if( programAttribute.id == personAttribute.type){                                    
                                    enrollmentAttributes.push(personAttribute);
                                }                                
                            });                   
                        });

                        angular.forEach(enrollmentAttributes, function(a){
                            registrationAttributes.splice(registrationAttributes.indexOf(a),1);
                        });
                        
                        person.registrationAttributes = registrationAttributes;
                        person.enrollmentAttributes = enrollmentAttributes;                    
                    });                     
                    return person;
                });
            //}      
            return promise;
        }
    };            
})

/* Factory for getting person attribute types*/
.factory('PersonAttributesFactory', function($http, $rootScope) { 
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
    
    var rAttributes, rPromise;
    var eAttributes, ePromise;
    
    return {
        getEnrollmentAttributes: function(uid){

            if( !rAttributes || !rPromise ){                
                rPromise = $http.get(dhis2Url + '/api/personAttributeTypes.json?program=' + uid + '&viewClass=detailed&paging=false').then(function(response){                    
                    angular.forEach(response.data.personAttributeTypes, function(registrationAttribute) {
                        if (angular.isObject(registrationAttribute.personAttributeOptions)) {
                            angular.forEach(registrationAttribute.personAttributeOptions, function(attributeOption) {
                                attributeOption.value = attributeOption.name;
                            });
                        }
                    });                     
                   rAttributes = response.data;
                   return rAttributes;
                });
            }
            return rPromise;
        },
        
        getRegistrationAttributes: function(){

            if( !eAttributes || !ePromise ){                
                ePromise = $http.get(dhis2Url + '/api/personAttributeTypes.json?withoutPrograms=true&viewClass=detailed&paging=false').then(function(response){                    
                    angular.forEach(response.data.personAttributeTypes, function(registrationAttribute) {
                        if (angular.isObject(registrationAttribute.personAttributeOptions)) {
                            angular.forEach(registrationAttribute.personAttributeOptions, function(attributeOption) {
                                attributeOption.value = attributeOption.name;
                            });
                        }
                    });                    
                   eAttributes = response.data;
                   return eAttributes;
                });
            }
            return ePromise;
        }
    };
})

/* factory for getting data elements */
.factory('DataElementFactory', function($http, $rootScope) {
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
   
    var DataElemmentFactory = {};

    DataElemmentFactory.getDataElement = function(uid) {
        return $http.get(dhis2Url + '/api/dataElements/' + uid + '.json');
    };

    DataElemmentFactory.getAllDataElements = function() {
        return $http.get(dhis2Url + '/api/dataElements');
    };

    DataElemmentFactory.getDataElementGroup = function(uid) {
        return $http.get(dhis2Url + '/api/dataElementGroups/' + uid + '.json');
    };

    DataElemmentFactory.getAllDataElementGroups = function() {
        return $http.get(dhis2Url + '/api/dataElementGroups');
    };

    return DataElemmentFactory;
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, $rootScope, $filter, storage) {   
    
    var dhis2Url = $rootScope.appConfiguration.activities.dhis.href;    
    
    return {
        
        getDHIS2Events: function(person, orgUnit, program){        

            var promise = $http.get(dhis2Url + '/api/events.json?' + 'person=' + person + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
                var dhis2Events = response.data.eventList;
                angular.forEach(dhis2Events, function(dhis2Event){
                    var programStage = storage.get(dhis2Event.programStage);
                    dhis2Event.name = programStage.name;                    
                });                
                return dhis2Events;                
            });            
            return promise;
        },
        
        getDHIS2Event: function(eventUID){
            
            var promise = $http.get(dhis2Url + '/api/events/' + eventUID + '.json').then(function(response){               
                var dhis2Event = response.data;
                var programStage = storage.get(dhis2Event.programStage);
                dhis2Event.name = programStage.name;
                return dhis2Event;
            });            
            return promise;
        },
        
        postDHIS2Event: function(dhis2Event){
            var promise = $http.post(dhis2Url + '/api/events.json?', dhis2Event).then(function(response){
                return response.data;
            });
            return promise;            
        },
    
        updateDHIS2Event: function(dhis2Event){            
            var promise = $http.put(dhis2Url + '/api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;
            });
            return promise;
        }
    };    
})

/* service to communicate current event with controllers */
.service('DHIS2EventService', function() {
    var currentEventUid;
    return {
        setCurrentEventUid: function(uid) {
            currentEventUid = uid;
        },
        getCurrentEventUid: function() {    
            return currentEventUid;
        }
    };
})

/* Service for loading app configurations */
.factory('TrackerApp', function($http) {
    
    var configuration, configurationPromise;

    return {
        getConfiguration: function() {
            
            if(!configuration || !configurationPromise){
                configurationPromise = $http.get('manifest.webapp').then(function(response){
                   configuration = response.data;
                   return configuration;
                });
            }
            
            return configurationPromise; 
        }
    };
})

/* Service for evaluating intervention rules */
.service('ExpressionService', function(storage) {
    
    return {
        getDataElementExpression: function(val, dhis2Events) {
          
            var regex = /#[^#]*#/g,
                    match,
                    m,
                    mDe,
                    matches = [];
            
            //first collect all variables that need data value from the expression
            while (match = regex.exec(val)) {               
                m = match.toString();
                mDe = m.substring(1,m.length-1);
                matches.push(mDe);             
                
            }
            
            //replace variables with actuall data values - here I am trusing the order of entry
            //if the expression requires a value not yet recorded - this will fail! 
            for(var k=0; k<matches.length; k++){
                var loopThrough = true;
                var de = storage.get(matches[k]);
                for(var i=0; i<dhis2Events.length && loopThrough; i++){
                    for(var j=0; j<dhis2Events[i].dataValues.length && loopThrough; j++){
                        if( de.id == dhis2Events[i].dataValues[j].dataElement ){
                            val = val.replace(new RegExp('#'+de.code+'#','g'), dhis2Events[i].dataValues[j].value);
                            loopThrough = false;
                        }                            
                    }
                }
            }            
            return val;
        }
    };
})

/* Service for handling dependency outomces of interventions */
.service('DependencyPageService', function() {
    var depPage;
    return {
        setDepPage: function(page) {
            depPage = page;
        },
        getDepPage: function() {    
            return depPage;
        }
    };
})

/* Service for handling outcomes of interventions */
.factory('TransferHandler', function() {

    return {
        store: function(input, code, value, output) {            
            for (var i = 0; i < input.length; i++) {
                if (input[i]) {
                    input[i] = input[i].replace(new RegExp('#' + code + '#', 'g'), value);
                    if (output.indexOf(input[i]) == -1) {
                        output.push(input[i]);
                    }
                }
            }
        }
    };
})

/* Modal service for user interaction */
.service('ModalService', ['$modal', function($modal) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: '../tracker/views/modal.html'
        };

        var modalOptions = {
            closeButtonText: 'Close',
            actionButtonText: 'OK',
            headerText: 'Proceed?',
            bodyText: 'Perform this action?'
        };

        this.showModal = function(customModalDefaults, customModalOptions) {
            if (!customModalDefaults)
                customModalDefaults = {};
            customModalDefaults.backdrop = 'static';
            return this.show(customModalDefaults, customModalOptions);
        };

        this.show = function(customModalDefaults, customModalOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempModalDefaults = {};
            var tempModalOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempModalOptions, modalOptions, customModalOptions);

            if (!tempModalDefaults.controller) {
                tempModalDefaults.controller = function($scope, $modalInstance) {
                    $scope.modalOptions = tempModalOptions;
                    $scope.modalOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };
                    $scope.modalOptions.close = function(result) {
                        $modalInstance.dismiss('cancel');
                    };
                };
            }

            return $modal.open(tempModalDefaults).result;
        };

    }])

/* Dialog service for user interaction */
.service('DialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,
            templateUrl: '../tracker/views/dialog.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }])

/* Popup dialog for displaying notes */
.service('NotesDialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,
            templateUrl: '../tracker/views/anc/note.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?',
            note: 'note',
            created_by: 'created_by',
            date: 'date'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }]);
