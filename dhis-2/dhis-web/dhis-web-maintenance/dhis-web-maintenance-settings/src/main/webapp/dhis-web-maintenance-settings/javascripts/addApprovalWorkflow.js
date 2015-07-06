function addApprovalWorkflow() {
	var approvalLevels = [];
	for (var i = 1; i <= $( "#approvalLevelCount").val(); i++) {
		if ($("#approvalLevel" + i).val().length) {
			approvalLevels.push( { id: $("#approvalLevel" + i).val() } );
		}
	}
	$.post("../api/dataApprovalWorkflows",
		{
			name: $("#name").val(),
			dataApprovalLevels: approvalLevels
		},
		function(json) {
			showSuccessMessage(i18n_create_data_approval_workflow_success);
			$("#addApprovalWorkflowForm").submit();
		},
	"json");
}
