'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])

.factory('TCStorageService', function(){
    var store = new dhis2.storage.Store({
        name: "dhis2tc",
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['programs', 'programStages', 'trackedEntities', 'trackedEntityForms', 'attributes', 'relationshipTypes', 'optionSets', 'programValidations', 'ouLevels', 'programRuleVariables', 'programRules', 'programRuleActions']
    });
    return{
        currentStore: store
    };
})

/* Service to fetch/store dasboard widgets */
.service('DashboardLayoutService', function($http) {
    
    var w = {};
    w.enrollmentWidget = {title: 'enrollment', view: "components/enrollment/enrollment.html", show: true, expand: true, parent: 'biggerWidget', order: 0};
    w.dataentryWidget = {title: 'dataentry', view: "components/dataentry/dataentry.html", show: true, expand: true, parent: 'biggerWidget', order: 1};
    w.reportWidget = {title: 'report', view: "components/report/tei-report.html", show: true, expand: true, parent: 'biggerWidget', order: 2};
    w.selectedWidget = {title: 'current_selections', view: "components/selected/selected.html", show: false, expand: true, parent: 'smallerWidget', order: 0};
    w.profileWidget = {title: 'profile', view: "components/profile/profile.html", show: true, expand: true, parent: 'smallerWidget', order: 1};
    w.relationshipWidget = {title: 'relationships', view: "components/relationship/relationship.html", show: true, expand: true, parent: 'smallerWidget', order: 2};
    w.notesWidget = {title: 'notes', view: "components/notes/notes.html", show: true, expand: true, parent: 'smallerWidget', order: 3};            
    var defaultLayout = new Object();
    defaultLayout['DEFAULT'] = {widgets: w, program: 'DEFAULT'};
    
    return {
        saveLayout: function(dashboardLayout){
            var layout = JSON.stringify(dashboardLayout);
            var promise = $http.post( '../api/userSettings/dhis2-tracker-dashboard?value=' + layout, '', {headers: {'Content-Type': 'text/plain;charset=utf-8'}}).then(function(response){
                return response.data;
            });
            return promise;            
        },
        get: function(){            
            var promise = $http.get(  '../api/userSettings/dhis2-tracker-dashboard' ).then(function(response){                
                return response.data === "" ? defaultLayout : response.data;
            }, function(){
                return defaultLayout;
            });
            return promise;
        }
    };
})

/* Factory to fetch optionSets */
.factory('OptionSetService', function($q, $rootScope, TCStorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('optionSets').done(function(optionSets){
                    $rootScope.$apply(function(){
                        def.resolve(optionSets);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('optionSets', uid).done(function(optionSet){                    
                    $rootScope.$apply(function(){
                        def.resolve(optionSet);
                    });
                });
            });                        
            return def.promise;            
        },        
        getCode: function(options, key){
            if(options){
                for(var i=0; i<options.length; i++){
                    if( key === options[i].name){
                        return options[i].code;
                    }
                }
            }            
            return key;
        },        
        getName: function(options, key){
            if(options){
                for(var i=0; i<options.length; i++){                    
                    if( key === options[i].code){
                        return options[i].name;
                    }
                }
            }            
            return key;
        }
    };
})

/* Factory to fetch relationships */
.factory('RelationshipFactory', function($q, $rootScope, TCStorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('relationshipTypes').done(function(relationshipTypes){
                    $rootScope.$apply(function(){
                        def.resolve(relationshipTypes);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('relationshipTypes', uid).done(function(relationshipType){                    
                    $rootScope.$apply(function(){
                        def.resolve(relationshipType);
                    });
                });
            });                        
            return def.promise;            
        }
    };
})

/* Factory to fetch programs */
.factory('ProgramFactory', function($q, $rootScope, TCStorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('programs').done(function(prs){
                    var programs = [];
                    angular.forEach(prs, function(pr){
                        if(pr.type === 1){
                            programs.push(pr);
                        }
                    });
                    $rootScope.$apply(function(){
                        def.resolve(programs);
                    });                      
                });
            });
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('programs', uid).done(function(pr){                    
                    $rootScope.$apply(function(){
                        def.resolve(pr);
                    });
                });
            });                        
            return def.promise;            
        },
        getProgramsByOu: function(ou, selectedProgram){
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('programs').done(function(prs){
                    var programs = [];
                    angular.forEach(prs, function(pr){                            
                        if(pr.organisationUnits.hasOwnProperty(ou.id)){                                
                            programs.push(pr);
                        }
                    });
                    if(programs.length === 0){
                        selectedProgram = null;
                    }
                    else{
                        if(selectedProgram){
                            angular.forEach(programs, function(pr){                            
                                if(pr.id === selectedProgram.id){                                
                                    selectedProgram = pr;
                                }
                            });
                        }
                        else{                        
                            if(programs.length === 1){
                                selectedProgram = programs[0];
                            }                        
                        }
                    }
                    
                    $rootScope.$apply(function(){
                        def.resolve({programs: programs, selectedProgram: selectedProgram});
                    });                      
                });
            });
            
            return def.promise;
        }          
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($q, $rootScope, TCStorageService) {  
    
    return {        
        get: function(uid){            
            var def = $q.defer();
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('programStages', uid).done(function(pst){                    
                    $rootScope.$apply(function(){
                        def.resolve(pst);
                    });
                });
            });            
            return def.promise;
        },
        getByProgram: function(program){
            var def = $q.defer();
            var stageIds = [];
            var programStages = [];
            angular.forEach(program.programStages, function(stage){
                stageIds.push(stage.id);
            });
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('programStages').done(function(stages){   
                    angular.forEach(stages, function(stage){
                        if(stageIds.indexOf(stage.id) !== -1){                            
                            programStages.push(stage);                               
                        }                        
                    });
                    $rootScope.$apply(function(){
                        def.resolve(programStages);
                    });
                });                
            });            
            return def.promise;
        }
    };    
})

/* Factory to fetch programValidations */
.factory('ProgramValidationFactory', function($q, $rootScope, TCStorageService) {  
    
    return {        
        get: function(uid){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('programValidations', uid).done(function(pv){                    
                    $rootScope.$apply(function(){
                        def.resolve(pv);
                    });
                });
            });                        
            return def.promise;
        },
        getByProgram: function(program){
            var def = $q.defer();
            var programValidations = [];
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('programValidations').done(function(pvs){   
                    angular.forEach(pvs, function(pv){
                        if(pv.program.id === program){                            
                            programValidations.push(pv);                               
                        }                        
                    });
                    $rootScope.$apply(function(){
                        def.resolve(programValidations);
                    });
                });                
            });            
            return def.promise;
        }
    };        
})

/*Orgunit service for local db */
.service('OrgUnitService', function($window, $q){
    
    var indexedDB = $window.indexedDB;
    var db = null;
    
    var open = function(){
        var deferred = $q.defer();
        
        var request = indexedDB.open("dhis2ou");
        
        request.onsuccess = function(e) {
          db = e.target.result;
          deferred.resolve();
        };

        request.onerror = function(){
          deferred.reject();
        };

        return deferred.promise;
    };
    
    var get = function(uid){
        
        var deferred = $q.defer();
        
        if( db === null){
            deferred.reject("DB not opened");
        }
        else{
            var tx = db.transaction(["ou"]);
            var store = tx.objectStore("ou");
            var query = store.get(uid);
                
            query.onsuccess = function(e){
                deferred.resolve(e.target.result);
            };
        }
        return deferred.promise;
    };
    
    return {
        open: open,
        get: get
    };    
})

