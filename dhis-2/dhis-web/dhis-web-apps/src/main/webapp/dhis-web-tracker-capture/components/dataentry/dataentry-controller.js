/* global angular, trackerCapture */

trackerCapture.controller('DataEntryController',
        function ($rootScope,
                $scope,
                $modal,
                $filter,
                $log,
                $timeout,
                $translate,
                Paginator,
                CommonUtils,
                DateUtils,
                EventUtils,
                orderByFilter,
                SessionStorageService,
                EnrollmentService,
                ProgramStageFactory,
                DHIS2EventFactory,
                ModalService,
                DialogService,
                CurrentSelection,
                TrackerRulesExecutionService,
                CustomFormService,
                PeriodService,
                TrackerRulesFactory,
                EventCreationService,
                $q) {

    $scope.maxOptionSize = 30;
    $scope.dashboardReady = false;
    
    //Data entry form
    $scope.outerForm = {};
    $scope.displayCustomForm = false;
    $scope.currentElement = {};
    $scope.schedulingEnabled = false;
    $scope.eventPeriods = [];
    $scope.currentPeriod = [];
    $scope.filterEvents = true;
    $scope.showEventsAsTables = true;
    //variable is set while looping through the program stages later.
    $scope.stagesCanBeShownAsTable = false;
    $scope.showHelpText = {};
    $scope.hiddenFields = [];
    $scope.assignedFields = [];
    $scope.errorMessages = {};
    $scope.warningMessages = {};
    $scope.hiddenSections = {};
    $scope.tableMaxNumberOfDataElements = 10;
    $scope.xVisitScheduleDataElement = false;

    
    //Labels
    $scope.dataElementLabel = $translate.instant('data_element');
    $scope.valueLabel = $translate.instant('value');
    $scope.providedElsewhereLabel = $translate.instant('provided_elsewhere');
    
    $scope.EVENTSTATUSCOMPLETELABEL = "COMPLETED";
    $scope.EVENTSTATUSSKIPPEDLABEL = "SKIPPED";
    $scope.EVENTSTATUSVISITEDLABEL = "VISITED";
    $scope.EVENTSTATUSACTIVELABEL = "ACTIVE";
    $scope.EVENTSTATUSSCHEDULELABEL = "SCHEDULE";
    $scope.validatedDateSetForEvent = {};
    $scope.eventCreationActions = EventCreationService.eventCreationActions;
    
    var userProfile = SessionStorageService.get('USER_PROFILE');
    var storedBy = userProfile && userProfile.username ? userProfile.username : '';

    var today = DateUtils.getToday();
    $scope.invalidDate = false;

    //note
    $scope.note = {};

    //event color legend
    $scope.eventColors = [
        {color: 'alert-success ', description: 'completed'},
        {color: 'alert-info ', description: 'executed'},
        {color: 'alert-warning ', description: 'ontime'},
        {color: 'alert-danger ', description: 'overdue'},
        {color: 'alert-default ', description: 'skipped'}
    ];
    $scope.showEventColors = false;

    //listen for new events created
    $scope.$on('eventcreated', function (event, args) {
        //TODO: Sort this out:
        $scope.addNewEvent(args.event);
    });
    
    $scope.$on('teiupdated', function(event, args){
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        $scope.executeRules();
    });

    //listen for rule effect changes
    $scope.$on('ruleeffectsupdated', function (event, args) {
        if ($rootScope.ruleeffects[args.event]) {
            processRuleEffect(args.event);            
        }
    });

    $scope.showReferral = false;
    //Check if user is allowed to make referrals
    var roles = SessionStorageService.get('USER_ROLES');
    if( roles && roles.userCredentials && roles.userCredentials.userRoles){
        var userRoles = roles.userCredentials.userRoles;
        for(var i=0; i<userRoles.length; i++){
            if(userRoles[i].authorities.indexOf('ALL') !== -1 || userRoles[i].authorities.indexOf('F_TRACKED_ENTITY_INSTANCE_SEARCH_IN_ALL_ORGUNITS') !== -1 ){
              $scope.showReferral = true;
              i=userRoles.length;
            }
        } 
    }
    $scope.model= {};

    $scope.print = function(divName){
        $scope.showProgramReportDetailsDiv = false;
        var printContents =  document.getElementById(divName).innerHTML;
        //var popupWin = window.open('', '_blank', 'resizable=1,scrollbars=no,width=500,height=200');
        var popupWin = window.open('', '_blank', 'fullscreen=1');
        popupWin.document.open();
        popupWin.document.write('<html>\n\
                                     <head>\n\
                                         <link rel="stylesheet" type="text/css" href="styles/bootstrap.min.css" media="print"/>\n\
                                         <link rel="stylesheet" type="text/css" href="styles/print.css" media="print"/>\n\
                                     </head>\n\
                                     <body onload="window.print()">' + printContents +
                                '</html>');
        popupWin.document.close();

    };

    var processRuleEffect = function(event){
        //Establish which event was affected:
        var affectedEvent = $scope.currentEvent;
        //In most cases the updated effects apply to the current event. In case the affected event is not the current event, fetch the correct event to affect:
        if(event === 'registration') return;

        if (event !== affectedEvent.event) {
            angular.forEach($scope.currentStageEvents, function (searchedEvent) {
                if (searchedEvent.event === event) {
                    affectedEvent = searchedEvent;
                }
            });
        }

        $scope.assignedFields[event] = [];
        $scope.hiddenSections[event] = [];
        $scope.warningMessages[event] = [];
        $scope.errorMessages[event] = [];
        $scope.hiddenFields[event] = [];
        
        angular.forEach($rootScope.ruleeffects[event], function (effect) {
            if (effect.dataElement) {
                //in the data entry controller we only care about the "hidefield", showerror and showwarning actions
                if (effect.action === "HIDEFIELD") {                    
                    if (effect.dataElement) {
                        
                        if (effect.ineffect && affectedEvent[effect.dataElement.id]) {
                            //If a field is going to be hidden, but contains a value, we need to take action;
                            if (effect.content) {
                                //TODO: Alerts is going to be replaced with a proper display mecanism.
                                alert(effect.content);
                            }
                            else {
                                //TODO: Alerts is going to be replaced with a proper display mecanism.
                                alert($scope.prStDes[effect.dataElement.id].dataElement.formName + "Was blanked out and hidden by your last action");
                            }

                            //Blank out the value:
                            affectedEvent[effect.dataElement.id] = "";
                            $scope.saveDatavalueForEvent($scope.prStDes[effect.dataElement.id], null, affectedEvent);
                        }

                        if(effect.ineffect) {
                            $scope.hiddenFields[event][effect.dataElement.id] = true;
                        } 
                        else if( !$scope.hiddenFields[event][effect.dataElement.id]) {
                            $scope.hiddenFields[event][effect.dataElement.id] = false;
                        }

                    }
                    else {
                        $log.warn("ProgramRuleAction " + effect.id + " is of type HIDEFIELD, bot does not have a dataelement defined");
                    }
                } else if (effect.action === "SHOWERROR") {
                    if (effect.ineffect) {
                        
                        if(effect.dataElement) {                           
                            var message = effect.content;
                            $scope.errorMessages[event][effect.dataElement.id] = message;
                            $scope.errorMessages[event].push($translate.instant($scope.prStDes[effect.dataElement.id].dataElement.name) + ": " + message);
                        }
                        else
                        {
                            $scope.errorMessages.push(message);
                        }
                    }
                    else {
                        
                    }
                } else if (effect.action === "SHOWWARNING") {
                    if (effect.ineffect) {
                        if(effect.dataElement) {
                            var message = effect.content;
                            $scope.warningMessages[event][effect.dataElement.id] = message;
                            $scope.warningMessages[event].push($translate.instant($scope.prStDes[effect.dataElement.id].dataElement.name) + ": " + message);
                        } else {
                            $scope.warningMessages[event].push(message);
                        }
                    }
                } else if (effect.action === "HIDESECTION"){                    
                    if(effect.programStageSection){
                        if(effect.ineffect){
                            $scope.hiddenSections[event][effect.programStageSection] = true;
                        } else{
                            $scope.hiddenSections[event][effect.programStageSection] = false;
                        }
                    }
                    else {
                        $log.warn("ProgramRuleAction " + effect.id + " is of type HIDESECTION, bot does not have a section defined");
                    }
                } else if (effect.action === "ASSIGN") {
                    if(effect.ineffect && effect.dataElement) {
                        //For "ASSIGN" actions where we have a dataelement, we save the calculated value to the dataelement:
                        //Blank out the value:
                        var processedValue = $filter('trimquotes')(effect.data);
                        affectedEvent[effect.dataElement.id] = processedValue;
                        $scope.assignedFields[event][effect.dataElement.id] = true;
                        $scope.saveDatavalueForEvent($scope.prStDes[effect.dataElement.id], null, affectedEvent);
                    }
                }
            }
        });
    };
    
    //check if field is hidden
    $scope.isHidden = function (id, event) {
        //In case the field contains a value, we cant hide it. 
        //If we hid a field with a value, it would falsely seem the user was aware that the value was entered in the UI.
        var EventToCheck = angular.isDefined(event) ? event : $scope.currentEvent;
        
        if (EventToCheck[id]) {
            return false;
        }
        else {            
            if(angular.isDefined($scope.hiddenFields[EventToCheck.event])){
                return $scope.hiddenFields[EventToCheck.event][id];
            }
            else {
                return false;
            }            
        }
    };

    $scope.executeRules = function () {        
        
        //$scope.allEventsSorted cannot be used, as it is not reflecting updates that happened within the current session
        var allSorted = [];
        for(var ps = 0; ps < $scope.programStages.length; ps++ ) {
            for(var e = 0; e < $scope.eventsByStageAsc[$scope.programStages[ps].id].length; e++) {
                allSorted.push($scope.eventsByStageAsc[$scope.programStages[ps].id][e]);
            }
        }
        allSorted = orderByFilter(allSorted, '-sortingDate').reverse();
        
        var evs = {all: allSorted, byStage: $scope.eventsByStageAsc};
        var flag = {debug: true, verbose: true};

        //If the events is displayed in a table, it is necessary to run the rules for all visible events.        
        if ($scope.currentStage.displayEventsInTable && angular.isUndefined($scope.currentStage.rulesExecuted)){
            angular.forEach($scope.currentStageEvents, function (event) {
                TrackerRulesExecutionService.executeRules($scope.allProgramRules, event, evs, $scope.prStDes, $scope.selectedTei, $scope.selectedEnrollment, flag);
                $scope.currentStage.rulesExecuted = true;
            });
        } else {
            TrackerRulesExecutionService.executeRules($scope.allProgramRules, $scope.currentEvent, evs, $scope.prStDes, $scope.selectedTei, $scope.selectedEnrollment, flag);
        }
    };


    //listen for the selected items
    $scope.$on('dashboardWidgets', function () {
        $scope.dashboardReady = true;
        $scope.showDataEntryDiv = false;
        $scope.showEventCreationDiv = false;
        $scope.currentEvent = null;
        $scope.currentStage = null;
        $scope.currentStageEvents = null;
        $scope.totalEvents = 0;

        $scope.allowEventCreation = false;
        $scope.repeatableStages = [];
        $scope.eventsByStage = [];
        $scope.eventsByStageAsc = [];
        $scope.programStages = [];
        $rootScope.ruleeffects = {};
        $scope.prStDes = [];
        $scope.allProgramRules = [];
        $scope.allowProvidedElsewhereExists = [];
        
        var selections = CurrentSelection.get();
        $scope.selectedOrgUnit = SessionStorageService.get('SELECTED_OU');        
        $scope.selectedEntity = selections.tei;
        $scope.selectedProgram = selections.pr;
        $scope.selectedEnrollment = selections.selectedEnrollment;        
        $scope.optionSets = selections.optionSets;

        $scope.stagesById = [];
        if ($scope.selectedOrgUnit && $scope.selectedProgram && $scope.selectedProgram.id && $scope.selectedEntity && $scope.selectedEnrollment && $scope.selectedEnrollment.enrollment) {
            ProgramStageFactory.getByProgram($scope.selectedProgram).then(function (stages) {
                
                $scope.programStages = stages;
                angular.forEach(stages, function (stage) {
                    if (stage.openAfterEnrollment) {
                        $scope.currentStage = stage;
                    }
          
                    angular.forEach(stage.programStageDataElements, function (prStDe) {
                        $scope.prStDes[prStDe.dataElement.id] = prStDe;
                        if(prStDe.allowProvidedElsewhere){
                            $scope.allowProvidedElsewhereExists[stage.id] = true;
                        }
                    });

                    $scope.stagesById[stage.id] = stage;
                    $scope.eventsByStage[stage.id] = [];

                    //If one of the stages has less than $scope.tableMaxNumberOfDataElements data elements, allow sorting as table:
                    if (stage.programStageDataElements.length < $scope.tableMaxNumberOfDataElements) {
                        $scope.stagesCanBeShownAsTable = true;
                    }
                });

                $scope.programStages = orderByFilter($scope.programStages, '-sortOrder').reverse();
                if (!$scope.currentStage) {
                    $scope.currentStage = $scope.programStages[0];
                }
                
                $scope.setDisplayTypeForStages();
                
                TrackerRulesFactory.getRules($scope.selectedProgram.id).then(function(rules){                    
                    $scope.allProgramRules = rules;
                    $scope.getEvents();                       
                    $rootScope.$broadcast('dataEntryControllerData', {programStages: $scope.programStages, eventsByStage: $scope.eventsByStage, addNewEvent: $scope.addNewEvent });
                });           
            });
        }
    });

    $scope.getEvents = function () {

        var events = CurrentSelection.getSelectedTeiEvents();
        events = $filter('filter')(events, {program: $scope.selectedProgram.id});
        if (angular.isObject(events)) {
            angular.forEach(events, function (dhis2Event) {
                var multiSelectsFound = [];
                if (dhis2Event.enrollment === $scope.selectedEnrollment.enrollment && dhis2Event.orgUnit) {
                    if (dhis2Event.notes) {
                        dhis2Event.notes = orderByFilter(dhis2Event.notes, '-storedDate');
                        angular.forEach(dhis2Event.notes, function (note) {
                            note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                        });
                    }

                    var eventStage = $scope.stagesById[dhis2Event.programStage];
                    if (angular.isObject(eventStage)) {
                        dhis2Event.name = eventStage.name;
                        dhis2Event.excecutionDateLabel = eventStage.excecutionDateLabel ? eventStage.excecutionDateLabel : $translate.instant('report_date');
                        dhis2Event.dueDate = DateUtils.formatFromApiToUser(dhis2Event.dueDate);
                        dhis2Event.sortingDate = dhis2Event.dueDate;

                        if (dhis2Event.eventDate) {
                            dhis2Event.eventDate = DateUtils.formatFromApiToUser(dhis2Event.eventDate);
                            dhis2Event.sortingDate = dhis2Event.eventDate;                            
                        }

                        dhis2Event.editingNotAllowed = setEventEditing(dhis2Event, eventStage);
                        
                        dhis2Event.statusColor = EventUtils.getEventStatusColor(dhis2Event);
                        dhis2Event = EventUtils.processEvent(dhis2Event, eventStage, $scope.optionSets, $scope.prStDes);
                        $scope.eventsByStage[dhis2Event.programStage].push(dhis2Event);
                        angular.forEach($scope.programStages, function(programStage) {
                            if(dhis2Event.programStage === programStage.id) {
                                angular.forEach(programStage.programStageDataElements, function(dataElement) {
                                    if(dataElement.dataElement.dataElementGroups && dataElement.dataElement.dataElementGroups.length > 0){
                                        angular.forEach(dataElement.dataElement.dataElementGroups, function(dataElementGroup){
                                            if(multiSelectsFound.indexOf(dataElementGroup.id)< 0) {
                                                dhis2Event[dataElementGroup.id] = {selections:[]};
                                                multiSelectsFound.push(dataElementGroup.id);
                                            }
                                            if(dhis2Event[dataElement.dataElement.id]){
                                            dhis2Event[dataElementGroup.id].selections.push(dataElement.dataElement);       
                                            }
                                        });
                                    } 
                                });
                            }
                        });
                        if ($scope.currentStage && $scope.currentStage.id === dhis2Event.programStage) {
                            $scope.currentEvent = dhis2Event;
                        }
                    }
                }
            });

            sortEventsByStage(null);
            $scope.showDataEntry($scope.currentEvent, true);
        }
    };
    

    var setEventEditing = function (dhis2Event, stage) {
        return dhis2Event.editingNotAllowed = dhis2Event.orgUnit !== $scope.selectedOrgUnit.id && dhis2Event.eventDate || (stage.blockEntryForm && dhis2Event.status === 'COMPLETED') || $scope.selectedEntity.inactive;
    };

    $scope.enableRescheduling = function () {
        $scope.schedulingEnabled = !$scope.schedulingEnabled;
    };

    $scope.stageCanBeShownAsTable = function (stage) {
        if (stage.programStageDataElements && stage.programStageDataElements.length < $scope.tableMaxNumberOfDataElements) {
            return true;
        }
        return false;
    };

    $scope.toggleEventsTableDisplay = function () {        
        
        $scope.showEventsAsTables = !$scope.showEventsAsTables;                
        
        angular.forEach($scope.programStages, function (stage) {
            if (stage.programStageDataElements.length < $scope.tableMaxNumberOfDataElements) {
                stage.displayEventsInTable = $scope.showEventsAsTables;
                if ($scope.currentStage === stage) {
                    $scope.getDataEntryForm();
                }
            }
        });
    };
    
    $scope.setDisplayTypeForStages = function(){
        angular.forEach($scope.programStages, function (stage) {
            $scope.setDisplayTypeForStage(stage);
        });
    }    
    
    $scope.setDisplayTypeForStage = function(stage){
        if (stage.programStageDataElements.length < $scope.tableMaxNumberOfDataElements) {
                    stage.displayEventsInTable = $scope.showEventsAsTables;
        }
    }

    $scope.stageNeedsEvent = function (stage) {
        //In case the event is a table, we sould always allow adding more events(rows)
        if (stage.displayEventsInTable) {
            return true;
        }

        if ($scope.eventsByStage[stage.id].length < 1) {
            return true;
        }

        if (stage.repeatable) {
            for (var j = 0; j < $scope.eventsByStage[stage.id].length; j++) {
                if (!$scope.eventsByStage[stage.id][j].eventDate && $scope.eventsByStage[stage.id][j].status !== 'SKIPPED') {
                    return false;
                }
            }
            return true;
        }
        return false;
    };
    
    $scope.addNewEvent = function(newEvent) {
        //Have to make sure the event is preprocessed - this does not happen unless "Dashboardwidgets" is invoked.
        newEvent = EventUtils.processEvent(newEvent, $scope.stagesById[newEvent.programStage], $scope.optionSets, $scope.prStDes);

        $scope.eventsByStage[newEvent.programStage].push(newEvent);
        
        sortEventsByStage('ADD', newEvent);
    };

    $scope.showCreateEvent = function (stage,eventCreationAction) {
        var dummyEvent = EventUtils.createDummyEvent($scope.eventsByStage[stage.id], $scope.selectedEntity, $scope.selectedProgram, stage, $scope.selectedOrgUnit, $scope.selectedEnrollment);
        
        var modalInstance = $modal.open({
            templateUrl: 'components/dataentry/new-event.html',
            controller: 'EventCreationController',
            resolve: {
                stage: function () {
                    return $scope.stagesById[stage.id];
                },
                dummyEvent: function () {
                    return dummyEvent;
                },
                autoCreate: function () {
                    //In case the programstage is a table, autocreate
                    return stage.displayEventsInTable;
                },
                eventCreationAction: function(){
                    return eventCreationAction;
                }                
            }
        });

        modalInstance.result.then(function (ev) {
            if (angular.isObject(ev)) {
                
                var newEvent = ev;
                newEvent.orgUnitName = dummyEvent.orgUnitName;
                newEvent.name = dummyEvent.name;
                newEvent.excecutionDateLabel = dummyEvent.excecutionDateLabel;
                newEvent.sortingDate = ev.eventDate ? ev.eventDate : ev.dueDate,
                newEvent.statusColor = EventUtils.getEventStatusColor(ev);
                newEvent.eventDate = DateUtils.formatFromApiToUser(ev.eventDate);
                newEvent.dueDate = DateUtils.formatFromApiToUser(ev.dueDate);
                newEvent.enrollmentStatus = dummyEvent.enrollmentStatus;

                if (dummyEvent.coordinate) {
                    newEvent.coordinate = {};
                }
                
                $scope.currentStage = stage;
                $scope.addNewEvent(newEvent);

                $scope.currentEvent = null;
                $scope.showDataEntry(newEvent, false);
            }
        }, function () {
        });
    };

    $scope.showDataEntry = function (event, rightAfterEnrollment) {
        if (event) {
            
            Paginator.setItemCount($scope.eventsByStage[event.programStage].length);
            Paginator.setPage($scope.eventsByStage[event.programStage].indexOf(event));
            Paginator.setPageCount(Paginator.getItemCount());
            Paginator.setPageSize(1);
            Paginator.setToolBarDisplay(5);

            if ($scope.currentEvent && !rightAfterEnrollment && $scope.currentEvent.event === event.event) {
                //clicked on the same stage, do toggling
                $scope.currentEvent = null;
                $scope.currentElement = {id: '', saved: false};
                $scope.showDataEntryDiv = !$scope.showDataEntryDiv;
            }
            else {
                $scope.currentElement = {};                
                $scope.currentEvent = event;
                
                var index = -1;
                for (var i = 0; i < $scope.eventsByStage[event.programStage].length && index === -1; i++) {
                    if ($scope.eventsByStage[event.programStage][i].event === event.event) {
                        index = i;
                    }
                }                
                if(index !== -1){
                    $scope.currentEvent = $scope.eventsByStage[event.programStage][index];
                }
                
                $scope.showDataEntryDiv = true;
                $scope.showEventCreationDiv = false;

                if ($scope.currentEvent.notes) {
                    angular.forEach($scope.currentEvent.notes, function (note) {
                        note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                    });

                    if ($scope.currentEvent.notes.length > 0) {
                        $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');
                    }
                }

                $scope.getDataEntryForm();
            }
        }
    };
    
    $scope.tableRowIsEditable = function(eventRow){
        if( eventRow === $scope.currentEvent &&
            eventRow.status !== $scope.EVENTSTATUSCOMPLETELABEL &&
            eventRow.status !== $scope.EVENTSTATUSSKIPPEDLABEL &&
            $scope.tableEditMode !== $scope.tableEditModes.form){
            
            return true;
        }
        return false;
    };
    
    $scope.tableRowStatusButtonsEnabled = function(event){
        return event.orgUnit === $scope.selectedOrgUnit.id && $scope.selectedEntity.inactive === false && $scope.selectedEnrollment.status === 'ACTIVE';
    };
    
    $scope.tableEditModes = { form: 0, table: 1, tableAndForm: 2 };
    $scope.tableEditMode = $scope.tableEditModes.form;
    
    $scope.toggleTableEditMode = function(){
        $scope.currentEvent = {};
        $scope.tableEditMode = $scope.tableEditMode === $scope.tableEditModes.tableAndForm ? $scope.tableEditModes.form : $scope.tableEditModes.tableAndForm;
    };
    
    $scope.eventRowChanged = false;
    $scope.eventRowClicked = function(event){        
        
        if($scope.currentEvent !== event){
            $scope.eventRowChanged = true;
        }
        else {
            $scope.eventRowChanged = false;
        }
        
        $scope.switchToEventRow(event); 
        if($scope.tableEditMode === $scope.tableEditModes.form){
            $scope.openEventEditFormModal(event);
        }
        
    };
    
    $scope.eventRowDblClicked = function(event){
                
        if($scope.currentEvent === event &&
           $scope.tableEditMode === $scope.tableEditModes.tableAndForm &&
           $scope.eventRowChanged === false){           
            $scope.openEventEditFormModal(event);
        }
    };
        
    $scope.openEventEditFormModal = function(event){
       
        //setEventEditing        
        setEventEditing(event, $scope.currentStage);                
        
        $scope.eventEditFormModalInstance = modalInstance = $modal.open({
            templateUrl: 'components/dataentry/modal-default-form.html',
            scope: $scope           
        });

        $scope.eventEditFormModalInstance.result.then(function (status) {                        
            //completed, deleted or skipped
            $scope.currentEvent = {};            
        }, function(){
            //closed            
        });
    };
    
    $scope.switchToEventRowDeselected = function(event){
        if($scope.currentEvent !== event) {
            $scope.showDataEntry(event,false);
        }
        $scope.currentEvent = {};
    };
    
    $scope.switchToEventRow = function (event) {
        if($scope.currentEvent !== event) {
            $scope.showDataEntry(event,false);
        }
    };

    $scope.switchDataEntryForm = function () {
        $scope.displayCustomForm = !$scope.displayCustomForm;
    };
    
    $scope.buildOtherValuesLists = function () {
        var otherValues = {};
        //Only default forms need to build an other values list.
        if($scope.displayCustomForm === "DEFAULT" || $scope.displayCustomForm === false) {
            //Build a list of datavalues OUTSIDE the current event. 
            angular.forEach($scope.currentStage.programStageDataElements, function(programStageDataElement) {
                angular.forEach($scope.programStages, function(stage) {
                    for(var i = 0; i < $scope.eventsByStageAsc[stage.id].length; i++) {
                        //We are not interested in the values from the current stage:
                        if($scope.eventsByStageAsc[stage.id][i] !== $scope.currentEvent) {
                            var currentId = programStageDataElement.dataElement.id;
                            if ( $scope.eventsByStageAsc[stage.id][i][currentId] ) {
                                //The current stage has a dataelement of the type in question

                                //Init the list if not already done:
                                if(!otherValues[currentId]) {
                                    otherValues[currentId] = [];
                                }
                                
                                //Decorate and push the alternate value to the list:
                                
                                var alternateValue = $scope.eventsByStageAsc[stage.id][i][currentId];                                
                                alternateValue = CommonUtils.displayBooleanAsYesNo(alternateValue, programStageDataElement.dataElement);
                                
                                //Else decorate with the event date:
                                alternateValue = $scope.eventsByStageAsc[stage.id][i].eventDate + ': ' + alternateValue;
                                
                                otherValues[currentId].push(alternateValue);
                            }
                        }
                    }
                });
            });
        }
        
        return otherValues;
    };
    
    $scope.getDataEntryForm = function () {
        $scope.currentStage = $scope.stagesById[$scope.currentEvent.programStage];
        $scope.currentStageEvents = $scope.eventsByStage[$scope.currentEvent.programStage];
        if(!$scope.currentStage.multiSelectGroups) {
            $scope.currentStage.multiSelectGroups = {};
        }

        for(var i = $scope.currentStage.programStageDataElements.length-1;i >=0;i--) {
            var s = $scope.currentStage.programStageDataElements[i].dataElement;
            
            //If the datatype is a boolean, and there is a list of dataElementGropus, put the boolean into the group:
            var s = $scope.currentStage.programStageDataElements[i].dataElement;
            if($scope.currentStage.programStageDataElements[i].dataElement.dataElementGroups
                    && $scope.currentStage.programStageDataElements[i].dataElement.valueType === "TRUE_ONLY") {
                angular.forEach($scope.currentStage.programStageDataElements[i].dataElement.dataElementGroups, function(dataElementGroup) {
                    //if the element it grouped, we only add a prStDe for the group element:
                    if( !$scope.currentStage.multiSelectGroups[dataElementGroup.id] ) {
                        $scope.currentStage.multiSelectGroups[dataElementGroup.id] = 
                            {dataElement:{valueType:'MULTI_SELECT_GROUP',name:dataElementGroup.name,id:dataElementGroup.id},
                             dataElements: []};
                        $scope.currentStage.programStageDataElements.push($scope.currentStage.multiSelectGroups[dataElementGroup.id]);
                    }
                    
                    $scope.currentStage.multiSelectGroups[dataElementGroup.id].dataElements.push($scope.currentStage.programStageDataElements[i]);
                    $scope.currentStage.programStageDataElements.splice(i,1);
                });
            }
        }
        

        angular.forEach($scope.currentStage.programStageSections, function (section) {
            section.open = true;
        });

        $scope.customForm = CustomFormService.getForProgramStage($scope.currentStage, $scope.prStDes);
        $scope.displayCustomForm = "DEFAULT";
        if ($scope.customForm) {
            $scope.displayCustomForm = "CUSTOM";
        }
        else if ($scope.currentStage.displayEventsInTable) {
            $scope.displayCustomForm = "TABLE";
        }

        $scope.currentEventOriginal = angular.copy($scope.currentEvent);

        $scope.currentStageEventsOriginal = angular.copy($scope.currentStageEvents);

        var period = {event: $scope.currentEvent.event, stage: $scope.currentEvent.programStage, name: $scope.currentEvent.sortingDate};
        $scope.currentPeriod[$scope.currentEvent.programStage] = period;        
        
        $scope.setDisplayTypeForStage($scope.currentStage);
        //Execute rules for the first time, to make the initial page appear correctly.
        //Subsequent calls will be made from the "saveDataValue" function.
        $scope.executeRules();
    };

    $scope.saveDatavalue = function (prStDe, field) {        
        $scope.saveDatavalueForEvent(prStDe, field, $scope.currentEvent);
    };
    
    $scope.saveDataValueForRadio = function(prStDe, event, value){
        
        var def = $q.defer();
        
        event[prStDe.dataElement.id] = value;
        
        var saveReturn = $scope.saveDatavalueForEvent(prStDe, null, event);
        if(angular.isDefined(saveReturn)){
            if(saveReturn === true){
                def.resolve("saved");                
            }
            else if(saveReturn === false){
                def.reject("error");
            }
            else{
                saveReturn.then(function(){
                    def.resolve("saved");
                }, function(){
                    def.reject("error");
                });
            }
        }
        else {
            def.resolve("notSaved");
        }

        
        return def.promise;
    };
    
    $scope.saveMultiSelectState = function (prStDe, eventToSave, currentElement, value) {
        //Called when a new option is added or removed from a multiselect list
        $scope.updateSuccess = false;

        $scope.currentElement = {id: currentElement.dataElement.id, event: eventToSave.event, saved: false};
        
        value = CommonUtils.formatDataValue(value, prStDe.dataElement, $scope.optionSets, 'API');
        var dataValue = {
            dataElement: prStDe.dataElement.id,
            value: value,
            providedElsewhere: false
        };
        
        var ev = {event: eventToSave.event,
            orgUnit: eventToSave.orgUnit,
            program: eventToSave.program,
            programStage: eventToSave.programStage,
            status: eventToSave.status,
            trackedEntityInstance: eventToSave.trackedEntityInstance,
            dataValues: [
                dataValue
            ]
        };
        DHIS2EventFactory.updateForSingleValue(ev).then(function (response) {

            $scope.currentElement.saved = true;
            if(value){
                $scope.currentEvent[dataValue.dataElement] = dataValue;                
            }else{
                $scope.currentEvent[dataValue.dataElement] = null;
            }
            $scope.currentEventOriginal = angular.copy($scope.currentEvent);
            $scope.currentStageEventsOriginal = angular.copy($scope.currentStageEvents);
            //Run rules on updated data:
            $scope.executeRules();
        });
    };
    
    $scope.initMultiSelect = function (eventRow) {
        if(!eventRow.multiSelectGroupSelection) {
            eventRow.multiSelectGroupSelection = {selections:[]};
        }
    };
    
    $scope.degub = function(item) {
        var i = item;
    };
    
    $scope.saveDatavalueForEvent = function (prStDe, field, eventToSave) {
        //Blank out the input-saved class on the last saved due date:
        $scope.eventDateSaved = false;
        $scope.currentElement = {};
        
        //check for input validity
        $scope.updateSuccess = false;
        if (field && field.$invalid) {
            $scope.currentElement = {id: prStDe.dataElement.id, saved: false};
            return false;
        }

        //input is valid
        var value = eventToSave[prStDe.dataElement.id];

        var oldValue = null;
        angular.forEach($scope.currentStageEventsOriginal, function (eventOriginal) {
            if (eventOriginal.event === eventToSave.event) {
                oldValue = eventOriginal[prStDe.dataElement.id];
            }
        });

        if (oldValue !== value) {
            
            value = CommonUtils.formatDataValue(value, prStDe.dataElement, $scope.optionSets, 'API');
            
            $scope.updateSuccess = false;

            $scope.currentElement = {id: prStDe.dataElement.id, event: eventToSave.event, saved: false};            

            var ev = {event: eventToSave.event,
                orgUnit: eventToSave.orgUnit,
                program: eventToSave.program,
                programStage: eventToSave.programStage,
                status: eventToSave.status,
                trackedEntityInstance: eventToSave.trackedEntityInstance,
                dataValues: [
                    {
                        dataElement: prStDe.dataElement.id,
                        value: value,
                        providedElsewhere: eventToSave.providedElsewhere[prStDe.dataElement.id] ? true : false
                    }
                ]
            };
            return DHIS2EventFactory.updateForSingleValue(ev).then(function (response) {

                $scope.currentElement.saved = true;

                $scope.currentEventOriginal = angular.copy($scope.currentEvent);

                $scope.currentStageEventsOriginal = angular.copy($scope.currentStageEvents);

                //Run rules on updated data:
                $scope.executeRules();                
            });

        }
    };

    $scope.saveDatavalueLocation = function (prStDe) {

        $scope.updateSuccess = false;

        if (!angular.isUndefined($scope.currentEvent.providedElsewhere[prStDe.dataElement.id])) {

            //currentEvent.providedElsewhere[prStDe.dataElement.id];
            var value = $scope.currentEvent[prStDe.dataElement.id];
            var ev = {event: $scope.currentEvent.event,
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
            DHIS2EventFactory.updateForSingleValue(ev).then(function (response) {
                $scope.updateSuccess = true;
            });
        }
    };

    $scope.saveEventDate = function () {
        $scope.saveEventDateForEvent($scope.currentEvent);
    };

    $scope.saveEventDateForEvent = function (eventToSave) {
        $scope.eventDateSaved = false;
        if (angular.isUndefined(eventToSave.eventDate) || eventToSave.eventDate === '') {
            $scope.invalidDate = eventToSave.event;
            $scope.validatedDateSetForEvent = {date: '', event: eventToSave};
            $scope.currentElement = {id: "eventDate", saved: false};
            return false;
        }

        var rawDate = angular.copy(eventToSave.eventDate);
        var convertedDate = DateUtils.format(eventToSave.eventDate);

        if (rawDate !== convertedDate) {
            $scope.invalidDate = eventToSave.event;
            $scope.validatedDateSetForEvent = {date: '', event: eventToSave};
            $scope.currentElement = {id: "eventDate", saved: false};
            return false;
        }
        
        $scope.currentElement = {id: "eventDate", event: eventToSave.event, saved: false};
        
        var e = {event: eventToSave.event,
            enrollment: eventToSave.enrollment,
            dueDate: DateUtils.formatFromUserToApi(eventToSave.dueDate),
            status: eventToSave.status === 'SCHEDULE' ? 'ACTIVE' : eventToSave.status,
            program: eventToSave.program,
            programStage: eventToSave.programStage,
            orgUnit: eventToSave.dataValues && eventToSave.dataValues.length > 0 ? eventToSave.orgUnit : $scope.selectedOrgUnit.id,
            eventDate: DateUtils.formatFromUserToApi(eventToSave.eventDate),
            trackedEntityInstance: eventToSave.trackedEntityInstance
        };
        
        DHIS2EventFactory.updateForEventDate(e).then(function (data) {
            eventToSave.sortingDate = eventToSave.eventDate;
            $scope.invalidDate = false;
            $scope.eventDateSaved = eventToSave.event;
            eventToSave.statusColor = EventUtils.getEventStatusColor(eventToSave); 
            sortEventsByStage('UPDATE');
            $scope.validatedDateSetForEvent = {date: eventToSave.eventDate, event: eventToSave};
            $scope.currentElement = {id: "eventDate", event: eventToSave.event, saved: true};
            $scope.executeRules();
        }, function(error){
            
        });
    };

    $scope.saveDueDate = function () {

        $scope.dueDateSaved = false;

        if ($scope.currentEvent.dueDate === '') {
            $scope.invalidDueDate = $scope.currentEvent.event;
            return false;
        }

        var rawDate = angular.copy($scope.currentEvent.dueDate);
        var convertedDate = DateUtils.format($scope.currentEvent.dueDate);

        if (rawDate !== convertedDate) {
            $scope.invalidDueDate = $scope.currentEvent.event;
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

        if ($scope.currentStage.periodType) {
            e.eventDate = e.dueDate;
        }

        if ($scope.currentEvent.coordinate) {
            e.coordinate = $scope.currentEvent.coordinate;
        }

        DHIS2EventFactory.update(e).then(function (data) {
            $scope.invalidDueDate = false;
            $scope.dueDateSaved = true;

            if (e.eventDate && !$scope.currentEvent.eventDate && $scope.currentStage.periodType) {
                $scope.currentEvent.eventDate = $scope.currentEvent.dueDate;
            }
            
            $scope.currentEvent.sortingDate = $scope.currentEvent.dueDate;
            $scope.currentEvent.statusColor = EventUtils.getEventStatusColor($scope.currentEvent);
            $scope.schedulingEnabled = !$scope.schedulingEnabled;
            sortEventsByStage('UPDATE');
        });

    };

    $scope.saveCoordinate = function (type) {

        if (type === 'LAT' || type === 'LATLNG') {
            $scope.latitudeSaved = false;
        }
        if (type === 'LAT' || type === 'LATLNG') {
            $scope.longitudeSaved = false;
        }

        if ((type === 'LAT' || type === 'LATLNG') && $scope.outerForm.latitude.$invalid ||
                (type === 'LNG' || type === 'LATLNG') && $scope.outerForm.longitude.$invalid) {//invalid coordinate            
            return;
        }

        if ((type === 'LAT' || type === 'LATLNG') && $scope.currentEvent.coordinate.latitude === $scope.currentEventOriginal.coordinate.latitude ||
                (type === 'LNG' || type === 'LATLNG') && $scope.currentEvent.coordinate.longitude === $scope.currentEventOriginal.coordinate.longitude) {//no change            
            return;
        }

        //valid coordinate(s), proceed with the saving
        var dhis2Event = $scope.makeDhis2EventToUpdate();

        DHIS2EventFactory.update(dhis2Event).then(function (response) {
            $scope.currentEventOriginal = angular.copy($scope.currentEvent);
            $scope.currentStageEventsOriginal = angular.copy($scope.currentStageEvents);

            if (type === 'LAT' || type === 'LATLNG') {
                $scope.latitudeSaved = true;
            }
            if (type === 'LAT' || type === 'LATLNG') {
                $scope.longitudeSaved = true;
            }
        });
    };

    $scope.addNote = function () {

        if ($scope.note.value !== "" || !angular.isUndefined($scope.note.value)) {
            var newNote = {value: $scope.note.value};
            
            if (angular.isUndefined($scope.currentEvent.notes)) {
                $scope.currentEvent.notes = [{value: newNote.value, storedDate: today, storedBy: storedBy}];
            }
            else {
                $scope.currentEvent.notes.splice(0, 0, {value: newNote.value, storedDate: today, storedBy: storedBy});
            }

            var e = {event: $scope.currentEvent.event,
                program: $scope.currentEvent.program,
                programStage: $scope.currentEvent.programStage,
                orgUnit: $scope.currentEvent.orgUnit,
                trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                notes: [newNote]
            };

            DHIS2EventFactory.updateForNote(e).then(function (data) {

                $scope.note = {};
            });
        }
    };
    
    $scope.notesModal = function(){
                
        var bodyList = [];

        if($scope.currentEvent.notes) {
            for(i = 0; i < $scope.currentEvent.notes.length; i++){
                var currentNote = $scope.currentEvent.notes[i];            
                bodyList.push({value1: currentNote.storedDate, value2: currentNote.value});
            }
        }
        
        var dialogOptions = {
            closeButtonText: 'Close',            
            textAreaButtonText: 'Add',
            textAreaButtonShow: $scope.currentEvent.status === $scope.EVENTSTATUSSKIPPEDLABEL ? false : true,
            headerText: 'Notes',
            bodyTextAreas: [{model: 'note', placeholder: 'Add another note here', required: true, show: $scope.currentEvent.status === $scope.EVENTSTATUSSKIPPEDLABEL ? false : true}],
            bodyList: bodyList,
            currentEvent: $scope.currentEvent
        };        
        
        var dialogDefaults = {
            
            templateUrl: 'views/list-with-textarea-modal.html',
            controller: function ($scope, $modalInstance, DHIS2EventFactory, DateUtils) {                    
                    $scope.modalOptions = dialogOptions;
                    $scope.formSubmitted = false;
                    $scope.currentEvent = $scope.modalOptions.currentEvent;
                    $scope.textAreaValues = [];
                    
                    $scope.textAreaButtonClick = function(){                                               
                        if($scope.textAreaModalForm.$valid){                                
                            $scope.note = $scope.textAreaValues["note"];
                            $scope.addNote();
                            $scope.textAreaModalForm.$setUntouched();
                            $scope.formSubmitted = false;                            
                        }
                        else {
                            $scope.formSubmitted = true;
                        }
                    };                   

                                        
                    $scope.modalOptions.close = function(){                        
                        $modalInstance.close($scope.currentEvent);
                    };
                    
                    $scope.addNote = function(){
                                                
                        newNote = {value: $scope.note};
                        var date = DateUtils.formatToHrsMins(new Date());

                        var e = {event: $scope.currentEvent.event,
                                program: $scope.currentEvent.program,
                                programStage: $scope.currentEvent.programStage,
                                orgUnit: $scope.currentEvent.orgUnit,
                                trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                                notes: [newNote]
                        };
                        DHIS2EventFactory.updateForNote(e).then(function (data) {
                            if (angular.isUndefined($scope.modalOptions.bodyList) || $scope.modalOptions.bodyList.length === 0) {
                                $scope.modalOptions.bodyList = [{value1: date, value2: newNote.value}];
                                $scope.modalOptions.currentEvent.notes = [{storedDate: date, value: newNote.value}];
                            }
                            else {
                                $scope.modalOptions.bodyList.splice(0, 0, {value1: date, value2: newNote.value});
                                $scope.modalOptions.currentEvent.notes.splice(0,0,{storedDate: date, value: newNote.value});
                            }
                                $scope.note = $scope.textAreaValues["note"] = "";
                            });

                    };                    
                }            
        };
        
        DialogService.showDialog(dialogDefaults, dialogOptions).then(function(e){

            $scope.currentEvent.notes = e.notes;
        });
        
    };

    $scope.clearNote = function () {
        $scope.note = {};
    };

    $scope.getInputDueDateClass = function (event) {
        if (event.event === $scope.eventDateSaved) {
            return 'input-success';
        }
        else {
            return '';
        }

    };

    $scope.getInputNotifcationClass = function(id, custom, event){
        if(!event) {
            event = $scope.currentEvent;
        }
        if($scope.currentElement.id && $scope.currentElement.id === id && $scope.currentElement.event && $scope.currentElement.event === event.event){
            if($scope.currentElement.saved){
                if(custom){
                    return 'input-success';
                }
                return 'form-control input-success';
            }            
            else{
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

    //Infinite Scroll
    $scope.infiniteScroll = {};
    $scope.infiniteScroll.optionsToAdd = 20;
    $scope.infiniteScroll.currentOptions = 20;

    $scope.resetInfScroll = function () {
        $scope.infiniteScroll.currentOptions = $scope.infiniteScroll.optionsToAdd;
    };

    $scope.addMoreOptions = function () {
        $scope.infiniteScroll.currentOptions += $scope.infiniteScroll.optionsToAdd;
    };

    var completeEnrollment = function () {
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'complete',
            headerText: 'complete_enrollment',
            bodyText: 'would_you_like_to_complete_enrollment'
        };

        ModalService.showModal({}, modalOptions).then(function (result) {
            EnrollmentService.completeIncomplete($scope.selectedEnrollment, 'completed').then(function (data) {
                $scope.selectedEnrollment.status = 'COMPLETED';
            });
        });
    };
    
    $scope.makeDhis2EventToUpdate = function(){
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);
        var dhis2EventToUpdate = angular.copy(dhis2Event);
        dhis2EventToUpdate.dataValues = [];
        
        if(dhis2Event.dataValues){
            angular.forEach(dhis2Event.dataValues, function(dataValue){
                if(dataValue.value && dataValue.value.selections){
                    angular.forEach(dataValue.value.selections, function(selection){
                        var dv = {
                            dataElement: selection.id,
                            value: true,
                            providedElsewhere: false
                        };
                        dhis2EventToUpdate.dataValues.push(dv);
                    });
                }else{
                    dhis2EventToUpdate.dataValues.push(dataValue);
                }
            });
        };
        return dhis2EventToUpdate;
    };
    
    $scope.completeIncompleteEvent = function (inTableView) {
            
        var modalOptions;
        var dhis2Event = $scope.makeDhis2EventToUpdate();        
        
        if ($scope.currentEvent.status === 'COMPLETED') {//activiate event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'edit',
                headerText: 'edit',
                bodyText: 'are_you_sure_to_incomplete_event'
            };
            dhis2Event.status = 'ACTIVE';
        }
        else {//complete event
                if(angular.isUndefined(inTableView) || inTableView === false){
                $scope.outerForm.$setSubmitted();
                if($scope.outerForm.$invalid){
                    return;
                }
            }
            
            if(angular.isDefined($scope.errorMessages[$scope.currentEvent.event]) && $scope.errorMessages[$scope.currentEvent.event].length > 0) {
                //There is unresolved program rule errors - show error message.
                var dialogOptions = {
                    headerText: 'errors',
                    bodyText: 'please_fix_errors_before_completing',
                    bodyList: $scope.errorMessages[$scope.currentEvent.event]
                };                
                
                DialogService.showDialog({}, dialogOptions);
                
                return;
            }
            else
            {
                modalOptions = {
                    closeButtonText: 'cancel',
                    actionButtonText: 'complete',
                    headerText: 'complete',
                    bodyText: 'are_you_sure_to_complete_event'
                };
                dhis2Event.status = 'COMPLETED';
            }
        }        
        
        ModalService.showModal({}, modalOptions).then(function (result) {            
            $scope.executeCompleteIncompleteEvent(dhis2Event);
        });        
    };
    
    $scope.executeCompleteIncompleteEvent = function(dhis2Event){
            
        DHIS2EventFactory.update(dhis2Event).then(function (data) {

            if ($scope.currentEvent.status === 'COMPLETED') {//activiate event                    
                $scope.currentEvent.status = 'ACTIVE';
            }
            else {//complete event                    
                $scope.currentEvent.status = 'COMPLETED';

            }

            setStatusColor();

            setEventEditing($scope.currentEvent, $scope.currentStage);

            if ($scope.currentEvent.status === 'COMPLETED') {

                if ($scope.currentStage.remindCompleted) {
                    completeEnrollment($scope.currentStage);
                }
                else {
                    if ($scope.currentStage.allowGenerateNextVisit) {
                        if($scope.currentStage.repeatable){
                            $scope.showCreateEvent($scope.currentStage, $scope.eventCreationActions.add);
                        }
                        else{
                            var index = -1, stage = null;
                            for(var i=0; i<$scope.programStages.length && index===-1; i++){
                                if($scope.currentStage.id === $scope.programStages[i].id){
                                    index = i;
                                    stage = $scope.programStages[i+1];
                                }
                            }
                            if(stage ){
                                if(!$scope.eventsByStage[stage.id] || $scope.eventsByStage[stage.id] && $scope.eventsByStage[stage.id].length === 0){
                                    $scope.showCreateEvent(stage, $scope.eventCreationActions.add);
                                }
                            }                                
                        }
                    }
                }

                if($scope.displayCustomForm !== "TABLE") {
                    //Close the event when the event is completed, to make it 
                    //more clear that the completion went through.
                    $scope.showDataEntry($scope.currentEvent, false);
                }
            }
        });
    };
    

    $scope.skipUnskipEvent = function () {
        var modalOptions;
        var dhis2Event = $scope.makeDhis2EventToUpdate();

        if ($scope.currentEvent.status === 'SKIPPED') {//unskip event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'unskip',
                headerText: 'unskip',
                bodyText: 'are_you_sure_to_unskip_event'
            };
            dhis2Event.status = 'ACTIVE';
        }
        else {//skip event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'skip',
                headerText: 'skip',
                bodyText: 'are_you_sure_to_skip_event'
            };
            dhis2Event.status = 'SKIPPED';
        }

        ModalService.showModal({}, modalOptions).then(function (result) {
            
            $scope.executeSkipUnskipEvent(dhis2Event);
            
        });
    };
    
    $scope.executeSkipUnskipEvent = function(dhis2Event){
        
        return DHIS2EventFactory.update(dhis2Event).then(function (data) {

                if ($scope.currentEvent.status === 'SKIPPED') {//activiate event                    
                    $scope.currentEvent.status = 'SCHEDULE';
                }
                else {//complete event                    
                    $scope.currentEvent.status = 'SKIPPED';
                }

                setStatusColor();
                setEventEditing($scope.currentEvent, $scope.currentStage);
            });
        
    };
    

    var setStatusColor = function () {
        var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);
        var continueLoop = true;
        for (var i = 0; i < $scope.eventsByStage[$scope.currentEvent.programStage].length && continueLoop; i++) {
            if ($scope.eventsByStage[$scope.currentEvent.programStage][i].event === $scope.currentEvent.event) {
                $scope.eventsByStage[$scope.currentEvent.programStage][i].statusColor = statusColor;
                $scope.currentEvent.statusColor = statusColor;
                continueLoop = false;
            }
        }
    };

    $scope.deleteEvent = function () {
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'delete',
            headerText: 'delete',
            bodyText: 'are_you_sure_to_delete_event'
        };

        ModalService.showModal({}, modalOptions).then(function (result) {
            $scope.executeDeleteEvent();
        });
    };
    
    $scope.executeDeleteEvent = function(){
        
        return DHIS2EventFactory.delete($scope.currentEvent).then(function (data) {

                var continueLoop = true, index = -1;
                for (var i = 0; i < $scope.eventsByStage[$scope.currentEvent.programStage].length && continueLoop; i++) {
                    if ($scope.eventsByStage[$scope.currentEvent.programStage][i].event === $scope.currentEvent.event) {
                        $scope.eventsByStage[$scope.currentEvent.programStage][i] = $scope.currentEvent;
                        continueLoop = false;
                        index = i;
                    }
                }
                               
                var programStageID = $scope.currentEvent.programStage;
                
                if($scope.displayCustomForm === "TABLE"){
                    $scope.currentEvent = {};                
                }
                else {
                    $scope.currentEvent = null;
                }

                $scope.eventsByStage[programStageID].splice(index, 1);
                $scope.currentStageEvents = $scope.eventsByStage[programStageID];
                sortEventsByStage('REMOVE');                                
            }, function(error){               
                return $q.reject(error);
            });        
    };

    $scope.toggleLegend = function () {
        $scope.showEventColors = !$scope.showEventColors;
    };

    $scope.getEventStyle = function (ev) {
        var style = EventUtils.getEventStatusColor(ev);

        if ($scope.currentEvent && $scope.currentEvent.event === ev.event) {
            style = style + ' ' + ' current-stage';
        }
        return style;
    };

    $scope.getColumnWidth = function (weight) {
        var width = weight <= 1 ? 1 : weight;
        width = (width / $scope.totalEvents) * 100;
        return "width: " + width + '%';
    };

    $scope.sortEventsByDate = function (dhis2Event) {
        var d = dhis2Event.sortingDate;
        return DateUtils.getDate(d);
    };

    var sortEventsByStage = function (operation, newEvent) {

        $scope.eventFilteringRequired = false;

        for (var key in $scope.eventsByStage) {

            var stage = $scope.stagesById[key];

            if ($scope.eventsByStage.hasOwnProperty(key) && stage) {

                var sortedEvents = $filter('orderBy')($scope.eventsByStage[key], function (event) {
                    return DateUtils.getDate(event.sortingDate);
                }, true);

                $scope.eventsByStage[key] = sortedEvents;
                $scope.eventsByStageAsc[key] = [];
                //Reverse the order of events, but keep the objects within the array.
                //angular.copy and reverse did not work - this messed up databinding.
                angular.forEach(sortedEvents, function(sortedEvent) {
                    $scope.eventsByStageAsc[key].splice(0,0,sortedEvent);
                });

                var periods = PeriodService.getPeriods(sortedEvents, stage, $scope.selectedEnrollment).occupiedPeriods;

                $scope.eventPeriods[key] = periods;
                $scope.currentPeriod[key] = periods.length > 0 ? periods[0] : null;
                $scope.eventFilteringRequired = $scope.eventFilteringRequired ? $scope.eventFilteringRequired : periods.length > 1;
            }
        }
        
        $scope.allEventsSorted = CurrentSelection.getSelectedTeiEvents();
        
        if (operation) {
            if (operation === 'ADD') {
                var ev = EventUtils.reconstruct(newEvent, $scope.currentStage, $scope.optionSets);                
                ev.enrollment = newEvent.enrollment;
                ev.visited = newEvent.visited;
                $scope.allEventsSorted.push(ev);
            }
            if (operation === 'UPDATE') {
                var ev = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);
                ev.enrollment = $scope.currentEvent.enrollment;
                ev.visited = $scope.currentEvent.visited;
                var index = -1;
                for (var i = 0; i < $scope.allEventsSorted.length && index === -1; i++) {
                    if ($scope.allEventsSorted[i].event === $scope.currentEvent.event) {
                        index = i;
                    }
                }
                if (index !== -1) {
                    $scope.allEventsSorted[index] = ev;
                }
            }
            if (operation === 'REMOVE') {
                var index = -1;
                for (var i = 0; i < $scope.allEventsSorted.length && index === -1; i++) {
                    if ($scope.allEventsSorted[i].event === $scope.currentEvent.event) {
                        index = i;
                    }
                }
                if (index !== -1) {
                    $scope.allEventsSorted.splice(index, 1);
                }
            }

            CurrentSelection.setSelectedTeiEvents($scope.allEventsSorted);

            $timeout(function () {
                $rootScope.$broadcast('tei-report-widget', {});
            }, 200);
        }
        
        $scope.allEventsSorted = orderByFilter($scope.allEventsSorted, '-sortingDate').reverse();
    };

    $scope.showLastEventInStage = function (stageId) {
        var ev = $scope.eventsByStage[stageId][$scope.eventsByStage[stageId].length - 1];
        $scope.showDataEntryForEvent(ev);
    };

    $scope.showDataEntryForEvent = function (event) {

        var period = {event: event.event, stage: event.programStage, name: event.sortingDate};
        $scope.currentPeriod[event.programStage] = period;

        var event = null;
        for (var i = 0; i < $scope.eventsByStage[period.stage].length; i++) {
            if ($scope.eventsByStage[period.stage][i].event === period.event) {
                event = $scope.eventsByStage[period.stage][i];
                break;
            }
        }

        if (event) {
            $scope.showDataEntry(event, false);
        }
        
    };

    $scope.showMap = function (event) {
        var modalInstance = $modal.open({
            templateUrl: '../dhis-web-commons/angular-forms/map.html',
            controller: 'MapController',
            windowClass: 'modal-full-window',
            resolve: {
                location: function () {
                    return {lat: event.coordinate.latitude, lng: event.coordinate.longitude};
                }
            }
        });

        modalInstance.result.then(function (location) {
            if (angular.isObject(location)) {
                event.coordinate.latitude = location.lat;
                event.coordinate.longitude = location.lng;
                $scope.saveCoordinate('LATLNG');
            }
        }, function () {
        });
    };

    $scope.interacted = function (field, form) {        
        
        var status = false;
        if (field) {
            if(angular.isDefined(form)){
                status = form.$submitted || field.$dirty;
            }
            else {
                status = $scope.outerForm.$submitted || field.$dirty;
            }            
        }
        return status;
    };
    
    $scope.deselectCurrent = function(id){        
        
        if($scope.currentEvent.event === id){
            $scope.currentEvent = {};
        }        
    };
    
    $scope.showCompleteErrorMessageInModal = function(message, fieldName, isWarning){
        var messages = [message];
        var headerPrefix = angular.isDefined(isWarning) && isWarning === true ? "Warning in " : "Error in ";
        
        var dialogOptions = {
                    headerText: headerPrefix + fieldName,
                    bodyText: '',
                    bodyList: messages
                };                
                
                DialogService.showDialog({}, dialogOptions);        
    };
})

