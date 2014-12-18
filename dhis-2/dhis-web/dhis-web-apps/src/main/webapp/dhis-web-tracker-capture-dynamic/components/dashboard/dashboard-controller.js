//Controller for dashboard
trackerCapture.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                storage,
                TEIService, 
                TEService,
                OptionSetService,
                orderByFilter,
                ProgramFactory,
                CurrentSelection,
                TrackerWidgetsConfigurationFactory) {

     //selections  
    $scope.selectedTeiId = ($location.search()).tei; 
    $scope.selectedProgramId = ($location.search()).program; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');

    $scope.selectedProgram;    
    $scope.selectedTei;  
 
    //dashboard items   
    $rootScope.biggerDashboardWidgets = [];
    $rootScope.smallerDashboardWidgets = [];
    
    //Get widget configuration and order ascending based on index
    var unorderedWidgetConfigs = TrackerWidgetsConfigurationFactory.getWidgetConfiguration($scope.selectedProgramId);
    var orderedWidgetConfigs = orderByFilter(
            unorderedWidgetConfigs,
            "+index");
    
    //Create each widget based on configuration
    angular.forEach(orderedWidgetConfigs,function(widgetConfig){
        var configuredWidget  = 
                    {
                        title:widgetConfig.title, 
                        show:widgetConfig.show,
                        expand:widgetConfig.expand,
                        code:widgetConfig.code
                    };
                    
        if(widgetConfig.type === "rulebound")
        {
            configuredWidget.view = "components/rulebound/rulebound.html";
        }
        else if(widgetConfig.type === "enrollment")
        {
            configuredWidget.view = "components/enrollment/enrollment.html";
             $rootScope.enrollmentWidget = configuredWidget;
        }
        else if(widgetConfig.type === "dataentry")
        {
            configuredWidget.view = "components/dataentry/dataentry.html";
            $rootScope.dataentryWidget = configuredWidget;
        }
        else if(widgetConfig.type === "report")
        {
            configuredWidget.view = "components/report/tei-report.html";
            $rootScope.reportWidget = configuredWidget;
        }
        else if(widgetConfig.type === "current_selections")
        {
            configuredWidget.view = "components/selected/selected.html";
            $rootScope.selectedWidget = configuredWidget;
        }
        else if(widgetConfig.type === "profile")
        {
            configuredWidget.view = "components/profile/profile.html";
            $rootScope.profileWidget = configuredWidget;
        }
        else if(widgetConfig.type === "relationships")
        {
            configuredWidget.view = "components/relationship/relationship.html";
            $rootScope.relationshipWidget = configuredWidget;
        }
        else if(widgetConfig.type === "notes")
        {
            configuredWidget.view = "components/notes/notes.html";
            $rootScope.notesWidget = configuredWidget;
        }
        
        if(widgetConfig.horizontalplacement==="left"){
            $rootScope.biggerDashboardWidgets.push(configuredWidget);
        } else {
            $rootScope.smallerDashboardWidgets.push(configuredWidget);
        }
    });
    
    if($scope.selectedTeiId){
        
        //get option sets
        $scope.optionSets = [];
        OptionSetService.getAll().then(function(optionSets){
            
            angular.forEach(optionSets, function(optionSet){                            
                $scope.optionSets[optionSet.id] = optionSet;
            });
        
            //Fetch the selected entity
            TEIService.get($scope.selectedTeiId, $scope.optionSets).then(function(response){
                $scope.selectedTei = response.data;

                //get the entity type
                TEService.get($scope.selectedTei.trackedEntity).then(function(te){                    
                    $scope.trackedEntity = te;

                    ProgramFactory.getAll().then(function(programs){  

                        $scope.programs = [];
                        
                        //get programs valid for the selected ou and tei
                        angular.forEach(programs, function(program){
                            if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
                               program.trackedEntity.id === $scope.selectedTei.trackedEntity){
                                $scope.programs.push(program);
                            }

                            if($scope.selectedProgramId && program.id === $scope.selectedProgramId){
                                $scope.selectedProgram = program;
                            }
                        }); 
                        
                        //broadcast selected items for dashboard controllers
                        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, pr: $scope.selectedProgram, enrollment: null, optionSets: $scope.optionSets});
                        $scope.broadCastSelections();                        
                    });
                });            
            });    
        });
    }    
    
    //listen for any change to program selection
    //it is possible that such could happen during enrollment.
    $scope.$on('mainDashboard', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedProgram = null;
        angular.forEach($scope.programs, function(pr){
            if(pr.id === selections.pr){
                $scope.selectedProgram = pr;
            }
        });
        $scope.broadCastSelections(); 
    }); 
    
    $scope.broadCastSelections = function(){
        
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        $scope.trackedEntity = selections.te;
        $scope.optionSets = selections.optionSets;
        
        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, pr: $scope.selectedProgram, enrollment: null, optionSets: $scope.optionSets});
        $timeout(function() { 
            $rootScope.$broadcast('selectedItems', {programExists: $scope.programs.length > 0});            
        }, 100); 
    };     
    
    $scope.back = function(){
        $location.path('/').search({program: $scope.selectedProgramId});                   
    };
    
    $scope.displayEnrollment = false;
    $scope.showEnrollment = function(){
        $scope.displayEnrollment = true;
    };
    
    $scope.removeWidget = function(widget){        
        widget.show = false;
    };
    
    $scope.expandCollapse = function(widget){
        widget.expand = !widget.expand;
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "components/dashboard/dashboard-widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };
});