/* Factory for fetching OrgUnit */
.factory('OrgUnitFactory', function($http) {    
    var orgUnit, orgUnitPromise, rootOrgUnitPromise, myOrgUnitsPromise;    
    return {
        get: function(uid){            
            if( orgUnit !== uid ){
                orgUnitPromise = $http.get( '../api/organisationUnits.json?filter=id:eq:' + uid + '&fields=id,name,children[id,name,children[id,name]]&paging=false' ).then(function(response){
                    orgUnit = response.data.id;
                    return response.data;
                });
            }
            return orgUnitPromise;
        },        
        getRoot: function(){
            if(!rootOrgUnitPromise){
                rootOrgUnitPromise = $http.get( '../api/organisationUnits.json?filter=level:eq:1&fields=id,name,children[id,name,children[id,name]]&paging=false' ).then(function(response){
                    return response.data;
                });
            }
            return rootOrgUnitPromise;
        },
        getMine: function(){
            if(!myOrgUnitsPromise){
                myOrgUnitsPromise = $http.get('../api/me/organisationUnits').then(function(response){
                    return response.data;
                });
            }
            return myOrgUnitsPromise;
        }
    }; 
})

/* Service to deal with enrollment */
.service('EnrollmentService', function($http, DateUtils) {
    
    var convertFromApiToUser = function(enrollment){
        if(enrollment.enrollments){
            angular.forEach(enrollment.enrollments, function(enrollment){
                enrollment.dateOfIncident = DateUtils.formatFromApiToUser(enrollment.dateOfIncident);
                enrollment.dateOfEnrollment = DateUtils.formatFromApiToUser(enrollment.dateOfEnrollment);                
            });
        }
        else{
            enrollment.dateOfIncident = DateUtils.formatFromApiToUser(enrollment.dateOfIncident);
            enrollment.dateOfEnrollment = DateUtils.formatFromApiToUser(enrollment.dateOfEnrollment);
        }
        
        return enrollment;
    };
    var convertFromUserToApi = function(enrollment){
        enrollment.dateOfIncident = DateUtils.formatFromUserToApi(enrollment.dateOfIncident);
        enrollment.dateOfEnrollment = DateUtils.formatFromUserToApi(enrollment.dateOfEnrollment);
        return enrollment;
    };
    return {        
        get: function( enrollmentUid ){
            var promise = $http.get(  '../api/enrollments/' + enrollmentUid ).then(function(response){
                return convertFromApiToUser(response.data);
            });
            return promise;
        },
        getByEntity: function( entity ){
            var promise = $http.get(  '../api/enrollments.json?trackedEntityInstance=' + entity ).then(function(response){
                return convertFromApiToUser(response.data);
            });
            return promise;
        },
        getByEntityAndProgram: function( entity, program ){
            var promise = $http.get(  '../api/enrollments.json?trackedEntityInstance=' + entity + '&program=' + program ).then(function(response){
                return convertFromApiToUser(response.data);
            });
            return promise;
        },
        getByStartAndEndDate: function( program, orgUnit, ouMode, startDate, endDate ){
            var promise = $http.get(  '../api/enrollments.json?program=' + program + '&orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&startDate=' + startDate + '&endDate=' + endDate + '&paging=false').then(function(response){
                return convertFromApiToUser(response.data);
            });
            return promise;
        },
        enroll: function( enrollment ){
            var en = convertFromUserToApi(angular.copy(enrollment));
            var promise = $http.post(  '../api/enrollments', en ).then(function(response){
                return response.data;
            });
            return promise;
        },
        update: function( enrollment ){
            var en = convertFromUserToApi(angular.copy(enrollment));
            var promise = $http.put( '../api/enrollments/' + en.enrollment , en ).then(function(response){
                return response.data;
            });
            return promise;
        },
        cancel: function(enrollment){
            var promise = $http.put('../api/enrollments/' + enrollment.enrollment + '/cancelled').then(function(response){
                return response.data;               
            });
            return promise;           
        },
        complete: function(enrollment){
            var promise = $http.put('../api/enrollments/' + enrollment.enrollment + '/completed').then(function(response){
                return response.data;               
            });
            return promise;           
        }
    };   
})

/* Service for getting tracked entity */
.factory('TEService', function(TCStorageService, $q, $rootScope) {

    return {        
        getAll: function(){            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('trackedEntities').done(function(entities){
                    $rootScope.$apply(function(){
                        def.resolve(entities);
                    });                    
                });
            });            
            return def.promise;
        },
        get: function(uid){            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('trackedEntities', uid).done(function(te){                    
                    $rootScope.$apply(function(){
                        def.resolve(te);
                    });
                });
            });                        
            return def.promise;            
        }
    };
})

/* Service for getting tracked entity Form */
.factory('TEFormService', function(TCStorageService, $q, $rootScope) {

    return {
        getByProgram: function(program, attributes){            
            
            if(!program){
                program = {id: 'NO_PROGRAM', name: 'NO_PROGRAM', selectIncidentDatesInFuture: false, selectEnrollmentDatesInFuture: false, displayIncidentDate: false};
            }
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('trackedEntityForms', program.id).done(function(teForm){                    
                    $rootScope.$apply(function(){
                        var trackedEntityForm = teForm;
                        if(angular.isObject(trackedEntityForm)){
                            trackedEntityForm.attributes = attributes;
                            trackedEntityForm.selectIncidentDatesInFuture = program.selectIncidentDatesInFuture;
                            trackedEntityForm.selectEnrollmentDatesInFuture = program.selectEnrollmentDatesInFuture;
                            trackedEntityForm.displayIncidentDate = program.displayIncidentDate;
                            def.resolve(trackedEntityForm);
                        }
                        else{
                            def.resolve(null);
                        }
                    });
                });
            });                        
            return def.promise;            
        }
    };
})

/* Service for getting tracked entity instances */
.factory('TEIService', function($http, $q, AttributesFactory) {
    
    return {
        get: function(entityUid, optionSets, attributesById){
            var promise = $http.get( '../api/trackedEntityInstances/' +  entityUid + '.json').then(function(response){
                var tei = response.data;
                angular.forEach(tei.attributes, function(att){
                    if(attributesById[att.attribute]){
                        att.displayName = attributesById[att.attribute].name;
                    }
                    att.value = AttributesFactory.formatAttributeValue(att, attributesById, optionSets, 'USER');
                });
                return tei;
            });
            
            return promise;
        },
        search: function(ouId, ouMode, queryUrl, programUrl, attributeUrl, pager, paging) {
                
            var url =  '../api/trackedEntityInstances.json?ou=' + ouId + '&ouMode='+ ouMode;
            
            if(queryUrl){
                url = url + '&'+ queryUrl;
            }
            if(programUrl){
                url = url + '&' + programUrl;
            }
            if(attributeUrl){
                url = url + '&' + attributeUrl;
            }
            
            if(paging){
                var pgSize = pager ? pager.pageSize : 50;
                var pg = pager ? pager.page : 1;
                pgSize = pgSize > 1 ? pgSize  : 1;
                pg = pg > 1 ? pg : 1;
                url = url + '&pageSize=' + pgSize + '&page=' + pg;
            }
            else{
                url = url + '&paging=false';
            }
            
            var promise = $http.get( url ).then(function(response){                                
                return response.data;
            });            
            return promise;
        },                
        update: function(tei, optionSets, attributesById){
            var formattedTei = angular.copy(tei);
            angular.forEach(formattedTei.attributes, function(att){                        
                att.value = AttributesFactory.formatAttributeValue(att, attributesById, optionSets, 'API');                                                                
            });
            var promise = $http.put( '../api/trackedEntityInstances/' + formattedTei.trackedEntityInstance , formattedTei ).then(function(response){                    
                return response.data;
            });
            
            return promise;
        },
        register: function(tei, optionSets, attributesById){
            var formattedTei = angular.copy(tei);
            angular.forEach(formattedTei.attributes, function(att){                        
                att.value = AttributesFactory.formatAttributeValue(att, attributesById, optionSets, 'API');                                                                
            });
            
            var promise = $http.put( '../api/trackedEntityInstances' , formattedTei ).then(function(response){                    
                return response.data;
            });            
            return promise;            
        },
        processAttributes: function(selectedTei, selectedProgram, selectedEnrollment){
            var def = $q.defer();            
            if(selectedTei.attributes){
                if(selectedProgram && selectedEnrollment){
                    //show attribute for selected program and enrollment
                    AttributesFactory.getByProgram(selectedProgram).then(function(atts){
                        selectedTei.attributes = AttributesFactory.showRequiredAttributes(atts,selectedTei.attributes, true);
                        def.resolve(selectedTei);
                    }); 
                }
                if(selectedProgram && !selectedEnrollment){
                    //show attributes for selected program            
                    AttributesFactory.getByProgram(selectedProgram).then(function(atts){    
                        selectedTei.attributes = AttributesFactory.showRequiredAttributes(atts,selectedTei.attributes, false);
                        def.resolve(selectedTei);
                    }); 
                }
                if(!selectedProgram && !selectedEnrollment){
                    //show attributes in no program            
                    AttributesFactory.getWithoutProgram().then(function(atts){                
                        selectedTei.attributes = AttributesFactory.showRequiredAttributes(atts,selectedTei.attributes, false);     
                        def.resolve(selectedTei);
                    });
                }
            }       
            return def.promise;
        }
    };
})

