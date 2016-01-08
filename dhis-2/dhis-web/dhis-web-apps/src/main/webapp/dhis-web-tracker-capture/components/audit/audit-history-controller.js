/* global trackerCapture, angular */

//Controller for audit history
trackerCapture.controller('AuditHistoryController', function( $scope, $modalInstance, $modal, AuditHistoryDataService,
                                                              dataElementId, dataElementName, dataType, DateUtils ) {

  $scope.close = function() {
    $modalInstance.close();
  };

  $scope.trackedEntity = dataElementName;

  AuditHistoryDataService.getAuditHistoryData(dataElementId, dataType).then(function( data ) {

    $scope.itemList = [];

    var reponseData = data.trackedEntityDataValueAudits ? data.trackedEntityDataValueAudits : data.trackedEntityAttributeValueAudits;
    if( reponseData ) {
        angular.forEach(reponseData, function( dataValue ) {
          /*The true/false values are displayed as Yes/No*/
          if (dataValue.value === "true") {
            dataValue.value = "Yes";
          } else if (dataValue.value === "false") {
            dataValue.value = "No";
          }
          $scope.itemList.push({created: DateUtils.formatToHrsMinsSecs(dataValue.created), value: dataValue.value, auditType: dataValue.auditType,
            modifiedBy:dataValue.modifiedBy});
        });
      }
  });

});
