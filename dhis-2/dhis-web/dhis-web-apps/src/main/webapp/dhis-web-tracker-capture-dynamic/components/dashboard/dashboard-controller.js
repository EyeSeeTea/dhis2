//Controller for dashboard
trackerCapture.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                $filter,
                orderByFilter,
                storage,
                TEIService, 
                TEService,
                OptionSetService,
				EnrollmentService,
                TrackerWidgetsConfigurationFactory,
                ProgramFactory,
                DashboardLayoutService,
                CurrentSelection) {
    //selections
    $scope.selectedTeiId = ($location.search()).tei; 
    $scope.selectedProgramId = ($location.search()).program; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram;    
    $scope.selectedTei;  
 
    //dashboard items
	var getDashboardLayout = function(){
      	$rootScope.dashboardWidgets = []; 
        $scope.widgetsChanged = [];
        $scope.dashboardStatus = [];
        $scope.dashboardWidgetsOrder = {biggerWidgets: [], smallerWidgets: []};
        $scope.orderChanged = false;
            
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
                
                $rootScope.dashboardWidgets.push(configuredWidget);
                $scope.dashboardStatus[configuredWidget.title] = angular.copy(configuredWidget);

	        if(widgetConfig.horizontalplacement==="left"){
                    configuredWidget.parent = 'biggerWidget';
	            $scope.dashboardWidgetsOrder.biggerWidgets.push(configuredWidget.title);
	        } else {
                    configuredWidget.parent = 'smallerWidget';
	            $scope.dashboardWidgetsOrder.smallerWidgets.push(configuredWidget.title);
	        }
	    });
            $scope.broadCastSelections();
	};
    
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

                    //get enrollments for the selected tei
                    EnrollmentService.getByEntity($scope.selectedTeiId).then(function(response){                    

                        var selectedEnrollment = null;
                        if(angular.isObject(response) && response.enrollments && response.enrollments.length === 1 && response.enrollments[0].status === 'ACTIVE'){
                            selectedEnrollment = response.enrollments[0];
                        }
                        
                        ProgramFactory.getAll().then(function(programs){
                            $scope.programs = [];

                            $scope.programNames = [];  
                            $scope.programStageNames = [];        
                            
                            //get programs valid for the selected ou and tei
                            angular.forEach(programs, function(program){
                                $scope.programNames[program.id] = {id: program.id, name: program.name};
                                angular.forEach(program.programStages, function(stage){                
                                    $scope.programStageNames[stage.id] = {id: stage.id, name: stage.name};
                                });
                                if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
                                   program.trackedEntity.id === $scope.selectedTei.trackedEntity){
                                    $scope.programs.push(program);                                    
                                }

                                if($scope.selectedProgramId && program.id === $scope.selectedProgramId || selectedEnrollment && selectedEnrollment.program === program.id){
                                    $scope.selectedProgram = program;
                                }
                            });
                            
                            //prepare selected items for broadcast
                            CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, prs: $scope.programs, pr: $scope.selectedProgram, prNames: $scope.programNames, prStNames: $scope.programStageNames, enrollments: response.enrollments, selectedEnrollment: selectedEnrollment, optionSets: $scope.optionSets});                            
                            getDashboardLayout();                    
                        });
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
    }); 
    
    //watch for widget sorting    
    $scope.$watch('widgetsOrder', function() {        
        if(angular.isObject($scope.widgetsOrder)){
            $scope.orderChanged = false;
            for(var i=0; i<$scope.widgetsOrder.smallerWidgets.length; i++){
                if($scope.widgetsOrder.smallerWidgets.length === $scope.dashboardWidgetsOrder.smallerWidgets.length && $scope.widgetsOrder.smallerWidgets[i] !== $scope.dashboardWidgetsOrder.smallerWidgets[i]){
                    $scope.orderChanged = true;
                }
                
                if($scope.widgetsOrder.smallerWidgets.length !== $scope.dashboardWidgetsOrder.smallerWidgets.length){
                    $scope.orderChanged = true;
                }
            }
            
            for(var i=0; i<$scope.widgetsOrder.biggerWidgets.length; i++){
                if($scope.widgetsOrder.biggerWidgets.length === $scope.dashboardWidgetsOrder.biggerWidgets.length && $scope.widgetsOrder.biggerWidgets[i] !== $scope.dashboardWidgetsOrder.biggerWidgets[i]){
                    $scope.orderChanged = true;
                }
                
                if($scope.widgetsOrder.biggerWidgets.length !== $scope.dashboardWidgetsOrder.biggerWidgets.length){
                    $scope.orderChanged = true;
                }
            }
            
            if($scope.orderChanged){
                saveDashboardLayout();
            }
        }
    });
    
    $scope.applySelectedProgram = function(){
        getDashboardLayout();
    };
    
    $scope.broadCastSelections = function(){
        
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        $scope.trackedEntity = selections.te;
        $scope.optionSets = selections.optionSets;
        
        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, prs: $scope.programs, pr: $scope.selectedProgram, prNames: $scope.programNames, prStNames: $scope.programStageNames, enrollments: selections.enrollments, selectedEnrollment: null, optionSets: $scope.optionSets});        
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
        saveDashboardLayout();
    };
    
    $scope.expandCollapse = function(widget){
        widget.expand = !widget.expand;
        saveDashboardLayout();;
    };
    
    var saveDashboardLayout = function(){
        var widgets = [];
        angular.forEach($rootScope.dashboardWidgets, function(widget){
            var w = angular.copy(widget);            
            if($scope.orderChanged){
                if($scope.widgetsOrder.biggerWidgets.indexOf(w.title) !== -1){
                    w.parent = 'biggerWidget';
                    w.order = $scope.widgetsOrder.biggerWidgets.indexOf(w.title);
                }
                
                if($scope.widgetsOrder.smallerWidgets.indexOf(w.title) !== -1){
                    w.parent = 'smallerWidget';
                    w.order = $scope.widgetsOrder.smallerWidgets.indexOf(w.title);
                }
            }            
            widgets.push(w);
        });
            
        if($scope.selectedProgram && $scope.selectedProgram.id){
            $scope.dashboardLayouts[$scope.selectedProgram.id] = {widgets: widgets, program: $scope.selectedProgram.id};
        }
        
        DashboardLayoutService.saveLayout($scope.dashboardLayouts).then(function(){
        });
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "components/dashboard/dashboard-widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };
    
    $rootScope.closeOpenWidget = function(widget){
        saveDashboardLayout();
    };
});