/* Factory for getting tracked entity attributes */
.factory('AttributesFactory', function($q, $rootScope, TCStorageService, orderByFilter, DateUtils, OptionSetService) {      

    return {
        getAll: function(){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('attributes').done(function(attributes){                    
                    $rootScope.$apply(function(){
                        def.resolve(attributes);
                    });
                });
            });            
            return def.promise;            
        }, 
        getByProgram: function(program){
            var def = $q.defer();
            this.getAll().then(function(atts){                
                
                if(program && program.id){
                    var attributes = [];
                    var programAttributes = [];
                    angular.forEach(atts, function(attribute){
                        attributes[attribute.id] = attribute;
                    });

                    angular.forEach(program.programTrackedEntityAttributes, function(pAttribute){
                        var att = attributes[pAttribute.trackedEntityAttribute.id];
                        att.mandatory = pAttribute.mandatory;
                        if(pAttribute.displayInList){
                            att.displayInListNoProgram = true;
                        }                    
                        programAttributes.push(att);                
                    });
                    
                    def.resolve(programAttributes);
                }                
                else{
                    var attributes = [];
                    angular.forEach(atts, function(attribute){
                        if (attribute.displayInListNoProgram) {
                            attributes.push(attribute);
                        }
                    });     
                    
                    attributes = orderByFilter(attributes, '-sortOrderInListNoProgram').reverse();
                    def.resolve(attributes);
                }                
            });
            return def.promise;    
        },
        getWithoutProgram: function(){   
            
            var def = $q.defer();
            this.getAll().then(function(atts){
                var attributes = [];
                angular.forEach(atts, function(attribute){
                    if (attribute.displayInListNoProgram) {
                        attributes.push(attribute);
                    }
                });     
                def.resolve(attributes);             
            });     
            return def.promise;
        },        
        getMissingAttributesForEnrollment: function(tei, program){
            var def = $q.defer();
            this.getByProgram(program).then(function(atts){
                var programAttributes = atts;
                var existingAttributes = tei.attributes;
                var missingAttributes = [];
                
                for(var i=0; i<programAttributes.length; i++){
                    var exists = false;
                    for(var j=0; j<existingAttributes.length && !exists; j++){
                        if(programAttributes[i].id === existingAttributes[j].attribute){
                            exists = true;
                        }
                    }
                    if(!exists){
                        missingAttributes.push(programAttributes[i]);
                    }
                }
                def.resolve(missingAttributes);
            });            
            return def.promise();            
        },
        showRequiredAttributes: function(requiredAttributes, teiAttributes, fromEnrollment){        
            
            //first reset teiAttributes
            for(var j=0; j<teiAttributes.length; j++){
                teiAttributes[j].show = false;
            }

            //identify which ones to show
            for(var i=0; i<requiredAttributes.length; i++){
                var processed = false;
                for(var j=0; j<teiAttributes.length && !processed; j++){
                    if(requiredAttributes[i].id === teiAttributes[j].attribute){                    
                        processed = true;
                        teiAttributes[j].show = true;
                        teiAttributes[j].order = i;
                        teiAttributes[j].mandatory = requiredAttributes[i].mandatory ? requiredAttributes[i].mandatory : false;
                        teiAttributes[j].allowFutureDate = requiredAttributes[i].allowFutureDate ? requiredAttributes[i].allowFutureDate : false;
                        teiAttributes[j].displayName = requiredAttributes[i].name;
                    }
                }

                if(!processed && fromEnrollment){//attribute was empty, so a chance to put some value
                    teiAttributes.push({show: true, order: i, allowFutureDate: requiredAttributes[i].allowFutureDate ? requiredAttributes[i].allowFutureDate : false, mandatory: requiredAttributes[i].mandatory ? requiredAttributes[i].mandatory : false, attribute: requiredAttributes[i].id, displayName: requiredAttributes[i].name, type: requiredAttributes[i].valueType, value: ''});
                }                   
            }

            teiAttributes = orderByFilter(teiAttributes, '-order');
            teiAttributes.reverse();
            return teiAttributes;
        },
        formatAttributeValue: function(att, attsById, optionSets, destination){
            var val = att.value;
            var type = '';
            if(att.type){
                type = att.type;
            }            
            if(att.valueType){
                type = att.valueType;
            }
            if(type === 'trueOnly'){
                if(destination === 'USER'){
                    val = val === 'true' ? true : '';
                }
                else{
                    val = val === true ? 'true' : '';
                }                
            }
            else{
                if(val){
                    if(type === 'date'){
                        if(destination === 'USER'){
                            val = DateUtils.formatFromApiToUser(val);
                        }
                        else{
                            val = DateUtils.formatFromUserToApi(val);
                        }                        
                    }
                    if(type === 'optionSet' && 
                            attsById[att.attribute] && 
                            attsById[att.attribute].optionSet && 
                            attsById[att.attribute].optionSet.id && 
                            optionSets[attsById[att.attribute].optionSet.id]){
                        if(destination === 'USER'){
                            val = OptionSetService.getName(optionSets[attsById[att.attribute].optionSet.id].options, val);                                
                        }
                        else{
                            val = OptionSetService.getCode(optionSets[attsById[att.attribute].optionSet.id].options, val);                                
                        }                        
                    }                    
                }
            }
            return val;
        }
    };
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, $q) {   
    
    return {     
        
        getEventsByStatus: function(entity, orgUnit, program, programStatus){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&orgUnit=' + orgUnit + '&program=' + program + '&programStatus=' + programStatus  + '&paging=false').then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        getEventsByProgram: function(entity, program){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&program=' + program + '&paging=false').then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        getByOrgUnitAndProgram: function(orgUnit, ouMode, program, startDate, endDate){
            var url;
            if(startDate && endDate){
                url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&program=' + program + '&startDate=' + startDate + '&endDate=' + endDate + '&paging=false';
            }
            else{
                url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&program=' + program + '&paging=false';
            }
            var promise = $http.get( url ).then(function(response){
                return response.data.events;
            });            
            return promise;
        },
        get: function(eventUid){            
            var promise = $http.get('../api/events/' + eventUid + '.json').then(function(response){               
                return response.data;
            });            
            return promise;
        },        
        create: function(dhis2Event){    
            var promise = $http.post('../api/events.json', dhis2Event).then(function(response){
                return response.data;           
            });
            return promise;            
        },
        delete: function(dhis2Event){
            var promise = $http.delete('../api/events/' + dhis2Event.event).then(function(response){
                return response.data;               
            });
            return promise;           
        },
        update: function(dhis2Event){   
            var promise = $http.put('../api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;         
            });
            return promise;
        },        
        updateForSingleValue: function(singleValue){   
            var promise = $http.put('../api/events/' + singleValue.event + '/' + singleValue.dataValues[0].dataElement, singleValue ).then(function(response){
                return response.data;
            });
            return promise;
        },
        updateForNote: function(dhis2Event){   
            var promise = $http.put('../api/events/' + dhis2Event.event + '/addNote', dhis2Event).then(function(response){
                return response.data;         
            });
            return promise;
        },
        updateForEventDate: function(dhis2Event){
            var promise = $http.put('../api/events/' + dhis2Event.event + '/updateEventDate', dhis2Event).then(function(response){
                return response.data;         
            });
            return promise;
        }
    };    
})

