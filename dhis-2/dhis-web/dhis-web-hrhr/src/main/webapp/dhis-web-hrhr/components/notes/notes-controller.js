trackerCapture.controller('NotesController',
        function($scope,
                $rootScope,
                storage,
                DHIS2EventFactory,
                orderByFilter,
                NotesDialogService,
                TranslationService) {

    TranslationService.translate();
    
    $scope.$on('noteController', function(event, args) {
        DHIS2EventFactory.get(args.currentEvent.event).then(function(data){    
            $scope.currentEvent = data;   
            
            if(angular.isUndefined( $scope.currentEvent.notes)){
                
                $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');
            
                angular.forEach($scope.currentEvent.notes, function(note){
                    note.storedDate = moment(note.storedDate).format('YYYY-MM-DD @ hh:mm:ss A');
                });
            }
        });          
    });
   
    $scope.searchNoteField = false;
    $scope.addNoteField = false;    
    
    $scope.showAddNote = function() {
        $scope.addNoteField = true;
    };
    
    $scope.addNote = function(){
        
        if(!angular.isUndefined($scope.note) && $scope.note != ""){
            
            var newNote = {value: $scope.note};

            if(angular.isUndefined( $scope.currentEvent.notes) ){
                $scope.currentEvent.notes = [newNote];
            }
            else{
                $scope.currentEvent.notes.splice(0,0,newNote);
            }

            var e = $scope.currentEvent;
            //e.notes = [newNote];
            DHIS2EventFactory.update(e).then(function(data){
                $scope.note = '';
                $scope.addNoteField = false; //note is added, hence no need to show note field.
                $rootScope.$broadcast('noteController', {currentEvent: $scope.currentEvent});
            });   
        }        
    };
    
    $scope.displayNote = function(note){       
        
        var dialogOptions = {
            headerText: 'consultation_note',
            bodyText: note
        };
        NotesDialogService.showDialog({}, dialogOptions);
    };
    
    $scope.closeAddNote = function(){
         $scope.addNoteField = false;
         $scope.note = '';           
    };
    
    $scope.searchNote = function(){        
        $scope.searchNoteField = $scope.searchNoteField === false ? true : false;
        $scope.noteSearchText = '';
    };
});