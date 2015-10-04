function editApprovalWorkflow() {
	var approvalLevels = [];
	for (var i = 1; i <= $( "#approvalLevelCount").val(); i++) {
		if ($("#approvalLevel" + i).val().length) {
			approvalLevels.push( { id: $("#approvalLevel" + i).val() } );
		}
	}
	$.ajax({
		url: '../api/dataApprovalWorkflows/' + dataApprovalWorkflowUid,
		type: 'PUT',
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		data: JSON.stringify({
				id: dataApprovalWorkflowUid,
				name: $("#name").val(),
				periodType: $("#frequency").val(),
				dataApprovalLevels: approvalLevels
			}),
		success: function (json) {
			location.href = 'systemApprovalWorkflows.action';
		},
		error: function( xhr, status, error) {
			setHeaderDelayMessage( error );
		}
	});
}