/* factory for handling event reports */
.factory('EventReportService', function($http, $q) {   
    
    return {        
        getEventReport: function(orgUnit, ouMode, program, startDate, endDate, programStatus, eventStatus, pager){ 
            var pgSize = pager ? pager.pageSize : 50;
        	var pg = pager ? pager.page : 1;
            pgSize = pgSize > 1 ? pgSize  : 1;
            pg = pg > 1 ? pg : 1; 
            var url = '../api/events/eventRows.json?' + 'orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&program=' + program + '&programStatus=' + programStatus + '&eventStatus='+ eventStatus + '&pageSize=' + pgSize + '&page=' + pg;
            if(startDate && endDate){
                url = url + '&startDate=' + startDate + '&endDate=' + endDate ;
            }
            var promise = $http.get( url ).then(function(response){
                return response.data;
            });            
            return promise;
        }
    };    
})

.factory('OperatorFactory', function(){
    
    var defaultOperators = ['IS', 'RANGE' ];
    var boolOperators = ['yes', 'no'];
    return{
        defaultOperators: defaultOperators,
        boolOperators: boolOperators
    };  
})

    /* Returns a function for getting rules for a specific program */
.factory('TrackerRulesFactory', function($q,$rootScope,TCStorageService){
    return{
        getOldProgramStageRules :function(programUid, programstageUid) {
            var rules = this.getProgramRules(programUid);
            
            //Only keep the rules actually matching the program stage we are in, or rules with no program stage defined.
            var programStageRules = [];
            angular.forEach(rules, function(rule) {
                if(rule.programstage_uid == null || rule.programstage_uid == "" || rule.programstage_uid == programstageUid) {
                   programStageRules.push(rule);
                }
            });
            
            return programStageRules;
        },
        
        getProgramStageRules : function(programUid, programStageUid){
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('programRules').done(function(rules){ 
                    TCStorageService.currentStore.getAll('programRuleActions').done(function(actions){
                        //The hash will serve as a direct-lookup for linking the actions to rules later.
                        var programRulesHash = {};
                        //The array will ultimately be returned to the caller.
                        var programRulesArray = [];
                        //Loop through and add the rules belonging to this program and program stage
                        angular.forEach(rules, function(rule){
                           if(rule.program.id == programUid) {
                               if(!rule.programStage || !rule.programStage.id || rule.programStage.id == programStageUid) {
                                    rule.actions = [];
                                    programRulesHash[rule.id] = rule;
                                    programRulesArray.push(rule);
                                }
                           }
                        });
                        
                        //Loop through and attach all actions to the correct rules:
                        angular.forEach(actions, function(action){
                           if(programRulesHash[action.programRule.id])
                           {
                               programRulesHash[action.programRule.id].actions.push(action);
                           }
                        });
                        
                        $rootScope.$apply(function(){
                            def.resolve(programRulesArray);
                        });
                    });
                });     
            });
                        
            return def.promise;
        }
    };  
})

/* Returns user defined variable names and their corresponding UIDs and types for a specific program */
.factory('TrackerRuleVariableFactory', function($rootScope, $q, TCStorageService){
    return{
        getProgramRuleVariables : function(programUid){
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                
                TCStorageService.currentStore.getAll('programRuleVariables').done(function(variables){
                    
                    //The array will ultimately be returned to the caller.
                    var programRuleVariablesArray = [];
                    //Loop through and add the variables belonging to this program
                    angular.forEach(variables, function(variable){
                       if(variable.program.id == programUid) {
                            programRuleVariablesArray.push(variable);
                       }
                    });

                    $rootScope.$apply(function(){
                        def.resolve(programRuleVariablesArray);
                    });
                });
            });
                        
            return def.promise;
        }
    };
})

/* Returns user defined variable names and their corresponding UIDs and types for a specific program */
.factory('TrackerWidgetsConfigurationFactory', function(){
    return{
        getWidgetConfiguration : function(programUid){
            //If no config exists, return default config
            
            return [
                {title: 'Details', type: 'rulebound', code:"det", show: true, expand: true, horizontalplacement:"left", index:0},
                {title: 'enrollment', type:'enrollment', show: false, expand: true, horizontalplacement:"left", index:1},
                {title: 'dataentry', type: 'dataentry', show: true, expand: true, horizontalplacement:"left", index:2},
                {title: 'report', type: 'report', show: false, expand: true, horizontalplacement:"left", index:3},
                {title: 'current_selections', type: 'current_selections', show: false, expand: true, horizontalplacement:"right", index:0},
                {title: 'profile', type: 'profile', show: false, expand: true, horizontalplacement:"right", index:1},
                {title: 'Conditions/Complications',  type:'rulebound', code:"con", show: true, expand: true, horizontalplacement:"right", index:2},
                {title: 'Reminders',  type:'rulebound', code:"rem", show: true, expand: true, horizontalplacement:"right", index:3},
                {title: 'relationships', type: 'relationships', show: false, expand: true, horizontalplacement:"right", index:4},
                {title: 'notes', type: 'notes', show: true, expand: true, horizontalplacement:"right", index:5},
                {title: 'Summary', type: 'rulebound', code:"sum", show: true, expand: true, horizontalplacement:"right", index:6}
            ];
        },
        getDefaultWidgetConfiguration: function() {
            return [
                {title: 'enrollment', type:'enrollment', show: true, expand: true, horizontalplacement:"left", index:0},
                {title: 'dataentry', type: 'dataentry', show: true, expand: true, horizontalplacement:"left", index:1},
                {title: 'report', type: 'report', show: true, expand: true, horizontalplacement:"left", index:2},
                {title: 'current_selections', type: 'current_selections', show: false, expand: true, horizontalplacement:"right", index:0},
                {title: 'profile', type: 'profile', show: true, expand: true, horizontalplacement:"right", index:1},
                {title: 'relationships', type: 'relationships', show: true, expand: true, horizontalplacement:"right", index:2},
                {title: 'notes', type: 'notes', show: true, expand: true, horizontalplacement:"right", index:3}
            ];
        }
    };
            
})

