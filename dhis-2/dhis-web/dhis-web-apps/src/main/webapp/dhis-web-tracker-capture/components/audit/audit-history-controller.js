/* global trackerCapture, angular */

//Controller for audit history
trackerCapture.controller('AuditHistoryController', function( $scope, $modalInstance, $modal, AuditHistoryDataService, dataElementId, dataElementName ) {

  $scope.close = function() {
    $modalInstance.close();
  };

  $scope.trackedEntity = dataElementName;

  AuditHistoryDataService.getAuditHistoryData(dataElementId).then(function( data ) {

    if( data.trackedEntityDataValueAudits ) {
      scope.itemList = [];
        angular.forEach(data.trackedEntityDataValueAudits, function( dataValue ) {
            $scope.itemList.push({date: dataValue.created, value: dataValue.value});
        });
    }
  });

});
