trackerCapture.controller('DataEntryController',
        function($rootScope,
                $scope,
				$modal,
                $log,
                DateUtils,
                EventUtils,
                orderByFilter,
                storage,
                ProgramStageFactory,
                DHIS2EventFactory,
                OptionSetService,
                ModalService,
                CurrentSelection,
				TrackerRulesExecutionService,
				CustomFormService) {
    
    //Data entry form
    $scope.dataEntryOuterForm = {};
    $scope.displayCustomForm = false;
    $scope.currentElement = {};
    $scope.schedulingEnabled = false;
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    var today = DateUtils.getToday();
    $scope.invalidDate = false;
    
    //note
    $scope.note = '';
    
    //event color legend
    $scope.eventColors = [
                            {color: 'alert-success', description: 'completed'},
                            {color: 'alert-info', description: 'executed'},
                            {color: 'alert-warning', description: 'ontime'},
                            {color: 'alert-danger', description: 'overdue'},
                            {color: 'alert-default', description: 'skipped'}
                         ];
    $scope.showEventColors = false;
    
    //check if field is hidden
    $scope.isHidden = function(id) {
        //TODO: This function is working, but non-optimalized.
        var hide = false;
        
        //In case the field contains a value, we cant hide it. 
        //If we hid a field with a value, it would falsely seem the user was aware that the value was entered in the UI.
        if($scope.currentEvent[id])
        {
           return false; 
        }
                
        angular.forEach($rootScope.ruleeffects, function(effect) {
            //in the data entry controller we only care about the "hidefield" actions
            if(effect.action === "HIDEFIELD" && effect.dataElement.id === id) {
                hide = effect.ineffect;
            }
        });
        return hide;
    }; 
    
    //check if field has a warning message
    $scope.errorMessage = function(id) { 
        var error = false;
        
        angular.forEach($rootScope.ruleeffects, function(effect) {
            //in the data entry controller we only care about the "hidefield" actions
            if(effect.action === "SHOWWARNING" && effect.location === id && effect.ineffect) {
                error = effect.content;
            }
        });
        //No error message 
        return error;
    }; 
    
    
    //listen for the selected items
    $scope.$on('dashboardWidgets', function() {        
        $scope.showDataEntryDiv = false;
        $scope.showEventCreationDiv = false;
        $scope.currentEvent = null;
        $scope.currentStage = null;
        $scope.totalEvents = 0;
            
        $scope.allowEventCreation = false;
        $scope.repeatableStages = [];        
        $scope.eventsByStage = [];
        $scope.programStages = [];
        $rootScope.ruleeffects = {};
		$scope.prStDes = [];
        
        var selections = CurrentSelection.get();          
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedEntity = selections.tei;      
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.selectedEnrollment;   
        $scope.optionSets = selections.optionSets;
        
        $scope.selectedProgramWithStage = [];        
        if($scope.selectedOrgUnit && $scope.selectedProgram && $scope.selectedEntity && $scope.selectedEnrollment){

            ProgramStageFactory.getByProgram($scope.selectedProgram).then(function(stages){
                $scope.programStages = stages;
                angular.forEach(stages, function(stage){
                    if(stage.openAfterEnrollment){
                        $scope.currentStage = stage;
                    }                   
                    
                    angular.forEach(stage.programStageDataElements, function(prStDe){
                        $scope.prStDes[prStDe.dataElement.id] = prStDe;
                    });
                    
                    $scope.selectedProgramWithStage[stage.id] = stage;
                    $scope.eventsByStage[stage.id] = [];
                });
                $scope.getEvents();                
            });
        }
    });
    
    $scope.getEvents = function(){
        DHIS2EventFactory.getEventsByProgram($scope.selectedEntity.trackedEntityInstance, $scope.selectedProgram.id).then(function(events){
            if(angular.isObject(events)){
                angular.forEach(events, function(dhis2Event){                    
                    if(dhis2Event.enrollment === $scope.selectedEnrollment.enrollment && dhis2Event.orgUnit){
                        if(dhis2Event.notes){
                            dhis2Event.notes = orderByFilter(dhis2Event.notes, '-storedDate');
                            angular.forEach(dhis2Event.notes, function(note){
                                note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                            });
                        }
                    
                        var eventStage = $scope.selectedProgramWithStage[dhis2Event.programStage];
                        if(angular.isObject(eventStage)){

                            dhis2Event.name = eventStage.name; 
                            dhis2Event.reportDateDescription = eventStage.reportDateDescription;
                            dhis2Event.dueDate = DateUtils.formatFromApiToUser(dhis2Event.dueDate);
                            dhis2Event.sortingDate = dhis2Event.dueDate;

                            if(dhis2Event.eventDate){
                                dhis2Event.eventDate = DateUtils.formatFromApiToUser(dhis2Event.eventDate);
                                dhis2Event.sortingDate = dhis2Event.eventDate;
                                dhis2Event.editingNotAllowed = dhis2Event.orgUnit !== $scope.selectedOrgUnit.id;
                            }                       

                            dhis2Event.statusColor = EventUtils.getEventStatusColor(dhis2Event);
                            dhis2Event = processEvent(dhis2Event, eventStage);
                            
                            if($scope.currentStage && $scope.currentStage.id === dhis2Event.programStage){
                                $scope.currentEvent = dhis2Event;                                
                                $scope.showDataEntry($scope.currentEvent, true);
                            }
                            $scope.eventsByStage[dhis2Event.programStage].push(dhis2Event);
                        }
                    }
                });
            }
            sortEventsByStage();  
            
            //Execute rules for the first time, to make the initial page appear correctly.
            //Subsequent calls will be made from the "saveDataValue" function.
            TrackerRulesExecutionService.executeRules($scope); 
        });          
    };
    
    $scope.enableRescheduling = function(){
        $scope.schedulingEnabled = !$scope.schedulingEnabled;
    };
    
    $scope.stageNeedsEvent = function(stage){  
      
        if($scope.eventsByStage[stage.id].length < 1){                
            return true;
        }

        if(stage.repeatable){
            for(var j=0; j<$scope.eventsByStage[stage.id].length; j++){
                if(!$scope.eventsByStage[stage.id][j].eventDate && $scope.eventsByStage[stage.id][j].status !== 'SKIPPED'){
                    return false;
                }
            }            
            return true;            
        }        
        return false;        
    };
    
    $scope.showCreateEvent = function(stage){
        
        var dummyEvent = EventUtils.createDummyEvent($scope.eventsByStage[stage.id], stage, $scope.selectedOrgUnit, $scope.selectedEnrollment);
        
        var modalInstance = $modal.open({
            templateUrl: 'components/dataentry/new-event.html',
            controller: 'EventCreationController',
            resolve: {
                dummyEvent: function(){
                    return dummyEvent;
                },
                programId: function () {
                    return $scope.selectedProgram.id;
                },
                trackedEntityInstanceId: function(){
                    return $scope.selectedEntity.trackedEntityInstance;
                }
            }
        });

        modalInstance.result.then(function (ev) {
            if(angular.isObject(ev)){
                var newEvent = ev;
                newEvent.orgUnitName = dummyEvent.orgUnitName;
                newEvent.name = dummyEvent.name;
                newEvent.reportDateDescription = dummyEvent.reportDateDescription;
                newEvent.sortingDate = ev.eventDate ? ev.eventDate : ev.dueDate,
                newEvent.statusColor = EventUtils.getEventStatusColor(ev);
                newEvent.eventDate = DateUtils.formatFromApiToUser(ev.eventDate);
                newEvent.dueDate =  DateUtils.formatFromApiToUser(ev.dueDate);
                newEvent.enrollmentStatus = dummyEvent.enrollmentStatus;
                
                if(dummyEvent.coordinate){
                    newEvent.coordinate = {};
                }                
                
                $scope.eventsByStage[newEvent.programStage].push(newEvent);
                $scope.showDataEntry(newEvent, false);
            }            
        }, function () {
        });
    };
    
    $scope.showDataEntry = function(event, rightAfterEnrollment){  
        
        if(event){

            if($scope.currentEvent && !rightAfterEnrollment && $scope.currentEvent.event === event.event){
                //clicked on the same stage, do toggling
                $scope.currentEvent = null;
                $scope.currentElement = {id: '', saved: false};
                $scope.showDataEntryDiv = !$scope.showDataEntryDiv;      
            }
            else{
                $scope.currentElement = {};
                $scope.currentEvent = event;
                $scope.showDataEntryDiv = true;
                $scope.showEventCreationDiv = false;

                if($scope.currentEvent.notes){
                    angular.forEach($scope.currentEvent.notes, function(note){
                        note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                    });

                    if($scope.currentEvent.notes.length > 0 ){
                        $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');
                    }
                }
                
                $scope.getDataEntryForm();
            }               
        }
    }; 
    
    $scope.switchDataEntryForm = function(){
        $scope.displayCustomForm = !$scope.displayCustomForm;
    };
    
    $scope.getDataEntryForm = function(){
        
        $scope.currentStage = $scope.selectedProgramWithStage[$scope.currentEvent.programStage];
        
        angular.forEach($scope.currentStage.programStageSections, function(section){
            section.open = true;
        });

        $scope.customForm = CustomFormService.getForProgramStage($scope.currentStage);
        $scope.displayCustomForm = $scope.customForm ? true:false;        

        $scope.currentEventOriginal = angular.copy($scope.currentEvent);        
    };
    
    var processEvent = function(event, stage){
        
        event.providedElsewhere = [];
        
        angular.forEach(event.dataValues, function(dataValue){
            
            var val = dataValue.value;
            var de = $scope.prStDes[dataValue.dataElement].dataElement;
            if(de){                
                if(val && de.type === 'string' && de.optionSet && $scope.optionSets[de.optionSet.id].options  ){
                    val = OptionSetService.getName($scope.optionSets[de.optionSet.id].options, val);
                }
                if(val && de.type === 'date'){
                    val = DateUtils.formatFromApiToUser(val);
                }
                if(de.type === 'trueOnly'){
                    if(val === 'true'){
                        val = true;
                    }
                    else{
                        val = '';
                    }
                }
                if(de.type === 'int') {
                    val = Number(val);
                }
            }    
            event[dataValue.dataElement] = val;
            if(dataValue.providedElsewhere){
                event.providedElsewhere[dataValue.dataElement] = dataValue.providedElsewhere;
            }
        });
        
        if(stage.captureCoordinates){
            event.coordinate = {latitude: event.coordinate.latitude ? event.coordinate.latitude : '',
                                     longitude: event.coordinate.longitude ? event.coordinate.longitude : ''};
        }
        
        
        event.allowProvidedElsewhereExists = false;
        angular.forEach(stage.programStageDataElements, function(prStDe){
            if(prStDe.allowProvidedElsewhere){
                event.allowProvidedElsewhereExists = true;                
            }
        });
        
        return event;
    };
    
    $scope.saveDatavalue = function(prStDe){

        //check for input validity
        $scope.dataEntryOuterForm.submitted = true;        
        if( $scope.dataEntryOuterForm.$invalid ){            
            return false;
        }

        //input is valid        
        var value = $scope.currentEvent[prStDe.dataElement.id];
        
        if($scope.currentEventOriginal[prStDe.dataElement.id] !== value){
            
            if(value){
                if(prStDe.dataElement.type === 'date'){                    
                    value = DateUtils.formatFromUserToApi(value);
                }
                if(prStDe.dataElement.type === 'string'){                    
                    if(prStDe.dataElement.optionSet && $scope.optionSets[prStDe.dataElement.optionSet.id] &&  $scope.optionSets[prStDe.dataElement.optionSet.id].options ) {
                        value = OptionSetService.getCode($scope.optionSets[prStDe.dataElement.optionSet.id].options, value);
                    }                    
                }
            }

            $scope.updateSuccess = false;

            $scope.currentElement = {id: prStDe.dataElement.id, saved: false};

            var ev = {  event: $scope.currentEvent.event,
                        orgUnit: $scope.currentEvent.orgUnit,
                        program: $scope.currentEvent.program,
                        programStage: $scope.currentEvent.programStage,
                        status: $scope.currentEvent.status,
                        trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                        dataValues: [
                                        {
                                            dataElement: prStDe.dataElement.id, 
                                            value: value, 
                                            providedElsewhere: $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] ? true : false
                                        }
                                    ]
                     };
            DHIS2EventFactory.updateForSingleValue(ev).then(function(response){
                var index = -1;
                for(var i=0; i<$scope.eventsByStage[$scope.currentEvent.programStage].length && index === -1; i++){
                    if($scope.eventsByStage[$scope.currentEvent.programStage][i].event === $scope.currentEvent.event){
                        index = i;
                    }
                }
                if(index !== -1){
                    $scope.eventsByStage[$scope.currentEvent.programStage].splice(index,1,$scope.currentEvent);
                }
                $scope.currentElement.saved = true;
                $scope.currentEventOriginal = angular.copy($scope.currentEvent);
				TrackerRulesExecutionService.executeRules($scope);
            });
            
        }
    };
    
    $scope.saveDatavalueLocation = function(prStDe){
                
        $scope.updateSuccess = false;
        
        if(!angular.isUndefined($scope.currentEvent.providedElsewhere[prStDe.dataElement.id])){

            //currentEvent.providedElsewhere[prStDe.dataElement.id];
            var value = $scope.currentEvent[prStDe.dataElement.id];
            var ev = {  event: $scope.currentEvent.event,
                        orgUnit: $scope.currentEvent.orgUnit,
                        program: $scope.currentEvent.program,
                        programStage: $scope.currentEvent.programStage,
                        status: $scope.currentEvent.status,
                        trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                        dataValues: [
                                        {
                                            dataElement: prStDe.dataElement.id, 
                                            value: value, 
                                            providedElsewhere: $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] ? true : false
                                        }
                                    ]
                     };
            DHIS2EventFactory.updateForSingleValue(ev).then(function(response){
                $scope.updateSuccess = true;
            });            
        }        
    };
    
    $scope.saveEventDate = function(){
        
        $scope.eventDateSaved = false;
        if($scope.currentEvent.eventDate === ''){            
            $scope.invalidDate = true;
            return false;
        }
        
        var rawDate = angular.copy($scope.currentEvent.eventDate);
        var convertedDate = DateUtils.format($scope.currentEvent.eventDate);
        
        if(rawDate !== convertedDate){
            $scope.invalidDate = true;
            return false;
        }
        
        var e = {event: $scope.currentEvent.event,
             enrollment: $scope.currentEvent.enrollment,
             dueDate: DateUtils.formatFromUserToApi($scope.currentEvent.dueDate),
             status: $scope.currentEvent.status === 'SCHEDULE' ? 'ACTIVE' : $scope.currentEvent.status,
             program: $scope.currentEvent.program,
             programStage: $scope.currentEvent.programStage,
             orgUnit: $scope.currentEvent.dataValues && $scope.currentEvent.dataValues.length > 0 ? $scope.currentEvent.orgUnit : $scope.selectedOrgUnit.id,
             eventDate: DateUtils.formatFromUserToApi($scope.currentEvent.eventDate),
             trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
            };

        DHIS2EventFactory.updateForEventDate(e).then(function(data){
            $scope.currentEvent.sortingDate = $scope.currentEvent.eventDate;
            $scope.invalidDate = false;
            $scope.eventDateSaved = true;
            $scope.currentEvent.statusColor = EventUtils.getEventStatusColor($scope.currentEvent);
        });
    };
    
    $scope.saveDueDate = function(){
        $scope.dueDateSaved = false;

        if($scope.currentEvent.dueDate === ''){
            $scope.invalidDate = true;
            return false;
        }
        
        var rawDate = angular.copy($scope.currentEvent.dueDate);
        var convertedDate = DateUtils.format($scope.currentEvent.dueDate);           

        if(rawDate !== convertedDate){
            $scope.invalidDate = true;
            return false;
        } 
        
        var e = {event: $scope.currentEvent.event,
             enrollment: $scope.currentEvent.enrollment,
             dueDate: DateUtils.formatFromUserToApi($scope.currentEvent.dueDate),
             status: $scope.currentEvent.status,
             program: $scope.currentEvent.program,
             programStage: $scope.currentEvent.programStage,
             orgUnit: $scope.selectedOrgUnit.id,
             trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
            };
        
        DHIS2EventFactory.update(e).then(function(data){            
            $scope.invalidDate = false;
            $scope.dueDateSaved = true;
            $scope.currentEvent.sortingDate = $scope.currentEvent.dueDate;
            $scope.currentEvent.statusColor = EventUtils.getEventStatusColor($scope.currentEvent);            
            $scope.schedulingEnabled = !$scope.schedulingEnabled;
        });
                      
    };
    
    $scope.saveCoordinate = function(type){
        
        if(type === 'LAT' || type === 'LATLNG' ){
            $scope.latitudeSaved = false;
        }
        if(type === 'LAT' || type === 'LATLNG'){
            $scope.longitudeSaved = false;
        }
        
        if( (type === 'LAT' || type === 'LATLNG') && $scope.outerForm.latitude.$invalid  || 
            (type === 'LNG' || type === 'LATLNG') && $scope.outerForm.longitude.$invalid ){//invalid coordinate            
            return;            
        }
        
        if( (type === 'LAT' || type === 'LATLNG') && $scope.currentEvent.coordinate.latitude === $scope.currentEventOriginal.coordinate.latitude  || 
            (type === 'LNG' || type === 'LATLNG') && $scope.currentEvent.coordinate.longitude === $scope.currentEventOriginal.coordinate.longitude){//no change            
            return;            
        }
        
        //valid coordinate(s), proceed with the saving
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);
        
        DHIS2EventFactory.update(dhis2Event).then(function(response){            
            $scope.currentEventOriginal = angular.copy($scope.currentEvent);
            if(type === 'LAT' || type === 'LATLNG' ){
                $scope.latitudeSaved = true;
            }
            if(type === 'LAT' || type === 'LATLNG'){
                $scope.longitudeSaved = true;
            }
        });
    };
    
    $scope.addNote = function(){
        if(!angular.isUndefined($scope.note) && $scope.note !== ""){
            var newNote = {value: $scope.note};

            if(angular.isUndefined( $scope.currentEvent.notes) ){
                $scope.currentEvent.notes = [{value: $scope.note, storedDate: today, storedBy: storedBy}];
            }
            else{
                $scope.currentEvent.notes.splice(0,0,{value: $scope.note, storedDate: today, storedBy: storedBy});
            }

            var e = {event: $scope.currentEvent.event,
                     program: $scope.currentEvent.program,
                     programStage: $scope.currentEvent.programStage,
                     orgUnit: $scope.currentEvent.orgUnit,
                     trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                     notes: [newNote]
                    };

            DHIS2EventFactory.updateForNote(e).then(function(data){
                $scope.note = ''; 
            });
        }        
    };    
    
    $scope.clearNote = function(){
         $scope.note = '';           
    };
    
    $scope.getInputNotifcationClass = function(id, custom){
        if($scope.currentElement.id){
            if($scope.currentElement.saved && ($scope.currentElement.id === id)){
                if(custom){
                    return 'input-success';
                }
                return 'form-control input-success';
            }            
            if(!$scope.currentElement.saved && ($scope.currentElement.id === id)){
                if(custom){
                    return 'input-error';
                }
                return 'form-control input-error';
            }            
        }  
        if(custom){
            return '';
        }
        return 'form-control';
    };
    
    $scope.completeIncompleteEvent = function(){
        var modalOptions;
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);        
        if($scope.currentEvent.status === 'COMPLETED'){//activiate event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'incomplete',
                headerText: 'incomplete',
                bodyText: 'are_you_sure_to_incomplete_event'
            };
            dhis2Event.status = 'ACTIVE';        
        }
        else{//complete event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'complete',
                headerText: 'complete',
                bodyText: 'are_you_sure_to_complete_event'
            };
            dhis2Event.status = 'COMPLETED';
        }        

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.update(dhis2Event).then(function(data){
                
                if($scope.currentEvent.status === 'COMPLETED'){//activiate event                    
                    $scope.currentEvent.status = 'ACTIVE'; 
                }
                else{//complete event                    
                    $scope.currentEvent.status = 'COMPLETED';
                }
                
                setStatusColor();
                
                if($scope.currentEvent.status === 'COMPLETED' && $scope.currentStage.allowGenerateNextVisit){
                    $scope.showCreateEvent($scope.currentStage);
                }
            });
        });
    };
    
    $scope.skipUnskipEvent = function(){
        var modalOptions;
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);   

        if($scope.currentEvent.status === 'SKIPPED'){//unskip event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'unskip',
                headerText: 'unskip',
                bodyText: 'are_you_sure_to_unskip_event'
            };
            dhis2Event.status = 'ACTIVE';        
        }
        else{//skip event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'skip',
                headerText: 'skip',
                bodyText: 'are_you_sure_to_skip_event'
            };
            dhis2Event.status = 'SKIPPED';
        }        

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.update(dhis2Event).then(function(data){
                
                if($scope.currentEvent.status === 'SKIPPED'){//activiate event                    
                    $scope.currentEvent.status = 'SCHEDULE'; 
                }
                else{//complete event                    
                    $scope.currentEvent.status = 'SKIPPED';
                }
                
                setStatusColor();                
            });
        });
    };
    
    var setStatusColor = function(){
        var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
        var continueLoop = true;
        for(var i=0; i< $scope.eventsByStage[$scope.currentEvent.programStage].length && continueLoop; i++){
            if($scope.eventsByStage[$scope.currentEvent.programStage][i].event === $scope.currentEvent.event ){
                $scope.eventsByStage[$scope.currentEvent.programStage][i].statusColor = statusColor;
                $scope.currentEvent.statusColor = statusColor;
                continueLoop = false;
            }
        }
    };
    
    $scope.validateEvent = function(){};    
    
    $scope.deleteEvent = function(){
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'delete',
            headerText: 'delete',
            bodyText: 'are_you_sure_to_delete_event'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.delete($scope.currentEvent).then(function(data){
                
                var continueLoop = true, index = -1;
                for(var i=0; i< $scope.eventsByStage[$scope.currentEvent.programStage].length && continueLoop; i++){
                    if($scope.eventsByStage[$scope.currentEvent.programStage][i].event === $scope.currentEvent.event ){
                        $scope.eventsByStage[$scope.currentEvent.programStage][i] = $scope.currentEvent;
                        continueLoop = false;
                        index = i;
                    }
                }
                $scope.eventsByStage[$scope.currentEvent.programStage].splice(index,1);
                $scope.currentEvent = null;
            });
        });
    };
    
    $scope.toggleLegend = function(){
        $scope.showEventColors = !$scope.showEventColors;
    };
    
    $scope.getEventStyle = function(ev){
        var style = EventUtils.getEventStatusColor(ev);
        
        if($scope.currentEvent && $scope.currentEvent.event === ev.event){
            style = style + ' ' + 'current-stage';
        }       
        return style;
    };
    
    $scope.getColumnWidth = function(weight){        
        var width = weight <= 1 ? 1 : weight;
        width = (width/$scope.totalEvents)*100;
        return "width: " + width + '%';
    };
    
    var sortEventsByStage = function(){
        for(var key in $scope.eventsByStage){
            if($scope.eventsByStage.hasOwnProperty(key)){
                $scope.eventsByStage[key] = orderByFilter($scope.eventsByStage[key], '-sortingDate').reverse(); 
                $scope.totalEvents += $scope.eventsByStage[key].length <=1 ? 1:$scope.eventsByStage[key].length;
            }
        }
    };
    
    $scope.showMap = function(event){
        var modalInstance = $modal.open({
            templateUrl: '../dhis-web-commons/coordinatecapture/map.html',
            controller: 'MapController',
            windowClass: 'modal-full-window',
            resolve: {
                location: function () {
                    return {lat: event.coordinate.latitude, lng: event.coordinate.longitude};
                }
            }
        });

        modalInstance.result.then(function (location) {
            if(angular.isObject(location)){
                event.coordinate.latitude = location.lat;
                event.coordinate.longitude = location.lng;                
                $scope.saveCoordinate('LATLNG');
            }
        }, function () {
        });
    };
    
})