.service('EntityQueryFactory', function(OperatorFactory, DateUtils){  
    
    this.getAttributesQuery = function(attributes, enrollment){

        var query = {url: null, hasValue: false};
        
        angular.forEach(attributes, function(attribute){           

            if(attribute.valueType === 'date' || attribute.valueType === 'number'){
                var q = '';
                
                if(attribute.operator === OperatorFactory.defaultOperators[0]){
                    if(attribute.exactValue && attribute.exactValue !== ''){
                        query.hasValue = true;
                        if(attribute.valueType === 'date'){
                            attribute.exactValue = DateUtils.formatFromUserToApi(attribute.exactValue);
                        }
                        q += 'EQ:' + attribute.exactValue + ':';
                    }
                }                
                if(attribute.operator === OperatorFactory.defaultOperators[1]){
                    if(attribute.startValue && attribute.startValue !== ''){
                        query.hasValue = true;
                        if(attribute.valueType === 'date'){
                            attribute.startValue = DateUtils.formatFromUserToApi(attribute.startValue);
                        }
                        q += 'GT:' + attribute.startValue + ':';
                    }
                    if(attribute.endValue && attribute.endValue !== ''){
                        query.hasValue = true;
                        if(attribute.valueType === 'date'){
                            attribute.endValue = DateUtils.formatFromUserToApi(attribute.endValue);
                        }
                        q += 'LT:' + attribute.endValue + ':';
                    }
                }                
                if(query.url){
                    if(q){
                        q = q.substr(0,q.length-1);
                        query.url = query.url + '&filter=' + attribute.id + ':' + q;
                    }
                }
                else{
                    if(q){
                        q = q.substr(0,q.length-1);
                        query.url = 'filter=' + attribute.id + ':' + q;
                    }
                }
            }
            else{
                if(attribute.value && attribute.value !== ''){                    
                    query.hasValue = true;                

                    if(angular.isArray(attribute.value)){
                        var q = '';
                        angular.forEach(attribute.value, function(val){                        
                            q += val + ';';
                        });

                        q = q.substr(0,q.length-1);

                        if(query.url){
                            if(q){
                                query.url = query.url + '&filter=' + attribute.id + ':IN:' + q;
                            }
                        }
                        else{
                            if(q){
                                query.url = 'filter=' + attribute.id + ':IN:' + q;
                            }
                        }                    
                    }
                    else{                        
                        if(query.url){
                            query.url = query.url + '&filter=' + attribute.id + ':LIKE:' + attribute.value;
                        }
                        else{
                            query.url = 'filter=' + attribute.id + ':LIKE:' + attribute.value;
                        }
                    }
                }
            }            
        });
        
        if(enrollment){
            var q = '';
            if(enrollment.operator === OperatorFactory.defaultOperators[0]){
                if(enrollment.programExactDate && enrollment.programExactDate !== ''){
                    query.hasValue = true;
                    q += '&programStartDate=' + DateUtils.formatFromUserToApi(enrollment.programExactDate) + '&programEndDate=' + DateUtils.formatFromUserToApi(enrollment.programExactDate);
                }
            }
            if(enrollment.operator === OperatorFactory.defaultOperators[1]){
                if(enrollment.programStartDate && enrollment.programStartDate !== ''){                
                    query.hasValue = true;
                    q += '&programStartDate=' + DateUtils.formatFromUserToApi(enrollment.programStartDate);
                }
                if(enrollment.programEndDate && enrollment.programEndDate !== ''){
                    query.hasValue = true;
                    q += '&programEndDate=' + DateUtils.formatFromUserToApi(enrollment.programEndDate);
                }
            }            
            if(q){
                if(query.url){
                    query.url = query.url + q;
                }
                else{
                    query.url = q;
                }
            }            
        }
        return query;
        
    };   
    
    this.resetAttributesQuery = function(attributes, enrollment){
        
        angular.forEach(attributes, function(attribute){
            attribute.exactValue = '';
            attribute.startValue = '';
            attribute.endValue = '';
            attribute.value = '';           
        });
        
        if(enrollment){
            enrollment.programStartDate = '';
            enrollment.programEndDate = '';          
        }        
        return attributes;        
    }; 
})

/* current selections */
.service('CurrentSelection', function(){
    this.currentSelection = '';
    this.relationshipInfo = '';
    this.optionSets = null;
    this.attributesById = null;
    this.ouLevels = null;
    
    this.set = function(currentSelection){  
        this.currentSelection = currentSelection;        
    };    
    this.get = function(){
        return this.currentSelection;
    };
    
    this.setRelationshipInfo = function(relationshipInfo){  
        this.relationshipInfo = relationshipInfo;        
    };    
    this.getRelationshipInfo = function(){
        return this.relationshipInfo;
    };
    
    this.setOptionSets = function(optionSets){
        this.optionSets = optionSets;
    };
    this.getOptionSets = function(){
        return this.optionSets;
    };    
    
    this.setAttributesById = function(attributesById){
        this.attributesById = attributesById;
    };
    this.getAttributesById = function(){
        return this.attributesById;
    }; 
    
    this.setOuLevels = function(ouLevels){
        this.ouLevels = ouLevels;
    };
    this.getOuLevels = function(){
        return this.ouLevels;
    };
})

.service('TEIGridService', function(OrgUnitService, OptionSetService, DateUtils, $translate, AttributesFactory){
    
    return {
        format: function(grid, map, optionSets){
            if(!grid || !grid.rows){
                return;
            }
            
            //grid.headers[0-4] = Instance, Created, Last updated, Org unit, Tracked entity
            //grid.headers[5..] = Attribute, Attribute,.... 
            var attributes = [];
            for(var i=5; i<grid.headers.length; i++){
                attributes.push({id: grid.headers[i].name, name: grid.headers[i].column, type: grid.headers[i].type});
            }

            var entityList = [];
            
            AttributesFactory.getAll().then(function(atts){
                
                var attributes = [];
                angular.forEach(atts, function(att){
                    attributes[att.id] = att;
                });
            
                OrgUnitService.open().then(function(){

                    angular.forEach(grid.rows, function(row){
                        var entity = {};
                        var isEmpty = true;

                        entity.id = row[0];
                        entity.created = DateUtils.formatFromApiToUser( row[1] );
                        entity.orgUnit = row[3];                              
                        entity.type = row[4];

                        OrgUnitService.get(row[3]).then(function(ou){
                            if(ou){
                                entity.orgUnitName = ou.n;
                            }                                                       
                        });

                        for(var i=5; i<row.length; i++){
                            if(row[i] && row[i] !== ''){
                                isEmpty = false;
                                var val = row[i];
                                
                                if(attributes[grid.headers[i].name] && 
                                        attributes[grid.headers[i].name].valueType === 'optionSet' && 
                                        optionSets &&    
                                        attributes[grid.headers[i].name].optionSet &&
                                        optionSets[attributes[grid.headers[i].name].optionSet.id] ){
                                    val = OptionSetService.getName(optionSets[attributes[grid.headers[i].name].optionSet.id].options, val);
                                }
                                if(attributes[grid.headers[i].name] && attributes[grid.headers[i].name].valueType === 'date'){                                    
                                    val = DateUtils.formatFromApiToUser( val );
                                }
                                
                                entity[grid.headers[i].name] = val;
                            }
                        }

                        if(!isEmpty){
                            if(map){
                                entityList[entity.id] = entity;
                            }
                            else{
                                entityList.push(entity);
                            }
                        }
                    });                
                });
            }); 
            return {headers: attributes, rows: entityList, pager: grid.metaData.pager};                                    
        },
        generateGridColumns: function(attributes, ouMode){
            
            var columns = attributes ? angular.copy(attributes) : [];
       
            //also add extra columns which are not part of attributes (orgunit for example)
            columns.push({id: 'orgUnitName', name: $translate('registering_unit'), valueType: 'string', displayInListNoProgram: false});
            columns.push({id: 'created', name: $translate('registration_date'), valueType: 'date', displayInListNoProgram: false});

            //generate grid column for the selected program/attributes
            angular.forEach(columns, function(column){
                if(column.id === 'orgUnitName' && ouMode !== 'SELECTED'){
                    column.show = true;    
                }

                if(column.displayInListNoProgram || column.displayInList){
                    column.show = true;
                }  
                column.showFilter = false;
            });
            return columns;  
        },
        getData: function(rows, columns){
            var data = [];
            angular.forEach(rows, function(row){
                var d = {};
                angular.forEach(columns, function(col){
                    if(col.show){
                        d[col.name] = row[col.id];
                    }                
                });
                data.push(d);            
            });
            return data;
        },
        getHeader: function(columns){
            var header = []; 
            angular.forEach(columns, function(col){
                if(col.show){
                    header.push($translate(col.name));
                }
            });        
            return header;
        }
    };
})