.controller('EventCreationController',
        function ($scope,
                $modalInstance,
                $timeout,
                DateUtils,
                DHIS2EventFactory,
                OrgUnitFactory,
                DialogService,
                EventCreationService,
                stage,
                dummyEvent,
                eventCreationAction,
                autoCreate) {
    $scope.eventCreationAction = eventCreationAction;
    $scope.eventCreationActions = EventCreationService.eventCreationActions;
    
    $scope.selectedStage = stage;
    $scope.isNewEvent = (eventCreationAction === $scope.eventCreationActions.add);
    $scope.isScheduleEvent = (eventCreationAction === $scope.eventCreationActions.schedule || eventCreationAction === $scope.eventCreationActions.referral);
    $scope.isReferralEvent = (eventCreationAction === $scope.eventCreationActions.referral);
    
    $scope.dhis2Event = {eventDate: $scope.isScheduleEvent ? '' : DateUtils.getToday(), dueDate: dummyEvent.dueDate, excecutionDateLabel : dummyEvent.excecutionDateLabel, name: dummyEvent.name, invalid: true};
    
    ////custom code for folkehelsa. Set empty eventDate if selectedStage is previous pregnancies
    if($scope.selectedStage.id === 'PUZaKR0Jh2k'){
        $scope.dhis2Event.eventDate = '';
    }
    
    if ($scope.selectedStage.periodType) {
        $scope.dhis2Event.eventDate = dummyEvent.dueDate;
        $scope.dhis2Event.periodName = dummyEvent.periodName;
        $scope.dhis2Event.periods = dummyEvent.periods;
        $scope.dhis2Event.selectedPeriod = dummyEvent.periods[0];
    }

    $scope.dueDateInvalid = false;
    $scope.eventDateInvalid = false;


    //watch for changes in due/event-date
    $scope.$watchCollection('[dhis2Event.dueDate, dhis2Event.eventDate]', function () {
        if (angular.isObject($scope.dhis2Event)) {
            if (!$scope.dhis2Event.dueDate) {
                $scope.dueDateInvalid = true;
                return;
            }

            if ($scope.dhis2Event.dueDate) {
                var rDueDate = $scope.dhis2Event.dueDate;
                var cDueDate = DateUtils.format($scope.dhis2Event.dueDate);
                $scope.dueDateInvalid = rDueDate !== cDueDate;
            }

            if ($scope.dhis2Event.eventDate) {
                var rEventDate = $scope.dhis2Event.eventDate;
                var cEventDate = DateUtils.format($scope.dhis2Event.eventDate);
                $scope.eventDateInvalid = rEventDate !== cEventDate;
            }
        }
    });

    $scope.save = function () {
        //check for form validity
        if ($scope.dueDateInvalid || $scope.eventDateInvalid) {
            return false;
        }
        if($scope.isReferralEvent && !$scope.selectedSearchingOrgUnit){
            $scope.orgUnitError = true;
            return false;
        }
        $scope.orgUnitError =  false;
        
        if ($scope.selectedStage.periodType) {
            $scope.dhis2Event.eventDate = $scope.dhis2Event.selectedPeriod.endDate;
            $scope.dhis2Event.dueDate = $scope.dhis2Event.selectedPeriod.endDate;
        }
        
        var eventDate = DateUtils.formatFromUserToApi($scope.dhis2Event.eventDate);
        var dueDate = DateUtils.formatFromUserToApi($scope.dhis2Event.dueDate);
        var newEvents = {events: []};
        var newEvent = {
            trackedEntityInstance: dummyEvent.trackedEntityInstance,
            program: dummyEvent.program,
            programStage: dummyEvent.programStage,
            enrollment: dummyEvent.enrollment,
            orgUnit: dummyEvent.orgUnit,
            dueDate: dueDate,
            eventDate: eventDate,
            notes: [],
            dataValues: [],
            status: 'ACTIVE'
        };

        newEvent.status = newEvent.eventDate ? 'ACTIVE' : 'SCHEDULE';

        newEvents.events.push(newEvent);
        DHIS2EventFactory.create(newEvents).then(function (response) {
            if (response.response && response.response.importSummaries[0].status === 'SUCCESS') {
                newEvent.event = response.response.importSummaries[0].reference;
                $modalInstance.close(newEvent);
            }
            else {
                var dialogOptions = {
                    headerText: 'event_creation_error',
                    bodyText: response.message
                };

                DialogService.showDialog({}, dialogOptions);
            }
        });
    };

    //Start referral logic
    $scope.setSelectedSearchingOrgUnit = function(orgUnit){
        $scope.selectedSearchingOrgUnit = orgUnit;
        dummyEvent.orgUnit = orgUnit.id;
        dummyEvent.orgUnitName = orgUnit.name;
    };
    
    var orgPath = [dummyEvent.orgUnit];
    function initOrgUnits(uid){
        $scope.orgUnitsLoading =true;
        $timeout(function(){
            OrgUnitFactory.getWithParents(uid).then(function(ou){
                if(ou.organisationUnits && ou.organisationUnits[0] && ou.organisationUnits[0].parent){
                    orgPath.push(ou.organisationUnits[0].parent.id);
                    var parent = ou.organisationUnits[0].parent;
                    var lastId = ou.organisationUnits[0].id;
                    while(parent){
                        orgPath.push(parent.id);
                        lastId = parent.id;
                        parent = parent.parent;
                    }
                    initOrgUnits(lastId);
                }else{
                    OrgUnitFactory.getSearchTreeRoot().then(function(response) {
                        $scope.orgUnits = response.organisationUnits;
                        angular.forEach($scope.orgUnits, function(ou){
                            ou.show = true;
                            angular.forEach(ou.children, function(o){
                                o.hasChildren = o.children && o.children.length > 0 ? true : false;
                                initExpand(o);
                            });
                        });
                    });
                }
            });
        },150);
        
    };
    
    function initExpand(orgUnit){
        if(orgPath.indexOf(orgUnit.id)>-1){
            if(orgUnit.hasChildren){
                //Get children for the selected orgUnit
                OrgUnitFactory.get(orgUnit.id).then(function(ou) {     
                    orgUnit.show = true;
                    orgUnit.hasChildren = false;
                    orgUnit.children = ou.organisationUnits[0].children;
                    angular.forEach(orgUnit.children, function(ou){            
                        ou.hasChildren = ou.children && ou.children.length > 0 ? true : false;
                        initExpand(ou);
                    });    
                });
            }else{
                setDefaultOrgUnit();
            }
        }
    };
    
    var defaultOrgUnitGroup = 'hrc';
    function setDefaultOrgUnit(){
        if(orgPath && orgPath.length>1){
            OrgUnitFactory.getWithGroups(orgPath[1]).then(function(ou){
                if(ou.organisationUnits && ou.organisationUnits[0]){
                    var o = ou.organisationUnits[0];
                    angular.forEach(o.children, function(oo){
                        angular.forEach(oo.organisationUnitGroups, function(oug){
                            var shortNameLC = oug.shortName.toLowerCase();
                            if(shortNameLC === defaultOrgUnitGroup){
                                $scope.setSelectedSearchingOrgUnit(oo);
                            }
                        });
                    });

                }
                $scope.orgUnitsLoading = false;
            });   
        }else{
            $scope.orgUnitsLoading = false;
        }
    };
    
    if($scope.isReferralEvent){
        initOrgUnits(orgPath[0]);
    }
           
    $scope.expandCollapse = function(orgUnit) {
        if( orgUnit.hasChildren ){            
            //Get children for the selected orgUnit
            OrgUnitFactory.get(orgUnit.id).then(function(ou) {                
                orgUnit.show = !orgUnit.show;
                orgUnit.hasChildren = false;
                orgUnit.children = ou.organisationUnits[0].children;                
                angular.forEach(orgUnit.children, function(ou){                    
                    ou.hasChildren = ou.children && ou.children.length > 0 ? true : false;
                });                
            });           
        }
        else{
            orgUnit.show = !orgUnit.show;   
        }
    };
    

    /*if($scope.isReferralEvent){
        OrgUnitFactory.getSearchTreeRoot().then(function(response) {
            $scope.orgUnits = response.organisationUnits;
            angular.forEach($scope.orgUnits, function(ou){
                ou.show = true;
                angular.forEach(ou.children, function(o){
                    o.hasChildren = o.children && o.children.length > 0 ? true : false;
                });
            });
        });
    }*/
    //end referral logic
    
    
    //If the caller wants to create right away, go ahead and save.
    if (autoCreate) {
        $scope.save();
    }
    ;

    $scope.cancel = function () {
        $modalInstance.close();
    };
    

})

