'use strict';

/* Controllers */
var trackerControllers = angular.module('trackerControllers', [])

//Controller for home page
.controller('HomeController',
        function($scope,
                CurrentUserProfile,
                $translate) {

            //Get current locale
            CurrentUserProfile.getProfile(function(data) {
                $translate.uses(data.settings.keyUiLocale);
            });
        })

//Controller for lab page
.controller('ANCLabController',
        function($scope,
                CurrentUserProfile,
                $location,
                $translate) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    $scope.lab = 'this is lab page';

    $scope.close = function() {
        $location.path('/anc');
    };
})

//Controller for summary page
.controller('ANCSummaryController',
        function($scope,
                CurrentUserProfile,
                $location,
                $translate) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    $scope.summary = 'this is summary page';

    $scope.close = function() {
        $location.path('/anc');
    };
})

//Controller for registration page
.controller('RegistrationController',
        function($scope,
                $location,
                CurrentUserProfile,
                RegistrationAttributesFactory,
                PersonFactory,
                $translate,
                storage,
                DialogService,
                $filter) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Get attributes for registration
    $scope.registrationAttributes = '';
    RegistrationAttributesFactory.getRegistrationAttributes().success(function(registrationAttributes) {
        angular.forEach(registrationAttributes, function(registrationAttribute) {
            if (angular.isObject(registrationAttribute.personAttributeOptions)) {
                angular.forEach(registrationAttribute.personAttributeOptions, function(attributeOption) {
                    attributeOption.value = attributeOption.name;
                });
            }
        });

        $scope.registrationAttributes = registrationAttributes;
    });

    $scope.saveAndRegisterPregnancy = function() {

        $scope.outerRegistrationForm.submitted = true;

        if ($scope.outerRegistrationForm.$invalid) {
            return false;
        }

        $scope.person.orgUnit = $scope.selectedOrgUnit.id;
        //$scope.person.gender = 'FEMALE';
        //$scope.person.dateOfBirth.type = 'VERIFIED';
        //$scope.person.dateOfRegistration = $filter('date')(new Date(), 'yyyy-MM-dd');

        var attributes = [];
        angular.forEach($scope.registrationAttributes, function(registrationAttribute) {
            if (!angular.isUndefined(registrationAttribute.value)) {
                var attribute = {type: registrationAttribute.id, value: registrationAttribute.value};
                attributes.push(attribute);
            }
        });

        $scope.person.attributes = attributes;

        PersonFactory.registerPerson($scope.person).success(function(data) {
            if (data.status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'registration_error',
                    bodyText: data.description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {//registration is successful, proceed to enrollment
                $location.path('/enrollment').search({personUid: data.reference});
            }
        });
    };

    $scope.saveAndClose = function() {

        $scope.outerRegistrationForm.submitted = true;

        if ($scope.outerRegistrationForm.$invalid) {
            return false;
        }

        $scope.person.orgUnit = $scope.selectedOrgUnit.id;
        $scope.person.gender = 'FEMALE';
        $scope.person.dateOfBirth.type = 'VERIFIED';
        $scope.person.dateOfRegistration = $filter('date')(new Date(), 'yyyy-MM-dd');

        var attributes = [];
        angular.forEach($scope.registrationAttributes, function(registrationAttribute) {
            if (!angular.isUndefined(registrationAttribute.value)) {
                var attribute = {type: registrationAttribute.id, value: registrationAttribute.value};
                attributes.push(attribute);
            }
        });

        $scope.person.attributes = attributes;

        PersonFactory.registerPerson($scope.person).success(function(data) {
            if (data.status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'registration_error',
                    bodyText: data.description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                $location.path('/anc');
            }
        });
    };

    $scope.cancel = function() {
        
        $location.path('/anc');

        /*var modalOptions = {
         closeButtonText: 'Cancel',
         actionButtonText: 'Delete action?',
         headerText: 'Delete .... blah blah?',
         bodyText: 'Are you sure you want to delete?'
         };

         ModalService.showModal({}, modalOptions).then(function(result) {
         $location.path('/anc');
         }); */
    };

    //calendar settings
    $scope.showWeeks = true;

    $scope.toggleWeeks = function() {
        $scope.showWeeks = !$scope.showWeeks;
    };

    $scope.clear = function() {
        $scope.dt = null;
    };

    $scope.toggleMin = function() {
        $scope.minDate = ($scope.minDate) ? null : new Date();
    };

    $scope.toggleMin();

    $scope.openCalendar = function() {
        $timeout(function() {
            $scope.opened = true;
        });
    };

})

