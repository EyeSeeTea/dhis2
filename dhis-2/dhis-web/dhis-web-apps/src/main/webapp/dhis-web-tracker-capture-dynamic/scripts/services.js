'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])

.factory('TCStorageService', function(){
    var store = new dhis2.storage.Store({
        name: "dhis2tc",
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['programs', 'programStages', 'trackedEntities', 'trackedEntityForms', 'attributes', 'relationshipTypes', 'optionSets', 'programValidations']
    });
    return{
        currentStore: store
    };
})

/* Factory to fetch geojsons */
.factory('GeoJsonFactory', function($q, $rootScope, TCStorageService) { 
    return {
        getAll: function(){

            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.getAll('geoJsons').done(function(geoJsons){
                    $rootScope.$apply(function(){
                        def.resolve(geoJsons);
                    });                    
                });
            });
            
            return def.promise;            
        },
        get: function(level){
            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('geoJsons', level).done(function(geoJson){                    
                    $rootScope.$apply(function(){
                        def.resolve(geoJson);
                    });
                });
            });                        
            return def.promise;            
        }
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
                return response.data === "" ? defaultLayout: response.data;
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
.factory('ProgramValidationFactory', function($q, $rootScope, ECStorageService) {  
    
    return {        
        get: function(uid){
            
            var def = $q.defer();
            
            ECStorageService.currentStore.open().done(function(){
                ECStorageService.currentStore.get('programValidations', uid).done(function(pv){                    
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
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity ).then(function(response){
                return convertFromApiToUser(response.data);
            });
            return promise;
        },
        getByEntityAndProgram: function( entity, program ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity + '&program=' + program ).then(function(response){
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
.factory('TEIService', function($http, $q, AttributesFactory, OptionSetService, CurrentSelection, DateUtils) {

    return {        
        convertFromApiToUser: function(promise, optionSets){            
            promise.then(function(response){
                var tei = response.data;
                var attsById = [];                
                AttributesFactory.getAll().then(function(atts){
                    angular.forEach(atts, function(att){                        
                        attsById[att.id] = att;
                    });

                    angular.forEach(tei.attributes, function(att){
                        if(attsById[att.attribute]){
                            att.displayName = attsById[att.attribute].name;
                        }
                        
                        if(att.type === 'trueOnly'){
                            if(att.value === 'true'){
                                att.value = true;
                            }
                            else{
                                att.value = '';
                            }
                        }
                        else{
                            var val = att.value;
                            if(val){
                                if(att.type === 'date'){
                                    val = DateUtils.formatFromApiToUser(val);
                                }
                                if(att.type === 'optionSet' && 
                                        attsById[att.attribute] && 
                                        attsById[att.attribute].optionSet && 
                                        attsById[att.attribute].optionSet.id && 
                                        optionSets[attsById[att.attribute].optionSet.id]){   
                                    val = OptionSetService.getName(optionSets[attsById[att.attribute].optionSet.id].options, val);                                
                                }
                                att.value = val;
                            }
                        }                                                
                    });                    
                });    
                return tei;
            });            
            return promise;
        },
        convertFromUserToApi: function(_tei, optionSets){            
            var attsById = [];      
            var def = $q.defer();
            
            var tei = angular.copy(_tei);
            AttributesFactory.getAll().then(function(atts){
                angular.forEach(atts, function(att){                        
                    attsById[att.id] = att;
                });

                angular.forEach(tei.attributes, function(att){                        
                    
                    if(att.type === 'trueOnly'){
                        if(att.value){
                            att.value = 'true';
                        }
                        else{
                            att.value = '';
                        }
                    }            
                    else{
                        var val = att.value;
                        if(val){
                            if(att.type === 'date'){
                                val = DateUtils.formatFromUserToApi(val);
                            }
                            if(att.type === 'optionSet' && 
                                    attsById[att.attribute] && 
                                    attsById[att.attribute].optionSet && 
                                    attsById[att.attribute].optionSet.id && 
                                    optionSets[attsById[att.attribute].optionSet.id]){   
                                val = OptionSetService.getCode(optionSets[attsById[att.attribute].optionSet.id].options, val);                                
                            }
                            att.value = val;
                        }
                    }                                            
                });     
                
                def.resolve(tei);
            });
            
            return def.promise;
        },        
        get: function(entityUid, optionSets){            
            var promise = $http.get(  '../api/trackedEntityInstances/' +  entityUid );
            this.convertFromApiToUser(promise, optionSets).then(function(response){
                return response.data; 
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
        update: function(tei, optionSets){   
            var url = '../api/trackedEntityInstances';
            var def = $q.defer();
           
            this.convertFromUserToApi(tei, optionSets).then(function(formattedTei){                
                $http.put( url + '/' + formattedTei.trackedEntityInstance , formattedTei ).then(function(response){                    
                    def.resolve( response.data );
                });
            });
            
            return def.promise;
        },
        register: function(tei, optionSets){            
            var url = '../api/trackedEntityInstances';
            var def = $q.defer();
            
            this.convertFromUserToApi(tei, optionSets).then(function(formattedTei){
                $http.post(url, formattedTei).then(function(response){
                    def.resolve( response.data );
                });
            });
            return def.promise;
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
        },
        reconstructForWebApi: function(attributes, attributesById, optionSets){
            
            var registrationAttributes = [];
            var formEmpty = true;
            angular.forEach(attributes, function(att){
                if(att.valueType === 'trueOnly'){
                    if(att.value){
                        registrationAttributes.push({attribute: att.id, value: 'true'});
                    }
                    else{
                        registrationAttributes.push({attribute: att.id, value: ''});
                    }
                    
                    formEmpty = false;
                }            
                else{
                    var val = att.value;
                    if(val){
                        if(att.valueType === 'date'){   
                            val = DateUtils.formatFromUserToApi(val);
                        }
                        if(att.valueType === 'optionSet' && 
                                attributesById[att.id] && 
                                attributesById[att.id].optionSet && 
                                optionSets[attributesById[att.id].optionSet.id]){                        
                            val = OptionSetService.getCode(optionSets[attributesById[att.id].optionSet.id].options, val);
                        }

                        registrationAttributes.push({attribute: att.id, value: val});
                        formEmpty = false;
                    }
                }                        
            });

            return {attributes: registrationAttributes, formEmpty: formEmpty};
        },
        reconstructForUser: function(tei, attributes, attributesById, optionSets){
            var registrationAttributes = [];
            var formEmpty = true;
            angular.forEach(attributes, function(att){            
                if(att.valueType === 'trueOnly'){ 
                    if(tei[att.id]){
                        registrationAttributes.push({attribute: att.id, value: 'true'});
                        formEmpty = false;                    
                    }
                    else{
                        registrationAttributes.push({attribute: att.id, value: ''});
                        formEmpty = false;
                    }
                }            
                else{
                    if(tei[att.id] !== '' && tei[att.id]){

                        var val = tei[att.id];                    
                        if(att.valueType === 'date'){   
                            val = DateUtils.formatFromApiToUser(val);
                        }   

                        if(att.valueType === 'optionSet' && attributesById[att.id] && attributesById[att.id].optionSet && optionSets[attributesById[att.id].optionSet.id]){                        
                            val = OptionSetService.getName(optionSets[attributesById[att.id].optionSet.id].options, val);
                        }

                        registrationAttributes.push({attribute: att.id, value: val});
                        formEmpty = false;
                    }
                }                        
            });

            return {attributes: registrationAttributes, formEmpty: formEmpty};
        }
    };
})

/* Factory for getting tracked entity attributes */
.factory('AttributesFactory', function($q, $rootScope, TCStorageService, orderByFilter) {      

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
.factory('TrackerRulesFactory', function(){
    return{
        getProgramStageRules :function(programUid, programstageUid) {
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
        
        getProgramRules : function(programUid){
            //Will be fetched from server
            
            var rules = [];
            
            if(programUid === "WSGAb5XwJ3Y") {
                rules = [
                    //The below rules is generated by an excel tool.
                    //Paste from here:
                    
{uid:'rule105104t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'true',actions:[{uid:'actn105109e',action:'displaykeydata',location:'det',content:'Unique ID',data:'$uniqueid'}]},
{uid:'rule106104t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'true',actions:[{uid:'actn106104e',action:'displaykeydata',location:'det',content:'Name',data:'$name'}]},
{uid:'rule107104t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'true',actions:[{uid:'actn107104e',action:'displaykeydata',location:'det',content:'Born',data:'$born'}]},
{uid:'rule108154t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$plurality !== \'\' && $plurality !== \'Not assessed\'',actions:[{uid:'actn108109e',action:'displaykeydata',location:'det',content:'Plurality',data:'$plurality'}]},
{uid:'rule109186t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$plurality !== \'\' && $plurality !== \'Not assessed\' && $plurality !== \'Singleton\'',actions:[{uid:'actn109100e',action:'displaytext',location:'con',content:'',data:'$plurality'},
{uid:'actn110100e',action:'displaytext',location:'sum',content:'',data:'$plurality'}]},
{uid:'rule111120t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$smoking === \'Yes\'',actions:[{uid:'actn111114e',action:'displaytext',location:'con',content:'Current smoker',data:null},
{uid:'actn112114e',action:'displaytext',location:'sum',content:'Current smoker',data:null}]},
{uid:'rule113120t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$gestationalage < 37',actions:[{uid:'actn113111e',action:'hidefield',location:null,content:'vPdXnmGWzfy',data:null}]},
{uid:'rule114226t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'($plurality === \'Singleton\'  || $plurality ===\'Not assessed\') && ($diastolicbloodpressure <= 90  || $gestationalage >= 20)',actions:[{uid:'actn114111e',action:'hidefield',location:null,content:'OSuxnldV4Ug',data:null}]},
{uid:'rule115120t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$smoking !== \'Yes\'',actions:[{uid:'actn115111e',action:'hidefield',location:null,content:'Ok9OQpitjQr',data:null}]},
{uid:'rule116124t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$smokingcouncellinggiven',actions:[{uid:'actn116169e',action:'displaytext',location:'sum',content:'Counseling and behavioral interventions on smoking cessation provided',data:null}]},
{uid:'rule117129t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$systolicbloodpressure >= 160',actions:[{uid:'actn117119e',action:'displaytext',location:'sum',content:'Severe hypertension',data:null}]},
{uid:'rule118161t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$systolicbloodpressure < 160 && $diastolicbloodpressure < 105',actions:[{uid:'actn118111e',action:'hidefield',location:null,content:'cKBSkBB3Mt4',data:null}]},
{uid:'rule119153t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$diastolicbloodpressure < 110 || $gestationalage > 21',actions:[{uid:'actn119111e',action:'hidefield',location:null,content:'lcaG1Pnh27I',data:null}]},
{uid:'rule120326t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:1,programStage_uid:null,condition:'($hemoglobincurrentevent !== 0 && $hemoglobincurrentevent > 7 && $hemoglobincurrentevent < 11) || ($hematocritcurrentevent !== 0 && $hemoglobincurrentevent === 0 && $hematocritcurrentevent > 20 && $hematocritcurrentevent < 33)',actions:[{uid:'actn120127e',action:'assignvariable',location:null,content:'$moderateanemiacurrentevent',data:'true'},
{uid:'actn121115e',action:'displaytext',location:'con',content:'Moderate anemia',data:null},
{uid:'actn122115e',action:'displaytext',location:'sum',content:'Moderate anemia',data:null}]},
{uid:'rule123361t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:1,programStage_uid:null,condition:'($hemoglobincurrentevent !== 0 && $hemoglobincurrentevent <= 7) || ($hematocritcurrentevent !== 0 && $hemoglobincurrentevent === 0 && $hematocritcurrentevent < 20) || ($hemoglobincurrentevent === 0 && $hematocritcurrentevent === 0 && $extremepallorcurrentevent)',actions:[{uid:'actn123125e',action:'assignvariable',location:null,content:'$severeanemiacurrentevent',data:'true'},
{uid:'actn124113e',action:'displaytext',location:'con',content:'Severe anemia',data:null},
{uid:'actn125113e',action:'displaytext',location:'sum',content:'Severe anemia',data:null}]},
{uid:'rule126271t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:2,programStage_uid:null,condition:'($hemoglobinlatest === 0 || $hemoglobinlatest >= 11) && ($hematocritlatest === 0 || $hematocritlatest >= 33)  && !$moderateanemiacurrentevent && !$severeanemiacurrentevent',actions:[{uid:'actn126110e',action:'displaytext',location:'sum',content:'No anaemia',data:null},
{uid:'actn127109e',action:'assignvariable',location:null,content:'$noanemia',data:'true'}]},
{uid:'rule128125t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hemoglobinprevious >= 11',actions:[{uid:'actn128111e',action:'hidefield',location:null,content:'vANAXwtLwcT',data:null}]},
{uid:'rule129125t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hematocritprevious >= 33',actions:[{uid:'actn129111e',action:'hidefield',location:null,content:'X8HbdaoS9LN',data:null}]},
{uid:'rule130137t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$moderateanemiacurrentevent === false',actions:[{uid:'actn130111e',action:'hidefield',location:null,content:'RxVNLSeTjto',data:null}]},
{uid:'rule131135t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$severeanemiacurrentevent === false',actions:[{uid:'actn131111e',action:'hidefield',location:null,content:'nB4Ui3ckmUi',data:null}]},
{uid:'rule132119t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$noanemia === false',actions:[{uid:'',action:'hidefield',location:null,content:'Kb6kZzUCJi1',data:null}]},
{uid:'rule133130t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$extremepallorinpreviousvisit',actions:[{uid:'actn133111e',action:'hidefield',location:null,content:'EyfTU3ibMmJ',data:null}]},
{uid:'rule134149t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis !== \'Positive\'  || $penicillinallergy',actions:[{uid:'actn134111e',action:'hidefield',location:null,content:'OhcR0fpFcWa',data:null}]},
{uid:'rule135150t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis !== \'Positive\'  || !$penicillinallergy',actions:[{uid:'actn135111e',action:'hidefield',location:null,content:'Js57E09s9fh',data:null}]},
{uid:'rule136126t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis === \'Positive\'',actions:[{uid:'actn136132e',action:'displaytext',location:'con',content:'Syphilis confirmed with RPR-test',data:null},
{uid:'actn137132e',action:'displaytext',location:'sum',content:'Syphilis confirmed with RPR-test',data:null}]},
{uid:'rule138115t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis === 0',actions:[{uid:'actn138188e',action:'displaytext',location:'rem',content:'Counsel on safe sex inclusive correct and consistent use of condoms to prevent infection',data:null}]},
{uid:'rule139170t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis === \'Positive\' && !$penicillingiven && !$erythromycingiven',actions:[{uid:'actn139154e',action:'displaytext',location:'rem',content:'Confirmed Syphilis, treatment missing or not performed',data:null}]},
{uid:'rule140147t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis === \'Positive\'  && $penicillingiven',actions:[{uid:'actn140127e',action:'displaytext',location:'sum',content:'Syphilis treatment provided',data:null},
{uid:'actn141127e',action:'displaytext',location:'con',content:'Syphilis treatment provided',data:null}]},
{uid:'rule142126t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$sleptunderbednet !== true',actions:[{uid:'actn142177e',action:'displaytext',location:'rem',content:'Promote use of bednet dipped in insecticide every 6 months to prevent malaria',data:null},
{uid:'actn143181e',action:'displaytext',location:'sum',content:'Use of bednet dipped in insecticide every 6 months to prevent malaria is promoted',data:null}]},
{uid:'rule144129t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:1,programStage_uid:null,condition:'$ultrasoundbirthdate !== \'\'',actions:[{uid:'actn144119e',action:'assignvariable',location:null,content:'$gestationalagedays',data:'(283 - dhis.daysbetween($eventdate,$ultrasoundbirthdate))'}]},
{uid:'rule145201t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:1,programStage_uid:null,condition:'$ultrasoundbirthdate === \'\' && $lastmenstrualdate === \'\'  && $clinicalestimatedbirthdate !== \'\'',actions:[{uid:'actn145119e',action:'assignvariable',location:null,content:'$gestationalagedays',data:'(283 - dhis.daysbetween($eventdate,$clinicalestimatedbirthdate))'}]},
{uid:'rule146160t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:1,programStage_uid:null,condition:'$ultrasoundbirthdate === \'\' && $lastmenstrualdate !== \'\'',actions:[{uid:'actn146119e',action:'assignvariable',location:null,content:'$gestationalagedays',data:'(dhis.daysbetween($lastmenstrualdate,$eventdate))'}]},
{uid:'rule147126t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:2,programStage_uid:null,condition:'$gestationalagedays !== -1',actions:[{uid:'actn147115e',action:'assignvariable',location:null,content:'$gestationalage',data:'dhis.floor($gestationalagedays/7)'}]},
{uid:'rule148200t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$ultrasoundbirthdate !== \'\' || $lastmenstrualdate !== \'\' || $clinicalestimatedbirthdate !== \'\'',actions:[{uid:'actn148115e',action:'displaykeydata',location:'det',content:'Gestational age',data:'$gestationalage'}]},
{uid:'rule149149t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$syphilis === \'Positive\'  && $erythromycingiven',actions:[{uid:'actn149187e',action:'displaytext',location:'sum',content:'Erythromycin given as first option treatment for Syphilis because of penicillin allergy',data:null},
{uid:'actn150187e',action:'displaytext',location:'con',content:'Erythromycin given as first option treatment for Syphilis because of penicillin allergy',data:null}]},
{uid:'rule151108t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$parity',actions:[{uid:'actn151111e',action:'hidefield',location:null,content:'mrVkW9h2Rdp',data:null},
{uid:'actn152111e',action:'hidefield',location:null,content:'W4zW3aPyS0G',data:null},
{uid:'actn153111e',action:'hidefield',location:null,content:'PuiTfPfSf86',data:null},
{uid:'actn154111e',action:'hidefield',location:null,content:'suhLG4CrzUw',data:null},
{uid:'actn155111e',action:'hidefield',location:null,content:'de0FEHSIoxh',data:null}]},
{uid:'rule156131t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$conditionsinpreviouspregnancy',actions:[{uid:'actn156111e',action:'hidefield',location:null,content:'knthdUD4YQg',data:null},
{uid:'actn157111e',action:'hidefield',location:null,content:'xaZc2Zw6SqB',data:null}]},
{uid:'rule158119t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$chronicconditions',actions:[{uid:'actn158111e',action:'hidefield',location:null,content:'UQ2Zo8CruPB',data:null},
{uid:'actn159111e',action:'hidefield',location:null,content:'zzGNbeMnTd6',data:null},
{uid:'actn160111e',action:'hidefield',location:null,content:'Q1x1HIhuwFN',data:null},
{uid:'actn161111e',action:'hidefield',location:null,content:'VFffa31SKjH',data:null},
{uid:'actn162111e',action:'hidefield',location:null,content:'sdchiIXIcCf',data:null},
{uid:'actn163111e',action:'hidefield',location:null,content:'xPTngRLQTnu',data:null}]},
{uid:'rule164129t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$otherchronicconditionexists',actions:[{uid:'actn164111e',action:'hidefield',location:null,content:'Mh7nK8UKoZP',data:null}]},
{uid:'rule165111t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$allergies',actions:[{uid:'actn165111e',action:'hidefield',location:null,content:'E6QaDtrQP5e',data:null},
{uid:'actn166111e',action:'hidefield',location:null,content:'dpOtt7HUQXa',data:null},
{uid:'actn167111e',action:'hidefield',location:null,content:'ZbDPeYzWsh2',data:null}]},
{uid:'rule168128t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$othermedicineallergyexists',actions:[{uid:'actn168111e',action:'hidefield',location:null,content:'VSmOcdK3v7y',data:null}]},
{uid:'rule169126t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$othersevereallergyexists',actions:[{uid:'actn169111e',action:'hidefield',location:null,content:'zk4Eui7Jhtr',data:null}]},
{uid:'rule170163t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$otherchronicconditionexists && $otherchroniccondition !== \'\'',actions:[{uid:'actn170119e',action:'displaytext',location:'con',content:'Chronic condition: ',data:'$otherchroniccondition'}]},
{uid:'rule171161t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$othermedicineallergyexists && $othermedicineallergy !== \'\'',actions:[{uid:'actn171118e',action:'displaytext',location:'con',content:'Medicine allergy: ',data:'$othermedicineallergy'}]},
{uid:'rule172157t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$othersevereallergyexists && $othersevereallergy !== \'\'',actions:[{uid:'actn172116e',action:'displaytext',location:'con',content:'Severe allergy: ',data:'$othersevereallergy'}]},
{uid:'rule173117t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$sleptunderbednet',actions:[{uid:'actn173111e',action:'hidefield',location:null,content:'ytV9rX4ADnn',data:null}]},
{uid:'rule174179t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$gestationalage < 14 || $gestationalage >= 28 || $malariatreatedsecondtrimester',actions:[{uid:'actn174111e',action:'hidefield',location:null,content:'MHW5v2Iljtw',data:null}]},
{uid:'rule175153t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$gestationalage < 28 || $malariatreatedthirdtrimester',actions:[{uid:'actn175111e',action:'hidefield',location:null,content:'zsWyjzmBLGz',data:null}]},
{uid:'rule176130t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$malariatreatedsecondtrimester',actions:[{uid:'actn176151e',action:'displaytext',location:'con',content:'Prophylactic antimalarial provided in 2nd trimester',data:null},
{uid:'actn177151e',action:'displaytext',location:'sum',content:'Prophylactic antimalarial provided in 2nd trimester',data:null}]},
{uid:'rule178129t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$malariatreatedthirdtrimester',actions:[{uid:'actn178151e',action:'displaytext',location:'con',content:'Prophylactic antimalarial provided in 3rd trimester',data:null},
{uid:'actn179151e',action:'displaytext',location:'sum',content:'Prophylactic antimalarial provided in 3rd trimester',data:null}]},
{uid:'rule180131t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$severeanemiatreatmentinitiated',actions:[{uid:'actn180153e',action:'displaytext',location:'sum',content:'Severe anemia; Iron and folic acid treament initiated',data:null},
{uid:'actn181153e',action:'displaytext',location:'con',content:'Severe anemia; Iron and folic acid treament initiated',data:null}]},
{uid:'rule182133t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$moderateanemiatreatmentinitiated',actions:[{uid:'actn182155e',action:'displaytext',location:'sum',content:'Moderate anemia; Iron and folic acid treament initiated',data:null},
{uid:'actn183155e',action:'displaytext',location:'con',content:'Moderate anemia; Iron and folic acid treament initiated',data:null}]},
{uid:'rule184125t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$ironsupplementsinitiated',actions:[{uid:'actn184181e',action:'displaytext',location:'sum',content:'Iron and folic acid supplementation for 6 months, with continuation for 3 months ',data:null}]},
{uid:'rule185188t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$extremepallorcurrentevent && hemoglobincurrentevent == 0 && hematocritcurrentevent == 0',actions:[{uid:'actn185166e',action:'displaytext',location:'rem',content:'Check haemoglobin value AND/OR haematocrit value to confirm anemia',data:null}]},
{uid:'rule186136t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivrapidteststatus === \'Positive\'',actions:[{uid:'actn186141e',action:'displaytext',location:'con',content:'HIV positive, confirmed by rapid HIV-test',data:null},
{uid:'actn187141e',action:'displaytext',location:'sum',content:'HIV positive, confirmed by rapid HIV-test',data:null}]},
{uid:'rule188136t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivrapidteststatus === \'Negative\'',actions:[{uid:'actn188141e',action:'displaytext',location:'sum',content:'HIV negative, confirmed by rapid HIV-test',data:null}]},
{uid:'rule189160t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:1,programStage_uid:null,condition:'$hivrapidteststatus === \'Positive\'  || $hivpreviouslyknown',actions:[{uid:'actn189112e',action:'assignvariable',location:null,content:'$hivpositive',data:'true'}]},
{uid:'rule190113t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'!$hivpositive',actions:[{uid:'actn190111e',action:'hidefield',location:null,content:'nhW3SZX9JaN',data:null},
{uid:'actn191111e',action:'hidefield',location:null,content:'yEmfXF9HJ1M',data:null}]},
{uid:'rule192136t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivrapidteststatus !== \'Negative\'',actions:[{uid:'actn192111e',action:'hidefield',location:null,content:'jRo4KlnCKeX',data:null}]},
{uid:'rule193203t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'($hivrapidteststatus === \'\' || $hivrapidteststatus ===\'Not performed\') || ($hivtestresultsprovided)',actions:[{uid:'actn193111e',action:'hidefield',location:null,content:'yq1qT0NdjYQ',data:null}]},
{uid:'rule194152t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivtreatmentstatus === \'ARV medication initiated\'',actions:[{uid:'actn194124e',action:'displaytext',location:'sum',content:'ARV-medication initiated',data:null},
{uid:'actn195124e',action:'displaytext',location:'con',content:'ARV-medication initiated',data:null}]},
{uid:'rule196149t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivtreatmentstatus === \'Ongoing ARV treatment\'',actions:[{uid:'actn196121e',action:'displaytext',location:'sum',content:'Ongoing ARV-treatment',data:null},
{uid:'actn197121e',action:'displaytext',location:'con',content:'Ongoing ARV-treatment',data:null}]},
{uid:'rule198199t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'($hivtreatmentstatus === \'No ARV medication plan\' || $hivtreatmentstatus === \'\')&& $hivpositive',actions:[{uid:'actn198147e',action:'displaytext',location:'rem',content:'No ARV-medication plan despite positive for HIV',data:null}]},
{uid:'rule199123t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivcouncellingpositive',actions:[{uid:'actn199147e',action:'displaytext',location:'con',content:'Counseling on partner treatment of HIV provided',data:null},
{uid:'actn200147e',action:'displaytext',location:'sum',content:'Counseling on partner treatment of HIV provided',data:null}]},
{uid:'rule201123t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivtestresultsprovided',actions:[{uid:'actn201149e',action:'displaytext',location:'con',content:'Feedback on HIV test result provided to the woman',data:null},
{uid:'actn202149e',action:'displaytext',location:'sum',content:'Feedback on HIV test result provided to the woman',data:null}]},
{uid:'rule203119t',trigger:'tracker_data_changed',program_uid:'WSGAb5XwJ3Y',priority:null,programStage_uid:null,condition:'$hivpreviouslyknown',actions:[{uid:'actn203129e',action:'displaytext',location:'con',content:'Previously known HIV Positive',data:null},
{uid:'actn204129e',action:'displaytext',location:'sum',content:'Previously known HIV Positive',data:null}]}
         

                    //...to here
                ];
            }
            else if(programUid === "Tr3yny4pQZy") {
                rules = [
                    //The below rules is generated by an excel tool.
                    //Paste from here:
                    {uid:'rule105115t',trigger:'tracker_data_changed',program_uid:'Tr3yny4pQZy',priority:null,programStage_uid:null,condition:'$elementa >= 10',actions:[{uid:'actn105141e',action:'validationerror',location:'GoRyn0A7SvT',content:'Data element A must be a number below 10.',data:null}]},
                    {uid:'rule106129t',trigger:'tracker_data_changed',program_uid:'Tr3yny4pQZy',priority:null,programStage_uid:null,condition:'$elementb  >= ($elementa * 2)',actions:[{uid:'actn106169e',action:'validationerror',location:'GoRyn0A7SvT',content:'Data element A must be higher than half of the value in dataelement B',data:null},
                    {uid:'actn107168e',action:'validationerror',location:'ETZ7vFxodFb',content:'Data element B must be smaller than twice the value of dataelement A',data:null}]}

                    //To here
                ];
            }
           

            return rules;
        }

    };  
})

/* Returns user defined variable names and their corresponding UIDs and types for a specific program */
.factory('TrackerFieldCodeFactory', function(){
    return{
        getUserDefinedProgramFieldCodes : function(programUid){
            if(programUid === "WSGAb5XwJ3Y")
            {
                return[ 
                    //The codes below this line is generated by an excel tool.
                    //Paste from here:
                    
{uid:'var103109vv',name:'plurality',dataType:'text',defaultValue:'Not assessed',sourceType:'dataelement_newest_event_program',dataElement_uid:'PN6HcGjTraL',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var104114vv',name:'gestationalage',dataType:'number',defaultValue:'0',sourceType:'calculated_value',dataElement_uid:'',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var105107vv',name:'smoking',dataType:'text',defaultValue:'Unknown',sourceType:'dataelement_newest_event_program',dataElement_uid:'sWoqcoByYmD',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var106122vv',name:'hemoglobincurrentevent',dataType:'number',defaultValue:'0',sourceType:'dataelement_current_event',dataElement_uid:'vANAXwtLwcT',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var107122vv',name:'hematocritcurrentevent',dataType:'number',defaultValue:'0',sourceType:'dataelement_current_event',dataElement_uid:'X8HbdaoS9LN',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var108125vv',name:'extremepallorcurrentevent',dataType:'bool',defaultValue:'false',sourceType:'dataelement_current_event',dataElement_uid:'EyfTU3ibMmJ',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var109124vv',name:'severeanemiacurrentevent',dataType:'bool',defaultValue:'false',sourceType:'calculated_value',dataElement_uid:'',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var110126vv',name:'moderateanemiacurrentevent',dataType:'bool',defaultValue:'false',sourceType:'calculated_value',dataElement_uid:'',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var111116vv',name:'sleptunderbednet',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'ytV9rX4ADnn',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var112121vv',name:'systolicbloodpressure',dataType:'number',defaultValue:'0',sourceType:'dataelement_newest_event_program',dataElement_uid:'M4HEOoEFTAT',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var113122vv',name:'diastolicbloodpressure',dataType:'number',defaultValue:'0',sourceType:'dataelement_newest_event_program',dataElement_uid:'dyYdfamSY2Z',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var114108vv',name:'syphilis',dataType:'text',defaultValue:'Not performed',sourceType:'dataelement_newest_event_program',dataElement_uid:'AAaJGnWR5js',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var115115vv',name:'penicillingiven',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'OhcR0fpFcWa',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var116117vv',name:'erythromycingiven',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'Js57E09s9fh',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var117104vv',name:'name',dataType:'text',defaultValue:'Name unspecified',sourceType:'tei_attribute',dataElement_uid:'',attribute_uid:'w75KJ2mc4zz',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var118104vv',name:'born',dataType:'date',defaultValue:'',sourceType:'tei_attribute',dataElement_uid:'',attribute_uid:'gHGyrwKPzej',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var119108vv',name:'uniqueid',dataType:'number',defaultValue:'-1',sourceType:'tei_attribute',dataElement_uid:'',attribute_uid:'lZGmxYbs97q',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var120119vv',name:'ultrasoundbirthdate',dataType:'date',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'DecmCMPDPdS',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var121117vv',name:'lastmenstrualdate',dataType:'date',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'w4ky6EkVahL',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var122126vv',name:'clinicalestimatedbirthdate',dataType:'date',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'YKXci7Sm0Zq',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var123118vv',name:'gestationalagedays',dataType:'number',defaultValue:'-1',sourceType:'calculated_value',dataElement_uid:'',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var124117vv',name:'penicillinallergy',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'E6QaDtrQP5e',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var125123vv',name:'smokingcouncellinggiven',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'Ok9OQpitjQr',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var126109vv',name:'allergies',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'QFX1FLWBwtq',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var127106vv',name:'parity',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'hisxuZstYJM',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var128129vv',name:'conditionsinpreviouspregnancy',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'suhLG4CrzUw',attribute_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var129117vv',name:'chronicconditions',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'de0FEHSIoxh',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var130127vv',name:'otherchronicconditionexists',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'xPTngRLQTnu',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var131126vv',name:'othermedicineallergyexists',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'dpOtt7HUQXa',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var132124vv',name:'othersevereallergyexists',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'ZbDPeYzWsh2',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var133121vv',name:'otherchroniccondition',dataType:'text',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'Mh7nK8UKoZP',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var134120vv',name:'othermedicineallergy',dataType:'text',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'VSmOcdK3v7y',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var135118vv',name:'othersevereallergy',dataType:'text',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'zk4Eui7Jhtr',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var136129vv',name:'malariatreatedsecondtrimester',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'MHW5v2Iljtw',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var137128vv',name:'malariatreatedthirdtrimester',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'zsWyjzmBLGz',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var138128vv',name:'extremepallorinpreviousvisit',dataType:'bool',defaultValue:'false',sourceType:'dataelement_previous_event',dataElement_uid:'EyfTU3ibMmJ',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var139108vv',name:'noanemia',dataType:'bool',defaultValue:'false',sourceType:'calculated_value',dataElement_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var140116vv',name:'hemoglobinlatest',dataType:'number',defaultValue:'0',sourceType:'dataelement_newest_event_program',dataElement_uid:'vANAXwtLwcT',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var141116vv',name:'hematocritlatest',dataType:'number',defaultValue:'0',sourceType:'dataelement_newest_event_program',dataElement_uid:'X8HbdaoS9LN',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var142118vv',name:'hemoglobinprevious',dataType:'number',defaultValue:'0',sourceType:'dataelement_previous_event',dataElement_uid:'vANAXwtLwcT',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var143118vv',name:'hematocritprevious',dataType:'number',defaultValue:'0',sourceType:'dataelement_previous_event',dataElement_uid:'X8HbdaoS9LN',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var144130vv',name:'severeanemiatreatmentinitiated',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'nB4Ui3ckmUi',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var145132vv',name:'moderateanemiatreatmentinitiated',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'RxVNLSeTjto',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var146124vv',name:'ironsupplementsinitiated',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'Kb6kZzUCJi1',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var147118vv',name:'hivpreviouslyknown',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'VFffa31SKjH',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var148118vv',name:'hivrapidteststatus',dataType:'text',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'Itl05OEupgQ',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var149122vv',name:'hivcouncellingnegative',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'jRo4KlnCKeX',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var150122vv',name:'hivcouncellingpositive',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'yEmfXF9HJ1M',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var151122vv',name:'hivtestresultsprovided',dataType:'bool',defaultValue:'false',sourceType:'dataelement_newest_event_program',dataElement_uid:'yq1qT0NdjYQ',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var152118vv',name:'hivtreatmentstatus',dataType:'text',defaultValue:'',sourceType:'dataelement_newest_event_program',dataElement_uid:'nhW3SZX9JaN',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'},
{uid:'var153111vv',name:'hivpositive',dataType:'bool',defaultValue:'false',sourceType:'calculated_value',dataElement_uid:'',programStage_uid:null,program_uid:'WSGAb5XwJ3Y'}


                    //...to here
                ];
            }
            else if(programUid === "Tr3yny4pQZy"){
                return [
                    
                //Paste variables for test program from here...
                
                {uid:'var103108vv',name:'elementa',dataType:'number',defaultValue:'0',sourceType:'dataelement_current_event',dataElement_uid:'GoRyn0A7SvT',attribute_uid:'',programStage_uid:null,programId:'Tr3yny4pQZy'},
                {uid:'var104108vv',name:'elementb',dataType:'number',defaultValue:'0',sourceType:'dataelement_current_event',dataElement_uid:'ETZ7vFxodFb',attribute_uid:'',programStage_uid:null,programId:'Tr3yny4pQZy'}

                //...to here
                
                ];
            }
            else {
                return null;
            }
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

.service('EventUtils', function(DateUtils, CalendarService, OptionSetService, OrgUnitService, $filter, orderByFilter){
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
.service('VariableService', function(TrackerFieldCodeFactory,$filter,orderByFilter,$log){
    return {
        getVariables: function($scope) {
            


            
            $scope.pushVariable = function(variablename, variablevalue, variabletype) {
                //First clean away single or double quotation marks at the start and end of the variable name.
                variablevalue = $filter('trimquotes')(variablevalue);
                
                //Append single quotation marks in case the variable is of text type:
                if(variabletype === 'text') {
                    variablevalue = "'" + variablevalue + "'";
                }
                
                if(variabletype === 'date') {
                    variablevalue = "'" + variablevalue + "'";
                }
                
                //"clean" the bool variable types
                if(variabletype === 'bool') {
                    if(eval(variablevalue)) {
                        variablevalue = true;
                    }
                    else {    
                        variablevalue = false;
                    }
                }
                
                if(variabletype === "number") {
                    variablevalue = Number(variablevalue);
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
                                variabletype:variabletype
                            });
            };
            
            var userDefinedFields = TrackerFieldCodeFactory.getUserDefinedProgramFieldCodes($scope.currentEvent.program);
            var variables = [];
            
            
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
            //End of region that neeeds specific implementation for event capture
            
            angular.forEach(userDefinedFields, function(fieldCode) {
                var valueFound = false;
                if(fieldCode.sourceType === "dataelement_newest_event_program_stage"){
                    angular.forEach(eventsSortedPerProgramStage[fieldCode.programStage_uid], function(event) {
                        if(angular.isDefined(event[fieldCode.dataElement_uid])
                                && event[fieldCode.dataElement_uid] !== null ){
                            $scope.pushVariable(fieldCode.name, event[fieldCode.dataElement_uid], fieldCode.dataType );
                            valueFound = true;
                        }

                    });
                }
                else if(fieldCode.sourceType === "dataelement_newest_event_program"){
                    angular.forEach(allEventsSorted, function(event) {
                        if(angular.isDefined(event[fieldCode.dataElement_uid])
                                && event[fieldCode.dataElement_uid] !== null ){
                             $scope.pushVariable(fieldCode.name, event[fieldCode.dataElement_uid], fieldCode.dataType );
                             valueFound = true;
                         }
                    });
                }
                else if(fieldCode.sourceType === "dataelement_current_event"){
                    if(angular.isDefined(currentEvent[fieldCode.dataElement_uid])
                            && currentEvent[fieldCode.dataElement_uid] !== null ){
                        $scope.pushVariable(fieldCode.name, currentEvent[fieldCode.dataElement_uid], fieldCode.dataType );
                        valueFound = true;
                    }      
                }
                else if(fieldCode.sourceType === "dataelement_previous_event"){
                    //Only continue checking for a value if there is more than one event.
                    if(allEventsSorted && allEventsSorted.length > 1) {
                        for(var i = 1; i < allEventsSorted.length; i++) {
                            //When we reach the current stage, and the current stage is not the oldest stage(allEventsSorted[0] is the oldest), 
                            //check if the value we want exists in the previous stage.
                            if(allEventsSorted[i].programStage !== currentEvent.programStage) {
                                //Fetch the value from the previous event.
                                var previousvalue = allEventsSorted[i-1][fieldCode.dataElement_uid];
                                if(angular.isDefined(previousvalue) 
                                        && previousvalue !== null ) {
                                    $scope.pushVariable(fieldCode.name, previousvalue, fieldCode.dataType );
                                    valueFound = true;
                                }
                            }
                        }
                    }
                }
                else if(fieldCode.sourceType === "tei_attribute"){
                    angular.forEach($scope.selectedEntity.attributes , function(attribute) {
                        if(!valueFound) {
                            if(attribute.attribute === fieldCode.attribute_uid) {
                                $scope.pushVariable(fieldCode.name, attribute.value, fieldCode.dataType );
                                valueFound = true;
                            }
                        }
                    });
                }
                else if(fieldCode.sourceType === "calculated_value"){
                    //We won't assign the calculated variables at this step. The rules execution will calculate and assign the variable.
                }
                else {
                    //Missing handing of ruletype
                    $log.warn("Unknown sourceType:" + fieldCode.sourceType);
                }

                if(!valueFound){
                    //If there is still no value found, assign default value:
                    $scope.pushVariable(fieldCode.name, fieldCode.defaultValue, fieldCode.dataType );
                }
            });
            
            //add context variables:
            $scope.pushVariable('eventdate', currentEvent.eventDate, 'date' );
            
            
            return variables;
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
            
            //Get all fieldCodes and resolve values
            var variables = VariableService.getVariables($scope);
            //Make a variables hash to allow direct lookup:
            var variablesHash = {};
            angular.forEach(variables, function(variable) {
                variablesHash[variable.variablename] = variable.variablevalue;
            });
            
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
            }
            
                        
            $scope.runDhisFunctions = function(expression) {
                //Called from "runExpression". Only proceed with this logic in case there seems to be dhis function calls: "dhis." is present.
                if(angular.isDefined(expression) && expression.indexOf("dhis.") !== -1){   
                    var dhisFunctions = [{name:"dhis.daysbetween",parameters:2},
                                        {name:"dhis.floor",parameters:1}];
                    
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
                            if((!angular.isDefined(parameters) && dhisFunction.parameters > 0)
                                    || parameters.length !== dhisFunction.parameters){
                                $log.warn(dhisFunction.name + " was called with the incorrect number of parameters");
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
                        });
                    });
                }
                
                return expression;
            }
            
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
            }
            
            //Get all rules that has the trigger "tracker_data_changed" in the current stage and program:
            var rules = TrackerRulesFactory.getProgramStageRules($scope.selectedProgram.id, $scope.currentStage.id);
            //But run rules in priority - lowest number first(priority null is last)
            rules = orderByFilter(rules, 'priority');
            
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
                    if(expression.indexOf('$') !== -1) {
                        expression = $scope.replaceVariables(expression)
                    }
                    
                    //run expression:
                    var ruleEffective = $scope.runExpression(expression, rule.condition, "rule:" + rule.uid);
                    
                    angular.forEach(rule.actions, function(action){
                        //In case the effect-hash is not populated, add entries
                        if(angular.isUndefined( $rootScope.ruleeffects[action.uid] )){
                            $rootScope.ruleeffects[action.uid] =  {
                                id:action.uid,
                                location:action.location, 
                                action:action.action,
                                content:action.content,
                                data:action.data,
                                ineffect:false
                            };
                        }
                        
                        //In case the rule is effective and contains specific data, 
                        //the effect be refreshed from the variables list.
                        //If the rule is not effective we can skip this step
                        if(ruleEffective && action.data !== null)
                        {
                            //The key data might be containing a dollar sign denoting that the key data is a variable.
                            //To make a lookup in variables hash, we must make a lookup without the dollar sign in the variable name
                            //The first strategy is to make a direct lookup. In case the "data" expression is more complex, we have to do more replacement and evaluation.
                            
                            var nameWithoutDollarSign = action.data.replace('$','')
                            if(angular.isDefined(variablesHash[nameWithoutDollarSign]))
                            {
                                //The variable exists, and is replaced with its corresponding value
                                $rootScope.ruleeffects[action.uid].data =
                                    variablesHash[nameWithoutDollarSign];
                            }
                            else if(action.data.indexOf('$') !== -1)
                            {
                                //Since the value couldnt be looked up directly, and contains a dollar sign, the expression was more complex
                                //Now we will have to make a thorough replacement and separate evaluation to find the correct value:
                                $rootScope.ruleeffects[action.uid].data = $scope.replaceVariables(action.data);
                                //In a scenario where the data contains a complex expression, evaluate the expression to compile(calculate) the result:
                                $rootScope.ruleeffects[action.uid].data = $scope.runExpression($rootScope.ruleeffects[action.uid].data, action.data, "action:" + action.uid);
                            }
                        }
                        
                        //Update the rule effectiveness if it changed in this evaluation;
                        if($rootScope.ruleeffects[action.uid].ineffect != ruleEffective)
                        {
                            //There is a change in the rule outcome, we need to update the effect object.
                            updatedEffectsExits = true;
                            $rootScope.ruleeffects[action.uid].ineffect = ruleEffective;
                        }
                        
                        //In case the rule is of type "assign variable" and the rule is effective,
                        //the variable data result needs to be applied to the correct variable:
                        if($rootScope.ruleeffects[action.uid].action === "assignvariable" && $rootScope.ruleeffects[action.uid].ineffect){
                            //from earlier evaluation, the data portion of the ruleeffect now contains the value of the variable to be assign.
                            //the content portion of the ruleeffect defines the name for the variable, when dollar is removed:
                            var variabletoassign = $rootScope.ruleeffects[action.uid].content.replace("$","");
                            
                            if(!angular.isDefined(variablesHash[variabletoassign])){
                                $log.warn("Variable " + variabletoassign + " was not defined.");
                            }
                            
                            //Even if the variable is not defined: we assign it:
                            if(variablesHash[variabletoassign] !== $rootScope.ruleeffects[action.uid].data){
                                //If the variable was actually updated, we assume that there is an updated ruleeffect somewhere:
                                updatedEffectsExits = true;
                                //Then we assign the new value:
                                variablesHash[variabletoassign] = $rootScope.ruleeffects[action.uid].data;
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
        }
    };
});

