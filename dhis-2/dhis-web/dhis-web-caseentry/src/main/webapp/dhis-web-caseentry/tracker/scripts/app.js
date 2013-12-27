'use strict';

/* App Module */

var ancTracker = angular.module('ancTracker',
		[ 'ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies', 
		  'trackerDirectives', 
		  'trackerControllers', 
		  'trackerServices', 
		  'angularLocalStorage', 
		  'pascalprecht.translate', 
		  'angularTreeview', 
                  'directive.contextMenu',
                  'ui.date',
		  'ui.bootstrap',
                  'angularCharts'])

.config(function($routeProvider, $translateProvider) {	
	
	$routeProvider.when('/', {
		templateUrl : 'views/home.html',
		controller : 'HomeController'
	}).when('/settings', {
		templateUrl : 'views/settings.html',
		controller : 'SettingsController'
	}).when('/dhis2', {
		templateUrl : 'views/dhis2.html',
		controller : 'DHIS2Controller'
	}).when('/anc', {
		templateUrl : 'views/anc/firstpage.html',
		controller : 'SearchController'			
	}).when('/anc/lab', {
		templateUrl : 'views/anc/lab.html',
		controller : 'ANCLabController'
	}).when('/anc/summary', {
		templateUrl : 'views/anc/summary.html',
		controller : 'ANCSummaryController'
	}).when('/anc/dashboard', {
		templateUrl : 'views/anc/dashboard.html',
		controller : 'ANCDashboardController'			
	}).when('/anc/visit', {
		templateUrl : 'views/anc/visit.html',
		controller : 'ANCVisitController'
	}).when('/anc/dataentry', {
		templateUrl : 'views/anc/dataentry.html',
		controller : 'ANCDataEntryController'
	}).when('/registration', {
		templateUrl : 'views/registration.html',
		controller : 'RegistrationController'		
	}).when('/personedit', {
		templateUrl : 'views/personedit.html',
		controller : 'PersonEditController'		
	}).when('/enrollment', {
		templateUrl : 'views/enrollment.html',
		controller : 'EnrollmentController'		
	}).otherwise({
		redirectTo : '/'
	});
		
	$translateProvider.useStaticFilesLoader({
		prefix: 'i18n/',
		suffix: '.json'
	});
	
	$translateProvider.preferredLanguage('no');	
})
.run(function($rootScope){
    $rootScope.$on('hanldeEmit', function(event, args){
       $rootScope.$broadcast('handleBroadcast', args); 
    });
});


