'use strict';

/* App Module */

var trackerCapture = angular.module('trackerCapture',
		 ['ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies',  
		  'trackerCaptureServices',
		  'trackerCaptureFilters',
          'trackerCaptureDirectives', 
          'trackerCaptureControllers',
		  'angularLocalStorage',
          'ui.select2',
		  'pascalprecht.translate'])
              
.value('DHIS2URL', '..')

.config(function($httpProvider, $routeProvider, $translateProvider) {    
            
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    
    $routeProvider.when('/', {
        templateUrl:'views/home.html',
        controller: 'SelectionController'
    }).when('/registration',{
        templateUrl:'anc/registration/registration.html',
        controller: 'RegistrationController'
    }).when('/dashboard',{
        templateUrl:'anc/dashboard/dashboard.html',
        controller: 'DashboardController'
    }).when('/visit',{
        templateUrl:'anc/visit/visit.html',
        controller: 'VisitController'
    }).when('/profile',{
        templateUrl:'anc/profile/profile.html',
        controller: 'ProfileController'
    }).when('/fundalheight',{
        templateUrl:'anc/fundalheight/fundalheight.html',
        controller: 'FundalheightController'
    }).otherwise({
        redirectTo : '/'
    });  
    
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
    
    $translateProvider.preferredLanguage('en');	
    
});