.service('EventUtils', function(DateUtils, CalendarService, OptionSetService, $filter, orderByFilter){
    return {
        createDummyEvent: function(eventsPerStage, programStage, orgUnit, enrollment){
            var today = DateUtils.getToday();    
            var dueDate = this.getEventDueDate(eventsPerStage, programStage, enrollment);
            var dummyEvent = {programStage: programStage.id, 
                              orgUnit: orgUnit.id,
                              orgUnitName: orgUnit.name,
                              dueDate: dueDate,
                              sortingDate: dueDate,
                              name: programStage.name,
                              reportDateDescription: programStage.reportDateDescription,
                              enrollmentStatus: 'ACTIVE',
                              status: 'SCHEDULED'};
            
            if(programStage.captureCoordinates){
                dummyEvent.coordinate = {};
            }
            
            dummyEvent.statusColor = 'alert-warning';//'stage-on-time';
            if(moment(today).isAfter(dummyEvent.dueDate)){
                dummyEvent.statusColor = 'alert-danger';//'stage-overdue';
            }
            return dummyEvent;        
        },
        getEventStatusColor: function(dhis2Event){    
            var eventDate = DateUtils.getToday();
            var calendarSetting = CalendarService.getSetting();
            
            if(dhis2Event.eventDate){
                eventDate = dhis2Event.eventDate;
            }
    
            if(dhis2Event.status === 'COMPLETED'){
                return 'alert-success';//'stage-completed';
            }
            else if(dhis2Event.status === 'SKIPPED'){
                return 'alert-default'; //'stage-skipped';
            }
            else{                
                if(dhis2Event.eventDate){
                    return 'alert-info'; //'stage-executed';
                }
                else{
                    if(moment(eventDate, calendarSetting.momentFormat).isAfter(dhis2Event.dueDate)){
                        return 'alert-danger';//'stage-overdue';
                    }                
                    return 'alert-warning';//'stage-on-time';
                }               
            }            
        },
        getEventDueDate: function(eventsByStage, programStage, enrollment){            
            var referenceDate = enrollment.dateOfIncident ? enrollment.dateOfIncident : enrollment.dateOfEnrollment,
                offset = programStage.minDaysFromStart,
                calendarSetting = CalendarService.getSetting();
        
            if(programStage.generatedByEnrollmentDate){
                referenceDate = enrollment.dateOfEnrollment;
            }
            
            if(programStage.repeatable){
                var evs = [];                
                angular.forEach(eventsByStage, function(ev){
                    if(ev.eventDate){
                        evs.push(ev);
                    }
                });
                
                if(evs.length > 0){
                    evs = orderByFilter(evs, '-eventDate');
                    referenceDate = evs[0].eventDate;
                    offset = programStage.standardInterval;
                }                
            }            
            
            var dueDate = moment(referenceDate, calendarSetting.momentFormat).add('d', offset)._d;
            dueDate = $filter('date')(dueDate, calendarSetting.keyDateFormat); 
            return dueDate;
        },
        reconstruct: function(dhis2Event, programStage, optionSets){
            
            var e = {dataValues: [], 
                    event: dhis2Event.event, 
                    program: dhis2Event.program, 
                    programStage: dhis2Event.programStage, 
                    orgUnit: dhis2Event.orgUnit, 
                    trackedEntityInstance: dhis2Event.trackedEntityInstance,
                    status: dhis2Event.status,
                    dueDate: DateUtils.formatFromUserToApi(dhis2Event.dueDate)
                };
                
            angular.forEach(programStage.programStageDataElements, function(prStDe){
                if(dhis2Event[prStDe.dataElement.id]){                    
                    var value = dhis2Event[prStDe.dataElement.id];
                    
                    if( value && prStDe.dataElement.type === 'string' && prStDe.dataElement.optionSet && optionSets[prStDe.dataElement.optionSet.id]){
                        value = OptionSetService.getCode(optionSets[prStDe.dataElement.optionSet.id].options, value);
                    }                    
                    if( value && prStDe.dataElement.type === 'date'){
                        value = DateUtils.formatFromUserToApi(value);
                    }
                    if( prStDe.dataElement.type === 'trueOnly' ){
                        if(value){
                            value = 'true';
                        }
                        else{
                            value = '';
                        }
                    }
                    
                    var val = {value: value, dataElement: prStDe.dataElement.id};
                    if(dhis2Event.providedElsewhere[prStDe.dataElement.id]){
                        val.providedElsewhere = dhis2Event.providedElsewhere[prStDe.dataElement.id];
                    }
                    e.dataValues.push(val);
                }                                
            });
            
            if(programStage.captureCoordinates){
                e.coordinate = {latitude: dhis2Event.coordinate.latitude ? dhis2Event.coordinate.latitude : 0,
                                longitude: dhis2Event.coordinate.longitude ? dhis2Event.coordinate.longitude : 0};
            }
            
            if(dhis2Event.eventDate){
                e.eventDate = DateUtils.formatFromUserToApi(dhis2Event.eventDate);
            }
            
            return e;
        }
    }; 
})