//Controller for person edit
.controller('PersonEditController',
        function($scope,
                $location,
                CurrentUserProfile,
                $translate,
                PersonFactory,
                $filter,
                storage) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Pick selected person and fetch profile from server
    $scope.personUid = ($location.search()).personUid;
    PersonFactory.getPerson($scope.personUid)
            .success(function(person) {
                $scope.person = person;
                $scope.person.dateOfBirth.date = $filter('date')($scope.person.dateOfBirth.date, 'yyyy-MM-dd');
                console.log('The person I am going to edit is:  ', $scope.person);
            });

    $scope.cancel = function() {
        $location.path('/anc');
    };
})

//Controller for enrollment
.controller('EnrollmentController',
        function($scope,
                $location,
                CurrentUserProfile,
                $translate,
                ProgramFactory,
                PersonFactory,
                EnrollmentFactory,
                DialogService,
                $filter,
                storage) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
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
    ProgramFactory.getProgram($scope.selectedProgram.id).success(function(program) {
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

        //Get registration attributes                                             
        angular.forEach($scope.person.attributes, function(registrationAttribute) {
            if (!angular.isUndefined(registrationAttribute.value)) {
                var attribute = {
                    type: registrationAttribute.type,
                    value: registrationAttribute.value
                };
                attributes.push(attribute);
            }
        });

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

        $scope.person.attributes = attributes;

        var enrollment = {person: $scope.person.person,
            program: $scope.program.id,
            status: 'ACTIVE',
            dateOfEnrollment: $filter('date')(new Date(), 'yyyy-MM-dd'),
            dateOfIncident: $filter('date')($scope.program.dateOfIncident, 'yyyy-MM-dd')
        };
        
        console.log('the person I am going to enroll is:  ', $scope.person);

        //Enroll person
        EnrollmentFactory.enrollPerson(enrollment).success(function(data) {
            if (data.status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'enrollment_error',
                    bodyText: data.description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {//Success, proceed to update person
                PersonFactory.updatePerson($scope.person).success(function(data) {
                    if (data.status === 'ERROR') {
                        var dialogOptions = {
                            headerText: 'person_update_error',
                            bodyText: data.description
                        };

                        DialogService.showDialog({}, dialogOptions);
                    }
                    else {//success, proceed to consultation page                        
                        $location.path('/anc/dashboard').search({personUid: $scope.person.person});
                    }
                });
            }
        });       
    };

    $scope.cancel = function() {
        $location.path('/anc');
    };
})

//Controller for settings page
.controller('SettingsController',
        function($scope,
                CurrentUserProfile,
                $translate,
                OrgUnitFactory,
                ProgramFactory,
                UtilityService,
                storage) {

    //Get current locale and do translations
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
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

        ProgramFactory.getMyPrograms().then(function(response) {

            var prs = response.data;
            angular.forEach(prs.organisationUnits, function(ou) {
                if (ou.id === $scope.selectedOrgUnit.id) {
                    angular.forEach(ou.programs, function(p) {
                        $scope.programs.push(p);
                        if (p.id === $scope.selecteProgram.id) {
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

            ProgramFactory.getMyPrograms().then(function(response) {

                var prs = response.data;

                angular.forEach(prs.organisationUnits, function(ou) {
                    if (ou.id === $scope.selectedOrgUnit.id) {
                        angular.forEach(ou.programs, function(p) {
                            $scope.programs.push(p);

                            if (p.id === $scope.selecteProgram.id) {
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
                $scope.selectedOrgUnit.name === 'not_selected' ||
                !angular.isObject($scope.selecteProgram) ||
                $scope.selecteProgram.label === 'not_selected') {

            alert('please select orgunit/program');

        }

        if (angular.isObject($scope.selectedOrgUnit) &&
                $scope.selectedOrgUnit.name !== 'not_selected' &&
                angular.isObject($scope.selecteProgram) &&
                $scope.selecteProgram.label !== 'not_selected') {

            storage.set('SELECTED_ORGUNIT', $scope.selectedOrgUnit);
            storage.set('SELECTED_PROGRAM', $scope.selecteProgram);

        }
    };
})

//Controller for first page
.controller('SearchController',
        function($scope,
                $location,
                CurrentUserProfile,
                $translate,
                PersonFactory,
                EnrollmentFactory,
                $modal,
                $log,
                storage) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    $scope.searchPerson = function(listAll) {

        if (!angular.isObject($scope.selectedOrgUnit) || $scope.selectedOrgUnit.name === 'not_selected') {
            alert('please select orgunit from settings page');
        }
        if (angular.isObject($scope.selectedOrgUnit) && $scope.selectedOrgUnit.name !== 'not_selected') {

            if (listAll) {
                PersonFactory.getAllPersons($scope.selectedOrgUnit.id).success(function(data) {
                    $scope.persons = data.personList;
                });
            }
            else {
                console.log('just do the search');
            }
        }
    };

    $scope.enrollPerson = function(personUid) {

        EnrollmentFactory.enrollPerson(personUid).success(function(data) {
            if (data.status === 'ERROR') {
                alert(data.status + '  ' + data.description);
            }
            else {
                alert(data.status);
            }
        });
    };

    $scope.showEditPerson = function(personId) {
        $scope.personId = personId;
        console.log('the person I am going to edit is:  ', $scope.personId);
    };

    $scope.showRegisterPerson = function() {

        if (!angular.isObject($scope.selectedOrgUnit) || $scope.selectedOrgUnit.name === 'not_selected') {
            alert('please select orgunit from settings page');
        }

        if (angular.isObject($scope.selectedOrgUnit) && $scope.selectedOrgUnit.name !== 'not_selected') {
            $location.path('/registration');
        }
    };
})

//Controller first ANC start page
.controller('ANCDashboardController',
        function($scope,
                CurrentUserProfile,
                ProgramFactory,
                ProgramStageFactory,
                PersonFactory,
                DHIS2EventFactory,
                storage,
                $filter,
                $translate,
                $location,
                DialogService,
                orderByFilter) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    //pick selected orgUnit and program
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //Pick selected person and fetch profile from server
    $scope.personUid = ($location.search()).personUid;
    PersonFactory.getPerson($scope.personUid).success(function(person) {
        $scope.person = person;
    });

    $scope.DHIS2Events = '';
    var programStage = '';

    //Get program and its programStages
    ProgramFactory.getProgram($scope.selectedProgram.id).then(function(response) {

        angular.forEach(response.data.programStages, function(programStage) {

            ProgramStageFactory.getProgramStage(programStage.id).then(function(response) {
                storage.set(response.data.id, response.data);
            });
        });

        //Fetch available events for the selected person
        DHIS2EventFactory.getDHIS2Events($scope.personUid, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).success(function(DHIS2Events) {

            $scope.DHIS2Events = DHIS2Events.eventList;

            if (!angular.isUndefined($scope.DHIS2Events)) {

                $scope.DHIS2Events = orderByFilter($scope.DHIS2Events, '-eventDate');
                $scope.DHIS2Events.reverse();

                $scope.ps = '';
                angular.forEach($scope.DHIS2Events, function(DHIS2Event) {
                    $scope.ps = storage.get(DHIS2Event.programStage);
                    DHIS2Event.programStage = {name: $scope.ps.name, uid: $scope.ps.id};
                    storage.set(DHIS2Event.event, DHIS2Event);
                });
            }
        });

    });

    //create new ANC visit
    $scope.createNewANCVisit = function() {

        //Get available visits for the selected person
        $scope.personUid = ($location.search()).personUid;

        $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
        $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

        if (angular.isUndefined($scope.DHIS2Events)) {
            programStage = 'CbewL3Uamem';
        }

        if (!angular.isUndefined($scope.DHIS2Events)) {
            programStage = 'LqLcUFPa9Gw';
        }

        $scope.DHIS2Event = {program: $scope.selectedProgram.id,
            programStage: programStage,
            orgUnit: $scope.selectedOrgUnit.id,
            status: 'ACTIVE',
            notes: ['I am setting this from the web app1'],
            person: $scope.personUid,
            eventDate: $filter('date')(new Date(), 'yyyy-MM-dd')
        };

        DHIS2EventFactory.postDHIS2Event($scope.DHIS2Event).success(function(data) {
            if (data.importSummaries[0].status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'consultation_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                storage.set(data.importSummaries[0].reference, $scope.DHIS2Event);
                $location.path('/anc/dataentry').search({personUid: $scope.personUid, eventUid: data.importSummaries[0].reference});
            }
        });
    };

    $scope.cancel = function() {
        $location.path('/anc');
    };
})

//Controller for managing visits
.controller('ANCVisitController',
        function($scope,
                $translate,
                $filter,
                CurrentUserProfile,
                EIFactory,
                $location,
                PersonFactory,
                OrgUnitFactory,
                DHIS2EventService,
                ProgramStageFactory,
                orderByFilter,
                storage) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    //get the selected event
    $scope.eventUid = ($location.search()).eventUid;
    $scope.dhis2Event = storage.get($scope.eventUid);
    $scope.dhis2Event.eventDate = $filter('date')($scope.dhis2Event.eventDate, 'yyyy-MM-dd');

    //Pick selected person UID and fetch full person profile from server
    $scope.personUid = $scope.dhis2Event.person;

    PersonFactory.getPerson($scope.personUid).success(function(person) {

        $scope.person = person;
        OrgUnitFactory.getOrgUnit(person.orgUnit).success(function(orgUnit) {
            $scope.person.orgUnit = {name: orgUnit.name, uid: orgUnit.id};
        });
    });

    //Load EIs
    EIFactory.getEI(function(data) {
        angular.forEach(data, function(ei) {
            storage.set(ei.databaseVariableName, ei);
        });
    });

    //Load program stage for the selected data elements
    //$scope.prStageDataElements = [];

    //Fetch program stage of the selected event
    ProgramStageFactory.getProgramStage($scope.dhis2Event.programStage.uid).then(function(response) {
        $scope.prStageDataElements = response.data.programStageDataElements;
    });
})

//Controller for managing Data Entry page
.controller('ANCDataEntryController',
        function($scope,                
                EIFactory,
                PersonFactory,
                DataElementFactory,
                storage,
                TransferHandler,
                ExpressionService,
                $location,
                $translate,
                CurrentUserProfile,
                orderByFilter) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });

    //get selected orgunit and program from settings
    $scope.selectedOrgUnit = storage.get('SELECTED_ORGUNIT');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //get selected person and event uids, fetch full person profile from server
    $scope.personUid = ($location.search()).personUid;
    $scope.eventUid = ($location.search()).eventUid;

    PersonFactory.getPerson($scope.personUid).success(function(person) {
        $scope.person = person;
        $scope.person.orgUnit = {name: $scope.selectedOrgUnit.name, uid: $scope.selectedOrgUnit.id};
    });

    //Load EIs
    EIFactory.getEI(function(data) {
        angular.forEach(data, function(ei) {
            storage.set(ei.databaseVariableName, ei);
        });
    });

    //which page to show
    $scope.ancPage = 'GEN';

    //Load program stage for the selected data elements
    $scope.dataElements = [];
    $scope.de = '';
    $scope.genDataElementGroupUid = 'T7rt8xdPGbC';
    DataElementFactory.getDataElementGroup($scope.genDataElementGroupUid).success(function(dataElementGroup) {
        angular.forEach(dataElementGroup.dataElements, function(ei) {
            $scope.de = storage.get(ei.code);
            $scope.de.id = ei.id;
            $scope.dataElements.push($scope.de);
        });
        $scope.dataElements = orderByFilter($scope.dataElements, '-sequenceOnPage');
        $scope.dataElements.reverse();
    });

    //Execute actions of interventions
    var dep = [],
            con = [],
            smr = [],
            rem = [],
            mes = [],
            sch = [],
            lab = [];

    $scope.interventionAction = function(dataElement) {

        var essentialIntervention = storage.get(dataElement.databaseVariableName);

        angular.forEach(essentialIntervention.actions, function(eiAction) {

            if (angular.isUndefined(dataElement.value)) {
                dataElement.value = -1;
            }

            var val = eiAction.value.replace(new RegExp('#' + dataElement.databaseVariableName + '#', 'g'), dataElement.value);
            console.log('the expression is:  ', val);

            if (val.indexOf('#') !== -1) {
                val = ExpressionService.getDataElementExpression(val, $scope.eventUid);
            }

            //console.log(essentialIntervention.databaseVariableName, ' : ', val, ' : ', dataElement.value, '  and the uid is:  ', dataElement.id);

            if ($scope.$eval(val)) {
                //console.log('the expression is true:  ', val);

                TransferHandler.store(eiAction.task.transferToDependencies, dep);
                TransferHandler.store(eiAction.task.transferToConditionsComplications, con);
                TransferHandler.store(eiAction.task.transferToSummary, smr);
                TransferHandler.store(eiAction.task.transferToReminders, rem);
                TransferHandler.store(eiAction.task.transferToMessaging, mes);
                TransferHandler.store(eiAction.task.transferToScheduling, sch);
            }

        });

        if (essentialIntervention.transferToLabResults) {
            if (lab.indexOf(essentialIntervention.transferToLabResults) !== -1) {
                lab.push(essentialIntervention.transferToLabResults);
            }
        }

        $scope.depResultGen = dep;
        $scope.conResultGen = con;
        $scope.smrResultGen = smr;
        $scope.remResultGen = rem;
        $scope.mesResultGen = mes;
        $scope.schResultGen = sch;
        $scope.labResultGen = lab;
    };

    $scope.showDepPage = function() {

        //which page to show
        $scope.ancPage = 'DEP';

        //Load DEP data elements - but which one?
        $scope.dataElements = [];
        $scope.de = '';

        $scope.depDataElementGroupUid = 'fxUUUmiyUzT';
        DataElementFactory.getDataElementGroup($scope.depDataElementGroupUid).success(function(dataElementGroup) {
            angular.forEach(dataElementGroup.dataElements, function(ei) {
                $scope.de = storage.get(ei.code);
                $scope.de.id = ei.id;
                $scope.dataElements.push($scope.de);
            });
        });
    };

    $scope.items = ['item1', 'item2', 'item3'];
    $scope.selected = {};

    $scope.writeNote = function() {

    };
})

//Controller for summary page
.controller('ANCVisitNotesController',
        function($scope,
                CurrentUserProfile,
                $location,
                $translate) {

    //Get current locale
    CurrentUserProfile.getProfile(function(data) {
        $translate.uses(data.settings.keyUiLocale);
    });
});
