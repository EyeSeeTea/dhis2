'use strict';

/* App Module */

var eventCapture = angular.module('eventCapture',
		 ['ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies', 
		  'eventCaptureDirectives', 
		  'eventCaptureControllers', 
		  'eventCaptureServices',
		  'eventCaptureFilters',
		  'angularLocalStorage', 
		  'pascalprecht.translate',
          'd2Menu'])
              
.value('DHIS2URL', '..')

.config(function($httpProvider, $routeProvider, $translateProvider) {    
            
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    
    $routeProvider.when('/', {
        templateUrl: 'views/home.html',
        controller: 'MainController'/*,
        resolve: {
            geoJsons: function(GeoJsonFactory){
                return GeoJsonFactory.getAll();
            }
        }*/
    }).otherwise({
        redirectTo : '/'
    });
    
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
    
    $translateProvider.preferredLanguage('en');    
});