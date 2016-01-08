/* global trackerCapture, angular */

//Controller for audit history
trackerCapture.controller('AuditHistoryController', function( $scope, $modalInstance, $modal, AuditHistoryDataService,
                                                              dataElementId, dataElementName, dataType ) {

  $scope.close = function() {
    $modalInstance.close();
  };

  $scope.trackedEntity = dataElementName;

  AuditHistoryDataService.getAuditHistoryData(dataElementId, dataType).then(function( data ) {

    $scope.itemList = [];

    var reponseData = data.trackedEntityDataValueAudits ? data.trackedEntityDataValueAudits : data.trackedEntityAttributeValueAudits
    if( reponseData ) {
        angular.forEach(reponseData, function( dataValue ) {
          $scope.itemList.push({created: dataValue.created, value: dataValue.value, auditType: dataValue.auditType,
            modifiedBy:dataValue.modifiedBy});
        });
    }
  });

});