.controller('EventCreationController', 
    function($scope, 
            $modalInstance, 
            DateUtils,
            DHIS2EventFactory,
            DialogService,
            dummyEvent,
            programId,
            trackedEntityInstanceId){
    $scope.programStageId = dummyEvent.programStage;
    $scope.programId = programId;
    $scope.orgUnitId = dummyEvent.orgUnit;    
    $scope.trackedEntityInstanceId = trackedEntityInstanceId;
    
    $scope.dhis2Event = {eventDate: '', dueDate: dummyEvent.dueDate, reportDateDescription: dummyEvent.reportDateDescription, name: dummyEvent.name, invalid: true};
    
    $scope.dueDateInvalid = false;
    $scope.eventDateInvalid = false;
    
    //watch for changes in eventDate
    $scope.$watchCollection('[dhis2Event.dueDate, dhis2Event.eventDate]', function() {        
        if(angular.isObject($scope.dhis2Event)){
            if(!$scope.dhis2Event.dueDate){
                $scope.dueDateInvalid = true;
                return;
            }
            
            if($scope.dhis2Event.dueDate){
                var rDueDate = $scope.dhis2Event.dueDate;
                var cDueDate = DateUtils.format($scope.dhis2Event.dueDate);                
                $scope.dueDateInvalid = rDueDate !== cDueDate;
            }
            
            if($scope.dhis2Event.eventDate){
                var rEventDate = $scope.dhis2Event.eventDate;
                var cEventDate = DateUtils.format($scope.dhis2Event.eventDate);
                $scope.eventDateInvalid = rEventDate !== cEventDate;
            }
        }
    });
    
    $scope.save = function () {
        //check for form validity
        if($scope.dueDateInvalid || $scope.eventDateInvalid){
            return false;
        }
        
        var eventDate = DateUtils.formatFromUserToApi($scope.dhis2Event.eventDate);
        var dueDate = DateUtils.formatFromUserToApi($scope.dhis2Event.dueDate);
        var newEvents = {events: []};
        var newEvent = {
                trackedEntityInstance: $scope.trackedEntityInstanceId,
                program: $scope.programId,
                programStage: $scope.programStageId,
                orgUnit: $scope.orgUnitId,                        
                dueDate: dueDate,
                eventDate: eventDate,
                notes: [],
                dataValues: [],
                status: 'ACTIVE'
            };
        newEvents.events.push(newEvent);        
        DHIS2EventFactory.create(newEvents).then(function(data){
            if (data.importSummaries[0].status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'event_creation_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                newEvent.event = data.importSummaries[0].reference;                
                $modalInstance.close(newEvent);
            }
        });
    };
    
    $scope.cancel = function(){
        $modalInstance.close();
    };      
});