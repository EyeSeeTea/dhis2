$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function editDataApprovalWorkflow( context ) {
    location.href = 'showEditApprovalWorkflowForm.action?dataApprovalWorkflowId=' + context.id;
}

function removeDataApprovalWorkflow( context ) {
    var result = window.confirm( i18n_confirm_delete_data_approval_workflow + "\n\n" + context.name );

    if ( result )
    {
        setHeaderWaitMessage( i18n_deleting );

        $.ajax({
            url: '../api/dataApprovalWorkflows/' + context.uid,
            type: 'DELETE',
            success: function (json) {
                jQuery( "tr#tr" + context.id ).remove();

                jQuery( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
                jQuery( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
                jQuery( "table.listTable tbody tr:even" ).addClass( "listRow" );
                jQuery( "table.listTable tbody" ).trigger("update");

                setHeaderDelayMessage( i18n_delete_success );
            },
            error: function( xhr, status, error) {
                setHeaderDelayMessage( error );
            }
        });
    }
}