/* service for building variables based on the data in users fields */
.service('VariableService', function($rootScope,$q,TrackerRuleVariableFactory,$filter,orderByFilter,$log){
    return {
        getVariables: function($scope) {
            var thePromisedVariables = $q.defer();
            var variables = [];
            
            $scope.pushVariable = function(variablename, variablevalue, variabletype, variablefound) {
                //First clean away single or double quotation marks at the start and end of the variable name.
                variablevalue = $filter('trimquotes')(variablevalue);
                
                //Append single quotation marks in case the variable is of text type:
                if(variabletype === 'string') {
                    variablevalue = "'" + variablevalue + "'";
                }
                else if(variabletype === 'date') {
                    variablevalue = "'" + variablevalue + "'";
                }
                else if(variabletype === 'bool') {
                    if(eval(variablevalue)) {
                        variablevalue = true;
                    }
                    else {    
                        variablevalue = false;
                    }
                }
                else if(variabletype === "int" || variabletype === "number") {
                    variablevalue = Number(variablevalue);
                }
                else{
                    $log.warn("unknown datatype:" + variabletype);
                }
                 
                
                //Make sure that the variablevalue does not contain a dollar sign anywhere 
                //- this would potentially mess up later use of the variable:
//                if(angular.isDefined(variablevalue) 
//                        && variablevalue !== null
//                        && variablevalue.indexOf("$") !== -1 ) {
//                    variablevalue = variablevalue.replace(/\\$/,"");
//                }
                
                //TODO:
                //Also clean away instructions that might be erroneusly evalutated in javascript

                variables.push({variablename:variablename,
                                variablevalue:variablevalue,
                                variabletype:variabletype,
                                hasValue:variablefound
                            });
            };
            
            TrackerRuleVariableFactory.getProgramRuleVariables($scope.currentEvent.program).then(function(programVariables){

                // The following section will need a different implementation for event capture:
                var allEventsSorted = [];
                var currentEvent = $scope.currentEvent;
                var eventsSortedPerProgramStage = [];
                
                for(var key in $scope.eventsByStage){
                    if($scope.eventsByStage.hasOwnProperty(key)){
                        eventsSortedPerProgramStage[key] = [];
                        angular.forEach($scope.eventsByStage[key], function(event){
                            allEventsSorted.push(event);
                            eventsSortedPerProgramStage[key].push(event);
                        });
                        eventsSortedPerProgramStage[key] = orderByFilter(eventsSortedPerProgramStage[key], '+eventDate').reverse(); 
                    }
                }
                allEventsSorted = orderByFilter(allEventsSorted, '+eventDate').reverse(); 
                
                var allDes = {};
                angular.forEach($scope.programStages, function(programStage){
                    angular.forEach(programStage.programStageDataElements, function(dataElement) {
                        allDes[dataElement.dataElement.id] = dataElement;
                    });
                });
                //End of region that neeeds specific implementation for event capture
                
                angular.forEach(programVariables, function(programVariable) {
                    var valueFound = false;
                    if(programVariable.sourceType === "DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE"){
                        angular.forEach(eventsSortedPerProgramStage[programVariable.programStage.id], function(event) {
                            if(angular.isDefined(event[programVariable.dataElement.id])
                                    && event[programVariable.dataElement.id] !== null ){
                                valueFound = true;
                                $scope.pushVariable(programVariable.name, event[programVariable.dataElement.id], allDes[programVariable.dataElement.id].dataElement.type, valueFound );
                            }

                        });
                    }
                    else if(programVariable.sourceType === "DATAELEMENT_NEWEST_EVENT_PROGRAM"){
                        angular.forEach(allEventsSorted, function(event) {
                            if(angular.isDefined(event[programVariable.dataElement.id])
                                    && event[programVariable.dataElement.id] !== null ){
                                valueFound = true;
                                 $scope.pushVariable(programVariable.name, event[programVariable.dataElement.id], allDes[programVariable.dataElement.id].dataElement.type, valueFound );
                             }
                        });
                    }
                    else if(programVariable.sourceType === "DATAELEMENT_CURRENT_EVENT"){
                        if(angular.isDefined(currentEvent[programVariable.dataElement.id])
                                && currentEvent[programVariable.dataElement.id] !== null ){
                            valueFound = true;
                            $scope.pushVariable(programVariable.name, currentEvent[programVariable.dataElement.id], allDes[programVariable.dataElement.id].dataElement.type, valueFound );
                        }      
                    }
                    else if(programVariable.sourceType === "DATAELEMENT_PREVIOUS_EVENT"){
                        //Only continue checking for a value if there is more than one event.
                        if(allEventsSorted && allEventsSorted.length > 1) {
                            for(var i = 1; i < allEventsSorted.length; i++) {
                                //When we reach the current stage, and the current stage is not the oldest stage(allEventsSorted[0] is the oldest), 
                                //check if the value we want exists in the previous stage.
                                if(allEventsSorted[i].programStage !== currentEvent.programStage) {
                                    //Fetch the value from the previous event.
                                    var previousvalue = allEventsSorted[i-1][programVariable.dataElement.id];
                                    if(angular.isDefined(previousvalue) 
                                            && previousvalue !== null ) {
                                        valueFound = true;
                                        $scope.pushVariable(programVariable.name, previousvalue, allDes[programVariable.dataElement.id].dataElement.type, valueFound );
                                    }
                                }
                            }
                        }
                    }
                    else if(programVariable.sourceType === "TEI_ATTRIBUTE"){
                        angular.forEach($scope.selectedEntity.attributes , function(attribute) {
                            if(!valueFound) {
                                if(attribute.attribute === programVariable.attribute.id) {
                                    valueFound = true;
                                    $scope.pushVariable(programVariable.name, attribute.value, attribute.type, valueFound );
                                }
                            }
                        });
                    }
                    else if(programVariable.sourceType === "CALCULATED_VALUE"){
                        //We won't assign the calculated variables at this step. The rules execution will calculate and assign the variable.
                    }
                    else {
                        //Missing handing of ruletype
                        $log.warn("Unknown sourceType:" + programVariable.sourceType);
                    }

                   
                    if(!valueFound){
                        //If there is still no value found, assign default value:
                        if(programVariable.dataElement) {
                            $scope.pushVariable(programVariable.name, "", allDes[programVariable.dataElement.id].dataElement.type );
                        }
                        else {
                            $scope.pushVariable(programVariable.name, "", "string" );
                        }
                    }
                });

                //add context variables:
                //last parameter "valuefound" is always true for event date
                $scope.pushVariable('eventdate', currentEvent.eventDate, 'date', true );
                
                thePromisedVariables.resolve(variables);
            });
            
            return thePromisedVariables.promise;
        }
    };
})
       