.controller('EventOptionsInTableController', function($scope){
    
    var COMPLETE = "Complete";
    var INCOMPLETE = "Incomplete";
    var VALIDATE = "Validate";
    var DELETE = "Delete";    
    var SKIP = "Skip";
    var UNSKIP = "Unskip";
    var NOTE = "Note";
    

    $scope.completeIncompleteEventFromTable = function(){
                        
        if ($scope.currentEvent.status !== 'COMPLETED'){
            $scope.currentEvent.submitted = true;           
            var formData = $scope.$eval('eventRowForm' + $scope.eventRow.event);                    
            if(formData.$valid){
                $scope.completeIncompleteEvent(true);
            }
        }
        else{
            $scope.completeIncompleteEvent(true);
        }
    };
    
    
    $scope.eventTableOptions = {};    
    $scope.eventTableOptions[COMPLETE] = {text: "Complete", tooltip: 'Complete', icon: "<span class='glyphicon glyphicon-check'></span>", value: COMPLETE, onClick: $scope.completeIncompleteEventFromTable, sort: 0};
    $scope.eventTableOptions[INCOMPLETE] = {text: "Reopen", tooltip: 'Reopen', icon: "<span class='glyphicon glyphicon-pencil'></span>", value: INCOMPLETE, onClick: $scope.completeIncompleteEventFromTable, sort: 1};
    //$scope.eventTableOptions[VALIDATE] = {text: "Validate", tooltip: 'Validate', icon: "<span class='glyphicon glyphicon-cog'></span>", value: VALIDATE, onClick: $scope.validateEvent, sort: 6};    
    $scope.eventTableOptions[DELETE] = {text: "Delete", tooltip: 'Delete', icon: "<span class='glyphicon glyphicon-floppy-remove'></span>", value: DELETE, onClick: $scope.deleteEvent, sort: 2};
    $scope.eventTableOptions[SKIP] = {text: "Skip", tooltip: 'Skip', icon: "<span class='glyphicon glyphicon-step-forward'></span>", value: SKIP, onClick: $scope.skipUnskipEvent, sort: 3};
    $scope.eventTableOptions[UNSKIP] = {text: "Schedule back", tooltip: 'Schedule back', icon: "<span class='glyphicon glyphicon-step-backward'></span>", value: UNSKIP, onClick: $scope.skipUnskipEvent, sort: 4};
    //$scope.eventTableOptions[NOTE] = {text: "Notes", tooltip: 'Show notes', icon: "<span class='glyphicon glyphicon-list-alt'></span>", value: NOTE, onClick: $scope.notesModal, sort: 5};
    
    $scope.eventRow.validatedEventDate = $scope.eventRow.eventDate;
    updateEventTableOptions();
    
    $scope.$watch("eventRow.status", function(newValue, oldValue){
        if(newValue !== oldValue){
            updateEventTableOptions();
        }    
        
    });
    
    $scope.$watch("validatedDateSetForEvent", function(newValue, oldValue){                        
        if(angular.isDefined(newValue)){
            if(!angular.equals(newValue, {})){
                var updatedEvent = newValue.event;
                if(updatedEvent === $scope.eventRow){                    
                        $scope.eventRow.validatedEventDate = newValue.date; 
                        updateEventTableOptions();                    
                }
            }
        }        
        
    });
    
    function updateEventTableOptions(){
        
        var eventRow = $scope.eventRow;        
        
        for(var key in $scope.eventTableOptions){
            $scope.eventTableOptions[key].show = true;
            $scope.eventTableOptions[key].disabled = false;
        }
        
        $scope.eventTableOptions[UNSKIP].show = false;        
        
        switch(eventRow.status){
            case $scope.EVENTSTATUSCOMPLETELABEL:
                $scope.eventTableOptions[COMPLETE].show = false;
                $scope.eventTableOptions[SKIP].show = false; 
                $scope.eventTableOptions[DELETE].show = false;
                //$scope.eventTableOptions[VALIDATE].show = false;                
                $scope.defaultOption = $scope.eventTableOptions[INCOMPLETE];
                break;
            case $scope.EVENTSTATUSSKIPPEDLABEL:
                $scope.eventTableOptions[COMPLETE].show = false;
                $scope.eventTableOptions[INCOMPLETE].show = false;
                //$scope.eventTableOptions[VALIDATE].show = false;                
                $scope.eventTableOptions[SKIP].show = false;
                
                $scope.eventTableOptions[UNSKIP].show = true;
                $scope.defaultOption = $scope.eventTableOptions[UNSKIP];
                break;            
            default:                 
                if(eventRow.validatedEventDate){
                    $scope.eventTableOptions[INCOMPLETE].show = false;
                    $scope.eventTableOptions[SKIP].show = false;
                    $scope.defaultOption = $scope.eventTableOptions[COMPLETE];
                }
                else {
                    $scope.eventTableOptions[INCOMPLETE].show = false;                    
                    //$scope.eventTableOptions[VALIDATE].show = false;
                    $scope.eventTableOptions[COMPLETE].disabled = true;
                    $scope.defaultOption = $scope.eventTableOptions[COMPLETE];
                }                          
                break;
        }
        
        createOptionsArray();
    }
    
    function createOptionsArray(){
        
        $scope.eventTableOptionsArr = [];        
        
        for(var key in $scope.eventTableOptions){
            $scope.eventTableOptionsArr[$scope.eventTableOptions[key].sort] = $scope.eventTableOptions[key];
        }
    }        
})
.controller('MakeReferralController', function($scope, $modalInstance, stage, OrgUnitFactory){
    $scope.stage = stage;
    $scope.cancel = function(){
        $modalInstance.close();
    };
    
    $scope.makeReferral = function(){
        if(!$scope.referralDate){
            $scope.dateError = true;
        }else{
            $scope.dateError = false;
        }
        
        if(!$scope.selectedSearchingOrgUnit){
            $scope.orgError = true;     
        }else{
            $scope.orgError = false;
        }
        if(!$scope.dateError && !$scope.orgError){
            $modalInstance.close();
        }
    };
    
    $scope.setSelectedSearchingOrgUnit = function(orgUnit){
        $scope.selectedSearchingOrgUnit = orgUnit;     
    };
    
    OrgUnitFactory.getSearchTreeRoot().then(function(response) {
        $scope.orgUnits = response.organisationUnits;
        angular.forEach($scope.orgUnits, function(ou){
            ou.show = true;
            angular.forEach(ou.children, function(o){                    
                o.hasChildren = o.children && o.children.length > 0 ? true : false;
            });            
        });
    });
    
    $scope.expandCollapse = function(orgUnit) {
        if( orgUnit.hasChildren ){            
            //Get children for the selected orgUnit
            OrgUnitFactory.get(orgUnit.id).then(function(ou) {                
                orgUnit.show = !orgUnit.show;
                orgUnit.hasChildren = false;
                orgUnit.children = ou.organisationUnits[0].children;                
                angular.forEach(orgUnit.children, function(ou){                    
                    ou.hasChildren = ou.children && ou.children.length > 0 ? true : false;
                });                
            });           
        }
        else{
            orgUnit.show = !orgUnit.show;   
        }        
    };
})
.controller('ModalDefaultFormController', function($scope){
    
    var defaultRequestError = "Server error. Please try again later.";
    
    $scope.completeIncompleteEventModal = function(){  
           
        $scope.requestError = "";
        debugger;
        if ($scope.currentEvent.status === 'COMPLETED'){
            var dhis2Event = $scope.makeDhis2EventToUpdate();
            dhis2Event.status = 'ACTIVE';
        }
        else{
            $scope.modalForm.$setSubmitted();
            $scope.modalForm.outerForm.$setSubmitted();     
            
            if($scope.modalForm.$invalid){
                return;
            }     
            
            //check for errors!
            if(angular.isDefined($scope.errorMessages[$scope.currentEvent.event]) && $scope.errorMessages[$scope.currentEvent.event].length > 0) {
                //There is unresolved program rule errors - show error message.
                return;
            }
            
            var dhis2Event = $scope.makeDhis2EventToUpdate();
            dhis2Event.status = 'COMPLETED';
        }        
        
        $scope.executeCompleteIncompleteEvent(dhis2Event).then(function(){                                    
            if(dhis2Event.status === 'COMPLETED'){
                $scope.eventEditFormModalInstance.close();            
            }
        }, function(error){
            $scope.requestError = defaultRequestError;                                   
        });
    };
    
    $scope.deleteEventModal = function(){
        
        $scope.executeDeleteEvent().then(function(){
            
            $scope.eventEditFormModalInstance.close();
        }, function(){
            
            $scope.requestError = defaultRequestError; 
        });        
    };
    
    $scope.skipUnskipEventModal = function(){
                
        var dhis2Event = $scope.makeDhis2EventToUpdate();
        
        if ($scope.currentEvent.status === 'SKIPPED') {//unskip event
            dhis2Event.status = 'ACTIVE';
        }
        else {//skip event
            dhis2Event.status = 'SKIPPED';
        }
        
        
        $scope.executeSkipUnskipEvent(dhis2Event).then(function(){
            if(dhis2Event.status === 'SKIPPED'){
                $scope.eventEditFormModalInstance.close();
            }
        }, function(){
            $scope.requestError = defaultRequestError;
        });        
    };
    
    $scope.closeEventModal = function(){
        $scope.eventEditFormModalInstance.dismiss();
    };
})
;