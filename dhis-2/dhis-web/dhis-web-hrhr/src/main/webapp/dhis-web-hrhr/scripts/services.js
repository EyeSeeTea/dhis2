'use strict';

/* Services */

var trackerCaptureServices = angular.module('trackerCaptureServices', ['ngResource'])


.factory('StorageService', function(){
    var store = new dhis2.storage.Store({
        name: "dhis2tc",
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['trackerCapturePrograms', 'programStages', 'trackedEntities','attributes','optionSets']
    });
    return{
        currentStore: store
    };
})
/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http) { 
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get( '../api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;         
        }
    };  
})

/* Factory to fetch programs */
.factory('ProgramFactory', function($q, $rootScope, StorageService, ProgramStageFactory) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('trackerCapturePrograms').done(function(programs){
                    $rootScope.$apply(function(){
                        def.resolve(programs);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('trackerCapturePrograms', uid).done(function(pr){                    
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
.factory('ProgramStageFactory', function($q, $rootScope, StorageService) {  
    
    return {        
        get: function(uid){            
            var def = $q.defer();
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('programStages', uid).done(function(pst){                    
                    angular.forEach(pst.programStageDataElements, function(pstDe){   
                        if(pstDe.dataElement.optionSet){
                            StorageService.currentStore.get('optionSets', pstDe.dataElement.optionSet.id).done(function(optionSet){
                                pstDe.dataElement.optionSet = optionSet;                                
                            });                            
                        }                        
                    });
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
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('programStages').done(function(stages){   
                    angular.forEach(stages, function(stage){
                        if(stageIds.indexOf(stage.id) !== -1){
                            angular.forEach(stage.programStageDataElements, function(pstDe){   
                                if(pstDe.dataElement.optionSet){
                                    StorageService.currentStore.get('optionSets', pstDe.dataElement.optionSet.id).done(function(optionSet){
                                        pstDe.dataElement.optionSet = optionSet;                                
                                    });                            
                                }                            
                            });
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
        get: function( entity ){
            var promise = $http.get(  '../api/enrollments?trackedEntityInstance=' + entity ).then(function(response){
                return response.data;
            });
            return promise;
        },
        enroll: function( enrollment ){
            var promise = $http.post(  '../api/enrollments', enrollment ).then(function(response){
                return response.data;
            });
            return promise;
        }        
    };   
})

/* Service for getting tracked entity instances */
.factory('TEService', function(StorageService, $q, $rootScope) {

    return {
        
        getAll: function(){            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('trackedEntities').done(function(entities){
                    $rootScope.$apply(function(){
                        def.resolve(entities);
                    });                    
                });
            });            
            return def.promise;
        }
    };
})

/* Service for getting tracked entity instances */
.factory('TEIService', function($http, $filter, EntityService) {
    
    var promise;
    return {
        
        get: function(entityUid) {
            promise = $http.get(  '../api/trackedEntityInstances/' +  entityUid ).then(function(response){     
                var tei = response.data;
                
                angular.forEach(tei.attributes, function(attribute){                   
                   if(attribute.type && attribute.value){                       
                       if(attribute.type === 'date'){                           
                           attribute.value = moment(attribute.value, 'YYYY-MM-DD')._d;
                           attribute.value = Date.parse(attribute.value);
                           attribute.value = $filter('date')(attribute.value, 'yyyy-MM-dd');                           
                       }
                   } 
                });
                return tei;
            });            
            return promise;
        },        
        getByOrgUnitAndProgram: function(orgUnitUid, programUid) {

            var url = '../api/trackedEntityInstances.json?ou=' + orgUnitUid + '&program=' + programUid;
            
            promise = $http.get( url ).then(function(response){               
                return EntityService.formatter(response.data);
            });            
            return promise;
        },
        getByOrgUnit: function(orgUnitUid) {           
            
            var url =  '../api/trackedEntityInstances.json?ou=' + orgUnitUid;
            
            promise = $http.get( url ).then(function(response){                                
                return EntityService.formatter(response.data);
            });            
            return promise;
        },        
        search: function(ouId, ouMode, queryUrl, programUrl, attributeUrl) {           
            
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
            
            promise = $http.get( url ).then(function(response){                                
                return EntityService.formatter(response.data);
            });            
            return promise;
        },                
        update: function(tei){
            
            var url = '../api/trackedEntityInstances';
            
            var promise = $http.put( url + '/' + tei.trackedEntityInstance , tei).then(function(response){
                return response.data;
            });
            return promise;
        },
        register: function(tei){
            
            var url = '../api/trackedEntityInstances';
            
            var promise = $http.post(url, tei).then(function(response){
                return response.data;
            });
            return promise;
        },
        formatWithAttribute: function(tei, attributes){
            
            angular.forEach(tei.attributes, function(att){
                if(att.type === 'number' && !isNaN(parseInt(att.value))){
                    att.value = parseInt(att.value);
                }
            });
            
            return tei;
        }
    };
})

/* Factory for getting tracked entity attributes */
.factory('AttributesFactory', function($http, $q, storage, $rootScope, StorageService) {      
    var atts, promise;
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('attributes').done(function(attributes){
                    angular.forEach(attributes, function(att){
                        if(att.optionSet){
                           StorageService.currentStore.get('optionSets', att.optionSet.id).done(function(optionSet){
                                att.optionSet = optionSet;
                            });
                        }
                        $rootScope.$apply(function(){
                            def.resolve(attributes);
                        });
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
        getLocalAttributes: function() {            
            if( !atts && !promise ){
                promise = $http.get('data/localAttributes.json').then(function(response){                    
                    atts = response.data;
                    return atts;
                });
            }
            return promise;            
        },
        getAttributesForPregnantWoman: function(){
            var pregnantWomanAttributes = [];
            var def = $q.defer();
            
            this.getAll().then(function(attributes){

                var localAttributes = storage.get('LOCAL_ATTRIBUTES');

                angular.forEach(localAttributes.pregnantWoman, function(localAttribute){                   
                    angular.forEach(attributes, function(attribute){
                        if(localAttribute.code === attribute.code){
                            var att = attribute;
                            att.mandatory = localAttribute.mandatory;
                            pregnantWomanAttributes.push(att);
                        }
                    });                    
                });                     
                def.resolve(pregnantWomanAttributes); 
            });
            
            return def.promise;
        },
        getAttributesForContactPerson: function(){
            var contactPersonAttributes = [];
            var def = $q.defer();
            
            this.getAll().then(function(attributes){
                
                var localAttributes = storage.get('LOCAL_ATTRIBUTES');

                angular.forEach(localAttributes.contactPerson, function(localAttribute){                   
                    angular.forEach(attributes, function(attribute){
                        if(localAttribute.code === attribute.code){
                            var att = attribute;
                            att.mandatory = localAttribute.mandatory;
                            contactPersonAttributes.push(att);
                        }
                    });                    
                });                     
                def.resolve(contactPersonAttributes); 
            });
            
            return def.promise;
        }
    };
})

/* Factory for loading Essential Interventions */
.factory('EIFactory', function($http) {
    
    var ei, promise;
    return {
        getEI: function() {            
            if( !ei || !promise ){
                promise = $http.get('data/EI_All.json').then(function(response){                    
                    ei = response.data;
                    return ei;
                });
            }
            return promise;            
        }
    };
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, ProgramStageFactory) {   
    
    return {
        
        getByEntity: function(entity, orgUnit, program){   
            var promise = $http.get( '../api/events.json?' + 'trackedEntityInstance=' + entity.trackedEntityInstance + '&orgUnit=' + orgUnit.id + '&program=' + program.id + '&paging=false').then(function(response){
                
                angular.forEach(response.data.events, function(event){
                    angular.forEach(program.programStages, function(stage){
                        if(event.programStage === stage.id){
                            event.name = stage.name;
                        }
                    });
                }); 
                return response.data.events;
            });            
            return promise;
        },
        get: function(eventUID){
            
            var promise = $http.get('../api/events/' + eventUID + '.json').then(function(response){     
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
        update: function(dhis2Event){            
            var promise = $http.put('../api/events/' + dhis2Event.event, dhis2Event).then(function(response){
                return response.data;
            });
            return promise;
        }
        
    };    
})

.service('EntityQueryFactory', function(){  
    
    this.getQueryForAttributes = function(attributes){
        
        var query = {url: null, hasValue: false};
        
        angular.forEach(attributes, function(attribute){           

            if(attribute.value && attribute.value !== ''){                    
                query.hasValue = true;                
                if(angular.isArray(attribute.value)){
                    var index = 0, q = '';
                    
                    angular.forEach(attribute.value, function(val){
                        
                        if(index < attribute.value.length-1){
                            q = q + val + ';';
                        }
                        else{
                            q = q + val;
                        }                        
                        index++;
                    });
                    
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
        });
        return query;
    };    
})

/* Context menu for grid*/
.service('ContextMenuSelectedItem', function(){
    this.selectedItem = '';
    
    this.setSelectedItem = function(selectedItem){  
        this.selectedItem = selectedItem;        
    };
    
    this.getSelectedItem = function(){
        return this.selectedItem;
    };
})

/* Popup dialog for displaying notes */
.service('NotesDialogService', ['$modal', function($modal) {

    var dialogDefaults = {
        backdrop: true,
        keyboard: true,
        backdropClick: true,
        modalFade: true,
        templateUrl: 'components/notes/note.html'
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

}])


/* Modal service for user interaction */
.service('ModalService', ['$modal', function($modal) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: 'views/modal.html'
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
            templateUrl: 'views/dialog.html'
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

/* current selections */
.service('CurrentSelection', function(){
    this.currentSelection = '';
    
    this.set = function(currentSelection){  
        this.currentSelection = currentSelection;        
    };
    
    this.get = function(){
        return this.currentSelection;
    };
})

/* Translation service - gets logged in user profile for the server, 
 * and apply user's locale to translation
 */
.service('TranslationService', function($translate, storage){
    
    this.translate = function(){
        var profile = storage.get('USER_PROFILE');        
        if( profile ){        
            $translate.uses(profile.settings.keyUiLocale);
        }
    };
})

/* Pagination service */
.service('Paginator', function () {
    this.page = 0;
    this.rowsPerPage = 50;
    this.itemCount = 0;
    this.limitPerPage = 5;

    this.setPage = function (page) {
        if (page > this.pageCount()) {
            return;
        }

        this.page = page;
    };
    
    this.getPage = function(){
        return this.page;
    };
    
    this.getRowsPerPage = function(){
        return this.rowsPerPage;
    };

    this.nextPage = function () {
        if (this.isLastPage()) {
            return;
        }

        this.page++;
    };

    this.perviousPage = function () {
        if (this.isFirstPage()) {
            return;
        }

        this.page--;
    };

    this.firstPage = function () {
        this.page = 0;
    };

    this.lastPage = function () {
        this.page = this.pageCount() - 1;
    };

    this.isFirstPage = function () {
        return this.page == 0;
    };

    this.isLastPage = function () {
        return this.page == this.pageCount() - 1;
    };

    this.pageCount = function () {
        var count = Math.ceil(parseInt(this.itemCount, 10) / parseInt(this.rowsPerPage, 10)); 
        if (count === 1) { this.page = 0; } return count;
    };

    this.lowerLimit = function() { 
        var pageCountLimitPerPageDiff = this.pageCount() - this.limitPerPage;

        if (pageCountLimitPerPageDiff < 0) { 
            return 0; 
        }

        if (this.page > pageCountLimitPerPageDiff + 1) { 
            return pageCountLimitPerPageDiff; 
        } 

        var low = this.page - (Math.ceil(this.limitPerPage/2) - 1); 

        return Math.max(low, 0);
    };
})

/*this is just a hack - there should be better way */
.service('ValidDate', function(){    
    var dateValidation;    
    return {
        get: function(dt) {
            dateValidation = dt;
        },
        set: function() {    
            return dateValidation;
        }
    };
            
})

.service('EntityService', function(OrgUnitService, $filter){
    
    return {
        formatter: function(grid){
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

            OrgUnitService.open().then(function(){

                angular.forEach(grid.rows, function(row){
                    var entity = {};
                    var isEmpty = true;

                    entity.id = row[0];
                    var rDate = row[1];
                    rDate = moment(rDate, 'YYYY-MM-DD')._d;
                    rDate = Date.parse(rDate);
                    rDate = $filter('date')(rDate, 'yyyy-MM-dd');                           
                    entity.created = rDate;
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
                            entity[grid.headers[i].name] = row[i];
                        }
                    }

                    if(!isEmpty){
                        entityList.push(entity);
                    }        
                });                
            });
            return {headers: attributes, rows: entityList};                                    
        }        
    };
})

/* Service for evaluating intervention rules */
.service('InterventionService', function(storage,
                                         $rootScope,
                                         ExpressionService, 
                                         TransferHandler,
                                         DialogService) {
    
    return {
        
        getResults: function(dhis2Events){      

            var checked = [];
            
            var dep = [], con = [], smr = [], rem = [], mes = [], sch = [];
            //Fetch available events for the selected person
            angular.forEach(dhis2Events, function(dhis2Event){         

                angular.forEach(dhis2Event.dataValues, function(dataValue){                    
                    
                    var de = storage.get(dataValue.dataElement);  
                    
                    if(angular.isObject(de)){ 
                        
                        var actualValue = dataValue.value;  
                        
                        if( de.type == 'string'){                            
                            actualValue = '"' + actualValue + '"';                            
                        }   
                        
                        //if(checked.indexOf(de.code) != -1 ){
                            
                            angular.forEach(de.actions, function(eiAction) {                           

                                var val = eiAction.value.replace(new RegExp('#' + de.code + '#', 'g'), actualValue);

                                //check if the expression contains some varibales
                                if (val.indexOf('#') != -1) {
                                    //format the expression, replace varibales with actual value
                                    //when replacing value, track back from the latest one.
                                    val = ExpressionService.getDataElementExpression(val, dhis2Events);
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

                                    if ($rootScope.$eval(val)) {
                                        TransferHandler.store(eiAction.task.dependencies, de.code, dataValue.value, dep);
                                        TransferHandler.store(eiAction.task.conditionsComplications, de.code, dataValue.value, con);                                
                                        TransferHandler.store(eiAction.task.reminders, de.code, dataValue.value, rem);
                                        TransferHandler.store(eiAction.task.messaging, de.code, dataValue.value, mes);
                                        TransferHandler.store(eiAction.task.scheduling, de.code, dataValue.value, sch);
                                        TransferHandler.store(eiAction.task.summary, de.code, dataValue.value, smr);
                                    }                        
                                }
                            });                     
                            
                            //no need to check it again in case
                            //it is collected in subsequent visits
                            checked.push(de.code); 
                        //}                                            
                    }                                            
                });                              
            });    
            var results = { depResult: dep,
                            conResult: con,
                            remResult: rem,
                            mesResult: mes,
                            schResult: sch,
                            smrResult: smr
                          };
            return results;
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
                            var dv = dhis2Events[i].dataValues[j].value;
                            if( de.type == 'string'){                                
                                dv = '"' + dv + '"';                                
                            }
                            val = val.replace(new RegExp('#'+de.code+'#','g'), dv);
                            
                            loopThrough = false;
                        }                            
                    }
                }
            }
            
            val = val.replace(/#[^#]*#/g, null);
            return val;
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
                    if (output.indexOf(input[i]) === -1) {
                        output.push(input[i]);
                    }
                }
            }
        }
    };
});