/* service for executing tracker rules and broadcasting results */
.service('TrackerRulesExecutionService', function(TrackerRulesFactory,VariableService, $rootScope, $log, $filter, orderByFilter){
    return {
        executeRules: function($scope) {
            //When debugging rules, the caller should provide a variable for wether or not the rules is being debugged.
            //hard coding this for now:
            var debug = true;
            var verbose = true;
            
            var variablesHash = {};
            var variablesWithValueHash = {};
                    
            $scope.replaceVariables = function(expression) {
                //replaces the variables in an expression with actual variable values.
                //First check if the expression contains variables at all(any dollar signs):
                if(expression.indexOf('$') !== -1) {
                    //Find every variable name in the expression;
                    var variablespresent = expression.match(/\$[a-zA-Z0-9]*/g);
                    //Replace each matched variable:
                    angular.forEach(variablespresent, function(variablepresent) {
                        //First strip away any dollar signs from the variable name:
                        variablepresent = variablepresent.replace("$","");
                        
                        if(angular.isDefined(variablesHash[variablepresent])) {
                            //Replace all occurrences of the variable name(hence using regex replacement):
                            expression = expression.replace(new RegExp("\\$" + variablepresent, 'g'),
                                variablesHash[variablepresent]);
                        }
                        else {
                            $log.warn("Expression " + expression + " conains variable " + variablepresent 
                                    + " - but this variable is not defined." )
                        }
                            
                    });
                }
                return expression;
            };
            
            $scope.runDhisFunctions = function(expression) {
                //Called from "runExpression". Only proceed with this logic in case there seems to be dhis function calls: "dhis." is present.
                if(angular.isDefined(expression) && expression.indexOf("dhis.") !== -1){   
                    var dhisFunctions = [{name:"dhis.daysbetween",parameters:2},
                                        {name:"dhis.floor",parameters:1},
                                        {name:"dhis.modulus",parameters:2},
                                        {name:"dhis.hasValue",parameters:1},
                                        {name:"dhis.concatenate"}];
                    
                    angular.forEach(dhisFunctions, function(dhisFunction){
                        //Replace each * with a regex that matches each parameter, allowing commas only inside single quotation marks.
                        var regularExFunctionCall = new RegExp(dhisFunction.name.replace(".","\\.") + "\\([^\\)]*\\)",'g');
                        var callsToThisFunction = expression.match(regularExFunctionCall);
                        angular.forEach(callsToThisFunction, function(callToThisFunction){
                            //Remove the function name and paranthesis:
                            var justparameters = callToThisFunction.replace(/(^[^\(]+\()|\)$/g,"");
                            //Then split into single parameters:
                            var parameters = justparameters.match(/(('[^']+')|([^,]+))/g);
                            
                            //Show error if no parameters is given and the function requires parameters,
                            //or if the number of parameters is wrong.
                            if(angular.isDefined(dhisFunction.parameters)){
                                //But we are only checking parameters where the dhisFunction actually has a defined set of parameters(concatenate, for example, does not have a fixed number);
                                if((!angular.isDefined(parameters) && dhisFunction.parameters > 0)
                                        || parameters.length !== dhisFunction.parameters){
                                    $log.warn(dhisFunction.name + " was called with the incorrect number of parameters");
                                }
                            }

                            //In case the function call is nested, the parameter itself contains an expression, run the expression.
                            if(angular.isDefined(parameters)) {
                                for (var i = 0; i < parameters.length; i++) {
                                    parameters[i] = $scope.runExpression(parameters[i],dhisFunction.name,"parameter:" + i);
                                }
                            }

                            //Special block for dhis.weeksBetween(*,*) - add such a block for all other dhis functions.
                            if(dhisFunction.name === "dhis.daysbetween")
                            {
                                var firstdate = $filter('trimquotes')(parameters[0]);
                                var seconddate = $filter('trimquotes')(parameters[1]);
                                firstdate = moment(firstdate);
                                seconddate = moment(seconddate);
                                //Replace the end evaluation of the dhis function:
                                expression = expression.replace(callToThisFunction, seconddate.diff(firstdate,'days'));
                            }
                            else if(dhisFunction.name === "dhis.floor")
                            {
                                var floored = Math.floor(parameters[0]);
                                //Replace the end evaluation of the dhis function:
                                expression = expression.replace(callToThisFunction, floored);
                            }
                            else if(dhisFunction.name === "dhis.modulus")
                            {
                                var dividend = Number(parameters[0]);
                                var divisor = Number(parameters[1]);
                                var rest = dividend % divisor;
                                //Replace the end evaluation of the dhis function:
                                expression = expression.replace(callToThisFunction, rest);
                            }
                            else if(dhisFunction.name === "dhis.hasValue")
                            {
                                //"evaluate" hasvalue to true or false:
                                if(variablesWithValueHash[parameters[0]]){
                                    expression = expression.replace(callToThisFunction, 'true');
                                } else {
                                    expression = expression.replace(callToThisFunction, 'false');
                                }
                            }
                            else if(dhisFunction.name === "dhis.concatenate")
                            {
                                var returnString = "";
                                for (var i = 0; i < parameters.length; i++) {
                                    returnString += parameters[i];
                                }
                                expression = expression.replace(callToThisFunction, returnString);
                            }
                        });
                    });
                }
                
                return expression;
            };
            
            $scope.runExpression = function(expression, beforereplacement, identifier ){
                //determine if expression is true, and actions should be effectuated
                //If DEBUG mode, use try catch and report errors. If not, omit the heavy try-catch loop.:
                var answer = false;
                if(debug) {
                    try{
                        
                        var dhisfunctionsevaluated = $scope.runDhisFunctions(expression);
                        answer = eval(dhisfunctionsevaluated);

                        if(verbose)
                        {
                            $log.info("Expression with id " + identifier + " was successfully run. Original condition was: " + beforereplacement + " - Evaluation ended up as:" + expression + " - Result of evaluation was:" + answer);
                        }
                    }
                    catch(e)
                    {
                        $log.warn("Expression with id " + identifier + " could not be run. Original condition was: " + beforereplacement + " - Evaluation ended up as:" + expression + " - error message:" + e);
                    }
                }
                else {
                    //Just run the expression. This is much faster than the debug route: http://jsperf.com/try-catch-block-loop-performance-comparison
                    var dhisfunctionsevaluated = $scope.runDhisFunctions(expression);
                    answer = eval(dhisfunctionsevaluated);
                }
                return answer;
            };
            
            
            VariableService.getVariables($scope).then(function(variables){
                TrackerRulesFactory.getProgramStageRules($scope.selectedProgram.id, $scope.currentStage.id).then(function(rules){
                    //But run rules in priority - lowest number first(priority null is last)
                    rules = orderByFilter(rules, 'priority');

                    //Make a variables hash to allow direct lookup:
                    angular.forEach(variables, function(variable) {
                        variablesHash[variable.variablename] = variable.variablevalue;
                    });
                    
                    //Make a variables-exists/hasvalue hash to allow direct lookup:
                    angular.forEach(variables, function(variable) {
                        variablesWithValueHash[variable.variablename] = variable.hasValue;
                    });

                    if(angular.isObject(rules) && angular.isArray(rules)){
                        //The program has rules, and we want to run them.
                        //Prepare repository unless it is already prepared:
                        if(angular.isUndefined( $rootScope.ruleeffects )){
                            $rootScope.ruleeffects = {};
                        }

                        var updatedEffectsExits = false;

                        angular.forEach(rules, function(rule) {
                            var expression = rule.condition;
                            //Go through and populate variables with actual values, but only if there actually is any replacements to be made(one or more "$" is present)
                            if(expression) {
                                if(expression.indexOf('$') !== -1) {
                                    expression = $scope.replaceVariables(expression);
                                }
                            } else {
                                debugger;
                            }

                            //run expression:
                            var ruleEffective = $scope.runExpression(expression, rule.condition, "rule:" + rule.id);

                            angular.forEach(rule.actions, function(action){
                                //In case the effect-hash is not populated, add entries
                                if(angular.isUndefined( $rootScope.ruleeffects[action.id] )){
                                    $rootScope.ruleeffects[action.id] =  {
                                        id:action.id,
                                        location:action.location, 
                                        action:action.programRuleActionType,
                                        dataElement:action.dataElement,
                                        content:action.content,
                                        data:action.data,
                                        ineffect:false
                                    };
                                }

                                //In case the rule is effective and contains specific data, 
                                //the effect be refreshed from the variables list.
                                //If the rule is not effective we can skip this step
                                if(ruleEffective && action.data)
                                {
                                    //The key data might be containing a dollar sign denoting that the key data is a variable.
                                    //To make a lookup in variables hash, we must make a lookup without the dollar sign in the variable name
                                    //The first strategy is to make a direct lookup. In case the "data" expression is more complex, we have to do more replacement and evaluation.

                                    var nameWithoutDollarSign = action.data.replace('$','');
                                    if(angular.isDefined(variablesHash[nameWithoutDollarSign]))
                                    {
                                        //The variable exists, and is replaced with its corresponding value
                                        $rootScope.ruleeffects[action.id].data =
                                            variablesHash[nameWithoutDollarSign];
                                    }
                                    else if(action.data.indexOf('$') !== -1)
                                    {
                                        //Since the value couldnt be looked up directly, and contains a dollar sign, the expression was more complex
                                        //Now we will have to make a thorough replacement and separate evaluation to find the correct value:
                                        $rootScope.ruleeffects[action.id].data = $scope.replaceVariables(action.data);
                                        //In a scenario where the data contains a complex expression, evaluate the expression to compile(calculate) the result:
                                        $rootScope.ruleeffects[action.id].data = $scope.runExpression($rootScope.ruleeffects[action.id].data, action.data, "action:" + action.id);
                                    }
                                }

                                //Update the rule effectiveness if it changed in this evaluation;
                                if($rootScope.ruleeffects[action.id].ineffect != ruleEffective)
                                {
                                    //There is a change in the rule outcome, we need to update the effect object.
                                    updatedEffectsExits = true;
                                    $rootScope.ruleeffects[action.id].ineffect = ruleEffective;
                                }

                                //In case the rule is of type "assign variable" and the rule is effective,
                                //the variable data result needs to be applied to the correct variable:
                                if($rootScope.ruleeffects[action.id].action === "ASSIGNVARIABLE" && $rootScope.ruleeffects[action.id].ineffect){
                                    //from earlier evaluation, the data portion of the ruleeffect now contains the value of the variable to be assign.
                                    //the content portion of the ruleeffect defines the name for the variable, when dollar is removed:
                                    var variabletoassign = $rootScope.ruleeffects[action.id].content.replace("$","");

                                    if(!angular.isDefined(variablesHash[variabletoassign])){
                                        $log.warn("Variable " + variabletoassign + " was not defined.");
                                    }

                                    //Even if the variable is not defined: we assign it:
                                    if(variablesHash[variabletoassign] !== $rootScope.ruleeffects[action.id].data){
                                        //If the variable was actually updated, we assume that there is an updated ruleeffect somewhere:
                                        updatedEffectsExits = true;
                                        //Then we assign the new value:
                                        variablesHash[variabletoassign] = $rootScope.ruleeffects[action.id].data;
                                    }
                                }
                            });
                        });

                        //Broadcast rules finished if there was any actual changes.
                        if(updatedEffectsExits){
                            $rootScope.$broadcast("ruleeffectsupdated");
                        }
                    }

                    return true;
                });
            });
        }
    };
});

