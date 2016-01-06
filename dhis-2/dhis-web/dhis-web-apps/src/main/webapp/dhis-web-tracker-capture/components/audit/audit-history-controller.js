/* global trackerCapture, angular */

//Controller for audit history
trackerCapture.controller('AuditHistoryController', function ($scope, $modalInstance) {
   $scope.itemList = [{"date":"Date 1", "value":"Value 1"},{"date":"Date 2", "value":"Value 2"},{"date":"Date 3", "value":"Value 3"},{"date":"Date 4", "value":"Value 4"}];
   $scope.trackedEntity = "Tracked Entity 1";
   $scope.close = function () {
      $modalInstance.close();
  };

});
