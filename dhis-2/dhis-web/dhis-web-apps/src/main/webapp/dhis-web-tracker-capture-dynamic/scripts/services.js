'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])

.factory('TCStorageService', function(){
    var store = new dhis2.storage.Store({
        name: "dhis2tc",
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['programs', 'programStages', 'trackedEntities', 'trackedEntityForms', 'attributes', 'relationshipTypes', 'optionSets']
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

/* Service to deal with enrollment */
.service('EnrollmentService', function($http) {
    
    return {        
        get: function( enrollmentUid ){
            var promise = $http.get(  '../api/enrollments/' + enrollmentUid ).then(function(response){
                return response.data;
            });
            return promise;
        },
        getByEntity: function( entity ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity ).then(function(response){
                return response.data;
            });
            return promise;
        },
        getByEntityAndProgram: function( entity, program ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity + '&program=' + program ).then(function(response){
                return response.data;
            });
            return promise;
        },
        getByStartAndEndDate: function( program, orgUnit, ouMode, startDate, endDate ){
            var promise = $http.get(  '../api/enrollments.json?program=' + program + '&orgUnit=' + orgUnit + '&ouMode='+ ouMode + '&startDate=' + startDate + '&endDate=' + endDate + '&paging=false').then(function(response){
                return response.data;
            });
            return promise;
        },
        enroll: function( enrollment ){
            var promise = $http.post(  '../api/enrollments', enrollment ).then(function(response){
                return response.data;
            });
            return promise;
        },
        update: function( enrollment ){
            var promise = $http.put( '../api/enrollments/' + enrollment.enrollment , enrollment).then(function(response){
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
        getByProgram: function(programUid){            
            var def = $q.defer();
            
            TCStorageService.currentStore.open().done(function(){
                TCStorageService.currentStore.get('trackedEntityForms', programUid).done(function(te){                    
                    $rootScope.$apply(function(){
                        def.resolve(te);
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
        register: function(tei){
            
            var url = '../api/trackedEntityInstances';
            
            var promise = $http.post(url, tei).then(function(response){
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
            
            var attributes = [];
            var programAttributes = [];

            var def = $q.defer();
            this.getAll().then(function(atts){
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
                if(teiAttributes[j].value){                    
                    if(teiAttributes[j].type === 'number' && !isNaN(parseInt(teiAttributes[j].value))){
                        teiAttributes[j].value = parseInt(teiAttributes[j].value);                        
                    }                    
                }               
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
        getEventsByProgram: function(entity, orgUnit, program){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity + '&orgUnit=' + orgUnit + '&program=' + program + '&paging=false').then(function(response){
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
        getProgramRules : function(programUid){
            //Will be fetched from server
            var rules = [
                //The below rules is generated by an excel tool.
                //Paste from here:
                
{rule_uID:'rule104104t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'true',actions:[{action_uID:'actn104109e',action:'displaykeydata',location:'det',content:'Unique ID',data:'$uniqueId'}]},
{rule_uID:'rule105104t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'true',actions:[{action_uID:'actn105104e',action:'displaykeydata',location:'det',content:'Name',data:'$name'}]},
{rule_uID:'rule106104t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'true',actions:[{action_uID:'actn106104e',action:'displaykeydata',location:'det',content:'Born',data:'$born'}]},
{rule_uID:'rule107116t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$plurality === 1',actions:[{action_uID:'actn107109e',action:'displaykeydata',location:'det',content:'Plurality',data:'Singleton'}]},
{rule_uID:'rule108116t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$plurality === 2',actions:[{action_uID:'actn108109e',action:'displaykeydata',location:'det',content:'Plurality',data:'Twins'},
{action_uID:'actn109105e',action:'displaytext',location:'con',content:'Twins',data:null},
{action_uID:'actn110105e',action:'displaytext',location:'sum',content:'Twins',data:null}]},
{rule_uID:'rule111116t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$plurality === 3',actions:[{action_uID:'actn111109e',action:'displaykeydata',location:'det',content:'Plurality',data:'Triplets'},
{action_uID:'actn112108e',action:'displaytext',location:'con',content:'Triplets',data:null},
{action_uID:'actn113108e',action:'displaytext',location:'sum',content:'Triplets',data:null}]},
{rule_uID:'rule114116t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$plurality === 4',actions:[{action_uID:'actn114109e',action:'displaykeydata',location:'det',content:'Plurality',data:'Quadruplets'},
{action_uID:'actn115111e',action:'displaytext',location:'con',content:'Quadruplets',data:null},
{action_uID:'actn116111e',action:'displaytext',location:'sum',content:'Quadruplets',data:null}]},
{rule_uID:'rule117116t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$plurality === 5',actions:[{action_uID:'actn117109e',action:'displaykeydata',location:'det',content:'Plurality',data:'Five fetuses'},
{action_uID:'actn118112e',action:'displaytext',location:'con',content:'Five fetuses',data:null},
{action_uID:'actn119112e',action:'displaytext',location:'sum',content:'Five fetuses',data:null}]},
{rule_uID:'rule120116t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$plurality === 6',actions:[{action_uID:'actn120109e',action:'displaykeydata',location:'det',content:'Plurality',data:'Six fetuses'},
{action_uID:'actn121111e',action:'displaytext',location:'con',content:'Six fetuses',data:null},
{action_uID:'actn122111e',action:'displaytext',location:'sum',content:'Six fetuses',data:null}]},
{rule_uID:'rule123108t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$smoking',actions:[{action_uID:'actn123114e',action:'displaytext',location:'con',content:'Current smoker',data:null}]},
{rule_uID:'rule124120t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$gestationalage < 37',actions:[{action_uID:'actn124111e',action:'hidefield',location:null,content:'vPdXnmGWzfy',data:null}]},
{rule_uID:'rule125174t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'($plurality > 1) || ($diastolicbloodpressure > 90 && $gestationalage < 20)',actions:[{action_uID:'actn125111e',action:'hidefield',location:null,content:'OSuxnldV4Ug',data:null}]},
{rule_uID:'rule126118t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$smoking === false',actions:[{action_uID:'actn126111e',action:'hidefield',location:null,content:'Ok9OQpitjQr',data:null}]},
{rule_uID:'rule127129t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$systolicbloodpressure >= 160',actions:[{action_uID:'actn127119e',action:'displaytext',location:'sum',content:'Severe hypertension',data:null}]},
{rule_uID:'rule128161t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$systolicbloodpressure < 160 && $diastolicbloodpressure < 105',actions:[{action_uID:'actn128111e',action:'hidefield',location:null,content:'cKBSkBB3Mt4',data:null}]},
{rule_uID:'rule129153t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$diastolicbloodpressure < 110 || $gestationalage > 21',actions:[{action_uID:'actn129111e',action:'hidefield',location:null,content:'lcaG1Pnh27I',data:null}]},
{rule_uID:'rule130115t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:'ANCnn',condition:'$hemoglobin > 7',actions:[{action_uID:'actn130111e',action:'hidefield',location:null,content:'vANAXwtLwcT',data:null}]},
{rule_uID:'rule131242t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'($hemoglobin !== 0 && $hemoglobin > 7 && $hemoglobin < 11) || ($hematocrit !== 0 && $hemoglobin === 0 && $hematocrit > 20 && $hematocrit < 33)',actions:[{action_uID:'actn131104e',action:'assignvariable',location:null,content:'true',data:'$moderateanaemia'},
{action_uID:'actn132115e',action:'displaytext',location:'con',content:'Moderate anemia',data:null},
{action_uID:'actn133115e',action:'displaytext',location:'sum',content:'Moderate anemia',data:null}]},
{rule_uID:'rule134265t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'($hemoglobin !== 0 && $hemoglobin <= 7) || ($hematocrit !== 0 && $hemoglobin === 0 && $hematocrit < 20) || ($hemoglobin === 0 && $hematocrit === 0 && $extremepallor)',actions:[{action_uID:'actn134104e',action:'assignvariable',location:null,content:'true',data:'$severeanaemia'},
{action_uID:'actn135113e',action:'displaytext',location:'con',content:'Severe anemia',data:null},
{action_uID:'actn136113e',action:'displaytext',location:'sum',content:'Severe anemia',data:null}]},
{rule_uID:'rule137202t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'($moderateanaemia === false && $severeanaemia === false && ($hemoglobin !== 0 ||  $hematocrit !== 0) )',actions:[{action_uID:'actn137110e',action:'displaytext',location:'sum',content:'No anaemia',data:null}]},
{rule_uID:'rule138117t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$hemoglobin >= 11',actions:[{action_uID:'actn138111e',action:'hidefield',location:null,content:'vANAXwtLwcT',data:null}]},
{rule_uID:'rule139117t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$hematocrit >= 33',actions:[{action_uID:'actn139111e',action:'hidefield',location:null,content:'X8HbdaoS9LN',data:null}]},
{rule_uID:'rule140125t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$moderateanemia === false',actions:[{action_uID:'actn140111e',action:'hidefield',location:null,content:'RxVNLSeTjto',data:null}]},
{rule_uID:'rule141123t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$severeanemia === false',actions:[{action_uID:'actn141111e',action:'hidefield',location:null,content:'nB4Ui3ckmUi',data:null}]},
{rule_uID:'rule142124t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$extremepallor === false',actions:[{action_uID:'actn142111e',action:'hidefield',location:null,content:'EyfTU3ibMmJ',data:null}]},
{rule_uID:'rule143115t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$syphilis !== 1',actions:[{action_uID:'actn143111e',action:'hidefield',location:null,content:'OhcR0fpFcWa',data:null}]},
{rule_uID:'rule144115t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$syphilis === 1',actions:[{action_uID:'actn144132e',action:'displaytext',location:'con',content:'Syphilis confirmed with RPR-test',data:null},
{action_uID:'actn145132e',action:'displaytext',location:'sum',content:'Syphilis confirmed with RPR-test',data:null},
{action_uID:'actn146154e',action:'displaytext',location:'rem',content:'Syphilis confirmed with RPR-test, prescribe medication',data:null}]},
{rule_uID:'rule147115t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$syphilis === 0',actions:[{action_uID:'actn147188e',action:'displaytext',location:'rem',content:'Counsel on safe sex inclusive correct and consistent use of condoms to prevent infection',data:null}]},
{rule_uID:'rule148146t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$syphilis === 1 && $antibioticsgiven === false',actions:[{action_uID:'actn148154e',action:'displaytext',location:'rem',content:'Confirmed Syphilis, treatment missing or not performed',data:null}]},
{rule_uID:'rule149145t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$syphilis === 1 && $antibioticsgiven === true',actions:[{action_uID:'actn149127e',action:'displaytext',location:'sum',content:'Syphilis treatment provided',data:null},
{action_uID:'actn150127e',action:'displaytext',location:'con',content:'Syphilis treatment provided',data:null}]},
{rule_uID:'rule151126t',trigger:'tracker_data_changed',program_uID:'WSGAb5XwJ3Y',programstage_uID:null,condition:'$sleptunderbednet !== true',actions:[{action_uID:'actn151177e',action:'displaytext',location:'rem',content:'Promote use of bednet dipped in insecticide every 6 months to prevent malaria',data:null},
{action_uID:'actn152181e',action:'displaytext',location:'sum',content:'Use of bednet dipped in insecticide every 6 months to prevent malaria is promoted',data:null}]}

                //...to here
                
            ];
            
            //Go through all rules and replace operators for javascript:
            /*
             * TODO: Fix the rule washing - needs to see difference between = and >=
             * angular.forEach(rules,function(rule){
               rule.ruleContent.condition = 
                    rule.ruleContent.condition.replace("=", "==="); 
            });*/

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
                    
{variable_uID:'var102109vv',variablename:'plurality',defaultvalue:'0',type:'dataelement_newest_event_program',dataelement_uID:'PN6HcGjTraL',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var103114vv',variablename:'gestationalage',defaultvalue:'0',type:'calculated_value',dataelement_uID:'',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var104107vv',variablename:'smoking',defaultvalue:'0',type:'dataelement_newest_event_program',dataelement_uID:'sWoqcoByYmD',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var105110vv',variablename:'hemoglobin',defaultvalue:'0',type:'dataelement_newest_event_program',dataelement_uID:'vANAXwtLwcT',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var106110vv',variablename:'hematocrit',defaultvalue:'0',type:'dataelement_newest_event_program',dataelement_uID:'X8HbdaoS9LN',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var107113vv',variablename:'extremepallor',defaultvalue:'-1',type:'dataelement_newest_event_program',dataelement_uID:'EyfTU3ibMmJ',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var108113vv',variablename:'severeanaemia',defaultvalue:'false',type:'calculated_value',dataelement_uID:'',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var109115vv',variablename:'moderateanaemia',defaultvalue:'false',type:'calculated_value',dataelement_uID:'',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var110116vv',variablename:'sleptunderbednet',defaultvalue:'false',type:'dataelement_newest_event_program',dataelement_uID:'ytV9rX4ADnn',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var111121vv',variablename:'systolicbloodpressure',defaultvalue:'0',type:'dataelement_newest_event_program',dataelement_uID:'M4HEOoEFTAT',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var112122vv',variablename:'diastolicbloodpressure',defaultvalue:'0',type:'dataelement_newest_event_program',dataelement_uID:'dyYdfamSY2Z',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var113108vv',variablename:'syphilis',defaultvalue:'3',type:'dataelement_newest_event_program',dataelement_uID:'EA6_LAB_SYP',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var114116vv',variablename:'antibioticsgiven',defaultvalue:'false',type:'dataelement_newest_event_program',dataelement_uID:'OhcR0fpFcWa',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'},
{variable_uID:'var115127vv',variablename:'syphiliscounsellingprovided',defaultvalue:'false',type:'dataelement_newest_event_program',dataelement_uID:'Js57E09s9fh',programstage_uID:null,program_uID:'WSGAb5XwJ3Y'}

                    //...to here
                ];
            }
            else
            {
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
            ]
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
            ]
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
        createDummyEvent: function(events, programStage, orgUnit, enrollment){
            var today = DateUtils.getToday();    
            var dueDate = this.getEventDueDate(events, programStage, enrollment);
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
            
            dummyEvent.statusColor = 'alert alert-warning';//'stage-on-time';
            if(moment(today).isAfter(dummyEvent.dueDate)){
                dummyEvent.statusColor = 'alert alert-danger';//'stage-overdue';
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
                return 'alert alert-success';//'stage-completed';
            }
            else if(dhis2Event.status === 'SKIPPED'){
                return 'alert alert-default'; //'stage-skipped';
            }
            else{                
                if(dhis2Event.eventDate){
                    return 'alert alert-info'; //'stage-executed';
                }
                else{
                    if(moment(eventDate, calendarSetting.momentFormat).isAfter(dhis2Event.dueDate)){
                        return 'alert alert-danger';//'stage-overdue';
                    }                
                    return 'alert alert-warning';//'stage-on-time';
                }               
            }            
        },
        getEventDueDate: function(events, programStage, enrollment){            
            var referenceDate = enrollment.dateOfIncident ? enrollment.dateOfIncident : enrollment.dateOfEnrollment,
                offset = programStage.minDaysFromStart,
                calendarSetting = CalendarService.getSetting();
        
            if(programStage.generatedByEnrollmentDate){
                referenceDate = enrollment.dateOfEnrollment;
            }
            
            if(programStage.repeatable){
                var eventsPerStage = [];
                angular.forEach(events, function(event){
                    if(event.programStage === programStage.id){
                        eventsPerStage.push(event);
                    }
                });

                if(eventsPerStage.length > 0){
                    eventsPerStage = orderByFilter(eventsPerStage, '-eventDate');
                    referenceDate = eventsPerStage[0].eventDate;
                    offset = programStage.standardInterval;
                }                
            }            
            
            var dueDate = moment(referenceDate, calendarSetting.momentFormat).add('d', offset)._d;
            dueDate = $filter('date')(dueDate, calendarSetting.keyDateFormat); 
            return dueDate;
        },
        getEventOrgUnitName: function(orgUnitId){            
            if(orgUnitId){
                OrgUnitService.open().then(function(){
                    OrgUnitService.get(orgUnitId).then(function(ou){
                        if(ou){
                            return ou.n;             
                        }                                                       
                    });                            
                }); 
            }
        },
        setEventOrgUnitName: function(dhis2Event){            
            if(dhis2Event.orgUnit){
                OrgUnitService.open().then(function(){
                    OrgUnitService.get(dhis2Event.orgUnit).then(function(ou){
                        if(ou){
                            dhis2Event.eventOrgUnitName = ou.n;
                            return dhis2Event;                            
                        }                                                       
                    });                            
                }); 
            }
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
.service('VariableService', function(TrackerFieldCodeFactory, $log){
    return {
        getVariables: function($scope) {
            
            var userDefinedFields = TrackerFieldCodeFactory.getUserDefinedProgramFieldCodes($scope.currentEvent.program);
            var variables = [];
            
            angular.forEach(userDefinedFields, function(fieldCode) {
                var valueFound = false;
                if(fieldCode.type === "dataelement_newest_event_program_stage"){
                    angular.forEach($scope.dhis2Events, function(event) {
                        if(!valueFound) {
                            if(event.programStage === fieldCode.programstage_uID) {
                                if(angular.isDefined(event[fieldCode.dataelement_uID])
                                        && event[fieldCode.dataelement_uID] != null ){
                                    variables.push({
                                            variablename:fieldCode.variablename,
                                            variablevalue:event[fieldCode.dataelement_uID]
                                        });
                                    valueFound = true;
                                }
                            }
                        }
                    });
                }
                else if(fieldCode.type === "dataelement_newest_event_program"){
                    angular.forEach($scope.dhis2Events, function(event) {
                        if(!valueFound) {
                           if(angular.isDefined(event[fieldCode.dataelement_uID])
                                   && event[fieldCode.dataelement_uID] != null ){
                                variables.push({
                                            variablename:fieldCode.variablename,
                                            variablevalue:event[fieldCode.dataelement_uID]
                                        });
                                 valueFound = true;
                            }
                        }
                    });
                }
                else if(fieldCode.type === "dataelement_current_event"){
                    angular.forEach($scope.dhis2Events, function(event) {
                        if(!valueFound) {
                            if(event.programStage === $scope.currentEvent.programStage) {
                                if(angular.isDefined(event[fieldCode.dataelement_uID])
                                        && event[fieldCode.dataelement_uID] != null ){
                                    variables.push({
                                            variablename:fieldCode.variablename,
                                            variablevalue:event[fieldCode.dataelement_uID]
                                        });
                                    valueFound = true;
                                }
                            }
                        }
                    });
                }
                else{
                    //Missing handing of ruletype
                    $log.warn("Unknown fieldCode type:" + fieldCode.type);
                }

                if(!valueFound){
                    //If there is still no value found, assign default value:
                    variables.push({
                            variablename:fieldCode.variablename,
                            variablevalue:fieldCode.defaultvalue
                    });
                }
            });
            
            //Start iterating the context variables - and add these to the variables hash
            
            return variables;
        }
    };
})
       


/* service for executing tracker rules and broadcasting results */
.service('TrackerRulesExecutionService', function(TrackerRulesFactory,VariableService, $rootScope, $log){
    return {
        executeRules: function($scope) {
            //When debugging rules, the caller should provide a variable for wether or not the rules is being debugged.
            //hard coding this for now:
            var debug = true;
            
            //Get all fieldCodes and resolve values
            var variables = VariableService.getVariables($scope);
            //Make a variables hash to allow direct lookup:
            var variablesHash = {};
            angular.forEach(variables, function(variable) {
                variablesHash[variable.variablename] = variable.variablevalue;
            });
            
            
            //Get all rules that has the trigger "TrackerDataChanged"
            //TODO: Only get the rules that is effective for this program and program stage
            var rules = TrackerRulesFactory.getProgramRules();
            
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
                        angular.forEach(variables, function(variable) {
                            expression = expression.replace(new RegExp("\\$" + variable.variablename, 'g'),
                                variable.variablevalue);
                            //[$][a-zA-Z0-9_]+
                        });
                    }
                    
                    
                    //determine if expression is true, and actions should be effectuated
                    
                    //If DEBUG mode, use try catch and report errors. If not, omit the heavy try-catch loop.:
                    var ruleEffective = false;
                    if(debug) {
                        try{
                            ruleEffective = eval(expression);
                        }
                        catch(e)
                        {
                            $log.warn("Rule " + rule.rule_uID + " could not be run. Original condition was: " + rule.condition + " - Evaluation ended up as:" + expression + " - error message:" + e);
                        }
                    }
                    else {
                        //Just run the expression. This is much faster than the debug route.
                        ruleEffective = eval(expression);
                    }
                    
                    angular.forEach(rule.actions, function(action){
                        //In case the effect-hash is not populated, add entries
                        if(angular.isUndefined( $rootScope.ruleeffects[action.action_uID] )){
                            $rootScope.ruleeffects[action.action_uID] =  {
                                id:action.action_uID,
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
                            if(angular.isDefined(variablesHash[action.data]))
                            {
                                $rootScope.ruleeffects[action.action_uID].data =
                                    variablesHash[action.data];
                            }
                        }
                        
                        if($rootScope.ruleeffects[action.action_uID].ineffect != ruleEffective)
                        {
                            //There is a change in the rule outcome, we need to update the effect object.
                            updatedEffectsExits = true;
                            $rootScope.ruleeffects[action.action_uID].ineffect = ruleEffective;
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

