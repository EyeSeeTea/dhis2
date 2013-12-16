'use strict';

/* Services */

var trackerFactory = angular.module('trackerServices', ['ngResource'])

        /* Factory to fetch programs */
        .factory('ProgramFactory', function($http, storage) {

            var baseUrl = '../../api/programs/';
            var ProgramFactory = {};

            ProgramFactory.getProgram = function(uid) {
                return $http.get(baseUrl + uid + '.json?viewClass=extended');
            };

            ProgramFactory.getMyPrograms = function() {
                return $http.get('../../api/me/programs');
            };

            return ProgramFactory;
        })

        .service('ProgramService', function() {
            var program = null;
            return {
                setProgram: function(p) {
                    program = p;
                },
                getProgram: function() {
                    return program;
                }
            };
        })

        /* Factory to enroll person in a program */
        .service('EnrollmentFactory', function( $http ) {

            var baseUrl = '../../api/enrollments';

            var EnrollmentFactory = {};

            EnrollmentFactory.enrollPerson = function( enrollment ) {
                return $http.post( baseUrl, enrollment );
            };

            return EnrollmentFactory;
        })

        /* Factory to fetch programStages */
        .factory('ProgramStageFactory', function($http) {

            var baseUrl = '../../api/programStages/';
            var ProgramStageFactory = {};

            ProgramStageFactory.getProgramStage = function(uid) {
                return $http.get(baseUrl + uid + '.json?viewClass=extended');
            };

            return ProgramStageFactory;
        })

        /* Factory for loading Essential Interventions */
        .factory('EIFactory', function($http) {

            return {
                getEI: function(data) {
                    $http.get('json/EI.json').success(data);
                }
            };
        })

        /* Factory for loading Current User Locale */
        .factory('LocaleFactory', function($http) {

            return {
                getCurrentLocale: function(data) {
                    $http.get('../../api/userSettings/currentLocale').success(data);
                }
            };
        })

        /* Factory for fetching current logged in user */
        .factory('UserFactory', function($http) {

            return {
                getCurrentUser: function(data) {
                    $http.get('../../api/me/user-account').success(data);
                }
            };
        })

        /* Factory for loading OrgUnit */
        .factory('OrgUnitFactory', function($http) {

            var baseUrl = '../../api/organisationUnits/';
            var OrgUnitFactory = {};

            OrgUnitFactory.getAllOrgUnits = function() {
                return $http.get(baseUrl);
            };

            OrgUnitFactory.getOrgUnit = function(uid) {
                return $http.get(baseUrl + uid);
            };

            OrgUnitFactory.getMyOrgUnits = function() {
                return $http.get('../../api/me/organisationUnits');
            };

            return OrgUnitFactory;
        })

        /* Factory for getting person */
        .factory('PersonFactory', function($http) {

            var baseUrl = '../../api/persons';
            var PersonFactory = {};

            PersonFactory.getPerson = function(uid) {
                return $http.get(baseUrl + '/' + uid + '.json');
            };

            PersonFactory.getAllPersons = function(orgUnitUid) {
                return $http.get(baseUrl + '?orgUnit=' + orgUnitUid);
            };

            PersonFactory.registerPerson = function(person) {
                return $http.post(baseUrl, person);
            };
            
            PersonFactory.updatePerson = function(person) {
                return $http.put(baseUrl + '/' + person.person , person);
            };

            return PersonFactory;
        })

        /* Factory for getting person attribute types*/
        .factory('RegistrationAttributesFactory', function($http) {

            var baseUrl = '../../api/personAttributeTypes/withoutPrograms?viewClass=extended';

            var RegistrationAttributesFactory = {};

            RegistrationAttributesFactory.getRegistrationAttributes = function() {
                return $http.get(baseUrl);
            };

            return RegistrationAttributesFactory;
        })

        /* Factory for getting person attribute types*/
        .factory('PersonAttributeTypesFactory', function($http) {

            var baseUrl = '../../api/personAttributeTypes';

            var PersonAttributeTypesFactory = {};

            PersonAttributeTypesFactory.getPersonAttributeTypes = function() {
                return $http.get(baseUrl + '.json');
            };

            PersonAttributeTypesFactory.getPersonAttributeType = function(uid) {
                return $http.get(baseUrl + '/' + uid + '.json');
            };

            return PersonAttributeTypesFactory;
        })

        .service('PersonService', function() {
            var person = null;

            return {
                setPerson: function(p) {
                    person = p;
                },
                getPerson: function() {
                    return person;
                }
            };
        })

        /* factory for getting data elements */
        .factory('DataElementFactory', function($http) {

            var dataElementGroupBaseUrl = '../../api/dataElementGroups/';
            var dataElementBaseUrl = '../../api/dataElements/';

            var DataElemmentFactory = {};

            DataElemmentFactory.getDataElement = function(uid) {
                return $http.get(dataElementBaseUrl + uid + '.json');
            };

            DataElemmentFactory.getAllDataElements = function() {
                return $http.get(dataElementBaseUrl);
            };

            DataElemmentFactory.getDataElementGroup = function(uid) {
                return $http.get(dataElementGroupBaseUrl + uid + '.json');
            };

            DataElemmentFactory.getAllDataElementGroups = function() {
                return $http.get(dataElementGroupBaseUrl);
            };

            return DataElemmentFactory;
        })

        .factory('DHIS2EventFactory', function($http) {

            var eventBaseUrl = '../../api/events.json?';

            var DHIS2EventFactory = {};

            DHIS2EventFactory.getDHIS2Events = function(person, orgUnit, program) {
                var url = 'person=' + person + '&orgUnit=' + orgUnit + '&program=' + program;
                return $http.get(eventBaseUrl + url);
            };

            DHIS2EventFactory.getDHIS2Event = function(eventUID) {
                eventBaseUrl = '../../api/events/';
                return $http.get(eventBaseUrl + eventUID + '.json');
            };

            DHIS2EventFactory.getMyDHIS2Event = function(params) {
                return $http.get(eventBaseUrl + params);
            };

            DHIS2EventFactory.postDHIS2Event = function(DHIS2event) {
                return $http.post(eventBaseUrl, DHIS2event);
            };

            return DHIS2EventFactory;
        })

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

        .factory('TransferHandler', function() {

            return {
                store: function(input, output) {
                    for (var i = 0; i < input.length; i++) {
                        if (input[i]) {
                            if (output.indexOf(input[i]) === -1) {
                                output.push(input[i]);
                            }
                        }
                    }
                }
            };
        })

        .factory('TrackerApp', function($http, storage) {

            return {
                getConfiguration: function(data) {
                    $http.get('manifest.webapp').success(data);
                },
                getDHISAPIURL: function() {
                    var DHIS2APIURL = 'http://localhost:8080/api/';
                    return DHIS2APIURL;
                }
            };
        })

        .service('UtilityService', function() {

            return {
                searchById: function(data, k) {

                    angular.forEach(data, function(d) {
                        if (k.id === d.id) {
                            return data.indexOf(d);
                        }
                    });
                    return -1;
                }
            };
        })

        .service('ExpressionService', function(DHIS2EventFactory) {

            var currentEvent = '';

            return {
                getDataElementExpression: function(val, eventUid) {

                    DHIS2EventFactory.getDHIS2Event(eventUid)
                            .success(function(e) {
                                currentEvent = e;
                                var regex = /#[^#]*#/g,
                                        match,
                                        m,
                                        mDe,
                                        matches = [];

                                while (match = regex.exec(val)) {
                                    matches.push(match);
                                    m = match.toString();
                                    mDe = m.substring(1, m.length - 1)
                                    var r = new RegExp(match, 'g');
                                    //val = val.replace(r, '@'+mDe+'@');
                                    val = val.replace(r, 100);
                                }
                                return val;
                            });
                }
            };
        })

        .service('ModalService', ['$modal',
            function($modal) {

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
        
        .service('DialogService', ['$modal',
            function($modal) {

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

            }]);
