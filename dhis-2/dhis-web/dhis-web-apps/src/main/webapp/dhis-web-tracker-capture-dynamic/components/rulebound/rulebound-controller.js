trackerCapture.controller('RuleBoundController',
        function(
                $rootScope,
                $scope,
                $log) {

    
    $scope.widget = $scope.$parent.$parent.biggerWidget ? $scope.$parent.$parent.biggerWidget
    : $scope.$parent.$parent.smallerWidget ? $scope.$parent.$parent.smallerWidget : null;
    $scope.widgetTitle = $scope.widget.title;
    $scope.widgetCode = $scope.widget.code;
    
    
    $scope.displayTextEffects = {};
    $scope.collectDataEffects = {};
    
    //listen for the selected items
    $scope.$on('ruleeffectsupdated', function(event, args) {
        
        //Bind non-bound rule effects, if any.
        angular.forEach($rootScope.ruleeffects, function(effect) {
            if(effect.location == $scope.widgetCode){
                //This effect is affecting the local widget
                
                if(effect.action == "displaytext") {
                    //this action is display text. Make sure the displaytext is
                    //added to the local list of displayed texts
                    if(!angular.isObject($scope.displayTextEffects[effect.id])){
                        $scope.displayTextEffects[effect.id] = effect;
                    }
                } else {
                    $log.warn("action: '" + effect.action + "' not supported by rulebound-controller.js")
                }
            }
        });
    });     
});