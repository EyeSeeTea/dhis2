/* global trackerCapture, angular */

//Controller for audit history
trackerCapture.controller('AuditHistoryController', function ($scope, $modalInstance, $modal, AuditHistoryDataService, dataElementId) {

   $scope.close = function () {
      $modalInstance.close();
  };

  var scope = $scope;
  AuditHistoryDataService.getAuditHistoryData(dataElementId).then(function(data){
      if(data.trackedEntityDataValueAudits) {
          angular.forEach(data.trackedEntityDataValueAudits, function(dataValue) {
            scope.itemList = [];
            $scope.itemList.push({date:dataValue.created, value:dataValue.value});
          });
      }
  });

});
