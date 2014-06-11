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
        templateUrl:'components/registration/registration.html',
        controller: 'RegistrationController'
    }).when('/dashboard',{
        templateUrl:'components/dashboard/dashboard.html',
        controller: 'DashboardController'
    }).when('/ancvisit',{
        templateUrl:'components/ancvisit/ancvisit.html',
        controller: 'AncVisitController'
    }).when('/profile',{
        templateUrl:'components/profile/profile.html',
        controller: 'ProfileController'
    }).otherwise({
        redirectTo : '/'
    });  
    
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
    
    $translateProvider.preferredLanguage('en');	
    
});
