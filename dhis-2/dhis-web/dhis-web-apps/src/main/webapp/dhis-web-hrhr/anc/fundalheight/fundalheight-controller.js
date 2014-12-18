trackerCapture.controller('FundalheightController',
        function($rootScope,
                $scope,
                $location,
                $timeout,
                storage,
                TEIService,
                CurrentSelection,
                FundalHeight) {
    
    
    //selections  
    $scope.selectedEntity = null;
    $scope.selectedEntityId = ($location.search()).tei; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram = storage.get('SELECTED_PROGRAM');

    //get dashboard for the selected entities
    if($scope.selectedEntityId && $scope.selectedProgram && $scope.selectedOrgUnit){  
        //Fetch the selected entity
        TEIService.get($scope.selectedEntityId).then(function(tei){
            
            $scope.selectedEntity = tei;                            
            if(!angular.isUndefined($scope.selectedEntity.relationships)){
                TEIService.get($scope.selectedEntity.relationships[0].trackedEntityInstanceB).then(function(contact){
                    CurrentSelection.set({tei: tei, contact: contact, pr: $scope.selectedProgram});
                    $timeout(function() { 
                        $rootScope.$broadcast('selectedEntity', {});
                    }, 100);
                });
            }
            else{
                CurrentSelection.set({tei: tei, pr: $scope.selectedProgram});
                $timeout(function() { 
                    $rootScope.$broadcast('selectedEntity', {});
                }, 100);
            }                        
        });        
    }     
    
    //Fundal height chart        
    var yAxisLabels = [];    
    for(var i=18; i<44; i++){
        yAxisLabels.push(i);
    }
    
    var xAxisLabels = [];
    FundalHeight.getBaseValues().then(function(data) {
        $scope.baseValues = data;     
                  
        var one = [], twoPointFive = [], five = [], ten = [], twentyFive = [],
            fifty = [], seventyFive = [], ninty = [], nintyFive = [], 
            nintySevenPointFive = [], nintyNine = [];
            
        var dataPointOne = [], dataPointTwoPointFive = [], dataPointFive = [], dataPointTen = [], dataPointTwentyFive = [],
            dataPointFifty = [], dataPointSeventyFive = [], dataPointNinty = [], dataPointNintyFive = [], 
            dataPointNintySevenPointFive = [], dataPointNintyNine = [];
            
        angular.forEach($scope.baseValues, function(baseValue){
            delete baseValue.weekAndDay;
            
            if((baseValue.day % 7) === 0 ){
                xAxisLabels.push([baseValue.day, Math.floor( baseValue.day / 7 )]);
            }
            
            dataPointOne.push(""); dataPointTwoPointFive.push(""); dataPointFive.push("");
            dataPointTen.push(""); dataPointTwentyFive.push(""); dataPointFifty.push("");
            dataPointSeventyFive.push(""); dataPointNinty.push(""); dataPointNintyFive.push("");
            dataPointNintySevenPointFive.push(""); dataPointNintyNine.push("");
            
            one.push([baseValue.day, baseValue.one]); 
            twoPointFive.push([baseValue.day, baseValue.twoPointFive]); 
            five.push([baseValue.day, baseValue.five]); 
            ten.push([baseValue.day, baseValue.ten]); 
            twentyFive.push([baseValue.day, baseValue.twentyFive]); 
            fifty.push([baseValue.day, baseValue.fifty]);
            seventyFive.push([baseValue.day, baseValue.seventyFive]); 
            ninty.push([baseValue.day, baseValue.ninty]); 
            nintyFive.push([baseValue.day, baseValue.nintyFive]);
            nintySevenPointFive.push([baseValue.day, baseValue.nintySevenPointFive]); 
            nintyNine.push([baseValue.day, baseValue.nintyNine]);   

        });      
        
        dataPointOne[dataPointOne.length - 1] = "1%";
        dataPointTwoPointFive[dataPointTwoPointFive.length - 1] = '2.5%';
        dataPointFive[dataPointFive.length - 1] = '5%';
        dataPointTen[dataPointTen.length - 1] = '10%';
        dataPointTwentyFive[dataPointTwentyFive.length - 1] = '25%';
        dataPointFifty[dataPointFifty.length - 1] = '50%';
        dataPointSeventyFive[dataPointSeventyFive.length - 1] = '75%';
        dataPointNinty[dataPointNinty.length - 1] = '90%';
        dataPointNintyFive[dataPointNintyFive.length - 1] = '95%';
        dataPointNintySevenPointFive[dataPointNintySevenPointFive.length - 1] = '97.5%';
        dataPointNintyNine[dataPointNintyNine.length - 1] = '99%';
        
        var ancVisitData = [];
        
        ancVisitData.push([170,24]); ancVisitData.push([190,26.1]);
        
        
        var fundalHeightData = [
            { data: one, color: '#000', label: 'For 1% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointOne, labelPlacement: "right", labelClass: "data-point-label-class" },
            //{ data: one, color: '#FF00FF', label: 'For 1% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointOne, labelPlacement: "right", canvasRender: true, cColor: "#FF00FF"},
            { data: twoPointFive, color: '#000', label: 'For 2.5% of the populaiton, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointTwoPointFive, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: five, color: '#000', label: 'For 5% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointFive, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: ten, color: '#000', label: 'For 10% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointTen, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: twentyFive, color: '#000', label: 'For 25% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointTwentyFive, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: fifty, color: '#000', label: 'For 50% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointFifty, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: seventyFive, color: '#000', label: 'For 75% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointSeventyFive, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: ninty, color: '#000', label: 'For 90% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false } , showLabels: true, labels: dataPointNinty, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: nintyFive, color: '#000', label: 'For 95% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false } , showLabels: true, labels: dataPointNintyFive, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: nintySevenPointFive, color: '#000', label: 'For 97.5% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointNintySevenPointFive, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: nintyNine, color: '#000', label: 'For 99% of the population, fundal height at pregnancy day', lines: { show: true }, points: { show: false }, showLabels: true, labels: dataPointNintyNine, labelPlacement: "right", labelClass: "data-point-label-class" },
            { data: ancVisitData, color: '#0101DF', label: 'Fundal height at pregnancy day', lines: { show: false }, points: { show: true, symbol: 'cross', radius: 5 } }
        ];        

        var options = { 
            legend: { show: false },
            yaxis: { min: 18, max: 43, ticks: yAxisLabels, show: true},
            xaxis: { ticks: xAxisLabels, show: true/*, axisLabel: 'My label'*/ },
            grid: { hoverable: true },
            tooltip: true,
            tooltipOpts: { content: "%s %x.1 is %y.4", shifts: { x: -60, y: 25 } },
            yAxisTitle: 'Fundal Height (cm)',
            xAxisTitle: 'Week of pregnancy',
            width: '100%',
            height: '300px'
        };    
        $scope.fundalChart = {data: fundalHeightData, options: options}; 
        
        $scope.myData = fundalHeightData;
        $scope.myChartOptions = options;
        
        //console.log("The data is:  ", $scope.fundalChart);
        
    });   
    
    $scope.close = function() {
        $location.path('/anc');
    };
});
