//Controller for the header section
trackerCapture.controller('HeaderController',
        function($scope,                
                DHIS2URL) {

    
    $scope.home = function(){        
        window.location = DHIS2URL;
    };    
});